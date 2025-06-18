package system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {
    public static void main(String[] args) {
        // Tạo cửa sổ chính
        JFrame frame = new JFrame("Click vào ảnh");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Tải ảnh từ thư mục assets (đặt trong /src/assets/logo.png)
        ImageIcon icon = new ImageIcon("src/assets/logo.png");
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Bắt sự kiện click chuột vào ảnh
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(frame, "Bạn đã click vào ảnh!");
            }
        });

        // Thêm ảnh vào frame
        frame.add(imageLabel, BorderLayout.CENTER);

        // Hiển thị giao diện
        frame.setVisible(true);
    }
}
