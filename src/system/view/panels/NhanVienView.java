package system.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

import system.components.CustomScrollBarUI;
import system.models.entity.NhanVien;
import system.models.entity.TaiKhoanNguoiDung; // Import TaiKhoanNguoiDung model

/**
 * Lớp EmployeeView tạo giao diện người dùng cho việc quản lý nhân viên.
 * Nó hiển thị bảng nhân viên, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class NhanVienView extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JTable nhanVienTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu
    private JTextField txtMaNhanVien;
    private JTextField txtHoTen;
    private JTextField txtNgaySinh; // Định dạng YYYY-MM-DD
    private JComboBox<String> cbxGioiTinh;
    private JTextField txtCCCD;
    private JTextField txtSdt;
    private JTextField txtLuong; // Số thập phân
    private JComboBox<String> cbxTrangThaiLamViec;
    private JTextField txtMaNguoiDung; // Mã người dùng liên kết (FK)

    // Các trường cho thông tin tài khoản liên kết (khi thêm mới)
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtEmail; // Thêm trường email
    private JCheckBox chkLinkAccount; // Checkbox để quyết định có liên kết tài khoản không

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NhanVienView() {
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
        String[] columnNames = {"Mã NV", "Họ Tên", "Ngày Sinh", "Giới Tính", "CCCD", "SĐT", "Lương", "Trạng Thái", "Mã Người Dùng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        nhanVienTable = new JTable(tableModel);
        nhanVienTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Input fields and labels
        txtMaNhanVien = new JTextField(10);
        txtMaNhanVien.setEditable(false); // Mã nhân viên tự động, không cho sửa

        txtHoTen = new JTextField(20);
        txtNgaySinh = new JTextField(10);
        cbxGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        txtCCCD = new JTextField(15);
        txtSdt = new JTextField(15);
        txtLuong = new JTextField(10);
        cbxTrangThaiLamViec = new JComboBox<>(new String[]{"Hoạt động", "Nghỉ phép", "Đã nghỉ việc"});
        txtMaNguoiDung = new JTextField(10);
        txtMaNguoiDung.setEditable(false); // Mã người dùng tự động, không cho sửa

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtEmail = new JTextField(20); // Khởi tạo trường email

        chkLinkAccount = new JCheckBox("Liên kết tài khoản mới");
        // Mặc định ẩn các trường tài khoản nếu checkbox chưa được chọn
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtEmail.setEnabled(false); // Vô hiệu hóa trường email

        // Thêm listener cho checkbox để bật/tắt các trường tài khoản
        chkLinkAccount.addActionListener(e -> {
            boolean enabled = chkLinkAccount.isSelected();
            txtUsername.setEnabled(enabled);
            txtPassword.setEnabled(enabled);
            txtEmail.setEnabled(enabled); // Kích hoạt/Vô hiệu hóa trường email
            if (!enabled) {
                txtUsername.setText("");
                txtPassword.setText("");
                txtEmail.setText(""); // Xóa nội dung email
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
     * Sắp xếp các thành phần UI trên panel.
     * Đã điều chỉnh để tránh ghi đè các thành phần ở khu vực SOUTH.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT/CCCD):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table
        JScrollPane scrollPane = new JScrollPane(nhanVienTable);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#2196F3"), Color.decode("#E0E0E0")));
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI(Color.decode("#4CAF50"), Color.decode("#E0E0E0")));
        add(scrollPane, BorderLayout.CENTER);

        // South Section: Container for input form, buttons, and message
        JPanel southSectionPanel = new JPanel(new BorderLayout(10, 10)); // Main container for bottom

        // Input form panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Mã NV (tự động)
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã NV (tự động):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaNhanVien, gbc);

        // Row 1: Họ Tên
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Họ Tên:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtHoTen, gbc);

        // Row 2: Ngày Sinh
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Ngày Sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtNgaySinh, gbc);

        // Row 3: Giới Tính
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Giới Tính:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbxGioiTinh, gbc);

        // Row 4: CCCD
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtCCCD, gbc);

        // Row 5: SĐT
        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtSdt, gbc);

        // Row 6: Lương
        gbc.gridx = 0; gbc.gridy = 6; inputPanel.add(new JLabel("Lương:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtLuong, gbc);

        // Row 7: Trạng Thái Làm Việc
        gbc.gridx = 0; gbc.gridy = 7; inputPanel.add(new JLabel("Trạng Thái:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbxTrangThaiLamViec, gbc);

        // Row 8: Mã Người Dùng (FK)
        gbc.gridx = 0; gbc.gridy = 8; inputPanel.add(new JLabel("Mã Người Dùng (FK):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaNguoiDung, gbc);

        // Checkbox và các trường tài khoản
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; inputPanel.add(chkLinkAccount, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        gbc.gridx = 0; gbc.gridy = 10; inputPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 11; inputPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 12; inputPanel.add(new JLabel("Email:"), gbc); // Thêm nhãn và trường email
        gbc.gridx = 1; inputPanel.add(txtEmail, gbc);

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

        add(southSectionPanel, BorderLayout.SOUTH); // southSectionPanel chiếm toàn bộ khu vực SOUTH của EmployeeView
    }

    /**
     * Điền dữ liệu vào bảng nhân viên.
     * @param nhanVienList Danh sách các đối tượng NhanVien để hiển thị.
     */
    public void populateTable(List<NhanVien> nhanVienList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (NhanVien nv : nhanVienList) {
            Vector<Object> row = new Vector<>();
            row.add(nv.getMaNhanVien());
            row.add(nv.getHoTen());
            row.add(nv.getNgaySinh() != null ? nv.getNgaySinh().format(dateFormatter) : ""); // Sử dụng dateFormatter
            row.add(nv.getGioiTinh());
            row.add(nv.getCccd());
            row.add(nv.getSdt());
            row.add(nv.getLuong());
            row.add(nv.getTrangThaiLamViec());
            row.add(nv.getMaNguoiDung() != null ? nv.getMaNguoiDung() : "N/A");
            tableModel.addRow(row);
        }
    }

    /**
     * Hiển thị thông tin nhân viên lên các trường nhập liệu khi chọn một hàng trên bảng.
     * @param nhanVien Đối tượng NhanVien để hiển thị.
     */
    public void displayNhanVienDetails(NhanVien nhanVien) {
        if (nhanVien != null) {
            txtMaNhanVien.setText(nhanVien.getMaNhanVien());
            txtHoTen.setText(nhanVien.getHoTen());
            txtNgaySinh.setText(nhanVien.getNgaySinh() != null ? nhanVien.getNgaySinh().format(dateFormatter) : "");
            cbxGioiTinh.setSelectedItem(nhanVien.getGioiTinh());
            txtCCCD.setText(nhanVien.getCccd());
            txtSdt.setText(nhanVien.getSdt());
            txtLuong.setText(nhanVien.getLuong().toPlainString());
            cbxTrangThaiLamViec.setSelectedItem(nhanVien.getTrangThaiLamViec());
            txtMaNguoiDung.setText(nhanVien.getMaNguoiDung() != null ? nhanVien.getMaNguoiDung() : "");

            // Khi hiển thị chi tiết, không cho phép liên kết tài khoản mới
            chkLinkAccount.setSelected(false);
            txtUsername.setText("");
            txtPassword.setText("");
            txtEmail.setText(""); // Clear email field
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
            txtEmail.setEnabled(false); // Disable email field
        } else {
            clearInputFields();
        }
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng NhanVien.
     * @return Đối tượng NhanVien với dữ liệu đã nhập.
     */
    public NhanVien getNhanVienFromInput() {
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(txtMaNhanVien.getText()); // Có thể là rỗng nếu đang thêm mới
        nv.setHoTen(txtHoTen.getText());
        try {
            if (!txtNgaySinh.getText().isEmpty()) {
                nv.setNgaySinh(LocalDate.parse(txtNgaySinh.getText(), dateFormatter));
            } else {
                nv.setNgaySinh(null);
            }
        } catch (DateTimeParseException e) {
            displayMessage("Ngày sinh không hợp lệ (YYYY-MM-DD).", true);
            return null;
        }
        nv.setGioiTinh((String) cbxGioiTinh.getSelectedItem());
        nv.setCccd(txtCCCD.getText());
        nv.setSdt(txtSdt.getText());
        try {
            nv.setLuong(new BigDecimal(txtLuong.getText()));
        } catch (NumberFormatException e) {
            displayMessage("Lương không hợp lệ (phải là số).", true);
            return null;
        }
        nv.setTrangThaiLamViec((String) cbxTrangThaiLamViec.getSelectedItem());
        nv.setMaNguoiDung(txtMaNguoiDung.getText()); // Có thể là rỗng nếu không liên kết TK

        // Kiểm tra các trường bắt buộc
        if (nv.getHoTen().trim().isEmpty() || nv.getCccd().trim().isEmpty() || nv.getSdt().trim().isEmpty() || nv.getLuong() == null || nv.getLuong().compareTo(BigDecimal.ZERO) <= 0) {
            displayMessage("Họ tên, CCCD, SĐT và Lương không được để trống hoặc không hợp lệ.", true);
            return null;
        }

        // Kiểm tra định dạng CCCD (ví dụ 12 chữ số)
        if (!nv.getCccd().matches("\\d{12}")) {
            displayMessage("CCCD không hợp lệ (phải là 12 chữ số).", true);
            return null;
        }
        // Kiểm tra định dạng số điện thoại (10 hoặc 11 chữ số)
        if (!nv.getSdt().matches("\\d{10,11}")) {
            displayMessage("Số điện thoại không hợp lệ (phải là 10-11 chữ số).", true);
            return null;
        }

        return nv;
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
     * Lấy email từ trường nhập liệu.
     * @return Email.
     */
    public String getEmailFromInput() {
        return txtEmail.getText();
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
        txtMaNhanVien.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        cbxGioiTinh.setSelectedIndex(0);
        txtCCCD.setText("");
        txtSdt.setText("");
        txtLuong.setText("");
        cbxTrangThaiLamViec.setSelectedIndex(0);
        txtMaNguoiDung.setText("");

        clearLinkedAccountFields(); // Sử dụng hàm mới để xóa trường tài khoản

        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);
    }

    /**
     * Xóa trắng các trường thông tin tài khoản liên kết.
     */
    public void clearLinkedAccountFields() {
        chkLinkAccount.setSelected(false); // Reset checkbox
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText(""); // Clear email field
        txtUsername.setEnabled(false); // Disable user account fields
        txtPassword.setEnabled(false); // Disable user account fields
        txtEmail.setEnabled(false); // Disable email field
    }

    /**
     * Hiển thị thông tin tài khoản người dùng liên kết.
     * @param account Đối tượng TaiKhoanNguoiDung.
     */
    public void displayLinkedAccountDetails(TaiKhoanNguoiDung account) {
        if (account != null) {
            chkLinkAccount.setSelected(true);
            txtUsername.setText(account.getUsername());
            txtPassword.setText("********"); // Don't display actual password
            txtEmail.setText(account.getEmail()); // Display email
            txtUsername.setEnabled(false); // Disable editing linked account username
            txtPassword.setEnabled(false); // Disable editing linked account password
            txtEmail.setEnabled(false); // Disable editing linked account email
        } else {
            clearLinkedAccountFields();
        }
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

    public JTable getNhanVienTable() {
        return nhanVienTable;
    }
}