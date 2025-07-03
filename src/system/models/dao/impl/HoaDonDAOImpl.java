// system.models.dao.impl/HoaDonDAOImpl.java
package system.models.dao.impl;

import system.models.dao.HoaDonDAO;
import system.models.entity.HoaDon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Thêm import này
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAOImpl implements HoaDonDAO {

    private static final String INSERT_SQL = "INSERT INTO HoaDon (MaKhachHang, TenKhachHangVangLai, SdtKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE HoaDon SET MaKhachHang = ?, TenKhachHangVangLai = ?, SdtKhachHangVangLai = ?, MaNhanVienLap = ?, NgayBan = ?, PhuongThucThanhToan = ? WHERE MaHoaDon = ?";
    private static final String DELETE_SQL = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SdtKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon WHERE MaHoaDon = ?";
    private static final String SELECT_ALL_SQL = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SdtKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon";
    private static final String SELECT_BY_MANHANVIENLAP_SQL = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SdtKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon WHERE MaNhanVienLap = ?";
    private static final String SELECT_BY_MAKHACHHANG_SQL = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SdtKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon WHERE MaKhachHang = ?";
    private static final String SELECT_BY_DATERANGE_SQL = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SdtKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon WHERE NgayBan BETWEEN ? AND ?";

    @Override
    public HoaDon addHoaDon(Connection conn, HoaDon hoaDon) throws SQLException { // Thay đổi kiểu trả về
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) { // Thêm Statement.RETURN_GENERATED_KEYS
            stmt.setString(1, hoaDon.getMaKhachHang());
            stmt.setString(2, hoaDon.getTenKhachHangVangLai());
            stmt.setString(3, hoaDon.getSdtKhachHangVangLai());
            stmt.setString(4, hoaDon.getMaNhanVienLap());
            stmt.setTimestamp(5, Timestamp.valueOf(hoaDon.getNgayBan()));
            stmt.setString(6, hoaDon.getPhuongThucThanhToan());
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Thêm hóa đơn thất bại, không có hàng nào bị ảnh hưởng.");
            }

            // Lấy MaHoaDon được sinh ra
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hoaDon.setMaHoaDon(generatedKeys.getInt(1)); // Lấy ID vừa được tạo (giả sử là cột đầu tiên và kiểu Integer)
                } else {
                    throw new SQLException("Thêm hóa đơn thất bại, không lấy được mã hóa đơn mới.");
                }
            }
            return hoaDon; // Trả về đối tượng HoaDon đã có MaHoaDon
        }
    }

    @Override
    public void updateHoaDon(Connection conn, HoaDon hoaDon) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, hoaDon.getMaKhachHang());
            stmt.setString(2, hoaDon.getTenKhachHangVangLai());
            stmt.setString(3, hoaDon.getSdtKhachHangVangLai());
            stmt.setString(4, hoaDon.getMaNhanVienLap());
            stmt.setTimestamp(5, Timestamp.valueOf(hoaDon.getNgayBan()));
            stmt.setString(6, hoaDon.getPhuongThucThanhToan());
            stmt.setInt(7, hoaDon.getMaHoaDon());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteHoaDon(Connection conn, Integer maHoaDon) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, maHoaDon);
            stmt.executeUpdate();
        }
    }

    @Override
    public HoaDon getHoaDonById(Connection conn, Integer maHoaDon) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, maHoaDon);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractHoaDonFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<HoaDon> getAllHoaDon(Connection conn) throws SQLException {
        List<HoaDon> hoaDonList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                hoaDonList.add(extractHoaDonFromResultSet(rs));
            }
        }
        return hoaDonList;
    }
    
    @Override
    public List<HoaDon> getHoaDonByMaNhanVienLap(Connection conn, String maNhanVienLap) throws SQLException {
        List<HoaDon> hoaDonList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MANHANVIENLAP_SQL)) {
            stmt.setString(1, maNhanVienLap);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hoaDonList.add(extractHoaDonFromResultSet(rs));
                }
            }
        }
        return hoaDonList;
    }

    @Override
    public List<HoaDon> getHoaDonByMaKhachHang(Connection conn, String maKhachHang) throws SQLException {
        List<HoaDon> hoaDonList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_MAKHACHHANG_SQL)) {
            stmt.setString(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hoaDonList.add(extractHoaDonFromResultSet(rs));
                }
            }
        }
        return hoaDonList;
    }

    @Override
    public List<HoaDon> getHoaDonByDateRange(Connection conn, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<HoaDon> hoaDonList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_DATERANGE_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hoaDonList.add(extractHoaDonFromResultSet(rs));
                }
            }
        }
        return hoaDonList;
    }

    private HoaDon extractHoaDonFromResultSet(ResultSet rs) throws SQLException {
        Integer maHoaDon = rs.getInt("MaHoaDon");
        String maKhachHang = rs.getString("MaKhachHang");
        String tenKhachHangVangLai = rs.getString("TenKhachHangVangLai");
        String sdtKhachHangVangLai = rs.getString("SdtKhachHangVangLai");
        String maNhanVienLap = rs.getString("MaNhanVienLap");
        LocalDateTime ngayBan = rs.getTimestamp("NgayBan").toLocalDateTime();
        String phuongThucThanhToan = rs.getString("PhuongThucThanhToan");
        return new HoaDon(maHoaDon, maKhachHang, tenKhachHangVangLai, sdtKhachHangVangLai, maNhanVienLap, ngayBan, phuongThucThanhToan);
    }
}