package io.github.mjcro.toybox.app.swing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.Abbreviator;
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.toybox.swing.Icons;
import io.github.mjcro.toybox.swing.Styles;

import javax.swing.*;
import java.awt.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

public class LogsJList extends JList<ILoggingEvent> {
    public LogsJList(Collection<ILoggingEvent> events) {
        super();

        setCellRenderer(new Renderer());
        setEvents(events);
    }

    public LogsJList() {
        this(null);
    }

    public void setEvents(Collection<ILoggingEvent> events) {
        ArrayList<ILoggingEvent> list = events == null || events.isEmpty() ? null : new ArrayList<>(events);
        if (list != null) {
            list.sort((a, b) -> -Long.compare(a.getTimeStamp(), b.getTimeStamp()));
        }
        applyEvents(list);
    }

    private void applyEvents(ArrayList<ILoggingEvent> events) {
        DefaultListModel<ILoggingEvent> model = new DefaultListModel<>();
        if (events != null) {
            model.addAll(events);
        }
        setModel(model);
    }

    private static class Renderer extends JPanel implements ListCellRenderer<ILoggingEvent> {
        private final JLabel iconLabel = new JLabel();
        private final JLabel timeLabel = new JLabel();
        private final JLabel messageLabel = new JLabel();
        private final JLabel loggerLabel = new JLabel();

        private final Icon iconTrace = Icons.get("fam://bullet_white").orElse(null);
        private final Icon iconDebug = Icons.get("fam://bullet_black").orElse(null);
        private final Icon iconInfo = Icons.get("fam://bullet_green").orElse(null);
        private final Icon iconWarn = Icons.get("fam://bullet_orange").orElse(null);
        private final Icon iconError = Icons.get("fam://bullet_red").orElse(null);

        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        private final Color normalBackground = UIManager.getColor("List.background");
        private final Color normalForeground = UIManager.getColor("List.foreground");
        private final Color selectedBackground = UIManager.getColor("List.selectionBackground");
        private final Color selectedForeground = UIManager.getColor("List.selectionForeground");

        private final Abbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(20);

        Renderer() {
            super(new FlowLayout(FlowLayout.LEFT, 4, 1));
            add(iconLabel);
            add(timeLabel);
            add(messageLabel);
            add(loggerLabel);

            Styles.TABLE_CELL_INSTANT.apply(timeLabel);
            Styles.BOLD.apply(messageLabel);
            Styles.ITALIC.apply(loggerLabel);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ILoggingEvent> list,
                ILoggingEvent e,
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
