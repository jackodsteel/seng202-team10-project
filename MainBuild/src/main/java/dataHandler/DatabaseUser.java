package dataHandler;

import main.HandleUsers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Handles all of the user data, ensuring that each username is unique when adding names.
 */
public class DatabaseUser {

    private static final String tableName = "users";
    private static final String[] fields = {
            "name                    VARCHAR(12)",
            "birth_day               INTEGER",
            "birth_month             INTEGER",
            "birth_year              INTEGER",
            "gender                  INTEGER"};
    private static final String primaryKey = "name";

    private final SQLiteDB db;

    private String addUserString = "insert or fail into users values(?,?,?,?,?)";
    private PreparedStatement addUser;


    /**
     * Initializes the database when creating an instance of the DatabaseUser.
     *
     * @param db database the user data is added to
     */
    public DatabaseUser(SQLiteDB db) {
        this.db = db;
        db.addTable(tableName, fields, primaryKey);
        addUser = db.getPreparedStatement(addUserString);
    }


    /**
     * Adds Name to the database.
     *
     * @param name   name of the user to add
     * @param day    users day of birth
     * @param month  users month of birth
     * @param year   users year of birth
     * @param gender users gender
     */
    public void addUser(String name, int day, int month, int year, int gender) {
        try {
            addUser.setString(1, name);
            addUser.setInt(2, day);
            addUser.setInt(3, month);
            addUser.setInt(4, year);
            addUser.setInt(5, gender);
            addUser.executeUpdate();
            db.commit();

        } catch (SQLException e) {
            addUser = db.getPreparedStatement(addUserString);
            System.out.println(e.getMessage());
        }
    }


    /**
     * Updates the name of the user in each of the tables where its previous name has been stored. It also updates the
     * users date of birth or gender if to the given parameters.
     *
     * @param newName the new name of the user
     * @param oldName the previous name of the user
     * @param day     users day of birth
     * @param month   users month of birth
     * @param year    users year of birth
     * @param gender  the users gender
     */
    public void updateDetails(String newName, String oldName, int day, int month, int year, int gender) {
        db.executeQuerySQL("UPDATE users SET name = '" + newName + "', birth_day = " + day + ", birth_month = " + month + "" +
                ", birth_year = " + year + ", gender = " + gender);
        db.executeQuerySQL("UPDATE taken_routes SET name = '" + newName + "' " + "WHERE name = '" + oldName + "';");
        db.executeQuerySQL("UPDATE favourite_routes SET name = '" + newName + "' " + "WHERE name = '" + oldName + "';");
        db.executeQuerySQL("UPDATE favourite_wifi SET name = '" + newName + "' " + "WHERE name = '" + oldName + "';");
        db.executeQuerySQL("UPDATE favourite_retail SET name = '" + newName + "' " + "WHERE name = '" + oldName + "';");
    }


    /**
     * Deletes everything about the user from all tables in the database.
     *
     * @param username name of the user to delete.
     * @param hu       the current HandleUsers object that is accessing the cyclists information
     */
    public void removeUserFromDatabase(String username, HandleUsers hu) {
        ListDataHandler listDataHandler = new ListDataHandler(db, hu.currentCyclist.getName());
        List<String> userLists = listDataHandler.getLists();
        for (String userList : userLists) {
            db.executeQuerySQL("UPDATE route_information SET list_name = null WHERE list_name = '" + userList + "';");
            db.executeQuerySQL("UPDATE wifi_location SET list_name = null WHERE list_name = '" + userList + "';");
            db.executeQuerySQL("UPDATE retailer SET list_name = null WHERE list_name = '" + userList + "';");
            db.executeQuerySQL("DELETE FROM lists WHERE list_owner = '" + username + "' AND list_name = '" + userList + "';");
        }
        db.executeQuerySQL("DELETE FROM users WHERE name = '" + username + "';");
        db.executeQuerySQL("DELETE FROM taken_routes WHERE name = '" + username + "';");
        db.executeQuerySQL("DELETE FROM favourite_routes WHERE name = '" + username + "';");
        db.executeQuerySQL("DELETE FROM favourite_wifi WHERE name = '" + username + "';");
        db.executeQuerySQL("DELETE FROM favourite_retail WHERE name = '" + username + "';");
        db.executeQuerySQL("DELETE FROM lists WHERE list_owner = '" + username + "';");
    }
}
