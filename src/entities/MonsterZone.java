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
public class MonsterZone {

    public static MonsterZone[] selectAll(String map_id) {
        MonsterZone[] monsterZones;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT ZONE_ID, MONSTER_ID FROM MONSTER_ZONE WHERE MAP_ID = " + map_id);
        try {
            monsterZones = new MonsterZone[selectCount(map_id)];

            int count = 0;
            while (result.next()) {
                String zone_id = result.getString("ZONE_ID");
                String monster_id = result.getString("MONSTER_ID");

                monsterZones[count] = new MonsterZone();
                monsterZones[count].select(zone_id, monster_id, map_id);
                count++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return monsterZones;
    }

    public static int selectCount(String map_id) {
        int counts = 0;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT COUNT(*) AS MONSTER_ZONE_COUNTS FROM MONSTER_ZONE WHERE MAP_ID = " + map_id);

        try {
            if (result.next()) {
                counts = result.getInt("MONSTER_ZONE_COUNTS");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }

        return counts;
    }
    
    // PRIMARY KEY
    public String zone_id;
    public String monster_id;
    public String map_id;
    // FIELDS
    public int monster_counts;

    public void setZoneId(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getZoneId() {
        return zone_id;
    }

    public void setMonsterId(String monster_id) {
        this.monster_id = monster_id;
    }

    public String getMonsterId() {
        return monster_id;
    }

    public void setMapId(String map_id) {
        this.map_id = map_id;
    }

    public void setMonsterCounts(int monster_counts) {
        this.monster_counts = monster_counts;
    }

    public int getMonsterCounts() {
        return monster_counts;
    }

    public MonsterZone() {
    }

    public void select(String zone_id, String monster_id, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        ResultSet result = con.query("SELECT * FROM MONSTER_ZONE WHERE ZONE_ID = " + zone_id + " AND MONSTER_ID = '" + monster_id + "' AND MAP_ID = " + map_id);
        try {
            while (result.next()) {
                this.zone_id = result.getString("ZONE_ID");
                this.monster_id = result.getString("MONSTER_ID");
                this.map_id = result.getString("MAP_ID");
                this.monster_counts = result.getInt("MONSTER_COUNTS");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean available(String zone_id, String monster_id, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();
        
        ResultSet result = con.query("SELECT COUNT(*) AS MONSTER_ZONE_COUNTS FROM MONSTER_ZONE WHERE ZONE_ID = " + zone_id + " AND MONSTER_ID = '" + monster_id + "' AND MAP_ID = " + map_id);
        try {
            int count = 0;
            if (result.next()) {
                count = result.getInt("MONSTER_ZONE_COUNTS");
            }
            return count > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean update(String zone_id, String monster_id, String map_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("UPDATE MONSTER_ZONE SET MONSTER_COUNTS = ? WHERE ZONE_ID = " + zone_id + " AND MONSTER_ID = '" + monster_id + "' AND MAP_ID = " + map_id);
        try {
            statement.setInt(1, monster_counts);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean insert() {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("INSERT INTO MONSTER_ZONE VALUES(?, ?, ?, ?)");
        try {
            statement.setString(1, zone_id);
            statement.setString(2, monster_id);
            statement.setString(3, map_id);
            statement.setInt(4, monster_counts);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        final MonsterZone other = (MonsterZone) obj;
        if (!Objects.equals(this.zone_id, other.zone_id)) {
            return false;
        }
        if (!Objects.equals(this.monster_id, other.monster_id)) {
            return false;
        }
        if (!Objects.equals(this.map_id, other.map_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MonsterZone{" + "zone_id=" + zone_id + ", monster_id=" + monster_id + ", map_id=" + map_id + ", monster_counts=" + monster_counts + '}';
    }
    
}
