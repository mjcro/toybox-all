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
import org.springframework.stereotype.Component;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;

/**
 * MDI (Multiple Document Interface) main window for the ToyBox application.
 *
 * <p>Each toy is displayed inside a separate {@link javax.swing.JInternalFrame}
 * on a shared {@link JDesktopPane}.
 */
@Component("mdiWindow")
public class MDIWindow extends JFrame implements ApplicationFrame {
    private static final @NonNull Logger log = LoggerFactory.getLogger(MDIWindow.class);

    private final @NonNull Environment environment;
    private final @NonNull ScheduledExecutorService daemonExecutor;
    private final @NonNull MDIWindowContext context;
    final @NonNull JDesktopPane desktop = new JDesktopPane();

    /**
     * Constructs the MDI window.
     *
     * @param environment    the application environment
     * @param daemonExecutor the scheduled executor for background daemon tasks
     */
    public MDIWindow(
            @NonNull Environment environment,
            @NonNull ScheduledExecutorService daemonExecutor
    ) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.context = new MDIWindowContext(environment, this);
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
        getContentPane().add(desktop, BorderLayout.CENTER);

        BiConsumer<Class<? extends Toy>, Object> toyRunner = (c, d) -> getContext().findAndShow(c, d, true);

        getContentPane().add(StatusBarWidget.interactive(environment, toyRunner, daemonExecutor), BorderLayout.PAGE_END);

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
