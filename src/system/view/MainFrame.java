// system/view/MainFrame.java
package system.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date; // Dùng cho mock user

import system.auth.AuthSession;
import system.theme.*;
import system.components.CustomScrollBarUI;
import system.components.ThemeToggleButton;
import system.view.panels.*;
import system.models.entity.*;
import system.services.*; // Giữ nguyên import các Service (Singleton)
import system.controllers.*;
import system.common.LoaiNguoiDung; // Import UserRoles mới

public class MainFrame extends JFrame {

    private JPanel menuPanel;
    private JPanel contentPanel;
    private TaiKhoanNguoiDung currentUser;
    private ThemeToggleButton themeToggleButton;

    // Các biến cho ảnh (tải một lần)
    private Image appIcon;
    private ImageIcon avatarImageIcon;

    // Các Service (Dependency Injection)
    private SanPhamService sanPhamService;
    private LoaiSanPhamService loaiSanPhamService;
    private KhachHangService khachHangService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;
    private NhanVienService nhanVienService;
    private HoaDonService hoaDonService;
    private PhieuNhapHangService phieuNhapHangService;
    private BaoCaoService baoCaoService;

    // Các View/Panel (khởi tạo một lần)
    private DashboardPanel dashboardPanel;
    private SanPhamView sanPhamView;
    private KhachHangView khachHangView;
    private NhanVienView nhanVienView;
    private LoaiSanPhamView loaiSanPhamView;
    private BaoCaoView baoCaoView;
    private HoaDonView hoaDonView;
    private PhieuNhapView phieuNhapView;

    // Các Controller (khởi tạo một lần)
    private SanPhamController sanPhamController;
    private KhachHangController khachHangController;
    private NhanVienController nhanVienController;
    private LoaiSanPhamController loaiSanPhamController;
    private BaoCaoController baoCaoController;
    
    private String maNhanVienLap; 

    // Constructor nhận các service làm tham số (Dependency Injection)
    public MainFrame(
            SanPhamService sanPhamService,
            LoaiSanPhamService loaiSanPhamService,
            KhachHangService khachHangService,
            TaiKhoanNguoiDungService taiKhoanNguoiDungService,
            NhanVienService nhanVienService,
            HoaDonService hoaDonService,
            PhieuNhapHangService phieuNhapHangService,
            BaoCaoService baoCaoService) {

        // 1. Inject Services
        this.sanPhamService = sanPhamService;
        this.loaiSanPhamService = loaiSanPhamService;
        this.khachHangService = khachHangService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;
        this.nhanVienService = nhanVienService;
        this.hoaDonService = hoaDonService;
        this.phieuNhapHangService = phieuNhapHangService;
        this.baoCaoService = baoCaoService;

        // 2. Kiểm tra phiên đăng nhập
        this.currentUser = AuthSession.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Bạn cần đăng nhập để truy cập chức năng này.", "Truy cập bị từ chối", JOptionPane.WARNING_MESSAGE);
            new LoginForm(sanPhamService,
                    loaiSanPhamService,
                    khachHangService,
                    taiKhoanNguoiDungService,
                    nhanVienService,
                    hoaDonService,
                    phieuNhapHangService,
                    baoCaoService).setVisible(true);
            dispose();
            return;
        }
        
        if (currentUser != null && (LoaiNguoiDung.NHAN_VIEN.equalsIgnoreCase(currentUser.getLoaiNguoiDung()) || LoaiNguoiDung.ADMIN.equalsIgnoreCase(currentUser.getLoaiNguoiDung()))) {
            NhanVien currentNhanVien = nhanVienService.getNhanVienByMaNguoiDung(currentUser.getMaNguoiDung());
            if (currentNhanVien != null) {
                this.maNhanVienLap = currentNhanVien.getMaNhanVien();
            } else {
                // Trường hợp người dùng là Admin nhưng không có thông tin trong bảng NhanVien
                // Hoặc là Nhân viên nhưng không tìm thấy thông tin NhanVien (lỗi dữ liệu)
                System.err.println("Cảnh báo: Không tìm thấy thông tin nhân viên cho người dùng hiện tại (MaNguoiDung: " + currentUser.getMaNguoiDung() + ", Loại: " + currentUser.getLoaiNguoiDung() + "). MaNhanVienLap sẽ rỗng.");
                this.maNhanVienLap = ""; // Gán rỗng nếu không tìm thấy
            }
        } else {
            this.maNhanVienLap = ""; // Người dùng không phải Admin/Nhân viên hoặc không có người dùng
        }
        

