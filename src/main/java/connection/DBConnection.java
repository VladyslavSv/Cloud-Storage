package connection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private String URL="jdbc:mysql://localhost:3306/mydbtest";
    private String USER="root";
    private String PASSWORD="root";

    private Connection connection;

    public DBConnection(){
        try{
            Driver driver=new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection= DriverManager.getConnection(URL,USER,PASSWORD);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void close()throws SQLException
    {
        connection.close();
    }
    public Connection getConnection() {
        return connection;
    }
}

