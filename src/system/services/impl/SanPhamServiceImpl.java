package system.services.impl;

import system.models.dao.SanPhamDAO;
import system.models.entity.SanPham;
import system.services.SanPhamService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SanPhamServiceImpl implements SanPhamService {

    private SanPhamDAO sanPhamDAO;

    // Thay đổi constructor để nhận SanPhamDAO
    public SanPhamServiceImpl(SanPhamDAO sanPhamDAO) {
        this.sanPhamDAO = sanPhamDAO;
    }

    // Các phương thức giờ đây nhận Connection từ bên ngoài
    @Override
    public void addSanPham(Connection conn, SanPham sanPham) throws SQLException {
        // Loại bỏ try-with-resources và gọi DatabaseConnection.getConnection()
        sanPhamDAO.addSanPham(conn, sanPham);
    }

    @Override
    public void updateSanPham(Connection conn, SanPham sanPham) throws SQLException {
        sanPhamDAO.updateSanPham(conn, sanPham);
    }

    @Override
    public void deleteSanPham(Connection conn, String maSanPham) throws SQLException {
        sanPhamDAO.deleteSanPham(conn, maSanPham);
    }

    @Override
    public SanPham getSanPhamById(Connection conn, String maSanPham) throws SQLException {
        return sanPhamDAO.getSanPhamById(conn, maSanPham);
    }

    @Override
    public SanPham getSanPhamByInternalID(Connection conn, Integer internalID) throws SQLException {
        return sanPhamDAO.getSanPhamByInternalID(conn, internalID);
    }

    @Override
    public List<SanPham> getAllSanPham(Connection conn) throws SQLException {
        return sanPhamDAO.getAllSanPham(conn);
    }

    @Override
    public List<SanPham> searchSanPhamByName(Connection conn, String name) throws SQLException {
        return sanPhamDAO.searchSanPhamByName(conn, name);
    }

    @Override
    public List<SanPham> getSanPhamByLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException {
        return sanPhamDAO.getSanPhamByLoaiSanPham(conn, maLoaiSanPham);
    }
}