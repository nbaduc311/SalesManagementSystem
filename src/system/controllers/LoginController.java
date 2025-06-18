package system.controllers;

import system.models.entity.LoginModel; // Import LoginModel
import system.models.entity.TaiKhoanNguoiDung; // Import TaiKhoanNguoiDung model
import system.services.TaiKhoanNguoiDungService; // Import TaiKhoanNguoiDungService
import system.view.LoginView; // Import LoginView
import system.view.MainDashBoard; // Giả định có MainDashboard để chuyển đến

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays; // Để so sánh mảng ký tự mật khẩu

/**
 * Lớp LoginController xử lý logic nghiệp vụ cho màn hình đăng nhập.
 * Nó lắng nghe các sự kiện từ LoginView, tương tác với TaiKhoanNguoiDungService
 * và cập nhật LoginView hoặc điều hướng đến màn hình chính.
 */
public class LoginController {
    private LoginView view;
    private TaiKhoanNguoiDungService taiKhoanService; // Service layer

    public LoginController(LoginView view, TaiKhoanNguoiDungService taiKhoanService) {
        this.view = view;
        this.taiKhoanService = taiKhoanService;

        // Đăng ký ActionListener cho nút đăng nhập
        this.view.addLoginListener(new LoginListener());
    }

    /**
     * Lớp nội bộ để xử lý sự kiện click nút đăng nhập.
     * Đây là cách Controller lắng nghe View.
     */
    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            char[] passwordChars = view.getPassword(); // Lấy mật khẩu dưới dạng mảng ký tự
            String password = new String(passwordChars); // Chuyển đổi sang String để truyền vào Service

            // Xóa mảng ký tự mật khẩu ngay sau khi sử dụng để tăng cường bảo mật
            Arrays.fill(passwordChars, ' '); 

            if (username.isEmpty() || password.isEmpty()) {
                view.displayMessage("Vui lòng nhập tên đăng nhập và mật khẩu.", true);
                return;
            }

            // Gọi service để xử lý logic đăng nhập
            TaiKhoanNguoiDung loggedInAccount = taiKhoanService.dangNhap(username, password);

            if (loggedInAccount != null) {
                view.displayMessage("Đăng nhập thành công! Chào mừng " + loggedInAccount.getUsername() + ".", false);
                // Đóng cửa sổ đăng nhập và mở màn hình chính
                view.dispose(); // Đóng LoginView
                
                // Mở MainDashboard hoặc điều hướng đến các màn hình khác tùy theo LoaiNguoiDung
                SwingUtilities.invokeLater(() -> {
                    // Truyền thông tin tài khoản đã đăng nhập vào MainDashboard
                    MainDashBoard mainDashboard = new MainDashBoard(loggedInAccount); 
                    mainDashboard.setVisible(true);
                });
            } else {
                view.displayMessage("Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản bị khóa.", true);
            }
        }
    }

    /**
     * Hiển thị màn hình đăng nhập.
     */
    public void showLoginView() {
        view.setVisible(true);
    }
}

