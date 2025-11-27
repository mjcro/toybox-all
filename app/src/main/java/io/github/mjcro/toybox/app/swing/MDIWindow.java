package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.app.ApplicationEnvironment;
import io.github.mjcro.toybox.app.ApplicationFrame;
import io.github.mjcro.toybox.app.swing.widgets.StatusBarWidget;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiConsumer;

@Component("mdiWindow")
@Slf4j
public class MDIWindow extends JFrame implements ApplicationFrame {
    private final Environment environment;
    private final ScheduledExecutorService daemonExecutor;
    private final MDIWindowContext context;
    final JDesktopPane desktop = new JDesktopPane();

    public MDIWindow(
            @NonNull Environment environment,
            @NonNull ScheduledExecutorService daemonExecutor
    ) {
        this.environment = environment;
        this.context = new MDIWindowContext(environment, this);
        this.daemonExecutor = daemonExecutor;
    }

    @Override
    public Context getContext() {
        return context;
    }

    private void initComponents(Environment environment) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Application.MAIN_TITLE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(desktop, BorderLayout.CENTER);

        BiConsumer<Class<? extends Toy>, Object> toyRunner = (c, d) -> getContext().findAndShow(c, d, true);

        getContentPane().add(StatusBarWidget.interactive(toyRunner, daemonExecutor), BorderLayout.PAGE_END);

        setJMenuBar(new NavigationTreeMenuBuilder().buildMenuBar(getContext(), environment.getRegisteredToys()));
        pack();
    }

    @Override
    public void initializeAndShow() {
        initComponents(environment);
        if (environment instanceof ApplicationEnvironment) {
            ((ApplicationEnvironment) environment).setModalParent(this);
        }

        ToyBoxIcons.setMainApplicationIcon(this, Application.MAIN_ICON);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
