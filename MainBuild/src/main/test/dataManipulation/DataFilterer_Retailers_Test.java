package dataManipulation;


import dataHandler.*;
import dataObjects.Cyclist;
import dataObjects.RetailLocation;
import javafx.concurrent.Task;
import main.HandleUsers;
import main.Main;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;


public class DataFilterer_Retailers_Test {


    private static SQLiteDB db;
    private static ListDataHandler listDataHandler;
    private static HandleUsers hu;
    private static DatabaseUser databaseUser;


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ApplicationTest.launch(Main.class);
        System.out.println("here");
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());

        hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = new Cyclist("Tester");
        listDataHandler = new ListDataHandler(db, "test name");
        ListDataHandler.setListName("test list");

        databaseUser = new DatabaseUser(db);
        databaseUser.addUser("Tester", 1, 1, 2017, 1);

        RetailerDataHandlerFake handler = new RetailerDataHandlerFake(db);
        Task<Void> task = new CSVImporter(db, DataFilterer_Retailers_Test.class.getClassLoader().getResource("CSV/Lower_Manhattan_Retailers-test.csv").getFile(), handler);
        task.run();
    }

    @Test
    public void filterRetailersTestName_Pizza_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;

        retailLocations = dataFilterer.filterRetailers("pizza", null, null, -1, null);
        int size = retailLocations.size();
        assertTrue(size == 2);
    }


    @Test
    public void filterRetailersTestName__() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers("", null, null, -1, null);
        int size = retailLocations.size();
        assertTrue(size == 50);
    }


    @Test
    public void filterRetailersTestAddress_broad_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, "broad", null, -1, null);
        int size = retailLocations.size();
        assertTrue(size == 8);
    }


    @Test
    public void filterRetailersTestAddress_a_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, "a", null, -1, null);
        int size = retailLocations.size();
        assertTrue(size == 38);
    }


    @Test
    public void filterRetailersTestAddress_q_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, "q", null, -1, null);
        int size = retailLocations.size();
        assertTrue(size == 1);
    }


    @Test
    public void filterRetailersTestAddress__() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, "", null, -1, null);
        int size = retailLocations.size();
        assertTrue(size == 50);
    }


    @Test
    public void filterRetailersTestPrimary_Nightlife_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailName.add("The Iron Horse");
        retailName.add("The Hook & Ladder Bar");
        retailLocations = dataFilterer.filterRetailers(null, null, "Nightlife", -1, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestPrimary_Casual_Eating_Takeout_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, "Casual Eating & Takeout", -1, null);
        int size = retailLocations.size();
        assertTrue(size == 19);
    }

    @Test
    public void filterRetailersTestPrimary__() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailLocations = dataFilterer.filterRetailers(null, null, "", -1, null);
        int size = retailLocations.size();
        assertTrue(size == 50);
    }


    @Test
    public void filterRetailersTestZip_10005_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailName.add("Eastern Newsstands");
        retailName.add("Fed Ex Kinko's");
        retailName.add("Hale and Hearty Soup");
        retailName.add("Cobbler Express");
        retailName.add("Ise Japanese Restaurant");
        retailLocations = dataFilterer.filterRetailers(null, null, null, 10005, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestZip_10038_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, 10038, null);
        int size = retailLocations.size();
        assertTrue(size == 24);
    }


    @Test
    public void filterRetailersTestZip_99999999_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailLocations = dataFilterer.filterRetailers(null, null, null, 99999999, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestZip_0_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailLocations = dataFilterer.filterRetailers(null, null, null, 0, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestList_foo() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, -1, "foo");
        int size = retailLocations.size();
        assertTrue(size == 0);
    }


    @Test
    public void filterRetailersTestList_test_list() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, -1, "test list");
        int size = retailLocations.size();
        assertTrue(size == 50);
    }


    @Test
    public void filterRetailersTestList__() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers(null, null, null, -1, "");
        int size = retailLocations.size();
        assertTrue(size == 0);
    }


    @Test
    public void filterRetailersTestName_soup_Zip_10005_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailName.add("Hale and Hearty Soup");
        retailLocations = dataFilterer.filterRetailers("soup", null, null, 10005, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestName_cafe_Address_broad_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        retailLocations = dataFilterer.filterRetailers("cafe", "broad", null, -1, null);
        int size = retailLocations.size();
        assertTrue(size == 2);
    }


    @Test
    public void filterRetailersTestName_burrito_Primary_Causal_eating_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailName.add("Burritoville");
        retailLocations = dataFilterer.filterRetailers("burrito", null, "Casual Eating & Takeout", -1, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestAddress_broad_Primary_Causal_eating_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailName.add("Variety Cafe On Broadway");
        retailName.add("Bento Nouveau Sushi");
        retailName.add("McDonald's");
        retailName.add("Cafe Toda");
        retailLocations = dataFilterer.filterRetailers(null, "broad", "Casual Eating & Takeout", -1, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestAddress_b_Zip_10004_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailName.add("Trader's Cafe");
        retailName.add("Perfect Copy Center");
        retailName.add("New York Lottery");
        retailName.add("Picnick");
        retailName.add("Battery Gardens");
        retailLocations = dataFilterer.filterRetailers(null, "b", null, 10004, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }


    @Test
    public void filterRetailersTestPrimary_shopping_Zip_10007_() throws Exception {
        DataFilterer dataFilterer = new DataFilterer(db);
        ArrayList<RetailLocation> retailLocations;
        ArrayList<String> retailName = new ArrayList();
        retailName.add("Duane Reade");
        retailName.add("AT&T");
        retailLocations = dataFilterer.filterRetailers(null, null, "Shopping", 10007, null);
        int size = retailName.size();
        for (int i = 0; i < size; i++){
            assertTrue(retailName.get(i).equals(retailLocations.get(i).getName()));
        }
    }
}
