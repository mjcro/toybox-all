package io.github.mjcro.toybox.app;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.circular.ConcurrentCircularList;

import java.util.ArrayList;
import java.util.List;

public class LogBuffer {
    public static final LogBuffer Instance = new LogBuffer();
    private final ConcurrentCircularList<ILoggingEvent> lines = new ConcurrentCircularList<>(1000);

    private LogBuffer() {
    }

    public void add(ILoggingEvent event) {
        if (event != null) {
            lines.add(event);
        }
    }

    public List<ILoggingEvent> getAll() {
        return new ArrayList<>(lines);
    }
}
