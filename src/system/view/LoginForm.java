package system.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import system.components.*; // Đảm bảo các component này tồn tại và đúng
import system.controllers.AuthController;
import system.auth.AuthSession;
import system.models.entity.TaiKhoanNguoiDung;
import system.services.*;
import system.auth.AuthService;
import system.app.AppServices;

public class LoginForm extends JFrame {
    private RoundedTextField usernameField;
    private RoundedPasswordField passwordField;

    private AuthController authController;

    public LoginForm(AuthController authController) {
        this.authController = authController;

        setTitle("Cửa hàng BLK của ETTN nhaaaa");
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Đảm bảo các đường dẫn ảnh đúng hoặc bỏ qua nếu không có
        setIconImage(new ImageIcon(getClass().getResource("/img/logo.png")).getImage());
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/img/bg.png"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(400, 500));
        leftPanel.setLayout(null);
        leftPanel.setOpaque(false); // Make it transparent to see background

        JLabel titleLabel = new JLabel("Đăng nhập");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBounds(150, 70, 200, 40);
        leftPanel.add(titleLabel);

        JLabel subTitle = new JLabel("Chào mừng bạn đã đến với cửa hàng BLK ETTN");
        subTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subTitle.setBounds(95, 110, 300, 20);
        leftPanel.add(subTitle);

        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setBounds(70, 150, 300, 20);
        leftPanel.add(usernameLabel);

        usernameField = new RoundedTextField(30);
        usernameField.setBounds(70, 170, 300, 30);
        usernameField.setPlaceholder("Điền username nè");
        leftPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setBounds(70, 210, 300, 20);
        leftPanel.add(passwordLabel);

        passwordField = new RoundedPasswordField(30);
        passwordField.setBounds(70, 230, 300, 30);
        passwordField.setPlaceholder("Điền mật khẩu");
        leftPanel.add(passwordField);

        JCheckBox showPassword = new JCheckBox("Hiển thị mật khẩu");
        showPassword.setBounds(70, 270, 150, 25);
        showPassword.setOpaque(false);
        showPassword.setFocusPainted(false);
        leftPanel.add(showPassword);

        JButton forgotBtn = new JButton("Quên mật khẩu");
        forgotBtn.setBounds(250, 270, 150, 20);
        forgotBtn.setFocusPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setForeground(Color.BLUE);
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        leftPanel.add(forgotBtn);

        // Hiệu ứng hover cho nút "Quên mật khẩu"
        Font forgotFont = forgotBtn.getFont();
        forgotBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                forgotBtn.setFont(forgotFont.deriveFont(forgotFont.getStyle(), forgotFont.getSize()));
                forgotBtn.setText("<html><u>Quên mật khẩu</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                forgotBtn.setText("Quên mật khẩu");
            }
        });

        RoundedButton signInBtn = new RoundedButton("Đăng nhập");
        signInBtn.setBounds(70, 310, 300, 40);
        leftPanel.add(signInBtn);

        JLabel registerLabel = new JLabel("Nếu chưa có tài khoản thì");
        registerLabel.setBounds(118, 360, 150, 20);
        leftPanel.add(registerLabel);

        JButton signUpBtn = new JButton("Đăng ký");
        signUpBtn.setBounds(250, 360, 80, 20);
        signUpBtn.setFocusPainted(false);
        signUpBtn.setContentAreaFilled(false);
        signUpBtn.setBorderPainted(false);
        signUpBtn.setForeground(Color.BLUE);
        signUpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        leftPanel.add(signUpBtn);

        // Hiệu ứng hover cho nút "Đăng ký"
        Font signUpFont = signUpBtn.getFont();
        signUpBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signUpBtn.setFont(signUpFont.deriveFont(signUpFont.getStyle(), signUpFont.getSize()));
                signUpBtn.setText("<html><u>Đăng ký</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signUpBtn.setText("Đăng ký");
            }
        });

        // --- Hành động của các thành phần UI ---
        usernameField.addActionListener(e -> {
            signInBtn.setPressedTemporarily();
            Timer timer = new Timer(500, evt -> signInBtn.doClick());
            timer.setRepeats(false);
            timer.start();
        });

        passwordField.addActionListener(e -> {
            signInBtn.setPressedTemporarily();
            Timer timer = new Timer(500, evt -> signInBtn.doClick());
            timer.setRepeats(false);
            timer.start();
        });

        signInBtn.addActionListener(e -> handleLogin());
        
        signUpBtn.addActionListener(e -> {
            // Truyền AuthController vào RegisterForm
            new RegisterForm(authController).setVisible(true);
        });
        
        forgotBtn.addActionListener(e -> {
            // Truyền AuthController vào ForgotPasswordForm
            new ForgotPasswordForm(authController).setVisible(true);
        });
        
        showPassword.addActionListener(e -> {
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });

        // Phần Slideshow panel
        String[] imagePaths = {
                "/img/liqi/img1.png", "/img/liqi/img2.png", "/img/liqi/img3.png",
                "/img/liqi/img4.png", "/img/liqi/img5.png", "/img/liqi/img6.png",
                "/img/liqi/img7.png", "/img/liqi/img8.png", "/img/liqi/img9.png",
                "/img/liqi/img10.png"
        };
        SlideShowPanel slidePanel = new SlideShowPanel(imagePaths, 430, 430, 50, 1000);
        slidePanel.setPreferredSize(new Dimension(500, 500));
        slidePanel.setOpaque(false);

        backgroundLabel.add(leftPanel, BorderLayout.WEST);
        backgroundLabel.add(slidePanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Lỗi đăng nhập", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi phương thức đăng nhập từ AuthController
        TaiKhoanNguoiDung user = authController.loginWithUsername(username, password);

        if (user != null) {
            System.out.println("Đăng nhập thành công cho user: " + user.getUsername());
            AuthSession.setCurrentUser(user);
            dispose();
            System.out.println("LoginForm đã đóng. Đang khởi tạo MainFrame...");

            AppServices appServices = AppServices.getInstance();
            System.out.println("Đã lấy AppServices instance.");

            // Khối try-catch bao bọc khởi tạo MainFrame
            try {
                new MainFrame(
                    appServices.getSanPhamService(),
                    appServices.getLoaiSanPhamService(),
                    appServices.getKhachHangService(),
                    appServices.getTaiKhoanNguoiDungService(),
                    appServices.getNhanVienService(),
                    appServices.getHoaDonService(),
                    appServices.getChiTietHoaDonService(),
                    appServices.getPhieuNhapHangService(),
                    appServices.getChiTietPhieuNhapService(),
                    appServices.getNhaCungCapService(),
                    appServices.getViTriDungSanPhamService(),
                    appServices.getChiTietViTriService(),
                    appServices.getSaoLuuService(),
                    appServices.getPhucHoiService(),
                    appServices.getBaoCaoService()
                ).setVisible(true);
                System.out.println("MainFrame đã được khởi tạo và hiển thị.");
            } catch (Exception ex) {
                System.err.println("Lỗi khi khởi tạo MainFrame: " + ex.getMessage());
                ex.printStackTrace(); // In toàn bộ stack trace để dễ debug
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi tải giao diện chính. Vui lòng thử lại.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // --- Khởi tạo ứng dụng ---
        AppServices appServices = AppServices.getInstance();

        // Lấy AuthService từ AppServices để tạo AuthController
        AuthService authService = appServices.getAuthService();
        AuthController authController = new AuthController(authService);

        // Chạy giao diện trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginForm(authController).setVisible(true);
        });
    }
}