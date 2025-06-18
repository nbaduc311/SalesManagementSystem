package system.services.impl;

import system.models.dao.impl.KhachHangDAOImpl;       // Corrected DAO import
import system.models.dao.impl.TaiKhoanNguoiDungDAOImpl; // Corrected DAO import
import system.models.dao.impl.HoaDonDAOImpl;           // Corrected DAO import
import system.models.entity.HoaDon;
import system.models.entity.KhachHang;
import system.models.entity.TaiKhoanNguoiDung;
import system.services.KhachHangService;             // Import the new Service interface
import system.database.DatabaseConnection;             // Corrected DatabaseConnection import

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class KhachHangServiceImpl implements KhachHangService {

    private static KhachHangServiceImpl instance;
    private KhachHangDAOImpl khachHangDAO;
    private TaiKhoanNguoiDungDAOImpl taiKhoanNguoiDungDAO;
    private HoaDonDAOImpl hoaDonDAO;

    // Private constructor to enforce Singleton
    private KhachHangServiceImpl() {
        this.khachHangDAO = KhachHangDAOImpl.getIns();
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAOImpl.getIns();
        this.hoaDonDAO = HoaDonDAOImpl.getIns();
    }

    // Static method to get the Singleton instance
    public static KhachHangServiceImpl getIns() {
        if (instance == null) {
            instance = new KhachHangServiceImpl();
        }
        return instance;
    }

    /**
     * Business operation: 3.1 Add new customer information.
     * Can create an associated user account if the customer wishes to register an account.
     * If the customer does not have an account, duplicate checking is based solely on the phone number (SDT).
     *
     * @param khachHang The KhachHang object containing the information. Requires HoTen, SDT.
     * @param username Account login (can be null or empty if no associated account is created).
     * @param password Account password (only needed if username is not null).
     * @param emailForAccount Email for the user account (only needed if username is not null).
     * @return KhachHang if successfully added, null if failed.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public KhachHang themKhachHang(KhachHang khachHang, String username, String password, String emailForAccount) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Check for duplicate phone number before adding customer (always perform)
            if (khachHangDAO.getBySdt(conn, khachHang.getSdt()) != null) {
                System.err.println("Lỗi: Số điện thoại khách hàng đã tồn tại.");
                conn.rollback();
                return null;
            }

            String maNguoiDung = null;
            // Only process user account creation if username is provided
            if (username != null && !username.trim().isEmpty()) {
                // Check for duplicate username or email account
                if (taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username) != null) {
                    System.err.println("Lỗi: Tên đăng nhập tài khoản đã tồn tại.");
                    conn.rollback();
                    return null;
                }
                // EmailForAccount is required if creating an account
                if (emailForAccount == null || emailForAccount.trim().isEmpty()) {
                    System.err.println("Lỗi: Email cho tài khoản người dùng không được để trống khi tạo tài khoản liên kết.");
                    conn.rollback();
                    return null;
                }
                // 1. Create user account for the customer
                // Ensure LoaiNguoiDung is "Khách hàng" and TrangThaiTaiKhoan is "Hoạt động"
                TaiKhoanNguoiDung newAccount = new TaiKhoanNguoiDung(username, password, emailForAccount, "Khách hàng", new Date(), "Hoạt động");
                TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungDAO.add(conn, newAccount);
                if (createdAccount == null) {
                    throw new SQLException("Không thể tạo tài khoản người dùng cho khách hàng.");
                }
                maNguoiDung = createdAccount.getMaNguoiDung();
                khachHang.setMaNguoiDung(maNguoiDung); // Assign MaNguoiDung to the customer
            } else {
                // If no account is created, ensure MaNguoiDung of KhachHang is null
                khachHang.setMaNguoiDung(null);
            }

            // 2. Add customer information
            KhachHang newKhachHang = khachHangDAO.add(conn, khachHang);
            if (newKhachHang == null) {
                throw new SQLException("Không thể thêm thông tin khách hàng.");
            }

            conn.commit(); // Commit transaction
            return newKhachHang;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm khách hàng: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction
                    System.err.println("Đã rollback giao dịch thêm khách hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            throw e; // Re-throw the exception to allow higher layers to handle it
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Business operation: 3.2 Update customer information.
     * Updates personal information of a customer.
     *
     * @param khachHang The KhachHang object containing updated information (MaKhachHang must be set).
     * @return true if updated successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean capNhatThongTinKhachHang(KhachHang khachHang) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Check if customer exists
            if (khachHangDAO.getById(conn, khachHang.getMaKhachHang()) == null) {
                System.err.println("Lỗi: Không tìm thấy khách hàng với mã: " + khachHang.getMaKhachHang());
                conn.rollback();
                return false;
            }
            // Check for duplicate SDT if SDT changes and it's not the current customer's
            KhachHang existingBySdt = khachHangDAO.getBySdt(conn, khachHang.getSdt());
            if (existingBySdt != null && !existingBySdt.getMaKhachHang().equals(khachHang.getMaKhachHang())) {
                System.err.println("Lỗi: Số điện thoại mới đã được sử dụng bởi khách hàng khác.");
                conn.rollback();
                return false;
            }

            boolean updated = khachHangDAO.update(conn, khachHang);
            conn.commit(); // Commit transaction
            return updated;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật thông tin khách hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch cập nhật khách hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw the exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Business operation: 3.3 Look up customer / 3.4 View detailed customer information.
     * Retrieves all customers.
     *
     * @return A list of all customers.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<KhachHang> getAllKhachHang() throws SQLException {
        // Assuming KhachHangDAOImpl.getAll() manages its own connection internally.
        // If it requires a connection, you'd need to open and close it here.
        // For simplicity, we'll keep the current implementation if the DAO handles it.
        return khachHangDAO.getAll();
    }

    /**
     * Business operation: 3.4 View detailed customer information.
     * Retrieves a customer by ID.
     *
     * @param maKhachHang Customer ID.
     * @return KhachHang if found, null if not.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public KhachHang getKhachHangById(String maKhachHang) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return khachHangDAO.getById(conn, maKhachHang);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to indicate a service layer failure
        }
    }

    /**
     * Business operation: Look up customer.
     * Retrieves a customer by phone number.
     *
     * @param sdt Phone number.
     * @return KhachHang if found, null if not.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public KhachHang getKhachHangBySdt(String sdt) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return khachHangDAO.getBySdt(conn, sdt);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy khách hàng theo SĐT: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Business operation: 3.5 View customer purchase history.
     *
     * @param maKhachHang Customer ID.
     * @return A list of invoices for the customer.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<HoaDon> getLichSuMuaHang(String maKhachHang) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Assuming HoaDonDAO has a findByMaKhachHang method for optimization
            return hoaDonDAO.getHoaDonByMaKhachHang(conn, maKhachHang);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch sử mua hàng: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Retrieves customer purchase history by phone number.
     *
     * @param sdt The phone number of the customer.
     * @return A list of invoices associated with the customer's phone number.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<HoaDon> getLichSuMuaHangBySdt(String sdt) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return hoaDonDAO.getHoaDonBySdt(conn, sdt);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch sử mua hàng: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}