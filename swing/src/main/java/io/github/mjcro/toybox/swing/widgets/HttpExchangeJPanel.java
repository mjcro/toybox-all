package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.api.http.HttpExchange;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Panel displaying single HTTP exchange (request and response headers and bodies) in tabs.
 *
 * <p>Bodies are decoded using charset declared in {@code Content-Type} header, UTF-8 otherwise.
 */
public class HttpExchangeJPanel extends JPanel {
    private final @NonNull JTabbedPane tabs = new JTabbedPane();
    private final @NonNull HttpHeadersJPanel requestHeaders = new HttpHeadersJPanel();
    private final @NonNull MultiViewTextAreaOrExceptionPanel requestBody = new MultiViewTextAreaOrExceptionPanel();
    private final @NonNull HttpHeadersJPanel responseHeaders = new HttpHeadersJPanel();
    private final @NonNull MultiViewTextAreaOrExceptionPanel responseBody = new MultiViewTextAreaOrExceptionPanel();

    /**
     * Constructs empty panel.
     */
    public HttpExchangeJPanel() {
        super(new BorderLayout());

        tabs.addTab("Request Headers", requestHeaders);
        tabs.addTab("Request Body", requestBody);
        tabs.addTab("Response Headers", responseHeaders);
        tabs.addTab("Response Body", responseBody);
        add(tabs, BorderLayout.CENTER);

        clear();
    }

    /**
     * Resets all tabs to empty state.
     */
    public void clear() {
        requestHeaders.setViewEmpty();
        setBody(requestBody, null, null);
        responseHeaders.setViewEmpty();
        setBody(responseBody, null, null);
    }

    /**
     * Displays captured exchange.
     *
     * @param exchange exchange to display
     */
    public void setExchange(@NonNull HttpExchange exchange) {
        setRequest(exchange.getRequestHeaders(), exchange.getRequestBody());
        if (exchange.getError().isPresent()) {
            setResponseError(exchange.getError().get());
        } else {
            setResponse(exchange.getResponseHeaders(), exchange.getResponseBody());
        }
    }

    /**
     * Displays request data.
     *
     * @param headers request headers, may be {@code null}
     * @param body    request body, may be {@code null}
     */
    public void setRequest(
            @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> headers,
            byte @Nullable [] body
    ) {
        requestHeaders.setViewHttpHeaders(headers);
        setBody(requestBody, headers, body);
    }

    /**
     * Displays response data.
     *
     * @param headers response headers, may be {@code null}
     * @param body    response body, may be {@code null}
     */
    public void setResponse(
            @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> headers,
            byte @Nullable [] body
    ) {
        responseHeaders.setViewHttpHeaders(headers);
        setBody(responseBody, headers, body);
    }

    /**
     * Displays error occurred instead of response and selects response body tab.
     *
     * @param error error to display
     */
    public void setResponseError(@NonNull Throwable error) {
        responseHeaders.setViewLabel("No response");
        responseBody.setViewException(error);
        tabs.setSelectedComponent(responseBody);
    }

    private static void setBody(
            @NonNull MultiViewTextAreaOrExceptionPanel view,
            @Nullable Map<@NonNull String, @NonNull List<@NonNull String>> headers,
            byte @Nullable [] body
    ) {
        if (body == null || body.length == 0) {
            view.setViewLabel("No body");
        } else {
            view.setViewText(new String(body, charsetOf(headers)));
            view.getTextArea().setCaretPosition(0);
        }
    }

    /**
     * Resolves charset from {@code Content-Type} header.
     *
     * @param headers headers to inspect, may be {@code null}
     * @return declared charset if present and supported, UTF-8 otherwise
     */
    private static @NonNull Charset charsetOf(@Nullable Map<@NonNull String, @NonNull List<@NonNull String>> headers) {
        if (headers != null) {
            for (final Map.Entry<@NonNull String, @NonNull List<@NonNull String>> header : headers.entrySet()) {
                if (!"content-type".equalsIgnoreCase(header.getKey())) {
                    continue;
                }
                for (final String value : header.getValue()) {
                    for (final String part : value.split(";")) {
                        final String p = part.trim();
                        if (p.toLowerCase(Locale.ROOT).startsWith("charset=")) {
                            try {
                                return Charset.forName(p.substring(8).replace("\"", "").trim());
                            } catch (IllegalArgumentException ignored) {
                                // Unknown or malformed charset, falling back to default
                            }
                        }
                    }
                }
            }
        }
        return StandardCharsets.UTF_8;
    }
}
