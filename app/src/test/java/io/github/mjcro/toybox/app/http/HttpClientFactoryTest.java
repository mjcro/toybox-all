package io.github.mjcro.toybox.app.http;

import com.sun.net.httpserver.HttpServer;
import io.github.mjcro.toybox.api.http.HttpExchange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpClientFactoryTest {
    private HttpServer server;
    private URI uri;
    private HttpCaptureBuffer buffer;
    private HttpClientFactory factory;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/echo", exchange -> {
            byte[] body = exchange.getRequestBody().readAllBytes();
            byte[] response = ("echo:" + new String(body, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("X-Test", "yes");
            exchange.sendResponseHeaders(201, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        server.start();
        uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/echo");
        buffer = new HttpCaptureBuffer(10);
        factory = new HttpClientFactory(buffer);
        client = factory.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void testSendCapturesRequestAndResponse() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder(uri)
                        .header("X-Request", "foo")
                        .POST(HttpRequest.BodyPublishers.ofString("hello"))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("echo:hello", response.body());

        List<HttpExchange> captured = buffer.getAll();
        Assertions.assertEquals(1, captured.size());
        HttpExchange exchange = captured.get(0);
        Assertions.assertEquals("POST", exchange.getMethod());
        Assertions.assertEquals(uri, exchange.getUri());
        Assertions.assertEquals(List.of("foo"), exchange.getRequestHeaders().get("X-Request"));
        Assertions.assertEquals("hello", new String(exchange.getRequestBody(), StandardCharsets.UTF_8));
        Assertions.assertEquals(201, exchange.getStatus().orElseThrow());
        Assertions.assertEquals(List.of("yes"), exchange.getResponseHeaders().get("x-test"));
        Assertions.assertEquals("echo:hello", new String(exchange.getResponseBody(), StandardCharsets.UTF_8));
        Assertions.assertTrue(exchange.getError().isEmpty());
    }

    @Test
    public void testSendAsyncWithoutBody() {
        HttpResponse<byte[]> response = client.sendAsync(
                HttpRequest.newBuilder(uri).GET().build(),
                HttpResponse.BodyHandlers.ofByteArray()
        ).join();

        Assertions.assertEquals("echo:", new String(response.body(), StandardCharsets.UTF_8));
        HttpExchange exchange = buffer.getAll().get(0);
        Assertions.assertEquals("GET", exchange.getMethod());
        Assertions.assertEquals(0, exchange.getRequestBody().length);
        Assertions.assertEquals("echo:", new String(exchange.getResponseBody(), StandardCharsets.UTF_8));
    }

    @Test
    public void testBuilderProducesCapturingClient() throws Exception {
        HttpClient built = factory.newHttpClientBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        built.send(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.discarding());

        Assertions.assertEquals(HttpClient.Redirect.NORMAL, built.followRedirects());
        Assertions.assertEquals(1, buffer.size());
    }

    @Test
    public void testFailureCaptured() throws IOException {
        int port;
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            port = socket.getLocalPort();
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/"))
                .PUT(HttpRequest.BodyPublishers.ofString("data"))
                .build();

        Assertions.assertThrows(IOException.class, () -> client.send(request, HttpResponse.BodyHandlers.ofString()));

        HttpExchange exchange = buffer.getAll().get(0);
        Assertions.assertEquals("PUT", exchange.getMethod());
        Assertions.assertTrue(exchange.getStatus().isEmpty());
        Assertions.assertTrue(exchange.getError().isPresent());
    }

    @Test
    public void testBufferEvictsOldestAndClears() throws Exception {
        for (int i = 0; i < 12; i++) {
            client.send(
                    HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString(String.valueOf(i))).build(),
                    HttpResponse.BodyHandlers.discarding()
            );
        }

        List<HttpExchange> captured = buffer.getAll();
        Assertions.assertEquals(10, buffer.size());
        Assertions.assertEquals("2", new String(captured.get(0).getRequestBody(), StandardCharsets.UTF_8));
        Assertions.assertEquals("11", new String(captured.get(9).getRequestBody(), StandardCharsets.UTF_8));

        buffer.clear();
        Assertions.assertEquals(0, buffer.size());
    }
}
