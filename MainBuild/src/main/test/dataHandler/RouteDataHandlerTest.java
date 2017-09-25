package dataHandler;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.*;

/**
 * Created by jes143 on 25/09/17.
 */
public class RouteDataHandlerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static SQLiteDB db;
    private static RouteDataHandler routeDataHandler;

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
    }

    @After
    public void tearDown() throws Exception {
        db.executeUpdateSQL("DROP TABLE route_information");
    }

    @Before
    public void init() throws Exception {
        routeDataHandler = new RouteDataHandler(db);
    }

    @Test
    public void validTable() throws Exception {
        ResultSet resultSet = db.executeQuerySQL("SELECT name FROM sqlite_master WHERE type='table' AND name='route_information'");
        assertTrue(resultSet.next());
    }

    @Test
    public void addSingleEntry() throws Exception {
        Boolean success = routeDataHandler.addSingleEntry(5,
                "2017", "01", "01", "00:00:00",
                "2017", "01", "01", "00:05:00",
                null, null, 0.0, 0.0,
                null, null, 0.1, 0.1,
                "1", "subscriber", 1990, 1);
        assertTrue(success);
    }

    @Test
    public void addSingleEntryAlreadyExists() throws Exception {
        routeDataHandler.addSingleEntry(5,
                "2017", "01", "01", "00:00:00",
                "2017", "01", "01", "00:05:00",
                null, null, 0.0, 0.0,
                null, null, 0.1, 0.1,
                "1", "subscriber", 1990, 1);
        Boolean success = routeDataHandler.addSingleEntry(5,
                "2017", "01", "01", "00:00:00",
                "2017", "01", "01", "00:05:00",
                null, null, 0.0, 0.0,
                null, null, 0.1, 0.1,
                "1", "subscriber", 1990, 1);

        assertFalse(success);
    }

    @Test
    public void addSingleEntryNullPrimaryKeys() throws Exception {
        Boolean success = routeDataHandler.addSingleEntry(5,
                null, "01", "01", "00:00:00",
                "2017", "01", "01", "00:05:00",
                null, null, 0.0, 0.0,
                null, null, 0.1, 0.1,
                "1", "subscriber", 1990, 1);
        assertFalse(success);
    }

    @Test
    public void processCSVIncorrectFormat() throws Exception {
        exception.expect(NoSuchFieldException.class);
        routeDataHandler.processCSV(getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile());
    }

    @Test
    public void processCSVInvalidFile() throws Exception {
        exception.expect(FileNotFoundException.class);
        routeDataHandler.processCSV("NotARealFile.csv");
    }

    @Test
    public void processCSVValid() throws Exception {
        routeDataHandler.processCSV(getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile());
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM route_information");
        assertEquals(50, rs.getInt(1));
        //System.out.println(rs.getInt(1));
    }

    @Test
    public void processCSVValidTwice() throws Exception {
        routeDataHandler.processCSV(getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile());
        routeDataHandler.processCSV(getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile());
        ResultSet rs = db.executeQuerySQL("SELECT COUNT(*) FROM route_information");
        assertEquals(50, rs.getInt(1));
    }

}