// src/main/com/domain/Handlers/DeleteHandler.java
package main.com.domain.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class DeleteHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Helper untuk mengirim respons JSON
    private static void sendJsonResponse(HttpExchange httpExchange, int statusCode, Map<String, String> responseMap) throws IOException {
        String jsonResponse = OBJECT_MAPPER.writeValueAsString(responseMap);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = jsonResponse.getBytes("UTF-8");
        httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    // Handler untuk DELETE /villas
    public static void handleVillas(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani DELETE request untuk /villas");
        // Di sini Anda akan mengimplementasikan logika penghapusan villa.
        // Anda mungkin perlu ID villa dari query parameter atau body.
        // Contoh sederhana:
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("message", "Permintaan DELETE untuk villa diterima. (Logika penghapusan perlu diimplementasikan)");
        sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, responseMap);
    }

    // Handler untuk DELETE /customer
    public static void handleCustomers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani DELETE request untuk /customer");
        // Logika untuk menghapus pelanggan
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("message", "Permintaan DELETE untuk pelanggan diterima. (Logika penghapusan perlu diimplementasikan)");
        sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, responseMap);
    }
}
