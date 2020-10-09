
package testscribe3;

import java.lang.Math; // For window resizing.
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.geometry.Rectangle2D; // For window resizing.
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
/**
 * FXML Controller class
 *
 * @author Rav19, Alvin Thamrin
 */
public class ExecutiveController implements Initializable {
    /**
     * Initializes the controller class.
     */
         
    static private final String FILE_NAME_EXT = Storage.getExt(); // The string that we add in front of a file that is designated as "Advanced SORT"
    static private final String FILE_TEMP_NAME = Storage.getTemp(); // Temporary File that we are actually using
    static private final String FILE_TEMP_NAME_ADV = Storage.getTempAdv(); // The temp file for "Advanced SORT"
    
    static private String fileNamePath;     // Placeholder for the file name/path
    static private String fileNameOnly;     // This one contains the file name only (without path)
    static private int sheetIndex = 2;
    static private int click = 0;
    
    @FXML 
    private MenuBar ExecMenuBar;
      
    @FXML 
    private VBox TestBox;
      
    @FXML 
    private TextArea discrep1;

    @FXML 
    private MenuItem discrepYes, discrepNo, saveItem, saveItemAs;

    @FXML 
    private MenuButton discrepChoose;

    @FXML 
    public VBox pane_main_grid;

    @FXML 
    public VBox pane_main_grid2;
    @FXML 
    private TextField testName, shiftLength, testDates1, testDates2, testLocation, buildName, shipVariant, TCVersion, PCVersion;

    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;

    private int padding = 0;

    private String storage1[] = new String [100];
    private String storage2[] = new String [100];

