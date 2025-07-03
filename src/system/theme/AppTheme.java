package system.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Lớp trừu tượng định nghĩa cấu trúc cơ bản cho một chủ đề ứng dụng.
 * Các chủ đề cụ thể (như LightTheme, DarkTheme) sẽ kế thừa lớp này
 * và cung cấp các giá trị màu sắc và font chữ cụ thể.
 */
public abstract class AppTheme {

    // Màu sắc chung
    private Color primaryColor;
    private Color secondaryColor;
    private Color backgroundColor;
    private Color panelBackgroundColor;
    private Color tableAlternateRowColor;
    private Color borderColor;
    private Color accentColor; // Màu nhấn, dùng cho highlight, selection
    private Color scrollColor; // Thêm thuộc tính mới cho màu thanh cuộn

    // Màu sắc cho Menu
    private Color menuBackgroundColor;
    private Color menuButtonColor;
    private Color menuButtonHoverColor;
    private Color menuButtonPressColor;
    private Color menuButtonForegroundColor;

    // Màu sắc cho Button chung
    private Color buttonBackgroundColor;
    private Color buttonForegroundColor;
    private Color buttonHoverColor;

    // Màu sắc cho Text & Title
    private Color titleColor;
    private Color textColor;
    private Color foregroundColor; 

    // Màu sắc cho TextField/TextArea/ComboBox
    private Color textFieldBackgroundColor;
    private Color textFieldForegroundColor;

    // Fonts
    private Font defaultFont;
    private Font titleFont;
    private Font buttonFont;

    // --- Getters và Setters cho tất cả các thuộc tính ---

    // Màu sắc chung
    public Color getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(Color primaryColor) { this.primaryColor = primaryColor; }

    public Color getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(Color secondaryColor) { this.secondaryColor = secondaryColor; }

    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }

    public Color getPanelBackgroundColor() { return panelBackgroundColor; }
    public void setPanelBackgroundColor(Color panelBackgroundColor) { this.panelBackgroundColor = panelBackgroundColor; }

    public Color getTableAlternateRowColor() { return tableAlternateRowColor; }
    public void setTableAlternateRowColor(Color tableAlternateRowColor) { this.tableAlternateRowColor = tableAlternateRowColor; }

    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }

    public Color getAccentColor() { return accentColor; }
    public void setAccentColor(Color accentColor) { this.accentColor = accentColor; }

    public Color getScrollColor() { return scrollColor; } // Getter cho màu thanh cuộn
    public void setScrollColor(Color scrollColor) { this.scrollColor = scrollColor; } // Setter cho màu thanh cuộn

    // Màu sắc cho Menu
    public Color getMenuBackgroundColor() { return menuBackgroundColor; }
    public void setMenuBackgroundColor(Color menuBackgroundColor) { this.menuBackgroundColor = menuBackgroundColor; }

    public Color getMenuButtonColor() { return menuButtonColor; }
    public void setMenuButtonColor(Color menuButtonColor) { this.menuButtonColor = menuButtonColor; }

    public Color getMenuButtonHoverColor() { return menuButtonHoverColor; }
    public void setMenuButtonHoverColor(Color menuButtonHoverColor) { this.menuButtonHoverColor = menuButtonHoverColor; }
    
    public Color getMenuButtonPressColor() { return menuButtonPressColor; }
    public void setMenuButtonPressColor(Color menuButtonPressColor) { this.menuButtonPressColor = menuButtonPressColor; }
    
    public Color getMenuButtonForegroundColor() { return menuButtonForegroundColor; }
    public void setMenuButtonForegroundColor(Color menuButtonForegroundColor) { this.menuButtonForegroundColor = menuButtonForegroundColor; }

    // Màu sắc cho Button chung
    public Color getButtonBackgroundColor() { return buttonBackgroundColor; } 
    public void setButtonBackgroundColor(Color buttonBackgroundColor) { this.buttonBackgroundColor = buttonBackgroundColor; }

    public Color getButtonForegroundColor() { return buttonForegroundColor; } 
    public void setButtonForegroundColor(Color buttonForegroundColor) { this.buttonForegroundColor = buttonForegroundColor; }

    public Color getButtonHoverColor() { return buttonHoverColor; }
    public void setButtonHoverColor(Color buttonHoverColor) { this.buttonHoverColor = buttonHoverColor; }

    // Màu sắc cho Text & Title
    public Color getTitleColor() { return titleColor; } 
    public void setTitleColor(Color titleColor) { this.titleColor = titleColor; }

    public Color getTextColor() { return textColor; } 
    public void setTextColor(Color textColor) { this.textColor = textColor; }
    
    public Color getForegroundColor() { return foregroundColor; } 
    public void setForegroundColor(Color foregroundColor) { this.foregroundColor = foregroundColor; }

    // Màu sắc cho TextField/TextArea/ComboBox
    public Color getTextFieldBackgroundColor() { return textFieldBackgroundColor; }
    public void setTextFieldBackgroundColor(Color textFieldBackgroundColor) { this.textFieldBackgroundColor = textFieldBackgroundColor; }

    public Color getTextFieldForegroundColor() { return textFieldForegroundColor; }
    public void setTextFieldForegroundColor(Color textFieldForegroundColor) { this.textFieldForegroundColor = textFieldForegroundColor; }


    // Fonts
    public Font getDefaultFont() { return defaultFont; }
    public void setDefaultFont(Font defaultFont) { this.defaultFont = defaultFont; }

    public Font getTitleFont() { return titleFont; }
    public void setTitleFont(Font titleFont) { this.titleFont = titleFont; }

    public Font getButtonFont() { return buttonFont; }
    public void setButtonFont(Font buttonFont) { this.buttonFont = buttonFont; }
}