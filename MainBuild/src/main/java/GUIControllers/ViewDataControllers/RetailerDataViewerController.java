package GUIControllers.ViewDataControllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import dataHandler.SQLiteDB;
import dataManipulation.DataFilterer;
import dataObjects.RetailLocation;
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
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

/**
 * Controller class for retailer data viewer.
 */
public class RetailerDataViewerController extends DataViewerController {

    static private RetailLocation retailer = null;
    @FXML
    private JFXTextField streetInput;
    @FXML
    private ComboBox<String> primaryInput;
    @FXML
    private ComboBox<String> retailerLists;
    @FXML
    private TableView<RetailLocation> tableView;
    @FXML
    private JFXTextField nameInput;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXTextField zipInput;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private TableColumn<RetailLocation, String> Name;
    @FXML
    private TableColumn<RetailLocation, String> Address;
    @FXML
    private TableColumn<RetailLocation, Integer> Zip;
    @FXML
    private TableColumn<RetailLocation, String> PrimaryType;
    private ObservableList<RetailLocation> retailList = FXCollections.observableArrayList();

    static public RetailLocation getRetailer() {
        return retailer;
    }


    /**
     * Runs on successfully loading up and fills the table with data currently stored in the database.
     * It then calls displayData() to visually display these.
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
                retailerLists.getItems().add(rs.getString(1));
            }
            retailerLists.getItems().add("No Selection");
        } catch (SQLException e) {
            System.out.println(e);
        }

        try {
            initialiseEditListener();
        } catch (IOException e ) {
            //do nothing
        }

        Name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        Address.setCellValueFactory(new PropertyValueFactory<>("Address"));
        Zip.setCellValueFactory(new PropertyValueFactory<>("Zip"));
        PrimaryType.setCellValueFactory(new PropertyValueFactory<>("MainType"));
        tableView.setItems(retailList);
        tableView.getColumns().setAll(Name, Address, Zip, PrimaryType);

        nameInputListener();
        streetInputListener();
        zipInputListener();

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
     * Called when the display all on map button is pressed. It changes the scene to the plan route one so that all data
     * can be seen on the map.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void displayDataOnMap(ActionEvent event) throws IOException {
        //Called when GUI button View on map is pressed.
        changeToPlanRouteScene(event, null, retailList.toArray(new RetailLocation[retailList.size()]), null);
    }

    /**
     * Called when the display selected on map button is pressed. If nothing is selected, it will prompt the user,
     * otherwise it will get the selected item, place it on the map, and change the scene so it is visible.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void displaySelectedDataOnMap(ActionEvent event) throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeErrorDialogueBox("No retailer selected.", "Please select a retailer from the table.");
        } else {
            RetailLocation[] location = {tableView.getSelectionModel().getSelectedItem()};
            changeToPlanRouteScene(event, null, location, null);
        }
    }

    /**
     * Called when the filter button is pressed. It gets what the user has inputted into the filter text fields,
     * filters the data, and calls for a refresh of the scene so it can be seen.
     *
     * @param event Created when the method is called
     **/
    @FXML
    void displayData(ActionEvent event) {

        String name = checkIfNameInputValid();
        String address = checkIfAddressInputValid();
        int zip = checkIfZipInputValid();
        String primaryType = checkPrimaryTypeInput();
        String list = checkListInput();

        DataFilterer filterer = new DataFilterer(Main.getDB());
        tableView.getItems().clear();
        retailList.addAll(filterer.filterRetailers(name, address, primaryType, zip, list));
    }


    /**
     * Checks if retailer name input is valid.
     *
     * @return name of type String. The retailer name to filter by.
     */
    private String checkIfNameInputValid() {
        String name = nameInput.getText();
        if (name.equals("")) {
            return null;
        }
        return name;
    }


    /**
     * Checks if retailer address input is valid.
     *
     * @return address of type String. The retailer address to filter by.
     */
    private String checkIfAddressInputValid() {
        String address = streetInput.getText();
        if (address.equals("")) {
            return null;
        }
        return address;
    }


