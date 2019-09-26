package dataHandler;


import main.Main;
import org.junit.*;

import java.nio.file.Files;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CSVImporterRouteTest {

    private static SQLiteDB db;
    private RouteDataHandler handler;

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @BeforeClass
    public static void setUp() {
        Main.initDB();
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
    }

    @After
    public void tearDown() {
        db.executeUpdateSQL("DROP TABLE route_information");
    }

    @Before
    public void init() {
        handler = new RouteDataHandler(db);
    }

    @Test
    public void processCSVIncorrectFormat() throws Exception {
        new CSVImporter(db, getClass().getResource("/CSV/Lower_Manhattan_Retailers-test.csv").getFile(), handler)
                .enableTestMode().call();
        assertEquals(0, getCurrentRouteCount());
    }

    @Test
    public void processCSVInvalidFile() throws Exception {
        new CSVImporter(db, "NotARealFile.csv", handler)
                .enableTestMode().call();
        assertEquals(0, getCurrentRouteCount());
    }

    @Test
    public void processCSVValid() throws Exception {
        new CSVImporter(db, getClass().getResource("/CSV/201601-citibike-tripdata-test.csv").getFile(), handler)
                .enableTestMode().call();
        assertEquals(50, getCurrentRouteCount());
    }

    @Test
    public void processCSVValidTwice() throws Exception {
        new CSVImporter(db, getClass().getResource("/CSV/201601-citibike-tripdata-test.csv").getFile(), handler)
                .enableTestMode().call();

        new CSVImporter(db, getClass().getResource("/CSV/201601-citibike-tripdata-test.csv").getFile(), handler)
                .enableTestMode().call();

        assertEquals(50, getCurrentRouteCount());
    }

    @Ignore
    @Test
    public void testImportSpeed() throws Exception {
        int REQUIRED_RECORDS_IMPORTED_PER_SECOND = 40;

        CSVImporter task = new CSVImporter(db, getClass().getResource("/CSV/201601-citibike-tripdata.csv").getFile(), handler).enableTestMode();
        long startTime = System.currentTimeMillis();
        task.call();

        long endTime = System.currentTimeMillis();
        long timeTakenInMillis = endTime - startTime;
        double timeTakenInSeconds = timeTakenInMillis / 1000.0;

        int recordsImported = getCurrentRouteCount();

        double recordsImportedPerSecond = recordsImported / timeTakenInSeconds;
        assertTrue(recordsImportedPerSecond > REQUIRED_RECORDS_IMPORTED_PER_SECOND);
    }

    private int getCurrentRouteCount() throws SQLException {
        return db.executeQuerySQL("SELECT COUNT(*) FROM route_information").getInt(1);
    }
}