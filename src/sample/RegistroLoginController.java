package sample;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class RegistroLoginController {

    public TextField nickname;

    public void login (javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void registro (javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("registro.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void chat (javafx.event.ActionEvent actionEvent) throws IOException {
        // to start the whole thing the server
        ChatController cc = new ChatController("localhost", 1500, nickname.getText());

        Parent loginParent = FXMLLoader.load(getClass().getResource("chat.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }
}
