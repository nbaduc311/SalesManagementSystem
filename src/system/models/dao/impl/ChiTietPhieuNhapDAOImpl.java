// system.models.dao.impl/ChiTietPhieuNhapDAOImpl.java
package system.models.dao.impl;

import system.models.dao.ChiTietPhieuNhapDAO;
import system.models.entity.ChiTietPhieuNhap;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhapDAOImpl implements ChiTietPhieuNhapDAO {

    private static final String INSERT_SQL = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE ChiTietPhieuNhap SET MaPhieuNhap = ?, MaSanPham = ?, SoLuong = ?, DonGiaNhap = ? WHERE MaChiTietPhieuNhap = ?";
    private static final String DELETE_SQL = "DELETE FROM ChiTietPhieuNhap WHERE MaChiTietPhieuNhap = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT MaChiTietPhieuNhap, MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap FROM ChiTietPhieuNhap WHERE MaChiTietPhieuNhap = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaChiTietPhieuNhap, MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap FROM ChiTietPhieuNhap";
    private static final String SELECT_BY_MAPHIEUNHAP_SQL = "SELECT MaChiTietPhieuNhap, MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";

    @Override
    public void addChiTietPhieuNhap(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, chiTietPhieuNhap.getMaPhieuNhap());
            stmt.setString(2, chiTietPhieuNhap.getMaSanPham());
            stmt.setInt(3, chiTietPhieuNhap.getSoLuong());
            stmt.setBigDecimal(4, chiTietPhieuNhap.getDonGiaNhap());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateChiTietPhieuNhap(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, chiTietPhieuNhap.getMaPhieuNhap());
            stmt.setString(2, chiTietPhieuNhap.getMaSanPham());
            stmt.setInt(3, chiTietPhieuNhap.getSoLuong());
            stmt.setBigDecimal(4, chiTietPhieuNhap.getDonGiaNhap());
            stmt.setInt(5, chiTietPhieuNhap.getMaChiTietPhieuNhap());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteChiTietPhieuNhap(Connection conn, Integer maChiTietPhieuNhap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, maChiTietPhieuNhap);
            stmt.executeUpdate();
        }
    }

    @Override
    public ChiTietPhieuNhap getChiTietPhieuNhapById(Connection conn, Integer maChiTietPhieuNhap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, maChiTietPhieuNhap);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractChiTietPhieuNhapFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ChiTietPhieuNhap> getAllChiTietPhieuNhap(Connection conn) throws SQLException {
        List<ChiTietPhieuNhap> chiTietPhieuNhapList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                chiTietPhieuNhapList.add(extractChiTietPhieuNhapFromResultSet(rs));
            }
        }
        return chiTietPhieuNhapList;
    }
    
    @Override
    public List<ChiTietPhieuNhap> getChiTietPhieuNhapByMaPhieuNhap(Connection conn, Integer maPhieuNhap) throws SQLException {
        List<ChiTietPhieuNhap> chiTietPhieuNhapList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MAPHIEUNHAP_SQL)) {
            stmt.setInt(1, maPhieuNhap);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chiTietPhieuNhapList.add(extractChiTietPhieuNhapFromResultSet(rs));
                }
            }
        }
        return chiTietPhieuNhapList;
    }

    private ChiTietPhieuNhap extractChiTietPhieuNhapFromResultSet(ResultSet rs) throws SQLException {
        Integer maChiTietPhieuNhap = rs.getInt("MaChiTietPhieuNhap");
        Integer maPhieuNhap = rs.getInt("MaPhieuNhap");
        String maSanPham = rs.getString("MaSanPham");
        Integer soLuong = rs.getInt("SoLuong");
        BigDecimal donGiaNhap = rs.getBigDecimal("DonGiaNhap");
        return new ChiTietPhieuNhap(maChiTietPhieuNhap, maPhieuNhap, maSanPham, soLuong, donGiaNhap);
    }
}