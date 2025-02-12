package com.newcodor.apirequester;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application
{

    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    public static void main( String[] args )
    {
        System.out.println( "[+] Start Project....." );
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        this.mInstance = this;
        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/fxml/gui.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ApiRequester 1.2");
//        primaryStage.setUserData();
        primaryStage.show();
    }
}
