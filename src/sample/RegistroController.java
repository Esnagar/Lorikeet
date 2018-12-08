package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.ResourceBundle;


import static sample.RegistroLogin.encriptarconPublica;
import static sample.RegistroLogin.get_SHA_256_SecurePassword;

public class RegistroController implements Initializable {

    public RegistroLogin client;
    public static RegistroLogin clientlogin;
    public boolean loaded = false;
    public boolean preparado = false;



    private boolean connected;
    private int defaultPort;
    private String defaultHost;
    private String user;

    @FXML
    private Button registro;
    @FXML
    private Button acerca;
    @FXML
    private TextField nombre;
    @FXML
    private PasswordField pass1,pass2;
    @FXML
    private Label resultado;
    @FXML
    private Label acercatexto;
    @FXML
    private Label ayudatexto;
    @FXML
    private Hyperlink login;
    @FXML
    private javafx.scene.image.ImageView pajaro;



    public RegistroController() {
        super();
    }

    public RegistroController(String host, int port, String user) {
        //super("Chat Client");
        this.defaultPort = port;
        this.defaultHost = host;
        this.user = user;

    }
    public void loginn(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginParent, 600, 400);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();

        RegistroLoginController ct = new RegistroLoginController("localhost", 1501,"");
        clientlogin = new RegistroLogin("localhost", 1501,"" , ct);
        clientlogin.start();
        ct.asignarCliente();

    }
    public void registrarse(ActionEvent actionEvent) throws IOException {
        Main.clientlog.start();
        if (!nombre.getText().isEmpty() && !pass1.getText().isEmpty() && !pass2.getText().isEmpty()) {
            append("REGISTRO: "+nombre.getText()+" "+pass1.getText()+" "+pass2.getText());
        }
    }

    public void append(String msg) {
        while (!preparado) {
            if (loaded)
                preparado = true;
        }

        if (loaded) {
            preparado = true;
            client.resultado=resultado;

            comprobarMensaje(msg);



        }

    }


    public void comprobarMensaje(String msg){
        asignarCliente();

        if (msg.equalsIgnoreCase("LOGOUT")) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            // break to do the disconnect

        }
        if(msg.contains("LOGIN")){
            RegistroLogin.trozos = msg.split(" ");

            RegistroLogin.trozos[1]=encriptarconPublica(RegistroLogin.trozos[1]);
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, RegistroLogin.trozos[1]));

        }
        if(msg.contains("REGISTRO")){
            RegistroLogin.trozos = msg.split(" ");
            byte[] salt=null;
            if(RegistroLogin.trozos[2].equals(RegistroLogin.trozos[3])){
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
                client.sendMessage(new ChatMessage(ChatMessage.REGISTRO, RegistroLogin.trozos[1],RegistroLogin.trozos[2]));
            }
            else{
                fracasillo();
            }
        }

    }
    public void exito(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
        resultado=client.resultado;
        asignarCliente();
        resultado.setText("Registro exitoso!");
        resultado.setLayoutX(390);
        resultado.setOpacity(1.0);
            }
        });
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
    public void fracasillo(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                resultado=client.resultado;
                asignarCliente();
                resultado.setText("Las contraseñas no coinciden");
                resultado.setLayoutX(360);
                resultado.setOpacity(1.0);
            }
        });
    }
    public void fracaso(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
        resultado=client.resultado;
        asignarCliente();
        resultado.setText("Usuario no disponible");
        resultado.setLayoutX(380);
        resultado.setOpacity(1.0);
            }
        });
    }

    public void asignarCliente() {
        this.client = Main.clientlog;
        System.out.println("Cliente: " + client);
    }

    void connectionFailed() {
        // reset port number and host name as a construction time
        //tfPort.setText("" + defaultPort);
        //tfServer.setText(defaultHost);
        connected = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loaded = true;
    }
}
