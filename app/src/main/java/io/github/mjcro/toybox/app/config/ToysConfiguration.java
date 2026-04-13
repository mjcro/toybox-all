package io.github.mjcro.toybox.app.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration that scans the toys package for toy components.
 */
@Configuration
@ComponentScan("io.github.mjcro.toybox.toys")
public class ToysConfiguration {
}
