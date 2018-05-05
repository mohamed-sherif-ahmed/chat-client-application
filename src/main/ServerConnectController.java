package main;


import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class ServerConnectController implements ControlledScreen{
    ScreensController myScreen;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        this.myScreen = screenPage;
    }

    @FXML
    private JFXTextField txtServerIP;

    @FXML
    private JFXTextField txtNickname;

    @FXML
    void connectServer(ActionEvent event) {
        ScreensController.clientName = txtNickname.getText();
        myScreen.initServerConnection(txtServerIP.getText(), txtNickname.getText());
        if(txtNickname.getText().equals("admoon")){
            myScreen.loadScreen("admoon", "/AdminScreeen.fxml");
            myScreen.setScreen("admoon");
        }else {
            myScreen.loadScreen("Lobby","/Lobby.fxml");
            myScreen.setScreen("Lobby");
        }
    }
}
