// system.models.dao.impl/ViTriDungSanPhamDAOImpl.java
package system.models.dao.impl;

import system.models.dao.ViTriDungSanPhamDAO;
import system.models.entity.ViTriDungSanPham;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViTriDungSanPhamDAOImpl implements ViTriDungSanPhamDAO {

    private static final String INSERT_SQL = "INSERT INTO ViTriDungSanPham (TenNganDung) VALUES (?)";
    private static final String UPDATE_SQL = "UPDATE ViTriDungSanPham SET TenNganDung = ? WHERE MaNganDung = ?";
    private static final String DELETE_SQL = "DELETE FROM ViTriDungSanPham WHERE MaNganDung = ?";
    private static final String SELECT_BY_MA_NGANDUNG_SQL = "SELECT InternalID, MaNganDung, TenNganDung FROM ViTriDungSanPham WHERE MaNganDung = ?";
    private static final String SELECT_BY_INTERNAL_ID_SQL = "SELECT InternalID, MaNganDung, TenNganDung FROM ViTriDungSanPham WHERE InternalID = ?";
    private static final String SELECT_ALL_SQL = "SELECT InternalID, MaNganDung, TenNganDung FROM ViTriDungSanPham";
    private static final String SELECT_BY_TEN_NGANDUNG_SQL = "SELECT InternalID, MaNganDung, TenNganDung FROM ViTriDungSanPham WHERE TenNganDung = ?";

    @Override
    public void addViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, viTriDungSanPham.getTenNganDung());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, viTriDungSanPham.getTenNganDung());
            stmt.setString(2, viTriDungSanPham.getMaNganDung());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteViTriDungSanPham(Connection conn, String maNganDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, maNganDung);
            stmt.executeUpdate();
        }
    }

    @Override
    public ViTriDungSanPham getViTriDungSanPhamById(Connection conn, String maNganDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MA_NGANDUNG_SQL)) {
            stmt.setString(1, maNganDung);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractViTriDungSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public ViTriDungSanPham getViTriDungSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_INTERNAL_ID_SQL)) {
            stmt.setInt(1, internalID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractViTriDungSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ViTriDungSanPham> getAllViTriDungSanPham(Connection conn) throws SQLException {
        List<ViTriDungSanPham> viTriDungSanPhamList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                viTriDungSanPhamList.add(extractViTriDungSanPhamFromResultSet(rs));
            }
        }
        return viTriDungSanPhamList;
    }

    @Override
    public ViTriDungSanPham getViTriDungSanPhamByTenNganDung(Connection conn, String tenNganDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TEN_NGANDUNG_SQL)) {
            stmt.setString(1, tenNganDung);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractViTriDungSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private ViTriDungSanPham extractViTriDungSanPhamFromResultSet(ResultSet rs) throws SQLException {
        Integer internalID = rs.getInt("InternalID");
        String maNganDung = rs.getString("MaNganDung");
        String tenNganDung = rs.getString("TenNganDung");
        return new ViTriDungSanPham(internalID, maNganDung, tenNganDung);
    }
}