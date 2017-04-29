package main;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by ASUS on 23.04.2017.
 */
public class SendHelper {
    //татические потоки для обмена данными
    public static InputStream inputStream;
    public static OutputStream outputStream;
    public static Stage stage;
    public static int state=0;
    public static void sendFile(String path,OutputStream outputStream) throws IOException {
    state=0;
        try (FileInputStream fin = new FileInputStream(path)) {
            //get file size
            long size = (new File(path)).length();
            long startSize=size;
            //convert number-long to byte array
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.putLong(size);
            //send to user size of file
            outputStream.write(byteBuffer.array());
            //create buffer
            byte[] buffer;
            for (; size != 0; ) {
                //initialize buffer
                buffer = new byte[size >= ( 1024*8) ? (1024*8) : (int) size];
                //read from file
                fin.read(buffer);
                //reduce size
                size-=(size >= (1024*8)) ? (1024*8) : (int) size;
                //send information to user
                outputStream.write(buffer);
                outputStream.flush();
                state=(int)((startSize-size)/(startSize/100));
                System.out.println(state+" of "+100);
            }
        }
    }

    public static void getFile(String path,InputStream inputStream) throws IOException,InterruptedException{
        state=0;
        try (FileOutputStream fout = new FileOutputStream(path)) {
            //create buffer
            byte[] buffer = new byte[8];
            //read in buffer size of file
            inputStream.read(buffer);
            //convert from byte array to long-number
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            long size = byteBuffer.getLong();
            long startSize=size;
            for (; size != 0; ) {
                //initialize buffer
                buffer = new byte[size >= (1024*8) ? (1024*8) : (int) size];
                //wait if information not reached
                while(inputStream.available()<buffer.length) {
                    Thread.sleep(1);
                }
                //read to array
                inputStream.read(buffer);
                //reduce size
                size-=(size >= (1024*8)) ? (1024*8) : (int) size;
                //write to file
                fout.write(buffer);
                fout.flush();
                state=(int)((startSize-size)/(startSize/100));
                System.out.println(state+" of "+100);
            }
        }
    }

    public static void sendMessge(String str,OutputStream outputStream) throws IOException {
        byte[] buffer=str.getBytes();
        int size=buffer.length;

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(size);

        outputStream.write(byteBuffer.array());

        outputStream.write(str.getBytes());
    }

    public static String getMessage(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4];
        inputStream.read(buffer);
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        int size = byteBuffer.getInt();

        buffer=new byte[size];
        if(inputStream.available()<size){
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException ie){
                System.out.println("InterruptedException in getMessage");
            }
        }
        int num=inputStream.read(buffer);

        String str=new String(buffer,0,num);
        return str;
    }

    public static int getInt(InputStream inputStream) throws IOException{

        return Integer.parseInt(getMessage(inputStream));
    }

    public static void sendInt(int i,OutputStream outputStream) throws IOException{
        sendMessge(String.valueOf(i),outputStream);
    }

    public static void addFileList(String path, TreeItem<String> parent, int mode){
        for(String elem:new File(path).list()){
            if(Files.isReadable(Paths.get(path+elem))) {
                if(mode==1) {
                    parent.getChildren().add(new TreeItem<String>(elem));
                }
                else if(mode==2){
                    if(Files.isDirectory(Paths.get(path+elem))){
                        parent.getChildren().add(new TreeItem<String>(elem));
                    }
                }
            }
        }
    }
    public static boolean isHaveDirectory(String path){
        for(String elem:new File(path).list()){
            if(Files.isDirectory(Paths.get(path+"\\"+elem))){
                return true;
            }
        }
        return false;
    }

    public static void addFilesToDirectories(String path,TreeItem<String> parent,int mode){
        if(parent.getChildren().size()>0) {
            return;
        }
        if(mode==1) {
            parent.getChildren().add(new TreeItem<String>("Folder"));
        }
        else if(mode==2){
            if(isHaveDirectory(path)){
                parent.getChildren().add(new TreeItem<String>("Folder"));
            }
        }



        parent.addEventHandler(TreeItem.branchExpandedEvent(),e->{
            int size=parent.getChildren().size();
            System.out.println(size);
            if(size>1) {
                System.out.println(path+" already entered");
                return;
            }
            else if(size==1) {
                System.out.println("in size==1");
                if(!parent.getChildren().get(0).getValue().equals("Folder")){
                    return;
                }
                parent.getChildren().remove(0);
                if (mode == 1) {
                    addFileList(path, parent, 1);
                    for(TreeItem<String> elem:parent.getChildren()) {
                        if (Files.isDirectory(Paths.get(path + elem.getValue())) &&
                                Files.isReadable(Paths.get(path + elem.getValue()))) {
                            SendHelper.addFilesToDirectories(path + elem.getValue()+"\\", elem, 1);
                        }
                    }
                } else if (mode == 2) {
                    addFileList(path, parent, 2);
                    for(TreeItem<String> elem:parent.getChildren()){
                        if(   Files.isDirectory(Paths.get(path+elem.getValue()+"\\"))&&
                                Files.isReadable(Paths.get(path+elem.getValue()+"\\"))   ) {
                            SendHelper.addFilesToDirectories(path+elem.getValue()+"\\",elem,2);
                        }
                    }
                }

            }
        });
    }

    public static Stage showTextWithScene(String title, String text){

        GridPane gridsPane=new GridPane();
        gridsPane.setPadding(new Insets(30,30,30,30));
        gridsPane.setVgap(8);
        gridsPane.setHgap(10);

        Label localLabel=new Label(text);

        gridsPane.getChildren().add(localLabel);

        Stage localStage=new Stage();
        localStage.setTitle(title);

        Scene localScene =new Scene(gridsPane,200,150);
        localStage.setScene(localScene);
        localStage.show();
        return localStage;
    }
}
