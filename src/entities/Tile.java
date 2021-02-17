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
public class Tile {

    public static Tile[] selectAll(String map_id) {
        Tile[] tiles;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT POSITION_X, POSITION_Y FROM TILE WHERE MAP_ID = " + map_id);
        try {
            tiles = new Tile[selectCount(map_id)];

            int count = 0;
            while (result.next()) {
                int position_x = result.getInt("POSITION_X");
                int position_y = result.getInt("POSITION_Y");

                tiles[count] = new Tile();
                tiles[count].select(position_x, position_y, map_id);
                count++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return tiles;
    }

    public static int selectCount(String map_id) {
        int counts = 0;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT COUNT(*) AS TILE_COUNTS FROM TILE WHERE MAP_ID = " + map_id);

        try {
            if (result.next()) {
                counts = result.getInt("TILE_COUNTS");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }

        return counts;
    }
    // PRIMARY KEY
    public int position_x;
    public int position_y;
    public String map_id;
    // FIELD
    public String tile_type;
    public String tile_id;
    public String property_id;

    public void setPositionX(int position_x) {
        this.position_x = position_x;
    }

    public int getPositionX() {
        return position_x;
    }

    public void setPositionY(int position_y) {
        this.position_y = position_y;
    }

    public int getPositionY() {
        return position_y;
    }

    public void setMapId(String map_id) {
        this.map_id = map_id;
    }

    public void setType(String tile_type) {
        this.tile_type = tile_type;
    }

    public String getType() {
        return tile_type;
    }

    public String getTileId() {
        return tile_id;
    }

    public void setTileId(String tile_id) {
        this.tile_id = tile_id;
    }

    public String getPropertyId() {
        return property_id;
    }

    public void setPropertyId(String property_id) {
        this.property_id = property_id;
    }

    public Tile() {
    }

    public void select(int position_x, int position_y, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        ResultSet result = con.query("SELECT * FROM TILE WHERE POSITION_X = " + position_x + " AND POSITION_Y = " + position_y + " AND MAP_ID = " + map_id);
        try {
            while (result.next()) {
                this.position_x = result.getInt("POSITION_X");
                this.position_y = result.getInt("POSITION_Y");
                this.map_id = result.getString("MAP_ID");
                this.tile_type = result.getString("TILE_TYPE");
                this.tile_id = result.getString("TILE_ID");
                this.property_id = result.getString("PROPERTY_ID");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean available(int position_x, int position_y, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();
        
        ResultSet result = con.query("SELECT COUNT(*) AS TILE_COUNTS FROM TILE WHERE POSITION_X = " + position_x + " AND POSITION_Y = " + position_y + " AND MAP_ID = " + map_id);
        try {
            int counts = 0;
            if (result.next()) {
                counts = result.getInt("TILE_COUNTS");
            }
            return counts > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean update(int position_x, int position_y, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("UPDATE TILE SET TILE_TYPE = ?, TILE_ID = ?, PROPERTY_ID = ? WHERE POSITION_X = " + position_x + " AND POSITION_Y = " + position_y + " AND MAP_ID = " + map_id);
        try {
            statement.setString(1, tile_type);
            statement.setString(2, tile_id);
            statement.setString(3, property_id);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean insert() {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("INSERT INTO TILE VALUES(?, ?, ?, ?, ?, ?)");
        try {
            statement.setInt(1, position_x);
            statement.setInt(2, position_y);
            statement.setString(3, map_id);
            statement.setString(4, tile_type);
            statement.setString(5, tile_id);
            statement.setString(6, property_id);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        final Tile other = (Tile) obj;
        if (this.position_x != other.position_x) {
            return false;
        }
        if (this.position_y != other.position_y) {
            return false;
        }
        if (!Objects.equals(this.map_id, other.map_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tile{" + "position_x=" + position_x + ", position_y=" + position_y + ", map_id=" + map_id + ", tile_type=" + tile_type + ", tile_id=" + tile_id + ", property_id=" + property_id + '}';
    }    
}
