package system.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerManagementPanel extends JPanel {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, phoneField, addressField;
    private JButton addButton, updateButton, deleteButton;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title ---
        JLabel title = new JLabel("Quản lý Khách hàng");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(30, 30, 30));
        add(title, BorderLayout.NORTH);

        // --- Input Form Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Thông tin Khách hàng"));
        inputPanel.setBackground(new Color(245, 245, 245));

        inputPanel.add(new JLabel("Mã KH:"));
        idField = new JTextField();
        idField.setEditable(false);
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Tên Khách hàng:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Số điện thoại:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        inputPanel.add(new JLabel("Địa chỉ:"));
        addressField = new JTextField();
        inputPanel.add(addressField);

        // --- Button Panel for actions ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xóa");

        styleButton(addButton, new Color(50, 150, 50));
        styleButton(updateButton, new Color(50, 100, 150));
        styleButton(deleteButton, new Color(150, 50, 50));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JPanel controlPanel = new JPanel(new BorderLayout(0, 10));
        controlPanel.add(inputPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.EAST);

        // --- Table Panel ---
        String[] columnNames = {"Mã KH", "Tên KH", "SĐT", "Địa chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setFillsViewportHeight(true);
        customerTable.setRowHeight(25);
        customerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        customerTable.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        loadSampleData();

        // Event Listeners
        addButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Thêm khách hàng"));
        updateButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Cập nhật khách hàng"));
        deleteButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Khách hàng đã được xóa.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng để xóa.");
            }
        });

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                int selectedRow = customerTable.getSelectedRow();
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                phoneField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                addressField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    private void loadSampleData() {
        tableModel.addRow(new Object[]{"KH001", "Nguyễn Văn A", "0912345678", "123 Đường ABC, Q.1, TP.HCM"});
        tableModel.addRow(new Object[]{"KH002", "Trần Thị B", "0987654321", "456 Đường XYZ, Q.Bình Thạnh, TP.HCM"});
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
}