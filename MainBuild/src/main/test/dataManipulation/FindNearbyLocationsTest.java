//package dataManipulation;
//
//import dataAnalysis.RetailLocation;
//import dataAnalysis.WifiLocation;
//import dataHandler.*;
//import de.saxsys.javafx.test.JfxRunner;
//import de.saxsys.javafx.test.TestInJfxThread;
//import main.HelperFunctions;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.Collections;
//
//import static org.junit.Assert.*;
//
//
///**
// * These tests all assume the getDistance helper function works correctly. If it does not, these tests may be incorrect.
// * There is duplicate code as this will ensure checks are valid even if FindNearby methodology diverges for the two datatypes
// */
//@RunWith(JfxRunner.class)
//public class FindNearbyLocationsTest {
//
//    private static SQLiteDB db;
//
//    @AfterClass
//    public static void clearDB() throws Exception {
//        String home = System.getProperty("user.home");
//        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
//        Files.delete(path);
//    }
//
//    @BeforeClass
//    @TestInJfxThread
//    public static void setUp() throws Exception {
//
//        String home = System.getProperty("user.home");
//        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
//        db = new SQLiteDB(path.toString());
//
//        WifiDataHandler wifiDataHandler = new WifiDataHandler(db);
//
//
//        wifiDataHandler.addSingleEntry("-50,-50", "Cost", "Provider","Fake", -50, -50, "Remarks",
//                "City", "SSID", "Suburb", "ZIP");
//
//        wifiDataHandler.addSingleEntry("-50,50","Cost", "Provider","Fake", -50, 50, "Remarks",
//                "City", "SSID", "Suburb", "ZIP");
//
//        wifiDataHandler.addSingleEntry("50,-50","Cost", "Provider","Fake", 50, -50, "Remarks",
//                "City", "SSID", "Suburb", "ZIP");
//
//        wifiDataHandler.addSingleEntry("50,50","Cost", "Provider","Fake", 50, 50, "Remarks",
//                "City", "SSID", "Suburb", "ZIP");
//
//        wifiDataHandler.addSingleEntry("10,10","Cost", "Provider","Fake", 10, 10, "Remarks",
//                "City", "SSID", "Suburb", "ZIP");
//
//        wifiDataHandler.addSingleEntry("-10,-10","Cost", "Provider","Fake", -10, -10, "Remarks",
//                "City", "SSID", "Suburb", "ZIP");
//
//
//        RetailerDataHandler retailerDataHandler = new RetailerDataHandler(db);
//
//        retailerDataHandler.addSingleEntry("-50,-50", "Fake", -50, -50, "City",
//                "State", "ZIP", "Main", "Sec");
//
//        retailerDataHandler.addSingleEntry("-50,50", "Fake", -50, 50, "City",
//                "State", "ZIP", "Main", "Sec");
//
//        retailerDataHandler.addSingleEntry("50,-50", "Fake", 50, -50, "City",
//                "State", "ZIP", "Main", "Sec");
//
//        retailerDataHandler.addSingleEntry("50,50", "Fake", 50, 50, "City",
//                "State", "ZIP", "Main", "Sec");
//
//        retailerDataHandler.addSingleEntry("10,10", "Fake", 10, 10, "City",
//                "State", "ZIP", "Main", "Sec");
//
//        retailerDataHandler.addSingleEntry("-10,-10", "Fake", -10, -10, "City",
//                "State", "ZIP", "Main", "Sec");
//    }
//
//    /**
//     * Helper function that takes a lat long and runs FindNearbyLocations, and converts the ArrayList of Retailer objects to an ArrayList of distances
//     * @param lat
//     * @param lon
//     * @return ArrayList<Double> of the distances of the returned route, in same order
//     */
//    private ArrayList<Double> getRetailerDistances(double lat, double lon) {
//        ArrayList<Double> distances = new ArrayList<>();
//        FindNearbyLocations nearbyLocations = new FindNearbyLocations(db);
//        ArrayList<RetailLocation> retailers = nearbyLocations.findNearbyRetail(lat, lon);
//        for (RetailLocation retailer : retailers) {
//            distances.add(HelperFunctions.getDistance(lat, lon, retailer.getLatitude(), retailer.getLongitude()));
//        }
//        return distances;
//    }
//
//    /**
//     * Helper function that takes a lat long and runs FindNearbyLocations, and converts the ArrayList of Wifi objects to an ArrayList of distances
//     * @param lat
//     * @param lon
//     * @return ArrayList<Double> of the distances of the returned route, in same order
//     */
//    private ArrayList<Double> getWifiDistances(double lat, double lon) {
//        ArrayList<Double> distances = new ArrayList<>();
//        FindNearbyLocations nearbyLocations = new FindNearbyLocations(db);
//        ArrayList<WifiLocation> wifi = nearbyLocations.findNearbyWifi(lat, lon);
//        for (WifiLocation wifiLocation : wifi) {
//            distances.add(HelperFunctions.getDistance(lat, lon, wifiLocation.getLatitude(), wifiLocation.getLongitude()));
//        }
//        return distances;
//    }
//
//
//    @Test
//    public void FindNearbyPosPos() throws Exception {
//        double lat = 0.001;
//        double lon = 0.001;
//
//        ArrayList<Double> wifiDistances = getWifiDistances(lat, lon);
//        ArrayList<Double> retailDistance = getRetailerDistances(lat, lon);
//
//        ArrayList<Double> sortedWifi = new ArrayList<>(wifiDistances);
//        Collections.sort(sortedWifi);
//
//        ArrayList<Double> sortedRetail = new ArrayList<>(retailDistance);
//        Collections.sort(sortedRetail);
//
//        assertTrue(wifiDistances.equals(sortedWifi));
//        assertTrue(retailDistance.equals(sortedRetail));
//    }
//
//
//    @Test
//    public void FindNearbyPosNeg() throws Exception {
//        double lat = 0.001;
//        double lon = -0.001;
//
//        ArrayList<Double> wifiDistances = getWifiDistances(lat, lon);
//        ArrayList<Double> retailDistance = getRetailerDistances(lat, lon);
//
//        ArrayList<Double> sortedWifi = new ArrayList<>(wifiDistances);
//        Collections.sort(sortedWifi);
//
//        ArrayList<Double> sortedRetail = new ArrayList<>(retailDistance);
//        Collections.sort(sortedRetail);
//
//        assertTrue(wifiDistances.equals(sortedWifi));
//        assertTrue(retailDistance.equals(sortedRetail));
//    }
//
//
//    @Test
//    public void FindNearbyNegPos() throws Exception {
//        double lat = -0.001;
//        double lon = 0.001;
//
//        ArrayList<Double> wifiDistances = getWifiDistances(lat, lon);
//        ArrayList<Double> retailDistance = getRetailerDistances(lat, lon);
//
//        ArrayList<Double> sortedWifi = new ArrayList<>(wifiDistances);
//        Collections.sort(sortedWifi);
//
//        ArrayList<Double> sortedRetail = new ArrayList<>(retailDistance);
//        Collections.sort(sortedRetail);
//
//        assertTrue(wifiDistances.equals(sortedWifi));
//        assertTrue(retailDistance.equals(sortedRetail));
//    }
//
//    @Test
//    public void FindNearbyNegNeg() throws Exception {
//        double lat = -0.001;
//        double lon = -0.001;
//
//        ArrayList<Double> wifiDistances = getWifiDistances(lat, lon);
//        ArrayList<Double> retailDistance = getRetailerDistances(lat, lon);
//
//        ArrayList<Double> sortedWifi = new ArrayList<>(wifiDistances);
//        Collections.sort(sortedWifi);
//
//        ArrayList<Double> sortedRetail = new ArrayList<>(retailDistance);
//        Collections.sort(sortedRetail);
//
//        assertTrue(wifiDistances.equals(sortedWifi));
//        assertTrue(retailDistance.equals(sortedRetail));
//    }
//}