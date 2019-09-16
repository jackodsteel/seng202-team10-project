package GUIControllers;

import com.jfoenix.controls.JFXTextField;
import dataHandler.SQLiteDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import main.HelperFunctions;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static java.lang.Character.isLetter;

/**
 * Handles the logging in scene of the GUI.
 */
public class StartUpController extends Controller implements Initializable {

    @FXML
    private JFXTextField username;

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private ComboBox<Integer> userday, usermonth, useryear;
    @FXML
    private ComboBox<String> usergender;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SQLiteDB db = Main.getDB();
        try {
            ResultSet rs = db.executeQuerySQL("SELECT * FROM users");
            while (rs.next()) {
                comboBox.getItems().add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        for (int day = 1; day <= 31; day++) {
            userday.getItems().add(day);
        }
        for (int mon = 1; mon <= 12; mon++) {
            usermonth.getItems().add(mon);
        }
        for (int year = 2019; year > 1900; year--) {
            useryear.getItems().add(year);
        }
    }


    /**
     * Creates new Cyclist instance with the given user name if it doesn't already exist in the database. Otherwise
     * it creates an error dialog box informing the use this name is already taken. Also throws error dialog boxes
     * for having no entries for a log in field or any incorrectly formatted dates.
     *
     * @param event clicking the sign up button
     * @throws IOException
     */
    @FXML
    public void createCyclist(ActionEvent event) throws IOException {

        String name = username.getText();
        boolean noNullEntries = true;
        if (name.equals("") || !isLetter(name.charAt(0))) {
            makeErrorDialogueBox("Enter a valid name", "Valid names must have at least one " +
                    "character\nand start with a letter.");
            noNullEntries = false;
        }
        if (usergender.getValue() == null) {
            makeErrorDialogueBox("Select a gender", "No gender was selected.");
            noNullEntries = false;
        }
        System.out.println(userday.getSelectionModel().getSelectedItem());
        if (userday.getSelectionModel().getSelectedItem() == null || usermonth.getSelectionModel().getSelectedItem() == null
                || useryear.getSelectionModel().getSelectedItem() == null) {
            makeErrorDialogueBox("Enter a valid birth date", "Use the format DD/MM/YYYY");
            noNullEntries = false;
        }
        if (noNullEntries) {
            String gender = usergender.getSelectionModel().getSelectedItem();
            int day = Integer.parseInt(userday.getSelectionModel().getSelectedItem().toString());
            int month = Integer.parseInt(usermonth.getSelectionModel().getSelectedItem().toString());
            int year = Integer.parseInt(useryear.getSelectionModel().getSelectedItem().toString());

            boolean dateError = HelperFunctions.checkDateDetails(day, month, year);

            if (dateError) {
                makeErrorDialogueBox("Enter a valid birth date", "Use the format DD/MM/YYYY");
            } else {
                boolean created = Main.hu.createNewUser(name, day, month, year, gender);
                if (created) {
                    changeToPlanRouteScene(event);
                } else {
                    makeErrorDialogueBox("Name already in use.", "");
                }
            }
        }
    }


    /**
     * Logs user from the existingUser text field in, and takes them to the home screen.
     *
     * @param event clicking the sign in button
     * @throws IOException
     */
    @FXML
    void logIn(ActionEvent event) throws IOException {
        if (comboBox.getValue() == null) {
            makeErrorDialogueBox("Select a valid user", "No user was selected. Please select " +
                    "an existing\nuser from the drop down box.");
        } else {
            String name = comboBox.getValue();
            Main.hu.logIn(name);
            changeToPlanRouteScene(event);
        }
    }


    /**
     * Displays the help screen.
     *
     * @param event clicking the help button
     * @throws IOException
     */
    @FXML
    private void openHelpStage(ActionEvent event) throws IOException {
        getHelp(event);
    }

}