package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.events.SetWindowHintEvent;
import io.github.mjcro.toybox.swing.Components;
import io.github.mjcro.toybox.swing.hint.Hints;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLaF;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.AbstractMap;
import java.util.HashMap;
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
        private Consumer<Event> eventConsumer = event -> {
        };

        private final JTextField inputField = ToyBoxTextComponents.createJTextField();
        private final JComboBox<TimeZoneSelection> tz = new JComboBox<>(TimeZoneSelection.items(true));
        private final ParsedInstantDisplay parsedInstantDisplay = new ParsedInstantDisplay();
        private final ModificationSet modificationSet = new ModificationSet();
        private final ShortInformationPanel informationPanel = new ShortInformationPanel();
        private final DefaultListModel<Instant> listModel = new DefaultListModel<>();

        private final List<DateTimeFormatter> formatters = List.of(
                // Format: "Nov 18, 2021, 4:22:24 PM UTC+2"
                // English month names, 12-hour format with AM/PM, and time zone
                new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("MMM d, yyyy, h:mm:ss a VV")
                        .toFormatter(Locale.ENGLISH),

                // Standard macOS date format: "пн 10 березня 2025 11:58:07 EET"
                // Ukrainian format: weekday (short), day, month (short), year, time, and time zone
                new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("E d MMMM yyyy HH:mm:ss z")
                        .toFormatter(new Locale("uk", "UA")),

                // Format: "2024-10-08 12:13:06.810834 UTC"
                // ISO format with microseconds and time zone
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS VV"),


                // Format: "2024-10-08 12:13:06 UTC"
                // Standard ISO format with time zone (no microseconds)
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss VV"),

                // Format: "2024-10-08 12:13:06"
                // Standard ISO format without time zone
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        private final List<BiFunction<String, ZoneId, Instant>> instantParsers = List.of(
                (s, zoneId) -> Instant.from(LocalDateTime.parse(s).atZone(zoneId)),
                (s, zoneId) -> Instant.from(LocalDateTime.parse(s.replaceAll("\\.", "-").replaceAll(" ", "T")).atZone(zoneId)),
                (s, zoneId) -> Instant.from(LocalDate.parse(s).atStartOfDay().atZone(zoneId)),
                (s, zoneId) -> Instant.parse(s),
                (s, zoneId) -> Instant.ofEpochSecond(Long.parseLong(s)),
                (s, zoneId) -> formatters.stream()
                        .map(formatter -> {
                            try {
                                return ZonedDateTime.parse(
                                        formatter.getLocale().getISO3Language().equals("ukr") ? replaceShortMonthWithFull(s) : s,
                                        formatter
                                ).toInstant();
                            } catch (Exception ignored) {
                                return null;
                            }
                        })
                        .filter(inst -> inst != null)
                        .findFirst()
                        .orElseThrow()
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
            applyInstant(instant, null, false);
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
            parsedInstantDisplay.setInstant(instant, null);
            modificationSet.setInstant(instant);
            eventConsumer.accept(new SetWindowHintEvent(instant.toString()));
        }

        private String replaceShortMonthWithFull(String s) {
            Map<String, String> shortToFullMonthMap = new HashMap<>();
            shortToFullMonthMap.put("січ", "січня");
            shortToFullMonthMap.put("лют", "лютого");
            shortToFullMonthMap.put("бер", "березня");
            shortToFullMonthMap.put("кві", "квітня");
            shortToFullMonthMap.put("тра", "травня");
            shortToFullMonthMap.put("чер", "червня");
            shortToFullMonthMap.put("лип", "липня");
            shortToFullMonthMap.put("сер", "серпня");
            shortToFullMonthMap.put("вер", "вересня");
            shortToFullMonthMap.put("жов", "жовтня");
            shortToFullMonthMap.put("лис", "листопада");
            shortToFullMonthMap.put("гру", "грудня");

            for (Map.Entry<String, String> entry : shortToFullMonthMap.entrySet()) {
                s = s.replace(entry.getKey(), entry.getValue());
            }
            return s;
        }

        private void initComponents() {
            super.setLayout(new BorderLayout());

            JPanel header = ToyBoxPanels.verticalRows(
                    informationPanel,
                    Hints.titledBorder("Parse").wrap(buildParsePanel()),
                    Hints.titledBorder("Result").wrap(parsedInstantDisplay),
                    Hints.titledBorder("Modifications").wrap(modificationSet)
            );

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

            JPanel historyPanel = new JPanel(new BorderLayout());
            historyPanel.add(new JScrollPane(previousInstants));
            super.add(Hints.titledBorder("History").wrap(historyPanel), BorderLayout.CENTER);
        }

        private JPanel buildParsePanel() {
            JPanel header = new JPanel(new BorderLayout());
            JPanel headerRight = new JPanel();
            tz.setEditable(false);
            headerRight.add(tz);
            JButton parseButton = ToyBoxButtons.createPrimary("Parse", this::onApplyButtonClick);
            headerRight.add(parseButton);
            header.add(headerRight, BorderLayout.LINE_END);

            JPanel inputFieldContainer = new JPanel(new BorderLayout());
            Hints.PADDING_NORMAL.apply(inputFieldContainer);
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
                        break;
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

        public static Vector<TimeZoneSelection> items(final boolean includeNull) {
            ZoneId local = ZoneId.systemDefault();

            Vector<TimeZoneSelection> v = new Vector<>();
            if (includeNull) {
                v.add(new TimeZoneSelection("", null));
            }
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
        private final JComboBox<TimeZoneSelection> tz = new JComboBox<>(TimeZoneSelection.items(false));

        private Consumer<Instant> setter;
        private Instant instant;

        ModificationSet() {
            super(new BorderLayout());

            JPanel buttons1 = ToyBoxPanels.horizontalGrid(2,
                    constructInstantMod("-24 hours", i -> i.plus(-24, ChronoUnit.HOURS)),
                    constructInstantMod("-1 hour", i -> i.plus(-1, ChronoUnit.HOURS)),
                    constructInstantMod("-1 minute", i -> i.plus(-1, ChronoUnit.MINUTES)),
                    constructInstantMod("-1 second", i -> i.plus(-1, ChronoUnit.SECONDS)),
                    constructInstantMod("+1 second", i -> i.plus(1, ChronoUnit.SECONDS)),
                    constructInstantMod("+1 minute", i -> i.plus(1, ChronoUnit.MINUTES)),
                    constructInstantMod("+1 hour", i -> i.plus(1, ChronoUnit.HOURS)),
                    constructInstantMod("+24 hours", i -> i.plus(24, ChronoUnit.HOURS))
            );

            JPanel buttons2 = ToyBoxPanels.horizontalGrid(2,
                    constructZDTMod("Year begin", t -> t.with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS)),
                    constructZDTMod("Month begin", t -> t.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS)),
                    constructZDTMod("Day begin", t -> t.truncatedTo(ChronoUnit.DAYS)),
                    constructZDTMod("Day end", t -> t.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusSeconds(1)),
                    constructZDTMod("Month end", t -> t.with(TemporalAdjusters.lastDayOfMonth()).truncatedTo(ChronoUnit.DAYS).plusDays(1).minusSeconds(1)),
                    constructZDTMod("Year end", t -> t.with(TemporalAdjusters.lastDayOfYear()).truncatedTo(ChronoUnit.DAYS).plusDays(1).minusSeconds(1))
            );

            JPanel buttons = ToyBoxPanels.verticalRows(2, buttons1, buttons2);

            this.add(buttons, BorderLayout.CENTER);

            JPanel tzSelector = ToyBoxPanels.twoColumnsRight(
                    new AbstractMap.SimpleEntry<>(ToyBoxLabels.create("Time Zone"), tz)
            );

            this.add(tzSelector, BorderLayout.PAGE_START);

            setInstant(null);
        }

        private void setInstant(Instant instant) {
            this.instant = instant;
            boolean enabled = instant != null;
            for (Component c : getComponents()) {
                if (c instanceof Container) {
                    for (Component cc : ((Container) c).getComponents()) {
                        cc.setEnabled(enabled);
                        if (cc instanceof Container) {
                            for (Component ccc : ((Container) cc).getComponents()) {
                                ccc.setEnabled(enabled);
                            }
                        }
                    }
                } else {
                    c.setEnabled(enabled);
                }
            }
        }

        public void setSetter(Consumer<Instant> setter) {
            this.setter = setter;
        }

        private JButton constructInstantMod(String text, UnaryOperator<Instant> mod) {
            return ToyBoxButtons.create(text, (ActionListener) e -> {
                if (instant != null) {
                    Instant modified = mod.apply(instant);
                    Consumer<Instant> s = setter;
                    if (s != null) {
                        s.accept(modified);
                    }
                }
            });
        }

        private JButton constructZDTMod(String text, UnaryOperator<ZonedDateTime> mod) {
            return ToyBoxButtons.create(text, (ActionListener) e -> {
                if (instant != null) {
                    ZoneId zoneId = ((TimeZoneSelection) tz.getSelectedItem()).zoneId;
                    ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
                    Instant modified = mod.apply(zdt).toInstant();
                    Consumer<Instant> s = setter;
                    if (s != null) {
                        s.accept(modified);
                    }
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
            Hints.PADDING_NORMAL.apply(this);
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
        private final JTextField textField = ToyBoxTextComponents.createJTextField(Hints.NOT_EDITABLE_TEXT);

        ParsedResultSingleElement(String label) {
            super(new BorderLayout());
            setBorder(new EmptyBorder(1, 1, 5, 1));
            add(ToyBoxLabels.create(label), BorderLayout.PAGE_START);
            add(textField, BorderLayout.CENTER);
        }

        public void setText(String s) {
            textField.setText(s);
        }
    }

    public static void main(String[] args) {
        ToyBoxLaF.initialize(false);
        Components.show(new Panel());
    }
}
