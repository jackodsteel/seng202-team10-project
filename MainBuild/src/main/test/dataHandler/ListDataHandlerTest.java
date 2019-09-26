package dataHandler;

import dataObjects.Cyclist;
import main.HandleUsers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.util.List;

import static main.Main.hu;
import static org.junit.Assert.*;

/**
 * tests for ListDataHandler
 */
public class ListDataHandlerTest {
    private static ListDataHandler listDataHandler;
    private static SQLiteDB db;

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        Files.delete(path);
    }


    @BeforeClass
    public static void setUpBeforeClass() {
        System.out.println("here");
        String home = System.getProperty("user.home");
        java.nio.file.Path path = java.nio.file.Paths.get(home, "testdatabase.db");
        db = new SQLiteDB(path.toString());

        hu = new HandleUsers();
        hu.init(db);
        hu.currentCyclist = new Cyclist("Tester");
        String userName = hu.currentCyclist.getName();
        listDataHandler = new ListDataHandler(db, "test name");
        ListDataHandler.setListName("test list");

        DatabaseUser databaseUser = new DatabaseUser(db);
        databaseUser.addUser("Tester", 1, 1, 2017, 1);

    }

    @Test
    public void getListName_setListName() {
        String string1 = "test list 1";
        String string2 = "test list 2";
        ListDataHandler.setListName(string1);
        String list1 = ListDataHandler.getListName();
        ListDataHandler.setListName(string2);
        String list2 = ListDataHandler.getListName();
        assertEquals(list1, string1);
        assertEquals(list2, string2);
    }


    @Test
    public void addList_null() throws Exception {
        listDataHandler.addList(null);
        int result = db.executeQuerySQL("SELECT count(*) FROM lists WHERE list_name = null;").getInt(1);
        assertEquals(0, result);
    }


    @Test
    public void addList__() throws Exception {
        listDataHandler.addList("");
        int result = db.executeQuerySQL("SELECT count(*) FROM lists WHERE list_name = '';").getInt(1);
        assertEquals(0, result);
    }


    @Test
    public void addList_test_list_() throws Exception {
        listDataHandler.addList("test list");
        int result = db.executeQuerySQL("SELECT count(*) FROM lists WHERE list_name = 'test list';").getInt("count(*)");
        assertEquals(1, result);
    }


    @Test
    public void checkListName_created_by_current_user() {
        listDataHandler.addList("check list name");
        boolean result = listDataHandler.checkListName("check list name");
        assertFalse(result);
    }


    @Test
    public void checkListName_created_by_other_user() {
        ListDataHandler listDataHandler2 = new ListDataHandler(db, "Tester 2");
        listDataHandler2.addList("test list 2");
        boolean result = listDataHandler.checkListName("test list 2");
        assertTrue(result);
    }


    @Test
    public void getLists() {
        List<String> lists = listDataHandler.getLists();
        assertTrue(lists.contains("test list"));
    }
}