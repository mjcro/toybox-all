package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.AbstractToy;
import io.github.mjcro.toybox.api.Context;
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
    public static final String INTERNAL_VERSION = "v0.4.1";

    public static boolean
            DEBUG = false,
            DEBUG_COMPONENTS = true,
            DARK_MODE = false;


    public static String
            MAIN_ICON = "toybox-64",
            MAIN_TITLE = "ToyBox",
            WINDOW = "tabWindow",
            VERSION = INTERNAL_VERSION;

    public static void main(String[] args) {
        // Obtaining and propagating settings
        changeSettings(args);
        ToyBoxIcons.DARK_MODE = DARK_MODE;

        // Initializing context and showing main window
        ApplicationContext applicationContext = startSpringApplication(MainConfiguration.class);

        // Running initial toy(s)
        if (args != null) {
            Context toyContext = applicationContext.getBean(ApplicationFrame.class).getContext();
            for (String arg : args) {
                if (arg.startsWith("-t")) {
                    try {
                        String className = arg.substring(2);
                        log.info("Showing startup toy {}", className);
                        Class<?> clazz = Class.forName(className);
                        Object bean = clazz.getConstructor().newInstance();
                        toyContext.show((AbstractToy) bean, null);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static ApplicationContext startSpringApplication(Class<?>... configurationClasses) {
        log.info("Starting ToyBox");
        Instant instant = Instant.now();

        // Installing look and feel
        ToyBoxLaF.initialize(DARK_MODE);
        log.info("LaF loaded");

        // Debugging appender
        if (DEBUG) {
            new CustomLoggingAppender().listen(e -> System.out.println("APP+" + e.getLevel() + " " + e.getFormattedMessage()));
        }

        // Logs buffer
        new CustomLoggingAppender().listen(LogBuffer.Instance::add);

        // Building application context
        ApplicationContext context = new AnnotationConfigApplicationContext(configurationClasses);
        log.info("Spring ApplicationContext ready");

        // Starting
        ApplicationFrame window = context.getBean(Application.WINDOW, ApplicationFrame.class);
        window.initializeAndShow();
        SwingUtilities.invokeLater(() -> {
            log.info("ToyBox initialized in {}", Duration.between(instant, Instant.now()));
        });

        return context;
    }

    private static void changeSettings(String[] args) {
        if (args == null) {
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
