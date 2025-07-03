package system.services.impl;

import system.models.dao.ChiTietViTriDAO;
import system.models.entity.ChiTietViTri;
import system.services.ChiTietViTriService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ChiTietViTriServiceImpl implements ChiTietViTriService {

    private ChiTietViTriDAO chiTietViTriDAO;

    // Modified constructor to accept ChiTietViTriDAO
    public ChiTietViTriServiceImpl(ChiTietViTriDAO chiTietViTriDAO) {
        this.chiTietViTriDAO = chiTietViTriDAO;
    }

    // Methods now accept Connection from the outside
    @Override
    public void addChiTietViTri(Connection conn, ChiTietViTri chiTietViTri) throws SQLException {
        // Removed try-with-resources and DatabaseConnection.getConnection() call
        chiTietViTriDAO.addChiTietViTri(conn, chiTietViTri);
    }

    @Override
    public void updateChiTietViTri(Connection conn, ChiTietViTri chiTietViTri) throws SQLException {
        chiTietViTriDAO.updateChiTietViTri(conn, chiTietViTri);
    }

    @Override
    public void deleteChiTietViTri(Connection conn, Integer maChiTietViTri) throws SQLException {
        chiTietViTriDAO.deleteChiTietViTri(conn, maChiTietViTri);
    }

    @Override
    public ChiTietViTri getChiTietViTriById(Connection conn, Integer maChiTietViTri) throws SQLException {
        return chiTietViTriDAO.getChiTietViTriById(conn, maChiTietViTri);
    }

    @Override
    public List<ChiTietViTri> getAllChiTietViTri(Connection conn) throws SQLException {
        return chiTietViTriDAO.getAllChiTietViTri(conn);
    }

    @Override
    public ChiTietViTri getChiTietViTriByNganDungAndSanPham(Connection conn, String maNganDung, String maSanPham) throws SQLException {
        return chiTietViTriDAO.getChiTietViTriByNganDungAndSanPham(conn, maNganDung, maSanPham);
    }

    @Override
    public List<ChiTietViTri> getChiTietViTriByMaNganDung(Connection conn, String maNganDung) throws SQLException {
        return chiTietViTriDAO.getChiTietViTriByMaNganDung(conn, maNganDung);
    }

    @Override
    public List<ChiTietViTri> getChiTietViTriByMaSanPham(Connection conn, String maSanPham) throws SQLException {
        return chiTietViTriDAO.getChiTietViTriByMaSanPham(conn, maSanPham);
    }
}