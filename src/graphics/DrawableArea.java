/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics;

import application.Assets;
import application.Database;
import entities.Monster;
import entities.MonsterZone;
import entities.Zone;
import generator.BSPRoom;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author 2014130020
 */
public class DrawableArea implements Drawable, BSPRoom {

    private final int cellSize;
    private int x, y;
    private int width, height;
    private boolean visible;
    private boolean selected;
    private Color color;
    
    private Zone zone;

    public DrawableArea(int cellSize) {
        this.cellSize = cellSize;

        selected = false;
        visible = true;
    }
    
    public void setZone(Zone zone){
        this.zone = zone;
    }
    
    public Zone getZone(){
        return zone;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        color = Color.BLACK;
    }

    public void setSelected(boolean selected, Color color) {
        this.selected = selected;
        this.color = color;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void draw(Graphics2D g) {
        if (visible) {
            Stroke old = g.getStroke();

            g.setPaint(Color.BLACK);
            g.setStroke(new BasicStroke(2.0f, 0, 0, 1, new float[]{10.0f, 10.0f}, 0));
            g.draw(new Rectangle2D.Float(x * cellSize, y * cellSize, width * cellSize, height * cellSize));

            g.setStroke(old);

            if (selected) {
                g.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (0.2f * 255f)));
                g.fill(new Rectangle2D.Float(x * cellSize, y * cellSize, width * cellSize, height * cellSize));
                g.setPaint(color);
                g.draw(new Rectangle2D.Float(x * cellSize, y * cellSize, width * cellSize, height * cellSize));
                
                drawInfo(x * cellSize, y * cellSize, width * cellSize, height * cellSize, g);
            }
        }
    }

    public void drawInfo(int x, int y, int width, int height, Graphics2D g) {

        if(zone == null)
            return;
        
        g.setPaint(Color.RED);
        g.drawString(zone.getStatArea(), x + 10, y + height - 10);

        Database database = Database.getInstance();
        List<MonsterZone> monsterZones = database.getMonsterZones(zone);

        if (monsterZones.isEmpty()) {
            return;
        }

        for (int i = 0; i < monsterZones.size(); i++) {

            MonsterZone monsterZone = monsterZones.get(i);

            Monster monster = new Monster();
            monster.select(monsterZone.getMonsterId());

            Image[] image = Assets.getInstance().get(monster.getFile());

            g.setColor(Color.BLACK);
            g.drawImage(image[1], x + 16, (y + 16) + (i * 16), 16, 16, null);

            g.drawString(" x " + monsterZone.getMonsterCounts(), x + 30, (y + 30) + (i * 16));
        }
    }

    @Override
    public void setBound(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean inside(int x, int y) {
        if ((x >= this.x && y >= this.y) && (x <= this.x + this.width && y <= this.y + this.height)) {
            return true;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        final DrawableArea other = (DrawableArea) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        return true;
    }
}
