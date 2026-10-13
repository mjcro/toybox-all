package io.github.mjcro.toybox.app.swing.widgets;

import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.ApplicationEnvironment;
import io.github.mjcro.toybox.app.CustomLoggingAppender;
import io.github.mjcro.toybox.app.http.HttpCaptureBuffer;
import io.github.mjcro.toybox.app.http.HttpClientFactory;
import io.github.mjcro.toybox.app.settings.storage.SettingsStorageDispatcher;
import io.github.mjcro.toybox.app.utils.TextFormat;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.SilkIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.toys.HttpLogToy;
import io.github.mjcro.toybox.toys.LogsToy;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * Status bar panel displayed at the bottom of the main window, showing
 * information messages, timing, memory usage, thread count, captured HTTP exchanges count
 * and settings file status.
 */
public class StatusBarWidget extends JPanel {
    private final @NonNull JLabel informationLabel = ToyBoxLabels.create("Welcome");
    private final @NonNull JLabel lastElapsedLabel = ToyBoxLabels.create("", Hints.CENTER);
    private final @NonNull JLabel memoryUsageLabel = ToyBoxLabels.create("0M", Hints.CENTER);
    private final @NonNull JLabel threadsCountLabel = ToyBoxLabels.create("1", Hints.CENTER);
    private final @NonNull JLabel httpCountLabel = ToyBoxLabels.create("0", Hints.CENTER);
    private final @NonNull SettingsFileLabel settingsFileLabel;
    private final @NonNull AtomicReference<@Nullable ILoggingEvent> pendingEvent = new AtomicReference<>();
    private final @NonNull AtomicReference<@Nullable Duration> pendingElapsed = new AtomicReference<>();
    private final @NonNull AtomicBoolean flushScheduled = new AtomicBoolean();

