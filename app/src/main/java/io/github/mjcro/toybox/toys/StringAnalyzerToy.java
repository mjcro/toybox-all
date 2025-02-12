package io.github.mjcro.toybox.toys;

import com.google.common.hash.Hashing;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.factories.LabelsFactory;
import io.github.mjcro.toybox.swing.widgets.panels.HorizontalComponentsPanel;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class StringAnalyzerToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://text_allcaps", "String Analyzer");
    }

    @Override
    public JPanel build(Context context) {
        Panel panel = new Panel();
        context.getInitialData()
                .filter($ -> $ instanceof CharSequence)
                .map(Object::toString)
                .ifPresent(panel::setSourceText);

        return panel;
    }

    private static final class Panel extends JPanel {
        private final JTextArea sourceText;
        private final JPanel resultPanel;

        public Panel() {
            this.sourceText = new JTextArea();

            Styles.TEXT_MONOSPACED.apply(this.sourceText);

            this.sourceText.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    doAnalyze();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    doAnalyze();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    doAnalyze();
                }
            });

            resultPanel = new HorizontalComponentsPanel();
            Styles.PADDING_NORMAL.apply(resultPanel);

            JSplitPane textAreasPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            textAreasPanel.setResizeWeight(.5d);
            textAreasPanel.add(new JScrollPane(sourceText));
            textAreasPanel.add(resultPanel);

            this.setLayout(new BorderLayout());
            this.add(textAreasPanel, BorderLayout.CENTER);
        }

        public void setSourceText(String s) {
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
                    Styles.PADDING_NORMAL.apply(panel);
                    panel.add(LabelsFactory.create(a.text));
                    resultPanel.add(panel);
                }
                Components.setInheritedPopupRecursively(resultPanel);
                updateUI();
            }

        }

        private static JPanel buildLabelAndText(String string, String value) {
            JLabel label = LabelsFactory.create(string);
            label.setBorder(new EmptyBorder(0, 0, 0, 5));
            JTextField text = new JTextField(value);
            text.setEditable(false);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(label, BorderLayout.LINE_START);
            panel.add(text, BorderLayout.CENTER);
            Styles.PADDING_NORMAL.apply(panel);
            return panel;
        }

        private static void predicate(StringBuilder sb, String name, String value, String sus, Predicate<String> predicate) {
            boolean test = predicate.test(value);
            sb.append(test ? " [+] " : " [ ] ");
            sb.append(name);
            if (test && sus != null) {
                sb.append(sus);
            }
            sb.append("\n");
        }

        private static void regexPredicate(StringBuilder sb, String name, String value, Pattern pattern) {
            boolean test = pattern.matcher(value).find();
            if (test) {
                sb.append(" [+] ").append(name).append("\n");
            }
        }

        @RequiredArgsConstructor
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


            private final String text;
            private final Predicate<String> predicate;
        }

    }

    public static void main(String[] args) {
        var panel = new Panel();
        Components.show(panel);
    }
}
