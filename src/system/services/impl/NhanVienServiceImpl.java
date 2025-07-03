// system.services.impl/NhanVienServiceImpl.java (Điều chỉnh)
package system.services.impl;

import system.models.dao.NhanVienDAO;
import system.models.entity.NhanVien;
import system.models.entity.TaiKhoanNguoiDung;
import system.services.NhanVienService;
import system.services.TaiKhoanNguoiDungService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NhanVienServiceImpl implements NhanVienService {

    private NhanVienDAO nhanVienDAO;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService; // Thêm dependency

    public NhanVienServiceImpl(NhanVienDAO nhanVienDAO, TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.nhanVienDAO = nhanVienDAO;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;
    }

    @Override
    public void addNhanVien(Connection conn, NhanVien nhanVien) throws SQLException { // Đã sửa kiểu trả về và tham số
        try {
            nhanVienDAO.addNhanVien(conn, nhanVien);
            // Logic tạo tài khoản người dùng đã được chuyển lên Controller hoặc Service layer cao hơn
            // (nếu muốn logic tạo TK và NV nằm trong cùng 1 service transaction nhưng vẫn tách biệt)
            // hoặc Controller sẽ gọi riêng TaiKhoanNguoiDungService trước rồi gán MaNguoiDung vào NhanVien
        } catch (SQLException e) {
            System.err.println("Lỗi SQL trong NhanVienServiceImpl.addNhanVien: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateNhanVien(Connection conn, NhanVien nhanVien) throws SQLException {
        try {
            nhanVienDAO.updateNhanVien(conn, nhanVien);
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL trong NhanVienServiceImpl.updateNhanVien: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteNhanVien(Connection conn, String maNhanVien) throws SQLException {
        try {
            nhanVienDAO.deleteNhanVien(conn, maNhanVien);
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL trong NhanVienServiceImpl.deleteNhanVien: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public NhanVien getNhanVienById(Connection conn, String maNhanVien) throws SQLException {
        return nhanVienDAO.getNhanVienById(conn, maNhanVien);
    }

    @Override
    public List<NhanVien> getAllNhanVien(Connection conn) throws SQLException {
        return nhanVienDAO.getAllNhanVien(conn);
    }

    @Override
    public NhanVien getNhanVienByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        return nhanVienDAO.getNhanVienByMaNguoiDung(conn, maNguoiDung);
    }

    @Override
    public NhanVien getNhanVienByCCCD(Connection conn, String cccd) throws SQLException {
        return nhanVienDAO.getNhanVienByCCCD(conn, cccd);
    }

    @Override
    public List<NhanVien> searchNhanVienByName(Connection conn, String name) throws SQLException {
        return nhanVienDAO.searchNhanVienByName(conn, name);
    }

    @Override
    public List<NhanVien> searchNhanVienBySdt(Connection conn, String sdt) throws SQLException { // Triển khai phương thức mới
        // Bạn cần thêm phương thức này vào NhanVienDAO và NhanVienDAOImpl
        // Hiện tại giả định rằng NhanVienDAOImpl sẽ có phương thức này.
        // Nếu không có, bạn cần tạo nó.
        return nhanVienDAO.searchNhanVienBySdt(conn, sdt);
    }

    @Override
    public List<NhanVien> getNhanVienByTrangThai(Connection conn, String trangThai) throws SQLException {
        return nhanVienDAO.getNhanVienByTrangThai(conn, trangThai);
    }
}