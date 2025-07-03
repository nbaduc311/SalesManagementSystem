package system.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date; // Date (java.util) không tốt cho thời gian chính xác, nên dùng java.time.*
import java.sql.Connection; // <-- Import Connection
import java.sql.SQLException; // <-- Import SQLException
import system.database.DatabaseConnection; // <-- Import DatabaseConnection

import system.auth.AuthSession;
import system.theme.*;
import system.components.*;
import system.view.panels.*;
import system.models.entity.*;
import system.services.*;
import system.controllers.*;
import system.common.LoaiNguoiDung;
import system.app.AppServices;
import system.auth.*;

public class MainFrame extends JFrame {
	
    private JPanel menuPanel;
    private JPanel contentPanel;
    private TaiKhoanNguoiDung currentUser;
    private ThemeToggleButton themeToggleButton;
    
    private CustomMenuButton activeButton;

    private Image appIcon;
    private ImageIcon avatarImageIcon;

    // Các Service (Dependency Injection)
    private SanPhamService sanPhamService;
    private LoaiSanPhamService loaiSanPhamService;
    private KhachHangService khachHangService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;
    private NhanVienService nhanVienService;
    private HoaDonService hoaDonService;
    private ChiTietHoaDonService chiTietHoaDonService;
    private PhieuNhapHangService phieuNhapHangService;
    private ChiTietPhieuNhapService chiTietPhieuNhapService;
    private NhaCungCapService nhaCungCapService;
    private ViTriDungSanPhamService viTriDungSanPhamService;
    private ChiTietViTriService chiTietViTriService;
    private SaoLuuService saoLuuService;
    private PhucHoiService phucHoiService;
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
    private NhaCungCapView nhaCungCapView;
    private ViTriSanPhamView viTriSanPhamView;
    private SaoLuuPhucHoiView saoLuuPhucHoiView;
    // Views cho người dùng (Khách hàng và Nhân viên)
    private LichSuMuaHangView lichSuMuaHangView;
    private ThongTinTaiKhoanView thongTinTaiKhoanView; // Dùng chung cho KH và NV

    // Các Controller (khởi tạo một lần)
    private SanPhamController sanPhamController;
    private KhachHangController khachHangController;
    private NhanVienController nhanVienController;
    private LoaiSanPhamController loaiSanPhamController;
    private BaoCaoController baoCaoController;
    private HoaDonController hoaDonController;
    private PhieuNhapController phieuNhapController;
    private NhaCungCapController nhaCungCapController;
    private ViTriSanPhamController viTriSanPhamController;
    private SaoLuuPhucHoiController saoLuuPhucHoiController;
    private LichSuMuaHangController lichSuMuaHangController;
    private ThongTinTaiKhoanController thongTinTaiKhoanController; // Dùng chung cho KH và NV

    private String maNhanVienLap;

    public MainFrame(
            SanPhamService sanPhamService,
            LoaiSanPhamService loaiSanPhamService,
            KhachHangService khachHangService,
            TaiKhoanNguoiDungService taiKhoanNguoiDungService,
            NhanVienService nhanVienService,
            HoaDonService hoaDonService,
            ChiTietHoaDonService chiTietHoaDonService,
            PhieuNhapHangService phieuNhapHangService,
            ChiTietPhieuNhapService chiTietPhieuNhapService,
            NhaCungCapService nhaCungCapService,
            ViTriDungSanPhamService viTriDungSanPhamService,
            ChiTietViTriService chiTietViTriService,
            SaoLuuService saoLuuService,
            PhucHoiService phucHoiService,
            BaoCaoService baoCaoService
        ) {

        // 1. Inject Services
        this.sanPhamService = sanPhamService;
        this.loaiSanPhamService = loaiSanPhamService;
        this.khachHangService = khachHangService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;
        this.nhanVienService = nhanVienService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.phieuNhapHangService = phieuNhapHangService;
        this.chiTietPhieuNhapService = chiTietPhieuNhapService;
        this.nhaCungCapService = nhaCungCapService;
        this.viTriDungSanPhamService = viTriDungSanPhamService;
        this.chiTietViTriService = chiTietViTriService;
        this.saoLuuService = saoLuuService;
        this.phucHoiService = phucHoiService;
        this.baoCaoService = baoCaoService;

        // 2. Kiểm tra phiên đăng nhập và lấy thông tin người dùng
        this.currentUser = AuthSession.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Bạn cần đăng nhập để truy cập chức năng này.", "Truy cập bị từ chối", JOptionPane.WARNING_MESSAGE);
            // Khởi tạo LoginForm qua AppServices để đảm bảo đúng AuthController
            AuthService authServiceForLogin = AppServices.getInstance().getAuthService();
            new LoginForm(new AuthController(authServiceForLogin)).setVisible(true);
            dispose();
            return;
        }
        
