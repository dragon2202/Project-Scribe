/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;

import java.lang.Math; // For window resizing.
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D; // For window resizing.
import javafx.stage.FileChooser;

import java.text.DecimalFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Rav19, Cody Moniz, Alvin Thamrin
 */
    

public class CountsController implements Initializable {
    /**
     * Initializes the controller class.
     */
    static private final String FILE_NAME_EXT = Storage.getExt(); // The string that we add in front of a file that is designated as "Advanced SORT"
    static private final String FILE_TEMP_NAME = Storage.getTemp(); // Temporary File that we are actually using
    static private final String FILE_TEMP_NAME_ADV = Storage.getTempAdv(); // The temp file for "Advanced SORT"
    
    static final int STEP_COLUMN = 3;
    static final int TEST_COLUMN = 4;    // This is where you would put the Passed/Failed/Not Tested description
    static final int COMMENT_COLUMN = 5; 
    static final int VARIANT_LIST_COLUMN = 9; // This column contains the list of variant used in the test
    static final int VARIANT_COLUMN = 6; // This column is associated with the STEP_COLUMN and is used for filtering purpose
    
    static final int DATE_TIME_COLUMN = 0; // For number of events

    static final int PR_TYPE_COLUMN = 11; // For counts concerning each type of PR
    static final int PR_NEW_REPEATED_COLUMN = 13; // For counts concerning whether each PR of a given type is new or repeated

    static final int TLAM_TYPE_COLUMN = 8; // For counts concerning each type of TLAM
    static final int TLAM_NUM_ATTEMPTED_COLUMN = 9; // For counts concerning the number of attempted TLAMs for a given type
    static final int TLAM_NUM_SUCCESSFUL_COLUMN = 10; // For counts concerning the number of successful TLAMs for a given type
    
    static private String fileNamePath; // Loaded file placeholder variable
    static private String fileNameOnly; // This one contains the file name only (without path)
    static private int sheetIndex = -1; // Starting position will be at -1
        
    @FXML
    ChoiceBox<String> sheetSelector = new ChoiceBox<>();
    
    // FXML labels
    // Label naming convention
    // numTS, numPassed, numFailed, numNotTested, numComments: number that passed, failed, etc.
    // total: total number (across all sheets)
    // _PN: percent numerator
    // _PD: percent denominator
    // _PR percent result
    @FXML
    Label numTS;
    @FXML
    Label numPassed;
    @FXML 
    Label numFailed;
    @FXML
    Label numNotTested;
    
    @FXML
    Label totalNumTS;
    @FXML
    Label totalNumPassed;
    @FXML 
    Label totalNumFailed;
    @FXML
    Label totalNumNotTested;
    
    @FXML
    Label numTS_PN;
    @FXML
    Label numPassed_PN;
    @FXML 
    Label numFailed_PN;
    @FXML
    Label numNotTested_PN;
    
    @FXML
    Label numTS_PD;
    @FXML
    Label numPassed_PD;
    @FXML 
    Label numFailed_PD;
    @FXML
    Label numNotTested_PD;
    
    @FXML
    Label numTS_PR;
    @FXML
    Label numPassed_PR;
    @FXML 
    Label numFailed_PR;
    @FXML
    Label numNotTested_PR;
    
    @FXML
    Label totalNumTS_PN;
    @FXML
    Label totalNumPassed_PN;
    @FXML 
    Label totalNumFailed_PN;
    @FXML
    Label totalNumNotTested_PN;
    
    @FXML
    Label totalNumTS_PD;
    @FXML
    Label totalNumPassed_PD;
    @FXML 
    Label totalNumFailed_PD;
    @FXML
    Label totalNumNotTested_PD;
    
    @FXML
    Label totalNumTS_PR;
    @FXML
    Label totalNumPassed_PR;
    @FXML 
    Label totalNumFailed_PR;
    @FXML
    Label totalNumNotTested_PR;
    
    @FXML
    Label numEventsLab;
    @FXML 
    Label numPRsLab;
    @FXML
    Label numNewPRsLab;
    @FXML
    Label numExistingPRsLab;
    @FXML
    Label numTLAMsLab;
    @FXML
    Label numAttemptedTLAMsLab;
    @FXML
    Label numSuccessfulTLAMsLab;
    
    @FXML
    Label loadFileLabel;
    
    @FXML 
    private MenuBar CountsMenuBar;
    
    
    @FXML 
    private VBox TestBox;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CountsMenuBar.prefWidthProperty().bind(TestBox.widthProperty());
        // Will get the fileNamePath and fileNameOnly from Storage.java for consistency across all related java files
        fileNameOnly = Storage.getFileNameOnly();
        fileNamePath = Storage.getFileNamePath();
        
