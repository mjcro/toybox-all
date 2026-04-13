package io.github.mjcro.toybox.app.swing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.toybox.swing.hint.Hint;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Panel that displays application log events with level-based filtering.
 *
 * <p>Toggle buttons allow the user to select which log levels are visible.
 * An optional {@link Supplier} can be provided to refresh the log collection
 * on demand.
 */
public class LogsJPanel extends JPanel {
    private static final @NonNull Logger log = LoggerFactory.getLogger(LogsJPanel.class);

    private final @NonNull LogsJList logsList = new LogsJList();

    private final @NonNull JToggleButton toggleTrace = new JToggleButton("Trace");
    private final @NonNull JToggleButton toggleDebug = new JToggleButton("Debug");
    private final @NonNull JToggleButton toggleInfo = new JToggleButton("Info");
    private final @NonNull JToggleButton toggleWarn = new JToggleButton("Warn");
    private final @NonNull JToggleButton toggleError = new JToggleButton("Error");

    private final @Nullable Supplier<@NonNull Collection<@NonNull ILoggingEvent>> eventSupplier;
    private @NonNull ArrayList<@NonNull ILoggingEvent> events = new ArrayList<>();

    /**
     * Creates a logs panel backed by the given event supplier.
     *
     * @param eventSupplier supplier that provides log events on refresh, may be {@code null}
     */
    public LogsJPanel(@Nullable Supplier<@NonNull Collection<@NonNull ILoggingEvent>> eventSupplier) {
        super(new BorderLayout());

        add(headerPanel(), BorderLayout.PAGE_START);
        add(new JScrollPane(logsList), BorderLayout.CENTER);

        this.eventSupplier = eventSupplier;
        setEvent(eventSupplier == null ? null : eventSupplier.get());
    }

    /**
     * Creates a logs panel without an event supplier.
     */
    public LogsJPanel() {
        this(null);
    }

    /**
     * Builds the header panel containing level toggle buttons and the refresh button.
     *
     * @return the header panel
     */
    private @NonNull JPanel headerPanel() {
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

    /**
     * Replaces the current log events with the given collection and refreshes the view.
     *
     * @param events the new log events, may be {@code null} or empty
     */
    public void setEvent(@Nullable Collection<@NonNull ILoggingEvent> events) {
        this.events = events == null || events.isEmpty() ? new ArrayList<>() : new ArrayList<>(events);
        applyEvents();
    }

    /**
     * Filters the stored events by the currently selected log levels and updates the list.
     */
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

    /**
     * Reloads log events from the supplier (if present) and refreshes the view.
     */
    private void refreshEvents() {
        if (eventSupplier != null) {
            log.debug(Slf4jUtil.TOYBOX_MARKER, "Refreshing logs collection");
            setEvent(eventSupplier.get());
        }
    }
}
