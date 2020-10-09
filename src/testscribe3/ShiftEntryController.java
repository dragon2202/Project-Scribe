/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 

package testscribe3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Rav19, Alvin Thamrin
 */

public class ShiftEntryController implements Initializable {
    static private final String FILE_NAME_EXT = Storage.getExt(); // The string that we add in front of a file that is designated as "Advanced SORT"
    static private final String FILE_TEMP_NAME = Storage.getTemp(); // Temporary File that we are actually using
    static private final String FILE_TEMP_NAME_ADV = Storage.getTempAdv(); // The temp file for "Advanced SORT"
    
    static private String fileNameOnly;       // Contains "Advanced SORT" name. Is the combination of FILE_NAME_EXT and the file name only (without path)
    static private String fileNamePath;   // Contains the selected SORT file (that the user have chosen)
    static private int sheetIndex = 1;
    static private int count = 1;
    static private String text = "Shift: ";
    static private int click = 0;
    private boolean sem = false;
    boolean lock = true;
    int numofRow;
    
    int arraylistCount = 0;
    
    @FXML 
    private MenuBar ShiftMenuBar;
    @FXML 
    private MenuItem saveItem, saveItemAs;
    @FXML 
    private VBox TestBox;
      
    @FXML 
    public VBox startTime;      
      
    @FXML 
    public VBox stopTime;
    @FXML 
    public VBox testDirector;
    @FXML 
    public VBox personnel;
    @FXML
    public VBox shift;
    @FXML
    private TextField testDuration, shiftLength, numShifts;
      
