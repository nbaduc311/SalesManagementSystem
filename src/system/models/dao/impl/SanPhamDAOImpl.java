// system.models.dao.impl/SanPhamDAOImpl.java
package system.models.dao.impl;

import system.models.dao.SanPhamDAO;
import system.models.entity.SanPham;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAOImpl implements SanPhamDAO {

    private static final String INSERT_SQL = "INSERT INTO SanPham (TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE SanPham SET TenSanPham = ?, DonGia = ?, NgaySanXuat = ?, ThongSoKyThuat = ?, MaLoaiSanPham = ? WHERE MaSanPham = ?";
    private static final String DELETE_SQL = "DELETE FROM SanPham WHERE MaSanPham = ?";
    private static final String SELECT_BY_MA_SANPHAM_SQL = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham WHERE MaSanPham = ?";
    private static final String SELECT_BY_INTERNAL_ID_SQL = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham WHERE InternalID = ?";
    private static final String SELECT_ALL_SQL = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham";
    private static final String SEARCH_BY_NAME_SQL = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham WHERE TenSanPham LIKE ?";
    private static final String SELECT_BY_LOAISANPHAM_SQL = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham WHERE MaLoaiSanPham = ?";

    @Override
    public void addSanPham(Connection conn, SanPham sanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, sanPham.getTenSanPham());
            stmt.setBigDecimal(2, sanPham.getDonGia());
            stmt.setDate(3, Date.valueOf(sanPham.getNgaySanXuat()));
            stmt.setString(4, sanPham.getThongSoKyThuat());
            stmt.setString(5, sanPham.getMaLoaiSanPham());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateSanPham(Connection conn, SanPham sanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, sanPham.getTenSanPham());
            stmt.setBigDecimal(2, sanPham.getDonGia());
            stmt.setDate(3, Date.valueOf(sanPham.getNgaySanXuat()));
            stmt.setString(4, sanPham.getThongSoKyThuat());
            stmt.setString(5, sanPham.getMaLoaiSanPham());
            stmt.setString(6, sanPham.getMaSanPham());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteSanPham(Connection conn, String maSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, maSanPham);
            stmt.executeUpdate();
        }
    }

    @Override
    public SanPham getSanPhamById(Connection conn, String maSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MA_SANPHAM_SQL)) {
            stmt.setString(1, maSanPham);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public SanPham getSanPhamByInternalID(Connection conn, Integer internalID) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_INTERNAL_ID_SQL)) {
            stmt.setInt(1, internalID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractSanPhamFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<SanPham> getAllSanPham(Connection conn) throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sanPhamList.add(extractSanPhamFromResultSet(rs));
            }
        }
        return sanPhamList;
    }
    
    @Override
    public List<SanPham> searchSanPhamByName(Connection conn, String name) throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_NAME_SQL)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sanPhamList.add(extractSanPhamFromResultSet(rs));
                }
            }
        }
        return sanPhamList;
    }

    @Override
    public List<SanPham> getSanPhamByLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_LOAISANPHAM_SQL)) {
            stmt.setString(1, maLoaiSanPham);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sanPhamList.add(extractSanPhamFromResultSet(rs));
                }
            }
        }
        return sanPhamList;
    }

    private SanPham extractSanPhamFromResultSet(ResultSet rs) throws SQLException {
        Integer internalID = rs.getInt("InternalID");
        String maSanPham = rs.getString("MaSanPham");
        String tenSanPham = rs.getString("TenSanPham");
        BigDecimal donGia = rs.getBigDecimal("DonGia");
        LocalDate ngaySanXuat = rs.getDate("NgaySanXuat").toLocalDate();
        String thongSoKyThuat = rs.getString("ThongSoKyThuat");
        String maLoaiSanPham = rs.getString("MaLoaiSanPham");
        return new SanPham(internalID, maSanPham, tenSanPham, donGia, ngaySanXuat, thongSoKyThuat, maLoaiSanPham);
    }
}