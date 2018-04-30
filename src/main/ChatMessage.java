package main;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class ChatMessage extends BorderPane {
    private Label txtUserName, txtMessage;
    public ChatMessage(String userName, String message, boolean paneColor) {
        super();
        this.txtMessage = new Label(message);
        this.txtUserName = new Label(userName);
        this.txtUserName.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 22));
        this.txtMessage.setFont(Font.font("verdana", FontWeight.THIN, FontPosture.REGULAR, 18));
        this.setTop(this.txtUserName);
        this.setLeft(this.txtMessage);
        if(paneColor) {
            this.setStyle("-fx-background-colour: #aa00ff");
        } else {
            this.setStyle("-fx-background-colour: #6200ea");
        }

    }
}
