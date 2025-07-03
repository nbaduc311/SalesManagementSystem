package system.auth;

import java.sql.Connection;
import java.sql.SQLException;
import system.models.entity.TaiKhoanNguoiDung;

public interface AuthService {
    TaiKhoanNguoiDung login(Connection conn, String username, String password) throws SQLException;
    boolean isAdmin(Connection conn, TaiKhoanNguoiDung taiKhoan) throws SQLException;
    boolean isNhanVien(Connection conn, TaiKhoanNguoiDung taiKhoan) throws SQLException;
    boolean isKhachHang(Connection conn, TaiKhoanNguoiDung taiKhoan) throws SQLException;
    // Thêm các phương thức khác liên quan đến xác thực nếu cần
    boolean registerUser(Connection conn, String username, String password, String email, String userType) throws SQLException;
    boolean changePassword(Connection conn, String username, String oldPassword, String newPassword) throws SQLException;
    boolean resetPasswordViaEmail(Connection conn, String email, String newPassword) throws SQLException;
}