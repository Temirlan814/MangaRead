package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws IOException {
        HelloApplication.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        primaryStage.getIcons().add(new Image("file:C:\\Users\\Temirlan\\IdeaProjects\\demo\\src\\main\\java\\com\\example\\demo\\images.png"));
        Parent root = loader.load();
        primaryStage.setTitle("Mangaread");
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("Style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void changeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("Catalog.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("CatologStyle.css")).toExternalForm());
        primaryStage.setScene(scene);
    }
    public static void ComicsRead() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ComicsRead.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("Comics.css")).toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}
