package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.util.DelayedInvoker;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A combo box with auto-complete support that fetches suggestions
 * from a data provider function after a configurable delay.
 *
 * @param <T> the type of items in this combo box
 */
public class AutoCompleteJComboBox<T> extends JComboBox<T> {
    private final @NonNull DelayedInvoker invoker = new DelayedInvoker(Duration.ofSeconds(2));
    private @Nullable Vector<@NonNull T> data = null;
    private final @NonNull Function<@Nullable String, @NonNull List<@NonNull T>> dataProvider;
    private @Nullable String filter = null;

    /**
     * Creates a new auto-complete combo box with the given data provider.
     *
     * @param dataProvider function that returns matching items for a given filter string
     */
    public AutoCompleteJComboBox(@NonNull Function<@Nullable String, @NonNull List<@NonNull T>> dataProvider) {
        super();
        this.dataProvider = dataProvider;
        super.setEditable(true);
        super.setEnabled(false);

        final JTextComponent editor = (JTextComponent) this.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(@NonNull KeyEvent e) {
                invoker.execute(() -> autoCompleteSetFilter(editor.getText()));
            }
        });
    }

    /**
     * Initiates the initial auto-complete data load without showing the popup.
     */
    public void autoCompleteStart() {
        applyFilter(false);
    }

    /**
     * Sets the filter text and refreshes the suggestions list.
     *
     * @param filter the filter text to apply
     */
    public void autoCompleteSetFilter(@Nullable String filter) {
        this.filter = filter;
        applyFilter(true);
    }

    /**
     * Applies the current filter by querying the data provider and updating the model.
     *
     * @param popup whether to show the popup after updating
     */
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

    /**
     * Demo entry point for visual testing of the auto-complete combo box.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String @NonNull [] args) {
        final List<@NonNull String> data = List.of(
                "Foo",
                "Bar",
                "Baz",
                "Hello"
        );

        final JPanel panel = new JPanel();
        final AutoCompleteJComboBox<@NonNull String> c = new AutoCompleteJComboBox<>(s -> {
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
