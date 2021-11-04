package server;

// @author vktrs

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class AutoReload implements Runnable {

    Statement pst;
    ResultSet rst;
    
    Connection conn;
   
    String dbName = "ubytovani";    
    
    double control;//rowCheck
    double counter;//count
    
    Text actiontarget;
    
    private final AtomicBoolean running = new AtomicBoolean(false);
    
    int id = 0;
    int id_max;
    Boolean sound = true;
    
    public AutoReload(int id,double count,Text actiontarget) throws ClassNotFoundException, SQLException {
        
        this.id = id;
        this.counter = count;       
        this.actiontarget = actiontarget;
        
    }

    public void soundOnOff(Boolean sound) {
        this.sound = sound;
        if (this.sound == true) {
            //System.out.println("ar:sound true");
        } else if (this.sound == false) {
        }
    }
    
    public void maxId() throws ClassNotFoundException, SQLException {
        String idmax = "SELECT max(idbooking) FROM rezervace";
        
        pst = null;
        rst = null;        
        
        conn = DBConnect.connect();     
        rst = conn.createStatement().executeQuery(idmax);

        while (rst.next()) {
            id_max = (int) rst.getDouble(1);
            
            System.out.println("idmax:"+id_max);
        }         
         
    }
    
    public void stop() throws ClassNotFoundException, SQLException{
        running.set(false);
        rowControl();
        rowCheck();        
    }
    public void continueThread() throws ClassNotFoundException, SQLException {
        running.set(true);
        rowControl();
        rowCheck();        
    }
    public void interruptThread() {
        Thread.currentThread().interrupt();
        
    }
    public void start() {
        new Thread(this).start();
    }       
   
    public void rowControl() throws ClassNotFoundException, SQLException {
        
        String sql_ctrl = "SELECT count(idbooking) FROM ubytovani.rezervace";        

        //pst = null;
        //rst = null;    
        conn = DBConnect.connect();    
        rst = conn.createStatement().executeQuery(sql_ctrl);

        while (rst.next()) {
            counter = rst.getDouble(1);
            
            System.out.println("ctrl_rowControl: "+counter);
            
        }        
    }    
    public void rowCheck() throws ClassNotFoundException, SQLException {
        
        String sql = "SELECT count(idbooking) FROM ubytovani.rezervace";        

        //pst = null;
        //rst = null;  
        conn = DBConnect.connect();
        rst = conn.createStatement().executeQuery(sql);
        
        while (rst.next()) {
            control = rst.getDouble(1);
            
            System.out.println("ctrl-rowCheck: "+control);
        } 
        
    }
    
    @Override
    public void run() {
        running.set(true);
        try {
            rowControl();
            maxId();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AutoReload.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (running.get() == true) {
                         
            try {
                
                rowCheck();
                
                System.out.println("ctrl_mainThread:"+control);
                System.out.println("count mainThread:"+counter);
                
                Thread.sleep(7500);
                
            if (control > counter) {
        
              if (this.sound == true) {
                  //System.out.println("arThread: sound true");
                
        FileInputStream fis = new FileInputStream("sounds\\dingding.mp3");
        Player player = new Player(fis);
        player.play();
        
                this.actiontarget.setText(" NOVÁ REZERVACE ");
                this.actiontarget.setVisible(true);
    
                id = 1;
                rowControl();
                
              } else if (this.sound == false) {
                  //System.out.println("arThread: sound false");
        
                this.actiontarget.setText(" NOVÁ REZERVACE ");
                this.actiontarget.setVisible(true);
                
                id = 1;
                rowControl();                    
                }
              
                
            } else if (control < counter) {
                
                this.actiontarget.setText(" SMAZANÁ REZERVACE ");
                this.actiontarget.setVisible(true);
                
                id = 1;
                rowControl();

            }    
            
            } catch (ClassNotFoundException | SQLException | InterruptedException | FileNotFoundException | JavaLayerException ex) {
                Logger.getLogger(AutoReload.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
