package sample;
//holaa

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
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
    public FlowPane zonaMensajes;
    public Button enviar;
    public ArrayList<TextArea> mensajes = new ArrayList<TextArea>();
    public ScrollPane scrollBarra;
    public boolean dentro = false;
    public Button archivo;

    public void logout(MouseEvent mouseEvent) throws IOException {
        client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));


        Parent loginParent = FXMLLoader.load(getClass().getResource("registro.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node)  mouseEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();

        RegistroController cg = new RegistroController("localhost", 1501,"");
        Main.clientlog = new RegistroLogin("localhost", 1501,"" , cg);
        Main.clientlog.start();
        cg.asignarCliente();
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
    private String user;

    public ChatController() {
        super();
    }

    // Constructor connection receiving a socket number
    public ChatController(String host, int port, String user) {
        //super("Chat Client");
        this.defaultPort = port;
        this.defaultHost = host;
        this.user = user;

    }

    // called by the Client to append text in the TextArea
    public void enviar(ActionEvent actionEvent) throws IOException {
        if (!mensajeChat.getText().isEmpty()) {
            append(mensajeChat.getText());
        }
    }

    public void append(String msg) {
        while (!preparado) {
            if (loaded)
                preparado = true;
        }

        if (loaded) {
            preparado = true;

            TextArea nuevoMensaje = new TextArea(msg);
            comprobarMensaje(msg);

            mensajeChat.clear();
            client.zonaMensajes = zonaMensajes;
            client.mensajes = mensajes;
            client.scrollBarra = scrollBarra;
            client.mensajeChat = mensajeChat;
            client.enviar = enviar;
            client.archivo=archivo;

        }

    }

    public void appendisplay(String msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                zonaMensajes = client.zonaMensajes;
                mensajes = client.mensajes;
                scrollBarra = client.scrollBarra;
                mensajeChat = client.mensajeChat;
                enviar = client.enviar;
                archivo=client.archivo;

                //Saber si es tu mensaje o de otro usuario
                String[] info = msg.split(": ", 2);
                int x = -10;
                if (!info[0].equalsIgnoreCase(client.getUsername())) {
                    x = -210;
                }

                //Saber si es un archivo o un mensaje normal
                if (info[1].equalsIgnoreCase(client.getComprobarArchivo())) {

                    Image imagenAux = new Image("file:C:/LorikeetFiles/" + info[1]);

                    TextArea usuario = new TextArea(info[0] + ": ");
                    usuario.setPrefHeight(29);
                    usuario.setPrefWidth(255);
                    usuario.maxWidth(255);
                    usuario.maxHeight(29);
                    usuario.setWrapText(true);
                    usuario.setTranslateX(x);
                    usuario.setEditable(false);

                    if (imagenAux.getHeight() > 0) { //Es una imagen
                        ImageView previsualizacion = new ImageView();
                        Image image = new Image("file:C:/LorikeetFiles/" + info[1], 250, 250, false, true, true);

                        previsualizacion.setImage(image);
                        previsualizacion.setTranslateX(x);
                        zonaMensajes.getChildren().addAll(usuario, previsualizacion);

                    } else { //La altura de la imagen es 0 (es decir, es un pdf, o cualquier otro archivo)
                        usuario.setText(info[0] + ": ha enviado el archivo " + info[1]);
                        zonaMensajes.getChildren().add(usuario);
                    }

                } else {
                    TextArea nuevoMensaje = new TextArea(msg);

                    nuevoMensaje.setPrefHeight(29 * (msg.length() / 37 + 1));
                    System.out.println(msg.length());
                    System.out.println((msg.length() / 33));
                    System.out.println(29 * (msg.length() / 37 + 1));
                    nuevoMensaje.setPrefWidth(250);
                    nuevoMensaje.maxWidth(250);
                    nuevoMensaje.setWrapText(true);
                    nuevoMensaje.setTranslateX(x);
                    nuevoMensaje.setEditable(false);

                    zonaMensajes.getChildren().add(nuevoMensaje);
                    mensajeChat.clear();
                }

                //Separador entre mensajes
                TextArea separador = new TextArea();
                separador.setFont(Font.font(1));
                separador.setPrefWidth(250);
                separador.setPrefHeight(1);
                separador.setTranslateX(x);
                separador.setVisible(false);

                zonaMensajes.getChildren().add(separador);

                scrollBarra.applyCss();
                scrollBarra.layout();
                scrollBarra.setVvalue(1.0);
            }
        });

    }


    public void comprobarMensaje(String msg) {
        asignarCliente();

        if (msg.equalsIgnoreCase("LOGOUT")) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        } else if (msg.equalsIgnoreCase("WHOISIN")) {
            client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));

        } else if (msg.contains("FILE")) { //FILE: Esther: C:/kfdmklfdfmfdlksfmkfndlfkdf.jpg
            String path = msg.substring(6, msg.length()); //Esther: C:/kfdmklfdfmfdlksfmkfndlfkdf.jpg
            String[] aux = path.split(" ", 2);  //aux[0]=Esther:             aux[1]=C:/kfdmklfdfmfdlksfmkfndlfkdf.jpg
            String nombreArchivo = aux[1].substring(aux[1].lastIndexOf("\\"));
            nombreArchivo = encriptarMensaje(aux[0] + nombreArchivo);
            ChatMessage mensaje = new ChatMessage(ChatMessage.FILE, nombreArchivo);

            File f = new File(aux[1]);
            byte[] content = null;
            try {
                content = Files.readAllBytes(f.toPath());
            } catch (IOException ex) {
                System.out.println("Problema con el archivo");
            }
            if (getClaveAES() != null) {
                content = encriptarMensajeBytes(content);
            }
            mensaje.setContenido(content);
            client.sendMessage(mensaje);

        } else {
            if (getClaveAES() != null) {
                msg = encriptarMensaje(client.getUsername() + ": " + msg);
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

    public void asignarVariables() {


        RegistroLoginController.enviar = enviar;

    }

    public void activarBoton() {

        enviar = client.enviar;
        archivo=client.archivo;

        enviar.setDisable(false);
        archivo.setDisable(false);
        System.out.println("llega");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loaded = true;
    }


    public void inicializar(MouseEvent mouseEvent) {
        if (!dentro) {
            asignarCliente();
            while (!preparado) {
                if (loaded)
                    preparado = true;
            }

            if (loaded) {
                client.zonaMensajes = zonaMensajes;
                client.mensajes = mensajes;
                client.scrollBarra = scrollBarra;
                client.mensajeChat = mensajeChat;
                client.enviar = enviar;
                client.archivo=archivo;
                client.start();
            }
        }
        dentro = true;
    }

    public void subirFichero(ActionEvent actionEvent) throws IOException {
        asignarCliente();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un archivo");

        Stage stage = (Stage) zonaMensajes.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        //zonaMensajes.set
        System.out.println(file);
        comprobarMensaje("FILE: " + client.getUsername() + ": " + file.toString());
    }
}
