// system.models.dao/KhachHangDAO.java
package system.models.dao;

import system.models.entity.KhachHang;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface KhachHangDAO {
    void addKhachHang(Connection conn, KhachHang khachHang) throws SQLException;
    void updateKhachHang(Connection conn, KhachHang khachHang) throws SQLException;
    void deleteKhachHang(Connection conn, String maKhachHang) throws SQLException;
    KhachHang getKhachHangById(Connection conn, String maKhachHang) throws SQLException;
    List<KhachHang> getAllKhachHang(Connection conn) throws SQLException;
    KhachHang getKhachHangByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    List<KhachHang> searchKhachHangByName(Connection conn, String name) throws SQLException;
    List<KhachHang> searchKhachHangBySdt(Connection conn, String sdt) throws SQLException;
    
    // Thêm phương thức mới để cập nhật MaNguoiDung cho KhachHang
    void updateMaNguoiDungForKhachHang(Connection conn, String maKhachHang, String maNguoiDung) throws SQLException;

    // Thêm phương thức để lấy KhachHang theo số điện thoại (để lấy MaKhachHang được tạo tự động)
    KhachHang getKhachHangBySdt(Connection conn, String sdt) throws SQLException;
}