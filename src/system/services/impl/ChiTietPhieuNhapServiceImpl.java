package system.services.impl;

import system.models.dao.ChiTietPhieuNhapDAO;
import system.models.entity.ChiTietPhieuNhap;
import system.services.ChiTietPhieuNhapService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ChiTietPhieuNhapServiceImpl implements ChiTietPhieuNhapService {

    private ChiTietPhieuNhapDAO chiTietPhieuNhapDAO;

    // Thay đổi constructor để nhận ChiTietPhieuNhapDAO
    public ChiTietPhieuNhapServiceImpl(ChiTietPhieuNhapDAO chiTietPhieuNhapDAO) {
        this.chiTietPhieuNhapDAO = chiTietPhieuNhapDAO;
    }

    // Các phương thức giờ đây nhận Connection từ bên ngoài
    @Override
    public void addChiTietPhieuNhap(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException {
        // Loại bỏ try-with-resources và gọi DatabaseConnection.getConnection()
        chiTietPhieuNhapDAO.addChiTietPhieuNhap(conn, chiTietPhieuNhap);
    }

    @Override
    public void updateChiTietPhieuNhap(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException {
        chiTietPhieuNhapDAO.updateChiTietPhieuNhap(conn, chiTietPhieuNhap);
    }

    @Override
    public void deleteChiTietPhieuNhap(Connection conn, Integer maChiTietPhieuNhap) throws SQLException {
        chiTietPhieuNhapDAO.deleteChiTietPhieuNhap(conn, maChiTietPhieuNhap);
    }

    @Override
    public ChiTietPhieuNhap getChiTietPhieuNhapById(Connection conn, Integer maChiTietPhieuNhap) throws SQLException {
        return chiTietPhieuNhapDAO.getChiTietPhieuNhapById(conn, maChiTietPhieuNhap);
    }

    @Override
    public List<ChiTietPhieuNhap> getAllChiTietPhieuNhap(Connection conn) throws SQLException {
        return chiTietPhieuNhapDAO.getAllChiTietPhieuNhap(conn);
    }

    @Override
    public List<ChiTietPhieuNhap> getChiTietPhieuNhapByMaPhieuNhap(Connection conn, Integer maPhieuNhap) throws SQLException {
        return chiTietPhieuNhapDAO.getChiTietPhieuNhapByMaPhieuNhap(conn, maPhieuNhap);
    }
}