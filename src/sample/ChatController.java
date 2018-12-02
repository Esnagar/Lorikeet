package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    public TextArea mensajeChat;
    public TextField output;
    public ImageView index;

    public void login (javafx.event.ActionEvent actionEvent) throws IOException {
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
    private Button login, logout, whoIsIn, enviar;

    private Pane pane;
    // for the chat room
    private TextArea ta;
    // if it is for connection
    private boolean connected;
    // the Client object
    private Client client;
    // the default port number
    private int defaultPort;
    private String defaultHost;

    public ChatController() { super(); };

    // Constructor connection receiving a socket number
    public ChatController(String host, int port) {
        System.out.println("Se crea");
        //super("Chat Client");
        this.defaultPort = port;
        this.defaultHost = host;
    }

    // called by the Client to append text in the TextArea
    public void append(ActionEvent actionEvent) throws IOException {
        output.setText(mensajeChat.getText());
        //ta.append(str);
        //ta.setCaretPosition(ta.getText().length() - 1);
    }


    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    void connectionFailed() {
        // reset port number and host name as a construction time
        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);
        connected = false;
    }

    /*
     * Button or JTextField clicked
     */
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        // if it is the Logout button
        if(o == logout) {
            client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
            return;
        }

        // ok it is coming from the JTextField
        if(connected) {
            // just have to send the message
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));
            tf.setText("");
            return;
        }


        if(o == login) {
            // ok it is a connection request
            String username = tf.getText().trim();
            // empty username ignore it
            if(username.length() == 0)
                return;
            // empty serverAddress ignore it
            String server = tfServer.getText().trim();
            if(server.length() == 0)
                return;
            // empty or invalid port numer, ignore it
            String portNumber = tfPort.getText().trim();
            if(portNumber.length() == 0)
                return;
            int port = 0;
            try {
                port = Integer.parseInt(portNumber);
            }
            catch(Exception en) {
                return;   // nothing I can do if port number is not valid
            }

            // try creating a new Client with GUI
            client = new Client(server, port, username, this);
            // test if we can start the Client
            if(!client.start())
                return;
            tf.setText("");
            label.setText("Enter your message below");
            connected = true;
            //tf.addActionListener(this);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
