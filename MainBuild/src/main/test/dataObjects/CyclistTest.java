package dataObjects;

import dataHandler.*;
import main.HandleUsers;
import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class CyclistTest {
    private static SQLiteDB db;
    private Cyclist testCyclist;
    private HandleUsers hu;

    @Before
    public void setUp() {
        testCyclist = new Cyclist("Tester");
        hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = testCyclist;
    }

    @BeforeClass
    public static void initDB() {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
    }

    @After
    public void tearDown() {
        db.executeUpdateSQL("DROP TABLE route_information");
        db.executeUpdateSQL("DROP TABLE wifi_location");
        db.executeUpdateSQL("DROP TABLE retailer");
        db.executeUpdateSQL("DROP TABLE favourite_route");
        db.executeUpdateSQL("DROP TABLE favourite_wifi");
        db.executeUpdateSQL("DROP TABLE favourite_retail");
        db.executeUpdateSQL("DROP TABLE users");
    }

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }


    @Test
    public void addFavouriteRoute() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        FavouriteRouteData frd = new FavouriteRouteData(db);
        Route testRoute = new Route(10, "00:00:00", "00:00:00", "01", "01", "2016",
                "01", "01", "2016", 0.0, 0.0,
                0.0, 0.0, 1, 2, "Test Street",
                "Test2 Street", "10000", 1, "Subscriber", 0, "20");
        //hu.currentCyclist = testCyclist;
        testCyclist.addFavouriteRoute(testRoute, testCyclist.getName(), 1, db, hu);
        ResultSet rs;

        rs = db.executeQuerySQL("SELECT * FROM favourite_routes WHERE name = '" + testCyclist.getName() + "' AND  start_year = '2016'" +
                " AND start_month = '01' AND start_day = '01' AND start_time = '00:00:00' AND bikeid = '10000' AND rank = '1'");
        assertFalse(rs.isClosed());
    }


    @Test
    public void addTakenRoute() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        TakenRoutes t = new TakenRoutes(db);
        Route testRoute = new Route(10, "00:00:00", "00:00:00", "01", "01", "2016",
                "01", "01", "2016", 0.0, 0.0,
                0.0, 0.0, 1, 2, "Test Street",
                "Test2 Street", "10000", 1, "Subscriber", 0, "20");
        hu.currentCyclist = testCyclist;
        testCyclist.addTakenRoute(testRoute, testCyclist.getName(), db, hu);
        ResultSet rs;

        rs = db.executeQuerySQL("SELECT * FROM taken_routes WHERE name = '" + testCyclist.getName() + "' AND  start_year = '2016'" +
                " AND start_month = '01' AND start_day = '01' AND start_time = '00:00:00' AND bikeid = '10000';");
        assertFalse(rs.isClosed());
    }


    @Test
    public void routeAlreadyInFavouritesListFalse() {
        Route testRoute = new Route(10, "00:00:00", "00:00:00", "01", "01", "2016",
                "01", "01", "2016", 0.0, 0.0,
                0.0, 0.0, 1, 2, "Test Street",
                "Test2 Street", "10000", 1, "Subscriber", 0, "20");
        boolean result = testCyclist.routeAlreadyInList(testRoute, "favourite_route");
        assertFalse(result);
    }


    @Test
    public void routeAlreadyInFavouritesListTrue() {
        Route testRoute = new Route(10, "00:00:00", "00:00:00", "01", "01", "2016",
                "01", "01", "2016", 0.0, 0.0,
                0.0, 0.0, 1, 2, "Test Street",
                "Test2 Street", "10000", 1, "Subscriber", 0, "20");
        testCyclist.addFavouriteRouteInstance(testRoute);
        boolean result = testCyclist.routeAlreadyInList(testRoute, "favourite_route");
        assertTrue(result);
    }

    @Test
    public void routeAlreadyInTakenListFalse() {
        Route testRoute = new Route(10, "00:00:00", "00:00:00", "01", "01", "2016",
                "01", "01", "2016", 0.0, 0.0,
                0.0, 0.0, 1, 2, "Test Street",
                "Test2 Street", "10000", 1, "Subscriber", 0, "20");
        boolean result = testCyclist.routeAlreadyInList(testRoute, "taken_route");
        assertFalse(result);
    }

    @Test
    public void routeAlreadyInTakenListTrue() {
        Route testRoute = new Route(10, "00:00:00", "00:00:00", "01", "01", "2016",
                "01", "01", "2016", 0.0, 0.0,
                0.0, 0.0, 1, 2, "Test Street",
                "Test2 Street", "10000", 1, "Subscriber", 0, "20");
        testCyclist.addTakenRouteInstance(testRoute);
        boolean result = testCyclist.routeAlreadyInList(testRoute, "taken_route");
        assertTrue(result);
    }

    @Test
    public void addFavouriteRetail() throws Exception {
        FavouriteRetailData frd = new FavouriteRetailData(db);
        RetailLocation testRetail = new RetailLocation("Test Shop", "1 Test Street", "NY",
                "Casual Eating", "F-Pizza", "NY", 10000, 0.0, 0.0, null);
        testCyclist.addFavouriteRetail(testRetail, testCyclist.getName(), db);
        ResultSet rs;

        rs = db.executeQuerySQL("SELECT * FROM favourite_retail WHERE name = '" + testCyclist.getName() + "' AND " +
                "RETAILER_NAME = 'Test Shop' AND ADDRESS = '1 Test Street';");
        assertFalse(rs.isClosed());
    }

    @Test
    public void addFavouriteWifi() throws Exception {
        FavouriteWifiData fwd = new FavouriteWifiData(db);
        WifiLocation testWifi = new WifiLocation("1", 0.0, 0.0, "1 Test Street", "Guest",
                "Free", "BPL", "free", "NY", "Manhattan",
                10000, null);
        testCyclist.addFavouriteWifi(testWifi, testCyclist.getName(), db);
        ResultSet rs;

        rs = db.executeQuerySQL("SELECT * FROM favourite_wifi WHERE name = '" + testCyclist.getName() + "' AND " +
                "WIFI_ID = '1'");
        assertFalse(rs.isClosed());
    }

}