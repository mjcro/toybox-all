package io.github.mjcro.toybox.app.swing.widgets;

import io.github.mjcro.toybox.app.CustomLoggingAppender;
import io.github.mjcro.toybox.app.utils.TextFormat;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatusBarWidget extends JPanel {
    private final JLabel informationLabel = ToyBoxLabels.create("Welcome");
    private final JLabel lastElapsedLabel = ToyBoxLabels.create("", Hints.CENTER);
    private final JLabel memoryUsageLabel = ToyBoxLabels.create("0M", Hints.CENTER);
    private final JLabel threadsCountLabel = ToyBoxLabels.create("1", Hints.CENTER);

    public static StatusBarWidget interactive(ScheduledExecutorService scheduler) {
        StatusBarWidget w = new StatusBarWidget();
        CustomLoggingAppender cla = new CustomLoggingAppender();
        cla.listen(e -> SwingUtilities.invokeLater(() -> w.setInformation(e.getFormattedMessage())));
        cla.listen(e -> {
            Object[] arr = e.getArgumentArray();
            if (arr != null) {
                for (Object o : arr) {
                    if (o instanceof Duration) {
                        SwingUtilities.invokeLater(() -> {
                            w.setLastElapsed((Duration) o);
                        });
                    }
                }
            }
        });

        scheduler.scheduleWithFixedDelay(() -> {
            Runtime runtime = Runtime.getRuntime();
            w.setMemoryUsage(runtime.totalMemory());
            w.setThreadCount(Thread.activeCount());
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return w;
    }

    public StatusBarWidget() {
        super(new BorderLayout());
        Hints.PADDING_NANO.apply(this);
        Hints.PADDING_NORMAL.apply(informationLabel);

        lastElapsedLabel.setToolTipText("Timing of last operation");
        informationLabel.setToolTipText("Information message");
        memoryUsageLabel.setToolTipText("Current memory usage");
        threadsCountLabel.setToolTipText("Threads count");

        JPanel rightSide = new JPanel(new GridLayout(1, 3));
        rightSide.add(wrapBevel(lastElapsedLabel));
        rightSide.add(wrapBevel(memoryUsageLabel));
        rightSide.add(wrapBevel(threadsCountLabel));
        add(new JSeparator(), BorderLayout.PAGE_START);
        BorderLayoutMaster.addCenterRight(this, informationLabel, rightSide);
    }

    private static JPanel wrapBevel(JComponent label) {
        JPanel outer = new JPanel(new BorderLayout());
        Hints.PADDING_NANO.apply(outer);

        JPanel bevel = new JPanel(new BorderLayout());
        Hints.BORDER_LOWERED_BEVEL.apply(bevel);
        outer.add(bevel, BorderLayout.CENTER);

        Hints.PADDING_NANO.apply(label);
        bevel.add(label, BorderLayout.CENTER);
        return outer;
    }

    public void setLastElapsed(Duration duration) {
        lastElapsedLabel.setText(TextFormat.duration(duration));
    }

    public void setInformation(String text) {
        informationLabel.setText(text);
    }

    public void setMemoryUsage(long total) {
        memoryUsageLabel.setText(TextFormat.bytes(total));
    }

    public void setThreadCount(int count) {
        threadsCountLabel.setText(" " + count + " ");
    }

    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        ToyBoxLaF.initialize(false);

        JPanel container = new JPanel(new BorderLayout());
        container.add(new JPanel(), BorderLayout.CENTER);
        container.add(StatusBarWidget.interactive(service), BorderLayout.PAGE_END);

        Components.show(container);
    }
}
