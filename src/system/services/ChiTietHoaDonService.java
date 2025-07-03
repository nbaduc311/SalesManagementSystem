package system.services;

import system.models.entity.ChiTietHoaDon;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

public interface ChiTietHoaDonService {
    // Thêm tham số Connection vào tất cả các phương thức
    void addChiTietHoaDon(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException;
    void updateChiTietHoaDon(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException;
    void deleteChiTietHoaDon(Connection conn, Integer maChiTietHoaDon) throws SQLException;
    ChiTietHoaDon getChiTietHoaDonById(Connection conn, Integer maChiTietHoaDon) throws SQLException;
    List<ChiTietHoaDon> getAllChiTietHoaDon(Connection conn) throws SQLException;
    List<ChiTietHoaDon> getChiTietHoaDonByMaHoaDon(Connection conn, Integer maHoaDon) throws SQLException;
}