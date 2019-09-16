package dataManipulation;

import dataHandler.SQLiteDB;
import dataObjects.Route;
import javafx.concurrent.Task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Route filterer class.
 */
public class RouteFiltererTask extends Task<Void> {

    private String routeCommand;
    private String genderCommand;
    private String timeCommand;
    private String dateCommand;
    private String startAddressCommand;
    private String endAddressCommand;
    private String bikeIDCommand;
    private String listCommand;
    private String getAllRoutesCommand;

    private String andCommand;
    private String commandEnd;

    private ArrayList<Integer> filterVariables;
    private ArrayList<String> filterVariableStrings;


    private SQLiteDB db;
    private int gender;
    private String dateLower, dateUpper, timeLower, timeUpper, startLocation, endLocation, bikeID, list;
    private AddRouteCallback callback;


    /**
     * RouteFilterTask constructor.
     *
     * @param gender        of type int. A value of -1 means not to filter by gender, 1 means filter by males and 2
     *                      means filter by females
     * @param dateLower     of type String. It is the lower limit that a route was started on, specified by
     *                      the user
     * @param dateUpper     of type String. It is the upper limit that a route was started on, specified by
     *                      the user
     * @param timeLower     of type String. It is the lower time limit of starting a route the user wants to
     *                      filter by
     * @param timeUpper     of type String. It is the upper time limit of starting a route the user wants to
     *                      filter by
     * @param startLocation of type String. It is the starting address of a route that the user wants
     *                      to filter by
     * @param endLocation   of type String. It is the ending address of a route that the user wants
     *                      to filter by
     * @param bikeID        of type String. It is the bikeID of a route that the user want to filter by
     * @param list          of type String. It is the list name of the list the user wants to filter by
     */
    public RouteFiltererTask(SQLiteDB db, int gender, String dateLower, String dateUpper, String timeLower,
                             String timeUpper, String startLocation, String endLocation, String bikeID,
                             String list, AddRouteCallback callback) {
        //---Route Strings---
        routeCommand = "SELECT * FROM route_information WHERE ";
        genderCommand = "gender = ?";
        dateCommand = "start_year || start_month || start_day BETWEEN ? AND ?";
        timeCommand = "start_time BETWEEN ? AND ?";
        startAddressCommand = "start_station_name LIKE ?";
        endAddressCommand = "end_station_name LIKE ?";
        bikeIDCommand = "bikeid = ?";
        listCommand = "list_name = ?";
        getAllRoutesCommand = "SELECT * FROM route_information;";

        //---Other---
        andCommand = " AND ";
        commandEnd = ";";
        filterVariables = new ArrayList<>();
        filterVariableStrings = new ArrayList<>();
        this.db = db;
        this.gender = gender;
        this.dateLower = dateLower;
        this.dateUpper = dateUpper;
        this.timeLower = timeLower;
        this.timeUpper = timeUpper;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.bikeID = bikeID;
        this.list = list;
        this.callback = callback;

    }