    private String labels[] = {"Test Name", "Shift Length", "Test Date Start", "Test Date End", "Test Location", 
    "Version Build", "Version Ship Variant", "TC Version", "PCVersion", "Discrepancies"};
      
    
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ExecMenuBar.prefWidthProperty().bind(TestBox.widthProperty());
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
        File ori = new File((FILE_NAME_EXT + fileNameOnly));
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
            FileOutputStream output_file = new FileOutputStream(new File((FILE_NAME_EXT + fileNameOnly)));
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
        FileInputStream file1 = new FileInputStream(new File(FILE_TEMP_NAME_ADV));
        Workbook workbook1 = null;
        try {
            workbook1 = WorkbookFactory.create(file1);
        } catch (InvalidFormatException ex) {
            Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
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
            String tempString = df.formatCellValue(row.getCell(0));
            testName.setText(tempString);
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
            String tempString = df.formatCellValue(row.getCell(0));
            shiftLength.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(2);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            testDates1.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(3);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            testDates2.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(4);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            testLocation.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(5);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            buildName.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(6);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            shipVariant.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(7);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            TCVersion.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(8);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            PCVersion.setText(tempString);
        }
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(9);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            discrep1.setVisible(true);
            String tempString = df.formatCellValue(row.getCell(0));
            discrep1.setText(tempString);
        }
        // Close FileInputStream
        file1.close(); 
        workbook1.close();
    }
    
    private class Info{
        private StringProperty label = new SimpleStringProperty();
        public StringProperty labelProperty(){
            return label;
        }
        
        public final String gettext(){
            return labelProperty().get();
        }
        
        
    }
    
    // What is this used for? Seems to be a deadweight ~ Alvin
    @FXML
    private void saveData(ActionEvent event) throws FileNotFoundException, IOException{
        String string = textfieldLeft.gettext();
        String string2 = textfieldRight.gettext();
        storage1[padding] = string;
        storage2[padding] = string2;
        
        String array[] = new String[10];
        int j = 0;
        
        //--------------------------------------------------------------------------------------
        if(testName.getText() == null || testName.getText().trim().isEmpty()){
            array[0] = "No Data Provided";
        } else {
            array[0] = testName.getText();
        }
        //--------------------------------------------------------------------------------------
        if(shiftLength.getText() == null || shiftLength.getText().trim().isEmpty()){
            array[1] = "No Data Provided";
        } else {
            array[1] = shiftLength.getText();
        }
        //--------------------------------------------------------------------------------------
        if(testDates1.getText() == null || testDates1.getText().trim().isEmpty()){
            array[2] = "No Data Provided";
        } else {
            array[2] = testDates1.getText();
        }
        //--------------------------------------------------------------------------------------
        if(testDates2.getText() == null || testDates2.getText().trim().isEmpty()){
            array[3] = "No Data Provided";
        } else {
            array[3] = testDates2.getText();
        }
        //--------------------------------------------------------------------------------------
        if(testLocation.getText() == null || testLocation.getText().trim().isEmpty()){
            array[4] = "No Data Provided";
        } else {
            array[4] = testLocation.getText();
        }
        //--------------------------------------------------------------------------------------
        if(buildName.getText() == null || buildName.getText().trim().isEmpty()){
            array[5] = "No Data Provided";
        } else {
            array[5] = buildName.getText();
        }
        //--------------------------------------------------------------------------------------
        if(shipVariant.getText() == null || shipVariant.getText().trim().isEmpty()){
            array[6] = "No Data Provided";
        } else {
            array[6] = shipVariant.getText();
        }
        //--------------------------------------------------------------------------------------
        if(TCVersion.getText() == null || TCVersion.getText().trim().isEmpty()){
            array[7] = "No Data Provided";
        } else {
            array[7] = TCVersion.getText();
        }
        //--------------------------------------------------------------------------------------
        if(PCVersion.getText() == null || PCVersion.getText().trim().isEmpty()){
            array[8] = "No Data Provided";
        } else {
            array[8] = PCVersion.getText();
        }
        //--------------------------------------------------------------------------------------
        if(discrep1.getText() == null || discrep1.getText().trim().isEmpty()){
            array[9] = "No Data Provided";
        } else {
            array[9] = discrep1.getText();
        }
        
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Executive Summary");
        
        /*by column
        for(int i = 0; i < labels.length; i++){
            Row row = sheet.createRow(i);
            row.createCell(j).setCellValue(labels[i]);
            row.createCell(j+1).setCellValue(array[i]);
        
        
        for(int i = 0; i < padding; i ++){
            System.out.println(padding);
            System.out.println(storage1[i]);
            System.out.println(storage2[i]);
        }
        }*/
        
        Row row1 = sheet.createRow(j);
        for(int i = 0; i < labels.length; i++){
            row1.createCell(i).setCellValue(labels[i]);
        }
        
        for(int i = 0; i < padding + 1; i++){
            row1.createCell(i + labels.length).setCellValue(storage1[i]);
        }
        //-----------------------------------------------------------------------
        Row row2 = sheet.createRow(j+1);
        for(int i = 0; i < array.length; i++){
            row2.createCell(i).setCellValue(array[i]);
        }
        for(int i = 0; i < padding + 1; i++){
            row2.createCell(i + labels.length).setCellValue(storage2[i]);
        }
        
        for(int i = 0; i < labels.length; i++){
            sheet.autoSizeColumn(i);
        }
       
        
        FileOutputStream file = new FileOutputStream("File.xlsx");
        workbook.write(file);
        file.close();
        
         
    }
    
    List<Info> infos  = new ArrayList<>();
    List<Info> infoz  = new ArrayList<>();
    Info textfieldLeft = new Info();
    Info textfieldRight = new Info();

    
    @FXML 
    public void AddTextField(ActionEvent event){
           
        if(count > 0 && (textfieldRight.gettext() == null || textfieldRight.gettext().trim().isEmpty())){
            //if( (count > 0 && (textfieldRight.gettext() || textfieldRight.gettext().trim().isEmpty()) == null) || (count > 0 && textfieldLeft.gettext() == null) ){
            JOptionPane.showMessageDialog(null, "Please fill out empty textfields");
        } else if(count > 0 && (textfieldLeft.gettext() == null || textfieldLeft.gettext().trim().isEmpty())){
            JOptionPane.showMessageDialog(null, "Please fill out empty textfields");
        } else {
            pane_main_grid.setSpacing(20.0);
            pane_main_grid2.setSpacing(20.0);
            TextField newField = new TextField();
            TextField newField2 = new TextField();

        if(click > 0){
            String string = textfieldLeft.gettext();
            String string2 = textfieldRight.gettext();
            storage1[padding] = string;
            storage2[padding] = string2;
            padding++; 
        }
        click++;

        pane_main_grid.getChildren().add(newField);
        pane_main_grid2.getChildren().add(newField2);

        infos.add(textfieldLeft);
        textfieldLeft.labelProperty().bind(newField2.textProperty());
        infoz.add(textfieldLeft);
        textfieldRight.labelProperty().bind(newField.textProperty());
        // saveExtraSection();
        count++;;
        }
    }
      
      
    // This is the saveFile ActionEvent that was used
    @FXML
    private void saveFile(ActionEvent event) throws IOException, InvalidFormatException{
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
    }
    
    // saveFileAs is associated with the Save As button in the File Menu
    @FXML 
    private void saveFileAs(ActionEvent event) throws FileNotFoundException, IOException, InvalidFormatException{
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
    }
    
    // This function saves whatever is on the screen into the temp file  
    private void savePage() throws IOException, InvalidFormatException{
        FileInputStream file1 = new FileInputStream(new File(FILE_TEMP_NAME_ADV)); 
        Workbook workbook1 = WorkbookFactory.create(file1);
        Sheet sheet1 =  workbook1.getSheetAt(sheetIndex);
        Row row;
        org.apache.poi.ss.usermodel.Cell cell;
        CellStyle style = workbook1.createCellStyle();
        style.setWrapText(true);
        
        // Saving strings found in TextField numAttempted
        row = sheet1.getRow(0);
        if(row == null){ 
            row = sheet1.createRow(0); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(testName.getText());
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(1);
        if(row == null){ 
            row = sheet1.createRow(1); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(shiftLength.getText());
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(2);
        if(row == null){ 
            row = sheet1.createRow(2); // The missing link! 
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(testDates1.getText());
        
                        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(3);
        if(row == null){ 
            row = sheet1.createRow(3); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(testDates2.getText());
        
        
        // Saving strings found in TextField numAttempted
        row = sheet1.getRow(4);
        if(row == null){ 
            row = sheet1.createRow(4); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(testLocation.getText());
        
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(5);
        if(row == null){ 
            row = sheet1.createRow(5); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(buildName.getText());
        
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(6);
        if(row == null){ 
            row = sheet1.createRow(6); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(shipVariant.getText());
        
              // Saving strings found in TextField numAttempted
        row = sheet1.getRow(7);
        if(row == null){ 
            row = sheet1.createRow(7); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(TCVersion.getText());
        
                // Saving strings found in TextField numAttempted
        row = sheet1.getRow(8);
        if(row == null){ 
            row = sheet1.createRow(8); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(PCVersion.getText());
        
           
                // Saving strings found in TextField numAttempted
        row = sheet1.getRow(9);
        if(row == null){ 
            row = sheet1.createRow(9); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(discrep1.getText());
        
        file1.close();
        //Open FileOutputStream to write updates
        FileOutputStream output_file = new FileOutputStream(new File(FILE_TEMP_NAME_ADV));
        //write changes
        workbook1.write(output_file);
        //close the stream
        output_file.close();
        workbook1.close();
    }
    
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
    private void handlediscrepChoiceYes(ActionEvent event) throws IOException{
        
        discrep1.setVisible(true);
        
    }
    
       @FXML
    private void handlediscrepChoiceNo(ActionEvent event) throws IOException{
        
        discrep1.setVisible(false);
        
    }
      
    /************************************ PAGE JUMPING OPTIONS BEGIN ***************************************************/
    /******************************************************************************************************/
    
  
    @FXML
    private void handleCCLog(ActionEvent event) throws IOException{      
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) ExecMenuBar.getScene().getWindow();
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
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) ExecMenuBar.getScene().getWindow();
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
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) ExecMenuBar.getScene().getWindow();
        Exec_Stage.setScene(Exec_Scene);       
        Exec_Stage.centerOnScreen();
        Exec_Stage.setTitle("Executive Summary");
        Exec_Stage.show();         
    }
    
      @FXML
    private void handleShift(ActionEvent event) throws IOException{
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Shift = FXMLLoader.load(getClass().getResource("ShiftEntry.fxml"));
        Scene Shift_Scene = new Scene(Shift);
        Stage Shift_Stage = (Stage) ExecMenuBar.getScene().getWindow();
        Shift_Stage.setScene(Shift_Scene);       
        Shift_Stage.centerOnScreen();
        Shift_Stage.setTitle("Shift Entry");
        Shift_Stage.show();         
    }
    
        @FXML
    private void handleTest(ActionEvent event) throws IOException{        
        File temp = new File(FILE_TEMP_NAME_ADV);
        if(temp.exists() && !temp.isDirectory()){
            try {
                savePage();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) ExecMenuBar.getScene().getWindow();
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
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("ViewMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) ExecMenuBar.getScene().getWindow();
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
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidFormatException ex) {
                Logger.getLogger(ExecutiveController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Parent Test = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) ExecMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();  
    }
      
    /************************************ PAGE JUMPING OPTIONS END ***************************************************/
    /******************************************************************************************************/
    
}