        // 3. Tải và xử lý ảnh một lần
        loadImages();

        // 4. Khởi tạo các View và Controller một lần duy nhất
        initializeViewsAndControllers();

        // 5. Khởi tạo và hiển thị UI
        initializeUIWithTheme();
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the frame to full screen
        setVisible(true);
    }

    private void loadImages() {
        // Tải icon ứng dụng một lần
        appIcon = new ImageIcon(getClass().getResource("/img/logo.png")).getImage();
        setIconImage(appIcon);

        // Tải và scale avatar một lần
        ImageIcon originalAvatarIcon = new ImageIcon(getClass().getResource("/img/liqi/img3.png")); // Ensure this image exists
        Image img = originalAvatarIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        avatarImageIcon = new ImageIcon(img);
    }

    private void initializeViewsAndControllers() {
        // Khởi tạo Panel
        dashboardPanel = new DashboardPanel();

        // Khởi tạo Views và Controllers
        sanPhamView = new SanPhamView();
        sanPhamController = new SanPhamController(sanPhamView, sanPhamService, loaiSanPhamService);

        khachHangView = new KhachHangView();
        khachHangController = new KhachHangController(khachHangView, khachHangService, taiKhoanNguoiDungService);

        nhanVienView = new NhanVienView();
        nhanVienController = new NhanVienController(nhanVienView, nhanVienService, taiKhoanNguoiDungService);
        
        loaiSanPhamView = new LoaiSanPhamView();
        loaiSanPhamController = new LoaiSanPhamController(loaiSanPhamView, loaiSanPhamService);

        baoCaoView = new BaoCaoView();
        baoCaoController = new BaoCaoController(baoCaoView, baoCaoService);
        
        hoaDonView = new HoaDonView();
        new HoaDonController(hoaDonView, hoaDonService, sanPhamService, khachHangService, nhanVienService, this.maNhanVienLap);
        
        phieuNhapView = new PhieuNhapView();
        new PhieuNhapController(phieuNhapView, phieuNhapHangService, sanPhamService, nhanVienService, this.maNhanVienLap);
        
        // Thêm các panel/view khác nếu có
        // customerManagementPanel = new CustomerManagementPanel();
        // userManagementPanel = new UserManagementPanel();
        // reportPanel = new ReportPanel();
    }


    private void initializeUIWithTheme() {
        getContentPane().removeAll(); // Clear existing components when theme changes

        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();

        setTitle("Trang Chính - Cửa hàng BLK ETTN");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Top Bar Panel (for Shop Name and Theme Toggle) ---
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(theme.getBackgroundColor());

        // 1. Shop Name Label (Center)
        JLabel shopNameLabel = new JLabel("Cửa hàng Bán Linh Kiện của ETTN", SwingConstants.CENTER);
        shopNameLabel.setFont(theme.getTitleFont().deriveFont(Font.BOLD, 26f));
        shopNameLabel.setForeground(theme.getPrimaryColor());
        shopNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topBarPanel.add(shopNameLabel, BorderLayout.CENTER);

        // 2. Theme Toggle Button (Right)
        themeToggleButton = new ThemeToggleButton();
        themeToggleButton.addActionListener(e -> {
            initializeUIWithTheme();
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            revalidate();
            repaint();
        });

        JPanel themeTogglePanelWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        themeTogglePanelWrapper.setBackground(theme.getBackgroundColor());
        themeTogglePanelWrapper.add(themeToggleButton);
        topBarPanel.add(themeTogglePanelWrapper, BorderLayout.EAST);

        JPanel emptyPanelWest = new JPanel();
        emptyPanelWest.setBackground(theme.getBackgroundColor());
        emptyPanelWest.setPreferredSize(new Dimension(20, 0));
        topBarPanel.add(emptyPanelWest, BorderLayout.WEST);

        add(topBarPanel, BorderLayout.NORTH);

        // --- Left (WEST) Panel for Menu and User Info ---
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBackground(theme.getMenuBackgroundColor());
        westPanel.setPreferredSize(new Dimension(220, getHeight()));

        // --- Avatar and Admin Name Panel ---
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(theme.getMenuBackgroundColor());
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Avatar
        JLabel avatarLabel = new JLabel(avatarImageIcon); // Sử dụng avatar đã tải
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setBorder(BorderFactory.createLineBorder(theme.getBorderColor(), 2, true));

        // Admin Name
        JLabel adminNameLabel = new JLabel(currentUser.getUsername());
        adminNameLabel.setFont(theme.getDefaultFont().deriveFont(Font.PLAIN, 14f));
        adminNameLabel.setForeground(theme.getSecondaryColor());
        adminNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userInfoPanel.add(avatarLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userInfoPanel.add(adminNameLabel);

        westPanel.add(userInfoPanel);

        // --- "MENU" Title ---
        JLabel menuTitle = new JLabel("MENU");
        menuTitle.setFont(theme.getTitleFont().deriveFont(Font.BOLD, 24f));
        menuTitle.setForeground(theme.getMenuButtonForegroundColor());
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel menuTitlePanel = new JPanel();
        menuTitlePanel.setBackground(theme.getMenuBackgroundColor());
        menuTitlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        menuTitlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        menuTitlePanel.add(menuTitle);

        westPanel.add(menuTitlePanel);

        // --- Scrollable Menu Panel ---
        menuPanel = new JPanel();
        menuPanel.setBackground(theme.getMenuBackgroundColor());
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupMenu(currentUser.getLoaiNguoiDung()); // Populate menu items based on user role

        JScrollPane menuScrollPane = new JScrollPane(menuPanel);
        menuScrollPane.setOpaque(false);
        menuScrollPane.getViewport().setOpaque(false);

        menuScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder());
        menuScrollPane.setViewportBorder(null);

        menuScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        menuScrollPane.getVerticalScrollBar().setUnitIncrement(8);

        westPanel.add(menuScrollPane);

        // --- Main Content Panel (CENTER) ---
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(theme.getPanelBackgroundColor());

        // Luôn bắt đầu với DashboardPanel là view mặc định
        displayPanel(dashboardPanel); // Sử dụng instance đã khởi tạo

        // --- Add panels to MainFrame ---
        add(westPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void setupMenu(String userRole) {
        menuPanel.removeAll();
        // Luôn hiển thị trang chủ
        addMenuItem("Trang chủ", e -> displayPanel(dashboardPanel));

        // Thêm các mục menu tùy theo vai trò
        if (LoaiNguoiDung.ADMIN.equalsIgnoreCase(userRole)) {
            addMenuItem("Quản lý Sản phẩm", e -> displayPanel(sanPhamView));
            addMenuItem("Quản lý Khách hàng", e -> displayPanel(khachHangView));
            addMenuItem("Quản lý Nhân viên", e -> displayPanel(nhanVienView));
            addMenuItem("Quản lý Loại Sản Phẩm", e -> displayPanel(loaiSanPhamView));
            addMenuItem("Quản lý Bán Hàng", e -> displayPanel(hoaDonView));
            addMenuItem("Quản lý Nhập Hàng", e -> displayPanel(phieuNhapView));
            addMenuItem("Nhà cung cấp", e -> JOptionPane.showMessageDialog(this, "Chức năng Nhà cung cấp chưa được phát triển"));
            addMenuItem("Báo cáo Thống kê", e -> displayPanel(baoCaoView));
            addMenuItem("Biểu đồ thống kê", e -> JOptionPane.showMessageDialog(this, "Chức năng Thống kê chi tiết"));
            addMenuItem("Cài đặt", e -> JOptionPane.showMessageDialog(this, "Chức năng Cài đặt chưa được phát triển"));
        }
        if (LoaiNguoiDung.NHAN_VIEN.equalsIgnoreCase(userRole)) {
            addMenuItem("Quản lý Sản phẩm", e -> displayPanel(sanPhamView));
            // Các panel này cần được khởi tạo và lưu trữ tương tự như SanPhamView nếu chúng cũng phức tạp
            addMenuItem("Quản lý Khách hàng", e -> displayPanel(new CustomerManagementPanel())); // Cân nhắc lưu trữ
            addMenuItem("Quản lý Tài khoản", e -> displayPanel(new UserManagementPanel())); // Cân nhắc lưu trữ
            addMenuItem("Báo cáo & Thống kê", e -> displayPanel(new ReportPanel())); // Cân nhắc lưu trữ
            addMenuItem("Quản lý nhân viên", e -> displayPanel(new NhanVienView())); // Cân nhắc lưu trữ
            addMenuItem("Quản lý loại sản phẩm", e -> displayPanel(new LoaiSanPhamView())); // Cân nhắc lưu trữ
            addMenuItem("Nhà cung cấp", e -> JOptionPane.showMessageDialog(this, "Chức năng Nhà cung cấp"));
            addMenuItem("Nhập kho", e -> JOptionPane.showMessageDialog(this, "Chức năng Nhập kho"));
            addMenuItem("Xuất kho", e -> JOptionPane.showMessageDialog(this, "Chức năng Xuất kho"));
            addMenuItem("Thống kê chi tiết", e -> JOptionPane.showMessageDialog(this, "Chức năng Thống kê chi tiết"));
            addMenuItem("Cài đặt", e -> JOptionPane.showMessageDialog(this, "Chức năng Cài đặt"));
            addMenuItem("Trợ giúp", e -> JOptionPane.showMessageDialog(this, "Chức năng Trợ giúp"));
        }

        menuPanel.add(Box.createVerticalGlue()); // Pushes content to the top
        addMenuItem("Đăng xuất", e -> {
            AuthSession.logout(); // Clear current user session
            new LoginForm(sanPhamService,
                    loaiSanPhamService,
                    khachHangService,
                    taiKhoanNguoiDungService,
                    nhanVienService,
                    hoaDonService,
                    phieuNhapHangService,
                    baoCaoService).setVisible(true); // Show login form
            dispose(); // Close MainFrame
        });
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Padding at the bottom
        menuPanel.revalidate(); // Revalidate menuPanel after adding/removing items
        menuPanel.repaint(); // Repaint menuPanel
    }

    private void addMenuItem(String text, ActionListener listener) {
        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFont(theme.getButtonFont());
        button.setForeground(theme.getMenuButtonForegroundColor());
        button.setBackground(theme.getMenuButtonColor());
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(theme.getMenuButtonHoverColor());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(theme.getMenuButtonColor());
            }
        });

        button.addActionListener(listener);
        menuPanel.add(button);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    /**
     * Displays a given JPanel in the content area and updates the theme toggle button's state.
     * @param panelToDisplay The JPanel to show in the main content area.
     */
    public void displayPanel(JPanel panelToDisplay) {
        contentPanel.removeAll();
        ThemeManager.getInstance().applyTheme(panelToDisplay);
        contentPanel.add(panelToDisplay, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        // Control the theme toggle button's state based on the current panel
        if (themeToggleButton != null) {
            // Theme toggle is enabled only on Dashboard, disable for other complex panels
            boolean enableToggle = panelToDisplay instanceof DashboardPanel;
            themeToggleButton.setEnabled(enableToggle);
        }
    }

//    public static void main(String[] args) {
//        // Set initial theme
//        ThemeManager.getInstance().setTheme(new LightTheme());
//
//        // Mock current user for testing purposes
//        AuthSession.setCurrentUser(new TaiKhoanNguoiDung(
//            1, "TK001", "Yena", "pass", "test@example.com", LoaiNguoiDung.ADMIN, new Date(), "Hoạt động"
//        ));
//
//        // Khởi tạo TẤT CẢ các Service ở đây (Dependency Injection Container đơn giản)
//        // Đảm bảo mỗi Service là Singleton và có phương thức getIns()
//        SanPhamService sanPhamService = SanPhamService.getIns();
//        LoaiSanPhamService loaiSanPhamService = LoaiSanPhamService.getIns();
//        KhachHangService khachHangService = KhachHangService.getIns();
//        TaiKhoanNguoiDungService taiKhoanNguoiDungService = TaiKhoanNguoiDungService.getIns();
//        NhanVienService nhanVienService = NhanVienService.getIns();
//        HoaDonService hoaDonService = HoaDonService.getIns();
//        PhieuNhapHangService phieuNhapHangService = PhieuNhapHangService.getIns();
//        BaoCaoService baoCaoService = BaoCaoService.getIns();
//
//        // Chạy ứng dụng trên Event Dispatch Thread (EDT)
//        SwingUtilities.invokeLater(() -> {
//            new MainFrame(
//                sanPhamService,
//                loaiSanPhamService,
//                khachHangService,
//                taiKhoanNguoiDungService,
//                nhanVienService,
//                hoaDonService,
//                phieuNhapHangService,
//                baoCaoService
//            );
//        });
//    }
}