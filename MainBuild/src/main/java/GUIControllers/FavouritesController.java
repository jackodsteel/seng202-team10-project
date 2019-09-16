package GUIControllers;


import dataHandler.FavouriteRetailData;
import dataHandler.FavouriteRouteData;
import dataHandler.FavouriteWifiData;
import dataObjects.RetailLocation;
import dataObjects.Route;
import dataObjects.WifiLocation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static main.Main.hu;

/**
 * Controller class for home.
 */

public class FavouritesController extends Controller implements Initializable {

    @FXML
    private GridPane gridPane;

    @FXML
    private TableColumn<Route, String> FavRoutes;

    @FXML
    private TableColumn<Route, String> StartAddress;

    @FXML
    private TableColumn<Route, String> Rating;

    @FXML
    private TableColumn<WifiLocation, String> FavWifi;

    @FXML
    private TableColumn<WifiLocation, String> SSID;

    @FXML
    private TableColumn<WifiLocation, String> WifiAddress;

    @FXML
    private TableColumn<RetailLocation, String> FavRetailers;

    @FXML
    private TableColumn<RetailLocation, String> RetailerName;

    @FXML
    private TableColumn<RetailLocation, String> RetailerAddress;

    @FXML
    private TableView<Route> tableViewRoutes;

    @FXML
    private TableView<WifiLocation> tableViewWifi;

    @FXML
    private TableView<RetailLocation> tableViewRetailers;


    private ObservableList<Route> routeList = FXCollections.observableArrayList();

    private ObservableList<WifiLocation> wifiList = FXCollections.observableArrayList();

    private ObservableList<RetailLocation> retailerList = FXCollections.observableArrayList();

    /**
     * Runs on successfully loading the fxml. Fills the favourites tables.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    public void initialize(URL location, ResourceBundle resources) {

        routeList.addAll(hu.currentCyclist.getFavouriteRouteList());
        wifiList.addAll(hu.currentCyclist.getFavouriteWifiLocations());
        retailerList.addAll(hu.currentCyclist.getFavouriteRetailLocations());

        StartAddress.setCellValueFactory(new PropertyValueFactory<>("StartAddress"));
        Rating.setCellValueFactory(new PropertyValueFactory<>("Rank"));
        tableViewRoutes.setItems(routeList);
        tableViewRoutes.getColumns().setAll(FavRoutes);

        SSID.setCellValueFactory(new PropertyValueFactory<>("SSID"));
        WifiAddress.setCellValueFactory(new PropertyValueFactory<>("Address"));
        tableViewWifi.setItems(wifiList);
        tableViewWifi.getColumns().setAll(FavWifi);

        RetailerName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        RetailerAddress.setCellValueFactory(new PropertyValueFactory<>("Address"));
        tableViewRetailers.setItems(retailerList);
        tableViewRetailers.getColumns().setAll(FavRetailers);

        tableViewRoutesSelectionListener();
        tableViewWifiSelectionListener();
        tableViewRetailerSelectionListener();
    }


    /**
     * Called when View Favourites on Map on map button is pressed. Changes the scene to the plan route with all of the
     * favourites data ready to be loaded in.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void showFavourites(ActionEvent event) throws IOException {
        //called when GUI button view on map button is pressed.
        changeToPlanRouteScene(event, wifiList.toArray(new WifiLocation[wifiList.size()]), retailerList.toArray(new RetailLocation[retailerList.size()]), routeList.toArray(new Route[routeList.size()]));
    }


    /**
     * Deletes the favourite selected from the chosen table.
     *
     * @param event Created when the method is called
     */
    @FXML
    public void deleteFavourite(ActionEvent event) {
        if (tableViewRoutes.getSelectionModel().getSelectedItem() != null) {
            FavouriteRouteData frd = new FavouriteRouteData(Main.getDB());
            frd.deleteFavouriteRoute(tableViewRoutes.getSelectionModel().getSelectedItem(), hu);
            Main.hu.currentCyclist.getFavouriteRouteList().remove(tableViewRoutes.getSelectionModel().getSelectedItem());
            routeList.remove(tableViewRoutes.getSelectionModel().getSelectedItem());

        } else if (tableViewWifi.getSelectionModel().getSelectedItem() != null) {
            FavouriteWifiData fwd = new FavouriteWifiData(Main.getDB());
            fwd.deleteFavouriteWifi(tableViewWifi.getSelectionModel().getSelectedItem(), hu);
            Main.hu.currentCyclist.getFavouriteWifiLocations().remove(tableViewWifi.getSelectionModel().getSelectedItem());
            wifiList.remove(tableViewWifi.getSelectionModel().getSelectedItem());

        } else if (tableViewRetailers.getSelectionModel().getSelectedItem() != null) {
            FavouriteRetailData frd = new FavouriteRetailData(Main.getDB());
            frd.deleteFavouriteRetail(tableViewRetailers.getSelectionModel().getSelectedItem(), hu);
            Main.hu.currentCyclist.getFavouriteRetailLocations().remove(tableViewRetailers.getSelectionModel().getSelectedItem());
            retailerList.remove(tableViewRetailers.getSelectionModel().getSelectedItem());


        } else {
            makeErrorDialogueBox("No favourite selected", "No route was selected to delete." +
                    " You must\nchoose which favourite you want to delete.");
        }
    }


