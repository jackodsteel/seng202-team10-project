package GUIControllers;

import com.google.maps.errors.ApiException;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import dataAnalysis.RetailLocation;
import dataAnalysis.Route;
import dataAnalysis.WifiLocation;
import dataHandler.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.HelperFunctions;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static dataAnalysis.Cyclist.name;
import static java.lang.Integer.parseInt;


/**
 * Controller class for the add data scene.
 */
public class AddDataController extends Controller implements Initializable {

    @FXML //This importButton reveals other buttons
    private Button importRoute, importRetailer, importWifi;
    @FXML // Route Errors
    private Text sTimeError, sDateError, eTimeError, eDateError;
    @FXML //Route Fields
    private JFXTextField rSAddress, rEAddress, rSTime, rETime;
    @FXML // Retailer Fields
    private JFXTextField retailerName, retailerAddress, retailerSec;
    @FXML
    private ComboBox retailerPrim;
    @FXML
    private DatePicker rSDate, rEDate;
    @FXML // Wifi Fields
    private JFXTextField wifiName, wifiAddress, wifiComments;
    @FXML
    private Text selectMessage;
    @FXML
    private Text selectListMessage;
    @FXML
    private ComboBox<String> listInput, manualListInput;
    @FXML
    private JFXCheckBox addToFavourites, addToCompletedRoutes;

    private SQLiteDB db;
    private RetailerDataHandler retailerDataHandler;
    private WifiDataHandler wifiDataHandler;
    private RouteDataHandler routeDataHandler;
    private ListDataHandler listDataHandler;
    public String startAddress = "";
    public String endAddress = "";


    /**
     * Runs on successfully loading the fxml. This initialises the data handlers so that CSV's can be imported in
     * the future.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(location);
        db = Main.getDB();
        retailerDataHandler = new RetailerDataHandler(db);
        wifiDataHandler = new WifiDataHandler(db);
        routeDataHandler = new RouteDataHandler(db);
        listDataHandler = new ListDataHandler(db, Main.hu.currentCyclist.getName());

        initListComboboxes();
    }


    /**
     * populates the list combobox.
     */
    public void initListComboboxes() {
        ArrayList<String> listNames = listDataHandler.getLists();
        listInput.getItems().clear();
        listInput.getItems().addAll(listNames);
        manualListInput.getItems().clear();
        manualListInput.getItems().addAll(listNames);
    }

    /**
     * Sets values of address from the PlanRoute scene.
     *
     * @param startAddress Starting address for route.
     * @param endAddress End address for route.
     */
    public void setRouteVals(String startAddress, String endAddress) {
        rSAddress.setText(startAddress);
        rEAddress.setText(endAddress);
    }

