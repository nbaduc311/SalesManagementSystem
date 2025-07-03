// system/components/CustomMenuButton.java
package system.components;

import system.theme.AppTheme;
import system.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D; // Import RoundRectangle2D

public class CustomMenuButton extends JButton {

    private boolean isSelected = false; // <-- Thêm thuộc tính này

    public CustomMenuButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    // <-- Getter và Setter cho isSelected -->
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        if (this.isSelected != selected) {
            this.isSelected = selected;
            repaint(); // Vẽ lại nút khi trạng thái thay đổi
        }
    }
    // <-- Hết Getter và Setter -->


    @Override
    protected void paintComponent(Graphics g) {
        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        ButtonModel model = getModel();
        Color currentColor;

        if (model.isPressed()) {
            currentColor = theme.getMenuButtonPressColor();
        } else if (model.isRollover()) {
            currentColor = theme.getMenuButtonHoverColor();
        } else {
            currentColor = theme.getMenuButtonColor();
        }

        // Vẽ nền cho nút
        g2.setColor(currentColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // <-- Vẽ border khi nút được chọn hoặc đang ở trạng thái nhấn -->
        if (isSelected || model.isPressed()) {
            g2.setColor(theme.getMenuButtonPressColor()); // Chọn màu cho border, ví dụ màu accent
            // Tùy chọn: Vẽ border bo tròn (nếu nút bạn có góc bo tròn)
            // g2.setStroke(new BasicStroke(2)); // Độ dày của border
            // g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 10, 10)); // Ví dụ bo tròn 10px
            
            // Vẽ border hình chữ nhật thông thường
            g2.setStroke(new BasicStroke(3)); // Độ dày của border (2 pixels)
            g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2); // Vẽ border vào trong 1 pixel để tránh bị cắt
        }
        // <-- Hết phần vẽ border -->

        super.paintComponent(g2);
        g2.dispose();
    }
}