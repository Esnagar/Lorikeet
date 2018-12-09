package sample;
//hola
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.RegistroLoginController;

import java.io.File;

public class ServerMain extends Application {
    private Server server;
    private ServidorLog serverlog;

    public void start(Stage primaryStage) throws Exception{
       serverlog= new ServidorLog(1501);
        new ServerRunning1().start();
       server= new Server(1500);
        new ServerRunning2().start();
    }
    public class ServerRunning1 extends Thread {
        public void run() {
            serverlog.start();
        }
    }
    public class ServerRunning2 extends Thread {
        public void run() {
            server.start();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
