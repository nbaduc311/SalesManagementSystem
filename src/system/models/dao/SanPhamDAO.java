// system.models.dao/SanPhamDAO.java
package system.models.dao;

import system.models.entity.SanPham;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SanPhamDAO {
    void addSanPham(Connection conn, SanPham sanPham) throws SQLException;
    void updateSanPham(Connection conn, SanPham sanPham) throws SQLException;
    void deleteSanPham(Connection conn, String maSanPham) throws SQLException;
    SanPham getSanPhamById(Connection conn, String maSanPham) throws SQLException;
    List<SanPham> getAllSanPham(Connection conn) throws SQLException;
    SanPham getSanPhamByInternalID(Connection conn, Integer internalID) throws SQLException;
    List<SanPham> searchSanPhamByName(Connection conn, String name) throws SQLException;
    List<SanPham> getSanPhamByLoaiSanPham(Connection conn, String maLoaiSanPham) throws SQLException;
}