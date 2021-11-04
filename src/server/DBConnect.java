package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TimeZone;

// @author vktrs

public class DBConnect {

    private static Connection conn;
    private static String url = "jdbc:mysql://YOUR.SERVER.ADRESS/ubytovani?useLegacyDatetimeCode=false&amp&serverTimezone=" + TimeZone.getDefault().getID();
    private static String user = "YOUR_USER";
    private static String pass = "YOUR_PASSWORD";
 
    
    public static Connection connect() throws SQLException{
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(ClassNotFoundException cnfe){
            System.err.println("Error: "+cnfe.getMessage());
        }catch(InstantiationException ie){
            System.err.println("Error: "+ie.getMessage());
        }catch(IllegalAccessException iae){
            System.err.println("Error: "+iae.getMessage());
        }
 
        conn = DriverManager.getConnection(url,user,pass);
        return conn;
    }
 
    public static Connection getConnection() throws SQLException {
        if(conn !=null && !conn.isClosed())
            return conn;
        connect();
        return conn;
 
    }    
}
