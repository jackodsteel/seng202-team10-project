package GUIControllers;

import com.jfoenix.controls.JFXTextField;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import dataManipulation.FindNearbyLocations;
import dataObjects.Location;
import dataObjects.RetailLocation;
import dataObjects.Route;
import dataObjects.WifiLocation;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.util.Duration;
import main.HelperFunctions;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Controller for the plan route scene.
 */

public class MapController extends Controller implements Initializable, MapComponentInitializedListener, DirectionsServiceCallback {

    @FXML
    protected JFXTextField startAddressField;
    @FXML
    protected JFXTextField endAddressField;
    @FXML
    protected GoogleMapView mapView;
    protected DirectionsService directionsService;
    protected DirectionsPane directionsPane;
    protected DirectionsRenderer directionsRenderer;
    @FXML
    private Button nearbyWifiButton;
    @FXML
    private Button nearbyRetailerButton;

    private GoogleMap map;
    private StringProperty startAddress = new SimpleStringProperty();
    private StringProperty endAddress = new SimpleStringProperty();
    private List<Marker> wifiMarkers = new ArrayList<>();
    private List<Marker> retailerMarkers = new ArrayList<>();
    private List<Marker> tripMarkers = new ArrayList<>();
    private List<Polyline> tripLines = new ArrayList<>();
    private DecimalFormat numberFormat = new DecimalFormat("0.00");
    private FindNearbyLocations nearbyFinder;
    private LatLong currentPoint;
    private InfoWindow currentInfoWindow;
    private String currentStart;
    private String currentEnd;
    private boolean hasResulted;

    private HashSet<WifiLocation> wifiLocations = new HashSet<>();
    private HashSet<RetailLocation> retailLocations = new HashSet<>();
    private HashSet<Route> routes = new HashSet<>();

    /**
     * Called automatically when the map is loaded. Loads the map.
     */
    @Override
    public void mapInitialized() {
        System.out.println("Init");
        GeocodingService geocodingService = new GeocodingService();
        MapOptions mapOptions = new MapOptions();

        currentInfoWindow = new InfoWindow();

        double STARTLON = -73.994039;
        double STARTLAT = 40.745968;
        mapOptions.center(new LatLong(STARTLAT, STARTLON))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .mapTypeControl(false)
                .zoomControl(true)
                .zoom(12);

        map = mapView.createMap(mapOptions);
        directionsService = new DirectionsService();

        directionsPane = mapView.getDirec();
        directionsRenderer = new DirectionsRenderer(true, mapView.getMap(), directionsPane);
        directionsRenderer.setOptions("true, suppressBicyclingLayer: true");


        renderWifiMarkers();
        renderRetailerMarkers();
        renderTripMarkers();
    }

