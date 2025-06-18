package system.services;

import system.models.dao.*;
import system.models.entity.*;
import system.database.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class KhachHangService {
    private static KhachHangService instance;
    private KhachHangDAO khachHangDAO;
    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO;
    private HoaDonDAO hoaDonDAO;

    private KhachHangService() {
        this.khachHangDAO = KhachHangDAO.getIns(); // Sử dụng Singleton instance
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAO.getIns();
        this.hoaDonDAO = HoaDonDAO.getIns(); // Sử dụng Singleton instance
    }

    public static KhachHangService getIns() {
        if (instance == null) {
            instance = new KhachHangService();
        }
        return instance;
    }

    /**
     * Nghiệp vụ: 3.1 Thêm thông tin khách hàng mới.
     * Có thể tạo tài khoản người dùng liên kết nếu khách hàng muốn đăng ký tài khoản.
     * Nếu khách hàng không có tài khoản, việc kiểm tra trùng lặp chỉ dựa trên số điện thoại (SDT).
     *
     * @param khachHang Đối tượng KhachHang chứa thông tin. Cần có HoTen, SDT.
     * @param username Tài khoản đăng nhập (có thể null hoặc rỗng nếu không tạo tài khoản liên kết).
     * @param password Mật khẩu tài khoản (chỉ cần thiết nếu username không null).
     * @param emailForAccount Email cho tài khoản người dùng (chỉ cần thiết nếu username không null).
     * @return KhachHang nếu thêm thành công, null nếu thất bại.
     */
    public KhachHang themKhachHang(KhachHang khachHang, String username, String password, String emailForAccount) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Kiểm tra trùng SDT trước khi thêm khách hàng (luôn thực hiện)
            if (khachHangDAO.getBySdt(conn, khachHang.getSdt()) != null) {
                System.err.println("Lỗi: Số điện thoại khách hàng đã tồn tại.");
                conn.rollback();
                return null;
            }

            String maNguoiDung = null;
            // Chỉ xử lý tạo tài khoản người dùng nếu username được cung cấp
            if (username != null && !username.trim().isEmpty()) {
                // Kiểm tra trùng username hoặc email tài khoản người dùng
                if (taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username) != null) {
                    System.err.println("Lỗi: Tên đăng nhập tài khoản đã tồn tại.");
                    conn.rollback();
                    return null;
                }
                // EmailForAccount là bắt buộc nếu tạo tài khoản
                if (emailForAccount == null || emailForAccount.trim().isEmpty()) {
                    System.err.println("Lỗi: Email cho tài khoản người dùng không được để trống khi tạo tài khoản liên kết.");
                    conn.rollback();
                    return null;
                }
                // 1. Tạo tài khoản người dùng cho khách hàng
                // Đảm bảo LoaiNguoiDung là "Khách hàng" và TrangThaiTaiKhoan là "Hoạt động"
                TaiKhoanNguoiDung newAccount = new TaiKhoanNguoiDung(username, password, emailForAccount, "Khách hàng", new Date(), "Hoạt động");
                TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungDAO.add(conn, newAccount);
                if (createdAccount == null) {
                    throw new SQLException("Không thể tạo tài khoản người dùng cho khách hàng.");
                }
                maNguoiDung = createdAccount.getMaNguoiDung();
                khachHang.setMaNguoiDung(maNguoiDung); // Gán MaNguoiDung cho khách hàng
            } else {
                // Nếu không tạo tài khoản, đảm bảo MaNguoiDung của KhachHang là null
                khachHang.setMaNguoiDung(null);
            }

            // 2. Thêm thông tin khách hàng
            KhachHang newKhachHang = khachHangDAO.add(conn, khachHang);
            if (newKhachHang == null) {
                throw new SQLException("Không thể thêm thông tin khách hàng.");
            }

            conn.commit(); // Cam kết giao dịch
            return newKhachHang;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm khách hàng: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch thêm khách hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            return null;
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
     * Nghiệp vụ: 3.2 Cập nhật thông tin khách hàng.
     * Cập nhật thông tin cá nhân của khách hàng.
     *
     * @param khachHang Đối tượng KhachHang chứa thông tin cập nhật (MaKhachHang phải được set).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean capNhatThongTinKhachHang(KhachHang khachHang) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Kiểm tra khách hàng có tồn tại không
            if (khachHangDAO.getById(conn, khachHang.getMaKhachHang()) == null) {
                System.err.println("Lỗi: Không tìm thấy khách hàng với mã: " + khachHang.getMaKhachHang());
                conn.rollback();
                return false;
            }
            // Kiểm tra trùng SDT nếu SDT thay đổi và không phải của chính khách hàng này
            KhachHang existingBySdt = khachHangDAO.getBySdt(conn, khachHang.getSdt());
            if (existingBySdt != null && !existingBySdt.getMaKhachHang().equals(khachHang.getMaKhachHang())) {
                System.err.println("Lỗi: Số điện thoại mới đã được sử dụng bởi khách hàng khác.");
                conn.rollback();
                return false;
            }

            boolean updated = khachHangDAO.update(conn, khachHang);
            conn.commit(); // Cam kết giao dịch
            return updated;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật thông tin khách hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch cập nhật khách hàng.");
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
     * Nghiệp vụ: 3.3 Tra cứu khách hàng / 3.4 Xem thông tin chi tiết khách hàng.
     * Lấy tất cả khách hàng.
     *
     * @return Danh sách tất cả khách hàng.
     */
    public List<KhachHang> getAllKhachHang() {
        return khachHangDAO.getAll(); // DAO này tự quản lý kết nối cho getAll()
    }

    /**
     * Nghiệp vụ: 3.4 Xem thông tin chi tiết khách hàng.
     * Lấy khách hàng theo mã.
     *
     * @param maKhachHang Mã khách hàng.
     * @return KhachHang nếu tìm thấy, null nếu không.
     */
    public KhachHang getKhachHangById(String maKhachHang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return khachHangDAO.getById(conn, maKhachHang);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nghiệp vụ: Tra cứu khách hàng.
     * Lấy khách hàng theo số điện thoại.
     *
     * @param sdt Số điện thoại.
     * @return KhachHang nếu tìm thấy, null nếu không.
     */
    public KhachHang getKhachHangBySdt(String sdt) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return khachHangDAO.getBySdt(conn, sdt); // Sử dụng phương thức mới getBySdt
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy khách hàng theo SĐT: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nghiệp vụ: 3.5 Xem lịch sử mua hàng của khách hàng.
     *
     * @param maKhachHang Mã khách hàng.
     * @return Danh sách các hóa đơn của khách hàng.
     */
    public List<HoaDon> getLichSuMuaHang(String maKhachHang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Giả định HoaDonDAO có phương thức findByMaKhachHang để tối ưu
            // Nếu không, cần thêm vào HoaDonDAO.
            return hoaDonDAO.getHoaDonByMaKhachHang(conn, maKhachHang);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch sử mua hàng: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public List<HoaDon> getLichSuMuaHangBySdt(String maKhachHang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Nếu không, cần thêm vào HoaDonDAO.
            return hoaDonDAO.getHoaDonBySdt(conn, maKhachHang);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch sử mua hàng: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
}

