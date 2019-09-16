package dataHandler;

import dataObjects.Route;
import main.HandleUsers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles all of the favourite route data for every user in the database.
 */
public class FavouriteRouteData {

    private static final String[] fields = {
            "name         VARCHAR(12)",
            "start_year   VARCHAR(4)",
            "start_month  VARCHAR(2)",
            "start_day    VARCHAR(2)",
            "start_time   VARCHAR(19)",
            "bikeid       VARCHAR(20)",
            "rank         INTEGER"};
    private static final String primaryKey = "name, start_year, start_month, start_day, start_time, bikeid";
    private static final String tableName = "favourite_routes";

    private final SQLiteDB db;

    private PreparedStatement addRoute;
    private String addRouteStatement = "insert or fail into favourite_routes values(?,?,?,?,?,?,?)";


    /**
     * Initializes the database when creating an instance of the FavouriteRouteData.
     *
     * @param db database the retail data is added to
     */
    public FavouriteRouteData(SQLiteDB db) {
        this.db = db;
        System.out.println(db.addTable(tableName, fields, primaryKey));
        addRoute = db.getPreparedStatement(addRouteStatement);
    }


    /**
     * Adds the given name, start year, start month, start day, start time, bike ID and rank to the table.
     *
     * @param name        name of the user
     * @param start_year  year the route started
     * @param start_month month the route started
     * @param start_day   day the route started
     * @param start_time  time the route started
     * @param bike_id     identification number of the bike
     * @param rank        rating of the route that the user has chosen
     * @param hu          the current HandleUsers object that is accessing the cyclists information
     */
    public void addFavouriteRoute(String name, String start_year, String start_month, String start_day,
                                  String start_time, String bike_id, int rank, HandleUsers hu) {
        try {
            addRoute.setObject(1, name);
            addRoute.setObject(2, start_year);
            addRoute.setObject(3, start_month);
            addRoute.setObject(4, start_day);
            addRoute.setObject(5, start_time);
            addRoute.setObject(6, bike_id);
            addRoute.setObject(7, rank);
            addRoute.executeUpdate();
            db.commit();
            hu.currentCyclist.updateUserRouteFavourites(hu);

        } catch (SQLException e) {
            addRoute = db.getPreparedStatement(addRouteStatement);
            System.out.println(e.getMessage());
        }
    }


    /**
     * Deletes the given route from the favourite_routes table.
     *
     * @param route the route to be deleted
     * @param hu    the current HandleUsers object that is accessing the cyclists information
     */
    public void deleteFavouriteRoute(Route route, HandleUsers hu) {
        db.executeQuerySQL("DELETE FROM favourite_routes WHERE name = '" + hu.currentCyclist.name + "' " +
                "AND start_year = '" + route.getStartYear() + "' AND start_month = '" + route.getStartMonth() + "' " +
                "AND start_day = '" + route.getStartDay() + "' AND start_time = '" + route.getStartTime() + "' " +
                "AND bikeid = '" + route.getBikeID() + "';");
    }
}
