package dataManipulation;

import dataHandler.SQLiteDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DeleteData class handles the deletion of routes, wifi locations and retailers from the database.
 */
public class DeleteData {

    public enum DeletionStatus {
        CAN_BE_DELETED,
        IN_ANOTHER_USERS_LIST,
        IN_ANOTHER_USERS_FAVOURITES,
        IN_ANOTHER_USERS_COMPLETED_ROUTES
    }

    private SQLiteDB db;
    private String userName;


    /**
     * DeleteData constructor.
     *
     * @param db database connection
     */
    public DeleteData(SQLiteDB db, String user) {
        this.db = db;
        userName = user;
    }


    /**
     * deletes the route identified by the given parameters from the database. Also removes this route from favourite
     * and completed routes tables. Prevents deletion if another user has the route in their favourite routes,
     * completed routes or a list they created.
     *
     * @param startTime The start time of a route
     * @param startDay The start day of a route
     * @param startMonth The start month of a route
     * @param startYear The start year of a route
     * @param bikeID The bike ID of a route
     */
    public void deleteRoute(String startTime, String startDay, String startMonth, String startYear, String bikeID) {
        int numLists = 3;
        String deleteRouteString = "DELETE FROM route_information WHERE bikeid = ? AND start_time = ? AND " +
                "start_day = ? AND start_month = ? AND start_year = ?;";
        String deleteRouteFavourite = "DELETE FROM favourite_routes WHERE bikeid = ? AND start_time = ? AND " +
                "start_day = ? AND start_month = ? AND start_year = ? AND name = ?;";
        String deleteRouteCompleted = "DELETE FROM taken_routes WHERE bikeid = ? AND start_time = ? AND " +
                "start_day = ? AND start_month = ? AND start_year = ? AND name = ?;";
        String[] deleteCommands = {deleteRouteString, deleteRouteFavourite, deleteRouteCompleted};

        try {
            for (int i = 0; i < numLists; i++) {
                System.out.println(deleteCommands[i]);
                PreparedStatement pstmt = db.getPreparedStatement(deleteCommands[i]);
                pstmt.setString(1, bikeID);
                pstmt.setString(2, startTime);
                pstmt.setString(3, startDay);
                pstmt.setString(4, startMonth);
                pstmt.setString(5, startYear);
                if (i > 0) {
                    pstmt.setString(6, userName);
                }
                pstmt.executeUpdate();

                System.out.println("delete attempted " + i);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Checks if another user has the route in their favourite routes, completed routes or a list they created.
     *
     * @param startTime The start time of a route
     * @param startDay The start day of a route
     * @param startMonth The start month of a route
     * @param startYear The start year of a route
     * @param bikeID The bike ID of a route
     * @return DeletionStatus advising if the record can be deleted, or if not, for what reason
     */
    public DeletionStatus checkRouteDeletionStatus(String startTime, String startDay, String startMonth, String startYear,
                                        String bikeID) {
        String listName;
        int count;

        String findRouteList = "SELECT list_name FROM route_information WHERE bikeid = ? AND start_time = ? AND " +
                "start_day = ? AND start_month = ? AND start_year = ?;";
        String findRouteListOwner = "SELECT count(*) FROM lists WHERE list_name = ? AND list_owner != ?;";
        String findRouteFavourite = "SELECT count(*) FROM favourite_routes WHERE bikeid = ? AND start_time = ? AND " +
                "start_day = ? AND start_month = ? AND start_year = ? AND name != ?;";
        String findRouteCompleted = "SELECT count(*) FROM taken_routes WHERE bikeid = ? AND start_time = ? AND " +
                "start_day = ? AND start_month = ? AND start_year = ? AND name != ?;";

        try {
            PreparedStatement pstmt = db.getPreparedStatement(findRouteList);
            pstmt.setString(1, bikeID);
            pstmt.setString(2, startTime);
            pstmt.setString(3, startDay);
            pstmt.setString(4, startMonth);
            pstmt.setString(5, startYear);

            ResultSet rs = pstmt.executeQuery();
            if (rs.getString(1) != null) {
                listName = rs.getString(1);
                pstmt = db.getPreparedStatement(findRouteListOwner);
                pstmt.setString(1, listName);
                pstmt.setString(2, userName);
                rs = pstmt.executeQuery();
                count = rs.getInt(1);
                if (count != 0) {
                    return DeletionStatus.IN_ANOTHER_USERS_LIST;
                }
            }

            pstmt = db.getPreparedStatement(findRouteFavourite);
            pstmt.setString(1, bikeID);
            pstmt.setString(2, startTime);
            pstmt.setString(3, startDay);
            pstmt.setString(4, startMonth);
            pstmt.setString(5, startYear);
            pstmt.setString(6, userName);
            rs = pstmt.executeQuery();
            if (rs.getInt(1) != 0) {
                return DeletionStatus.IN_ANOTHER_USERS_FAVOURITES;
            }

            pstmt = db.getPreparedStatement(findRouteCompleted);
            pstmt.setString(1, bikeID);
            pstmt.setString(2, startTime);
            pstmt.setString(3, startDay);
            pstmt.setString(4, startMonth);
            pstmt.setString(5, startYear);
            pstmt.setString(6, userName);
            rs = pstmt.executeQuery();
            if (rs.getInt(1) != 0) {
                return DeletionStatus.IN_ANOTHER_USERS_COMPLETED_ROUTES;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return DeletionStatus.CAN_BE_DELETED;
    }


    /**
     * deletes the wifi location identified by the given parameters from the database. Also removes this wifi location
     * from the favourite wifi location table. Prevents deletion if another user has the route in their favourite wifi
     * or a list they created.
     *
     * @param wifiID the ID number for a wifiLocation
     */
    public void deleteWifiLocation(String wifiID) {
        String deleteWifi = "DELETE FROM wifi_location WHERE wifi_id = ?;";
        String deleteWifiFavourite = "DELETE FROM favourite_wifi WHERE wifi_id = ? AND name = ?;";

        try {
            PreparedStatement pstmt = db.getPreparedStatement(deleteWifi);
            pstmt.setString(1, wifiID);
            pstmt.executeUpdate();
            System.out.println("delete attempted " + 0);

            pstmt = db.getPreparedStatement(deleteWifiFavourite);
            pstmt.setString(1, wifiID);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
            System.out.println("delete attempted " + 1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Checks if another user has the wifi location in their favourite wifi or a list they created.
     *
     * @param wifiID the ID number for a wifiLocation
     * @return DeletionStatus advising if the record can be deleted, or if not, for what reason
     */
    public DeletionStatus checkWifiDeletionStatus(String wifiID) {
        String listName;
        int count;

        String findWifiList = "SELECT list_name FROM wifi_location WHERE wifi_id = ?;";
        String findWifiListOwner = "SELECT count(*) FROM lists WHERE list_name = ? AND list_owner != ?;";
        String findWifiFavourite = "SELECT count(*) FROM favourite_wifi WHERE wifi_id = ? AND name != ?;";

        try {
            PreparedStatement pstmt = db.getPreparedStatement(findWifiList);
            pstmt.setString(1, wifiID);

            ResultSet rs = pstmt.executeQuery();
            if (rs.getString(1) != null) {
                listName = rs.getString(1);
                pstmt = db.getPreparedStatement(findWifiListOwner);
                pstmt.setString(1, listName);
                pstmt.setString(2, userName);
                rs = pstmt.executeQuery();
                count = rs.getInt(1);
                if (count != 0) {
                    return DeletionStatus.IN_ANOTHER_USERS_LIST;
                }
            }

            pstmt = db.getPreparedStatement(findWifiFavourite);
            pstmt.setString(1, wifiID);
            pstmt.setString(2, userName);
            rs = pstmt.executeQuery();
            if (rs.getInt(1) != 0) {
                return DeletionStatus.IN_ANOTHER_USERS_FAVOURITES;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return DeletionStatus.CAN_BE_DELETED;
    }


    /**
     * deletes the retail location identified by the given parameters from the database. Also removes this retail
     * location from the favourite retailer table.
     *
     * @param retailName the name of a retail location
     * @param address the address of a retail location
     */
    public void deleteRetailer(String retailName, String address) {
        String deleteRetailer = "DELETE FROM retailer WHERE retailer_name = ? AND address = ?;";
        String deleteRetailFavourite = "DELETE FROM favourite_retail WHERE retailer_name = ? AND address = ? AND " +
                "name = ?;";

        try {
            PreparedStatement pstmt = db.getPreparedStatement(deleteRetailer);
            pstmt.setString(1, retailName);
            pstmt.setString(2, address);
            pstmt.executeUpdate();
            System.out.println("delete attempted " + 0);

            pstmt = db.getPreparedStatement(deleteRetailFavourite);
            pstmt.setString(1, retailName);
            pstmt.setString(2, address);
            pstmt.setString(3, userName);
            pstmt.executeUpdate();
            System.out.println("delete attempted " + 1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Checks if another user has the retail location in their favourite retailers or a list they created.
     *
     * @param retailName the name of a retail location
     * @param address the address of a retail location
     * @return DeletionStatus advising if the record can be deleted, or if not, for what reason
     */
    public DeletionStatus checkRetailDeletionStatus(String retailName, String address) {
        String listName;
        int count;

        String findRetailList = "SELECT list_name FROM retailer WHERE retailer_name = ? AND address = ?;";
        String findRetailListOwner = "SELECT count(*) FROM lists WHERE list_name = ? AND list_owner != ?;";
        String findRetailFavourite = "SELECT count(*) FROM favourite_retail WHERE retailer_name = ? AND address = ?" +
                "AND name != ?;";

        try {
            PreparedStatement pstmt = db.getPreparedStatement(findRetailList);
            pstmt.setString(1, retailName);
            pstmt.setString(2, address);

            ResultSet rs = pstmt.executeQuery();
            if (rs.getString(1) != null) {
                listName = rs.getString(1);
                pstmt = db.getPreparedStatement(findRetailListOwner);
                pstmt.setString(1, listName);
                pstmt.setString(2, userName);
                rs = pstmt.executeQuery();
                count = rs.getInt(1);
                if (count != 0) {
                    return DeletionStatus.IN_ANOTHER_USERS_LIST;
                }
            }

            pstmt = db.getPreparedStatement(findRetailFavourite);
            pstmt.setString(1, retailName);
            pstmt.setString(2, address);
            pstmt.setString(3, userName);
            rs = pstmt.executeQuery();
            if (rs.getInt(1) != 0) {
                return DeletionStatus.IN_ANOTHER_USERS_FAVOURITES;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return DeletionStatus.CAN_BE_DELETED;
    }
}
