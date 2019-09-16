package GUIControllers.ViewDataControllers;

import com.jfoenix.controls.JFXTextField;
import dataHandler.ListDataHandler;
import dataHandler.SQLiteDB;
import dataManipulation.DeleteData;
import dataObjects.Route;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

/**
 * Controller class for the detailed route information.
 */

public class DetailedRouteInformation extends RouteDataViewerController {

    static private ActionEvent mainAppEvent = null;
    @FXML
    private ComboBox<String> gender;
    @FXML
    private Text startMonth;
    @FXML
    private JFXTextField startAddress;
    @FXML
    private Text startDay;
    @FXML
    private Text startYear;
    @FXML
    private Text bikeID;
    @FXML
    private JFXTextField endLatitude;
    @FXML
    private JFXTextField endYear;
    @FXML
    private JFXTextField startLatitude;
    @FXML
    private JFXTextField cyclistBirthYear;
    @FXML
    private JFXTextField endDay;
    @FXML
    private JFXTextField endLongitude;
    @FXML
    private JFXTextField endStationID;
    @FXML
    private Text startTime;
    @FXML
    private JFXTextField userType;
    @FXML
    private JFXTextField endTime;
    @FXML
    private JFXTextField startLongitude;
    @FXML
    private JFXTextField startStationID;
    @FXML
    private Button delete;
    @FXML
    private JFXTextField tripDuration;
    @FXML
    private JFXTextField endMonth;
    @FXML
    private JFXTextField endAddress;
    @FXML
    private ComboBox<String> list;
    @FXML
    private Button update;
    private Route currentRoute = null;
    private ListDataHandler listDataHandler;
    private SQLiteDB db;

    static public void setMainAppEvent(ActionEvent event) {
        mainAppEvent = event;
    }

    /**
     * Called upon successful opening on the stage. This fills the text fields with the currently stored values of each.
     *
     * @param location  Location of the fxml
     * @param resources Locale-specific data required for the method to run automatically
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        db = Main.getDB();
        listDataHandler = new ListDataHandler(db, Main.hu.currentCyclist.getName());
        List<String> listNames = listDataHandler.getLists();
        list.getItems().addAll(listNames);

        currentRoute = RouteDataViewerController.getRoute();
        startAddress.setText(currentRoute.getStartAddress());
        endAddress.setText(currentRoute.getEndAddress());
        startLatitude.setText(Double.toString(currentRoute.getStartLatitude()));
        endLatitude.setText(Double.toString(currentRoute.getEndLatitude()));
        startLongitude.setText(Double.toString(currentRoute.getStartLongitude()));
        endLongitude.setText(Double.toString(currentRoute.getEndLongitude()));
        startTime.setText(currentRoute.getStartTime());
        endTime.setText(currentRoute.getStopTime());
        startDay.setText(currentRoute.getStartDay());
        endDay.setText(currentRoute.getStopDay());
        startMonth.setText(currentRoute.getStartMonth());
        endMonth.setText(currentRoute.getStopMonth());
        startYear.setText(currentRoute.getStartYear());
        endYear.setText(currentRoute.getStopYear());
        startStationID.setText(Integer.toString(currentRoute.getStartStationID()));
        endStationID.setText(Integer.toString(currentRoute.getEndStationID()));
        tripDuration.setText(Integer.toString(currentRoute.getDuration()));
        cyclistBirthYear.setText(Integer.toString(currentRoute.getAge()));
        gender.getSelectionModel().select(currentRoute.getGender());
        userType.setText(currentRoute.getUserType());
        bikeID.setText(currentRoute.getBikeID());
        list.getEditor().setText(currentRoute.getListName());
        startAddressListener();
        endAddressListener();
        startLatListener();
        startLongListener();
        endLatListener();
        endLongListener();
        endTimeListener();
        endDayListener();
        endMonthListener();
        endYearListener();
        startStationIDListener();
        endStationIDListener();
        tripDurationListener();
        userTypeListener();
        cyclistBirthYearListener();
        listListener();

        list.getEditor().setOnMouseClicked(event -> {
            List<String> lists = listDataHandler.getLists();
            if (!lists.contains(currentRoute.getListName()) && currentRoute.getListName() != null) {
                makeErrorDialogueBox("Cannot edit List", "This Route is part of another users " +
                        "list and\ncannot be changed");
                list.setDisable(true);
            }
        });
    }


    /**
     * Checks each textfield and update the values in the database. If any fail, the user is informed and expected to
     * correct them.
     *
     * @param event Created when the method is called
     */
    @FXML
    void updateValues(ActionEvent event) {
        try {
            System.out.println("Update button clicked");
            currentRoute.setStartAddress(startAddress.getText());
            currentRoute.setEndAddress(endAddress.getText());
            currentRoute.setStartLat(Double.parseDouble(startLatitude.getText()));
            currentRoute.setEndLat(Double.parseDouble(endLatitude.getText()));
            currentRoute.setStartLong(Double.parseDouble(startLongitude.getText()));
            currentRoute.setEndLong(Double.parseDouble(endLongitude.getText()));
            currentRoute.setStopTime(endTime.getText());
            currentRoute.setStopDay(endDay.getText());
            currentRoute.setStopMonth(endMonth.getText());
            currentRoute.setStopYear(endYear.getText());
            currentRoute.setStartID(Integer.parseInt(startStationID.getText()));
            currentRoute.setEndID(Integer.parseInt(endStationID.getText()));
            currentRoute.setDuration(Integer.parseInt(tripDuration.getText()));
            currentRoute.setAge(Integer.parseInt(cyclistBirthYear.getText()));
            currentRoute.setGender(gender.getSelectionModel().getSelectedItem());
            currentRoute.setUserType(userType.getText());
            if (list.getEditor().getText() != null) {
                listDataHandler.addList(list.getSelectionModel().getSelectedItem());
                currentRoute.setListName(list.getEditor().getText());
            }

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            showRoutes(mainAppEvent);
        } catch (Exception exception) {
            makeErrorDialogueBox("Cannot update data.", "One (or more) field(s) is of an " +
                    "incorrect type.");
        }
    }


