package system.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import system.components.CustomScrollBarUI;
import system.models.entity.KhachHang; // Import KhachHang model

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
    JTextField txtMaKhachHang; // Thay đổi sang protected hoặc để mặc định package-private nếu cần truy cập từ Controller
    JTextField txtHoTen;
    JTextField txtNgaySinh;
    JComboBox<String> cbxGioiTinh;
    JTextField txtSdt;
    JTextField txtMaNguoiDung;

    // Các trường cho thông tin tài khoản liên kết (chỉ khi thêm mới)
    JTextField txtUsername;
    JPasswordField txtPassword;
    JCheckBox chkLinkAccount; // Checkbox để quyết định có liên kết tài khoản không

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public KhachHangView() {
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
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Input fields and labels
        txtMaKhachHang = new JTextField(10);
        txtMaKhachHang.setEditable(false); // Mã khách hàng tự động, không cho sửa

        txtHoTen = new JTextField(20);
        txtNgaySinh = new JTextField(10);
        cbxGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"}); // Giới tính
        txtSdt = new JTextField(15);
        txtMaNguoiDung = new JTextField(10);
        txtMaNguoiDung.setEditable(false); // Mã người dùng tự động, không cho sửa

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        chkLinkAccount = new JCheckBox("Liên kết tài khoản mới");
        // Mặc định ẩn các trường tài khoản nếu checkbox chưa được chọn
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        
        // Thêm listener cho checkbox để bật/tắt các trường tài khoản
        chkLinkAccount.addActionListener(e -> {
            boolean enabled = chkLinkAccount.isSelected();
            txtUsername.setEnabled(enabled);
            txtPassword.setEnabled(enabled);
            if (!enabled) {
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
        txtSearch = new JTextField(15);

        messageLabel = new JLabel("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Sắp xếp các thành phần UI.
     * Đã điều chỉnh để tránh ghi đè các thành phần ở khu vực SOUTH.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#2196F3"), Color.decode("#E0E0E0")));
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#4CAF50"), Color.decode("#E0E0E0")));
        add(scrollPane, BorderLayout.CENTER);

        // South Section: Container for input form, buttons, and message
        JPanel southSectionPanel = new JPanel(new BorderLayout(10, 10)); // Main container for bottom

        // Input form panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Mã KH
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã KH (tự động):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaKhachHang, gbc);

        // Row 1: Họ Tên
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Họ Tên:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtHoTen, gbc);

        // Row 2: Ngày Sinh
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Ngày Sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtNgaySinh, gbc);

        // Row 3: Giới Tính
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Giới Tính:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbxGioiTinh, gbc);

        // Row 4: SĐT
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtSdt, gbc);

        // Row 5: Mã Người Dùng (nếu có liên kết)
        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(new JLabel("Mã Người Dùng (FK):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaNguoiDung, gbc);

        // Checkbox và các trường tài khoản
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; inputPanel.add(chkLinkAccount, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        gbc.gridx = 0; gbc.gridy = 7; inputPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 8; inputPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPassword, gbc);

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

        add(southSectionPanel, BorderLayout.SOUTH); // southSectionPanel chiếm toàn bộ khu vực SOUTH của CustomerView
    }

    /**
     * Điền dữ liệu vào bảng khách hàng.
     * @param khachHangList Danh sách các đối tượng KhachHang để hiển thị.
     */
    public void populateTable(List<KhachHang> khachHangList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (KhachHang kh : khachHangList) {
            Vector<Object> row = new Vector<>();
            row.add(kh.getMaKhachHang());
            row.add(kh.getHoTen());
            row.add(kh.getNgaySinh() != null ? dateFormat.format(kh.getNgaySinh()) : "");
            row.add(kh.getGioiTinh());
            row.add(kh.getSdt());
            row.add(kh.getMaNguoiDung() != null ? kh.getMaNguoiDung() : "N/A");
            tableModel.addRow(row);
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
            txtNgaySinh.setText(khachHang.getNgaySinh() != null ? dateFormat.format(khachHang.getNgaySinh()) : "");
            cbxGioiTinh.setSelectedItem(khachHang.getGioiTinh());
            txtSdt.setText(khachHang.getSdt());
            txtMaNguoiDung.setText(khachHang.getMaNguoiDung() != null ? khachHang.getMaNguoiDung() : "");

            // Khi hiển thị chi tiết, không cho phép liên kết tài khoản mới
            chkLinkAccount.setSelected(false);
            txtUsername.setText("");
            txtPassword.setText("");
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
        kh.setMaKhachHang(txtMaKhachHang.getText()); // Có thể là rỗng nếu đang thêm mới
        kh.setHoTen(txtHoTen.getText());
        try {
            if (!txtNgaySinh.getText().isEmpty()) {
                kh.setNgaySinh(dateFormat.parse(txtNgaySinh.getText()));
            } else {
                kh.setNgaySinh(null);
            }
        } catch (ParseException e) {
            displayMessage("Ngày sinh không hợp lệ (YYYY-MM-DD).", true);
            return null;
        }
        kh.setGioiTinh((String) cbxGioiTinh.getSelectedItem());
        kh.setSdt(txtSdt.getText());
        // MaNguoiDung sẽ được thiết lập bởi controller khi có tài khoản liên kết

        // Kiểm tra các trường bắt buộc
        if (kh.getHoTen().trim().isEmpty() || kh.getSdt().trim().isEmpty()) {
            displayMessage("Họ tên và Số điện thoại không được để trống.", true);
            return null;
        }
        
        // Kiểm tra định dạng số điện thoại (đơn giản)
        if (!kh.getSdt().matches("\\d{10,11}")) { // 10 hoặc 11 chữ số
            displayMessage("Số điện thoại không hợp lệ (phải là 10-11 chữ số).", true);
            return null;
        }

        return kh;
    }

    /**
     * Lấy tên đăng nhập từ trường nhập liệu.
     * @return Tên đăng nhập.
     */
    public String getUsernameFromInput() {
        return txtUsername.getText();
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
        
        chkLinkAccount.setSelected(false); // Reset checkbox
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.setEnabled(false); // Disable user account fields
        txtPassword.setEnabled(false); // Disable user account fields

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

    public JTable getCustomerTable() {
        return customerTable;
    }
}
