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
        myScreen.clientName = txtNickname.getText();
        myScreen.initServerConnection(txtServerIP.getText(), txtNickname.getText());
        myScreen.loadScreen("Lobby","/Lobby.fxml");
        MenuItem goToLobby = new MenuItem("Go To Lobby");
        goToLobby.setOnAction(actionEvent -> {
            myScreen.setScreen("Lobby");
        });
        myScreen.clientMenu.getItems().add(goToLobby);
        myScreen.setScreen("Lobby");
    }

}
