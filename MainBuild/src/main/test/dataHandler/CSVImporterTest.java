package dataHandler;


import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;


public class CSVImporterTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static SQLiteDB db;

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
    }

    @After
    public void tearDown() {
        db.executeUpdateSQL("DROP TABLE route_information");
        db.executeUpdateSQL("DROP TABLE retailer");
        db.executeUpdateSQL("DROP TABLE wifi_location");
    }

    @Before
    public void init() {
    }

    @Test
    public void call() {

    }

    @Test
    public void result() {

    }

}