package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.app.config.MainConfiguration;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.SwingUtilities;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

/**
 * Main entry point for the ToyBox application.
 *
 * <p>Parses command-line arguments, initializes look-and-feel, starts
 * the Spring application context, and shows the main window.
 */
public class Application {
    private static final @NonNull Logger log = LoggerFactory.getLogger(Application.class);

    /** Internal version string embedded in the build. */
    public static final @NonNull String INTERNAL_VERSION = "v0.4.4";

    /** Whether debug mode is enabled. */
    public static boolean
            DEBUG = false,
            DEBUG_COMPONENTS = true,
            DARK_MODE = false;

    /** Configurable application display properties and window type. */
    public static @NonNull String
            MAIN_ICON = "toybox-64",
            MAIN_TITLE = "ToyBox",
            WINDOW = "tabWindow",
            VERSION = INTERNAL_VERSION;

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(@Nullable String[] args) {
        // Initializing context and showing main window
        startSpringApplication(args, MainConfiguration.class);
    }

    /**
     * Starts Spring-based application.
     *
     * @param args                         command-line arguments
     * @param applicationContextSupplier   supplier for {@link ApplicationContext}
     * @return the created application context
     */
    public static @NonNull ApplicationContext startSpringApplication(
            @Nullable String[] args,
            @NonNull Supplier<@NonNull ApplicationContext> applicationContextSupplier
    ) {
        // Obtaining and propagating settings
        changeSettings(args);
        ToyBoxIcons.DARK_MODE = DARK_MODE;

        // Registering exception handler
        Thread.setDefaultUncaughtExceptionHandler((@NonNull Thread thread, @NonNull Throwable throwable) -> {
            log.error(Slf4jUtil.TOYBOX_MARKER, "Uncaught exception", throwable);
        });

        log.info(Slf4jUtil.TOYBOX_MARKER, "Starting ToyBox");
        Instant instant = Instant.now();

        // Installing look and feel
        ToyBoxLaF.initialize(DARK_MODE);
        log.info(Slf4jUtil.TOYBOX_MARKER, "LaF loaded");

        // Debugging appender
        if (DEBUG) {
            new CustomLoggingAppender().listen(e -> System.out.println("APP+" + e.getLevel() + " " + e.getFormattedMessage()));
        }

        // Logs buffer
        new CustomLoggingAppender().listen(LogBuffer.Instance::add);

        // Building application context
        ApplicationContext context = applicationContextSupplier.get();
        log.info(Slf4jUtil.TOYBOX_MARKER, "Spring ApplicationContext ready");

        // Starting
        ApplicationFrame window = context.getBean(Application.WINDOW, ApplicationFrame.class);
        window.initializeAndShow();
        SwingUtilities.invokeLater(() -> log.info(Slf4jUtil.TOYBOX_MARKER, "ToyBox initialized in {}", Duration.between(instant, Instant.now())));

        // Running initial toy(s)
        if (args != null) {
            Context toyContext = context.getBean(ApplicationFrame.class).getContext();
            for (String arg : args) {
                if (arg.startsWith("-t")) {
                    String className = arg.substring(2);
                    log.info(Slf4jUtil.TOYBOX_MARKER, "Showing startup toy {}", className);
                    SwingUtilities.invokeLater(() -> {
                        toyContext.findAndShow(className, null, true);
                    });
                }
            }
        }

        return context;
    }

    /**
     * Starts Spring-based application using the given configuration classes.
     *
     * @param args                 command-line arguments
     * @param configurationClasses Spring configuration classes
     * @return the created application context
     */
    public static @NonNull ApplicationContext startSpringApplication(@Nullable String[] args, @NonNull Class<?>... configurationClasses) {
        return startSpringApplication(args, () -> new AnnotationConfigApplicationContext(configurationClasses));
    }

    /**
     * Starts Spring-based application without command-line arguments.
     *
     * @param configurationClasses Spring configuration classes
     * @return the created application context
     */
    @SuppressWarnings("NullAway") // null is valid for @Nullable String[] args in the delegate overload
    public static @NonNull ApplicationContext startSpringApplication(@NonNull Class<?>... configurationClasses) {
        return startSpringApplication(null, () -> new AnnotationConfigApplicationContext(configurationClasses));
    }

    /**
     * Parses command-line arguments and updates static configuration fields.
     *
     * @param args command-line arguments, may be {@code null}
     */
    private static void changeSettings(@Nullable String[] args) {
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