    /**
     * Collects, formats and checks for valid data input by user
     * to create a valid entry into the route section of the local database.
     * Contains calls to convertDates and routeDataHandler.
     *
     * @param event Clicking the Add to Database button on the manual entry page.
     * @throws IOException
     */
    @FXML
    void routeCSVLine(ActionEvent event) throws IOException {
        double SLatitude, SLongitude, ELatitude, ELongitude;
        boolean errorOccurred = false;
        String sTime = "00:00:00";
        String eTime = "00:00:00";
        String[] sDate = new String[3];
        String[] eDate = new String[3];

        double[] sLatLon;
        double[] eLatLon;

        if (listDataHandler.checkListName(manualListInput.getSelectionModel().getSelectedItem())) {
            makeErrorDialogueBox("List name already exists", "This list name has been used by " +
                    "another user,\nplease choose another name");
        } else {
            ListDataHandler.setListName(manualListInput.getSelectionModel().getSelectedItem());
            listDataHandler.addList(manualListInput.getSelectionModel().getSelectedItem());
            initListComboboxes();

            try {
                if (rSAddress.getText().length() != 0 || rEAddress.getText().length() != 0) {
                    sLatLon = Geocoder.addressToLatLon(rSAddress.getText());
                    eLatLon = Geocoder.addressToLatLon(rEAddress.getText());
                } else {
                    makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
                    return;
                }
            } catch (ApiException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("API Error");
                alert.setHeaderText("Google API Error");
                alert.setContentText("The application could not get the lat and lon because there was an issue with the API");
                alert.showAndWait();
                return;
            } catch (IOException | InterruptedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("IO Error");
                alert.setHeaderText("Input Error");
                alert.setContentText("The application could not convert the address as it could not connect to the internet");
                alert.showAndWait();
                return;
            }
            SLatitude = sLatLon[0];
            SLongitude = sLatLon[1];
            ELatitude = eLatLon[0];
            ELongitude = eLatLon[1];

            String username;
            try {
                username = Main.hu.currentCyclist.getName();
            } catch (Exception e) {
                username = "_";
            }

            try { //Start Date
                sDateError.setVisible(false);
                sDate = HelperFunctions.convertDates(rSDate.getValue().toString());
                if (sDate == null) {
                    errorOccurred = true;
                    sDateError.setVisible(true);
                }
            } catch (Exception e) {
                sDateError.setVisible(true);
                errorOccurred = true;
            }

            try { // End Date
                eDateError.setVisible(false);
                eDate = HelperFunctions.convertDates(rEDate.getValue().toString());
                if (eDate == null) {
                    errorOccurred = true;
                    //eDateError.setVisible(true);
                }
            } catch (Exception e) {
                eDateError.setVisible(true);
                errorOccurred = true;
            }

            if (HelperFunctions.checkTime(rSTime.getText())) {
                sTime = rSTime.getText() + ":00";
                sTimeError.setVisible(false);
            } else {
                errorOccurred = true;
                sTimeError.setVisible(true);
            }

            if (HelperFunctions.checkTime(rETime.getText())) {
                eTime = rETime.getText() + ":00";
                eTimeError.setVisible(false);
            } else {
                eTimeError.setVisible(true);
                errorOccurred = true;
            }
            if (errorOccurred) {
                makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
                return;
            }

            int duration = HelperFunctions.getDuration(sDate[0], sDate[1], sDate[2], sTime,
                    eDate[0], eDate[1], eDate[2], eTime);
            RouteDataHandler newRoute = new RouteDataHandler(Main.getDB());

            Boolean fromHandler = newRoute.addSingleEntry(duration, sDate[0], sDate[1], sDate[2], sTime,
                    eDate[0], eDate[1], eDate[2], eTime, null,
                    rSAddress.getText(), SLatitude, SLongitude, null, rEAddress.getText(), ELatitude, ELongitude,
                    username, "Custom", Main.hu.currentCyclist.getBYear(), Main.hu.currentCyclist.getGender());
            //get distance

            if (!fromHandler) {
                makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
            } else {
                Route routeToAdd = new Route(duration, sTime, eTime, sDate[2], sDate[1], sDate[0],
                        eDate[2], eDate[1], eDate[0], SLatitude, SLongitude,
                        ELatitude, ELongitude, 0, 1, rSAddress.getText(),
                        rEAddress.getText(), username, Main.hu.currentCyclist.getGender(), "Custom", Main.hu.currentCyclist.getBYear(),
                        ListDataHandler.getListName());

                if (addToFavourites.isSelected() && addToCompletedRoutes.isSelected()) {
                    openRouteRankStage(routeToAdd, Main.hu.currentCyclist.getName());
                    Main.hu.currentCyclist.routeAlreadyInList(routeToAdd, "taken_route");
                    Main.hu.currentCyclist.addTakenRoute(routeToAdd, name, Main.getDB(), Main.hu);
                    makeSuccessDialogueBox("Successfully added this route to the database and favourite + completed routes", "You may add more entries");

                } else if (addToFavourites.isSelected()) {
                    openRouteRankStage(routeToAdd, Main.hu.currentCyclist.getName());
                    makeSuccessDialogueBox("Successfully added this route to the database and your favourite routes", "You may add more entries");

                } else if (addToCompletedRoutes.isSelected()) {
                    Main.hu.currentCyclist.routeAlreadyInList(routeToAdd, "taken_route");
                    Main.hu.currentCyclist.addTakenRoute(routeToAdd, name, Main.getDB(), Main.hu);
                    makeSuccessDialogueBox("Successfully added this route to the database and your completed routes", "You may add more entries");
                } else {
                    makeSuccessDialogueBox("Successfully added this route to the database", "You may add more entries");
                }
            }
        }
    }

