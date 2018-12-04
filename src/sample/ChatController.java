package sample;
//hola

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
    public boolean loaded = false;
    public boolean preparado = false;

    @FXML
    public TextArea mensajeChat;
    public ImageView index;

    @FXML
    public Pane fondoChat;

    @FXML
    public AnchorPane zonaMensajes;
    public Button enviar;
    public ArrayList<TextArea> mensajes = new ArrayList<TextArea>();
    public ScrollBar scrollBarra;


    public void login(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    private Label label;
    @FXML
    private TextField tf;
    private TextField tfServer, tfPort;
    @FXML
    private Button login, logout, whoIsIn;
    private TextArea ta;
    private boolean connected;
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
    }

    // called by the Client to append text in the TextArea
    public void enviar(ActionEvent actionEvent) throws IOException {
        if (!mensajeChat.getText().isEmpty()) {
            append(mensajeChat.getText());
        }
        //ta.append(str);
        //ta.setCaretPosition(ta.getText().length() - 1);
    }

    public void appendisplay(String msg) {
        TextArea nuevoMensaje = new TextArea(msg);

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

    public void append(String msg) {
        while (!preparado) {
            if(loaded)
                preparado = true;
        }

        if (loaded) {
            preparado = true;

            TextArea nuevoMensaje = new TextArea(msg);
            comprobarMensaje(msg);

            nuevoMensaje.setPrefWidth(255);
            nuevoMensaje.setPrefHeight(30);
            nuevoMensaje.maxWidth(255);
            nuevoMensaje.setDisable(false);
            nuevoMensaje.setWrapText(true);
            scrollBarra.setDisable(true);

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

    }

    public void appendisplay(String msg) {
        while (!preparado) {
            if(loaded)
                preparado = true;
        }

        if (loaded) {
            preparado = true;

            TextArea nuevoMensaje = new TextArea(msg);
            nuevoMensaje.setPrefWidth(255);
            nuevoMensaje.setPrefHeight(30);
            nuevoMensaje.maxWidth(255);
            nuevoMensaje.setDisable(false);
            nuevoMensaje.setWrapText(true);
            scrollBarra.setDisable(true);

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

    }


    public void comprobarMensaje(String msg) {
        asignarCliente();

        if (msg.equalsIgnoreCase("LOGOUT")) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        } else if (msg.equalsIgnoreCase("WHOISIN")) {
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


    public void enviarIntro(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            enviar.fire();
        }
    }

    public void asignarCliente() {
        this.client = RegistroLoginController.client;
        System.out.println("Cliente: " + client);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loaded = true;
    }
}
