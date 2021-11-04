package server;

import java.io.IOException; 
import java.util.Locale;
import java.util.logging.Level; 
import java.util.logging.Logger; 
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos; 
import javafx.scene.Scene; 
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField; 
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; 
import javafx.scene.text.Font; 
import javafx.scene.text.FontWeight; 
import javafx.scene.text.Text; 
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

// @author vktrs

public class LoginStage extends Stage {
    
    
    LoginStage() throws IOException {  
        
        Locale.setDefault(Locale.forLanguageTag("cs-CZ"));
        
        this.setTitle(" UBYTOVNA");
        this.getIcons().add(new Image("http://vikinet.cz/4Adam/building.png"));
        Image logo = new Image("/images/logo.png");
        
        HBox logobox = new HBox();
               
        Client_log cl = new Client_log(this);   
        
        GridPane grid = new GridPane();
        Scene scene = new Scene(grid, 400, 300);
        scene.getStylesheets().add("/styles/test.css");
        grid.setStyle("-fx-background-color: #e0e0e0;");
     
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text scenetitle = new Text("Přihlášení");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        
        final HBox hbox_scenetitle = new HBox();
        hbox_scenetitle.setPadding(new Insets(15,0,10,0));
        hbox_scenetitle.getChildren().add(scenetitle);
        
        grid.add(hbox_scenetitle, 0, 0);
        
        
        Label userName = new Label("Uživatel");
        grid.add(userName, 0, 1);  
        
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        
        Label pw = new Label("Heslo");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        
        Button btn = new Button("Přihlásit");
        btn.getStyleClass().add("button-archive");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        
        grid.add(hbBtn, 1, 5);

        final Text actiontarget = new Text();
        
        actiontarget.setId("label");
        grid.add(actiontarget, 1, 6);
        
        btn.setOnAction((ActionEvent e) -> {
            
            try {
                cl.login("YOUR.SERVER.ADRESS", 59898, userTextField.getText(), pwBox.getText(), actiontarget);
                
            } catch (IOException ex) {
                Logger.getLogger(LoginStage.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        this.centerOnScreen();
        this.setScene(scene);
        this.show();        
        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {

                    //ar.stop();
                    Platform.exit();
                    System.exit(0);

            }
        });         
    }
    
}