    /**
     * Collects, formats and checks for valid data input by user
     * to create a valid entry into the retailer section of the local database.
     * Contains calls to convertDates and retailerDataHandler.
     *
     * @param event Clicking the Add to Database button on the retailer entry page.
     * @throws IOException
     */
    @FXML
    void retailerCSVLine(ActionEvent event) throws IOException {
        Boolean errorOccured = false;
        double[] latLon;

        if (listDataHandler.checkListName(manualListInput.getSelectionModel().getSelectedItem())) {
            makeErrorDialogueBox("List name already exists", "This list name has been used by " +
                    "another user,\nplease choose another name");
        } else {
            ListDataHandler.setListName(manualListInput.getSelectionModel().getSelectedItem());
            listDataHandler.addList(manualListInput.getSelectionModel().getSelectedItem());
            initListComboboxes();

            try {
                if (retailerAddress.getText().length() != 0) {
                    latLon = Geocoder.addressToLatLon(retailerAddress.getText());
                } else {
                    makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
                    return;
                }
            } catch (ApiException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("API Error");
                alert.setHeaderText("Google API Error");
                alert.setContentText("The application could not get the lat and lon because there was an issue with the API");
                alert.showAndWait();
                return;
            } catch (IOException | InterruptedException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("IO Error");
                alert.setHeaderText("Input Error");
                alert.setContentText("The application could not convert the address as it could not connect to the internet");
                alert.showAndWait();
                return;
            }

            if (retailerAddress.getText() == null) {
                errorOccured = true;
            }
            if (retailerName.getText() == null) {
                errorOccured = true;
            }
            if (retailerPrim.getSelectionModel().isEmpty()) {
                errorOccured = true;
            }
            if (retailerSec.getText() == null) {
                errorOccured = true;
            }
            if (!errorOccured) {
                RetailerDataHandler newRetailer = new RetailerDataHandler(Main.getDB());
                Boolean fromHandler = newRetailer.addSingleEntry(retailerName.getText(), retailerAddress.getText(), latLon[0], latLon[1], null,
                        null, null, retailerPrim.getValue().toString(), retailerSec.getText());
                if (!fromHandler) {
                    makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
                } else {
                    if (addToFavourites.isSelected()) {
                        RetailLocation retailToAdd = new RetailLocation(retailerName.getText(), retailerAddress.getText(), "",
                                retailerPrim.getSelectionModel().getSelectedItem().toString(), retailerSec.getText(), "",
                                0, latLon[0], latLon[1], ListDataHandler.getListName());
                        Main.hu.currentCyclist.addFavouriteRetail(retailToAdd, name, Main.getDB());
                        makeSuccessDialogueBox("Successfully added to this retailer to the database and your favourites list.", "You may add more entries");
                    } else {
                        makeSuccessDialogueBox("Successfully added to this retailer to the database", "You may add more entries");
                    }
                }
            } else {
                makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
            }
        }
    }

