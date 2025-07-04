// src/main/com/domain/Handlers/PutHandler.java
package main.com.domain.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import main.com.domain.Models.Villa;
import main.com.domain.Models.Voucher;
import main.com.domain.Database.DatabaseHelper;
import main.com.domain.Models.Customer;
import main.com.domain.Models.RoomType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class PutHandler {
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

    // Handler untuk PUT /villas/{id}
    public static void handleVillaById(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT /villas/{id}");
        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 3 && parts[1].equals("villas")) {
            try {
                int id = Integer.parseInt(parts[2]);

                String requestBody = getRequestBody(httpExchange);
                Villa updatedVilla = OBJECT_MAPPER.readValue(requestBody, Villa.class);

                // Gunakan ID dari path, bukan dari body
                updatedVilla.setId(id);

                // Validasi field penting
                if (updatedVilla.getName() == null || updatedVilla.getDescription() == null || updatedVilla.getAddress() == null) {
                    sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Data tidak lengkap.");
                    return;
                }

                boolean success = DatabaseHelper.updateVilla(updatedVilla);
                if (success) {
                    sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, updatedVilla);
                } else {
                    sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Villa tidak ditemukan.");
                }

            } catch (NumberFormatException e) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "ID harus berupa angka.");
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Terjadi kesalahan.");
            }
        } else {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Path tidak sesuai.");
        }
    }

    // Handler untuk PUT /villas/{id}/rooms/{id}
    public static void handleUpdateRoom(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT request untuk /villas/{villaId}/rooms/{roomId}");
        String path = httpExchange.getRequestURI().getPath(); // contoh: /villas/1/rooms/2
        String[] parts = path.split("/");

        if (parts.length != 5) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Path tidak valid.");
            return;
        }

        try {
            int villaId = Integer.parseInt(parts[2]);
            int roomId = Integer.parseInt(parts[4]);

            String requestBody = getRequestBody(httpExchange);
            RoomType updatedRoom = OBJECT_MAPPER.readValue(requestBody, RoomType.class);
            updatedRoom.setId(roomId); // pastikan ID sesuai path
            updatedRoom.setVilla(villaId);

            boolean success = DatabaseHelper.updateRoomType(updatedRoom);
            if (success) {
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, updatedRoom);
            } else {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Room tidak ditemukan.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal memperbarui room.");
        }
    }


    // Handler untuk PUT /customer{id}
    public static void handleUpdateCustomer(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT /customers/{id}");

        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[2]);

        if (!DatabaseHelper.isCustomerExist(id)) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Customer tidak ditemukan.");
            return;
        }

        String body = getRequestBody(httpExchange);
        try {
            Customer customer = OBJECT_MAPPER.readValue(body, Customer.class);

            // Validasi field wajib
            if (customer.getName() == null || customer.getEmail() == null || customer.getPhone() == null) {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Nama, email, dan nomor telepon wajib diisi.");
                return;
            }

            boolean updated = DatabaseHelper.updateCustomer(id, customer);
            if (updated) {
                customer.setId(id); // Pastikan ID sesuai path
                sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, customer);
            } else {
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Gagal mengupdate customer.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid.");
        }
    }


    public static void handleVouchers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT request untuk /voucher");
        String requestBody = getRequestBody(httpExchange);
        try {
            // Deserialisasi JSON ke objek Voucher
            Voucher updatedVoucher = OBJECT_MAPPER.readValue(requestBody, Voucher.class);
            
            // Logika untuk memperbarui data pelanggan di "database" atau daftar.
            System.out.println("Data pelanggan diterima untuk diperbarui: Kode " + updatedVoucher.getCode() + ", Diskon : " + updatedVoucher.getDiscount());

            // Kirim kembali objek pelanggan yang telah diperbarui atau konfirmasi
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, updatedVoucher);
        } catch (Exception e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid atau data Voucher bermasalah.");
        }
    }

    public static void handleVoucherById(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT /vouchers/{id}");
        String path = httpExchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 3 && parts[1].equals("vouchers")) {
            try {
                int id = Integer.parseInt(parts[2]); // Ambil kode dari URL

                String requestBody = getRequestBody(httpExchange);
                Voucher updatedVoucher = OBJECT_MAPPER.readValue(requestBody, Voucher.class);

                // Gunakan code dari path, bukan dari body
                updatedVoucher.setId(id);

                // Validasi field penting
                if (updatedVoucher.getCode() == null ||
                        updatedVoucher.getDescription() == null ||
                        updatedVoucher.getStartDate() == null ||
                        updatedVoucher.getEndDate() == null) {
                    sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Data tidak lengkap.");
                    return;
                }

                boolean success = DatabaseHelper.updateVoucher(updatedVoucher);
                if (success) {
                    sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, updatedVoucher);
                } else {
                    sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, "Voucher tidak ditemukan.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Terjadi kesalahan.");
            }
        } else {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Path tidak sesuai.");
        }
    }

}
