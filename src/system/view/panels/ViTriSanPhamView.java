// system.view.panels/ViTriSanPhamView.java
package system.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector; // Dùng để mô phỏng dữ liệu bảng

public class ViTriSanPhamView extends JPanel {

    private JTable locationTable;
    private DefaultTableModel tableModel;
    private JTextField txtMaViTri, txtTenViTri, txtMoTa;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;

    public ViTriSanPhamView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(230, 240, 250));
        JLabel titleLabel = new JLabel("QUẢN LÝ VỊ TRÍ SẢN PHẨM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 100, 150));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Vị trí"));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Mã Vị trí:"));
        txtMaViTri = new JTextField();
        inputPanel.add(txtMaViTri);

        inputPanel.add(new JLabel("Tên Vị trí:"));
        txtTenViTri = new JTextField();
        inputPanel.add(txtTenViTri);
        
        inputPanel.add(new JLabel("Mô tả:"));
        txtMoTa = new JTextField();
        inputPanel.add(txtMoTa);

        add(inputPanel, BorderLayout.CENTER);

        // Table Panel
        String[] columnNames = {"Mã Vị trí", "Tên Vị trí", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0);
        locationTable = new JTable(tableModel);
        locationTable.setRowHeight(25);
        locationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        locationTable.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(locationTable);
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
        add(buttonPanel, BorderLayout.EAST);

        // Mock data
        // Vector<String> row1 = new Vector<>();
        // row1.add("VT001"); row1.add("Kệ A1"); row1.add("Kệ hàng điện thoại");
        // tableModel.addRow(row1);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(50, 90, 120), 1));
        button.setPreferredSize(new Dimension(120, 35));
    }

    // Getters
    public JTable getLocationTable() { return locationTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtMaViTri() { return txtMaViTri; }
    public JTextField getTxtTenViTri() { return txtTenViTri; }
    public JTextField getTxtMoTa() { return txtMoTa; }
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public JButton getBtnSearch() { return btnSearch; }
}