    /**
     * Checks if list is owned by current user. If not creates an error popup and disables the list field
     *
     */
    @FXML
    void checkIfEditable() {
        List<String> lists = listDataHandler.getLists();
        if (!lists.contains(currentRoute.getListName()) && currentRoute.getListName() != null) {
            makeErrorDialogueBox("Cannot edit List", "This Route is part of another users " +
                    "list and\ncannot be changed");
            list.setDisable(true);
        }
    }


    /**
     * Error handler for startAddress field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void startAddressListener() {
        startAddress.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (startAddress.getText().length() > 50) {
                startAddress.setFocusColor(RED);
                startAddress.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                startAddress.setFocusColor(DARKSLATEBLUE);
                startAddress.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for endAddress field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endAddressListener() {
        endAddress.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (endAddress.getText().length() > 50) {
                endAddress.setFocusColor(RED);
                endAddress.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endAddress.setFocusColor(DARKSLATEBLUE);
                endAddress.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for startLatitude field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void startLatListener() {
        startLatitude.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!startLatitude.getText().matches("-?[0-9]?[0-9]?[0-9].[0-9]+")) {
                startLatitude.setFocusColor(RED);
                startLatitude.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                startLatitude.setFocusColor(DARKSLATEBLUE);
                startLatitude.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for startLongitude field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void startLongListener() {
        startLongitude.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!startLongitude.getText().matches("-?[0-9]?[0-9]?[0-9].[0-9]+")) {
                startLongitude.setFocusColor(RED);
                startLongitude.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                startLongitude.setFocusColor(DARKSLATEBLUE);
                startLongitude.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for endLatitude field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endLatListener() {
        endLatitude.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endLatitude.getText().matches("-?[0-9]?[0-9]?[0-9].[0-9]+")) {
                endLatitude.setFocusColor(RED);
                endLatitude.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endLatitude.setFocusColor(DARKSLATEBLUE);
                endLatitude.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for endLongitude field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endLongListener() {
        endLongitude.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endLongitude.getText().matches("-?[0-9]?[0-9]?[0-9].[0-9]+")) {
                endLongitude.setFocusColor(RED);
                endLongitude.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endLongitude.setFocusColor(DARKSLATEBLUE);
                endLongitude.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for endTime field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endTimeListener() {
        endTime.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endTime.getText().matches("([0-1][0-9]|2[0-4]):[0-5][0-9](:[0-5][0-9])?")) {
                endTime.setFocusColor(RED);
                endTime.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endTime.setFocusColor(DARKSLATEBLUE);
                endTime.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for endDay field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endDayListener() {
        endDay.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endDay.getText().matches("[0-2]?[0-9]|3[0-1]")) {
                endDay.setFocusColor(RED);
                endDay.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endDay.setFocusColor(DARKSLATEBLUE);
                endDay.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for endMonth field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endMonthListener() {
        endMonth.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endMonth.getText().matches("0?[0-9]|1[0-2]")) {
                endMonth.setFocusColor(RED);
                endMonth.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endMonth.setFocusColor(DARKSLATEBLUE);
                endMonth.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for endYear field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endYearListener() {
        endYear.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endYear.getText().matches("[0-9]*") || endYear.getText().length() < 4 || endYear.getText().length() > 4) {
                endYear.setFocusColor(RED);
                endYear.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endYear.setFocusColor(DARKSLATEBLUE);
                endYear.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        });
    }


    /**
     * Error handler for startStationID field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void startStationIDListener() {
        startStationID.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!startStationID.getText().matches("[0-9]*") || startStationID.getText().length() > 6) {
                startStationID.setFocusColor(RED);
                startStationID.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                startStationID.setFocusColor(DARKSLATEBLUE);
                startStationID.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for endStationID field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void endStationIDListener() {
        endStationID.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!endStationID.getText().matches("[0-9]*") || endStationID.getText().length() > 6) {
                endStationID.setFocusColor(RED);
                endStationID.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                endStationID.setFocusColor(DARKSLATEBLUE);
                endStationID.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for tripDuration field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void tripDurationListener() {
        tripDuration.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!tripDuration.getText().matches("[0-9]*") || tripDuration.getText().length() > 9) {
                tripDuration.setFocusColor(RED);
                tripDuration.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                tripDuration.setFocusColor(DARKSLATEBLUE);
                tripDuration.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for cyclistBirthYear field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void cyclistBirthYearListener() {
        cyclistBirthYear.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (!cyclistBirthYear.getText().matches("[0-9]*") || cyclistBirthYear.getText().length() > 4) {
                cyclistBirthYear.setFocusColor(RED);
                cyclistBirthYear.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                cyclistBirthYear.setFocusColor(DARKSLATEBLUE);
                cyclistBirthYear.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for userType field. Uses a listener to see state of text. Sets color and makes confirm button
     * un-selectable if text field incorrect.
     */
    private void userTypeListener() {
        userType.textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (userType.getText().length() > 10) {
                userType.setFocusColor(RED);
                userType.setUnFocusColor(RED);
                update.setDisable(true);
            } else {
                userType.setFocusColor(DARKSLATEBLUE);
                userType.setUnFocusColor(GREEN);
                update.setDisable(false);
            }
        }));
    }


    /**
     * Error handler for list field. Uses a listener to see state of text. Makes confirm button
     * un-selectable if text field incorrect. Stops strings over 25 char long from being entered.
     */
    private void listListener() {
        list.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println("TextField Text Changed (newValue: " + newValue + ")");
            if (list.getEditor().getText().length() > 25) {
                String listName = list.getEditor().getText();
                listName = listName.substring(0, 25);
                list.getEditor().setText(listName);
            } else if (listDataHandler.checkListName(list.getEditor().getText())) {
                makeErrorDialogueBox("List name already exists", "This list name has already " +
                        "been used by another\nuser. Please choose a new name.\n");
                update.setDisable(true);
            } else {
                update.setDisable(false);
            }
        }));
    }


    /**
     * Called when the delete retailer button is pressed. Does a popup check as to whether the user is sure he/she/other
     * wants to delete the retailer and if so, removes it from the database.
     * @param event Created when the method is called
     */
    @FXML
    void deleteRoute(ActionEvent event)  throws IOException{
        if (makeConfirmationDialogueBox("Are you sure you want to delete this retailer?", "This cannot be undone.")) {

            DeleteData deleteData = new DeleteData(db, Main.hu.currentCyclist.getName());
            int deleteStatus = deleteData.checkRouteDeletionStatus(currentRoute.getStartTime(),
                    currentRoute.getStartDay(), currentRoute.getStartMonth(), currentRoute.getStartYear(),
                    currentRoute.getBikeID());
            if (deleteStatus == 1) {
                makeErrorDialogueBox("Failed to delete route", "Another user has this route " +
                        "in a list they created.");
            } else if (deleteStatus == 2) {
                makeErrorDialogueBox("Failed to delete route", "Another user has this route " +
                        "in their favourite\nroutes list.");
            } else if (deleteStatus == 3) {
                makeErrorDialogueBox("Failed to delete route", "Another user has this route " +
                        "in their completed\nroutes list.");
            } else {
                System.out.println("OK to delete");
                deleteData.deleteRoute(currentRoute.getStartTime(),
                        currentRoute.getStartDay(), currentRoute.getStartMonth(), currentRoute.getStartYear(),
                        currentRoute.getBikeID());
            }

            //Closes popup
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            showRoutes(mainAppEvent);
        }
    }
}
