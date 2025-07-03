// system.models.dao/ViTriDungSanPhamDAO.java
package system.models.dao;

import system.models.entity.ViTriDungSanPham;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ViTriDungSanPhamDAO {
    void addViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException;
    void updateViTriDungSanPham(Connection conn, ViTriDungSanPham viTriDungSanPham) throws SQLException;
    void deleteViTriDungSanPham(Connection conn, String maNganDung) throws SQLException;
    ViTriDungSanPham getViTriDungSanPhamById(Connection conn, String maNganDung) throws SQLException;
    List<ViTriDungSanPham> getAllViTriDungSanPham(Connection conn) throws SQLException;
    ViTriDungSanPham getViTriDungSanPhamByInternalId(Connection conn, Integer internalID) throws SQLException;
    ViTriDungSanPham getViTriDungSanPhamByTenNganDung(Connection conn, String tenNganDung) throws SQLException;
}