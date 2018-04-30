package main;

import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatView implements ControlledScreen, Initializable {

    BufferedWriter chatOut;
    BufferedReader chatIn;
    ScreensController myController;
    UpdateChatView chatService = new UpdateChatView();
    String chatName;
    String groupChatNumber = "-1";
    @FXML
    private JFXTextField txtMessage;

    @FXML
    private VBox chatArea;

    class UpdateChatView extends Service {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Object call() throws Exception {
                    while (true) {
                        try {
                            final String msg = chatIn.readLine();
                            Platform.runLater(
                                    () -> {
                                        // Update UI here.
                                        chatArea.getChildren().add(new ChatMessage(chatName, msg, true));
                                    }
                            );
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }


    @FXML
    void sendMessage(ActionEvent event) {
        try{
            if(groupChatNumber.equals("-1")){
                chatOut.write(txtMessage.getText() + "\n");
                chatOut.flush();
                chatArea.getChildren().add(new ChatMessage(myController.clientName, txtMessage.getText(), false));
                txtMessage.setText("");
            } else {
                chatOut.write(groupChatNumber + ";" + txtMessage.getText() + "\n");
                chatOut.flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setChatOut(BufferedWriter chatOut) {
        this.chatOut = chatOut;
    }

    public void setChatIn(BufferedReader chatIn) {
        this.chatIn = chatIn;
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        this.myController = screenPage;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}

