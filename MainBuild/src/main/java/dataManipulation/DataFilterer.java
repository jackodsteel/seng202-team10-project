package dataManipulation;

import dataHandler.SQLiteDB;
import dataObjects.RetailLocation;
import dataObjects.Route;
import dataObjects.WifiLocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * DataFilterer class contains methods that sent requests to the database and return a array of data based on what
 * filtering parameters are passed in.
 */
public class DataFilterer {

    //---Wifi Strings---
    private String wifiCommand;
    private String wifiNameCommand;
    private String boroughCommand;
    private String typeCommand;
    private String providerCommand;
    private String getAllWifiCommand;

    //---Retailer Commands---
    private String retailerCommand;
    private String retailerNameCommand;
    private String streetCommand;
    private String zipCommand;
    private String primaryCommand;
    private String getAllRetailersCommand;

    //---Other---
    private String listCommand;
    private String andCommand;
    private String commandEnd;

    private ArrayList<Route> routes;
    private ArrayList<Integer> filterVariables;
    private ArrayList<String> filterVariableStrings;

    private ArrayList<WifiLocation> wifiLocations;
    private ArrayList<RetailLocation> retailLocations;

    private SQLiteDB db;


    /**
     * Constructor for DataFilterer class.
     *
     * @param db
     */
    public DataFilterer(SQLiteDB db) {
        setVariables();
        this.db = db;
    }

