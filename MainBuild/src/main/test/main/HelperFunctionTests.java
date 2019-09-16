package main;

import dataHandler.SQLiteDB;
import dataObjects.Cyclist;
import dataObjects.Route;
import org.junit.Test;

import static org.junit.Assert.*;


public class HelperFunctionTests {
    public double testingDistance1 = 11.12;
    public double testingDistance2 = 55.6;
    /**
     * Helper class to populate the given cyclist with the given number of routes of distance 11.12km.
     * @param cyclist cyclist to have routes populated
     * @param numberOfRoutes number of routes to populate
     * @return the same cyclist object with its TakenRoute list populated
     */
    public Cyclist addRoutesToCyclist(Cyclist cyclist, int numberOfRoutes) {
            Route r = new Route(0, "0", "0", "0", "0", "0",
                    "0", "0",
                    "0", 0,
                    0, 0,
                    0.1, 0,
                    0, "0",
                    "0", "0",
                    0, "0",
                    0, "0");

        for(int i =0; i < numberOfRoutes; i++) {cyclist.addTakenRouteInstance(r);}

        return cyclist;
    }

    /**
     * Helper class to populate the given cyclist with the given number of routes of distance 55.6.
     * @param cyclist cyclist to have routes populated
     * @param numberOfRoutes number of routes to populate
     * @return the same cyclist object with its TakenRoute list populated
     */
    public Cyclist addHigherLengthRoutesToCyclist(Cyclist cyclist, int numberOfRoutes) {
        Route r = new Route(0, "0", "0", "0", "0", "0",
                "0", "0",
                "0", 0,
                0, 0,
                0.5, 0,
                0, "0",
                "0", "0",
                0, "0",
                0, "0");

        for(int i =0; i < numberOfRoutes; i++) {cyclist.addTakenRouteInstance(r);}

        return cyclist;
    }

    @Test
    public void dateConversion(){
        String date1 = "2017-12-31";
        String date2 = "25-12-2017";
        String date3 = "2017/12/25";
        String date4 = "1111111111";
        String date5 = "2017-13-32"; // Edge case
        String[] date1Arr = HelperFunctions.convertDates(date1);
        String[] date2Arr = HelperFunctions.convertDates(date2);
        String[] date3Arr = HelperFunctions.convertDates(date3);
        String[] date4Arr = HelperFunctions.convertDates(date4);
        String[] date5Arr = HelperFunctions.convertDates(date5);
        assertTrue(date1Arr[0].equals("2017") &&date1Arr[1].equals("12") || date1Arr[2].equals("25"));
        assertTrue(date2Arr == null);
        assertTrue(date3Arr == null);
        assertTrue(date4Arr == null);
        assertTrue(date5Arr == null);

    }

    @Test
    public void timeChecking() {
        String time1 = "00:00:00";
        String time2 = "25-12-2017";
        String time3 = "11111";
        String time4 = "23:59"; // Edge case
        String time5 = "-3:-4";
        assertTrue(HelperFunctions.checkTime(time1));
        assertFalse(HelperFunctions.checkTime(time2));
        assertFalse(HelperFunctions.checkTime(time3));
        assertTrue(HelperFunctions.checkTime(time4));
        assertFalse(HelperFunctions.checkTime(time5));
    }

    @Test
    public void getDurationTests() {
        int oneSecond = HelperFunctions.getDuration("2017", "01", "01", "00:00:00",
                "2017", "01", "01", "00:00:01");
        int negativeOneSecond = HelperFunctions.getDuration("2017", "01", "01", "00:00:01",
                "2017", "01", "01", "00:00:00");
        int oneDay = HelperFunctions.getDuration("2017", "01", "01", "00:00:00",
                "2017", "01", "02", "00:00:00");
        int janurary = HelperFunctions.getDuration("2017", "01", "01", "00:00:00",
                "2017", "02", "01", "00:00:00");
        int february = HelperFunctions.getDuration("2017", "02", "01", "00:00:00",
                "2017", "03", "01", "00:00:00");
        assertTrue(oneSecond == 1);
        assertTrue(negativeOneSecond == -1);
        assertTrue(oneDay == 86400); //60*60*24
        assertTrue(janurary > february);
    }

    @Test
    public void secondsToStringTests() {
        String oneSecond = HelperFunctions.secondsToString(1);
        String oneMinute = HelperFunctions.secondsToString(60);
        String oneHour = HelperFunctions.secondsToString(3600);
        String oneDay = HelperFunctions.secondsToString(86400);
        String oneOfEach = HelperFunctions.secondsToString(90061);
        assertTrue(oneSecond.equals("1s"));
        assertTrue(oneMinute.equals("1m, 0s"));
        assertTrue(oneHour.equals("1h, 0m, 0s"));
        assertTrue(oneDay.equals("1d, 0h, 0m, 0s"));
        assertTrue(oneOfEach.equals("1d, 1h, 1m, 1s"));
    }

    @Test
    public void getDistance1() {
        double lat1 = -43.698442;
        double lon1 = 172.780681;
        double lat2 = -42.424081;
        double lon2 = 146.074937;
        double dist = HelperFunctions.getDistance(lat1, lon1, lat2, lon2);
        assertEquals(2164.85, dist, 2.16485);
    }

    @Test
    public void getDistance2() {
        double lat1 = -8.581382;
        double lon1 = 68.560900;
        double lat2 = 52.679915;
        double lon2 = 68.001759;
        double dist = HelperFunctions.getDistance(lat1, lon1, lat2, lon2);
        System.out.println(dist);
        assertEquals( 6812.16, dist, 6.81216);
    }

