package main;


import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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
        myScreen.clientName = txtNickname.getText();
        myScreen.initServerConnection(txtServerIP.getText(), txtNickname.getText());
        myScreen.loadScreen("Lobby","/Lobby.fxml");
        myScreen.setScreen("Lobby");
    }

}
