// system.models.dao.impl/ChiTietViTriDAOImpl.java
package system.models.dao.impl;

import system.models.dao.ChiTietViTriDAO;
import system.models.entity.ChiTietViTri;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietViTriDAOImpl implements ChiTietViTriDAO {

    private static final String INSERT_SQL = "INSERT INTO ChiTietViTri (MaNganDung, MaSanPham, SoLuong) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE ChiTietViTri SET MaNganDung = ?, MaSanPham = ?, SoLuong = ? WHERE MaChiTietViTri = ?";
    private static final String DELETE_SQL = "DELETE FROM ChiTietViTri WHERE MaChiTietViTri = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaChiTietViTri = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri";
    private static final String SELECT_BY_NGANDUNG_SANPHAM_SQL = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaNganDung = ? AND MaSanPham = ?";
    private static final String SELECT_BY_MANGANDUNG_SQL = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaNganDung = ?";
    private static final String SELECT_BY_MASANPHAM_SQL = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaSanPham = ?";


    @Override
    public void addChiTietViTri(Connection conn, ChiTietViTri chiTietViTri) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, chiTietViTri.getMaNganDung());
            stmt.setString(2, chiTietViTri.getMaSanPham());
            stmt.setInt(3, chiTietViTri.getSoLuong());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateChiTietViTri(Connection conn, ChiTietViTri chiTietViTri) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, chiTietViTri.getMaNganDung());
            stmt.setString(2, chiTietViTri.getMaSanPham());
            stmt.setInt(3, chiTietViTri.getSoLuong());
            stmt.setInt(4, chiTietViTri.getMaChiTietViTri());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteChiTietViTri(Connection conn, Integer maChiTietViTri) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, maChiTietViTri);
            stmt.executeUpdate();
        }
    }

    @Override
    public ChiTietViTri getChiTietViTriById(Connection conn, Integer maChiTietViTri) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, maChiTietViTri);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractChiTietViTriFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ChiTietViTri> getAllChiTietViTri(Connection conn) throws SQLException {
        List<ChiTietViTri> chiTietViTriList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                chiTietViTriList.add(extractChiTietViTriFromResultSet(rs));
            }
        }
        return chiTietViTriList;
    }
    
    @Override
    public ChiTietViTri getChiTietViTriByNganDungAndSanPham(Connection conn, String maNganDung, String maSanPham) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NGANDUNG_SANPHAM_SQL)) {
            stmt.setString(1, maNganDung);
            stmt.setString(2, maSanPham);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractChiTietViTriFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ChiTietViTri> getChiTietViTriByMaNganDung(Connection conn, String maNganDung) throws SQLException {
        List<ChiTietViTri> chiTietViTriList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MANGANDUNG_SQL)) {
            stmt.setString(1, maNganDung);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chiTietViTriList.add(extractChiTietViTriFromResultSet(rs));
                }
            }
        }
        return chiTietViTriList;
    }

    @Override
    public List<ChiTietViTri> getChiTietViTriByMaSanPham(Connection conn, String maSanPham) throws SQLException {
        List<ChiTietViTri> chiTietViTriList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MASANPHAM_SQL)) {
            stmt.setString(1, maSanPham);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chiTietViTriList.add(extractChiTietViTriFromResultSet(rs));
                }
            }
        }
        return chiTietViTriList;
    }

    private ChiTietViTri extractChiTietViTriFromResultSet(ResultSet rs) throws SQLException {
        Integer maChiTietViTri = rs.getInt("MaChiTietViTri");
        String maNganDung = rs.getString("MaNganDung");
        String maSanPham = rs.getString("MaSanPham");
        Integer soLuong = rs.getInt("SoLuong");
        return new ChiTietViTri(maChiTietViTri, maNganDung, maSanPham, soLuong);
    }
}