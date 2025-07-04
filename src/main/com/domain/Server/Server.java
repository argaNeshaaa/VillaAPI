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
                    if (path.matches("/villas/\\d+")) {
                        GetHandler.handleVillaByPath(httpExchange); // GET /villas/{id}
                    } else if (path.equals("/villas")) {
                        String query = httpExchange.getRequestURI().getQuery();
                        if (query != null && query.contains("ci_date") && query.contains("co_date")) {
                            GetHandler.handleVillaAvailability(httpExchange); // ← tangani dengan filter tanggal
                        } else {
                            GetHandler.handleVillas(httpExchange); // ← default
                        }
                    } else if (path.matches("/villas/\\d+/bookings")) {
                        GetHandler.handleBookingsByVillaId(httpExchange);
                    } else if (path.matches("/villas/\\d+/reviews")) {
                        GetHandler.handleReviewsByVillaId(httpExchange);
                    } else if (path.equals("/customers")) {
                        GetHandler.handleCustomers(httpExchange);
                    } else if (path.matches("/customers/\\d+")) {
                        GetHandler.handleCustomerById(httpExchange);
                    } else if (path.matches("/customers/\\d+/bookings")) {
                        GetHandler.handleBookingsByCustomerId(httpExchange);
                    } else if (path.matches("/customers/\\d+/reviews")) {
                        GetHandler.handleReviewsByCustomerId(httpExchange);
                    } else if (path.equals("/voucher")) {
                        GetHandler.handleVouchers(httpExchange);
                    } else if (path.matches("/villas/\\d+/rooms")) {
                            GetHandler.handleRoomsByVillaId(httpExchange);
                    } else {
                        sendNotFoundResponse(httpExchange, "Endpoint GET tidak ditemukan.");
                    }
                    break;

                case "POST":
                    if (path.equals("/villas")) {
                        PostHandler.handleVillas(httpExchange);
                    } else if (path.matches("/villas/\\d+/rooms")) {
                        PostHandler.handlePostRoomByVillaId(httpExchange);
                    } else if (path.equals("/customers")) {
                        PostHandler.handleCreateCustomer(httpExchange);
                    } else if (path.matches("/customers/\\d+/bookings/\\d+/reviews")) {
                        PostHandler.handleCreateReviewForBooking(httpExchange);
                    } else {
                        sendNotFoundResponse(httpExchange, "Endpoint POST tidak ditemukan.");
                    }
                    break;

                case "PUT":
                    if (path.matches("/villas/\\d+")) {
                        PutHandler.handleVillaById(httpExchange); 
                    } else if (path.matches("/villas/\\d+/rooms/\\d+")) {
                        PutHandler.handleUpdateRoom(httpExchange);
                    } else if (path.matches("/customers/\\d+")) {
                        PutHandler.handleUpdateCustomer(httpExchange);
                    } else if (path.equals("/voucher")) {
                        PutHandler.handleVouchers(httpExchange);
                    } else {
                        sendNotFoundResponse(httpExchange, "Endpoint PUT tidak ditemukan.");
                    }
                    break;

                case "DELETE":
                    if (path.matches("/villas/\\d+")) {
                        DeleteHandler.handleVillaByPath(httpExchange); // DELETE /villas/{id}
                    } else if (path.matches("/villas/\\d+/rooms/\\d+")) {
                        DeleteHandler.handleDeleteRoomById(httpExchange);  
                    } else if (path.equals("/customer")) {
                        DeleteHandler.handleCustomers(httpExchange);
                    } else if (path.matches("/customers/\\d+/bookings")) {
                        PostHandler.handleCreateBookingForCustomer(httpExchange);
                    } else {
                        sendNotFoundResponse(httpExchange, "Endpoint DELETE tidak ditemukan.");
                    }
                    break;

                default:
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