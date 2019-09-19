package GUIControllers.ViewDataControllers;

import com.jfoenix.controls.JFXTextField;
import dataHandler.ListDataHandler;
import dataHandler.SQLiteDB;
import dataManipulation.DeleteData;
import dataObjects.WifiLocation;
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
 * Controller class for detailed wifi information.
 */
public class DetailedWifiInformation extends DataViewerController {

    static private ActionEvent mainAppEvent = null;
    @FXML
    private JFXTextField Zip;
    @FXML
    private JFXTextField address;
    @FXML
    private ComboBox<String> cost;
    @FXML
    private JFXTextField provider;
    @FXML
    private JFXTextField city;
    @FXML
    private Text wifiID;
    @FXML
    private JFXTextField longitude;
    @FXML
    private ComboBox<String> suburb;
    @FXML
    private Button delete;
    @FXML
    private JFXTextField latitude;
    @FXML
    private JFXTextField SSID;
    @FXML
    private JFXTextField remarks;
    @FXML
    private ComboBox<String> list;
    @FXML
    private Button update;
    private WifiLocation currentWifi = null;
    private ListDataHandler listDataHandler;
    private SQLiteDB db;

    static public void setMainAppEvent(ActionEvent event) {
        mainAppEvent = event;
    }

    /**
     * Fills each textfield to what is currently stored in the database.
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

        currentWifi = WifiDataViewerController.getWifi();
        wifiID.setText(currentWifi.getWifiID());
        address.setText(currentWifi.getAddress());
        provider.setText(currentWifi.getProvider());
        cost.getSelectionModel().select(currentWifi.getCost());
        latitude.setText(Double.toString(currentWifi.getLatitude()));
        longitude.setText(Double.toString(currentWifi.getLongitude()));
        remarks.setText(currentWifi.getRemarks());
        city.setText(currentWifi.getCity());
        SSID.setText(currentWifi.getSSID());
        suburb.getSelectionModel().select(currentWifi.getSuburb());
        Zip.setText(Integer.toString(currentWifi.getZip()));
        list.getEditor().setText(currentWifi.getListName());
        addressListener();
        providerListener();
        latitudeListener();
        longitudeListener();
        remarksListener();
        cityListener();
        SSIDListener();
        zipListener();
        listListener();

        list.getEditor().setOnMouseClicked(event -> {
            List<String> lists = listDataHandler.getLists();
            if (!lists.contains(currentWifi.getListName()) && currentWifi.getListName() != null) {
                makeErrorDialogueBox("Cannot edit List", "This Route is part of another users " +
                        "list and\ncannot be changed");
                list.setDisable(true);
            }
        });
    }


    /**
     * Checks each text field and updates the database to what the user has inputted. If any update fails, the user
     * is informed and expected to update their input.
     *
     * @param event Created when the method is called
     */
    @FXML
    void updateValues(ActionEvent event) {
        try {
            currentWifi.setAddress(address.getText());
            currentWifi.setProvider(provider.getText());
            currentWifi.setCost(cost.getSelectionModel().getSelectedItem());
            currentWifi.setLatitude(Double.parseDouble(latitude.getText()));
            currentWifi.setLongitude(Double.parseDouble(longitude.getText()));
            currentWifi.setRemarks(remarks.getText());
            currentWifi.setCity(city.getText());
            currentWifi.setSSID(SSID.getText());
            currentWifi.setSuburb(suburb.getSelectionModel().getSelectedItem());
            currentWifi.setZip(Integer.parseInt(Zip.getText()));
            if (list.getEditor().getText() != null) {
                listDataHandler.addList(list.getSelectionModel().getSelectedItem());
                currentWifi.setListName(list.getEditor().getText());
            }

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            showWifiLocations(mainAppEvent);
        } catch (Exception exception) {
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
        if (!lists.contains(currentWifi.getListName()) && currentWifi.getListName() != null) {
            makeErrorDialogueBox("Cannot edit List", "This Route is part of another users " +
                    "list and\ncannot be changed");
            list.setDisable(true);
        }
    }


    /**
     * Error handler for address field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void addressListener() {
        address.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (address.getText().length() > 50) {
                address.setFocusColor(RED);
                address.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                address.setFocusColor(DARKSLATEBLUE);
                address.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for provider field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void providerListener() {
        provider.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (provider.getText().length() > 50) {
                provider.setFocusColor(RED);
                provider.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                provider.setFocusColor(DARKSLATEBLUE);
                provider.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for latitude field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void latitudeListener() {
        latitude.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
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
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
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
     * Error handler for remarks field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void remarksListener() {
        remarks.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (remarks.getText().length() > 50) {
                remarks.setFocusColor(RED);
                remarks.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                remarks.setFocusColor(DARKSLATEBLUE);
                remarks.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for city field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void cityListener() {
        city.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
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
     * Error handler for SSID field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void SSIDListener() {
        SSID.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (SSID.getText().length() > 50) {
                SSID.setFocusColor(RED);
                SSID.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                SSID.setFocusColor(DARKSLATEBLUE);
                SSID.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for zip field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void zipListener() {
        Zip.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!Zip.getText().matches("[0-9]+") || Zip.getText().length() > 8) {
                Zip.setFocusColor(RED);
                Zip.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                Zip.setFocusColor(DARKSLATEBLUE);
                Zip.setUnFocusColor(GREEN);
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
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
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
     * Called when the delete wifi location button is pressed. Does a popup check as to whether the user is sure he/she/other
     * wants to delete the wifi location and if so, removes it from the database.
     * @param event Created when the method is called
     */
    @FXML
    void deleteWifi(ActionEvent event) throws IOException{
        if (makeConfirmationDialogueBox("Are you sure you want to delete this Wifi Location?", "This cannot be undone.")) {
            DeleteData deleteData = new DeleteData(db, Main.hu.currentCyclist.getName());
            DeleteData.DeletionStatus deleteStatus = deleteData.checkWifiDeletionStatus(currentWifi.getWifiID());
            switch (deleteStatus) {
                case IN_ANOTHER_USERS_LIST:
                    makeErrorDialogueBox("Failed to delete wifi location", "Another user has this " +
                            "wifi location in a list\nthey created.");
                    break;
                case IN_ANOTHER_USERS_FAVOURITES:
                    makeErrorDialogueBox("Failed to delete wifi location", "Another user has this " +
                            "wifi location in their\nfavourite wifi list.");
                    break;
                case CAN_BE_DELETED:
                    System.out.println("OK to delete");
                    deleteData.deleteWifiLocation(currentWifi.getWifiID());
                    break;
            }

            //Closes popup
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            showWifiLocations(mainAppEvent);
        }
    }

}