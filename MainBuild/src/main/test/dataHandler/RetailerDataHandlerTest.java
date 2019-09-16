package dataHandler;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by jes143 on 25/09/17.
 */
public class RetailerDataHandlerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static SQLiteDB db;
    private static RetailerDataHandlerFake retailerDataHandler;

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
        db.executeUpdateSQL("DROP TABLE retailer");
    }

    @Before
    public void init() throws Exception {
        retailerDataHandler = new RetailerDataHandlerFake(db);
    }

    @Test
    public void validTable() throws Exception {
        ResultSet resultSet = db.executeQuerySQL("SELECT name FROM sqlite_master WHERE type='table' AND name='retailer'");
        assertTrue(resultSet.next());
    }

    @Test
    public void addSingleEntry() throws Exception {
        Boolean success = retailerDataHandler.addSingleEntry("name", "address", 0.0, 0.0, "city", "state", "zip", "type1", "type2");
        assertTrue(success);
    }

    @Test
    public void addSingleEntryAlreadyExists() throws Exception {
        retailerDataHandler.addSingleEntry("name", "address", 0.0, 0.0, "city", "state", "zip", "type1", "type2");
        Boolean success = retailerDataHandler.addSingleEntry("name", "address", 0.0, 0.0, "city", "state", "zip", "type1", "type2");

        assertFalse(success);
    }

    @Test
    public void addSingleEntryNullPrimaryKeys() throws Exception {
        Boolean success = retailerDataHandler.addSingleEntry(null, "address", 0.0, 0.0, "city", "state", "zip", "type1", "type2");
        assertFalse(success);
    }

    @Test
    public void processLineValidOld() throws Exception {

        String[] list = {"Starbucks Coffee","3 New York Plaza","","New York","NY","10004","8-32","Casual Eating & Takeout","F-Coffeehouse"};
        CSVImporter importer = mock(CSVImporter.class);
        retailerDataHandler.processLine(list, importer);
        verify(importer).result(true);
    }

    @Test
    public void processLineValidNew() throws Exception {

        String[] list = {"New York Health & Racquet Club","39 Whitehall Street","","New York","NY","10004","Aug-32","Personal and Professional Services","P-Athletic Clubs/Fitness","MANHATTAN","40.703037","-74.012969","1","1","9","1087700","1000087501","Battery Park City-Lower Manhattan"};
        CSVImporter importer = mock(CSVImporter.class);
        retailerDataHandler.processLine(list, importer);
        verify(importer).result(true);
    }

    @Test
    public void processLineInvalidFieldCount() throws Exception {
        String[] list = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        CSVImporter importer = mock(CSVImporter.class);
        retailerDataHandler.processLine(list, importer);
        verify(importer).result(false);
    }
}