        // Lấy mã nhân viên lập nếu người dùng hiện tại là NHAN_VIEN hoặc ADMIN
        if (LoaiNguoiDung.NHAN_VIEN.equalsIgnoreCase(currentUser.getLoaiNguoiDung()) || LoaiNguoiDung.ADMIN.equalsIgnoreCase(currentUser.getLoaiNguoiDung())) {
            Connection conn = null; // Khởi tạo Connection ở đây
            try {
                conn = DatabaseConnection.getConnection(); // Lấy kết nối
                NhanVien currentNhanVien = nhanVienService.getNhanVienByMaNguoiDung(conn, currentUser.getMaNguoiDung()); // Truyền Connection vào
                if (currentNhanVien != null) {
                    this.maNhanVienLap = currentNhanVien.getMaNhanVien();
                } else {
                    System.err.println("Cảnh báo: Không tìm thấy thông tin nhân viên cho người dùng hiện tại (MaNguoiDung: " + currentUser.getMaNguoiDung() + ", Loại: " + currentUser.getLoaiNguoiDung() + "). Mã nhân viên lập sẽ để trống.");
                    this.maNhanVienLap = "";
                }
            } catch (SQLException e) { // Bắt SQLException
                System.err.println("Lỗi khi lấy thông tin nhân viên từ MaNguoiDung: " + e.getMessage());
                e.printStackTrace();
                this.maNhanVienLap = "";
            } finally {
                DatabaseConnection.closeConnection(conn); // Đảm bảo đóng kết nối
            }
        } else {
            this.maNhanVienLap = ""; // Khách hàng không có mã nhân viên lập
        }
        
        // 3. Tải và xử lý ảnh một lần
        loadImages();

        // 4. Khởi tạo các View và Controller một lần duy nhất
        initializeViewsAndControllers();

