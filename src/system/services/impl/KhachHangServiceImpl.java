package system.services.impl;

import system.models.dao.KhachHangDAO;
// import system.models.dao.TaiKhoanNguoiDungDAO; // Bỏ import này
import system.models.entity.KhachHang;
import system.models.entity.TaiKhoanNguoiDung;
import system.services.KhachHangService;
import system.services.TaiKhoanNguoiDungService; // Import service tài khoản

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangDAO khachHangDAO;
    private final TaiKhoanNguoiDungService taiKhoanNguoiDungService; // Thay đổi từ DAO sang Service

    // Sửa đổi constructor để nhận KhachHangDAO và TaiKhoanNguoiDungService
    public KhachHangServiceImpl(KhachHangDAO khachHangDAO, TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.khachHangDAO = khachHangDAO;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;
    }

    // Phương thức cũ để thêm KhachHang (nếu không có tài khoản liên kết) - giữ lại nếu cần
    @Override
    public void addKhachHang(Connection conn, KhachHang khachHang) throws SQLException {
        khachHangDAO.addKhachHang(conn, khachHang);
    }
    
    // Phương thức addKhachHang MỚI để xử lý việc liên kết tài khoản
    @Override
    public KhachHang addKhachHang(Connection conn, KhachHang khachHang, String username, String password, String email) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        KhachHang newKhachHang = null;
        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm KhachHang vào DB
            khachHangDAO.addKhachHang(conn, khachHang);

            // Lấy lại thông tin khách hàng vừa thêm để có MaKhachHang (nếu MaKhachHang được tạo tự động bởi DB)
            // Giả định SDT là UNIQUE để có thể lấy lại khách hàng vừa thêm
            // Lưu ý: getKhachHangBySdt không có trong KhachHangDAO của bạn, cần thêm vào hoặc dùng cách khác
            // Ví dụ: Nếu DAO.addKhachHang có thể trả về đối tượng có ID, thì tốt hơn.
            // Tạm thời, tôi sẽ giả định có thể lấy lại bằng SDT hoặc ID.
            // Hoặc, nếu MaKhachHang không sinh tự động, có thể dùng khachHang.getMaKhachHang()
            KhachHang addedKhachHang = khachHangDAO.getKhachHangBySdt(conn, khachHang.getSdt()); // <-- Cần phương thức này trong KhachHangDAO
            if (addedKhachHang == null) {
                conn.rollback();
                throw new SQLException("Không thể lấy thông tin khách hàng vừa thêm để liên kết tài khoản.");
            }
            newKhachHang = addedKhachHang;

            // 2. Nếu có yêu cầu liên kết tài khoản
            if (username != null && !username.trim().isEmpty()) {
                TaiKhoanNguoiDung taiKhoan = new TaiKhoanNguoiDung();
                taiKhoan.setUsername(username);
                taiKhoan.setPassword(password); // Mật khẩu NÊN được hash trước khi đến đây
                taiKhoan.setEmail(email != null && !email.isEmpty() ? email : username + "@example.com");
                taiKhoan.setLoaiNguoiDung("KHACH_HANG"); // Hoặc quyền phù hợp
                taiKhoan.setTrangThaiTaiKhoan("Hoạt động"); // Điều chỉnh dòng này: trạng thái, không phải email

                // Thêm tài khoản người dùng thông qua SERVICE
                taiKhoanNguoiDungService.addTaiKhoanNguoiDung(conn, taiKhoan);

                // Lấy lại tài khoản vừa tạo để có MaNguoiDung
                TaiKhoanNguoiDung addedTaiKhoan = taiKhoanNguoiDungService.getTaiKhoanNguoiDungByUsername(conn, username); // Lấy qua Service
                if (addedTaiKhoan == null) {
                    conn.rollback();
                    throw new SQLException("Không thể lấy thông tin tài khoản vừa tạo.");
                }

                // Cập nhật MaNguoiDung cho khách hàng vừa tạo
                newKhachHang.setMaNguoiDung(addedTaiKhoan.getMaNguoiDung());
                // Cần một phương thức trong KhachHangDAO để cập nhật MaNguoiDung cho KhachHang đã tồn tại
                // Ví dụ: updateMaNguoiDungForKhachHang
                // Phương thức này hiện chưa có trong KhachHangDAOImpl bạn đã cung cấp, cần bổ sung.
                khachHangDAO.updateMaNguoiDungForKhachHang(conn, newKhachHang.getMaKhachHang(), newKhachHang.getMaNguoiDung());
            }

            conn.commit(); // Commit transaction
            return newKhachHang;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm khách hàng và/hoặc liên kết tài khoản: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    System.err.println("Lỗi rollback transaction: " + ex.getMessage());
                }
            }
            throw e; // Ném lại ngoại lệ để Controller xử lý
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(originalAutoCommit); // Khôi phục auto-commit
                } catch (SQLException ex) {
                    System.err.println("Lỗi khôi phục auto-commit: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public void updateKhachHang(Connection conn, KhachHang khachHang) throws SQLException {
        khachHangDAO.updateKhachHang(conn, khachHang);
    }

    @Override
    public void deleteKhachHang(Connection conn, String maKhachHang) throws SQLException {
        khachHangDAO.deleteKhachHang(conn, maKhachHang);
    }

    @Override
    public KhachHang getKhachHangById(Connection conn, String maKhachHang) throws SQLException {
        return khachHangDAO.getKhachHangById(conn, maKhachHang);
    }

    @Override
    public List<KhachHang> getAllKhachHang(Connection conn) throws SQLException {
        return khachHangDAO.getAllKhachHang(conn);
    }

    @Override
    public KhachHang getKhachHangByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        return khachHangDAO.getKhachHangByMaNguoiDung(conn, maNguoiDung);
    }

    @Override
    public List<KhachHang> searchKhachHangByName(Connection conn, String name) throws SQLException {
        return khachHangDAO.searchKhachHangByName(conn, name);
    }
    @Override
    public List<KhachHang> searchKhachHangBySdt(Connection conn, String sdt) throws SQLException {
        return khachHangDAO.searchKhachHangBySdt(conn, sdt);
    }
}