    private String labels[] = {"Test Duration", "Shift Length", "Number of Shifts"};
    private String labels2[] = {"", "Start Time", "Stop Time", "Test Director", "Personnel"};
      
    
    @FXML
    private void callOnScreenKeyboard(ActionEvent event) throws IOException{  
        try{  
            Runtime.getRuntime().exec("cmd /c C:\\Windows\\System32\\osk.exe");  
        } catch (Exception e){  
            System.out.println("Error: Unable to open on screen keyboard");  
        }  
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
        
        // Set temp for SORT file first 
        // One can safely assume that the loaded file must exist
        fileNamePath = tests.getAbsolutePath(); // Important! FILE_NAME_PATH holds the target's name and path
        File file1 = new File(fileNamePath);
        File file2 = new File(FILE_TEMP_NAME); 
        Files.copy(file1.toPath(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        file2.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        
        fileNameOnly = tests.getName();
        /*
        * The idea for the code above is that since the "Advanced SORT" is unique to each SORT file
        * and since the "Advanced SORT" is located within the program directory (which I assumed user
        * has limited access to), the only way to access these files is to load the associated SORT files
        * instead. Hence, this is an indirect way to access the "Advanced SORT" file by loading up the
        * "publicly" available SORT file instead.
        */
        
        // Then we set up temp file for the "Advance SORT" file
        // Copying from original to a temporary one so we could fiddle around with greater leeway
        File ori = new File(FILE_NAME_EXT + fileNameOnly);
        File temp = new File(FILE_TEMP_NAME_ADV); 
        // Check if the "Advance SORT" file exist or not
        if(ori.exists() && !ori.isDirectory()){
            // If it exist
            Files.copy(ori.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
            
            populateGUI();
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
            Files.copy(ori.toPath(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            temp.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        }
        
        saveItem.setDisable(false); // Enable the Save button in the Menu
        saveItemAs.setDisable(false); // Enable the Save As button in the Menu
        
        
        // Set file names for other files
        Storage.setNameTwice(fileNamePath, fileNameOnly);
        TestModeController.setReset();
        ViewModeController.setReset();
    }
    
    // This function pretty much load the files from FILE_TEMP_NAME into the GUI
    private void populateGUI() throws FileNotFoundException, IOException{
        DataFormatter df = new DataFormatter();
        FileInputStream file1 = null; 
        try {
            file1 = new FileInputStream(new File(FILE_TEMP_NAME_ADV));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Workbook workbook1 = null;
        try {
            workbook1 = WorkbookFactory.create(file1);
        } catch (IOException ex) {
            Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Sheet sheet1 =  workbook1.getSheetAt(sheetIndex);
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(0);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            //String tempString = df.formatCellValue(row.getCell(0));
            //testDuration.setText(tempString);
        }
        else{
            System.out.println("Cell is empty!"); // Debugging purpose
        }
        
         
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(1);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            //String tempString = df.formatCellValue(row.getCell(0));
            //shiftLength.setText(tempString);
        }
        
        
         
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(2);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
                        
           //String tempString = df.formatCellValue(row.getCell(0));
            //numShifts.setText(tempString);
        }
        try {
            loadInfo();
        } catch (IOException ex) {
            Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            file1.close();
        } catch (IOException ex) {
            Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ShiftMenuBar.prefWidthProperty().bind(TestBox.widthProperty());
        // Will get the fileNamePath and fileNameOnly from Storage.java for consistency across all related java files
        fileNameOnly = Storage.getFileNameOnly();
        fileNamePath = Storage.getFileNamePath();
        
        /*
        *   The code below will attempt to automatically load FILE_TEMP_NAME_ADV if it exist
        *   with a condition that FILE_TEMP must also exist
        *   The purpose is to essentially maintain a "persistent" state if the user 
        *   changes mode and come back
        */
        File temp1 = new File(FILE_TEMP_NAME_ADV);
        File temp2 = new File(FILE_TEMP_NAME);
        // Check if temp1 file exist in the .jar directory
        if(temp1.exists() && !temp1.isDirectory()){ 
            // Check if temp2 file exist in the .jar directory
            if(temp2.exists() && !temp2.isDirectory()){ 
                saveItem.setDisable(false); // Enable the Save button in the Menu
                saveItemAs.setDisable(false); // Enable the Save As button in the Menu
                temp1.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
                temp2.deleteOnExit();
                try {
                    // Automatically populate the GUI with the variables found in the temp file
                    populateGUI();
                } catch (IOException ex) {
                    Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }    
    
    List<TextField> allStartTime = new ArrayList<>();
    List<TextField> allStopTime = new ArrayList<>();
    List<TextField> allTestDirector = new ArrayList<>();
    List<TextField> allPersonnel = new ArrayList<>();
    List<String> allShiftNum = new ArrayList<>();
    
    List<String> ifNum = new ArrayList<>();
    List<String> ifNum2 = new ArrayList<>();//start time
    List<String> ifNum3 = new ArrayList<>();//stop time
    
    TextField ShiftNum;
    TextField StartTime;
    TextField StopTime;
    TextField TestDirector;
    TextField Personnel;
    @FXML
    public void AddTextFields(){
        arraylistCount++;
        String string = "Shift ";
        String shiftString = (string + arraylistCount);
        ShiftNum = new TextField(shiftString);
        allShiftNum.add(string + arraylistCount);
        StartTime = new TextField();
        allStartTime.add(StartTime);
        StopTime = new TextField();
        allStopTime.add(StopTime);
        TestDirector = new TextField();
        allTestDirector.add(TestDirector);
        Personnel = new TextField();
        allPersonnel.add(Personnel);
        
        shift.setSpacing(20.0);
        startTime.setSpacing(20.0);
        stopTime.setSpacing(20.0);
        testDirector.setSpacing(20.0);
        personnel.setSpacing(20.0);
        
        shift.getChildren().add(ShiftNum);
        startTime.getChildren().add(StartTime);
        stopTime.getChildren().add(StopTime);
        testDirector.getChildren().add(TestDirector);
        personnel.getChildren().add(Personnel);
    }
    
    @FXML
    public void dummy() throws FileNotFoundException, IOException{
        
    }
    
    
    @FXML // // This is the ActionEvent that is associated with the Save button
    public void saveDataShift() throws FileNotFoundException, IOException, InvalidFormatException{
        if((FILE_NAME_EXT + fileNameOnly) == null || (FILE_NAME_EXT + fileNameOnly).isEmpty()){
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
        File ori = new File((FILE_NAME_EXT + fileNameOnly));
        File temp = new File(FILE_TEMP_NAME_ADV); 
        Files.copy(temp.toPath(), ori.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        // Also saves FILE_TEMP_NAME
        File ori2 = new File(fileNamePath);
        File temp2 = new File(FILE_TEMP_NAME);
        //Check if FILE_TEMP_NAME exist or not
        if(!temp2.exists() || temp2.isDirectory()){
            // If no, create a new one
            Files.copy(ori2.toPath(), temp2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            temp2.deleteOnExit(); // Temp file needs to be removed upon the termination of VM
        }
        else{
            // If yes, then replace the selected file with this temp file
            Files.copy(temp2.toPath(), ori2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        /*
        * The reasoning behind this block of code is just to add cover your tracks if user decided to go straight to any mode
        * that deals with "Advanced SORT" first and then load a file from there
        */
        JOptionPane.showMessageDialog(null, "Save Successful");
    }
    // saveDataShiftAs is associated with the Save As button in the File Menu
    @FXML 
    private void saveDataShiftAs(ActionEvent event) throws FileNotFoundException, IOException, InvalidFormatException{
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
        
        fileNamePath = tests.getAbsolutePath();
        // Rewriting FILE_NAME with the file selected by user
        fileNameOnly = tests.getName();
        /*
        * The idea for the code above is that since the "Advanced SORT" is unique to each SORT file
        * and since the "Advanced SORT" is located within the program directory (which I assumed user
        * has limited access to), the only way to access these files is to load the associated SORT files
        * instead. Hence, this is an indirect way to access the "Advanced SORT" file by loading up the
        * "publicly" available SORT file instead.
        * 
        * In this case, Save As would overwrite/transplant the information in the "Advanced SORT" into
        * whichever file the user have chosen. User is not allowed to type a non-existing file (a.k.a. new file),
        * since the logic would be too complicated to deal with
        */
        
        // Actual saving
        savePage(); // The actual saving mechanic used in the GUI
        // Copying from temp to original excel file to maintain illusion of saving
        File ori = new File((FILE_NAME_EXT + fileNameOnly));
        File temp = new File(FILE_TEMP_NAME_ADV); 
        Files.copy(temp.toPath(), ori.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        // Also saves FILE_TEMP_NAME
        File ori2 = new File(fileNamePath);
        File temp2 = new File(FILE_TEMP_NAME);
        Files.copy(temp2.toPath(), ori2.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        // Set file names for other files
        Storage.setNameTwice(fileNamePath, fileNameOnly);
        JOptionPane.showMessageDialog(null, "Save Successful");
    }
    // This function saves whatever is on the screen into the temp file  
    private void savePage() throws IOException, InvalidFormatException{
        FileInputStream file = new FileInputStream(FILE_TEMP_NAME_ADV);
        XSSFWorkbook wb = new XSSFWorkbook(file);
        XSSFSheet CorrectSheet = null;
        XSSFRow sheetrow = null;
        
        for(int i = 0; i < wb.getNumberOfSheets(); i++){
            Sheet sheet = wb.getSheetAt(i);
            if(sheet.getSheetName().equals("Shift Entry")){
                CorrectSheet = wb.getSheetAt(i);
            }
        }
        String [] shift = new String[allShiftNum.size()];
        String [] starttime = new String[allStartTime.size()];
        String [] stoptime = new String[allStopTime.size()];
        String [] testdirector = new String[allTestDirector.size()];
        String [] personnel = new String[allPersonnel.size()];
        
        for(int i = 0; i < allStartTime.size(); i++){
            shift[i] = allShiftNum.get(i);
            if(checkifNum(allStartTime.get(i).getText()) == false){
                ifNum2.add("" + (i + 1));
            } else {
                starttime[i] = allStartTime.get(i).getText();
            }
            
            if(checkifNum(allStopTime.get(i).getText()) == false){
                ifNum3.add("" + (i + 1));
            } else {
                stoptime[i] = allStopTime.get(i).getText();
            }
            testdirector[i] = allTestDirector.get(i).getText();
            personnel[i] = allPersonnel.get(i).getText();
        }
        for(int i = 0; i < allStartTime.size(); i++){
            sheetrow = CorrectSheet.createRow(i);
            if(i == 0){
                if(checkifNum(testDuration.getText()) == false){
                    ifNum.add("Test Duration");
                } else {
                    sheetrow.createCell(6).setCellValue(testDuration.getText());
                }
                
                if(checkifNum(shiftLength.getText()) == false){
                    ifNum.add("Shift Length");
                } else {
                    sheetrow.createCell(7).setCellValue(shiftLength.getText());
                }
                
                if(checkifNum(numShifts.getText()) == false){
                    ifNum.add("Number of Shifts");
                } else {
                    sheetrow.createCell(8).setCellValue(numShifts.getText());
                }
            }
            sheetrow.createCell(0).setCellValue(shift[i]);
            sheetrow.createCell(1).setCellValue(starttime[i]);
            sheetrow.createCell(2).setCellValue(stoptime[i]);
            sheetrow.createCell(3).setCellValue(testdirector[i]);
            sheetrow.createCell(4).setCellValue(personnel[i]);
        }
        if(ifNum.size() + ifNum2.size()+ ifNum3.size() > 0){
            JOptionPane.showMessageDialog(null, 
                "The following are not numbers \n"
                + "General Info: " + ifNum + "\n"
                + "Start Time: " + ifNum2 + "\n"
                + "Stop Time: " + ifNum3 + "\n");
            ifNum.clear();
            ifNum2.clear();
            ifNum3.clear();
            return;
        }
        
        file.close();
        FileOutputStream OutFile = new FileOutputStream(new File(FILE_TEMP_NAME_ADV));
        wb.write(OutFile);
        OutFile.close();
    }
    
    private void loadInfo() throws FileNotFoundException, IOException{
        DataFormatter df = new DataFormatter(); // This is a data formatter which is useful to convert cell types into anything you want. In this case, String.
        int num = 0;
        String string = "Shift ";
        
        startTime.setSpacing(20.0);
        stopTime.setSpacing(20.0);
        testDirector.setSpacing(20.0);
        personnel.setSpacing(20.0);
        shift.setSpacing(20.0);
        
        Cell testduration = null;
        Cell shiftlength = null;
        Cell numofshift = null;
        int testduration2 = 0;
        int shiftlength2 = 0;
        int numofshift2 = 0;
        
        Cell shiftC = null;
        Cell startC = null;
        Cell stopC = null;
        Cell directorC = null;
        Cell personnelC = null;
        
        String shift2 = null;
        String start2 = null;
        String stop2 = null;
        String director2 = null;
        String personnel2 = null;
        
        FileInputStream file = new FileInputStream(FILE_TEMP_NAME_ADV);
        XSSFWorkbook wb = new XSSFWorkbook(file);
        XSSFSheet CorrectSheet = null;
        XSSFRow sheetrow = null;
        int loop = 0;
        
        for(int i = 0; i < wb.getNumberOfSheets(); i++){
            Sheet sheet = wb.getSheetAt(i);
            if(sheet.getSheetName().equals("Shift Entry")){
                CorrectSheet = wb.getSheetAt(i);
            }
        }
        while(lock){//gets number of events
            if(CorrectSheet.getRow(0) == null){
                return;
            }
            if(CorrectSheet.getRow(numofRow + 1) == null){
                lock = false;
            }
            numofRow++;
        }
        for(int i = 0; i < numofRow; i++){
            sheetrow = CorrectSheet.getRow(i);
            if (i == 0){
                testDuration.setText(df.formatCellValue(sheetrow.getCell(6)));
                shiftLength.setText(df.formatCellValue(sheetrow.getCell(7)));
                numShifts.setText(df.formatCellValue(sheetrow.getCell(8)));
            }
            shiftC = sheetrow.getCell(0);
            startC = sheetrow.getCell(1);
            stopC = sheetrow.getCell(2);
            directorC = sheetrow.getCell(3);
            personnelC = sheetrow.getCell(4);
            
            shift2 = shiftC.getStringCellValue();
            
            start2 = df.formatCellValue(startC);
            stop2 = df.formatCellValue(stopC);
            director2 = directorC.getStringCellValue();
            personnel2 = personnelC.getStringCellValue();
            
            StartTime = new TextField(start2);
            StopTime = new TextField(stop2);
            TestDirector = new TextField(director2);
            Personnel = new TextField(personnel2);
            ShiftNum = new TextField(string + (i + 1));
            
            shift.getChildren().add(ShiftNum);
            startTime.getChildren().add(StartTime);
            stopTime.getChildren().add(StopTime);
            testDirector.getChildren().add(TestDirector);
            personnel.getChildren().add(Personnel);
            
            allShiftNum.add(string + (i + 1));
            allStartTime.add(StartTime);
            allStopTime.add(StopTime);
            allTestDirector.add(TestDirector);
            allPersonnel.add(Personnel);
        }
        
        arraylistCount = numofRow;
        
        
        
    }
    
    
    private boolean checkifNum(String string){
        boolean isInt = true;
        try{
            Integer.parseInt(string);
        }
        catch(NumberFormatException e){
            isInt = false;
        }
        return isInt;
    }
    /*
    public class InfoShift{
        private StringProperty label = new SimpleStringProperty();
        public StringProperty labelProperty(){
            return label;
        }
        
        public final String gettext(){
            return labelProperty().get();
        }
    }
    
    public boolean validateField1(){
        Pattern num = Pattern.compile("[0-9]+");
        Matcher match = num.matcher(testDuration.getText());
        
        if(match.find() && match.group().equals(testDuration.getText()))
            return true;
        else
        {
            Alert numAlert = new Alert(AlertType.WARNING);
            numAlert.setTitle("Number Validation");
            numAlert.setHeaderText(null);
            numAlert.setContentText("Field Must Be A Number!");
            numAlert.showAndWait();
            
            return false;
        }
    }
    
       public boolean validateField2(){
        Pattern num = Pattern.compile("[0-9]+");
        Matcher match = num.matcher(shiftLength.getText());
        
        if(match.find() && match.group().equals(shiftLength.getText()))
            return true;
        else
        {
            Alert numAlert = new Alert(AlertType.WARNING);
            numAlert.setTitle("Number Validation");
            numAlert.setHeaderText(null);
            numAlert.setContentText("Field Must Be A Number!");
            numAlert.showAndWait();
            
            return false;
        }
    }
    
    
    @FXML
    private void saveDataShift() throws FileNotFoundException, IOException{
        FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(file);
        XSSFSheet CorrectSheet = null;
        XSSFRow sheetrow = null;
        boolean lock = true;
        int starttime = 0;
        int stoptime = 0;
        int row = 0;
        int counter = 0;
        int notInt = 0;
        for(int i = 0; i < wb.getNumberOfSheets(); i++){
            Sheet sheet = wb.getSheetAt(i);
            if(sheet.getSheetName().equals("Shift Entry")){
                CorrectSheet = wb.getSheetAt(i);
            }
        }
        
        if(arraylistCount == 0 && click == 0){
            JOptionPane.showMessageDialog(null, "Please Create a Shift");
            return;
        }
        
        if(checkifNum(testDuration.getText()) == false){
            notInt += 1;
        }
        if(checkifNum(shiftLength.getText()) == false){
            notInt += 2;
        }
        if(checkifNum(numShifts.getText()) == false){
            notInt += 5;
        }
        
        switch(notInt){
            case 1: JOptionPane.showMessageDialog(null, "Enter a number for Test Duration.");
                    return;
            case 2: JOptionPane.showMessageDialog(null, "Enter a number for Shift Length.");
                    return;
            case 3: JOptionPane.showMessageDialog(null, "Enter a number for Test Duration and Shift Length.");
                    return;
            case 5: JOptionPane.showMessageDialog(null, "Enter a number for Number of Shifts.");
                    return;
            case 6: JOptionPane.showMessageDialog(null, "Enter a number for Test Duration and Number of Shifts.");
                    return;
            case 7: JOptionPane.showMessageDialog(null, "Enter a number for Shift length and Number of Shifts.");
                    return;
            case 8: JOptionPane.showMessageDialog(null, "Enter a number for Test Duration, Shift length and Number of Shifts.");
                    return;
            case 0: break;
            
        }
        if (click > 0){
        if(checkifNum(start.gettext()) == false){
            notInt += 1;
        }
        if(checkifNum(stop.gettext()) == false){
            notInt+= 2;
        }
        switch(notInt){
            case 1: JOptionPane.showMessageDialog(null, "Enter a number for Start Time.");
                return;
            case 2: JOptionPane.showMessageDialog(null, "Enter a number for Stop Time");
                 return;
            case 3: JOptionPane.showMessageDialog(null, "Enter a number for Start Time and Stop Time");
                return;
             case 0: break;
        }
        
        shiftNum.add(text + arraylistCount);
        storage.add(start.gettext());
        storage2.add(stop.gettext());
        storage3.add(TestDir.gettext());
        storage4.add(personnelinfo.gettext());
    }
        while(lock){
            //
                for(int i = 0; i < storage.size(); i++){
                    if(CorrectSheet.getRow(counter + i) == null){
                        sheetrow = CorrectSheet.createRow(counter + i);
                    } else {
                        sheetrow = CorrectSheet.getRow(counter + i);
                    }
                    
                    if (arraylistCount > 0 && click == 0){
                        sheetrow.createCell(6).setCellValue(Integer.parseInt(testDuration.getText()));
                        sheetrow.createCell(7).setCellValue(Integer.parseInt(shiftLength.getText()));
                        sheetrow.createCell(8).setCellValue(Integer.parseInt(numShifts.getText()));
                        
                        file.close();
                        FileOutputStream OutFile = new FileOutputStream(new File("ADVANCEDSORT.xlsx"));
                        wb.write(OutFile);
                        OutFile.close();
                        JOptionPane.showMessageDialog(null, "Save Successful");
                        return;
                    }
                    
                    starttime = Integer.parseInt(storage.get(i));
                    stoptime = Integer.parseInt(storage2.get(i));
                    sheetrow.createCell(0).setCellValue(shiftNum.get(i));
                    sheetrow.createCell(1).setCellValue(starttime);
                    sheetrow.createCell(2).setCellValue(stoptime);
                    sheetrow.createCell(3).setCellValue(storage3.get(i));
                    sheetrow.createCell(4).setCellValue(storage4.get(i));
                lock = false;
            }
            counter++;
        }
        
        sheetrow = CorrectSheet.getRow(0);
        sheetrow.createCell(6).setCellValue(Integer.parseInt(testDuration.getText()));
        sheetrow.createCell(7).setCellValue(Integer.parseInt(shiftLength.getText()));
        sheetrow.createCell(8).setCellValue(Integer.parseInt(numShifts.getText()));
        
        click = 0;
        counter = 0;
        lock = true;
        
        file.close();
        FileOutputStream OutFile = new FileOutputStream(new File("ADVANCEDSORT.xlsx"));
        wb.write(OutFile);
        OutFile.close();
        JOptionPane.showMessageDialog(null, "Save Successful");
    }
    
    
    List<InfoShift> starttimeInfo  = new ArrayList<>();
    List<InfoShift> stoptimeInfo  = new ArrayList<>();
    List<InfoShift> TestDirectInfo  = new ArrayList<>();
    List<InfoShift> personnelInfo  = new ArrayList<>();
    
    InfoShift start = new InfoShift();
    InfoShift stop = new InfoShift();
    InfoShift TestDir = new InfoShift();
    InfoShift personnelinfo = new InfoShift();
    
    private ArrayList<String>  shiftNum = new ArrayList<>();//start time
    private ArrayList<String>  storage = new ArrayList<>();//start time
    private ArrayList<String>  storage2 = new ArrayList<>();//stop time
    private ArrayList<String>  storage3 = new ArrayList<>();// test director
    private ArrayList<String>  storage4 = new ArrayList<>();//personnel
    
    
    @FXML
    public void testAction(){
        System.out.println(storage.size());
        System.out.println(storage);
    }
    
    private boolean checkifNum(String string){
        boolean isInt = true;
        try{
            Integer.parseInt(string);
        }
        catch(NumberFormatException e){
            isInt = false;
        }
        return isInt;
    }
    
    @FXML
    private void addShiftInfo(ActionEvent event) throws IOException{
        int notInt = 0;
               
        if(click > 0){
            
            if(checkifNum(start.gettext()) == false){
            notInt += 1;
            }
            if(checkifNum(stop.gettext()) == false){
                notInt+= 2;
            }

            switch(notInt){
                case 1: JOptionPane.showMessageDialog(null, "Enter a number for Start Time.");
                    return;
                case 2: JOptionPane.showMessageDialog(null, "Enter a number for Stop Time");
                    return;
                case 3: JOptionPane.showMessageDialog(null, "Enter a number for Start Time and Stop Time");
                    return;
                case 0: break;
            }
            
            System.out.println(text + (arraylistCount));
            shiftNum.add(text + (arraylistCount));
            System.out.println(text + (arraylistCount));
            storage.add(start.gettext());
            storage2.add(stop.gettext());
            storage3.add(TestDir.gettext());
            storage4.add(personnelinfo.gettext());
            
        }
        click++;
        
        startTime.setSpacing(20.0);
        stopTime.setSpacing(20.0);
        TestDirector.setSpacing(20.0);
        Personnel.setSpacing(20.0);
        string.setSpacing(20.0);
        TextField starttime = new TextField();
        TextField stoptime = new TextField();
        TextField testdirector = new TextField();
        TextField personnel = new TextField();
        TextField shift = new TextField(text + ++arraylistCount);
        
        
        
        startTime.getChildren().add(starttime);
        stopTime.getChildren().add(stoptime);
        TestDirector.getChildren().add(testdirector);
        Personnel.getChildren().add(personnel);
        string.getChildren().add(shift);
        
        starttimeInfo.add(start);
        start.labelProperty().bind(starttime.textProperty());
        
        stoptimeInfo.add(stop);
        stop.labelProperty().bind(stoptime.textProperty());
        
        TestDirectInfo.add(TestDir);
        TestDir.labelProperty().bind(testdirector.textProperty());
        
        personnelInfo.add(personnelinfo);
        personnelinfo.labelProperty().bind(personnel.textProperty());
        
    }
    */
    
    
     private static boolean isCellEmpty(final Cell cell) { 
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) { 
            return true;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
            return true;
        }
        return false;
    }
    
    
    
    
 @FXML
    private void handleCCLog(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
                System.out.println("TEst!");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) ShiftMenuBar.getScene().getWindow();
        CCLog_Stage.setScene(CCLogScene);       
        CCLog_Stage.centerOnScreen();
        CCLog_Stage.setTitle("CCLog");
        CCLog_Stage.show();         
    }
    
    
        @FXML
    private void handleCounts(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) ShiftMenuBar.getScene().getWindow();
        Counts_Stage.setScene(Counts_Scene);       
        Counts_Stage.centerOnScreen();
        Counts_Stage.setTitle("Counts");
        Counts_Stage.show();         
    }
    
     @FXML
    private void handleExec(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) ShiftMenuBar.getScene().getWindow();
        Exec_Stage.setScene(Exec_Scene);       
        Exec_Stage.centerOnScreen();
        Exec_Stage.setTitle("Executive Summary");
        Exec_Stage.show();         
    }
    
    @FXML
    private void handleTest(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) ShiftMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();         
    }
    
    @FXML
    private void handleViewButton(ActionEvent event) throws IOException {
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("ViewMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) ShiftMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show(); 
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) throws IOException{        
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ShiftEntryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) ShiftMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show(); 
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 
