package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.app.config.MainConfiguration;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;

@Slf4j
public class Application {
    public static boolean
            DEBUG = false,
            DEBUG_COMPONENTS = true,
            DARK_MODE = false;

    public static String
            MAIN_ICON = "toybox-64",
            MAIN_TITLE = "ToyBox",
            WINDOW = "tabWindow",
            VERSION = "v0.2";

    public static void main(String[] args) {
        // Obtaining and propagating settings
        changeSettings(args);
        ToyBoxIcons.DARK_MODE = DARK_MODE;

        startSpringApplication(MainConfiguration.class);
    }

    public static void startSpringApplication(Class<?>... configurationClasses) {
        log.info("Starting Toybox");
        Instant instant = Instant.now();

        // Installing look and feel
        ToyBoxLaF.initialize(DARK_MODE);

        // Debugging appender
        if (DEBUG) {
            new CustomLoggingAppender().listen(e -> System.out.println("APP+" + e.getLevel() + " " + e.getFormattedMessage()));
        }

        // Logs buffer
        new CustomLoggingAppender().listen(LogBuffer.Instance::add);

        // Building application context
        ApplicationContext context = new AnnotationConfigApplicationContext(configurationClasses);

        // Starting
        context.getBean(Application.WINDOW, ApplicationFrame.class).initializeAndShow();
        SwingUtilities.invokeLater(() -> {
            log.info("Toybox initialized in {}", Duration.between(instant, Instant.now()));
        });
    }

    private static void changeSettings(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }

        for (String a : args) {
            if ("-d".equalsIgnoreCase(a) || "-debug".equalsIgnoreCase(a) || "--debug".equalsIgnoreCase(a)) {
                DEBUG = true;
            }
            if ("-mdi".equalsIgnoreCase(a) || "--mdi".equalsIgnoreCase(a)) {
                WINDOW = "mdiWindow";
            }
            if ("-tab".equalsIgnoreCase(a) || "--tab".equalsIgnoreCase(a)) {
                WINDOW = "tabWindow";
            }
            if ("--dark".equalsIgnoreCase(a)) {
                DARK_MODE = true;
            }
        }
    }
}
