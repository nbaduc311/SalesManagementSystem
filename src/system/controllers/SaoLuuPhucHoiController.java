package system.controllers;

import system.database.DatabaseConnection; // Để lấy kết nối CSDL
import system.models.entity.SaoLuu;
import system.models.entity.PhucHoi;
import system.services.SaoLuuService;
import system.services.PhucHoiService;
import system.view.panels.SaoLuuPhucHoiView; // Import View mà chúng ta vừa định nghĩa

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaoLuuPhucHoiController {

    private SaoLuuPhucHoiView view;
    private SaoLuuService saoLuuService;
    private PhucHoiService phucHoiService;

    // TODO: Cần truyền mã người dùng hiện tại từ phiên đăng nhập vào đây
    private String maNguoiDungHienTai; 

    public SaoLuuPhucHoiController(SaoLuuPhucHoiView view, SaoLuuService saoLuuService, PhucHoiService phucHoiService) {
        this.view = view;
        this.saoLuuService = saoLuuService;
        this.phucHoiService = phucHoiService;
        
        // TODO: Đặt mã người dùng hiện tại, ví dụ: AuthSession.getLoggedInUser().getMaNguoiDung();
        // Giả sử mã người dùng được set từ lớp gọi controller này (MainApplicationFrame)
        // Nếu SaoLuuPhucHoiView có setter cho maNguoiDungHienTai, bạn có thể gọi:
        // this.maNguoiDungHienTai = someAuthService.getLoggedInUserMa(); 
        // view.setMaNguoiDungHienTai(this.maNguoiDungHienTai); 
        // For now, let's just use a dummy value or ensure it's passed from the main app.
        // I'll make a placeholder for now.
        this.maNguoiDungHienTai = "NV001"; // <<< Cần thay thế bằng mã người dùng thực tế

        initListeners();
    }

    private void initListeners() {
        view.addSaoLuuListener(new SaoLuuActionListener());
        view.addPhucHoiListener(new PhucHoiActionListener());
        view.addBrowseBackupListener(new BrowseBackupActionListener());
        view.addBrowseRestoreListener(new BrowseRestoreActionListener());
    }

    // --- Action Listeners Implementations ---

    private class SaoLuuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String backupLocation = view.getTxtBackupLocation().getText().trim();
            String backupType = (String) view.getCbBackupType().getSelectedItem();

            if (backupLocation.isEmpty()) {
                view.displayMessage("Vui lòng chọn hoặc nhập đường dẫn sao lưu.", true);
                return;
            }

            File backupDir = new File(backupLocation);
            if (!backupDir.exists()) {
                boolean created = backupDir.mkdirs(); // Tạo thư mục nếu chưa tồn tại
                if (!created) {
                    view.displayMessage("Không thể tạo thư mục sao lưu: " + backupLocation, true);
                    return;
                }
            } else if (!backupDir.isDirectory()) {
                view.displayMessage("Đường dẫn sao lưu phải là một thư mục.", true);
                return;
            }

            // Tạo tên file backup với timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "QL_CuaHang_DB_" + timestamp + ".bak";
            String fullPath = backupLocation + File.separator + fileName;

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có muốn sao lưu cơ sở dữ liệu tới:\n" + fullPath + "\n\n" +
                            "Lưu ý: Ứng dụng sẽ tạm thời mất kết nối đến CSDL trong quá trình sao lưu.",
                    "Xác nhận Sao Lưu", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Connection conn = null;
                try {
                    conn = DatabaseConnection.getConnection();
                    // Thực hiện lệnh sao lưu SQL Server
                    // LƯU Ý: Lệnh BACKUP DATABASE cần quyền sysadmin hoặc db_owner.
                    // Nếu bạn dùng tài khoản 'sa', nó có quyền này.
                    String sqlBackupCommand = String.format(
                            "BACKUP DATABASE BLK TO DISK = '%s' WITH NOFORMAT, NOINIT, NAME = N'BLK-Full Database Backup', SKIP, NOREWIND, NOUNLOAD, STATS = 10",
                            fullPath.replace("\\", "\\\\") // Escape backslashes for SQL string
                    );
                    
                    // Thực thi lệnh SQL
                    conn.createStatement().execute(sqlBackupCommand);

                    // Ghi lại thông tin sao lưu vào bảng SaoLuu
                    SaoLuu saoLuu = new SaoLuu(backupType, maNguoiDungHienTai, fullPath);
                    saoLuuService.addSaoLuu(conn, saoLuu);

                    view.displayMessage("Sao lưu dữ liệu thành công tới: " + fullPath, false);

                } catch (SQLException ex) {
                    view.displayMessage("Lỗi khi sao lưu dữ liệu: " + ex.getMessage(), true);
                    ex.printStackTrace();
                } finally {
                    DatabaseConnection.closeConnection(conn); // Đảm bảo đóng kết nối
                }
            }
        }
    }

    private class PhucHoiActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String restoreFilePath = view.getTxtRestoreFile().getText().trim();

            if (restoreFilePath.isEmpty()) {
                view.displayMessage("Vui lòng chọn file phục hồi.", true);
                return;
            }

            File restoreFile = new File(restoreFilePath);
            if (!restoreFile.exists() || !restoreFile.isFile()) {
                view.displayMessage("File phục hồi không tồn tại hoặc không hợp lệ.", true);
                return;
            }
            if (!restoreFile.getName().toLowerCase().endsWith(".bak")) {
                view.displayMessage("File phục hồi phải có định dạng .bak", true);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view,
                    "Bạn có muốn phục hồi cơ sở dữ liệu từ:\n" + restoreFilePath + "\n\n" +
                            "Cảnh báo: Việc này sẽ GHI ĐÈ toàn bộ dữ liệu hiện tại của cơ sở dữ liệu BLK.\n" +
                            "Ứng dụng sẽ tạm thời mất kết nối đến CSDL trong quá trình phục hồi.\n" +
                            "Bạn có chắc chắn muốn tiếp tục?",
                    "Xác nhận Phục Hồi Dữ Liệu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                Connection conn = null;
                try {
                    // Để phục hồi database, bạn cần đảm bảo không có kết nối nào đang active đến database đó.
                    // Một cách thông thường là đưa database vào chế độ SINGLE_USER trước khi RESTORE.
                    // Hoặc đơn giản hơn là đóng tất cả các kết nối trước đó (bao gồm kết nối của ứng dụng).
                    // Việc này đòi hỏi quyền sysadmin hoặc db_owner.

                    // Đóng kết nối hiện tại của ứng dụng (nếu có)
                    // Lưu ý: DatabaseConnection.getConnection() tạo mới, nên không cần đóng trực tiếp.
                    // Quan trọng là không có kết nối nào khác từ ứng dụng hoặc công cụ khác đến DB BLK.
                    // Lệnh ALTER DATABASE sẽ làm điều đó.

                    conn = DatabaseConnection.getConnection(); // Lấy một kết nối mới để thực hiện lệnh
                    
                    // Đặt database vào chế độ single-user để đảm bảo không có kết nối nào khác
                    conn.createStatement().execute("ALTER DATABASE BLK SET SINGLE_USER WITH ROLLBACK IMMEDIATE");

                    // Thực hiện lệnh phục hồi SQL Server
                    String sqlRestoreCommand = String.format(
                            "RESTORE DATABASE BLK FROM DISK = '%s' WITH FILE = 1, NOUNLOAD, REPLACE, STATS = 10",
                            restoreFilePath.replace("\\", "\\\\") // Escape backslashes for SQL string
                    );
                    
                    // Thực thi lệnh SQL
                    conn.createStatement().execute(sqlRestoreCommand);

                    // Đưa database trở lại chế độ multi-user
                    conn.createStatement().execute("ALTER DATABASE BLK SET MULTI_USER");

                    // Ghi lại thông tin phục hồi vào bảng PhucHoi
                    PhucHoi phucHoi = new PhucHoi("Full Restore", maNguoiDungHienTai, null); // MaSaoLuu có thể null nếu không link trực tiếp
                    // Hoặc bạn có thể tìm maSaoLuu gần nhất từ fullPath nếu muốn
                    // For now, setting maSaoLuu to null or based on a selection in the view
                    // Nếu bạn có một JTable liệt kê các bản sao lưu để chọn phục hồi, thì lấy maSaoLuu từ đó.
                    phucHoiService.addPhucHoi(conn, phucHoi);

                    view.displayMessage("Phục hồi dữ liệu thành công từ: " + restoreFilePath, false);

                } catch (SQLException ex) {
                    view.displayMessage("Lỗi khi phục hồi dữ liệu: " + ex.getMessage() + "\n" +
                                        "Đảm bảo không có ứng dụng nào khác đang kết nối đến CSDL BLK.", true);
                    ex.printStackTrace();
                } finally {
                    if (conn != null) {
                        try {
                            // Đảm bảo đưa database về MULTI_USER ngay cả khi có lỗi
                            conn.createStatement().execute("ALTER DATABASE BLK SET MULTI_USER");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        DatabaseConnection.closeConnection(conn); // Đảm bảo đóng kết nối
                    }
                }
            }
        }
    }

    private class BrowseBackupActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            File selectedDirectory = view.showFileChooser(true, "Chọn thư mục lưu trữ sao lưu");
            if (selectedDirectory != null) {
                view.getTxtBackupLocation().setText(selectedDirectory.getAbsolutePath());
            }
        }
    }

    private class BrowseRestoreActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            File selectedFile = view.showFileChooser(false, "Chọn file phục hồi (.bak)");
            if (selectedFile != null) {
                view.getTxtRestoreFile().setText(selectedFile.getAbsolutePath());
            }
        }
    }
}