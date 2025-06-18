package system.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import system.theme.AppTheme;
import system.theme.ThemeManager;

public class CustomScrollBarUI extends BasicScrollBarUI {

    private final int THUMB_THICKNESS = 3; // Chiều dày của thumb (dẹt lại)
    private final int SB_SIZE = 3;        // Chiều rộng của thanh cuộn (không đổi)

    private final Color primaryThumbColor; // Màu của núm kéo (AccentColor từ theme)
    private final Color trackBackgroundColor; // Màu nền của track (MenuBackgroundColor từ theme)

    private boolean isScrollBarRollover = false; 
    private boolean isThumbBeingDragged = false; 
    private boolean isMouseWheelScrolling = false; 

    private float currentAlpha = 0f; 

    private Timer fadeOutAnimator;   
    private Timer hideDelayTimer;    

    private final int HIDE_DELAY = 1000;    
    private final int FADE_DURATION = 100;  
    private final int FADE_STEP = 10;       

    public CustomScrollBarUI() {
        AppTheme theme = ThemeManager.getInstance().getCurrentTheme();
        this.primaryThumbColor = theme.getAccentColor(); 
        this.trackBackgroundColor = theme.getMenuBackgroundColor(); 
    }
 // --- Hàm tạo MỚI: Cho phép thiết lập màu tùy chỉnh ---
    public CustomScrollBarUI(Color thumbColor, Color trackColor) {
        this.primaryThumbColor = thumbColor;
        this.trackBackgroundColor = trackColor;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Đặt alpha của track về 0 để nó hoàn toàn trong suốt
        g2.setColor(new Color(trackBackgroundColor.getRed(), trackBackgroundColor.getGreen(), trackBackgroundColor.getBlue(), 255)); 
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        int w = thumbBounds.width;
        int h = thumbBounds.height;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int alpha = (int) (currentAlpha * 255);
        if (alpha > 255) alpha = 255;
        if (alpha < 0) alpha = 0; 

        g2.setColor(new Color(primaryThumbColor.getRed(), primaryThumbColor.getGreen(), primaryThumbColor.getBlue(), alpha));

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            g2.fillRoundRect(thumbBounds.x + (w - THUMB_THICKNESS) / 2, 
                             thumbBounds.y,
                             THUMB_THICKNESS, h, THUMB_THICKNESS, THUMB_THICKNESS); 
        } else { 
            g2.fillRoundRect(thumbBounds.x,
                             thumbBounds.y + (h - THUMB_THICKNESS) / 2, 
                             w, THUMB_THICKNESS, THUMB_THICKNESS, THUMB_THICKNESS);
        }
        g2.dispose();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        scrollbar.setPreferredSize(new Dimension(SB_SIZE, SB_SIZE));
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        
        fadeOutAnimator = new Timer(FADE_STEP, e -> {
            currentAlpha -= (float) FADE_STEP / FADE_DURATION;
            if (currentAlpha <= 0.0f) {
                currentAlpha = 0.0f;
                fadeOutAnimator.stop(); 
            }
            scrollbar.repaint();
        });
        fadeOutAnimator.setRepeats(true); 

        hideDelayTimer = new Timer(HIDE_DELAY, e -> {
            if (!isThumbRollover() && !isScrollBarRollover && !isThumbBeingDragged && !isMouseWheelScrolling && currentAlpha > 0.0f) {
                fadeOutAnimator.start();
            }
        });
        hideDelayTimer.setRepeats(false); 

        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isScrollBarRollover = true;
                showScrollBar(); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isScrollBarRollover = false;
                scheduleHide(); 
            }
        });

        scrollbar.addAdjustmentListener(e -> {
            isMouseWheelScrolling = true; 
            showScrollBar(); 
            scheduleHide(); 
            
            Timer resetScrollTimer = new Timer(50, evt -> { 
                isMouseWheelScrolling = false;
                ((Timer)evt.getSource()).stop();
                scheduleHide(); 
            });
            resetScrollTimer.setRepeats(false);
            resetScrollTimer.start();
        });
        
        scrollbar.addPropertyChangeListener("valueIsAdjusting", evt -> {
            if (scrollbar.getValueIsAdjusting()) {
                isThumbBeingDragged = true;
                showScrollBar(); 
            } else {
                isThumbBeingDragged = false;
                scheduleHide(); 
            }
        });
    }

    private void showScrollBar() {
        if (fadeOutAnimator.isRunning()) {
            fadeOutAnimator.stop(); 
        }
        if (hideDelayTimer.isRunning()) {
            hideDelayTimer.stop(); 
        }
        currentAlpha = 1.0f; 
        scrollbar.repaint();
        scheduleHide(); 
    }

    private void scheduleHide() {
        if (!isThumbRollover() && !isScrollBarRollover && !isThumbBeingDragged && !isMouseWheelScrolling) {
            if (!hideDelayTimer.isRunning()) { 
                hideDelayTimer.restart(); 
            }
        }
    }
}