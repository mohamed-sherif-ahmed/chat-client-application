package main;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class LobbyController implements ControlledScreen, Initializable {
    ScreensController myScreen;

    @FXML
    private VBox usersVBox;

    @FXML
    private JFXTextField chatName;

    @FXML
    private VBox groupsVBox;

    @FXML
    private TabPane chatsTabPane;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Label txtUser;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        this.myScreen = screenPage;
    }

    private HashMap<String, ChatView> groupChatController = new HashMap<>();
    private HashMap<String, ChatView> p2pChatController = new HashMap<>();

    class ClientMessageListener extends Service {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Object call() throws Exception {

                    while (true) {
                        System.out.println("ACCEPTING CLIENT !");
                        Socket sk = myScreen.clientInSocket.accept();
                        System.out.println("CLIENT ACCEPTED !");
                        BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sk.getOutputStream()));
                        String initInbound = in.readLine();
//                        FXMLLoader loadedChat = myScreen.loadScreen(initInbound, "/ChatView.fxml");
//                        ChatView loadedChatView = loadedChat.getController();
//                        loadedChatView.setChatName(initInbound);
//                        loadedChatView.setChatIn(in);
//                        loadedChatView.setChatOut(out);
//                        loadedChatView.chatService.start();
//                        myScreen.setScreen(initInbound);


                        System.out.println("Hereadasdsadasda");
                        User uUS = null;
                        for(User us: myScreen.connectedUsers){
                            if(us.getName().equals(initInbound)){
                                uUS = us;
                            }
                        }
                        if(uUS != null){
                            if(p2pChatController.containsKey(uUS.getId())){
                                continue;
                            }
                        }
                        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
                        Parent loadScreen = (Parent) myLoader.load();
                        ChatView loadedChatView = myLoader.getController();
                        loadedChatView.setChatName(initInbound);
                        loadedChatView.setChatIn(in);
                        loadedChatView.setChatOut(out);
                        loadedChatView.chatService.start();
                        loadedChatView.disableLeaveBtn();
                        p2pChatController.put(uUS.getId(), loadedChatView);
                        Tab nChatTab = new Tab();
                        nChatTab.setContent(loadScreen);
                        nChatTab.setText(initInbound);
                        Platform.runLater( () -> {
                            chatsTabPane.getTabs().add(nChatTab);
                        });
//                        addChatToMenu(initInbound);
                    }
                }
            };
        }
    }

    class updateUserListService extends Service {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Object call() throws Exception {
                    while (true) {
                        try{
                            System.out.println("Reading");
                            String s = myScreen.server_in.readLine();
                            System.out.println(s);
                            StringTokenizer stringTokenizer = new StringTokenizer(s, "%");
                            String operation = stringTokenizer.nextToken();
                            if(!stringTokenizer.hasMoreTokens()){
                                continue;
                            }
                            if (operation.equals("clients_online")){
                                updateUserList(stringTokenizer.nextToken() + ";");
                            } else if (operation.equals("group_available")){
                                updateGroupList(stringTokenizer.nextToken() + ";");
                            } else if (operation.equals("group_msg")){
                                System.out.println("In Group Msg");
                                String[] grpMessage = stringTokenizer.nextToken().split(",");

                                ChatView d = groupChatController.get(grpMessage[0]);
                                Platform.runLater( () -> {
                                    d.updateGroupChatMessage(grpMessage[1], grpMessage[2]);
                                });
                            } else if ( operation.equals("kick")){
                                System.out.println("jnljnjndslknklslknslknvlknkkk");
                                Platform.exit();
                            } else if(operation.equals("group_leave")){
                                String g_id = stringTokenizer.nextToken();
                                groupChatController.get(g_id);
                                GroupChatClient gxx = null;
                                for(GroupChatClient gcc: myScreen.availableGroupChats){
                                    if (gcc.getId() == Integer.parseInt(g_id)){
                                        gxx = gcc;
                                    }
                                }
                                myScreen.availableGroupChats.remove(gxx);
                                Tab tt = null;
                                for(Tab t: chatsTabPane.getTabs()){
                                    if(t.getText().equals(gxx.getGroupName())){
                                        tt = t;
                                    }
                                }
                                chatsTabPane.getTabs().remove(tt);
                                groupChatController.remove(g_id);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }

    public void updateUserList(String users) {
        System.out.println("UPDATING...!");
        try {
            StringTokenizer s_tknzr = new StringTokenizer(users, ";");
            Platform.runLater(
                    () -> {
                        // Update UI here.
                        usersVBox.getChildren().clear();
                    }
            );
            Platform.runLater(() -> {
                while(s_tknzr.hasMoreTokens()){
                    String inMsg = s_tknzr.nextToken();
                    System.out.println(inMsg);
                    String[] userData = inMsg.split(",");
                    final String finalString = "Username : " + userData[1] + " ID : " + userData[0];
                    System.out.println(userData[2].substring(1));

                    myScreen.connectedUsers.add(new User(userData[0], userData[1], userData[2].substring(1), userData[3], userData[4], userData[5]));
                    ChatSelectorButton csb = new ChatSelectorButton(userData[1] + ", " + userData[5], Integer.parseInt(userData[0]), 'c');
                    csb.setOnAction(generalButtonActionHandler());
                    usersVBox.getChildren().add(csb);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGroupList(String group){
        try {
            StringTokenizer s_tknzr = new StringTokenizer(group, ";");
            Platform.runLater(
                    () -> {
                        // Update UI here.
                        groupsVBox.getChildren().clear();
                    }
            );
            Platform.runLater(() -> {
                while(s_tknzr.hasMoreTokens()) {
                    String[] groupData = s_tknzr.nextToken().split(",");
                    final String finalString = "Group ID : " + groupData[0] + " Group Name : " + groupData[1];
                    myScreen.availableGroupChats.add(new GroupChatClient(Integer.parseInt(groupData[0]), groupData[1]));
                    //myScreen.connectedUsers.add(new User(userData[0], userData[1], userData[2].substring(1), userData[3], userData[4]));
                    ChatSelectorButton csb = new ChatSelectorButton(groupData[1], Integer.parseInt(groupData[0]), 'g');
                    csb.setOnAction(generalButtonActionHandler());
                    groupsVBox.getChildren().add(csb);
//                    Platform.runLater(
//                            () -> {
//                                // Update UI here.
//
//                            }
//                    );
                }
            });
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
        ClientMessageListener cml = new ClientMessageListener();
        cml.start();
        txtUser.setText(ScreensController.clientName);
        Platform.runLater(() -> {
            statusComboBox.getItems().add(0, "Online");
            statusComboBox.getItems().add(1, "Busy");
            statusComboBox.getItems().add(2, "Away");
            statusComboBox.getSelectionModel().select(0);
            statusComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    try{
                        if(oldValue.equals(newValue)){
                            return;
                        }
                        myScreen.server_out.write("change_status\n");
                        myScreen.server_out.flush();
                        myScreen.server_out.write(newValue +"\n");
                        myScreen.server_out.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        });
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
            if (p2pChatController.containsKey("" + desiredUser.getId())){
                return;
            }
            myScreen.peer_socket = new Socket(desiredUser.getIp(),Integer.parseInt(desiredUser.getTx()));
            myScreen.peer_out =  new BufferedWriter(new OutputStreamWriter(myScreen.peer_socket.getOutputStream()));
            myScreen.peer_in =  new BufferedReader(new InputStreamReader(myScreen.peer_socket.getInputStream()));
            myScreen.peer_out.write(ScreensController.clientName + "\n");
            myScreen.peer_out.flush();
//            FXMLLoader loadedChat = myScreen.loadScreen(desiredUser.getName(), "/ChatView.fxml");
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent loadScreen = (Parent) myLoader.load();
            ChatView loadedChatView = myLoader.getController();
            loadedChatView.setChatName(desiredUser.getName());
            loadedChatView.setChatIn(myScreen.peer_in);
            loadedChatView.setChatOut(myScreen.peer_out);
            loadedChatView.disableLeaveBtn();
            loadedChatView.chatService.start();
            p2pChatController.put("" + desiredUser.getId(), loadedChatView);
            Tab nChatTab = new Tab();
            nChatTab.setContent(loadScreen);
            nChatTab.setText(desiredUser.getName());
            chatsTabPane.getTabs().add(nChatTab);
//            myScreen.addChatToMenu(desiredUser.getName());
//            myScreen.setScreen(desiredUser.getName());
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
        try {
            if (groupChatController.containsKey("" + desiredGroup.getId())) {
                return;
            }
            myScreen.server_out.write("group_join\n");
            myScreen.server_out.flush();
            myScreen.server_out.write(id + "\n");
            myScreen.server_out.flush();
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
            Parent loadScreen = (Parent) myLoader.load();
            ChatView loadedChatView = myLoader.getController();
            loadedChatView.setChatName(desiredGroup.getGroupName());
            loadedChatView.setChatIn(myScreen.server_in);
            loadedChatView.setChatOut(myScreen.server_out);
            loadedChatView.groupChatNumber = "" + desiredGroup.getId();
//            loadedChatView.chatService.start();
            groupChatController.put("" + desiredGroup.getId(), loadedChatView);
            Tab nChatTab = new Tab();
            nChatTab.setContent(loadScreen);
            nChatTab.setText(desiredGroup.getGroupName());
            chatsTabPane.getTabs().add(nChatTab);
        } catch(IOException e){
            e.printStackTrace();
        }

//        myScreen.addChatToMenu(desiredGroup.getGroupName());
        myScreen.setScreen(desiredGroup.getGroupName());
    }

    public void createGroupChat() {
        try{
            myScreen.server_out.write("group_create\n");
            myScreen.server_out.flush();
            myScreen.server_out.write(chatName.getText() + "\n");
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

    public void exit(){
        Platform.exit();
    }

//    public void generalButtonActionHandler(ActionEvent ae){
//        if(((ChatSelectorButton)ae.getSource()).getType() == 'g'){
//
//        } else {
//            startChat(null);
//        }
//    }

}
