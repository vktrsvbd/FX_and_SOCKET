package server;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// @author vktrs

public class DBDriver {
    
    final public byte LOGIN = 1;
    final public byte DBSELECT = 2;
    final public byte DBUPDATE = 3;
    final public byte DBINSERT = 4;
    
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://YOUR.SERVER.ADRESS/ubytovani";
    
    static final String DB_URL_localhost = "jdbc:mysql://localhost/ubytovani";
    
    static final String USER = "YOUR_USER";
    static final String PASS = "YOUR_PASSWORD";
    
    PreparedStatement select;
    PreparedStatement update;
    PreparedStatement insert;
    
    Connection conn = null;    

    public void dbRequest(byte type) throws ClassNotFoundException, SQLException {
        
        Class.forName("com.mysql.jdbc.Driver");
        
        System.out.println("Connecting");
        this.conn = DriverManager.getConnection(DB_URL_localhost, USER, PASS);//DB_URL
        
        Statement stmt = null;
        
        try {        
            switch (type) {
                case LOGIN:
                    select = conn.prepareStatement("SELECT password from ubytovani.users where name = ? ;");
                    ResultSet rs = select.executeQuery(); 
                    break;
            }        
                
            stmt = conn.createStatement();
            String sql = "SELECT idunit, unit, floor, cell, flat, beds, text, active FROM ubytovani.pokoje";

            try (ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {

                    int idunit = rs.getInt("idunit");
                    int unit = rs.getInt("unit");
                    
                    int beds = rs.getInt("floor");
                    String size = rs.getString("size");
                    String floor = rs.getString("floor");                        
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }                
    }
    
    public boolean checkLogin (String user, String pwd) throws ClassNotFoundException, SQLException {
        // pripojeni k DB
        
        String tempPwd = null;
        Class.forName("com.mysql.jdbc.Driver");
        
        this.conn = DriverManager.getConnection(DB_URL, USER, PASS);
        select = conn.prepareStatement("SELECT password from ubytovani.users where name = ? ;");
        // priprava selectu z prepareStatement
        
        select.setString(1, user); // dosazeni hodnot do prep. stat.
        
        try (ResultSet rs = select.executeQuery()) { // spusti query na DB server
            while (rs.next()) { // zachytavani odpovedi v loopu dokud jsou radky k nacitani
                tempPwd = rs.getString("password"); // prirazeni promenne ze sloupce password
            }
        }
        select.close(); // uzavreni vysledku
        conn.close();
        select = null;
        conn = null;
        
        try {
            if (select != null) {
                select.close();
            }
        } catch (SQLException se2) {
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return pwd.equals(tempPwd); // porovnani stringu tzn. heslo na vstupu (pwd) a odpoved z DB (tempPwd) >> vrati boolean
    }    
    
}
