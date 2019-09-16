package dataManipulation;

import dataHandler.SQLiteDB;
import dataObjects.RetailLocation;
import dataObjects.WifiLocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * FindNearByLocations class contains methods that find WifiLocation and RetailLocation objects given a set of
 * coordinates.
 */
public class FindNearbyLocations {

    private ArrayList<WifiLocation> nearbyWifi = new ArrayList<>();
    private ArrayList<RetailLocation> nearbyRetail = new ArrayList<>();
    private SQLiteDB db;


    /**
     * Constructor initializes the database for use within the FindNearByLocations class.
     *
     * @param database SQLite database connection
     */
    public FindNearbyLocations(SQLiteDB database) {
        db = database;
    }


    /**
     * generateWifiArray takes a result set, rs of wifi data entries from the database, it converts each one into a
     * WifiLocation object and adds them to and ArrayList.
     *
     * @param rs result set of data entries from the wifi_location table of the database
     */
    private void generateWifiArray(ResultSet rs) {
        try {
            nearbyWifi.clear();
            while (rs.next()) {
                nearbyWifi.add(new WifiLocation(rs.getString("wifi_id"), rs.getDouble("lat"),
                        rs.getDouble("lon"), rs.getString("address"),
                        rs.getString("ssid"), rs.getString("cost"),
                        rs.getString("provider"), rs.getString("remarks"),
                        rs.getString("city"), rs.getString("suburb"),
                        rs.getInt("zip"), rs.getString("list_name")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * generateRetailArray takes a result set, rs of retail data entries from the database, it converts each one into a
     * RetailLocation object and adds them to and ArrayList.
     *
     * @param rs result set of data entries from the retailer table of the database
     */
    private void generateRetailerArray(ResultSet rs) {
        try {
            nearbyRetail.clear();
            while (rs.next()) {
                nearbyRetail.add(new RetailLocation(rs.getString("retailer_name"),
                        rs.getString("address"), rs.getString("city"),
                        rs.getString("main_type"), rs.getString("secondary_type"),
                        rs.getString("state"), rs.getInt("zip"),
                        rs.getDouble("lat"), rs.getDouble("long"),
                        rs.getString("list_name")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     */
    private ResultSet getNearbyResults(PreparedStatement statement, double lat, double lon, double dist, double fudge) {
        try {
            statement.setDouble(1, lat - dist);
            statement.setDouble(2, lat + dist);
            statement.setDouble(3, lon - dist);
            statement.setDouble(4, lon + dist);
            statement.setDouble(5, lat);
            statement.setDouble(6, lat);
            statement.setDouble(7, lon);
            statement.setDouble(8, lon);
            statement.setDouble(9, fudge);
            return statement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    /**
     * findNearbyWifi finds wifi data entries that are nearby the coordinates given as the parameters, lat and lon.
     * Returns all found wifi entries as WifiLocation objects in an ArrayList.
     *
     * @param lat of type Double. A number describing a latitude value
     * @param lon of type Double. A number describing a longitude value
     * @return ArrayList<WifiLocation>, contains all results from the query.
     */
    public ArrayList<WifiLocation> findNearbyWifi(double lat, double lon) {

        double dist = 0.01;
        double fudge = Math.pow(Math.cos(Math.toRadians(lat)), 2);
        ResultSet rs;
        PreparedStatement statement = db.getPreparedStatement(
                "SELECT * FROM wifi_location WHERE LAT BETWEEN ? AND ? AND LON BETWEEN ? AND ?" +
                " order by ((? - LAT) * (? - LAT) + (? - LON) * (? - LON) * ?) limit 100");

        rs = getNearbyResults(statement, lat, lon, dist, fudge);
        generateWifiArray(rs);

        while (nearbyWifi.size() < 100) {
            dist *= 2;
            if (dist > 360) {
                return nearbyWifi;
            }
            rs = getNearbyResults(statement, lat, lon, dist, fudge);
            generateWifiArray(rs);
        }
        return nearbyWifi;
    }


    /**
     * findNearbyRetail finds retail data entries that are nearby the coordinates given as the parameters, lat and lon.
     * Returns all found retail entries as RetailLocation objects in an ArrayList.
     *
     * @param lat of type Double. A number describing a latitude value
     * @param lon of type Double. A number describing a longitude value
     * @return ArrayList<RetailLocation>, contains all results from the query.
     */
    public ArrayList<RetailLocation> findNearbyRetail(double lat, double lon) {

        double dist = 0.01;
        double fudge = Math.pow(Math.cos(Math.toRadians(lat)), 2);
        ResultSet rs;

        PreparedStatement statement = db.getPreparedStatement(
                "SELECT * FROM retailer WHERE lat BETWEEN ? AND ? AND long BETWEEN ? AND ?" +
                " order by ((? - lat) * (? - lat) + (? - long) * (? - long) * ?) limit 100");

        rs = getNearbyResults(statement, lat, lon, dist, fudge);
        generateRetailerArray(rs);

        while (nearbyRetail.size() < 100) {
            dist *= 2;
            if (dist > 360) {
                return nearbyRetail;
            }
            rs = getNearbyResults(statement, lat, lon, dist, fudge);
            generateRetailerArray(rs);
        }
        return nearbyRetail;
    }
}

