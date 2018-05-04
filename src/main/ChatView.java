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
import java.util.StringTokenizer;

public class ChatView implements Initializable {

    BufferedWriter chatOut;
    BufferedReader chatIn;
//    ScreensController myController;
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
                            System.out.println("UPDATE CHAT VIEW THREAD " + msg + " GCN " +groupChatNumber);
                            StringTokenizer s_tknzr = new StringTokenizer(msg, ";");
                            String msgCode = s_tknzr.nextToken();
                            if (!msgCode.equals("group_msg") && !groupChatNumber.equals("-1")) {
                                System.out.println("ESKSDADSADSDS !! !!");
//                                Thread.sleep(1000);
                                continue;
                            }
                            final String finalMsg = s_tknzr.nextToken();
                            System.out.println(finalMsg);
                            Platform.runLater(
                                    () -> {
                                        // Update UI here.
                                        chatArea.getChildren().add(new ChatMessage(chatName, finalMsg, true));
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
                chatOut.write("p2p_msg;" + txtMessage.getText() + "\n");
                chatOut.flush();
                chatArea.getChildren().add(new ChatMessage(ScreensController.clientName, txtMessage.getText(), false));
                txtMessage.setText("");
            } else {
                chatOut.write("group_msg\n");
                chatOut.flush();
                chatOut.write(groupChatNumber + ";" + txtMessage.getText() + ";" + ScreensController.clientName + "\n");
                chatOut.flush();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateGroupChatMessage(String msg, String wad){
        chatArea.getChildren().add(new ChatMessage(wad, msg, true));
    }

    public void setChatOut(BufferedWriter chatOut) {
        this.chatOut = chatOut;
    }

    public void setChatIn(BufferedReader chatIn) {
        this.chatIn = chatIn;
    }

//    @Override
//    public void setScreenParent(ScreensController screenPage) {
//        this.myController = screenPage;
//    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}

