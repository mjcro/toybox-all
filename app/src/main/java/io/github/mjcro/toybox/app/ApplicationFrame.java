package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Context;

public interface ApplicationFrame {
    Context getContext();

    void initializeAndShow();
}
