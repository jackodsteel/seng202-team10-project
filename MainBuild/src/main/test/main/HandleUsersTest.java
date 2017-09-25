package main;

import dataAnalysis.RetailLocation;
import dataAnalysis.Route;
import dataAnalysis.WifiLocation;
import dataHandler.*;
import dataManipulation.DataFilterer;
import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


/**
 * Created by jto59 on 24/09/17.
 */
public class HandleUsersTest {

    private static SQLiteDB db;
    private Cyclist currentCyclist;
    private HandleUsers hu;

    @Before
    public void setUp() throws Exception {
        currentCyclist = new Cyclist("Tester");
        hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = currentCyclist;
    }


    @BeforeClass
    public static void initDB() {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
    }


    /**
     * This creates SQL errors in the command line about missing tables in the database. Dropping all of these tables at
     * the end of each test makes it clearer on what the individual tests are actually doing. The
     * "missing table" messages can be ignored as they are not created for each test.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
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


    /**
     * Testing the user logged into has had their currentCyclist object created. Valid favourites of the user is being
     * tested below.
     * @throws Exception
     */
    @Test
    public void logIn() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        WifiDataHandler wdh = new WifiDataHandler(db);
        RetailerDataHandler rDh = new RetailerDataHandler(db);
        FavouriteRouteData frd = new FavouriteRouteData(db);
        FavouriteWifiData fwd = new FavouriteWifiData(db);
        FavouriteRetailData fRd = new FavouriteRetailData(db);

        String testName = "Another Tester";
        hu.logIn(testName); // Logging into a new user called "Another Tester".

        assertEquals(testName, hu.currentCyclist.getName());
    }


    /**
     * Creating a test route and then adding parameters with identical values to the database. The method is then
     * called and compared to test equality of the newly found route and the test route. There are no other side cases
     * as routes can only have been added to a users favourites list if they already existed in the database.
     * @throws Exception
     */
    @Test
    public void getUserRouteFavourites() throws Exception {

        RouteDataHandler rdh = new RouteDataHandler(db);
        Route testRoute = new Route(10, "00:00:00", "00:00:00", "01", "01", "2016",
                "01", "01", "2016", 0.0, 0.0,
                0.0, 0.0, 1, 2, "Test Street",
                "Test2 Street", "10000", 1, "Subscriber", 20);

        rdh.addSingleEntry(10, "2016","01","01","00:00:00", "2016",
                "01", "01", "00:00:01", "10", "Test Station",
                0.0, 0.0, "11","Test2 Station", 10.0,
                10.0, "10000", "Subscriber", 1997, 1);

        FavouriteRouteData frd = new FavouriteRouteData(db);
        frd.addFavouriteRoute("Tester", "2016", "01", "01", "00:00:00",
                "10000", 1, hu); //Adding the newly created route to "Tester" favourites.
        hu.getUserRouteFavourites();

        Route foundRoute = currentCyclist.getFavouriteRouteList().get(0); // Get Testers first favourite route.
        assertEquals(testRoute, foundRoute);
    }


    /**
     * Creating a test wifi hotspot and then adding parameters with identical values to the database. The method is then
     * called and compared to test equality of the newly found hotspot and the test hotspot. There are no other side cases
     * as wifi hotspots can only have been added to a users favourites list if they already existed in the database.
     * @throws Exception
     */
    @Test
    public void getUserWifiFavourites() throws Exception {
        // Init WifiDataHandler and add an entry.
        WifiDataHandler wdh = new WifiDataHandler(db);
        WifiLocation testWifi = new WifiLocation("1", 0.0, 0.0, "1 Test Street", "Guest",
                "Free", "BPL", "free", "NY", "Manhattan",
                10000);
        wdh.addSingleEntry("1", "Free", "BPL", "1 Test Street", 0.0, 0.0,
                "free", "NY", "Guest", "Manhattan", "10000");

        // Init FavouriteWifiData and add an entry.
        FavouriteWifiData fwd = new FavouriteWifiData(db);
        fwd.addFavouriteWifi("Tester", "1");

        hu.getUserWifiFavourites();
        WifiLocation foundWifi = hu.currentCyclist.getFavouriteWifiLocations().get(0);

        assertEquals(testWifi, foundWifi);
    }


    /**
     * Creating a test retail store and then adding parameters with identical values to the database. The method is then
     * called and compared to test equality of the newly found retailer and the test retailer. There are no other side cases
     * as retail stores can only have been added to a users favourites list if they already existed in the database.
     * @throws Exception
     */
    @Test
    public void getUserRetailFavourites() throws Exception {

        // Init RetailerDataHandler and add an entry.
        RetailerDataHandler rdh = new RetailerDataHandler(db);
        RetailLocation testRetail = new RetailLocation("Test Shop", "1 Test Street", "NY",
                "Casual Eating", "F-Pizza", "NY", 10000, 0.0, 0.0);
        rdh.addSingleEntry("Test Shop", "1 Test Street", 0.0, 0.0, "NY", "NY",
                "10000", "Casual Eating", "F-Pizza");

        // Init FavouriteRetailData and add an entry.
        FavouriteRetailData frd = new FavouriteRetailData(db);
        frd.addFavouriteRetail("Tester", "Test Shop", "1 Test Street");

        hu.getUserRetailFavourites();
        RetailLocation foundRetail = hu.currentCyclist.getFavouriteRetailLocations().get(0);

        assertEquals(testRetail, foundRetail);
    }

    /**
     * Testing the currentCyclist is equal to null when logged out.
     * @throws Exception
     */
    @Test
    public void logOutOfUser() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        WifiDataHandler wdh = new WifiDataHandler(db);
        RetailerDataHandler rDh = new RetailerDataHandler(db);
        FavouriteRouteData frd = new FavouriteRouteData(db);
        FavouriteWifiData fwd = new FavouriteWifiData(db);
        FavouriteRetailData fRd = new FavouriteRetailData(db);

        String testName = "Another Tester";
        hu.logIn(testName); // Logging into a new user called "Another Tester".

        hu.logOutOfUser(); // Logging out of "Another Tester"'s account.
        assertEquals(null, hu.currentCyclist);
    }

    /**
     * Testing the created users username is correctly set to the currentCyclist.
     * @throws Exception
     */
    @Test
    public void createNewUser1() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        WifiDataHandler wdh = new WifiDataHandler(db);
        RetailerDataHandler rDh = new RetailerDataHandler(db);
        FavouriteRouteData frd = new FavouriteRouteData(db);
        FavouriteWifiData fwd = new FavouriteWifiData(db);
        FavouriteRetailData fRd = new FavouriteRetailData(db);
        DatabaseUser u = new DatabaseUser(db);

        String testName = "New Tester";
        hu.createNewUser(testName); // Logging into a new user called "New Tester".

        assertEquals(testName, hu.currentCyclist.getName());
    }


    /**
     * Testing that the created users username is added to the database.
     * @throws Exception
     */
    @Test
    public void createNewUser2() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        WifiDataHandler wdh = new WifiDataHandler(db);
        RetailerDataHandler rDh = new RetailerDataHandler(db);
        FavouriteRouteData frd = new FavouriteRouteData(db);
        FavouriteWifiData fwd = new FavouriteWifiData(db);
        FavouriteRetailData fRd = new FavouriteRetailData(db);
        DatabaseUser u = new DatabaseUser(db);

        String testName = "New Tester";
        hu.createNewUser(testName); // Logging into a new user called "New Tester".

        ResultSet rs;
        rs = db.executeQuerySQL("SELECT * FROM users WHERE name = '" + testName + "';");

        assertEquals(testName, rs.getString(1));
    }

}