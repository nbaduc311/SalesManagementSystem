package system.services;

import system.models.entity.SaoLuu;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface SaoLuuService {
    // Thêm tham số Connection vào tất cả các phương thức
    void addSaoLuu(Connection conn, SaoLuu saoLuu) throws SQLException;
    void updateSaoLuu(Connection conn, SaoLuu saoLuu) throws SQLException;
    void deleteSaoLuu(Connection conn, Integer maSaoLuu) throws SQLException;
    SaoLuu getSaoLuuById(Connection conn, Integer maSaoLuu) throws SQLException;
    List<SaoLuu> getAllSaoLuu(Connection conn) throws SQLException;
    List<SaoLuu> getSaoLuuByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    List<SaoLuu> getSaoLuuByLoaiSaoLuu(Connection conn, String loaiSaoLuu) throws SQLException;
    List<SaoLuu> getSaoLuuByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
}