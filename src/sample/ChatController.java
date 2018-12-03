package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.ResourceBundle;

import sample.Client;

import static sample.ChatMessage.CIPHERMESSAGE;
import static sample.Client.encriptarMensaje;
import static sample.Client.encriptarMensajeBytes;
import static sample.Client.getClaveAES;

public class ChatController implements Initializable {
    public Client client;
    public TextArea mensajeChat;
    public ImageView index;
    public Pane fondoChat;
    public AnchorPane zonaMensajes;
    public Button enviar;
    public ArrayList<TextArea> mensajes = new ArrayList<TextArea>();

    public void login(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    // will first hold "Username:", later on "Enter message"
    private Label label;
    // to hold the Username and later on the messages
    @FXML
    private TextField tf;
    // to hold the server address an the port number
    private TextField tfServer, tfPort;
    // to Logout and get the list of the users
    @FXML
    private Button login, logout, whoIsIn;

    // for the chat room
    private TextArea ta;
    // if it is for connection
    private boolean connected;
    // the Client object
    // the default port number
    private int defaultPort;
    private String defaultHost;

    public ChatController() {
        super();
    }

    // Constructor connection receiving a socket number
    public ChatController(String host, int port, String user) {
        //super("Chat Client");
        this.defaultPort = port;
        this.defaultHost = host;

        client = new Client(host, port, user, this);
        client.start();
        System.out.println(client);
    }

    // called by the Client to append text in the TextArea
    public void enviar(ActionEvent actionEvent) throws IOException {
        if (!mensajeChat.getText().isEmpty()) {
            append(mensajeChat.getText());
        }
        //ta.append(str);
        //ta.setCaretPosition(ta.getText().length() - 1);
    }

    public void append(String msg) {
        TextArea nuevoMensaje = new TextArea(msg);
        comprobarMensaje(msg);

        nuevoMensaje.setPrefWidth(255);
        nuevoMensaje.setPrefHeight(30);
        nuevoMensaje.maxWidth(255);
        nuevoMensaje.setDisable(false);
        nuevoMensaje.setWrapText(true);

        ScrollBar scrollBarv = (ScrollBar) mensajeChat.lookup(".scroll-bar:vertical");
        scrollBarv.setDisable(true);

        int y = 230;
        int x = 200;

        //Su posición inicial
        nuevoMensaje.setTranslateX(x);
        nuevoMensaje.setTranslateY(y);

        mensajes.add(0, nuevoMensaje); //Es el mensaje más reciente

        zonaMensajes.getChildren().clear(); //Borramos all para actualizar

        for (TextArea mensaje : mensajes) {
            mensaje.setTranslateY(y); //Desplazamos los mensajes hacia arriba
            zonaMensajes.getChildren().add(mensaje);
            y -= 60;
        }

        mensajeChat.clear();

    }


    public void comprobarMensaje(String msg) {
        if (msg.equalsIgnoreCase("LOGOUT")) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        }

        else if (msg.equalsIgnoreCase("WHOISIN")) {
            client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));

        } else if (msg.contains("FILE")) {
            String msg2 = msg.substring(6, msg.length());
            msg2 = msg2.substring(msg2.lastIndexOf("\\"));
            msg2 = encriptarMensaje(msg2);
            ChatMessage cosa = new ChatMessage(ChatMessage.FILE, msg2);
            String archivo = msg.substring(6, msg.length());
            File f = new File(archivo);
            byte[] content = null;
            try {
                content = Files.readAllBytes(f.toPath());
            } catch (IOException ex) {
                System.out.println("Problema con el archivo");
            }
            if (getClaveAES() != null) {
                content = encriptarMensajeBytes(content);
            }
            cosa.setContenido(content);
            client.sendMessage(cosa);

        } else {
            if (getClaveAES() != null) {
                msg = encriptarMensaje("Esther" + ": " + msg);
            }
            if (client == null) {
                System.out.println("problemitas");
            }

            client.sendMessage(new ChatMessage(CIPHERMESSAGE, msg));
        }
    }

    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    void connectionFailed() {
        // reset port number and host name as a construction time
        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);
        connected = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void enviarIntro(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            enviar.fire();
        }
    }
}
