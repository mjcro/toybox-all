package io.github.mjcro.toybox.api.http;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Log of HTTP exchanges captured by clients obtained from {@link io.github.mjcro.toybox.api.Environment}.
 */
public interface HttpExchangeLog {
    /**
     * Returns captured exchanges.
     *
     * @return Snapshot of captured exchanges, oldest first.
     */
    @NonNull List<@NonNull HttpExchange> getAll();

    /**
     * Returns number of captured exchanges.
     *
     * @return Number of captured exchanges.
     */
    int size();

    /**
     * Removes all captured exchanges.
     */
    void clear();
}
