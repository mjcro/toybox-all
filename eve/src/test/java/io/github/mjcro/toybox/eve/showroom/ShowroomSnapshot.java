package io.github.mjcro.toybox.eve.showroom;

import java.awt.BorderLayout;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import io.github.mjcro.toybox.eve.EveLookAndFeel;
import io.github.mjcro.toybox.eve.Snapshots;

/**
 * Renders showroom pages into PNG files without opening windows (works headless).
 *
 * <p>Usage: {@code ShowroomSnapshot <outDir> [pageName...]}; with no page names renders
 * every page plus {@code Frame.png} (menu bar, toolbar, tabs and status bar).
 */
public final class ShowroomSnapshot {
    private ShowroomSnapshot() {
    }

    /**
     * Entry point.
     *
     * @param args output directory followed by optional page names.
     * @throws Exception on rendering or IO failure.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException("Usage: ShowroomSnapshot <outDir> [pageName...]");
        }
        final Path out = Path.of(args[0]);
        final List<String> only = List.of(args).subList(1, args.length);
        Snapshots.onEdt(() -> {
            EveLookAndFeel.install();
            for (Map.Entry<String, JComponent> page : ShowroomPages.all().entrySet()) {
                if (only.isEmpty() || only.contains(page.getKey())) {
                    Snapshots.write(Snapshots.render(page.getValue(), 1100, 660), out.resolve(page.getKey() + ".png"));
                }
            }
            if (only.isEmpty() || only.contains("Frame")) {
                final JPanel frame = new JPanel(new BorderLayout());
                frame.add(EveShowroom.createMenuBar(), BorderLayout.NORTH);
                frame.add(EveShowroom.createContent(), BorderLayout.CENTER);
                Snapshots.write(Snapshots.render(frame, 1120, 740), out.resolve("Frame.png"));
            }
            return Boolean.TRUE;
        });
        System.exit(0);
    }
}
