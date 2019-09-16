package GUIControllers;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import dataHandler.DatabaseUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static dataObjects.Cyclist.*;


/**
 * Controller for the user information scene.
 */
public class ProfileController extends Controller implements Initializable {


    @FXML
    private ComboBox<String> gender;

    @FXML
    private DatePicker dob;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXHamburger hamburger;



    /**
     * Runs on loading the page. Sets the values of the user profile to what is currently saved in the database.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        name.setText(getName());
        dob.setValue(LocalDate.of(getBirthYear(), getBirthMonth(), getBirthDay()));
        System.out.println(getGender());
        if (getGender() == 1) {
            gender.getSelectionModel().select("Male");
        } else if (getGender() == 2) {
            gender.getSelectionModel().select("Female");
        } else {
            gender.getSelectionModel().select("Other");
        }
    }


    /**
     * Called when the update profile button is pressed. Gets the information currently in the GUI fields and sets the
     * current users information to them. Alerts user of success/failure with dialogue boxes.
     *
     * @param event Created on pressing the button. Not used.
     */
    @FXML
    void updateProfile(ActionEvent event) {
        try {
            LocalDate newDOB = dob.getValue();
            String newDOBString = newDOB.toString();
            int newYear = Integer.parseInt(newDOBString.split("-")[0]);
            int newMonth = Integer.parseInt(newDOBString.split("-")[1]);
            int newDay = Integer.parseInt(newDOBString.split("-")[2]);
            int newGender;
            setBirthday(newDay, newMonth, newYear);
            if (gender.getSelectionModel().getSelectedItem().equals("Male")) {
                setGender(1);
                newGender = 1;
            } else if (gender.getSelectionModel().getSelectedItem().equals("Female")) {
                setGender(2);
                newGender = 2;
            } else {
                setGender(0);
                newGender = 0;
            }
            String oldName = getName();
            String newName = name.getText();
            setName(newName);
            DatabaseUser d = new DatabaseUser(Main.getDB());
            d.updateDetails(newName, oldName, newDay, newMonth, newYear, newGender);
            makeSuccessDialogueBox("Success!", "Your profile has been successfully updated.");
        } catch (Exception e) {
            makeErrorDialogueBox("Failed", "An error occurred while updating your profile.");
        }
    }


    /**
     * Runs when the log out button is pressed. Changes the scene to the log in scene and signs out the current user.
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    public void logout(ActionEvent event) throws IOException {
        Main.hu.logOutOfUser();
        navigateToStartUp(event);
    }

    /**
     * Deletes the user completely from the database.
     * @param event
     * @throws IOException
     */
    public void deleteUser(ActionEvent event) throws IOException {
        if (makeConfirmationDialogueBox("Are you sure you want to delete this account?", "This cannot be undone.")) {
            Main.hu.logOutOfUser();
            DatabaseUser d = new DatabaseUser(Main.getDB());
            d.removeUserFromDatabase(Main.hu.currentCyclist.getName(), Main.hu);
            navigateToStartUp(event);
        }
    }

    /**
     * Navigates the user to the start up screen.
     * @param event
     * @throws IOException
     */
    private void navigateToStartUp(ActionEvent event) throws IOException {
        Parent logInParent = FXMLLoader.load(getClass().getResource("/FXML/startUp.fxml"));
        Scene logInScene = new Scene(logInParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(logInScene);
    }

}
