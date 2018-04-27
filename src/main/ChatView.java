package main;



import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatView {
    BufferedReader in;
    BufferedWriter out;
    @FXML
    private JFXTextField serverIP;

    @FXML
    private JFXTextField userConnect;

    @FXML
    private JFXTextArea screen;

    @FXML
    private JFXTextArea type;

    @FXML
    private JFXTextArea userDisplay;

    @FXML
    void connect(ActionEvent event) {

    }



    @FXML
    void login(ActionEvent event) {
        try {
            Socket socket = new Socket(serverIP.getText(), 50000);
            ClientListener cl = new ClientListener();
            cl.start();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String a = "Amr;" +socket.getLocalAddress().toString()+";"+socket.getLocalPort()+";"+cl.getServerSocket().getLocalPort();
            out.write(a + "\n");
            out.flush();
            String s = "";
        }
            catch (Exception e){
                e.printStackTrace();
            }

    }

    @FXML
    void displayUsers(ActionEvent event) {
        try{
            out.write("info\n");
            out.flush();
            String s = in.readLine();
            userDisplay.setText(s);
            System.out.println(s);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    void send(ActionEvent event) {

    }

}
