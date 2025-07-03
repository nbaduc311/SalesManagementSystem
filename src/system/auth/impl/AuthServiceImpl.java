package system.auth.impl;

import system.auth.AuthService;
import system.models.entity.TaiKhoanNguoiDung;
import system.services.TaiKhoanNguoiDungService;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime; // Cần để thiết lập thời gian tạo/cập nhật

public class AuthServiceImpl implements AuthService {

    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;

    public AuthServiceImpl(TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;
    }

    @Override
    public TaiKhoanNguoiDung login(Connection conn, String username, String password) throws SQLException {
        // Lấy thông tin tài khoản người dùng dựa trên username
        // Trong thực tế: Lấy mật khẩu đã hash từ DB và so sánh với hash của 'password' đầu vào
        TaiKhoanNguoiDung taiKhoan = taiKhoanNguoiDungService.getTaiKhoanNguoiDungByUsername(conn, username);

        // Kiểm tra xem tài khoản có tồn tại và mật khẩu có khớp không
        // Trong thực tế: So sánh hash của password đầu vào với hash lưu trong DB
        if (taiKhoan != null && taiKhoan.getPassword().equals(password)) {
            // Đăng nhập thành công
            return taiKhoan;
        }
        // Đăng nhập thất bại
        return null;
    }

