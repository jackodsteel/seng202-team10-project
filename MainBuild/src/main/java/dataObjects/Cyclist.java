package dataObjects;

import dataHandler.*;
import main.HandleUsers;

import java.util.ArrayList;


/**
 * The user of the program gets an instance of this is created when they log in. It contains all the users
 * taken routes, favourite routes, retail stores and wifi locations.
 */
public class Cyclist {
    static public String name;
    static private int birthDay, birthMonth, birthYear;
    static private int gender;   // gender either 0 other, 1 male, or 2 female.
    private ArrayList<Route> favouriteRouteList = new ArrayList<>();
    private ArrayList<RetailLocation> favouriteRetailLocations = new ArrayList<>();
    private ArrayList<WifiLocation> favouriteWifiLocations = new ArrayList<>();
    private ArrayList<Route> takenRoutesList = new ArrayList<>();

    public Cyclist() {
    }

    public Cyclist(String cName) {
        name = cName;
    }

    public Cyclist(String username, int day, int month, int year, int usergender) {
        name = username;
        birthDay = day;
        birthMonth = month;
        birthYear = year;
        gender = usergender;
    }

    //Getters

    static public int getBirthYear() {
        return birthYear;
    }

    static public int getBirthMonth() {
        return birthMonth;
    }

    static public int getBirthDay() {
        return birthDay;
    }

    static public String getBirthDate() {
        return birthDay + "/" + birthMonth + "/" + birthYear;
    }

    static public int getGender() {
        return gender;
    }

    static public void setGender(int inputGender) {
        gender = inputGender;
    }

    static public String getName() {
        return name;
    }

    static public void setName(String newName) {
        name = newName;
    }

    static public void setBirthday(int day, int month, int year) {
        birthDay = day;
        birthMonth = month;
        birthYear = year;
    }

    public ArrayList<WifiLocation> getFavouriteWifiLocations() {
        return favouriteWifiLocations;
    }

    public ArrayList<RetailLocation> getFavouriteRetailLocations() {
        return favouriteRetailLocations;
    }

    public ArrayList<Route> getFavouriteRouteList() {
        return favouriteRouteList;
    }

    public ArrayList<Route> getTakenRoutes() {return takenRoutesList;}


    /**
     * Adds a route to the cyclists favouriteRoute list.
     * @param route the route to be added
     */
    public void addTakenRouteInstance(Route route) {
        takenRoutesList.add(route);
    }


    /**
     * Adds a route to the cyclists favouriteRoute list.
     * @param route the route to be added
     */
    public void addFavouriteRouteInstance(Route route) {
        favouriteRouteList.add(route);
    }


    /**
     * Adds a wifi location to the cyclists favouriteWifiLocation list.
     * @param wifi the wifi location to be added
     */
    public void addWifiInstance(WifiLocation wifi) {
        favouriteWifiLocations.add(wifi);
    }


    /**
     * Adds a retail store to the cyclists favouriteRetailLocation list.
     * @param retail the retail store to be added
     */
    public void addRetailInstance(RetailLocation retail) {
        favouriteRetailLocations.add(retail);
    }


    /**
     * Updates the users favourite routes. This will set the rank variable for the new Route object that has been added.
     * @param hu the instance of HandleUsers to retrieve favourite_route's from
     */
    public void updateUserRouteFavourites(HandleUsers hu) {
        favouriteRouteList.clear();
        hu.getUserRouteFavourites();
    }


    /**
     * Updates the users taken routes. This will set the rank variable for the new Route object that has been added.
     * @param hu the instance of HandleUsers to retrieve taken_routes's from
     */
    public void updateUserTakenRoutes(HandleUsers hu) {
        takenRoutesList.clear();
        hu.getUserTakenRoutes();
    }


    /**
     * Adds a Route to the Users routeList if it is not already in it.
     *
     * @param route the route to be added
     * @param name  the username of whose favourite route this is
     * @param rank  the rank score which the user gives. If none is given it is set to 0
     * @param db    the database's favourite_route table that is to have the row added
     * @param hu    the current HandleUsers object that is accessing the cyclists information
     */
    public void addFavouriteRoute(Route route, String name, int rank, SQLiteDB db, HandleUsers hu) {
        favouriteRouteList.add(route);
        FavouriteRouteData f = new FavouriteRouteData(db);
        f.addFavouriteRoute(name, route.getStartYear(), route.getStartMonth(), route.getStartDay(),
                route.getStartTime(), route.getBikeID(), rank, hu);
    }


