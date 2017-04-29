package main;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connection.*;

public class ServerExecutor extends Thread{
    //Our socket
    private Socket socket;
    //root
    private static String root="D:\\files";

    final static int SEND=1;
    final static int DOWNLOAD=2;
    final static int GETALLFILES=3;
    //list of files
    private static String[] fileArray=(new File(root)).list();
    //data base connection
    private static DBConnection connector=new DBConnection();

    public ServerExecutor(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        String login="none";
        try {
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream=socket.getOutputStream();
            while (true) {
                if (SendHelper.getInt(inputStream) == -1) {
                    return;
                }
                login = SendHelper.getMessage(inputStream);
                String password = SendHelper.getMessage(inputStream);
                if (verification(login, password)) {
                    SendHelper.sendInt(1, outputStream);
                    System.out.println("User "+login+" connected to server");
                    break;
                } else {
                    SendHelper.sendInt(0, outputStream);
                    System.out.println("Ver not succesfully");
                }
            }

            while(true) {
                int num = SendHelper.getInt(inputStream);// получил тип запроса

                switch (num) {
                    case SEND:
                        System.out.println("case 1");
                        String expansion = SendHelper.getMessage(inputStream);//получил название
                        String path = root + "\\" + expansion;

                        System.out.println(path);

                        SendHelper.getFile(path, inputStream);//получил файл
                        System.out.println("file has got success");
                        synchronized (fileArray) {
                            fileArray = (new File(root)).list();
                        }
                        break;
                    case DOWNLOAD:
                        System.out.println("case 2");
                        while (true) {
                            int choise = SendHelper.getInt(inputStream);
                            System.out.println("Choise in run"+choise);
                            if (choise == -1) {
                                break;
                            }
                            System.out.println(root + "\\" + fileArray[choise]);

                            SendHelper.sendFile(root + "\\" + fileArray[choise], outputStream);
                        }
                        System.out.println("Files are downloaded ");
                        break;
                    case GETALLFILES:
                        SendHelper.sendInt(fileArray.length, outputStream);//отправляем размер массива
                        for (int i = 0; i < fileArray.length; i++) {//пересылаем масив по элементам
                            SendHelper.sendMessge(fileArray[i], outputStream);
                        }
                        break;
                }
            }
        } catch (SocketException e) {
            System.out.println("User "+login+" disconnected from server");
        }
        catch (IOException io){
            System.out.println("IOException in run, user-"+login);
        }
        catch (InterruptedException interruptedException){
            System.out.println("InterruptedException in run,user-"+login);
        }

    }
    public boolean verification(String login,String password){
        boolean isVerification=false;
        try {

            String query="SELECT * FROM users WHERE login=? AND password=?";

            try(PreparedStatement statement = connector.getConnection().prepareStatement(query)) {

                statement.setString(1,login);
                statement.setString(2,password);
                statement.executeBatch();

                ResultSet resultSet=statement.executeQuery();

                while(resultSet.next()){
                    isVerification=true;
                }

            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        return isVerification;
    }
}

