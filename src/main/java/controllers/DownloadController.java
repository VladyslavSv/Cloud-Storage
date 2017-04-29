package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import main.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ASUS on 23.04.2017.
 */
public class DownloadController implements Initializable{
    public ListView<String> listView;
    public TextField textFieldWithPath;
    public ImageView imageView;
    private String[] arOfFiles;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SendHelper.stage.setTitle("Download dialog");
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imageView.setImage(new Image("file:arrow-to-back.png"));
        try {
            SendHelper.sendInt(3, SendHelper.outputStream);
            int arrayOfFileSize = SendHelper.getInt(SendHelper.inputStream);
            arOfFiles = new String[arrayOfFileSize];

            for (int i = 0; i < arrayOfFileSize; i++) {
                arOfFiles[i] = SendHelper.getMessage(SendHelper.inputStream);
                listView.getItems().addAll(arOfFiles[i]);
            }
        }
        catch (IOException ex){
            System.out.println("IOExceprion in initialize method in DownloadController");
        }
    }
    public void onThreeDotAction(){

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Dialog");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            textFieldWithPath.setText(chooser.getSelectedFile().toString());
        } else {
            System.out.println("No Selection ");
        }
    }
    public void onDownloadButtonAction(){
            new Thread(() -> {
                try {
                    SendHelper.sendInt(2, SendHelper.outputStream);

                    String path = textFieldWithPath.getText();
                    if (new File(path).isDirectory()) {
                        ObservableList<String> list = listView.getSelectionModel().getSelectedItems();

                        for (String str : list) {
                            for (int i = 0; i < arOfFiles.length; i++) {

                                if (str.equals(arOfFiles[i])) {
                                    SendHelper.sendInt(i, SendHelper.outputStream);
                                    SendHelper.getFile(path + "\\" + arOfFiles[i], SendHelper.inputStream);
                                    break;
                                }
                            }
                        }

                        SendHelper.sendInt(-1, SendHelper.outputStream);
                        SendHelper.showTextWithScene("Success", "Files downloaded");
                    } else {
                        SendHelper.showTextWithScene("Fail", "File not found \nTry again!");
                    }

                }
                catch (IOException io){
                    System.out.println("IOException in getDownloadScene method in buttonDownload.setOnAction");
                }
                catch (InterruptedException interrupted){
                    System.out.println("InterruptedException in getDownloadScene method in buttonDownload.setOnAction");
                }
            }).start();
    }
    public void onClickToImage(){
        try {
            SendHelper.stage.setScene(new Scene(FXMLLoader.load(new File("src/main/java/controllers/scenes/choice scene.fxml").toURL())));
            SendHelper.stage.show();
        }
        catch (IOException ex){
            System.out.println("IOException in onClickImage method");
        }
    }
    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == (KeyCode.ENTER)){
            onDownloadButtonAction();
        }
    }
}
