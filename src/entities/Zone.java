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
public class Zone {

    public static Zone[] selectAll(String map_id) {
        Zone[] zones;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT ZONE_ID FROM ZONE WHERE MAP_ID = " + map_id);
        try {
            zones = new Zone[selectCount(map_id)];

            int count = 0;
            while (result.next()) {
                String zone_id = result.getString("ZONE_ID");

                zones[count] = new Zone();
                zones[count].select(zone_id, map_id);
                count++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return zones;
    }

    public static int selectCount(String map_id) {
        int counts = 0;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT COUNT(*) AS ZONE_COUNTS FROM ZONE WHERE MAP_ID = " + map_id);

        try {
            if (result.next()) {
                counts = result.getInt("ZONE_COUNTS");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }

        return counts;
    }
    // PRIMARY KEY
    public String zone_id;
    public String map_id;
    // FIELDS
    public String stat_area;
    public int bound_x;
    public int bound_y;
    public int bound_width;
    public int bound_height;

    public String getZoneId() {
        return zone_id;
    }

    public void setZoneId(String zone_id) {
        this.zone_id = zone_id;
    }

    public void setMapId(String map_id) {
        this.map_id = map_id;
    }

    public String getStatArea() {
        return stat_area;
    }

    public void setStatArea(String stat_area) {
        this.stat_area = stat_area;
    }

    public int getBoundX() {
        return bound_x;
    }

    public void setBoundX(int bound_x) {
        this.bound_x = bound_x;
    }

    public int getBoundY() {
        return bound_y;
    }

    public void setBoundY(int bound_y) {
        this.bound_y = bound_y;
    }

    public int getBoundWidth() {
        return bound_width;
    }

    public void setBoundWidth(int bound_width) {
        this.bound_width = bound_width;
    }

    public int getBoundHeight() {
        return bound_height;
    }

    public void setBoundHeight(int bound_height) {
        this.bound_height = bound_height;
    }

    public Zone() {
    }

    public void select(String zone_id, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        ResultSet result = con.query("SELECT * FROM ZONE WHERE ZONE_ID = " + zone_id + " AND MAP_ID = " + map_id);
        try {
            while (result.next()) {
                this.zone_id = result.getString("ZONE_ID");
                this.map_id = result.getString("MAP_ID");
                this.stat_area = result.getString("STAT_AREA");
                this.bound_x = result.getInt("BOUND_X");
                this.bound_y = result.getInt("BOUND_Y");
                this.bound_width = result.getInt("BOUND_WIDTH");
                this.bound_height = result.getInt("BOUND_HEIGHT");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean available(String zone_id, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();
        
        ResultSet result = con.query("SELECT COUNT(*) AS ZONE_COUNTS FROM ZONE WHERE ZONE_ID = " + zone_id + " AND MAP_ID = " + map_id);
        try {
            int counts = 0;
            if (result.next()) {
                counts = result.getInt("ZONE_COUNTS");
            }
            return counts > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean update(String zone_id, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("UPDATE ZONE SET STAT_AREA = ?, BOUND_X = ?, BOUND_Y = ?, BOUND_WIDTH = ?, BOUND_HEIGHT = ? WHERE ZONE_ID = " + zone_id + " AND MAP_ID = " + map_id);
        try {
            statement.setString(1, stat_area);
            statement.setInt(2, bound_x);
            statement.setInt(3, bound_y);
            statement.setInt(4, bound_width);
            statement.setInt(5, bound_height);
            statement.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean insert() {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("INSERT INTO ZONE VALUES(?, ?, ?, ?, ?, ?, ?)");
        try {
            statement.setString(1, zone_id);
            statement.setString(2, map_id);
            statement.setString(3, stat_area);
            statement.setInt(4, bound_x);
            statement.setInt(5, bound_y);
            statement.setInt(6, bound_width);
            statement.setInt(7, bound_height);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        final Zone other = (Zone) obj;
        if (!Objects.equals(this.zone_id, other.zone_id)) {
            return false;
        }
        if (!Objects.equals(this.map_id, other.map_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Zone{" + "zone_id=" + zone_id + ", map_id=" + map_id + ", stat_area=" + stat_area + ", bound_x=" + bound_x + ", bound_y=" + bound_y + ", bound_width=" + bound_width + ", bound_height=" + bound_height + '}';
    }    
}
