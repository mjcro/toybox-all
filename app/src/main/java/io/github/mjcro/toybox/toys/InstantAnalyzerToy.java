package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.events.SetWindowHintEvent;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.Styles;
import io.github.mjcro.toybox.swing.ToyboxLaF;
import io.github.mjcro.toybox.swing.factories.ButtonsFactory;
import io.github.mjcro.toybox.swing.widgets.panels.ShortInformationPanel;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class InstantAnalyzerToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://time", "Instant Analyzer");
    }

    @Override
    public JPanel build(Context context) {
        Instant instant = context.getInitialData()
                .filter($ -> $ instanceof Instant)
                .map($ -> (Instant) $)
                .orElse(null);
        Panel panel = new Panel(instant);
        panel.setEventConsumer(context::sendEvent);
        return panel;
    }

    private static final class Panel extends JPanel {
        private Instant currentInstant = null;
        private ZoneId currentZoneId = null;
        private Consumer<Event> eventConsumer = event -> {
        };

        private final JTextField inputField = new JTextField();
        private final JComboBox<TimeZoneSelection> tz = new JComboBox<>(TimeZoneSelection.items());
        private final ParsedInstantDisplay parsedInstantDisplay = new ParsedInstantDisplay();
        private final ModificationSet modificationSet = new ModificationSet();
        private final ShortInformationPanel informationPanel = new ShortInformationPanel();
        private final DefaultListModel<Instant> listModel = new DefaultListModel<>();

        private final List<BiFunction<String, ZoneId, Instant>> instantParsers = List.of(
                (s, zoneId) -> Instant.from(LocalDateTime.parse(s).atZone(zoneId)),
                (s, zoneId) -> Instant.from(LocalDate.parse(s).atStartOfDay().atZone(zoneId)),
                (s, zoneId) -> Instant.parse(s),
                (s, zoneId) -> Instant.ofEpochSecond(Long.parseLong(s))
        );

        public Panel() {
            this(null);
        }

        public Panel(Instant instant) {
            super();
            if (instant != null) {
                inputField.setText(instant.toString());
                setInstant(instant, null);
            }
            initComponents();
        }

        private void modifyInstant(Instant instant) {
            applyInstant(instant, currentZoneId, false);
        }

        public void setEventConsumer(Consumer<Event> consumer) {
            this.eventConsumer = consumer != null
                    ? consumer
                    : event -> {
            };
        }

        public void setInstant(Instant instant, ZoneId zoneId) {
            applyInstant(instant, zoneId, true);
        }

        private void applyInstant(Instant instant, ZoneId zoneId, boolean storeHistory) {
            informationPanel.setNone();
            if (instant == null) {
                return;
            }
            if (storeHistory) {
                listModel.insertElementAt(instant, 0);
            }
            currentInstant = instant;
            parsedInstantDisplay.setInstant(instant, null);
            modificationSet.setInstant(instant);
            eventConsumer.accept(new SetWindowHintEvent(instant.toString()));
        }

        private void initComponents() {
            super.setLayout(new BorderLayout());

            JPanel header = new JPanel();
            BoxLayout layout = new BoxLayout(header, BoxLayout.PAGE_AXIS);
            header.setLayout(layout);

            header.add(informationPanel);
            header.add(Styles.titledBorder("Parse").wrap(buildParsePanel()));
            header.add(Styles.titledBorder("Result").wrap(parsedInstantDisplay));
            header.add(Styles.titledBorder("Simple modifications").wrap(modificationSet));

            super.add(header, BorderLayout.PAGE_START);

            modificationSet.setSetter(this::modifyInstant);

            JList<Instant> previousInstants = new JList<>(listModel);
            previousInstants.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    JList list = (JList) evt.getSource();
                    if (evt.getClickCount() == 2) {
                        // Double-click detected
                        int index = list.locationToIndex(evt.getPoint());
                        applyInstant(listModel.get(index), null, false);
                    }
                }
            });

            super.add(Styles.titledBorder("History").wrap(new JScrollPane(previousInstants)), BorderLayout.CENTER);
        }

        private JPanel buildParsePanel() {
            JPanel header = new JPanel(new BorderLayout());
            JPanel headerRight = new JPanel();
            tz.setEditable(false);
            headerRight.add(tz);
            JButton parseButton = ButtonsFactory.create("Parse", this::onApplyButtonClick, Styles.BUTTON_PRIMARY);
            headerRight.add(parseButton);
            header.add(headerRight, BorderLayout.LINE_END);

            JPanel inputFieldContainer = new JPanel(new BorderLayout());
            Styles.PADDING_NORMAL.apply(inputFieldContainer);
            inputFieldContainer.add(inputField, BorderLayout.CENTER);
            header.add(inputFieldContainer, BorderLayout.CENTER);
            return header;
        }

        private void onApplyButtonClick(ActionEvent e) {
            try {
                String text = inputField.getText();
                if (text.isBlank() || "now".equalsIgnoreCase(text) || "current".equalsIgnoreCase(text)) {
                    setInstant(Instant.now().truncatedTo(ChronoUnit.SECONDS), null);
                    return;
                }

                ZoneId zoneId = ((TimeZoneSelection) tz.getSelectedItem()).zoneId;
                Instant instant = null;

                for (BiFunction<String, ZoneId, Instant> parser : instantParsers) {
                    try {
                        instant = parser.apply(text, zoneId);
                    } catch (RuntimeException error) {
                        // ignore
                    }
                }

                if (instant == null) {
                    if (zoneId == null) {
                        throw new RuntimeException("Unable to parse " + text + ", maybe choose timezone?");
                    }
                    throw new RuntimeException("Unable to parse " + text);
                }

                setInstant(instant, zoneId);
            } catch (RuntimeException err) {
                informationPanel.setException(err);
                eventConsumer.accept(new SetWindowHintEvent(null));
                err.printStackTrace();
                ;
            }
        }
    }

    @RequiredArgsConstructor
    private static class TimeZoneSelection {
        final String name;
        final ZoneId zoneId;

        TimeZoneSelection(ZoneId zoneId) {
            this(zoneId.getDisplayName(TextStyle.NARROW, Locale.ROOT), zoneId);
        }

        @Override
        public String toString() {
            return name;
        }

        public static Vector<TimeZoneSelection> items() {
            ZoneId local = ZoneId.systemDefault();

            Vector<TimeZoneSelection> v = new Vector<>();
            v.add(new TimeZoneSelection("", null));
            v.add(new TimeZoneSelection("* UTC", ZoneOffset.UTC));
            v.add(new TimeZoneSelection("* Local - " + local.getDisplayName(TextStyle.NARROW, Locale.ROOT), local));

            ArrayList<TimeZoneSelection> otherZones = new ArrayList<>();
            ZoneId.getAvailableZoneIds().forEach($ -> otherZones.add(new TimeZoneSelection(ZoneId.of($))));
            otherZones.sort(Comparator.comparing(a -> a.name));
            v.addAll(otherZones);

            return v;
        }
    }

    private static class ModificationSet extends JPanel {
        private Consumer<Instant> setter;
        private Instant instant;

        ModificationSet() {
            super(new GridLayout(1, 8));
            Styles.PADDING_NORMAL.apply(this);

            add(construct("-24 hours", i -> i.plus(-24, ChronoUnit.HOURS)));
            add(construct("-1 hour", i -> i.plus(-1, ChronoUnit.HOURS)));
            add(construct("-1 minute", i -> i.plus(-1, ChronoUnit.MINUTES)));
            add(construct("-1 second", i -> i.plus(-1, ChronoUnit.SECONDS)));
            add(construct("+1 second", i -> i.plus(1, ChronoUnit.SECONDS)));
            add(construct("+1 minute", i -> i.plus(1, ChronoUnit.MINUTES)));
            add(construct("+1 hour", i -> i.plus(1, ChronoUnit.HOURS)));
            add(construct("+24 hours", i -> i.plus(24, ChronoUnit.HOURS)));

            setInstant(null);
        }

        private void setInstant(Instant instant) {
            this.instant = instant;
            boolean enabled = instant != null;
            for (Component c : getComponents()) {
                c.setEnabled(enabled);
            }
        }

        public void setSetter(Consumer<Instant> setter) {
            this.setter = setter;
        }

        private JButton construct(String text, UnaryOperator<Instant> mod) {
            return ButtonsFactory.create(text, (ActionListener) e -> {
                Instant modified = mod.apply(instant);
                Consumer<Instant> s = setter;
                if (s != null) {
                    s.accept(modified);
                }
            });
        }
    }

    private static class ParsedInstantDisplay extends JPanel {
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private final ParsedResultSingleElement
                provided = new ParsedResultSingleElement("Given"),
                unix = new ParsedResultSingleElement("Unix"),
                utc = new ParsedResultSingleElement("UTC"),
                iso = new ParsedResultSingleElement("ISO"),
                sanFran = new ParsedResultSingleElement("San Francisco"),
                newYork = new ParsedResultSingleElement("New York"),
                austin = new ParsedResultSingleElement("Austin"),
                beijing = new ParsedResultSingleElement("Beijing"),
                tokyo = new ParsedResultSingleElement("Tokyo"),
                local = new ParsedResultSingleElement("Local " + ZoneId.systemDefault().getDisplayName(TextStyle.NARROW, Locale.ROOT));

        ParsedInstantDisplay() {
            super(new GridLayout(0, 4, 4, 4));
            Styles.PADDING_NORMAL.apply(this);
            super.add(provided);
            super.add(unix);
            super.add(utc);
            super.add(iso);
            super.add(local);
            super.add(sanFran);
            super.add(newYork);
            super.add(austin);
            super.add(beijing);
            super.add(tokyo);
        }

        public void setInstant(Instant instant, ZoneId zoneId) {
            unix.setText(String.valueOf(instant.getEpochSecond()));
            utc.setText(fmt.withZone(ZoneOffset.UTC).format(instant));
            iso.setText(DateTimeFormatter.ISO_DATE_TIME.withZone(zoneId == null ? ZoneOffset.UTC : zoneId).format(instant));
            sanFran.setText(fmt.withZone(ZoneId.of("America/Los_Angeles")).format(instant));
            austin.setText(fmt.withZone(ZoneId.of("US/Central")).format(instant));
            newYork.setText(fmt.withZone(ZoneId.of("America/New_York")).format(instant));
            beijing.setText(fmt.withZone(ZoneId.of("Asia/Shanghai")).format(instant));
            tokyo.setText(fmt.withZone(ZoneId.of("Asia/Tokyo")).format(instant));
            local.setText(fmt.withZone(ZoneId.systemDefault()).format(instant));
            provided.setText(
                    zoneId == null
                            ? ""
                            : fmt.withZone(zoneId).format(instant)
            );
        }
    }

    private static class ParsedResultSingleElement extends JPanel {
        private final JTextField textField = new JTextField();

        ParsedResultSingleElement(String label) {
            super(new BorderLayout());
            setBorder(new EmptyBorder(1, 1, 5, 1));
            textField.setEditable(false);
            add(new JLabel(label), BorderLayout.PAGE_START);
            add(textField, BorderLayout.CENTER);
        }

        public void setText(String s) {
            textField.setText(s);
        }
    }

    public static void main(String[] args) {
        ToyboxLaF.initialize(false);
        Components.show(new Panel());
    }
}
