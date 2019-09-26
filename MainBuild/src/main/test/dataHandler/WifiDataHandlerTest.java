package dataHandler;

import org.junit.*;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by jes143 on 25/09/17.
 */
public class WifiDataHandlerTest {

    private static SQLiteDB db;
    private static WifiDataHandler wifiDataHandler;

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
        db.executeUpdateSQL("DROP TABLE wifi_location");
    }

    @Before
    public void init() {
        wifiDataHandler = new WifiDataHandler(db);
    }

    @Test
    public void validTable() throws Exception {
        ResultSet resultSet = db.executeQuerySQL("SELECT name FROM sqlite_master WHERE type='table' AND name='wifi_location'");
        assertTrue(resultSet.next());
    }

    @Test
    public void addSingleEntry() {
        Boolean success = wifiDataHandler.addSingleEntry("ID", "cost", "provider", "address", 0.0, 0.0, "remarks", "city", "ssid", "suburb", "zip");
        assertTrue(success);
    }

    @Test
    public void addSingleEntryAlreadyExists() {
        wifiDataHandler.addSingleEntry("ID", "cost", "provider", "address", 0.0, 0.0, "remarks", "city", "ssid", "suburb", "zip");
        Boolean success = wifiDataHandler.addSingleEntry("ID", "cost", "provider", "address", 0.0, 0.0, "remarks", "city", "ssid", "suburb", "zip");

        assertFalse(success);
    }

    @Test
    public void addSingleEntryNullPrimaryKeys() {
        Boolean success = wifiDataHandler.addSingleEntry(null, "cost", "provider", "address", 0.0, 0.0, "remarks", "city", "ssid", "suburb", "zip");
        assertFalse(success);
    }

    @Test
    public void processLineValid() {
        String[] list = {"998","POINT (-73.99403913047428 40.745968480330795)","MN","Free","LinkNYC - Citybridge","mn-05-123662","179 WEST 26 STREET","40.745968","-73.994039","985901.695307","211053.130644","Outdoor Kiosk","Tablet Internet -phone "," Free 1 GB Wi-FI Service","New York","LinkNYC Free Wi-Fi","LINK-008695","01/18/2017 12:00:00 AM +0000","1","Manhattan","MN17","Midtown-Midtown South","3","10001","105","95","1009500","0","0","1425"};
        CSVImporter importer = mock(CSVImporter.class);
        wifiDataHandler.processLine(list, importer);
        verify(importer).result(true);
    }
}