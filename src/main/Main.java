package main;

import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.StageStyle;

public class Main extends Application{
    public static Scanner sc = new Scanner(System.in);
    ScreensController mainScreen;

    @Override
    public void start(Stage primaryStage) throws Exception{
//
//
//    Parent root = FXMLLoader.load(getClass().getResource("/ChatView.fxml"));
//    Scene scene = new Scene(root);
//
//
//        primaryStage.setScene(scene);
//        primaryStage.resizableProperty().setValue(Boolean.FALSE);
//        primaryStage.show();


        mainScreen = new ScreensController();
        mainScreen.loadScreen("MainView","/ServerConnect.fxml");
//        mainScreen.loadScreen("Lobby","/Lobby.fxml");
        mainScreen.setScreen("MainView");
        Group root = new Group();
        root.getChildren().addAll(mainScreen);
        Scene scene = new Scene(root, 600, 450);
//        scene.setFill(new Color(0.247,0.318,0.71,1));
        primaryStage.setScene(scene);
//        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
//        mainStagePosX = primaryStage.getX();
//        mainStagePosY = primaryStage.getY();
}



    public static void main(String[] args) {

        launch(args);

    }
}
