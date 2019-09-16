package GUIControllers.ViewDataControllers;

import com.jfoenix.controls.JFXTextField;
import customExceptions.FilterByTimeException;
import dataHandler.SQLiteDB;
import dataManipulation.AddRouteCallback;
import dataManipulation.RouteFiltererTask;
import dataObjects.Route;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;


/**
 * Controller class for the route data viewer.
 */

public class RouteDataViewerController extends DataViewerController implements AddRouteCallback {

    static private Route route = null;


    @FXML
    private ToggleGroup genderGroup;

    @FXML
    private JFXTextField startLocationInput;

    @FXML
    private JFXTextField endLocationInput;

    @FXML
    private JFXTextField startTimeInput;

    @FXML
    private JFXTextField endTimeInput;

    @FXML
    private JFXTextField bikeIDInput;

    @FXML
    private TableView<Route> tableView;

    @FXML
    private TableColumn<Route, String> StartLocation;

    @FXML
    private TableColumn<Route, String> EndLocation;

    @FXML
    private TableColumn<Route, String> Date;

    @FXML
    private TableColumn<Route, String> StartTime;

    @FXML
    private TableColumn<Route, String> EndTime;

    @FXML
    private TableColumn<Route, String> Distance;

    @FXML
    private DatePicker startDateInput;

    @FXML
    private DatePicker endDateInput;

    @FXML
    private ComboBox<String> routeLists;

    @FXML
    private Label favouritesError;


    private ObservableList<Route> routeList = FXCollections.observableArrayList();

    private List<Route> routes = new ArrayList<>();

    static public Route getRoute() {
        return route;
    }


    private int getRank() {
        return 0;
    }

