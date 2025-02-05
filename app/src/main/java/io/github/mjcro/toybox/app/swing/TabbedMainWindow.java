package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.app.Application;
import io.github.mjcro.toybox.app.ApplicationEnvironment;
import io.github.mjcro.toybox.app.ApplicationFrame;
import io.github.mjcro.toybox.app.swing.widgets.StatusBarWidget;
import io.github.mjcro.toybox.swing.Icons;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

@Primary
@Component("tabWindow")
@Slf4j
public class TabbedMainWindow extends JFrame implements ApplicationFrame {
    private final ScheduledExecutorService daemonExecutor;
    final JTabbedPane tabbedPane = new JTabbedPane();

    public TabbedMainWindow(
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
        TabbedMainWindowContext context = new TabbedMainWindowContext(environment, this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Application.MAIN_TITLE);
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(StatusBarWidget.interactive(daemonExecutor), BorderLayout.PAGE_END);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        setJMenuBar(new NavigationTreeMenuBuilder().buildMenuBar(context, environment.getRegisteredToys()));
        pack();
    }

    @Override
    public void initializeAndShow() {
        Icons.setMainApplicationIcon(this, Application.MAIN_ICON);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
