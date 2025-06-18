package system.auth; // Giữ nguyên package

import system.models.entity.TaiKhoanNguoiDung;

public class AuthSession {
    private static TaiKhoanNguoiDung currentUser; // Biến static để giữ thông tin người dùng hiện tại

    private AuthSession() {
        // Private constructor để ngăn tạo instance từ bên ngoài (Singleton pattern)
    }

    /**
     * Thiết lập người dùng hiện tại sau khi đăng nhập thành công.
     * @param user Đối tượng TaiKhoanNguoiDung của người dùng đã đăng nhập.
     */
    public static void setCurrentUser(TaiKhoanNguoiDung user) {
        currentUser = user;
    }

    /**
     * Lấy thông tin người dùng hiện tại.
     * @return Đối tượng TaiKhoanNguoiDung của người dùng đang đăng nhập, hoặc null nếu không có ai đăng nhập.
     */
    public static TaiKhoanNguoiDung getCurrentUser() {
        return currentUser;
    }

    /**
     * Kiểm tra xem có người dùng nào đang đăng nhập không.
     * @return true nếu có người dùng đăng nhập, false nếu không.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Đăng xuất người dùng hiện tại.
     */
    public static void logout() {
        currentUser = null;
        System.out.println("Người dùng đã đăng xuất.");
    }

    /**
     * Lấy loại người dùng hiện tại.
     * @return Loại người dùng (Admin, Nhân viên, Khách hàng) hoặc null nếu không đăng nhập.
     */
    public static String getCurrentUserRole() {
        if (currentUser != null) {
            return currentUser.getLoaiNguoiDung();
        }
        return null;
    }
}