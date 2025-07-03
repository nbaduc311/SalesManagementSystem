package system.services.impl;

import system.models.dao.PhucHoiDAO;
import system.models.entity.PhucHoi;
import system.services.PhucHoiService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class PhucHoiServiceImpl implements PhucHoiService {

    private PhucHoiDAO phucHoiDAO;

    // Thay đổi constructor để nhận PhucHoiDAO
    public PhucHoiServiceImpl(PhucHoiDAO phucHoiDAO) {
        this.phucHoiDAO = phucHoiDAO;
    }

    // Các phương thức giờ đây nhận Connection từ bên ngoài
    @Override
    public void addPhucHoi(Connection conn, PhucHoi phucHoi) throws SQLException {
        // Loại bỏ try-with-resources và gọi DatabaseConnection.getConnection()
        phucHoiDAO.addPhucHoi(conn, phucHoi);
    }

    @Override
    public void updatePhucHoi(Connection conn, PhucHoi phucHoi) throws SQLException {
        phucHoiDAO.updatePhucHoi(conn, phucHoi);
    }

    @Override
    public void deletePhucHoi(Connection conn, Integer maPhucHoi) throws SQLException {
        phucHoiDAO.deletePhucHoi(conn, maPhucHoi);
    }

    @Override
    public PhucHoi getPhucHoiById(Connection conn, Integer maPhucHoi) throws SQLException {
        return phucHoiDAO.getPhucHoiById(conn, maPhucHoi);
    }

    @Override
    public List<PhucHoi> getAllPhucHoi(Connection conn) throws SQLException {
        return phucHoiDAO.getAllPhucHoi(conn);
    }

    @Override
    public List<PhucHoi> getPhucHoiByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        return phucHoiDAO.getPhucHoiByMaNguoiDung(conn, maNguoiDung);
    }

    @Override
    public List<PhucHoi> getPhucHoiByMaSaoLuu(Connection conn, Integer maSaoLuu) throws SQLException {
        return phucHoiDAO.getPhucHoiByMaSaoLuu(conn, maSaoLuu);
    }

    @Override
    public List<PhucHoi> getPhucHoiByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return phucHoiDAO.getPhucHoiByDateRange(conn, startDate, endDate);
    }
}