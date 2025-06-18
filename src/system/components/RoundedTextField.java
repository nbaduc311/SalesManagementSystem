package system.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private int arc;
    private String placeholder = "";

    public RoundedTextField(int arc) {
        super();
        this.arc = arc;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
        setBackground(new Color(255, 255, 255, 255));

        // Cập nhật lại giao diện khi focus hoặc blur để vẽ placeholder
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Vẽ nền trắng bo tròn
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
        g2.setClip(round);
        g2.setColor(getBackground());
        g2.fill(round);
        g2.dispose();

        super.paintComponent(g);

        // Vẽ placeholder nếu chưa nhập gì và không có focus
        if (getText().isEmpty() && !isFocusOwner() && placeholder != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(Color.GRAY);
            g2d.setFont(getFont().deriveFont(Font.ITALIC));
            Insets insets = getInsets();
            int textY = (getHeight() + getFont().getSize()) / 2 - 2;
            g2d.drawString(placeholder, insets.left, textY);
            g2d.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();
    }
}