    /**
     * tableViewRoutesSelectionListener will deselect the selected cell in tableViewRoutes if the mouse is clicked
     * anywhere else.
     */
    private void tableViewRoutesSelectionListener() {
        ObjectProperty<TableRow<Route>> lastSelectedRow = new SimpleObjectProperty<>();
        tableViewRoutes.setRowFactory(tableView -> {
            TableRow<Route> row = new TableRow<>();

            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    lastSelectedRow.set(row);
                }
            });
            return row;
        });

        GridPane stage = gridPane;
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (lastSelectedRow.get() != null) {
                Bounds boundsOfSelectedRow = lastSelectedRow.get().localToScene(lastSelectedRow.get().getLayoutBounds());
                if (!boundsOfSelectedRow.contains(event.getSceneX(), event.getSceneY())) {
                    tableViewRoutes.getSelectionModel().clearSelection();
                }
            }
        });
    }


    /**
     * tableViewRoutesSelectionListener will deselect the selected cell in tableViewWifi if the mouse is clicked
     * anywhere else.
     */
    private void tableViewWifiSelectionListener() {
        ObjectProperty<TableRow<WifiLocation>> lastSelectedRow = new SimpleObjectProperty<>();
        tableViewWifi.setRowFactory(tableView -> {
            TableRow<WifiLocation> row = new TableRow<>();

            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    lastSelectedRow.set(row);
                }
            });
            return row;
        });

        GridPane stage = gridPane;
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (lastSelectedRow.get() != null) {
                Bounds boundsOfSelectedRow = lastSelectedRow.get().localToScene(lastSelectedRow.get().getLayoutBounds());
                if (!boundsOfSelectedRow.contains(event.getSceneX(), event.getSceneY())) {
                    tableViewWifi.getSelectionModel().clearSelection();
                }
            }
        });
    }


    /**
     * tableViewRoutesSelectionListener will deselect the selected cell in tableViewRetailers if the mouse is clicked
     * anywhere else.
     */
    private void tableViewRetailerSelectionListener() {
        ObjectProperty<TableRow<RetailLocation>> lastSelectedRow = new SimpleObjectProperty<>();
        tableViewRetailers.setRowFactory(tableView -> {
            TableRow<RetailLocation> row = new TableRow<>();

            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    lastSelectedRow.set(row);
                }
            });
            return row;
        });

        GridPane stage = gridPane;
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (lastSelectedRow.get() != null) {
                Bounds boundsOfSelectedRow = lastSelectedRow.get().localToScene(lastSelectedRow.get().getLayoutBounds());
                if (!boundsOfSelectedRow.contains(event.getSceneX(), event.getSceneY())) {
                    tableViewRetailers.getSelectionModel().clearSelection();
                }
            }
        });
    }

}
