package dataManipulation;


import dataHandler.RetailerDataHandler;
import dataHandler.SQLiteDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertTrue;

public class UpdateData_Retailers_Test {

    private SQLiteDB db;


    @After
    public void tearDown() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }


    @Before
    public void setUp() {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());
        UpdateData.init(db);
        RetailerDataHandler retailerDataHandler = new RetailerDataHandler(db);
        retailerDataHandler.addSingleEntry("Starbucks Coffee","3 New York Plaza",0,0,"New York","NY","10004", "Casual Eating & Takeout","F-Coffeehouse");
    }


    @Test
    public void updateRetailLat_50_123456789_() throws Exception {
        UpdateData.updateRetailerField("lat", "50.123456789", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        double lat = rs.getDouble("lat");
        assertTrue(lat == 50.123456789);
    }


    @Test
    public void updateRetailLat_minus_50_123456789_() throws Exception {
        UpdateData.updateRetailerField("lat", "-50.123456789", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        double lat = rs.getDouble("lat");
        assertTrue(lat == -50.123456789);
    }


    @Test
    public void updateRetailLat_0_() throws Exception {
        UpdateData.updateRetailerField("lat", "0", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        double lat = rs.getDouble("lat");
        assertTrue(lat == 0.0);
    }


    @Test
    public void updateRetailLong_50_123456789_() throws Exception {
        UpdateData.updateRetailerField("long", "50.123456789", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT long FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        double lon = rs.getDouble("long");
        assertTrue(lon == 50.123456789);
    }


    @Test
    public void updateRetailLong_minus_50_123456789_() throws Exception {
        UpdateData.updateRetailerField("long", "-50.123456789", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT long FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        double lon = rs.getDouble("long");
        assertTrue(lon == -50.123456789);
    }


    @Test
    public void updateRetailLong_0_() throws Exception {
        UpdateData.updateRetailerField("long", "0", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT long FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        double lon = rs.getDouble("long");
        assertTrue(lon == 0.0);
    }


    @Test
    public void updateRetailCity_foovile_() throws Exception {
        UpdateData.updateRetailerField("city", "foovile", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT city FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String city = rs.getString("city");
        assertTrue(city.equals("foovile"));
    }


    @Test
    public void updateRetailCity__() throws Exception {
        UpdateData.updateRetailerField("city", "", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT city FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String city = rs.getString("city");
        assertTrue(city.equals(""));
    }


    @Test
    public void updateRetailState_FOO_() throws Exception {
        UpdateData.updateRetailerField("state", "FOO", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT state FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String state = rs.getString("state");
        assertTrue(state.equals("FOO"));
    }

    @Test
    public void updateRetailState__() throws Exception {
        UpdateData.updateRetailerField("state", "", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT state FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String state = rs.getString("state");
        assertTrue(state.equals(""));
    }


    @Test
    public void updateRetailZip_99999999() throws Exception {
        UpdateData.updateRetailerField("zip", "99999999", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        int zip = rs.getInt("zip");
        assertTrue(zip == 99999999);
    }


    @Test
    public void updateRetailZip_46754() throws Exception {
        UpdateData.updateRetailerField("zip", "46754", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        int zip = rs.getInt("zip");
        assertTrue(zip == 46754);
    }


    @Test
    public void updateRetailZip_0() throws Exception {
        UpdateData.updateRetailerField("zip", "0", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        int zip = rs.getInt("zip");
        assertTrue(zip == 0);
    }


    @Test
    public void updateRetailMainType_longString_() throws Exception {
        UpdateData.updateRetailerField("main_type", "Personal and Professional Services", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT main_type FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String mainType = rs.getString("main_type");
        assertTrue(mainType.equals("Personal and Professional Services"));
    }


    @Test
    public void updateRetailMainType_shortString_() throws Exception {
        UpdateData.updateRetailerField("main_type", "Shopping", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT main_type FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String mainType = rs.getString("main_type");
        assertTrue(mainType.equals("Shopping"));
    }


    @Test
    public void updateRetailMainType_noString_() throws Exception {
        UpdateData.updateRetailerField("main_type", "", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT main_type FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String mainType = rs.getString("main_type");
        assertTrue(mainType.equals(""));
    }


    @Test
    public void updateRetailSecondaryType_longString_() throws Exception {
        UpdateData.updateRetailerField("secondary_type", "P-Banks and Check Cashing", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT secondary_type FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String secondaryType = rs.getString("secondary_type");
        assertTrue(secondaryType.equals("P-Banks and Check Cashing"));
    }


    @Test
    public void updateRetailSecondaryType_shortString_() throws Exception {
        UpdateData.updateRetailerField("secondary_type", "F-Deli", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT secondary_type FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String secondaryType = rs.getString("secondary_type");
        assertTrue(secondaryType.equals("F-Deli"));
    }


    @Test
    public void updateRetailSecondaryType_noString_() throws Exception {
        UpdateData.updateRetailerField("secondary_type", "", "Starbucks Coffee", "3 New York Plaza");
        ResultSet rs = db.executeQuerySQL("SELECT secondary_type FROM retailer WHERE retailer_name = 'Starbucks Coffee' AND address = '3 New York Plaza'");
        String secondaryType = rs.getString("secondary_type");
        assertTrue(secondaryType.equals(""));
    }
}
