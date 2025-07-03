package system; 

import system.app.AppServices;
import system.auth.AuthService; // Import AuthService
import system.controllers.AuthController; // Import AuthController
import system.view.LoginForm;
import javax.swing.*;

public class AppEntry { // Hoặc Main
    public static void main(String[] args) {
        // Khởi tạo AppServices (đảm bảo đã có các service cần thiết)
        AppServices appServices = AppServices.getInstance();

        // 1. Lấy AuthService từ AppServices
        AuthService authService = appServices.getAuthService(); // Giả định AppServices có phương thức này

        // 2. Khởi tạo AuthController bằng AuthService
        AuthController authController = new AuthController(authService);

        // Chạy ứng dụng trên Event Dispatch Thread (luôn là cách tốt nhất cho Swing)
        SwingUtilities.invokeLater(() -> {
            // Khi ứng dụng khởi động, hiển thị LoginForm đầu tiên
            LoginForm loginForm = new LoginForm(authController); // Truyền AuthController vào LoginForm
            loginForm.setVisible(true);

            // MainFrame sẽ được hiển thị sau khi đăng nhập thành công từ LoginForm
            // (Không khởi tạo MainFrame trực tiếp ở đây trừ khi đã đăng nhập)
        });
    }
}