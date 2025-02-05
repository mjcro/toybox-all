package io.github.mjcro.toybox.app.swing.widgets;

import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.app.CustomLoggingAppender;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Hint;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.ToyboxLaF;
import io.github.mjcro.toybox.swing.factories.LabelsFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatusBarWidget extends JPanel {
    private final JLabel informationLabel = LabelsFactory.create("Welcome", Styles.TEXT_SMALL);
    private final JLabel lastElapsedLabel = LabelsFactory.create("", Styles.TEXT_SMALL, Styles.CENTER);
    private final JLabel memoryUsageLabel = LabelsFactory.create("0M", Styles.TEXT_SMALL, Styles.CENTER);
    private final JLabel threadsCountLabel = LabelsFactory.create("1", Styles.TEXT_SMALL, Styles.CENTER);
    private final JLabel versionLabel = LabelsFactory.create(Application.VERSION, Styles.TEXT_SMALL, Styles.CENTER);
    private final JProgressBar progressBar = new JProgressBar();

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
        Styles.PADDING_NANO.apply(this);

        lastElapsedLabel.setToolTipText("Timing of last operation");
        informationLabel.setToolTipText("Information message");
        memoryUsageLabel.setToolTipText("Current memory usage");
        threadsCountLabel.setToolTipText("Threads count");
        versionLabel.setToolTipText("Current application version");

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setBorder(new EmptyBorder(3, 1, 3, 1));
        progressBar.setPreferredSize(new Dimension(70, progressBar.getPreferredSize().height));

        JPanel rightSide = new JPanel(new GridLayout(1, 5));
        rightSide.add(progressBar);
        rightSide.add(wrapBevel(lastElapsedLabel));
        rightSide.add(wrapBevel(memoryUsageLabel));
        rightSide.add(wrapBevel(threadsCountLabel));
        rightSide.add(versionLabel);
        BorderLayoutMaster.addCenterRight(this, wrapBevel(informationLabel), rightSide);
    }

    private static JPanel wrapBevel(JComponent label) {
        JPanel outer = new JPanel(new BorderLayout());
        Styles.PADDING_NANO.apply(outer);

        JPanel bevel = new JPanel(new BorderLayout());
        Styles.BORDER_LOWERED_BEVEL.apply(bevel);
        outer.add(bevel, BorderLayout.CENTER);

        Styles.PADDING_MINI.apply(label);
        bevel.add(label, BorderLayout.CENTER);
        return outer;
    }

    public void setLastElapsed(Duration duration) {
        lastElapsedLabel.setText(
                duration == null ? "" : String.format("%.3fs", duration.toMillis() / 1000.)
        );
    }

    public void setInformation(String text) {
        informationLabel.setText(text);
    }

    public void setMemoryUsage(long total) {
        memoryUsageLabel.setText(total / 1024 / 1024 + "M");
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

        JPanel container = new JPanel(new BorderLayout());
        container.add(new JPanel(), BorderLayout.CENTER);
        container.add(StatusBarWidget.interactive(service), BorderLayout.PAGE_END);

        ToyboxLaF.initialize(false);
        Components.show(container);
    }
}
