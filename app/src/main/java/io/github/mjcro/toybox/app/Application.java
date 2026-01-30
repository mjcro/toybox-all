package io.github.mjcro.toybox.app;

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
import java.util.function.Supplier;

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
        // Initializing context and showing main window
        startSpringApplication(args, MainConfiguration.class);
    }

    /**
     * Starts Spring-based application.
     *
     * @param applicationContextSupplier Supplier for {@link ApplicationContext}.
     * @return Given application context.
     */
    public static ApplicationContext startSpringApplication(String[] args, Supplier<ApplicationContext> applicationContextSupplier) {
        // Obtaining and propagating settings
        changeSettings(args);
        ToyBoxIcons.DARK_MODE = DARK_MODE;

        // Registering exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            log.error("Uncaught exception", throwable);
        });

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
        ApplicationContext context = applicationContextSupplier.get();
        log.info("Spring ApplicationContext ready");

        // Starting
        ApplicationFrame window = context.getBean(Application.WINDOW, ApplicationFrame.class);
        window.initializeAndShow();
        SwingUtilities.invokeLater(() -> log.info("ToyBox initialized in {}", Duration.between(instant, Instant.now())));

        // Running initial toy(s)
        if (args != null) {
            Context toyContext = context.getBean(ApplicationFrame.class).getContext();
            for (String arg : args) {
                if (arg.startsWith("-t")) {
                    String className = arg.substring(2);
                    log.info("Showing startup toy {}", className);
                    SwingUtilities.invokeLater(() -> {
                        toyContext.findAndShow(className, null, true);
                    });
                }
            }
        }

        return context;
    }

    /**
     * Starts Spring-based application.
     *
     * @param args                 Command line arguments.
     * @param configurationClasses Spring configuration classes.
     * @return Created application context.
     */
    public static ApplicationContext startSpringApplication(String[] args, Class<?>... configurationClasses) {
        return startSpringApplication(args, () -> new AnnotationConfigApplicationContext(configurationClasses));
    }

    /**
     * Starts Spring-based application.
     *
     * @param configurationClasses Spring configuration classes.
     * @return Created application context.
     */
    public static ApplicationContext startSpringApplication(Class<?>... configurationClasses) {
        return startSpringApplication(null, () -> new AnnotationConfigApplicationContext(configurationClasses));
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
            if ("--light".equalsIgnoreCase(a)) {
                DARK_MODE = false;
            }
        }
    }
}
