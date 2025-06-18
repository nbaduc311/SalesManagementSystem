package system.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.List;
import system.models.entity.LoaiSanPham; // Import LoaiSanPham để đổ dữ liệu vào JComboBox
import system.models.entity.SanPham; // Import SanPham model
import system.components.CustomScrollBarUI; // Import CustomScrollBarUI

/**
 * Lớp SanPhamView tạo giao diện người dùng cho việc quản lý sản phẩm.
 * Nó hiển thị bảng sản phẩm, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class SanPhamView extends JPanel {
    /**
	 * */
	private static final long serialVersionUID = 1L;
	private JTable productTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu
    private JTextField txtMaSanPham;
    private JTextField txtTenSanPham;
    private JTextField txtDonGia;
    private JTextField txtNgaySanXuat; // Định dạng YYYY-MM-DD
    private JTextArea txtThongSoKyThuat; // Could be JSON string or simple text
    private JComboBox<String> cbxMaLoaiSanPham; // For selecting product category
    private JLabel lblSoLuongTon; // Thêm JLabel để hiển thị số lượng tồn

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public SanPhamView() {
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
        String[] columnNames = {"Mã SP", "Tên SP", "Đơn Giá", "Ngày SX", "Thông Số KT", "Mã Loại SP", "Tồn Kho"}; // Thêm cột "Tồn Kho"
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Input fields and labels
        txtMaSanPham = new JTextField(10);
        txtMaSanPham.setEditable(false); // Mã sản phẩm tự động, không cho sửa

        txtTenSanPham = new JTextField(20);
        txtDonGia = new JTextField(10);
        txtNgaySanXuat = new JTextField(10);
        txtThongSoKyThuat = new JTextArea(3, 20); // 3 hàng, 20 cột
        txtThongSoKyThuat.setLineWrap(true);
        txtThongSoKyThuat.setWrapStyleWord(true);
        // JScrollPane scrollThongSo = new JScrollPane(txtThongSoKyThuat); // This line is not needed here as it's handled in layoutComponents

        cbxMaLoaiSanPham = new JComboBox<>(); // Sẽ được điền dữ liệu từ Controller
        lblSoLuongTon = new JLabel("Tồn Kho: 0"); // Khởi tạo nhãn tồn kho

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
     * Đã điều chỉnh để tránh ghi đè các thành phần ở khu vực SOUTH.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Tìm kiếm (Tên SP):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table
        JScrollPane scrollPane = new JScrollPane(productTable);
        
        // --- Apply CustomScrollBarUI to the JScrollPane's scrollbars ---
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#2196F3"), Color.decode("#E0E0E0")));
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#4CAF50"), Color.decode("#E0E0E0")));
        // --- End CustomScrollBarUI application ---

        add(scrollPane, BorderLayout.CENTER);

        // South Section: Container for input form, buttons, and message
        JPanel southSectionPanel = new JPanel(new BorderLayout(10, 10)); // Main container for bottom

        // Input form panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Mã SP
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã SP (tự động):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaSanPham, gbc);

        // Row 1: Tên SP
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Tên SP:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtTenSanPham, gbc);

        // Row 2: Đơn Giá
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Đơn Giá:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtDonGia, gbc);

        // Row 3: Ngày Sản Xuất
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Ngày SX (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtNgaySanXuat, gbc);

        // Row 4: Thông Số Kỹ Thuật
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Thông Số KT:"), gbc);
        JScrollPane scrollThongSo = new JScrollPane(txtThongSoKyThuat);
        
        // --- Apply CustomScrollBarUI to the JTextArea's scrollbar (if it appears) ---
        scrollThongSo.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollThongSo.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        // --- End CustomScrollBarUI application ---
        
        gbc.gridx = 1; inputPanel.add(scrollThongSo, gbc); // Add JTextArea inside a JScrollPane

        // Row 5: Mã Loại Sản Phẩm
        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(new JLabel("Loại SP:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbxMaLoaiSanPham, gbc);
        
        // Row 6: Tồn Kho (display only)
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; inputPanel.add(lblSoLuongTon, gbc); // Chiếm 2 cột, căn trái
        gbc.gridwidth = 1; // Reset gridwidth

        southSectionPanel.add(inputPanel, BorderLayout.NORTH); // Input panel ở phía trên trong southSectionPanel

        // Panel chứa các nút và nhãn thông báo
        JPanel buttonAndMessagePanel = new JPanel(new BorderLayout(0, 5)); // Dùng BorderLayout để xếp nút và label
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonAndMessagePanel.add(buttonPanel, BorderLayout.NORTH); // Các nút ở phía trên trong buttonAndMessagePanel

        // Message label at the bottom of buttonAndMessagePanel
        buttonAndMessagePanel.add(messageLabel, BorderLayout.SOUTH);
        
        southSectionPanel.add(buttonAndMessagePanel, BorderLayout.CENTER); // buttonAndMessagePanel ở giữa trong southSectionPanel

        add(southSectionPanel, BorderLayout.SOUTH); // southSectionPanel chiếm toàn bộ khu vực SOUTH của ProductView
    }

    /**
     * Điền dữ liệu vào bảng sản phẩm.
     * @param sanPhamList Danh sách các đối tượng SanPham để hiển thị.
     */
    public void populateTable(List<SanPham> sanPhamList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (SanPham sp : sanPhamList) {
            Vector<Object> row = new Vector<>();
            row.add(sp.getMaSanPham());
            row.add(sp.getTenSanPham());
            row.add(sp.getDonGia());
            row.add(sp.getNgaySanXuat() != null ? dateFormat.format(sp.getNgaySanXuat()) : "");
            row.add(sp.getThongSoKyThuat());
            row.add(sp.getMaLoaiSanPham());
            row.add(sp.getSoLuongTon()); // Thêm cột tồn kho
            tableModel.addRow(row);
        }
    }

    /**
     * Điền dữ liệu vào ComboBox loại sản phẩm.
     * @param loaiSanPhamList Danh sách các đối tượng LoaiSanPham.
     */
    public void populateLoaiSanPhamComboBox(List<LoaiSanPham> loaiSanPhamList) {
        cbxMaLoaiSanPham.removeAllItems();
        for (LoaiSanPham lsp : loaiSanPhamList) {
            cbxMaLoaiSanPham.addItem(lsp.getMaLoaiSanPham() + " - " + lsp.getTenLoaiSanPham());
        }
    }

    /**
     * Hiển thị thông tin sản phẩm lên các trường nhập liệu khi chọn một hàng trên bảng.
     * @param sanPham Đối tượng SanPham để hiển thị.
     */
    public void displaySanPhamDetails(SanPham sanPham) {
        if (sanPham != null) {
            txtMaSanPham.setText(sanPham.getMaSanPham());
            txtTenSanPham.setText(sanPham.getTenSanPham());
            txtDonGia.setText(String.valueOf(sanPham.getDonGia())); // Convert int to String
            txtNgaySanXuat.setText(sanPham.getNgaySanXuat() != null ? dateFormat.format(sanPham.getNgaySanXuat()) : "");
            txtThongSoKyThuat.setText(sanPham.getThongSoKyThuat());
            
            // Hiển thị số lượng tồn kho
            lblSoLuongTon.setText("Tồn Kho: " + sanPham.getSoLuongTon());
            
            // Chọn đúng item trong combobox
            for (int i = 0; i < cbxMaLoaiSanPham.getItemCount(); i++) {
                if (cbxMaLoaiSanPham.getItemAt(i).startsWith(sanPham.getMaLoaiSanPham())) {
                    cbxMaLoaiSanPham.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            clearInputFields();
        }
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng SanPham.
     * @return Đối tượng SanPham với dữ liệu đã nhập.
     */
    public SanPham getSanPhamFromInput() {
        SanPham sp = new SanPham();
        sp.setMaSanPham(txtMaSanPham.getText()); // Có thể là rỗng nếu đang thêm mới
        sp.setTenSanPham(txtTenSanPham.getText());
        try {
            sp.setDonGia(Integer.parseInt(txtDonGia.getText())); // Convert String to int
        } catch (NumberFormatException e) {
            displayMessage("Đơn giá không hợp lệ (phải là số nguyên).", true);
            return null;
        }
        try {
            if (!txtNgaySanXuat.getText().isEmpty()) {
                sp.setNgaySanXuat(dateFormat.parse(txtNgaySanXuat.getText()));
            } else {
                sp.setNgaySanXuat(null);
            }
        } catch (ParseException e) {
            displayMessage("Ngày sản xuất không hợp lệ (YYYY-MM-DD).", true);
            return null;
        }
        sp.setThongSoKyThuat(txtThongSoKyThuat.getText());
        
        // Lấy MaLoaiSanPham từ combobox
        String selectedLoaiSP = (String) cbxMaLoaiSanPham.getSelectedItem();
        if (selectedLoaiSP != null && !selectedLoaiSP.isEmpty()) {
            sp.setMaLoaiSanPham(selectedLoaiSP.split(" - ")[0]); // Chỉ lấy phần mã
        } else {
            displayMessage("Vui lòng chọn loại sản phẩm.", true);
            return null;
        }

        // soLuongTon không được nhập từ đây, nó được tính toán từ CSDL
        // sp.setSoLuongTon(...) // KHÔNG đặt giá trị từ UI

        // Kiểm tra các trường bắt buộc
        if (sp.getTenSanPham().trim().isEmpty()) {
            displayMessage("Tên sản phẩm không được để trống.", true);
            return null;
        }
        if (sp.getDonGia() <= 0) {
            displayMessage("Đơn giá phải lớn hơn 0.", true);
            return null;
        }

        return sp;
    }

    /**
     * Xóa trắng các trường nhập liệu.
     */
    public void clearInputFields() {
        txtMaSanPham.setText("");
        txtTenSanPham.setText("");
        txtDonGia.setText("");
        txtNgaySanXuat.setText("");
        txtThongSoKyThuat.setText("");
        if (cbxMaLoaiSanPham.getItemCount() > 0) {
            cbxMaLoaiSanPham.setSelectedIndex(0);
        }
        lblSoLuongTon.setText("Tồn Kho: 0"); // Reset nhãn tồn kho
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

    public JTable getProductTable() {
        return productTable;
    }

    // Thêm các getters cho các trường input để Controller có thể đọc dữ liệu
    public JTextField getTxtMaSanPham() {
        return txtMaSanPham;
    }

    public JTextField getTxtTenSanPham() {
        return txtTenSanPham;
    }

    public JTextField getTxtDonGia() {
        return txtDonGia;
    }

    public JTextField getTxtNgaySanXuat() {
        return txtNgaySanXuat;
    }

    public JTextArea getTxtThongSoKyThuat() {
        return txtThongSoKyThuat;
    }

    public JComboBox<String> getCbxMaLoaiSanPham() {
        return cbxMaLoaiSanPham;
    }
}