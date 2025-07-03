// system.models.dao.impl/KhachHangDAOImpl.java
package system.models.dao.impl;

import system.models.dao.KhachHangDAO;
import system.models.entity.KhachHang;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAOImpl implements KhachHangDAO {

    // Note: InternalID and MaKhachHang are handled by the database for insertion and retrieval
    private static final String INSERT_SQL = "INSERT INTO KhachHang (MaNguoiDung, HoTen, NgaySinh, GioiTinh, Sdt) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE KhachHang SET MaNguoiDung = ?, HoTen = ?, NgaySinh = ?, GioiTinh = ?, Sdt = ? WHERE MaKhachHang = ?";
    private static final String DELETE_SQL = "DELETE FROM KhachHang WHERE MaKhachHang = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, Sdt FROM KhachHang WHERE MaKhachHang = ?";
    private static final String SELECT_ALL_SQL = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, Sdt FROM KhachHang";
    private static final String SELECT_BY_MANGUOIDUNG_SQL = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, Sdt FROM KhachHang WHERE MaNguoiDung = ?";
    private static final String SEARCH_BY_NAME_SQL = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, Sdt FROM KhachHang WHERE HoTen LIKE ?";
    private static final String SELECT_BY_SDT_SQL = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, Sdt FROM KhachHang WHERE Sdt = ?";
    // Thêm SQL cho tìm kiếm theo SĐT (sử dụng LIKE cho tìm kiếm gần đúng)
    private static final String SEARCH_BY_SDT_SQL = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, Sdt FROM KhachHang WHERE Sdt LIKE ?";
    private static final String UPDATE_MANGUOIDUNG_SQL = "UPDATE KhachHang SET MaNguoiDung = ? WHERE MaKhachHang = ?";


    @Override
    public void addKhachHang(Connection conn, KhachHang khachHang) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, khachHang.getMaNguoiDung());
            stmt.setString(2, khachHang.getHoTen());
            if (khachHang.getNgaySinh() != null) {
                stmt.setDate(3, Date.valueOf(khachHang.getNgaySinh()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            stmt.setString(4, khachHang.getGioiTinh());
            stmt.setString(5, khachHang.getSdt());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateKhachHang(Connection conn, KhachHang khachHang) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, khachHang.getMaNguoiDung());
            stmt.setString(2, khachHang.getHoTen());
            if (khachHang.getNgaySinh() != null) {
                stmt.setDate(3, Date.valueOf(khachHang.getNgaySinh()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            stmt.setString(4, khachHang.getGioiTinh());
            stmt.setString(5, khachHang.getSdt());
            stmt.setString(6, khachHang.getMaKhachHang());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteKhachHang(Connection conn, String maKhachHang) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, maKhachHang);
            stmt.executeUpdate();
        }
    }

    @Override
    public KhachHang getKhachHangById(Connection conn, String maKhachHang) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setString(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractKhachHangFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<KhachHang> getAllKhachHang(Connection conn) throws SQLException {
        List<KhachHang> khachHangList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                khachHangList.add(extractKhachHangFromResultSet(rs));
            }
        }
        return khachHangList;
    }
    
    @Override
    public KhachHang getKhachHangByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MANGUOIDUNG_SQL)) {
            stmt.setString(1, maNguoiDung);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractKhachHangFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<KhachHang> searchKhachHangByName(Connection conn, String name) throws SQLException {
        List<KhachHang> khachHangList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_NAME_SQL)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    khachHangList.add(extractKhachHangFromResultSet(rs));
                }
            }
        }
        return khachHangList;
    }

    @Override
    public void updateMaNguoiDungForKhachHang(Connection conn, String maKhachHang, String maNguoiDung) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_MANGUOIDUNG_SQL)) {
            stmt.setString(1, maNguoiDung);
            stmt.setString(2, maKhachHang);
            stmt.executeUpdate();
        }
    }

    @Override
    public KhachHang getKhachHangBySdt(Connection conn, String sdt) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_SDT_SQL)) {
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractKhachHangFromResultSet(rs);
                }
            }
        }
        return null;
    }

    // Phương thức MỚI: searchKhachHangBySdt
    @Override
    public List<KhachHang> searchKhachHangBySdt(Connection conn, String sdt) throws SQLException {
        List<KhachHang> khachHangList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_SDT_SQL)) {
            stmt.setString(1, "%" + sdt + "%"); // Sử dụng LIKE để tìm kiếm gần đúng
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    khachHangList.add(extractKhachHangFromResultSet(rs));
                }
            }
        }
        return khachHangList;
    }

    private KhachHang extractKhachHangFromResultSet(ResultSet rs) throws SQLException {
        Integer internalID = rs.getInt("InternalID");
        String maKhachHang = rs.getString("MaKhachHang");
        String maNguoiDung = rs.getString("MaNguoiDung");
        String hoTen = rs.getString("HoTen");
        
        LocalDate ngaySinh = null;
        Date sqlDate = rs.getDate("NgaySinh");
        if (sqlDate != null) {
            ngaySinh = sqlDate.toLocalDate();
        }
        
        String gioiTinh = rs.getString("GioiTinh");
        String sdt = rs.getString("Sdt");
        return new KhachHang(internalID, maKhachHang, maNguoiDung, hoTen, ngaySinh, gioiTinh, sdt);
    }
}