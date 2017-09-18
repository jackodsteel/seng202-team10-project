package GUIControllers;
    import dataManipulation.DataFilterer;
    import com.jfoenix.controls.JFXDrawer;
    import com.jfoenix.controls.JFXHamburger;
    import com.jfoenix.controls.JFXTextField;
    import com.jfoenix.controls.JFXToggleButton;
    import dataAnalysis.Route;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.layout.VBox;
    import javafx.fxml.FXMLLoader;
    import java.io.IOException;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.ResourceBundle;

public class dataViewerController {

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private JFXToggleButton female;

    @FXML
    private JFXToggleButton male;

    @FXML
    private ToggleGroup genderToggleGroup;

    @FXML
    private JFXTextField startAgeInput;

    @FXML
    private JFXTextField endAgeInput;

    @FXML
    private JFXTextField startTimeInput;

    @FXML
    private JFXTextField endTimeInput;

    @FXML
    private JFXTextField startDateInput;

    @FXML
    private JFXTextField endDateInput;

    @FXML
    private JFXDrawer drawer;

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

    private ObservableList<Route> routeList = FXCollections.observableArrayList();

    @FXML
    void openDrawer() throws IOException {

        VBox box = FXMLLoader.load(getClass().getClassLoader().getResource("SidePanel.fxml"));
        drawer.setSidePane(box);
        if (drawer.isShown()) {
            drawer.close();
        }
        else {
            drawer.open();
        }

    }

    public void initialize(URL url, ResourceBundle rb) {
        StartLocation.setCellValueFactory(new PropertyValueFactory<Route, String>("StartLocation"));
        EndLocation.setCellValueFactory(new PropertyValueFactory<Route, String>("EndLocation"));
        Date.setCellValueFactory(new PropertyValueFactory<Route, String>("StartDate"));
        StartTime.setCellValueFactory(new PropertyValueFactory<Route, String>("StartTime"));
        EndTime.setCellValueFactory(new PropertyValueFactory<Route, String>("StopTime"));
        tableView.setItems(routeList);
        tableView.getColumns().setAll(StartLocation, EndLocation, Date, StartTime, EndTime);
    }

    private List<Route> parseList() {
        List<Route> routes = new ArrayList<Route>();
        return routes;
    }

    @FXML
    void displayData(ActionEvent event) throws IOException {
        int gender;
        if(genderToggleGroup.getSelectedToggle() == null) {
            gender = -1;
        } else {
            gender = Integer.valueOf(genderToggleGroup.getSelectedToggle().getUserData().toString());
        }
        int ageLower = Integer.valueOf(startAgeInput.getText());
        int ageUpper = Integer.valueOf(endAgeInput.getText());
        String dateLower = startDateInput.getText();
        String dateUpper = endDateInput.getText();
        if ("DD/MM/YYYY".equals(dateLower) || "DD/MM/YYYY".equals(dateUpper)) {
            dateLower = null;
            dateUpper = null;
        }
        String timeLower = startTimeInput.getText();
        String timeUpper = endTimeInput.getText();
        if ("HH:MM:SS".equals(timeLower) || "HH:MM:SS".equals(timeUpper)) {
            timeLower = null;
            timeUpper = null;
        }
        DataFilterer filterer = new DataFilterer();
        ArrayList<Route> routes = filterer.filter(gender, dateLower, dateUpper, ageLower, ageUpper,
                timeLower, timeUpper, -1, -1);
        tableView.getItems().clear();
        routeList.addAll(routes);
    }


}