    /**
     * filterRoutesWithCallback takes all the possible filter values for routes and creates a prepared statement to
     * query the database.
     */
    private void filterRoutesWithCallback() {
        String queryString;
        queryString = generateQueryString(gender, dateLower, dateUpper, timeLower, timeUpper, startLocation,
                endLocation, bikeID, list);
        if (queryString.equals(routeCommand)) {
            getAllRoutesWithCallback(callback);
        } else {
            try {
                System.out.println(1);
                PreparedStatement pstmt;
                System.out.println(2);
                System.out.println(queryString);
                pstmt = db.getPreparedStatement(queryString);
                System.out.println(3);
                setQueryParameters(pstmt);
                System.out.println(4);
                ResultSet rs = pstmt.executeQuery();
                System.out.println(5);
                generateRoutesWithCallback(rs, callback);
                System.out.println(6);
                rs.close();
                System.out.println("thread done!");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    /**
     * generates ArrayList of Routes using the results from the Result Set.
     *
     * @param rs Result Set of Routes from database
     * @param callback
     */
    private void generateRoutesWithCallback(ResultSet rs, AddRouteCallback callback) {
        try {
            int scalefactor = 1;
            ArrayList<Route> routes = new ArrayList<>();
            while (rs.next()) {
                if (isCancelled()) {
                    routes.clear();
                    return;
                }
                routes.add(new Route(rs.getInt("tripduration"), rs.getString("start_time"),
                        rs.getString("end_time"), rs.getString("start_day"),
                        rs.getString("start_month"), rs.getString("start_year"),
                        rs.getString("end_day"), rs.getString("end_month"),
                        rs.getString("end_year"), rs.getDouble("start_latitude"),
                        rs.getDouble("start_longitude"), rs.getDouble("end_latitude"),
                        rs.getDouble("end_longitude"), rs.getInt("start_station_id"),
                        rs.getInt("end_station_id"), rs.getString("start_station_name"),
                        rs.getString("end_station_name"), rs.getString("bikeid"),
                        rs.getInt("gender"), rs.getString("usertype"),
                        rs.getInt("birth_year"), rs.getString("list_name")));


                if (routes.size() == 1000 * scalefactor) {
                    scalefactor *= 2;
                    callback.addRoutes(routes);
                    routes.clear();
                }
            }
            System.out.println(routes.size());
            callback.addRoutes(routes);
            routes.clear();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * generateQueryString takes all the possible filter requirement values and appends the necessary strings onto the
     * end of the database query statement. A value of -1 (int) or null (string) means the parameter has not been set
     * by the user, and this will not be used in the query.
     *
     * @param gender        of type int. A value of -1 means not to filter by gender, 1 means filter by males and 2
     *                      means filter by females
     * @param dateLower     of type String. It is the lower limit that a route was started on, specified by
     *                      the user
     * @param dateUpper     dof type String. It is the upper limit that a route was started on, specified by
     *                      the user
     * @param timeLower     tof type String. It is the lower time limit of starting a route the user wants to
     *                      filter by
     * @param timeUpper     of type String. It is the upper time limit of starting a route the user wants to
     *                      filter by
     * @param startLocation of type String. It is the starting address of a route that the user wants
     *                      to filter by
     * @param endLocation   of type String. It is the ending address of a route that the user wants
     *                      to filter by
     * @param bikeID        of type String. It is the bikeID of a route that the user want to filter by.
     * @param list          of type String. It is the list name of the list the user wants to filter by.
     * @return queryCommand, of type String. This is the string that will be used as a query statement to the database
     */
    private String generateQueryString(int gender, String dateLower, String dateUpper, String timeLower,
                                       String timeUpper, String startLocation, String endLocation, String bikeID,
                                       String list) {
        String queryCommand = routeCommand;
        int queryLength = 0;

        if (gender != -1) {
            queryCommand = queryCommand + genderCommand;
            filterVariables.add(gender);
            queryLength += 1;
        }
        if (dateLower != null && dateUpper != null) {
            convertDates(dateLower, dateUpper);
            queryCommand = addAndToStmt(queryCommand, queryLength);
            queryCommand = queryCommand + dateCommand;
            queryLength += 1;
        }
        if (timeLower != null && timeUpper != null) {
            queryCommand = addAndToStmt(queryCommand, queryLength);
            queryCommand = queryCommand + timeCommand;
            filterVariableStrings.add(timeLower);
            filterVariableStrings.add(timeUpper);
            queryLength += 1;
        }
        if (startLocation != null) {
            queryCommand = addAndToStmt(queryCommand, queryLength);
            queryCommand = queryCommand + startAddressCommand;
            filterVariableStrings.add("%" + startLocation + "%");
            queryLength += 1;
        }
        if (endLocation != null) {
            queryCommand = addAndToStmt(queryCommand, queryLength);
            queryCommand = queryCommand + endAddressCommand;
            filterVariableStrings.add("%" + endLocation + "%");
            queryLength += 1;
        }
        if (bikeID != null) {
            queryCommand = addAndToStmt(queryCommand, queryLength);
            queryCommand = queryCommand + bikeIDCommand;
            filterVariableStrings.add(bikeID);
            queryLength += 1;
        }
        if (list != null) {
            queryCommand = addAndToStmt(queryCommand, queryLength);
            queryCommand = queryCommand + listCommand;
            filterVariableStrings.add(list);
            queryLength += 1;
        }
        if (queryLength > 0) {
            queryCommand = queryCommand + commandEnd;
        }
        System.out.println(queryCommand);
        return queryCommand;
    }


    /**
     * convertDates takes an upper and lower bound of dates and converts them into a single number. The date format
     * must be DD/MM/YYYY. The date will be converted into the format: YYYYMMDD. This allows the easy querying of dates
     * in the database.
     * <p>
     * Ex. date: 21/01/2016 will be converted into 20160121
     *
     * @param dateLower of type String
     * @param dateUpper of type String
     */
    private void convertDates(String dateLower, String dateUpper) {
        filterVariableStrings.add(dateLower.substring(6) + dateLower.substring(3, 5) + dateLower.substring(0, 2));
        filterVariableStrings.add(dateUpper.substring(6) + dateUpper.substring(3, 5) + dateUpper.substring(0, 2));
    }


    /**
     * addAndToStmt checks if the query statement has at least one other filter requirement included. If it does the
     * andCommand string is appended to the end of the query command. Takes a string, queryCommand, and a int,
     * queryLength, as parameters. Returns the query command.
     *
     * @param queryCommand of type String. This is the string that will be used as the query statement to
     *                     the database
     * @param queryLength  of type int. This is the number of filter requirements that have been added to the
     *                     queryCommand already
     * @return queryCommand, of type String. This is the string that will be used as a query statement to the database
     */
    private String addAndToStmt(String queryCommand, int queryLength) {
        if (queryLength > 0) {
            queryCommand = queryCommand + andCommand;
        }
        return queryCommand;
    }


    /**
     * setQueryParameters takes a PreparedStatement as a parameter and uses the values in class ArrayList variables,
     * filterVariables and filterVariableStrings, to set the parameters of the PreparedStatement.
     *
     * @param pstmt of type PreparedStatement. This is the query statement to be called to the database
     */
    private void setQueryParameters(PreparedStatement pstmt) {
        try {
            int i;
            for (i = 0; i < filterVariables.size(); i++) {
                pstmt.setInt(i + 1, filterVariables.get(i));
            }

            for (String filterVariableString : filterVariableStrings) {
                pstmt.setString(++i, filterVariableString);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Called if there is no filter options. Gets all routes from database.
     *
     * @param callback
     */
    private void getAllRoutesWithCallback(AddRouteCallback callback) {
        try {
            String queryString = getAllRoutesCommand;
            PreparedStatement pstmt;
            pstmt = db.getPreparedStatement(queryString);
            setQueryParameters(pstmt);

            ResultSet rs = pstmt.executeQuery();
            generateRoutesWithCallback(rs, callback);
            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Starts new thread to filter routes.
     *
     * @return null
     */
    @Override
    protected Void call() {
        System.out.println(this);
        filterRoutesWithCallback();
        return null;
    }
}
