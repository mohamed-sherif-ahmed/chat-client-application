package main;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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

    @FXML
    private VBox groupsVBox;

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
                        updateGroupList();
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
            myScreen.server_out.write("clients_online\n");
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
                final String finalString = "Username : " + userData[1] + " ID : " + userData[0];
                System.out.println(userData[2].substring(1));

                myScreen.connectedUsers.add(new User(userData[0], userData[1], userData[2].substring(1), userData[3], userData[4]));
                Platform.runLater(
                        () -> {
                            // Update UI here.
                            ChatSelectorButton csb = new ChatSelectorButton(finalString, Integer.parseInt(userData[0]), 'c');
                            csb.setOnAction(generalButtonActionHandler());
                            usersVBox.getChildren().add(csb);
                        }
                );
            }
//            userDisplay.setText(finalString);
//            System.out.println(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGroupList(){
        try {
            myScreen.server_out.write("group_available\n");
            myScreen.server_out.flush();
            String s = myScreen.server_in.readLine();
            if(s.equals("empty_groups")){
                return;
            }
            String[] groups = s.split(";");
//            String finalString = "";
            Platform.runLater(
                    () -> {
                        // Update UI here.
                        groupsVBox.getChildren().clear();
                    }
            );
            for (String u :
                    groups) {
                String[] groupData = u.split(",");
                final String finalString = "Group ID : " + groupData[0] + " Group Name : " + groupData[1];
                myScreen.availableGroupChats.add(new GroupChatClient(Integer.parseInt(groupData[0]), groupData[1]));
                //myScreen.connectedUsers.add(new User(userData[0], userData[1], userData[2].substring(1), userData[3], userData[4]));
                Platform.runLater(
                        () -> {
                            // Update UI here.
                            ChatSelectorButton csb = new ChatSelectorButton(finalString, Integer.parseInt(groupData[0]), 'g');
                            csb.setOnAction(generalButtonActionHandler());
                            groupsVBox.getChildren().add(csb);
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


    void startChat(String id) {
        User desiredUser = myScreen.connectedUsers.get(0);
        for (User u :
                myScreen.connectedUsers) {
            if(u.getId().equals(id)) {
                desiredUser = u;
                break;
            }
        }
        try {
            myScreen.peer_socket = new Socket(desiredUser.getIp(),Integer.parseInt(desiredUser.getTx()));
            myScreen.peer_out =  new BufferedWriter(new OutputStreamWriter(myScreen.peer_socket.getOutputStream()));
            myScreen.peer_in =  new BufferedReader(new InputStreamReader(myScreen.peer_socket.getInputStream()));
            myScreen.peer_out.write(myScreen.clientName + "\n");
            myScreen.peer_out.flush();
            FXMLLoader loadedChat = myScreen.loadScreen(desiredUser.getName(), "/ChatView.fxml");
            ChatView loadedChatView = loadedChat.getController();
            loadedChatView.setChatName(desiredUser.getName());
            loadedChatView.setChatIn(myScreen.peer_in);
            loadedChatView.setChatOut(myScreen.peer_out);
            loadedChatView.chatService.start();
            myScreen.addChatToMenu(desiredUser.getName());
            myScreen.setScreen(desiredUser.getName());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startGroupChat(int id) {
        GroupChatClient desiredGroup = myScreen.availableGroupChats.get(0);
        for (GroupChatClient u :
                myScreen.availableGroupChats) {
            if(u.getId() == id) {
                desiredGroup = u;
                break;
            }
        }
        FXMLLoader loadedChat = myScreen.loadScreen(desiredGroup.getGroupName(), "/ChatView.fxml");
        ChatView loadedChatView = loadedChat.getController();
        loadedChatView.setChatName(desiredGroup.getGroupName());
        loadedChatView.setChatIn(myScreen.server_in);
        loadedChatView.setChatOut(myScreen.server_out);
        loadedChatView.groupChatNumber = "" + desiredGroup.getId();
        loadedChatView.chatService.start();
        myScreen.addChatToMenu(desiredGroup.getGroupName());
        myScreen.setScreen(desiredGroup.getGroupName());
    }

    public void createGroupChat() {
        try{
            myScreen.server_out.write("group_create\n");
            myScreen.server_out.flush();
            myScreen.server_out.write("TEST !!!\n");
            myScreen.server_out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public EventHandler<ActionEvent> generalButtonActionHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(((ChatSelectorButton)event.getSource()).getType() == 'g'){
                    startGroupChat(((ChatSelectorButton)event.getSource()).getModelId());
                } else {
                    startChat("" + ((ChatSelectorButton)event.getSource()).getModelId());
                }
            }
        };
    }

//    public void generalButtonActionHandler(ActionEvent ae){
//        if(((ChatSelectorButton)ae.getSource()).getType() == 'g'){
//
//        } else {
//            startChat(null);
//        }
//    }

}
