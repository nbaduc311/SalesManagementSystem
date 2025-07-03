package system.view;

import javax.swing.*;
import java.awt.*;
import system.components.*;
import system.controllers.AuthController; // Thay vì RegisterController

public class RegisterForm extends JFrame {
    private RoundedTextField usernameField, emailField;
    private RoundedPasswordField passField, confirmPassField;
    private AuthController authController; 

    // Constructor nhận AuthController
    public RegisterForm(AuthController authController) {
        this.authController = authController; // Gán AuthController được truyền vào

        setTitle("Đăng ký tài khoản");
        setSize(450, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/img/bg_register.jpg"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 450, 500);
        getLayeredPane().add(backgroundLabel, new Integer(Integer.MIN_VALUE));

        JPanel content = (JPanel) getContentPane();
        content.setOpaque(false);
        content.setLayout(null);
        setIconImage(new ImageIcon(getClass().getResource("/img/logo.png")).getImage());

        JLabel title = new JLabel("Tạo tài khoản mới");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(110, 30, 300, 30);
        add(title);

        JLabel nameLabel = new JLabel("Username:");
        nameLabel.setBounds(50, 80, 100, 25);
        add(nameLabel);

        usernameField = new RoundedTextField(30);
        usernameField.setPlaceholder("Đây sẽ là tên đăng nhập");
        usernameField.setBounds(50, 110, 330, 35);
        add(usernameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 150, 100, 25);
        add(emailLabel);

        emailField = new RoundedTextField(30);
        emailField.setPlaceholder("Đây sẽ là để bạn khôi phục mật khẩu");
        emailField.setBounds(50, 180, 330, 35);
        add(emailField);

        JLabel passLabel = new JLabel("Mật khẩu:");
        passLabel.setBounds(50, 220, 100, 25);
        add(passLabel);

        passField = new RoundedPasswordField(30);
        passField.setPlaceholder("Điền mật khẩu nè");
        passField.setBounds(50, 250, 330, 35);
        add(passField);

        JLabel confirmLabel = new JLabel("Xác nhận mật khẩu:");
        confirmLabel.setBounds(50, 290, 150, 25);
        add(confirmLabel);

        confirmPassField = new RoundedPasswordField(30);
        confirmPassField.setPlaceholder("Điền xác nhận điiii");
        confirmPassField.setBounds(50, 320, 330, 35);
        add(confirmPassField);

        RoundedButton registerBtn = new RoundedButton("Đăng ký");
        registerBtn.setBounds(125, 380, 180, 40);
        add(registerBtn);

        confirmPassField.addActionListener(e -> {
            registerBtn.setPressedTemporarily();
            Timer timer = new Timer(500, evt -> registerBtn.doClick());
            timer.setRepeats(false);
            timer.start();
        });

        // Gọi phương thức handleRegister() thay vì controller trực tiếp
        registerBtn.addActionListener(e -> handleRegister());

        setVisible(true);
    }

    // Phương thức xử lý logic đăng ký
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passField.getPassword());
        String confirmPassword = new String(confirmPassField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi phương thức đăng ký từ AuthController
        // Giả định loại người dùng mặc định là "KhachHang" khi đăng ký từ form này
        boolean success = authController.registerUser(username, password, email, "KhachHang");

        if (success) {
            JOptionPane.showMessageDialog(this, "Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Đóng form đăng ký
        } else {
            // AuthController đã xử lý thông báo lỗi chi tiết hơn nếu có lỗi DB hoặc username trùng lặp
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại. Tên đăng nhập hoặc Email có thể đã tồn tại, hoặc có lỗi xảy ra.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void closeForm() {
        dispose();
    }
}