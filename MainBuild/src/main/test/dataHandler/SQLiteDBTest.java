package dataHandler;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Created by jes143 on 19/09/17.
 */
public class SQLiteDBTest {

    private SQLiteDB db;

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @Before
    public void setUp() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());


//        WifiDataHandler wdh = new WifiDataHandler(db);
//        System.out.println(wdh);
//        wdh.processCSV(getClass().getClassLoader().getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile());
//
//        RouteDataHandler rdh = new RouteDataHandler(db);
//        rdh.processCSV(getClass().getClassLoader().getResource("CSV/201601-citibike-tripdata-test.csv").getFile());

//        RetailerDataHandler retailerDataHandler = new RetailerDataHandler(db);
//        System.out.println("Made");
//        retailerDataHandler.processCSV(getClass().getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile());
    }

    @After
    public void tearDown() throws Exception {
        db.executeUpdateSQL("DROP TABLE test_table");
    }

    private boolean create_test_table() {
        String[] fields =
                { "F1 VARCHAR(5)",
                        "F2 INTEGER NOT NULL"};

        String primaryKey = "F1";
        String tableName = "test_table";
        return db.addTable(tableName, fields, primaryKey);
    }

    @Test
    public void addTableWhenExists() throws Exception {
        create_test_table();
        assertFalse(create_test_table());
    }

    @Test
    public void addTableWhenNotExists() throws Exception {
        assertTrue(create_test_table());
    }

    @Test
    public void addTableInvalidFieldExists() throws Exception {
        String[] fields =
                { "F1 VARCHAR(5)",
                "F2 INTEGER NOT NULL"};

        String primaryKey = "F3";
        String tableName = "test_table";
        assertFalse(db.addTable(tableName, fields, primaryKey));
    }

    @Test
    public void executeUpdateInsertSQL() throws Exception {
        create_test_table();
        int outcome = db.executeUpdateSQL("insert into test_table values('Hello', 5)");
        assertEquals(1, outcome);
    }

    @Test
    public void executeUpdateInsertDuplicateSQL() throws Exception {
        create_test_table();
        db.executeUpdateSQL("insert into test_table values('Hello', 5)");
        int outcome = db.executeUpdateSQL("insert into test_table values('Hello', 5)");
        assertEquals(-1, outcome);
    }

    @Test
    public void executeValidQuerySQL() throws Exception {
        create_test_table();
        db.executeUpdateSQL("insert into test_table values('Hello', 5)");
        ResultSet rs = db.executeQuerySQL("select * from test_table where F1 = 'Hello'");
        assertTrue(rs.next());
    }

    @Test
    public void executeInvalidQuerySQL() throws Exception {
        create_test_table();
        db.executeUpdateSQL("insert into test_table values('Hello', 5)");
        ResultSet rs = db.executeQuerySQL("select * from test_table where F1 = 'a'");
        assertFalse(rs.next());
    }

    @Test
    public void getValidPreparedStatement() throws Exception {
        create_test_table();
        PreparedStatement ps = db.getPreparedStatement("select * from test_table where F1 = ?");
        assertNotNull(ps);
    }

    @Test
    public void getInvalidPreparedStatement() throws Exception {
        create_test_table();
        PreparedStatement ps = db.getPreparedStatement("select * from test_table where F4 = ?");
        assertNull(ps);
    }

    @Test
    public void setAutoCommit() throws Exception {

    }

    @Test
    public void commit() throws Exception {

    }

    @Test
    public void rollback() throws Exception {

    }
}