    /**
     * Collects, formats and checks for valid data input by user
     * to create a valid entry into the wifi section of the local database.
     * Contains calls to convertDates and wifiDataHandler.
     *
     * @param event Clicking the Add to Database button on the wifi entry page.
     * @throws IOException
     */
    @FXML
    void wifiCSVLine(ActionEvent event) throws IOException {
        Boolean errorOccured = false;
        double[] latLon;

        if (listDataHandler.checkListName(manualListInput.getSelectionModel().getSelectedItem())) {
            makeErrorDialogueBox("List name already exists", "This list name has been used by " +
                    "another user,\nplease choose another name");
        } else {
            ListDataHandler.setListName(manualListInput.getSelectionModel().getSelectedItem());
            listDataHandler.addList(manualListInput.getSelectionModel().getSelectedItem());
            initListComboboxes();

            try {
                if (wifiAddress.getText().length() != 0) {
                    latLon = Geocoder.addressToLatLon(wifiAddress.getText());
                } else {
                    makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
                    return;
                }
            } catch (ApiException e) {
                makeErrorDialogueBox("Google API Error", "The application could not get the lat and lon\nThere was an issue with the API");
                return;
            } catch (IOException | InterruptedException e) {
                makeErrorDialogueBox("Input Error", "The application could not convert the address \nIt could not connect to the internet");
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                makeErrorDialogueBox("Input Error", "Invalid address.");
                return;

            }

            if (wifiName.getText() == null) {
                errorOccured = true;
            }
            if (wifiAddress.getText() == null) {
                errorOccured = true;
            }

            if (!errorOccured) {

                WifiDataHandler newWifi = new WifiDataHandler(Main.getDB());
                Boolean fromHandler = newWifi.addSingleEntry(wifiName.getText(), "", "", wifiAddress.getText(), latLon[0], latLon[1],
                        wifiComments.getText(), "", wifiName.getText(), "", null);
                if (!fromHandler) {
                    makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
                } else {
                    if (addToFavourites.isSelected()) {
                        WifiLocation wifiToAdd = new WifiLocation(wifiName.getText(), latLon[0], latLon[1], wifiAddress.getText(),
                                wifiName.getText(), "", "", wifiComments.getText(), "", "",
                                0, ListDataHandler.getListName());
                        Main.hu.currentCyclist.addFavouriteWifi(wifiToAdd, name, Main.getDB());
                        makeSuccessDialogueBox("Successfully added this entry to the database and your favourite Wifi list.", "You may add more entries");
                    } else {
                        makeSuccessDialogueBox("Successfully added this entry to the database", "You may add more entries");
                    }
                }
            } else {
                makeErrorDialogueBox("Something wrong with input", "Fill all required fields\nCheck entry is not already in database");
            }
        }

    }


    /**
     * Opens system file viewer and accepts only *.csv type files,
     * will print the file string to system and send it to the
     * routeDataHandler.
     *
     * @param event Clicking the Routes button after Import File.
     * @throws IOException
     */
    @FXML
    // Import file -> only allows *.csv and prints location afterwards for now...
    void chooseRoute(ActionEvent event) throws IOException {
        String currentList;
        currentList = listInput.getSelectionModel().getSelectedItem();

        if (listDataHandler.checkListName(currentList)) {
            makeErrorDialogueBox("List name already exists", "This list name has been used by " +
                    "another user,\nplease choose another name");
        } else {

            boolean result = true;
            System.out.println(currentList);
            if (currentList == null) {
                result = makeConfirmationDialogueBox("You haven't selected a list to import your data into.",
                        "This will import the data into a general list.\nAre you sure you want to continue?");
            }

            if (result) {

                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
                System.out.println(file);
                if (file == null) {
                    return;
                }

                ListDataHandler.setListName(currentList);
                listDataHandler.addList(currentList);
                initListComboboxes();

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/progressPopup.fxml"));
                Parent progressParent = loader.load();
                ProgressPopupController controller = loader.getController();
                Scene progressScene = new Scene(progressParent);
                Stage dialogStage = new Stage();
                dialogStage.setResizable(false);
                System.out.println(dialogStage.isResizable());
                dialogStage.setTitle("Import Status");
                dialogStage.setMaxHeight(240);
                dialogStage.setMaxWidth(400);
                dialogStage.setMinHeight(240);
                dialogStage.setMinWidth(400);
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(progressScene);
                dialogStage.setResizable(false);
                dialogStage.show();

                RouteDataHandler handler = new RouteDataHandler(db);
                Task<Void> task = new CSVImporter(db, file.toString(), handler);
                controller.activateProgressBar(task);
                Thread thread = new Thread(task);
                thread.start();
                dialogStage.setOnCloseRequest(e -> task.cancel());
            }
        }
    }


