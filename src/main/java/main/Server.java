package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ASUS on 26.10.2016.
 */
public class Server {
    public static void main(String[] agrs){
        try {
            ServerSocket serverSocket = new ServerSocket(44440);
            System.out.println("Server started at "+44440+" Port");
            while(true){
                Socket socket=serverSocket.accept();
                System.out.println("have connect");
                Thread th=new ServerExecutor(socket);
                th.start();
            }
        }
        catch (IOException e){
            System.out.println("IOException");
        }
    }
}