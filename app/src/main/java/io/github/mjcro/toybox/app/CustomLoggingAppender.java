package io.github.mjcro.toybox.app;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Custom Logback appender that dispatches logging events to registered consumers.
 */
public class CustomLoggingAppender extends AppenderBase<ILoggingEvent> {

    private static final @NonNull Queue<@NonNull Consumer<@NonNull ILoggingEvent>> consumers = new ConcurrentLinkedQueue<>();
    private static final @NonNull String @NonNull [] skipPrefixes = new String[]{
            "org.eclipse.jetty"
    };

    private static void add(final @NonNull ILoggingEvent event) {
        for (String prefix : skipPrefixes) {
            if (event.getLoggerName().startsWith(prefix) && event.getLevel() == Level.DEBUG) {
                // Skip
                return;
            }
        }

        consumers.forEach($ -> $.accept(event));
    }

    /**
     * Registers a consumer to receive logging events.
     *
     * @param consumer the event consumer
     */
    public void listen(@NonNull Consumer<@NonNull ILoggingEvent> consumer) {
        Objects.requireNonNull(consumer, "consumer");
        consumers.add(consumer);
    }

    @Override
    protected void append(final @NonNull ILoggingEvent event) {
        if (event != null) {
            add(event);
        }
    }
}
