package io.github.mjcro.toybox.app.http;

import org.jspecify.annotations.NonNull;

import java.net.http.HttpClient;
import java.util.Objects;

/**
 * Factory of {@link HttpClient} instances capturing all requests and responses
 * made through them into {@link HttpCaptureBuffer}.
 */
public class HttpClientFactory {
    private final @NonNull HttpCaptureBuffer buffer;

    /**
     * Constructs factory.
     *
     * @param buffer buffer to capture exchanges into
     */
    public HttpClientFactory(@NonNull HttpCaptureBuffer buffer) {
        this.buffer = Objects.requireNonNull(buffer, "buffer");
    }

    /**
     * @return buffer exchanges are captured into
     */
    public @NonNull HttpCaptureBuffer getBuffer() {
        return buffer;
    }

    /**
     * Creates capturing client with default settings.
     *
     * @return capturing client
     */
    public @NonNull HttpClient newHttpClient() {
        return wrap(HttpClient.newHttpClient());
    }

    /**
     * Creates builder producing capturing clients.
     *
     * @return client builder
     */
    public HttpClient.@NonNull Builder newHttpClientBuilder() {
        return new CapturingHttpClientBuilder(HttpClient.newBuilder(), buffer);
    }

    /**
     * Wraps existing client making it capturing.
     *
     * @param client client to wrap
     * @return capturing client
     */
    public @NonNull HttpClient wrap(@NonNull HttpClient client) {
        return new CapturingHttpClient(client, buffer);
    }
}
