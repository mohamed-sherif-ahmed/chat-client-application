package main;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class ChatMessage extends BorderPane {
    private Label txtUserName, txtMessage;
    public ChatMessage(String userName, String message, boolean paneColor) {
        super();
        VBox msg = new VBox();
        this.txtMessage = new Label(message);
        this.txtUserName = new Label(userName);
        this.txtUserName.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 22));
        this.txtMessage.setFont(Font.font("verdana", FontWeight.THIN, FontPosture.REGULAR, 18));
        if(paneColor){
            this.txtMessage.setAlignment(Pos.CENTER_RIGHT);
            this.txtUserName.setAlignment(Pos.CENTER_RIGHT);
        } else {
            this.txtMessage.setAlignment(Pos.CENTER_LEFT);
            this.txtUserName.setAlignment(Pos.CENTER_LEFT);
        }
        msg.setFillWidth(true);
        msg.getChildren().addAll(txtUserName, txtMessage);
        this.setCenter(msg);
        if(paneColor) {
            this.setStyle("-fx-background-colour: #aa00ff");
        } else {
            this.setStyle("-fx-background-colour: #6200ea");
        }

    }
}
