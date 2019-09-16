package GUIControllers;

import com.jfoenix.controls.JFXHamburger;
import dataHandler.TakenRoutes;
import dataObjects.Route;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import main.HandleUsers;
import main.HelperFunctions;
import main.Main;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * Controller for the user information scene.
 */
public class CompletedRoutesController extends Controller implements Initializable {

    @FXML
    private Text longestRoute;

    @FXML
    private Text averageRoute;

    @FXML
    private Text shortestRoute;

    @FXML
    private Text totalDistance;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private Text errorText;

    @FXML
    private BorderPane errorBorder;

    @FXML
    private BarChart<String, Number> graph;

    @FXML
    private TableView<Route> tableCompletedRoutes;

    @FXML
    private TableColumn<Route, String> startLocation;

    @FXML
    private TableColumn<Route, String> endLocation;

    @FXML
    private TableColumn<Route, String> distance;

    @FXML
    private TableColumn<Route, String> completedRoutes;

    private ArrayList<Route> routeList = new ArrayList<>();
    private ObservableList<Route> routeListObservable = FXCollections.observableArrayList();



    /**
     * Runs on start up. Sets the values of the user profile to what is currently saved in the database.
     * Also, calculates the User statistics and displays them for the user.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        findStatistics(Main.hu);
        loadGraph();

        //Initialise routes completed table.
        routeListObservable.addAll(Main.hu.currentCyclist.getTakenRoutes());
        startLocation.setCellValueFactory(new PropertyValueFactory<>("StartAddress"));
        endLocation.setCellValueFactory(new PropertyValueFactory<>("EndAddress"));
        distance.setCellValueFactory(new PropertyValueFactory<>("Distance"));
        tableCompletedRoutes.setItems(routeListObservable);
        tableCompletedRoutes.getColumns().setAll(completedRoutes);
    }

    /**
     * Finds all of the most recent statistics for the user and displays them.
     */
    private void findStatistics(HandleUsers hu) {
        double total = HelperFunctions.calculateDistanceCycled(hu.currentCyclist);
        double average = HelperFunctions.cacluateAverageDistance(hu.currentCyclist, hu);

        double shortest = HelperFunctions.calculateShortestRoute(hu.currentCyclist);
        if (shortest == 9999999) {
            shortest = 0;
        }
        double longest = HelperFunctions.calculateLongestRoute(hu.currentCyclist);
        if (longest == -1) {
            longest = 0;
        }

        totalDistance.setText(total + " km");
        averageRoute.setText(average + " km");
        shortestRoute.setText(shortest + " km");
        longestRoute.setText(longest + " km");
    }

    /**
     * Refreshes the graph, loads its data, and displays it on the graph. If less than 3 routes are completed,
     * then the graph will not display, instead showing a message informing the user that they need to add more routes
     * to the completed routes.
     *
     */
    private void loadGraph() {
        //initialises the graph
        errorText.setVisible(false);
        errorBorder.setVisible(false);
        graph.setOpacity(1);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        graph.setTitle("           Most Recent Route Distances\n");


        TakenRoutes t = new TakenRoutes(Main.getDB());
        ArrayList<String> recentRoutes = t.findFiveRecentRoutes(Main.hu);
        XYChart.Series<String,Number> series1 = new XYChart.Series();

        if (recentRoutes.size() >= 3) {
            //Adds the data
            String currentRoute;
            String currentDate = "";
            Double currentDistance;
            String[] currentData;

            for (int count = 0; count < recentRoutes.size(); count++) {
                currentRoute = recentRoutes.get(count);
                currentData = currentRoute.split("\\|");
                if (count == 0) {
                    currentDate = "Most Recent";
                } else if (count == 1) {
                    currentDate = "2nd";
                } else if (count == 2) {
                    currentDate = "3rd";
                } else if (count == 3) {
                    currentDate = "4th";
                } else if (count == 4) {
                    currentDate = "5th";
                }
                currentDistance = Double.parseDouble(currentData[1]);
                series1.getData().add(new XYChart.Data(currentDate, currentDistance));
            }
        } else {
            //Does not add the data as there is not enough routes in completed routes.
            graph.setOpacity(0.3);
            errorBorder.setVisible(true);
            errorText.setVisible(true);
        }

        graph.getData().clear();
        graph.layout();
        graph.getData().addAll(series1);

        //Sets the colour
        series1.getData().forEach(d->
                d.getNode().setStyle("-fx-bar-fill: navy;"));

        graph.setLegendVisible(false);
    }


    /**
     * Deletes the selected route from the users taken routes.
     */
    @FXML
    private void deleteTakenRoute() {
        if (tableCompletedRoutes.getSelectionModel().getSelectedItem() != null) {
            TakenRoutes t = new TakenRoutes(Main.getDB());
            Route removingRoute = tableCompletedRoutes.getSelectionModel().getSelectedItem();
            t.deleteTakenRoute(removingRoute, Main.hu);
            routeList.remove(removingRoute);
            routeListObservable.remove(removingRoute);
            Main.hu.currentCyclist.getTakenRoutes().remove(removingRoute);
            findStatistics(Main.hu);
            loadGraph();
        } else {
            makeErrorDialogueBox("No route selected", "No route was selected to delete." +
                    " You must\nchoose which route you want to delete.");

        }
    }

}
