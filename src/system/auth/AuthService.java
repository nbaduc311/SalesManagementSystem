package system.auth; // Đã thay đổi package

import system.models.entity.TaiKhoanNguoiDung;

public interface AuthService {

    /**
     * Xác thực thông tin đăng nhập của người dùng.
     *
     * @param username Tên đăng nhập.
     * @param password Mật khẩu (chưa hash).
     * @return Đối tượng TaiKhoanNguoiDung nếu xác thực thành công, ngược lại là null.
     * @throws Exception Nếu có lỗi xảy ra trong quá trình xác thực (ví dụ: lỗi cơ sở dữ liệu).
     */
    TaiKhoanNguoiDung authenticateUser(String username, String password) throws Exception;
}