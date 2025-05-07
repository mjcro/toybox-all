package io.github.mjcro.toybox.swing.widgets;

import javax.swing.*;
import java.awt.*;

public abstract class MultiViewBasicPanel extends JPanel {
    public static final String
            CARD_EMPTY = "empty",
            CARD_EXCEPTION = "exception";

    private final ExceptionDetailsJPanel exceptionDetailsJPanel = new ExceptionDetailsJPanel();

    public MultiViewBasicPanel() {
        super(new CardLayout());

        add(new JPanel(), CARD_EMPTY);
        add(exceptionDetailsJPanel, CARD_EXCEPTION);
    }

    public void setSelectedCard(String card) {
        ((CardLayout) getLayout()).show(this, card);
    }

    public void setViewEmpty() {
        setSelectedCard(CARD_EMPTY);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        exceptionDetailsJPanel.setEnabled(enabled);
    }

    public void setViewException(Throwable t) {
        if (t == null) {
            setViewEmpty();
        } else {
            exceptionDetailsJPanel.setException(t);
            setSelectedCard(CARD_EXCEPTION);
        }
    }
}
