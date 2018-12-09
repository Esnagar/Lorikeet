package sample;
//hola

import javafx.application.Platform;
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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;
import static sample.ChatMessage.CIPHERMESSAGE;
import static sample.Client.encriptarMensaje;
import static sample.Client.encriptarMensajeBytes;
import static sample.Client.getClaveAES;
import static sample.RegistroLogin.encriptarconPublica;
import static sample.RegistroLogin.get_SHA_256_SecurePassword;

public class RegistroLoginController implements Initializable{


    public static Client client;

    public static Button enviar;

    @FXML
    private TextField nickname;
    @FXML
    private PasswordField passlog;
    @FXML
    private Button login;
    @FXML
    private Hyperlink registro;
    @FXML
    private Label fallido;
    @FXML
    private Label acercatexto;
    @FXML
    private Label serverlabel;
    @FXML
    private TextField serverip;
    @FXML
    private Label ayudatexto;
    @FXML
    private javafx.scene.image.ImageView pajaro;

    public RegistroLogin clientlog;
    public boolean loaded = false;
    public boolean preparado = false;

    public static boolean granted=false;

    private boolean connected;
    private int defaultPort;
    private String defaultHost;
    public static String user;
    public static String ip;

    public static javafx.event.ActionEvent actionEventaux;

    public RegistroLoginController() {
        super();
    }

    public RegistroLoginController(String host, int port, String user) {
        //super("Chat Client");
        this.defaultPort = port;
        this.defaultHost = host;
        this.user = user;

    }

    public void login(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    public void registroo(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("registro.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();

        RegistroController cg = new RegistroController("localhost", 1501,"");
        Main.clientlog = new RegistroLogin(RegistroController.ip, 1501,"" , cg);
        Main.clientlog.start();
        cg.asignarCliente();

    }

    public void chat(javafx.event.ActionEvent actionEvent) throws IOException {

        // to start the whole thing the server
        if(granted==true) {
            Parent loginParent=null;
            loginParent = FXMLLoader.load(getClass().getResource("chat.fxml"));
            Scene loginScene = new Scene(loginParent, 600, 400);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(loginScene);
            window.show();

            ChatController cc = new ChatController("localhost", 1500, user);
            client = new Client(ip, 1500, user, cc);
            //client.start();
            cc.asignarCliente();


            //cc.setClient(client);
            //commit suicide
        }
        if(granted==false){
            fallido=clientlog.fallido;
            asignarCliente();
            fallido.setOpacity(1.0);
        }

    }
    public void adelante() throws IOException{
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try{
        chat(actionEventaux);}
                catch (IOException ex){
                }
            }

        });
    }

    public void iniciar(javafx.event.ActionEvent actionEvent) throws IOException {
        if (!nickname.getText().isEmpty() && !passlog.getText().isEmpty()) {
            user=nickname.getText();
            ip=RegistroController.ip;
            append("LOGIN: "+nickname.getText()+" "+passlog.getText());
        }
        actionEventaux=actionEvent;
    }

    public void serverpass(){
        asignarCliente();
        serverlabel.setOpacity(1.0);
        serverip.setOpacity(1.0);
        serverip.setDisable(false);
    }

    public void acerca(){
        asignarCliente();
        pajaro.setOpacity(0);
        ayudatexto.setOpacity(0);
        acercatexto.setOpacity(1.0);
    }
    public void ayuda(){
        asignarCliente();
        pajaro.setOpacity(0);
        acercatexto.setOpacity(0);
        ayudatexto.setOpacity(1.0);
    }
    public void repajaro(){
        asignarCliente();
        acercatexto.setOpacity(0);
        ayudatexto.setOpacity(0);
        pajaro.setOpacity(1);
    }

    public void append(String msg) {
        while (!preparado) {
            if (loaded)
                preparado = true;
        }

        if (loaded) {
            preparado = true;

            clientlog.fallido=fallido;
            comprobarMensaje(msg);



        }

    }
    public void comprobarMensaje(String msg){
        asignarCliente();

        if (msg.equalsIgnoreCase("LOGOUT")) {
            clientlog.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            // break to do the disconnect

        }
        if(msg.contains("LOGIN")){
            RegistroLogin.trozos = msg.split(" ");

            RegistroLogin.trozos[1]=encriptarconPublica(RegistroLogin.trozos[1]);
            clientlog.sendMessage(new ChatMessage(ChatMessage.MESSAGE, RegistroLogin.trozos[1]));

        }
        if(msg.contains("REGISTRO")){
            RegistroLogin.trozos = msg.split(" ");
            byte[] salt=null;
            if(RegistroLogin.trozos[2].equalsIgnoreCase(RegistroLogin.trozos[3])){
                try{
                    salt = RegistroLogin.getSalt();
                    System.out.println(Base64.getEncoder().encodeToString(salt));
                }
                catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }
                RegistroLogin.trozos[2] = get_SHA_256_SecurePassword(RegistroLogin.trozos[2], salt);
                RegistroLogin.trozos[1]=encriptarconPublica(RegistroLogin.trozos[1]);
                RegistroLogin.trozos[2]=encriptarconPublica(RegistroLogin.trozos[2]+Base64.getEncoder().encodeToString(salt));
                clientlog.sendMessage(new ChatMessage(ChatMessage.REGISTRO, RegistroLogin.trozos[1],RegistroLogin.trozos[2]));
            }
        }

    }

    public void asignarCliente() {
        this.clientlog = RegistroController.clientlogin;
        System.out.println("Cliente: " + clientlog);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loaded = true;
    }
}