    @Test
    public void getDistancePoleToPole() {
        double lat1 = 90.0;
        double lon1 = 0.0;
        double lat2 = -90.0;
        double lon2 = 0;
        double dist = HelperFunctions.getDistance(lat1, lon1, lat2, lon2);
        System.out.println(dist);
        assertEquals( 20035, dist, 20.035);
    }


    @Test
    public void checkDateDetailsValid1() {
        boolean invalidDate = HelperFunctions.checkDateDetails(1, 1, 2017);
        assertFalse(invalidDate);
    }

    @Test
    public void checkDateDetailsValid2() {
        boolean invalidDate = HelperFunctions.checkDateDetails(5, 9, 2012);
        assertFalse(invalidDate);
    }

    @Test
    public void checkDateDetailsInvalidYear() {
        boolean invalidDate = HelperFunctions.checkDateDetails(1, 1, 2018);
        assertTrue(invalidDate);
    }

    @Test
    public void checkDateDetailsInvalidMonth() {
        boolean invalidDate = HelperFunctions.checkDateDetails(1, 0, 2017);
        assertTrue(invalidDate);
    }

    @Test
    public void checkDateDetailsInvalidDay() {
        boolean invalidDate = HelperFunctions.checkDateDetails(32, 1, 2017);
        assertTrue(invalidDate);
    }


    @Test
    public void calculateDistanceCycled1() {
        // NO ROUTES
        Cyclist cyclist = new Cyclist("Tester");
        double distance = HelperFunctions.calculateDistanceCycled(cyclist);
        assertEquals(0, distance, 0.001);
    }

    @Test
    public void calculateDistanceCycled2() {
        // ONE ROUTE
        Cyclist cyclist = addRoutesToCyclist(new Cyclist("Tester"), 1);
        double distance = HelperFunctions.calculateDistanceCycled(cyclist);
        assertEquals(testingDistance1, distance, 0.001);
    }

    @Test
    public void calculateDistanceCycled3() {
        // MULTIPLE ROUTES
        Cyclist cyclist = addRoutesToCyclist(new Cyclist("Tester"), 100);
        double distance = HelperFunctions.calculateDistanceCycled(cyclist);
        assertEquals(testingDistance1*100, distance, 0.001);
    }

    @Test
    public void cacluateAverageDistance1() {
        SQLiteDB db = new SQLiteDB();
        HandleUsers hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = new Cyclist("Tester");
        double distance = HelperFunctions.cacluateAverageDistance(hu.currentCyclist, hu);
        assertEquals(0, distance, 0.001);
    }

    @Test
    public void cacluateAverageDistance2() {
        SQLiteDB db = new SQLiteDB();
        HandleUsers hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = addRoutesToCyclist(new Cyclist("Tester"), 1);
        double distance = HelperFunctions.cacluateAverageDistance(hu.currentCyclist, hu);
        assertEquals(testingDistance1, distance, 0.001);
    }

    @Test
    public void cacluateAverageDistance3() {
        SQLiteDB db = new SQLiteDB();
        HandleUsers hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = addRoutesToCyclist(new Cyclist("Tester"), 100);
        double distance = HelperFunctions.cacluateAverageDistance(hu.currentCyclist, hu);
        assertEquals(testingDistance1, distance, 0.001);
    }

    @Test
    public void calculateShortestRoute1() {
        Cyclist cyclist = new Cyclist("Tester");
        double distance = HelperFunctions.calculateShortestRoute(cyclist);
        assertEquals(9999999.0, distance, 0.001); // Arbitrarily large number.
    }

    @Test
    public void calculateShortestRoute2() {
        Cyclist cyclist = addRoutesToCyclist(new Cyclist("Tester"), 1);
        cyclist = addHigherLengthRoutesToCyclist(cyclist, 1);
        double distance = HelperFunctions.calculateShortestRoute(cyclist);
        assertEquals(testingDistance1, distance, 0.001);
    }

    @Test
    public void calculateShortestRoute3() {
        Cyclist cyclist = addRoutesToCyclist(new Cyclist("Tester"), 1);
        cyclist = addHigherLengthRoutesToCyclist(cyclist, 1);
        double distance = HelperFunctions.calculateShortestRoute(cyclist);
        assertEquals(testingDistance1, distance, 0.001);
    }


    @Test
    public void calculateLongestRoute1() {
        Cyclist cyclist = new Cyclist("Tester");
        double distance = HelperFunctions.calculateLongestRoute(cyclist);
        assertEquals(-1, distance, 0.001);
    }

    @Test
    public void calculateLongestRoute2() {
        Cyclist cyclist = addRoutesToCyclist(new Cyclist("Tester"), 1);
        double distance = HelperFunctions.calculateLongestRoute(cyclist);
        assertEquals(testingDistance1, distance, 0.001);
    }

    @Test
    public void calculateLongestRoute3() {
        Cyclist cyclist = addRoutesToCyclist(new Cyclist("Tester"), 1);
        cyclist = addHigherLengthRoutesToCyclist(cyclist, 1);
        double distance = HelperFunctions.calculateLongestRoute(cyclist);
        assertEquals(testingDistance2, distance, 0.001);
    }

    @Test
    public void format2dp1() {
        double number = 1.1234212345452;
        HelperFunctions.format2dp(number);
        assertEquals(1.12, number, 0.1);
    }

    @Test
    public void format2dp2() {
        double number = 1;
        HelperFunctions.format2dp(number);
        assertEquals(1.0, number, 0.1);
    }

    @Test
    public void format2dp3() {
        double number = 10000000;
        HelperFunctions.format2dp(number);
        assertEquals(10000000.0, number, 0.1);
    }

}