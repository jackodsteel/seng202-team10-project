package GUIControllers.ViewDataControllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import dataAnalysis.WifiLocation;
import dataHandler.SQLiteDB;
import dataManipulation.DataFilterer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.GREEN;

/**
 * Controller class for wifi data viewer.
 */

public class WifiDataViewerController extends DataViewerController {

    static private WifiLocation wifi = null;
    @FXML
    private JFXTextField nameInput;
    @FXML
    private JFXTextField providerInput;
    @FXML
    private TableView<WifiLocation> tableView;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private ComboBox<String> boroughInput;
    @FXML
    private ComboBox<String> typeInput;
    @FXML
    private ComboBox<String> wifiLists;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private TableColumn<WifiLocation, String> Name;
    @FXML
    private TableColumn<WifiLocation, String> Provider;
    @FXML
    private TableColumn<WifiLocation, String> Address;
    @FXML
    private TableColumn<WifiLocation, String> Suburb;
    @FXML
    private TableColumn<WifiLocation, String> Cost;
    private ObservableList<WifiLocation> wifiList = FXCollections.observableArrayList();

    static public WifiLocation getWifi() {
        return wifi;
    }

    /**
     * Runs on startup of the fxml. It gets the wifi information from the database and display it in the table.
     * A call of displayData is used to refresh the GUI.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {

        SQLiteDB db = Main.getDB();
        try {
            ResultSet rs = db.executeQuerySQL("SELECT list_name FROM lists");
            while (rs.next()) {
                wifiLists.getItems().add(rs.getString(1));
            }
            wifiLists.getItems().add("No Selection");
        } catch (SQLException e) {
            System.out.println(e);
        }

        try {
            initialiseEditListener();
        } catch (IOException e ) {
            //do nothing
        }

        Name.setCellValueFactory(new PropertyValueFactory<>("SSID"));
        Provider.setCellValueFactory(new PropertyValueFactory<>("Provider"));
        Address.setCellValueFactory(new PropertyValueFactory<>("Address"));
        Suburb.setCellValueFactory(new PropertyValueFactory<>("Suburb"));
        Cost.setCellValueFactory(new PropertyValueFactory<>("Cost"));
        tableView.setItems(wifiList);
        tableView.getColumns().setAll(Name, Provider, Address, Suburb, Cost);

        nameInputListener();
        providerInputListener();

        ActionEvent event = new ActionEvent();
        try {
            displayData(event);
        } catch (Exception e) {
            System.out.println("Initialising data has failed.");
        }
    }

    /**
     * Starts listener for double clicking an item in the table.
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    private void initialiseEditListener() throws IOException {
        tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    try {
                        ActionEvent ae = new ActionEvent(event.getSource(), null);
                        editData(ae);
                    } catch (IOException e) {
                        //do nothing
                    }

                }
            }
        });
    }

    /**
     * Called when the display all on map button is pressed. Changes the scene to the plan route one.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void displayDataOnMap(ActionEvent event) throws IOException {
        changeToPlanRouteScene(event, wifiList.toArray(new WifiLocation[wifiList.size()]), null, null);
    }

    /**
     * Called when the display selected button is pressed. If nothing is selected, it will prompt the user. Otherwise,
     * it will get the selected wifi location, and load the plan route scene with it in it.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void displaySelectedDataOnMap(ActionEvent event) throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeErrorDialogueBox("No Wifi Location selected.", "Please select one from the table.");
        } else {
            WifiLocation[] location = {tableView.getSelectionModel().getSelectedItem()};
            changeToPlanRouteScene(event, location, null, null);
        }
    }

    /**
     * Called when the filter button is pressed. It checks the user input and filters the data by those fields. It then
     * refreshes the table so it can be viewed visually.
     *
     * @param event Created when the method is called
     */
    @FXML
    public void displayData(ActionEvent event) {

        String name = nameInput.getText();
        if (name.equals("")) {
            name = null;
        }

        String provider = providerInput.getText();
        if (provider.equals("")) {
            provider = null;
        }

        String suburb = boroughInput.getSelectionModel().getSelectedItem();
        if (suburb == null || suburb.equals("No Selection")) {
            suburb = null;
        }

        String cost = typeInput.getSelectionModel().getSelectedItem();
        if (cost == null || cost.equals("No Selection")) {
            cost = null;
        }

        String list = wifiLists.getSelectionModel().getSelectedItem();
        if (list == null || list.equals("No Selection")) {
            list = null;
        }

        DataFilterer filterer = new DataFilterer(Main.getDB());


        tableView.getItems().clear();
        wifiList.addAll(filterer.filterWifi(name, suburb, cost, provider, list));
    }

    /**
     * Adds the wifi location to the users favourites list if it is not already in their favourites, otherwise it
     * creates an error dialogue box telling them it has already been added to their favourites.
     */
    @FXML
    private void addFavouriteWifi() {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeSuccessDialogueBox("Select which wifi hotspot to add.", "");
        } else {
            String name = Main.hu.currentCyclist.getName();
            WifiLocation wifiToAdd = tableView.getSelectionModel().getSelectedItem();
            boolean alreadyInList = Main.hu.currentCyclist.addFavouriteWifi(wifiToAdd, name, Main.getDB());
            if (!alreadyInList) {
                makeSuccessDialogueBox(wifiToAdd.getProvider() + " successfully added.", "");
            } else {
                makeErrorDialogueBox(wifiToAdd.getProvider() + " already in favourites", "This wifi location has already been " +
                        "added\nto this users favourites list.");
            }
        }
    }

    /**
     * Called when view/edit wifi location button is pressed. If nothing is selected, it will prompt the user, otherwise,
     * it will get the selection and launch the detailed view of it.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void editData(ActionEvent event) throws IOException {

        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeErrorDialogueBox("No Wifi Location selected.", "Please select one from the table.");
        } else {
            wifi = tableView.getSelectionModel().getSelectedItem();
            Stage popup = new Stage();
            popup.setTitle("Detailed Wifi Location View");
            popup.setResizable(false);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(((Node) event.getSource()).getScene().getWindow());
            Parent popupParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/DataViewerFXMLs/detailedWifiLocationInformation.fxml"));
            Scene popupScene = new Scene(popupParent);
            popup.setScene(popupScene);
            popup.show();
            DetailedWifiInformation.setMainAppEvent(event);
        }
    }

    /**
     * Listener for nameInput field. Uses a listener to see state of text. Sets focus colour when text is changed.
     */
    private void nameInputListener() {
        nameInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            nameInput.setFocusColor(GREEN);
            nameInput.setUnFocusColor(GREEN);
        }));
    }


    /**
     * Listener for providerInput field. Uses a listener to see state of text. Sets focus colour when text is changed.
     */
    private void providerInputListener() {
        providerInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            providerInput.setFocusColor(GREEN);
            providerInput.setUnFocusColor(GREEN);
        }));
    }
}