        /*
        *   The code below will attempt to automatically load FILE_NAME if it exist
        *   The purpose is to essentially maintain a "persistent" state if the user 
        *   changes mode and come back
        */
        File temp = new File(FILE_TEMP_NAME);
        File temp1 = new File(FILE_TEMP_NAME_ADV);
        // Check if temp file exist in the .jar directory
        if(temp.exists() && !temp.isDirectory()){
            // Check if temp2 file exist in the .jar directory
            if(temp1.exists() && !temp1.isDirectory()){ 
                temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
                temp1.deleteOnExit();
                try {
                    // If file has been opened before, attempt to load file automatically
                    loadSheetsToBox();
                    // Execute count() automatically
                    count();
                    // Set default value of the choicebox to a previously chosen option
                    if(sheetIndex != -1){
                        sheetSelector.getSelectionModel().select(sheetIndex);
                    }

                } catch (EncryptedDocumentException ex) {
                    Logger.getLogger(CountsController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidFormatException ex) {
                    Logger.getLogger(CountsController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CountsController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    Logger.getLogger(CountsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
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

 @FXML
    private void handleCCLog(ActionEvent event) throws IOException{
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) CountsMenuBar.getScene().getWindow();
        CCLog_Stage.setScene(CCLogScene);       
        CCLog_Stage.centerOnScreen();
        CCLog_Stage.setTitle("CCLog");
        CCLog_Stage.show();         
    }
    
    
        @FXML
    private void handleCounts(ActionEvent event) throws IOException{
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) CountsMenuBar.getScene().getWindow();
        Counts_Stage.setScene(Counts_Scene);       
        Counts_Stage.centerOnScreen();
        Counts_Stage.setTitle("Counts");
        Counts_Stage.show();         
    }
    
     @FXML
    private void handleExec(ActionEvent event) throws IOException{
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) CountsMenuBar.getScene().getWindow();
        Exec_Stage.setScene(Exec_Scene);       
        Exec_Stage.centerOnScreen();
        Exec_Stage.setTitle("Executive Summary");
        Exec_Stage.show();         
    }
    
      @FXML
    private void handleShift(ActionEvent event) throws IOException{
        Parent Shift = FXMLLoader.load(getClass().getResource("ShiftEntry.fxml"));
        Scene Shift_Scene = new Scene(Shift);
        Stage Shift_Stage = (Stage) CountsMenuBar.getScene().getWindow();
        Shift_Stage.setScene(Shift_Scene);       
        Shift_Stage.centerOnScreen();
        Shift_Stage.setTitle("Shift Entry");
        Shift_Stage.show();         
    }
    
      @FXML
    private void handleTest(ActionEvent event) throws IOException{
        Parent Test = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) CountsMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();         
    }
    
    
    @FXML
    private void handleViewButton(ActionEvent event) throws IOException {
        Parent Test = FXMLLoader.load(getClass().getResource("ViewMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) CountsMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();  
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) throws IOException{        
        Parent Test = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) CountsMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();  
    }
      
    @FXML
    public void count() throws EncryptedDocumentException, InvalidFormatException, IOException, NullPointerException {
        if(sheetIndex == -1){
            return; // Do nothing if user haven't picked anything in the ChoiceBox yet
        }

        // Select file and establish necessary objects
        FileInputStream file = new FileInputStream(new File(FILE_TEMP_NAME));
        Workbook workbook1 = WorkbookFactory.create(file);

        // Get number of sheets
        int numSheets = workbook1.getNumberOfSheets();

        // Declare counter array variables
        int[] numSteps = new int[numSheets];
        int[] numPass = new int[numSheets];
        int[] numFail = new int[numSheets];
        int[] numNT = new int[numSheets];
        int[] numComment = new int[numSheets];

        // Initialize all to zero
        for (int i = 0; i < numSheets; i++){
                numSteps[i] = 0;
                numPass[i] = 0;
                numFail[i] = 0;
                numNT[i] = 0;
                numComment[i] = 0;
        }

        // Total count variables (across all sheets)
        int totalNumSteps = 0;
        int totalNumPass = 0;
        int totalNumFail = 0;
        int totalNumNT = 0;
        int totalNumComment = 0; 

        // Loop through each sheet
        for (int i = 0; i < numSheets; i++) {
                Sheet sheet_i =  workbook1.getSheetAt(i);    	
                Iterator<Row> iterator = sheet_i.iterator();
                // Skip header
                iterator.next();
                // Core logic of counts, iterate and check each condition
                while (iterator.hasNext()) {
                        Row nextRow = iterator.next();
                        if (!nextRow.getCell(STEP_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                                numSteps[i]++;
                        }
                        if (nextRow.getCell(TEST_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("Passed") /* && nextRow.getCell(TEST_COLUMN) != null*/) {
                                numPass[i]++;
                        } else if (nextRow.getCell(TEST_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("Failed")) {
                                numFail[i]++;
                        } else if (nextRow.getCell(TEST_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("Not Tested")) {
                                numNT[i]++;
                        }
                        if (!nextRow.getCell(COMMENT_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("") && !nextRow.getCell(COMMENT_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("Comment")) {
                                numComment[i]++;
                        }
                }
        }

        // Calculate counts across all sheets
        for (int i = 0; i < numSheets; i++) {
                totalNumSteps += numSteps[i];
                totalNumPass += numPass[i];
                totalNumFail += numFail[i];
                totalNumNT += numNT[i];
                totalNumComment += numComment[i];
        }
        
        // Create object to round doubles to two decimal places
    	DecimalFormat roundTwo = new DecimalFormat(".##");

        // Set GUI labels depending on choice box selection (counts)
        numTS.setText("" + numSteps[sheetIndex]);
        numPassed.setText("" + numPass[sheetIndex]);
        numFailed.setText("" + numFail[sheetIndex]);
        numNotTested.setText("" + numNT[sheetIndex]);

        numTS_PN.setText("" + numSteps[sheetIndex]);
        numPassed_PN.setText("" + numPass[sheetIndex]);
        numFailed_PN.setText("" + numFail[sheetIndex]);
        numNotTested_PN.setText("" + numNT[sheetIndex]);

        numTS_PD.setText("" + numSteps[sheetIndex]);
        numPassed_PD.setText("" + numSteps[sheetIndex]);
        numFailed_PD.setText("" + numSteps[sheetIndex]);
        numNotTested_PD.setText("" + numSteps[sheetIndex]);

        numTS_PR.setText("" + roundTwo.format((double)numSteps[sheetIndex]/(double)numSteps[sheetIndex] * 100));
        numPassed_PR.setText("" + roundTwo.format((double)numPass[sheetIndex]/(double)numSteps[sheetIndex] * 100));
        numFailed_PR.setText("" + roundTwo.format((double)numFail[sheetIndex]/(double)numSteps[sheetIndex] * 100));
        numNotTested_PR.setText("" + roundTwo.format((double)numNT[sheetIndex]/(double)numSteps[sheetIndex] * 100));

        totalNumTS.setText("" + totalNumSteps);
        totalNumPassed.setText("" + totalNumPass);
        totalNumFailed.setText("" + totalNumFail);
        totalNumNotTested.setText("" + totalNumNT);

        totalNumTS_PN.setText("" + numSteps[sheetIndex]);
        totalNumPassed_PN.setText("" + numPass[sheetIndex]);
        totalNumFailed_PN.setText("" + numFail[sheetIndex]);
        totalNumNotTested_PN.setText("" + numNT[sheetIndex]);

        totalNumTS_PD.setText("" + totalNumSteps);
        totalNumPassed_PD.setText("" + totalNumPass);
        totalNumFailed_PD.setText("" + totalNumFail);
        totalNumNotTested_PD.setText("" + totalNumNT);

        totalNumTS_PR.setText("" + roundTwo.format((double)numSteps[sheetIndex]/(double)totalNumSteps * 100));
        totalNumPassed_PR.setText("" + roundTwo.format((double)numPass[sheetIndex]/(double)totalNumPass * 100));
        totalNumFailed_PR.setText("" + roundTwo.format((double)numFail[sheetIndex]/(double)totalNumFail * 100));
        totalNumNotTested_PR.setText("" + roundTwo.format((double)numNT[sheetIndex]/(double)totalNumNT * 100));

        // CCLog Counter variables
        int numEvents = 0;
        int numPRs = 0;
        int numNewPR = 0;
        int numRepeatedPR = 0;
        int numTLAM = 0;
        int numAttemptedTLAM = 0;
        int numSuccessfulTLAM = 0;

        // Local variables
        String stringToInt;

        
        // Select file and establish necessary objects
        File file1 = new File(FILE_NAME_EXT + fileNameOnly); // Select the "Advanced SORT" file associated with the chosen file
        // Check if file1 exist in the .jar directory
        if(!file1.exists() || file1.isDirectory()){ 
            // If not, then exit the code
            return;
        }
        // If yes, then continue
        FileInputStream advancedFile = new FileInputStream(new File(FILE_NAME_EXT + fileNameOnly)); // Select the "Advanced SORT" file associated with the chosen file
        Workbook advancedWorkbook1 = WorkbookFactory.create(advancedFile);

        Sheet CCLog =  advancedWorkbook1.getSheetAt(0);    	
        Iterator<Row> iterator = CCLog.iterator();

        while (iterator.hasNext()) {
        Row nextRow = iterator.next();
            if (!nextRow.getCell(DATE_TIME_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("")) {
                numEvents++;
            }
            if (!nextRow.getCell(PR_TYPE_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("No Data Provided")) {
                numPRs++;
            }
            if (nextRow.getCell(PR_NEW_REPEATED_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("New")) {
                numNewPR++;
            }
            if (nextRow.getCell(PR_NEW_REPEATED_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("Existing")) {
                numRepeatedPR++;
            }
            if (!nextRow.getCell(TLAM_TYPE_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("No Data Provided")) {
                numTLAM++;
            }
            if (!nextRow.getCell(TLAM_NUM_ATTEMPTED_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("No Data Provided")) {
                stringToInt = nextRow.getCell(TLAM_NUM_ATTEMPTED_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                numAttemptedTLAM += Integer.parseInt(stringToInt);
            }
            if (!nextRow.getCell(TLAM_NUM_SUCCESSFUL_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue().equals("No Data Provided")) {
                stringToInt = nextRow.getCell(TLAM_NUM_SUCCESSFUL_COLUMN, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                numSuccessfulTLAM += Integer.parseInt(stringToInt);
            }
        }

        numEventsLab.setText("" + numEvents);
        numPRsLab.setText("" + numPRs);
        numNewPRsLab.setText("" + numNewPR);
        numExistingPRsLab.setText("" + numRepeatedPR);
        numTLAMsLab.setText("" + numTLAM);
        numAttemptedTLAMsLab.setText("" + numAttemptedTLAM);
        numSuccessfulTLAMsLab.setText("" + numSuccessfulTLAM);
        
        file.close();
        workbook1.close();
        advancedFile.close();
        advancedWorkbook1.close();
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
        
        fileNamePath = tests.getAbsolutePath(); // Important! FILE_NAME holds the target's name/path
        fileNameOnly = tests.getName();
        // Set temp file 
        File ori = new File(fileNamePath);
        File temp = new File(FILE_TEMP_NAME); 
        Files.copy(ori.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        
        
        // Then we set up temp file for the "Advance SORT" file simultaneously
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
        
        // Reset global variable and associated GUI labels
        sheetIndex = -1;
        numTS.setText("---");
        numTS_PN.setText("---");
        numTS_PD.setText("---");
        numTS_PR.setText("---");
        numPassed.setText("---");
        numPassed_PN.setText("---");
        numPassed_PD.setText("---");
        numPassed_PR.setText("---");
        numFailed.setText("---");
        numFailed_PN.setText("---");
        numFailed_PD.setText("---");
        numFailed_PR.setText("---");
        numNotTested.setText("---");
        numNotTested_PN.setText("---");
        numNotTested_PD.setText("---");
        numNotTested_PR.setText("---");

        totalNumTS.setText("---");
        totalNumTS_PN.setText("---");
        totalNumTS_PD.setText("---");
        totalNumTS_PR.setText("---");
        totalNumPassed.setText("---");
        totalNumPassed_PN.setText("---");
        totalNumPassed_PD.setText("---");
        totalNumPassed_PR.setText("---");
        totalNumFailed.setText("---");
        totalNumFailed_PN.setText("---");
        totalNumFailed_PD.setText("---");
        totalNumFailed_PR.setText("---");
        totalNumNotTested.setText("---");
        totalNumNotTested_PN.setText("---");
        totalNumNotTested_PD.setText("---");
        totalNumNotTested_PR.setText("---");
        
        numEventsLab.setText("---");
        numPRsLab.setText("---");
        numNewPRsLab.setText("---");
        numExistingPRsLab.setText("---");
        numTLAMsLab.setText("---");
        numAttemptedTLAMsLab.setText("---");
        numSuccessfulTLAMsLab.setText("---");
        
        // Clear any items currently in choice box
        sheetSelector.getItems().clear();
        
        loadSheetsToBox();
         
        // Set file names for other files
        Storage.setNameTwice(fileNamePath, fileNameOnly);
        TestModeController.setReset();
        ViewModeController.setReset();
    }
    
    // This function will load up FILE_TEMP_NAME and attempt to fill in all the sheets into the ChoiceBox sheetSelector
    private void loadSheetsToBox() throws IOException, InvalidFormatException{       
        // Open the excel file through FILE_TEMP_NAME
        FileInputStream file = new FileInputStream(new File(FILE_TEMP_NAME));
        Workbook workbook1 = WorkbookFactory.create(file);
        int numSheets = workbook1.getNumberOfSheets();
        // Loop through each sheet and add it to the choice box
        for (int i = 0; i < numSheets; i++) {
            sheetSelector.getItems().add(workbook1.getSheetName(i));
        }
        // Add a ChangeListener for sheetSelector (ChoiceBox)
        sheetSelector.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                // Code will execute here once an option is picked
                // new_value.intValue() seems to return the index of which choice you've selected
                sheetIndex = new_value.intValue();
            }
        });
        
        loadFileLabel.setVisible(false);
        
        // Closing FileInputStream
        file.close();
        workbook1.close();
    }
}