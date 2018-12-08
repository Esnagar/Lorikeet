package sample;
//hola
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.RegistroLoginController;

import java.io.File;

public class Main extends Application {
    public static RegistroLogin clientlog;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //creacion de la carpeta de archivos si no existe

        new File("C:/LorikeetFiles").mkdirs();


        Parent root = FXMLLoader.load(getClass().getResource("registro.fxml"));
        primaryStage.setTitle("Lorikeet");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        RegistroController cg = new RegistroController("localhost", 1501,"");
        Main.clientlog = new RegistroLogin("localhost", 1501,"" , cg);
        cg.asignarCliente();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