    private void setVariables() {
        //---Wifi Strings---
        wifiCommand = "SELECT * FROM wifi_location WHERE ";
        wifiNameCommand = "SSID LIKE ?";
        boroughCommand = "suburb LIKE ?";
        typeCommand = "cost LIKE ?";
        providerCommand = "provider LIKE ?";
        getAllWifiCommand = "SELECT * FROM wifi_location;";
        //---Retailer Strings---
        retailerCommand = "SELECT * FROM retailer WHERE ";
        retailerNameCommand = "retailer_name LIKE ?";
        streetCommand = "address LIKE ?";
        zipCommand = "zip = ?";
        primaryCommand = "main_type LIKE ?";
        getAllRetailersCommand = "SELECT * FROM retailer;";

        //---Other---
        listCommand = "list_name = ?";
        andCommand = " AND ";
        commandEnd = ";";
        routes = new ArrayList<>();
        filterVariables = new ArrayList<>();
        filterVariableStrings = new ArrayList<>();
        wifiLocations = new ArrayList<>();
        retailLocations = new ArrayList<>();
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



///////////////////////////////---WIFI FILTERING---\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * GenerateWifiArray takes a result set (set of records received from a database query) and creates a WifiLocation
     * from each result and adds them to an ArrayList.
     *
     * @param rs is a result set of data records from a query to the database
     */
    private void generateWifiArray(ResultSet rs) {
        try {
            while (rs.next()) {
                wifiLocations.add(new WifiLocation(rs.getString("wifi_id"), rs.getDouble("lat"),
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
     * getAllWifiLocations gets all wifi points from the database and returns these as WifiLocation objects in an
     * ArrayList.
     */
    private void getAllWifiLocations() {
        setVariables();
        String queryString = getAllWifiCommand;
        try {
            PreparedStatement pstmt;
            pstmt = db.getPreparedStatement(queryString);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("Got result set");
            generateWifiArray(rs);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * filterWifi takes all the possible filter values for wifi points and returns a ArrayList of WifiLocations that
     * meet the filter requirements.
     *
     * @param name     of type String. This is a sub string that the user wants to filter wifi SSIDs by
     * @param suburb   of type String. This is a string that the user wants to filter wifi suburbs by
     * @param type     of type String. This is a string that the user wants to filter wifi types by
     * @param provider of type String. This is a sub string that the user wants to filter providers by
     * @param list     of type String. It is the list name of the list the user wants to filter by
     * @return ArrayList<WifiLocation>, an ArrayList that contains WifiLocation objects
     */
    public ArrayList<WifiLocation> filterWifi(String name, String suburb, String type, String provider, String list) {
        setVariables();
        int queryLen = 0;
        String queryString = wifiCommand;

        if (name != null) {
            queryString = queryString + wifiNameCommand;
            queryLen += 1;
            filterVariableStrings.add("%" + name + "%");
        }
        if (suburb != null) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + boroughCommand;
            queryLen += 1;
            filterVariableStrings.add(suburb);
        }
        if (type != null) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + typeCommand;
            queryLen += 1;
            filterVariableStrings.add(type);
        }
        if (provider != null) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + providerCommand;
            queryLen += 1;
            filterVariableStrings.add("%" + provider + "%");
        }
        if (list != null) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + listCommand;
            queryLen += 1;
            filterVariableStrings.add(list);
        }
        if (queryLen > 0) {
            queryString = queryString + commandEnd;
        }
        if (queryString.equals(wifiCommand)) {
            getAllWifiLocations();
            return wifiLocations;
        }

        try {
            PreparedStatement pstmt;
            pstmt = db.getPreparedStatement(queryString);
            for (int i = 0; i < queryLen; i++) {
                pstmt.setString(i + 1, filterVariableStrings.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Got result set");
            generateWifiArray(rs);
            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return wifiLocations;
    }


///////////////////////////////---RETAIL FILTERING---\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * GenerateRetailArray takes a result set (set of records received from a database query) and creates a
     * RetailLocation from each result and adds them to an ArrayList.
     *
     * @param rs is a result set of data records from a query to the database
     */
    private void generateRetailArray(ResultSet rs) {
        try {
            while (rs.next()) {
                retailLocations.add(new RetailLocation(rs.getString("retailer_name"),
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
     * getAllRetailLocations gets all retailers from the database and returns these as RetailLocations objects in an
     * ArrayList.
     */
    private void getAllRetailLocations() {
        setVariables();
        String queryString = getAllRetailersCommand;
        try {
            PreparedStatement pstmt;
            pstmt = db.getPreparedStatement(queryString);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("Got result set");
            generateRetailArray(rs);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * filterRetailers takes all the possible filter values for retailers and returns a ArrayList of retailLocations
     * that meet the filter requirements.
     *
     * @param name    of type String. This is a sub string that the user wants to filter retail names by
     * @param address of type String. This is a sub string that the user wants to filter retail addresses by
     * @param primary of type String. This is a string that the user wants to filter retail primary types by
     * @param zip     of type int. This is a integer that the user wants to filter retailer zip codes by
     * @param list    of type String. It is the list name of the list the user wants to filter by
     * @return ArrayList<RetailLocation>, this is an ArrayList of RetailLocations objects
     */
    public ArrayList<RetailLocation> filterRetailers(String name, String address, String primary, int zip, String list) {
        setVariables();
        int queryLen = 0;
        String queryString = retailerCommand;

        if (name != null) {
            queryString = queryString + retailerNameCommand;
            queryLen += 1;
            filterVariableStrings.add("%" + name + "%");
        }
        if (address != null) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + streetCommand;
            queryLen += 1;
            filterVariableStrings.add("%" + address + "%");
        }
        if (primary != null) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + primaryCommand;
            queryLen += 1;
            filterVariableStrings.add("%" + primary + "%");
        }
        if (zip != -1) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + zipCommand;
            queryLen += 1;
            filterVariables.add(zip);
        }
        if (list != null) {
            queryString = addAndToStmt(queryString, queryLen);
            queryString = queryString + listCommand;
            queryLen += 1;
            filterVariableStrings.add(list);
        }

        if (queryLen > 0) {
            queryString = queryString + commandEnd;
        }

        if (queryString.equals(retailerCommand)) {
            getAllRetailLocations();
            return retailLocations;
        }
        try {
            PreparedStatement pstmt;
            pstmt = db.getPreparedStatement(queryString);
            int i;
            for (i = 0; i < filterVariableStrings.size(); i++) {
                pstmt.setString(i + 1, filterVariableStrings.get(i));
            }
            for (Integer filterVariable : filterVariables) {
                pstmt.setInt(i + 1, filterVariable);
                i++;
            }

            ResultSet rs = pstmt.executeQuery();
            System.out.println("Got result set");
            generateRetailArray(rs);
            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return retailLocations;
    }
}

