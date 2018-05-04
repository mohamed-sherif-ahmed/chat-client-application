package main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mohamedsherif on 12/29/16.
 */
public class ScreensController extends StackPane {
    private HashMap<String, Node> screens = new HashMap<>();
    private StackPane mainStackPane = new StackPane(new AnchorPane());
    private MenuBar mainMenuBar = new MenuBar();
    private Menu chatMenu = new Menu("Available Chats");
    public Menu clientMenu;
    BufferedReader server_in;
    BufferedWriter server_out;

    Socket peer_socket;
    BufferedReader peer_in;
    BufferedWriter peer_out;

    ServerSocket clientInSocket;

    ArrayList<User> connectedUsers = new ArrayList<>();
    ArrayList<GroupChatClient> availableGroupChats = new ArrayList<>();


    ClientListener cl;
    static String clientName;

    public ScreensController() {
//        mainMenuBar.prefWidthProperty().bind(widthProperty());
//        clientMenu = new Menu("Client");
//        MenuItem exitMenuItem = new MenuItem("Exit");
//        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
//        clientMenu.getItems().add(exitMenuItem);
//        mainMenuBar.getMenus().addAll(clientMenu, chatMenu);
//        setTop(mainMenuBar);
//        setCenter(mainStackPane);
    }

//    class ClientMessageListener extends Service {
//        @Override
//        protected Task createTask() {
//            return new Task() {
//                @Override
//                protected Object call() throws Exception {
//
//                    while (true) {
//                        Socket sk = clientInSocket.accept();
//                        BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
//                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sk.getOutputStream()));
//                        String initInbound = in.readLine();
//                        FXMLLoader loadedChat = loadScreen(initInbound, "/ChatView.fxml");
//                        ChatView loadedChatView = loadedChat.getController();
//                        loadedChatView.setChatName(initInbound);
//                        loadedChatView.setChatIn(in);
//                        loadedChatView.setChatOut(out);
//                        loadedChatView.chatService.start();
//                        setScreen(initInbound);
////                        addChatToMenu(initInbound);
//                    }
//                }
//            };
//        }
//    }

        public ArrayList<User> getConnectedUsers() {
            return connectedUsers;
        }

        public void initServerConnection(String ip, String nickname) {
            try {
                Socket socket = new Socket(ip, 50000);
//                cl = new ClientListener();
//                cl.start();
                clientInSocket = new ServerSocket(0);
                server_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                server_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String a = nickname + ";" + socket.getLocalAddress().toString() + ";" + socket.getLocalPort() + ";" + clientInSocket.getLocalPort()  + ";" + "Online";
                server_out.write(a + "\n");
                server_out.flush();
                System.out.println(a);
                String s = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void addScreen(String name, Node screen) {
            screens.put(name, screen);
        }

        public FXMLLoader loadScreen(String name, String resource) {
            try {
                FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
                Parent loadScreen = (Parent) myLoader.load();
                ControlledScreen myScreenControler = ((ControlledScreen) myLoader.getController());
                myScreenControler.setScreenParent(this);
                addScreen(name, loadScreen);
                return myLoader;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        public boolean setScreen(final String name) {

            if (screens.get(name) != null) { //screen loaded
                final DoubleProperty opacity = opacityProperty();

                //Is there is more than one screen
                if (!getChildren().isEmpty()) {
                    Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)), new KeyFrame(new Duration(500), new EventHandler() {
                        @Override
                        public void handle(Event event) {
                            //remove displayed screen
                            getChildren().remove(0);
                            //add new screen
                            getChildren().add(0, screens.get(name));
                            Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }
                    }, new KeyValue(opacity, 0.0)));
                    fade.play();
                } else {
                    //no one else been displayed, then just show
                    setOpacity(0.0);
                    getChildren().add(screens.get(name));
                    Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(500), new KeyValue(opacity, 1.0)));
                    fadeIn.play();
                }
                return true;
            } else {
                System.out.println("screen hasn't been loaded!\n");
                return false;
            }
        }

        public boolean unloadScreen(String name) {
            if (screens.remove(name) == null) {
                System.out.println("Screen didn't exist");
                return false;
            } else {
                return true;
            }
        }

        public String getScreenName(Node screen){
            Iterator it = screens.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
//                System.out.println(pair.getKey() + " = " + pair.getValue());
//                it.remove(); // avoids a ConcurrentModificationException
                if (pair.getValue().equals(screen)){
                    return (String) pair.getKey();
                }
            }
            return "";
        }

        public void addChatToMenu(String screenName){
            MenuItem chatItem = new MenuItem(screenName);
            chatItem.setOnAction(actionEvent -> {
                setScreen(((MenuItem)actionEvent.getSource()).getText());
            });
            chatMenu.getItems().add(chatItem);
        }
    }

