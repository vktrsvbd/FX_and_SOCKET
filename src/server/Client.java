package server;

import javafx.application.Application;
import javafx.stage.Stage;

// @author vktrs

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        new LoginStage();
        
    }
    
}
