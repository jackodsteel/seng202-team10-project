package main;

import dataAnalysis.RetailLocation;
import dataAnalysis.Route;
import dataAnalysis.WifiLocation;
import dataHandler.*;
import dataManipulation.DataFilterer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.Assert.*;


/**
 * Created by jto59 on 24/09/17.
 */
public class HandleUsersTest {
    private DataFilterer dataFilterer;
    private ArrayList<Route> routes = new ArrayList<>();
    private ArrayList<WifiLocation> wifiLocations = new ArrayList<>();
    private ArrayList<RetailLocation> retailLocations = new ArrayList<>();

    private ArrayList<String> userList = new ArrayList<>();
    public Cyclist currentCyclist;

    private SQLiteDB db;

    @AfterClass
    public static void tearDownClass() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @Before
    public void setUp() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
        dataFilterer = new DataFilterer(db);

//        WifiDataHandler wdh = new WifiDataHandler(db);
//        wdh.processCSV(getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile());
//
//        RouteDataHandler rdh = new RouteDataHandler(db);
//        rdh.processCSV(getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile());
//
//        RetailerDataHandler retailerDataHandler = new RetailerDataHandler(db);
//        System.out.println("Made");
//        retailerDataHandler.processCSV(getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile());
//

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void init() throws Exception {

    }

    @Test
    public void fillUserList() throws Exception {

    }

    @Test
    public void logIn() throws Exception {

    }

    @Test
    public void getUserRouteFavourites() throws Exception {

    }

    @Test
    public void getUserWifiFavourites() throws Exception {

    }

    @Test
    public void getUserRetailFavourites() throws Exception {

    }

    @Test
    public void logOutOfUser() throws Exception {
        currentCyclist = new Cyclist("Tester");
        HandleUsers hu = new HandleUsers();
        hu.logOutOfUser();
        assertEquals(null, hu.currentCyclist);
    }

    @Test
    public void createNewUser1() throws Exception {

    }

    @Test
    public void createNewUser2() throws Exception {

    }

}