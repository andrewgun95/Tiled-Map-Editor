/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.util.List;
import java.util.Random;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author 2014130020
 */
public class BSPDungeon {

    final Random rand = new Random();
    float partition;
    float randomness;
    int size;
    BSPTree tree;
    BSPCell[][] cells;

    public BSPDungeon(BSPCell[][] cells) {
        this.cells = cells;

        // default dungeon generator
        partition = 0.25f;
        randomness = .0f;
        size = 1;
    }

    public BSPDungeon(BSPCell[][] cells, float partition, float randomness, int size) {
        this.cells = cells;
        this.partition = partition;
        this.randomness = randomness;
        this.size = size;
    }

    public void generate() {
        // initial grid every cell is obstacle
        tree = new BSPTree(new DefaultMutableTreeNode(cells));
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].setObstacle(true);
            }
        }

        // construct bsp partition
        constructBSP(tree.getRoot(), cells);
        // construct each paritition corridor
        constructCorridor();
        // construct each partition room
        constructRoom();
    }

    private void constructBSP(DefaultMutableTreeNode parent, BSPCell[][] area) {
        if (area.length / 2 < cells.length * partition || area[0].length / 2 < cells[0].length * partition) {
            return;
        }

        BSPCell[][] area_a, area_b;

        // area can be partition along a horizontal line or vertical line
        if (rand.nextBoolean()) {
            // partition along a horizontal line
            int p = rand.nextInt(area.length);
            while (p < cells.length * partition || p > (area.length - cells.length * partition)) {
                p = rand.nextInt(area.length);
            }

            area_a = gridArea(0, 0, p, area[0].length, area);
            area_b = gridArea(p, 0, area.length - p, area[0].length, area);

        } else {
            // partition along a vertical line
            int p = rand.nextInt(area[0].length);
            while (p < cells[0].length * partition || p > (area[0].length - cells[0].length * partition)) {
                p = rand.nextInt(area[0].length);
            }

            area_a = gridArea(0, 0, area.length, p, area);
            area_b = gridArea(0, p, area.length, area[0].length - p, area);

        }

        // get empty room based of area
        BSPCell[][] room_a = gridRoom(area_a, size);
        BSPCell[][] room_b = gridRoom(area_b, size);

        // create tree branches based of parent
        DefaultMutableTreeNode node_a = new DefaultMutableTreeNode(room_a);
        DefaultMutableTreeNode node_b = new DefaultMutableTreeNode(room_b);
        tree.addBranch(parent, node_a, node_b);

        // recursive part
        constructBSP(node_a, area_a);
        constructBSP(node_b, area_b);
    }

    private void constructCorridor() {
        List<DefaultMutableTreeNode> nodes = tree.reverseBFO();

        DefaultMutableTreeNode prev = null;
        for (int i = 0; i < nodes.size(); i++) {
            DefaultMutableTreeNode curr = nodes.get(i);
            // create possible corridor
            if (i % 2 == 1) {
                corridor((BSPCell[][]) prev.getUserObject(), (BSPCell[][]) curr.getUserObject());
            } else {
                prev = curr;
            }
        }
    }

    private void constructRoom() {
        for (DefaultMutableTreeNode node : tree.leaves()) {
            //construct room with given mess value
            BSPCell[][] room = (BSPCell[][]) node.getUserObject();
            for (int x = 0; x < room.length; x++) {
                for (int y = 0; y < room[x].length; y++) {
                    room[x][y].setObstacle(Math.random() < randomness);
                }
            }
        }
    }

    private void corridor(BSPCell[][] area_a, BSPCell[][] area_b) {
        int hor, ver;

        // random area a point along horizontal line and vertical line
        hor = rand.nextInt(area_a.length);
        ver = rand.nextInt(area_a[0].length);

        int x1 = area_a[hor][ver].getX();
        int y1 = area_a[hor][ver].getY();

        // random area b point along horizontal line and vertical line
        hor = rand.nextInt(area_b.length);
        ver = rand.nextInt(area_b[0].length);

        int x2 = area_b[hor][ver].getX();
        int y2 = area_b[hor][ver].getY();

        path(x1, y1, x2, y2);
    }

    private void path(int x1, int y1, int x2, int y2) {
        int dx = 0, dy = 0;

        if (Math.abs(x2 - x1) != 0) {
            dx = (x2 - x1) / Math.abs(x2 - x1);
        }
        if (Math.abs(y2 - y1) != 0) {
            dy = (y2 - y1) / Math.abs(y2 - y1);
        }

        // horizontal path line
        int x = x1, y = y1;
        while (x != x2) {
            cells[x][y].setObstacle(false);
            x += dx;
        }

        // vertical path line
        x = x2;
        y = y1;
        while (y != y2) {
            cells[x][y].setObstacle(false);
            y += dy;
        }

        cells[x][y].setObstacle(false);
    }

    private BSPCell[][] gridRoom(BSPCell[][] area, int size) {

        int x = size;
        int y = size;
        int w = area.length - size * 2;
        int h = area[0].length - size * 2;

        return gridArea(x, y, w, h, area);
    }

    private BSPCell[][] gridArea(int x, int y, int width, int height, BSPCell[][] grid) {
        BSPCell[][] area = new BSPCell[width][height];

        int area_x = 0, area_y;

        for (int grid_x = x; grid_x < x + width; grid_x++) {
            area_y = 0;
            for (int grid_y = y; grid_y < y + height; grid_y++) {
                area[area_x][area_y] = grid[grid_x][grid_y];
                area_y++;
            }
            area_x++;
        }

        return area;
    }

    public class RoomAccessor {

        private int index;

        public RoomAccessor() {
            if (tree == null) {
                System.err.println("dungeon haven't been generated");
            }
            index = -1;
        }

        public boolean available() {
            List<DefaultMutableTreeNode> nodes = tree.leaves();
            return index + 1 < nodes.size();
        }

        public void access(BSPRoom room) {
            index++;
            List<DefaultMutableTreeNode> nodes = tree.leaves();

            if (index < nodes.size()) {
                BSPCell[][] cells = (BSPCell[][]) nodes.get(index).getUserObject();

                int x = cells[0][0].getX();
                int y = cells[0][0].getY();
                int width = cells.length;
                int height = cells[0].length;

                room.setBound(x, y, width, height);
            }
        }
    }
}
