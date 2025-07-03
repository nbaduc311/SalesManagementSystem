package system.controllers;

import system.view.panels.LichSuMuaHangView;
import system.services.HoaDonService;
import system.services.ChiTietHoaDonService;
import system.services.SanPhamService;
import system.models.entity.HoaDon;
import system.models.entity.ChiTietHoaDon;
import system.models.entity.SanPham;
import system.database.DatabaseConnection; // Import DatabaseConnection

import javax.swing.JOptionPane;
import java.sql.Connection; // Import Connection
import java.sql.SQLException;
import java.math.BigDecimal; // Import BigDecimal
import java.util.List;

public class LichSuMuaHangController {

    private LichSuMuaHangView view;
    private HoaDonService hoaDonService;
    private ChiTietHoaDonService chiTietHoaDonService;
    private SanPhamService sanPhamService;
    private String maKhachHangHienTai; // Mã khách hàng hiện tại của người dùng đăng nhập

    public LichSuMuaHangController(LichSuMuaHangView view, HoaDonService hoaDonService, ChiTietHoaDonService chiTietHoaDonService, SanPhamService sanPhamService, String maKhachHangHienTai) {
        this.view = view;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.sanPhamService = sanPhamService;
        this.maKhachHangHienTai = maKhachHangHienTai;

        initListeners();
        loadLichSuMuaHang(); // Tải dữ liệu ngay khi khởi tạo
    }

    private void initListeners() {
        view.addChiTietButtonListener(e -> xemChiTietHoaDon());
    }

    // Phương thức để tải và hiển thị lịch sử mua hàng
    public void loadLichSuMuaHang() {
        try (Connection conn = DatabaseConnection.getConnection()) { // Sử dụng try-with-resources để tự động đóng Connection
            List<HoaDon> hoaDonList = hoaDonService.getHoaDonByMaKhachHang(conn, maKhachHangHienTai);
            view.displayHoaDonList(hoaDonList);
        } catch (SQLException e) {
            view.showMessage("Lỗi kết nối cơ sở dữ liệu khi tải lịch sử mua hàng: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            view.showMessage("Lỗi khi tải lịch sử mua hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Phương thức xử lý khi người dùng nhấn nút "Xem Chi Tiết Hóa Đơn"
    private void xemChiTietHoaDon() {
        String maHoaDonStr = view.getSelectedHoaDonMa();
        if (maHoaDonStr == null) {
            view.showMessage("Vui lòng chọn một hóa đơn để xem chi tiết.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer maHoaDon;
        try {
            maHoaDon = Integer.parseInt(maHoaDonStr);
        } catch (NumberFormatException e) {
            view.showMessage("Mã hóa đơn không hợp lệ. Vui lòng thử lại.", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) { // Sử dụng try-with-resources
            // Lấy chi tiết hóa đơn từ service
            List<ChiTietHoaDon> chiTietList = chiTietHoaDonService.getChiTietHoaDonByMaHoaDon(conn, maHoaDon);

            if (chiTietList.isEmpty()) {
                view.showMessage("Không tìm thấy chi tiết cho hóa đơn " + maHoaDon, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder detailMessage = new StringBuilder();
            detailMessage.append("Chi tiết Hóa đơn ").append(maHoaDon).append(":\n\n");
            BigDecimal tongTienChiTiet = BigDecimal.ZERO; // Sử dụng BigDecimal cho tổng tiền

            for (ChiTietHoaDon cthd : chiTietList) {
                // Lấy thông tin sản phẩm
                SanPham sp = sanPhamService.getSanPhamById(conn, cthd.getMaSanPham());
                String tenSanPham = (sp != null) ? sp.getTenSanPham() : "Không xác định";
                
                // Sử dụng getThanhTien() đã được tính toán sẵn từ Entity
                // Hoặc tính lại: BigDecimal thanhTien = cthd.getDonGiaBan().multiply(BigDecimal.valueOf(cthd.getSoLuong()));
                BigDecimal thanhTien = cthd.getThanhTien() != null ? cthd.getThanhTien() : cthd.getDonGiaBan().multiply(BigDecimal.valueOf(cthd.getSoLuong()));
                tongTienChiTiet = tongTienChiTiet.add(thanhTien);

                detailMessage.append("- Sản phẩm: ").append(tenSanPham)
                             .append(" (Mã: ").append(cthd.getMaSanPham()).append(")\n")
                             .append("  Số lượng: ").append(cthd.getSoLuong())
                             .append(", Đơn giá: ").append(String.format("%,.0f VNĐ", cthd.getDonGiaBan()))
                             .append(", Thành tiền: ").append(String.format("%,.0f VNĐ", thanhTien))
                             .append("\n");
            }
            detailMessage.append("\n-----------------------------------\n");
            detailMessage.append("Tổng cộng: ").append(String.format("%,.0f VNĐ", tongTienChiTiet));


            view.showMessage(detailMessage.toString(), "Chi Tiết Hóa Đơn " + maHoaDon, JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            view.showMessage("Lỗi kết nối cơ sở dữ liệu khi tải chi tiết hóa đơn: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            view.showMessage("Lỗi khi tải chi tiết hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}