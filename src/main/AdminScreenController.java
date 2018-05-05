package main;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class AdminScreenController implements ControlledScreen, Initializable {

    private ScreensController myController;

    @FXML
    private JFXTextArea txtAreaClientList;

    @FXML
    private JFXTextField txtUserID;

    @FXML
    void kickClient(ActionEvent event) {
        try{
            myController.server_out.write("kick_client\n");
            myController.server_out.flush();
            myController.server_out.write(txtUserID.getText() + "\n");
            myController.server_out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    void exit(){
        Platform.exit();
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateUserListService uuls = new updateUserListService();
        uuls.start();
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
                            String s = myController.server_in.readLine();
                            System.out.println(s);
                            StringTokenizer stringTokenizer = new StringTokenizer(s, "%");
                            String operation = stringTokenizer.nextToken();

                            if (operation.equals("clients_online")){
                                if(!stringTokenizer.hasMoreTokens()){
                                    Platform.runLater(
                                        () -> {
                                            txtAreaClientList.setText("");
                                        }
                                    );
                                    continue;
                                }
                                String users = stringTokenizer.nextToken();
                                StringTokenizer s_tknzr = new StringTokenizer(users, ";");
                                Platform.runLater(
                                        () -> {
                                            txtAreaClientList.setText("");
                                        }
                                );
                                Platform.runLater(() -> {
                                    while(s_tknzr.hasMoreTokens()){
                                        String inMsg = s_tknzr.nextToken();
                                        String[] userData = inMsg.split(",");
                                        final String finalString = "Username : " + userData[1] + " ID : " + userData[0];
                                        txtAreaClientList.setText(txtAreaClientList.getText() + "\n" + finalString);
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
    }
}
