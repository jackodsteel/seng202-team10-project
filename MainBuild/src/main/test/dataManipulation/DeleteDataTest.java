package dataManipulation;

import dataHandler.*;
import dataObjects.Cyclist;
import dataObjects.Route;
import main.HandleUsers;
import main.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

/**
 * Testing class for DeleteData class
 */
public class DeleteDataTest {
    private static SQLiteDB db;
    private DeleteData deleteData;

    @AfterClass
    public static void clearDB() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        Main.initDB();
        // creates all needed tables, users and populates tables.
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());

        HandleUsers hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = new Cyclist("Tester1");

        DatabaseUser databaseUser = new DatabaseUser(db);
        databaseUser.addUser("Tester1", 1, 1, 2017, 1);
        databaseUser.addUser("Tester2", 1, 1, 2017, 1);

        ListDataHandler listDataHandler = new ListDataHandler(db, "Tester2");
        String testList2 = "test list 2";
        ListDataHandler.setListName(testList2);
        listDataHandler.addList(testList2);

        ClassLoader loader = DatabaseUserTest.class.getClassLoader();

        WifiDataHandler wifiDataHandler = new WifiDataHandler(db);
        RouteDataHandler routeDataHandler = new RouteDataHandler(db);
        RetailerDataHandlerFake retailDataHandler = new RetailerDataHandlerFake(db);

        new CSVImporter(db, loader.getResource("CSV/NYC_Free_Public_WiFi_03292017-test.csv").getFile(), wifiDataHandler)
                .enableTestMode().call();

        new CSVImporter(db, loader.getResource("CSV/201601-citibike-tripdata-test.csv").getFile(), routeDataHandler)
                .enableTestMode().call();

        new CSVImporter(db, loader.getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile(), retailDataHandler)
                .enableTestMode().call();


        FavouriteRouteData favouriteRouteData = new FavouriteRouteData(db);
        TakenRoutes takenRoutes = new TakenRoutes(db);
        FavouriteRetailData favouriteRetailData = new FavouriteRetailData(db);
        FavouriteWifiData favouriteWifiData = new FavouriteWifiData(db);

        favouriteRouteData.addFavouriteRoute("Tester1", "2016", "01",
                "01", "00:00:41", "22285", 5, hu);
        takenRoutes.addTakenRoute("Tester1", "2016", "01",
                "01", "00:26:55", "16498", 5, hu);
        favouriteRetailData.addFavouriteRetail("Tester1", "Starbucks Coffee", "3 New York Plaza");
        favouriteWifiData.addFavouriteWifi("Tester1", "998");

        hu.currentCyclist = new Cyclist("Tester2");
        favouriteRouteData.addFavouriteRoute("Tester2", "2016", "01", "03",
                "18:15:55", "21416", 5, hu);
        takenRoutes.addTakenRoute("Tester2", "2016", "01", "03",
                "18:15:55", "21416", 5, hu);
        favouriteWifiData.addFavouriteWifi("Tester2", "1020");
        favouriteRetailData.addFavouriteRetail("Tester2", "WirelessRUS", "200 Water Street");
    }

    @Test
    public void checkRouteDeletionStatus_in_other_user_list() {
        deleteData = new DeleteData(db, "Tester1");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkRouteDeletionStatus(new Route("18702", "18:02:29", "03", "01", "2016"));
        assertEquals(DeleteData.DeletionStatus.IN_ANOTHER_USERS_LIST, deletionStatus);
    }


    @Test
    public void checkRouteDeletionStatus_in_other_user_fav() {
        deleteData = new DeleteData(db, "Tester2");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkRouteDeletionStatus(new Route("22285", "00:00:41", "01", "01", "2016"));
        assertEquals(DeleteData.DeletionStatus.IN_ANOTHER_USERS_FAVOURITES, deletionStatus);
    }


    @Test
    public void checkRouteDeletionStatus_in_other_user_completed() {
        deleteData = new DeleteData(db, "Tester2");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkRouteDeletionStatus(new Route("16498", "00:26:55", "01", "01", "2016"));
        assertEquals(DeleteData.DeletionStatus.IN_ANOTHER_USERS_COMPLETED_ROUTES, deletionStatus);
    }


    @Test
    public void checkRouteDeletionStatus_clear_to_delete() {
        deleteData = new DeleteData(db, "Tester2");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkRouteDeletionStatus(new Route("21416", "18:15:55", "03", "01", "2016"));
        assertEquals(DeleteData.DeletionStatus.CAN_BE_DELETED, deletionStatus);
    }


    @Test
    public void deleteRoute() throws Exception {
        deleteData = new DeleteData(db, "Tester2");
        deleteData.deleteRoute(new Route("21416", "18:15:55", "03", "01", "2016"));
        int resultRoute = db.executeQuerySQL("SELECT count(*) FROM route_information WHERE start_time = '18:15:55'" +
                "AND start_day = '03' AND start_month = '01' AND start_year = '2016' AND " +
                "bikeid = '21416';").getInt(1);
        int resultFav = db.executeQuerySQL("SELECT count(*) FROM favourite_routes WHERE start_time = '18:15:55'" +
                "AND start_day = '03' AND start_month = '01' AND start_year = '2016' AND " +
                "bikeid = '21416' AND name = 'Tester2';").getInt(1);
        int resultTaken = db.executeQuerySQL("SELECT count(*) FROM taken_routes WHERE start_time = '18:15:55'" +
                "AND start_day = '03' AND start_month = '01' AND start_year = '2016' AND " +
                "bikeid = '21416' AND name = 'Tester2';").getInt(1);

        assertEquals(0, resultRoute);
        assertEquals(0, resultFav);
        assertEquals(0, resultTaken);
    }


    @Test
    public void checkWifiDeletionStatus_in_other_user_list() {
        deleteData = new DeleteData(db, "Tester1");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkWifiDeletionStatus("3");
        assertEquals(DeleteData.DeletionStatus.IN_ANOTHER_USERS_LIST, deletionStatus);
    }


    @Test
    public void checkWifiDeletionStatus_in_other_user_fav() {
        deleteData = new DeleteData(db, "Tester2");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkWifiDeletionStatus("998");
        assertEquals(DeleteData.DeletionStatus.IN_ANOTHER_USERS_FAVOURITES, deletionStatus);
    }


    @Test
    public void checkWifiDeletionStatus_clear_to_delete() {
        deleteData = new DeleteData(db, "Tester2");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkWifiDeletionStatus("1020");
        assertEquals(DeleteData.DeletionStatus.CAN_BE_DELETED, deletionStatus);
    }


    @Test
    public void deleteWifiLocation() throws Exception {
        deleteData = new DeleteData(db, "Tester2");
        deleteData.deleteWifiLocation("1020");
        int resultWifi = db.executeQuerySQL("SELECT count(*) FROM wifi_location WHERE " +
                "wifi_id = '1020';").getInt(1);
        int resultFav = db.executeQuerySQL("SELECT count(*) FROM favourite_wifi WHERE wifi_id = '1020' AND " +
                "name = 'Tester2';").getInt(1);

        assertEquals(0, resultWifi);
        assertEquals(0, resultFav);

    }


    @Test
    public void checkRetailDeletionStatus_in_other_user_list() {
        deleteData = new DeleteData(db, "Tester1");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkRetailDeletionStatus("Yip's Oriental Express",
                "136 William Street");
        assertEquals(DeleteData.DeletionStatus.IN_ANOTHER_USERS_LIST, deletionStatus);
    }


    @Test
    public void checkRetailDeletionStatus_in_other_user_fav() {
        deleteData = new DeleteData(db, "Tester2");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkRetailDeletionStatus("Starbucks Coffee",
                "3 New York Plaza");
        assertEquals(DeleteData.DeletionStatus.IN_ANOTHER_USERS_FAVOURITES, deletionStatus);
    }


    @Test
    public void checkRetailDeletionStatus_clear_to_delete() {
        deleteData = new DeleteData(db, "Tester2");
        DeleteData.DeletionStatus deletionStatus = deleteData.checkRetailDeletionStatus("WirelessRUS",
                "200 Water Street");
        assertEquals(DeleteData.DeletionStatus.CAN_BE_DELETED, deletionStatus);
    }


    @Test
    public void deleteRetailer() throws Exception {
        deleteData = new DeleteData(db, "Tester2");
        deleteData.deleteRetailer("WirelessRUS", "200 Water Street");
        int resultRetail = db.executeQuerySQL("SELECT count(*) FROM retailer WHERE " +
                "retailer_name = 'WirelessRUS' AND address = '200 Water Street';").getInt(1);
        int resultFav = db.executeQuerySQL("SELECT count(*) FROM favourite_retail WHERE retailer_name = " +
                "'WirelessRUS' AND address = '200 Water Street' AND name = 'Tester2';").getInt(1);

        assertEquals(0, resultRetail);
        assertEquals(0, resultFav);
    }
}