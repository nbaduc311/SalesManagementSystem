// system.models.dao.impl/PhucHoiDAOImpl.java
package system.models.dao.impl;

import system.models.dao.PhucHoiDAO;
import system.models.entity.PhucHoi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhucHoiDAOImpl implements PhucHoiDAO {

    private static final String INSERT_SQL = "INSERT INTO PhucHoi (NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE PhucHoi SET NgayPhucHoi = ?, LoaiPhucHoi = ?, MaNguoiDungThucHien = ?, MaSaoLuu = ? WHERE MaPhucHoi = ?";
    private static final String DELETE_SQL = "DELETE FROM PhucHoi WHERE MaPhucHoi = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT MaPhucHoi, NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu FROM PhucHoi WHERE MaPhucHoi = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaPhucHoi, NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu FROM PhucHoi";
    private static final String SELECT_BY_MANGUOIDUNG_SQL = "SELECT MaPhucHoi, NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu FROM PhucHoi WHERE MaNguoiDungThucHien = ?";
    private static final String SELECT_BY_MASAOLUU_SQL = "SELECT MaPhucHoi, NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu FROM PhucHoi WHERE MaSaoLuu = ?";
    private static final String SELECT_BY_DATERANGE_SQL = "SELECT MaPhucHoi, NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu FROM PhucHoi WHERE NgayPhucHoi BETWEEN ? AND ?";

    @Override
    public void addPhucHoi(Connection conn, PhucHoi phucHoi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(phucHoi.getNgayPhucHoi()));
            stmt.setString(2, phucHoi.getLoaiPhucHoi());
            stmt.setString(3, phucHoi.getMaNguoiDungThucHien());
            stmt.setInt(4, phucHoi.getMaSaoLuu());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updatePhucHoi(Connection conn, PhucHoi phucHoi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(phucHoi.getNgayPhucHoi()));
            stmt.setString(2, phucHoi.getLoaiPhucHoi());
            stmt.setString(3, phucHoi.getMaNguoiDungThucHien());
            stmt.setInt(4, phucHoi.getMaSaoLuu());
            stmt.setInt(5, phucHoi.getMaPhucHoi());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletePhucHoi(Connection conn, Integer maPhucHoi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, maPhucHoi);
            stmt.executeUpdate();
        }
    }

    @Override
    public PhucHoi getPhucHoiById(Connection conn, Integer maPhucHoi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, maPhucHoi);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPhucHoiFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<PhucHoi> getAllPhucHoi(Connection conn) throws SQLException {
        List<PhucHoi> phucHoiList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                phucHoiList.add(extractPhucHoiFromResultSet(rs));
            }
        }
        return phucHoiList;
    }
    
    @Override
    public List<PhucHoi> getPhucHoiByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        List<PhucHoi> phucHoiList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MANGUOIDUNG_SQL)) {
            stmt.setString(1, maNguoiDung);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phucHoiList.add(extractPhucHoiFromResultSet(rs));
                }
            }
        }
        return phucHoiList;
    }

    @Override
    public List<PhucHoi> getPhucHoiByMaSaoLuu(Connection conn, Integer maSaoLuu) throws SQLException {
        List<PhucHoi> phucHoiList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MASAOLUU_SQL)) {
            stmt.setInt(1, maSaoLuu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phucHoiList.add(extractPhucHoiFromResultSet(rs));
                }
            }
        }
        return phucHoiList;
    }

    @Override
    public List<PhucHoi> getPhucHoiByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<PhucHoi> phucHoiList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATERANGE_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phucHoiList.add(extractPhucHoiFromResultSet(rs));
                }
            }
        }
        return phucHoiList;
    }

    private PhucHoi extractPhucHoiFromResultSet(ResultSet rs) throws SQLException {
        Integer maPhucHoi = rs.getInt("MaPhucHoi");
        LocalDateTime ngayPhucHoi = rs.getTimestamp("NgayPhucHoi").toLocalDateTime();
        String loaiPhucHoi = rs.getString("LoaiPhucHoi");
        String maNguoiDungThucHien = rs.getString("MaNguoiDungThucHien");
        Integer maSaoLuu = rs.getInt("MaSaoLuu");
        return new PhucHoi(maPhucHoi, ngayPhucHoi, loaiPhucHoi, maNguoiDungThucHien, maSaoLuu);
    }
}