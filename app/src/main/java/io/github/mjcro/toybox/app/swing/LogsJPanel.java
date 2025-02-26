package io.github.mjcro.toybox.app.swing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class LogsJPanel extends JPanel {
    private final LogsJList logsList = new LogsJList();

    private final JToggleButton toggleTrace = new JToggleButton("Trace");
    private final JToggleButton toggleDebug = new JToggleButton("Debug");
    private final JToggleButton toggleInfo = new JToggleButton("Info");
    private final JToggleButton toggleWarn = new JToggleButton("Warn");
    private final JToggleButton toggleError = new JToggleButton("Error");

    private final Supplier<Collection<ILoggingEvent>> eventSupplier;
    private ArrayList<ILoggingEvent> events = null;

    public LogsJPanel(Supplier<Collection<ILoggingEvent>> eventSupplier) {
        super(new BorderLayout());

        add(headerPanel(), BorderLayout.PAGE_START);
        add(new JScrollPane(logsList), BorderLayout.CENTER);

        this.eventSupplier = eventSupplier;
        setEvent(eventSupplier.get());
    }

    public LogsJPanel() {
        this(null);
    }

    private JPanel headerPanel() {
        JPanel header = new JPanel();

        JPanel togglers = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
        ToyBoxIcons.get("fam://bullet_white").ifPresent(toggleTrace::setIcon);
        ToyBoxIcons.get("fam://bullet_black").ifPresent(toggleDebug::setIcon);
        ToyBoxIcons.get("fam://bullet_green").ifPresent(toggleInfo::setIcon);
        ToyBoxIcons.get("fam://bullet_orange").ifPresent(toggleWarn::setIcon);
        ToyBoxIcons.get("fam://bullet_red").ifPresent(toggleError::setIcon);

        toggleTrace.setSelected(false);
        toggleDebug.setSelected(false);
        toggleInfo.setSelected(true);
        toggleWarn.setSelected(true);
        toggleError.setSelected(true);

        Hint<AbstractButton> action = Hints.onAction(this::applyEvents);
        action.apply(toggleTrace);
        action.apply(toggleDebug);
        action.apply(toggleInfo);
        action.apply(toggleWarn);
        action.apply(toggleError);

        togglers.add(toggleTrace);
        togglers.add(toggleDebug);
        togglers.add(toggleInfo);
        togglers.add(toggleWarn);
        togglers.add(toggleError);
        header.add(togglers);

        JButton refresh = ToyBoxButtons.create("Refresh", Hints.onAction(this::refreshEvents));
        header.add(refresh);

        return header;
    }

    public void setEvent(Collection<ILoggingEvent> events) {
        this.events = events == null || events.isEmpty() ? new ArrayList<>() : new ArrayList<>(events);
        applyEvents();
    }

    private void applyEvents() {
        HashSet<Level> levels = new HashSet<>();
        if (toggleTrace.isSelected()) levels.add(Level.TRACE);
        if (toggleDebug.isSelected()) levels.add(Level.DEBUG);
        if (toggleInfo.isSelected()) levels.add(Level.INFO);
        if (toggleWarn.isSelected()) levels.add(Level.WARN);
        if (toggleError.isSelected()) levels.add(Level.ERROR);

        logsList.setEvents(
                events.stream()
                        .filter(e -> e != null && levels.contains(e.getLevel()))
                        .collect(Collectors.toList())
        );
    }

    private void refreshEvents() {
        if (eventSupplier != null) {
            log.debug("Refreshing logs collection");
            setEvent(eventSupplier.get());
        }
    }
}
