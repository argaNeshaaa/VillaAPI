// src/main/com/domain/Handlers/PostHandler.java
package main.com.domain.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import main.com.domain.Models.Villa;
import main.com.domain.Database.DatabaseHelper;
import main.com.domain.Models.Booking;
import main.com.domain.Models.Customer;
import main.com.domain.Models.Review;
import main.com.domain.Models.RoomType;
import main.com.domain.Models.Voucher;


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

            // Validasi input
            if (newVilla.getName() == null || newVilla.getDescription() == null || newVilla.getAddress() == null) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Field tidak boleh kosong.");
                return;
            }

            int insertedId = DatabaseHelper.insertVilla(newVilla);
            newVilla.setId(insertedId);
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, newVilla);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal menyimpan villa.");
        }
    }

    // Handler untuk POST /villas/{id}/rooms
    public static void handlePostRoomByVillaId(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani POST /villas/{id}/rooms");
        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 4 && parts[1].equals("villas") && parts[3].equals("rooms")) {
            try {
                int villaId = Integer.parseInt(parts[2]);
                String body = getRequestBody(httpExchange);

                RoomType room = OBJECT_MAPPER.readValue(body, RoomType.class);
                room.setVilla(villaId); // pakai villa ID dari path, abaikan kalau di body

                int newId = DatabaseHelper.insertRoomType(room);
                if (newId != -1) {
                    room.setId(newId);
                    sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, room);
                } else {
                    sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal menyimpan tipe kamar.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Data kamar tidak valid.");
            }
        } else {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Path tidak sesuai.");
        }
    }


    // Handler untuk POST /customer
    public static void handleCreateCustomer(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani POST /customers");

        String body = getRequestBody(httpExchange);
        try {
            Customer customer = OBJECT_MAPPER.readValue(body, Customer.class);

            if (customer.getName() == null || customer.getEmail() == null) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Nama dan email wajib diisi.");
                return;
            }

            int newId = DatabaseHelper.insertCustomer(customer);
            if (newId > 0) {
                customer.setId(newId);
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, customer);
            } else {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal menambahkan customer.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid.");
        }
    }
    public static void handleVouchers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani POST request untuk /vouchers");

        String requestBody = getRequestBody(httpExchange);
        try {
            Voucher newVoucher = OBJECT_MAPPER.readValue(requestBody, Voucher.class);

            if (newVoucher.getCode() == null || newVoucher.getDescription() == null ||
                    newVoucher.getStartDate() == null || newVoucher.getEndDate() == null) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Field tidak boleh kosong.");
                return;
            }

            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, newVoucher);

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal menyimpan voucher.");
        }
    }



    // Handler untuk POST customers/{id}/bookings
    public static void handleCreateBookingForCustomer(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath(); // contoh: /customers/3/bookings
        String[] parts = path.split("/");

        if (parts.length >= 4) {
            try {
                int customerId = Integer.parseInt(parts[2]);
                String requestBody = getRequestBody(httpExchange);
                Booking booking = OBJECT_MAPPER.readValue(requestBody, Booking.class);

                // Set customer ID dari path
                booking.setCustomer(customerId);

                int insertedId = DatabaseHelper.insertBooking(booking);
                if (insertedId != -1) {
                    booking.setId(insertedId);
                    sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, booking);
                } else {
                    sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal menyimpan booking.");
                }
            } catch (NumberFormatException e) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "ID customer harus angka.");
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Terjadi kesalahan.");
            }
        } else {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format URL salah.");
        }
    }

    // Handler untuk POST /customers/{id}/bookings{id}/reviews
    public static void handleCreateReviewForBooking(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani POST /customers/{id}/bookings/{id}/reviews");

        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        int customerId = Integer.parseInt(parts[2]);
        int bookingId = Integer.parseInt(parts[4]);

        try {
            // Validasi booking milik customer
            if (!DatabaseHelper.isBookingOwnedByCustomer(bookingId, customerId)) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_FORBIDDEN, "Booking tidak ditemukan untuk customer ini.");
                return;
            }

            // Ambil JSON body
            String body = getRequestBody(httpExchange);
            Review review = OBJECT_MAPPER.readValue(body, Review.class);

            // Validasi bintang
            if (review.getStar() < 1 || review.getStar() > 5) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Nilai bintang harus antara 1 sampai 5.");
                return;
            }

            // Validasi booking ID pada body = path
            if (review.getBooking() != bookingId) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Booking ID pada body tidak cocok dengan URL.");
                return;
            }

            // Simpan review
            boolean success = DatabaseHelper.insertReview(review);
            if (success) {
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_CREATED, review);
            } else {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal menambahkan review. Mungkin review untuk booking ini sudah ada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid.");
        }
    }
}
