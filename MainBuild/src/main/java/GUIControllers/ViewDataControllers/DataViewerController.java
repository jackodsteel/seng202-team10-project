package GUIControllers.ViewDataControllers;

import GUIControllers.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Abstract parent controller class for all controllers in the data viewer section of the application.
 * Here, scene changing methods are defined.
 */


public abstract class DataViewerController extends Controller implements Initializable {

    /**
     * Changes the current scene to view route data.
     *
     * @param event Created when the method is called.
     * @throws IOException Handles errors caused by an fxml not loading correctly.
     */
    @FXML
    void showRoutes(ActionEvent event) throws IOException {
        showPage("/FXML/DataViewerFXMLs/routeViewData.fxml", event);
    }

    /**
     * Changes the current scene to wifi location viewer.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void showWifiLocations(ActionEvent event) throws IOException {
        showPage("/FXML/DataViewerFXMLs/wifiViewData.fxml", event);
    }

    /**
     * Changes the current scene to the the retailer data viewer.
     *
     * @param event Created when the method is called.
     * @throws IOException Handles errors caused by an fxml not loading correctly.
     */
    @FXML
    void showRetailers(ActionEvent event) throws IOException {
        showPage("/FXML/DataViewerFXMLs/retailerViewData.fxml", event);
    }

    private void showPage(String fxmlLocation, ActionEvent event) throws IOException {
        doOnSceneChange();
        Parent parent = FXMLLoader.load(getClass().getResource(fxmlLocation));
        Scene scene = new Scene(parent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(scene);
    }

}

