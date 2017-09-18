package GUIControllers;

import com.jfoenix.controls.JFXDrawer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Controller {
    @FXML
    private JFXDrawer drawer;

    @FXML
    private Button addDataButton;

    @FXML
    private Button viewDataButton;

    @FXML
    private Button planRouteButton;

    @FXML
    private Button homeButton;

    @FXML
    void openDrawer() throws IOException {
        VBox box = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/SidePanel.fxml"));
        drawer.setSidePane(box);
        System.out.println("clicked");

        if (drawer.isShown()) {
            drawer.close();
        }
        else {
            drawer.open();
        }
    }

    @FXML
    void changeToPlanRouteScene(ActionEvent event) throws IOException {
        Parent planRouteParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/planRoute.fxml"));
        Scene planRouteScene = new Scene(planRouteParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(planRouteScene);
    }

    @FXML
    void changeToHomeScene(ActionEvent event) throws IOException {
        Parent homeParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/home.fxml"));
        Scene homeScene = new Scene(homeParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(homeScene);
    }


    @FXML
    void changeToAddDataScene(ActionEvent event) throws IOException {
        Parent addDataParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/addData.fxml"));
        Scene addDataScene = new Scene(addDataParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(addDataScene);
    }

    @FXML
    void changeToViewDataScene(ActionEvent event) throws IOException {
        Parent viewDataParent = FXMLLoader.load(getClass().getClassLoader().getResource("FXML/viewData.fxml"));
        Scene viewDataScene = new Scene(viewDataParent);
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setScene(viewDataScene);
    }

    @FXML
    void makeErrorDialogueBox(String errorMessage, String errorDetails) {
        Alert alert = new Alert(Alert.AlertType.ERROR, errorDetails, ButtonType.OK);
        alert.setHeaderText(errorMessage);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            System.out.println("Ok pressed");
        }
    }

    /**
     * Makes a confirmation dialogue box and returns yes as true, no as false.
     * Assumes that if dialogue is exited, result is no.
     *
     * @param errorMessage String that provides the error message for the dialogue box.
     * @param errorDetails String that provides the error details for the dialogue box.
     * @return boolean representing result of yes/no answer in confirmation box.
     */
    @FXML
    boolean makeConfirmationDialogueBox(String errorMessage, String errorDetails) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, errorDetails, ButtonType.NO, ButtonType.YES);
        alert.setHeaderText(errorMessage);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            return true;
        } else {
            return false;
        }
    }





}