    @Override
    public boolean isAdmin(Connection conn, TaiKhoanNguoiDung taiKhoan) throws SQLException {
        if (taiKhoan != null && "Admin".equalsIgnoreCase(taiKhoan.getLoaiNguoiDung())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNhanVien(Connection conn, TaiKhoanNguoiDung taiKhoan) throws SQLException {
        if (taiKhoan != null && "NhanVien".equalsIgnoreCase(taiKhoan.getLoaiNguoiDung())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isKhachHang(Connection conn, TaiKhoanNguoiDung taiKhoan) throws SQLException {
        if (taiKhoan != null && "KhachHang".equalsIgnoreCase(taiKhoan.getLoaiNguoiDung())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean registerUser(Connection conn, String username, String password, String email, String userType) throws SQLException {
        // 1. Kiểm tra xem tên đăng nhập đã tồn tại chưa
        if (taiKhoanNguoiDungService.getTaiKhoanNguoiDungByUsername(conn, username) != null) {
            System.err.println("Lỗi đăng ký: Tên đăng nhập '" + username + "' đã tồn tại.");
            return false; // Trả về false nếu tên đăng nhập đã được sử dụng
        }

        // 2. Tạo một đối tượng TaiKhoanNguoiDung mới
        TaiKhoanNguoiDung newTaiKhoan = new TaiKhoanNguoiDung();
        // Giả sử MaNguoiDung được tạo tự động bởi database (ví dụ: IDENTITY column hoặc trigger)
        // hoặc logic tạo mã nằm trong lớp DAO khi thêm mới.
        // Nếu cần tạo ở đây, bạn sẽ phải có một phương thức tiện ích để tạo ID duy nhất.
        newTaiKhoan.setUsername(username);
        newTaiKhoan.setPassword(password); 
        newTaiKhoan.setEmail(email); // Hoặc yêu cầu email khi đăng ký nếu cần
        newTaiKhoan.setLoaiNguoiDung(userType);
        newTaiKhoan.setNgayTao(LocalDateTime.now());
        newTaiKhoan.setTrangThaiTaiKhoan("Hoạt động"); // Mặc định là 'Hoạt động'

        // 3. Thêm tài khoản vào cơ sở dữ liệu thông qua TaiKhoanNguoiDungService
        try {
            taiKhoanNguoiDungService.addTaiKhoanNguoiDung(conn, newTaiKhoan);
            return true; // Đăng ký thành công
        } catch (SQLException e) {
            System.err.println("Lỗi cơ sở dữ liệu khi đăng ký người dùng: " + e.getMessage());
            throw e; // Ném lại ngoại lệ để lớp gọi xử lý transaction (rollback)
        }
    }

    @Override
    public boolean changePassword(Connection conn, String username, String oldPassword, String newPassword) throws SQLException {
        // 1. Tìm tài khoản người dùng theo tên đăng nhập
        TaiKhoanNguoiDung taiKhoan = taiKhoanNguoiDungService.getTaiKhoanNguoiDungByUsername(conn, username);

        // 2. Kiểm tra xem tài khoản có tồn tại không
        if (taiKhoan == null) {
            System.err.println("Lỗi đổi mật khẩu: Người dùng '" + username + "' không tồn tại.");
            return false;
        }

        // 3. Kiểm tra mật khẩu cũ có khớp không
        // Trong thực tế: So sánh hash của oldPassword đầu vào với hash lưu trong DB
        if (!taiKhoan.getPassword().equals(oldPassword)) {
            System.err.println("Lỗi đổi mật khẩu: Mật khẩu cũ không đúng cho người dùng '" + username + "'.");
            return false;
        }

        // 4. Cập nhật mật khẩu mới và ngày cập nhật
        // !!! LƯU Ý QUAN TRỌNG: TRONG THỰC TẾ, HÃY HASH MẬT KHẨU MỚI TRƯỚC KHI LƯU !!!
        taiKhoan.setPassword(newPassword);
        // NgayCapNhat không có trong entity TaiKhoanNguoiDung của bạn,
        // nếu muốn theo dõi, bạn cần thêm thuộc tính ngayCapNhat vào entity và DB.
        // Ví dụ nếu có: taiKhoan.setNgayCapNhat(LocalDateTime.now());

        // 5. Cập nhật tài khoản vào cơ sở dữ liệu thông qua TaiKhoanNguoiDungService
        try {
            taiKhoanNguoiDungService.updateTaiKhoanNguoiDung(conn, taiKhoan);
            return true; // Đổi mật khẩu thành công
        } catch (SQLException e) {
            System.err.println("Lỗi cơ sở dữ liệu khi đổi mật khẩu cho người dùng '" + username + "': " + e.getMessage());
            throw e; // Ném lại ngoại lệ để lớp gọi xử lý transaction (rollback)
        }
    }
    @Override
    public boolean resetPasswordViaEmail(Connection conn, String email, String newPassword) throws SQLException {
        // 1. Tìm tài khoản người dùng theo email
        TaiKhoanNguoiDung taiKhoan = taiKhoanNguoiDungService.getTaiKhoanNguoiDungByEmail(conn, email);

        // 2. Kiểm tra xem email có tồn tại không
        if (taiKhoan == null) {
            System.err.println("Lỗi đặt lại mật khẩu: Email '" + email + "' không tồn tại trong hệ thống.");
            return false;
        }

        // 3. Cập nhật mật khẩu mới cho tài khoản tìm được
        // !!! LƯU Ý QUAN TRỌNG: TRONG THỰC TẾ, HÃY HASH MẬT KHẨU MỚI TRƯỚC KHI LƯU !!!
        taiKhoan.setPassword(newPassword);
        // Nếu có trường ngayCapNhat trong TaiKhoanNguoiDung entity và DB, hãy cập nhật nó
        // taiKhoan.setNgayCapNhat(LocalDateTime.now());

        // 4. Cập nhật tài khoản vào cơ sở dữ liệu
        try {
            taiKhoanNguoiDungService.updateTaiKhoanNguoiDung(conn, taiKhoan);
            System.out.println("Mật khẩu cho người dùng với email '" + email + "' đã được đặt lại thành công.");
            // Trong ứng dụng thực tế, bạn sẽ gửi email thông báo hoặc liên kết đặt lại ở đây
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi cơ sở dữ liệu khi đặt lại mật khẩu cho email '" + email + "': " + e.getMessage());
            throw e; // Ném lại ngoại lệ để lớp gọi xử lý transaction
        }
    }
}