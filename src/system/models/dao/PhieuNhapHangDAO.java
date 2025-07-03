// system.models.dao/PhieuNhapHangDAO.java
package system.models.dao;

import system.models.entity.PhieuNhapHang;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface PhieuNhapHangDAO {
    // Thay đổi kiểu trả về từ void thành Integer để trả về mã phiếu nhập đã tạo
    Integer addPhieuNhapHang(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException;
    void updatePhieuNhapHang(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException;
    void deletePhieuNhapHang(Connection conn, Integer maPhieuNhap) throws SQLException;
    PhieuNhapHang getPhieuNhapHangById(Connection conn, Integer maPhieuNhap) throws SQLException;
    List<PhieuNhapHang> getAllPhieuNhapHang(Connection conn) throws SQLException;
    List<PhieuNhapHang> getPhieuNhapHangByNhanVien(Connection conn, String maNhanVien) throws SQLException;
    List<PhieuNhapHang> getPhieuNhapHangByNhaCungCap(Connection conn, String maNhaCungCap) throws SQLException;
    List<PhieuNhapHang> getPhieuNhapHangByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
}