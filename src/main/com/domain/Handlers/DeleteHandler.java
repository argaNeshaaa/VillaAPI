// src/main/com/domain/Handlers/DeleteHandler.java
package main.com.domain.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import main.com.domain.Database.DatabaseHelper;

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
    public static void handleVillaByPath(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani DELETE request /villas/{id}");

        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length != 3) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format URL salah.");
            return;
        }

        try {
            int id = Integer.parseInt(parts[2]);
            boolean deleted = DatabaseHelper.deleteVilla(id);

            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Villa berhasil dihapus.");
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, response);
            } else {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Villa tidak ditemukan.");
            }
        } catch (NumberFormatException e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "ID tidak valid.");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Terjadi kesalahan saat menghapus.");
        }
    }

    // Handler untuk DELETE /villas/{id}/rooms/{id}
    public static void handleDeleteRoomById(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani DELETE /villas/{id}/rooms/{id}");

        String path = httpExchange.getRequestURI().getPath(); // contoh: /villas/5/rooms/3
        String[] segments = path.split("/");

        if (segments.length != 5) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Path tidak valid.");
            return;
        }

        try {
            int villaId = Integer.parseInt(segments[2]);
            int roomId = Integer.parseInt(segments[4]);

            boolean success = DatabaseHelper.deleteRoomById(villaId, roomId);
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Kamar berhasil dihapus.");
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, response);
            } else {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Kamar tidak ditemukan.");
            }
        } catch (NumberFormatException e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "ID harus berupa angka.");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Terjadi kesalahan.");
        }
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
    public static void handleDeleteVoucherById(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani DELETE /vouchers/{code}");
        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 3 && parts[1].equals("vouchers")) {
            int id = Integer.parseInt(parts[2]) ;

            try {
                boolean deleted = DatabaseHelper.deleteVoucherByCode(id);
                if (deleted) {
                    sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK,
                            Map.of("message", "Voucher berhasil dihapus."));
                } else {
                    sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND,
                            "Voucher tidak ditemukan.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR,
                        "Terjadi kesalahan saat menghapus voucher.");
            }
        } else {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Path tidak sesuai.");
        }
    }

}
