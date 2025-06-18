package system.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import system.components.CustomScrollBarUI;
import system.models.entity.LoaiSanPham; // Import LoaiSanPham model

/**
 * Lớp LoaiSanPhamView tạo giao diện người dùng cho việc quản lý loại sản phẩm.
 * Nó hiển thị bảng loại sản phẩm, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class LoaiSanPhamView extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable loaiSanPhamTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu
    public JTextField txtMaLoaiSanPham;
    private JTextField txtTenLoaiSanPham;
    private JTextArea txtMoTa;

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    public LoaiSanPhamView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        layoutComponents();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Table setup
        String[] columnNames = {"Mã Loại SP", "Tên Loại SP", "Mô Tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        loaiSanPhamTable = new JTable(tableModel);
        loaiSanPhamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Input fields and labels
        txtMaLoaiSanPham = new JTextField(10);
        txtMaLoaiSanPham.setEditable(false); // Mã loại sản phẩm tự động, không cho sửa

        txtTenLoaiSanPham = new JTextField(20);
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane moTaScrollPane = new JScrollPane(txtMoTa);

        // Buttons
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");
        btnSearch = new JButton("Tìm kiếm");
        txtSearch = new JTextField(15);

        messageLabel = new JLabel("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Tìm kiếm (Tên loại SP):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table
        JScrollPane scrollPane = new JScrollPane(loaiSanPhamTable);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#2196F3"), Color.decode("#E0E0E0")));
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#4CAF50"), Color.decode("#E0E0E0")));
        add(scrollPane, BorderLayout.CENTER);

        // South Section: Input form and buttons
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));

        // Input form panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin loại sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Mã Loại SP (tự động)
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã Loại SP (tự động):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaLoaiSanPham, gbc);

        // Row 1: Tên Loại SP
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Tên Loại SP:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtTenLoaiSanPham, gbc);

        // Row 2: Mô Tả
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Mô Tả:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(new JScrollPane(txtMoTa), gbc);
        gbc.gridheight = 1; // Reset gridheight

        southPanel.add(inputPanel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        southPanel.add(buttonPanel, BorderLayout.CENTER); // Nút ở giữa trong southPanel
        southPanel.add(messageLabel, BorderLayout.SOUTH); // Message label ở cuối trong southPanel

        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Điền dữ liệu vào bảng loại sản phẩm.
     * @param loaiSanPhamList Danh sách các đối tượng LoaiSanPham để hiển thị.
     */
    public void populateTable(List<LoaiSanPham> loaiSanPhamList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (LoaiSanPham lsp : loaiSanPhamList) {
            Vector<Object> row = new Vector<>();
            row.add(lsp.getMaLoaiSanPham());
            row.add(lsp.getTenLoaiSanPham());
            row.add(lsp.getMoTa());
            tableModel.addRow(row);
        }
    }

    /**
     * Hiển thị thông tin loại sản phẩm lên các trường nhập liệu khi chọn một hàng trên bảng.
     * @param loaiSanPham Đối tượng LoaiSanPham để hiển thị.
     */
    public void displayLoaiSanPhamDetails(LoaiSanPham loaiSanPham) {
        if (loaiSanPham != null) {
            txtMaLoaiSanPham.setText(loaiSanPham.getMaLoaiSanPham());
            txtTenLoaiSanPham.setText(loaiSanPham.getTenLoaiSanPham());
            txtMoTa.setText(loaiSanPham.getMoTa());
        } else {
            clearInputFields();
        }
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng LoaiSanPham.
     * @return Đối tượng LoaiSanPham với dữ liệu đã nhập.
     */
    public LoaiSanPham getLoaiSanPhamFromInput() {
        LoaiSanPham lsp = new LoaiSanPham();
        lsp.setMaLoaiSanPham(txtMaLoaiSanPham.getText()); // Có thể là rỗng nếu đang thêm mới
        lsp.setTenLoaiSanPham(txtTenLoaiSanPham.getText().trim());
        lsp.setMoTa(txtMoTa.getText().trim());

        // Kiểm tra các trường bắt buộc
        if (lsp.getTenLoaiSanPham().isEmpty()) {
            displayMessage("Tên loại sản phẩm không được để trống.", true);
            return null;
        }

        return lsp;
    }

    /**
     * Xóa trắng các trường nhập liệu.
     */
    public void clearInputFields() {
        txtMaLoaiSanPham.setText("");
        txtTenLoaiSanPham.setText("");
        txtMoTa.setText("");
        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);
    }

    /**
     * Lấy văn bản từ trường tìm kiếm.
     * @return Chuỗi tìm kiếm.
     */
    public String getSearchText() {
        return txtSearch.getText();
    }

    /**
     * Hiển thị thông báo trên giao diện.
     * @param message Nội dung thông báo.
     * @param isError True nếu là thông báo lỗi, False nếu là thông báo thành công.
     */
    public void displayMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : Color.BLUE);
        if (isError) {
             JOptionPane.showMessageDialog(this, message, "Thông báo lỗi", JOptionPane.ERROR_MESSAGE);
        } else {
             // JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- Add ActionListeners for Controller to register ---
    public void addAddButtonListener(ActionListener listener) {
        btnAdd.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        btnUpdate.addActionListener(listener);
    }

    public void addDeleteButtonListener(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    public void addClearButtonListener(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    public void addSearchButtonListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
    }

    public JTable getLoaiSanPhamTable() {
        return loaiSanPhamTable;
    }
}

