package system.services.impl;

import system.models.dao.NhaCungCapDAO;
import system.models.entity.NhaCungCap;
import system.services.NhaCungCapService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NhaCungCapServiceImpl implements NhaCungCapService {

    private NhaCungCapDAO nhaCungCapDAO;

    // Modified constructor to accept NhaCungCapDAO
    public NhaCungCapServiceImpl(NhaCungCapDAO nhaCungCapDAO) {
        this.nhaCungCapDAO = nhaCungCapDAO;
    }

    // Methods now accept Connection from the outside
    @Override
    public void addNhaCungCap(Connection conn, NhaCungCap nhaCungCap) throws SQLException {
        // Removed try-with-resources and DatabaseConnection.getConnection() call
        nhaCungCapDAO.addNhaCungCap(conn, nhaCungCap);
    }

    @Override
    public void updateNhaCungCap(Connection conn, NhaCungCap nhaCungCap) throws SQLException {
        nhaCungCapDAO.updateNhaCungCap(conn, nhaCungCap);
    }

    @Override
    public void deleteNhaCungCap(Connection conn, String maNhaCungCap) throws SQLException {
        nhaCungCapDAO.deleteNhaCungCap(conn, maNhaCungCap);
    }

    @Override
    public NhaCungCap getNhaCungCapById(Connection conn, String maNhaCungCap) throws SQLException {
        return nhaCungCapDAO.getNhaCungCapById(conn, maNhaCungCap);
    }

    @Override
    public NhaCungCap getNhaCungCapByInternalId(Connection conn, Integer internalID) throws SQLException {
        return nhaCungCapDAO.getNhaCungCapByInternalId(conn, internalID);
    }

    @Override
    public List<NhaCungCap> getAllNhaCungCap(Connection conn) throws SQLException {
        return nhaCungCapDAO.getAllNhaCungCap(conn);
    }

    @Override
    public NhaCungCap getNhaCungCapBySdt(Connection conn, String sdt) throws SQLException {
        return nhaCungCapDAO.getNhaCungCapBySdt(conn, sdt);
    }

    @Override
    public List<NhaCungCap> searchNhaCungCapByName(Connection conn, String name) throws SQLException {
        return nhaCungCapDAO.searchNhaCungCapByName(conn, name);
    }
}