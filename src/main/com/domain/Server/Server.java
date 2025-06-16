package main.com.domain.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

// Import handler untuk GET, POST, DELETE di package berbeda
import main.com.domain.Handlers.GetHandler;
import main.com.domain.Handlers.PostHandler;
import main.com.domain.Handlers.DeleteHandler;
import main.com.domain.Handlers.PutHandler;

public class Server {
    private HttpServer server;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private class RequestHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) {
            Server.processHttpExchange(httpExchange);
        }
    }

    public Server(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 128);
        server.createContext("/", new RequestHandler());
        server.start();
    }

    public static void processHttpExchange(HttpExchange httpExchange) {
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String path = uri.getPath();

        System.out.printf("Received %s request on path: %s\n", method, path);

        try {
            switch (method) {
                case "GET":
                    // Menentukan tujuan berdasarkan PATH untuk metode GET
                    switch (path) {
                        case "/villas":
                            GetHandler.handleVillas(httpExchange);
                            break;
                        case "/customer":
                            GetHandler.handleCustomers(httpExchange);
                            break;
                        case "/voucher":
                            GetHandler.handleVouchers(httpExchange);
                            break;
                        default:
                            sendNotFoundResponse(httpExchange, "Endpoint GET tidak ditemukan.");
                            break;
                    }
                    break;
                case "POST":
                    // Menentukan tujuan berdasarkan PATH untuk metode POST
                    switch (path) {
                        case "/villas":
                            PostHandler.handleVillas(httpExchange);
                            break;
                        case "/customer":
                            PostHandler.handleCustomers(httpExchange);
                            break;
                        // Tambahkan case untuk /voucher jika ada POST untuk voucher
                        default:
                            sendNotFoundResponse(httpExchange, "Endpoint POST tidak ditemukan.");
                            break;
                    }
                    break;
                case "PUT": // Contoh untuk PUT request
                    // Menentukan tujuan berdasarkan PATH untuk metode PUT
                    switch (path) {
                        case "/villas":
                            // Misal: Update villa
                            PutHandler.handleVillas(httpExchange);
                            sendNotImplementedResponse(httpExchange, "PUT /villas belum diimplementasikan.");
                            break;
                        case "/customer":
                            PutHandler.handleCustomers(httpExchange);
                            break;
                        case "/voucher":
                            PutHandler.handleVouchers(httpExchange);
                        default:
                            sendNotFoundResponse(httpExchange, "Endpoint PUT tidak ditemukan.");
                            break;
                    }
                    break;
                case "DELETE":
                    // Menentukan tujuan berdasarkan PATH untuk metode DELETE
                    switch (path) {
                        case "/villas":
                            DeleteHandler.handleVillas(httpExchange);
                            break;
                        case "/customer":
                            DeleteHandler.handleCustomers(httpExchange); // Asumsi ada handler untuk ini
                            break;
                        default:
                            sendNotFoundResponse(httpExchange, "Endpoint DELETE tidak ditemukan.");
                            break;
                    }
                    break;
                default:
                    // Method tidak didukung
                    sendMethodNotAllowedResponse(httpExchange, "Method Not Allowed");
                    break;
            }
        } catch (Exception e) {
            // Tangani error umum
            System.out.println("Error processing request: " + e.getMessage());
            try {
                String resError = "{\"message\":\"Internal Server Error\"}";
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, resError.length());
                httpExchange.getResponseBody().write(resError.getBytes());
                httpExchange.getResponseBody().close();
            } catch (Exception ex) {
                System.out.println("Error sending error response: " + ex.getMessage());
            }
        }
    }
    private static void sendResponse(HttpExchange httpExchange, int statusCode, String contentType, String responseBody) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", contentType);
        byte[] responseBytes = responseBody.getBytes("UTF-8");
        httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static void sendJsonResponse(HttpExchange httpExchange, int statusCode, Map<String, String> responseMap) throws IOException {
        String jsonResponse = OBJECT_MAPPER.writeValueAsString(responseMap);
        sendResponse(httpExchange, statusCode, "application/json", jsonResponse);
    }

    private static void sendNotFoundResponse(HttpExchange httpExchange, String message) throws IOException {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "error");
        responseMap.put("message", message);
        sendJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, responseMap);
    }

    private static void sendMethodNotAllowedResponse(HttpExchange httpExchange, String message) throws IOException {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "error");
        responseMap.put("message", message);
        sendJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_METHOD, responseMap);
    }

    private static void sendNotImplementedResponse(HttpExchange httpExchange, String message) throws IOException {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "error");
        responseMap.put("message", message);
        sendJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_IMPLEMENTED, responseMap);
    }
}

