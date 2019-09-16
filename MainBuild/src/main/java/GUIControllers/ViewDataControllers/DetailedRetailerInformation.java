package GUIControllers.ViewDataControllers;


import com.jfoenix.controls.JFXTextField;
import dataHandler.ListDataHandler;
import dataHandler.SQLiteDB;
import dataManipulation.DeleteData;
import dataObjects.RetailLocation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

/**
 * Controller for the detailed retailer information fxml.
 */

public class DetailedRetailerInformation extends DataViewerController {

    static private ActionEvent mainAppEvent = null;
    @FXML
    private JFXTextField zip;
    @FXML
    private JFXTextField secondaryType;
    @FXML
    private Text address;
    @FXML
    private Text retailerName;
    @FXML
    private JFXTextField latitude;
    @FXML
    private JFXTextField city;
    @FXML
    private JFXTextField state;
    @FXML
    private ComboBox<String> mainType;
    @FXML
    private JFXTextField longitude;
    @FXML
    private Button delete;
    @FXML
    private ComboBox<String> list;
    @FXML
    private Button update;
    private RetailLocation currentRetailer = null;
    private ListDataHandler listDataHandler;
    private SQLiteDB db;

    static public void setMainAppEvent(ActionEvent event) {
        mainAppEvent = event;
    }


    /**
     * Called on start up and initialises the values in each of the editable and non-editable fields.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        db = Main.getDB();
        listDataHandler = new ListDataHandler(db, Main.hu.currentCyclist.getName());
        List<String> listNames = listDataHandler.getLists();
        list.getItems().addAll(listNames);

        currentRetailer = RetailerDataViewerController.getRetailer();
        retailerName.setText(currentRetailer.getName());
        address.setText(currentRetailer.getAddress());
        latitude.setText(Double.toString(currentRetailer.getLatitude()));
        longitude.setText(Double.toString(currentRetailer.getLongitude()));
        city.setText(currentRetailer.getCity());
        state.setText(currentRetailer.getState());
        zip.setText(Integer.toString(currentRetailer.getZip()));
        mainType.getSelectionModel().select(currentRetailer.getMainType());
        secondaryType.setText(currentRetailer.getSecondaryType());
        list.getEditor().setText(currentRetailer.getListName());
        latitudeListener();
        longitudeListener();
        cityListener();
        stateListener();
        zipListener();
        secondaryListener();
        listListener();

        list.getEditor().setOnMouseClicked(event -> {
            List<String> lists = listDataHandler.getLists();
            if (!lists.contains(currentRetailer.getListName()) && currentRetailer.getListName() != null) {
                makeErrorDialogueBox("Cannot edit List", "This Route is part of another users " +
                        "list and\ncannot be changed");
                list.setDisable(true);
            }
        });
    }

    /**
     * Called when the Update button is pressed. It gets the current value from all text fields and updates them in
     * the database. If any fail to update, an error message prompts the user to check the input.
     *
     * @param event Created when the method is called
     */
    @FXML
    void updateValues(ActionEvent event) {
        try {
            currentRetailer.setAddress(address.getText());
            currentRetailer.setLatitude(Double.parseDouble(latitude.getText()));
            currentRetailer.setLongitude(Double.parseDouble(longitude.getText()));
            currentRetailer.setCity(city.getText());
            currentRetailer.setState(state.getText());
            currentRetailer.setZip(Integer.parseInt(zip.getText()));
            currentRetailer.setMainType(mainType.getSelectionModel().getSelectedItem());
            currentRetailer.setSecondaryType(secondaryType.getText());
            if (list.getEditor().getText() != null) {
                listDataHandler.addList(list.getSelectionModel().getSelectedItem());
                currentRetailer.setListName(list.getEditor().getText());
            }
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            showRetailers(mainAppEvent);
        } catch (Exception exception) {
            System.out.println(exception);
            makeErrorDialogueBox("Cannot update data.", "One (or more) field(s) is of an incorrect type.");
        }
    }


