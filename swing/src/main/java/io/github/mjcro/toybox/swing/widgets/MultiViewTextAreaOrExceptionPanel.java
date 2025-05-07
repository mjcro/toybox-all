package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;

import javax.swing.*;

public class MultiViewTextAreaOrExceptionPanel extends MultiViewBasicPanel {
    public static final String
            CARD_TEXT_AREA = "textArea";

    private final JTextArea textArea = ToyBoxTextComponents.createJTextArea();

    public MultiViewTextAreaOrExceptionPanel() {
        add(new JScrollPane(textArea), CARD_TEXT_AREA);
    }

    public MultiViewTextAreaOrExceptionPanel(CharSequence cs) {
        this();
        setViewText(cs);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textArea.setEnabled(enabled);
    }

    public void setViewText(CharSequence cs) {
        if (cs == null) {
            setViewEmpty();

        } else {
            textArea.setText(cs.toString());
            setSelectedCard(CARD_TEXT_AREA);
        }
    }

    public String getViewText() {
        return textArea.getText();
    }
}