    /**
     * Runs on successfully loading fxml. Loads routes from database and displays them in the table.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        initialiseEditListener();
        SQLiteDB db = Main.getDB();
        try {
            ResultSet rs = db.executeQuerySQL("SELECT list_name FROM lists");
            while (rs.next()) {
                routeLists.getItems().add(rs.getString(1));
            }
            routeLists.getItems().add("No Selection");
        } catch (SQLException e) {
            e.printStackTrace();
        }



        StartLocation.setCellValueFactory(new PropertyValueFactory<>("StartAddress"));
        EndLocation.setCellValueFactory(new PropertyValueFactory<>("EndAddress"));
        Distance.setCellValueFactory(new PropertyValueFactory<>("Distance"));
        Date.setCellValueFactory(new PropertyValueFactory<>("StartDate"));
        StartTime.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        EndTime.setCellValueFactory(new PropertyValueFactory<>("StopTime"));
        tableView.setItems(routeList);

        startLocationInputListener();
        endLocationInputListener();
        endTimeInputListener();
        startTimeInputListener();
        bikeIDInputListener();

        ActionEvent event = new ActionEvent();
        try {
            displayData(event);
        } catch (Exception e) {
            System.out.println("Initialising data has failed.");
        }
    }

    /**
     * Starts listener for double clicking an item in the table.
     */
    private void initialiseEditListener() {
        tableView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                try {
                    ActionEvent ae = new ActionEvent(event.getSource(), null);
                    editData(ae);
                } catch (IOException e) {
                    //do nothing
                    System.out.println("Failed to load detailed route information scene");
                }

            } else {
                System.out.println("j");
            }
        });
    }

    /**
     * Called when filter button is pressed. Gets all input from text fields, checks it is valid, and let's user
     * know if it isn't. If all input is valid, it will filter the data and proceed to update the table with the new set
     * of data.
     *
     * @param event Created when the method is called
     */
    @FXML
    void displayData(ActionEvent event) {

        int gender = checkGenderInput();
        String[] dates = checkIfDateInputValid();
        String[] times = checkIfTimeInputValid();
        String startLocation = checkIfStartLocationInputValid();
        String endLocation = checkIfEndLocationInputValid();
        String bikeID = checkIfBikeIDInputValid();
        String list = checkListInput();


        if (dataViewTask != null)
            dataViewTask.cancel();

        routeList.clear();
        System.gc();
        dataViewTask = new RouteFiltererTask(Main.getDB(), gender, dates[0], dates[1], times[0], times[1], startLocation, endLocation,
                bikeID, list, this);
        Thread thread = new Thread(dataViewTask);
        thread.start();
    }


    /**
     * Checks if route rider gender input is valid.
     *
     * @return type int. The rider gender to filter by.
     */
    private int checkGenderInput() {
        System.out.println(genderGroup.getSelectedToggle());
        if (genderGroup.getSelectedToggle() == null) {
            return -1;
        } else {
            return Integer.parseInt(genderGroup.getSelectedToggle().getUserData().toString());
        }
    }


    /**
     * Checks if route start and end date input is valid.
     *
     * @return dates of type String[]. The start and end dates to filter by.
     */
    private String[] checkIfDateInputValid() {
        String dateLower;
        String dateUpper;
        String pattern = "dd/MM/yyyy";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate startDate = startDateInput.getValue();
        LocalDate endDate = endDateInput.getValue();
        if (startDate != null) {
            dateLower = startDate.format(dateFormatter);
        } else {
            dateLower = null;
        }
        if (endDate != null) {
            dateUpper = endDate.format(dateFormatter);
        } else {
            dateUpper = null;
        }
        if ((dateLower == null && dateUpper != null) || (dateLower != null && dateUpper == null)) {
            makeErrorDialogueBox("Missing date field", "Please enter both a lower and upper date" +
                    "\nto filter by.");
            dateLower = null;
            dateUpper = null;
        }
        String[] dates = {dateLower, dateUpper};
        return dates;
    }


    /**
     * Checks if route start and end time input is valid.
     *
     * @return times of type String[]. The start and end times to filter by.
     */
    private String[] checkIfTimeInputValid() {
        String timeLower = startTimeInput.getText() + ":00";
        String timeUpper = endTimeInput.getText() + ":00";
        try {
            if (":00".equals(timeLower)) {
                timeLower = null;
            } else {
                if (!timeLower.matches("([0-1][0-9]|2[0-4]):[0-5][0-9]:00")) {
                    throw new FilterByTimeException("Incorrect time format on start time");
                }
            }
            if (":00".equals(timeUpper)) {
                timeUpper = null;
            } else {
                if (!timeUpper.matches("([0-1][0-9]|2[0-4]):[0-5][0-9]:00")) {
                    throw new FilterByTimeException("Incorrect time format on end time");
                }
            }
            if (!(timeLower == null && timeUpper == null) && (timeLower == null || timeUpper == null)) {
                throw new FilterByTimeException("Missing time field");
            }
        } catch (FilterByTimeException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.equals("Missing time field")) {
                makeErrorDialogueBox("Missing time field", "Please enter both a lower and " +
                        "upper time limit\nto filter by.");
            } else {
                makeErrorDialogueBox(errorMessage, "Please use the format HH:SS.");
            }
            timeLower = null;
            timeUpper = null;
        }
        String[] times = {timeLower, timeUpper};
        return times;
    }


    /**
     * Checks if route start location name input is valid.
     *
     * @return startLocation of type String. The start location name to filter by.
     */
    private String checkIfStartLocationInputValid() {
        String startLocation = startLocationInput.getText();
        if ("".equals(startLocation)) {
            return null;
        }
        return startLocation;
    }


    /**
     * Checks if route end location name input is valid.
     *
     * @return endLocation of type String. The end location name to filter by.
     */
    private String checkIfEndLocationInputValid() {
        String endLocation = endLocationInput.getText();
        if ("".equals(endLocation)) {
            return null;
        }
        return endLocation;
    }


    /**
     * Checks if route bikeID input is valid.
     *
     * @return bikeID of type String. The bikeID to filter by.
     */
    private String checkIfBikeIDInputValid() {
        String bikeID = bikeIDInput.getText();
        if ("".equals(bikeID)) {
            return null;
        }
        return bikeID;
    }


    /**
     * Checks if route list name input is valid.
     *
     * @return list of type String. The list name to filter by.
     */
    private String checkListInput() {
        String list = routeLists.getSelectionModel().getSelectedItem();
        if (list == null || list.equals("No Selection")) {
            return null;
        }
        return list;
    }


    /**
     * Adds the route to the users favourites list if it is not already in their favourites, otherwise it
     * creates an error dialogue box telling them it has already been added to their favourites.
     */
    public void addFavouriteRoute(ActionEvent event) {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeSuccessDialogueBox("Select which route to add.", "");
        } else {
            String name = Main.hu.currentCyclist.getName();
            Route routeToAdd = tableView.getSelectionModel().getSelectedItem();
            boolean alreadyInList = Main.hu.currentCyclist.routeAlreadyInList(routeToAdd, "favourite_route");
            if (!alreadyInList) {
                openRouteRankStage(routeToAdd, name);
            } else {
                makeErrorDialogueBox("Route already in favourites", "This route has already been " +
                        "added\nto this users favourites list.");
            }
        }
    }

    /**
     * Adds the selected route to the completed routes.---------------------------------test
     */
    public void addTakenRoute(ActionEvent event) {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeSuccessDialogueBox("Select a route to add.", "");
        } else {
            String name = Main.hu.currentCyclist.getName();
            Route routeToAdd = tableView.getSelectionModel().getSelectedItem();
            boolean alreadyInList = Main.hu.currentCyclist.routeAlreadyInList(routeToAdd, "taken_route");
            if (!alreadyInList) {
                makeSuccessDialogueBox("Route successfully added.", "This route has been added to" +
                        " your completed routes.");
                Main.hu.currentCyclist.addTakenRoute(routeToAdd, name, Main.getDB(), Main.hu);
            } else {
                makeErrorDialogueBox("Route already in completed", "This route has already been " +
                        "added\nto this users completed list.");
            }
        }
    }


    /**
     * Called when view all on map button is pressed. Changes the scene to the plan route with the given list of data
     * ready to be loaded in.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    public void viewOnMap(ActionEvent event) throws IOException {
        //called when GUI button view on map button is pressed.
        changeToPlanRouteScene(event, null, null, routeList.toArray(new Route[0]));
    }

    /**
     * Called when view selected on map button is pressed. Checks that a route has been selected and if not, informs
     * the user. If so, the scene is changed to plan route and the chosen route is loaded in for view.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    public void viewSelectedOnMap(ActionEvent event) throws IOException {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeErrorDialogueBox("No route selected.", "Please select a route from the table.");
        } else {
            Route[] route = {tableView.getSelectionModel().getSelectedItem()};
            changeToPlanRouteScene(event, null, null, route);
        }
    }

    /**
     * Called when the edit/view all details button is pressed. Checks if a route is selected and informs the user if
     * it is not. If a route is selected, the detailed route viewer is opened, ready to load in the selected routes
     * information.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    public void editData(ActionEvent event) throws IOException {
        //called by GUI button View/edit route.

        if (tableView.getSelectionModel().getSelectedItem() == null) {
            makeErrorDialogueBox("No route selected.", "Please select a route from the table.");
        } else {
            route = tableView.getSelectionModel().getSelectedItem();
            Stage popup = new Stage();
            popup.setTitle("Detailed Route View");
            popup.setResizable(false);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.initOwner(((Node) event.getSource()).getScene().getWindow());
            Parent popupParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/DataViewerFXMLs/detailedRouteInformation.fxml"));
            Scene popupScene = new Scene(popupParent);
            popup.setScene(popupScene);
            popup.show();
            DetailedRouteInformation.setMainAppEvent(event);
        }
    }


    /**
     * Listener for startLocationInput field. Uses a listener to see state of text. Sets focus colour when text is
     * changed.
     */
    private void startLocationInputListener() {
        startLocationInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            startLocationInput.setFocusColor(GREEN);
            startLocationInput.setUnFocusColor(GREEN);
        }));
    }


    /**
     * Listener for endLocationInput field. Uses a listener to see state of text. Sets focus colour when text is
     * changed.
     */
    private void endLocationInputListener() {
        endLocationInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            endLocationInput.setFocusColor(GREEN);
            endLocationInput.setUnFocusColor(GREEN);
        }));
    }


    /**
     * Error handler for endTimeInput field. Uses a listener to see state of text. Sets focus color if text field
     * incorrect.
     */
    private void endTimeInputListener() {
        endTimeInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endTimeInput.getText().matches("([0-1][0-9]|2[0-4]):[0-5][0-9]|^$")) {
                endTimeInput.setFocusColor(RED);
                endTimeInput.setUnFocusColor(RED);
            } else {
                endTimeInput.setFocusColor(GREEN);
                endTimeInput.setUnFocusColor(GREEN);
            }
        }));
    }


    /**
     * Error handler for startTimeInput field. Uses a listener to see state of text. Sets focus color if text field
     * incorrect.
     */
    private void startTimeInputListener() {
        startTimeInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!startTimeInput.getText().matches("([0-1][0-9]|2[0-4]):[0-5][0-9]|^$")) {
                startTimeInput.setFocusColor(RED);
                startTimeInput.setUnFocusColor(RED);
            } else {
                startTimeInput.setFocusColor(GREEN);
                startTimeInput.setUnFocusColor(GREEN);
            }
        }));
    }


    /**
     * Listener for bikeIDInputListener field. Uses a listener to see state of text. Sets focus colour when text is
     * changed.
     */
    private void bikeIDInputListener() {
        bikeIDInput.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            bikeIDInput.setFocusColor(GREEN);
            bikeIDInput.setUnFocusColor(GREEN);
        }));
    }


    @Override
    public void addRoute(Route route) {
        routeList.add(route);
    }

    @Override
    public void addRoutes(List<Route> routes) {
        routeList.addAll(routes);
    }
}
