package io.github.mjcro.toybox.app.http;

import io.github.mjcro.circular.ConcurrentCircularList;
import io.github.mjcro.toybox.api.http.HttpExchange;
import io.github.mjcro.toybox.api.http.HttpExchangeLog;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Thread-safe circular buffer holding recent captured HTTP exchanges.
 * When full, the oldest exchange is evicted.
 */
public class HttpCaptureBuffer implements HttpExchangeLog {
    private final @NonNull ConcurrentCircularList<@NonNull HttpExchange> exchanges;

    /**
     * Constructs buffer.
     *
     * @param capacity maximum number of exchanges to keep
     */
    public HttpCaptureBuffer(int capacity) {
        this.exchanges = new ConcurrentCircularList<>(capacity);
    }

    /**
     * Adds exchange to buffer.
     *
     * @param exchange exchange to add
     */
    public void add(@NonNull HttpExchange exchange) {
        exchanges.add(Objects.requireNonNull(exchange, "exchange"));
    }

    @Override
    public @NonNull List<@NonNull HttpExchange> getAll() {
        return new ArrayList<>(exchanges);
    }

    @Override
    public int size() {
        return exchanges.size();
    }

    @Override
    public void clear() {
        exchanges.clear();
    }
}
