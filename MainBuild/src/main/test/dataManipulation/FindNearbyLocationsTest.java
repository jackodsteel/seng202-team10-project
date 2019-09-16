package dataManipulation;

import dataHandler.RetailerDataHandler;
import dataHandler.SQLiteDB;
import dataHandler.WifiDataHandler;
import dataObjects.RetailLocation;
import dataObjects.WifiLocation;
import main.HelperFunctions;
import main.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * These tests all assume the getDistance helper function works correctly. If it does not, these tests may be incorrect.
 * There is duplicate code as this will ensure checks are valid even if FindNearby methodology diverges for the two datatypes
 */
public class FindNearbyLocationsTest {

    private static SQLiteDB db;

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        ApplicationTest.launch(Main.class);

        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());

        WifiDataHandler wifiDataHandler = new WifiDataHandler(db);


        wifiDataHandler.addSingleEntry("-50,-50", "Cost", "Provider","Fake", -50, -50, "Remarks",
                "City", "SSID", "Suburb", "ZIP");

        wifiDataHandler.addSingleEntry("-50,50","Cost", "Provider","Fake", -50, 50, "Remarks",
                "City", "SSID", "Suburb", "ZIP");

        wifiDataHandler.addSingleEntry("50,-50","Cost", "Provider","Fake", 50, -50, "Remarks",
                "City", "SSID", "Suburb", "ZIP");

        wifiDataHandler.addSingleEntry("50,50","Cost", "Provider","Fake", 50, 50, "Remarks",
                "City", "SSID", "Suburb", "ZIP");

        wifiDataHandler.addSingleEntry("10,10","Cost", "Provider","Fake", 10, 10, "Remarks",
                "City", "SSID", "Suburb", "ZIP");

        wifiDataHandler.addSingleEntry("-10,-10","Cost", "Provider","Fake", -10, -10, "Remarks",
                "City", "SSID", "Suburb", "ZIP");


        RetailerDataHandler retailerDataHandler = new RetailerDataHandler(db);

        retailerDataHandler.addSingleEntry("-50,-50", "Fake", -50, -50, "City",
                "State", "ZIP", "Main", "Sec");

        retailerDataHandler.addSingleEntry("-50,50", "Fake", -50, 50, "City",
                "State", "ZIP", "Main", "Sec");

        retailerDataHandler.addSingleEntry("50,-50", "Fake", 50, -50, "City",
                "State", "ZIP", "Main", "Sec");

        retailerDataHandler.addSingleEntry("50,50", "Fake", 50, 50, "City",
                "State", "ZIP", "Main", "Sec");

        retailerDataHandler.addSingleEntry("10,10", "Fake", 10, 10, "City",
                "State", "ZIP", "Main", "Sec");

        retailerDataHandler.addSingleEntry("-10,-10", "Fake", -10, -10, "City",
                "State", "ZIP", "Main", "Sec");
    }

    /**
     * Helper function that takes a lat long and runs FindNearbyLocations, and converts the List of Retailer objects to a List of distances
     * @param lat
     * @param lon
     * @return list of the distances of the returned route, in same order
     */
    private List<Double> getRetailerDistances(double lat, double lon) {
        List<Double> distances = new ArrayList<>();
        FindNearbyLocations nearbyLocations = new FindNearbyLocations(db);
        List<RetailLocation> retailers = nearbyLocations.findNearbyRetail(lat, lon);
        for (RetailLocation retailer : retailers) {
            distances.add(HelperFunctions.getDistance(lat, lon, retailer.getLatitude(), retailer.getLongitude()));
        }
        return distances;
    }

    /**
     * Helper function that takes a lat long and runs FindNearbyLocations, and converts the List of Wifi objects to a List of distances
     * @param lat
     * @param lon
     * @return list of the distances of the returned route, in same order
     */
    private List<Double> getWifiDistances(double lat, double lon) {
        List<Double> distances = new ArrayList<>();
        FindNearbyLocations nearbyLocations = new FindNearbyLocations(db);
        List<WifiLocation> wifi = nearbyLocations.findNearbyWifi(lat, lon);
        for (WifiLocation wifiLocation : wifi) {
            distances.add(HelperFunctions.getDistance(lat, lon, wifiLocation.getLatitude(), wifiLocation.getLongitude()));
        }
        return distances;
    }


    @Test
    public void FindNearbyPosPos() {
        double lat = 0.001;
        double lon = 0.001;

        List<Double> wifiDistances = getWifiDistances(lat, lon);
        List<Double> retailDistance = getRetailerDistances(lat, lon);

        List<Double> sortedWifi = new ArrayList<>(wifiDistances);
        Collections.sort(sortedWifi);

        List<Double> sortedRetail = new ArrayList<>(retailDistance);
        Collections.sort(sortedRetail);

        assertEquals(wifiDistances, sortedWifi);
        assertEquals(retailDistance, sortedRetail);
    }


    @Test
    public void FindNearbyPosNeg() {
        double lat = 0.001;
        double lon = -0.001;

        List<Double> wifiDistances = getWifiDistances(lat, lon);
        List<Double> retailDistance = getRetailerDistances(lat, lon);

        List<Double> sortedWifi = new ArrayList<>(wifiDistances);
        Collections.sort(sortedWifi);

        List<Double> sortedRetail = new ArrayList<>(retailDistance);
        Collections.sort(sortedRetail);

        assertEquals(wifiDistances, sortedWifi);
        assertEquals(retailDistance, sortedRetail);
    }


    @Test
    public void FindNearbyNegPos() {
        double lat = -0.001;
        double lon = 0.001;

        List<Double> wifiDistances = getWifiDistances(lat, lon);
        List<Double> retailDistance = getRetailerDistances(lat, lon);

        List<Double> sortedWifi = new ArrayList<>(wifiDistances);
        Collections.sort(sortedWifi);

        List<Double> sortedRetail = new ArrayList<>(retailDistance);
        Collections.sort(sortedRetail);

        assertEquals(wifiDistances, sortedWifi);
        assertEquals(retailDistance, sortedRetail);
    }

    @Test
    public void FindNearbyNegNeg() {
        double lat = -0.001;
        double lon = -0.001;

        List<Double> wifiDistances = getWifiDistances(lat, lon);
        List<Double> retailDistance = getRetailerDistances(lat, lon);

        List<Double> sortedWifi = new ArrayList<>(wifiDistances);
        Collections.sort(sortedWifi);

        List<Double> sortedRetail = new ArrayList<>(retailDistance);
        Collections.sort(sortedRetail);

        assertEquals(wifiDistances, sortedWifi);
        assertEquals(retailDistance, sortedRetail);
    }
}