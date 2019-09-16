package dataHandler;

import dataObjects.Cyclist;
import dataObjects.Route;
import main.HandleUsers;
import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class TakenRoutesTest {
    private static SQLiteDB db;
    private static TakenRoutes takenRoutes;
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
        db.executeUpdateSQL("DROP TABLE taken_routes");
    }

    @Before
    public void init() {
        takenRoutes = new TakenRoutes(db);
    }

    @Test
    public void addTakenRoute() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "01",
                "00:00:00", "10000", 1, hu);
        ResultSet rs;
        rs = db.executeQuerySQL("SELECT count(*) FROM taken_routes WHERE name = 'Tester' AND start_year = '2016' AND " +
                "start_month = '01' AND start_day = '01' AND start_time = '00:00:00' AND bikeid = '10000' " +
                "AND distance = '1';");
        int result = rs.getInt("count(*)");
        assertEquals(1, result);
    }

    @Test
    public void deleteTakenRoute() throws Exception {
        RouteDataHandler rdh = new RouteDataHandler(db);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "01",
                "00:00:00", "10000", 1, hu);
        Route route = new Route(0, "00:00:00", "0", "01", "01",
                "2016", "2016", "0", "0", 0, 0, 0,
                0, 0, 0, "0", "0", "10000",
                0, "0", 1, "0");

        takenRoutes.deleteTakenRoute(route, hu);
        ResultSet rs;
        rs = db.executeQuerySQL("SELECT COUNT(*) FROM taken_routes;");
        int result = rs.getInt("count(*)");
        assertEquals(0, result);
    }

    @Test
    public void findFiveRecentRoutesMoreThanFive() {
        RouteDataHandler rdh = new RouteDataHandler(db);

        // Adding routes in random order with different days for testing.
        takenRoutes.addTakenRoute("Tester", "2016", "01", "01",
                "00:00:00", "10000", 1, hu);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "02",
                "00:00:00", "10000", 1, hu);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "05",
                "00:00:00", "10000", 1, hu);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "03",
                "00:00:00", "10000", 1, hu);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "06",
                "00:00:00", "10000", 1, hu);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "04",
                "00:00:00", "10000", 1, hu);
        ArrayList<String> result = takenRoutes.findFiveRecentRoutes(hu);

        // Setting up the expected result.
        ArrayList<String> expected = new ArrayList<>();
        expected.add("2016 01 06 00:00:00|1.0");
        expected.add("2016 01 05 00:00:00|1.0");
        expected.add("2016 01 04 00:00:00|1.0");
        expected.add("2016 01 03 00:00:00|1.0");
        expected.add("2016 01 02 00:00:00|1.0");
        assertEquals(expected, result);
    }

    @Test
    public void findFiveRecentRoutesLessThanFive() {
        RouteDataHandler rdh = new RouteDataHandler(db);
        takenRoutes.addTakenRoute("Tester", "2016", "01", "01",
                "00:00:00", "10000", 1, hu);
        ArrayList<String> result = takenRoutes.findFiveRecentRoutes(hu);
        ArrayList<String> expected = new ArrayList<>();
        expected.add("2016 01 01 00:00:00|1.0");

        assertEquals(expected, result);
    }

    @Test
    public void findFiveRecentRoutesEmpty() {
        ArrayList<String> result = takenRoutes.findFiveRecentRoutes(hu);
        ArrayList<String> expected = new ArrayList<>();
        assertEquals(expected, result);
    }
}