/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;
import java.lang.Math; // For window resizing.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.print.*;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D; // For window resizing.
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.control.Menu;
import javafx.stage.FileChooser;




/**
 * FXML Controller class
 *
 * @author Ryan, Alvin
 */


public class ViewModeController implements Initializable {
    static private final String FILE_NAME_EXT = Storage.getExt(); // The string that we add in front of a file that is designated as "Advanced SORT"
    static private final String FILE_TEMP_NAME = Storage.getTemp(); // Temporary File that we are actually using
    static private final String FILE_TEMP_NAME_ADV = Storage.getTempAdv(); // The temp file for "Advanced SORT"
    
    static final int NUM_PAGE = 5;          // Number of steps (instructions) in a page
    static final int STEP_COLUMN = 3;       // Location of said steps in the excel file
    static final int TEST_COLUMN = 4;       // This is where you would put the Passed/Failed/Not Tested description
    static final int COMMENT_COLUMN = 5;    // Location of comment in the excel file
    static final int ID_COLUMN = 0;         // Location of the ID in the excel file
    static final int VARIANT_COLUMN = 7;    // Location of variant in the excel file (In this mode, it's not used for filtering purpose, only for display)
    static final int SUBHEADER_COLUMN = 2;  // Location of the subheader section in the excel file
    static final int HEADER_COLUMN = 1;     // Location of the header in the excel file
    
    static private String fileNamePath;     // Placeholder for the file name/path
    static private String fileNameOnly;     // This one contains the file name only (without path)
    static private int maxPageNum;          // The maximum page number for the current variant/mode
    static private int currentPageNum;      // The current page number that the user is currently viewing
    static private int sheetIndex = 0;      // This is the sheet index based on the excel file
    static private int rowIndex = 0;        // This is the row index based on the excel file
    static private boolean condition = true;    // if value == false, then the program has reached the very end of the excel file
    static private int stepIndex = -1;      // Step index now means number of steps that have matching variant filter that have been counted
    static private ArrayList<Integer> record = new ArrayList<Integer>();    // This ArrayList contains the row index recorded on recorded "test steps"
    static private ArrayList<Integer> recordSH = new ArrayList<Integer>();  // ArrayList for subheader
    static private ArrayList<Integer> recordH = new ArrayList<Integer>();   // For header
    Color passed = new Color(0,255,0);
    static private boolean newSH = false;       // This boolean is used in the readExcel() and previousArrow(). This is for subheader checking
    static private boolean newH = false;        // This boolean is used in the readExcel() and previousArrow(). This is for header checking
    static private boolean executeReset = false; // This boolean is used in initialize to check whether variable resets are needed or not
    
    /**
     * Initializes the controller class.
     */
    @FXML
    private Button saveButton;
    @FXML
    private VBox VBoxView;
    @FXML 
    private MenuBar TestMenuBar;
    @FXML
    private Label labelSaved, id1, id2, id3, id4, id5;
    @FXML
    private Button GoBack3;
    @FXML
    private TextArea question1, question2, question3, question4, question5,
             comment1, comment2, comment3, comment4, comment5,
            subHead1, subHead2, subHead3, subHead4, subHead5,
            var1, var2, var3, var4, var5,
            header1, header2, header3, header4, header5;
    @FXML
    private TextField testDescription1, testDescription2, testDescription3, testDescription4, testDescription5, pageNumber;
    
