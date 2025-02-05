package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.util.DelayedInvoker;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutoCompleteJComboBox<T> extends JComboBox<T> {
    private final DelayedInvoker invoker = new DelayedInvoker(Duration.ofSeconds(2));
    private Vector<T> data = null;
    private final Function<String, List<T>> dataProvider;
    private String filter = null;

    public AutoCompleteJComboBox(Function<String, List<T>> dataProvider) {
        super();
        this.dataProvider = dataProvider;
        super.setEditable(true);
        super.setEnabled(false);

        JTextComponent editor = (JTextComponent) this.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                invoker.execute(() -> autoCompleteSetFilter(editor.getText()));
            }
        });
    }

    public void autoCompleteStart() {
        applyFilter(false);
    }

    public void autoCompleteSetFilter(String filter) {
        this.filter = filter;
        applyFilter(true);
    }

    private void applyFilter(boolean popup) {
        SwingUtilities.invokeLater(() -> {
            data = new Vector<>(dataProvider.apply(filter));
            setModel(new DefaultComboBoxModel<>(this.data));
            setSelectedItem(filter);
            System.out.println(filter);
            setEnabled(true);
            if (popup) {
                showPopup();
            }
        });
    }

    public static void main(String[] args) {
        List<String> data = List.of(
                "Foo",
                "Bar",
                "Baz",
                "Hello"
        );

        JPanel panel = new JPanel();
        AutoCompleteJComboBox<String> c = new AutoCompleteJComboBox<>(s -> {
            if (s == null || s.isBlank()) {
                return data.stream().limit(2).collect(Collectors.toList());
            }
            return data.stream().filter($ -> $.contains(s)).collect(Collectors.toList());
        });

        panel.add(c);
        Components.show(panel);
        c.autoCompleteStart();
    }
}
