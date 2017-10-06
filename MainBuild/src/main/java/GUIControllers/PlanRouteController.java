package GUIControllers;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import dataAnalysis.RetailLocation;
import dataAnalysis.Route;
import dataAnalysis.WifiLocation;
import dataManipulation.FindNearbyLocations;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.HelperFunctions;
import main.Main;
import netscape.javascript.JSObject;

import javax.print.attribute.standard.Fidelity;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * Controller for the plan route scene.
 */

public class PlanRouteController extends Controller implements Initializable, MapComponentInitializedListener, DirectionsServiceCallback {

    @FXML
    protected TextField startAddressField;
    @FXML
    protected TextField endAddressField;
    @FXML
    protected GoogleMapView mapView;
    protected DirectionsService directionsService;
    protected DirectionsPane directionsPane;
    protected DirectionsRenderer directionsRenderer;
    private double STARTLAT = 40.745968;
    private double STARTLON = -73.994039;
    @FXML
    private Button nearbyWifiButton;
    @FXML
    private Button nearbyRetailerButton;
    private GeocodingService geocodingService;

    private GoogleMap map;
    private StringProperty startAddress = new SimpleStringProperty();
    private StringProperty endAddress = new SimpleStringProperty();
    private ArrayList<Marker> wifiMarkers = new ArrayList<Marker>();
    private ArrayList<Marker> retailerMarkers = new ArrayList<Marker>();
    private ArrayList<Marker> tripMarkers = new ArrayList<Marker>();
    private ArrayList<Polyline> tripLines = new ArrayList<Polyline>();
    private DecimalFormat numberFormat = new DecimalFormat("0.00");
    private FindNearbyLocations nearbyFinder;
    private LatLong currentPoint;
    private InfoWindow currentInfoWindow;
    private String currentStart;
    private String currentEnd;

    private HashSet<WifiLocation> wifiLocations = new HashSet<>();
    private HashSet<RetailLocation> retailLocations = new HashSet<>();
    private HashSet<Route> routes = new HashSet<>();

