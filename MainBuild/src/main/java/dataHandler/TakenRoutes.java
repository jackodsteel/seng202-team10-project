package dataHandler;

import dataAnalysis.Route;
import main.HandleUsers;
import main.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by jto59 on 6/10/17.
 */
public class TakenRoutes {

    private SQLiteDB db;
    private String[] fields =
            {"name         VARCHAR(12)",
                    "start_year   VARCHAR(4)",
                    "start_month  VARCHAR(2)",
                    "start_day    VARCHAR(2)",
                    "start_time   VARCHAR(19)",
                    "bikeid       VARCHAR(20)",
                    "distance     DOUBLE"};
    private String primaryKey = "name, start_year, start_month, start_day, start_time, bikeid";
    private String tableName = "taken_routes";
    private PreparedStatement addTakenRoute;
    private String addRouteStatement = "insert or fail into taken_routes values(?,?,?,?,?,?,?)";


    /**
     * Initializes the database when creating an instance of the FavouriteRouteData.
     *
     * @param db database the retail data is added to
     */
    public TakenRoutes(SQLiteDB db) {
        this.db = db;
        System.out.println(db.addTable(tableName, fields, primaryKey));
        addTakenRoute = db.getPreparedStatement(addRouteStatement);
    }

    /**
     * Adds the given name, start year, start month, start day, start time, bike ID and distance to the table.
     *
     * @param name        name of the user
     * @param start_year  year the route started
     * @param start_month month the route started
     * @param start_day   day the route started
     * @param start_time  time the route started
     * @param bike_id     identification number of the bike
     * @param distance    distance of  the route
     */
    public void addTakenRoute(String name, String start_year, String start_month, String start_day,
                                  String start_time, String bike_id, double distance, HandleUsers hu) {//---------------test
        try {
            addTakenRoute.setObject(1, name);
            addTakenRoute.setObject(2, start_year);
            addTakenRoute.setObject(3, start_month);
            addTakenRoute.setObject(4, start_day);
            addTakenRoute.setObject(5, start_time);
            addTakenRoute.setObject(6, bike_id);
            addTakenRoute.setObject(7, distance);
            addTakenRoute.executeUpdate();
            db.commit();
            hu.currentCyclist.updateUserRouteFavourites(hu);

        } catch (SQLException e) {
            addTakenRoute = db.getPreparedStatement(addRouteStatement);
            System.out.println(e.getMessage());
        }
    }

    /**
     * Deletes the given route from the taken_routes table.
     *
     * @param route the route to be deleted
     */
    public void deleteTakenRoute(Route route) {
        db.executeQuerySQL("DELETE FROM taken_routes WHERE name = '" + Main.hu.currentCyclist.name + "' " +
                "AND start_year = '" + route.getStartYear() + "' AND start_month = '" + route.getStartMonth() + "' " +
                "AND start_day = '" + route.getStartDay() + "' AND start_time = '" + route.getStartTime() + "' " +
                "AND bikeid = '" + route.getBikeID() + "';");
    }


    /**
     * Finds the five (or less) most recently taken routes found from the users taken_route table.
     * @return up to five of the most recently taken routes
     */
    public ArrayList<String> findFiveRecentRoutes() {
        ResultSet rs;
        ArrayList<String> recentRoutes = new ArrayList<>();
        rs = db.executeQuerySQL("SELECT start_year, start_month, start_day, start_time, start_time, distance FROM taken_routes " +
                                "WHERE name = '" + Main.hu.currentCyclist.getName() + "' " +
                                "ORDER BY start_year DESC, start_month DESC, start_day DESC, start_time DESC, start_time DESC;");

        for(int i = 0; i < 5; i++) {
            try {
                rs.next();
                System.out.println("START DAY = " + rs.getString("start_day"));
                recentRoutes.add(rs.getString("start_year") + " " + rs.getString("start_month") +
                        " " + rs.getString("start_day") + " " + rs.getString("start_time") +
                        "|" + rs.getDouble("distance"));

            } catch (SQLException e) {
                addTakenRoute = db.getPreparedStatement(addRouteStatement);
                System.out.println(e.getMessage());
            }
        }
        return recentRoutes;
    }
}
