package io.github.mjcro.toybox.app.swing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.Abbreviator;
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A JList component that displays logging events with color-coded level icons,
 * timestamps, messages, and abbreviated logger names.
 */
public class LogsJList extends JList<ILoggingEvent> {
    /**
     * Constructs a logs list pre-populated with the given events.
     *
     * @param events the initial logging events to display, or null for an empty list
     */
    public LogsJList(@Nullable Collection<@NonNull ILoggingEvent> events) {
        super();

        setCellRenderer(new Renderer());
        setEvents(events);
    }

    /**
     * Constructs an empty logs list.
     */
    public LogsJList() {
        this(null);
    }

    /**
     * Replaces the displayed events with the given collection, sorted newest first.
     *
     * @param events the events to display, or null to clear
     */
    public void setEvents(@Nullable Collection<@NonNull ILoggingEvent> events) {
        ArrayList<ILoggingEvent> list = events == null || events.isEmpty() ? null : new ArrayList<>(events);
        if (list != null) {
            list.sort((a, b) -> -Long.compare(a.getTimeStamp(), b.getTimeStamp()));
        }
        applyEvents(list);
    }

    /**
     * Applies a sorted list of events to the underlying list model.
     *
     * @param events the sorted events, or null to clear
     */
    private void applyEvents(@Nullable ArrayList<@NonNull ILoggingEvent> events) {
        DefaultListModel<ILoggingEvent> model = new DefaultListModel<>();
        if (events != null) {
            model.addAll(events);
        }
        setModel(model);
    }

    /**
     * Custom cell renderer that displays each logging event as a panel with
     * an icon, timestamp, message, and abbreviated logger name.
     */
    private static class Renderer extends JPanel implements ListCellRenderer<ILoggingEvent> {
        private final @NonNull JLabel iconLabel = ToyBoxLabels.create();
        private final @NonNull JLabel timeLabel = ToyBoxLabels.create();
        private final @NonNull JLabel messageLabel = ToyBoxLabels.create();
        private final @NonNull JLabel loggerLabel = ToyBoxLabels.create();

        private final @Nullable Icon iconTrace = ToyBoxIcons.get("fam://bullet_white").orElse(null);
        private final @Nullable Icon iconDebug = ToyBoxIcons.get("fam://bullet_black").orElse(null);
        private final @Nullable Icon iconInfo = ToyBoxIcons.get("fam://bullet_green").orElse(null);
        private final @Nullable Icon iconWarn = ToyBoxIcons.get("fam://bullet_orange").orElse(null);
        private final @Nullable Icon iconError = ToyBoxIcons.get("fam://bullet_red").orElse(null);

        private final @NonNull DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        private final @Nullable Color normalBackground = UIManager.getColor("List.background");
        private final @Nullable Color normalForeground = UIManager.getColor("List.foreground");
        private final @Nullable Color selectedBackground = UIManager.getColor("List.selectionBackground");
        private final @Nullable Color selectedForeground = UIManager.getColor("List.selectionForeground");

        private final @NonNull Abbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(20);

        /**
         * Constructs the renderer and lays out its child labels.
         */
        Renderer() {
            super(new FlowLayout(FlowLayout.LEFT, 4, 1));
            add(iconLabel);
            add(timeLabel);
            add(messageLabel);
            add(loggerLabel);

            Hints.TABLE_CELL_INSTANT.apply(timeLabel);
            Hints.BOLD.apply(messageLabel);
            Hints.ITALIC.apply(loggerLabel);
        }

        @Override
        public @NonNull Component getListCellRendererComponent(
                @NonNull JList<? extends ILoggingEvent> list,
                @NonNull ILoggingEvent e,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            Level level = e.getLevel();
            if (level == Level.ERROR) {
                iconLabel.setIcon(iconError);
            } else if (level == Level.WARN) {
                iconLabel.setIcon(iconWarn);
            } else if (level == Level.INFO) {
                iconLabel.setIcon(iconInfo);
            } else if (level == Level.DEBUG) {
                iconLabel.setIcon(iconDebug);
            } else {
                iconLabel.setIcon(iconTrace);
            }

            timeLabel.setText(timeFormatter.format(e.getInstant().atZone(ZoneOffset.UTC)));
            messageLabel.setText(e.getFormattedMessage());
            loggerLabel.setText("@" + abbreviator.abbreviate(e.getLoggerName()));

            if (isSelected) {
                setBackground(selectedBackground);
                iconLabel.setForeground(selectedForeground);
                messageLabel.setForeground(selectedForeground);
                loggerLabel.setForeground(selectedForeground);
            } else {
                setBackground(normalBackground);
                iconLabel.setForeground(normalForeground);
                messageLabel.setForeground(normalForeground);
                loggerLabel.setForeground(normalForeground);
            }

            return this;
        }
    }
}
