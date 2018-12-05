package sample;
//hola

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static sample.ChatMessage.CIPHERMESSAGE;
import static sample.Client.encriptarMensaje;
import static sample.Client.encriptarMensajeBytes;
import static sample.Client.getClaveAES;

public class RegistroLoginController {

    public TextField nickname;
    public static Client client;




    public void login(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void registro(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("registro.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void chat(javafx.event.ActionEvent actionEvent) throws IOException {
        // to start the whole thing the server
        ChatController cc = new ChatController("localhost", 1500, nickname.getText());
        client = new Client("localhost", 1500, nickname.getText(), cc);
        client.start();
        cc.asignarCliente();


        Parent loginParent = FXMLLoader.load(getClass().getResource("chat.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();


        //cc.setClient(client);
        //commit suicide
    }
}
