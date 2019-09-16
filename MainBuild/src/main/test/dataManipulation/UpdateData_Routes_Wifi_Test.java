package dataManipulation;

import dataHandler.CSVImporter;
import dataHandler.RouteDataHandler;
import dataHandler.SQLiteDB;
import dataHandler.WifiDataHandler;
import javafx.concurrent.Task;
import main.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

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
        ApplicationTest.launch(Main.class);
        ClassLoader loader = UpdateData_Routes_Wifi_Test.class.getClassLoader();
        Task<Void> task;

        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());

        UpdateData.init(db);

        WifiDataHandler wifiDataHandler = new WifiDataHandler(db);
        RouteDataHandler routeDataHandler = new RouteDataHandler(db);

        task = new CSVImporter(db, loader.getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile(), wifiDataHandler);
        task.run();

        task = new CSVImporter(db, loader.getResource("CSV/201601-citibike-tripdata-test.csv").getFile(), routeDataHandler);
        System.out.println(1);
        task.run();
        System.out.println(2);
        try {
            System.out.println(db.executeQuerySQL("select count(*) from route_information").getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateRouteFieldDuration_500_() throws Exception {
        UpdateData.updateRouteField("tripduration", "500", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT tripduration FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int duration = rs.getInt("tripduration");
        assertTrue(duration == 500);
    }


    @Test
    public void updateRouteFieldDuration_0_() throws Exception {
        UpdateData.updateRouteField("tripduration", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT tripduration FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int duration = rs.getInt("tripduration");
        assertTrue(duration == 0);
    }


    @Test
    public void updateRouteFieldEndYear_2040_() throws Exception {
        UpdateData.updateRouteField("end_year", "2040", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endYear = rs.getString("end_year");
        assertTrue(endYear.equals("2040"));
    }

    @Test
    public void updateRouteFieldEndYear_0000_() throws Exception {
        UpdateData.updateRouteField("end_year", "0000", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endYear = rs.getString("end_year");
        assertTrue(endYear.equals("0000"));
    }


    @Test
    public void updateRouteFieldEndMonth_12_() throws Exception {
        UpdateData.updateRouteField("end_month", "12", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_month FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endMonth = rs.getString("end_month");
        assertTrue(endMonth.equals("12"));
    }


    @Test
    public void updateRouteFieldEndMonth_00_() throws Exception {
        UpdateData.updateRouteField("end_month", "00", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_month FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endMonth = rs.getString("end_month");
        assertTrue(endMonth.equals("00"));
    }


    @Test
    public void updateRouteFieldEndDay_25_() throws Exception {
        UpdateData.updateRouteField("end_day", "25", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_day FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endDay = rs.getString("end_day");
        assertTrue(endDay.equals("25"));
    }


    @Test
    public void updateRouteFieldEndDay_00_() throws Exception {
        UpdateData.updateRouteField("end_day", "00", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_day FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endDay = rs.getString("end_day");
        assertTrue(endDay.equals("00"));
    }


    @Test
    public void updateRouteFieldStartStationID_234543_() throws Exception {
        UpdateData.updateRouteField("start_station_id", "234543", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startID = rs.getString("start_station_id");
        assertTrue(startID.equals("234543"));
    }


    @Test
    public void updateRouteFieldStartStationID_0_() throws Exception {
        UpdateData.updateRouteField("start_station_id", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startID = rs.getString("start_station_id");
        assertTrue(startID.equals("0"));
    }


    @Test
    public void updateRouteFieldEndStationID_234543_() throws Exception {
        UpdateData.updateRouteField("end_station_id", "234543", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endID = rs.getString("end_station_id");
        assertTrue(endID.equals("234543"));
    }


    @Test
    public void updateRouteFieldEndStationID_0_() throws Exception {
        UpdateData.updateRouteField("end_station_id", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_id FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endID = rs.getString("end_station_id");
        assertTrue(endID.equals("0"));
    }


    @Test
    public void updateRouteFieldStartStationName_12_Foo_Street_() throws Exception {
        UpdateData.updateRouteField("start_station_name", "12 Foo Street", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startName = rs.getString("start_station_name");
        assertTrue(startName.equals("12 Foo Street"));
    }


    @Test
    public void updateRouteFieldStartStationName__() throws Exception {
        UpdateData.updateRouteField("start_station_name", "", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String startName = rs.getString("start_station_name");
        assertTrue(startName.equals(""));

    }


    @Test
    public void updateRouteFieldEndStationName_12_Foo_Street_() throws Exception {
        UpdateData.updateRouteField("end_station_name", "12 Foo Street", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endName = rs.getString("end_station_name");
        assertTrue(endName.equals("12 Foo Street"));
    }


    @Test
    public void updateRouteFieldEndStationName__() throws Exception {
        UpdateData.updateRouteField("end_station_name", "", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_station_name FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String endName = rs.getString("end_station_name");
        assertTrue(endName.equals(""));
    }


    @Test
    public void updateRouteFieldStartLat_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_latitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLat = rs.getDouble("start_latitude");
        assertTrue(startLat == 50.123456789);
    }


    @Test
    public void updateRouteFieldStartLat_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_latitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLat = rs.getDouble("start_latitude");
        assertTrue(startLat == -50.123456789);
    }


    @Test
    public void updateRouteFieldStartLat_0_() throws Exception {
        UpdateData.updateRouteField("start_latitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLat = rs.getDouble("start_latitude");
        assertTrue(startLat == 0.0);
    }


    @Test
    public void updateRouteFieldStartLong_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_longitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLong = rs.getDouble("start_longitude");
        assertTrue(startLong == 50.123456789);
    }


    @Test
    public void updateRouteFieldStartLong_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("start_longitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLong = rs.getDouble("start_longitude");
        assertTrue(startLong == -50.123456789);
    }


    @Test
    public void updateRouteFieldStartLong_0_() throws Exception {
        UpdateData.updateRouteField("start_longitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT start_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double startLong = rs.getDouble("start_longitude");
        assertTrue(startLong == 0.0);
    }


    @Test
    public void updateRouteFieldEndLat_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_latitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLat = rs.getDouble("end_latitude");
        assertTrue(endLat == 50.123456789);
    }


    @Test
    public void updateRouteFieldEndLat_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_latitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLat = rs.getDouble("end_latitude");
        assertTrue(endLat == -50.123456789);
    }


    @Test
    public void updateRouteFieldEndLat_0_() throws Exception {
        UpdateData.updateRouteField("end_latitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_latitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLat = rs.getDouble("end_latitude");
        assertTrue(endLat == 0.0);
    }


    @Test
    public void updateRouteFieldEndLong_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_longitude", "50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLong = rs.getDouble("end_longitude");
        assertTrue(endLong == 50.123456789);
    }


    @Test
    public void updateRouteFieldEndLong_minus_50_123456789_() throws Exception {
        UpdateData.updateRouteField("end_longitude", "-50.123456789", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLong = rs.getDouble("end_longitude");
        assertTrue(endLong == -50.123456789);
    }


    @Test
    public void updateRouteFieldEndLong_0_() throws Exception {
        UpdateData.updateRouteField("end_longitude", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT end_longitude FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        double endLong = rs.getDouble("end_longitude");
        assertTrue(endLong == 0.0);
    }


    @Test
    public void updateRouteFieldUserType_foo_() throws Exception {
        UpdateData.updateRouteField("usertype", "foo", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT usertype FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String type = rs.getString("usertype");
        assertTrue(type.equals("foo"));
    }


    @Test
    public void updateRouteFieldUserType__() throws Exception {
        UpdateData.updateRouteField("usertype", "", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT usertype FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        String type = rs.getString("usertype");
        assertTrue(type.equals(""));
    }


    @Test
    public void updateRouteFieldBirthYear_2040_() throws Exception {
        UpdateData.updateRouteField("birth_year", "2040", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT birth_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int year = rs.getInt("birth_year");
        assertTrue(year == 2040);
    }


    @Test
    public void updateRouteFieldBirthYear_0_() throws Exception {
        UpdateData.updateRouteField("birth_year", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT birth_year FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int year = rs.getInt("birth_year");
        assertTrue(year == 0);
    }


    @Test
    public void updateRouteFieldGender_0_() throws Exception {
        UpdateData.updateRouteField("gender", "0", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertTrue(gender == 0);
    }


    @Test
    public void updateRouteFieldGender_1_() throws Exception {
        UpdateData.updateRouteField("gender", "1", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertTrue(gender == 1);
    }


    @Test
    public void updateRouteFieldGender_2_() throws Exception {
        UpdateData.updateRouteField("gender", "2", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertTrue(gender == 2);
    }


    @Test
    public void updateRouteFieldGender_3_() throws Exception {
        UpdateData.updateRouteField("gender", "3", "22285", "2016", "01", "01", "00:00:41");
        ResultSet rs = db.executeQuerySQL("SELECT gender FROM route_information WHERE bikeid = '22285' AND " +
                "start_year = '2016' AND start_month = '01' AND start_day = '01' AND start_time = '00:00:41'");
        int gender = rs.getInt("gender");
        assertTrue(gender == 3);
    }


    @Test
    public void updateWifiFieldCost_foo_() throws Exception {
        UpdateData.updateWifiField("cost", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT cost FROM wifi_location WHERE wifi_id = '998'");
        String cost = rs.getString("cost");
        assertTrue(cost.equals("foo"));
    }


    @Test
    public void updateWifiFieldCost__() throws Exception {
        UpdateData.updateWifiField("cost", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT cost FROM wifi_location WHERE wifi_id = '998'");
        String cost = rs.getString("cost");
        assertTrue(cost.equals(""));
    }


    @Test
    public void updateWifiFieldProvider_foo_() throws Exception {
        UpdateData.updateWifiField("provider", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT provider FROM wifi_location WHERE wifi_id = '998'");
        String provider = rs.getString("provider");
        assertTrue(provider.equals("foo"));
    }


    @Test
    public void updateWifiFieldProvider__() throws Exception {
        UpdateData.updateWifiField("provider", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT provider FROM wifi_location WHERE wifi_id = '998'");
        String provider = rs.getString("provider");
        assertTrue(provider.equals(""));
    }


    @Test
    public void updateWifiFieldAddress_12_Foo_Street_() throws Exception {
        UpdateData.updateWifiField("address", "12 Foo Street", "998");
        ResultSet rs = db.executeQuerySQL("SELECT address FROM wifi_location WHERE wifi_id = '998'");
        String address = rs.getString("address");
        assertTrue(address.equals("12 Foo Street"));
    }


    @Test
    public void updateWifiFieldAddress__() throws Exception {
        UpdateData.updateWifiField("address", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT address FROM wifi_location WHERE wifi_id = '998'");
        String address = rs.getString("address");
        assertTrue(address.equals(""));
    }


    @Test
    public void updateWifiFieldLat_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lat", "50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM wifi_location WHERE wifi_id = '998'");
        double lat = rs.getDouble("lat");
        assertTrue(lat == 50.123456789);
    }


    @Test
    public void updateWifiFieldLat_minus_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lat", "-50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM wifi_location WHERE wifi_id = '998'");
        double lat = rs.getDouble("lat");
        assertTrue(lat == -50.123456789);
    }


    @Test
    public void updateWifiFieldLat_0_() throws Exception {
        UpdateData.updateWifiField("lat", "0", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lat FROM wifi_location WHERE wifi_id = '998'");
        double lat = rs.getDouble("lat");
        assertTrue(lat == 0.0);
    }


    @Test
    public void updateWifiFieldLong_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lon", "50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lon FROM wifi_location WHERE wifi_id = '998'");
        double lon = rs.getDouble("lon");
        assertTrue(lon == 50.123456789);
    }


    @Test
    public void updateWifiFieldLong_minus_50_123456789_() throws Exception {
        UpdateData.updateWifiField("lon", "-50.123456789", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lon FROM wifi_location WHERE wifi_id = '998'");
        double lon = rs.getDouble("lon");
        assertTrue(lon == -50.123456789);
    }


    @Test
    public void updateWifiFieldLong_0_() throws Exception {
        UpdateData.updateWifiField("lon", "0", "998");
        ResultSet rs = db.executeQuerySQL("SELECT lon FROM wifi_location WHERE wifi_id = '998'");
        double lon = rs.getDouble("lon");
        assertTrue(lon == 0.0);
    }


    @Test
    public void updateWifiFieldRemarks_longString_() throws Exception {
        UpdateData.updateWifiField("remarks", "The quick brown fox jumped over the lazy dog", "998");
        ResultSet rs = db.executeQuerySQL("SELECT remarks FROM wifi_location WHERE wifi_id = '998'");
        String remark = rs.getString("remarks");
        assertTrue(remark.equals("The quick brown fox jumped over the lazy dog"));
    }


    @Test
    public void updateWifiFieldRemarks_shortString_() throws Exception {
        UpdateData.updateWifiField("remarks", "very short", "998");
        ResultSet rs = db.executeQuerySQL("SELECT remarks FROM wifi_location WHERE wifi_id = '998'");
        String remark = rs.getString("remarks");
        assertTrue(remark.equals("very short"));
    }


    @Test
    public void updateWifiFieldRemarks_noString_() throws Exception {
        UpdateData.updateWifiField("remarks", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT remarks FROM wifi_location WHERE wifi_id = '998'");
        String remark = rs.getString("remarks");
        assertTrue(remark.equals(""));
    }


    @Test
    public void updateWifiFieldCity_Foovile_() throws Exception {
        UpdateData.updateWifiField("city", "foovile", "998");
        ResultSet rs = db.executeQuerySQL("SELECT city FROM wifi_location WHERE wifi_id = '998'");
        String city = rs.getString("city");
        assertTrue(city.equals("foovile"));
    }


    @Test
    public void updateWifiFieldCity__() throws Exception {
        UpdateData.updateWifiField("city", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT city FROM wifi_location WHERE wifi_id = '998'");
        String city = rs.getString("city");
        assertTrue(city.equals(""));
    }


    @Test
    public void updateWifiFieldSSID_foo_() throws Exception {
        UpdateData.updateWifiField("ssid", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT ssid FROM wifi_location WHERE wifi_id = '998'");
        String SSID = rs.getString("ssid");
        assertTrue(SSID.equals("foo"));
    }


    @Test
    public void updateWifiFieldSSID__() throws Exception {
        UpdateData.updateWifiField("ssid", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT ssid FROM wifi_location WHERE wifi_id = '998'");
        String SSID = rs.getString("ssid");
        assertTrue(SSID.equals(""));
    }


    @Test
    public void updateWifiFieldSuburb_foo_() throws Exception {
        UpdateData.updateWifiField("suburb", "foo", "998");
        ResultSet rs = db.executeQuerySQL("SELECT suburb FROM wifi_location WHERE wifi_id = '998'");
        String suburb = rs.getString("suburb");
        assertTrue(suburb.equals("foo"));
    }


    @Test
    public void updateWifiFieldSuburb__() throws Exception {
        UpdateData.updateWifiField("suburb", "", "998");
        ResultSet rs = db.executeQuerySQL("SELECT suburb FROM wifi_location WHERE wifi_id = '998'");
        String suburb = rs.getString("suburb");
        assertTrue(suburb.equals(""));
    }


    @Test
    public void updateWifiFieldZip_99999999_() throws Exception {
        UpdateData.updateWifiField("zip", "99999999", "998");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM wifi_location WHERE wifi_id = '998'");
        int zip = rs.getInt("zip");
        assertTrue(zip == 99999999);
    }


    @Test
    public void updateWifiFieldZip_43256_() throws Exception {
        UpdateData.updateWifiField("zip", "43256", "998");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM wifi_location WHERE wifi_id = '998'");
        int zip = rs.getInt("zip");
        assertTrue(zip == 43256);
    }


    @Test
    public void updateWifiFieldZip_0_() throws Exception {
        UpdateData.updateWifiField("zip", "0", "998");
        ResultSet rs = db.executeQuerySQL("SELECT zip FROM wifi_location WHERE wifi_id = '998'");
        int zip = rs.getInt("zip");
        assertTrue(zip == 0);
    }
}