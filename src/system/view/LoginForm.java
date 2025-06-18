package system.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import system.components.*;
import system.controllers.AuthController;
import system.auth.AuthSession; 	
import system.models.entity.TaiKhoanNguoiDung;
import system.services.*; // Import tất cả các service bạn cần

public class LoginForm extends JFrame {
    private RoundedTextField usernameField;
    private RoundedPasswordField passwordField;
    
    // Khai báo các service cần thiết để truyền cho MainFrame
    private SanPhamService sanPhamService;
    private LoaiSanPhamService loaiSanPhamService;
    private KhachHangService khachHangService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;
    private NhanVienService nhanVienService;
    private HoaDonService hoaDonService;
    private PhieuNhapHangService phieuNhapHangService;
    private BaoCaoService baoCaoService;
    
    public LoginForm(SanPhamService sanPhamService,
            LoaiSanPhamService loaiSanPhamService,
            KhachHangService khachHangService,
            TaiKhoanNguoiDungService taiKhoanNguoiDungService,
            NhanVienService nhanVienService,
            HoaDonService hoaDonService,
            PhieuNhapHangService phieuNhapHangService,
            BaoCaoService baoCaoService) {
    	
    	// Inject services
        this.sanPhamService = sanPhamService;
        this.loaiSanPhamService = loaiSanPhamService;
        this.khachHangService = khachHangService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;
        this.nhanVienService = nhanVienService;
        this.hoaDonService = hoaDonService;
        this.phieuNhapHangService = phieuNhapHangService;
        this.baoCaoService = baoCaoService;
        
        setTitle("Cửa hàng BLK của ETTN nhaaaa");
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setIconImage(new ImageIcon(getClass().getResource("/img/logo.png")).getImage());

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/img/bg.png"));
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(400, 500));
        leftPanel.setLayout(null);
        leftPanel.setOpaque(false);

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
        
        Font forgotFont = forgotBtn.getFont();
        // Gạch chân khi hover vào "Quên mật khẩu"
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
        
        Font signUpFont = signUpBtn.getFont();
        // Gạch chân khi hover vào "Đăng ký"
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
        
     // Hành động 
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
        signUpBtn.addActionListener(e -> new RegisterForm());
        forgotBtn.addActionListener(e -> new ForgotPasswordForm());
        showPassword.addActionListener(e -> {
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });

        String[] imagePaths = {
            "/img/liqi/img1.png", "/img/liqi/img2.png", "/img/liqi/img3.png",
            "/img/liqi/img4.png", "/img/liqi/img5.png", "/img/liqi/img6.png",
            "/img/liqi/img7.png", "/img/liqi/img8.png", "/img/liqi/img9.png",
            "/img/liqi/img10.png"
        };
//        String[] imagePaths = {
//        	    "/img/login_img/img1.png", "/img/login_img/img2.png", "/img/login_img/img3.png",
//        	    "/img/login_img/img4.png", "/img/login_img/img5.png", "/img/login_img/img6.png",
//        	    "/img/login_img/img7.png", "/img/login_img/img8.png", "/img/login_img/img9.png",
//        	    "/img/login_img/img10.png"    
//        };
//        
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
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        AuthController authController = new AuthController();
        TaiKhoanNguoiDung user = authController.loginWithUsername(username, password);

        if (user != null) {
            AuthSession.setCurrentUser(user);
            dispose();
         // Mở MainFrame chung
            new MainFrame(
                    sanPhamService,
                    loaiSanPhamService,
                    khachHangService,
                    taiKhoanNguoiDungService,
                    nhanVienService,
                    hoaDonService,
                    phieuNhapHangService,
                    baoCaoService
                );
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
    	SanPhamService sanPhamService = SanPhamService.getIns();
        LoaiSanPhamService loaiSanPhamService = LoaiSanPhamService.getIns();
        KhachHangService khachHangService = KhachHangService.getIns();
        TaiKhoanNguoiDungService taiKhoanNguoiDungService = TaiKhoanNguoiDungService.getIns();
        NhanVienService nhanVienService = NhanVienService.getIns();
        HoaDonService hoaDonService = HoaDonService.getIns();
        PhieuNhapHangService phieuNhapHangService = PhieuNhapHangService.getIns();
        BaoCaoService baoCaoService = BaoCaoService.getIns();
        SwingUtilities.invokeLater(() -> {
            new LoginForm(
                sanPhamService,
                loaiSanPhamService,
                khachHangService,
                taiKhoanNguoiDungService,
                nhanVienService,
                hoaDonService,
                phieuNhapHangService,
                baoCaoService
            ).setVisible(true);
        });
    }
}
