package system.controllers; // Giữ nguyên package

import system.auth.AuthService; // Đã thay đổi import
import system.auth.impl.AuthServiceImpl; // Đã thay đổi import
import system.models.entity.TaiKhoanNguoiDung;

// AuthController có thể là một Singleton hoặc được khởi tạo mỗi khi cần
// Tùy thuộc vào cách bạn muốn quản lý instance của nó.
// Ở đây, tôi sẽ làm nó đơn giản, không phải singleton.
public class AuthController {
    private AuthService authService;

    public AuthController() {
        this.authService = new AuthServiceImpl();
    }

    /**
     * Xử lý logic đăng nhập bằng tên đăng nhập và mật khẩu.
     *
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @return Đối tượng TaiKhoanNguoiDung nếu đăng nhập thành công, ngược lại là null.
     */
    public TaiKhoanNguoiDung loginWithUsername(String username, String password) {
        try {
            // Gọi tầng service để kiểm tra thông tin đăng nhập
            TaiKhoanNguoiDung user = authService.authenticateUser(username, password);
            return user;
        } catch (Exception e) {
            // Log lỗi hoặc xử lý cụ thể hơn tùy vào loại Exception
            System.err.println("Lỗi khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
            return null; // Trả về null khi có lỗi
        }
    }
}