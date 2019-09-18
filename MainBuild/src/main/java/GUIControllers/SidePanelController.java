package GUIControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the side panel.
 */

public class SidePanelController extends Controller implements Initializable {

    @FXML
    private Button profileButton;

    /**
     * Runs on loading the side panel. Sets the username for the logout button.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String userName = Main.hu.currentCyclist.getName();
        profileButton.setText("Profile (" + userName + ")");
    }


    /**
     * Opens the help menu.
     *
     * @param event Created when the method is called
     * @throws IOException Handles errors caused by an fxml not loading correctly
     */
    @FXML
    void openHelpStage(ActionEvent event) throws IOException {
        getHelp(event);
    }

}