        // 5. Khởi tạo và hiển thị UI
        initializeUIWithTheme();
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Phóng to toàn màn hình
        setVisible(true);
    }

    private void loadImages() {
        appIcon = new ImageIcon(getClass().getResource("/img/logo.png")).getImage();
        setIconImage(appIcon);

        ImageIcon originalAvatarIcon = new ImageIcon(getClass().getResource("/img/liqi/img3.png")); // Giả sử ảnh avatar mặc định
        Image img = originalAvatarIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        avatarImageIcon = new ImageIcon(img);
    }

    private void initializeViewsAndControllers() {
        dashboardPanel = new DashboardPanel();

        // Khởi tạo Views và Controllers cho các chức năng quản lý chung
        sanPhamView = new SanPhamView();
        sanPhamController = new SanPhamController(sanPhamView, sanPhamService, loaiSanPhamService, viTriDungSanPhamService, chiTietViTriService);

        khachHangView = new KhachHangView();
        khachHangController = new KhachHangController(khachHangView, khachHangService, taiKhoanNguoiDungService);

        nhanVienView = new NhanVienView();
        nhanVienController = new NhanVienController(nhanVienView, nhanVienService, taiKhoanNguoiDungService);
        
        loaiSanPhamView = new LoaiSanPhamView();
        loaiSanPhamController = new LoaiSanPhamController(loaiSanPhamView, loaiSanPhamService);

        baoCaoView = new BaoCaoView();
        baoCaoController = new BaoCaoController(baoCaoView, baoCaoService);
        
        hoaDonView = new HoaDonView();
        hoaDonController = new HoaDonController(hoaDonView, hoaDonService, chiTietHoaDonService, sanPhamService, khachHangService, nhanVienService, chiTietViTriService, this.maNhanVienLap);
        
        phieuNhapView = new PhieuNhapView();
        phieuNhapController = new PhieuNhapController(phieuNhapView, phieuNhapHangService, chiTietPhieuNhapService, sanPhamService, nhaCungCapService, chiTietViTriService, this.maNhanVienLap);
        
        nhaCungCapView = new NhaCungCapView();
        nhaCungCapController = new NhaCungCapController(nhaCungCapView, nhaCungCapService);

        viTriSanPhamView = new ViTriSanPhamView();
        viTriSanPhamController = new ViTriSanPhamController(viTriSanPhamView, viTriDungSanPhamService, chiTietViTriService, sanPhamService);

        // SaoLuuPhucHoiView cần được khởi tạo trước khi SaoLuuPhucHoiController
        saoLuuPhucHoiView = new SaoLuuPhucHoiView();
        saoLuuPhucHoiController = new SaoLuuPhucHoiController(saoLuuPhucHoiView, saoLuuService, phucHoiService);

        // Khởi tạo Views và Controllers dành riêng cho Khách hàng VÀ NHÂN VIÊN
        lichSuMuaHangView = new LichSuMuaHangView();
        lichSuMuaHangController = new LichSuMuaHangController(lichSuMuaHangView, hoaDonService, chiTietHoaDonService, sanPhamService, currentUser.getMaNguoiDung());

        // ThongTinTaiKhoanView và Controller dùng chung cho cả Khách hàng và Nhân viên
        thongTinTaiKhoanView = new ThongTinTaiKhoanView();
        thongTinTaiKhoanController = new ThongTinTaiKhoanController(
            thongTinTaiKhoanView, 
            khachHangService, 
            nhanVienService, // Truyền thêm NhanVienService
            taiKhoanNguoiDungService, 
            AppServices.getInstance().getAuthService(), // Truyền AuthService cho chức năng đổi mật khẩu
            currentUser.getMaNguoiDung(), 
            currentUser.getLoaiNguoiDung()
        );
    }


    private void initializeUIWithTheme() {
        getContentPane().removeAll(); // Clear existing components

        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();

        setTitle("Trang Chính - Cửa hàng BLK ETTN");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(theme.getBackgroundColor());

        JLabel shopNameLabel = new JLabel("Cửa hàng Bán Linh Kiện của ETTN", SwingConstants.CENTER);
        shopNameLabel.setFont(theme.getTitleFont().deriveFont(Font.BOLD, 26f));
        shopNameLabel.setForeground(theme.getPrimaryColor());
        shopNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topBarPanel.add(shopNameLabel, BorderLayout.CENTER);

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

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBackground(theme.getMenuBackgroundColor());
        westPanel.setPreferredSize(new Dimension(220, getHeight()));

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(theme.getMenuBackgroundColor());
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel avatarLabel = new JLabel(avatarImageIcon);
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setBorder(BorderFactory.createLineBorder(theme.getBorderColor(), 2, true));

        JLabel userNameLabel = new JLabel(currentUser.getUsername());
        userNameLabel.setFont(theme.getDefaultFont().deriveFont(Font.PLAIN, 14f));
        userNameLabel.setForeground(theme.getSecondaryColor());
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userRoleLabel = new JLabel("(" + currentUser.getLoaiNguoiDung() + ")");
        userRoleLabel.setFont(theme.getDefaultFont().deriveFont(Font.ITALIC, 12f));
        userRoleLabel.setForeground(theme.getSecondaryColor().darker());
        userRoleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        userInfoPanel.add(avatarLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userInfoPanel.add(userNameLabel);
        userInfoPanel.add(userRoleLabel);

        westPanel.add(userInfoPanel);

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

        menuPanel = new JPanel();
        menuPanel.setBackground(theme.getMenuBackgroundColor());
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        setupMenu(currentUser.getLoaiNguoiDung());

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

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(theme.getPanelBackgroundColor());

        displayPanel(dashboardPanel); // Hiển thị dashboardPanel mặc định khi khởi động

        add(westPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void setupMenu(String userRole) {
        menuPanel.removeAll();
        addMenuItem("Trang chủ", e -> displayPanel(dashboardPanel));

        if (LoaiNguoiDung.ADMIN.equalsIgnoreCase(userRole)) {
            addMenuItem("Quản lý Sản phẩm", e -> displayPanel(sanPhamView));
            addMenuItem("Quản lý Khách hàng", e -> displayPanel(khachHangView));
            addMenuItem("Quản lý Nhân viên", e -> displayPanel(nhanVienView));
            addMenuItem("Quản lý Loại Sản Phẩm", e -> displayPanel(loaiSanPhamView));
            addMenuItem("Quản lý Bán Hàng", e -> displayPanel(hoaDonView));
            addMenuItem("Quản lý Nhập Hàng", e -> displayPanel(phieuNhapView));
            addMenuItem("Quản lý Nhà cung cấp", e -> displayPanel(nhaCungCapView));
            addMenuItem("Quản lý Vị trí SP", e -> displayPanel(viTriSanPhamView));
            addMenuItem("Báo cáo Thống kê", e -> displayPanel(baoCaoView));
            // Dòng này đã được sửa đổi theo yêu cầu của bạn
            addMenuItem("Sao lưu & Phục hồi", e -> displayPanel(saoLuuPhucHoiView));
            addMenuItem("Cài đặt", e -> JOptionPane.showMessageDialog(this, "Chức năng Cài đặt chưa được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        } else if (LoaiNguoiDung.NHAN_VIEN.equalsIgnoreCase(userRole)) {
            addMenuItem("Quản lý Sản phẩm", e -> displayPanel(sanPhamView));
            addMenuItem("Quản lý Khách hàng", e -> displayPanel(khachHangView));
            addMenuItem("Quản lý Bán Hàng", e -> displayPanel(hoaDonView));
            addMenuItem("Quản lý Nhập Hàng", e -> displayPanel(phieuNhapView));
            addMenuItem("Nhà cung cấp", e -> displayPanel(nhaCungCapView));
            addMenuItem("Báo cáo & Thống kê", e -> displayPanel(baoCaoView));
            addMenuItem("Thông tin Tài khoản", e -> displayPanel(thongTinTaiKhoanView));
            addMenuItem("Trợ giúp", e -> JOptionPane.showMessageDialog(this, "Chức năng Trợ giúp chưa được phát triển", "Thông báo", JOptionPane.INFORMATION_MESSAGE));
        } else if (LoaiNguoiDung.KHACH_HANG.equalsIgnoreCase(userRole)) {
            addMenuItem("Xem Sản phẩm", e -> displayPanel(sanPhamView));
            addMenuItem("Lịch sử Mua hàng", e -> displayPanel(lichSuMuaHangView));
            addMenuItem("Thông tin Tài khoản", e -> displayPanel(thongTinTaiKhoanView));
            addMenuItem("Liên hệ", e -> JOptionPane.showMessageDialog(this, "Vui lòng liên hệ hotline: 0123-456-789 để được hỗ trợ", "Thông tin Liên hệ", JOptionPane.INFORMATION_MESSAGE));
        }

        menuPanel.add(Box.createVerticalGlue());
        
        addMenuItem("Đăng xuất", e -> {
            AuthSession.logout();
            
            AuthService logoutAuthService = AppServices.getInstance().getAuthService();
            AuthController logoutAuthController = new AuthController(logoutAuthService);
            
            new LoginForm(logoutAuthController).setVisible(true);
            dispose();
        });
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void addMenuItem(String text, ActionListener listener) {
        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();
        
        // Sử dụng CustomMenuButton
        CustomMenuButton button = new CustomMenuButton(text); // <-- Đã thay đổi
        
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFont(theme.getButtonFont());
        button.setForeground(theme.getMenuButtonForegroundColor());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Bọc listener gốc để thêm logic quản lý trạng thái activeButton
        button.addActionListener(e -> {
            // Đặt lại trạng thái của nút đang được chọn trước đó
            if (activeButton != null) {
                activeButton.setSelected(false);
            }
            // Đặt nút hiện tại là nút đang được chọn
            button.setSelected(true);
            activeButton = button; // Cập nhật nút đang hoạt động
            listener.actionPerformed(e); // Gọi listener gốc của nút
        });

        menuPanel.add(button);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Khởi tạo nút Dashboard là nút hoạt động mặc định nếu nó được thêm đầu tiên
        if (text.equals("Trang chủ") && activeButton == null) {
            button.setSelected(true);
            activeButton = button;
        }
    }

    public void displayPanel(JPanel panelToDisplay) {
        contentPanel.removeAll();
        // Áp dụng theme cho panel mới nếu cần, hoặc giả định ThemeManager tự động xử lý các thành phần bên trong
        ThemeManager.getInstance().applyTheme(panelToDisplay); 
        contentPanel.add(panelToDisplay, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        // Điều khiển nút themeToggleButton tùy thuộc vào panel đang hiển thị
        if (themeToggleButton != null) {
            boolean enableToggle = panelToDisplay instanceof DashboardPanel;
            themeToggleButton.setEnabled(enableToggle);
        }
    }
}