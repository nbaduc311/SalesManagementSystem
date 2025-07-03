// system.models.dao/HoaDonDAO.java
package system.models.dao;

import system.models.entity.HoaDon;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface HoaDonDAO {
    HoaDon addHoaDon(Connection conn, HoaDon hoaDon) throws SQLException;
    void updateHoaDon(Connection conn, HoaDon hoaDon) throws SQLException;
    void deleteHoaDon(Connection conn, Integer maHoaDon) throws SQLException;
    HoaDon getHoaDonById(Connection conn, Integer maHoaDon) throws SQLException;
    List<HoaDon> getAllHoaDon(Connection conn) throws SQLException;
    List<HoaDon> getHoaDonByMaNhanVienLap(Connection conn, String maNhanVienLap) throws SQLException;
    List<HoaDon> getHoaDonByMaKhachHang(Connection conn, String maKhachHang) throws SQLException;
    List<HoaDon> getHoaDonByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
}