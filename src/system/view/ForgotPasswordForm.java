package system.view;

import javax.swing.*;
import java.awt.*;
import system.components.*; // Đảm bảo bạn đã có RoundedTextField, RoundedPasswordField, RoundedButton
import system.controllers.AuthController; // Import AuthController

public class ForgotPasswordForm extends JFrame {
    private RoundedTextField emailField;
    private RoundedPasswordField newPasswordField; // Thêm trường cho mật khẩu mới
    private AuthController authController; // Thêm AuthController

    // Constructor nhận AuthController
    public ForgotPasswordForm(AuthController authController) {
        this.authController = authController; // Gán AuthController được truyền vào

        setTitle("Quên Mật Khẩu");
        setSize(420, 350); // Tăng chiều cao để chứa trường mật khẩu mới
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setIconImage(new ImageIcon(getClass().getResource("/img/logo.png")).getImage());

        // === Thêm ảnh background ===
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/img/bg_fg.jpg"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 420, 350); // Đặt đúng kích thước cửa sổ
        getLayeredPane().add(backgroundLabel, new Integer(Integer.MIN_VALUE)); // Cho background xuống dưới cùng

        // Làm trong suốt panel nội dung
        JPanel content = (JPanel) getContentPane();
        content.setOpaque(false);
        content.setLayout(null);

        // === Các thành phần giao diện ===
        JLabel titleLabel = new JLabel("Khôi phục mật khẩu", SwingConstants.CENTER); // Căn giữa
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBounds(0, 25, getWidth(), 30); // Đặt x=0, width=getWidth() để căn giữa tự động
        titleLabel.setForeground(new Color(30, 30, 30));
        add(titleLabel);

        JLabel emailLabel = new JLabel("Nhập email đã đăng ký:");
        emailLabel.setBounds(50, 80, 300, 20);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(emailLabel);

        emailField = new RoundedTextField(20);
        emailField.setBounds(50, 110, 320, 35);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(emailField);

        // Thêm trường nhập mật khẩu mới
        JLabel newPassLabel = new JLabel("Mật khẩu mới:");
        newPassLabel.setBounds(50, 155, 300, 20);
        newPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(newPassLabel);

        newPasswordField = new RoundedPasswordField(20);
        newPasswordField.setBounds(50, 190, 320, 35);
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(newPasswordField);


        RoundedButton submitBtn = new RoundedButton("Đặt lại mật khẩu"); // Đổi tên nút
        submitBtn.setBounds(50, 240, 320, 40); // Điều chỉnh vị trí nút
        add(submitBtn);

        // Hành động khi nhấn Enter ở emailField hoặc newPasswordField
        emailField.addActionListener(e -> {
            newPasswordField.requestFocusInWindow(); // Chuyển focus sang mật khẩu mới
        });
        
        newPasswordField.addActionListener(e -> {
            submitBtn.setPressedTemporarily();
            Timer timer = new Timer(500, evt -> submitBtn.doClick());
            timer.setRepeats(false);
            timer.start();
        });

        submitBtn.addActionListener(e -> handleResetPassword()); // Gọi handleResetPassword

        setVisible(true);
    }

    private void handleResetPassword() {
        String email = emailField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());

        if (email.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ email và mật khẩu mới!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi phương thức resetPassword từ AuthController
        boolean success = authController.resetPassword(email, newPassword);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu của bạn đã được đặt lại thành công. Vui lòng đăng nhập lại.",
                    "Khôi phục thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            // AuthController đã xử lý thông báo lỗi chi tiết hơn
            JOptionPane.showMessageDialog(this, "Email không tồn tại trong hệ thống hoặc có lỗi xảy ra trong quá trình khôi phục.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}