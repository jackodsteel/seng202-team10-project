package dataHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WifiDataHandler implements DataHandler {

    private static final String[] fields =
            {"WIFI_ID    VARCHAR(20) NOT NULL",
                    "COST       VARCHAR(12)",
                    "PROVIDER   VARCHAR(20)",
                    "ADDRESS    VARCHAR(50)",
                    "LAT        NUMERIC(9,6) NOT NULL",
                    "LON        NUMERIC(9,6) NOT NULL",
                    "REMARKS    VARCHAR(50)",
                    "CITY       VARCHAR(8)",
                    "SSID       VARCHAR(50) NOT NULL",
                    "SUBURB     VARCHAR(20)",
                    "ZIP        VARCHAR(8)",
                    "list_name  VARCHAR(25)"};
    private static final String primaryKey = "WIFI_ID";
    private static final String tableName = "wifi_location";

    private final SQLiteDB db;

    private PreparedStatement addData;
    private String addDataStatement = "insert or fail into wifi_location values(?,?,?,?,?,?,?,?,?,?,?,?)";

    private int fieldCount = 29;

    /**
     * Initializes an object, linked to the given database. Can process CSVs and add single entries
     *
     * @param db
     */
    public WifiDataHandler(SQLiteDB db) {
        this.db = db;
        db.addTable(tableName, fields, primaryKey);
        addData = db.getPreparedStatement(addDataStatement);
    }

    /**
     * Processes a CSV line and adds to the database if valid.
     *
     * @param record   A string array of object corresponding to the CSV
     * @param callback Used to callback a bool stating the success state of the process.
     */
    public void processLine(String[] record, Callback callback) {
        try {
            double lat = Double.parseDouble(record[7]);
            double lon = Double.parseDouble(record[8]);
            callback.result(addSingleEntry(record[0], record[3], record[4], record[6], lat, lon, record[12], record[13], record[14], record[18], record[22]));

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Incorrect string array size");
            callback.result(false);
        } catch (NumberFormatException e) {
            callback.result(false);
        }
    }

    /**
     * Takes a full list of parameters for an element in the table and adds that to the database using a PreparedStatement
     *
     * @param ID
     * @param COST
     * @param PROVIDER
     * @param ADDRESS
     * @param LAT
     * @param LONG
     * @param REMARKS
     * @param CITY
     * @param SSID
     * @param SUBURB
     * @param ZIP
     * @return A value representing the success of the addition. Fails on such things as PrimaryKey collisions.
     */
    public Boolean addSingleEntry(
            String ID, String COST, String PROVIDER, String ADDRESS, double LAT, double LONG,
            String REMARKS, String CITY, String SSID, String SUBURB, String ZIP) {
        String listName = dataHandler.ListDataHandler.getListName();
        try {
            addData.setObject(1, ID);
            addData.setObject(2, COST);
            addData.setObject(3, PROVIDER);
            addData.setObject(4, ADDRESS);
            addData.setObject(5, LAT);
            addData.setObject(6, LONG);
            addData.setObject(7, REMARKS);
            addData.setObject(8, CITY);
            addData.setObject(9, SSID);
            addData.setObject(10, SUBURB);
            addData.setObject(11, ZIP);
            addData.setObject(12, listName);
            addData.executeUpdate();
            return true;
        } catch (SQLException e) {
            addData = db.getPreparedStatement(addDataStatement);
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean canProcess(int columnCount) {
        return columnCount == fieldCount;
    }

    @Override
    public String getFieldCounts() {
        return Integer.toString(fieldCount);
    }
}
