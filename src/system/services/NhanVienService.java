// system.services/NhanVienService.java (Điều chỉnh)
package system.services;

import system.models.entity.NhanVien;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface NhanVienService {
    // Đã loại bỏ tham số username và password
    void addNhanVien(Connection conn, NhanVien nhanVien) throws SQLException; // Sửa lại kiểu trả về thành void

    boolean updateNhanVien(Connection conn, NhanVien nhanVien) throws SQLException;
    boolean deleteNhanVien(Connection conn, String maNhanVien) throws SQLException;
    NhanVien getNhanVienById(Connection conn, String maNhanVien) throws SQLException;
    List<NhanVien> getAllNhanVien(Connection conn) throws SQLException;
    NhanVien getNhanVienByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    NhanVien getNhanVienByCCCD(Connection conn, String cccd) throws SQLException;
    List<NhanVien> searchNhanVienByName(Connection conn, String name) throws SQLException;
    List<NhanVien> searchNhanVienBySdt(Connection conn, String sdt) throws SQLException; // Bổ sung phương thức này
    List<NhanVien> getNhanVienByTrangThai(Connection conn, String trangThai) throws SQLException;
}