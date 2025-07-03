package system.services;

import system.models.entity.ChiTietPhieuNhap;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

public interface ChiTietPhieuNhapService {
    // Thêm tham số Connection vào tất cả các phương thức
    void addChiTietPhieuNhap(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException;
    void updateChiTietPhieuNhap(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException;
    void deleteChiTietPhieuNhap(Connection conn, Integer maChiTietPhieuNhap) throws SQLException;
    ChiTietPhieuNhap getChiTietPhieuNhapById(Connection conn, Integer maChiTietPhieuNhap) throws SQLException;
    List<ChiTietPhieuNhap> getAllChiTietPhieuNhap(Connection conn) throws SQLException;
    List<ChiTietPhieuNhap> getChiTietPhieuNhapByMaPhieuNhap(Connection conn, Integer maPhieuNhap) throws SQLException;
}