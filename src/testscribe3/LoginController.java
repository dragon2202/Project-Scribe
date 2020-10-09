/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;

import java.lang.Math; // For window resizing.
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.geometry.Rectangle2D; // For window resizing.

/**
 * FXML Controller class
 *
 * @author Ryan, Alvin
 */
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private boolean condition = false;
    
    @FXML
    private PasswordField pass1; // use pass1.getText() to get the string from the field
    @FXML 
    private MenuBar LoginMenuBar;
    @FXML
    private Label badPassword;
    
    @FXML
    private MenuBar LoginMenu;
    
    
    /**
    * Enable On Screen Keyboard
    * 
    */
    @FXML
    private void callOnScreenKeyboard(ActionEvent event) throws IOException{
        try{
            Runtime.getRuntime().exec("cmd /c C:\\Windows\\System32\\osk.exe");
            
        } catch (Exception e){
            System.out.println("Error: Unable to open on screen keyboard");
        }
    }
    
     @FXML
    private void handleLoginButton(ActionEvent event) throws IOException { 
        String userInput = pass1.getText(); 
        String fileName = "password.txt"; // Later on, you might need to place the file somewhere else and specify its path
        String line = null; // This will reference one line at a time (currently redundant, may prove useful if more passwords are added)
        
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) { // Will keep reading a single line at a time until it reaches the end
                if(line.length() > 0) // Ignore blank lines
                {
                    if(Objects.equals(userInput,line)) // Compare strings from user input with the one from the file
                    {
                        condition = true;
                        break;
                    }
                    else
                        condition = false;
                }
            }   
            // Always close files.
            bufferedReader.close();
            fileReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '"+ fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        
        if(condition == true) // If user has inputted the correct password
        {
            Parent Edit_Mode = FXMLLoader.load(getClass().getResource("EditMode.fxml"));
            Scene edit_mode_scene = new Scene(Edit_Mode);
            Stage edit_mode_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            edit_mode_stage.setScene(edit_mode_scene);
            edit_mode_stage.centerOnScreen();
            edit_mode_stage.show();       
        }
        else 
        {
            badPassword.setVisible(true);
        }
    }
    
    @FXML
    private void handleCCLog(ActionEvent event) throws IOException{
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) LoginMenuBar.getScene().getWindow();
        CCLog_Stage.setScene(CCLogScene);       
        CCLog_Stage.centerOnScreen();
        CCLog_Stage.setTitle("CCLog");
        CCLog_Stage.show();         
    }
    
    
    @FXML
    private void handleCounts(ActionEvent event) throws IOException{
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) LoginMenuBar.getScene().getWindow();
        Counts_Stage.setScene(Counts_Scene);       
        Counts_Stage.centerOnScreen();
        Counts_Stage.setTitle("Counts");
        Counts_Stage.show();         
    }
    
     @FXML
    private void handleExec(ActionEvent event) throws IOException{
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) LoginMenuBar.getScene().getWindow();
        Exec_Stage.setScene(Exec_Scene);       
        Exec_Stage.centerOnScreen();
        Exec_Stage.setTitle("Executive Summary");
        Exec_Stage.show();         
    }
    
      @FXML
    private void handleShift(ActionEvent event) throws IOException{
        Parent Shift = FXMLLoader.load(getClass().getResource("ShiftEntry.fxml"));
        Scene Shift_Scene = new Scene(Shift);
        Stage Shift_Stage = (Stage) LoginMenuBar.getScene().getWindow();
        Shift_Stage.setScene(Shift_Scene);       
        Shift_Stage.centerOnScreen();
        Shift_Stage.setTitle("Shift Entry");
        Shift_Stage.show();         
    }
    
    @FXML
    private void handleViewButton(ActionEvent event) throws IOException {
        Parent Test = FXMLLoader.load(getClass().getResource("ViewMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) LoginMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();  
        
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) throws IOException{ 
        Parent Test = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) LoginMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();
    }
    
    @FXML
    private void handleTestStep(ActionEvent event) throws IOException{
        Parent BacktoMain = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
        Scene MainScene = new Scene(BacktoMain);
        Stage Main_Stage = (Stage) LoginMenuBar.getScene().getWindow();
        Main_Stage.setScene(MainScene);      
        Main_Stage.centerOnScreen();
        Main_Stage.show();         
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
