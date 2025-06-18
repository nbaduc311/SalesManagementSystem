package system.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import system.utils.ImageUtils;



public class SlideShowPanel extends JPanel {
    private String[] imagePaths;
    private JLabel imageLabel;
    private int index = 0;
    private Timer timer;
    private int width, height, radius;

    public SlideShowPanel(String[] imagePaths, int width, int height, int radius, int delayMillis) {
        this.imagePaths = imagePaths;
        this.width = width;
        this.height = height;
        this.radius = radius;

        setLayout(new BorderLayout());
        setOpaque(false);

        // Ảnh đầu tiên
        ImageIcon icon = ImageUtils.getRoundedImageIcon(imagePaths[0], width, height, radius);
        imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Timer để chuyển ảnh
        timer = new Timer(delayMillis, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                index = (index + 1) % imagePaths.length;
                ImageIcon newIcon = ImageUtils.getRoundedImageIcon(imagePaths[index], width, height, radius);
                imageLabel.setIcon(newIcon);
            }
        });
        timer.start();
    }
}
