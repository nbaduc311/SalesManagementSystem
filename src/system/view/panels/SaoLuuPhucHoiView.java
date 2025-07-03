package system.view.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class SaoLuuPhucHoiView extends JPanel { // Hoặc JFrame nếu bạn muốn nó là cửa sổ riêng
    private JButton btnSaoLuu;
    private JButton btnPhucHoi;
    private JTextField txtBackupLocation;
    private JButton btnBrowseBackup;
    private JTextField txtRestoreFile;
    private JButton btnBrowseRestore;
    private JLabel lblMessage; // Để hiển thị thông báo lỗi/thành công
    private JComboBox<String> cbBackupType; // Ví dụ: Full, Incremental
    private String maNguoiDungHienTai; // Để lưu thông tin người dùng đang đăng nhập

    public SaoLuuPhucHoiView() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        btnSaoLuu = new JButton("Sao Lưu Dữ Liệu");
        btnPhucHoi = new JButton("Phục Hồi Dữ Liệu");
        txtBackupLocation = new JTextField(30);
        btnBrowseBackup = new JButton("Duyệt...");
        txtRestoreFile = new JTextField(30);
        btnBrowseRestore = new JButton("Duyệt...");
        lblMessage = new JLabel("Sẵn sàng.");
        lblMessage.setForeground(Color.BLUE);

        cbBackupType = new JComboBox<>(new String[]{"Full Backup", "Differential Backup"}); // Tùy chọn kiểu sao lưu
        txtBackupLocation.setText(System.getProperty("user.home") + File.separator + "database_backup"); // Mặc định đường dẫn
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Backup section
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Kiểu Sao Lưu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(cbBackupType, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Đường dẫn Sao Lưu:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(txtBackupLocation, gbc);
        gbc.gridx = 2; gbc.gridy = 1;
        add(btnBrowseBackup, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(btnSaoLuu, gbc);
        gbc.gridwidth = 1; // Reset

        // Separator
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 3;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1; // Reset

        // Restore section
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("File Phục Hồi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        add(txtRestoreFile, gbc);
        gbc.gridx = 2; gbc.gridy = 4;
        add(btnBrowseRestore, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(btnPhucHoi, gbc);
        gbc.gridwidth = 1; // Reset

        // Message label
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblMessage, gbc);
    }

    // --- Getters for UI Components to add Listeners in Controller ---
    public JButton getBtnSaoLuu() {
        return btnSaoLuu;
    }

    public JButton getBtnPhucHoi() {
        return btnPhucHoi;
    }

    public JTextField getTxtBackupLocation() {
        return txtBackupLocation;
    }

    public JButton getBtnBrowseBackup() {
        return btnBrowseBackup;
    }

    public JTextField getTxtRestoreFile() {
        return txtRestoreFile;
    }

    public JButton getBtnBrowseRestore() {
        return btnBrowseRestore;
    }

    public JComboBox<String> getCbBackupType() {
        return cbBackupType;
    }

    // --- Methods to interact with the UI from Controller ---
    public void displayMessage(String message, boolean isError) {
        lblMessage.setText(message);
        lblMessage.setForeground(isError ? Color.RED : Color.BLUE);
    }

    public String getMaNguoiDungHienTai() {
        return maNguoiDungHienTai;
    }

    public void setMaNguoiDungHienTai(String maNguoiDungHienTai) {
        this.maNguoiDungHienTai = maNguoiDungHienTai;
    }
    
    // Phương thức để mở dialog chọn folder/file
    public File showFileChooser(boolean isDirectory, String dialogTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"))); // Mặc định thư mục người dùng

        if (isDirectory) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false); // Only allow specific file types
            // Optionally, add a filter for .bak files for restore
            // fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("SQL Backup Files (*.bak)", "bak"));
        }

        int userSelection = fileChooser.showOpenDialog(this); // 'this' refers to JPanel, good for parent component
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    // Thêm các phương thức lắng nghe (Listener) cho Controller gọi
    public void addSaoLuuListener(ActionListener listener) {
        btnSaoLuu.addActionListener(listener);
    }

    public void addPhucHoiListener(ActionListener listener) {
        btnPhucHoi.addActionListener(listener);
    }

    public void addBrowseBackupListener(ActionListener listener) {
        btnBrowseBackup.addActionListener(listener);
    }

    public void addBrowseRestoreListener(ActionListener listener) {
        btnBrowseRestore.addActionListener(listener);
    }
}