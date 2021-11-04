package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.text.Text;

// @author vktrs

public class Client_log implements Runnable {
    
    Message msg;
    Message reply = null;
    
    LoginStage loginStage;
    ChartStage chartStage;
    
    String user;
    
    Socket clientSocket;
    ObjectInputStream oIn;
    ObjectOutputStream oOut;
    Text actionTarget; 
    
    boolean logout = false;
    
    final public byte LOGIN = 1;
    final public byte DBSELECT = 2;
    final public byte DBUPDATE = 3;
    final public byte DBINSERT = 4;
    final public byte SUCCESS = 5;
    final public byte FAILURE = 6;
    final public byte REQUEST = 7;
    final public byte LOGOUT = 8;
    
    
    public Client_log (LoginStage loginStage) { //, MainStage mainStage
        
        this.loginStage = loginStage;
    }   

    public void connect(String serverName, int port) throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(serverName, port));

        this.oOut = new ObjectOutputStream(clientSocket.getOutputStream());
        this.oIn = new ObjectInputStream(clientSocket.getInputStream());
    }
    
    private static String get_SHA_256_SecurePassword(String passwordToHash) {
	String generatedPassword = null;
	try {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		//md.update(salt.getBytes());
		byte[] bytes = md.digest(passwordToHash.getBytes());
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		generatedPassword = sb.toString();
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
	return generatedPassword;
    }
    
    public void login(String serverName, int port, String userName, String password, Text actiontarget) throws IOException {

        user = userName;
        
        String passwordToHash = password;
        String securePassword = get_SHA_256_SecurePassword(passwordToHash);
        
        
        this.actionTarget = actiontarget; 
        this.connect(serverName, port);                         // pripojeni k serveru
        
        msg = new Message(LOGIN, REQUEST, userName, securePassword);  // vytvori objekt s pozadavkem co ma server vykonat a potrebna data
        oOut.writeObject(msg);                                  // zapis objektu do streamu
        oOut.flush();                                           // odeslani streamu

        
        this.actionTarget.setText("Přihlášení na server");
        new Thread(this).start();                              // spusti nove vlakno ktere odchytne odpoved

    }
    
    public void processLoginReply(Message reply) {
        
        switch (reply.getResponse()) {
            case SUCCESS:
                Platform.runLater(() -> {
                    loginStage.close();
                
                try {
                
                chartStage = new ChartStage();
                
                } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client_log.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                Logger.getLogger(Client_log.class.getName()).log(Level.SEVERE, null, ex);
                }
                chartStage.LoggedUser(user);

        });              
                break;
            case FAILURE:
                this.actionTarget.setText("Jeden z údajů je chybný");
                break;
        }
    }

    @Override
    public void run() {
        while (true) {
            reply = null;

            try {
                reply = (Message) oIn.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Client_log.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (reply != null) {
                switch (reply.getType()) {
                    case LOGIN:
                        this.processLoginReply(reply);
                        break;
                    case DBSELECT:
                        break;                                              
                    case DBUPDATE:
                        break;
                    case DBINSERT:
                        break;
                    case LOGOUT:
                        break;
                    default:
                        break;

                }
            }
        } //To change body of generated methods, choose Tools | Templates.
    }
    
}