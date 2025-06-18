package system.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserManagementPanel extends JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField, emailField;
    private JComboBox<String> roleComboBox, statusComboBox;
    private JButton addButton, updateButton, deleteButton;

    public UserManagementPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title ---
        JLabel title = new JLabel("Quản lý Tài khoản Người dùng");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(30, 30, 30));
        add(title, BorderLayout.NORTH);

        // --- Input Form Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Thông tin Tài khoản"));
        inputPanel.setBackground(new Color(245, 245, 245));

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Loại người dùng:"));
        String[] roles = {"Admin", "Nhân viên", "Khách hàng"};
        roleComboBox = new JComboBox<>(roles);
        inputPanel.add(roleComboBox);

        inputPanel.add(new JLabel("Trạng thái:"));
        String[] statuses = {"Hoạt động", "Bị khóa", "Vô hiệu hóa"};
        statusComboBox = new JComboBox<>(statuses);
        inputPanel.add(statusComboBox);

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
        String[] columnNames = {"InternalID", "Mã ND", "Username", "Email", "Loại ND", "Ngày Tạo", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userTable.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        loadSampleData();

        // Event Listeners
        addButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Thêm tài khoản"));
        updateButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng Cập nhật tài khoản"));
        deleteButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Tài khoản đã được xóa.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một tài khoản để xóa.");
            }
        });

        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && userTable.getSelectedRow() != -1) {
                int selectedRow = userTable.getSelectedRow();
                usernameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                emailField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
                statusComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 6).toString());
            }
        });
    }

    private void loadSampleData() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tableModel.addRow(new Object[]{1, "TK001", "admin1", "admin1@mail.com", "Admin", sdf.format(new Date()), "Hoạt động"});
        tableModel.addRow(new Object[]{2, "TK002", "nhanvien1", "nv1@mail.com", "Nhân viên", sdf.format(new Date()), "Hoạt động"});
        tableModel.addRow(new Object[]{3, "TK003", "khachhang1", "kh1@mail.com", "Khách hàng", sdf.format(new Date()), "Hoạt động"});
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