    @FXML
    private Button previousArrow1, previousArrow5, previousArrow10, nextArrow1, nextArrow5, nextArrow10;  
    @FXML
    private Menu sheetSelector, headerJumpTo;

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
    *Convert excel file to .csv
    */
    static void Xslx_to_CSV(File inputFile, File outputFile) {
			//stores data into files
			StringBuffer data = new StringBuffer();
			try{
				FileOutputStream FileOutput = new FileOutputStream(outputFile);
				XSSFWorkbook book = new XSSFWorkbook (new FileInputStream(inputFile));//get the workbook object for XLSX file
				XSSFSheet sheet = book.getSheetAt(0);
				Row row;
				Cell cell;
				
				Iterator <Row> rowIterator = sheet.iterator();//iterate through each row from first sheet
				while(rowIterator.hasNext()) {// as long as there's a next row
					row = rowIterator.next();
					//For Each row(above) iterate through each column
					Iterator<Cell> cellIterator = row.cellIterator();
					while(cellIterator.hasNext()) {
						cell = cellIterator.next();
						
						switch(cell.getCellType()) {
							case Cell.CELL_TYPE_BOOLEAN:
								data.append(cell.getBooleanCellValue() + ",");
								break;
							
							case Cell.CELL_TYPE_NUMERIC:
                                data.append(cell.getNumericCellValue() + ",");

                                break;
							case Cell.CELL_TYPE_STRING:
                                	data.append(cell.getStringCellValue() + ",");
                                break;

							case Cell.CELL_TYPE_BLANK:
                                data.append("" + ",");
                                break;
							default:
                                data.append(cell + ",");

                        }
								
						}
				}
				FileOutput.write(data.toString().getBytes());
				FileOutput.close();
			} catch (Exception ioe){
				ioe.printStackTrace();
			}
	}
    
    // Note: Fix the path file! Make it dynamic!
    /*
    *Export excel sheet.
    */
    public void exportactivate() {
		File inputFile = new File("SORT.xlsx");//csv file path
		File outputFile = new File("SORT.csv");
		Xslx_to_CSV(inputFile,outputFile);
		System.out.println("Conversion of XSLX file into CSV succeeded.");
	}

   /*
    * Print excel sheet
    */
   public void printactivate(){
        try {
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
            desktop.print(new File("SORT.xlsx"));//file path 
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
   }
    
    /*
    *These ActionEvents are found on the Go To... section
    */
   @FXML
    private void handleTestStep(ActionEvent event) throws IOException{
        Parent BacktoMain = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
        Scene MainScene = new Scene(BacktoMain);
        Stage Main_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Main_Stage.setScene(MainScene);      
        Main_Stage.centerOnScreen();
        Main_Stage.show();         
    }

    /*
    *Logic for switching to CCLog
    *@param event button click
    */
    @FXML
    private void handleCCLog(ActionEvent event) throws IOException{
        
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) TestMenuBar.getScene().getWindow();
        CCLog_Stage.setScene(CCLogScene);       
        CCLog_Stage.centerOnScreen();
        CCLog_Stage.setTitle("CCLog");
        CCLog_Stage.show();         
    }
    /*
    *Logic for switching to counts
    *@param event button click
    */
    @FXML
    private void handleCounts(ActionEvent event) throws IOException{                 
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Counts_Stage.setScene(Counts_Scene);       
        Counts_Stage.centerOnScreen();
        Counts_Stage.setTitle("Counts");
        Counts_Stage.show();         
    }
    
    /*
    *Logic for switching to executive summary
    *@param event button click 
    */
    @FXML
    private void handleExec(ActionEvent event) throws IOException{                       
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Exec_Stage.setScene(Exec_Scene);       
        Exec_Stage.centerOnScreen();
        Exec_Stage.setTitle("Executive Summary");
        Exec_Stage.show();         
    }
    