    /**
     * Creates an interactive status bar that auto-updates from logging events and a scheduler.
     *
     * @param environment the application environment
     * @param toyRunner   a consumer that opens a toy by class and optional initial data
     * @param scheduler   the scheduler for periodic metric updates
     * @return a configured and self-updating status bar widget
     */
    public static @NonNull StatusBarWidget interactive(
            @NonNull Environment environment,
            @NonNull BiConsumer<@NonNull Class<? extends @NonNull Toy>, @Nullable Object> toyRunner,
            @NonNull ScheduledExecutorService scheduler
    ) {
        StatusBarWidget w = new StatusBarWidget(environment, toyRunner);
        CustomLoggingAppender cla = new CustomLoggingAppender();
        cla.listen(w::offerLoggingEvent);

        scheduler.scheduleWithFixedDelay(() -> {
            long memory = Runtime.getRuntime().totalMemory();
            int threads = Thread.activeCount();
            int httpExchanges = environment.getHttpExchangeLog().size();
            SwingUtilities.invokeLater(() -> {
                w.setMemoryUsage(memory);
                w.setThreadCount(threads);
                w.setHttpExchangesCount(httpExchanges);
            });
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return w;
    }

    /**
     * Constructs the status bar widget with clickable labels that open toys.
     *
     * @param environment the application environment
     * @param toyRunner   a consumer that opens a toy by class and optional initial data
     */
    public StatusBarWidget(
            @NonNull Environment environment,
            @NonNull BiConsumer<@NonNull Class<? extends @NonNull Toy>, @Nullable Object> toyRunner
    ) {
        super(new BorderLayout());
        this.settingsFileLabel = new SettingsFileLabel(environment);
        Hints.PADDING_NANO.apply(this);
        Hints.PADDING_NORMAL.apply(informationLabel);

        lastElapsedLabel.setToolTipText("Timing of last operation");
        informationLabel.setToolTipText("Information message");
        memoryUsageLabel.setToolTipText("Current memory usage");
        threadsCountLabel.setToolTipText("Threads count");
        httpCountLabel.setToolTipText("Captured HTTP exchanges, click to open log");
        ToyBoxIcons.get(SilkIcons.TRANSMIT).ifPresent(httpCountLabel::setIcon);

        informationLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        informationLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    toyRunner.accept(LogsToy.class, null);
                }
            }
        });

        httpCountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        httpCountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    toyRunner.accept(HttpLogToy.class, null);
                }
            }
        });

        JPanel metrics = new JPanel(new GridLayout(1, 4));
        metrics.add(wrapBevel(lastElapsedLabel));
        metrics.add(wrapBevel(memoryUsageLabel));
        metrics.add(wrapBevel(threadsCountLabel));
        metrics.add(wrapBevel(httpCountLabel));

        // Settings file section is placed rightmost and sized to its content
        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.add(metrics, BorderLayout.CENTER);
        rightSide.add(wrapBevel(settingsFileLabel), BorderLayout.LINE_END);

        add(new JSeparator(), BorderLayout.PAGE_START);
        BorderLayoutMaster.addCenterRight(this, informationLabel, rightSide);
    }

    /**
     * Wraps a component in a beveled panel for visual separation.
     *
     * @param label the component to wrap
     * @return a panel containing the wrapped component
     */
    private static @NonNull JPanel wrapBevel(@NonNull JComponent label) {
        JPanel outer = new JPanel(new BorderLayout());
        Hints.PADDING_NANO.apply(outer);

        JPanel bevel = new JPanel(new BorderLayout());
        Hints.BORDER_LOWERED_BEVEL.apply(bevel);
        outer.add(bevel, BorderLayout.CENTER);

        Hints.PADDING_NANO.apply(label);
        bevel.add(label, BorderLayout.CENTER);
        return outer;
    }

    /**
     * Records the latest logging event and schedules a single coalesced EDT update.
     * Safe to call from any thread; bursts of events result in one label update.
     *
     * @param event the logging event
     */
    private void offerLoggingEvent(@NonNull ILoggingEvent event) {
        pendingEvent.set(event);
        final Object[] arguments = event.getArgumentArray();
        if (arguments != null) {
            for (final Object argument : arguments) {
                if (argument instanceof Duration) {
                    pendingElapsed.set((Duration) argument);
                }
            }
        }
        if (flushScheduled.compareAndSet(false, true)) {
            SwingUtilities.invokeLater(this::flushLoggingEvents);
        }
    }

    /**
     * Applies the latest pending logging state to the labels. Must run on the EDT.
     */
    private void flushLoggingEvents() {
        // Reset before reading so events offered during the flush schedule another one
        flushScheduled.set(false);
        final ILoggingEvent event = pendingEvent.getAndSet(null);
        if (event != null) {
            setInformation(event.getFormattedMessage());
        }
        final Duration elapsed = pendingElapsed.getAndSet(null);
        if (elapsed != null) {
            setLastElapsed(elapsed);
        }
    }

    /**
     * Updates the last elapsed duration display.
     *
     * @param duration the duration to display
     */
    public void setLastElapsed(@NonNull Duration duration) {
        lastElapsedLabel.setText(TextFormat.duration(duration));
    }

    /**
     * Updates the information label text.
     *
     * @param text the text to display
     */
    public void setInformation(@Nullable String text) {
        informationLabel.setText(text);
    }

    /**
     * Updates the memory usage display.
     *
     * @param total total memory in bytes
     */
    public void setMemoryUsage(long total) {
        memoryUsageLabel.setText(TextFormat.bytes(total));
    }

    /**
     * Updates the thread count display.
     *
     * @param count current thread count
     */
    public void setThreadCount(int count) {
        threadsCountLabel.setText(" " + count + " ");
    }

    /**
     * Updates the captured HTTP exchanges count display.
     *
     * @param count current number of captured exchanges
     */
    public void setHttpExchangesCount(int count) {
        httpCountLabel.setText(" " + count + " ");
    }

    /**
     * Main method for standalone visual testing of the status bar widget.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        ToyBoxLaF.initialize(false);

        Environment environment = new ApplicationEnvironment(
                new SettingsStorageDispatcher(),
                service,
                new HttpClientFactory(new HttpCaptureBuffer(1000))
        );

        JPanel container = new JPanel(new BorderLayout());
        container.add(new JPanel(), BorderLayout.CENTER);
        container.add(StatusBarWidget.interactive(environment, (c, o) -> {
        }, service), BorderLayout.PAGE_END);

        Components.show(container);
    }
}
