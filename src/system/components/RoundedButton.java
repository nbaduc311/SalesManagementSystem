package system.components;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private Color normalColor = new Color(60, 120, 255);
    private Color hoverColor = new Color(30, 90, 220);
    private Color pressedColor = new Color(128, 173, 255);
	private boolean hovered = false;
	private Color currentColor = normalColor;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setBackground(normalColor);
        setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Hover và nhấn chuột
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hovered = true;
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hovered = false;
                currentColor = normalColor;
                repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                currentColor = pressedColor;
                repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (hovered) {
                    currentColor = hoverColor;
                } else {
                    currentColor = normalColor;
                }
                repaint();
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 30;
        int strokeWidth = 2; // độ dày viền
        int inset = strokeWidth / 2;

        // Vẽ nền
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // Vẽ viền bên trong nếu đang hover hoặc click
        if (hovered) {
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.setColor(new Color(0, 33, 105, 255)); // màu viền nhẹ
            g2.drawRoundRect(inset, inset, getWidth() - strokeWidth-1, getHeight() - strokeWidth, arc, arc);
        }

        // Vẽ text
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }


    @Override
    public void paintBorder(Graphics g) {
        // Không cần vì đã vẽ trong paintComponent()
    }
    public void setPressedTemporarily() {
        currentColor = pressedColor;
        repaint();

        Timer timer = new Timer(350, e -> {
            currentColor = normalColor;
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }


}
