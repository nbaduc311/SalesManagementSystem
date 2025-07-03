package system.controllers;

import system.auth.AuthService; // Import interface Auth Service
import system.models.entity.TaiKhoanNguoiDung;
import java.sql.Connection;
import java.sql.SQLException;
import system.app.AppServices; // Để lấy Connection

public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public TaiKhoanNguoiDung loginWithUsername(String username, String password) {
        Connection conn = null;
        try {
            conn = AppServices.getInstance().getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            TaiKhoanNguoiDung user = authService.login(conn, username, password);
            if (user != null) {
                AppServices.getInstance().commitTransaction(conn);
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
            AppServices.getInstance().rollbackTransaction(conn);
        } finally {
            AppServices.getInstance().closeConnection(conn);
        }
        return null;
    }

    public boolean registerUser(String username, String password, String email, String userType) {
        Connection conn = null;
        try {
            conn = AppServices.getInstance().getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            boolean success = authService.registerUser(conn, username, password, email, userType);
            if (success) {
                AppServices.getInstance().commitTransaction(conn);
            } else {
                AppServices.getInstance().rollbackTransaction(conn);
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký: " + e.getMessage());
            AppServices.getInstance().rollbackTransaction(conn);
            return false;
        } finally {
            AppServices.getInstance().closeConnection(conn);
        }
    }

    public boolean resetPassword(String email, String newPassword) {
        Connection conn = null;
        try {
            conn = AppServices.getInstance().getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            boolean success = authService.resetPasswordViaEmail(conn, email, newPassword);
            if (success) {
                AppServices.getInstance().commitTransaction(conn);
            } else {
                AppServices.getInstance().rollbackTransaction(conn);
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Lỗi đặt lại mật khẩu: " + e.getMessage());
            AppServices.getInstance().rollbackTransaction(conn);
            return false;
        } finally {
            AppServices.getInstance().closeConnection(conn);
        }
    }
    
    // Bạn có thể thêm các phương thức khác như changePassword ở đây
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Connection conn = null;
        try {
            conn = AppServices.getInstance().getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            boolean success = authService.changePassword(conn, username, oldPassword, newPassword);
            if (success) {
                AppServices.getInstance().commitTransaction(conn);
            } else {
                AppServices.getInstance().rollbackTransaction(conn);
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Lỗi đổi mật khẩu: " + e.getMessage());
            AppServices.getInstance().rollbackTransaction(conn);
            return false;
        } finally {
            AppServices.getInstance().closeConnection(conn);
        }
    }
}