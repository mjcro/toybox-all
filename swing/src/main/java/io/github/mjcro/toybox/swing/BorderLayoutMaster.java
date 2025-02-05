package io.github.mjcro.toybox.swing;

import java.awt.*;

public class BorderLayoutMaster {
    public static void setBorderLayout(Container container) {
        if (container.getLayout() instanceof BorderLayout) {
            return;
        }

        container.setLayout(new BorderLayout());
    }

    public static void addTopCenter(Container container, Component top, Component center) {
        setBorderLayout(container);
        container.add(top, BorderLayout.PAGE_START);
        container.add(center, BorderLayout.CENTER);
    }

    public static void addCenterRight(Container container, Component center, Component right) {
        setBorderLayout(container);
        container.add(center, BorderLayout.CENTER);
        container.add(right, BorderLayout.LINE_END);
    }
}
