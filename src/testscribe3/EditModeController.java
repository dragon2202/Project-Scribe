/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;

import java.lang.Math; // For window resizing.
import java.io.*;
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
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D; // For window resizing.
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;

/**
 * FXML Controller class
 *
 * @author Ryan, Alvin, Cody
 */
public class EditModeController implements Initializable {
    static private final String FILE_TEMP_NAME = "tempEM.xlsx";   // Temporary File that we are actually using
    
    static final int NUM_PAGE = 5;          // Number of steps (instructions) in a page
    static final int STEP_COLUMN = 3;       // Location of said steps in the excel file
    static final int TEST_COLUMN = 4;       // This is where you would put the Passed/Failed/Not Tested description    
    static final int COMMENT_COLUMN = 5;    // Location of comment in the excel file
    static final int ID_COLUMN = 0;         // Location of the ID in the excel file
    static final int VARIANT_COLUMN = 7;    // This column is associated with the STEP_COLUMN and is used for filtering purpose
    static final int SUBHEADER_COLUMN = 2;  // Location of the subheader section in the excel file
    static final int HEADER_COLUMN = 1;     // Location of the header in the excel file
    
    static private String fileName;          // Placeholder for the file name/path
    static private int maxPageNum;           // The maximum page number for the current variant/mode
    static private int currentPageNum;       // The current page number that the user is currently viewing
    static private int sheetIndex = 0;              // This is the sheet index based on the excel file
    static private int rowIndex = 0;                // This is the row index based on the excel file
    static private int stepIndex = -1;              // Step index now means number of steps that have matching variant filter that have been counted
    static private boolean condition = true;        // if value == false, then the program has reached the very end of the excel file
    static private ArrayList<Integer> record = new ArrayList<Integer>();    // This ArrayList contains the row index recorded on compatible "test steps" (matching variant)
    static private ArrayList<Integer> recordSH = new ArrayList<Integer>();  // Same as record, but for subheader instead
    static private ArrayList<Integer> recordH = new ArrayList<Integer>();   // For header
    static private boolean newSH = false;           // This boolean is used in the readExcel() and previousArrow(). This is for subheader checking
    static private boolean newH = false;            // This boolean is used in the readExcel() and previousArrow(). This is for header checking
    
    /**
     * Initializes the controller class.
     */       
    @FXML
    private MenuBar EditMenuBar;
    
    @FXML
    private Label labelEditSaved, labelStepsNotFound;
    
    @FXML
    private Button GoBack3;
    
     @FXML
    private Button previousArrow1, previousArrow5, previousArrow10, nextArrow1, nextArrow5, nextArrow10;               
    
     @FXML
     private VBox VBoxEdit;
     
    @FXML
    private TextArea question1, question2, question3, question4, question5, 
                     var1, var2, var3, var4, var5,
                     subHead1, subHead2, subHead3, subHead4, subHead5,
                     header1, header2, header3, header4, header5;
    
    @FXML
    private TextField id1, id2, id3, id4, id5, pageNumber;
         
    @FXML
    private Menu sheetSelector, headerJumpTo;
    
    @FXML
    private MenuItem saveTest, saveTestAs;
    
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
     
    /*
    *These ActionEvents are found on the Go To... section
    */
    @FXML
    private void handleTestStep(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent BacktoMain = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
        Scene MainScenefromEdit = new Scene(BacktoMain);
        Stage Main_stage = (Stage) EditMenuBar.getScene().getWindow();
        Main_stage.setScene(MainScenefromEdit);
        Main_stage.centerOnScreen();
        Main_stage.show(); 
    }
    
