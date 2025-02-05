package io.github.mjcro.toybox.swing.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DelayedInvoker implements Executor {
    private static final ScheduledExecutorService delays = Executors.newScheduledThreadPool(2);

    @NonNull
    private final Duration delay;
    private volatile ScheduledFuture<?> future;

    @Override
    public synchronized void execute(Runnable r) {
        if (future != null) {
            future.cancel(false);
            future = null;
        }

        future = delays.schedule(r, delay.toMillis(), TimeUnit.MILLISECONDS);
    }
}
