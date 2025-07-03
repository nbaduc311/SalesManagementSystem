package system.services.impl;

import system.models.dao.TaiKhoanNguoiDungDAO;
import system.models.entity.TaiKhoanNguoiDung;
import system.services.TaiKhoanNguoiDungService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TaiKhoanNguoiDungServiceImpl implements TaiKhoanNguoiDungService {

    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO;

    // Thay đổi constructor để nhận TaiKhoanNguoiDungDAO và AuthService
    public TaiKhoanNguoiDungServiceImpl(TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO) {
        this.taiKhoanNguoiDungDAO = taiKhoanNguoiDungDAO;
    }

    // Các phương thức Service giờ đây sẽ nhận Connection từ bên ngoài
    @Override
    public void addTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException {
        taiKhoanNguoiDungDAO.addTaiKhoanNguoiDung(conn, taiKhoanNguoiDung);
    }

    @Override
    public void updateTaiKhoanNguoiDung(Connection conn, TaiKhoanNguoiDung taiKhoanNguoiDung) throws SQLException {
        taiKhoanNguoiDungDAO.updateTaiKhoanNguoiDung(conn, taiKhoanNguoiDung);
    }

    @Override
    public void deleteTaiKhoanNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        taiKhoanNguoiDungDAO.deleteTaiKhoanNguoiDung(conn, maNguoiDung);
    }

    @Override
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungById(Connection conn, String maNguoiDung) throws SQLException {
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungById(conn, maNguoiDung);
    }

    @Override
    public List<TaiKhoanNguoiDung> getAllTaiKhoanNguoiDung(Connection conn) throws SQLException {
        return taiKhoanNguoiDungDAO.getAllTaiKhoanNguoiDung(conn);
    }

    @Override
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungByUsername(Connection conn, String username) throws SQLException {
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungByUsername(conn, username);
    }

    @Override
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungByEmail(Connection conn, String email) throws SQLException {
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungByEmail(conn, email);
    }

    @Override
    public List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByLoaiNguoiDung(Connection conn, String loaiNguoiDung) throws SQLException {
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungByLoaiNguoiDung(conn, loaiNguoiDung);
    }

    @Override
    public List<TaiKhoanNguoiDung> getTaiKhoanNguoiDungByTrangThai(Connection conn, String trangThai) throws SQLException {
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungByTrangThai(conn, trangThai);
    }

    @Override
    public TaiKhoanNguoiDung createTaiKhoanNguoiDung(Connection conn, String username, String password, String email, String loaiNguoiDung) throws SQLException {
        // 1. Kiểm tra trùng lặp Username
        if (isUsernameExists(conn, username)) {
            throw new SQLException("Tên đăng nhập '" + username + "' đã tồn tại.");
        }

        // 2. Kiểm tra trùng lặp Email
        if (isEmailExists(conn, email)) {
            throw new SQLException("Email '" + email + "' đã được sử dụng.");
        }

//        // 3. Mã hóa mật khẩu
//        String hashedPassword = authService.hashPassword(password); // Sử dụng AuthService để mã hóa

        // 4. Tạo đối tượng TaiKhoanNguoiDung
        TaiKhoanNguoiDung newAccount = new TaiKhoanNguoiDung(username, password, email, loaiNguoiDung);

        // 5. Thêm tài khoản vào DB
        taiKhoanNguoiDungDAO.addTaiKhoanNguoiDung(conn, newAccount);

        // 6. Lấy lại đối tượng TaiKhoanNguoiDung đầy đủ (bao gồm MaNguoiDung từ DB)
        // Do MaNguoiDung là computed column, cách an toàn nhất là truy vấn lại bằng username
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungByUsername(conn, username);
    }

    @Override
    public boolean isUsernameExists(Connection conn, String username) throws SQLException {
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungByUsername(conn, username) != null;
    }

    @Override
    public boolean isEmailExists(Connection conn, String email) throws SQLException {
        return taiKhoanNguoiDungDAO.getTaiKhoanNguoiDungByEmail(conn, email) != null;
    }
}