/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;

import java.io.File;
import java.lang.Math; // For window resizing.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D; // For window resizing.
/**
 *
 * @author Ryan
 */
public class TestScribe3 extends Application {
    
    static final String FILE_TEMP_NAME = "tmp.xlsx"; // Temp files to be deleted
    /**
     * Create window
     * @param stage window
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
  
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Project Scribe");
        stage.show();
        
        /**
        * Set Stage boundaries to visible bounds of the main screen, if screen is too small
        * The default width and height are only set after first call to stage.show()
        */
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(Math.min(stage.getWidth(), primaryScreenBounds.getWidth()));
        stage.setHeight(Math.min(stage.getHeight(), primaryScreenBounds.getHeight()));
        
        // Center window.
        stage.centerOnScreen();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        /*
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                File temp = new File(FILE_TEMP_NAME);
                temp.delete();
            }
        }, "Shutdown-thread"));*/
    }
    
}
