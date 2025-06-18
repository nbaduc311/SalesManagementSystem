package system.auth.impl; // Đã thay đổi package

import system.auth.AuthService; // Đã thay đổi import
import system.models.entity.TaiKhoanNguoiDung;
import system.models.dao.TaiKhoanNguoiDungDAO;
import system.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthServiceImpl implements AuthService { // Triển khai giao diện AuthService mới

    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO;
    
    public AuthServiceImpl() {
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAO.getIns();
    }

    @Override
    public TaiKhoanNguoiDung authenticateUser(String username, String password) throws Exception {
        Connection conn = null;
        TaiKhoanNguoiDung user = null;
        try {
            conn = DatabaseConnection.getConnection(); // Lấy một kết nối mới

            // 1. Lấy thông tin người dùng từ database theo username
            user = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username);

            // 2. Kiểm tra xem người dùng có tồn tại không
            if (user == null) {
                return null; // Tên đăng nhập không tồn tại
            }

            // 3. So sánh mật khẩu
            // LƯU Ý QUAN TRỌNG: Đây là so sánh mật khẩu PLAIN TEXT.
            // Trong ứng dụng THỰC TẾ, bạn PHẢI mã hóa mật khẩu khi đăng ký (dùng BCrypt)
            // và so sánh hash ở đây (ví dụ: BCrypt.checkpw(password, user.getPassword()))
            if (user.getPassword().equals(password)) {
                // Đăng nhập thành công
                // Kiểm tra trạng thái tài khoản
                if ("Hoạt động".equals(user.getTrangThaiTaiKhoan())) {
                    return user;
                } else {
                    // Ném ngoại lệ hoặc trả về null với thông báo cụ thể hơn
                    throw new Exception("Tài khoản của bạn đang ở trạng thái: " + user.getTrangThaiTaiKhoan());
                }
            } else {
                return null; // Sai mật khẩu
            }

        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi xác thực người dùng: " + e.getMessage());
            throw new Exception("Lỗi hệ thống trong quá trình đăng nhập. Vui lòng thử lại sau.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Đóng kết nối sau khi sử dụng
                } catch (SQLException closeEx) {
                    System.err.println("Lỗi khi đóng kết nối CSDL: " + closeEx.getMessage());
                }
            }
        }
    }
}