    /*
    *Logic for switching to shift entry.
    *@param event button click
    */
    @FXML
    private void handleShift(ActionEvent event) throws IOException{         
        Parent Shift = FXMLLoader.load(getClass().getResource("ShiftEntry.fxml"));
        Scene Shift_Scene = new Scene(Shift);
        Stage Shift_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Shift_Stage.setScene(Shift_Scene);       
        Shift_Stage.centerOnScreen();
        Shift_Stage.setTitle("Shift Entry");
        Shift_Stage.show();         
    }       
    /*
    * Logic for switching to edit mode?
    *@param event click button event
    */
    @FXML
    private void handleEdit(ActionEvent event) throws IOException {
        Parent Test = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) TestMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();  
    }
    
     /*
    *Load excel test file.
    *@param event button click
    */
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
        
        fileNamePath = tests.getAbsolutePath(); // Important! FILE_NAME holds the target's name/path
        // Set temp file 
        File ori = new File(fileNamePath);
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
        rowIndex = 0;
        condition = true;
        stepIndex = -1;
        record.clear(); 
        sheetIndex = 0;
        recordSH.clear();
        recordH.clear();
        newSH = false;
        newH = false;
        headerJumpTo.getItems().clear(); // Important! You don't want duplicates or old header option to remain.
        hideLabels();
        resetTextFields();
        getSheet();
        getHeader(); // Get all header for the "Find" menu
        getPageNumber();
        readExcel();
        exposeArrowPage();
        
        // Set file names for other files
        Storage.setNameTwice(fileNamePath, fileNameOnly);
        TestModeController.setReset();
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
        TestMenuBar.prefWidthProperty().bind(VBoxView.widthProperty());
        
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
        if(temp.exists() && !temp.isDirectory()){
            // Check if temp2 file exist in the .jar directory
            if(temp1.exists() && !temp1.isDirectory()){ 
                // if user has a temp file has already existed and user has loaded/Save As a file in other .java files
                // Do a partial variable reset (the rest will be reset below)
                if (executeReset == true){
                    executeReset = false;
                    sheetIndex = 0;
                    rowIndex = 0;
                    stepIndex = -1;
                    record.clear();
                    recordSH.clear();
                    recordH.clear();
                }
                FileInputStream sort = null;
                try {
                    sort = new FileInputStream(new File(FILE_TEMP_NAME));
                    Workbook viewMode = WorkbookFactory.create(sort);
                    Sheet stage0 =  viewMode.getSheetAt(sheetIndex);
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
                        temp1.deleteOnExit();
                    }
                    // If Condition B is false, then this code will just load file1 normally (similar loadFile())
                    // Finally, execute readExcel()
                    readExcel();   
                    exposeArrowPage();
                    // Closing workbook
                    viewMode.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ViewModeController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ViewModeController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidFormatException ex) {
                    Logger.getLogger(ViewModeController.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        sort.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ViewModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }    
    
    
    /*** All code below works as a custom function/method ***/
    
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
    
    private void hideLabels() { 
        if(labelSaved.isVisible())
            labelSaved.setVisible(false);
    }
    
    private void hideTextFields(){
        if(question1.isVisible())
            question1.setVisible(false);
        if(testDescription1.isVisible())
            testDescription1.setVisible(false);
        if(comment1.isVisible())
            comment1.setVisible(false);
        if(question2.isVisible())
            question2.setVisible(false);
        if(testDescription2.isVisible())
            testDescription2.setVisible(false);
        if(comment2.isVisible())
            comment2.setVisible(false);
        if(question3.isVisible())
            question3.setVisible(false);
        if(testDescription3.isVisible())
            testDescription3.setVisible(false);
        if(comment3.isVisible())
            comment3.setVisible(false);
        if(question4.isVisible())
            question4.setVisible(false);
        if(testDescription4.isVisible())
            testDescription4.setVisible(false);
        if(comment4.isVisible())
            comment4.setVisible(false);
        if(question5.isVisible())
            question5.setVisible(false);
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
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); // Choose your file 
        Workbook viewMode = WorkbookFactory.create(sort);
        Sheet stage0 =  viewMode.getSheetAt(sheetIndex); // Just supply a button to move to the next sheet       
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
            
            // This section will check for cells in step column
            else if(isCellEmpty(cell) == false) // Check if the cell in that column is empty or not, if it's not empty then enter the if condition
            {
                stepIndex++; // Incrementing index (to 0 if first time)
                record.add(stepIndex,rowIndex); // Adding this particular row index to the array list (stepIndex is quite redundant, but it is used here to emphasize how the logic works)
                //System.out.println("Reading row: "+ rowIndex); // Debugging purpose

                if(question1.getText().isEmpty())
                {      
                    question1.setText(cell.getStringCellValue());
                    question1.setVisible(true);
                    if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                    {
                        testDescription1.setVisible(true);
                        testDescription1.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                        
                        if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                        {
                            comment1.setVisible(true);
                            comment1.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                        }
                    }
                    // ID section
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        id1.setVisible(true);
                        String tempString = df.formatCellValue(row.getCell(ID_COLUMN)); // DataFormatter is needed because there is a high chance that the id can show up as numeric when entered
                                                                                        // manually in the excel (if inputted purely as numbers)
                        id1.setText(tempString);
                    }
                    // Variant section
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
                    if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                    {
                        testDescription2.setVisible(true);
                        testDescription2.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                        
                        if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                        {
                            comment2.setVisible(true);
                            comment2.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                        }
                    }
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        id2.setVisible(true);
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
                    if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                    {
                        testDescription3.setVisible(true);
                        testDescription3.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                        
                        if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                        {
                            comment3.setVisible(true);
                            comment3.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                        }
                    }
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        id3.setVisible(true);
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
                    if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                    {
                        testDescription4.setVisible(true);
                        testDescription4.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                        
                        if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                        {
                            comment4.setVisible(true);
                            comment4.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                        }
                    }
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        id4.setVisible(true);
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
                    if(!isCellEmpty(row.getCell(TEST_COLUMN))) // If there exist something on the test column, then we should display it too
                    {
                        testDescription5.setVisible(true);
                        testDescription5.setText(row.getCell(TEST_COLUMN).getStringCellValue());
                        
                        if(!isCellEmpty(row.getCell(COMMENT_COLUMN))) // Same thing with comment column
                        {
                            comment5.setVisible(true);
                            comment5.setText(row.getCell(COMMENT_COLUMN).getStringCellValue());
                        }
                    }
                    if(!isCellEmpty(row.getCell(ID_COLUMN))) 
                    {
                        id5.setVisible(true);
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
        viewMode.close();
    }
    
    private void peekNextStep() throws FileNotFoundException, IOException, InvalidFormatException // This function is to check whether there is a next step at all
    {    
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook viewMode = WorkbookFactory.create(sort);
        Sheet stage0 =  viewMode.getSheetAt(sheetIndex);
        
        Iterator<Row> iterator = stage0.iterator();
        Row row;
        
        //This section is to cycle the iterator to the next available row (cycling through old rows that have been read) (first row if first time intializing it)
        for(int i=0;i<=(stepIndex+1);)
        {
            if(iterator.hasNext())
            {
                row = iterator.next();
                if(isCellEmpty(row.getCell(STEP_COLUMN)) == false)
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
        viewMode.close();
    }
    
    // This function would be executed once each time a file is loaded
    // This function would be similar to getVariant(), in which this would populate the "Select Sheet"/sheetSelector Menu with all of the sheets present in the excel file
    private void getSheet() throws FileNotFoundException, IOException, InvalidFormatException{
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook viewMode = WorkbookFactory.create(sort);   
        
        // for each sheet in the workbook
        for (int i = 0; i < viewMode.getNumberOfSheets(); i++) {
            int index = i;
            javafx.scene.control.MenuItem sheetOption = new javafx.scene.control.MenuItem(viewMode.getSheetName(i)); // Creating instance
            sheetSelector.getItems().add(sheetOption); // Adding menu item into the menu
            sheetOption.setOnAction(new EventHandler<ActionEvent>() { // Giving the menu item its action event
                @Override public void handle(ActionEvent e)  {
                    try {
                        // Since this would change to a different sheet, do a soft reset
                        sheetIndex = index; // Set the sheetIndex (must be done after savePage, lest you save it in the wrong sheet)
                        rowIndex = 0;
                        condition = true;
                        stepIndex = -1;
                        record.clear();
                        recordSH.clear();
                        recordH.clear();
                        headerJumpTo.getItems().clear(); // Important! You don't want duplicates or old header option to remain.
                        newSH = false;
                        newH = false;
                        hideLabels();
                        resetTextFields();
                        hideTextFields();
                        getHeader(); // Get all header for the "Find" menu
                        getPageNumber();
                        readExcel();   
                        exposeArrowPage();
                    } catch (IOException | InvalidFormatException ex) {
                        Logger.getLogger(ViewModeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
        sort.close();
        viewMode.close();
        return;
    }
    
    // This function gets the list of all headers available in HEADER_COLUMN and stores it in variantList ArrayList
    // This function populates the "Find" Menu with header(s) MenuItem and assign each of them their own ActionEvent
    private void getHeader() throws IOException, InvalidFormatException{
        ArrayList<String> headerList = new ArrayList<String>();  // This ArrayList stores all the header found in the excel file on HEADER_COLUMN
        FileInputStream sort = new FileInputStream(new File(FILE_TEMP_NAME)); 
        Workbook viewMode = WorkbookFactory.create(sort);
        Sheet stage0 =  viewMode.getSheetAt(sheetIndex);     
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
        viewMode.close();
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
                        exposeArrowPage();
                        // End of code
                    } catch (IOException | InvalidFormatException ex) {
                        Logger.getLogger(ViewModeController.class.getName()).log(Level.SEVERE, null, ex);
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
        Workbook viewMode = WorkbookFactory.create(sort);
        Sheet stage0 =  viewMode.getSheetAt(sheetIndex);     
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
        viewMode.close();
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
    
    // executeReset is checked at initialize
    static public void setReset(){
        executeReset = true;
    }
}  

   


//    
//    @SuppressWarnings("deprecation")
//    @FXML   
//	private void Xslx_to_CSV(File inputFile, File outputFile) {
//			//stores data into files
//			StringBuffer data = new StringBuffer();
//			try{
//				FileOutputStream FileOutput = new FileOutputStream(outputFile);
//				XSSFWorkbook book = new XSSFWorkbook (new FileInputStream(inputFile));//get the workbook object for XLSX file
//				XSSFSheet sheet = book.getSheetAt(0);
//				Row row;
//				Cell cell;
//				
//				Iterator <Row> rowIterator = sheet.iterator();//iterate through each row from first sheet
//				while(rowIterator.hasNext()) {// as long as there's a next row
//					row = rowIterator.next();
//					//For Each row(above) iterate through each column
//					Iterator<Cell> cellIterator = row.cellIterator();
//					while(cellIterator.hasNext()) {
//						cell = cellIterator.next();
//						
//						switch(cell.getCellType()) {
//							case Cell.CELL_TYPE_BOOLEAN:
//								data.append(cell.getBooleanCellValue() + ",");
//								break;
//							
//							case Cell.CELL_TYPE_NUMERIC:
//                                data.append(cell.getNumericCellValue() + ",");
//
//                                break;
//							case Cell.CELL_TYPE_STRING:
//                                	data.append(cell.getStringCellValue() + ",");
//                                break;
//
//							case Cell.CELL_TYPE_BLANK:
//                                data.append("" + ",");
//                                break;
//				default:
//                                data.append(cell + ",");
//
//                        	}
//			}
//					data.append('\n'); 
//				}
//				FileOutput.write(data.toString().getBytes());
//				FileOutput.close();
//			} catch (Exception ioe){
//				ioe.printStackTrace();
//			}
//}
//        
//        
//        @FXML   
//	private void Create_CSV(File inputFile, File outputFile) {
//        inputFile = new File("SORT.xlsx");
//        outputFile = new File("C:\\Users\\Ryan\\Documents\\output.csv");
//	Xslx_to_CSV(inputFile,outputFile);
//        System.out.println("Conversion of XSLX file into CSV succeeded.");
//        }
//        
        
   
    


