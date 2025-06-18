package system.view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import system.components.*; // Đảm bảo bạn đã có RoundedTextField, RoundedPasswordField, RoundedButton
import system.database.DatabaseConnection;

public class ForgotPasswordForm extends JFrame {
    private RoundedTextField emailField;

    public ForgotPasswordForm() {
        setTitle("Quên Mật Khẩu");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null); // Vẫn dùng setBounds
        setIconImage(new ImageIcon(getClass().getResource("/img/logo.png")).getImage());

        // === Thêm ảnh background ===
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/img/bg_fg.jpg"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, 420, 300); // Đặt đúng kích thước cửa sổ
        getLayeredPane().add(backgroundLabel, new Integer(Integer.MIN_VALUE)); // Cho background xuống dưới cùng

        // Làm trong suốt panel nội dung
        JPanel content = (JPanel) getContentPane();
        content.setOpaque(false);
        content.setLayout(null);

        // === Các thành phần giao diện ===
        JLabel titleLabel = new JLabel("Khôi phục mật khẩu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBounds(90, 25, 250, 30);
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

        RoundedButton submitBtn = new RoundedButton("Khôi phục");
        submitBtn.setBounds(50, 170, 320, 40);
        add(submitBtn);

        emailField.addActionListener(e -> {
            submitBtn.setPressedTemporarily();
            Timer timer = new Timer(500, evt -> submitBtn.doClick());
            timer.setRepeats(false);
            timer.start();
        });

        submitBtn.addActionListener(e -> handleRecover());

        setVisible(true);
    }


    private void handleRecover() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT Username, Password FROM TaiKhoanNguoiDung WHERE Email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String username = rs.getString("Username");
                String password = rs.getString("Password");

                JOptionPane.showMessageDialog(this,
                        "Tài khoản: " + username + "\nMật khẩu: " + password,
                        "Khôi phục thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email không tồn tại trong hệ thống!", "Lỗiiiiiiii", JOptionPane.ERROR_MESSAGE);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi truy vấn CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