    /**
     * Adds a Route to the Users takenRoutesList if it is not already in it.
     *
     * @param route the route to be added
     * @param name  the username of whose taken route this is
     * @param db    the database's taken_route table that is to have the row added.
     */
    public void addTakenRoute(Route route, String name, SQLiteDB db, HandleUsers hu) {
        takenRoutesList.add(route);
        TakenRoutes t = new TakenRoutes(db);
        t.addTakenRoute(name, route.getStartYear(), route.getStartMonth(), route.getStartDay(),
                route.getStartTime(), route.getBikeID(), route.getDistance(), hu);
    }


    /**
     * Checks to see if a route is already in the cyclists favouriteRoute list.------------------------------test---------
     *
     * @param route the route to be checked if it is already in the list
     * @param type the type of table to add the route to, either favourite_routes or taken_routes
     * @return true if it is already in the list, otherwise false
     */
    public boolean routeAlreadyInList(Route route, String type) {
        boolean alreadyInList = false;
        if (type.equals("favourite_route")) {
            for (Route tempRoute : favouriteRouteList) {
                if (route.getStartYear().equals(tempRoute.getStartYear()) && route.getStartMonth().equals(tempRoute.getStartMonth()) &&
                        route.getStartDay().equals(tempRoute.getStartDay()) && route.getStartTime().equals(tempRoute.getStartTime()) &&
                        route.getBikeID().equals(tempRoute.getBikeID())) {
                    alreadyInList = true;
                    break;
                }
            }
        } else {
            for (Route tempRoute : takenRoutesList) {
                if (route.getStartYear().equals(tempRoute.getStartYear()) && route.getStartMonth().equals(tempRoute.getStartMonth()) &&
                        route.getStartDay().equals(tempRoute.getStartDay()) && route.getStartTime().equals(tempRoute.getStartTime()) &&
                        route.getBikeID().equals(tempRoute.getBikeID())) {
                    alreadyInList = true;
                    break;
                }
            }
        }
        return alreadyInList;
    }


    /**
     * Adds a RetailLocation to the Users favouriteStationLocations.
     *
     * @param retail Retail to be added
     * @param name   name of the user
     * @param db the database whose tables are to be accessed
     * @return true if the Retail is already in the favouriteRetail list, false otherwise
     */
    public boolean addFavouriteRetail(RetailLocation retail, String name, SQLiteDB db) {
        boolean alreadyInList = false;
        for (RetailLocation tempRetail : favouriteRetailLocations) {
            if (retail.getName().equals(tempRetail.getName()) && retail.getAddress().equals(tempRetail.getAddress())) {
                alreadyInList = true;
                break;
            }
        }
        if (!alreadyInList) {
            favouriteRetailLocations.add(retail);
            FavouriteRetailData f = new FavouriteRetailData(db);
            f.addFavouriteRetail(name, retail.getName(), retail.getAddress());
        }
        return alreadyInList;
    }


    /**
     * Adds a WifiLocation to the Users favouriteWifiLocations.
     *
     * @param wifi wifi object to be added to favouriteWifi
     * @param name name of the user
     * @param db the database whose tables are to be accessed
     * @return true if the Wifi is already in the favouriteWifi, false otherwise
     */
    public boolean addFavouriteWifi(WifiLocation wifi, String name, SQLiteDB db) {
        boolean alreadyInList = false;
        for (WifiLocation tempWifi : favouriteWifiLocations) {
            if (wifi.getWifiID().equals(tempWifi.getWifiID())) {
                alreadyInList = true;
                break;
            }
        }
        if (!alreadyInList) {
            favouriteWifiLocations.add(wifi);
            FavouriteWifiData f = new FavouriteWifiData(db);
            f.addFavouriteWifi(name, wifi.getWifiID());
        }
        return alreadyInList;
    }
}