    // saveEdits functions as the save button, it essentially rewrites the loaded excel file with the temporary file
    @FXML void saveEdits(ActionEvent event) throws FileNotFoundException, IOException, InvalidFormatException, InterruptedException{            
        if(fileName == null || fileName.isEmpty()){
            /*
            *   This condition can only activate if the temp file was loaded automatically without user's input
            *   This means that the temp file has already existed in the first place from the start
            */
            saveAs();
            return;
        }
        savePage(); 
        // Copying from temp to original excel file to maintain illusion of saving
        File ori = new File(fileName);
        File temp = new File(FILE_TEMP_NAME); 
        Files.copy(temp.toPath(), ori.toPath(), StandardCopyOption.REPLACE_EXISTING);
        labelEditSaved.setVisible(true);
        // Refreshes the drop-down "Find" menu
        headerJumpTo.getItems().clear();
        getHeader();
    }     
    // saveEditAs is associated with the Save As button in the File Menu
    @FXML 
    private void saveEditsAs(ActionEvent event) throws FileNotFoundException, IOException, InvalidFormatException{
        saveAs();
    } 
    private void saveAs() throws IOException, FileNotFoundException, InvalidFormatException{
        // User select or input file name to save
        FileChooser selectExcel = new FileChooser();
        selectExcel.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX FILES", "*.xlsx"));
        selectExcel.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS FILES", "*.xls"));
        File tests = selectExcel.showSaveDialog(null);
        
        // To avoid any exception or nullpointer error, this condition is required
        if(tests == null){
            return;
        }
        
        // Rewriting FILE_NAME with the file selected by user
        fileName = tests.getAbsolutePath(); // Important! FILE_NAME holds the target's name/path
        
        // Actual saving
        savePage(); // The actual saving mechanic used in the GUI
        // Copying from temp to original excel file to maintain illusion of saving
        File ori = new File(fileName);
        File temp = new File(FILE_TEMP_NAME); 
        Files.copy(temp.toPath(), ori.toPath(), StandardCopyOption.REPLACE_EXISTING);
        labelEditSaved.setVisible(true); // Will make the label "Saved!" appear
    }
    
