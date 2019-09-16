package dataHandler;


import main.Main;
import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CSVImporterWifiTest {

    private static SQLiteDB db;
    private WifiDataHandler handler;

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
        db.executeUpdateSQL("DROP TABLE wifi_location");
    }

    @Before
    public void init() {
        handler = new WifiDataHandler(db);
    }

    @Test
    public void processCSVIncorrectFormat() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile(), handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM wifi_location");
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void processCSVInvalidFile() throws Exception {
        new CSVImporter(db, "NotARealFile.csv", handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM wifi_location");
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void processCSVValid() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile(), handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM wifi_location");
        assertEquals(50, rs.getInt(1));
    }

    @Test
    public void processCSVValidTwice() throws Exception {
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile(), handler)
                .enableTestMode().call();
        new CSVImporter(db, getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile(), handler)
                .enableTestMode().call();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM wifi_location");
        assertEquals(50, rs.getInt(1));
    }

    @Ignore
    @Test
    public void testImportSpeed() throws Exception {
        CSVImporter task = new CSVImporter(db, getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017.csv").getFile(), handler)
                .enableTestMode();
        long startTime = System.currentTimeMillis();
        task.call();

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        long average = 2566 / timeTaken;
        long expectedAverage = 10000 / 500;
        System.out.println(timeTaken);
        System.out.println(average);
        System.out.println(expectedAverage);
        assertTrue(average > expectedAverage);
    }
}