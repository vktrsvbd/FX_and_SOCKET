package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

// @author vktrs

public class Server_log implements Runnable {
    final public byte LOGIN = 1;
    final public byte DBSELECT = 2;
    final public byte DBUPDATE = 3;
    final public byte DBINSERT = 4;
    final public byte SUCCESS = 5;
    final public byte FAILURE = 6;
    final public byte REQUEST = 7;
    final public byte LOGOUT = 8;

    ResultSet rs;

    Message msg;
    Message request = null;

    Socket soc;
    ObjectInputStream oIn;
    ObjectOutputStream oOut;
    int id;
    
    DBDriver dbD = new DBDriver(); 
    
    public Server_log(Socket socket, int id) throws IOException {
        this.soc = socket;
        this.id = id;

        this.oOut = new ObjectOutputStream(soc.getOutputStream());
        this.oIn = new ObjectInputStream(soc.getInputStream());

    }    
    public void start() {
        new Thread(this).start();
    }    
    @Override
    public void run() {
        
        while (true) {
            try {
                try {
                request = (Message) oIn.readObject();
                } catch (IOException ex) {
                    Logger.getLogger(Server_log.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server_log.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            switch (request.getType()) {                
                case LOGIN:
                    if (dbD.checkLogin(request.getUser(), request.getText())) { // vola metodu pro overeni login + argumenty                        
                        // posle objekt na server vrati-li true 
                        oOut.writeObject(new Message(LOGIN, SUCCESS, request.getUser(), "Login successful"));
                        // vrati-li false
                    } else {
                        oOut.writeObject(new Message(LOGIN, FAILURE, request.getUser(), "Login failed"));
                    }
                    break;
                case DBSELECT:
                    break;
                case DBUPDATE:
                    break;
                case DBINSERT:
                    break;    
                
            }
            } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server_log.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                    Logger.getLogger(Server_log.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                    Logger.getLogger(Server_log.class.getName()).log(Level.SEVERE, null, ex);
            }
   
        }
    
    }     
}