    @FXML
    private void handleCCLog(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) EditMenuBar.getScene().getWindow();
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
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) EditMenuBar.getScene().getWindow();
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
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) EditMenuBar.getScene().getWindow();
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
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Shift = FXMLLoader.load(getClass().getResource("ShiftEntry.fxml"));
        Scene Shift_Scene = new Scene(Shift);
        Stage Shift_Stage = (Stage) EditMenuBar.getScene().getWindow();
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
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("ViewMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) EditMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();   
    }
    
    
    @FXML // This loads file 
    private void loadFile(ActionEvent event) throws IOException, InvalidFormatException{
        FileChooser selectExcel = new FileChooser();
        selectExcel.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX FILES", "*.xlsx"));
        selectExcel.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS FILES", "*.xls"));
        File tests = selectExcel.showOpenDialog(null);
        
        // To avoid any exception or nullpointer error, this condition is required
        if(tests == null){
            return;
        }
        
        // Copying from original to a temporary one so we could fiddle around with greater leeway
        File ori = new File(tests.getAbsolutePath());
        fileName = tests.getAbsolutePath(); // Important! FILE_NAME holds the target's name/path
        File temp = new File(FILE_TEMP_NAME); 
        Files.copy(ori.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        
        rowIndex = 0;
        condition = true;
        stepIndex = -1;
        record.clear();
        recordSH.clear();
        recordH.clear();
        headerJumpTo.getItems().clear();
        newSH = false;
        newH = false;
        
        hideLabels();
        hideTextFields();
        resetTextFields();
        getSheet(); // Get list of sheets
        getHeader();
        getPageNumber();
        saveTest.setDisable(false); // Enable the Save button in the Menu
        saveTestAs.setDisable(false); // Enable the Save As button in the Menu
        readExcel();
        
        exposeArrowPage();
    }
    
    /*
    *   These ActionEvents are found in the arrow button in the fxml file
    */
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
    }
    // This function is essentially the "master" function for the previous arrow that goes from 1, 5, or 10 pages 
    private void previousArrowFunction(int y) throws FileNotFoundException, IOException, InvalidFormatException{    
        for(int x = 0;x<y;x++)
        {
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
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        EditMenuBar.prefWidthProperty().bind(VBoxEdit.widthProperty());
        
        /*
        *   The code below will attempt to automatically load FILE_TEMP_NAME if it exist
        *   The purpose is to essentially maintain a "persistent" state if the user 
        *   changes mode and come back
        */
        File temp = new File(FILE_TEMP_NAME);
        // Check if temp file exist in the .jar directory
        if(temp.exists() && !temp.isDirectory()){ // Condition A
            saveTest.setDisable(false); // Enable the Save button in the Menu
            saveTestAs.setDisable(false); // Enable the Save As button in the Menu
            FileInputStream sort = null;
            try {
                sort = new FileInputStream(new File(FILE_TEMP_NAME));
                Workbook editMode = WorkbookFactory.create(sort);
                Sheet stage0 =  editMode.getSheetAt(sheetIndex);
                // Load the essential MenuItems
                getHeader();
                getSheet();
                getPageNumber(); // Need to readjust the current page number if user is somewhere other than the first page (beginning)
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
                // Check if temp file already existed before file is loaded in EditMode, since there is no way stepIndex can go back and stays at -1 if that were the case
                if(stepIndex != -1){ // Condition B
                    // If Condition B is true, then temp file has already been loaded and/or tampered with
                    // Thus, code will attempt to use the static variables to load the GUI back to the previous state right before user left
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
                } // End of Condition B
                else{
                    temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
                }
                // If Condition B is false, then this code will just load the temp file normally (similar loadFile())
                // Finally, execute readExcel()
                readExcel();   
                exposeArrowPage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    sort.close();
                } catch (IOException ex) {
                    Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } // End of Condition A
    } // End of initialize
    
    /*** All code below works as a custom function/method ***/
    
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
        
        if(question1.isVisible())   // If question1 Text Field is visible, then saves the text on it back to the temp excel file
        {
            // Writes whatever is on question1 box back to excel (if it's visible/present
            row = sheet1.getRow(record.get(tempIndex));
            cell = row.createCell(STEP_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(question1.getText());
            // Writes whatever is on id1 box (whether it's present or not) back to excel
            cell = row.createCell(ID_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(id1.getText());
            // For var1 box 
            cell = row.createCell(VARIANT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(var1.getText());
        }
        if(question2.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+1));
            cell = row.createCell(STEP_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(question2.getText());  
            cell = row.createCell(ID_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(id2.getText());
            cell = row.createCell(VARIANT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(var2.getText());
        }
        if(question3.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+2));
            cell = row.createCell(STEP_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(question3.getText());     
            cell = row.createCell(ID_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(id3.getText());
            cell = row.createCell(VARIANT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(var3.getText());
        }
        if(question4.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+3));
            cell = row.createCell(STEP_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(question4.getText());
            cell = row.createCell(ID_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(id4.getText());
            cell = row.createCell(VARIANT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(var4.getText());
        }
        if(question5.isVisible())
        {
            row = sheet1.getRow(record.get(tempIndex+4));
            cell = row.createCell(STEP_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(question5.getText());
            cell = row.createCell(ID_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(id5.getText());
            cell = row.createCell(VARIANT_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(var5.getText());
        }
        
        //Subheader section
        int count = 0;
        if(subHead5.isVisible()){
            row = sheet1.getRow(recordSH.get(recordSH.size()-1-count));
            cell = row.createCell(SUBHEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(subHead5.getText());
            count++;
        }
        if(subHead4.isVisible()){
            row = sheet1.getRow(recordSH.get(recordSH.size()-1-count));
            cell = row.createCell(SUBHEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(subHead4.getText());
            count++;
        }
        if(subHead3.isVisible()){
            row = sheet1.getRow(recordSH.get(recordSH.size()-1-count));
            cell = row.createCell(SUBHEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(subHead3.getText());
            count++;
        }
        if(subHead2.isVisible()){
            row = sheet1.getRow(recordSH.get(recordSH.size()-1-count));
            cell = row.createCell(SUBHEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(subHead2.getText());
            count++;
        }
        if(subHead1.isVisible()){
            row = sheet1.getRow(recordSH.get(recordSH.size()-1-count));
            cell = row.createCell(SUBHEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(subHead1.getText());
            count++;
        }
        
        //Header section
        int countH = 0;
        if(header5.isVisible()){
            row = sheet1.getRow(recordH.get(recordH.size()-1-countH));
            cell = row.createCell(HEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(header5.getText());
            countH++;
        }
        if(header4.isVisible()){
            row = sheet1.getRow(recordH.get(recordH.size()-1-countH));
            cell = row.createCell(HEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(header4.getText());
            countH++;
        }
        if(header3.isVisible()){
            row = sheet1.getRow(recordH.get(recordH.size()-1-countH));
            cell = row.createCell(HEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(header3.getText());
            countH++;
        }
        if(header2.isVisible()){
            row = sheet1.getRow(recordH.get(recordH.size()-1-countH));
            cell = row.createCell(HEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(header2.getText());
            countH++;
        }
        if(header1.isVisible()){
            row = sheet1.getRow(recordH.get(recordH.size()-1-countH));
            cell = row.createCell(HEADER_COLUMN);
            cell.setCellStyle(style);
            cell.setCellValue(header1.getText());
            countH++;
        }
        
        file1.close();
        //Open FileOutputStream to write updates
        FileOutputStream output_file = new FileOutputStream(new File(FILE_TEMP_NAME)); // For the purpose of this project, you must choose the same file that you read  
        //write changes
        workbook1.write(output_file);
        //close the stream
        output_file.close();
        // If any changes have been made to header, then readjustment is needed
    }
    
    private static boolean isCellEmpty(final Cell cell) { // This function is to check whether a cell is empty or not
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
        if(labelEditSaved.isVisible())
            labelEditSaved.setVisible(false);
        labelStepsNotFound.setVisible(false);
    }
    
    // Self-explanatory
    private void resetTextFields() { 
        question1.setText(""); 
        question2.setText("");
        question3.setText("");
        question4.setText("");
        question5.setText("");
        id1.setText("");
        id2.setText("");
        id3.setText("");
        id4.setText("");
        id5.setText("");
        var1.setText("");
        var2.setText("");
        var3.setText("");
        var4.setText("");
        var5.setText("");
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
    
    // Set TextFields to not visible
    private void hideTextFields(){
        if(question1.isVisible())
            question1.setVisible(false);
        if(question2.isVisible())
            question2.setVisible(false);
        if(question3.isVisible())
            question3.setVisible(false);
        if(question4.isVisible())
            question4.setVisible(false);
        if(question5.isVisible())
            question5.setVisible(false);
        id1.setVisible(false);
        id2.setVisible(false);
        id3.setVisible(false);
        id4.setVisible(false);
        id5.setVisible(false);
        var1.setVisible(false);
        var2.setVisible(false);
        var3.setVisible(false);
        var4.setVisible(false);
        var5.setVisible(false);
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
    
    // This function is to read the excel file and populate it on the GUI
    private void readExcel() throws IOException, InvalidFormatException{       
        DataFormatter df = new DataFormatter(); // This is a data formatter which is useful to convert cell types into anything you want. In this case, String.
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME));
        Workbook editMode = WorkbookFactory.create(sort);
        Sheet stage0 =  editMode.getSheetAt(sheetIndex); 
        
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
                    //System.out.println("Initially added row "+row.getRowNum()); // Debugging
                    newSH = true; // If found a new subheader, then turn boolean value into true
                }
                //Checks test steps
                else if(isCellEmpty(row.getCell(STEP_COLUMN)) == false)
                {
                    i++; // Increment the index if and only if steps are found in that row (in column 3)
                    rowIndex = row.getRowNum();
                }
            }
            else // If there's nothing to load, which is not supposed to happen at all 
            {
                //System.out.println("Error! No steps are found!"); // Debugging purpose
                condition = false;
                return; // Should not go here at all, unless file is missing content or there is some serious logic error in this file
            }
        }
        
        // This section deals with the program attempting to read information from the excel file and writing it into the GUI
        while(question5.getText().isEmpty() && condition == true)
        {
            //System.out.println("This is row: "+rowIndex); // Debugging purpose
            
            row = stage0.getRow(rowIndex); // Get the starting row index first
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
            // This section will check for cells in test step column
            else if(isCellEmpty(cell) == false) // Check if the cell in that column is empty or not, if it's not empty then enter the if condition
            {
                stepIndex++; // Incrementing index (to 0 if first time)
                record.add(stepIndex,rowIndex); // Adding this particular row index to the array list (stepIndex is quite redundant, but it is used here to emphasize how the logic works)
                //System.out.println("Reading row: "+ rowIndex); // Debugging purpose

                if(question1.getText().isEmpty())
                {      
                    question1.setText(cell.getStringCellValue());
                    question1.setVisible(true);
                    // We want the id field to show up only when the test steps are present
                    id1.setVisible(true);
                    // Only populate them with stuff if it exist from the excel file
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(ID_COLUMN)); // DataFormatter is needed because there is a high chance that the id can show up as numeric when entered
                                                                                        // manually in the excel (if inputted purely as numbers)
                        id1.setText(tempString);
                    }
                    // Same logic also applies for variant section
                    var1.setVisible(true);
                    if(!isCellEmpty(row.getCell(VARIANT_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(VARIANT_COLUMN)); 
                        var1.setText(tempString);
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
                    id2.setVisible(true);
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(ID_COLUMN));
                        id2.setText(tempString);
                    }
                    var2.setVisible(true);
                    if(!isCellEmpty(row.getCell(VARIANT_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(VARIANT_COLUMN)); 
                        var2.setText(tempString);
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
                    id3.setVisible(true);
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(ID_COLUMN));
                        id3.setText(tempString);
                    }                
                    var3.setVisible(true);
                    if(!isCellEmpty(row.getCell(VARIANT_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(VARIANT_COLUMN)); 
                        var3.setText(tempString);
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
                    id4.setVisible(true);
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(ID_COLUMN));
                        id4.setText(tempString);
                    }
                    var4.setVisible(true);
                    if(!isCellEmpty(row.getCell(VARIANT_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(VARIANT_COLUMN)); 
                        var4.setText(tempString);
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
                    id5.setVisible(true);
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(ID_COLUMN));
                        id5.setText(tempString);
                    }
                    var5.setVisible(true);
                    if(!isCellEmpty(row.getCell(VARIANT_COLUMN))) 
                    {
                        String tempString = df.formatCellValue(row.getCell(VARIANT_COLUMN)); 
                        var5.setText(tempString);
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
        editMode.close();
    }
    
    private void peekNextStep() throws FileNotFoundException, IOException, InvalidFormatException // This function is to check whether there is a next step at all
    {    
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook editMode = WorkbookFactory.create(sort);
        Sheet stage0 =  editMode.getSheetAt(sheetIndex);
        
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        
        //This section is to cycle the iterator to the next available row (cycling through old rows that have been read) (first row if first time intializing it)
        for(int i=0;i<=(stepIndex+1);)
        {
            if(iterator.hasNext())
            {
                row = iterator.next();
                if(isCellEmpty(row.getCell((STEP_COLUMN))) == false)
                {
                    i++; // Increment the index if and only if steps are found in that row (in column 3)
                }
            }
            else // If there's no more rows to be found (means no more steps to be found
            {
                condition = false;
                break;
            }
        }
        // There is a next step if your code reach this point and condition remains as true
        sort.close();
        editMode.close();
    }
    
    // This function would be executed once each time a file is loaded
    // This function would be similar to getVariant(), in which this would populate the "Select Sheet"/sheetSelector Menu with all of the sheets present in the excel file
    private void getSheet() throws FileNotFoundException, IOException, InvalidFormatException{
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook editMode = WorkbookFactory.create(sort);   
        
        // for each sheet in the workbook
        for (int i = 0; i < editMode.getNumberOfSheets(); i++) {
            int index = i;
            MenuItem sheetOption = new MenuItem(editMode.getSheetName(i)); // Creating instance
            sheetSelector.getItems().add(sheetOption); // Adding menu item into the menu
            sheetOption.setOnAction(new EventHandler<ActionEvent>() { // Giving the menu item its action event
                @Override public void handle(ActionEvent e)  {
                    try {
                        // Since this would change to a different sheet, do a soft reset
                        savePage(); // Don't forget to save current page before soft-resetting
                        sheetIndex = index; // Set the sheetIndex (must be done after savePage, lest you save it in the wrong sheet)
                        rowIndex = 0;
                        condition = true;
                        stepIndex = -1;
                        record.clear();
                        recordSH.clear();
                        recordH.clear();
                        headerJumpTo.getItems().clear();
                        newSH = false;
                        newH = false;
                        hideLabels();
                        resetTextFields();
                        hideTextFields();
                        getHeader();
                        getPageNumber();
                        readExcel();   
                        exposeArrowPage();
                    } catch (IOException | InvalidFormatException ex) {
                        Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        sort.close();
        editMode.close();
        return;
    }
    
    // This function gets the list of all headers available in HEADER_COLUMN and stores it in headerList ArrayList
    // This function populates the "Find" Menu with header(s) MenuItem and assign each of them their own ActionEvent
    private void getHeader() throws IOException, InvalidFormatException{
        ArrayList<String> headerList = new ArrayList<String>(); // This ArrayList stores all the header found in the excel file on HEADER_COLUMN
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook editMode = WorkbookFactory.create(sort);
        Sheet stage0 =  editMode.getSheetAt(sheetIndex);     
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
        editMode.close();
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
                        if(condition == false) labelStepsNotFound.setVisible(true); // This simply means no test steps are found due to mismatching variant
                        exposeArrowPage();
                        // End of code
                    } catch (IOException | InvalidFormatException ex) {
                        Logger.getLogger(EditModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            
        }
        
    }// End of getHeader
    // This function will get all the number of available test steps and compute the total amount of pages that the user can traverse on the GUI
    // It will then populate the page number (max and current) onto the pageNumber TextField
    // This function should be executed only once after the file is loaded and/or when you've chosen another sheet
    private void getPageNumber() throws IOException, InvalidFormatException{
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook editMode = WorkbookFactory.create(sort);
        Sheet stage0 =  editMode.getSheetAt(sheetIndex);     
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        int totalSteps=0;
        while(iterator.hasNext()){
            row = iterator.next();
            //Checks test step
            if(isCellEmpty(row.getCell(STEP_COLUMN)) == false)
            {
                totalSteps++; // Increment the totalSteps if a matching test step has been found
            }
        }
        sort.close();
        editMode.close();
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
}


