/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import entities.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 *
 * @author 2014130020
 */
public class Database {

    static Database instance = null;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    private Database() {
    }
    private Map map;
    private List<Tile> tiles;
    private List<Zone> zones;
    private List<MonsterZone> monsterZones;

    public void init() {
        MySQLConnect.getInstance().start();

        map = new Map();
        tiles = new ArrayList<>();
        zones = new ArrayList<>();
        monsterZones = new ArrayList<>();

        load(new Map().getMapId());
    }

    /**
     * load database
     *
     * @param mapId
     */
    public void load(String mapId) {
        map.setMapId(mapId);
        if (map.available(mapId)) {
            map.select(mapId);
        } else {
            map.setName("");
            map.setDescription("");
            map.setDifficulty("");
        }

        tiles.clear();
        tiles.addAll(Arrays.asList(Tile.selectAll(mapId)));

        zones.clear();
        zones.addAll(Arrays.asList(Zone.selectAll(mapId)));

        monsterZones.clear();
        monsterZones.addAll(Arrays.asList(MonsterZone.selectAll(mapId)));
    }

    public Map getMap() {
        return map;
    }

    /**
     * add tile
     *
     * @param positionX
     * @param positionY
     * @param obstacle
     * @param tileId
     */
    public void addTile(int positionX, int positionY, boolean obstacle, String tileId) {

        Tile tile = new Tile();
        // set keys
        tile.setPositionX(positionX);
        tile.setPositionY(positionY);
        tile.setMapId(map.getMapId());
        // set fields
        if (!tiles.contains(tile)) {
            tile.setType(obstacle ? "OBSTACLE" : "NON_OBSTACLE");
            tile.setTileId(tileId);
            tile.setPropertyId(obstacle ? tileId : "");

            tiles.add(tile);
        }
    }

    /**
     * remove tile
     *
     * @param positionX
     * @param positionY
     */
    public void removeTile(int positionX, int positionY) {

        Tile tile = new Tile();
        tile.setPositionX(positionX);
        tile.setPositionY(positionY);
        tile.setMapId(map.getMapId());

        if (tiles.remove(tile)) {
            System.out.println("removed " + tile);
        }
    }

    /**
     * add tile property
     *
     * @param positionX
     * @param positionY
     * @param propertyId
     */
    public void addTileProperty(int positionX, int positionY, String propertyId) {

        Tile tile = new Tile();
        tile.setPositionX(positionX);
        tile.setPositionY(positionY);
        tile.setMapId(map.getMapId());

        if (!tiles.contains(tile)) {
        } else {
            int index = tiles.indexOf(tile);

            Tile oldTile = tiles.get(index);
            oldTile.setPropertyId(propertyId);
        }
    }

    /**
     * remove tile property
     *
     * @param positionX
     * @param positionY
     */
    public void removeTileProperty(int positionX, int positionY) {

        Tile tile = new Tile();
        tile.setPositionX(positionX);
        tile.setPositionY(positionY);
        tile.setMapId(map.getMapId());

        if (!tiles.contains(tile)) {
        } else {
            int index = tiles.indexOf(tile);

            Tile oldTile = tiles.get(index);
            oldTile.setPropertyId("");
        }
    }

    /**
     * refresh tile property
     */
    public void refreshTileProperty() {
        for (Tile tile : tiles) {
            if (!tile.getPropertyId().equals("")) {
                tile.setType("OBSTACLE");
            } else {
                tile.setType("NON-OBSTACLE");
            }
        }
    }

