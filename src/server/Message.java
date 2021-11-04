package server;

import java.io.Serializable;

// @author vktrs
public class Message implements Serializable {
    
    final public byte LOGIN = 1;
    final public byte DBSELECT = 2;
    final public byte DBUPDATE = 3;
    final public byte DBINSERT = 4;
    final public byte SUCCESS = 5;
    final public byte FAILURE = 6;
    final public byte REQUEST = 7;
    final public byte LOGOUT = 8;
    
    private final byte type;
    private final byte response;
    private final String user;
    private final String text;
    
    public Message(byte type, byte response, String user, String text) {
        this.type = type;           //LOGIN
        this.response = response;   //RESPONSE
        this.user = user;
        this.text = text;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public byte getResponse() {
        return this.response;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getText() {
        return this.text;
    }
    
}