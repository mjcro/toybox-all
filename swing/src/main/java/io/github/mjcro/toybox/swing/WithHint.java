package io.github.mjcro.toybox.swing;

public interface WithHint<T extends javax.swing.JComponent> {
    Hint<? super T> getHint();
}
