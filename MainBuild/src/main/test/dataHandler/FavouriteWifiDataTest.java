package dataHandler;

import dataObjects.Cyclist;
import dataObjects.WifiLocation;
import main.HandleUsers;
import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

/**
 * Test for the FavouriteWifiData database table when adding data. Note that error
 * handling for retail shops being added twice is found in the HandleUser getUserWifiFavourites method.
 */
public class FavouriteWifiDataTest {
    private static SQLiteDB db;
    private static FavouriteWifiData favouriteWifiData;
    private static HandleUsers hu;

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @BeforeClass
    public static void setUp() {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
        hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = new Cyclist("Tester");
    }

    @After
    public void tearDown() {
        db.executeUpdateSQL("DROP TABLE favourite_retail");
    }

    @Before
    public void init() {
        favouriteWifiData = new FavouriteWifiData(db);
    }
    @Test
    public void addFavouriteWifi() throws Exception {
        favouriteWifiData.addFavouriteWifi("Tester", "1");
        ResultSet rs;
        rs = db.executeQuerySQL("SELECT count(*) FROM favourite_wifi WHERE name = 'Tester' AND WIFI_ID = '1';");
        int result = rs.getInt("count(*)");
        assertEquals(1, result);
    }

    @Test
    public void deleteFavouriteWifi() throws Exception {
        WifiDataHandler wdh = new WifiDataHandler(db);
        favouriteWifiData.addFavouriteWifi("Tester", "1");
        WifiLocation wifi = new WifiLocation("1", 0.0, 0.0, "", "", "","",
                "", "", "", 1, "");


        favouriteWifiData.deleteFavouriteWifi(wifi, hu);
        ResultSet rs;
        rs = db.executeQuerySQL("SELECT COUNT(*) FROM favourite_wifi;");
        int result = rs.getInt("count(*)");
        assertEquals(0, result);
    }

}