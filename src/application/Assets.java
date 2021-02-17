/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import utility.ImageLoader;

/**
 *
 * @author 2014130020
 */
public class Assets {

    static Assets instance;

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }
    
    private final HashMap<String, BufferedImage[]> map;

    public Assets() {
        map = new HashMap<>();
    }

    public void load() {
        // load images here
        loadMonsters();
        loadWalls();
        loadProperties();
        loadFloors();
    }

    private void loadProperties() {
        final ImageLoader loader = new ImageLoader("images/properties/");

        map.put("big-tree", loader.loadArrayImage("big-tree", 16, 16, ImageLoader.PNG));
        map.put("chest", loader.loadArrayImage("chest", 16, 16, ImageLoader.PNG));
        map.put("desk", loader.loadArrayImage("desk", 16, 16, ImageLoader.PNG));
        map.put("diamond", loader.loadArrayImage("diamond", 16, 16, ImageLoader.PNG));
        map.put("door-jail", loader.loadArrayImage("door-jail", 16, 16, ImageLoader.PNG));
        map.put("door", loader.loadArrayImage("door", 16, 16, ImageLoader.PNG));
        map.put("fence-front", loader.loadArrayImage("fence-front", 16, 16, ImageLoader.PNG));
        map.put("fence-side", loader.loadArrayImage("fence-side", 16, 16, ImageLoader.PNG));
        map.put("gold", loader.loadArrayImage("gold", 16, 16, ImageLoader.PNG));
        map.put("grass", loader.loadArrayImage("grass", 16, 16, ImageLoader.PNG));
        map.put("half-pillar", loader.loadArrayImage("half-pillar", 16, 16, ImageLoader.PNG));
        map.put("jar", loader.loadArrayImage("jar", 16, 16, ImageLoader.PNG));
        map.put("king-chair", loader.loadArrayImage("king-chair", 16, 16, ImageLoader.PNG));
        map.put("pine-tree", loader.loadArrayImage("pine-tree", 16, 16, ImageLoader.PNG));
        map.put("rock", loader.loadArrayImage("rock", 16, 16, ImageLoader.PNG));
        map.put("sand", loader.loadArrayImage("sand", 16, 16, ImageLoader.PNG));
        map.put("sign", loader.loadArrayImage("sign", 16, 16, ImageLoader.PNG));
        map.put("stairs-down", loader.loadArrayImage("stairs-down", 16, 16, ImageLoader.PNG));
        map.put("stairs-up", loader.loadArrayImage("stairs-up", 16, 16, ImageLoader.PNG));
        map.put("statue", loader.loadArrayImage("statue", 16, 16, ImageLoader.PNG));
        map.put("table", loader.loadArrayImage("table", 16, 16, ImageLoader.PNG));
        map.put("torchlight", loader.loadArrayImage("torchlight", 16, 16, ImageLoader.PNG));
        map.put("well", loader.loadArrayImage("well", 16, 16, ImageLoader.PNG));
    }

    private void loadMonsters() {
        final ImageLoader loader = new ImageLoader("images/monsters/");

        map.put("bat", loader.loadArrayImage("bat", 16, 16, ImageLoader.PNG));
        map.put("ghost", loader.loadArrayImage("ghost", 16, 16, ImageLoader.PNG));
        map.put("rat", loader.loadArrayImage("rat", 16, 16, ImageLoader.PNG));
        map.put("skeleton", loader.loadArrayImage("skeleton", 16, 16, ImageLoader.PNG));
        map.put("slime", loader.loadArrayImage("slime", 16, 16, ImageLoader.PNG));
        map.put("spider", loader.loadArrayImage("spider", 16, 16, ImageLoader.PNG));
        map.put("zombie", loader.loadArrayImage("zombie", 16, 16, ImageLoader.PNG));
    }

    private void loadWalls() {
        final ImageLoader loader = new ImageLoader("images/walls/");

        map.put("brick-wall", loader.loadArrayImage("brick-wall", 16, 16, ImageLoader.PNG));
        map.put("green-stone-wall", loader.loadArrayImage("green-stone-wall", 16, 16, ImageLoader.PNG));
        map.put("rock-wall", loader.loadArrayImage("rock-wall", 16, 16, ImageLoader.PNG));
        map.put("stone-wall", loader.loadArrayImage("stone-wall", 16, 16, ImageLoader.PNG));
    }

    private void loadFloors() {
        final ImageLoader loader = new ImageLoader("images/floors/");

        map.put("floors", loader.loadArrayImage("floors", 16, 16, ImageLoader.PNG));
    }

    public BufferedImage[] get(String filename) {
        return map.get(filename);
    }
}
