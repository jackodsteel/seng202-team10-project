package dataHandler;


import javafx.concurrent.Task;
import main.Main;
import org.junit.*;
import org.testfx.framework.junit.ApplicationTest;

import java.nio.file.Files;
import java.sql.ResultSet;

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
    public static void setUp() throws Exception {
        ApplicationTest.launch(Main.class);
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
        Task<Void> task = new CSVImporter(db, getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile(), handler);
        task.run();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM route_information");
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void processCSVInvalidFile() throws Exception {
        Task<Void> task = new CSVImporter(db, "NotARealFile.csv", handler);
        task.run();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM route_information");
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void processCSVValid() throws Exception {
        Task<Void> task = new CSVImporter(db, getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile(), handler);
        task.run();
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM route_information");
        assertEquals(50, rs.getInt(1));
    }

    @Test
    public void processCSVValidTwice() throws Exception {
        Task<Void> task;

        task = new CSVImporter(db, getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile(), handler);
        task.run();

        task = new CSVImporter(db, getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile(), handler);
        task.run();

        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM route_information");
        assertEquals(50, rs.getInt(1));
    }

    @Ignore
    @Test
    public void testImportSpeed() {
        Task task = new CSVImporter(db, getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata.csv").getFile(), handler);
        long startTime = System.currentTimeMillis();
        task.run();

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        long average = 509478/timeTaken;
        long expectedAverage = 10000/500;
        System.out.println(timeTaken);
        System.out.println(average);
        System.out.println(expectedAverage);
        assertTrue(average > expectedAverage);
    }
}