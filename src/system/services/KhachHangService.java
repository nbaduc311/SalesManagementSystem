package system.services;

import system.models.entity.KhachHang;
import system.models.entity.TaiKhoanNguoiDung; // Import TaiKhoanNguoiDung nếu cần cho interface này
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.util.List;

public interface KhachHangService {
    // Thêm phương thức mới để xử lý việc thêm KhachHang cùng với Tài Khoản Người Dùng (nếu có)
    KhachHang addKhachHang(Connection conn, KhachHang khachHang, String username, String password, String email) throws SQLException;
    
    // Phương thức cũ để thêm KhachHang (nếu không có tài khoản liên kết)
    void addKhachHang(Connection conn, KhachHang khachHang) throws SQLException;

    void updateKhachHang(Connection conn, KhachHang khachHang) throws SQLException;
    void deleteKhachHang(Connection conn, String maKhachHang) throws SQLException;
    KhachHang getKhachHangById(Connection conn, String maKhachHang) throws SQLException;
    List<KhachHang> getAllKhachHang(Connection conn) throws SQLException;
    KhachHang getKhachHangByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    List<KhachHang> searchKhachHangByName(Connection conn, String name) throws SQLException;
    
    // Phương thức mới để tìm kiếm khách hàng bằng số điện thoại
    List<KhachHang> searchKhachHangBySdt(Connection conn, String sdt) throws SQLException; // Dòng này đã được thêm
}