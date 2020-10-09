/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;

import java.lang.Math; // For window resizing.
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.swing.JTextField;
import javafx.geometry.Rectangle2D; // For window resizing.
import javafx.stage.Screen;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 * FXML Controller class
 *
 * @author Ryan, Alvin
 */
public class TestModeController implements Initializable {
    static private final String FILE_NAME_EXT = Storage.getExt(); // The string that we add in front of a file that is designated as "Advanced SORT"
    static private final String FILE_TEMP_NAME = Storage.getTemp(); // Temporary File that we are actually using
    static private final String FILE_TEMP_NAME_ADV = Storage.getTempAdv(); // The temp file for "Advanced SORT"
    
    static final int NUM_PAGE = 5;              // Number of steps (instructions) in a page
    static final int STEP_COLUMN = 3;           // Location of said steps in the excel file
    static final int TEST_COLUMN = 4;           // This is where you would put the Passed/Failed/Not Tested description
    static final int COMMENT_COLUMN = 5;        // Location of comment in the excel file
    static final int VARIANT_LIST_COLUMN = 9;   // This column contains the list of variant used in the test
    static final int VARIANT_COLUMN = 7;        // This column is associated with the STEP_COLUMN and is used for filtering purpose
    static final int ID_COLUMN = 0;             // Location of the ID in the excel file
    static final int SUBHEADER_COLUMN = 2;      // Location of the subheader section in the excel file
    //static final String FILE_TEMP_NAME_ADD = ; // The name of the temp file
    static final int HEADER_COLUMN = 1;         // Location of the header in the excel file
    
    static private String fileNamePath;         // Placeholder for the file name/path
    static private String fileNameOnly;         // This one contains the file name only (without path)
    static private int maxPageNum;              // The maximum page number for the current variant/mode
    static private int currentPageNum;          // The current page number that the user is currently viewing
    static private int sheetIndex = 0;          // This is the sheet index based on the excel file
    static private int rowIndex = 0;            // This is the row index based on the excel file
    static private int stepIndex = -1;          // Step index now means number of steps that have matching variant filter that have been counted
    static private boolean condition = true;    // if value == false, then the program has reached the very end of the excel file
    static private ArrayList<Integer> record = new ArrayList<Integer>();    // This ArrayList contains the row index recorded on compatible "test steps" (matching variant)
    static private ArrayList<Integer> recordSH = new ArrayList<Integer>();  // Same as record, but for subheader instead
    static private ArrayList<Integer> recordH = new ArrayList<Integer>();   // For header
    static private String currentVariantFilter = "";    // This changes based on the variant that you have selected
    static private boolean newSH = false;       // This boolean is used in the readExcel() and previousArrow(). This is for subheader checking
    static private boolean newH = false;        // This boolean is used in the readExcel() and previousArrow(). This is for header checking
    static private boolean executeReset = false; // This boolean is used in initialize to check whether variable resets are needed or not
    
    Color passed = new Color(0,255,0); // Is this needed? 
    
    /**
     * Initializes the controller class.
     */
    @FXML
    private Button saveButton;
    @FXML 
    private MenuBar TestMenuBar;
    
    @FXML 
    private VBox TestBox;
    
    @FXML
    private Label labelSaved, closeFileLabel, askVariant, introLabel, selectTest,
                  id1, id2, id3, id4, id5, labelFileChoose, labelStepsNotFound, checklistLabel;
    
    @FXML
    private Button GoBack3, fileChoose;
    
    @FXML
    private TextArea question1, question2, question3, question4, question5,
            comment1, comment2, comment3, comment4, comment5,
            subHead1, subHead2, subHead3, subHead4, subHead5,
            header1, header2, header3, header4, header5;
    
    @FXML
    private TextField testDescription1, testDescription2, testDescription3, testDescription4, testDescription5, jumpTest, pageNumber;    
    
    @FXML
    private Button previousArrow1, previousArrow5, previousArrow10, nextArrow1, nextArrow5, nextArrow10;
    @FXML 
    private MenuButton dropMenu1, dropMenu2, dropMenu3, dropMenu4, dropMenu5; // WHY DON'T THESE HAVE BETTER NAMES? T.T
    
    @FXML
    private Menu variantFilter, View, sheetSelector, headerJumpTo;
    
    @FXML 
    private MenuItem TutorialButton, saveItem, saveItemAs;
    
    /************************************ WRITE CHOICES BEGINS ***************************************************/
    /*************************************Horribly inelegant, but it works*********************************************/
    
