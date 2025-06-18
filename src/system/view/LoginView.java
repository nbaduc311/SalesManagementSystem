package system.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Import ActionListener

/**
 * Lớp LoginView tạo giao diện người dùng cho màn hình đăng nhập.
 * Nó hiển thị các trường nhập liệu và nút bấm, không chứa logic nghiệp vụ.
 */
public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    public LoginView() {
        setTitle("Đăng nhập Hệ thống Quản lý Cửa hàng");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình

        initComponents(); // Khởi tạo các thành phần UI
        layoutComponents(); // Sắp xếp các thành phần UI
    }

    /**
     * Khởi tạo các thành phần giao diện người dùng.
     */
    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng nhập");
        messageLabel = new JLabel(""); // Ban đầu trống
        messageLabel.setForeground(Color.RED); // Màu đỏ cho thông báo lỗi
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Sắp xếp các thành phần giao diện trên cửa sổ.
     */
    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Chào mừng! Vui lòng đăng nhập");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Message Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        // Login Button
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Không kéo dài nút
        gbc.anchor = GridBagConstraints.CENTER; // Đặt nút ở giữa
        panel.add(loginButton, gbc);

        add(panel); // Thêm panel vào JFrame
    }

    /**
     * Lấy tên đăng nhập từ trường nhập liệu.
     * @return Tên đăng nhập đã nhập.
     */
    public String getUsername() {
        return usernameField.getText();
    }

    /**
     * Lấy mật khẩu từ trường nhập liệu.
     * @return Mật khẩu đã nhập dưới dạng mảng ký tự.
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /**
     * Hiển thị thông báo (lỗi hoặc thành công) trên giao diện.
     * @param message Nội dung thông báo.
     * @param isError True nếu là thông báo lỗi, False nếu là thông báo thành công.
     */
    public void displayMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : Color.BLUE);
        // Có thể thêm JOptionPane.showMessageDialog cho các lỗi nghiêm trọng
        if (isError) {
             JOptionPane.showMessageDialog(this, message, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        } else {
             // JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Thiết lập ActionListener cho nút đăng nhập.
     * Điều này cho phép Controller đăng ký để lắng nghe sự kiện từ View.
     * @param listener ActionListener để gán.
     */
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}
