/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2014130020
 */
public class DrawableGrid implements Drawable {

    private final int cellSize;
    private final DrawableCell[][] cells;
    private final ArrayList<DrawableArea> areas;

    public DrawableGrid(int width, int height, int cellSize) {
        this.cellSize = cellSize;

        int columns = width / cellSize;
        int rows = height / cellSize;

        cells = new DrawableCell[columns][rows];

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y] = new DrawableCell(x, y, cellSize);
            }
        }

        areas = new ArrayList<>();
    }

    public void showCells(boolean state) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].setVisible(state);
            }
        }
    }

    public void showSelections(boolean state) {
        for (DrawableArea drawableArea : areas) {
            drawableArea.setVisible(state);
        }
    }

    private boolean grid;

    public void showGrid(boolean state) {
        grid = state;
    }

    private boolean info;

    public void showInfo(boolean state) {
        info = state;
    }

    public void clearCells() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y] = new DrawableCell(x, y, cellSize);
            }
        }
    }

    public void addSelection(DrawableArea area) {
        areas.add(area);
    }

    public void clearSelections() {
        areas.clear();
    }

    public void clear() {
        clearCells();
        clearSelections();
    }

    public BufferedImage createImage() {

        BufferedImage image = new BufferedImage(getWidth() * cellSize, getHeight() * cellSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].image(g);
            }
        }

        return image;
    }

    public int getWidth() {
        return cells.length;
    }

    public int getHeight() {
        return cells[0].length;
    }

    public DrawableCell getCell(int x, int y) {
        return cells[x][y];
    }

    public DrawableCell getCellScreen(int x, int y) {
        return cells[x / cellSize][y / cellSize];
    }

    public DrawableCell[][] getCellSelectionScreen(int x, int y) {

        DrawableArea area = getSelectionScreen(x, y);
        if (area != null) {
            DrawableCell[][] temp = new DrawableCell[area.getWidth()][area.getHeight()];

            int tempX = 0, tempY;

            for (int cellsX = area.getX(); cellsX < area.getX() + area.getWidth(); cellsX++) {
                tempY = 0;
                for (int cellsY = area.getY(); cellsY < area.getY() + area.getHeight(); cellsY++) {
                    temp[tempX][tempY] = cells[cellsX][cellsY];
                    tempY++;
                }
                tempX++;
            }

            return temp;
        }
        return null;
    }

    public DrawableArea getSelectionScreen(int x, int y) {
        for (DrawableArea area : areas) {
            if (area.inside(x / cellSize, y / cellSize)) {
                return area;
            }
        }
        return null;
    }

    @Override
    public void draw(Graphics2D g) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].draw(g);

                if (grid) {
                    cells[x][y].strip(g);
                }

                if (info) {
                    cells[x][y].info(g);
                }
            }
        }
        if (areas != null) {
            for (DrawableArea area : areas) {
                area.draw(g);
            }
        }
    }

    public DrawableCell[][] getCells() {
        return cells;
    }

    public List<DrawableArea> getSelections() {
        return areas;
    }

    public int getCellSize() {
        return cellSize;
    }

}
