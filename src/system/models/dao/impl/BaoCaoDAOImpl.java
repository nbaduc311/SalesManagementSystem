package system.models.dao.impl;

import system.models.dao.BaoCaoDAO;
import system.models.dto.ThongKeDoanhThu;
import system.models.dto.TopKhachHang;
import system.models.dto.TopSanPhamBanChay;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BaoCaoDAOImpl implements BaoCaoDAO {

    @Override
    public List<ThongKeDoanhThu> getDoanhThuTheoNgay(Connection conn, LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        List<ThongKeDoanhThu> danhSachThongKe = new ArrayList<>();
        String sql = "SELECT " +
                     "CAST(hd.NgayBan AS DATE) AS Ngay, " +
                     "SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongDoanhThu, " +
                     "COUNT(DISTINCT hd.MaHoaDon) AS SoLuongHoaDon " +
                     "FROM HoaDon hd " +
                     "JOIN ChiTietHoaDon cthd ON hd.MaHoaDon = cthd.MaHoaDon " +
                     "WHERE CAST(hd.NgayBan AS DATE) BETWEEN ? AND ? " +
                     "GROUP BY CAST(hd.NgayBan AS DATE) " +
                     "ORDER BY Ngay";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ThongKeDoanhThu thongKe = new ThongKeDoanhThu();
                    thongKe.setNgay(rs.getDate("Ngay").toLocalDate());
                    thongKe.setTongDoanhThu(rs.getBigDecimal("TongDoanhThu"));
                    thongKe.setSoLuongHoaDon(rs.getLong("SoLuongHoaDon"));
                    danhSachThongKe.add(thongKe);
                }
            }
        }
        return danhSachThongKe;
    }

    @Override
    public BigDecimal getTongDoanhThuTrongKhoangThoiGian(Connection conn, LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        String sql = "SELECT SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongDoanhThu " +
                     "FROM HoaDon hd " +
                     "JOIN ChiTietHoaDon cthd ON hd.MaHoaDon = cthd.MaHoaDon " +
                     "WHERE CAST(hd.NgayBan AS DATE) BETWEEN ? AND ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tongDoanhThu = rs.getBigDecimal("TongDoanhThu");
                    if (tongDoanhThu == null) { // Handle case where no sales in period
                        tongDoanhThu = BigDecimal.ZERO;
                    }
                }
            }
        }
        return tongDoanhThu;
    }

    @Override
    public List<TopSanPhamBanChay> getTopSanPhamBanChay(Connection conn, int limit, String orderBy, LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        List<TopSanPhamBanChay> danhSachSanPham = new ArrayList<>();
        String sql = "SELECT " +
                     "sp.MaSanPham, sp.TenSanPham, " +
                     "SUM(cthd.SoLuong) AS TongSoLuongBan, " +
                     "SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongDoanhThuTuSP " +
                     "FROM SanPham sp " +
                     "JOIN ChiTietHoaDon cthd ON sp.MaSanPham = cthd.MaSanPham " +
                     "JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon ";

        StringBuilder whereClause = new StringBuilder();
        if (tuNgay != null && denNgay != null) {
            whereClause.append("WHERE CAST(hd.NgayBan AS DATE) BETWEEN ? AND ? ");
        }

        sql += whereClause.toString() +
               "GROUP BY sp.MaSanPham, sp.TenSanPham " +
               "ORDER BY ";

        if ("quantity".equalsIgnoreCase(orderBy)) {
            sql += "TongSoLuongBan DESC ";
        } else if ("revenue".equalsIgnoreCase(orderBy)) {
            sql += "TongDoanhThuTuSP DESC ";
        } else {
            sql += "TongDoanhThuTuSP DESC "; // Default to revenue
        }
        sql += "LIMIT ?"; // Use LIMIT for MySQL/PostgreSQL, TOP for SQL Server

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (tuNgay != null && denNgay != null) {
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay));
            }
            stmt.setInt(paramIndex, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TopSanPhamBanChay sp = new TopSanPhamBanChay();
                    sp.setMaSanPham(rs.getString("MaSanPham"));
                    sp.setTenSanPham(rs.getString("TenSanPham"));
                    sp.setTongSoLuongBan(rs.getLong("TongSoLuongBan"));
                    sp.setTongDoanhThuTuSP(rs.getBigDecimal("TongDoanhThuTuSP"));
                    danhSachSanPham.add(sp);
                }
            }
        }
        return danhSachSanPham;
    }

    @Override
    public List<TopKhachHang> getTopKhachHang(Connection conn, int limit, String orderBy, LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        List<TopKhachHang> danhSachKhachHang = new ArrayList<>();
        // Query để lấy thông tin khách hàng thân thiết (có MaKhachHang)
        String sqlRegistered = "SELECT " +
                               "kh.MaKhachHang AS MaKH, " +
                               "kh.HoTen AS TenKH, " +
                               "SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongChiTieu, " +
                               "COUNT(DISTINCT hd.MaHoaDon) AS SoLuongHoaDonDaMua " +
                               "FROM KhachHang kh " +
                               "JOIN HoaDon hd ON kh.MaKhachHang = hd.MaKhachHang " +
                               "JOIN ChiTietHoaDon cthd ON hd.MaHoaDon = cthd.MaHoaDon ";

        // Query để lấy thông tin khách hàng vãng lai (TenKhachHangVangLai)
        String sqlWalkin = "SELECT " +
                           "'VANG_LAI_' || hd.SdtKhachHangVangLai AS MaKH, " + // Tạo mã giả cho khách vãng lai
                           "hd.TenKhachHangVangLai AS TenKH, " +
                           "SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongChiTieu, " +
                           "COUNT(DISTINCT hd.MaHoaDon) AS SoLuongHoaDonDaMua " +
                           "FROM HoaDon hd " +
                           "JOIN ChiTietHoaDon cthd ON hd.MaHoaDon = cthd.MaHoaDon " +
                           "WHERE hd.MaKhachHang IS NULL AND hd.TenKhachHangVangLai IS NOT NULL ";

        StringBuilder whereClause = new StringBuilder();
        if (tuNgay != null && denNgay != null) {
            whereClause.append("WHERE CAST(hd.NgayBan AS DATE) BETWEEN ? AND ? ");
        }

        // Kết hợp cả hai loại khách hàng
        String combinedSql = "(" + sqlRegistered + whereClause.toString() + " GROUP BY kh.MaKhachHang, kh.HoTen) " +
                             "UNION ALL " +
                             "(" + sqlWalkin + (tuNgay != null && denNgay != null ? "AND CAST(hd.NgayBan AS DATE) BETWEEN ? AND ? " : "") + " GROUP BY hd.TenKhachHangVangLai, hd.SdtKhachHangVangLai) " +
                             "ORDER BY ";

        if ("spending".equalsIgnoreCase(orderBy)) {
            combinedSql += "TongChiTieu DESC ";
        } else if ("orders".equalsIgnoreCase(orderBy)) {
            combinedSql += "SoLuongHoaDonDaMua DESC ";
        } else {
            combinedSql += "TongChiTieu DESC "; // Default to spending
        }
        combinedSql += "LIMIT ?"; // Use LIMIT for MySQL/PostgreSQL, TOP for SQL Server

        try (PreparedStatement stmt = conn.prepareStatement(combinedSql)) {
            int paramIndex = 1;
            if (tuNgay != null && denNgay != null) {
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay));
                // For the walk-in part of UNION ALL
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
                stmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay));
            }
            stmt.setInt(paramIndex, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TopKhachHang kh = new TopKhachHang();
                    kh.setMaKhachHang(rs.getString("MaKH"));
                    kh.setTenKhachHang(rs.getString("TenKH"));
                    kh.setTongChiTieu(rs.getBigDecimal("TongChiTieu"));
                    kh.setSoLuongHoaDonDaMua(rs.getLong("SoLuongHoaDonDaMua"));
                    danhSachKhachHang.add(kh);
                }
            }
        }
        return danhSachKhachHang;
    }
}