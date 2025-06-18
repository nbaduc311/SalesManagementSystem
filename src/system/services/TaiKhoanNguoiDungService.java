package system.services; 

import system.models.dao.TaiKhoanNguoiDungDAO;
import system.models.entity.TaiKhoanNguoiDung;
import system.database.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TaiKhoanNguoiDungService {
    private static TaiKhoanNguoiDungService instance;
    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO;

    private TaiKhoanNguoiDungService() {
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAO.getIns(); // Lấy instance của DAO
    }

    public static TaiKhoanNguoiDungService getIns() {
        if (instance == null) {
            instance = new TaiKhoanNguoiDungService();
        }
        return instance;
    }

    /**
     * Nghiệp vụ: 1.1 Đăng ký tài khoản mới.
     * Kiểm tra username hoặc email đã tồn tại hay chưa trước khi thêm.
     * Mặc định LoaiNguoiDung là "Khách hàng" nếu tham số truyền vào là null hoặc rỗng.
     * Mặc định TrangThaiTaiKhoan là "Hoạt động" nếu tham số truyền vào là null hoặc rỗng.
     *
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @param email Email.
     * @param loaiNguoiDung Loại người dùng (có thể là null hoặc rỗng để mặc định là "Khách hàng").
     * @param ngayTao Ngày tạo tài khoản.
     * @param trangThaiTaiKhoan Trạng thái tài khoản (có thể là null hoặc rỗng để mặc định là "Hoạt động").
     * @return TaiKhoanNguoiDung nếu đăng ký thành công, null nếu thất bại (trùng username/email hoặc lỗi DB).
     */
    public TaiKhoanNguoiDung dangKyTaiKhoan(String username, String password, String email, String loaiNguoiDung, Date ngayTao, String trangThaiTaiKhoan) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Xử lý mặc định LoaiNguoiDung ở tầng Service
            String finalLoaiNguoiDung = loaiNguoiDung;
            if (finalLoaiNguoiDung == null || finalLoaiNguoiDung.trim().isEmpty()) {
                finalLoaiNguoiDung = "Khách hàng"; // Mặc định là "Khách hàng"
            }

            // Xử lý mặc định TrangThaiTaiKhoan ở tầng Service
            String finalTrangThaiTaiKhoan = trangThaiTaiKhoan;
            if (finalTrangThaiTaiKhoan == null || finalTrangThaiTaiKhoan.trim().isEmpty()) {
                finalTrangThaiTaiKhoan = "Hoạt động"; // Mặc định là "Hoạt động"
            }

            // Đảm bảo không có tài khoản trùng username hoặc email
            if (taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username) != null) {
                System.err.println("Lỗi: Tên đăng nhập đã tồn tại.");
                conn.rollback();
                return null;
            }
            // Giả sử có phương thức kiểm tra email trong TaiKhoanNguoiDungDAO
            // if (taiKhoanNguoiDungDAO.getTaiKhoanByEmail(conn, email) != null) {
            //     System.err.println("Lỗi: Email đã tồn tại.");
            //     conn.rollback();
            //     return null;
            // }

            TaiKhoanNguoiDung newAccount = new TaiKhoanNguoiDung(username, password, email, finalLoaiNguoiDung, ngayTao, finalTrangThaiTaiKhoan);
            TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungDAO.add(conn, newAccount); // Truyền conn vào phương thức add
            conn.commit(); // Cam kết giao dịch
            return createdAccount;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng ký tài khoản: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch đăng ký tài khoản.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit to true
                    conn.close(); // Đóng kết nối
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Nghiệp vụ: 1.2 Đăng nhập tài khoản.
     *
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @return TaiKhoanNguoiDung nếu đăng nhập thành công, null nếu thất bại (sai thông tin hoặc tài khoản không hoạt động).
     */
    public TaiKhoanNguoiDung dangNhap(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username); // Truyền conn
            if (account != null && account.getPassword().equals(password) && account.getTrangThaiTaiKhoan().equals("Hoạt động")) {
                System.out.println("Đăng nhập thành công cho tài khoản: " + username);
                return account;
            } else if (account != null && !account.getPassword().equals(password)) {
                System.err.println("Đăng nhập thất bại: Sai mật khẩu.");
            } else if (account != null && !account.getTrangThaiTaiKhoan().equals("Hoạt động")) {
                 System.err.println("Đăng nhập thất bại: Tài khoản không hoạt động hoặc bị khóa.");
            }
            else {
                System.err.println("Đăng nhập thất bại: Tên đăng nhập không tồn tại.");
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nghiệp vụ: 1.3 Bảng vai trò tài khoản / Lấy tất cả tài khoản.
     * Phương thức getAll() trong GenericDAO không nhận tham số Connection.
     *
     * @return Danh sách tất cả tài khoản người dùng.
     */
    public List<TaiKhoanNguoiDung> getAllTaiKhoan() {
        return taiKhoanNguoiDungDAO.getAll();
    }

    /**
     * Nghiệp vụ: 1.4 Quên mật khẩu.
     * Cho phép người dùng đặt lại mật khẩu mới nếu quên mật khẩu cũ.
     * Phương thức này không yêu cầu mật khẩu cũ.
     *
     * @param username Tên đăng nhập của tài khoản cần đặt lại mật khẩu.
     * @param newPassword Mật khẩu mới.
     * @return true nếu đặt lại mật khẩu thành công, false nếu thất bại (không tìm thấy tài khoản hoặc lỗi DB).
     */
    public boolean quenMatKhau(String username, String newPassword) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Tìm tài khoản bằng username
            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username); // Truyền conn
            if (account != null) {
                account.setPassword(newPassword); // Đặt mật khẩu mới
                boolean updated = taiKhoanNguoiDungDAO.update(conn, account); // Cập nhật vào DB
                conn.commit();
                return updated;
            } else {
                System.err.println("Lỗi: Không tìm thấy tài khoản với tên đăng nhập: " + username);
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi quên mật khẩu: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch quên mật khẩu.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Nghiệp vụ: 1.5 Quản lí mật khẩu (Admin reset/manage).
     * Cho phép Admin đặt lại mật khẩu hoặc thay đổi trạng thái tài khoản.
     *
     * @param maNguoiDung Mã người dùng cần quản lý.	
     * @param newPassword Mật khẩu mới (có thể là null nếu chỉ đổi trạng thái).
     * @param newTrangThaiTaiKhoan Trạng thái tài khoản mới (có thể là null nếu chỉ đổi mật khẩu).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean quanLyTaiKhoan(String maNguoiDung, String newTrangThaiTaiKhoan) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getById(conn, maNguoiDung); // Truyền conn
            if (account == null) {
                System.err.println("Lỗi: Không tìm thấy tài khoản với mã người dùng: " + maNguoiDung);
                conn.rollback();
                return false;
            }

            if (newTrangThaiTaiKhoan != null && !newTrangThaiTaiKhoan.isEmpty()) {
                account.setTrangThaiTaiKhoan(newTrangThaiTaiKhoan);
            }
            boolean updated = taiKhoanNguoiDungDAO.update(conn, account); // Truyền conn
            conn.commit();
            return updated;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi quản lý tài khoản: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch quản lý tài khoản.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Lấy tài khoản theo MaNguoiDung.
     * @param maNguoiDung Mã người dùng.
     * @return TaiKhoanNguoiDung nếu tìm thấy, null nếu không.
     */
    public TaiKhoanNguoiDung getTaiKhoanByMaNguoiDung(String maNguoiDung) {
        try (Connection conn = DatabaseConnection.getConnection()) {
             return taiKhoanNguoiDungDAO.getById(conn, maNguoiDung); // Truyền conn
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy tài khoản theo mã: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Xóa tài khoản người dùng.
     * @param maNguoiDung Mã người dùng.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean xoaTaiKhoan(String maNguoiDung) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // TODO: Cần kiểm tra các ràng buộc khóa ngoại trước khi xóa tài khoản người dùng.
            // Ví dụ: tài khoản này có phải là nhân viên, khách hàng, hay có liên quan đến hóa đơn, phiếu nhập nào không.
            // Nếu có, nên ngăn cản xóa hoặc chuyển trạng thái tài khoản sang "Inactive" thay vì xóa cứng.
            boolean deleted = taiKhoanNguoiDungDAO.delete(conn, maNguoiDung); // Truyền conn
            conn.commit();
            return deleted;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa tài khoản: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch xóa tài khoản.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }
}
