package system.models.dao.impl;

import system.models.dao.NhanVienDAO;
import system.models.entity.NhanVien;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAOImpl implements NhanVienDAO {

    private static final String INSERT_SQL = "INSERT INTO NhanVien (MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE NhanVien SET MaNguoiDung = ?, HoTen = ?, NgaySinh = ?, GioiTinh = ?, CCCD = ?, Sdt = ?, Luong = ?, TrangThaiLamViec = ? WHERE MaNhanVien = ?";
    private static final String DELETE_SQL = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
    private static final String SELECT_BY_MA_NHANVIEN_SQL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec FROM NhanVien WHERE MaNhanVien = ?";
    private static final String SELECT_ALL_SQL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec FROM NhanVien";
    private static final String SELECT_BY_MA_NGUOIDUNG_SQL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec FROM NhanVien WHERE MaNguoiDung = ?";
    private static final String SELECT_BY_CCCD_SQL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec FROM NhanVien WHERE CCCD = ?";
    private static final String SEARCH_BY_NAME_SQL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec FROM NhanVien WHERE HoTen LIKE ?";
    private static final String SELECT_BY_TRANGTHAI_SQL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec FROM NhanVien WHERE TrangThaiLamViec = ?";
    private static final String SEARCH_BY_SDT_SQL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, Sdt, Luong, TrangThaiLamViec FROM NhanVien WHERE Sdt LIKE ?"; // New SQL query

    @Override
    public void addNhanVien(Connection conn, NhanVien nhanVien) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, nhanVien.getMaNguoiDung());
            stmt.setString(2, nhanVien.getHoTen());
            stmt.setDate(3, Date.valueOf(nhanVien.getNgaySinh()));
            stmt.setString(4, nhanVien.getGioiTinh());
            stmt.setString(5, nhanVien.getCccd());
            stmt.setString(6, nhanVien.getSdt());
            stmt.setBigDecimal(7, nhanVien.getLuong());
            stmt.setString(8, nhanVien.getTrangThaiLamViec());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateNhanVien(Connection conn, NhanVien nhanVien) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, nhanVien.getMaNguoiDung());
            stmt.setString(2, nhanVien.getHoTen());
            stmt.setDate(3, Date.valueOf(nhanVien.getNgaySinh()));
            stmt.setString(4, nhanVien.getGioiTinh());
            stmt.setString(5, nhanVien.getCccd());
            stmt.setString(6, nhanVien.getSdt());
            stmt.setBigDecimal(7, nhanVien.getLuong());
            stmt.setString(8, nhanVien.getTrangThaiLamViec());
            stmt.setString(9, nhanVien.getMaNhanVien());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteNhanVien(Connection conn, String maNhanVien) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, maNhanVien);
            stmt.executeUpdate();
        }
    }

    @Override
    public NhanVien getNhanVienById(Connection conn, String maNhanVien) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MA_NHANVIEN_SQL)) {
            stmt.setString(1, maNhanVien);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNhanVienFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<NhanVien> getAllNhanVien(Connection conn) throws SQLException {
        List<NhanVien> nhanVienList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                nhanVienList.add(extractNhanVienFromResultSet(rs));
            }
        }
        return nhanVienList;
    }

    @Override
    public NhanVien getNhanVienByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MA_NGUOIDUNG_SQL)) {
            stmt.setString(1, maNguoiDung);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNhanVienFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public NhanVien getNhanVienByCCCD(Connection conn, String cccd) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_CCCD_SQL)) {
            stmt.setString(1, cccd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractNhanVienFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<NhanVien> searchNhanVienByName(Connection conn, String name) throws SQLException {
        List<NhanVien> nhanVienList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_NAME_SQL)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    nhanVienList.add(extractNhanVienFromResultSet(rs));
                }
            }
        }
        return nhanVienList;
    }

    @Override
    public List<NhanVien> getNhanVienByTrangThai(Connection conn, String trangThai) throws SQLException {
        List<NhanVien> nhanVienList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_TRANGTHAI_SQL)) {
            stmt.setString(1, trangThai);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    nhanVienList.add(extractNhanVienFromResultSet(rs));
                }
            }
        }
        return nhanVienList;
    }

    @Override
    public List<NhanVien> searchNhanVienBySdt(Connection conn, String sdt) throws SQLException {
        List<NhanVien> nhanVienList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_SDT_SQL)) {
            stmt.setString(1, "%" + sdt + "%"); // Using LIKE for partial matches
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    nhanVienList.add(extractNhanVienFromResultSet(rs));
                }
            }
        }
        return nhanVienList;
    }

    private NhanVien extractNhanVienFromResultSet(ResultSet rs) throws SQLException {
        Integer internalID = rs.getInt("InternalID");
        String maNhanVien = rs.getString("MaNhanVien");
        String maNguoiDung = rs.getString("MaNguoiDung");
        String hoTen = rs.getString("HoTen");
        LocalDate ngaySinh = rs.getDate("NgaySinh").toLocalDate();
        String gioiTinh = rs.getString("GioiTinh");
        String cccd = rs.getString("CCCD");
        String sdt = rs.getString("Sdt");
        BigDecimal luong = rs.getBigDecimal("Luong");
        String trangThaiLamViec = rs.getString("TrangThaiLamViec");

        NhanVien nhanVien = new NhanVien();
        nhanVien.setInternalID(internalID);
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setMaNguoiDung(maNguoiDung);
        nhanVien.setHoTen(hoTen);
        nhanVien.setNgaySinh(ngaySinh);
        nhanVien.setGioiTinh(gioiTinh);
        nhanVien.setCccd(cccd);
        nhanVien.setSdt(sdt);
        nhanVien.setLuong(luong);
        nhanVien.setTrangThaiLamViec(trangThaiLamViec);
        return nhanVien;
    }
}