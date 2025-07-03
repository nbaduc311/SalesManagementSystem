package system.view.panels;

import system.models.entity.LoaiSanPham;
import system.models.entity.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Vector;

public class SanPhamView extends JPanel {

    private JTextField txtMaSanPham;
    private JTextArea txtTenSanPham; // Đã đổi từ JTextField sang JTextArea
    private JTextField txtDonGia;
    private JFormattedTextField txtNgaySanXuat; // Sử dụng JFormattedTextField cho ngày
    private JTextArea txtThongSoKyThuat;
    private JComboBox<String> cbxLoaiSanPham;
    private JTextField txtTimKiem;
    private JTextField txtViTriDungSanPham; // Trường mới: Vị trí đựng sản phẩm
    private JTextField txtSoLuongSanPham;   // Trường mới: Số lượng sản phẩm
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnTimKiem;
    // private JButton btnQuanLyViTri; // Đã bỏ nút này theo yêu cầu
    private JTable sanPhamTable;
    private DefaultTableModel tableModel;

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    public SanPhamView() {
        initComponents();
        setupLayout();
    }
    
    public JTable getSanPhamTable() {
        return sanPhamTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    private void initComponents() {
        // Form Components
        txtMaSanPham = new JTextField(20);
        txtMaSanPham.setEditable(false); // Mã sản phẩm là computed column, không cho chỉnh sửa
        
        // Đã đổi txtTenSanPham sang JTextArea và đặt 1 hàng
        txtTenSanPham = new JTextArea(1, 20); 
        txtTenSanPham.setLineWrap(true); // Tự động xuống dòng khi text quá dài
        txtTenSanPham.setWrapStyleWord(true); // Xuống dòng theo từ
        
        txtDonGia = new JTextField(20);
        
        try {
            MaskFormatter dateFormatter = new MaskFormatter("####-##-##");
            txtNgaySanXuat = new JFormattedTextField(dateFormatter);
            txtNgaySanXuat.setColumns(20);
        } catch (ParseException e) {
            e.printStackTrace();
            txtNgaySanXuat = new JFormattedTextField(20); // Fallback if formatter fails
        }
        
        // Đã giảm số hàng của JTextArea xuống còn 1 (đây là Thông số kỹ thuật)
        txtThongSoKyThuat = new JTextArea(1, 20); 
        txtThongSoKyThuat.setLineWrap(true);
        txtThongSoKyThuat.setWrapStyleWord(true);

        cbxLoaiSanPham = new JComboBox<>();

        // Khởi tạo các trường mới
        txtViTriDungSanPham = new JTextField(20);
        txtSoLuongSanPham = new JTextField(20);

        // Buttons
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm Mới");

        // Search Components
        txtTimKiem = new JTextField(30); // Đã tăng kích thước cho trường Tìm kiếm
        btnTimKiem = new JButton("Tìm Kiếm");

        // Table
        String[] columnNames = {"Mã SP", "Tên SP", "Đơn Giá", "Ngày SX", "Thông số KT", "Loại SP"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        sanPhamTable = new JTable(tableModel);
        sanPhamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ cho phép chọn 1 hàng
    }

    private void setupLayout() {
        setLayout(new BorderLayout()); // Sử dụng BorderLayout cho JPanel chính

        // 1. Panel chứa bảng (Table Panel)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách Sản phẩm"));
        JScrollPane scrollPane = new JScrollPane(sanPhamTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 2. Panel chứa Form thông tin, Search và Buttons (Right Panel)
        JPanel rightPanel = new JPanel(new GridBagLayout()); // Sử dụng GridBagLayout để kiểm soát vị trí và kích thước
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Thêm khoảng trống xung quanh

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(5, 5, 5, 5);
        rightGbc.fill = GridBagConstraints.BOTH; // Đổ đầy không gian

        // 2a. Search Panel (ROW 0 of Right Panel) - Đã thay đổi layout và insets
        JPanel searchPanel = new JPanel(new GridBagLayout()); // Đổi sang GridBagLayout
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(10, 5, 10, 5); // Tăng insets để khung cao hơn
        searchGbc.fill = GridBagConstraints.HORIZONTAL;

        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchGbc.weightx = 0; 
        searchGbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(new JLabel("Tìm kiếm (Tên SP):"), searchGbc);

        searchGbc.gridx = 1;
        searchGbc.weightx = 1.0; 
        searchPanel.add(txtTimKiem, searchGbc);

        searchGbc.gridx = 2;
        searchGbc.weightx = 0; 
        searchPanel.add(btnTimKiem, searchGbc);
        
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.weighty = 0.15; // Đã tăng tỷ lệ chiếm không gian dọc cho searchPanel
        rightPanel.add(searchPanel, rightGbc);

        // 2b. Form Panel (ROW 1 of Right Panel)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Sản phẩm chi tiết"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Các thành phần sẽ đổ đầy theo chiều ngang

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Mã Sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtMaSanPham, gbc);

        row++; // row hiện tại là 1 cho Tên Sản phẩm
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Tên Sản phẩm:"), gbc);
        
        // Cấu hình cho JTextArea txtTenSanPham
        gbc.gridx = 1; 
        gbc.gridheight = 1; // Chiếm 1 hàng dọc
        gbc.fill = GridBagConstraints.BOTH; // Đổ đầy cả chiều ngang và dọc
        gbc.weightx = 1.0; 
        gbc.weighty = 0.02; // Cung cấp một chút trọng số dọc
        formPanel.add(new JScrollPane(txtTenSanPham), gbc); // Bọc trong JScrollPane
        gbc.gridheight = 1; // Reset gridheight cho các thành phần tiếp theo
        gbc.weighty = 0;    // Reset weighty cho các thành phần tiếp theo
        row++; // Tăng row thêm 2 vì txtTenSanPham đã chiếm 3 hàng (hàng hiện tại + 2 hàng nữa)

        row++; // row hiện tại là 4 cho Đơn Giá
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Đơn Giá:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtDonGia, gbc);

        row++; // row hiện tại là 5 cho Ngày Sản xuất
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Ngày Sản xuất (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtNgaySanXuat, gbc);

        row++; // row hiện tại là 6 cho Thông số kỹ thuật
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Thông số kỹ thuật:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 1; gbc.fill = GridBagConstraints.BOTH; 
        gbc.weightx = 1.0; gbc.weighty = 0.05; 
        formPanel.add(new JScrollPane(txtThongSoKyThuat), gbc);
        gbc.gridheight = 1; gbc.weighty = 0; 

        row++; // row hiện tại là 7 cho Loại Sản phẩm
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Loại Sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(cbxLoaiSanPham, gbc);

        // Thêm các trường mới vào formPanel
        row++; // row hiện tại là 8 cho Vị trí đựng SP
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Vị trí đựng SP:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtViTriDungSanPham, gbc);

        row++; // row hiện tại là 9 cho Số lượng SP
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Số lượng SP:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; formPanel.add(txtSoLuongSanPham, gbc);
        
        rightGbc.gridx = 0;
        rightGbc.gridy = 1;
        rightGbc.weighty = 0.75; // Đã điều chỉnh tỷ lệ chiếm không gian dọc cho formPanel (0.15 + 0.75 + 0.1 = 1.0)
        rightPanel.add(formPanel, rightGbc);

        // 2c. Button Panel (ROW 2 of Right Panel)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        // btnQuanLyViTri đã được bỏ

        rightGbc.gridx = 0;
        rightGbc.gridy = 2;
        rightGbc.weighty = 0.1; // Cho buttonPanel chiếm một phần nhỏ không gian dọc
        rightPanel.add(buttonPanel, rightGbc);

        // 3. Sử dụng JSplitPane để chia giao diện chính
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, rightPanel);
        splitPane.setResizeWeight(0.5); // Chia đều 2 bên ban đầu
        splitPane.setDividerLocation(850); // Đặt vị trí phân chia ban đầu

