package main;

import dataHandler.*;
import dataManipulation.UpdateData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static HandleUsers hu;
    static SQLiteDB db;

    public static SQLiteDB getDB() {
        return db;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initDB();

        Parent root = FXMLLoader.load(getClass().getResource("/FXML/startUp.fxml"));

        String url = getClass().getResource("/Images/bicycleIcon.png").toString();
        primaryStage.getIcons().add(new Image(url));


        primaryStage.setTitle("Pedals");
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root, 1100, 650));
        primaryStage.show();
    }

    public static void initDB() {
        db = new SQLiteDB();
        Geocoder.init();
        hu = new HandleUsers();
        hu.init(db);
        UpdateData.init(db);
        new DatabaseUser(db);
        new TakenRoutes(db);
        new FavouriteRouteData(db);
        new FavouriteWifiData(db);
        new FavouriteRetailData(db);
    }

}

