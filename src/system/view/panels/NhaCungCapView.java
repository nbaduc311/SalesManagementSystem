// system.view.panels/NhaCungCapView.java
package system.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector; // Dùng để mô phỏng dữ liệu bảng

public class NhaCungCapView extends JPanel {

    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField txtMaNCC, txtTenNCC, txtDiaChi, txtDienThoai, txtEmail;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;

    public NhaCungCapView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE); // Màu nền mặc định

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(230, 240, 250)); // Light blueish
        JLabel titleLabel = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 100, 150));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Nhà cung cấp"));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Mã NCC:"));
        txtMaNCC = new JTextField();
        inputPanel.add(txtMaNCC);

        inputPanel.add(new JLabel("Tên NCC:"));
        txtTenNCC = new JTextField();
        inputPanel.add(txtTenNCC);
        
        inputPanel.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        inputPanel.add(txtDiaChi);

        inputPanel.add(new JLabel("Điện thoại:"));
        txtDienThoai = new JTextField();
        inputPanel.add(txtDienThoai);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        add(inputPanel, BorderLayout.CENTER);

        // Table Panel
        String[] columnNames = {"Mã NCC", "Tên NCC", "Địa chỉ", "Điện thoại", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        supplierTable = new JTable(tableModel);
        supplierTable.setRowHeight(25);
        supplierTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        supplierTable.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        add(scrollPane, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");
        btnSearch = new JButton("Tìm kiếm");

        styleButton(btnAdd);
        styleButton(btnUpdate);
        styleButton(btnDelete);
        styleButton(btnClear);
        styleButton(btnSearch);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnSearch);
        add(buttonPanel, BorderLayout.EAST); // Đặt nút sang phải

        // Mock data (thay thế bằng dữ liệu thực tế từ controller)
        // Vector<String> row1 = new Vector<>();
        // row1.add("NCC001"); row1.add("FPT Shop"); row1.add("TP.HCM"); row1.add("0901234567"); row1.add("fpt@example.com");
        // tableModel.addRow(row1);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(50, 90, 120), 1));
        button.setPreferredSize(new Dimension(120, 35));
    }

    // Các getters để Controller có thể truy cập các thành phần UI
    public JTable getSupplierTable() { return supplierTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtMaNCC() { return txtMaNCC; }
    public JTextField getTxtTenNCC() { return txtTenNCC; }
    public JTextField getTxtDiaChi() { return txtDiaChi; }
    public JTextField getTxtDienThoai() { return txtDienThoai; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public JButton getBtnSearch() { return btnSearch; }
}