    /**
     * Called when the fxml is loaded. Initialises the start and end address text fields so they cna be used to
     * create routes.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init");
        mapView.addMapInializedListener(this);
        startAddress.bindBidirectional(startAddressField.textProperty());
        endAddress.bindBidirectional(endAddressField.textProperty());
        nearbyFinder = new FindNearbyLocations(Main.getDB());
    }


    /**
     * Called when enter key is pressed from inside the address textfields or the search button is pressed. Requests a
     * route and loads it on the map.
     *
     * @param event Created when the method is called
     */
    @FXML
    public void addressTextFieldAction(ActionEvent event) {
        hasResulted = false;
        String start = startAddress.get().replace("'", "\'");
        String end = endAddress.get().replace("'", "\'");
        DirectionsRequest request = new DirectionsRequest(start, end, TravelModes.BICYCLING);
        directionsService.getRoute(request, this, directionsRenderer);
        currentStart = startAddress.get();
        currentEnd = endAddress.get();

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> {
            if (!hasResulted) {
                makeDirectionsError();
            }
        });
        delay.play();

    }

    private void makeDirectionsError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Could not get directions");
        alert.setContentText("Either there was an error with one of your\naddresses, or there was a problem with\nthe connection.");

        alert.show();
    }

    /**
     * Called when a route is created. It gets the start and end location latitude and longitude to find the mid point
     * of the route. This can then be used to find nearby retailers and wifi locations.
     *
     * @param results The directions the route takes
     * @param status  default constructor when collecting route directions
     */
    @Override
    public void directionsReceived(DirectionsResult results, DirectionStatus status) {
        hasResulted = true;
        System.out.println("Recvd");
        nearbyRetailerButton.setDisable(false);
        nearbyWifiButton.setDisable(false);
        System.out.println(results.getRoutes().size());
        DirectionsLeg leg = results.getRoutes().get(0).getLegs().get(0);
        double midLat = (leg.getStartLocation().getLatitude() + leg.getEndLocation().getLatitude()) / 2;
        double midLon = (leg.getStartLocation().getLongitude() + leg.getEndLocation().getLongitude()) / 2;
        LatLong mid = new LatLong(midLat, midLon);
        System.out.println(mid);
        currentPoint = mid;
    }

    private void renderWifiMarker(WifiLocation location) {
        String infoWindowHtmlContent =
                new StringJoiner("<br>")
                        .add("SSID: " + location.getSSID())
                        .add("Provider: " + location.getProvider())
                        .add("Address: " + location.getAddress())
                        .add("Extra Info: " + location.getRemarks())
                        .toString();
        String iconUrl = "https://i.imgur.com/4WWeG4S.png"; // TODO: fix this broken URL
        renderMarker(location, wifiMarkers, iconUrl, infoWindowHtmlContent);
    }

    /**
     * Renders the CurrentStates wifiLocations HashSet on the map.
     * Initially removes all current wifiMarkers, then renders each one from the HashSet
     */
    private void renderWifiMarkers() {
        wifiMarkers.forEach(marker -> map.removeMarker(marker));
        wifiMarkers.clear();
        wifiLocations.forEach(this::renderWifiMarker);
    }

    private void renderRetailerMarker(RetailLocation location) {
        String infoWindowHtmlContent =
                new StringJoiner("<br>")
                        .add("Name: " + location.getName())
                        .add("Address: " + location.getAddress())
                        .add("Category: " + location.getMainType())
                        .add("Extra Info: " + location.getSecondaryType())
                        .toString();
        String iconUrl = "https://i.imgur.com/nPCouZN.png"; // TODO: fix this broken URL
        renderMarker(location, retailerMarkers, iconUrl, infoWindowHtmlContent);
    }

    private void renderMarker(LatLong latLong, String name, List<Marker> markers, String iconUrl, String infoWindowHtmlContent) {
        Marker marker = new Marker(
                new MarkerOptions()
                        .position(latLong)
                        .title(name)
                        .visible(true)
                        .icon(iconUrl)
        );
        markers.add(marker);
        map.addMarker(marker);
        map.addUIEventHandler(marker, UIEventType.click, jsObject -> {
            nearbyRetailerButton.setDisable(false);
            nearbyWifiButton.setDisable(false);
            InfoWindowOptions infoWindowOptions = new InfoWindowOptions()
                    .content(infoWindowHtmlContent);
            currentInfoWindow.close();
            currentInfoWindow = new InfoWindow(infoWindowOptions);
            currentInfoWindow.open(map, marker);
            currentPoint = latLong;
        });
    }

    private void renderMarker(Location location, List<Marker> markers, String iconUrl, String infoWindowHtmlContent) {
        LatLong latLong = new LatLong(location.getLatitude(), location.getLongitude());
        renderMarker(latLong, location.getName(), markers, iconUrl, infoWindowHtmlContent);
    }

    /**
     * Renders the CurrentStates retailerMarkers HashSet on the map.
     * Initially removes all current retailerMarkers, then renders each one from the HashSet
     */
    private void renderRetailerMarkers() {
        retailerMarkers.forEach(marker -> map.removeMarker(marker));
        retailerMarkers.clear();
        retailLocations.forEach(this::renderRetailerMarker);
    }

    /**
     * Renders the CurrentStates routes HashSet on the map.
     * Initially removes all current route markers, and route polylines, then for each route, adds a start and end marker, and a polyline between them.
     * If there is only one trip, render it singularly as a direction based route, otherwise render the set with polylines
     */
    private void renderTripMarkers() {
        for (Marker marker : tripMarkers) {
            map.removeMarker(marker);
        }
        for (Polyline line : tripLines) {
            map.removeMapShape(line);
        }
        tripMarkers.clear();
        tripLines.clear();

        System.out.println(routes.size());

        if (routes.size() == 1) {
            System.out.println(routes.size());
            Route route = routes.iterator().next();
            System.out.println(route);
            LatLong start = new LatLong(route.getStartLatitude(), route.getStartLongitude());
            LatLong end = new LatLong(route.getEndLatitude(), route.getEndLongitude());
            System.out.println(start);
            System.out.println(route.getStartAddress());
            currentStart = route.getStartAddress();
            System.out.println(currentStart);
            currentEnd = route.getEndAddress();
            DirectionsRequest request = new DirectionsRequest(route.getStartAddress(), route.getEndAddress(), TravelModes.BICYCLING);
            directionsService.getRoute(request, this, directionsRenderer);
        } else {

            for (Route route : routes) {
                LatLong startLatLong = new LatLong(route.getStartLatitude(), route.getStartLongitude());
                String startInfoWindowHtmlContent =
                        new StringJoiner("<br>")
                                .add("Start Address: " + route.getStartAddress())
                                .add("Start Date: " + route.getStartDate())
                                .add("Start Time: " + route.getStartTime())
                                .add("Duration: " + HelperFunctions.secondsToString(route.getDuration()))
                                .add("Distance: " + numberFormat.format(route.getDistance()) + "km")
                                .toString();
                String startIconUrl = "https://i.imgur.com/pxqx0G9.png"; // TODO: fix this broken URL
                renderMarker(startLatLong, route.getName(), tripMarkers, startIconUrl, startInfoWindowHtmlContent);


                LatLong endLatLong = new LatLong(route.getEndLatitude(), route.getEndLongitude());
                String endInfoWindowHtmlContent =
                        new StringJoiner("<br>")
                                .add("End Address: " + route.getEndAddress())
                                .add("End Date: " + route.getStopDate())
                                .add("End Time: " + route.getStopTime())
                                .add("Duration: " + HelperFunctions.secondsToString(route.getDuration()))
                                .add("Distance: " + numberFormat.format(route.getDistance()) + "km")
                                .toString();
                String endIconUrl = "https://i.imgur.com/Ha4m8R6.png"; // TODO: fix this broken URL
                renderMarker(startLatLong, route.getName(), tripMarkers, endIconUrl, endInfoWindowHtmlContent);

                MVCArray mvc = new MVCArray(new LatLong[]{startLatLong, endLatLong});
                PolylineOptions polylineOptions = new PolylineOptions().path(mvc).strokeColor("red").strokeWeight(2);
                Polyline polyline = new Polyline(polylineOptions);

                tripLines.add(polyline);
                map.addMapShape(polyline);
            }
        }
    }

    /**
     * Adds a list of wifi locations to the map.
     *
     * @param wifiLocations A list of wifi locations
     */
    public void addWifiLocations(WifiLocation[] wifiLocations) {
        if (wifiLocations == null) {
            return;
        }
        this.wifiLocations.addAll(Arrays.asList(wifiLocations));
    }

    /**
     * Clears wifi Locations from the map.
     */
    public void clearWifiLocations() {
        wifiLocations.clear();
    }

    /**
     * Adds a list of retailers to the map.
     *
     * @param retailLocations List of retailers to be added to the map
     */
    public void addRetailLocations(RetailLocation[] retailLocations) {
        if (retailLocations == null) {
            return;
        }
        this.retailLocations.addAll(Arrays.asList(retailLocations));
    }

    /**
     * Removes all retailers from the map.
     */
    public void clearRetailLocations() {
        retailLocations.clear();
    }

    /**
     * Adds a list of routes to the map.
     *
     * @param routes List of routes to be added to the map.
     */

    public void addRoutes(Route[] routes) {
        if (routes == null) {
            return;
        }
        this.routes.addAll(Arrays.asList(routes));
    }

    /**
     * Removes all routes from the map.
     */
    public void clearRoutes() {
        routes.clear();
    }

    /**
     * Clears the map.
     */
    public void clearAll() {
        clearWifiLocations();
        clearRetailLocations();
        clearRoutes();
    }

    /**
     * If a marker has been selected, this will add nearby wifi locations to the map. Else, it will inform the user that
     * either a marker must be selected or that there are no nearby wifi locations.
     */
    @FXML
    public void showNearbyWifi() {
        //Called by GUI when show nearby wifi button is pressed.
        if (currentPoint == null) {
            makeErrorDialogueBox("Error", "Please select a point");
            return;
        }
        boolean newPoint = false;
        List<WifiLocation> locations = nearbyFinder.findNearbyWifi(currentPoint.getLatitude(), currentPoint.getLongitude());
        for (WifiLocation location : locations) {
            if (wifiLocations.add(location)) {
                renderWifiMarker(location);
                newPoint = true;
                break;
            }
        }
        if (!newPoint) {
            makeErrorDialogueBox("Error", "There aren't any more points nearby");
        }
    }

    /**
     * If a marker has been selected, this will add nearby retailers to the map. Else, it will inform the user that
     * either a marker must be selected or that there are no nearby retailers.
     */
    @FXML
    public void showNearbyRetailers() {
        if (currentPoint == null) {
            makeErrorDialogueBox("Error", "Please select a point.");
        }
        boolean newPoint = false;
        List<RetailLocation> locations = nearbyFinder.findNearbyRetail(currentPoint.getLatitude(), currentPoint.getLongitude());
        for (RetailLocation location : locations) {
            if (retailLocations.add(location)) {
                renderRetailerMarker(location);
                newPoint = true;
                break;
            }
        }
        if (!newPoint) {
            makeErrorDialogueBox("Error", "There aren't any more points nearby.");
        }
    }

    /**
     * Changes the scene to the add data scene while pre-loading the start and end address that was being viewed
     * on the map.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    public void addRouteToDatabase(ActionEvent event) throws IOException {
        //called by GUI when add current route to database button is pressed.
        changeToAddDataScene(event, currentStart, currentEnd);
    }
}