    /**
     * Called automatically when the map is loaded. Loads the map.
     */
    @Override
    public void mapInitialized() {
        System.out.println("Init");
        geocodingService = new GeocodingService();
        MapOptions mapOptions = new MapOptions();

        currentInfoWindow = new InfoWindow();

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
     * Called when enter key is pressed from inside the address textfields. Requests a route and loads it on the map.
     *
     * @param event Created when the method is called
     */
    @FXML
    public void addressTextFieldAction(ActionEvent event) {
        String start = startAddress.get().replace("'", "\'");
        String end = endAddress.get().replace("'", "\'");
        DirectionsRequest request = new DirectionsRequest(start, end, TravelModes.BICYCLING);
        directionsService.getRoute(request, this, directionsRenderer);
        currentStart = startAddress.get();
        currentEnd = endAddress.get();
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
        nearbyRetailerButton.setDisable(false);
        nearbyWifiButton.setDisable(false);
        System.out.println(results.getRoutes().size());
        results.getRoutes().get(0).getLegs().get(0);
        DirectionsLeg leg = results.getRoutes().get(0).getLegs().get(0);
        double midLat = (leg.getStartLocation().getLatitude() + leg.getEndLocation().getLatitude()) / 2;
        double midLon = (leg.getStartLocation().getLongitude() + leg.getEndLocation().getLongitude()) / 2;
        LatLong mid = new LatLong(midLat, midLon);
        System.out.println(mid);
        currentPoint = mid;
    }

    private void renderWifiMarker(WifiLocation location) {
        MarkerOptions options = new MarkerOptions();
        LatLong latLong = new LatLong(location.getLatitude(), location.getLongitude());
        options.position(latLong)
                .title(location.getName())
                .visible(true)
                .icon("http://maps.google.com/mapfiles/ms/icons/orange-dot.png");
        Marker marker = new Marker(options);
        wifiMarkers.add(marker);
        map.addMarker(marker);

        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
            nearbyRetailerButton.setDisable(false);
            nearbyWifiButton.setDisable(false);
            System.out.println("Clicked");
            InfoWindowOptions infoWindowOptions = new InfoWindowOptions()
                    .content(
                            "SSID: " + location.getSSID() + "<br>" +
                                    "Provider: " + location.getProvider() + "<br>" +
                                    "Address: " + location.getAddress() + "<br>" +
                                    "Extra Info: " + location.getRemarks());
            currentInfoWindow.close();
            currentInfoWindow = new InfoWindow(infoWindowOptions);
            currentInfoWindow.open(map, marker);
            currentPoint = latLong;
        });
    }

    /**
     * Renders the CurrentStates wifiLocations HashSet on the map.
     * Initially removes all current wifiMarkers, then renders each one from the HashSet
     */
    private void renderWifiMarkers() {
        for (Marker marker : wifiMarkers) {
            map.removeMarker(marker);
        }
        wifiMarkers.clear();

        for (WifiLocation location : wifiLocations) {
            renderWifiMarker(location);
        }
    }

    private void renderRetailerMarker(RetailLocation location) {
        MarkerOptions options = new MarkerOptions();
        LatLong latLong = new LatLong(location.getLatitude(), location.getLongitude());
        options.position(latLong)
                .title(location.getName())
                .visible(true)
                .icon("http://maps.google.com/mapfiles/ms/icons/blue-dot.png");
        Marker marker = new Marker(options);
        retailerMarkers.add(marker);
        map.addMarker(marker);
        System.out.println("Added");
        System.out.println(location.getLatitude());
        System.out.println(location.getLongitude());

        map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
            nearbyRetailerButton.setDisable(false);
            nearbyWifiButton.setDisable(false);
            System.out.println("Clicked");
            InfoWindowOptions infoWindowOptions = new InfoWindowOptions()
                    .content(
                            "Name: " + location.getName() + "<br>" +
                                    "Address: " + location.getAddress() + "<br>" +
                                    "Category: " + location.getMainType() + "<br>" +
                                    "Extra Info: " + location.getSecondaryType());
            currentInfoWindow.close();
            currentInfoWindow = new InfoWindow(infoWindowOptions);
            currentInfoWindow.open(map, marker);
            currentPoint = latLong;
        });
    }

    /**
     * Renders the CurrentStates retailerMarkers HashSet on the map.
     * Initially removes all current retailerMarkers, then renders each one from the HashSet
     */
    private void renderRetailerMarkers() {
        for (Marker marker : retailerMarkers) {
            map.removeMarker(marker);
        }
        retailerMarkers.clear();

        for (RetailLocation location : retailLocations) {
            renderRetailerMarker(location);
        }
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
                MarkerOptions options = new MarkerOptions();
                System.out.println(route.getStartLatitude());
                System.out.println(route.getStartLongitude());
                LatLong latLong = new LatLong(route.getStartLatitude(), route.getStartLongitude());
                options.position(latLong)
                        .title(route.getName())
                        .visible(true)
                        .icon("http://maps.google.com/mapfiles/ms/icons/green-dot.png");
                Marker marker = new Marker(options);

                tripMarkers.add(marker);
                map.addMarker(marker);
                map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
                    nearbyRetailerButton.setDisable(false);
                    nearbyWifiButton.setDisable(false);
                    System.out.println("Clicked");
                    InfoWindowOptions infoWindowOptions = new InfoWindowOptions()
                            .content(
                                    "Start Address: " + route.getStartAddress() + "<br>" +
                                            "Start Date: " + route.getStartDate() + "<br>" +
                                            "Start Time: " + route.getStartTime() + "<br>" +
                                            "Duration: " + HelperFunctions.secondsToString(route.getDuration()) + "<br>" +
                                            "Distance: " + numberFormat.format(route.getDistance()) + "km");
                    currentInfoWindow.close();
                    currentInfoWindow = new InfoWindow(infoWindowOptions);
                    currentInfoWindow.open(map, marker);
                    currentPoint = latLong;
                });

                MarkerOptions options2 = new MarkerOptions();
                LatLong latLong2 = new LatLong(route.getEndLatitude(), route.getEndLongitude());
                options2.position(latLong2)
                        .title(route.getName())
                        .visible(true)
                        .icon("http://maps.google.com/mapfiles/ms/icons/red-dot.png");
                Marker marker2 = new Marker(options2);

                tripMarkers.add(marker2);
                map.addMarker(marker2);

                map.addUIEventHandler(marker2, UIEventType.click, (JSObject obj) -> {
                    nearbyRetailerButton.setDisable(false);
                    nearbyWifiButton.setDisable(false);
                    System.out.println("Clicked");
                    InfoWindowOptions infoWindowOptions = new InfoWindowOptions()
                            .content(
                                    "End Address: " + route.getEndAddress() + "<br>" +
                                            "End Date: " + route.getStopDate() + "<br>" +
                                            "End Time: " + route.getStopTime() + "<br>" +
                                            "Duration: " + HelperFunctions.secondsToString(route.getDuration()) + "<br>" +
                                            "Distance: " + numberFormat.format(route.getDistance()) + "km");
                    currentInfoWindow.close();
                    currentInfoWindow = new InfoWindow(infoWindowOptions);
                    currentInfoWindow.open(map, marker2);
                    currentPoint = latLong2;
                });

                LatLong[] ary = new LatLong[]{latLong, latLong2};
                MVCArray mvc = new MVCArray(ary);
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
    public void addWifiLocations(ArrayList<WifiLocation> wifiLocations) {
        if (wifiLocations == null) {
            return;
        }
        for (WifiLocation location : wifiLocations) {
            this.wifiLocations.add(location);
        }
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
    public void addRetailLocations(ArrayList<RetailLocation> retailLocations) {
        if (retailLocations == null) {
            return;
        }
        for (RetailLocation location : retailLocations) {
            this.retailLocations.add(location);
        }
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
    public void addRoutes(ArrayList<Route> routes) {
        if (routes == null) {
            return;
        }
        for (Route route : routes) {
            this.routes.add(route);
        }
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
        } else {
            boolean newPoint = false;
            ArrayList<WifiLocation> locations = nearbyFinder.findNearbyWifi(currentPoint.getLatitude(), currentPoint.getLongitude());
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
    }

    /**
     * If a marker has been selected, this will add nearby retailers to the map. Else, it will inform the user that
     * either a marker must be selected or that there are no nearby retailers.
     */
    @FXML
    public void showNearbyRetailers() {
        //Called by GUI when show nearby retails button is pressed.
        if (currentPoint == null) {
            makeErrorDialogueBox("Error", "Please select a point");
        } else {
            boolean newPoint = false;
            ArrayList<RetailLocation> locations = nearbyFinder.findNearbyRetail(currentPoint.getLatitude(), currentPoint.getLongitude());
            for (RetailLocation location : locations) {
                if (retailLocations.add(location)) {
                    renderRetailerMarker(location);
                    newPoint = true;
                    break;
                }
            }
            if (!newPoint) {
                makeErrorDialogueBox("Error", "There aren't any more points nearby");
            }
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