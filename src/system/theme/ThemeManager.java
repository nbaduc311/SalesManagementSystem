package system.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Lớp quản lý chủ đề hiện tại của ứng dụng.
 * Sử dụng Singleton pattern để đảm bảo chỉ có một instance duy nhất của ThemeManager.
 * Điều này cho phép các phần khác của ứng dụng dễ dàng truy cập và thay đổi chủ đề.
 */
public class ThemeManager {
    private static ThemeManager instance;
    private AppTheme currentTheme;
    private PropertyChangeSupport support; // Using PropertyChangeSupport

    /**
     * Constructor riêng tư để triển khai Singleton pattern.
     * Khởi tạo chủ đề mặc định là LightTheme khi ThemeManager được tạo lần đầu.
     */
    private ThemeManager() {
        this.currentTheme = new LightTheme(); 
        this.support = new PropertyChangeSupport(this); // Initialize PropertyChangeSupport
    }

    /**
     * Trả về instance duy nhất của ThemeManager.
     * Nếu instance chưa tồn tại, nó sẽ được tạo mới.
     * @return Instance của ThemeManager.
     */
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Trả về chủ đề hiện tại đang được sử dụng bởi ứng dụng.
     * @return Đối tượng AppTheme hiện tại.
     */
    public AppTheme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Thiết lập chủ đề mới cho ứng dụng.
     * Phương thức này sẽ thông báo cho tất cả các listener rằng chủ đề đã thay đổi.
     * @param newTheme Chủ đề mới muốn áp dụng (một thể hiện của LightTheme hoặc DarkTheme).
     */
    public void setTheme(AppTheme newTheme) {
        // Only change and notify if the theme is actually different
        // We use object reference comparison here. If you want a deeper comparison,
        // you might need to override equals() in AppTheme and its subclasses.
        if (this.currentTheme != newTheme) { 
            AppTheme oldTheme = this.currentTheme; // Lưu trữ theme cũ
            this.currentTheme = newTheme;
            System.out.println("Chủ đề đã được thay đổi thành: " + newTheme.getClass().getSimpleName());
            
            // Thông báo cho tất cả các listener rằng theme đã thay đổi
            // Chú ý: Fire property change on the Event Dispatch Thread (EDT)
            // This is crucial for Swing applications to avoid threading issues.
            SwingUtilities.invokeLater(() -> {
                support.firePropertyChange("currentTheme", oldTheme, newTheme); 
            });
        }
    }

