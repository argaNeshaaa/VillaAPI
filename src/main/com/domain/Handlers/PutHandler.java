// src/main/com/domain/Handlers/PutHandler.java
package main.com.domain.Handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import main.com.domain.Models.Villa;
import main.com.domain.Models.Voucher;
import main.com.domain.Models.Customer;

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

    // Handler untuk PUT /villas
    // Asumsi: body request berisi data Villa yang diperbarui
    public static void handleVillas(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT request untuk /villas");
        String requestBody = getRequestBody(httpExchange);
        try {
            // Deserialisasi JSON ke objek Villa
            Villa updatedVilla = OBJECT_MAPPER.readValue(requestBody, Villa.class);
            
            // Logika untuk memperbarui data villa di "database" atau daftar.
            // Biasanya, Anda akan mencari villa berdasarkan ID (yang mungkin ada di updatedVilla
            // atau dari query/path parameter jika ada), lalu memperbaruinya.
            System.out.println("Data villa diterima untuk diperbarui: ID " + updatedVilla.getId() + ", Nama: " + updatedVilla.getNama());

            // Kirim kembali objek villa yang telah diperbarui atau konfirmasi
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, updatedVilla);
        } catch (Exception e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid atau data Villa bermasalah.");
        }
    }

    // Handler untuk PUT /customer
    // Asumsi: body request berisi data Customer yang diperbarui
    public static void handleCustomers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT request untuk /customer");
        String requestBody = getRequestBody(httpExchange);
        try {
            // Deserialisasi JSON ke objek Customer
            Customer updatedCustomer = OBJECT_MAPPER.readValue(requestBody, Customer.class);
            
            // Logika untuk memperbarui data pelanggan di "database" atau daftar.
            System.out.println("Data pelanggan diterima untuk diperbarui: ID " + updatedCustomer.getId() + ", Nama: " + updatedCustomer.getNama());

            // Kirim kembali objek pelanggan yang telah diperbarui atau konfirmasi
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, updatedCustomer);
        } catch (Exception e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid atau data Customer bermasalah.");
        }
    }

    public static void handleVouchers(HttpExchange httpExchange) throws IOException {
        System.out.println("Menangani PUT request untuk /voucher");
        String requestBody = getRequestBody(httpExchange);
        try {
            // Deserialisasi JSON ke objek Voucher
            Voucher updatedVoucher = OBJECT_MAPPER.readValue(requestBody, Voucher.class);
            
            // Logika untuk memperbarui data pelanggan di "database" atau daftar.
            System.out.println("Data pelanggan diterima untuk diperbarui: Kode " + updatedVoucher.getKode() + ", Diskon : " + updatedVoucher.getDiskon());

            // Kirim kembali objek pelanggan yang telah diperbarui atau konfirmasi
            sendJsonResponse(httpExchange, HttpURLConnection.HTTP_OK, updatedVoucher);
        } catch (Exception e) {
            sendErrorJsonResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, "Format JSON tidak valid atau data Voucher bermasalah.");
        }
    }
}