    /**
     * Checks if retailer zip input is valid.
     *
     * @return zip of type int. The retailer zip to filter by.
     */
    private int checkIfZipInputValid() {
        int zip;
        try {
            if (zipInput.getText().equals("")) {
                return -1;
            } else {
                if (Integer.valueOf(zipInput.getText()) <= 0) {
                    throw new NumberFormatException();
                }
                if (Integer.valueOf(zipInput.getText()) >= 100000000) {
                    throw new NumberFormatException();
                }
                zip = Integer.valueOf(zipInput.getText());
            }
        } catch (NumberFormatException e) {
            makeErrorDialogueBox("Incorrect input for zip number", "Please enter a positive number between 1 and 8\ndigits long.");
            return -1;
        }
        return zip;
    }


    /**
     * Checks if retailer primary type input is valid.
     *
     * @return primaryType of type String. The retailer primary type to filter by.
     */
    private String checkPrimaryTypeInput() {
        String primaryType = primaryInput.getSelectionModel().getSelectedItem();
        if (primaryType == null || primaryType.equals("No Selection")) {
            return null;
        }
        return primaryType;
    }


    /**
     * Checks if retailer list input is valid.
     *
     * @return list of type String. The retailer list to filter by.
     */
    private String checkListInput() {
        String list = retailerLists.getSelectionModel().getSelectedItem();
        if (list == null || list.equals("No Selection")) {
            return null;
        }
        return list;
    }


    /**
     * Adds the retail store to the users favourites list if it is not already in their favourites, otherwise it
     * creates an error dialogue box telling them it has already been added to their favourites.
     */
    @FXML
    private void addFavouriteRetail() {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeSuccessDialogueBox("Select which retail store to add.", "");
        } else {
            String name = Main.hu.currentCyclist.getName();
            RetailLocation retailToAdd = tableView.getSelectionModel().getSelectedItem();
            boolean alreadyInList = Main.hu.currentCyclist.addFavouriteRetail(retailToAdd, name, Main.getDB());
            if (!alreadyInList) {
                makeSuccessDialogueBox(retailToAdd.getName() + " successfully added.", "");
            } else {
                makeErrorDialogueBox(retailToAdd.getName() + " already in favourites", "This retail store has already been " +
                        "added\nto this users favourites list.");
            }
        }
    }


    /**
     * Called when view/edit retailer is pressed. If nothing is selected, it will prompt the user. Otherwise,
     * it will open open the detailed view stage.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    void editData(ActionEvent event) throws IOException {

        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeErrorDialogueBox("No retailer selected.", "Please select a retailer from the table.");
        } else {
            retailer = tableView.getSelectionModel().getSelectedItem();
            Stage popup = new Stage();
            popup.setTitle("Detailed Retailer View");
            popup.setResizable(false);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(((Node) event.getSource()).getScene().getWindow());
            Parent popupParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/DataViewerFXMLs/detailedRetailerInformation.fxml"));
            Scene popupScene = new Scene(popupParent);
            popup.setScene(popupScene);
            popup.show();
            DetailedRetailerInformation.setMainAppEvent(event);
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
     * Listener for streetInput field. Uses a listener to see state of text. Sets focus colour when text is changed.
     */
    private void streetInputListener() {
        streetInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            streetInput.setFocusColor(GREEN);
            streetInput.setUnFocusColor(GREEN);
        }));
    }


    /**
     * Error handler for zipInput field. Uses a listener to see state of text. Sets focus color if text field incorrect.
     */
    private void zipInputListener() {
        zipInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!zipInput.getText().matches("[0-9]*|^$") || zipInput.getText().length() > 8) {
                zipInput.setFocusColor(RED);
                zipInput.setUnFocusColor(RED);
            } else {
                zipInput.setFocusColor(GREEN);
                zipInput.setUnFocusColor(GREEN);
            }
        }));
    }
}
