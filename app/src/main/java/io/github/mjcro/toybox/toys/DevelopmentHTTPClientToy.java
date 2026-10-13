package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.swing.prefab.SilkIcons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxLabels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxPanels;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import io.github.mjcro.toybox.swing.widgets.HttpExchangeJPanel;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Development toy performing HTTP request to arbitrary URL using environment's HTTP client.
 */
public class DevelopmentHTTPClientToy implements Toy {
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_MENU, Menu.TOYBOX_DEVELOPMENT_MENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName(SilkIcons.WORLD_GO, "HTTP client");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel(context.getEnvironment());
    }

    /**
     * Panel with request inputs and exchange result.
     */
    private static class Panel extends JPanel {
        private static final @NonNull Logger log = LoggerFactory.getLogger(Panel.class);

        private final @NonNull Environment environment;
        private final @NonNull JComboBox<@NonNull String> method = new JComboBox<>(
                new String[]{"GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"}
        );
        private final @NonNull JTextField url = ToyBoxTextComponents.createTextField("https://");
        private final @NonNull JButton sendButton = ToyBoxButtons.createPrimary("Send", $ -> onSend());
        private final @NonNull JTextArea requestHeaders = ToyBoxTextComponents.createTextAreaMonospaced();
        private final @NonNull JTextArea requestBody = ToyBoxTextComponents.createTextAreaMonospaced();
        private final @NonNull JLabel resultLabel = ToyBoxLabels.create("No request made yet");
        private final @NonNull HttpExchangeJPanel result = new HttpExchangeJPanel();

        /**
         * Constructs panel.
         *
         * @param environment environment to send requests with
         */
        Panel(@NonNull Environment environment) {
            super(new BorderLayout());
            this.environment = environment;

            method.setEditable(true);
            requestHeaders.setToolTipText("One header per line in \"Name: value\" format");

            JPanel requestLine = new JPanel(new BorderLayout(4, 0));
            requestLine.add(method, BorderLayout.LINE_START);
            requestLine.add(url, BorderLayout.CENTER);
            requestLine.add(sendButton, BorderLayout.LINE_END);

            JPanel request = ToyBoxPanels.horizontalGrid(
                    4,
                    ToyBoxPanels.titledBordered("Request headers", new JScrollPane(requestHeaders)),
                    ToyBoxPanels.titledBordered("Request body", new JScrollPane(requestBody))
            );

            JPanel response = new JPanel(new BorderLayout());
            response.add(resultLabel, BorderLayout.PAGE_START);
            response.add(result, BorderLayout.CENTER);

            add(ToyBoxPanels.titledBordered("Request", requestLine), BorderLayout.PAGE_START);
            add(ToyBoxPanels.verticalSplit(request, ToyBoxPanels.titledBordered("Result", response)), BorderLayout.CENTER);
        }

        /**
         * Builds request from inputs and sends it in background.
         */
        private void onSend() {
            final HttpRequest request;
            final byte[] body = requestBody.getText().getBytes(StandardCharsets.UTF_8);
            try {
                request = buildRequest(body);
            } catch (RuntimeException e) {
                showError("Invalid request", e);
                return;
            }

            sendButton.setEnabled(false);
            resultLabel.setText("Sending " + request.method() + " " + request.uri());
            environment.execute(() -> send(request, body));
        }

        /**
         * Builds request from current inputs.
         *
         * @param body request body bytes
         * @return request
         * @throws IllegalArgumentException if URL, method or headers are invalid
         */
        private @NonNull HttpRequest buildRequest(byte @NonNull [] body) {
            final Object selected = method.getSelectedItem();
            final String m = selected == null ? "" : selected.toString().strip().toUpperCase();
            final HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url.getText().strip()))
                    .timeout(Duration.ofSeconds(30))
                    .method(m, body.length == 0
                            ? HttpRequest.BodyPublishers.noBody()
                            : HttpRequest.BodyPublishers.ofByteArray(body));

            for (final String line : requestHeaders.getText().split("\\R")) {
                if (line.isBlank()) {
                    continue;
                }
                final int colon = line.indexOf(':');
                if (colon < 1) {
                    throw new IllegalArgumentException("Invalid header line \"" + line + "\", expected \"Name: value\"");
                }
                builder.header(line.substring(0, colon).strip(), line.substring(colon + 1).strip());
            }

            return builder.build();
        }

        /**
         * Sends request and displays outcome. Runs off the EDT.
         *
         * @param request request to send
         * @param body    request body bytes
         */
        private void send(@NonNull HttpRequest request, byte @NonNull [] body) {
            final Instant before = Instant.now();
            try {
                final HttpResponse<byte[]> response = environment.sendHttpRequest(request, HttpResponse.BodyHandlers.ofByteArray());
                final Duration elapsed = Duration.between(before, Instant.now());
                log.info(Slf4jUtil.TOYBOX_MARKER, "HTTP {} {} returned {} in {}", request.method(), request.uri(), response.statusCode(), elapsed);
                SwingUtilities.invokeLater(() -> {
                    resultLabel.setText("HTTP " + response.statusCode() + " in " + elapsed.toMillis() + "ms");
                    result.setRequest(request.headers().map(), body);
                    result.setResponse(response.headers().map(), response.body());
                });
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                log.error(Slf4jUtil.TOYBOX_MARKER, "HTTP {} {} failed", request.method(), request.uri(), e);
                SwingUtilities.invokeLater(() -> {
                    showError("Request failed", e);
                    result.setRequest(request.headers().map(), body);
                });
            } finally {
                SwingUtilities.invokeLater(() -> sendButton.setEnabled(true));
            }
        }

        /**
         * Displays error in result area. Must run on the EDT.
         *
         * @param title human-readable error title
         * @param e     error occurred
         */
        private void showError(@NonNull String title, @NonNull Throwable e) {
            resultLabel.setText(title + ": " + e.getMessage());
            result.clear();
            result.setResponseError(e);
        }
    }
}
