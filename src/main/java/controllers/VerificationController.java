package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.*;

import java.io.File;
import java.io.IOException;

public class VerificationController{
    public TextField loginTextForm;
    public PasswordField passwordField;

    public void onButtonLoginAction(){
        try {
            SendHelper.sendInt(0,SendHelper.outputStream);//кидаем 0 чтобы дать понять серверу что всё нормально.
            // -1 отправляется при закрытии программы
            SendHelper.sendMessge(loginTextForm.getText(), SendHelper.outputStream);
            SendHelper.sendMessge(passwordField.getText(), SendHelper.outputStream);
            if(SendHelper.getInt(SendHelper.inputStream)==1){//если сервер отослал 1 верификация успешна
                SendHelper.stage.setScene(new Scene(FXMLLoader.load(new File("src/main/java/controllers/scenes/choice scene.fxml").toURL())));
                SendHelper.stage.show();
            }
            else {
                SendHelper.showTextWithScene("Wrong","incorrect data");
            }
        }
        catch (IOException es){
            System.out.println("IOException in button in VerificationController");
        }
    }
    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == (KeyCode.ENTER)){
            onButtonLoginAction();
        }
    }
}
