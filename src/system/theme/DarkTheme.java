package system.theme;

import java.awt.Color;
import java.awt.Font;

public class DarkTheme extends AppTheme {
    public DarkTheme() {
        // --- Colors ---

        // General Colors
        setPrimaryColor(new Color(90, 170, 250));      
        setSecondaryColor(new Color(180, 180, 180));   
        setBackgroundColor(new Color(35, 35, 35));     
        setPanelBackgroundColor(new Color(45, 45, 45)); 
        setTableAlternateRowColor(new Color(55, 55, 55)); 
        setBorderColor(new Color(70, 70, 70));         
        setAccentColor(new Color(100, 100, 100));     

        setScrollColor(new Color(170, 170, 170)); // Màu sáng hơn cho dark mode, ví dụ xám nhạt hơn
        // Hoặc một màu sáng hơn nữa, ví dụ:
        // setScrollColor(new Color(130, 200, 255)); // Màu xanh nhạt
        // setScrollColor(new Color(200, 200, 200)); // Xám rất nhạt

        // Menu Specific Colors
        setMenuBackgroundColor(new Color(25, 25, 25)); 
        setMenuButtonColor(new Color(35, 35, 35));     
        setMenuButtonHoverColor(new Color(50, 50, 50)); 
        setMenuButtonPressColor(new Color(100, 100, 100)); 
        setMenuButtonForegroundColor(new Color(220, 220, 220)); 

        // General Button Colors
        setButtonBackgroundColor(new Color(90, 170, 250)); 
        setButtonForegroundColor(new Color(240, 240, 240)); 
        setButtonHoverColor(new Color(110, 190, 255));     

        // Text & Title Colors
        setTitleColor(new Color(240, 240, 240));      
        setTextColor(new Color(210, 210, 210));       
        setForegroundColor(new Color(210, 210, 210)); 

        // TextField/TextArea/ComboBox Colors
        setTextFieldBackgroundColor(new Color(60, 60, 60)); 
        setTextFieldForegroundColor(new Color(240, 240, 240)); 

        // --- Fonts ---
        setDefaultFont(new Font("Segoe UI", Font.PLAIN, 14));
        setTitleFont(new Font("Segoe UI", Font.BOLD, 28));
        setButtonFont(new Font("Segoe UI", Font.PLAIN, 16));
    }
}