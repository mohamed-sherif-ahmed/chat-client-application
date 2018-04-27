package main;

import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Main extends Application{
    public static Scanner sc = new Scanner(System.in);
    @Override
    public void start(Stage primaryStage) throws Exception{


        Parent root = FXMLLoader.load(getClass().getResource("/ChatView.fxml"));
        Scene scene = new Scene(root);


        primaryStage.setScene(scene);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
    }



    public static void main(String[] args) {

        launch(args);

    }
}
