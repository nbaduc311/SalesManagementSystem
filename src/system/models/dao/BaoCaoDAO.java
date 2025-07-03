package system.models.dao;

import system.models.dto.ThongKeDoanhThu;
import system.models.dto.TopKhachHang;
import system.models.dto.TopSanPhamBanChay;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface BaoCaoDAO {

    /**
     * Lấy danh sách thống kê doanh thu theo từng ngày trong khoảng thời gian.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param tuNgay Ngày bắt đầu của khoảng thời gian.
     * @param denNgay Ngày kết thúc của khoảng thời gian.
     * @return Danh sách các đối tượng ThongKeDoanhThu, mỗi đối tượng chứa tổng doanh thu và số lượng hóa đơn cho một ngày.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    List<ThongKeDoanhThu> getDoanhThuTheoNgay(Connection conn, LocalDate tuNgay, LocalDate denNgay) throws SQLException;

    /**
     * Lấy tổng doanh thu trong một khoảng thời gian cụ thể.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param tuNgay Ngày bắt đầu của khoảng thời gian.
     * @param denNgay Ngày kết thúc của khoảng thời gian.
     * @return Tổng doanh thu trong khoảng thời gian.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    BigDecimal getTongDoanhThuTrongKhoangThoiGian(Connection conn, LocalDate tuNgay, LocalDate denNgay) throws SQLException;

    /**
     * Lấy danh sách sản phẩm bán chạy nhất (theo số lượng hoặc doanh thu).
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param limit Số lượng sản phẩm hàng đầu muốn lấy.
     * @param orderBy Chỉ định sắp xếp theo "quantity" (số lượng) hoặc "revenue" (doanh thu).
     * @param tuNgay Ngày bắt đầu (có thể null để lấy tất cả).
     * @param denNgay Ngày kết thúc (có thể null để lấy tất cả).
     * @return Danh sách các đối tượng TopSanPhamBanChay.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    List<TopSanPhamBanChay> getTopSanPhamBanChay(Connection conn, int limit, String orderBy, LocalDate tuNgay, LocalDate denNgay) throws SQLException;

    /**
     * Lấy danh sách khách hàng hàng đầu (theo tổng chi tiêu hoặc số lượng hóa đơn).
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param limit Số lượng khách hàng hàng đầu muốn lấy.
     * @param orderBy Chỉ định sắp xếp theo "spending" (tổng chi tiêu) hoặc "orders" (số lượng hóa đơn).
     * @param tuNgay Ngày bắt đầu (có thể null để lấy tất cả).
     * @param denNgay Ngày kết thúc (có thể null để lấy tất cả).
     * @return Danh sách các đối tượng TopKhachHang.
     * @throws SQLException Nếu có lỗi khi truy vấn cơ sở dữ liệu.
     */
    List<TopKhachHang> getTopKhachHang(Connection conn, int limit, String orderBy, LocalDate tuNgay, LocalDate denNgay) throws SQLException;
}