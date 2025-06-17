package main.com.domain.Database;

import main.com.domain.Models.Villa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:villa_booking.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC"); // pastikan driver terdaftar
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Mendapatkan semua villa
    public static List<Villa> getAllVillas() throws SQLException {
        List<Villa> villas = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM villas")) {

            while (rs.next()) {
                Villa villa = new Villa();
                villa.setId(rs.getInt("id"));
                villa.setName(rs.getString("name"));
                villa.setDescription(rs.getString("description"));
                villa.setAddress(rs.getString("address"));
                villas.add(villa);
            }
        }

        return villas;
    }

    // Menambahkan villa baru
    public static int insertVilla(Villa villa) throws SQLException {
        String sql = "INSERT INTO villas (name, description, address) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, villa.getName());
            pstmt.setString(2, villa.getDescription());
            pstmt.setString(3, villa.getAddress());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Gagal menyisipkan villa.");

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // kembalikan ID villa yang baru dibuat
                } else {
                    throw new SQLException("ID villa tidak dihasilkan.");
                }
            }
        }
    }

    // Update data villa
    public static boolean updateVilla(Villa villa) throws SQLException {
        String sql = "UPDATE villas SET name = ?, description = ?, address = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, villa.getName());
            pstmt.setString(2, villa.getDescription());
            pstmt.setString(3, villa.getAddress());
            pstmt.setInt(4, villa.getId());

            return pstmt.executeUpdate() > 0;
        }
    }
}
