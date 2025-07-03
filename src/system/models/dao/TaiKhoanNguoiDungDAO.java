// system.models.dao/TaiKhoanNguoiDungDAO.java
package system.models.dao;

import system.models.entity.TaiKhoanNguoiDung;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TaiKhoanNguoiDungDAO {
    void addTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException;
    void updateTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException;
    void deleteTaiKhoanNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    TaiKhoanNguoiDung getTaiKhoanNguoiDungById(Connection conn, String maNguoiDung) throws SQLException;
    List<TaiKhoanNguoiDung> getAllTaiKhoanNguoiDung(Connection conn) throws SQLException;
    TaiKhoanNguoiDung getTaiKhoanNguoiDungByUsername(Connection conn, String username) throws SQLException;
    TaiKhoanNguoiDung getTaiKhoanNguoiDungByEmail(Connection conn, String email) throws SQLException;
    List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByLoaiNguoiDung(Connection conn, String loaiNguoiDung) throws SQLException;
    List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByTrangThai(Connection conn, String trangThai) throws SQLException;
    // Thêm phương thức mới này
    TaiKhoanNguoiDung getTaiKhoanNguoiDungByInternalID(Connection conn, int internalID) throws SQLException;
}