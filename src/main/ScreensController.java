package main;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

/**
 * Created by mohamedsherif on 12/29/16.
 */
public class ScreensController extends StackPane {
    private HashMap<String, Node> screens = new HashMap<>();

    BufferedReader server_in;
    BufferedWriter server_out;

    Socket peer_socket;
    BufferedReader peer_in;
    BufferedWriter peer_out;

    ServerSocket clientInSocket;

    ArrayList<User> connectedUsers = new ArrayList<>();


    ClientListener cl;

    class ClientMessageListener extends Service {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Object call() throws Exception {

                    while (true) {
                        Socket sk = clientInSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sk.getOutputStream()));
                        String initInbound = in.readLine();
                        FXMLLoader loadedChat = loadScreen(initInbound, "/ChatView.fxml");
                        ChatView loadedChatView = loadedChat.getController();
                        loadedChatView.setChatIn(in);
                        loadedChatView.setChatOut(out);
                        loadedChatView.chatService.start();
                        setScreen(initInbound);
                    }
                }
            };
        }
    }

        public ArrayList<User> getConnectedUsers() {
            return connectedUsers;
        }

        public void updateUserList() {
            try {
                server_out.write("info\n");
                server_out.flush();
                String s = server_in.readLine();
                String[] users = s.split(";");
                String finalString = "";
                for (String u :
                        users) {
                    String[] userData = u.split(",");
                    finalString += "Username : " + userData[0] + " ID : " + userData[1] + "\n";
                    System.out.println(userData[2].substring(1));
                    connectedUsers.add(new User(userData[0], userData[1], userData[2].substring(1), userData[3], userData[4]));
                }
//            userDisplay.setText(finalString);
//            System.out.println(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void initServerConnection(String ip, String nickname) {
            try {
                Socket socket = new Socket(ip, 50000);
//                cl = new ClientListener();
//                cl.start();
                ClientMessageListener clientMessageListener = new ClientMessageListener();
                clientInSocket = new ServerSocket(0);
                server_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                server_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String a = nickname + ";" + socket.getLocalAddress().toString() + ";" + socket.getLocalPort() + ";" + clientInSocket.getLocalPort();
                System.out.println(a);
                server_out.write(a + "\n");
                server_out.flush();
                String s = "";
                clientMessageListener.start();
            } catch (Exception e) {

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
    }

