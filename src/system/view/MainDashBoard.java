package system.view;

import system.models.entity.*;
import system.services.*;
import system.controllers.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException; // Keep for SQLException handling if needed

/**
 * Lớp MainDashBoard là màn hình chính của ứng dụng sau khi đăng nhập thành công.
 * Nó sử dụng JTabbedPane để tổ chức các module chức năng khác nhau.
 */
public class MainDashBoard extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TaiKhoanNguoiDung loggedInUser;
    private JTabbedPane tabbedPane;

    public MainDashBoard(TaiKhoanNguoiDung user) {
        this.loggedInUser = user;
        setTitle("Trang chủ - Hệ thống Quản lý Cửa hàng (" + user.getLoaiNguoiDung() + ")");
        setSize(1200, 700); // Tăng kích thước để chứa nhiều tab hơn nếu cần
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // 1. Tab Trang chủ (Welcome)
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        JLabel welcomeLabel = new JLabel("<html><h1 style='text-align: center; color: #333;'>Chào mừng, " + loggedInUser.getUsername() + " (" + loggedInUser.getLoaiNguoiDung() + ")!</h1>" +
                                        "<p style='text-align: center; font-size: 14px; color: #666;'>Đây là giao diện quản lý chính của hệ thống. Vui lòng chọn chức năng từ các tab bên trên.</p></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.CENTER);
        homePanel.add(welcomeLabel, BorderLayout.CENTER);
        tabbedPane.addTab("Trang chủ", homePanel);

        // Khởi tạo tất cả các Service một lần và truyền chúng vào các Controller
        SanPhamService sanPhamService = SanPhamService.getIns();
        LoaiSanPhamService loaiSanPhamService = LoaiSanPhamService.getIns();
        KhachHangService khachHangService = KhachHangService.getIns();
        TaiKhoanNguoiDungService taiKhoanNguoiDungService = TaiKhoanNguoiDungService.getIns();
        NhanVienService nhanVienService = NhanVienService.getIns();
        HoaDonService hoaDonService = HoaDonService.getIns();
        PhieuNhapHangService phieuNhapHangService = PhieuNhapHangService.getIns(); 
        BaoCaoService baoCaoService = BaoCaoService.getIns(); 


        // Lấy MaNhanVien của người dùng đang đăng nhập
        String maNhanVienLap = "";
        NhanVien currentNhanVien = nhanVienService.getNhanVienByMaNguoiDung(loggedInUser.getMaNguoiDung());
        if (currentNhanVien != null) {
            maNhanVienLap = currentNhanVien.getMaNhanVien();
        } else {
               System.err.println("Cảnh báo: Không tìm thấy thông tin nhân viên cho người dùng hiện tại (MaNguoiDung: " + loggedInUser.getMaNguoiDung() + "). MaNhanVienLap sẽ rỗng.");
            // Có thể hiển thị thông báo lỗi hoặc xử lý khác tùy theo yêu cầu nghiệp vụ
        }
        

        // 2. Tab Quản lý Sản phẩm
        SanPhamView sanPhamView = new SanPhamView();
        new SanPhamController(sanPhamView, sanPhamService, loaiSanPhamService);
        tabbedPane.addTab("Quản lý Sản phẩm", sanPhamView);
        
        // 3. Tab Quản lý Khách hàng
        KhachHangView khachHangView = new KhachHangView();
        new KhachHangController(khachHangView, khachHangService, taiKhoanNguoiDungService);
        tabbedPane.addTab("Quản lý Khách hàng", khachHangView);

        // 4. Tab Quản lý Nhân viên
        NhanVienView nhanVienView = new NhanVienView();
        new NhanVienController(nhanVienView, nhanVienService, taiKhoanNguoiDungService);
        tabbedPane.addTab("Quản lý Nhân viên", nhanVienView);

        // 5. Tab Quản lý Loại Sản phẩm
        LoaiSanPhamView loaiSanPhamView = new LoaiSanPhamView();
        new LoaiSanPhamController(loaiSanPhamView, loaiSanPhamService);
        tabbedPane.addTab("Quản lý Loại Sản phẩm", loaiSanPhamView);

        // 6. Tab Quản lý Bán hàng
        HoaDonView hoaDonView = new HoaDonView();
        new HoaDonController(hoaDonView, hoaDonService, sanPhamService, khachHangService, nhanVienService, maNhanVienLap);
        tabbedPane.addTab("Quản lý Bán hàng", hoaDonView);

        // 7. Tab Quản lý Nhập hàng
        PhieuNhapView phieuNhapView = new PhieuNhapView();
        new PhieuNhapController(phieuNhapView, phieuNhapHangService, sanPhamService, nhanVienService, maNhanVienLap);
        tabbedPane.addTab("Quản lý Nhập hàng", phieuNhapView);
        
        //8. Tab Quản lý báo cáo thống kê
        BaoCaoView baoCaoView = new BaoCaoView();
        new BaoCaoController(baoCaoView, baoCaoService);
        tabbedPane.addTab("Quản lý Báo cáo", baoCaoView);
        
        

        // JPanel reportPanel = new JPanel();
        // tabbedPane.addTab("Báo cáo & Thống kê", reportPanel);

        // JPanel backupRestorePanel = new JPanel();
        // tabbedPane.addTab("Sao lưu & Phục hồi", backupRestorePanel);
    }

    private void layoutComponents() {
        add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                });
            }
        });
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);
    }
}
