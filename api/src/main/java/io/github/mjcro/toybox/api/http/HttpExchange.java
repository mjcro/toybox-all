package io.github.mjcro.toybox.api.http;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * Captured HTTP exchange - request and either response or error.
 *
 * <p>Request headers contain only headers set explicitly on request,
 * headers added by HTTP client internally (like {@code Host}) are not visible.
 */
public final class HttpExchange {
    private final @NonNull Instant startedAt;
    private final @NonNull Duration elapsed;
    private final @NonNull String method;
    private final @NonNull URI uri;
    private final @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> requestHeaders;
    private final byte @NonNull [] requestBody;
    private final int status;
    private final @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> responseHeaders;
    private final byte @NonNull [] responseBody;
    private final @Nullable Throwable error;

    /**
     * Constructs successful exchange.
     *
     * @param startedAt       request start time
     * @param elapsed         time taken from request start to response body completion
     * @param method          HTTP method
     * @param uri             request URI
     * @param requestHeaders  request headers
     * @param requestBody     request body bytes
     * @param status          response status code
     * @param responseHeaders response headers
     * @param responseBody    response body bytes
     * @return exchange
     */
    public static @NonNull HttpExchange success(
            @NonNull Instant startedAt,
            @NonNull Duration elapsed,
            @NonNull String method,
            @NonNull URI uri,
            @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> requestHeaders,
            byte @NonNull [] requestBody,
            int status,
            @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> responseHeaders,
            byte @NonNull [] responseBody
    ) {
        return new HttpExchange(
                startedAt, elapsed, method, uri, requestHeaders, requestBody,
                status, responseHeaders, responseBody, null
        );
    }

    /**
     * Constructs failed exchange.
     *
     * @param startedAt      request start time
     * @param elapsed        time taken from request start to failure
     * @param method         HTTP method
     * @param uri            request URI
     * @param requestHeaders request headers
     * @param requestBody    request body bytes sent before failure
     * @param error          error occurred
     * @return exchange
     */
    public static @NonNull HttpExchange failure(
            @NonNull Instant startedAt,
            @NonNull Duration elapsed,
            @NonNull String method,
            @NonNull URI uri,
            @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> requestHeaders,
            byte @NonNull [] requestBody,
            @NonNull Throwable error
    ) {
        return new HttpExchange(
                startedAt, elapsed, method, uri, requestHeaders, requestBody,
                -1, Map.of(), new byte[0], Objects.requireNonNull(error, "error")
        );
    }

    private HttpExchange(
            @NonNull Instant startedAt,
            @NonNull Duration elapsed,
            @NonNull String method,
            @NonNull URI uri,
            @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> requestHeaders,
            byte @NonNull [] requestBody,
            int status,
            @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> responseHeaders,
            byte @NonNull [] responseBody,
            @Nullable Throwable error
    ) {
        this.startedAt = Objects.requireNonNull(startedAt, "startedAt");
        this.elapsed = Objects.requireNonNull(elapsed, "elapsed");
        this.method = Objects.requireNonNull(method, "method");
        this.uri = Objects.requireNonNull(uri, "uri");
        this.requestHeaders = copyOf(requestHeaders);
        this.requestBody = requestBody.clone();
        this.status = status;
        this.responseHeaders = copyOf(responseHeaders);
        this.responseBody = responseBody.clone();
        this.error = error;
    }

    private static @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> copyOf(
            @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> headers
    ) {
        return headers.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                e -> List.copyOf(e.getValue())
        ));
    }

    /**
     * @return request start time
     */
    public @NonNull Instant getStartedAt() {
        return startedAt;
    }

    /**
     * @return time taken from request start to response body completion or failure
     */
    public @NonNull Duration getElapsed() {
        return elapsed;
    }

    /**
     * @return HTTP method
     */
    public @NonNull String getMethod() {
        return method;
    }

    /**
     * @return request URI
     */
    public @NonNull URI getUri() {
        return uri;
    }

    /**
     * @return unmodifiable request headers
     */
    public @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * @return copy of request body bytes, empty if request had no body
     */
    public byte @NonNull [] getRequestBody() {
        return requestBody.clone();
    }

    /**
     * @return response status code, empty for failed exchange
     */
    public @NonNull OptionalInt getStatus() {
        return error == null ? OptionalInt.of(status) : OptionalInt.empty();
    }

    /**
     * @return unmodifiable response headers, empty for failed exchange
     */
    public @NonNull Map<@NonNull String, @NonNull List<@NonNull String>> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * @return copy of response body bytes, empty for failed exchange
     */
    public byte @NonNull [] getResponseBody() {
        return responseBody.clone();
    }

    /**
     * @return response body size in bytes
     */
    public int getResponseBodySize() {
        return responseBody.length;
    }

    /**
     * @return error occurred, empty for successful exchange
     */
    public @NonNull Optional<@NonNull Throwable> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public @NonNull String toString() {
        return "HttpExchange{" + method + " " + uri + " -> "
                + (error == null ? String.valueOf(status) : error.toString())
                + " in " + elapsed + "}";
    }
}