    /**
     * Checks if list is owned by current user. If not creates an error popup and disables the list field
     *
     */
    @FXML
    void checkIfEditable() {
        List<String> lists = listDataHandler.getLists();
        if (!lists.contains(currentRetailer.getListName()) && currentRetailer.getListName() != null) {
            makeErrorDialogueBox("Cannot edit List", "This Route is part of another users " +
                    "list and\ncannot be changed");
            list.setDisable(true);
        }
    }


    /**
     * Error handler for latitude field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void latitudeListener() {
        latitude.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField lat Changed (newValue: " + newValue + ")");
            if (!latitude.getText().matches("-?[0-9]?[0-9]?[0-9].[0-9]+")) {
                latitude.setFocusColor(RED);
                latitude.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                latitude.setFocusColor(DARKSLATEBLUE);
                latitude.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for longitude field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void longitudeListener() {
        longitude.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField long Changed (newValue: " + newValue + ")");
            if (!longitude.getText().matches("-?[0-9]?[0-9]?[0-9].[0-9]+")) {
                longitude.setFocusColor(RED);
                longitude.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                longitude.setFocusColor(DARKSLATEBLUE);
                longitude.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for city field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void cityListener() {
        city.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField city Changed (newValue: " + newValue + ")");
            if (city.getText().length() > 50) {
                city.setFocusColor(RED);
                city.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                city.setFocusColor(DARKSLATEBLUE);
                city.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for state field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void stateListener() {
        state.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField state Changed (newValue: " + newValue + ")");
            if (state.getText().length() > 12) {
                state.setFocusColor(RED);
                state.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                state.setFocusColor(DARKSLATEBLUE);
                state.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for zip field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void zipListener() {
        zip.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField zip Changed (newValue: " + newValue + ")");
            if (!zip.getText().matches("[0-9]+") || zip.getText().length() > 8) {
                zip.setFocusColor(RED);
                zip.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                zip.setFocusColor(DARKSLATEBLUE);
                zip.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for secondaryType field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void secondaryListener() {
        secondaryType.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField secondary Changed (newValue: " + newValue + ")");
            if (secondaryType.getText().length() > 50) {
                secondaryType.setFocusColor(RED);
                secondaryType.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                secondaryType.setFocusColor(DARKSLATEBLUE);
                secondaryType.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for list field. Uses a listener to see state of text. Makes confirm button
     * un-selectable if text field incorrect. Stops strings over 25 char long from being entered.
     */
    private void listListener() {
        list.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField list Changed (newValue: " + newValue + ")");
            if (list.getEditor().getText().length() > 25) {
                String listName = list.getEditor().getText();
                listName = listName.substring(0, 25);
                list.getEditor().setText(listName);
            } else if (listDataHandler.checkListName(list.getEditor().getText())) {
                makeErrorDialogueBox("List name already exists", "This list name has already " +
                        "been used by another\nuser. Please choose a new name.\n");
                update.setDisable(true);
            } else {
                update.setDisable(false);
            }
        }));
    }

    /**
     * Called when the delete retailer button is pressed. Does a popup check as to whether the user is sure he/she/other
     * wants to delete the retailer and if so, removes it from the database.
     * @param event Created when the method is called
     */
    @FXML
    void deleteRetailer(ActionEvent event)  throws IOException{
        if (makeConfirmationDialogueBox("Are you sure you want to delete this retailer?", "This cannot be undone.")) {
            DeleteData deleteData = new DeleteData(db, Main.hu.currentCyclist.getName());
            int deleteStatus = deleteData.checkRetailDeletionStatus(currentRetailer.getName(),
                    currentRetailer.getAddress());
            if (deleteStatus == 1) {
                makeErrorDialogueBox("Failed to delete retailer", "Another user has this " +
                        "retail location in a list\nthey created.");
            } else if (deleteStatus == 2) {
                makeErrorDialogueBox("Failed to delete retailer", "Another user has this " +
                        "retail location in their\nfavourite retail list.");
            } else {
                System.out.println("OK to delete");
                deleteData.deleteRetailer(currentRetailer.getName(), currentRetailer.getAddress());
            }

            //Closes popup
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            showRetailers(mainAppEvent);
        }
    }

}
