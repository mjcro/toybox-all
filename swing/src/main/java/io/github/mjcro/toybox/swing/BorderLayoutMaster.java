package io.github.mjcro.toybox.swing;

import org.jspecify.annotations.NonNull;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

/**
 * Utility for applying {@link BorderLayout} to AWT containers
 * and adding components in common layout configurations.
 */
public class BorderLayoutMaster {
    /**
     * Ensures the given container uses a {@link BorderLayout}.
     * Does nothing if the container already has one.
     *
     * @param container container to configure
     */
    public static void setBorderLayout(@NonNull Container container) {
        if (container.getLayout() instanceof BorderLayout) {
            return;
        }

        container.setLayout(new BorderLayout());
    }

    /**
     * Adds a top and center component to the container using {@link BorderLayout}.
     *
     * @param container target container
     * @param top       component placed at the top (PAGE_START)
     * @param center    component placed in the center
     */
    public static void addTopCenter(@NonNull Container container, @NonNull Component top, @NonNull Component center) {
        setBorderLayout(container);
        container.add(top, BorderLayout.PAGE_START);
        container.add(center, BorderLayout.CENTER);
    }

    /**
     * Adds a center and right component to the container using {@link BorderLayout}.
     *
     * @param container target container
     * @param center    component placed in the center
     * @param right     component placed at the right (LINE_END)
     */
    public static void addCenterRight(@NonNull Container container, @NonNull Component center, @NonNull Component right) {
        setBorderLayout(container);
        container.add(center, BorderLayout.CENTER);
        container.add(right, BorderLayout.LINE_END);
    }
}