    @FXML 
    private void writeChoice1Passed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription1.setVisible(true);
        testDescription1.setText("Passed");
        dropMenu1.setText("Passed");
        comment1.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice1Failed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription1.setVisible(true);
        testDescription1.setText("Failed");
        dropMenu1.setText("Failed");
        comment1.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice1NotTested(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription1.setVisible(true);
        testDescription1.setText("Not Tested");
        dropMenu1.setText("Not Tested");
        comment1.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice2Passed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription2.setVisible(true);
        testDescription2.setText("Passed");      
        dropMenu2.setText("Passed");   
        comment2.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice2Failed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription2.setVisible(true);
        testDescription2.setText("Failed");
        dropMenu2.setText("Failed");   
        comment2.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice2NotTested(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription2.setVisible(true);
        testDescription2.setText("Not Tested");
        dropMenu2.setText("Not Tested");   
        comment2.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice3Passed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription3.setVisible(true);
        testDescription3.setText("Passed");
        dropMenu3.setText("Passed");   
        comment3.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice3Failed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription3.setVisible(true);
        testDescription3.setText("Failed");
        dropMenu3.setText("Failed");   
        comment3.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice3NotTested(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription3.setVisible(true);
        testDescription3.setText("Not Tested");
        dropMenu3.setText("Not Tested");   
        comment3.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice4Passed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription4.setVisible(true);
        testDescription4.setText("Passed");         
        dropMenu4.setText("Passed");   
        comment4.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice4Failed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription4.setVisible(true);
        testDescription4.setText("Failed");
        dropMenu4.setText("Failed");   
        comment4.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice4NotTested(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription4.setVisible(true);
        testDescription4.setText("Not Tested");
        dropMenu4.setText("Not Tested");   
        comment4.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice5Passed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription5.setVisible(true);
        testDescription5.setText("Passed");
        dropMenu5.setText("Passed");   
        comment5.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice5Failed(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription5.setVisible(true);
        testDescription5.setText("Failed");
        dropMenu5.setText("Failed");   
        comment5.setVisible(true); // Open up the comment field
    }
    @FXML 
    private void writeChoice5NotTested(ActionEvent event) throws IOException, InvalidFormatException{
        testDescription5.setVisible(true);
        testDescription5.setText("Not Tested");
        dropMenu5.setText("Not Tested");   
        comment5.setVisible(true); // Open up the comment field
    }
    
        
    /************************************ WRITE CHOICES ENDS ***************************************************/
        /******************************************************************************************************/
    
    
    // saveToOri functions as the save button, it essentially rewrites the loaded excel file with the temporary file
    @FXML 
    private void saveToOri(ActionEvent event) throws FileNotFoundException, IOException, InvalidFormatException{
        if(fileNamePath == null || fileNamePath.isEmpty()){
            /*
            *   This condition can only activate if the temp file was loaded automatically without user's input
            *   This means that the temp file has already existed in the first place from the start
            */
            saveAs();
            return;
        }
        
        // Actual saving
        savePage(); // The actual saving mechanic used in the GUI
        // Copying from temp to original excel file to maintain illusion of saving
        File ori = new File(fileNamePath);
        File temp = new File(FILE_TEMP_NAME); 
        Files.copy(temp.toPath(), ori.toPath(), StandardCopyOption.REPLACE_EXISTING);

        
        File ori2 = new File(FILE_NAME_EXT + fileNameOnly);
        File temp2 = new File(FILE_TEMP_NAME_ADV); 
        // Check if the "Advance SORT" file exist or not
        if(ori2.exists() && !ori2.isDirectory()){
            // If it exist, then check if FILE_TEMP_NAME_ADV exist or not
            if(!temp2.exists() || temp2.isDirectory()){
                // If no, create a new one
                Files.copy(ori2.toPath(), temp2.toPath(), StandardCopyOption.REPLACE_EXISTING);
                temp2.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
            }
            else{
                // If yes, then replace the selected file with this temp file
                Files.copy(temp2.toPath(), ori2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        // If the "Advanced SORT" file does not exist, create a new one
        else{
            XSSFWorkbook workbook1 = new XSSFWorkbook();
            Sheet sheet1 = workbook1.createSheet("CCLog");
            sheet1 = workbook1.createSheet("Shift Entry");
            sheet1 = workbook1.createSheet("Executive Summary");
            // Writing out the changes
            FileOutputStream output_file = new FileOutputStream(new File(FILE_NAME_EXT + fileNameOnly));
            workbook1.write(output_file);
            // Closing files
            output_file.close();
            workbook1.close();
            // Don't forget to create a temp version of it
            Files.copy(ori2.toPath(), temp2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            temp2.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        }
        labelSaved.setVisible(true); // Will make the label "Saved!" appear
    }     
    // saveToOriAs is associated with the Save As button in the File Menu
    @FXML 
    private void saveToOriAs(ActionEvent event) throws FileNotFoundException, IOException, InvalidFormatException{
        saveAs();
    } 
    private void saveAs() throws IOException, FileNotFoundException, InvalidFormatException{
        // User select or input file name to save
        FileChooser selectExcel = new FileChooser();
        selectExcel.getExtensionFilters().add(new ExtensionFilter("XLSX FILES", "*.xlsx"));
        selectExcel.getExtensionFilters().add(new ExtensionFilter("XLS FILES", "*.xls"));
        File tests = selectExcel.showSaveDialog(null);
        
        // To avoid any exception or nullpointer error, this condition is required
        if(tests == null){
            return;
        }
        
        // Rewriting FILE_NAME with the file selected by user
        fileNamePath = tests.getAbsolutePath(); // Important! FILE_NAME holds the target's name/path
        fileNameOnly = tests.getName();
        
        // Actual saving
        savePage(); // The actual saving mechanic used in the GUI
        // Copying from temp to original excel file to maintain illusion of saving
        File ori = new File(fileNamePath);
        File temp = new File(FILE_TEMP_NAME); 
        Files.copy(temp.toPath(), ori.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Also saves FILE_TEMP_NAME_ADV
        File ori2 = new File(FILE_NAME_EXT + fileNameOnly);
        File temp2 = new File(FILE_TEMP_NAME_ADV); 
        Files.copy(temp2.toPath(), ori2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        labelSaved.setVisible(true); // Will make the label "Saved!" appear
        
        Storage.setNameTwice(fileNamePath, fileNameOnly);
    }
    
    
    /**
     * Help Section
     * @param event
     * Note: event may not be necessary.
     */
    @FXML
    private void openHelpDocument(ActionEvent event) throws IOException{
        try {
            // Load window fxml file.
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HelpWindow.fxml"));
//            Parent root1 = (Parent) fxmlLoader.load(); // I don't know what this does.
//            
//            // Create window instance.
//            Stage stage = new Stage();
//            stage.setTitle("Help Section");
//            stage.setScene(new Scene(root1));
//            
//            // Get visual bounds of screen and size window based on that.
//            // This is necessary to avoid it being cut off.
//            // For some reason this doesn't work until after stage.show but does for the main application window.
//            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//            //stage.setWidth(Math.min(stage.getWidth(), primaryScreenBounds.getWidth()));
//            stage.setHeight(Math.min(stage.getHeight(), primaryScreenBounds.getHeight()));
            
            //stage.show();

            File helphtml = new File("C:\\Users\\viole\\Documents\\scribe\\Project-Scribe\\src\\testscribe3\\helpmenu\\\\help.html");
            Desktop.getDesktop().browse(helphtml.toURI());
            //File htmlFile = new File("./helpmenu/help.html");
            //Desktop.getDesktop().browse(htmlFile.toURI());

            } catch (Exception e){
                System.out.println("Error: Unable to load help.html");
        }
//        TutorialButton.setOnAction(event); {
//            // Open help window.
//            
//        }
    }

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
    /************************************ HANDLES BEGINS ***************************************************/
    /******************This ActionEvent is found on the Go To... section************************************/

    @FXML
    private void handleCCLog(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) TestMenuBar.getScene().getWindow();
        CCLog_Stage.setScene(CCLogScene);       
        CCLog_Stage.centerOnScreen();
        CCLog_Stage.setTitle("CCLog");
        CCLog_Stage.show();         
    }
    
    
    @FXML
    private void handleCounts(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Counts_Stage.setScene(Counts_Scene);       
        Counts_Stage.centerOnScreen();
        Counts_Stage.setTitle("Counts");
        Counts_Stage.show();         
    }
    
     @FXML
    private void handleExec(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Exec_Stage.setScene(Exec_Scene);       
        Exec_Stage.centerOnScreen();
        Exec_Stage.setTitle("Executive Summary");
        Exec_Stage.show();         
    }
    
      @FXML
    private void handleShift(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Shift = FXMLLoader.load(getClass().getResource("ShiftEntry.fxml"));
        Scene Shift_Scene = new Scene(Shift);
        Stage Shift_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Shift_Stage.setScene(Shift_Scene);       
        Shift_Stage.centerOnScreen();
        Shift_Stage.setTitle("Shift Entry");
        Shift_Stage.show();         
    }
    
    @FXML
    private void handleViewButton(ActionEvent event) throws IOException {
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("ViewMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();  
        
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) throws IOException{ 
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();
    }
    
    /************************************ HANDLES ENDS ***************************************************/
    /******************************************************************************************************/
    
    // This Action Event is used for loading the excel file that you want to use 
    @FXML 
    private void loadFile(ActionEvent select) throws IOException, InvalidFormatException{
        
        FileChooser selectExcel = new FileChooser();
        selectExcel.getExtensionFilters().add(new ExtensionFilter("XLSX FILES", "*.xlsx"));
        selectExcel.getExtensionFilters().add(new ExtensionFilter("XLS FILES", "*.xls"));
        File tests = selectExcel.showOpenDialog(null);
        
        // To avoid any exception or nullpointer error, this condition is required
        if(tests == null){
            return;
        }
        
        // Copying from original to a temporary one so we could fiddle around with greater leeway
        File ori = new File(tests.getAbsolutePath());
        fileNamePath = tests.getAbsolutePath(); // Important! FILE_NAME holds the target's name/path
        File temp = new File(FILE_TEMP_NAME); 
        Files.copy(ori.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        
        // Then we set up temp file for the "Advance SORT" file simultaneously
        fileNameOnly = tests.getName();
        File ori2 = new File(FILE_NAME_EXT + fileNameOnly);
        File temp2 = new File(FILE_TEMP_NAME_ADV); 
        // Check if the "Advance SORT" file exist or not
        if(ori2.exists() && !ori2.isDirectory()){
            // If it exist
            Files.copy(ori2.toPath(), temp2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            temp2.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        } // End of if condition
        // If the "Advanced SORT" file does not exist, create a new one
        else{
            XSSFWorkbook workbook1 = new XSSFWorkbook();
            Sheet sheet1 = workbook1.createSheet("CCLog");
            sheet1 = workbook1.createSheet("Shift Entry");
            sheet1 = workbook1.createSheet("Executive Summary");
            // Writing out the changes
            FileOutputStream output_file = new FileOutputStream(new File(FILE_NAME_EXT + fileNameOnly));
            workbook1.write(output_file);
            // Closing files
            output_file.close();
            workbook1.close();
            // Don't forget to create a temp version of it
            Files.copy(ori2.toPath(), temp2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            temp2.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        }
        
        // Reset variables
        selectTest.setVisible(false);
        introLabel.setVisible(false);  
        checklistLabel.setVisible(true);
        currentVariantFilter = "";
        variantFilter.getItems().clear(); // Important! You don't want duplicates or old variant option to remain.
        headerJumpTo.getItems().clear();
        sheetSelector.getItems().clear(); // Only delete the sheets if you choose to open a different/same file
        hideLabels();
        resetTextFields();
        hideTextFields();
        // These two codes are unique since Test Mode is the only mode that prompts you to select the variant before allowing you to see the result yet
        hideArrowPage();
        
        getVariant(); // Get the list of variant first
        getSheet(); // Get list of sheets
        getHeader(); // Get all header for the "Find" menu
        askVariant.setVisible(true);
        saveItem.setDisable(false); // Enable the Save button in the Menu
        saveItemAs.setDisable(false); // Enable the Save As button in the Menu

        // Set file names for other files
        Storage.setNameTwice(fileNamePath, fileNameOnly);
        ViewModeController.setReset();
    }
    
    
    
    /************************************ ARROW ACTION BEGINS ***************************************************/
    /******This ActionEvent is found in the arrow button in the fxml file****************************************/
    
    @FXML 
    private void nextArrow1(ActionEvent event) throws IOException, InvalidFormatException{
        nextArrowFunction(1);
    }
    @FXML 
    private void nextArrow5(ActionEvent event) throws IOException, InvalidFormatException{
        nextArrowFunction(5);
    }
    @FXML 
    private void nextArrow10(ActionEvent event) throws IOException, InvalidFormatException{
        nextArrowFunction(10);
    }
    @FXML
    private void previousArrow1(ActionEvent event) throws IOException, InvalidFormatException{  
        previousArrowFunction(1);
    }
    @FXML
    private void previousArrow5(ActionEvent event) throws IOException, InvalidFormatException{  
        previousArrowFunction(5);
    }
    @FXML
    private void previousArrow10(ActionEvent event) throws IOException, InvalidFormatException{  
        previousArrowFunction(10);
    }
    // This function is essentially the "master" function for the next arrow that goes from 1, 5, or 10 pages 
    private void nextArrowFunction(int i) throws FileNotFoundException, IOException, InvalidFormatException{    
        for(int x = 0;x<i;x++)
        {
            if(alertBox() == true){
                hideLabels();

                if (condition == false || rowIndex == 0) 
                {
                    return; 
                    // If we have reached the end/limit, then do nothing
                }

                peekNextStep(); // Peek at the next available steps

                if (condition == false) // Check again
                {
                    return; // If we have reached the end, then do nothing
                }
                if(x==0){ // Saving your current page is only useful before you transition to the next page (not during the process)
                    savePage(); // Save info into tmp excel file first
                }
                hideTextFields();
                resetTextFields();
                readExcel();
                pageNumber.setText(Integer.toString(++currentPageNum)+"/"+Integer.toString(maxPageNum));
            }   
            else{
                break;
            }
        }
    }
    // This function is essentially the "master" function for the previous arrow that goes from 1, 5, or 10 pages 
    private void previousArrowFunction(int y) throws FileNotFoundException, IOException, InvalidFormatException{    
        for(int x = 0;x<y;x++)
        {
            if(alertBox() == true){
                hideLabels();
                //System.out.println("Previous Arrow button has been hit.\n Step index before decrement: "+stepIndex); // Debugging purpose
                if (stepIndex - NUM_PAGE < 0 || rowIndex == 0)
                {
                    return; // If there are no steps behind, do nothing
                }
                if(x==0){ // Saving your current page is only useful before you transition to the previous page (not during the process)
                    savePage(); // Save info into tmp excel file first
                }
                int tempIndex;
                condition = true;   // Since you've went back to a page before, you should be allowed to go to the next page again
                tempIndex = stepIndex - (NUM_PAGE + (stepIndex % NUM_PAGE) ); // Go back to previous page by locating the previous starting stepIndex
                // Remove the all of the record of the page that you were at before and after hitting the previous arrow button
                for(int i=tempIndex;i<=stepIndex;i++) 
                {
                    //System.out.println("Removing record: "+record.get(tempIndex)); // Debugging purpose
                    record.remove(tempIndex);
                }   
                hideTextFields();
                resetTextFields();
                newSH = false; // Went back to a page before, this boolean needs to be reset back to false
                newH = false;
                stepIndex = tempIndex - 1; // Turn stepIndex back in time (readjusting value)
                // Removing recordSH up until stepIndex point
                if(stepIndex < 0){
                    recordSH.clear(); // If stepIndex == -1, then just clear everything since we've essentially gone back to the beginning
                }
                else{
                    for(int i=recordSH.size()-1; i>=0; i--)
                    {
                        if(recordSH.get(i)>=record.get(stepIndex)){ // If subheader row index is higher (or equal to, for safety sake) than the teststep row index, remove!
                            recordSH.remove(i);
                        }
                        else{
                            break; // No need to go through all the ArrayList, since the row index should be in an ascending order
                        }
                    }
                }
                // Removing recordH up until stepIndex point, uses the same logic as above
                if(stepIndex < 0){
                    recordH.clear();
                }
                else{
                    for(int i=recordH.size()-1; i>=0; i--)
                    {
                        if(recordH.get(i)>=record.get(stepIndex)){ 
                            recordH.remove(i);
                        }
                        else{
                            break;
                        }
                    }
                }
                readExcel();
                pageNumber.setText(Integer.toString(--currentPageNum)+"/"+Integer.toString(maxPageNum));
            }
            else{
                break;
            }
        }
    }

    /************************************ ARROW ACTION ENDS ***************************************************/
    /******************************************************************************************************/
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        TestMenuBar.prefWidthProperty().bind(TestBox.widthProperty());
        // Will get the fileNamePath and fileNameOnly from Storage.java for consistency across all related java files
        fileNameOnly = Storage.getFileNameOnly();
        fileNamePath = Storage.getFileNamePath();
        
        /*
        *   The code below will attempt to automatically load FILE_TEMP_NAME if it exist
        *   The purpose is to essentially maintain a "persistent" state if the user 
        *   changes mode and come back
        *   Newest update will make the file loaded to be consistently pre-loaded across all mode, except for Edit Mode
        */
        File temp = new File(FILE_TEMP_NAME);
        File temp1 = new File(FILE_TEMP_NAME_ADV);
        // Check if temp file exist in the .jar directory
        if(temp.exists() && !temp.isDirectory()){ // Master Condition
            // Check if temp1 file exist in the .jar directory
            if(temp1.exists() && !temp1.isDirectory()){ // Master Condition 2 
                // Check if the user has merely loaded the file at the beginning, or if user has made any changes previously without closing
                // the GUI
                saveItem.setDisable(false); // Enable the Save button in the Menu
                saveItemAs.setDisable(false); // Enable the Save As button in the Menu
                if(currentVariantFilter.isEmpty() || executeReset == true){ // Condition A
                    executeReset = false; // Reset this variable
                    // This means that user has loaded this file at the beginning but have not yet selected any variant before switching mode
                    // Another case would be user has loaded the program for the first time and a temp file has already existed (program crashed earlier perhaps?) 
                    // Another case would be if user has a temp file has already existed and user has loaded/Save As a file in other .java files
                    // Will attempt to use the temp file for the GUI, 90% of this code is taken from loadFile()
                    try {
                        temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
                        temp1.deleteOnExit();
                        // Adjusting variables
                        selectTest.setVisible(false);
                        introLabel.setVisible(false);  
                        checklistLabel.setVisible(true);
                        // Resetting variables just to be safe
                        currentVariantFilter = ""; 
                        hideLabels();
                        resetTextFields();
                        hideTextFields();
                        hideArrowPage();
                        getVariant();
                        getSheet();
                        getHeader();
                        askVariant.setVisible(true);
                    } catch (IOException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidFormatException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } // End of Condition A

                else{ // Condition B
                    // This condition means that the user has loaded a file and have selected a variant. User then switched modes and came back to this mode
                    // Thus, code will attempt to use the static variables to load the GUI back to the previous state right before user left
                    FileInputStream sort = null;
                    try {
                        sort = new FileInputStream(new File(FILE_TEMP_NAME));
                        Workbook testMode = WorkbookFactory.create(sort);
                        Sheet stage0 =  testMode.getSheetAt(sheetIndex);
                        // Load the essential MenuItems
                        getHeader();
                        getSheet();
                        getVariant();
                        getPageNumber(); // Need to readjust the current page number if user is somewhere other than the first page (beginning)
                        selectTest.setVisible(false);
                        introLabel.setVisible(false);  
                        checklistLabel.setVisible(true);
                        // Readjusting current page number
                        if(stepIndex>4){ // If stepIndex <= 4, then the current page number is equal to 1, thus no changes are required
                            int temp2 = stepIndex+1; // stepIndex starting position is -1, therefore increment by 1 to adjust the value
                            int total = 0;
                            while(true){
                                if(temp2 - NUM_PAGE>=0){
                                    temp2 = temp2 - NUM_PAGE;
                                    total++;
                                }
                                else{
                                    if(temp2>0){
                                        total++;
                                        break;
                                    }
                                    else{
                                        break;
                                    }
                                }
                            }
                            // Update page number
                            currentPageNum = total;
                            pageNumber.setText(Integer.toString(currentPageNum)+"/"+Integer.toString(maxPageNum)); 
                        }
                        // Need to readjust variables to the current page to execute readExcel() properly
                        // These codes are taken from previousArrowFunction() with some readjustments
                        newSH = false;
                        newH = false;
                        condition = true;
                        int tempIndex;
                        tempIndex = stepIndex - (stepIndex % NUM_PAGE); // Read the current page
                        for(int i=tempIndex;i<=stepIndex;i++) 
                        {
                            record.remove(tempIndex);
                        }   
                        stepIndex = tempIndex - 1;
                        // Removing recordSH up until stepIndex point
                        if(stepIndex < 0){
                            recordSH.clear(); // If stepIndex == -1, then just clear everything since we've essentially gone back to the beginning
                        }
                        else{
                            for(int i=recordSH.size()-1; i>=0; i--)
                            {
                                if(recordSH.get(i)>=record.get(stepIndex)){ // If subheader row index is higher (or equal to, for safety sake) than the teststep row index, remove!
                                    recordSH.remove(i);
                                }
                                else{
                                    break; // No need to go through all the ArrayList, since the row index should be in an ascending order
                                }
                            }
                        }
                        // Removing recordH up until stepIndex point, uses the same logic as above
                        if(stepIndex < 0){
                            recordH.clear();
                        }
                        else{
                            for(int i=recordH.size()-1; i>=0; i--)
                            {
                                if(recordH.get(i)>=record.get(stepIndex)){ 
                                    recordH.remove(i);
                                }
                                else{
                                    break;
                                }
                            }
                        }
                        // Finally, execute readExcel()
                        readExcel();   
                        exposeArrowPage();
                        testMode.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidFormatException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (EncryptedDocumentException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            sort.close();
                        } catch (IOException ex) {
                            Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } // End of Condition B
            } // End of Master Condition 2
        } // End of Master Condition
    } // End of initialize    
    
    
    /**
     *All code below works as a custom function/method 
    */
    /************************************ GUI DETAILS BEGIN ***************************************************/
    /******************************************************************************************************/
    
    // This function saves whatever is on the screen into the tmp file 
    private void savePage() throws FileNotFoundException, IOException, InvalidFormatException{            
        
        FileInputStream file1 = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook workbook1 = WorkbookFactory.create(file1);
        Sheet sheet1 =  workbook1.getSheetAt(sheetIndex);
 
        int tempIndex = stepIndex - (stepIndex % NUM_PAGE); // Get the initial stepIndex of your current page (the "first" step in your current page)
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        
        CellStyle style = workbook1.createCellStyle();
        style.setWrapText(true);  
        
        if(testDescription1.isVisible())    // If testDescription1 Text Field is visible, then saves the text on it back to the temp excel file
        {
            row = sheet1.getRow(record.get(tempIndex));
            cell = row.createCell(TEST_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(testDescription1.getText());
            // Will also get the text/string on the comment1 Text Field box, regardless whether it's visible or not
            cell = row.createCell(COMMENT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(comment1.getText());
        }
        if(testDescription2.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+1));
            cell = row.createCell(TEST_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(testDescription2.getText());
           
            cell = row.createCell(COMMENT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(comment2.getText());
        }
        if(testDescription3.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+2));
            cell = row.createCell(TEST_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(testDescription3.getText());
           
            cell = row.createCell(COMMENT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(comment3.getText());
        }
        if(testDescription4.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+3));
            cell = row.createCell(TEST_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(testDescription4.getText());
           
            cell = row.createCell(COMMENT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(comment4.getText());
        }
        if(testDescription5.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+4));
            cell = row.createCell(TEST_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(testDescription5.getText());
           
            cell = row.createCell(COMMENT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(comment5.getText());
        }
        
        file1.close();
        //Open FileOutputStream to write updates
        FileOutputStream output_file = new FileOutputStream(new File(FILE_TEMP_NAME)); // For the purpose of this project, you must choose the same file that you read  
        //write changes
        workbook1.write(output_file);
        //close the stream
        output_file.close();
    }
   
    // This checks whether the specified cell in the excel file is empty or not
    private static boolean isCellEmpty(final Cell cell) { 
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) { // getCellType is deprecated (old-fashioned), hence either ignore or try to find the current "hip" way
            return true;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
            return true;
        }
        return false;
    }
    
    // Set labels to not visible
    private void hideLabels() { 
        if(labelSaved.isVisible())
            labelSaved.setVisible(false);
        labelStepsNotFound.setVisible(false);
    }
    
    // Set TextFields to not visible
    private void hideTextFields(){
        if(question1.isVisible())
            question1.setVisible(false);
        if(dropMenu1.isVisible())
            dropMenu1.setVisible(false);
        if(testDescription1.isVisible())
            testDescription1.setVisible(false);
        if(comment1.isVisible())
            comment1.setVisible(false);
        if(question2.isVisible())
            question2.setVisible(false);
        if(dropMenu2.isVisible())
            dropMenu2.setVisible(false);
        if(testDescription2.isVisible())
            testDescription2.setVisible(false);
        if(comment2.isVisible())
            comment2.setVisible(false);
        if(question3.isVisible())
            question3.setVisible(false);
        if(dropMenu3.isVisible())
            dropMenu3.setVisible(false);
        if(testDescription3.isVisible())
            testDescription3.setVisible(false);
        if(comment3.isVisible())
            comment3.setVisible(false);
        if(question4.isVisible())
            question4.setVisible(false);
        if(dropMenu4.isVisible())
            dropMenu4.setVisible(false);
        if(testDescription4.isVisible())
            testDescription4.setVisible(false);
        if(comment4.isVisible())
            comment4.setVisible(false);
        if(question5.isVisible())
            question5.setVisible(false);
        if(dropMenu5.isVisible())
            dropMenu5.setVisible(false);
        if(testDescription5.isVisible())
            testDescription5.setVisible(false);
        if(comment5.isVisible())
            comment5.setVisible(false);
        // Yes, they are technically labels, but ultimately they function more or less the same as those textfields
        id1.setVisible(false);
        id2.setVisible(false);
        id3.setVisible(false);
        id4.setVisible(false);
        id5.setVisible(false);
        subHead1.setVisible(false);
        subHead2.setVisible(false);
        subHead3.setVisible(false);
        subHead4.setVisible(false);
        subHead5.setVisible(false);
        header1.setVisible(false);
        header2.setVisible(false);
        header3.setVisible(false);
        header4.setVisible(false);
        header5.setVisible(false);
    }
    
    // Hides all the arrow button and the page number box
    private void hideArrowPage(){
        previousArrow1.setVisible(false);
        previousArrow5.setVisible(false);
        previousArrow10.setVisible(false);
        nextArrow1.setVisible(false);
        nextArrow5.setVisible(false);
        nextArrow10.setVisible(false);
        pageNumber.setVisible(false);
    }
    // Makes the arrow buttons and page number box visible again
    private void exposeArrowPage(){
        previousArrow1.setVisible(true);
        previousArrow5.setVisible(true);
        previousArrow10.setVisible(true);
        nextArrow1.setVisible(true);
        nextArrow5.setVisible(true);
        nextArrow10.setVisible(true);
        pageNumber.setVisible(true);
    }
    
    // Self-explanatory
    private void resetTextFields() { 
        question1.setText(""); 
        question2.setText("");
        question3.setText("");
        question4.setText("");
        question5.setText("");
        testDescription1.setText("");
        testDescription2.setText("");
        testDescription3.setText("");
        testDescription4.setText("");
        testDescription5.setText("");
        dropMenu1.setText("Choose action");
        dropMenu2.setText("Choose action");
        dropMenu3.setText("Choose action");
        dropMenu4.setText("Choose action");
        dropMenu5.setText("Choose action");
        comment1.setText("");
        comment2.setText("");
        comment3.setText("");
        comment4.setText("");
        comment5.setText("");
        id1.setText("");
        id2.setText("");
        id3.setText("");
        id4.setText("");
        id5.setText("");
        subHead1.setText("");
        subHead2.setText("");
        subHead3.setText("");
        subHead4.setText("");
        subHead5.setText("");
        header1.setText("");
        header2.setText("");
        header3.setText("");
        header4.setText("");
        header5.setText("");
    }
    
    
       /************************************ GUI DETAILS END ***************************************************/
    /******************************************************************************************************/
    
    
    
    
    
       /************************************ TEST DETAIL SELECTION BEGINS ***************************************************/
    /******************************************************************************************************/
    // This function will get all the number of available test steps and compute the total amount of pages that the user can traverse on the GUI
    // It will then populate the page number (max and current) onto the pageNumber TextField
    // This function should be executed only once after the file is loaded and/or when you've chosen another variant/sheet
    private void getPageNumber() throws IOException, InvalidFormatException{
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook testMode = WorkbookFactory.create(sort);
        Sheet stage0 =  testMode.getSheetAt(sheetIndex);     
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        int totalSteps=0;
        while(iterator.hasNext()){
            row = iterator.next();
            //Checks test step
            if(isCellEmpty(row.getCell(STEP_COLUMN)) == false)
            {
                //Conduct variant verification (Currently this variant verification is only found in TestModeController.java, other java files does not use variant verification)
                boolean variantCheck = false;
                if(isCellEmpty(row.getCell(VARIANT_COLUMN)) || row.getCell(VARIANT_COLUMN).getStringCellValue().equals("ALL")){
                    variantCheck = true;
                }
                else if(row.getCell(VARIANT_COLUMN).getStringCellValue().length() > 4){
                    String lump = row.getCell(VARIANT_COLUMN).getStringCellValue();
                    String clean[] = lump.split("[ \\,]");
                    for(String list: clean){
                        if(list.equals(currentVariantFilter)){
                            variantCheck = true;
                            break;
                        }
                    }
                }
                else{
                    if(currentVariantFilter.equals(row.getCell(VARIANT_COLUMN).getStringCellValue())){
                        variantCheck = true;
                    }
                }
                // Check for final result
                if(variantCheck == true){
                    totalSteps++; // Increment the totalSteps if a matching test step has been found
                }
            }
        }
        sort.close();
        testMode.close();
        // Now we calculate the total number of pages available
        int tempMaxPage=0;
        while(true){
            if(totalSteps - NUM_PAGE>=0){
                totalSteps = totalSteps - NUM_PAGE;
                tempMaxPage++;
            }
            else{
                if(totalSteps>0){
                    tempMaxPage++;
                    break;
                }
                else{
                    break;
                }
            }
        }
        maxPageNum = tempMaxPage;
        currentPageNum = 1; // Set the currentPageNum to 1 since this function is loaded at the beginning of the file (read from the beginning)
        pageNumber.setText(Integer.toString(currentPageNum)+"/"+Integer.toString(maxPageNum)); // Set text into the pageNumber TextField box
    }
    // This function gets the list of all headers available in HEADER_COLUMN and stores it in variantList ArrayList
    // This function populates the "Find" Menu with header(s) MenuItem and assign each of them their own ActionEvent
    private void getHeader() throws IOException, InvalidFormatException{
        ArrayList<String> headerList = new ArrayList<String>(); // This ArrayList stores all the header found in the excel file on HEADER_COLUMN
    
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook testMode = WorkbookFactory.create(sort);
        Sheet stage0 =  testMode.getSheetAt(sheetIndex);     
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        // Traversing through all rows and storing the text found in HEADER_COLUMN into headerList ArrayList
        while(iterator.hasNext()){
            row = iterator.next();
            if(!isCellEmpty(row.getCell(HEADER_COLUMN))){
                headerList.add(row.getCell(HEADER_COLUMN).getStringCellValue());
            }
        }
        headerList.remove(0); // Removing the first part (since on the excel file, they are usually the sheet's name i.e. "Stage 1")
        sort.close();   
        testMode.close();
        // headerList will be populated with all available header
        
        // Populating headerJumpTo (better known as the "Find" option on the GUI) with the header(s) stored in headerList ArrayList
        for(String header: headerList){
            // Splitting header to get the first sentence (since those header can get too long) by a new line or a period as the delimiter
            String firstSentence = header.split("\\n|\\.")[0];
            //System.out.println(firstSentence);
            //currentVariantFilter = firstWord;
            
            MenuItem headerOption = new MenuItem(firstSentence); // Creating instance of the menu item
            headerJumpTo.getItems().add(headerOption); // Adding menu item into the menu
            headerOption.setOnAction(new EventHandler<ActionEvent>() { // Giving the menu item its action event
                @Override public void handle(ActionEvent e)  {
                    try {
                        // Put code here
                        // Disallow user to proceed if currentVariantFilter is empty
                        if(variantAlert() == true){
                            return;
                        }
                        
                        if(alertBox() == true){ // Check for missing comments
                            // Since this would jump to the specified page location, do a soft reset
                            savePage(); // Don't forget to save current page before soft-resetting
                            rowIndex = 0;
                            condition = true;
                            stepIndex = -1;
                            record.clear();
                            recordSH.clear();
                            recordH.clear();
                            newSH = false;
                            newH = false;
                            currentPageNum = 0; // Set to 0 instead of 1, because we want to increment it naturally in the code below
                            // Actual meat of the code
                            boolean headerIsFound = false; // Only set to positive if the correct header is located
                            while(headerIsFound == false && condition == true){ 
                                if(alertBox() == true){ // Check again for missing comments
                                    // Check if there are any test steps still available for the program to traverse through
                                    peekNextStep();
                                    // If there are none left, condition = false
                                    if(condition == false) break; 
                                    hideLabels();
                                    resetTextFields();
                                    hideTextFields();
                                    readExcel();  // Read from excel
                                    pageNumber.setText(Integer.toString(++currentPageNum)+"/"+Integer.toString(maxPageNum));
                                    if(header1.isVisible()){
                                        if(header1.getText().equals(header)){
                                            headerIsFound = true;
                                            continue;
                                        }
                                    }
                                    if(header2.isVisible()){
                                        if(header2.getText().equals(header)){
                                            headerIsFound = true;
                                            continue;
                                        }
                                    }
                                    if(header3.isVisible()){
                                        if(header3.getText().equals(header)){
                                            headerIsFound = true;
                                            continue;
                                        }
                                    }
                                    if(header4.isVisible()){
                                        if(header4.getText().equals(header)){
                                            headerIsFound = true;
                                            continue;
                                        }
                                    }
                                    if(header5.isVisible()){
                                        if(header5.getText().equals(header)){
                                            headerIsFound = true;
                                            continue;
                                        }
                                    }
                                }
                                else{
                                    break;
                                }
                            }
                            if(condition == false) labelStepsNotFound.setVisible(true); // This simply means no test steps are found due to mismatching variant
                            exposeArrowPage();
                            // End of code
                        }
                    } catch (IOException | InvalidFormatException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
        }
        
    }// End of getHeader
    
    // This function would be executed once each time a file is loaded
    // This function would be similar to getVariant(), in which this would populate the "Select Sheet"/sheetSelector Menu with all of the sheets present in the excel file
    private void getSheet() throws FileNotFoundException, IOException, InvalidFormatException{
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook testMode = WorkbookFactory.create(sort);   
        
        // for each sheet in the workbook
        for (int i = 0; i < testMode.getNumberOfSheets(); i++) {
            int index = i;
            MenuItem sheetOption = new MenuItem(testMode.getSheetName(i)); // Creating instance
            sheetSelector.getItems().add(sheetOption); // Adding menu item into the menu
            sheetOption.setOnAction(new EventHandler<ActionEvent>() { // Giving the menu item its action event
                @Override public void handle(ActionEvent e)  {
                    try {
                        // Disallow user to proceed if currentVariantFilter is empty
                        if(variantAlert() == true){
                            return;
                        }
                        // Since this would change to a different sheet, do a soft reset
                        savePage(); // Don't forget to save current page before soft-resetting
                        sheetIndex = index; // Set the sheetIndex (must be done after savePage, lest you save it in the wrong sheet)
                        rowIndex = 0;
                        condition = true;
                        stepIndex = -1;
                        record.clear();
                        recordSH.clear();
                        recordH.clear();
                        headerJumpTo.getItems().clear(); // Important! You don't want duplicates or old header option to remain.
                        variantFilter.getItems().clear(); // This line is unique to Test Steps mode
                        newSH = false;
                        newH = false;
                        hideLabels();
                        resetTextFields();
                        hideTextFields();
                        getVariant(); // Get the list of variant first
                        getHeader(); // Get all header for the "Find" menu
                        getPageNumber();
                        readExcel();   
                        exposeArrowPage();
                       
                    } catch (IOException | InvalidFormatException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        sort.close();
        testMode.close();
        return;
    }
    
    // This function would be executed once each time a file is loaded
    // This function gets the list of all variants available in VARIANT_LIST_COLUMN and stores it in variantList ArrayList
    // This function populates the "Select Variant"/variantFilter Menu with the variant(s) MenuItem and assign each of them their own ActionEvent
    private void getVariant() throws IOException, InvalidFormatException{
        ArrayList<String> variantList = new ArrayList<String>(); // This ArrayList stores all the variant found in the excel file on VARIANT_LIST_COLUMN
        boolean readVariant = false;
        
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook testMode = WorkbookFactory.create(sort);
        Sheet stage0 =  testMode.getSheetAt(sheetIndex);     
        
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        
        //Search for the word "Variant" first in VARIANT_LIST_COLUMN
        while(iterator.hasNext() && readVariant == false)
        {
            row = iterator.next();
            if(isCellEmpty(row.getCell(VARIANT_LIST_COLUMN)) == false)
            {
                if(row.getCell(VARIANT_LIST_COLUMN).getStringCellValue().equals("Variant")){
                    readVariant = true;
                }
            }
        }
        if(readVariant == true){ // If the word "Variant" has been found
            while(iterator.hasNext()){
                row = iterator.next();
                if(!isCellEmpty(row.getCell(VARIANT_LIST_COLUMN))){
                    variantList.add(row.getCell(VARIANT_LIST_COLUMN).getStringCellValue());
                }
            }
        }
    
        sort.close();   
        testMode.close();
        // Several outcome after this part
        // 1. Variants are found and stored in variantList array
        // 2. The word "Variant" is not found and thus, variantList is empty
        // 3. The word "Variant" is found but there are nothing below (distance does not matter, only content) and thus, variantList is empty
        
        // Populating variantOption (better known as the "Select Variant" option on the GUI) with the variant(s) stored in variantList ArrayList
        for(String variant: variantList){
            MenuItem variantOption = new MenuItem(variant); // Creating instance
            variantFilter.getItems().add(variantOption); // Adding menu item into the menu
            variantOption.setOnAction(new EventHandler<ActionEvent>() { // Giving the menu item its action event
                @Override public void handle(ActionEvent e)  {
                    try {
                        // Put code here
                        selectVariant(variant);
                    } catch (IOException | InvalidFormatException ex) {
                        Logger.getLogger(TestModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }
    
    // This function is a continuation from getVariant()
    private void selectVariant(String variant) throws IOException, InvalidFormatException{
        askVariant.setVisible(false);       
        // Splitting variant to get the first word. Example: V(1)-Stealth bomber --> V(1)
        String firstWord = variant.split("[ \\-]")[0];
        currentVariantFilter = firstWord;
        //System.out.println(currentVariantFilter+"||"); // Debugging
        
        // Do a "soft reset" (don't reset everything!) if one were to select/change variant
        // Since you would essentially need to start from the beginning if you change variant
        savePage(); // Don't forget to save current page before soft-resetting
        rowIndex = 0;
        condition = true;
        stepIndex = -1;
        record.clear();
        recordSH.clear();
        recordH.clear();
        newSH = false;
        newH = false;
        hideLabels();
        resetTextFields();
        hideTextFields();
        getPageNumber();
        readExcel();   
        exposeArrowPage();
    }
    
    // This function is to read the excel file and populate it on the GUI
    private void readExcel() throws IOException, InvalidFormatException{       
        DataFormatter df = new DataFormatter(); // This is a data formatter which is useful to convert cell types into anything you want. In this case, String.
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME));
        Workbook testMode = WorkbookFactory.create(sort);
        Sheet stage0 =  testMode.getSheetAt(sheetIndex);    
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        
        //This section is to cycle the iterator to the next available row (including cycling through old rows that have been read, if any)
        //This is set up due to the iterator's inability to jump to a specified row index as a starting point (always start at the beginning)
        for(int i=0;i<=(stepIndex+1);)
        {
            if(iterator.hasNext())
            {
                row = iterator.next();
                
                //Checks if the cell in header is empty or not first
                if(isCellEmpty(row.getCell(HEADER_COLUMN)) == false && i == stepIndex+1) // i == stepIndex+1 is where the iterator is 
                {                                                                        // in the middle of cycling to the next new (previously unexplored) row, if any exist
                    recordH.add(row.getRowNum());
                    newH = true; // If found a new subheader, then turn boolean value into true
                }
                
                //Checks subheader
                else if(isCellEmpty(row.getCell(SUBHEADER_COLUMN)) == false && i == stepIndex+1) // i == stepIndex+1 is where the iterator is 
                {                                                                           // in the middle of cycling to the next new row, if any exist
                    recordSH.add(row.getRowNum());
                    //System.out.println("Begin: "+recordSH+" End!"); // Debugging
                    newSH = true; // If found a new subheader, then turn boolean value into true
                }
                //Checks test step
                else if(isCellEmpty(row.getCell(STEP_COLUMN)) == false)
                {
                    //Conduct variant verification (Currently this variant verification is only found in TestModeController.java, other java files does not use variant verification)
                    boolean variantCheck = false;
                    if(isCellEmpty(row.getCell(VARIANT_COLUMN)) || row.getCell(VARIANT_COLUMN).getStringCellValue().equals("ALL")){
                        variantCheck = true;
                    }
                    else if(row.getCell(VARIANT_COLUMN).getStringCellValue().length() > 4){
                        String lump = row.getCell(VARIANT_COLUMN).getStringCellValue();
                        String clean[] = lump.split("[ \\,]");
                        for(String list: clean){
                            if(list.equals(currentVariantFilter)){
                                variantCheck = true;
                                break;
                            }
                        }
                    }
                    else{
                        if(currentVariantFilter.equals(row.getCell(VARIANT_COLUMN).getStringCellValue())){
                            variantCheck = true;
                        }
                    }
                    // Check for final result
                    if(variantCheck == true){
                        i++; // Increment the index if and only if steps are found in that row and column, and if it has a matching variant
                    }
                    rowIndex = row.getRowNum();
                }
            }
            else // If there's nothing to load
            {
                condition = false;
                return; // Should not go here at all, unless file is missing content or there is some serious logic error in this file
            }
        }
        
        // This section deals with the program attempting to read information from the excel file and writing it into the GUI
        while(question5.getText().isEmpty() && condition == true)
        {   
            row = stage0.getRow(rowIndex); // Initializing row
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(STEP_COLUMN); // cell variable will be used in the step column section
            
            // This section will check for cells in the header column
            if(isCellEmpty(row.getCell(HEADER_COLUMN)) == false){
                recordH.add(rowIndex);
                //System.out.println("Added: "+rowIndex); // Debugging
                newH = true; // If found a new header, then turn boolean value into true
            }
            
            // This section will check for cells in subheader column
            else if(isCellEmpty(row.getCell(SUBHEADER_COLUMN)) == false){
                recordSH.add(rowIndex);
                //System.out.println("Added: "+rowIndex); // Debugging
                newSH = true; // If found a new subheader, then turn boolean value into true
            }
            
            // This section will check for cells in step column
            else if(isCellEmpty(cell) == false) // Check if the cell in that column is empty or not, if it's not empty then enter the if condit
            {
                //Conduct variant verification
                boolean variantCheck = false;
                // If variant cell is empty or contains "ALL", it gets a free pass
                if(isCellEmpty(row.getCell(VARIANT_COLUMN)) || row.getCell(VARIANT_COLUMN).getStringCellValue().equals("ALL")){
                    variantCheck = true;
                }
                // Check if there is a single or multiple variant in the VARIANT_COLUMN
                else if(row.getCell(VARIANT_COLUMN).getStringCellValue().length() > 4){
                    String lump = row.getCell(VARIANT_COLUMN).getStringCellValue();
                    String clean[] = lump.split("[ \\,]");
                    for(String list: clean){
                        if(list.equals(currentVariantFilter)){
                            variantCheck = true;
                            break;
                        }
                    }
                }
                else{
                    if(currentVariantFilter.equals(row.getCell(VARIANT_COLUMN).getStringCellValue())){
                        variantCheck = true;
                    }
                }
                
                // If the step's variant matches with the currentVariantFilter
                if(variantCheck == true){
                    stepIndex++; // Incrementing index (to 0 if first time)
                    record.add(stepIndex,rowIndex); // Adding this particular row index to the array list (stepIndex is quite redundant, but it is used here to emphasize how the logic works)
                    //System.out.println("Reading row: "+ rowIndex); // Debugging purpose

                    if(question1.getText().isEmpty())
                    {      
                        question1.setText(cell.getStringCellValue());
                        question1.setVisible(true);
                        dropMenu1.setVisible(true);
                        if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                        {
                            testDescription1.setVisible(true);
                            testDescription1.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                            dropMenu1.setText(testDescription1.getText());
                            
                            comment1.setVisible(true);
                            if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                            {
                                comment1.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                            }
                        }
                        // Onward with the id column section
                        if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                        {
                            id1.setVisible(true);
                            String tempString = df.formatCellValue(row.getCell(ID_COLUMN)); // DataFormatter is needed because there is a high chance that the id can show up as numeric when entered
                                                                                            // manually in the excel (if inputted purely as numbers)
                            id1.setText(tempString);
                        }
                        
                        // Moving on to the subheader display
                        if(newSH == true){ // If there is subheader found before 
                            int closestIndex = recordSH.get(recordSH.size()-1); // The last position in the ArrayList will always contain the closest subheader
                            // Now we display the subheader
                            subHead1.setVisible(true);
                            subHead1.setText(stage0.getRow(closestIndex).getCell(SUBHEADER_COLUMN).getStringCellValue());
                            newSH = false; // Set this to false after using it
                        }
                        // Last but not least, the header display
                        // Attention! For the first box in the page, you would ideally want to always just find the nearest header (from current row index to the top only)
                        if(recordH.size()>0){ // If there is header present
                            int closestIndex = recordH.get(recordH.size()-1); // The last position in the ArrayList will always contain the closest subheader
                            // Now we display the header
                            header1.setVisible(true);
                            header1.setText(stage0.getRow(closestIndex).getCell(HEADER_COLUMN).getStringCellValue());
                            newH = false; // Set this to false after using it
                        }
                    }
                    else if (question2.getText().isEmpty())
                    {
                        question2.setText(cell.getStringCellValue());
                        question2.setVisible(true);
                        dropMenu2.setVisible(true);
                        if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                        {
                            testDescription2.setVisible(true);
                            testDescription2.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                            dropMenu2.setText(testDescription2.getText());
                            comment2.setVisible(true);
                            if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                            {
                                comment2.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                            }
                        }
                        if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                        {
                            id2.setVisible(true);
                            String tempString = df.formatCellValue(row.getCell(ID_COLUMN)); 
                            id2.setText(tempString);
                        }
                        if(newSH == true){
                            int closestIndex = recordSH.get(recordSH.size()-1);
                            // Now we display the subheader
                            subHead2.setVisible(true);
                            subHead2.setText(stage0.getRow(closestIndex).getCell(SUBHEADER_COLUMN).getStringCellValue());
                            newSH = false; // Set this to false after using it
                        }
                        // Condition on from the second box and onward is different from the first one
                        if(newH == true){ // If there is header found before
                            int closestIndex = recordH.get(recordH.size()-1); // The last position in the ArrayList will always contain the closest subheader
                            // Now we display the header
                            header2.setVisible(true);
                            header2.setText(stage0.getRow(closestIndex).getCell(HEADER_COLUMN).getStringCellValue());
                            newH = false; // Set this to false after using it
                        }
                    }
                    else if (question3.getText().isEmpty())
                    {
                        question3.setText(cell.getStringCellValue());
                        question3.setVisible(true);
                        dropMenu3.setVisible(true);
                        if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                        {
                            testDescription3.setVisible(true);
                            testDescription3.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                            dropMenu3.setText(testDescription3.getText());
                            comment3.setVisible(true);
                            if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                            {
                                comment3.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                            }
                        }
                        if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                        {
                            id3.setVisible(true);
                            String tempString = df.formatCellValue(row.getCell(ID_COLUMN)); 
                            id3.setText(tempString);
                        }
                        if(newSH == true){
                            int closestIndex = recordSH.get(recordSH.size()-1);
                            // Now we display the subheader
                            subHead3.setVisible(true);
                            subHead3.setText(stage0.getRow(closestIndex).getCell(SUBHEADER_COLUMN).getStringCellValue());
                            newSH = false; // Set this to false after using it
                        }
                        if(newH == true){ // If there is header found before
                            int closestIndex = recordH.get(recordH.size()-1); // The last position in the ArrayList will always contain the closest subheader
                            // Now we display the header
                            header3.setVisible(true);
                            header3.setText(stage0.getRow(closestIndex).getCell(HEADER_COLUMN).getStringCellValue());
                            newH = false; // Set this to false after using it
                        }
                    }
                    else if (question4.getText().isEmpty())
                    {
                        question4.setText(cell.getStringCellValue());
                        question4.setVisible(true);
                        dropMenu4.setVisible(true);
                        if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                        {
                            testDescription4.setVisible(true);
                            testDescription4.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                            dropMenu4.setText(testDescription4.getText());
                            comment4.setVisible(true);
                            if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                            {
                                comment4.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                            }
                        }
                        if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                        {
                            id4.setVisible(true);
                            String tempString = df.formatCellValue(row.getCell(ID_COLUMN)); 
                            id4.setText(tempString);
                        }
                        if(newSH == true){
                            int closestIndex = recordSH.get(recordSH.size()-1);
                            // Now we display the subheader
                            subHead4.setVisible(true);
                            subHead4.setText(stage0.getRow(closestIndex).getCell(SUBHEADER_COLUMN).getStringCellValue());
                            newSH = false; // Set this to false after using it
                        }
                        if(newH == true){ // If there is header found before
                            int closestIndex = recordH.get(recordH.size()-1); // The last position in the ArrayList will always contain the closest subheader
                            // Now we display the header
                            header4.setVisible(true);
                            header4.setText(stage0.getRow(closestIndex).getCell(HEADER_COLUMN).getStringCellValue());
                            newH = false; // Set this to false after using it
                        }
                    }
                    else if (question5.getText().isEmpty())
                    {
                        question5.setText(cell.getStringCellValue());
                        question5.setVisible(true);
                        dropMenu5.setVisible(true);
                        if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                        {
                            testDescription5.setVisible(true);
                            testDescription5.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                            dropMenu5.setText(testDescription5.getText());
                            comment5.setVisible(true);
                            if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                            {
                                comment5.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                            }
                        }
                        if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                        {
                            id5.setVisible(true);
                            String tempString = df.formatCellValue(row.getCell(ID_COLUMN)); 
                            id5.setText(tempString);
                        }
                        if(newSH == true){
                            int closestIndex = recordSH.get(recordSH.size()-1);
                            // Now we display the subheader
                            subHead5.setVisible(true);
                            subHead5.setText(stage0.getRow(closestIndex).getCell(SUBHEADER_COLUMN).getStringCellValue());
                            newSH = false; // Set this to false after using it
                        }
                        if(newH == true){ // If there is header found before
                            int closestIndex = recordH.get(recordH.size()-1); // The last position in the ArrayList will always contain the closest subheader
                            // Now we display the header
                            header5.setVisible(true);
                            header5.setText(stage0.getRow(closestIndex).getCell(HEADER_COLUMN).getStringCellValue());
                            newH = false; // Set this to false after using it
                        }
                    }
                }
            }
            //rowIndex++; // Whether the cell is empty or not empty, check next row
            if(iterator.hasNext()) // Check for the next non-null row
            {
                row = iterator.next();
                rowIndex = row.getRowNum(); // We are not checking the cell in the column yet, just get the non-null row index
            }
            else{
                condition = false; // If there's nothing more to load
            }
        }
        sort.close();
        testMode.close();
    }
    
    private void peekNextStep() throws FileNotFoundException, IOException, InvalidFormatException // This function is to check whether there is a next step at all
    {    
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook testMode = WorkbookFactory.create(sort);
        Sheet stage0 =  testMode.getSheetAt(sheetIndex);
        
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        
        for(int i=0;i<=(stepIndex+1);)
        {
            if(iterator.hasNext())
            {
                row = iterator.next();
                if(isCellEmpty(row.getCell(STEP_COLUMN)) == false)
                {
                    //Conduct variant verification
                    boolean variantCheck = false;
                    if(isCellEmpty(row.getCell(VARIANT_COLUMN)) || row.getCell(VARIANT_COLUMN).getStringCellValue().equals("ALL")){
                        variantCheck = true;
                    }
                    else if(row.getCell(VARIANT_COLUMN).getStringCellValue().length() > 4){
                        String lump = row.getCell(VARIANT_COLUMN).getStringCellValue();
                        String clean[] = lump.split("[ \\,]");
                        for(String list: clean){
                            if(list.equals(currentVariantFilter)){
                                variantCheck = true;
                                break;
                            }
                        }
                    }
                    else{
                        if(currentVariantFilter.equals(row.getCell(VARIANT_COLUMN).getStringCellValue())){
                            variantCheck = true;
                        }
                    }
                    // Check for final result
                    if(variantCheck == true){
                        i++; // Increment the index if and only if steps are found in that row and column, and if it has a matching variant
                    }
                }
            }
            else // If there's no more rows to be found (means no more steps to be found)
            {
                condition = false;
                break;
            }
        }
        // There is a next step if your code reach this point and condition remains as true
        sort.close();
        testMode.close();
    }

    // This function checks if the comment box is visible and if the test description has the value failed/not tested
    // If it does, then disallow user from moving until it is filled
    private boolean alertBox()
    {
        boolean flag = true;
        // Checks if the comment box is visible and if the test description has the value failed/not tested
        // If it does, then disallow user from moving until it is filled
        if(comment1.isVisible() && !testDescription1.getText().equals("Passed")){
            if(comment1.getText().isEmpty() || comment1.getText().trim().length() == 0) flag = false;
        }
        if(comment2.isVisible() && !testDescription2.getText().equals("Passed")){
            if(comment2.getText().isEmpty() || comment2.getText().trim().length() == 0) flag = false;
        }
        if(comment3.isVisible() && !testDescription3.getText().equals("Passed")){
            if(comment3.getText().isEmpty() || comment3.getText().trim().length() == 0) flag = false;
        }
        if(comment4.isVisible() && !testDescription4.getText().equals("Passed")){
            if(comment4.getText().isEmpty() || comment4.getText().trim().length() == 0) flag = false;
        }
        if(comment5.isVisible() && !testDescription5.getText().equals("Passed")){
            if(comment5.getText().isEmpty() || comment5.getText().trim().length() == 0) flag = false;
        }
        // Shows an popup message to disallow user from changing page
        if (flag == false){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Halt!");
            alert.setHeaderText("Missing Comments!");
            alert.setContentText("Please fill in the missing comments for failed, or not tested steps!");
            alert.showAndWait();
            return false;
        }
        else
        {
            return true;
        }
    }
    
    // Disallow user to proceed if currentVariantFilter is empty
    private boolean variantAlert()
    {
        if(currentVariantFilter == null || currentVariantFilter.isEmpty()){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Halt!");
            alert.setHeaderText("Missing Variant!");
            alert.setContentText("Please select a variant first before proceeding!");
            alert.showAndWait();
            return true;
        }
        return false;
    }
    
    // executeReset is checked at initialize
    static public void setReset(){
        executeReset = true;
    }
}  
   
   /************************************ TEST DETAIL SELECTION ENDS ***************************************************/
    /******************************************************************************************************/
                                                                                                                                                                                                       
                                                                                                                                                                                                        
                                                                                                                                                                                                        
//                                                           @@                                                                                                                                           
//                                                          #;+@@@;                                                                                                                                       
//                                                          ';;@:;@#                                                                                                                                      
//                                                          ;;;'+::@'                                                                                                                                     
//                                                         ,;;;;@:::@                                                                                                                                     
//                                                         ';;;;@;::''                                                                                                                                    
//                                                         #;;;;@':::@                                                                                                                                    
//                                                         @;;;;@':::@                                                                                                                                    
//                                                         @;;;;#':::;,                                                                                                                                   
//                                                         @;;;;+'::::#                                                                                                                                   
//                                                         @;;;;+'::::@                                                                                                                                   
//                                                         #;;;;#'::::@                                                                                                                                   
//                                                         #;;;;@'::::@                                                                                                                                   
//                                                         ;;;;;@'::::#                                                                                                                                   
//                                                         `';;;@'::::+                                                                                                                                   
//                                                          @;;;#'::::'                                                                                                                                   
//                                                          @;;@'';:::;`                                                                                                                                  
//                                                          :#;@''':::;.                                                                                                                                  
//                                                           @@@@#'::::,                                                                                                                                  
//                                                               @'::::,                                                                                                                                  
//                                                               .#;:::,                                                                                                                                  
//                                                                @;:::,                                                                                                                                  
//                                                                @':::,                                                                                                                                  
//                                                                #':::,                                                                                                                                  
//                                                                ;':::,                                                                                                                                  
//                                                                ,'::;,                                                                                                                                  
//                                                                 +::;.                                                                                                                                  
//                                                                 +::;`                                                                                                                                  
//                                                                 #::;`                                                                                                                                  
//                                                                 #;:'`                                                                                                                                  
//                                                                 #;:'                                                                                                                                   
//                                                                 #':+                                                                                                                                   
//                                                                 #':+                                                                                                                                   
//                                                                 #':'                                                                                                                                   
//                                                                 #'';  .'@@@@@@@@@@@@@@+:`                                                                                                              
//                                                                 +''#@@@@+:.````````.:+#@@@@#,                                                                                                          
//                                                                 #@@@:`````````````````````:#@@@+`                                                                                                      
//                                                               #@@:``````````......@@@'````````,#@@#`                                                                                                   
//                                                             +@@.``````...........@';;@+``````````.@@@'                                                                                                 
//                                                           ,@@``````.............@;;;;;@,...`````````:@@@                                                                                               
//                                                          #@,`````...............@;;@@;;@......`````````#@+                                                                                             
//                                                         @@`````................,';@@#@;@.........````````@@                                                                                            
//                                                        @'`````.................+;@:,@#';#..........```````'@                                                                                           
//                                                       @'````...................@;+,,@+#;@............``````,@                                                                                          
//                                                      #+````....................@;,,,@+@;@...............````,@                                                                                         
//                                                      @````.....................@;,,,@+@;@.................```'#                                                                                        
//                                                     #`````.....................@;,,,@+@;@..................```@.                                                                                       
//                                                     @````......................@;@,,@++;@...................```@                                                                                       
//                                                    `'```.......................';@:,@#;;@.....................`+:                                                                                      
//                                                    +````........................@;@##@;;+......................`@                                                                                      
//                                                    @```.........................@;;@@;;@.......................`+.                                                                                     
//                                                    @``...........................@;;;;#@........................`@                                                :@#                                  
//                                                    +``...........................:@@#@@..........................@                                               '@+@@                                 
//                                                   .,``.............................#@'...........................,;                                              @;'+@:                                
//                                                   ;``.............................................................@                                             @;'+++@                                
//                                                   @``.............................................................@                                             @;++++#`                               
//                                                   @`.................,,;'##@@#+':,................................:,                                           +''++++++                               
//                                                   @`............+@@@@@@@@@@@@@@@@@@@@@#'...........................@                                           @;++++++@                               
//                                                   @`.......,#@@@@+;,,,,,,,:::::::::::'#@@@@@:......................@                                           @;++++++@                               
//                                                   @`....,#@@@;,,,,,,::::::::::::::::::::::'#@@@#,..................#                                          `+'++++++@                               
//                                                   +...#@@#,,,,,,::::::::::::::::::::::::::::::'@@@@:...............:.                                         +;+++++++#                               
//                                                   ,;@@#,,,,,,:::::::::::::::::::::::::::::::::::::#@@@,.............#                                         @;++++++++.                              
//                                                  ;@@',,,,,::::::::::::::::::::::::::::::::::::::::::;@@@+...........@                                         @;++++++++:                              
//                                                :@@:,,,,,:::::::::::::::::::::::::::::::::::::::::::::::;@@@,........@                                         @;++++++++'                              
//                                               @@:,,,,,::::::::::::::::::::::::::::::::::::::::::::::::::::#@@'......+                                         @'++++++++#                              
//                                             '@+,,,,,::::::::::::::::::::::::::::::::::::::::::::::::::::::::'@@#....,`                                        @'++++++++@                              
//                                            @@,,,,,::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;@@@...,                                        #'++++++++@                              
//                                          ,@+,,,,,::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::#@@,;                                        +'++++++++@                              
//                                         +@,,,,,::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+@@.                                       +'++++++++@                              
//                                        @@,,,,,:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+@@`                                    `+'++++++++@                              
//                                       @#,,,,:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+@@`                                  `+'++++++++@                              
//                                      @+,,,,::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::#@#                                 `+'++++++++@                              
//                                     @+,,,,::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.#@'                                +'++++++++@                              
//                                    @:#,,,:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@..@@.                              #'++++++++@                              
//                                   @:`.',::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;+..;@#                             #'++++++++@                              
//                                  @:```@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@....#@,                           @'++++++++@                              
//                                 @;```.#::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::#.....,@#                          @'++++++++@                              
//                                @+```...+::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@......@@`                        @;++++++++@                              
//                               +@```....@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@.......;@;                       @;++++++++@                              
//                              .@````....@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::#.........@@                      @;++++++++@                              
//                              @````.....;;::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'.........#@                     #;++++++++@                              
//                             @.```.......#::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@..........'@`                   ';++++++++#                              
//                            ++```........@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@............@:                  .'+++++++++                              
//                           `@````........@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@.............@'                  +'+++++++;                              
//                           @````......,@.+::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'..............@#                 @'+++++++,                              
//                          +'```.......#@.,'::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'...'..........@#                @'+++++++.                              
//                          @````......::;..#::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@..@@...........@@               @;++++++#                               
//                         @````.......::#..@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@..+:'...........@@              #;++++++#                               
//                        .@```.........@@..@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@..+,+............@@             :'++++++@                               
//                        @````.........@'..@:::::::::::::::::::::::::::::::::::::::::::::::::'+'::::::::::::::::::::::::::::::::@..@@:.............#@             +++++++@                               
//                       ,#```..............+::::::::::::::::::::::::::::::::::::::::::::::'@@@@@@@::::::::::::::::::::::::::::::+...:...............@#            @'+++++@                               
//                       @````..............::::::::::::::::::::::::::::::::::::::::::::::@@',,,,,#@;::::::::::::::::::::::::::::;,...................@#           @'+++++@                               
//                      .+```................':::::::::;';:::::::::::::::::::::::::::::::@@,:;;;;;::@;::::::::::::::::::::::::::::;....................@'          @;++++++                               
//                      @````................+:::::::#@@@@@@::::::::::::::::::::::::::::+@,;;;;;;;;;:@::::::::::::::::::::::::::::+.....................@,         ';+++++,                               
//                      #```.................@:::::;@@,,:::@@:::::::::::::::::::::::::::@,:;;;;;;;;;;;@:::::::::::::::::::::::::::@......................@`        `+++++#                                
//                     @````.................@:::::@;,:;;;;;'@:::::::::::::::::::::::::@;:;;;;;;;;;;;;#+::::::::::::::::::::::::::@......................:@         @++++@                                
//                     @````.................@::::@:,;;;;;;;;+@::::::::::::::::::::::::@,;;;;'##;;;;;;;@::::::::::::::::::::::::::@.......................'@        @'+++@                                
//                    ::```..................@:::#',;;;;;;;;;;@;::::::::::::::::::::::#;:;;;@@@@@@;;;;;+':::::::::::::::::::::::::@.......................,#'       @;+++@                                
//                    @````..................@:::#,;;'@@@;;;;;;@::::::::::::::::::::::@,;;;@@@@@+@@;;;;;@:::::::::::::::::::::::::@.......................,,@`      ';+++@                                
//                    @```...................@::+,:;+@@@@@#;;;;@;:::::::::::::::::::::@,;;@@::::@++@;;;;@:::::::::::::::::::::::::@........................,:@       +++++                                
//                   :,```...................@::@,;;@#;'@@@#;;;;@::::::::::::::::::::'::;;@':::,:@+#@;;;;+::::::::::::::::::::::::@........................,,+'      @+++,                                
//                   @````...................#:#::;@::::,#@@';;;@::::::::::::::::::::@,;;'@:::,,,@++@;;;;@::::::::::::::::::::::::#........................,,,@      @++#                                 
//                   @```....................+:@,;+':::,,,@+@;;;@;:::::::::::::::::::@,;;@::::,,,,@++@;;;@::::::::::::::::::::::::#........................,,,:@     @'+@                                 
//                   '```....................':@,;@:::,,,,,@#';;'':::::::::::::::::::@:;;@:::,,,,,@++@;;;#::::::::::::::::::::::::+........................,,,,@     :'+@                                 
//                  ;````....................':',;@::,,,,,,@+@;;;#:::::::::::::::::::#:;;':::,,,,,'++++;;'::::::::::::::::::::::::+.........................,,,:@     #+@                                 
//                  @````....................;':,;+::,,,,,,'+@:;;@:::::::::::::::::::+:;#:::,,,,,,,@++@;;;':::::::::::::::::::::::'...+.....................,,,,@     @++                                 
//                  @```...................;.:+,:;'::,,,,,,,@+;;;@:::::::::::::::::::';;@:::,,,,,,,@++@;;;+:::::::::::::::::::::::'..@@@....................,,,,:'    @+.                                 
//                  @```..................@@.:#,:;':,,,,,,,,@++;;@;:::::::::::::::::;;;;@:::,,,,,,,@++@;;;#:::::::::::::::::::::::'..@,@....................,,,,,@    ;#,`                                
//                  ;```.................;,@.:@,:;':,,,,,,,,@+#;;@':::::::::::::::::;;;;@::,,,,,,,,#++@:;;#:::::::::::::::::::::::'..@,#....................,,,,,@    :@@@@                               
//                 ..```.................+,@.:#,:;#:,,,,,,,,@+#:;@'::::::::::::::::::';;@::,,,,,,,,'++#:;;#:::::::::::::::::::::::'..@#@....................,,,,,:``#@@;;;#@                              
//                 '```..................,#@.:#,:;@:,,,,,,,,@++:;@'::::::::::::::::::+;;@::,,,,,,,,:++#,;;#:::::::::::::::::::::::'...@:....................,,,,,,@@#::;;;;@.                             
//                 #```...................@..:+,:;@:,,,,,,,,#+::;@'::::::::::::::::::#;;@::,,,,,,,,:++@,;;+:::::::::::::::::::::::'.........................,,,,,,@:::;;;;;;@                             
//                 @```......................:;;:;#:,,,,,,,++@,:;@'::::::::::::::::::@;;@::,,,,,,,,:++@,;;':::::::::::::::::::::::'.........................,,,,,,@:::';;;;;@                             
//                 @```......................;:+,;;@,,,,,,,@+@,;;@'::::::::::::::::::@:;'':,,,,,,,,;++@,;';:::::::::::::::::::::::'.........................,,,,,,@:::+;;;;;+                             
//                 @```......................;:@,;;@,,,,,,;#@:,;;@'::::::::::::::::::@:;;@:,,,,,,,,+++@,;#::::::::::::::::::::::::'.........................,,,,,,@:::+;;;;;'`                            
//                 @```......................':@,;;;@,,,,,@+@,,;;+'::::::::::::::::::#:;;@:,,,,,,,,@++;:;@::::::::::::::::::::::::+.........................,,,,,,@:::';;;;;+                             
//                 @```......................':@,;;;@+,,:@+@,,:;+''::::::::::::::::::'+;;;@,,,,,,,,@+@,:;@::::::::::::::::::::::::#.........................,,,,,,@:::;';;;;@                             
//                 @``.......................+:';;;;;@@@@#@;,,;;@''::::::::::::::::::'@;;;@;,,,,,,:#+@,;;@::::::::::::::::::::::::#.........................,,,,,,@@@@#@;;;@+                             
//                 @``.......................#::@:;;;:#@@@:,,:;;@''::::::::::::::::::'@:;;:@:,,,,,@+@,,;''::::::::::::::::::::::::@.........................,,,,,,@`.;@@@@@#                              
//                 @``.......................@::@,;;;;,,,,,,:;;'+';::::::::::::::::::;+';;;:@',,,@#@#,:;@'::::::::::::::::::::::::@........................,,,,,,:'   '@`.                                
//                 @``.......................@::++;;;;;:,,,:;;;@'';:::::::::::::::::::'@;;;;,@@@@@@#,,;;@'::::::::::::::::::::::::@........................,,,,,,+   ';@                                  
//                 +``.......................@::;@:;;;;;;;;;;;;@''::::::::::::::::::::'#';;;;,;@@#:,,;;++'::::::::::::::::::::::::@........................,,,,,,@   @'#                                  
//                 ;``.......................@:::+@;;;;;;;;;;;@'''::::::::::::::::::::''@;;;;;,,,,,,;;;@';::::::::::::::::::::::::@........................,,,,,,@   @'#`                                 
//                 `,`.......................@:::'@+;;;;;;;;;##'''::::::::::::::::::::;'##;;;;;;::;;;;+#':::::::::::::::::::::::::@........................,,,,,+`   @++,                                 
//                  +`.......................@::::'@';;;;;;;+@''';:::::::::::::::::::::''@';;;;;;;;;;;@'':::::::::::::::::::::::::@..'@...................,,,,,,@    ++++                                 
//                  @`.......................@::::''@#;;;;;@@''''::::::::::::::::::::::;''@';;;;;;;;;@'';:::::::::::::::::::::::::#..,+,..................,,,,,;'   ;;++@                                 
//                  @`.......................@:::::''@@@#@@@''''':::::::::::::::::::::::'''@#;;;;;;;@#''::::::::::::::::::::::::::+.',,'..................,,,,,@    #;++@                                 
//                  #`.......................+::::::'''#@#''''''::::::::::::::::::::::::;'''@@+;;;+@#'';::::::::::::::::::::::::::;.#,,+.................,,,,,;#    @;++@                                 
//                  ,,.......................':::::::'''''''''';:::::::::::::::::::::::::''''+@@@@@''''::::::::::::::::::::::::::;,.;@@,.................,,,,,@     @'++#                                 
//                   @...................@#.,;::::::::;''''''';:::::::::::::::::::::::::::'''''''''''':::::::::::::::::::::::::::'...#+.................,,,,,'+     @'+++,                                
//                   @..................+,@.;:::::::::::;''';::::::::::::::::::::::::::::::;'''''''''::::::::::::::::::::::::::::#......................,,,,,@      +'+++#                                
//                   #..................#,@.+::::::::::::::::::::::::::::::::::::::::::::::::;'''''::::::::::::::::::::::::::::::@.....................,,,,,@.     .'++++@                                
//                    +.................#@;.@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@.....................,,,,;@      ';++++@                                
//                    @.....................@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@....................,,,,,@       #;++++@                                
//                    #.....................@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@....................,,,,@,       @;++++#                                
//                     #....................@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+...................,,,,+#        @;+++++,                               
//                     @....................#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;:..................,,,,:@         @;++++++                               
//                     :;...................':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+...................,,,,@          @;+++++@                               
//                      @..................;::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@..................,,,,@,          +'+++++@                               
//                      ':.................#::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@.................,,,,@+          `''+++++@                               
//                       @.................@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@................,,,,'@           ,''+++++@                               
//                       +:................@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@...............,,,,:@            ;;++++++#                               
//                        @................@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'..............,,,,,@`            #;+++++++,                              
//                        '+...............+:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+,.............,,,,,@,             #;+++++++'                              
//                         @..............:;:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@,............,,,,,@'              @;+++++++@                              
//                         `@.............#::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@,.+@........,,,,,##               @;+++++++@                              
//                          #;............@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@,.,,,.....,,,,,,#@                @;+++++++@                              
//                           @............@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+,:,,+.,,,,,,,,,+@                 @;+++++++@                              
//                           `@.......@+..#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+,,;,,+,,,,,,,,,+@                  @;+++++++@                              
//                            '@......@@.:;:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@,,::;:,,,,,,,,#@                   @;+++++++@                              
//                             @'....+,@.#::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::@,,,@@,,,,,,,,#@                    @;+++++++@                              
//                              @:...,@@.@:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;#,,,,,,,,,,,,@@                     @;+++++++#                              
//                               @.......@:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::+;,,,,,,,,,,,@@                      @;+++++++#                              
//                               `@.....,'::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;@,,,,,,,,,,,@#                       @;+++++++#                              
//                                `@....#:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'@,,,,,,,,,:@'                        @;+++++++#                              
//                                 .@,..@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::''@,,,,,,,,'@.                         @;+++++++#                              
//                                  `@:.+:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;'+:,,,,,,,@@                           @;+++++++#                              
//                                    @+::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::''@,,,,,,:@#                            @;+++++++@                              
//                                     @@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'''@,,,,,#@,                             @;+++++++@                              
//                                      #@::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::''''+,,,:@@                               #;+++++++@                              
//                                       :@+:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::''''#,,,@@:                                ';+++++++@                              
//                                         @@:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'''''@,+@#                                  .'+++++++@                              
//                                          '@#::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'''''''@@`                                    +++++++++                              
//                                            @@+:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'''''+@@,                                      @'++++++.                              
//                                             .@@':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;''''+@@;                                        @;+++++@                               
//                                               ,@@':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;''''#@@:                                          @;+++++@                               
//                                                 ,@@#:::::::::::::::::::::::::::::::::::::::::::::::::::::::;''''@@@.                                            ,'+++++@                               
//                                                   .@@@':::::::::::::::::::::::::::::::::::::::::::::::::;''''@@@+                                                @'+++#,                               
//                                                      '@@@':::::::::::::::::::::::::::::::::::::::::;;''''+@@@#`                                                  @;+++@                                
//                                                        `#@@@+:::::::::::::::::::::::::::::::;;;'''''''#@@@@.                                                     ,#+++@                                
//                                                            ;@@@@#+'''''''''''''''''''''''''''''''+@@@@@;                                                          @++@                                 
//                                                               `;@@@@@@#+''''''''''''''''''+#@@@@@@#,                                                               @@:                                 
//                                                                    `:+@@@@@@@@@@@@@@@@@@@@@@+;`                                                                                                        
//                                                                            ```,,::,,```                                                                                                                
