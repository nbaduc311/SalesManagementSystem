package system.services;

import system.models.entity.ChiTietViTri;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

public interface ChiTietViTriService {
    // Add Connection conn parameter to all methods
    void addChiTietViTri(Connection conn, ChiTietViTri chiTietViTri) throws SQLException;
    void updateChiTietViTri(Connection conn, ChiTietViTri chiTietViTri) throws SQLException;
    void deleteChiTietViTri(Connection conn, Integer maChiTietViTri) throws SQLException;
    ChiTietViTri getChiTietViTriById(Connection conn, Integer maChiTietViTri) throws SQLException;
    List<ChiTietViTri> getAllChiTietViTri(Connection conn) throws SQLException;
    ChiTietViTri getChiTietViTriByNganDungAndSanPham(Connection conn, String maNganDung, String maSanPham) throws SQLException;
    List<ChiTietViTri> getChiTietViTriByMaNganDung(Connection conn, String maNganDung) throws SQLException;
    List<ChiTietViTri> getChiTietViTriByMaSanPham(Connection conn, String maSanPham) throws SQLException;
}