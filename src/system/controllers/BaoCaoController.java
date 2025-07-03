package system.controllers;

import system.services.BaoCaoService;
import system.view.panels.BaoCaoView;
import system.models.dto.ThongKeDoanhThu;
import system.models.dto.TopKhachHang;
import system.models.dto.TopSanPhamBanChay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BaoCaoController {

    private BaoCaoView view;
    private BaoCaoService service;

    public BaoCaoController(BaoCaoView view, BaoCaoService service) {
        this.view = view;
        this.service = service;
        addListeners();
    }

    private void addListeners() {
        view.addGenerateReportButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
    }

    private void generateReport() {
        String reportType = view.getSelectedReportType();
        String yearInput = view.getYearInput();
        int year = 0;

        // Validate year input for relevant report types
        if (reportType.equals(BaoCaoView.REPORT_TYPE_MONTHLY_REVENUE) ||
            reportType.equals(BaoCaoView.REPORT_TYPE_YEARLY_REVENUE) ||
            reportType.equals(BaoCaoView.REPORT_TYPE_MONTHLY_IMPORT_QUANTITY)) {
            try {
                year = Integer.parseInt(yearInput);
                if (year <= 0) {
                    view.displayMessage("Năm không hợp lệ. Vui lòng nhập số nguyên dương.", true);
                    return;
                }
            } catch (NumberFormatException ex) {
                view.displayMessage("Vui lòng nhập năm hợp lệ (ví dụ: 2023).", true);
                return;
            }
        }

        view.displayMessage("Đang tạo báo cáo...", false);

        switch (reportType) {
            case BaoCaoView.REPORT_TYPE_MONTHLY_REVENUE:
                generateMonthlyRevenueReport(year);
                break;
            case BaoCaoView.REPORT_TYPE_YEARLY_REVENUE:
                generateYearlyRevenueReport(year); // Doanh thu theo năm có thể tổng hợp từ doanh thu theo ngày
                break;
            case BaoCaoView.REPORT_TYPE_TOP_SELLING_PRODUCTS:
                generateTopSellingProductsReport();
                break;
            case BaoCaoView.REPORT_TYPE_PRODUCT_STOCK_LEVELS:
                generateProductStockLevelsReport(); // Báo cáo tồn kho
                break;
            case BaoCaoView.REPORT_TYPE_MONTHLY_IMPORT_QUANTITY:
                generateMonthlyImportQuantityReport(year); // Số lượng nhập theo tháng
                break;
            default:
                view.displayMessage("Loại báo cáo không được hỗ trợ.", true);
                break;
        }
    }

    private void generateMonthlyRevenueReport(int year) {
        try {
            String[] columnNames = {"Tháng", "Tổng Doanh Thu", "Số Lượng Hóa Đơn"};
            Vector<Vector<Object>> data = new Vector<>();

            for (int month = 1; month <= 12; month++) {
                LocalDate startDate = LocalDate.of(year, month, 1);
                LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();

                // Call service to get daily revenue for the month and aggregate
                List<ThongKeDoanhThu> dailyStats = service.getDoanhThuTheoNgay(startDate, endDate);

                BigDecimal totalMonthlyRevenue = BigDecimal.ZERO;
                long totalMonthlyOrders = 0;

                for (ThongKeDoanhThu stat : dailyStats) {
                    totalMonthlyRevenue = totalMonthlyRevenue.add(stat.getTongDoanhThu());
                    totalMonthlyOrders += stat.getSoLuongHoaDon();
                }

                Vector<Object> row = new Vector<>();
                row.add("Tháng " + month);
                row.add(view.formatCurrency(totalMonthlyRevenue.doubleValue()));
                row.add(view.formatNumber((int) totalMonthlyOrders));
                data.add(row);
            }
            view.updateReportTable(columnNames, data);
            view.displayMessage("Đã tạo báo cáo doanh thu theo tháng cho năm " + year, false);
        } catch (Exception e) {
            view.displayMessage("Lỗi khi tạo báo cáo doanh thu theo tháng: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void generateYearlyRevenueReport(int year) {
        try {
            String[] columnNames = {"Năm", "Tổng Doanh Thu", "Số Lượng Hóa Đơn"};
            Vector<Vector<Object>> data = new Vector<>();

            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);

            // Get all daily revenue for the year and aggregate
            List<ThongKeDoanhThu> dailyStats = service.getDoanhThuTheoNgay(startDate, endDate);

            BigDecimal totalYearlyRevenue = BigDecimal.ZERO;
            long totalYearlyOrders = 0;

            for (ThongKeDoanhThu stat : dailyStats) {
                totalYearlyRevenue = totalYearlyRevenue.add(stat.getTongDoanhThu());
                totalYearlyOrders += stat.getSoLuongHoaDon();
            }

            Vector<Object> row = new Vector<>();
            row.add(String.valueOf(year));
            row.add(view.formatCurrency(totalYearlyRevenue.doubleValue()));
            row.add(view.formatNumber((int) totalYearlyOrders));
            data.add(row);

            view.updateReportTable(columnNames, data);
            view.displayMessage("Đã tạo báo cáo tổng doanh thu năm " + year, false);
        } catch (Exception e) {
            view.displayMessage("Lỗi khi tạo báo cáo tổng doanh thu năm: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void generateTopSellingProductsReport() {
        try {
            String[] columnNames = {"Mã Sản Phẩm", "Tên Sản Phẩm", "Tổng Số Lượng Bán", "Tổng Doanh Thu"};
            Vector<Vector<Object>> data = new Vector<>();

            // Hiện tại, không có trường nhập ngày tháng cho loại báo cáo này trên UI,
            // nên ta sẽ lấy top 10 sản phẩm bán chạy nhất trong 3 tháng gần nhất (hoặc toàn bộ)
            // Cần xem xét thêm nếu muốn thêm khoảng thời gian vào UI cho báo cáo này.
            // Để đơn giản, ở đây ta lấy top 10 theo doanh thu trong toàn bộ thời gian.
            List<TopSanPhamBanChay> topProducts = service.getTopSanPhamBanChay(10, "revenue", null, null);

            for (TopSanPhamBanChay product : topProducts) {
                Vector<Object> row = new Vector<>();
                row.add(product.getMaSanPham());
                row.add(product.getTenSanPham());
                row.add(view.formatNumber(product.getTongSoLuongBan()));
                row.add(view.formatCurrency(product.getTongDoanhThuTuSP().doubleValue()));
                data.add(row);
            }
            view.updateReportTable(columnNames, data);
            view.displayMessage("Đã tạo báo cáo sản phẩm bán chạy nhất.", false);
        } catch (Exception e) {
            view.displayMessage("Lỗi khi tạo báo cáo sản phẩm bán chạy nhất: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void generateProductStockLevelsReport() {
        try {
            String[] columnNames = {"Mã Sản Phẩm", "Tên Sản Phẩm", "Tồn Kho Hiện Tại", "Vị Trí Lưu Trữ"};
            Vector<Vector<Object>> data = new Vector<>();

            // Bạn cần bổ sung phương thức này vào BaoCaoDAO và BaoCaoService
            // Ví dụ: service.getInventoryLevels();
            // Tạm thời, tôi sẽ tạo dữ liệu giả định hoặc để trống nếu bạn chưa có DAO/Service cho nó.
            // Vì hiện tại BaoCaoDAO không có phương thức này, ta sẽ hiển thị thông báo.
            view.displayMessage("Báo cáo mức tồn kho sản phẩm chưa được triển khai đầy đủ.", true);
            view.updateReportTable(new String[]{}, new Vector<>()); // Clear table

        } catch (Exception e) {
            view.displayMessage("Lỗi khi tạo báo cáo mức tồn kho sản phẩm: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void generateMonthlyImportQuantityReport(int year) {
        try {
            String[] columnNames = {"Tháng", "Tổng Số Lượng Nhập"};
            Vector<Vector<Object>> data = new Vector<>();

            // Tương tự như báo cáo tồn kho, bạn cần bổ sung phương thức này vào BaoCaoDAO và BaoCaoService
            // Ví dụ: service.getMonthlyImportQuantities(year);
            // Hiện tại BaoCaoDAO không có phương thức này, ta sẽ hiển thị thông báo.
            view.displayMessage("Báo cáo số lượng nhập theo tháng chưa được triển khai đầy đủ.", true);
            view.updateReportTable(new String[]{}, new Vector<>()); // Clear table

        } catch (Exception e) {
            view.displayMessage("Lỗi khi tạo báo cáo số lượng nhập theo tháng: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    // Bạn sẽ cần một phương thức khởi tạo chính (main method hoặc tương tự)
    // để tạo các đối tượng và kết nối chúng lại với nhau.
    // Ví dụ:
    /*
    public static void main(String[] args) {
        // Khởi tạo DatabaseConnection trước khi sử dụng
        // DatabaseConnection.initialize(...); // Tùy thuộc vào cách bạn thiết lập kết nối

        // Khởi tạo DAO
        BaoCaoDAO baoCaoDAO = new BaoCaoDAOImpl();
        // Khởi tạo Service
        BaoCaoService baoCaoService = new BaoCaoServiceImpl(baoCaoDAO);
        // Khởi tạo View
        BaoCaoView baoCaoView = new BaoCaoView();
        // Khởi tạo Controller, truyền View và Service vào
        BaoCaoController baoCaoController = new BaoCaoController(baoCaoView, baoCaoService);

        // Hiển thị View trong JFrame
        JFrame frame = new JFrame("Báo Cáo Thống Kê");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(baoCaoView);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    */
}