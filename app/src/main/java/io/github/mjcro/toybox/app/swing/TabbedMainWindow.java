package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.app.ApplicationEnvironment;
import io.github.mjcro.toybox.app.ApplicationFrame;
import io.github.mjcro.toybox.app.swing.widgets.StatusBarWidget;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;

/**
 * Tabbed main window for the ToyBox application.
 *
 * <p>Each toy is displayed as a tab inside a shared {@link JTabbedPane}.
 * This is the default (primary) window implementation.
 */
@Primary
@Component("tabWindow")
public class TabbedMainWindow extends JFrame implements ApplicationFrame {
    private static final @NonNull Logger log = LoggerFactory.getLogger(TabbedMainWindow.class);

    private final @NonNull Environment environment;
    private final @NonNull ScheduledExecutorService daemonExecutor;
    private final @NonNull TabbedMainWindowContext context;
    final @NonNull JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Constructs the tabbed main window.
     *
     * @param environment    the application environment
     * @param daemonExecutor the scheduled executor for background daemon tasks
     */
    public TabbedMainWindow(
            @NonNull Environment environment,
            @NonNull ScheduledExecutorService daemonExecutor
    ) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.context = new TabbedMainWindowContext(environment, this);
        this.daemonExecutor = Objects.requireNonNull(daemonExecutor, "daemonExecutor");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull Context getContext() {
        return context;
    }

    /**
     * Initializes the window's UI components.
     *
     * @param environment the application environment used for toy registration
     */
    private void initComponents(@NonNull Environment environment) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Application.MAIN_TITLE);
        getContentPane().setLayout(new BorderLayout());

        BiConsumer<Class<? extends Toy>, Object> toyRunner = (c, d) -> getContext().findAndShow(c, d, true);

        getContentPane().add(StatusBarWidget.interactive(environment, toyRunner, daemonExecutor), BorderLayout.PAGE_END);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        setJMenuBar(new NavigationTreeMenuBuilder().buildMenuBar(getContext(), environment.getRegisteredToys()));
        pack();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeAndShow() {
        initComponents(environment);
        if (environment instanceof ApplicationEnvironment) {
            ((ApplicationEnvironment) environment).setModalParent(this);
        }

        ToyBoxIcons.setMainApplicationIcon(this, Application.MAIN_ICON);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
