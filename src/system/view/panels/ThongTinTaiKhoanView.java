package system.view.panels;

import system.models.entity.KhachHang;
import system.models.entity.NhanVien;
import system.models.entity.TaiKhoanNguoiDung;
import system.theme.AppTheme;
import system.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ThongTinTaiKhoanView extends JPanel {

    // --- Thông tin cá nhân (Khách hàng / Nhân viên) ---
    private JLabel titleLabel;
    private JTextField maNguoiDungField; // Dùng chung cho MaKH/MaNV
    private JTextField hoTenField;
    private JTextField sdtField;
    private JTextField ngaySinhField; // Cho phép nhập LocalDate String
    private JComboBox<String> gioiTinhComboBox;

    // --- Thông tin tài khoản ---
    private JTextField usernameField;
    private JTextField emailField;

    // --- Đổi mật khẩu ---
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private JButton capNhatThongTinButton;
    private JButton doiMatKhauButton;

    public ThongTinTaiKhoanView() {
        initializeUI();
    }

    private void initializeUI() {
        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();
        setLayout(new BorderLayout(20, 20)); // Padding cho toàn bộ panel
        setBackground(theme.getPanelBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("THÔNG TIN TÀI KHOẢN", SwingConstants.CENTER);
        titleLabel.setFont(theme.getTitleFont().deriveFont(Font.BOLD, 32f));
        titleLabel.setForeground(theme.getPrimaryColor());
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(theme.getPanelBackgroundColor());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // --- Panel Thông tin cá nhân ---
        JPanel personalInfoPanel = new JPanel(new GridBagLayout());
        personalInfoPanel.setBackground(theme.getPanelBackgroundColor());
        personalInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.getBorderColor()),
                "Thông Tin Cá Nhân",
                TitledBorder.LEFT, TitledBorder.TOP,
                theme.getButtonFont().deriveFont(Font.BOLD, 16f),
                theme.getPrimaryColor()
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding for components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        personalInfoPanel.add(createLabel("Mã người dùng:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        maNguoiDungField = createTextField(false, theme); // Không cho phép chỉnh sửa
        personalInfoPanel.add(maNguoiDungField, gbc(gbc, 1, row++));

        personalInfoPanel.add(createLabel("Họ và Tên:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        hoTenField = createTextField(true, theme);
        personalInfoPanel.add(hoTenField, gbc(gbc, 1, row++));

        personalInfoPanel.add(createLabel("Số Điện Thoại:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        sdtField = createTextField(true, theme);
        personalInfoPanel.add(sdtField, gbc(gbc, 1, row++));

        personalInfoPanel.add(createLabel("Ngày Sinh (yyyy-MM-dd):", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        ngaySinhField = createTextField(true, theme);
        personalInfoPanel.add(ngaySinhField, gbc(gbc, 1, row++));
        
        personalInfoPanel.add(createLabel("Giới Tính:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        gioiTinhComboBox = createComboBox(new String[]{"Nam", "Nữ", "Khác"}, theme);
        personalInfoPanel.add(gioiTinhComboBox, gbc(gbc, 1, row++));


        mainPanel.add(personalInfoPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Spacer

        // --- Panel Thông tin tài khoản ---
        JPanel accountInfoPanel = new JPanel(new GridBagLayout());
        accountInfoPanel.setBackground(theme.getPanelBackgroundColor());
        accountInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.getBorderColor()),
                "Thông Tin Đăng Nhập",
                TitledBorder.LEFT, TitledBorder.TOP,
                theme.getButtonFont().deriveFont(Font.BOLD, 16f),
                theme.getPrimaryColor()
        ));

        row = 0;
        accountInfoPanel.add(createLabel("Tên đăng nhập:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        usernameField = createTextField(false, theme); // Không cho phép chỉnh sửa
        accountInfoPanel.add(usernameField, gbc(gbc, 1, row++));

        accountInfoPanel.add(createLabel("Email:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        emailField = createTextField(true, theme);
        accountInfoPanel.add(emailField, gbc(gbc, 1, row++));

        mainPanel.add(accountInfoPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Spacer

        // --- Panel Đổi mật khẩu ---
        JPanel passwordChangePanel = new JPanel(new GridBagLayout());
        passwordChangePanel.setBackground(theme.getPanelBackgroundColor());
        passwordChangePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.getBorderColor()),
                "Đổi Mật Khẩu",
                TitledBorder.LEFT, TitledBorder.TOP,
                theme.getButtonFont().deriveFont(Font.BOLD, 16f),
                theme.getPrimaryColor()
        ));

        row = 0;
        passwordChangePanel.add(createLabel("Mật khẩu cũ:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        oldPasswordField = createPasswordField(theme);
        passwordChangePanel.add(oldPasswordField, gbc(gbc, 1, row++));

        passwordChangePanel.add(createLabel("Mật khẩu mới:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        newPasswordField = createPasswordField(theme);
        passwordChangePanel.add(newPasswordField, gbc(gbc, 1, row++));

        passwordChangePanel.add(createLabel("Xác nhận mật khẩu mới:", theme), gbc(gbc, 0, row, GridBagConstraints.EAST));
        confirmPasswordField = createPasswordField(theme);
        passwordChangePanel.add(confirmPasswordField, gbc(gbc, 1, row++));

        mainPanel.add(passwordChangePanel);

        // Wrap mainPanel in a JScrollPane if content might overflow
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Make scroll smoother
        add(scrollPane, BorderLayout.CENTER);


        // --- Buttons Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center buttons with gap
        buttonPanel.setBackground(theme.getPanelBackgroundColor());

        capNhatThongTinButton = createStyledButton("Cập Nhật Thông Tin", theme);
        doiMatKhauButton = createStyledButton("Đổi Mật Khẩu", theme);

        buttonPanel.add(capNhatThongTinButton);
        buttonPanel.add(doiMatKhauButton);
        add(buttonPanel, BorderLayout.SOUTH);

        ThemeManager.getInstance().applyTheme(this);
    }

    // --- Helper methods for UI components ---
    private JLabel createLabel(String text, AppTheme theme) {
        JLabel label = new JLabel(text);
        label.setFont(theme.getDefaultFont().deriveFont(Font.BOLD, 14f));
        label.setForeground(theme.getTextColor());
        return label;
    }

    private JTextField createTextField(boolean editable, AppTheme theme) {
        JTextField field = new JTextField(25);
        field.setFont(theme.getDefaultFont().deriveFont(Font.PLAIN, 14f));
        field.setBackground(theme.getBackgroundColor());
        field.setForeground(theme.getTextColor());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setEditable(editable);
        return field;
    }

    private JPasswordField createPasswordField(AppTheme theme) {
        JPasswordField field = new JPasswordField(25);
        field.setFont(theme.getDefaultFont().deriveFont(Font.PLAIN, 14f));
        field.setBackground(theme.getBackgroundColor());
        field.setForeground(theme.getTextColor());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JComboBox<String> createComboBox(String[] items, AppTheme theme) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(theme.getDefaultFont().deriveFont(Font.PLAIN, 14f));
        comboBox.setBackground(theme.getBackgroundColor());
        comboBox.setForeground(theme.getTextColor());
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        ((JLabel)comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT); // Align text
        return comboBox;
    }

    private JButton createStyledButton(String text, AppTheme theme) {
        JButton button = new JButton(text);
        button.setFont(theme.getButtonFont().deriveFont(Font.BOLD, 16f));
        button.setBackground(theme.getPrimaryColor());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(theme.getPrimaryColor().darker(), 1));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45)); // Fixed size for consistency

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(theme.getPrimaryColor().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(theme.getPrimaryColor());
            }
        });
        return button;
    }

    // Helper for GridBagLayout
    private GridBagConstraints gbc(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = (x == 1) ? 1.0 : 0.0; // Give weight to value fields
        gbc.anchor = (x == 0) ? GridBagConstraints.EAST : GridBagConstraints.WEST; // Align labels right, fields left
        return gbc;
    }
    
    private GridBagConstraints gbc(GridBagConstraints gbc, int x, int y, int anchor) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = (x == 1) ? 1.0 : 0.0;
        gbc.anchor = anchor;
        return gbc;
    }

    // --- Methods for Controller to interact with View ---

    public void displayThongTin(KhachHang kh, NhanVien nv, TaiKhoanNguoiDung tk) {
        // Xóa tất cả các trường trước khi đổ dữ liệu mới
        clearForm();

        // Hiển thị thông tin chung từ TaiKhoanNguoiDung
        if (tk != null) {
            maNguoiDungField.setText(tk.getMaNguoiDung());
            usernameField.setText(tk.getUsername());
            emailField.setText(tk.getEmail());
            // MaNguoiDung và Username không cho phép chỉnh sửa
            maNguoiDungField.setEditable(false);
            usernameField.setEditable(false);
        } else {
            // Không có thông tin tài khoản, vô hiệu hóa tất cả các trường
            setFieldsEditable(false);
            return; // Không tiếp tục nếu không có tài khoản
        }

        // Hiển thị thông tin riêng của Khách hàng hoặc Nhân viên
        if (kh != null) {
            hoTenField.setText(kh.getHoTen());
            sdtField.setText(kh.getSdt());
            if (kh.getNgaySinh() != null) {
                ngaySinhField.setText(kh.getNgaySinh().format(DateTimeFormatter.ISO_LOCAL_DATE)); // yyyy-MM-dd
            }
            if (kh.getGioiTinh() != null) {
                gioiTinhComboBox.setSelectedItem(kh.getGioiTinh());
            }
            // Các trường của KhachHang là có thể chỉnh sửa
            hoTenField.setEditable(true);
            sdtField.setEditable(true);
            ngaySinhField.setEditable(true);
            gioiTinhComboBox.setEnabled(true);
        } else if (nv != null) {
            hoTenField.setText(nv.getHoTen());
            sdtField.setText(nv.getSdt());
            if (nv.getNgaySinh() != null) {
                ngaySinhField.setText(nv.getNgaySinh().format(DateTimeFormatter.ISO_LOCAL_DATE)); // yyyy-MM-dd
            }
            if (nv.getGioiTinh() != null) {
                gioiTinhComboBox.setSelectedItem(nv.getGioiTinh());
            }
            // Các trường của NhanVien là có thể chỉnh sửa
            hoTenField.setEditable(true);
            sdtField.setEditable(true);
            ngaySinhField.setEditable(true);
            gioiTinhComboBox.setEnabled(true);
        } else {
            // Nếu không tìm thấy thông tin Khách hàng/Nhân viên, ẩn/vô hiệu hóa các trường liên quan
            hoTenField.setEditable(false);
            sdtField.setEditable(false);
            ngaySinhField.setEditable(false);
            gioiTinhComboBox.setEnabled(false);
        }
    }
    
    private void setFieldsEditable(boolean editable) {
        maNguoiDungField.setEditable(false); // Luôn không editable
        usernameField.setEditable(false);   // Luôn không editable
        hoTenField.setEditable(editable);
        sdtField.setEditable(editable);
        ngaySinhField.setEditable(editable);
        gioiTinhComboBox.setEnabled(editable);
        emailField.setEditable(editable);
        oldPasswordField.setEditable(editable);
        newPasswordField.setEditable(editable);
        confirmPasswordField.setEditable(editable);
    }

    public void clearForm() {
        maNguoiDungField.setText("");
        hoTenField.setText("");
        sdtField.setText("");
        ngaySinhField.setText("");
        gioiTinhComboBox.setSelectedIndex(0); // Reset to default
        usernameField.setText("");
        emailField.setText("");
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
    
    public String getMaNguoiDung() { // Dùng để lấy mã người dùng hiện tại (MaKH hoặc MaNV)
        return maNguoiDungField.getText().trim();
    }

    public String getHoTen() {
        return hoTenField.getText().trim();
    }

    public String getSdt() {
        return sdtField.getText().trim();
    }
    
    public LocalDate getNgaySinh() {
        String ngaySinhText = ngaySinhField.getText().trim();
        if (ngaySinhText.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(ngaySinhText, DateTimeFormatter.ISO_LOCAL_DATE); // Expects yyyy-MM-dd
        } catch (DateTimeParseException e) {
            // Handle invalid date format
            return null;
        }
    }
    
    public String getGioiTinh() {
        return (String) gioiTinhComboBox.getSelectedItem();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getOldPassword() {
        return new String(oldPasswordField.getPassword());
    }

    public String getNewPassword() {
        return new String(newPasswordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }
    
    public void resetPasswordFields() {
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    public void addCapNhatThongTinListener(ActionListener listener) {
        capNhatThongTinButton.addActionListener(listener);
    }

    public void addDoiMatKhauListener(ActionListener listener) {
        doiMatKhauButton.addActionListener(listener);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public String getUsername() { // Thêm getter cho username (read-only)
        return usernameField.getText();
    }
}