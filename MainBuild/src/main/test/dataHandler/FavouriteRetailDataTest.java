package dataHandler;

import dataObjects.Cyclist;
import dataObjects.RetailLocation;
import main.HandleUsers;
import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

/**
 * Test for the FavouriteRetailData database table when adding data. Note that error
 * handling for retail shops being added twice is found in the HandleUser getUserRetailFavourites method.
 */
public class FavouriteRetailDataTest {
    private static SQLiteDB db;
    private static FavouriteRetailData favouriteRetailData;
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
        db = new SQLiteDB(path.toString());        hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = new Cyclist("Tester");

    }

    @After
    public void tearDown() {
        db.executeUpdateSQL("DROP TABLE favourite_retail");
    }

    @Before
    public void init() {
        favouriteRetailData = new FavouriteRetailData(db);
    }


    @Test
    public void addFavouriteRetail() throws Exception {
        favouriteRetailData.addFavouriteRetail("Tester", "Test shop", "1 Test Street");
        ResultSet rs;
        rs = db.executeQuerySQL("SELECT count(*) FROM favourite_retail WHERE name = 'Tester' AND RETAILER_NAME = 'Test shop'" +
                "AND ADDRESS = '1 Test Street';");
        int result = rs.getInt("count(*)");
        assertEquals(1, result);
    }

    @Test
    public void deleteFavouriteWifi() throws Exception {
        RetailerDataHandler rdh = new RetailerDataHandler(db);
        favouriteRetailData.addFavouriteRetail("Tester", "Shop", "Address");
        RetailLocation retail = new RetailLocation("Shop", "Address", "", "", "",
                "", 1, 1, 1, "");


        favouriteRetailData.deleteFavouriteRetail(retail, hu);
        ResultSet rs;
        rs = db.executeQuerySQL("SELECT COUNT(*) FROM favourite_retail;");
        int result = rs.getInt("count(*)");
        assertEquals(0, result);
    }

}