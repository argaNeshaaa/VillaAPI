package main.com.domain.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.com.domain.Models.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:villa_booking.db";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }


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

    // GET /villas?ci_date={checkin_date}&co_date={checkout_date}
    public static List<Villa> getAvailableVillas(String ciDate, String coDate) {
        List<Villa> availableVillas = new ArrayList<>();
        String sql = "SELECT DISTINCT v.* " +
                    "FROM villas v " +
                    "LEFT JOIN room_types r ON v.id = r.villa " +
                    "LEFT JOIN bookings b ON r.id = b.room_type " +
                    "AND (b.checkin_date < ? AND b.checkout_date > ?) " +
                    "WHERE b.id IS NULL";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, coDate);
            pstmt.setString(2, ciDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Villa villa = new Villa();
                villa.setId(rs.getInt("id"));
                villa.setName(rs.getString("name"));
                villa.setDescription(rs.getString("description"));
                villa.setAddress(rs.getString("address"));
                availableVillas.add(villa);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableVillas;
    }

    // GET/Customers
    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                customers.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static List<Voucher> getAllVoucher() {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM vouchers";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setId(rs.getInt("id"));
                voucher.setCode(rs.getString("code"));
                voucher.setDiscount(rs.getInt("discount"));
                voucher.setStartDate(rs.getString("start_date"));
                voucher.setEndDate(rs.getString("end_date"));
                vouchers.add(voucher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vouchers;
    }


    // GET /customers/{id}
    public static Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // GET/customers/{id}/bookings
    public static List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
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

    // GET/customers/{id}/reviews
    public static List<Review> getReviewsByCustomerId(int customerId) {
        List<Review> reviews = new ArrayList<>();
        String sql = """
            SELECT r.booking, r.star, r.title, r.content
            FROM reviews r
            JOIN bookings b ON r.booking = b.id
            WHERE b.customer = ?
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
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

    public static Voucher getVoucherById(int id){
        String sql = "SELECT * FROM vouchers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Enter to voucher");
            if (rs.next()) {
                Voucher voucher = new Voucher();
                voucher.setId(rs.getInt("id"));
                voucher.setCode(rs.getString("code"));
                voucher.setDiscount(rs.getInt("discount"));
                voucher.setStartDate(rs.getString("start_date"));
                voucher.setEndDate(rs.getString("end_date"));
                return voucher;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int insertVoucher(Voucher voucher) {
        String sql = "INSERT INTO vouchers (code, description, discount, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, voucher.getCode());
            pstmt.setString(2, voucher.getDescription());
            pstmt.setInt(3, voucher.getDiscount());
            pstmt.setString(4, voucher.getStartDate());
            pstmt.setString(5, voucher.getEndDate());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Gagal
    }
    public static boolean updateVoucher(Voucher voucher) {
        String sql = "UPDATE vouchers SET description = ?, discount = ?, start_date = ?, end_date = ?, code = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, voucher.getDescription());
            pstmt.setInt(2, voucher.getDiscount());
            pstmt.setString(3, voucher.getStartDate());
            pstmt.setString(4, voucher.getEndDate());
            pstmt.setString(5, voucher.getCode());
            pstmt.setInt(6, voucher.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean deleteVoucherById(int id) {
        String sql = "DELETE FROM vouchers WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
public static int insertCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // ID baru
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // POST/customers/{id}/bookings
    public static int insertBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer, room_type, checkin_date, checkout_date, price, voucher, final_price, payment_status, has_checkedin, has_checkedout) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, booking.getCustomer());
            pstmt.setInt(2, booking.getRoom_type());
            pstmt.setString(3, booking.getCheckin_date());
            pstmt.setString(4, booking.getCheckout_date());
            pstmt.setInt(5, booking.getPrice());
            pstmt.setInt(6, booking.getVoucher());
            pstmt.setInt(7, booking.getFinal_price());
            pstmt.setString(8, booking.getPayment_status());
            pstmt.setInt(9, booking.isHas_checkedin() ? 1 : 0);
            pstmt.setInt(10, booking.isHas_checkedout() ? 1 : 0);

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

    // Helper untuk cek booking milik customer
    public static boolean isBookingOwnedByCustomer(int bookingId, int customerId) {
        String sql = "SELECT * FROM bookings WHERE id = ? AND customer = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, customerId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper untuk insert review customer
    public static boolean insertReview(Review review) {
        String sql = "INSERT INTO reviews (booking, star, title, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getBooking());
            pstmt.setInt(2, review.getStar());
            pstmt.setString(3, review.getTitle());
            pstmt.setString(4, review.getContent());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper untuk update customer
    public static boolean updateCustomer(int id, Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setInt(4, id);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isCustomerExist(int id) {
        String sql = "SELECT 1 FROM customers WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}