package system.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import system.theme.AppTheme;
import system.theme.ThemeManager;
import system.theme.DarkTheme;
import system.theme.LightTheme;

public class ThemeToggleButton extends JPanel implements PropertyChangeListener {

    private boolean isLightMode; // true for Light, false for Dark
    private final int TOGGLE_WIDTH = 50;
    private final int TOGGLE_HEIGHT = 26;
    private final int CIRCLE_RADIUS = 10;
    // Đã bỏ biến labelText vì không còn cần hiển thị "Theme " nữa

    private List<ActionListener> actionListeners = new ArrayList<>();

    public ThemeToggleButton() {
        // Xác định trạng thái ban đầu dựa trên theme hiện tại
        isLightMode = ThemeManager.getInstance().getCurrentTheme() instanceof LightTheme;
        setPreferredSize(new Dimension(150, TOGGLE_HEIGHT + 10)); // Giữ kích thước panel đủ rộng
        setOpaque(false); // Đặt nền trong suốt

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // --- THAY ĐỔI: CHỈ KÍCH HOẠT KHI NHẤP VÀO VÙNG NÚT GẠT ---
                int leftPadding = 10; // Phải khớp với giá trị trong paintComponent
                int gapBetweenTextAndToggle = 8; // Phải khớp với giá trị trong paintComponent

                String currentText = (isLightMode ? "Light" : "Dark");
                FontMetrics fm = getFontMetrics(getFont());
                int textWidth = fm.stringWidth(currentText);

                int textX = leftPadding;
                int toggleX = textX + textWidth + gapBetweenTextAndToggle; // Tính toán lại toggleX để khớp

                int toggleY = (getHeight() - TOGGLE_HEIGHT) / 2;

                // Tạo một hình chữ nhật biểu thị vùng nút gạt
                Rectangle toggleBounds = new Rectangle(toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT);

                // Kiểm tra xem chuột có nhấp vào trong vùng nút gạt không
                if (isEnabled() && toggleBounds.contains(e.getPoint())) {
                    toggleTheme();
                    fireActionPerformed();
                }
                // --- KẾT THÚC THAY ĐỔI ---
            }
        });

        ThemeManager.getInstance().addPropertyChangeListener(this);
        // Cập nhật giao diện ban đầu
        updateAppearance(ThemeManager.getInstance().getCurrentTheme());
    }

    // Triển khai phương thức propertyChange từ giao diện PropertyChangeListener
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Chúng ta chỉ quan tâm đến thay đổi thuộc tính "currentTheme"
        if ("currentTheme".equals(evt.getPropertyName())) {
            AppTheme newTheme = (AppTheme) evt.getNewValue();
            updateAppearance(newTheme);
        }
    }

    private void updateAppearance(AppTheme theme) {
        isLightMode = theme instanceof LightTheme;
        repaint(); // Yêu cầu vẽ lại component để áp dụng theme mới
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    private void fireActionPerformed() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "themeToggle");
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }

    private void toggleTheme() {
        if (isLightMode) {
            ThemeManager.getInstance().setTheme(new DarkTheme());
        } else {
            ThemeManager.getInstance().setTheme(new LightTheme());
        }
        // Không cần cập nhật isLightMode trực tiếp ở đây; propertyChange sẽ được gọi bởi ThemeManager
        // sau đó sẽ gọi updateAppearance.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();

        FontMetrics fm = g2d.getFontMetrics(getFont()); 
        
        // --- CÁC THAY ĐỔI ĐỂ GIẢM KHOẢNG CÁCH GIỮA CHỮ VÀ NÚT GẠT ---
        int leftPadding = 10; // Khoảng cách từ lề trái của panel đến chữ "Light/Dark"
        int gapBetweenTextAndToggle = 8; // Khoảng cách giữa chữ và nút gạt (điều chỉnh giá trị này để thay đổi khoảng cách)

        String currentText = (isLightMode ? "Light" : "Dark");
        int textWidth = fm.stringWidth(currentText);

        // Vị trí X của chữ: bắt đầu từ khoảng đệm bên trái
        int textX = leftPadding; 
        // Vị trí Y của chữ: đã được dịch xuống một chút (như yêu cầu trước)
        int textY = getHeight() / 2 + fm.getAscent() / 2 + 5; 

        // Vị trí X của nút gạt: nằm ngay sau chữ, cộng với khoảng cách mong muốn
        int toggleX = textX + textWidth + gapBetweenTextAndToggle + 10; 
        // Vị trí Y của nút gạt: căn giữa theo chiều dọc
        int toggleY = (getHeight() - TOGGLE_HEIGHT) / 2 + 4; 
        // --- KẾT THÚC CÁC THAY ĐỔI VỀ VỊ TRÍ NẰM NGANG ---


        // Vẽ chữ "Light" hoặc "Dark"
        g2d.setFont(theme.getButtonFont().deriveFont(Font.BOLD, 14f)); 
        g2d.setColor(isEnabled() ? theme.getMenuButtonForegroundColor() : theme.getMenuButtonForegroundColor().darker()); 
        g2d.drawString(currentText, textX, textY); // Chỉ vẽ "Light" hoặc "Dark"

        // Vẽ đường ray của nút gạt
        Color trackColor = isEnabled() ? theme.getAccentColor() : theme.getAccentColor().darker(); 
        g2d.setColor(trackColor); 
        g2d.fillRoundRect(toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT, TOGGLE_HEIGHT); 

        // Vẽ hình tròn của nút gạt
        int circleX; 
        Color circleColor; 

        if (isLightMode) { // Chế độ Light (hình tròn ở bên phải)
            circleX = toggleX + TOGGLE_WIDTH - CIRCLE_RADIUS * 2 - (TOGGLE_HEIGHT - CIRCLE_RADIUS * 2) / 2; 
            circleColor = isEnabled() ? theme.getButtonForegroundColor() : theme.getButtonForegroundColor().darker();  
        } else { // Chế độ Dark (hình tròn ở bên trái)
            circleX = toggleX + (TOGGLE_HEIGHT - CIRCLE_RADIUS * 2) / 2; 
            circleColor = isEnabled() ? theme.getButtonForegroundColor() : theme.getButtonForegroundColor().darker(); 
        } 

        g2d.setColor(circleColor); 
        g2d.fillOval(circleX, toggleY + (TOGGLE_HEIGHT - CIRCLE_RADIUS * 2) / 2, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2); 

        // Vẽ viền xung quanh đường ray của nút gạt
        g2d.setColor(isEnabled() ? theme.getBorderColor() : theme.getBorderColor().darker()); 
        g2d.setStroke(new BasicStroke(1)); 
        g2d.drawRoundRect(toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, TOGGLE_HEIGHT, TOGGLE_HEIGHT); 

        g2d.dispose(); 
    }

    @Override 
    public void setEnabled(boolean enabled) { 
        super.setEnabled(enabled); 
        setCursor(enabled ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor()); 
        repaint(); // Yêu cầu vẽ lại để hiển thị trạng thái bật/tắt
    }
}