package system.services.impl;

import system.models.dao.HoaDonDAO;
import system.models.entity.HoaDon;
import system.services.HoaDonService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDonServiceImpl implements HoaDonService {

    private HoaDonDAO hoaDonDAO;

    // Modified constructor to accept HoaDonDAO
    public HoaDonServiceImpl(HoaDonDAO hoaDonDAO) {
        this.hoaDonDAO = hoaDonDAO;
    }

    // Methods now accept Connection from the outside
    @Override
    public void addHoaDon(Connection conn, HoaDon hoaDon) throws SQLException {
        // Removed try-with-resources and DatabaseConnection.getConnection() call
        hoaDonDAO.addHoaDon(conn, hoaDon);
    }

    @Override
    public void updateHoaDon(Connection conn, HoaDon hoaDon) throws SQLException {
        hoaDonDAO.updateHoaDon(conn, hoaDon);
    }

    @Override
    public void deleteHoaDon(Connection conn, Integer maHoaDon) throws SQLException {
        hoaDonDAO.deleteHoaDon(conn, maHoaDon);
    }

    @Override
    public HoaDon getHoaDonById(Connection conn, Integer maHoaDon) throws SQLException {
        return hoaDonDAO.getHoaDonById(conn, maHoaDon);
    }

    @Override
    public List<HoaDon> getAllHoaDon(Connection conn) throws SQLException {
        return hoaDonDAO.getAllHoaDon(conn);
    }

    @Override
    public List<HoaDon> getHoaDonByMaNhanVienLap(Connection conn, String maNhanVienLap) throws SQLException {
        return hoaDonDAO.getHoaDonByMaNhanVienLap(conn, maNhanVienLap);
    }

    @Override
    public List<HoaDon> getHoaDonByMaKhachHang(Connection conn, String maKhachHang) throws SQLException {
        return hoaDonDAO.getHoaDonByMaKhachHang(conn, maKhachHang);
    }

    @Override
    public List<HoaDon> getHoaDonByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        return hoaDonDAO.getHoaDonByDateRange(conn, startDate, endDate);
    }
}