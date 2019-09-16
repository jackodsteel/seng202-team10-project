package dataHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * ListDataHandler creates the lists table in the database and adds list details to the table.
 */
public class ListDataHandler {

    private static final String tableName = "lists";
    private static final String[] tableFields = {
            "list_name    VARCHAR(50)",
            "list_owner   VARCHAR(12)"};
    private static final String primaryKey = "list_name";

    private static String listName;
    private final SQLiteDB db;

    private String userName;
    private String addListCommand = "insert or fail into lists values(?,?)";


    /**
     * Constructor for ListDataHandler, creates new table in database.
     *
     * @param db the database connection.
     */
    public ListDataHandler(SQLiteDB db, String userName) {
        this.db = db;
        this.userName = userName;
        db.addTable(tableName, tableFields, primaryKey);
    }


    /**
     * gets the listName variable.
     *
     * @return listName of type String
     */
    public static String getListName() {
        return listName;
    }

    /**
     * sets the listName variable to the given name.
     *
     * @param name type String
     */
    public static void setListName(String name) {
        if (name == null || name.equals("")) {
            listName = null;
        } else {
            listName = name;
        }
    }

    /**
     * checks name of list to be created hasn't all ready been created by another user.
     */
    public boolean checkListName(String listName) {
        try {
            ResultSet rs = db.executeQuerySQL("SELECT list_name FROM lists WHERE list_owner != '" + userName + "';");
            while (rs.next()) {
                if (rs.getString("list_name").equals(listName)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Creates a ArrayList of all the lists that the user had created.
     *
     * @return lists of type ArrayList
     */
    public ArrayList getLists() {
        ArrayList<String> lists = new ArrayList<>();
        System.out.println(userName);
        try {
            ResultSet rs = db.executeQuerySQL("SELECT list_name FROM lists WHERE list_owner = '" + userName + "';");
            while (rs.next()) {
                lists.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lists;
    }


    /**
     * adds a new list to the table from the given listName and the current user.
     *
     * @param listName type String. The name of the new list to be added to the database.
     */
    public void addList(String listName) {
        if (listName == null) {
            return;
        } else if (listName.equals("")) {
            return;
        } else {
            try {
                String addListCommand = "insert or fail into lists values(?,?)";
                PreparedStatement pstmt = db.getPreparedStatement(addListCommand);
                pstmt.setString(1, listName);
                pstmt.setString(2, userName);
                pstmt.executeUpdate();
                db.commit();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
