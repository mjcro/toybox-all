package io.github.mjcro.toybox.app.config;

import io.github.mjcro.toybox.app.settings.storage.SettingsStorageDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@Import(ToysConfiguration.class)
@ComponentScan("io.github.mjcro.toybox.app")
public class MainConfiguration {
    @Bean
    public ScheduledExecutorService daemonExecutor() {
        return Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    @Bean
    public SettingsStorageDispatcher settingsStorage() {
        return new SettingsStorageDispatcher();
    }
}
