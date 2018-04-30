package main;

import com.jfoenix.controls.JFXButton;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class ChatSelectorButton extends JFXButton{
    private int id;
    private char type;

    public ChatSelectorButton(String text, int id, char type) {
        super(text);
        this.id = id;
        this.type = type;
        this.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 22));
    }

    public int getModelId() {
        return id;
    }

    public char getType() {
        return type;
    }
}
