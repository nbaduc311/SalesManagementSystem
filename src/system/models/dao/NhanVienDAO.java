package system.models.dao;

import system.models.entity.NhanVien;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface NhanVienDAO {
    void addNhanVien(Connection conn, NhanVien nhanVien) throws SQLException;
    void updateNhanVien(Connection conn, NhanVien nhanVien) throws SQLException;
    void deleteNhanVien(Connection conn, String maNhanVien) throws SQLException;
    NhanVien getNhanVienById(Connection conn, String maNhanVien) throws SQLException;
    List<NhanVien> getAllNhanVien(Connection conn) throws SQLException;
    NhanVien getNhanVienByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    NhanVien getNhanVienByCCCD(Connection conn, String cccd) throws SQLException;
    List<NhanVien> searchNhanVienByName(Connection conn, String name) throws SQLException;
    List<NhanVien> getNhanVienByTrangThai(Connection conn, String trangThai) throws SQLException;

    // Thêm phương thức tìm kiếm nhân viên theo số điện thoại
    List<NhanVien> searchNhanVienBySdt(Connection conn, String sdt) throws SQLException;
}