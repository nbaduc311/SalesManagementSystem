package system.services.impl;

import system.models.dao.ChiTietHoaDonDAO;
import system.models.entity.ChiTietHoaDon;
import system.services.ChiTietHoaDonService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ChiTietHoaDonServiceImpl implements ChiTietHoaDonService {

    private ChiTietHoaDonDAO chiTietHoaDonDAO;

    // Thay đổi constructor để nhận ChiTietHoaDonDAO
    public ChiTietHoaDonServiceImpl(ChiTietHoaDonDAO chiTietHoaDonDAO) {
        this.chiTietHoaDonDAO = chiTietHoaDonDAO;
    }

    // Các phương thức giờ đây nhận Connection từ bên ngoài
    @Override
    public void addChiTietHoaDon(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException {
        // Loại bỏ try-with-resources và gọi DatabaseConnection.getConnection()
        chiTietHoaDonDAO.addChiTietHoaDon(conn, chiTietHoaDon);
    }

    @Override
    public void updateChiTietHoaDon(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException {
        chiTietHoaDonDAO.updateChiTietHoaDon(conn, chiTietHoaDon);
    }

    @Override
    public void deleteChiTietHoaDon(Connection conn, Integer maChiTietHoaDon) throws SQLException {
        chiTietHoaDonDAO.deleteChiTietHoaDon(conn, maChiTietHoaDon);
    }

    @Override
    public ChiTietHoaDon getChiTietHoaDonById(Connection conn, Integer maChiTietHoaDon) throws SQLException {
        return chiTietHoaDonDAO.getChiTietHoaDonById(conn, maChiTietHoaDon);
    }

    @Override
    public List<ChiTietHoaDon> getAllChiTietHoaDon(Connection conn) throws SQLException {
        return chiTietHoaDonDAO.getAllChiTietHoaDon(conn);
    }

    @Override
    public List<ChiTietHoaDon> getChiTietHoaDonByMaHoaDon(Connection conn, Integer maHoaDon) throws SQLException {
        return chiTietHoaDonDAO.getChiTietHoaDonByMaHoaDon(conn, maHoaDon);
    }
}