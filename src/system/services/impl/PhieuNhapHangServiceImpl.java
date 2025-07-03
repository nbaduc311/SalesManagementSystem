package system.services.impl;

import system.models.dao.PhieuNhapHangDAO;
import system.models.entity.PhieuNhapHang;
import system.services.PhieuNhapHangService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PhieuNhapHangServiceImpl implements PhieuNhapHangService {

    private PhieuNhapHangDAO phieuNhapHangDAO;

    // Thay đổi constructor để nhận PhieuNhapHangDAO
    public PhieuNhapHangServiceImpl(PhieuNhapHangDAO phieuNhapHangDAO) {
        this.phieuNhapHangDAO = phieuNhapHangDAO;
    }

    // Các phương thức giờ đây nhận Connection từ bên ngoài
    @Override
    public Integer addPhieuNhapHang(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException {
        // Loại bỏ try-with-resources và gọi DatabaseConnection.getConnection()
        return phieuNhapHangDAO.addPhieuNhapHang(conn, phieuNhapHang); // Trả về ID được tạo
    }

    @Override
    public void updatePhieuNhapHang(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException {
        phieuNhapHangDAO.updatePhieuNhapHang(conn, phieuNhapHang);
    }

    @Override
    public void deletePhieuNhapHang(Connection conn, Integer maPhieuNhap) throws SQLException {
        phieuNhapHangDAO.deletePhieuNhapHang(conn, maPhieuNhap);
    }

    @Override
    public PhieuNhapHang getPhieuNhapHangById(Connection conn, Integer maPhieuNhap) throws SQLException {
        return phieuNhapHangDAO.getPhieuNhapHangById(conn, maPhieuNhap);
    }

    @Override
    public List<PhieuNhapHang> getAllPhieuNhapHang(Connection conn) throws SQLException {
        return phieuNhapHangDAO.getAllPhieuNhapHang(conn);
    }

    @Override
    public List<PhieuNhapHang> getPhieuNhapHangByNhanVien(Connection conn, String maNhanVien) throws SQLException {
        return phieuNhapHangDAO.getPhieuNhapHangByNhanVien(conn, maNhanVien);
    }

    @Override
    public List<PhieuNhapHang> getPhieuNhapHangByNhaCungCap(Connection conn, String maNhaCungCap) throws SQLException {
        return phieuNhapHangDAO.getPhieuNhapHangByNhaCungCap(conn, maNhaCungCap);
    }

    @Override
    public List<PhieuNhapHang> getPhieuNhapHangByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return phieuNhapHangDAO.getPhieuNhapHangByDateRange(conn, startDate, endDate);
    }
}