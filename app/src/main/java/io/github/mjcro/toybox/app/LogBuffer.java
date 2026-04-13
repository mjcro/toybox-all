package io.github.mjcro.toybox.app;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.circular.ConcurrentCircularList;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton circular buffer that holds recent logging events for display in the UI.
 */
public class LogBuffer {
    /**
     * The singleton instance.
     */
    public static final @NonNull LogBuffer Instance = new LogBuffer();

    private final @NonNull ConcurrentCircularList<@NonNull ILoggingEvent> lines = new ConcurrentCircularList<>(1000);

    private LogBuffer() {
    }

    /**
     * Adds a logging event to the buffer. Null events are silently ignored.
     *
     * @param event the logging event to add, may be {@code null}
     */
    public void add(@Nullable ILoggingEvent event) {
        if (event != null) {
            lines.add(event);
        }
    }

    /**
     * Returns a snapshot of all buffered logging events.
     *
     * @return a new list containing all current events
     */
    public @NonNull List<@NonNull ILoggingEvent> getAll() {
        return new ArrayList<>(lines);
    }
}
