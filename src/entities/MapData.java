/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import application.MySQLConnect;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

/**
 *
 * @author 2014130020
 */
public class MapData {

    public static MapData[] selectAll() {
        MapData[] maps;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT MAP_ID FROM MAP_DATA");
        try {
            maps = new MapData[selectCount()];

            int count = 0;
            while (result.next()) {
                String map_id = result.getString("MAP_ID");

                maps[count] = new MapData();
                maps[count].select(map_id);
                count++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return maps;
    }

    public static int selectCount() {
        int counts = 0;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT COUNT(*) AS MAP_DATA_COUNTS FROM MAP_DATA");

        try {
            if (result.next()) {
                counts = result.getInt("MAP_DATA_COUNTS");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }

        return counts;
    }
    // PRIMARY KEY
    public String map_id;
    // FIELD
    public Timestamp recent_update;

    public String getMapId() {
        return map_id;
    }

    public void setMapId(String map_id) {
        this.map_id = map_id;
    }

    public Timestamp getRecentUpdate() {
        return recent_update;
    }

    public void setRecentUpdate(Timestamp recent_update) {
        this.recent_update = recent_update;
    }

    public MapData() {
        // auto generated id
        MySQLConnect con = MySQLConnect.getInstance();

        int count = 0;

        ResultSet result = con.query("SELECT MAP_ID FROM MAP_DATA ORDER BY MAP_ID DESC LIMIT 1");
        try {
            if (result.next()) {
                String id = result.getString("MAP_ID");
                count = Integer.parseInt(id);
            }
            count++;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        map_id = String.format("%3d", count).replace(' ', '0');
    }

    public void select(String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        ResultSet result = con.query("SELECT * FROM MAP_DATA WHERE MAP_ID = " + map_id);
        try {
            while (result.next()) {
                this.map_id = result.getString("MAP_ID");
                this.recent_update = result.getTimestamp("RECENT_UPDATE");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean available(String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();
        
        ResultSet result = con.query("SELECT COUNT(*) AS MAP_DATA_COUNTS FROM MAP_DATA WHERE MAP_ID = "+map_id);
        try {
            int counts = 0;
            if (result.next()) {
                counts = result.getInt("MAP_DATA_COUNTS");
            }
            return counts > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean update(String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("UPDATE MAP_DATA SET RECENT_UPDATE = ? WHERE MAP_ID = " + map_id);
        try {
            statement.setTimestamp(1, recent_update);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean insert() {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("INSERT INTO MAP_DATA VALUES(?, ?)");
        try {
            statement.setString(1, map_id);
            statement.setTimestamp(2, recent_update);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        final Map other = (Map) obj;
        if (!Objects.equals(this.map_id, other.map_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MapData{" + "map_id=" + map_id + ", recent_update=" + recent_update + '}';
    }    
}
