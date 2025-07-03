package system.app;

import system.database.DatabaseConnection;
import system.models.dao.*;
import system.models.dao.impl.*;
import system.services.*;
import system.services.impl.*;
import system.auth.AuthService;
import system.auth.impl.AuthServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;

public class AppServices {
    private static AppServices instance;

    // --- Services (sử dụng Interface) ---
    private SanPhamService sanPhamService;
    private LoaiSanPhamService loaiSanPhamService;
    private KhachHangService khachHangService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;
    private NhanVienService nhanVienService;
    private HoaDonService hoaDonService;
    private PhieuNhapHangService phieuNhapHangService;
    private PhucHoiService phucHoiService;
    private SaoLuuService saoLuuService;
    private ChiTietHoaDonService chiTietHoaDonService;
    private ChiTietPhieuNhapService chiTietPhieuNhapService;
    private ChiTietViTriService chiTietViTriService;
    private ViTriDungSanPhamService viTriDungSanPhamService;
    private NhaCungCapService nhaCungCapService;
    private BaoCaoService baoCaoService; // <-- Đã thêm BaoCaoService
    private AuthService authService;

    // Private constructor để đảm bảo chỉ có 1 instance (Singleton)
    private AppServices() {
        // --- Khởi tạo DAOs Implementations ---
        // DAOs không cần biết về DatabaseConnection nữa, chúng chỉ nhận Connection.
        TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO = new TaiKhoanNguoiDungDAOImpl();
        NhanVienDAO nhanVienDAO = new NhanVienDAOImpl();
        SanPhamDAO sanPhamDAO = new SanPhamDAOImpl();
        LoaiSanPhamDAO loaiSanPhamDAO = new LoaiSanPhamDAOImpl();
        KhachHangDAO khachHangDAO = new KhachHangDAOImpl();
        HoaDonDAO hoaDonDAO = new HoaDonDAOImpl();
        PhieuNhapHangDAO phieuNhapHangDAO = new PhieuNhapHangDAOImpl();
        PhucHoiDAO phucHoiDAO = new PhucHoiDAOImpl();
        SaoLuuDAO saoLuuDAO = new SaoLuuDAOImpl();
        ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAOImpl();
        ChiTietPhieuNhapDAO chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAOImpl();
        ChiTietViTriDAO chiTietViTriDAO = new ChiTietViTriDAOImpl();
        ViTriDungSanPhamDAO viTriDungSanPhamDAO = new ViTriDungSanPhamDAOImpl();
        NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAOImpl();
        BaoCaoDAO baoCaoDAO = new BaoCaoDAOImpl(); 

        // --- Khởi tạo Services Implementations và truyền DAOs tương ứng ---
        // Các ServiceImpl giờ đây nhận DAO qua constructor
        this.taiKhoanNguoiDungService = new TaiKhoanNguoiDungServiceImpl(taiKhoanNguoiDungDAO);
        this.nhanVienService = new NhanVienServiceImpl(nhanVienDAO,taiKhoanNguoiDungService);
        this.sanPhamService = new SanPhamServiceImpl(sanPhamDAO);
        this.loaiSanPhamService = new LoaiSanPhamServiceImpl(loaiSanPhamDAO);
        this.khachHangService = new KhachHangServiceImpl(khachHangDAO, taiKhoanNguoiDungService);
        this.hoaDonService = new HoaDonServiceImpl(hoaDonDAO);
        this.phieuNhapHangService = new PhieuNhapHangServiceImpl(phieuNhapHangDAO);
        this.phucHoiService = new PhucHoiServiceImpl(phucHoiDAO);
        this.saoLuuService = new SaoLuuServiceImpl(saoLuuDAO);
        this.chiTietHoaDonService = new ChiTietHoaDonServiceImpl(chiTietHoaDonDAO);
        this.chiTietPhieuNhapService = new ChiTietPhieuNhapServiceImpl(chiTietPhieuNhapDAO);
        this.chiTietViTriService = new ChiTietViTriServiceImpl(chiTietViTriDAO);
        this.viTriDungSanPhamService = new ViTriDungSanPhamServiceImpl(viTriDungSanPhamDAO);
        this.nhaCungCapService = new NhaCungCapServiceImpl(nhaCungCapDAO);
        this.baoCaoService = new BaoCaoServiceImpl(baoCaoDAO); // <-- Đã khởi tạo BaoCaoService

        // Khởi tạo AuthService và truyền TaiKhoanNguoiDungService
        this.authService = new AuthServiceImpl(taiKhoanNguoiDungService);
    }

    // Phương thức để lấy instance duy nhất của AppServices (Singleton)
    public static synchronized AppServices getInstance() {
        if (instance == null) {
            instance = new AppServices();
        }
        return instance;
    }

    // --- Phương thức để lấy Connection và quản lý Transaction ---
    public Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public void commitTransaction(Connection conn) throws SQLException {
        if (conn != null && !conn.getAutoCommit()) {
            conn.commit();
        }
    }

    public void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                System.err.println("Error rolling back transaction: " + e.getMessage());
            }
        }
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // --- Getters cho tất cả các Services ---
    public SanPhamService getSanPhamService() { return sanPhamService; }
    public LoaiSanPhamService getLoaiSanPhamService() { return loaiSanPhamService; }
    public KhachHangService getKhachHangService() { return khachHangService; }
    public TaiKhoanNguoiDungService getTaiKhoanNguoiDungService() { return taiKhoanNguoiDungService; }
    public NhanVienService getNhanVienService() { return nhanVienService; }
    public HoaDonService getHoaDonService() { return hoaDonService; }
    public PhieuNhapHangService getPhieuNhapHangService() { return phieuNhapHangService; }
    public PhucHoiService getPhucHoiService() { return phucHoiService; }
    public SaoLuuService getSaoLuuService() { return saoLuuService; }
    public ChiTietHoaDonService getChiTietHoaDonService() { return chiTietHoaDonService; }
    public ChiTietPhieuNhapService getChiTietPhieuNhapService() { return chiTietPhieuNhapService; }
    public ChiTietViTriService getChiTietViTriService() { return chiTietViTriService; }
    public ViTriDungSanPhamService getViTriDungSanPhamService() { return viTriDungSanPhamService; }
    public NhaCungCapService getNhaCungCapService() { return nhaCungCapService; }
    public BaoCaoService getBaoCaoService() { return baoCaoService; } // <-- Đã thêm getter
    public AuthService getAuthService() { return authService; }
}