package system.services.impl;

import system.models.dao.SaoLuuDAO;
import system.models.entity.SaoLuu;
import system.services.SaoLuuService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class SaoLuuServiceImpl implements SaoLuuService {

    private SaoLuuDAO saoLuuDAO;

    // Thay đổi constructor để nhận SaoLuuDAO
    public SaoLuuServiceImpl(SaoLuuDAO saoLuuDAO) {
        this.saoLuuDAO = saoLuuDAO;
    }

    // Các phương thức giờ đây nhận Connection từ bên ngoài
    @Override
    public void addSaoLuu(Connection conn, SaoLuu saoLuu) throws SQLException {
        // Loại bỏ try-with-resources và gọi DatabaseConnection.getConnection()
        saoLuuDAO.addSaoLuu(conn, saoLuu);
    }

    @Override
    public void updateSaoLuu(Connection conn, SaoLuu saoLuu) throws SQLException {
        saoLuuDAO.updateSaoLuu(conn, saoLuu);
    }

    @Override
    public void deleteSaoLuu(Connection conn, Integer maSaoLuu) throws SQLException {
        saoLuuDAO.deleteSaoLuu(conn, maSaoLuu);
    }

    @Override
    public SaoLuu getSaoLuuById(Connection conn, Integer maSaoLuu) throws SQLException {
        return saoLuuDAO.getSaoLuuById(conn, maSaoLuu);
    }

    @Override
    public List<SaoLuu> getAllSaoLuu(Connection conn) throws SQLException {
        return saoLuuDAO.getAllSaoLuu(conn);
    }

    @Override
    public List<SaoLuu> getSaoLuuByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        return saoLuuDAO.getSaoLuuByMaNguoiDung(conn, maNguoiDung);
    }

    @Override
    public List<SaoLuu> getSaoLuuByLoaiSaoLuu(Connection conn, String loaiSaoLuu) throws SQLException {
        return saoLuuDAO.getSaoLuuByLoaiSaoLuu(conn, loaiSaoLuu);
    }

    @Override
    public List<SaoLuu> getSaoLuuByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return saoLuuDAO.getSaoLuuByDateRange(conn, startDate, endDate);
    }
}