    /**
     * Opens system file viewer and accepts only *.csv type files,
     * will print the file string to system and send it to the
     * retailerDataHandler.
     *
     * @param event Clicking the Retailers button after Import File.
     * @throws IOException
     */
    @FXML
    //Specifies file types.
    void chooseRetailer(ActionEvent event) throws IOException {

        if (listDataHandler.checkListName(listInput.getSelectionModel().getSelectedItem())) {
            makeErrorDialogueBox("List name already exists", "This list name has been used by " +
                    "another user,\nplease choose another name");
        } else {
            String currentList = listInput.getSelectionModel().getSelectedItem();

            boolean result = true;
            System.out.println(currentList);
            if (currentList == null) {
                result = makeConfirmationDialogueBox("You haven't selected a list to import your data into.",
                        "This will import the data into a general list.\nAre you sure you want to continue?");
            }

            if (result) {

                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
                System.out.println(file);
                if (file == null) {
                    return;
                }

                ListDataHandler.setListName(currentList);
                listDataHandler.addList(currentList);
                initListComboboxes();

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/progressPopup.fxml"));
                Parent progressParent = loader.load();
                ProgressPopupController controller = loader.getController();
                Scene progressScene = new Scene(progressParent);
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Import Status");
                dialogStage.setMaxHeight(240);
                dialogStage.setMaxWidth(400);
                dialogStage.setMinHeight(240);
                dialogStage.setMinWidth(400);
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(progressScene);
                dialogStage.show();

                RetailerDataHandler handler = new RetailerDataHandler(db);
                Task<Void> task = new CSVImporter(db, file.toString(), handler);
                controller.activateProgressBar(task);
                Thread thread = new Thread(task);
                thread.start();

                dialogStage.setOnCloseRequest(e -> task.cancel());
            }
        }
    }

    /**
     * Opens system file viewer and accepts only *.csv type files,
     * will print the file string to system and send it to the
     * wifiDataHandler.
     *
     * @param event Clicking the Wifi button after Import File.
     * @throws IOException
     */
    @FXML
    void chooseWifi(ActionEvent event) throws IOException {

        String currentList = listInput.getSelectionModel().getSelectedItem();

        if (listDataHandler.checkListName(currentList)) {
            makeErrorDialogueBox("List name already exists", "This list name has been used by " +
                    "another user,\nplease choose another name");
        } else {

            boolean result = true;
            System.out.println(currentList);
            if (currentList == null) {
                result = makeConfirmationDialogueBox("You haven't selected a list to import your data into.",
                        "This will import the data into a general list.\nAre you sure you want to continue?");
            }

            if (result) {

                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
                System.out.println(file);
                if (file == null) {
                    return;
                }

                ListDataHandler.setListName(currentList);
                listDataHandler.addList(currentList);
                initListComboboxes();

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/progressPopup.fxml"));
                Parent progressParent = loader.load();
                ProgressPopupController controller = loader.getController();
                Scene progressScene = new Scene(progressParent);
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Import Status");
                dialogStage.initStyle(StageStyle.UTILITY);
                dialogStage.setMaxHeight(240);
                dialogStage.setMaxWidth(400);
                dialogStage.setMinHeight(240);
                dialogStage.setMinWidth(400);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(progressScene);
                dialogStage.show();

                WifiDataHandler handler = new WifiDataHandler(db);
                Task<Void> task = new CSVImporter(db, file.toString(), handler);
                controller.activateProgressBar(task);
                Thread thread = new Thread(task);
                thread.start();
                dialogStage.setOnCloseRequest(e -> task.cancel());
            }
        }
    }

    /**
     * Changes the current scene to the Route entry scene.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void changeToRouteEntryScene(ActionEvent event) throws IOException {
        Parent manualEntryParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/routeManualEntry.fxml"));
        Scene manualEntryScene = new Scene(manualEntryParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(manualEntryScene);
    }

    /**
     * Changes the current scene to the wifi entry scene.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void changeToWifiEntryScene(ActionEvent event) throws IOException {
        Parent manualEntryParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/wifiManualEntry.fxml"));
        Scene manualEntryScene = new Scene(manualEntryParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(manualEntryScene);
    }

    /**
     * Changes the current scene to the Retailer entry scene
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void changeToRetailerEntryScene(ActionEvent event) throws IOException {
        Parent manualEntryParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/retailerManualEntry.fxml"));
        Scene manualEntryScene = new Scene(manualEntryParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(manualEntryScene);
    }
}