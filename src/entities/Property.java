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
public class Property {

    public static Property[] selectAll() {
        Property[] properties;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT PROPERTY_ID FROM PROPERTY");
        try {
            properties = new Property[selectCount()];

            int count = 0;
            while (result.next()) {
                String property_id = result.getString("PROPERTY_ID");

                properties[count] = new Property();
                properties[count].select(property_id);
                count++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        return properties;
    }

    public static int selectCount() {
        int counts = 0;

        MySQLConnect con = MySQLConnect.getInstance();
        ResultSet result = con.query("SELECT COUNT(*) AS PROPERTY_COUNTS FROM PROPERTY");

        try {
            if (result.next()) {
                counts = result.getInt("PROPERTY_COUNTS");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }

        return counts;
    }
    
    // PRIMARY KEY
    public String property_id;
    // FIELD
    public String property_name;
    public String file_name;

    public String getPropertyId() {
        return property_id;
    }

    public void setName(String property_name) {
        this.property_name = property_name;
    }

    public void setFile(String file_name) {
        this.file_name = file_name;
    }

    public String getFile() {
        return file_name;
    }

    public Property() {
        // auto generated id
        MySQLConnect con = MySQLConnect.getInstance();

        int count = 0;

        ResultSet result = con.query("SELECT PROPERTY_ID FROM PROPERTY ORDER BY PROPERTY_ID DESC LIMIT 1");
        try {
            if (result.next()) {
                String id = result.getString("PROPERTY_ID");
                count = Integer.parseInt(id.substring(2, id.length()));
            }
            count++;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        property_id = "P-"+String.format("%3d", count).replace(' ', '0');
    }

    public boolean select(String property_id) {

        MySQLConnect con = MySQLConnect.getInstance();

        ResultSet result = con.query("SELECT * FROM PROPERTY WHERE PROPERTY_ID = '" + property_id + "'");
        try {
            while (result.next()) {
                this.property_id = result.getString("PROPERTY_ID");
                this.property_name = result.getString("PROPERTY_NAME");
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

        PreparedStatement statement = con.statement("INSERT INTO PROPERTY VALUES(?, ?, ?)");
        try {
            statement.setString(1, property_id);
            statement.setString(2, property_name);
            statement.setString(3, file_name);
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        final Property other = (Property) obj;
        if (!Objects.equals(this.property_id, other.property_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Property{" + "property_id=" + property_id + ", property_name=" + property_name + ", file_name=" + file_name + '}';
    }
    
}
