package dataHandler;

import dataAnalysis.RetailLocation;
import main.Main;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles all of the favourite retail data for every user in the database.
 */
public class FavouriteRetailData {

    private SQLiteDB db;
    private String[] fields =
            {"name         VARCHAR(12)",
                    "RETAILER_NAME    VARCHAR(50) NOT NULL",
                    "ADDRESS            VARCHAR(50)"};
    private String primaryKey = "name, RETAILER_NAME, ADDRESS";
    private String tableName = "favourite_retail";
    private PreparedStatement addRetail;
    private String addRetailStatement = "insert or fail into favourite_retail values(?,?,?)";


    /**
     * Initializes the database when creating an instance of the FavouriteRetailData.
     *
     * @param db database the retail data is added to
     */
    public FavouriteRetailData(SQLiteDB db) {
        this.db = db;
        db.addTable(tableName, fields, primaryKey);
        addRetail = db.getPreparedStatement(addRetailStatement);
    }


    /**
     * Adds the given name, retail name and address to the table.
     *
     * @param name        name of the user
     * @param retail_name name of the retail store
     * @param address     address of the retail store
     */
    public void addFavouriteRetail(String name, String retail_name, String address) {
        try {
            addRetail.setObject(1, name);
            addRetail.setObject(2, retail_name);
            addRetail.setObject(3, address);
            addRetail.executeUpdate();
            db.commit();
        } catch (SQLException e) {
            addRetail = db.getPreparedStatement(addRetailStatement);
            System.out.println(e.getMessage());
        }
    }


    /**
     * Deletes the given store from the database.
     *
     * @param store retail store to be deleted
     */
    public void deleteFavouriteRetail(RetailLocation store) {
        db.executeQuerySQL("DELETE FROM favourite_retail WHERE name = '" + Main.hu.currentCyclist.name + "' AND RETAILER_NAME = '" + store.getName() + "' " +
                "AND ADDRESS = '" + store.getAddress() + "';");
    }
}
