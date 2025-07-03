package system.controllers;

import system.view.panels.ThongTinTaiKhoanView;
import system.services.KhachHangService;
import system.services.NhanVienService;
import system.services.TaiKhoanNguoiDungService;
import system.auth.AuthService;
import system.models.entity.KhachHang;
import system.models.entity.NhanVien;
import system.models.entity.TaiKhoanNguoiDung;
import system.common.LoaiNguoiDung;
import system.database.DatabaseConnection;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class ThongTinTaiKhoanController {

    private ThongTinTaiKhoanView view;
    private KhachHangService khachHangService;
    private NhanVienService nhanVienService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService;
    private AuthService authService;
    private String maNguoiDungHienTai;
    private String loaiNguoiDungHienTai;

    public ThongTinTaiKhoanController(
            ThongTinTaiKhoanView view,
            KhachHangService khachHangService,
            NhanVienService nhanVienService,
            TaiKhoanNguoiDungService taiKhoanNguoiDungService,
            AuthService authService,
            String maNguoiDungHienTai,
            String loaiNguoiDungHienTai) {

        this.view = view;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;
        this.authService = authService;
        this.maNguoiDungHienTai = maNguoiDungHienTai;
        this.loaiNguoiDungHienTai = loaiNguoiDungHienTai;

        initListeners();
        loadThongTinTaiKhoan();
    }

    private void initListeners() {
        view.addCapNhatThongTinListener(e -> capNhatThongTin());
        view.addDoiMatKhauListener(e -> doiMatKhau());
    }

    public void loadThongTinTaiKhoan() {
        TaiKhoanNguoiDung tk = null;
        KhachHang kh = null;
        NhanVien nv = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            tk = taiKhoanNguoiDungService.getTaiKhoanNguoiDungById(conn, maNguoiDungHienTai);
            if (tk == null) {
                view.showMessage("Không tìm thấy tài khoản người dùng với mã: " + maNguoiDungHienTai, "Lỗi", JOptionPane.ERROR_MESSAGE);
                view.clearForm();
                return;
            }

            if (LoaiNguoiDung.KHACH_HANG.equalsIgnoreCase(loaiNguoiDungHienTai)) {
                kh = khachHangService.getKhachHangByMaNguoiDung(conn, maNguoiDungHienTai);
                if (kh == null) {
                    view.showMessage("Không tìm thấy thông tin khách hàng cho tài khoản này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } else if (LoaiNguoiDung.NHAN_VIEN.equalsIgnoreCase(loaiNguoiDungHienTai) || LoaiNguoiDung.ADMIN.equalsIgnoreCase(loaiNguoiDungHienTai)) {
                nv = nhanVienService.getNhanVienByMaNguoiDung(conn, maNguoiDungHienTai);
                if (nv == null) {
                    view.showMessage("Không tìm thấy thông tin nhân viên cho tài khoản này.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            view.showMessage("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            view.showMessage("Lỗi khi tải thông tin tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        view.displayThongTin(kh, nv, tk);
    }

    // PHƯƠNG THỨC capNhatThongTin ĐƯỢC SỬA ĐỔI
    private void capNhatThongTin() {
        String hoTen = view.getHoTen();
        String sdt = view.getSdt();
        LocalDate ngaySinh = view.getNgaySinh();
        String gioiTinh = view.getGioiTinh();
        String email = view.getEmail();

        if (hoTen.isEmpty() || sdt.isEmpty() || ngaySinh == null || gioiTinh == null || email.isEmpty()) {
            view.showMessage("Vui lòng điền đầy đủ và đúng định dạng các thông tin cá nhân (Ngày sinh: YYYY-MM-DD).", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!sdt.matches("\\d+")) {
            view.showMessage("Số điện thoại chỉ được chứa ký tự số.", "Lỗi Dữ Liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            view.showMessage("Email không đúng định dạng.", "Lỗi Dữ Liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null; // Khai báo Connection bên ngoài try để có thể sử dụng trong catch
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Cập nhật thông tin trong TaiKhoanNguoiDung (Email)
            TaiKhoanNguoiDung tk = taiKhoanNguoiDungService.getTaiKhoanNguoiDungById(conn, maNguoiDungHienTai);
            if (tk != null) {
                tk.setEmail(email);
                // GỌI PHƯƠNG THỨC VOID. Nếu có lỗi, nó sẽ ném SQLException.
                taiKhoanNguoiDungService.updateTaiKhoanNguoiDung(conn, tk);
            } else {
                throw new SQLException("Không tìm thấy tài khoản để cập nhật.");
            }

            // 2. Cập nhật thông tin trong KhachHang hoặc NhanVien
            if (LoaiNguoiDung.KHACH_HANG.equalsIgnoreCase(loaiNguoiDungHienTai)) {
                KhachHang kh = khachHangService.getKhachHangByMaNguoiDung(conn, maNguoiDungHienTai);
                if (kh != null) {
                    kh.setHoTen(hoTen);
                    kh.setSdt(sdt);
                    kh.setNgaySinh(ngaySinh);
                    kh.setGioiTinh(gioiTinh);
                    // GỌI PHƯƠNG THỨC VOID. Nếu có lỗi, nó sẽ ném SQLException.
                    khachHangService.updateKhachHang(conn, kh);
                } else {
                    throw new SQLException("Không tìm thấy thông tin khách hàng để cập nhật.");
                }
            } else if (LoaiNguoiDung.NHAN_VIEN.equalsIgnoreCase(loaiNguoiDungHienTai) || LoaiNguoiDung.ADMIN.equalsIgnoreCase(loaiNguoiDungHienTai)) {
                NhanVien nv = nhanVienService.getNhanVienByMaNguoiDung(conn, maNguoiDungHienTai);
                if (nv != null) {
                    nv.setHoTen(hoTen);
                    nv.setSdt(sdt);
                    nv.setNgaySinh(ngaySinh);
                    nv.setGioiTinh(gioiTinh);
                    // GỌI PHƯƠNG THỨC VOID. Nếu có lỗi, nó sẽ ném SQLException.
                    nhanVienService.updateNhanVien(conn, nv);
                } else {
                    throw new SQLException("Không tìm thấy thông tin nhân viên để cập nhật.");
                }
            }
            // Nếu code chạy đến đây mà không có exception nào được ném ra
            // thì coi như cả hai phần cập nhật đều thành công.
            conn.commit(); // Commit transaction
            view.showMessage("Cập nhật thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadThongTinTaiKhoan(); // Tải lại thông tin sau khi cập nhật

        } catch (SQLException e) {
            // Nếu có lỗi SQL xảy ra ở bất kỳ đâu trong khối try, thì catch này sẽ bắt nó.
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            view.showMessage("Lỗi kết nối hoặc thao tác DB khi cập nhật thông tin: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            // Bắt các lỗi chung khác
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            view.showMessage("Lỗi khi cập nhật thông tin: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Đảm bảo đóng kết nối trong khối finally
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối CSDL trong finally: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void doiMatKhau() {
        String oldPass = view.getOldPassword();
        String newPass = view.getNewPassword();
        String confirmPass = view.getConfirmPassword();
        String username = view.getUsername();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            view.showMessage("Vui lòng điền đầy đủ các trường mật khẩu.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            view.showMessage("Mật khẩu mới và xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newPass.length() < 6) {
            view.showMessage("Mật khẩu mới phải có ít nhất 6 ký tự.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null; // Khai báo Connection bên ngoài try để có thể sử dụng trong catch
        try {
            conn = DatabaseConnection.getConnection();
            boolean success = authService.changePassword(conn, username, oldPass, newPass);

            if (success) {
                view.showMessage("Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                view.resetPasswordFields();
            } else {
                view.showMessage("Đổi mật khẩu thất bại. Mật khẩu cũ không đúng hoặc có lỗi xảy ra.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            view.showMessage("Lỗi kết nối cơ sở dữ liệu khi đổi mật khẩu: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            view.showMessage("Lỗi khi đổi mật khẩu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối CSDL trong finally: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}