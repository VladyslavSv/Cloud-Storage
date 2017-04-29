package controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import main.SendHelper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ASUS on 23.04.2017.
 */
public class ChoiceController implements Initializable{
    public ChoiceBox<String> choiceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choiceBox.getItems().add("Send file");
        choiceBox.getItems().add("Download file");
        choiceBox.setValue("Send file");
        SendHelper.stage.setTitle("Choice dialog");
    }
    public void onButtonSendAction()
    {
            try {
                if (choiceBox.getValue().equals("Send file")) {
                        //show modal dialog which choice a path to the file
                        FileChooser fileChooser = new FileChooser();
                        File selectedFile = fileChooser.showOpenDialog(null);

                        if(selectedFile!=null)

                        {
                                String path = selectedFile.getPath();
                                new Thread(()->{
                                    try {
                                        SendHelper.sendInt(1, SendHelper.outputStream);
                                        System.out.println(path.toString());

                                        String[] ar = (path.toString()).split("\\\\");
                                        String expansion = ar[ar.length - 1];

                                        SendHelper.sendMessge(expansion, SendHelper.outputStream);//отправил расширение
                                        SendHelper.sendFile(path.toString(), SendHelper.outputStream);

                                        SendHelper.showTextWithScene("Success", "File has been sended");
                                    }
                                    catch (IOException ex){
                                        ex.printStackTrace();
                                    }
                                }).start();


                        }

                        else

                        {
                            System.out.println("File selection cancelled.");
                        }

                } else if (choiceBox.getValue().equals("Download file")) {//кинул вид запроса
                    SendHelper.stage.setScene(new Scene(FXMLLoader.load(new File("src/main/java/controllers/scenes/download scene.fxml").toURL())));
                    SendHelper.stage.show();
                }
            } catch (MalformedURLException ex) {
                System.out.println("MalformedURLException in initialize in ChoiseBox");
            } catch (IOException ioex) {
                System.out.println("IOException in initialize in ChoiseBox");
            }
        }
        public void onKeyPressed(KeyEvent keyEvent) {
            if(keyEvent.getCode() == (KeyCode.ENTER)){
                onButtonSendAction();
            }
        }

}
