// system.models.dao.impl/PhieuNhapHangDAOImpl.java
package system.models.dao.impl;

import system.models.dao.PhieuNhapHangDAO;
import system.models.entity.PhieuNhapHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Import Statement
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapHangDAOImpl implements PhieuNhapHangDAO {

    private static final String INSERT_SQL = "INSERT INTO PhieuNhapHang (NgayNhap, MaNhanVienThucHien, MaNhaCungCap) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE PhieuNhapHang SET NgayNhap = ?, MaNhanVienThucHien = ?, MaNhaCungCap = ? WHERE MaPhieuNhap = ?";
    private static final String DELETE_SQL = "DELETE FROM PhieuNhapHang WHERE MaPhieuNhap = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT MaPhieuNhap, NgayNhap, MaNhanVienThucHien, MaNhaCungCap FROM PhieuNhapHang WHERE MaPhieuNhap = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaPhieuNhap, NgayNhap, MaNhanVienThucHien, MaNhaCungCap FROM PhieuNhapHang";
    private static final String SELECT_BY_NHANVIEN_SQL = "SELECT MaPhieuNhap, NgayNhap, MaNhanVienThucHien, MaNhaCungCap FROM PhieuNhapHang WHERE MaNhanVienThucHien = ?";
    private static final String SELECT_BY_NHACUNGCAP_SQL = "SELECT MaPhieuNhap, NgayNhap, MaNhanVienThucHien, MaNhaCungCap FROM PhieuNhapHang WHERE MaNhaCungCap = ?";
    private static final String SELECT_BY_DATERANGE_SQL = "SELECT MaPhieuNhap, NgayNhap, MaNhanVienThucHien, MaNhaCungCap FROM PhieuNhapHang WHERE NgayNhap BETWEEN ? AND ?";

    @Override
    public Integer addPhieuNhapHang(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException {
        Integer generatedId = null;
        // Thêm Statement.RETURN_GENERATED_KEYS để lấy ID được sinh ra tự động
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(phieuNhapHang.getNgayNhap()));
            stmt.setString(2, phieuNhapHang.getMaNhanVienThucHien());
            stmt.setString(3, phieuNhapHang.getMaNhaCungCap());
            stmt.executeUpdate();

            // Lấy ID tự động tăng
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1); // Lấy giá trị của cột đầu tiên (ID)
                }
            }
        }
        return generatedId; // Trả về ID đã tạo
    }

    @Override
    public void updatePhieuNhapHang(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(phieuNhapHang.getNgayNhap()));
            stmt.setString(2, phieuNhapHang.getMaNhanVienThucHien());
            stmt.setString(3, phieuNhapHang.getMaNhaCungCap());
            stmt.setInt(4, phieuNhapHang.getMaPhieuNhap());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletePhieuNhapHang(Connection conn, Integer maPhieuNhap) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, maPhieuNhap);
            stmt.executeUpdate();
        }
    }

    @Override
    public PhieuNhapHang getPhieuNhapHangById(Connection conn, Integer maPhieuNhap) throws SQLException {
        PhieuNhapHang phieuNhapHang = null;
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, maPhieuNhap);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    phieuNhapHang = extractPhieuNhapHangFromResultSet(rs);
                }
            }
        }
        return phieuNhapHang;
    }

    @Override
    public List<PhieuNhapHang> getAllPhieuNhapHang(Connection conn) throws SQLException {
        List<PhieuNhapHang> phieuNhapHangList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phieuNhapHangList.add(extractPhieuNhapHangFromResultSet(rs));
                }
            }
        }
        return phieuNhapHangList;
    }

    @Override
    public List<PhieuNhapHang> getPhieuNhapHangByNhanVien(Connection conn, String maNhanVien) throws SQLException {
        List<PhieuNhapHang> phieuNhapHangList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NHANVIEN_SQL)) {
            stmt.setString(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phieuNhapHangList.add(extractPhieuNhapHangFromResultSet(rs));
                }
            }
        }
        return phieuNhapHangList;
    }

    @Override
    public List<PhieuNhapHang> getPhieuNhapHangByNhaCungCap(Connection conn, String maNhaCungCap) throws SQLException {
        List<PhieuNhapHang> phieuNhapHangList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NHACUNGCAP_SQL)) {
            stmt.setString(1, maNhaCungCap);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phieuNhapHangList.add(extractPhieuNhapHangFromResultSet(rs));
                }
            }
        }
        return phieuNhapHangList;
    }

    @Override
    public List<PhieuNhapHang> getPhieuNhapHangByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<PhieuNhapHang> phieuNhapHangList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATERANGE_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    phieuNhapHangList.add(extractPhieuNhapHangFromResultSet(rs));
                }
            }
        }
        return phieuNhapHangList;
    }

    private PhieuNhapHang extractPhieuNhapHangFromResultSet(ResultSet rs) throws SQLException {
        Integer maPhieuNhap = rs.getInt("MaPhieuNhap");
        LocalDateTime ngayNhap = rs.getTimestamp("NgayNhap").toLocalDateTime();
        String maNhanVienThucHien = rs.getString("MaNhanVienThucHien");
        String maNhaCungCap = rs.getString("MaNhaCungCap");
        return new PhieuNhapHang(maPhieuNhap, ngayNhap, maNhanVienThucHien, maNhaCungCap);
    }
}