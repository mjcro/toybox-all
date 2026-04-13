package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Context;
import org.jspecify.annotations.NonNull;

/**
 * Represents the main application window frame.
 */
public interface ApplicationFrame {
    /**
     * Returns the application context associated with this frame.
     *
     * @return the context
     */
    @NonNull Context getContext();

    /**
     * Initializes the frame and makes it visible.
     */
    void initializeAndShow();
}
