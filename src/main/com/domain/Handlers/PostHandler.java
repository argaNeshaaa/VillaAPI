// src/main/com/domain/Handlers/PostHandler.java
package main.com.domain.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import main.com.domain.Models.Villa;
import main.com.domain.Models.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class PostHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Helper untuk membaca body request JSON
    private static String getRequestBody(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        isr.close();
        return sb.toString();
    }

    // Helper untuk mengirim respons JSON
    private static void sendJsonResponse(HttpExchange httpExchange, int statusCode, Object data) throws IOException {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("data", data);
        String jsonResponse = OBJECT_MAPPER.writeValueAsString(responseMap);

        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = jsonResponse.getBytes("UTF-8");
        httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private static void sendErrorJsonResponse(HttpExchange httpExchange, int statusCode, String message) throws IOException {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "error");
        responseMap.put("message", message);
        String jsonResponse = OBJECT_MAPPER.writeValueAsString(responseMap);

        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] responseBytes = jsonResponse.getBytes("UTF-8");
        httpExchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }


    // Handler untuk POST /villas
    public static void handleVillas(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani POST request untuk /villas");
        String requestBody = getRequestBody(httpExchange);
        try {
            Villa newVilla = OBJECT_MAPPER.readValue(requestBody, Villa.class);
            // Logika untuk menyimpan villa baru ke database atau daftar
            System.out.println("Villa baru diterima: " + newVilla.getNama());
            // Berikan ID baru jika perlu, atau kembalikan objek yang disimpan
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, newVilla);
        } catch (Exception e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid untuk Villa.");
        }
    }

    // Handler untuk POST /customer
    public static void handleCustomers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani POST request untuk /customer");
        String requestBody = getRequestBody(httpExchange);
        try {
            Customer newCustomer = OBJECT_MAPPER.readValue(requestBody, Customer.class);
            // Logika untuk menyimpan pelanggan baru
            System.out.println("Pelanggan baru diterima: " + newCustomer.getNama());
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, newCustomer);
        } catch (Exception e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid untuk Customer.");
        }
    }
}
