package system.theme;

import java.awt.Color;
import java.awt.Font;

public class LightTheme extends AppTheme {
    public LightTheme() {
        // --- Colors ---

        // General Colors
        setPrimaryColor(new Color(60, 140, 220));      
        setSecondaryColor(new Color(100, 100, 100));   
        setBackgroundColor(new Color(240, 240, 240));  
        setPanelBackgroundColor(new Color(255, 255, 255)); 
        setTableAlternateRowColor(new Color(245, 245, 245)); 
        setBorderColor(new Color(200, 200, 200));     
        setAccentColor(new Color(200, 200, 200));     

        setScrollColor(new Color(100, 100, 100)); // Màu sáng hơn, ví dụ xám đậm hơn một chút
        // Hoặc một màu sáng hơn nữa, ví dụ:
        // setScrollColor(new Color(100, 100, 255)); // Màu xanh sáng
        // setScrollColor(new Color(190, 190, 190)); // Xám sáng

        // Menu Specific Colors
        setMenuBackgroundColor(new Color(230, 230, 230)); 
        setMenuButtonColor(new Color(210, 210, 210));     
        setMenuButtonHoverColor(new Color(190, 190, 190)); 
        setMenuButtonForegroundColor(new Color(50, 50, 50)); 

        // General Button Colors
        setButtonBackgroundColor(new Color(60, 140, 220)); 
        setButtonForegroundColor(new Color(240, 240, 240)); 
        setButtonHoverColor(new Color(80, 160, 240));     

        // Text & Title Colors
        setTitleColor(new Color(30, 30, 30));         
        setTextColor(new Color(40, 40, 40));          
        setForegroundColor(new Color(40, 40, 40));    

        // TextField/TextArea/ComboBox Colors
        setTextFieldBackgroundColor(new Color(250, 250, 250)); 
        setTextFieldForegroundColor(new Color(30, 30, 30));     

        // --- Fonts ---
        setDefaultFont(new Font("Segoe UI", Font.PLAIN, 14));
        setTitleFont(new Font("Segoe UI", Font.BOLD, 28));
        setButtonFont(new Font("Segoe UI", Font.PLAIN, 16));
    }
}