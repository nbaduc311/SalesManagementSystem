package system.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtils {

    /**
     * Tạo ImageIcon từ ảnh có bo tròn góc
     *
     * @param path đường dẫn tới ảnh (ví dụ: "/img/login.png")
     * @param width chiều rộng mong muốn
     * @param height chiều cao mong muốn
     * @param arc độ cong của góc bo (ví dụ: 50)
     * @return ImageIcon đã bo góc
     */
	public static ImageIcon getRoundedImageIcon(String path, int width, int height, int arc) {
	    try {
	        BufferedImage original = ImageIO.read(ImageUtils.class.getResource(path));
	        Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);

	        int shadowSize = 13; // Độ mờ bóng
	        int totalWidth = width + shadowSize * 2;
	        int totalHeight = height + shadowSize * 2;

	        BufferedImage result = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2 = result.createGraphics();
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        // Vẽ bóng mờ bo tròn
	        for (int i = shadowSize; i > 0; i--) {
	            float alpha = (float) i / shadowSize * 0.4f;
	            g2.setColor(new Color(0, 26, 82, (int) (alpha * 100)));
	            g2.fillRoundRect(i, i, width + (shadowSize - i) * 2, height + (shadowSize - i) * 2,
	                    arc + (shadowSize - i) * 2, arc + (shadowSize - i) * 2);
	        }

	        // Vẽ ảnh bo góc
	        RoundRectangle2D.Float clip = new RoundRectangle2D.Float(shadowSize, shadowSize, width, height, arc, arc);
	        g2.setClip(clip);
	        g2.drawImage(scaled, shadowSize, shadowSize, null);

	        // Vẽ stroke trắng inner
	        g2.setClip(null); // Bỏ clip để vẽ stroke đầy đủ
	        g2.setStroke(new BasicStroke(4.5f)); // Độ dày viền
	        g2.setColor(new Color(201, 218, 255));
	        g2.draw(clip);

	        g2.dispose();
	        return new ImageIcon(result);

	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}





}
