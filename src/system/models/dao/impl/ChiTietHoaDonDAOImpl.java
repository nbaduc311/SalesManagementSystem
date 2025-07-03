// system.models.dao.impl/ChiTietHoaDonDAOImpl.java
package system.models.dao.impl;

import system.models.dao.ChiTietHoaDonDAO;
import system.models.entity.ChiTietHoaDon;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAOImpl implements ChiTietHoaDonDAO {

    private static final String INSERT_SQL = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGiaBan) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE ChiTietHoaDon SET MaHoaDon = ?, MaSanPham = ?, SoLuong = ?, DonGiaBan = ? WHERE MaChiTietHoaDon = ?";
    private static final String DELETE_SQL = "DELETE FROM ChiTietHoaDon WHERE MaChiTietHoaDon = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT MaChiTietHoaDon, MaHoaDon, MaSanPham, SoLuong, DonGiaBan, ThanhTien FROM ChiTietHoaDon WHERE MaChiTietHoaDon = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaChiTietHoaDon, MaHoaDon, MaSanPham, SoLuong, DonGiaBan, ThanhTien FROM ChiTietHoaDon";
    private static final String SELECT_BY_MAHOADON_SQL = "SELECT MaChiTietHoaDon, MaHoaDon, MaSanPham, SoLuong, DonGiaBan, ThanhTien FROM ChiTietHoaDon WHERE MaHoaDon = ?";

    @Override
    public void addChiTietHoaDon(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, chiTietHoaDon.getMaHoaDon());
            stmt.setString(2, chiTietHoaDon.getMaSanPham());
            stmt.setInt(3, chiTietHoaDon.getSoLuong());
            stmt.setBigDecimal(4, chiTietHoaDon.getDonGiaBan());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateChiTietHoaDon(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, chiTietHoaDon.getMaHoaDon());
            stmt.setString(2, chiTietHoaDon.getMaSanPham());
            stmt.setInt(3, chiTietHoaDon.getSoLuong());
            stmt.setBigDecimal(4, chiTietHoaDon.getDonGiaBan());
            stmt.setInt(5, chiTietHoaDon.getMaChiTietHoaDon());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteChiTietHoaDon(Connection conn, Integer maChiTietHoaDon) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, maChiTietHoaDon);
            stmt.executeUpdate();
        }
    }

    @Override
    public ChiTietHoaDon getChiTietHoaDonById(Connection conn, Integer maChiTietHoaDon) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, maChiTietHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractChiTietHoaDonFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ChiTietHoaDon> getAllChiTietHoaDon(Connection conn) throws SQLException {
        List<ChiTietHoaDon> chiTietHoaDonList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                chiTietHoaDonList.add(extractChiTietHoaDonFromResultSet(rs));
            }
        }
        return chiTietHoaDonList;
    }
    
    @Override
    public List<ChiTietHoaDon> getChiTietHoaDonByMaHoaDon(Connection conn, Integer maHoaDon) throws SQLException {
        List<ChiTietHoaDon> chiTietHoaDonList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MAHOADON_SQL)) {
            stmt.setInt(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chiTietHoaDonList.add(extractChiTietHoaDonFromResultSet(rs));
                }
            }
        }
        return chiTietHoaDonList;
    }

    private ChiTietHoaDon extractChiTietHoaDonFromResultSet(ResultSet rs) throws SQLException {
        Integer maChiTietHoaDon = rs.getInt("MaChiTietHoaDon");
        Integer maHoaDon = rs.getInt("MaHoaDon");
        String maSanPham = rs.getString("MaSanPham");
        Integer soLuong = rs.getInt("SoLuong");
        BigDecimal donGiaBan = rs.getBigDecimal("DonGiaBan");
        BigDecimal thanhTien = rs.getBigDecimal("ThanhTien");
        return new ChiTietHoaDon(maChiTietHoaDon, maHoaDon, maSanPham, soLuong, donGiaBan, thanhTien);
    }
}