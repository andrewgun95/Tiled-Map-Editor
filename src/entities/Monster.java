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
public class Monster {

    public static Monster[] selectAll() {
        Monster[] monsters;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT MONSTER_ID FROM MONSTER");
        try {
            monsters = new Monster[selectCount()];

            int count = 0;
            while (result.next()) {
                String monster_id = result.getString("MONSTER_ID");

                monsters[count] = new Monster();
                monsters[count].select(monster_id);
                count++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return monsters;
    }

    public static int selectCount() {
        int counts = 0;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT COUNT(*) AS MONSTER_COUNTS FROM MONSTER");

        try {
            if (result.next()) {
                counts = result.getInt("MONSTER_COUNTS");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }

        return counts;
    }
    // PRIMARY KEY
    public String monster_id;
    // FIELD
    public String monster_name;
    public String monster_element;
    public String monster_rarity;
    public float monster_health;
    public float monster_attack;
    public float monster_defense;
    public float xp_reward;
    public float gp_reward;
    public String file_name;

    public String getMonsterId() {
        return monster_id;
    }

    public void setName(String monster_name) {
        this.monster_name = monster_name;
    }

    public void setElement(String monster_element) {
        this.monster_element = monster_element;
    }

    public String getRarity() {
        return monster_rarity;
    }

    public void setRarity(String monster_rarity) {
        this.monster_rarity = monster_rarity;
    }

    public void setHealth(float monster_health) {
        this.monster_health = monster_health;
    }

    public void setAttack(float monster_attack) {
        this.monster_attack = monster_attack;
    }

    public void setDefense(float monster_defense) {
        this.monster_defense = monster_defense;
    }

    public void setXP(float xp_reward) {
        this.xp_reward = xp_reward;
    }

    public void setGP(float gp_reward) {
        this.gp_reward = gp_reward;
    }

    public String getFile() {
        return file_name;
    }

    public void setFile(String file_name) {
        this.file_name = file_name;
    }

    public Monster() {
        // auto generated id
        MySQLConnect con = MySQLConnect.getInstance();

        int count = 0;

        ResultSet result = con.query("SELECT MONSTER_ID FROM MONSTER ORDER BY MONSTER_ID DESC LIMIT 1");
        try {
            if (result.next()) {
                String id = result.getString("MONSTER_ID");
                count = Integer.parseInt(id.substring(2, id.length()));
            }
            count++;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        monster_id = "M-" + String.format("%3d", count).replace(' ', '0');
    }

    public boolean select(String monster_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        ResultSet result = con.query("SELECT * FROM MONSTER WHERE MONSTER_ID = '" + monster_id + "'");
        try {
            while (result.next()) {
                this.monster_id = result.getString("MONSTER_ID");
                this.monster_name = result.getString("MONSTER_NAME");
                this.monster_element = result.getString("MONSTER_ELEMENT");
                this.monster_rarity = result.getString("MONSTER_RARITY");
                this.monster_health = result.getFloat("MONSTER_HEALTH");
                this.monster_attack = result.getFloat("MONSTER_ATTACK");
                this.monster_defense = result.getFloat("MONSTER_DEFENSE");
                this.xp_reward = result.getFloat("XP_REWARD");
                this.gp_reward = result.getFloat("GP_REWARD");
                this.file_name = result.getString("FILE_NAME");
            }

            return result.next();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean insert() {

        MySQLConnect con = MySQLConnect.getInstance();

        PreparedStatement statement = con.statement("INSERT INTO MONSTER VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            statement.setString(1, monster_id);
            statement.setString(2, monster_name);
            statement.setString(3, monster_element);
            statement.setString(4, monster_rarity);
            statement.setFloat(5, monster_health);
            statement.setFloat(6, monster_attack);
            statement.setFloat(7, monster_defense);
            statement.setFloat(8, xp_reward);
            statement.setFloat(9, gp_reward);
            statement.setString(10, file_name);
            statement.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        final Monster other = (Monster) obj;
        if (!Objects.equals(this.monster_id, other.monster_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Monster{" + "monster_id=" + monster_id + ", monster_name=" + monster_name + ", monster_element=" + monster_element + ", monster_rarity=" + monster_rarity + ", monster_health=" + monster_health + ", monster_attack=" + monster_attack + ", monster_defense=" + monster_defense + ", xp_reward=" + xp_reward + ", gp_reward=" + gp_reward + ", file_name=" + file_name + '}';
    }
    
}
