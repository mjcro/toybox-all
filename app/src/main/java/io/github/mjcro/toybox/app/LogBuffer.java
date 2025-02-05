package io.github.mjcro.toybox.app;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.circular.ConcurrentCircularList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LogBuffer {
    private final ConcurrentCircularList<LogLine> lines = new ConcurrentCircularList<>(1000);

    public void add(LogLine line) {
        if (line != null) {
            lines.add(line);
        }
    }

    public List<LogLine> getAll() {
        return new ArrayList<>(lines);
    }

    public static class LogLine {
        private final Instant time;
        private final String logger;
        private final String message;

        public LogLine(ILoggingEvent source) {
            this.time = Instant.now();
            this.logger = source.getLoggerName();
            this.message = source.getFormattedMessage();
        }
    }
}
