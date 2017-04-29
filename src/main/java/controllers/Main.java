package controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.SendHelper;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ASUS on 23.04.2017.
 */
public class Main extends Application{

    public static void main(String ... args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        SendHelper.stage=primaryStage;
        try {
            //create connection
            Socket socket = new Socket("195.138.81.175", 44440);
            SendHelper.inputStream=socket.getInputStream();
            SendHelper.outputStream=socket.getOutputStream();

            primaryStage.getIcons().add(new Image("file:title.png"));
            primaryStage.setTitle("Verification");
            primaryStage.setScene(new Scene(FXMLLoader.load(new File("src/main/java/controllers/scenes/verification scene.fxml").toURL())));
            primaryStage.setResizable(false);
            primaryStage.show();
        }
        catch (IOException ex){
            System.out.printf("IOException in start method");
        }
    }
}
