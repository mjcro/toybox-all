package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.Environment;
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

@Component("mdiWindow")
@Slf4j
public class MDIWindow extends JFrame implements ApplicationFrame {
    private final ScheduledExecutorService daemonExecutor;
    final JDesktopPane desktop = new JDesktopPane();

    public MDIWindow(
            @NonNull Environment environment,
            @NonNull ScheduledExecutorService daemonExecutor
    ) {
        this.daemonExecutor = daemonExecutor;
        initComponents(environment);
        if (environment instanceof ApplicationEnvironment) {
            ((ApplicationEnvironment) environment).setModalParent(this);
        }
    }

    private void initComponents(Environment environment) {
        MDIWindowContext context = new MDIWindowContext(environment, this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Application.MAIN_TITLE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(desktop, BorderLayout.CENTER);

        getContentPane().add(StatusBarWidget.interactive(daemonExecutor), BorderLayout.PAGE_END);

        setJMenuBar(new NavigationTreeMenuBuilder().buildMenuBar(context, environment.getRegisteredToys()));
        pack();
    }

    @Override
    public void initializeAndShow() {
        ToyBoxIcons.setMainApplicationIcon(this, Application.MAIN_ICON);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
