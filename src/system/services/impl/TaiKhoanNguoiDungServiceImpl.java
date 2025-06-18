package system.services.impl;

import system.models.dao.impl.TaiKhoanNguoiDungDAOImpl; // Corrected DAO import
import system.models.entity.TaiKhoanNguoiDung;
import system.database.DatabaseConnection;              // Corrected DatabaseConnection import
import system.services.TaiKhoanNguoiDungService;         // Import the new Service interface

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Implementation of {@link TaiKhoanNguoiDungService} providing business logic
 * for user account management. This class adheres to the Singleton pattern.
 */
public class TaiKhoanNguoiDungServiceImpl implements TaiKhoanNguoiDungService {

    private static TaiKhoanNguoiDungServiceImpl instance;
    private TaiKhoanNguoiDungDAOImpl taiKhoanNguoiDungDAO; // Use specific DAO implementation

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the DAO instance using its singleton `getIns()` method.
     */
    private TaiKhoanNguoiDungServiceImpl() {
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAOImpl.getIns();
    }

    /**
     * Provides the Singleton instance of {@code TaiKhoanNguoiDungServiceImpl}.
     *
     * @return The unique instance of {@code TaiKhoanNguoiDungServiceImpl}.
     */
    public static TaiKhoanNguoiDungServiceImpl getIns() {
        if (instance == null) {
            instance = new TaiKhoanNguoiDungServiceImpl();
        }
        return instance;
    }

    // --- BUSINESS OPERATIONS ---

