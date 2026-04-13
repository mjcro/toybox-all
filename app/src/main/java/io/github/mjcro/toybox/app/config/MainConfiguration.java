package io.github.mjcro.toybox.app.config;

import io.github.mjcro.toybox.app.settings.storage.SettingsStorageDispatcher;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Root Spring configuration for the ToyBox application.
 * Imports {@link ToysConfiguration} and scans the app package for components.
 */
@Configuration
@Import(ToysConfiguration.class)
@ComponentScan("io.github.mjcro.toybox.app")
public class MainConfiguration {
    /**
     * Creates a daemon scheduled executor service with 5 threads.
     *
     * @return the scheduled executor service
     */
    @Bean
    public @NonNull ScheduledExecutorService daemonExecutor() {
        return Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Creates the settings storage dispatcher bean.
     *
     * @return the settings storage dispatcher
     */
    @Bean
    public @NonNull SettingsStorageDispatcher settingsStorage() {
        return new SettingsStorageDispatcher();
    }
}
