package dataHandler;

import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RouteDataHandler implements DataHandler {

    private static final String[] fields = {
            "tripduration            INTEGER",
            "start_year              VARCHAR(4) NOT NULL",
            "start_month             VARCHAR(2) NOT NULL",
            "start_day               VARCHAR(2) NOT NULL",
            "start_time              VARCHAR(19) NOT NULL",
            "end_year                VARCHAR(4)",
            "end_month               VARCHAR(2)",
            "end_day                 VARCHAR(2)",
            "end_time                VARCHAR(19)",
            "start_station_id        VARCHAR(6)",
            "start_station_name      VARCHAR(21)",
            "start_latitude          NUMERIC(9,6) NOT NULL",
            "start_longitude         NUMERIC(9,6) NOT NULL",
            "end_station_id          VARCHAR(6)",
            "end_station_name        VARCHAR(21)",
            "end_latitude            NUMERIC(9,6) NOT NULL",
            "end_longitude           NUMERIC(9,6) NOT NULL",
            "bikeid                  VARCHAR(20) NOT NULL",
            "usertype                VARCHAR(10)",
            "birth_year              INTEGER",
            "gender                  INTEGER",
            "list_name               VARCHAR(25)"};
    private static final String primaryKey = "start_year, start_month, start_day, start_time, bikeid";
    private static final String tableName = "route_information";

    private final SQLiteDB db;

    private PreparedStatement addData;
    private String addDataStatement = "insert or fail into route_information values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private int fieldCount = 15;

    /**
     * Initializes an object, linked to the given database. Can process CSVs and add single entries
     *
     * @param db
     */
    public RouteDataHandler(SQLiteDB db) {
        this.db = db;
        db.addTable(tableName, fields, primaryKey);
        addData = db.getPreparedStatement(addDataStatement);
    }

    /**
     * Processes a CSV line and adds to the database if valid.
     *
     * @param record A string array of object corresponding to the CSV
     */
    public void processLine(String[] record, Callback callback) {
        try {
            int duration = Integer.parseInt(record[0]);

            String[] date_time_start = record[1].split(" ");
            String[] date_start = date_time_start[0].split("/");
            String start_year = date_start[2];
            String start_month = StringUtils.leftPad(date_start[0], 2, '0');
            String start_day = StringUtils.leftPad(date_start[1], 2, '0');
            String start_time = date_time_start[1];

            String[] date_time_end = record[2].split(" ");
            String[] date_end = date_time_start[0].split("/");
            String end_year = date_end[2];
            String end_month = StringUtils.leftPad(date_end[0], 2, '0');
            String end_day = StringUtils.leftPad(date_end[1], 2, '0');
            String end_time = date_time_end[1];


            double start_lat = Double.parseDouble(record[5]);
            double start_lon = Double.parseDouble(record[6]);
            double end_lat = Double.parseDouble(record[9]);
            double end_lon = Double.parseDouble(record[10]);

            int gender = Integer.parseInt(record[14]);
            Integer birth_year;
            if (record[13].equals("")) {
                birth_year = null;
            } else {
                birth_year = Integer.parseInt(record[13]);
            }

            callback.result(addSingleEntry(
                    duration, start_year, start_month, start_day, start_time, end_year,
                    end_month, end_day, end_time, record[3], record[4], start_lat, start_lon, record[7],
                    record[8], end_lat, end_lon, record[11], record[12], birth_year, gender));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Incorrect string array size");
            callback.result(false);
        }
    }

    /**
     * Takes a full list of parameters for an element in the table and adds that to the database using a PreparedStatement
     *
     * @param tripduration
     * @param start_year
     * @param start_month
     * @param start_day
     * @param start_time
     * @param end_year
     * @param end_month
     * @param end_day
     * @param end_time
     * @param start_station_id
     * @param start_station_name
     * @param start_latitude
     * @param start_longitude
     * @param end_station_id
     * @param end_station_name
     * @param end_latitude
     * @param end_longitude
     * @param bikeid
     * @param usertype
     * @param birth_year
     * @param gender
     * @return A value representing the success of the addition. Fails on such things as PrimaryKey collisions.
     */
    public Boolean addSingleEntry(int tripduration, String start_year, String start_month, String start_day, String start_time,
                                  String end_year, String end_month, String end_day, String end_time, String start_station_id,
                                  String start_station_name, double start_latitude, double start_longitude,
                                  String end_station_id, String end_station_name, double end_latitude,
                                  double end_longitude, String bikeid, String usertype, Integer birth_year, int gender) {
        String listName = dataHandler.ListDataHandler.getListName();
        try {
            addData.setObject(1, tripduration);
            addData.setObject(2, start_year);
            addData.setObject(3, start_month);
            addData.setObject(4, start_day);
            addData.setObject(5, start_time);
            addData.setObject(6, end_year);
            addData.setObject(7, end_month);
            addData.setObject(8, end_day);
            addData.setObject(9, end_time);
            addData.setObject(10, start_station_id);
            addData.setObject(11, start_station_name);
            addData.setObject(12, start_latitude);
            addData.setObject(13, start_longitude);
            addData.setObject(14, end_station_id);
            addData.setObject(15, end_station_name);
            addData.setObject(16, end_latitude);
            addData.setObject(17, end_longitude);
            addData.setObject(18, bikeid);
            addData.setObject(19, usertype);
            addData.setObject(20, birth_year);
            addData.setObject(21, gender);
            addData.setObject(22, listName);
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

