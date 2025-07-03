package system.services;

import system.models.entity.ViTriDungSanPham;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

public interface ViTriDungSanPhamService {
    // Thêm tham số Connection vào tất cả các phương thức
    void addViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException;
    void updateViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException;
    void deleteViTriDungSanPham(Connection conn, String maNganDung) throws SQLException;
    ViTriDungSanPham getViTriDungSanPhamById(Connection conn, String maNganDung) throws SQLException;
    ViTriDungSanPham getViTriDungSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException;
    List<ViTriDungSanPham> getAllViTriDungSanPham(Connection conn) throws SQLException;
    ViTriDungSanPham getViTriDungSanPhamByTenNganDung(Connection conn, String tenNganDung) throws SQLException;
}