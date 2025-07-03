package system.services.impl;

import system.models.dao.ViTriDungSanPhamDAO;
import system.models.entity.ViTriDungSanPham;
import system.services.ViTriDungSanPhamService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ViTriDungSanPhamServiceImpl implements ViTriDungSanPhamService {

    private ViTriDungSanPhamDAO viTriDungSanPhamDAO;

    // Thay đổi constructor để nhận ViTriDungSanPhamDAO
    public ViTriDungSanPhamServiceImpl(ViTriDungSanPhamDAO viTriDungSanPhamDAO) {
        this.viTriDungSanPhamDAO = viTriDungSanPhamDAO;
    }

    // Các phương thức giờ đây nhận Connection từ bên ngoài
    @Override
    public void addViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException {
        // Loại bỏ try-with-resources và gọi DatabaseConnection.getConnection()
        viTriDungSanPhamDAO.addViTriDungSanPham(conn, viTriDungSanPham);
    }

    @Override
    public void updateViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException {
        viTriDungSanPhamDAO.updateViTriDungSanPham(conn, viTriDungSanPham);
    }

    @Override
    public void deleteViTriDungSanPham(Connection conn, String maNganDung) throws SQLException {
        viTriDungSanPhamDAO.deleteViTriDungSanPham(conn, maNganDung);
    }

    @Override
    public ViTriDungSanPham getViTriDungSanPhamById(Connection conn, String maNganDung) throws SQLException {
        return viTriDungSanPhamDAO.getViTriDungSanPhamById(conn, maNganDung);
    }

    @Override
    public ViTriDungSanPham getViTriDungSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException {
        return viTriDungSanPhamDAO.getViTriDungSanPhamByInternalId(conn, internalID);
    }

    @Override
    public List<ViTriDungSanPham> getAllViTriDungSanPham(Connection conn) throws SQLException {
        return viTriDungSanPhamDAO.getAllViTriDungSanPham(conn);
    }

    @Override
    public ViTriDungSanPham getViTriDungSanPhamByTenNganDung(Connection conn, String tenNganDung) throws SQLException {
        return viTriDungSanPhamDAO.getViTriDungSanPhamByTenNganDung(conn, tenNganDung);
    }
}