/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author 2014130020
 */
public class ImageLoader {

    public static final String PNG = ".png";
    public static final String JPG = ".jpg";
    public static final String GIF = ".gif";
    
    private final GraphicsConfiguration gc;
    private final String dir;

    public ImageLoader(String dir) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
        
        this.dir = dir;
    }

    public BufferedImage loadImage(String ref, String fileformat) {
        try {
            BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource(dir + ref + fileformat));
            int transparency = img.getColorModel().getTransparency();

            BufferedImage buff_img = gc.createCompatibleImage(img.getWidth(), img.getHeight(), transparency);

            Graphics2D g = buff_img.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();

            return buff_img;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public BufferedImage[] loadArrayImage(String ref, int width, int height, String fileformat) {
        BufferedImage[] img = null;
        try {
            BufferedImage buff_img = loadImage(ref, fileformat);

            int rows = buff_img.getHeight() / height;
            int columns = buff_img.getWidth() / width;

            img = new BufferedImage[rows * columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    img[i * columns + j] = buff_img.getSubimage(j * width, i * height, width, height);
                }
            }

            return img;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return img;
    }
}
