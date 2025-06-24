package main.com.domain.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.com.domain.Models.Booking;
import main.com.domain.Models.Review;
import main.com.domain.Models.RoomType;
import main.com.domain.Models.Villa;

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

    // GET /villas{id}
    public static Villa getVillaById(int id) {
        String sql = "SELECT * FROM villas WHERE id = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Villa villa = new Villa();
                    villa.setId(rs.getInt("id"));
                    villa.setName(rs.getString("name"));
                    villa.setDescription(rs.getString("description"));
                    villa.setAddress(rs.getString("address"));
                    return villa;
                }
            }

        } catch (SQLException e) {
            System.err.println("Gagal mengambil villa dengan ID: " + id);
            e.printStackTrace();
        }

        return null;
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

    // Menghapus villa
    public static boolean deleteVilla(int id) {
        String sql = "DELETE FROM villas WHERE id = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET /villas{id}/rooms
    public static List<RoomType> getRoomsByVillaId(int villaId) {
        List<RoomType> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room_types WHERE villa = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, villaId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                RoomType room = new RoomType(
                    rs.getInt("id"),
                    rs.getInt("villa"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getInt("capacity"),
                    rs.getInt("price"),
                    rs.getString("bed_size"),
                    rs.getInt("has_desk") == 1,
                    rs.getInt("has_ac") == 1,
                    rs.getInt("has_tv") == 1,
                    rs.getInt("has_wifi") == 1,
                    rs.getInt("has_shower") == 1,
                    rs.getInt("has_hotwater") == 1,
                    rs.getInt("has_fridge") == 1
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // POST /villas/{id}/rooms
    public static int insertRoomType(RoomType room) {
        String sql = "INSERT INTO room_types (villa, name, quantity, capacity, price, bed_size, has_desk, has_ac, has_tv, has_wifi, has_shower, has_hotwater, has_fridge) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, room.getVilla());
            pstmt.setString(2, room.getName());
            pstmt.setInt(3, room.getQuantity());
            pstmt.setInt(4, room.getCapacity());
            pstmt.setInt(5, room.getPrice());
            pstmt.setString(6, room.getBed_size());
            pstmt.setInt(7, room.isHas_desk() ? 1 : 0);
            pstmt.setInt(8, room.isHas_ac() ? 1 : 0);
            pstmt.setInt(9, room.isHas_tv() ? 1 : 0);
            pstmt.setInt(10, room.isHas_wifi() ? 1 : 0);
            pstmt.setInt(11, room.isHas_shower() ? 1 : 0);
            pstmt.setInt(12, room.isHas_hotwater() ? 1 : 0);
            pstmt.setInt(13, room.isHas_fridge() ? 1 : 0);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // ID yang baru dibuat
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Gagal
    }

    // PUT /villas/{id}/rooms/{id}
    public static boolean updateRoomType(RoomType room) {
        String sql = "UPDATE room_types SET name = ?, quantity = ?, capacity = ?, price = ?, bed_size = ?, " +
                "has_desk = ?, has_ac = ?, has_tv = ?, has_wifi = ?, has_shower = ?, has_hotwater = ?, has_fridge = ? " +
                "WHERE id = ? AND villa = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getName());
            pstmt.setInt(2, room.getQuantity());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setInt(4, room.getPrice());
            pstmt.setString(5, room.getBed_size());
            pstmt.setInt(6, room.isHas_desk() ? 1 : 0);
            pstmt.setInt(7, room.isHas_ac() ? 1 : 0);
            pstmt.setInt(8, room.isHas_tv() ? 1 : 0);
            pstmt.setInt(9, room.isHas_wifi() ? 1 : 0);
            pstmt.setInt(10, room.isHas_shower() ? 1 : 0);
            pstmt.setInt(11, room.isHas_hotwater() ? 1 : 0);
            pstmt.setInt(12, room.isHas_fridge() ? 1 : 0);
            pstmt.setInt(13, room.getId());
            pstmt.setInt(14, room.getVilla());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE /villas/{id}/rooms/{id}
    public static boolean deleteRoomById(int villaId, int roomId) {
        String sql = "DELETE FROM room_types WHERE id = ? AND villa = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, villaId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET /villas/{id}/bookings
    public static List<Booking> getBookingsByVillaId(int villaId) {
        String sql = "SELECT b.* FROM bookings b " +
                    "JOIN room_types r ON b.room_type = r.id " +
                    "WHERE r.villa = ?";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:villa_booking.db");
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, villaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setCustomer(rs.getInt("customer"));
                booking.setRoom_type(rs.getInt("room_type"));
                booking.setCheckin_date(rs.getString("checkin_date"));
                booking.setCheckout_date(rs.getString("checkout_date"));
                booking.setPrice(rs.getInt("price"));
                booking.setVoucher(rs.getInt("voucher"));
                booking.setFinal_price(rs.getInt("final_price"));
                booking.setPayment_status(rs.getString("payment_status"));
                booking.setHas_checkedin(rs.getInt("has_checkedin") == 1);
                booking.setHas_checkedout(rs.getInt("has_checkedout") == 1);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    // GET /villas/{id}/reviews
    public static List<Review> getReviewsByVillaId(int villaId) {
        List<Review> reviews = new ArrayList<>();
        String sql = """
            SELECT r.booking, r.star, r.title, r.content
            FROM reviews r
            JOIN bookings b ON r.booking = b.id
            JOIN room_types rt ON b.room_type = rt.id
            WHERE rt.villa = ?
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:villa_booking.db");
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, villaId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setBooking(rs.getInt("booking"));
                review.setStar(rs.getInt("star"));
                review.setTitle(rs.getString("title"));
                review.setContent(rs.getString("content"));
                reviews.add(review);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reviews;
    }



    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