        add(splitPane, BorderLayout.CENTER); // Thêm JSplitPane vào CENTER của JPanel chính
        
        setPreferredSize(new Dimension(1000, 700)); // Đặt kích thước ưu tiên cho toàn bộ view
    }

    // region Getter for form fields
    public String getMaSanPham() {
        return txtMaSanPham.getText().trim();
    }

    public String getTenSanPham() {
        return txtTenSanPham.getText().trim();
    }

    public BigDecimal getDonGia() {
        try {
            return new BigDecimal(txtDonGia.getText().trim());
        } catch (NumberFormatException e) {
            return null; // Hoặc ném ngoại lệ để controller xử lý
        }
    }

    public LocalDate getNgaySanXuat() {
        try {
            return LocalDate.parse(txtNgaySanXuat.getText().trim(), dateFormatter);
        } catch (DateTimeParseException e) {
            return null; // Hoặc ném ngoại lệ
        }
    }

    public String getThongSoKyThuat() {
        return txtThongSoKyThuat.getText().trim();
    }

    public String getSelectedMaLoaiSanPham() {
        return (String) cbxLoaiSanPham.getSelectedItem();
    }

    public String getSearchQuery() {
        return txtTimKiem.getText().trim();
    }

    // Getter cho các trường mới
    public String getViTriDungSanPham() {
        return txtViTriDungSanPham.getText().trim();
    }

    public int getSoLuongSanPham() {
        try {
            return Integer.parseInt(txtSoLuongSanPham.getText().trim());
        } catch (NumberFormatException e) {
            return -1; // Hoặc ném ngoại lệ, hoặc trả về giá trị mặc định không hợp lệ
        }
    }
    // endregion

    // region Display/Clear/Update Table
    public void fillLoaiSanPhamComboBox(List<LoaiSanPham> loaiSanPhamList) {
        cbxLoaiSanPham.removeAllItems();
        if (loaiSanPhamList != null) {
            for (LoaiSanPham lsp : loaiSanPhamList) {
                cbxLoaiSanPham.addItem(lsp.getTenLoaiSanPham());
            }
        }
    }
    
    public void displaySanPhamList(List<SanPham> sanPhamList) {
        tableModel.setRowCount(0); // Clear existing data
        for (SanPham sp : sanPhamList) {
            Vector<Object> row = new Vector<>();
            row.add(sp.getMaSanPham());
            row.add(sp.getTenSanPham());
            row.add(sp.getDonGia());
            row.add(sp.getNgaySanXuat() != null ? sp.getNgaySanXuat().format(dateFormatter) : "");
            row.add(sp.getThongSoKyThuat());
            row.add(sp.getMaLoaiSanPham()); // Hiển thị mã loại sản phẩm (có thể cần tên loại sau này)
            tableModel.addRow(row);
        }
    }
    
    // Cập nhật phương thức này để hiển thị dữ liệu của vị trí và số lượng
    // Giả định rằng bạn sẽ truyền thêm thông tin này từ Controller
    public void displaySelectedSanPham(String tenLoaiSanPham, String viTri, int soLuong) { 
        int selectedRow = sanPhamTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtMaSanPham.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtTenSanPham.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtDonGia.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtNgaySanXuat.setText(tableModel.getValueAt(selectedRow, 3).toString());
            txtThongSoKyThuat.setText(tableModel.getValueAt(selectedRow, 4).toString());
            
            cbxLoaiSanPham.setSelectedItem(tenLoaiSanPham);
            if (cbxLoaiSanPham.getSelectedItem() == null) {
                 cbxLoaiSanPham.setSelectedIndex(-1); 
            }
            
            // Hiển thị dữ liệu cho các trường mới
            txtViTriDungSanPham.setText(viTri);
            txtSoLuongSanPham.setText(String.valueOf(soLuong));
        }
    }

    public void clearForm() {
        txtMaSanPham.setText("");
        txtTenSanPham.setText("");
        txtDonGia.setText("");
        txtNgaySanXuat.setText("");
        txtThongSoKyThuat.setText("");
        cbxLoaiSanPham.setSelectedIndex(-1); // Clear selection
        sanPhamTable.clearSelection();
        txtTimKiem.setText(""); // Xóa luôn trường tìm kiếm

        // Làm sạch các trường mới
        txtViTriDungSanPham.setText("");
        txtSoLuongSanPham.setText("");
    }
    // endregion

    // region Action Listeners
    public void addThemListener(ActionListener listener) {
        btnThem.addActionListener(listener);
    }

    public void addSuaListener(ActionListener listener) {
        btnSua.addActionListener(listener);
    }

    public void addXoaListener(ActionListener listener) {
        btnXoa.addActionListener(listener);
    }

    public void addLamMoiListener(ActionListener listener) {
        btnLamMoi.addActionListener(listener);
    }

    public void addTimKiemListener(ActionListener listener) {
        btnTimKiem.addActionListener(listener);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}