    /**
     * Thêm một PropertyChangeListener để lắng nghe sự kiện thay đổi theme.
     * @param listener Listener muốn thêm.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Xóa một PropertyChangeListener.
     * @param listener Listener muốn xóa.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * Áp dụng theme hiện tại cho một Component và tất cả các Component con của nó một cách đệ quy.
     * Phương thức này cố gắng đặt các thuộc tính màu sắc và font chữ dựa trên theme hiện tại.
     * Cần được gọi khi một panel/frame mới được hiển thị hoặc khi theme được chuyển đổi.
     *
     * @param component Component gốc (ví dụ: JPanel, JFrame) để bắt đầu áp dụng theme.
     */
    public void applyTheme(Component component) {
        if (component == null) {
            return;
        }

        AppTheme theme = getCurrentTheme(); // Lấy theme hiện tại

        // Áp dụng cho JPanel
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            if (panel.isOpaque() || (panel.getName() != null && panel.getName().equals("mainContentPanel"))) {
                panel.setBackground(theme.getPanelBackgroundColor());
            }

            Border border = panel.getBorder();
            if (border instanceof TitledBorder) {
                TitledBorder titledBorder = (TitledBorder) border;
                titledBorder.setTitleColor(theme.getForegroundColor()); 
                titledBorder.setBorder(BorderFactory.createLineBorder(theme.getBorderColor())); 
            } else if (border != null && !(border instanceof javax.swing.border.EmptyBorder)) {
                if (border instanceof javax.swing.border.LineBorder) {
                    panel.setBorder(BorderFactory.createLineBorder(theme.getBorderColor(), ((javax.swing.border.LineBorder) border).getThickness()));
                }
            }
        }
        // Áp dụng cho JLabel
        else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            if (label.getName() != null && label.getName().equals("menuTitleLabel")) {
                label.setForeground(theme.getTitleColor());
                label.setFont(theme.getTitleFont());
            } else {
                label.setForeground(theme.getTextColor());
                label.setFont(theme.getDefaultFont()); 
            }
        }
        // Áp dụng cho JButton
        else if (component instanceof JButton) {
            JButton button = (JButton) component;
            if (button.getName() != null && button.getName().startsWith("menuButton_")) {
                button.setBackground(theme.getMenuButtonColor());
                button.setForeground(theme.getMenuButtonForegroundColor());
            } else {
                button.setBackground(theme.getButtonBackgroundColor()); 
                button.setForeground(theme.getButtonForegroundColor()); 
            }
            button.setFont(theme.getButtonFont());
        }
        // Áp dụng cho JToggleButton
        else if (component instanceof JToggleButton) {
            JToggleButton toggleButton = (JToggleButton) component;
            toggleButton.setBackground(theme.getButtonBackgroundColor());
            toggleButton.setForeground(theme.getButtonForegroundColor());
            toggleButton.setFont(theme.getButtonFont());
        }
        // Áp dụng cho JTextField, JPasswordField, JTextArea
        else if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setBackground(theme.getTextFieldBackgroundColor());
            field.setForeground(theme.getTextFieldForegroundColor());
            field.setCaretColor(theme.getTextFieldForegroundColor()); 
            field.setFont(theme.getDefaultFont());
            field.setBorder(BorderFactory.createLineBorder(theme.getBorderColor()));
        }
        else if (component instanceof JTextArea) {
            JTextArea textArea = (JTextArea) component;
            textArea.setBackground(theme.getTextFieldBackgroundColor());
            textArea.setForeground(theme.getTextFieldForegroundColor());
            textArea.setCaretColor(theme.getTextFieldForegroundColor());
            textArea.setFont(theme.getDefaultFont());
            textArea.setBorder(BorderFactory.createLineBorder(theme.getBorderColor())); 
        }
        // Áp dụng cho JCheckBox, JRadioButton
        else if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            checkBox.setForeground(theme.getTextColor());
            checkBox.setFont(theme.getDefaultFont());
        }
        else if (component instanceof JRadioButton) {
            JRadioButton radioButton = (JRadioButton) component;
            radioButton.setForeground(theme.getTextColor());
            radioButton.setFont(theme.getDefaultFont());
        }
        // Áp dụng cho JComboBox
        else if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.setBackground(theme.getTextFieldBackgroundColor());
            comboBox.setForeground(theme.getTextFieldForegroundColor());
            comboBox.setFont(theme.getDefaultFont());
        }
        // Áp dụng cho JTable
        else if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(theme.getPanelBackgroundColor()); 
            table.setForeground(theme.getTextColor());     
            table.setGridColor(theme.getBorderColor());        
            table.setFont(theme.getDefaultFont());

            table.getTableHeader().setBackground(theme.getMenuBackgroundColor()); 
            table.getTableHeader().setForeground(theme.getMenuButtonForegroundColor()); 
            table.getTableHeader().setFont(theme.getDefaultFont().deriveFont(Font.BOLD)); 

            table.setSelectionBackground(theme.getAccentColor());
            table.setSelectionForeground(Color.WHITE);
        }
        // Áp dụng cho JScrollPane
        else if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            scrollPane.getViewport().setBackground(theme.getPanelBackgroundColor());
            scrollPane.setBorder(BorderFactory.createLineBorder(theme.getBorderColor()));
        }
        // Áp dụng cho JSeparator
        else if (component instanceof JSeparator) {
            JSeparator separator = (JSeparator) component;
            separator.setForeground(theme.getBorderColor()); 
            separator.setBackground(theme.getBorderColor()); 
        }
        // Áp dụng cho các component có khả năng chứa các component khác (Containers)
        if (component instanceof Container) {
            for (Component comp : ((Container) component).getComponents()) {
                applyTheme(comp); // Gọi đệ quy cho các component con
            }
        }
    }
}