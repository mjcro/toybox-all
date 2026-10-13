package io.github.mjcro.toybox.app.http;

import org.jspecify.annotations.NonNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * {@link HttpClient.Builder} decorator producing clients capturing exchanges into {@link HttpCaptureBuffer}.
 * {@code localAddress} added in Java 19 is not delegated, as module targets Java 17.
 */
final class CapturingHttpClientBuilder implements HttpClient.Builder {
    private final HttpClient.@NonNull Builder delegate;
    private final @NonNull HttpCaptureBuffer buffer;

    /**
     * Constructs builder.
     *
     * @param delegate builder to configure actual client
     * @param buffer   buffer to record exchanges into
     */
    CapturingHttpClientBuilder(HttpClient.@NonNull Builder delegate, @NonNull HttpCaptureBuffer buffer) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.buffer = Objects.requireNonNull(buffer, "buffer");
    }

    @Override
    public HttpClient.@NonNull Builder cookieHandler(@NonNull CookieHandler cookieHandler) {
        delegate.cookieHandler(cookieHandler);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder connectTimeout(@NonNull Duration duration) {
        delegate.connectTimeout(duration);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder sslContext(@NonNull SSLContext sslContext) {
        delegate.sslContext(sslContext);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder sslParameters(@NonNull SSLParameters sslParameters) {
        delegate.sslParameters(sslParameters);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder executor(@NonNull Executor executor) {
        delegate.executor(executor);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder followRedirects(HttpClient.@NonNull Redirect policy) {
        delegate.followRedirects(policy);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder version(HttpClient.@NonNull Version version) {
        delegate.version(version);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder priority(int priority) {
        delegate.priority(priority);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder proxy(@NonNull ProxySelector proxySelector) {
        delegate.proxy(proxySelector);
        return this;
    }

    @Override
    public HttpClient.@NonNull Builder authenticator(@NonNull Authenticator authenticator) {
        delegate.authenticator(authenticator);
        return this;
    }

    @Override
    public @NonNull HttpClient build() {
        return new CapturingHttpClient(delegate.build(), buffer);
    }
}
