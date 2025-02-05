package io.github.mjcro.toybox.app;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.NonNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class CustomLoggingAppender extends AppenderBase<ILoggingEvent> {
    private static final Queue<Consumer<ILoggingEvent>> consumers = new ConcurrentLinkedQueue<>();
    private static final String[] skipPrefixes = new String[]{
            "org.eclipse.jetty"
    };

    private static void add(final ILoggingEvent event) {
        for (String prefix : skipPrefixes) {
            if (event.getLoggerName().startsWith(prefix) && event.getLevel() == Level.DEBUG) {
                // Skip
                return;
            }
        }

        consumers.forEach($ -> $.accept(event));
    }

    public void listen(@NonNull final Consumer<ILoggingEvent> consumer) {
        consumers.add(consumer);
    }

    @Override
    protected void append(final ILoggingEvent event) {
        if (event != null) {
            add(event);
        }
    }
}
