/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import application.MySQLConnect;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 *
 * @author 2014130020
 */
public class Map {

    public static Map[] selectAll() {
        Map[] maps;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT MAP_ID FROM MAP");
        try {
            maps = new Map[selectCount()];

            int count = 0;
            while (result.next()) {
                String map_id = result.getString("MAP_ID");

                maps[count] = new Map();
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
        ResultSet result = con.query("SELECT COUNT(*) AS MAP_COUNTS FROM MAP");

        try {
            if (result.next()) {
                counts = result.getInt("MAP_COUNTS");
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
    public String map_name;
    public String map_desc;
    public String map_diff;

    public String getMapId() {
        return map_id;
    }

    public void setMapId(String map_id) {
        this.map_id = map_id;
    }

    public String getName() {
        return map_name;
    }

    public void setName(String map_name) {
        this.map_name = map_name;
    }

    public String getDescription() {
        return map_desc;
    }

    public void setDescription(String map_desc) {
        this.map_desc = map_desc;
    }

    public String getDifficulty() {
        return map_diff;
    }

    public void setDifficulty(String map_diff) {
        this.map_diff = map_diff;
    }

    public Map() {
        // auto generated id
        MySQLConnect con = MySQLConnect.getInstance();

        int count = 0;

        ResultSet result = con.query("SELECT MAP_ID FROM MAP ORDER BY MAP_ID DESC LIMIT 1");
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

        ResultSet result = con.query("SELECT * FROM MAP WHERE MAP_ID = " + map_id);
        try {
            while (result.next()) {
                this.map_id = result.getString("MAP_ID");
                this.map_name = result.getString("MAP_NAME");
                this.map_desc = result.getString("MAP_DESCRIPTION");
                this.map_diff = result.getString("MAP_DIFFICULTY");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean available(String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        ResultSet result = con.query("SELECT COUNT(*) AS MAP_COUNTS FROM MAP WHERE MAP_ID = " + map_id);
        try {
            int counts = 0;
            if (result.next()) {
                counts = result.getInt("MAP_COUNTS");
            }
            return counts > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());

            return false;
        }
    }

    public boolean update(String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("UPDATE MAP SET MAP_NAME = ?, MAP_DESCRIPTION = ?, MAP_DIFFICULTY = ? WHERE MAP_ID = " + map_id);
        try {
            statement.setString(1, map_name);
            statement.setString(2, map_desc);
            statement.setString(3, map_diff);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean insert() {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("INSERT INTO MAP VALUES(?, ?, ?, ?)");
        try {
            statement.setString(1, map_id);
            statement.setString(2, map_name);
            statement.setString(3, map_desc);
            statement.setString(4, map_diff);
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
        return "Map{" + "map_id=" + map_id + ", map_name=" + map_name + ", map_desc=" + map_desc + ", map_diff=" + map_diff + '}';
    }
        
}
