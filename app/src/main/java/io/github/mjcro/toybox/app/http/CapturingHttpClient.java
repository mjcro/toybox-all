package io.github.mjcro.toybox.app.http;

import io.github.mjcro.toybox.api.http.HttpExchange;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link HttpClient} decorator recording every exchange into {@link HttpCaptureBuffer}.
 *
 * <p>Bodies are copied while flowing through, so delegate behavior is not changed.
 * Exchange is recorded once response body is fully consumed or on failure, so
 * streamed responses (e.g. {@code ofInputStream}) not read till the end are not recorded.
 * Push promises and web sockets are not captured.
 * Shutdown methods added in Java 21 are not delegated, as module targets Java 17.
 */
final class CapturingHttpClient extends HttpClient {
    private static final @NonNull Logger log = LoggerFactory.getLogger(CapturingHttpClient.class);

    private final @NonNull HttpClient delegate;
    private final @NonNull HttpCaptureBuffer buffer;

    /**
     * Constructs capturing client.
     *
     * @param delegate client performing actual requests
     * @param buffer   buffer to record exchanges into
     */
    CapturingHttpClient(@NonNull HttpClient delegate, @NonNull HttpCaptureBuffer buffer) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.buffer = Objects.requireNonNull(buffer, "buffer");
    }

    @Override
    public @NonNull Optional<CookieHandler> cookieHandler() {
        return delegate.cookieHandler();
    }

    @Override
    public @NonNull Optional<Duration> connectTimeout() {
        return delegate.connectTimeout();
    }

    @Override
    public @NonNull Redirect followRedirects() {
        return delegate.followRedirects();
    }

    @Override
    public @NonNull Optional<ProxySelector> proxy() {
        return delegate.proxy();
    }

    @Override
    public @NonNull SSLContext sslContext() {
        return delegate.sslContext();
    }

    @Override
    public @NonNull SSLParameters sslParameters() {
        return delegate.sslParameters();
    }

    @Override
    public @NonNull Optional<Authenticator> authenticator() {
        return delegate.authenticator();
    }

    @Override
    public @NonNull Version version() {
        return delegate.version();
    }

    @Override
    public @NonNull Optional<Executor> executor() {
        return delegate.executor();
    }

    @Override
    public WebSocket.@NonNull Builder newWebSocketBuilder() {
        return delegate.newWebSocketBuilder();
    }

    @Override
    public <T> @NonNull HttpResponse<T> send(
            @NonNull HttpRequest request,
            HttpResponse.@NonNull BodyHandler<T> responseBodyHandler
    ) throws IOException, InterruptedException {
        final Capture capture = new Capture(request);
        try {
            return delegate.send(capture.request, capture.wrap(responseBodyHandler));
        } catch (IOException | InterruptedException | RuntimeException e) {
            capture.fail(e);
            throw e;
        }
    }

    @Override
    public <T> @NonNull CompletableFuture<HttpResponse<T>> sendAsync(
            @NonNull HttpRequest request,
            HttpResponse.@NonNull BodyHandler<T> responseBodyHandler
    ) {
        final Capture capture = new Capture(request);
        return capture.watch(delegate.sendAsync(capture.request, capture.wrap(responseBodyHandler)));
    }

    @Override
    public <T> @NonNull CompletableFuture<HttpResponse<T>> sendAsync(
            @NonNull HttpRequest request,
            HttpResponse.@NonNull BodyHandler<T> responseBodyHandler,
            HttpResponse.@NonNull PushPromiseHandler<T> pushPromiseHandler
    ) {
        final Capture capture = new Capture(request);
        return capture.watch(delegate.sendAsync(capture.request, capture.wrap(responseBodyHandler), pushPromiseHandler));
    }

    /**
     * State of single exchange being captured.
     */
    private final class Capture {
        private final @NonNull Instant startedAt = Instant.now();
        private final long startedNanos = System.nanoTime();
        private final @NonNull HttpRequest original;
        private final @NonNull HttpRequest request;
        private final @NonNull ByteArrayOutputStream requestBody = new ByteArrayOutputStream();
        private final @NonNull AtomicBoolean recorded = new AtomicBoolean();

        private Capture(@NonNull HttpRequest original) {
            this.original = original;
            // Zero-length publishers are left as is to keep delegate's Content-Length handling intact
            this.request = original.bodyPublisher()
                    .filter(p -> p.contentLength() != 0)
                    .map(p -> HttpRequest.newBuilder(original, (name, value) -> true)
                            .method(original.method(), new TeePublisher(p))
                            .build())
                    .orElse(original);
        }

        private <T> HttpResponse.@NonNull BodyHandler<T> wrap(HttpResponse.@NonNull BodyHandler<T> handler) {
            return info -> new TeeSubscriber<>(handler.apply(info), info);
        }

        private <T> @NonNull CompletableFuture<T> watch(@NonNull CompletableFuture<T> future) {
            // Original future is returned so cancellation still reaches delegate
            future.whenComplete((ignored, error) -> {
                if (error != null) {
                    fail(error instanceof CompletionException && error.getCause() != null ? error.getCause() : error);
                }
            });
            return future;
        }

        private void succeed(HttpResponse.@NonNull ResponseInfo info, byte @NonNull [] body) {
            if (recorded.compareAndSet(false, true)) {
                record(HttpExchange.success(
                        startedAt,
                        elapsed(),
                        original.method(),
                        original.uri(),
                        original.headers().map(),
                        requestBody(),
                        info.statusCode(),
                        info.headers().map(),
                        body
                ));
            }
        }

        private void fail(@NonNull Throwable error) {
            if (recorded.compareAndSet(false, true)) {
                record(HttpExchange.failure(
                        startedAt,
                        elapsed(),
                        original.method(),
                        original.uri(),
                        original.headers().map(),
                        requestBody(),
                        error
                ));
            }
        }

        private void record(@NonNull HttpExchange exchange) {
            buffer.add(exchange);
            log.debug(Slf4jUtil.TOYBOX_MARKER, "Captured {}", exchange);
        }

        private @NonNull Duration elapsed() {
            return Duration.ofNanos(System.nanoTime() - startedNanos);
        }

        private byte @NonNull [] requestBody() {
            synchronized (requestBody) {
                return requestBody.toByteArray();
            }
        }

        /**
         * Request body publisher copying bytes sent by delegate.
         */
        private final class TeePublisher implements HttpRequest.BodyPublisher {
            private final HttpRequest.@NonNull BodyPublisher publisher;

            private TeePublisher(HttpRequest.@NonNull BodyPublisher publisher) {
                this.publisher = publisher;
            }

            @Override
            public long contentLength() {
                return publisher.contentLength();
            }

            @Override
            public void subscribe(Flow.@NonNull Subscriber<? super ByteBuffer> subscriber) {
                // Body may be re-sent (e.g. on redirect or retry), keeping last attempt only
                synchronized (requestBody) {
                    requestBody.reset();
                }
                publisher.subscribe(new Flow.Subscriber<ByteBuffer>() {
                    @Override
                    public void onSubscribe(Flow.@NonNull Subscription subscription) {
                        subscriber.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(@NonNull ByteBuffer item) {
                        synchronized (requestBody) {
                            copy(item, requestBody);
                        }
                        subscriber.onNext(item);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        subscriber.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();
                    }
                });
            }
        }

        /**
         * Response body subscriber copying bytes received by delegate.
         *
         * @param <T> response body type
         */
        private final class TeeSubscriber<T> implements HttpResponse.BodySubscriber<T> {
            private final HttpResponse.@NonNull BodySubscriber<T> subscriber;
            private final HttpResponse.@NonNull ResponseInfo info;
            private final @NonNull ByteArrayOutputStream body = new ByteArrayOutputStream();

            private TeeSubscriber(
                    HttpResponse.@NonNull BodySubscriber<T> subscriber,
                    HttpResponse.@NonNull ResponseInfo info
            ) {
                this.subscriber = subscriber;
                this.info = info;
            }

            @Override
            public @NonNull CompletionStage<T> getBody() {
                return subscriber.getBody();
            }

            @Override
            public void onSubscribe(Flow.@NonNull Subscription subscription) {
                subscriber.onSubscribe(subscription);
            }

            @Override
            public void onNext(@NonNull List<ByteBuffer> item) {
                for (final ByteBuffer chunk : item) {
                    copy(chunk, body);
                }
                subscriber.onNext(item);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                fail(throwable);
                subscriber.onError(throwable);
            }

            @Override
            public void onComplete() {
                // Recording before delegate completes, so exchange is visible once send() returns
                succeed(info, body.toByteArray());
                subscriber.onComplete();
            }
        }
    }

    /**
     * Copies remaining bytes of buffer without changing its position.
     *
     * @param source buffer to copy from
     * @param target stream to copy into
     */
    private static void copy(@NonNull ByteBuffer source, @NonNull ByteArrayOutputStream target) {
        final ByteBuffer duplicate = source.duplicate();
        final byte[] bytes = new byte[duplicate.remaining()];
        duplicate.get(bytes);
        target.write(bytes, 0, bytes.length);
    }
}
