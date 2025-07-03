package system.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

import system.components.CustomScrollBarUI;
import system.models.entity.KhachHang;

/**
 * Lớp CustomerView tạo giao diện người dùng cho việc quản lý khách hàng.
 * Nó hiển thị bảng khách hàng, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class KhachHangView extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu
    JTextField txtMaKhachHang;
    JTextField txtHoTen;
    JTextField txtNgaySinh; // Giữ là JTextField để người dùng nhập chuỗi
    JComboBox<String> cbxGioiTinh;
    JTextField txtSdt;
    JTextField txtMaNguoiDung;

    // Các trường cho thông tin tài khoản liên kết (chỉ khi thêm mới)
    JTextField txtEmail; // Thêm trường Email
    JTextField txtUsername;
    JPasswordField txtPassword;
    JCheckBox chkLinkAccount;

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    // Sử dụng DateTimeFormatter cho LocalDate thay vì SimpleDateFormat cho Date
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public KhachHangView() {
        // Đặt layout cho JPanel chính là BorderLayout để chứa JSplitPane
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Điều chỉnh border

        initComponents();
        layoutComponents();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Table setup
        String[] columnNames = {"Mã KH", "Họ Tên", "Ngày Sinh", "Giới Tính", "SĐT", "Mã Người Dùng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Chúng ta sẽ hiển thị ngày sinh dưới dạng String trong JTable
                return Object.class;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Input fields and labels
        txtMaKhachHang = new JTextField(10);
        txtMaKhachHang.setEditable(false); // Mã KH là computed, không cho sửa

        txtHoTen = new JTextField(20);
        txtNgaySinh = new JTextField(10);
        cbxGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        txtSdt = new JTextField(15);
        txtMaNguoiDung = new JTextField(10);
        txtMaNguoiDung.setEditable(false);

        txtEmail = new JTextField(20); // Khởi tạo trường Email
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        chkLinkAccount = new JCheckBox("Liên kết tài khoản mới");

        // Ban đầu vô hiệu hóa các trường liên quan đến tài khoản
        txtEmail.setEnabled(false); // Vô hiệu hóa Email
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);

        chkLinkAccount.addActionListener(e -> {
            boolean enabled = chkLinkAccount.isSelected();
            txtEmail.setEnabled(enabled); // Bật/tắt Email
            txtUsername.setEnabled(enabled);
            txtPassword.setEnabled(enabled);
            if (!enabled) {
                txtEmail.setText(""); // Xóa trắng Email
                txtUsername.setText("");
                txtPassword.setText("");
            }
        });

        // Buttons
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");
        btnSearch = new JButton("Tìm kiếm");
        txtSearch = new JTextField(30); // Tăng kích thước cho trường tìm kiếm
    }

    /**
     * Sắp xếp các thành phần UI.
     */
    private void layoutComponents() {
        // 1. Panel chứa bảng (Table Panel)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách Khách hàng"));
        JScrollPane scrollPane = new JScrollPane(customerTable);
        // Áp dụng CustomScrollBarUI
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#2196F3"), Color.decode("#E0E0E0")));
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#4CAF50"), Color.decode("#E0E0E0")));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 2. Panel chứa Form thông tin, Search và Buttons (Right Panel)
        JPanel rightPanel = new JPanel(new GridBagLayout()); // Sử dụng GridBagLayout cho panel bên phải
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Thêm khoảng trống xung quanh

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.insets = new Insets(5, 5, 5, 5);
        rightGbc.fill = GridBagConstraints.BOTH; // Đổ đầy không gian

        // 2a. Search Panel (ROW 0 of Right Panel)
        JPanel searchPanel = new JPanel(new GridBagLayout()); // Dùng GridBagLayout cho searchPanel
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));
        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(10, 5, 10, 5); // Tăng insets để khung cao hơn
        searchGbc.fill = GridBagConstraints.HORIZONTAL;

        searchGbc.gridx = 0;
        searchGbc.gridy = 0;
        searchGbc.weightx = 0; // Label không giãn ngang
        searchGbc.anchor = GridBagConstraints.WEST;
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):"), searchGbc);

        searchGbc.gridx = 1;
        searchGbc.weightx = 1.0; // Text field giãn ngang
        searchPanel.add(txtSearch, searchGbc);

        searchGbc.gridx = 2;
        searchGbc.weightx = 0; // Button không giãn ngang
        searchPanel.add(btnSearch, searchGbc);

        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1.0;
        rightGbc.weighty = 0.15; // Tỷ lệ chiếm không gian dọc cho searchPanel
        rightPanel.add(searchPanel, rightGbc);

        // 2b. Input/Form Panel (ROW 1 of Right Panel)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng chi tiết"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Các thành phần sẽ đổ đầy theo chiều ngang
        gbc.weightx = 1.0; // Cho các trường nhập liệu giãn ngang

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Mã KH (tự động):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaKhachHang, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Họ Tên:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtHoTen, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Ngày Sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtNgaySinh, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Giới Tính:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbxGioiTinh, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtSdt, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Mã Người Dùng (FK):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaNguoiDung, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; // Checkbox chiếm 2 cột
        inputPanel.add(chkLinkAccount, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        row++; // Hàng mới cho Email
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtEmail, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtUsername, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; inputPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPassword, gbc);

        rightGbc.gridx = 0;
        rightGbc.gridy = 1;
        rightGbc.weighty = 0.75; // Tỷ lệ chiếm không gian dọc cho inputPanel
        rightPanel.add(inputPanel, rightGbc);

        // 2c. Button Panel (ROW 2 of Right Panel)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        rightGbc.gridx = 0;
        rightGbc.gridy = 2;
        rightGbc.weighty = 0.1; // Tỷ lệ chiếm không gian dọc cho buttonPanel
        rightPanel.add(buttonPanel, rightGbc);

        // 3. Sử dụng JSplitPane để chia giao diện chính
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, rightPanel);
        splitPane.setResizeWeight(0.5); // Chia đều 2 bên ban đầu
        splitPane.setDividerLocation(750); // Đặt vị trí phân chia ban đầu
        
        add(splitPane, BorderLayout.CENTER); // Thêm JSplitPane vào CENTER của JPanel chính

        // Đặt kích thước ưu tiên cho toàn bộ view
        setPreferredSize(new Dimension(1000, 700));
    }

    /**
     * Điền dữ liệu vào bảng khách hàng.
     * @param khachHangList Danh sách các đối tượng KhachHang để hiển thị.
     */
    public void populateTable(List<KhachHang> khachHangList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        if (khachHangList != null) {
            for (KhachHang kh : khachHangList) {
                Vector<Object> row = new Vector<>();
                row.add(kh.getMaKhachHang());
                row.add(kh.getHoTen());
                row.add(kh.getNgaySinh() != null ? kh.getNgaySinh().format(dateFormatter) : "");
                row.add(kh.getGioiTinh());
                row.add(kh.getSdt());
                row.add(kh.getMaNguoiDung() != null ? kh.getMaNguoiDung() : "N/A");
                tableModel.addRow(row);
            }
        }
    }

    /**
     * Hiển thị thông tin khách hàng lên các trường nhập liệu khi chọn một hàng trên bảng.
     * @param khachHang Đối tượng KhachHang để hiển thị.
     */
    public void displayKhachHangDetails(KhachHang khachHang) {
        if (khachHang != null) {
            txtMaKhachHang.setText(khachHang.getMaKhachHang());
            txtHoTen.setText(khachHang.getHoTen());
            txtNgaySinh.setText(khachHang.getNgaySinh() != null ? khachHang.getNgaySinh().format(dateFormatter) : "");
            cbxGioiTinh.setSelectedItem(khachHang.getGioiTinh());
            txtSdt.setText(khachHang.getSdt());
            txtMaNguoiDung.setText(khachHang.getMaNguoiDung() != null ? khachHang.getMaNguoiDung() : "");

            chkLinkAccount.setSelected(false);
            txtEmail.setText(""); // Xóa trắng Email khi hiển thị chi tiết khách hàng không có tài khoản
            txtUsername.setText("");
            txtPassword.setText("");
            txtEmail.setEnabled(false); // Vô hiệu hóa Email
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
        } else {
            clearInputFields();
        }
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng KhachHang.
     * @return Đối tượng KhachHang với dữ liệu đã nhập.
     */
    public KhachHang getKhachHangFromInput() {
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(txtMaKhachHang.getText());
        kh.setHoTen(txtHoTen.getText());

        try {
            if (!txtNgaySinh.getText().trim().isEmpty()) {
                kh.setNgaySinh(LocalDate.parse(txtNgaySinh.getText().trim(), dateFormatter));
            } else {
                kh.setNgaySinh(null); // Cho phép ngày sinh là null
            }
        } catch (java.time.format.DateTimeParseException e) {
            // Sử dụng JOptionPane thay vì messageLabel
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ. Định dạng phải là YYYY-MM-DD.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        kh.setGioiTinh((String) cbxGioiTinh.getSelectedItem());
        kh.setSdt(txtSdt.getText());

        if (kh.getHoTen().trim().isEmpty() || kh.getSdt().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên và Số điện thoại không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (!kh.getSdt().matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải là 10-11 chữ số).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return kh;
    }

    /**
     * Lấy email từ trường nhập liệu.
     * @return Email.
     */
    public String getEmailFromInput() {
        return txtEmail.getText().trim();
    }

    /**
     * Lấy tên đăng nhập từ trường nhập liệu.
     * @return Tên đăng nhập.
     */
    public String getUsernameFromInput() {
        return txtUsername.getText().trim();
    }

    /**
     * Lấy mật khẩu từ trường nhập liệu.
     * @return Mật khẩu dưới dạng char array.
     */
    public char[] getPasswordFromInput() {
        return txtPassword.getPassword();
    }

    /**
     * Kiểm tra xem checkbox liên kết tài khoản có được chọn không.
     * @return true nếu được chọn, false nếu không.
     */
    public boolean isLinkAccountSelected() {
        return chkLinkAccount.isSelected();
    }

    /**
     * Xóa trắng các trường nhập liệu.
     */
    public void clearInputFields() {
        txtMaKhachHang.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        cbxGioiTinh.setSelectedIndex(0);
        txtSdt.setText("");
        txtMaNguoiDung.setText("");

        chkLinkAccount.setSelected(false);
        txtEmail.setText(""); // Xóa trắng Email
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setEnabled(false); // Vô hiệu hóa Email
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
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
        if (isError) {
            JOptionPane.showMessageDialog(this, message, "Thông báo lỗi", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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

    public JTable getCustomerTable() {
        return customerTable;
    }
}