    /**
     * Business operation: Registers a new user account.
     * Checks if the username or email already exists before adding.
     * Defaults {@code LoaiNguoiDung} to "Khách hàng" and {@code TrangThaiTaiKhoan} to "Hoạt động"
     * if the provided parameters are null or empty.
     *
     * @param username The desired username.
     * @param password The account password.
     * @param email The account email.
     * @param loaiNguoiDung The type of user (e.g., "Admin", "Nhân viên", "Khách hàng"). Can be null or empty to default.
     * @param ngayTao The creation date of the account.
     * @param trangThaiTaiKhoan The status of the account (e.g., "Hoạt động", "Bị khóa"). Can be null or empty to default.
     * @return The {@link TaiKhoanNguoiDung} object if registration is successful, otherwise {@code null}
     * (e.g., duplicate username/email, or database error).
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public TaiKhoanNguoiDung dangKyTaiKhoan(String username, String password, String email,
                                             String loaiNguoiDung, Date ngayTao, String trangThaiTaiKhoan) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Input validation
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Tên đăng nhập, mật khẩu và email không được rỗng.");
                conn.rollback();
                return null;
            }

            // Default LoaiNguoiDung
            String finalLoaiNguoiDung = (loaiNguoiDung == null || loaiNguoiDung.trim().isEmpty()) ? "Khách hàng" : loaiNguoiDung.trim();

            // Default TrangThaiTaiKhoan
            String finalTrangThaiTaiKhoan = (trangThaiTaiKhoan == null || trangThaiTaiKhoan.trim().isEmpty()) ? "Hoạt động" : trangThaiTaiKhoan.trim();

            // Check for duplicate username
            if (taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username) != null) {
                System.err.println("Lỗi: Tên đăng nhập '" + username + "' đã tồn tại.");
                conn.rollback();
                return null;
            }
            // Check for duplicate email (assuming TaiKhoanNguoiDungDAO has getTaiKhoanByEmail method)
            if (taiKhoanNguoiDungDAO.getTaiKhoanByEmail(conn, email) != null) {
                System.err.println("Lỗi: Email '" + email + "' đã tồn tại.");
                conn.rollback();
                return null;
            }

            TaiKhoanNguoiDung newAccount = new TaiKhoanNguoiDung(username, password, email, finalLoaiNguoiDung, ngayTao, finalTrangThaiTaiKhoan);
            TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungDAO.add(conn, newAccount);
            conn.commit(); // Commit transaction
            return createdAccount;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi đăng ký tài khoản: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                    System.err.println("Đã rollback giao dịch đăng ký tài khoản.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Re-throw to propagate the exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close(); // Close the connection
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Business operation: Authenticates a user for login.
     *
     * @param username The username provided for login.
     * @param password The password provided for login.
     * @return The {@link TaiKhoanNguoiDung} object if login is successful (username exists, password matches, and account is active),
     * otherwise {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public TaiKhoanNguoiDung dangNhap(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.err.println("Lỗi nghiệp vụ: Tên đăng nhập hoặc mật khẩu không được rỗng.");
            return null;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username);

            if (account == null) {
                System.err.println("Đăng nhập thất bại: Tên đăng nhập không tồn tại.");
                return null;
            }

            // In a real application, you'd hash and compare passwords securely.
            // For now, assuming plain text comparison.
            if (!account.getPassword().equals(password)) {
                System.err.println("Đăng nhập thất bại: Sai mật khẩu.");
                return null;
            }

            if (!account.getTrangThaiTaiKhoan().equals("Hoạt động")) {
                System.err.println("Đăng nhập thất bại: Tài khoản không hoạt động hoặc bị khóa.");
                return null;
            }

            System.out.println("Đăng nhập thành công cho tài khoản: " + username);
            return account;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to propagate the exception
        }
    }

    /**
     * Business operation: Retrieves all user accounts.
     *
     * @return A {@link List} of all {@link TaiKhoanNguoiDung} objects. Returns an empty list if no accounts are found.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public List<TaiKhoanNguoiDung> getAllTaiKhoan() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return taiKhoanNguoiDungDAO.getAll(conn); // Assuming DAO.getAll() now takes a connection
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy tất cả tài khoản: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Business operation: Resets a user's password (e.g., for "Forgot Password" functionality).
     * This method does not require the old password.
     *
     * @param username The username of the account for which to reset the password.
     * @param newPassword The new password.
     * @return {@code true} if the password reset is successful, {@code false} otherwise
     * (e.g., account not found or database error).
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean quenMatKhau(String username, String newPassword) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (username == null || username.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Tên đăng nhập hoặc mật khẩu mới không được rỗng.");
                conn.rollback();
                return false;
            }

            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username);
            if (account != null) {
                account.setPassword(newPassword); // Set new password
                boolean updated = taiKhoanNguoiDungDAO.update(conn, account); // Update in DB
                conn.commit();
                return updated;
            } else {
                System.err.println("Lỗi: Không tìm thấy tài khoản với tên đăng nhập: " + username);
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi quên mật khẩu: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch quên mật khẩu.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e;
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
     * Business operation: Manages (updates) a user account, typically by an Admin.
     * Allows updating the account's status.
     *
     * @param maNguoiDung The ID of the user account to manage.
     * @param newTrangThaiTaiKhoan The new status for the account (e.g., "Hoạt động", "Bị khóa", "Tạm dừng").
     * @return {@code true} if the account update is successful, {@code false} otherwise
     * (e.g., account not found or database error).
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean quanLyTaiKhoan(String maNguoiDung, String newTrangThaiTaiKhoan) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (maNguoiDung == null || maNguoiDung.trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Mã người dùng không được rỗng.");
                conn.rollback();
                return false;
            }
            
            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getById(conn, maNguoiDung);
            if (account == null) {
                System.err.println("Lỗi: Không tìm thấy tài khoản với mã người dùng: " + maNguoiDung);
                conn.rollback();
                return false;
            }

            if (newTrangThaiTaiKhoan != null && !newTrangThaiTaiKhoan.trim().isEmpty()) {
                account.setTrangThaiTaiKhoan(newTrangThaiTaiKhoan.trim());
            } else {
                 System.err.println("Lỗi nghiệp vụ: Trạng thái tài khoản mới không được rỗng.");
                 conn.rollback();
                 return false;
            }

            boolean updated = taiKhoanNguoiDungDAO.update(conn, account);
            conn.commit();
            return updated;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi quản lý tài khoản: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch quản lý tài khoản.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e;
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
     * Business operation: Retrieves a user account by its user ID.
     *
     * @param maNguoiDung The unique ID of the user.
     * @return The {@link TaiKhoanNguoiDung} object if found, otherwise {@code null}.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public TaiKhoanNguoiDung getTaiKhoanByMaNguoiDung(String maNguoiDung) throws SQLException {
        if (maNguoiDung == null || maNguoiDung.trim().isEmpty()) {
            System.err.println("Lỗi nghiệp vụ: Mã người dùng không được rỗng.");
            return null;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            return taiKhoanNguoiDungDAO.getById(conn, maNguoiDung);
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy tài khoản theo mã: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Business operation: Deletes a user account.
     * Important: Consider implementing a "soft delete" (changing status to inactive)
     * instead of a "hard delete" to maintain data integrity, especially if accounts
     * have associated records (orders, employees, customers, etc.).
     *
     * @param maNguoiDung The ID of the user account to delete.
     * @return {@code true} if the account deletion is successful, {@code false} otherwise.
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean xoaTaiKhoan(String maNguoiDung) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (maNguoiDung == null || maNguoiDung.trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Mã người dùng không được rỗng để xóa.");
                conn.rollback();
                return false;
            }

            // TODO: Critical business rule: Check for foreign key constraints before deleting a user account.
            // For example: Is this account linked to any employees, customers, orders, or import/export slips?
            // If so, you should either prevent deletion or change the account status to "Inactive" instead of hard deleting.
            // This prevents data inconsistency and Referential Integrity violations.
            TaiKhoanNguoiDung accountToDelete = taiKhoanNguoiDungDAO.getById(conn, maNguoiDung);
            if (accountToDelete == null) {
                System.err.println("Lỗi: Không tìm thấy tài khoản với mã: " + maNguoiDung + " để xóa.");
                conn.rollback();
                return false;
            }

            boolean deleted = taiKhoanNguoiDungDAO.delete(conn, maNguoiDung);
            conn.commit();
            return deleted;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi xóa tài khoản: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch xóa tài khoản.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e;
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
     * Business operation: Updates a user's password.
     * This method is intended for authenticated users to change their own password,
     * likely requiring the old password for verification (though not enforced here).
     *
     * @param maNguoiDung The ID of the user whose password is to be updated.
     * @param newPassword The new password.
     * @return {@code true} if the password update is successful, {@code false} otherwise.
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean capNhatMatKhau(String maNguoiDung, String newPassword) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (maNguoiDung == null || maNguoiDung.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Mã người dùng hoặc mật khẩu mới không được rỗng.");
                conn.rollback();
                return false;
            }

            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getById(conn, maNguoiDung);
            if (account == null) {
                System.err.println("Lỗi: Không tìm thấy tài khoản với mã người dùng: " + maNguoiDung + " để cập nhật mật khẩu.");
                conn.rollback();
                return false;
            }

            account.setPassword(newPassword.trim());
            boolean updated = taiKhoanNguoiDungDAO.update(conn, account);
            conn.commit();
            return updated;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi cập nhật mật khẩu: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch cập nhật mật khẩu.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e;
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
     * Business operation: Updates a user's email address.
     *
     * @param maNguoiDung The ID of the user whose email is to be updated.
     * @param newEmail The new email address.
     * @return {@code true} if the email update is successful, {@code false} otherwise.
     * @throws SQLException if a database access error occurs during the transaction.
     */
    @Override
    public boolean capNhatEmail(String maNguoiDung, String newEmail) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (maNguoiDung == null || maNguoiDung.trim().isEmpty() || newEmail == null || newEmail.trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Mã người dùng hoặc email mới không được rỗng.");
                conn.rollback();
                return false;
            }

            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getById(conn, maNguoiDung);
            if (account == null) {
                System.err.println("Lỗi: Không tìm thấy tài khoản với mã người dùng: " + maNguoiDung + " để cập nhật email.");
                conn.rollback();
                return false;
            }

            // Check if the new email already exists for another account
            TaiKhoanNguoiDung existingEmailAccount = taiKhoanNguoiDungDAO.getTaiKhoanByEmail(conn, newEmail.trim());
            if (existingEmailAccount != null && !existingEmailAccount.getMaNguoiDung().equals(maNguoiDung)) {
                System.err.println("Lỗi: Email '" + newEmail + "' đã được sử dụng bởi tài khoản khác.");
                conn.rollback();
                return false;
            }

            account.setEmail(newEmail.trim());
            boolean updated = taiKhoanNguoiDungDAO.update(conn, account);
            conn.commit();
            return updated;
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi cập nhật email: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch cập nhật email.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e;
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
}