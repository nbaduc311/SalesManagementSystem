package system.view.panels;

import javax.swing.*;
import java.awt.*;
import system.auth.AuthSession;
import system.models.entity.TaiKhoanNguoiDung;
import system.theme.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        // Không set màu ở đây, sẽ gọi applyTheme() sau
        // setBackground(new Color(240, 248, 255)); // Bỏ dòng này

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        TaiKhoanNguoiDung currentUser = AuthSession.getCurrentUser();
        String username = (currentUser != null) ? currentUser.getUsername() : "Guest";
        String role = (currentUser != null) ? currentUser.getLoaiNguoiDung() : "Unknown";

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel welcomeLabel = new JLabel("Chào mừng trở lại, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 38)); // Font sẽ được applyTheme ghi đè
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, gbc);

        gbc.gridy++;
        JLabel roleLabel = new JLabel("Vai trò của bạn: " + role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Font sẽ được applyTheme ghi đè
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(roleLabel, gbc);

        gbc.gridy++;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(300, 5));
        add(separator, gbc);

        gbc.gridy++;
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>"
                                      + "Đây là bảng điều khiển chính của bạn.<br>"
                                      + "Bạn có thể xem tổng quan về cửa hàng BLK ETTN tại đây.<br>"
                                      + "Sử dụng menu bên trái để điều hướng đến các chức năng khác."
                                      + "</div></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16)); // Font sẽ được applyTheme ghi đè
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(infoLabel, gbc);

        // --- Rất quan trọng: Gọi applyTheme sau khi thêm tất cả components ---
        ThemeManager.getInstance().applyTheme(this);
        // Sau khi applyTheme, bạn có thể ghi đè lại một số thuộc tính nếu cần thiết
        // Ví dụ: Màu nền đặc biệt cho DashboardPanel
        setBackground(ThemeManager.getInstance().getCurrentTheme().getPanelBackgroundColor()); // Hoặc màu cụ thể hơn
        welcomeLabel.setForeground(ThemeManager.getInstance().getCurrentTheme().getPrimaryColor()); // Màu đặc biệt cho tiêu đề
    }
}