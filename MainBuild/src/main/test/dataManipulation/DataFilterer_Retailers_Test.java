package dataManipulation;


import dataHandler.*;
import dataObjects.Cyclist;
import dataObjects.RetailLocation;
import main.HandleUsers;
import main.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class DataFilterer_Retailers_Test {


    private static SQLiteDB db;


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Main.initDB();

        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());

        HandleUsers hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = new Cyclist("Tester");
        ListDataHandler listDataHandler = new ListDataHandler(db, "test name");
        ListDataHandler.setListName("test list");

        DatabaseUser databaseUser = new DatabaseUser(db);
        databaseUser.addUser("Tester", 1, 1, 2017, 1);

        RetailerDataHandlerFake handler = new RetailerDataHandlerFake(db);
        new CSVImporter(db, DataFilterer_Retailers_Test.class.getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv")
                .getFile(), handler).enableTestMode().call();
}

    @Test
    public void filterRetailersTestName_Pizza_() {
        DataFilterer dataFilterer = new DataFilterer(db);

        List<RetailLocation> retailLocations = dataFilterer.filterRetailers("pizza", null, null, -1, null);
        int size = retailLocations.size();
        assertEquals(2, size);
    }


    @Test
    public void filterRetailersTestName__() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations = dataFilterer.filterRetailers("", null, null, -1, null);
        int size = retailLocations.size();
        assertEquals(50, size);
    }


    @Test
    public void filterRetailersTestAddress_broad_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations = dataFilterer.filterRetailers(null, "broad", null, -1, null);
        int size = retailLocations.size();
        assertEquals(8, size);
    }


    @Test
    public void filterRetailersTestAddress_a_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, "a", null, -1, null);
        int size = retailLocations.size();
        assertEquals(38, size);
    }


    @Test
    public void filterRetailersTestAddress_q_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, "q", null, -1, null);
        int size = retailLocations.size();
        assertEquals(1, size);
    }


    @Test
    public void filterRetailersTestAddress__() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, "", null, -1, null);
        int size = retailLocations.size();
        assertEquals(50, size);
    }


    @Test
    public void filterRetailersTestPrimary_Nightlife_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailName.add("The Iron Horse");
        retailName.add("The Hook & Ladder Bar");
        retailLocations = dataFilterer.filterRetailers(null, null, "Nightlife", -1, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertEquals(retailName.get(i), retailLocations.get(i).getName());
        }
    }


    @Test
    public void filterRetailersTestPrimary_Casual_Eating_Takeout_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations = dataFilterer.filterRetailers(null, null, "Casual Eating & Takeout", -1, null);
        int size = retailLocations.size();
        assertEquals(19, size);
    }

    @Test
    public void filterRetailersTestPrimary__() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailLocations = dataFilterer.filterRetailers(null, null, "", -1, null);
        int size = retailLocations.size();
        assertEquals(50, size);
    }


    @Test
    public void filterRetailersTestZip_10005_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailName.add("Eastern Newsstands");
        retailName.add("Fed Ex Kinko's");
        retailName.add("Hale and Hearty Soup");
        retailName.add("Cobbler Express");
        retailName.add("Ise Japanese Restaurant");
        retailLocations = dataFilterer.filterRetailers(null, null, null, 10005, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertEquals(retailName.get(i), retailLocations.get(i).getName());
        }
    }


    @Test
    public void filterRetailersTestZip_10038_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, 10038, null);
        int size = retailLocations.size();
        assertEquals(24, size);
    }

    @Test
    public void filterRetailersTestList_foo() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, -1, "foo");
        int size = retailLocations.size();
        assertEquals(0, size);
    }


    @Test
    public void filterRetailersTestList_test_list() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, -1, "test list");
        int size = retailLocations.size();
        assertEquals(50, size);
    }


    @Test
    public void filterRetailersTestList__() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, -1, "");
        int size = retailLocations.size();
        assertEquals(0, size);
    }


    @Test
    public void filterRetailersTestName_soup_Zip_10005_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailName.add("Hale and Hearty Soup");
        retailLocations = dataFilterer.filterRetailers("soup", null, null, 10005, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertEquals(retailName.get(i), retailLocations.get(i).getName());
        }
    }


    @Test
    public void filterRetailersTestName_cafe_Address_broad_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers("cafe", "broad", null, -1, null);
        int size = retailLocations.size();
        assertEquals(2, size);
    }


    @Test
    public void filterRetailersTestName_burrito_Primary_Causal_eating_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailName.add("Burritoville");
        retailLocations = dataFilterer.filterRetailers("burrito", null, "Casual Eating & Takeout", -1, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertEquals(retailName.get(i), retailLocations.get(i).getName());
        }
    }


    @Test
    public void filterRetailersTestAddress_broad_Primary_Causal_eating_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailName.add("Variety Cafe On Broadway");
        retailName.add("Bento Nouveau Sushi");
        retailName.add("McDonald's");
        retailName.add("Cafe Toda");
        retailLocations = dataFilterer.filterRetailers(null, "broad", "Casual Eating & Takeout", -1, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertEquals(retailName.get(i), retailLocations.get(i).getName());
        }
    }


    @Test
    public void filterRetailersTestAddress_b_Zip_10004_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailName.add("Trader's Cafe");
        retailName.add("Perfect Copy Center");
        retailName.add("New York Lottery");
        retailName.add("Picnick");
        retailName.add("Battery Gardens");
        retailLocations = dataFilterer.filterRetailers(null, "b", null, 10004, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertEquals(retailName.get(i), retailLocations.get(i).getName());
        }
    }


    @Test
    public void filterRetailersTestPrimary_shopping_Zip_10007_() {
        DataFilterer dataFilterer = new DataFilterer(db);
        List<RetailLocation> retailLocations;
        List<String> retailName = new ArrayList<>();
        retailName.add("Duane Reade");
        retailName.add("AT&T");
        retailLocations = dataFilterer.filterRetailers(null, null, "Shopping", 10007, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertEquals(retailName.get(i), retailLocations.get(i).getName());
        }
    }
}
