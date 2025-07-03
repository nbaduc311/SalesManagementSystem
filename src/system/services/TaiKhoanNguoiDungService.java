package system.services;

import system.models.entity.TaiKhoanNguoiDung;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TaiKhoanNguoiDungService {
    // Phương thức thêm tài khoản ban đầu
    void addTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException;
    void updateTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException;
    void deleteTaiKhoanNguoiDung(Connection conn, String maNguoiDung) throws SQLException;
    TaiKhoanNguoiDung getTaiKhoanNguoiDungById(Connection conn, String maNguoiDung) throws SQLException;
    List<TaiKhoanNguoiDung> getAllTaiKhoanNguoiDung(Connection conn) throws SQLException;
    TaiKhoanNguoiDung getTaiKhoanNguoiDungByUsername(Connection conn, String username) throws SQLException;
    TaiKhoanNguoiDung getTaiKhoanNguoiDungByEmail(Connection conn, String email) throws SQLException;
    List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByLoaiNguoiDung(Connection conn, String loaiNguoiDung) throws SQLException;
    List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByTrangThai(Connection conn, String trangThai) throws SQLException;

    // Phương thức mới để tạo tài khoản người dùng hoàn chỉnh và trả về đối tượng đã được tạo
    // (bao gồm MaNguoiDung được sinh ra bởi DB)
    TaiKhoanNguoiDung createTaiKhoanNguoiDung(Connection conn, String username, String password, String email, String loaiNguoiDung) throws SQLException;

    // Phương thức hỗ trợ kiểm tra username/email đã tồn tại
    boolean isUsernameExists(Connection conn, String username) throws SQLException;
    boolean isEmailExists(Connection conn, String email) throws SQLException;
}