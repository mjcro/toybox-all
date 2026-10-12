package io.github.mjcro.toybox.eve;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EveColorAdapterTest {
    @BeforeAll
    static void install() throws Exception {
        Snapshots.onEdt(() -> {
            EveLookAndFeel.install();
            return Boolean.TRUE;
        });
    }

    @Test
    void recolorsHardcodedWhiteAreaWhenDisplayed() throws Exception {
        final JTextArea area = Snapshots.onEdt(() -> display(new ForeignArea()));
        Assertions.assertEquals(EvePalette.SURFACE, area.getBackground());
        Assertions.assertEquals(EvePalette.TEXT, area.getForeground());
    }

    @Test
    void keepsApplicationChosenColors() throws Exception {
        final JTextArea area = Snapshots.onEdt(() -> {
            final ForeignArea foreign = new ForeignArea();
            foreign.setBackground(Color.YELLOW);
            return display(foreign);
        });
        Assertions.assertEquals(Color.YELLOW, area.getBackground());
    }

    @Test
    void honorsOptOut() throws Exception {
        final JTextArea area = Snapshots.onEdt(() -> {
            final ForeignArea foreign = new ForeignArea();
            foreign.putClientProperty(EveColorAdapter.KEEP_COLORS, Boolean.TRUE);
            return display(foreign);
        });
        Assertions.assertEquals(Color.WHITE, area.getBackground());
    }

    @Test
    void leavesRegularTextAreasAlone() throws Exception {
        final JTextArea area = Snapshots.onEdt(() -> display(new JTextArea()));
        Assertions.assertEquals(EvePalette.SURFACE, area.getBackground());
    }

    private static JTextArea display(JTextArea area) {
        final JPanel host = new JPanel();
        host.add(area);
        host.addNotify();
        return area;
    }

    /**
     * Mimics third-party components (like RTextArea) hardcoding non-UIResource white and black.
     */
    private static final class ForeignArea extends JTextArea {
        ForeignArea() {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }
    }
}
