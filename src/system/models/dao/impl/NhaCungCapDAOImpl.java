// system.models.dao.impl/NhaCungCapDAOImpl.java
package system.models.dao.impl;

import system.models.dao.NhaCungCapDAO;
import system.models.entity.NhaCungCap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAOImpl implements NhaCungCapDAO {

    private static final String INSERT_SQL = "INSERT INTO NhaCungCap (TenNhaCungCap, DiaChi, Sdt, Email, Website, MoTa) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE NhaCungCap SET TenNhaCungCap = ?, DiaChi = ?, Sdt = ?, Email = ?, Website = ?, MoTa = ? WHERE MaNhaCungCap = ?";
    private static final String DELETE_SQL = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";
    private static final String SELECT_BY_MA_NHACUNGCAP_SQL = "SELECT InternalID, MaNhaCungCap, TenNhaCungCap, DiaChi, Sdt, Email, Website, MoTa FROM NhaCungCap WHERE MaNhaCungCap = ?";
    private static final String SELECT_BY_INTERNAL_ID_SQL = "SELECT InternalID, MaNhaCungCap, TenNhaCungCap, DiaChi, Sdt, Email, Website, MoTa FROM NhaCungCap WHERE InternalID = ?";
    private static final String SELECT_ALL_SQL = "SELECT InternalID, MaNhaCungCap, TenNhaCungCap, DiaChi, Sdt, Email, Website, MoTa FROM NhaCungCap";
    private static final String SELECT_BY_SDT_SQL = "SELECT InternalID, MaNhaCungCap, TenNhaCungCap, DiaChi, Sdt, Email, Website, MoTa FROM NhaCungCap WHERE Sdt = ?";
    private static final String SEARCH_BY_NAME_SQL = "SELECT InternalID, MaNhaCungCap, TenNhaCungCap, DiaChi, Sdt, Email, Website, MoTa FROM NhaCungCap WHERE TenNhaCungCap LIKE ?";

    @Override
    public void addNhaCungCap(Connection conn, NhaCungCap nhaCungCap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, nhaCungCap.getTenNhaCungCap());
            stmt.setString(2, nhaCungCap.getDiaChi());
            stmt.setString(3, nhaCungCap.getSdt());
            stmt.setString(4, nhaCungCap.getEmail());
            stmt.setString(5, nhaCungCap.getWebsite());
            stmt.setString(6, nhaCungCap.getMoTa());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateNhaCungCap(Connection conn, NhaCungCap nhaCungCap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, nhaCungCap.getTenNhaCungCap());
            stmt.setString(2, nhaCungCap.getDiaChi());
            stmt.setString(3, nhaCungCap.getSdt());
            stmt.setString(4, nhaCungCap.getEmail());
            stmt.setString(5, nhaCungCap.getWebsite());
            stmt.setString(6, nhaCungCap.getMoTa());
            stmt.setString(7, nhaCungCap.getMaNhaCungCap());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteNhaCungCap(Connection conn, String maNhaCungCap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, maNhaCungCap);
            stmt.executeUpdate();
        }
    }

    @Override
    public NhaCungCap getNhaCungCapById(Connection conn, String maNhaCungCap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MA_NHACUNGCAP_SQL)) {
            stmt.setString(1, maNhaCungCap);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNhaCungCapFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public NhaCungCap getNhaCungCapByInternalId(Connection conn, Integer internalID) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_INTERNAL_ID_SQL)) {
            stmt.setInt(1, internalID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNhaCungCapFromResultSet(rs);
                }
            }
        }
        return null;
            }

    @Override
    public List<NhaCungCap> getAllNhaCungCap(Connection conn) throws SQLException {
        List<NhaCungCap> nhaCungCapList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                nhaCungCapList.add(extractNhaCungCapFromResultSet(rs));
            }
        }
        return nhaCungCapList;
    }
    
    @Override
    public NhaCungCap getNhaCungCapBySdt(Connection conn, String sdt) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_SDT_SQL)) {
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNhaCungCapFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<NhaCungCap> searchNhaCungCapByName(Connection conn, String name) throws SQLException {
        List<NhaCungCap> nhaCungCapList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_NAME_SQL)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    nhaCungCapList.add(extractNhaCungCapFromResultSet(rs));
                }
            }
        }
        return nhaCungCapList;
    }

    private NhaCungCap extractNhaCungCapFromResultSet(ResultSet rs) throws SQLException {
        Integer internalID = rs.getInt("InternalID");
        String maNhaCungCap = rs.getString("MaNhaCungCap");
        String tenNhaCungCap = rs.getString("TenNhaCungCap");
        String diaChi = rs.getString("DiaChi");
        String sdt = rs.getString("Sdt");
        String email = rs.getString("Email");
        String website = rs.getString("Website");
        String moTa = rs.getString("MoTa");
        return new NhaCungCap(internalID, maNhaCungCap, tenNhaCungCap, diaChi, sdt, email, website, moTa);
    }
}