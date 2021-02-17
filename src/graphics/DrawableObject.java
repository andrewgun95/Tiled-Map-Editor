/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 2014130020
 */
public abstract class DrawableObject {

    private static List<DrawableObject> data = new ArrayList<>();
    private static int counter = 0;

    public static DrawableObject get(String id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
                return data.get(i);
            }
        }
        return null;
    }
    
    public static List<DrawableObject> getData(){
        return data;
    }
    
    private final String id;

    public DrawableObject() {
        id = String.format("%d", ++counter);
        
        data.add(this);
    }

    public DrawableObject(String id) {
        this.id = id;
        
        data.add(this);
    }

    public String getId() {
        return id;
    }

    abstract public void draw(Graphics2D g, int x, int y, int width, int height);

    @Override
    public String toString() {
        return id;
    }
}