    public Tile getTile(int positionX, int positionY) {
        for (Tile tile : tiles) {
            if (tile.getPositionX() == positionX && tile.getPositionY() == positionY) {
                return tile;
            }
        }
        return null;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    private int countZone;

    public void addZone(int boundX, int boundY, int boundWidth, int boundHeight) {

        Zone zone = new Zone();
        // set keys
        zone.setZoneId(String.format("%3d", ++countZone).replace(' ', '0'));
        zone.setMapId(map.getMapId());
        // set fields
        zone.setStatArea("SAVE_AREA");
        zone.setBoundX(boundX);
        zone.setBoundY(boundY);
        zone.setBoundWidth(boundWidth);
        zone.setBoundHeight(boundHeight);

        zones.add(zone);
    }

    public Zone getZone(int boundX, int boundY, int boundWidth, int boundHeight) {
        for (Zone zone : zones) {
            if (zone.getBoundX() == boundX && zone.getBoundY() == boundY && zone.getBoundWidth() == boundWidth && zone.getBoundHeight() == boundHeight) {
                return zone;
            }
        }
        return null;
    }

    public List<Zone> getZones() {
        return zones;
    }

    /**
     *
     * @param zone
     * @param monster
     */
    public void addMonsterZone(Zone zone, Monster monster) {

        MonsterZone monsterZone = new MonsterZone();
        // set keys
        monsterZone.setZoneId(zone.getZoneId());
        monsterZone.setMonsterId(monster.getMonsterId());
        monsterZone.setMapId(map.getMapId());

        // set fields
        if (!monsterZones.contains(monsterZone)) {
            monsterZone.setMonsterCounts(monsterZone.getMonsterCounts() + 1);
            monsterZones.add(monsterZone);
        } else {
            int index = monsterZones.indexOf(monsterZone);

            // update old monster zone
            MonsterZone oldMonsterZone = monsterZones.get(index);
            oldMonsterZone.setMonsterCounts(oldMonsterZone.getMonsterCounts() + 1);
        }
    }

    /**
     * remove monster zone
     *
     * @param zone
     */
    public void removeMonsterZone(Zone zone) {
        int i = 0;
        while (i < monsterZones.size()) {
            MonsterZone monsterZone = monsterZones.get(i);
            if (monsterZone.getZoneId().equals(zone.getZoneId())) {
                monsterZones.remove(i);
            } else {
                i++;
            }
        }
    }

    public void refreshMonsterZone() {
        for (Zone zone : zones) {
            zone.setStatArea("SAVE_AREA");
            for (MonsterZone monsterZone : getMonsterZones(zone)) {

                Monster monster = new Monster();
                monster.select(monsterZone.getMonsterId());

                String rarity = monster.getRarity();

                if (rarity.equals("Rare") || rarity.equals("Epic") || rarity.equals("Legend")) {
                    zone.setStatArea("BOSS_AREA");
                    break;
                } else {
                    zone.setStatArea("MONSTER_AREA");
                }
            }
        }
    }

    public List<MonsterZone> getMonsterZones(Zone zone) {
        List<MonsterZone> temp = new ArrayList<>();
        if (monsterZones != null) {
            for (int i = 0; i < monsterZones.size(); i++) {
                MonsterZone monsterZone = monsterZones.get(i);
                if (monsterZone.getZoneId().equals(zone.getZoneId())) {
                    temp.add(monsterZone);
                }
            }
        }
        return temp;
    }

    public void addMonster(String name, String element, String rarity, float health, float attack, float defense, float xpReward, float gpReward, String filename) {

        Monster monster = new Monster();
        monster.setName(name);
        monster.setElement(element);
        monster.setRarity(rarity);
        monster.setAttack(attack);
        monster.setDefense(defense);
        monster.setXP(xpReward);
        monster.setGP(gpReward);
        monster.setFile(filename);

        if (monster.insert()) {
            System.out.println("insert monster successfull");
        } else {
            System.out.println("insert monster not successfull");
        }

    }

    public List<Monster> getMonsters() {
        return Arrays.asList(Monster.selectAll());
    }

    public void addProperty(String name, String filename) {

        Property property = new Property();
        property.setName(name);
        property.setFile(filename);

        if (property.insert()) {
            System.out.println("insert property successfull");
        } else {
            System.out.println("insert property not successfull");
        }
    }

    public List<Property> getProperties() {
        return Arrays.asList(Property.selectAll());
    }

    public void save(String name, String description, String difficulty, final JLabel progress) {

        map.setName(name);
        map.setDescription(description);
        map.setDifficulty(difficulty);

        SwingWorker worker = new SwingWorker<Boolean, Integer>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                int count = 0;

                // save map
                if (map.available(map.getMapId())) {
                    if (map.update(map.getMapId())) {
                        setProgress(++count);
                    } else {
                        return false;
                    }
                } else {
                    if (map.insert()) {
                        setProgress(++count);
                    } else {
                        return false;
                    }
                }
                // save tiles
                for (Tile tile : tiles) {
                    if (tile.available(tile.getPositionX(), tile.getPositionY(), map.getMapId())) {
                        if (tile.update(tile.getPositionX(), tile.getPositionY(), map.getMapId())) {
                            setProgress(++count);
                        } else {
                            return false;
                        }
                    } else {
                        if (tile.insert()) {
                            setProgress(++count);
                        } else {
                            return false;
                        }
                    }
                }
                // save zones
                for (Zone zone : zones) {
                    if (zone.available(zone.getZoneId(), map.getMapId())) {
                        if (zone.update(zone.getZoneId(), map.getMapId())) {
                            setProgress(++count);
                        } else {
                            return false;
                        }
                    } else {
                        if (zone.insert()) {
                            setProgress(++count);
                        } else {
                            return false;
                        }
                    }
                }
                // save monster zones
                for (MonsterZone monsterZone : monsterZones) {
                    if (monsterZone.available(monsterZone.getZoneId(), monsterZone.getMonsterId(), map.getMapId())) {
                        if (monsterZone.update(monsterZone.getZoneId(), monsterZone.getMonsterId(), map.getMapId())) {
                            setProgress(++count);
                        } else {
                            return false;
                        }
                    } else {
                        if (monsterZone.insert()) {
                            setProgress(++count);
                        } else {
                            return false;
                        }
                    }
                }
                // save map data
                MapData mapData = new MapData();
                mapData.setMapId(map.getMapId());
                mapData.setRecentUpdate(new Timestamp(System.currentTimeMillis()));

                if (mapData.available(map.getMapId())) {
                    if (mapData.update(map.getMapId())) {
                        setProgress(++count);
                    } else {
                        return false;
                    }
                } else {
                    if (mapData.insert()) {
                        setProgress(++count);
                    } else {
                        return false;
                    }
                }

                return true;
            }

        };
        worker.execute();

        while (!worker.isDone()) {
            System.out.println("LOADING_" + worker.getProgress() + "%");
        }

        System.out.println("SAVE_SUCCESFULL");
        progress.setText("SAVE_SUCCESFULL");
    }

    public void clear() {
        zones.clear();
        countZone = 0;
        tiles.clear();
        monsterZones.clear();
    }

    public void close() {
        MySQLConnect.getInstance().terminate();
    }

}
