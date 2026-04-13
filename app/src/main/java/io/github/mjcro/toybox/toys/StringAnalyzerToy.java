package io.github.mjcro.toybox.toys;

import com.google.common.hash.Hashing;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.widgets.panels.HorizontalComponentsPanel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Toy for analyzing string properties such as length, encoding,
 * character classes, and hash values.
 */
public class StringAnalyzerToy implements Toy {

    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://text_allcaps", "String Analyzer");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        Panel panel = new Panel();
        context.getInitialData()
                .filter($ -> $ instanceof CharSequence)
                .map(Object::toString)
                .ifPresent(panel::setSourceText);

        return panel;
    }

    /**
     * Main panel containing the source text area and analysis results.
     */
    private static final class Panel extends JPanel {

        private final @NonNull JTextArea sourceText;
        private final @NonNull JPanel resultPanel;

        Panel() {
            this.sourceText = ToyBoxTextComponents.createJTextArea(Hints.TEXT_MONOSPACED);

            this.sourceText.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(@NonNull DocumentEvent e) {
                    doAnalyze();
                }

                @Override
                public void removeUpdate(@NonNull DocumentEvent e) {
                    doAnalyze();
                }

                @Override
                public void changedUpdate(@NonNull DocumentEvent e) {
                    doAnalyze();
                }
            });

            resultPanel = new HorizontalComponentsPanel();
            Hints.PADDING_NORMAL.apply(resultPanel);

            JSplitPane textAreasPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            textAreasPanel.setResizeWeight(.5d);
            textAreasPanel.add(new JScrollPane(sourceText));
            textAreasPanel.add(resultPanel);

            this.setLayout(new BorderLayout());
            this.add(textAreasPanel, BorderLayout.CENTER);
        }

        void setSourceText(@NonNull String s) {
            sourceText.setText(s);
            doAnalyze();
        }

        private void doAnalyze() {
            String s = sourceText.getText();
            resultPanel.removeAll();
            if (!s.isEmpty()) {
                resultPanel.add(buildLabelAndText("Length", String.valueOf(s.length())));
                resultPanel.add(buildLabelAndText("Bytes", String.valueOf(s.getBytes(StandardCharsets.UTF_8).length)));
                resultPanel.add(buildLabelAndText("Lines", String.valueOf(s.lines().count())));
                resultPanel.add(buildLabelAndText("SHA256", String.valueOf(Hashing.sha256().hashString(s, StandardCharsets.UTF_8))));

                LinkedHashSet<Assertion> assertions = new LinkedHashSet<>();
                for (Assertion a : Assertion.values()) {
                    if (a.predicate.test(s)) {
                        assertions.add(a);
                    }
                }
                if (assertions.contains(Assertion.LETTERS_LATIN) && assertions.contains(Assertion.LETTERS_CYR)) {
                    assertions.add(Assertion.CYR_LATIN_MIX);
                }

                for (Assertion a : assertions) {
                    JPanel panel = new JPanel();
                    Hints.PADDING_NORMAL.apply(panel);
                    panel.add(ToyBoxLabels.create(a.text));
                    resultPanel.add(panel);
                }
                Components.setInheritedPopupRecursively(resultPanel);
                updateUI();
            }
        }

        private static @NonNull JPanel buildLabelAndText(@NonNull String string, @NonNull String value) {
            JLabel label = ToyBoxLabels.create(string);
            label.setBorder(new EmptyBorder(0, 0, 0, 5));
            JTextField text = ToyBoxTextComponents.createJTextField(value, Hints.NOT_EDITABLE_TEXT);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(label, BorderLayout.LINE_START);
            panel.add(text, BorderLayout.CENTER);
            Hints.PADDING_NORMAL.apply(panel);
            return panel;
        }

        private static void predicate(
                @NonNull StringBuilder sb,
                @NonNull String name,
                @NonNull String value,
                @Nullable String sus,
                @NonNull Predicate<@NonNull String> predicate
        ) {
            boolean test = predicate.test(value);
            sb.append(test ? " [+] " : " [ ] ");
            sb.append(name);
            if (test && sus != null) {
                sb.append(sus);
            }
            sb.append("\n");
        }

        private static void regexPredicate(
                @NonNull StringBuilder sb,
                @NonNull String name,
                @NonNull String value,
                @NonNull Pattern pattern
        ) {
            boolean test = pattern.matcher(value).find();
            if (test) {
                sb.append(" [+] ").append(name).append("\n");
            }
        }

        /**
         * Assertions for detecting character class properties in strings.
         */
        private enum Assertion {
            LEADING_SPACES("Leading spaces", $ -> Character.isWhitespace($.charAt(0))),
            TRAILING_SPACES("Trailing spaces", $ -> Character.isWhitespace($.charAt($.length() - 1))),
            TAB("Tab symbol", $ -> $.contains("\t")),
            NUMBERS("Numbers", $ -> Pattern.compile("\\d+").matcher($).find()),
            PUNCTUATION("Punctuation", $ -> Pattern.compile("\\p{Punct}").matcher($).find()),
            LETTERS("Letters", $ -> Pattern.compile("\\p{L}").matcher($).find()),
            LETTERS_LATIN("Latin", $ -> Pattern.compile("\\p{IsLatin}").matcher($).find()),
            LETTERS_CYR("Cyrillic", $ -> Pattern.compile("\\p{IsCyrillic}").matcher($).find()),
            LETTERS_EMOJI("Emoji", $ -> Pattern.compile("[\uD800-\uDBFF\uDC00-\uDFFF]+").matcher($).find()),
            NON_SPACING("Control characters", $ -> Pattern.compile("\\p{Cntrl}").matcher($).find()),
            CYR_LATIN_MIX("Both cyrillic and latin", $ -> false),
            VOID("Void", $ -> false);

            private final @NonNull String text;
            private final @NonNull Predicate<@NonNull String> predicate;

            Assertion(@NonNull String text, @NonNull Predicate<@NonNull String> predicate) {
                this.text = text;
                this.predicate = predicate;
            }
        }
    }

    /**
     * Main method for standalone testing.
     *
     * @param args command-line arguments
     */
    public static void main(@NonNull String @NonNull [] args) {
        var panel = new Panel();
        Components.show(panel);
    }
}
