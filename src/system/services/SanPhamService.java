package system.services;

import system.models.entity.SanPham;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

public interface SanPhamService {
    // Thêm tham số Connection vào tất cả các phương thức
    void addSanPham(Connection conn, SanPham sanPham) throws SQLException;
    void updateSanPham(Connection conn, SanPham sanPham) throws SQLException;
    void deleteSanPham(Connection conn, String maSanPham) throws SQLException;
    SanPham getSanPhamById(Connection conn, String maSanPham) throws SQLException;
    SanPham getSanPhamByInternalID(Connection conn, Integer internalID) throws SQLException;
    List<SanPham> getAllSanPham(Connection conn) throws SQLException;
    List<SanPham> searchSanPhamByName(Connection conn, String name) throws SQLException;
    List<SanPham> getSanPhamByLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException;
}