package system.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

/**
 * Lớp ReportView tạo giao diện người dùng cho việc xem các báo cáo và thống kê.
 * Nó cung cấp các tùy chọn để chọn loại báo cáo, khoảng thời gian (nếu có),
 * và hiển thị kết quả trong một bảng.
 */
public class BaoCaoView extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> cbReportType;
    private JTextField txtYear;
    private JButton btnGenerateReport;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JLabel messageLabel;

    private NumberFormat currencyFormatter = new DecimalFormat("#,##0₫");

    // Constants for report types
    public static final String REPORT_TYPE_MONTHLY_REVENUE = "Doanh thu theo tháng (năm)";
    public static final String REPORT_TYPE_YEARLY_REVENUE = "Doanh thu theo năm";
    public static final String REPORT_TYPE_TOP_SELLING_PRODUCTS = "Sản phẩm bán chạy nhất";
    public static final String REPORT_TYPE_PRODUCT_STOCK_LEVELS = "Mức tồn kho sản phẩm";
    public static final String REPORT_TYPE_MONTHLY_IMPORT_QUANTITY = "Số lượng nhập theo tháng (năm)";

    public BaoCaoView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        layoutComponents();
        setupInitialState();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Report type selection
        String[] reportTypes = {
            REPORT_TYPE_MONTHLY_REVENUE,
            REPORT_TYPE_YEARLY_REVENUE,
            REPORT_TYPE_TOP_SELLING_PRODUCTS,
            REPORT_TYPE_PRODUCT_STOCK_LEVELS,
            REPORT_TYPE_MONTHLY_IMPORT_QUANTITY
        };
        cbReportType = new JComboBox<>(reportTypes);
        txtYear = new JTextField(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), 4);
        btnGenerateReport = new JButton("Tạo Báo cáo");

        // Report table
        reportTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read-only
            }
        };
        reportTable = new JTable(reportTableModel);
        reportTable.setFillsViewportHeight(true); // Make table fill the scroll pane height
        reportTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering

        messageLabel = new JLabel("Chọn loại báo cáo để bắt đầu.");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(Color.BLACK);
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     */
    private void layoutComponents() {
        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Tùy chọn Báo cáo"));
        controlPanel.add(new JLabel("Loại báo cáo:"));
        controlPanel.add(cbReportType);
        controlPanel.add(new JLabel("Năm (nếu có):"));
        controlPanel.add(txtYear);
        controlPanel.add(btnGenerateReport);

        add(controlPanel, BorderLayout.NORTH);

        // Center panel for report table
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for messages
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.add(messageLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     * Thiết lập trạng thái ban đầu của giao diện.
     */
    private void setupInitialState() {
        // Ensure initial table is empty or has a default message
        reportTableModel.setColumnCount(0);
        reportTableModel.setRowCount(0);
    }

    /**
     * Cập nhật tiêu đề cột và dữ liệu cho bảng báo cáo.
     *
     * @param columnNames Mảng các tên cột.
     * @param data Vector chứa các Vector dòng dữ liệu.
     */
    public void updateReportTable(String[] columnNames, Vector<Vector<Object>> data) {
        reportTableModel.setColumnIdentifiers(columnNames);
        reportTableModel.setRowCount(0); // Clear existing data
        for (Vector<Object> row : data) {
            reportTableModel.addRow(row);
        }
    }

    /**
     * Lấy loại báo cáo được chọn từ ComboBox.
     *
     * @return Chuỗi tên loại báo cáo.
     */
    public String getSelectedReportType() {
        return (String) cbReportType.getSelectedItem();
    }

    /**
     * Lấy giá trị năm được nhập vào JTextField.
     *
     * @return Chuỗi năm.
     */
    public String getYearInput() {
        return txtYear.getText().trim();
    }

    /**
     * Định dạng số tiền thành chuỗi tiền tệ (VND).
     *
     * @param amount Số tiền.
     * @return Chuỗi số tiền đã định dạng.
     */
    public String formatCurrency(double amount) {
        return currencyFormatter.format(amount);
    }

    /**
     * Định dạng số nguyên thành chuỗi.
     *
     * @param number Số nguyên.
     * @return Chuỗi số nguyên đã định dạng.
     */
    public String formatNumber(int number) {
        return String.format(Locale.getDefault(), "%,d", number);
    }

    /**
     * Hiển thị thông báo trên giao diện.
     *
     * @param message Nội dung thông báo.
     * @param isError True nếu là thông báo lỗi, False nếu là thông báo thành công.
     */
    public void displayMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : Color.BLUE);
    }

    // --- Action Listeners for Controller ---
    public void addGenerateReportButtonListener(ActionListener listener) {
        btnGenerateReport.addActionListener(listener);
    }
}
