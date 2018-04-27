package main;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements ControlledScreen, Initializable {
    ScreensController myScreen;

    @FXML
    private VBox usersVBox;

    @FXML
    private JFXTextField userIdSelection;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        this.myScreen = screenPage;
    }


    class updateUserListService extends Service {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Object call() throws Exception {
                    while (true) {
                        updateUserList();


                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException interrupted) {
                            if (isCancelled()) {
                                updateMessage("Cancelled");

                            }

                        }
                        //return null;

                    }
                }
            };
        }
    }

    public void updateUserList() {
        System.out.println("UPDATING...!");
        try {
            myScreen.server_out.write("info\n");
            myScreen.server_out.flush();
            String s = myScreen.server_in.readLine();
            String[] users = s.split(";");
//            String finalString = "";
            Platform.runLater(
                    () -> {
                        // Update UI here.
                        usersVBox.getChildren().clear();
                    }
            );
            for (String u :
                    users) {
                String[] userData = u.split(",");
                final String finalString = "Username : " + userData[0] + " ID : " + userData[1];
                System.out.println(userData[2].substring(1));

                myScreen.connectedUsers.add(new User(userData[0], userData[1], userData[2].substring(1), userData[3], userData[4]));
                Platform.runLater(
                        () -> {
                            // Update UI here.
                            usersVBox.getChildren().add(new Label(finalString));
                        }
                );
            }
//            userDisplay.setText(finalString);
//            System.out.println(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Init Lobby");
        updateUserListService updateUserListService = new updateUserListService();
        updateUserListService.start();
    }

    @FXML
    void startChat(ActionEvent event) {
        User desiredUser = myScreen.connectedUsers.get(0);
        for (User u :
                myScreen.connectedUsers) {
            if(u.getId().equals(userIdSelection.getText())) {
                desiredUser = u;
                break;
            }
        }
        try {
            myScreen.peer_socket = new Socket(desiredUser.getIp(),Integer.parseInt(desiredUser.getTx()));
            myScreen.peer_out =  new BufferedWriter(new OutputStreamWriter(myScreen.peer_socket.getOutputStream()));
            myScreen.peer_in =  new BufferedReader(new InputStreamReader(myScreen.peer_socket.getInputStream()));
            myScreen.peer_out.write("CHAT A\n");
            myScreen.peer_out.flush();
            FXMLLoader loadedChat = myScreen.loadScreen("A", "/ChatView.fxml");
            ChatView loadedChatView = loadedChat.getController();
            loadedChatView.setChatIn(myScreen.peer_in);
            loadedChatView.setChatOut(myScreen.peer_out);
            loadedChatView.chatService.start();
            myScreen.setScreen("A");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
