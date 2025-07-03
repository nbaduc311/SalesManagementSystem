// system.models.dao/PhucHoiDAO.java
package system.models.dao;

import system.models.entity.PhucHoi;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface PhucHoiDAO {
    void addPhucHoi(Connection conn, PhucHoi phucHoi) throws SQLException;
    void updatePhucHoi(Connection conn, PhucHoi phucHoi) throws SQLException;
    void deletePhucHoi(Connection conn, Integer maPhucHoi) throws SQLException;
    PhucHoi getPhucHoiById(Connection conn, Integer maPhucHoi) throws SQLException;
    List<PhucHoi> getAllPhucHoi(Connection conn) throws SQLException;
    List<PhucHoi> getPhucHoiByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    List<PhucHoi> getPhucHoiByMaSaoLuu(Connection conn, Integer maSaoLuu) throws SQLException;
    List<PhucHoi> getPhucHoiByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
}