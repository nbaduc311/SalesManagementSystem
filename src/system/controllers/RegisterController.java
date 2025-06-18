package system.controllers;

import system.view.RegisterForm;
import system.services.UserService;
import system.models.entity.TaiKhoanNguoiDung;

import java.util.Date;

public class RegisterController {
    private RegisterForm view;
    private UserService userService;

    public RegisterController(RegisterForm view) {
        this.view = view;
        // Initialize the service layer
        this.userService = new UserService();
    }

    /**
     * Handles the registration logic based on user input from the form.
     *
     * @param username The username entered by the user.
     * @param email The email entered by the user.
     * @param password The password entered by the user.
     * @param confirmPassword The confirmed password entered by the user.
     */
    public void register(String username, String email, String password, String confirmPassword) {
        // 1. Input Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.showMessage("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.showMessage("Mật khẩu xác nhận không khớp.");
            return;
        }

        if (password.length() < 6) {
            view.showMessage("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!isValidEmail(email)) {
            view.showMessage("Địa chỉ email không hợp lệ.");
            return;
        }

        // 2. Prepare the TaiKhoanNguoiDung object
        TaiKhoanNguoiDung newUser = new TaiKhoanNguoiDung();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // In a real application, hash this password!
        newUser.setLoaiNguoiDung("Khách hàng"); // Default user type
        newUser.setNgayTao(new Date()); // Current date and time
        newUser.setTrangThaiTaiKhoan("Hoạt động"); // Default account status

        // 3. Call the service layer to perform registration
        try {
            TaiKhoanNguoiDung registeredUser = userService.registerUser(newUser);

            if (registeredUser != null) {
                view.showMessage("Đăng ký thành công! Mã người dùng của bạn là: " + registeredUser.getMaNguoiDung());
                view.closeForm(); // Close registration form on success
                // Optionally, open the login form or main application window here
            } else {
                view.showMessage("Đăng ký thất bại. Tên đăng nhập hoặc Email có thể đã tồn tại.");
            }
        } catch (Exception e) {
            view.showMessage("Đã xảy ra lỗi trong quá trình đăng ký: " + e.getMessage());
            e.printStackTrace(); // Log the error for debugging
        }
    }

    /**
     * Simple email validation regex.
     * A more robust validation might be needed in a production application.
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}