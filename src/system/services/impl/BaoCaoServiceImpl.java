package system.services.impl;

import system.models.dao.BaoCaoDAO;
import system.services.BaoCaoService;
import system.database.DatabaseConnection; // Import DatabaseConnection
import system.models.dto.ThongKeDoanhThu;
import system.models.dto.TopKhachHang;
import system.models.dto.TopSanPhamBanChay;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaoCaoServiceImpl implements BaoCaoService {

    private static final Logger LOGGER = Logger.getLogger(BaoCaoServiceImpl.class.getName());
    private BaoCaoDAO baoCaoDAO;

    // Constructor để inject BaoCaoDAO (có thể sử dụng Spring hoặc thủ công)
    public BaoCaoServiceImpl(BaoCaoDAO baoCaoDAO) {
        this.baoCaoDAO = baoCaoDAO;
    }

    @Override
    public List<ThongKeDoanhThu> getDoanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return baoCaoDAO.getDoanhThuTheoNgay(conn, tuNgay, denNgay);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy doanh thu theo ngày từ " + tuNgay + " đến " + denNgay, e);
            return Collections.emptyList();
        }
    }

    @Override
    public BigDecimal getTongDoanhThuTrongKhoangThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return baoCaoDAO.getTongDoanhThuTrongKhoangThoiGian(conn, tuNgay, denNgay);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng doanh thu trong khoảng thời gian từ " + tuNgay + " đến " + denNgay, e);
            return BigDecimal.ZERO; // Trả về 0 nếu có lỗi
        }
    }

    @Override
    public List<TopSanPhamBanChay> getTopSanPhamBanChay(int limit, String orderBy, LocalDate tuNgay, LocalDate denNgay) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return baoCaoDAO.getTopSanPhamBanChay(conn, limit, orderBy, tuNgay, denNgay);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy top sản phẩm bán chạy (limit: " + limit + ", order by: " + orderBy + ") từ " + tuNgay + " đến " + denNgay, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TopKhachHang> getTopKhachHang(int limit, String orderBy, LocalDate tuNgay, LocalDate denNgay) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return baoCaoDAO.getTopKhachHang(conn, limit, orderBy, tuNgay, denNgay);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy top khách hàng (limit: " + limit + ", order by: " + orderBy + ") từ " + tuNgay + " đến " + denNgay, e);
            return Collections.emptyList();
        }
    }
}