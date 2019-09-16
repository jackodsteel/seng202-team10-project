package dataHandler;

import main.Main;
import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Using fake retailerdatahandler as mocks were really complicated as Geocoder is static
 */
public class CSVImporterRetailerTest {


    private static SQLiteDB db;
    private RetailerDataHandlerFake handler;

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        Main.initDB();

        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
    }

    @After
    public void tearDown() {
        db.executeUpdateSQL("DROP TABLE retailer");
    }

    @Before
    public void init() {
        handler = new RetailerDataHandlerFake(db);
    }

    @Test
    public void processCSVIncorrectFormat() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile(), handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM retailer");
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void processCSVInvalidFile() throws Exception {
        new CSVImporter(db, "NotARealFile.csv", handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM retailer");
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void processCSVValidOld() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-new-test.csv").getFile(), handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM retailer");
        assertEquals(50, rs.getInt(1));
    }

    @Test
    public void processCSVValidNew() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile(), handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM retailer");
        assertEquals(50, rs.getInt(1));
    }

    @Test
    public void processCSVValidTwiceOld() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile(), handler)
                .enableTestMode().call();

        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile(), handler)
                .enableTestMode().call();

        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM retailer");
        assertEquals(50, rs.getInt(1));
    }

    @Test
    public void processCSVValidTwiceNew() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-new-test.csv").getFile(), handler)
                .enableTestMode().call();

        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-new-test.csv").getFile(), handler)
                .enableTestMode().call();

        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM retailer");
        assertEquals(50, rs.getInt(1));
    }

    @Ignore
    @Test
    public void testImportSpeed() throws Exception {
        CSVImporter task = new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers.csv").getFile(), handler)
                .enableTestMode();
        long startTime = System.currentTimeMillis();
        task.call();

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        long average = 772 / timeTaken;
        long expectedAverage = 10000 / 500;
        System.out.println(timeTaken);
        System.out.println(average);
        System.out.println(expectedAverage);
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM retailer");
        System.out.println(rs.getInt(1));
        assertTrue(average > expectedAverage);
    }
}