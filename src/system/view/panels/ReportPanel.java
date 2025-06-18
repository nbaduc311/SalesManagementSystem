package system.view.panels;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {

    public ReportPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title ---
        JLabel title = new JLabel("Báo cáo & Thống kê");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(30, 30, 30));
        add(title, BorderLayout.NORTH);

        // --- Content Area ---
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(new Color(245, 245, 245));
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel reportLabel1 = new JLabel("<html><h2>Doanh thu tháng này: <span style='color: green;'>50,000,000 VNĐ</span></h2></html>");
        reportLabel1.setFont(new Font("Arial", Font.PLAIN, 18));
        reportLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentArea.add(reportLabel1);
        contentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel reportLabel2 = new JLabel("<html><h3>Số lượng sản phẩm bán chạy nhất: <span style='color: blue;'>Laptop Gaming (150 chiếc)</span></h3></html>");
        reportLabel2.setFont(new Font("Arial", Font.PLAIN, 16));
        reportLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentArea.add(reportLabel2);
        contentArea.add(Box.createRigidArea(new Dimension(0, 15)));

        JTextArea detailedReport = new JTextArea("Đây là nơi hiển thị các báo cáo chi tiết và biểu đồ.\n" +
                                                 "Bạn có thể tích hợp thư viện biểu đồ (ví dụ: JFreeChart) tại đây.\n" +
                                                 "Bao gồm các bộ lọc ngày, loại báo cáo, v.v.");
        detailedReport.setEditable(false);
        detailedReport.setFont(new Font("Consolas", Font.PLAIN, 14));
        detailedReport.setBackground(new Color(230, 230, 230));
        detailedReport.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        detailedReport.setAlignmentX(Component.CENTER_ALIGNMENT);
        JScrollPane scrollPane = new JScrollPane(detailedReport);
        scrollPane.setMaximumSize(new Dimension(600, 300)); // Giới hạn kích thước
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentArea.add(scrollPane);

        add(contentArea, BorderLayout.CENTER);

        // Button để xuất báo cáo
        JButton exportButton = new JButton("Xuất báo cáo (PDF/Excel)");
        exportButton.setFont(new Font("Arial", Font.BOLD, 16));
        exportButton.setBackground(new Color(70, 130, 180)); // SteelBlue
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        exportButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(new Color(255, 255, 255));
        southPanel.add(exportButton);
        add(southPanel, BorderLayout.SOUTH);

        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng xuất báo cáo sẽ được phát triển tại đây!"));
    }
}