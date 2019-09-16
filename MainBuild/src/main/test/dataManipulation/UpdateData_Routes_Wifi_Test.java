package dataManipulation;

import dataHandler.CSVImporter;
import dataHandler.RouteDataHandler;
import dataHandler.SQLiteDB;
import dataHandler.WifiDataHandler;
import main.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

public class UpdateData_Routes_Wifi_Test {

    private static SQLiteDB db;

    @AfterClass
    public static void tearDown() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }


    @BeforeClass
    public static void setUp() throws Exception {
        Main.initDB();
        ClassLoader loader = UpdateData_Routes_Wifi_Test.class.getClassLoader();

        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());

        UpdateData.init(db);

        WifiDataHandler wifiDataHandler = new WifiDataHandler(db);
        RouteDataHandler routeDataHandler = new RouteDataHandler(db);

        new CSVImporter(db, loader.getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile(), wifiDataHandler)
                .enableTestMode().call();

        new CSVImporter(db, loader.getResource("CSV/201601-citibike-tripdata-test.csv").getFile(), routeDataHandler)
                .enableTestMode().call();

        db.executeQuerySQL("select count(*) from route_information").getInt(1);
    }

    @Test
    public void updateRouteFieldDuration_500_() throws Exception {
        UpdateData.updateRouteField("tripduration", "500", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT tripduration FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int duration = rs.getInt("tripduration");
        assertEquals(500, duration);
    }


    @Test
    public void updateRouteFieldDuration_0_() throws Exception {
        UpdateData.updateRouteField("tripduration", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT tripduration FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int duration = rs.getInt("tripduration");
        assertEquals(0, duration);
    }


    @Test
    public void updateRouteFieldEndYear_2040_() throws Exception {
        UpdateData.updateRouteField("end_year", "2040", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endYear = rs.getString("end_year");
        assertEquals("2040", endYear);
    }

    @Test
    public void updateRouteFieldEndYear_0000_() throws Exception {
        UpdateData.updateRouteField("end_year", "0000", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endYear = rs.getString("end_year");
        assertEquals("0000", endYear);
    }


    @Test
    public void updateRouteFieldEndMonth_12_() throws Exception {
        UpdateData.updateRouteField("end_month", "12", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_month FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endMonth = rs.getString("end_month");
        assertEquals("12", endMonth);
    }


    @Test
    public void updateRouteFieldEndMonth_00_() throws Exception {
        UpdateData.updateRouteField("end_month", "00", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_month FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endMonth = rs.getString("end_month");
        assertEquals("00", endMonth);
    }


    @Test
    public void updateRouteFieldEndDay_25_() throws Exception {
        UpdateData.updateRouteField("end_day", "25", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_day FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endDay = rs.getString("end_day");
        assertEquals("25", endDay);
    }


    @Test
    public void updateRouteFieldEndDay_00_() throws Exception {
        UpdateData.updateRouteField("end_day", "00", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_day FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endDay = rs.getString("end_day");
        assertEquals("00", endDay);
    }


    @Test
    public void updateRouteFieldStartStationID_234543_() throws Exception {
        UpdateData.updateRouteField("start_station_id", "234543", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startID = rs.getString("start_station_id");
        assertEquals("234543", startID);
    }


    @Test
    public void updateRouteFieldStartStationID_0_() throws Exception {
        UpdateData.updateRouteField("start_station_id", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startID = rs.getString("start_station_id");
        assertEquals("0", startID);
    }


    @Test
    public void updateRouteFieldEndStationID_234543_() throws Exception {
        UpdateData.updateRouteField("end_station_id", "234543", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endID = rs.getString("end_station_id");
        assertEquals("234543", endID);
    }


    @Test
    public void updateRouteFieldEndStationID_0_() throws Exception {
        UpdateData.updateRouteField("end_station_id", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endID = rs.getString("end_station_id");
        assertEquals("0", endID);
    }


    @Test
    public void updateRouteFieldStartStationName_12_Foo_Street_() throws Exception {
        UpdateData.updateRouteField("start_station_name", "12 Foo Street", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startName = rs.getString("start_station_name");
        assertEquals("12 Foo Street", startName);
    }


    @Test
    public void updateRouteFieldStartStationName__() throws Exception {
        UpdateData.updateRouteField("start_station_name", "", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startName = rs.getString("start_station_name");
        assertEquals("", startName);

    }


    @Test
    public void updateRouteFieldEndStationName_12_Foo_Street_() throws Exception {
        UpdateData.updateRouteField("end_station_name", "12 Foo Street", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endName = rs.getString("end_station_name");
        assertEquals("12 Foo Street", endName);
    }


    @Test
    public void updateRouteFieldEndStationName__() throws Exception {
        UpdateData.updateRouteField("end_station_name", "", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endName = rs.getString("end_station_name");
        assertEquals("", endName);
    }


    @Test
    public void updateRouteFieldStartLat_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_latitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLat = rs.getDouble("start_latitude");
        assertEquals(50.123456789, startLat, 0.0);
    }


    @Test
    public void updateRouteFieldStartLat_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_latitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLat = rs.getDouble("start_latitude");
        assertEquals(startLat, -50.123456789, 0.0);
    }


    @Test
    public void updateRouteFieldStartLat_0_() throws Exception {
        UpdateData.updateRouteField("start_latitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLat = rs.getDouble("start_latitude");
        assertEquals(0.0, startLat, 0.0);
    }


    @Test
    public void updateRouteFieldStartLong_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_longitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLong = rs.getDouble("start_longitude");
        assertEquals(50.123456789, startLong, 0.0);
    }


    @Test
    public void updateRouteFieldStartLong_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_longitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLong = rs.getDouble("start_longitude");
        assertEquals(startLong, -50.123456789, 0.0);
    }


    @Test
    public void updateRouteFieldStartLong_0_() throws Exception {
        UpdateData.updateRouteField("start_longitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLong = rs.getDouble("start_longitude");
        assertEquals(0.0, startLong, 0.0);
    }


    @Test
    public void updateRouteFieldEndLat_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_latitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLat = rs.getDouble("end_latitude");
        assertEquals(50.123456789, endLat, 0.0);
    }


    @Test
    public void updateRouteFieldEndLat_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_latitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLat = rs.getDouble("end_latitude");
        assertEquals(endLat, -50.123456789, 0.0);
    }


    @Test
    public void updateRouteFieldEndLat_0_() throws Exception {
        UpdateData.updateRouteField("end_latitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLat = rs.getDouble("end_latitude");
        assertEquals(0.0, endLat, 0.0);
    }


    @Test
    public void updateRouteFieldEndLong_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_longitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLong = rs.getDouble("end_longitude");
        assertEquals(50.123456789, endLong, 0.0);
    }


    @Test
    public void updateRouteFieldEndLong_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_longitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLong = rs.getDouble("end_longitude");
        assertEquals(endLong, -50.123456789, 0.0);
    }


    @Test
    public void updateRouteFieldEndLong_0_() throws Exception {
        UpdateData.updateRouteField("end_longitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLong = rs.getDouble("end_longitude");
        assertEquals(0.0, endLong, 0.0);
    }


    @Test
    public void updateRouteFieldUserType_foo_() throws Exception {
        UpdateData.updateRouteField("usertype", "foo", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT usertype FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String type = rs.getString("usertype");
        assertEquals("foo", type);
    }


    @Test
    public void updateRouteFieldUserType__() throws Exception {
        UpdateData.updateRouteField("usertype", "", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT usertype FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String type = rs.getString("usertype");
        assertEquals("", type);
    }


    @Test
    public void updateRouteFieldBirthYear_2040_() throws Exception {
        UpdateData.updateRouteField("birth_year", "2040", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT birth_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int year = rs.getInt("birth_year");
        assertEquals(2040, year);
    }


    @Test
    public void updateRouteFieldBirthYear_0_() throws Exception {
        UpdateData.updateRouteField("birth_year", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT birth_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int year = rs.getInt("birth_year");
        assertEquals(0, year);
    }


    @Test
    public void updateRouteFieldGender_0_() throws Exception {
        UpdateData.updateRouteField("gender", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertEquals(0, gender);
    }


    @Test
    public void updateRouteFieldGender_1_() throws Exception {
        UpdateData.updateRouteField("gender", "1", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertEquals(1, gender);
    }


    @Test
    public void updateRouteFieldGender_2_() throws Exception {
        UpdateData.updateRouteField("gender", "2", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertEquals(2, gender);
    }


    @Test
    public void updateRouteFieldGender_3_() throws Exception {
        UpdateData.updateRouteField("gender", "3", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertEquals(3, gender);
    }


    @Test
    public void updateWifiFieldCost_foo_() throws Exception {
        UpdateData.updateWifiField("cost", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT cost FROM wifi_location WHERE wifi_id = '998'");
        String cost = rs.getString("cost");
        assertEquals("foo", cost);
    }


    @Test
    public void updateWifiFieldCost__() throws Exception {
        UpdateData.updateWifiField("cost", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT cost FROM wifi_location WHERE wifi_id = '998'");
        String cost = rs.getString("cost");
        assertEquals("", cost);
    }


    @Test
    public void updateWifiFieldProvider_foo_() throws Exception {
        UpdateData.updateWifiField("provider", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT provider FROM wifi_location WHERE wifi_id = '998'");
        String provider = rs.getString("provider");
        assertEquals("foo", provider);
    }


    @Test
    public void updateWifiFieldProvider__() throws Exception {
        UpdateData.updateWifiField("provider", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT provider FROM wifi_location WHERE wifi_id = '998'");
        String provider = rs.getString("provider");
        assertEquals("", provider);
    }


    @Test
    public void updateWifiFieldAddress_12_Foo_Street_() throws Exception {
        UpdateData.updateWifiField("address", "12 Foo Street", "998");
        ResultSet rs = db.executeQuerySQL("SELECT address FROM wifi_location WHERE wifi_id = '998'");
        String address = rs.getString("address");
        assertEquals("12 Foo Street", address);
    }


    @Test
    public void updateWifiFieldAddress__() throws Exception {
        UpdateData.updateWifiField("address", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT address FROM wifi_location WHERE wifi_id = '998'");
        String address = rs.getString("address");
        assertEquals("", address);
    }


    @Test
    public void updateWifiFieldLat_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lat", "50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM wifi_location WHERE wifi_id = '998'");
        double lat = rs.getDouble("lat");
        assertEquals(50.123456789, lat, 0.0);
    }


    @Test
    public void updateWifiFieldLat_minus_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lat", "-50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM wifi_location WHERE wifi_id = '998'");
        double lat = rs.getDouble("lat");
        assertEquals(lat, -50.123456789, 0.0);
    }


    @Test
    public void updateWifiFieldLat_0_() throws Exception {
        UpdateData.updateWifiField("lat", "0", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM wifi_location WHERE wifi_id = '998'");
        double lat = rs.getDouble("lat");
        assertEquals(0.0, lat, 0.0);
    }


    @Test
    public void updateWifiFieldLong_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lon", "50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lon FROM wifi_location WHERE wifi_id = '998'");
        double lon = rs.getDouble("lon");
        assertEquals(50.123456789, lon, 0.0);
    }


    @Test
    public void updateWifiFieldLong_minus_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lon", "-50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lon FROM wifi_location WHERE wifi_id = '998'");
        double lon = rs.getDouble("lon");
        assertEquals(lon, -50.123456789, 0.0);
    }


    @Test
    public void updateWifiFieldLong_0_() throws Exception {
        UpdateData.updateWifiField("lon", "0", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lon FROM wifi_location WHERE wifi_id = '998'");
        double lon = rs.getDouble("lon");
        assertEquals(0.0, lon, 0.0);
    }


    @Test
    public void updateWifiFieldRemarks_longString_() throws Exception {
        UpdateData.updateWifiField("remarks", "The quick brown fox jumped over the lazy dog", "998");
        ResultSet rs = db.executeQuerySQL("SELECT remarks FROM wifi_location WHERE wifi_id = '998'");
        String remark = rs.getString("remarks");
        assertEquals("The quick brown fox jumped over the lazy dog", remark);
    }


    @Test
    public void updateWifiFieldRemarks_shortString_() throws Exception {
        UpdateData.updateWifiField("remarks", "very short", "998");
        ResultSet rs = db.executeQuerySQL("SELECT remarks FROM wifi_location WHERE wifi_id = '998'");
        String remark = rs.getString("remarks");
        assertEquals("very short", remark);
    }


    @Test
    public void updateWifiFieldRemarks_noString_() throws Exception {
        UpdateData.updateWifiField("remarks", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT remarks FROM wifi_location WHERE wifi_id = '998'");
        String remark = rs.getString("remarks");
        assertEquals("", remark);
    }


    @Test
    public void updateWifiFieldCity_Foovile_() throws Exception {
        UpdateData.updateWifiField("city", "foovile", "998");
        ResultSet rs = db.executeQuerySQL("SELECT city FROM wifi_location WHERE wifi_id = '998'");
        String city = rs.getString("city");
        assertEquals("foovile", city);
    }


    @Test
    public void updateWifiFieldCity__() throws Exception {
        UpdateData.updateWifiField("city", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT city FROM wifi_location WHERE wifi_id = '998'");
        String city = rs.getString("city");
        assertEquals("", city);
    }


    @Test
    public void updateWifiFieldSSID_foo_() throws Exception {
        UpdateData.updateWifiField("ssid", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT ssid FROM wifi_location WHERE wifi_id = '998'");
        String SSID = rs.getString("ssid");
        assertEquals("foo", SSID);
    }


    @Test
    public void updateWifiFieldSSID__() throws Exception {
        UpdateData.updateWifiField("ssid", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT ssid FROM wifi_location WHERE wifi_id = '998'");
        String SSID = rs.getString("ssid");
        assertEquals("", SSID);
    }


    @Test
    public void updateWifiFieldSuburb_foo_() throws Exception {
        UpdateData.updateWifiField("suburb", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT suburb FROM wifi_location WHERE wifi_id = '998'");
        String suburb = rs.getString("suburb");
        assertEquals("foo", suburb);
    }


    @Test
    public void updateWifiFieldSuburb__() throws Exception {
        UpdateData.updateWifiField("suburb", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT suburb FROM wifi_location WHERE wifi_id = '998'");
        String suburb = rs.getString("suburb");
        assertEquals("", suburb);
    }


    @Test
    public void updateWifiFieldZip_99999999_() throws Exception {
        UpdateData.updateWifiField("zip", "99999999", "998");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM wifi_location WHERE wifi_id = '998'");
        int zip = rs.getInt("zip");
        assertEquals(99999999, zip);
    }


    @Test
    public void updateWifiFieldZip_43256_() throws Exception {
        UpdateData.updateWifiField("zip", "43256", "998");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM wifi_location WHERE wifi_id = '998'");
        int zip = rs.getInt("zip");
        assertEquals(43256, zip);
    }


    @Test
    public void updateWifiFieldZip_0_() throws Exception {
        UpdateData.updateWifiField("zip", "0", "998");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM wifi_location WHERE wifi_id = '998'");
        int zip = rs.getInt("zip");
        assertEquals(0, zip);
    }
}