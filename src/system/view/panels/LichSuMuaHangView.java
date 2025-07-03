package system.view.panels;

import system.models.entity.HoaDon;
import system.theme.AppTheme;
import system.theme.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LichSuMuaHangView extends JPanel {

    private JTable hoaDonTable;
    private DefaultTableModel tableModel;
    private JButton chiTietButton;
    private JLabel titleLabel;
    private JScrollPane scrollPane;

    public LichSuMuaHangView() {
        initializeUI();
    }

    private void initializeUI() {
        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();
        setLayout(new BorderLayout(10, 10)); // Add gaps
        setBackground(theme.getPanelBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        titleLabel = new JLabel("LỊCH SỬ MUA HÀNG", SwingConstants.CENTER);
        titleLabel.setFont(theme.getTitleFont().deriveFont(Font.BOLD, 32f));
        titleLabel.setForeground(theme.getPrimaryColor());
        add(titleLabel, BorderLayout.NORTH);

        // Table Model
        String[] columnNames = {"Mã Hóa Đơn", "Ngày Bán", "Khách Hàng", "SĐT", "PT Thanh Toán"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        hoaDonTable = new JTable(tableModel);

        // Table appearance
        hoaDonTable.setFont(theme.getDefaultFont().deriveFont(Font.PLAIN, 14f));
        hoaDonTable.setForeground(theme.getTextColor());
        hoaDonTable.setBackground(theme.getBackgroundColor());
        hoaDonTable.setSelectionBackground(theme.getAccentColor());
        hoaDonTable.setSelectionForeground(Color.WHITE); // Make selected text white for better contrast
        hoaDonTable.setRowHeight(30);
        hoaDonTable.getTableHeader().setFont(theme.getButtonFont());
        hoaDonTable.getTableHeader().setBackground(theme.getPrimaryColor());
        hoaDonTable.getTableHeader().setForeground(Color.WHITE);
        hoaDonTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        hoaDonTable.setFillsViewportHeight(true); // Fill the entire height of the scroll pane

        // Scroll Pane
        scrollPane = new JScrollPane(hoaDonTable);
        scrollPane.setBackground(theme.getPanelBackgroundColor());
        scrollPane.setBorder(BorderFactory.createLineBorder(theme.getBorderColor(), 1)); // Add border to scroll pane
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Add gaps
        buttonPanel.setBackground(theme.getPanelBackgroundColor());

        chiTietButton = new JButton("Xem Chi Tiết Hóa Đơn");
        chiTietButton.setFont(theme.getButtonFont());
        chiTietButton.setBackground(theme.getPrimaryColor());
        chiTietButton.setForeground(Color.WHITE);
        chiTietButton.setFocusPainted(false);
        chiTietButton.setBorder(BorderFactory.createLineBorder(theme.getPrimaryColor().darker(), 1));
        chiTietButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add mouse listener for hover effect
        chiTietButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                chiTietButton.setBackground(theme.getPrimaryColor().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                chiTietButton.setBackground(theme.getPrimaryColor());
            }
        });

        buttonPanel.add(chiTietButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Apply theme to ensure all components get the correct colors
        ThemeManager.getInstance().applyTheme(this);
    }

    // --- Methods for Controller to interact with View ---

    public void displayHoaDonList(List<HoaDon> hoaDonList) {
        tableModel.setRowCount(0); // Clear existing data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        if (hoaDonList != null && !hoaDonList.isEmpty()) {
            for (HoaDon hd : hoaDonList) {
                String customerName = hd.getMaKhachHang() != null ? hd.getMaKhachHang() : hd.getTenKhachHangVangLai();
                String customerSdt = hd.getMaKhachHang() != null ? "" : hd.getSdtKhachHangVangLai(); // Assume SDT is only for walk-in
                
                tableModel.addRow(new Object[]{
                    hd.getMaHoaDon(),
                    hd.getNgayBan().format(formatter),
                    customerName,
                    customerSdt,
                    hd.getPhuongThucThanhToan()
                });
            }
        }
    }

    public String getSelectedHoaDonMa() {
        int selectedRow = hoaDonTable.getSelectedRow();
        if (selectedRow != -1) {
            // Assuming MaHoaDon is in the first column (index 0)
            Object maHoaDonObj = tableModel.getValueAt(selectedRow, 0);
            return maHoaDonObj != null ? maHoaDonObj.toString() : null;
        }
        return null;
    }

    public void addChiTietButtonListener(ActionListener listener) {
        chiTietButton.addActionListener(listener);
    }

    public JButton getChiTietButton() {
        return chiTietButton;
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}