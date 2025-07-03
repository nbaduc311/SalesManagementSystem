// system.models.dao.impl/SaoLuuDAOImpl.java
package system.models.dao.impl;

import system.models.dao.SaoLuuDAO;
import system.models.entity.SaoLuu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaoLuuDAOImpl implements SaoLuuDAO {

    private static final String INSERT_SQL = "INSERT INTO SaoLuu (NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE SaoLuu SET NgaySaoLuu = ?, LoaiSaoLuu = ?, MaNguoiDungThucHien = ?, ViTriLuuTru = ? WHERE MaSaoLuu = ?";
    private static final String DELETE_SQL = "DELETE FROM SaoLuu WHERE MaSaoLuu = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT MaSaoLuu, NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru FROM SaoLuu WHERE MaSaoLuu = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaSaoLuu, NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru FROM SaoLuu";
    private static final String SELECT_BY_MANGUOIDUNG_SQL = "SELECT MaSaoLuu, NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru FROM SaoLuu WHERE MaNguoiDungThucHien = ?";
    private static final String SELECT_BY_LOAISAOLUU_SQL = "SELECT MaSaoLuu, NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru FROM SaoLuu WHERE LoaiSaoLuu = ?";
    private static final String SELECT_BY_DATERANGE_SQL = "SELECT MaSaoLuu, NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru FROM SaoLuu WHERE NgaySaoLuu BETWEEN ? AND ?";

    @Override
    public void addSaoLuu(Connection conn, SaoLuu saoLuu) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(saoLuu.getNgaySaoLuu()));
            stmt.setString(2, saoLuu.getLoaiSaoLuu());
            stmt.setString(3, saoLuu.getMaNguoiDungThucHien());
            stmt.setString(4, saoLuu.getViTriLuuTru());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateSaoLuu(Connection conn, SaoLuu saoLuu) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(saoLuu.getNgaySaoLuu()));
            stmt.setString(2, saoLuu.getLoaiSaoLuu());
            stmt.setString(3, saoLuu.getMaNguoiDungThucHien());
            stmt.setString(4, saoLuu.getViTriLuuTru());
            stmt.setInt(5, saoLuu.getMaSaoLuu());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteSaoLuu(Connection conn, Integer maSaoLuu) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, maSaoLuu);
            stmt.executeUpdate();
        }
    }

    @Override
    public SaoLuu getSaoLuuById(Connection conn, Integer maSaoLuu) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, maSaoLuu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractSaoLuuFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<SaoLuu> getAllSaoLuu(Connection conn) throws SQLException {
        List<SaoLuu> saoLuuList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                saoLuuList.add(extractSaoLuuFromResultSet(rs));
            }
        }
        return saoLuuList;
    }
    
    @Override
    public List<SaoLuu> getSaoLuuByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        List<SaoLuu> saoLuuList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MANGUOIDUNG_SQL)) {
            stmt.setString(1, maNguoiDung);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saoLuuList.add(extractSaoLuuFromResultSet(rs));
                }
            }
        }
        return saoLuuList;
    }

    @Override
    public List<SaoLuu> getSaoLuuByLoaiSaoLuu(Connection conn, String loaiSaoLuu) throws SQLException {
        List<SaoLuu> saoLuuList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_LOAISAOLUU_SQL)) {
            stmt.setString(1, loaiSaoLuu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saoLuuList.add(extractSaoLuuFromResultSet(rs));
                }
            }
        }
        return saoLuuList;
    }

    @Override
    public List<SaoLuu> getSaoLuuByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<SaoLuu> saoLuuList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATERANGE_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saoLuuList.add(extractSaoLuuFromResultSet(rs));
                }
            }
        }
        return saoLuuList;
    }

    private SaoLuu extractSaoLuuFromResultSet(ResultSet rs) throws SQLException {
        Integer maSaoLuu = rs.getInt("MaSaoLuu");
        LocalDateTime ngaySaoLuu = rs.getTimestamp("NgaySaoLuu").toLocalDateTime();
        String loaiSaoLuu = rs.getString("LoaiSaoLuu");
        String maNguoiDungThucHien = rs.getString("MaNguoiDungThucHien");
        String viTriLuuTru = rs.getString("ViTriLuuTru");
        return new SaoLuu(maSaoLuu, ngaySaoLuu, loaiSaoLuu, maNguoiDungThucHien, viTriLuuTru);
    }
}