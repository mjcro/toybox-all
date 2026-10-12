package io.github.mjcro.toybox.eve;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Off-screen (headless-friendly) rendering of Swing components into images.
 */
public final class Snapshots {
    private Snapshots() {
    }

    /**
     * Lays out and paints the component into a new image of the given size.
     * MUST be called on the Event Dispatch Thread, see {@link #onEdt(Callable)}.
     *
     * @param c component to render, not null; it MUST NOT have a parent.
     * @param w image width in pixels.
     * @param h image height in pixels.
     * @return non-null RGB image.
     */
    public static BufferedImage render(JComponent c, int w, int h) {
        // Host panel gives root panes a parent; lightweight peers make addNotify-time wiring
        // (e.g. table headers in scroll panes) happen.
        final JPanel host = new JPanel(new BorderLayout());
        host.add(c, BorderLayout.CENTER);
        host.addNotify();
        host.setSize(w, h);
        host.validate();
        final BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = image.createGraphics();
        try {
            g.setColor(EvePalette.BACKGROUND);
            g.fillRect(0, 0, w, h);
            host.paint(g);
        } finally {
            g.dispose();
        }
        return image;
    }

    /**
     * Writes the image as PNG, creating parent folders.
     *
     * @param image image to write, not null.
     * @param file  target file, not null.
     * @throws IOException on write failure.
     */
    public static void write(BufferedImage image, Path file) throws IOException {
        final Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        ImageIO.write(image, "png", file.toFile());
    }

    /**
     * Runs the task on the Event Dispatch Thread and waits for its result.
     *
     * @param task task to run, not null.
     * @param <T>  result type.
     * @return task result.
     * @throws Exception thrown by the task.
     */
    public static <T> T onEdt(Callable<T> task) throws Exception {
        final AtomicReference<T> result = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    result.set(task.call());
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            });
        } catch (InvocationTargetException e) {
            final Throwable cause = e.getCause() instanceof IllegalStateException ise && ise.getCause() != null
                    ? ise.getCause()
                    : e.getCause();
            if (cause instanceof Exception ex) {
                throw ex;
            }
            throw new IllegalStateException(cause);
        }
        return Objects.requireNonNull(result.get(), "EDT task returned null");
    }
}
