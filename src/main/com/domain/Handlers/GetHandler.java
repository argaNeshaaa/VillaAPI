// src/main/com/domain/Handlers/GetHandler.java
package main.com.domain.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import main.com.domain.Models.Villa;
import main.com.domain.Database.DatabaseHelper;
import main.com.domain.Models.Booking;
import main.com.domain.Models.Customer;
import main.com.domain.Models.RoomType;
import main.com.domain.Models.Voucher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    // Handler untuk GET /villas
    public static void handleVillas(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani GET request untuk /villas");

        try {
            List<Villa> villas = DatabaseHelper.getAllVillas();
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, villas);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal mengambil data villa.");
        }
    }

    // Handler untuk GET /villas/{id}
    public static void handleVillaByPath(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani GET request /villas/{id}");

        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length != 3) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format URL salah.");
            return;
        }

        try {
            int id = Integer.parseInt(parts[2]);
            Villa villa = DatabaseHelper.getVillaById(id);
            if (villa != null) {
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, villa);
            } else {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Villa tidak ditemukan.");
            }
        } catch (NumberFormatException e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "ID tidak valid.");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Terjadi kesalahan.");
        }
    }

    // GET /villas/{id}/rooms
    public static void handleRoomsByVillaId(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani GET /villas/{id}/rooms");
        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 4 && parts[1].equals("villas") && parts[3].equals("rooms")) {
            try {
                int villaId = Integer.parseInt(parts[2]);
                List<RoomType> rooms = DatabaseHelper.getRoomsByVillaId(villaId);
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, rooms);
            } catch (NumberFormatException e) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "ID villa tidak valid.");
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Terjadi kesalahan.");
            }
        } else {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Path tidak sesuai.");
        }
    }

    // Handler untuk GET /villas/{id}/bookings
    public static void handleBookingsByVillaId(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath(); // contoh: /villas/5/bookings
        String[] parts = path.split("/");

        if (parts.length >= 4) {
            try {
                int villaId = Integer.parseInt(parts[2]);
                List<Booking> bookings = DatabaseHelper.getBookingsByVillaId(villaId);
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, bookings);
            } catch (NumberFormatException e) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "ID harus berupa angka.");
            }
        } else {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format URL tidak sesuai.");
        }
    }


    // Handler untuk GET /customer
    public static void handleCustomers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani GET request untuk /customer");
        // Logika untuk mengambil daftar pelanggan
        List<Customer> customers = Arrays.asList(
            new Customer("C001", "Andi", "andi@example.com"),
            new Customer("C002", "Beti", "beti@example.com")
        );
        sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, customers);
    }

    // Handler untuk GET /voucher
    public static void handleVouchers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani GET request untuk /voucher");
        // Logika untuk mengambil daftar voucher
        List<Voucher> vouchers = Arrays.asList(
            new Voucher("DISC20", "20%"),
            new Voucher("FREESHIP", "Gratis Pengiriman")
        );
        sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, vouchers);
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


}
