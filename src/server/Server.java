package server;

import java.io.IOException;
import java.net.ServerSocket;

// @author vktrs

public class Server {
    int id = 0;

    public Server() throws IOException {
        
        ServerSocket srvSocket = new ServerSocket(59898);
        while (true) {

            new Server_log(srvSocket.accept(), id).start();
            id++;
        }       
    }    
    public static void main(String[] args) throws IOException {
        
        new Server();
  
    }
}
