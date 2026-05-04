package io.github.mjcro.toybox.swing.util;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An executor that delays command execution, cancelling any previously
 * scheduled but not yet started command when a new one arrives.
 *
 * <p>Useful for debouncing rapid user input events such as
 * keystroke-triggered searches.
 */
public class DelayedInvoker implements Executor {
    private static final @NonNull ScheduledExecutorService delays = Executors.newScheduledThreadPool(2, r -> {
        final Thread t = new Thread(r, "toybox-delayed-invoker");
        t.setDaemon(true);
        return t;
    });

    private final @NonNull Duration delay;
    private volatile @Nullable ScheduledFuture<?> future;

    /**
     * Creates a new delayed invoker with the specified delay.
     *
     * @param delay the delay before executing commands
     */
    public DelayedInvoker(@NonNull Duration delay) {
        this.delay = Objects.requireNonNull(delay, "delay");
    }

    @Override
    public synchronized void execute(@NonNull Runnable r) {
        if (future != null) {
            future.cancel(false);
            future = null;
        }

        future = delays.schedule(r, delay.toMillis(), TimeUnit.MILLISECONDS);
    }
}
