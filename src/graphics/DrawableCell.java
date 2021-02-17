/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics;

import generator.BSPCell;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author 2014130020
 */
public class DrawableCell implements Drawable, BSPCell {

    private final int x, y;
    private final int size;
    private DrawableObject object;
    private DrawableObject background;
    private boolean obstacle;
    private boolean visible;
    private boolean selected;
    private Color selectedColor;

    public DrawableCell(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;

        obstacle = false;
        visible = true;
        selected = false;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        selectedColor = Color.BLACK;
    }

    public void setSelected(boolean selected, Color color) {
        this.selected = selected;
        this.selectedColor = color;
    }

    /**
     * set user object
     * @param object
     */
    public void setUserObject(DrawableObject object) {
        this.object = object;
    }

    /**
     * get user object
     * @return 
     */
    public DrawableObject getUserObject() {
        return object;
    }

    /**
     * set background object
     *
     * @param background
     */
    public void setBackgroundObject(DrawableObject background) {
        this.background = background;
    }

    /**
     * get background object
     *
     * @return background
     */
    public DrawableObject getBackgroundObject() {
        return background;
    }

    public void strip(Graphics2D g) {
        Stroke old = g.getStroke();

        g.setPaint(Color.LIGHT_GRAY);
        g.setStroke(new BasicStroke(1.0f, 0, 0, 1, new float[]{2.0f, 2.0f}, 0));
        g.draw(new Rectangle2D.Float(x * size, y * size, size - 1, size - 1));

        g.setStroke(old);
    }

    public void info(Graphics2D g) {
        g.setPaint(Color.BLUE);
        if (background != null) {
            g.drawString(background.getId(), x * size, y * size + size);
        }else
        if (object != null) {
            g.drawString(object.getId(), x * size, y * size + size);
        }
    }

    public void image(Graphics2D g) {
        if (background != null) {
            background.draw(g, x * size, y * size, size, size);
        }
        if (object != null) {
            object.draw(g, x * size, y * size, size, size);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (visible) {

            if (background != null) {
                background.draw(g, x * size, y * size, size, size);
            }
            if (object != null) {
                object.draw(g, x * size, y * size, size, size);
            }

            if (selected) {
                g.setPaint(new Color(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), (int) (0.2f * 255f)));
                g.fill(new Rectangle2D.Float(x * size, y * size, size - 1, size - 1));

                g.setPaint(selectedColor);
                g.draw(new Rectangle2D.Float(x * size, y * size, size - 1, size - 1));
            }
        }
    }

    @Override
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    @Override
    public boolean isObstacle() {
        return obstacle;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        final DrawableCell other = (DrawableCell) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
}
