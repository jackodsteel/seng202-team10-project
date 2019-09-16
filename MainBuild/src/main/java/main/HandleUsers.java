package main;

import dataHandler.DatabaseUser;
import dataHandler.SQLiteDB;
import dataObjects.Cyclist;
import dataObjects.RetailLocation;
import dataObjects.Route;
import dataObjects.WifiLocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.commons.text.WordUtils.capitalizeFully;


/**
 * Handles users on the start up screen to choose which user is to be logged in or created.
 */
public class HandleUsers {

    public Cyclist currentCyclist;
    private SQLiteDB db;


    /**
     * Initializes the database.
     * @param sqLiteDB database to be initialized.
     */
    public void init(SQLiteDB sqLiteDB) {
        db = sqLiteDB;
    }

    /**
     * Logs into the user whose parameter is handed into the function.
     * @param username user to be logged in
     */
    public void logIn(String username) {
        currentCyclist = new Cyclist(username);
        getUserDetails(username);
        getUserTakenRoutes();
        getUserRouteFavourites();
        getUserWifiFavourites();
        getUserRetailFavourites();
    }

    /**
     * Finds all of the users birth details and gender information then sets these to the current cyclists properties.
     * @param username the username of the user whose details are being retrieved
     */
    public void getUserDetails(String username) {
        ResultSet rs;
        rs = db.executeQuerySQL("SELECT birth_day, birth_month, birth_year, gender FROM users WHERE name = '" + username + "';");
        try {
            currentCyclist.setBirthday(rs.getInt("birth_day"), rs.getInt("birth_month"),
                    rs.getInt("birth_year"));
            currentCyclist.setGender(rs.getInt("gender"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets all of the Cyclists taken Route's and adds them to the instances list.
     */
    public void getUserTakenRoutes() {
        ResultSet rsFavourites, rsRoute;
        String name = currentCyclist.getName();
        Route tempRoute;
        try {
            rsFavourites = db.executeQuerySQL("SELECT * FROM taken_routes WHERE name = '" + name + "';");
            while (rsFavourites.next()) {
                PreparedStatement ps = db.getPreparedStatement("SELECT * FROM route_information where start_year = ? " +
                        "AND start_month = ? AND start_day = ? AND start_time = ? AND bikeid = ?");
                ps.setString(1, rsFavourites.getString(2));
                ps.setString(2, rsFavourites.getString(3));
                ps.setString(3, rsFavourites.getString(4));
                ps.setString(4, rsFavourites.getString(5));
                ps.setString(5, rsFavourites.getString(6));
                rsRoute = ps.executeQuery();
                tempRoute = new Route(rsRoute.getInt("tripduration"), rsRoute.getString("start_time"),
                        rsRoute.getString("end_time"), rsRoute.getString("start_day"),
                        rsRoute.getString("start_month"), rsRoute.getString("start_year"),
                        rsRoute.getString("end_day"), rsRoute.getString("end_month"),
                        rsRoute.getString("end_year"), rsRoute.getDouble("start_latitude"),
                        rsRoute.getDouble("start_longitude"), rsRoute.getDouble("end_latitude"),
                        rsRoute.getDouble("end_longitude"), rsRoute.getInt("start_station_id"),
                        rsRoute.getInt("end_station_id"), rsRoute.getString("start_station_name"),
                        rsRoute.getString("end_station_name"), rsRoute.getString("bikeid"),
                        rsRoute.getInt("gender"), rsRoute.getString("usertype"),
                        rsRoute.getInt("birth_year"), rsRoute.getString("list_name"),
                        rsFavourites.getFloat("distance"));
                currentCyclist.addTakenRouteInstance(tempRoute);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets all of the Cyclists favourite Route's and adds them to the instances list.
     */
    public void getUserRouteFavourites() {
        ResultSet rsFavourites, rsRoute;
        String name = currentCyclist.getName();
        Route tempRoute;
        try {
            rsFavourites = db.executeQuerySQL("SELECT * FROM favourite_routes WHERE name = '" + name + "';");
            while (rsFavourites.next()) {
                PreparedStatement ps = db.getPreparedStatement("SELECT * FROM route_information where start_year = ? " +
                        "AND start_month = ? AND start_day = ? AND start_time = ? AND bikeid = ?");
                ps.setString(1, rsFavourites.getString(2));
                ps.setString(2, rsFavourites.getString(3));
                ps.setString(3, rsFavourites.getString(4));
                ps.setString(4, rsFavourites.getString(5));
                ps.setString(5, rsFavourites.getString(6));
                rsRoute = ps.executeQuery();
                tempRoute = new Route(rsRoute.getInt("tripduration"), rsRoute.getString("start_time"),
                        rsRoute.getString("end_time"), rsRoute.getString("start_day"),
                        rsRoute.getString("start_month"), rsRoute.getString("start_year"),
                        rsRoute.getString("end_day"), rsRoute.getString("end_month"),
                        rsRoute.getString("end_year"), rsRoute.getDouble("start_latitude"),
                        rsRoute.getDouble("start_longitude"), rsRoute.getDouble("end_latitude"),
                        rsRoute.getDouble("end_longitude"), rsRoute.getInt("start_station_id"),
                        rsRoute.getInt("end_station_id"), rsRoute.getString("start_station_name"),
                        rsRoute.getString("end_station_name"), rsRoute.getString("bikeid"),
                        rsRoute.getInt("gender"), rsRoute.getString("usertype"),
                        rsRoute.getInt("birth_year"), rsRoute.getString("list_name"),
                        rsFavourites.getInt("rank"));
                System.out.println("HERE");
                currentCyclist.addFavouriteRouteInstance(tempRoute);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Gets all of the Cyclists favourite WifiLocation's and adds them to the instances list.
     */
    public void getUserWifiFavourites() {
        ResultSet rsFavourites, rsWifi;
        String name = currentCyclist.getName();
        WifiLocation tempWifi;
        try {
            rsFavourites = db.executeQuerySQL("SELECT * FROM favourite_wifi WHERE name = '" + name + "';");
            while (rsFavourites.next()) {
                PreparedStatement ps = db.getPreparedStatement("SELECT * FROM wifi_location where WIFI_ID = ?");

                ps.setString(1, rsFavourites.getString(2));
                rsWifi = ps.executeQuery();
                tempWifi = new WifiLocation(rsWifi.getString("wifi_id"), rsWifi.getDouble("lat"),
                        rsWifi.getDouble("lon"), rsWifi.getString("address"),
                        rsWifi.getString("ssid"), rsWifi.getString("cost"),
                        rsWifi.getString("provider"), rsWifi.getString("remarks"),
                        rsWifi.getString("city"), rsWifi.getString("suburb"),
                        rsWifi.getInt("zip"), rsWifi.getString("list_name"));
                currentCyclist.addWifiInstance(tempWifi);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets all of the Cyclists favourite RetailLocation's and adds them to the instances list.
     */
    public void getUserRetailFavourites() {
        ResultSet rsFavourites, rsRetail;
        String name = currentCyclist.getName();
        RetailLocation tempRetail;
        try {
            rsFavourites = db.executeQuerySQL("SELECT * FROM favourite_retail WHERE name = '" + name + "';");
            while (rsFavourites.next()) {
                PreparedStatement ps = db.getPreparedStatement("SELECT * FROM retailer where RETAILER_NAME = ? AND ADDRESS = ?");

                ps.setString(1, rsFavourites.getString(2));
                ps.setString(2, rsFavourites.getString(3));
                rsRetail = ps.executeQuery();
                tempRetail = new RetailLocation(rsRetail.getString("retailer_name"),
                        rsRetail.getString("address"), rsRetail.getString("city"),
                        rsRetail.getString("main_type"), rsRetail.getString("secondary_type"),
                        rsRetail.getString("state"), rsRetail.getInt("zip"),
                        rsRetail.getDouble("lat"), rsRetail.getDouble("long"),
                        rsRetail.getString("list_name"));
                currentCyclist.addRetailInstance(tempRetail);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    /**
     * Logs out of the currently logged in user by terminating the instance of the currentCyclist.
     */
    public void logOutOfUser() {
        currentCyclist = null;
    }


    /**
     * Checks if the username already exists, if not it creates a new user and adds them to the users list.
     * @param username the users name they want to use
     * @param day the users day of birth
     * @param month the users month of birth
     * @param year the users year of birth
     * @param gender the users gender
     */
    public boolean createNewUser(String username, int day, int month, int year, String gender) {
        ResultSet rs;
        boolean created = false;
        try {
            rs = db.executeQuerySQL("SELECT * FROM users WHERE NAME = '" + username + "';");
            String s = rs.getString("NAME");

        } catch (SQLException e) {
            e.getMessage();
            String name = capitalizeFully(username);
            int genderInt = convertGender(gender);
            currentCyclist = new Cyclist(name, day, month, year, genderInt);
            DatabaseUser d = new DatabaseUser(db);
            d.addUser(name, day, month, year, genderInt);
            created = true;
        }
        return created;
    }

    /**
     * Converts a gender to a string so that it can correctly be entered into the database.
     * @param gender string to be converted into an integer
     * @return 0 for other, 1 for male, 2 for female.
     */
    public int convertGender(String gender) {
        if (gender.equals("Male")) {
            return 1;
        } else if (gender.equals("Female")) {
            return 2;
        } else {
            return 0;
        }
    }
}
