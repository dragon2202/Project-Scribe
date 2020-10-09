/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.Math; // For window resizing.
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D; // For window resizing.
import javafx.scene.control.ComboBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author Rav19
 */
public class CCLogController implements Initializable {

    /**
     * Initializes the controller class.
     */
       
    static final String FILE_NAME = "ADVANCEDSORT.xlsx";
    
    static private int sheetIndex = 0;
    
    private boolean lock = true;
    
    @FXML 
    private MenuBar CCLogMenuBar;
    
    @FXML 
    private VBox TestBox;
     
    @FXML 
    private Label ifExisting;
     
    @FXML
    private MenuButton resultMenu;
    
    @FXML
    private TextField numAttempted, numSuccessful, prNumber, prTitle, eventDate, recoveryTime, systemTag;
    
    @FXML
    private TextArea commentBox;
    
    @FXML
    private AnchorPane anchorpane;
    
    @FXML
    private VBox eventName;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CCLogMenuBar.prefWidthProperty().bind(TestBox.widthProperty());
        
        
    }
    
    /**
     * Open on screen keyboard.
     * @param event
     * @throws IOException 
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
    private void newEvent(ActionEvent event) throws IOException, Exception{
        Stage newstage = new Stage();
        generateEvent(newstage);
        
        /*
        int eventNum = 1;
        ButtonList.setSpacing(20.0);
        Button Event = new Button("Event "+ eventNum);
        ButtonList.getChildren().add(Event);
        eventNum++;
        */
    }
    
    private void createBlankText() throws IOException{
        String[] textName = {"action.txt", "function.txt", "subfunction.txt", "device.txt", "impact.txt", "tlamtype.txt", "prtype.txt", "neworexisting.txt", "existing.txt"}; 
        
        for(int i = 0; i < textName.length; i++){
            File file = new File(textName[i]);
            if(file.createNewFile()){
                
            } else {
            
            }
        }
    }
    
    
    private void generateEvent(Stage primaryStage)throws Exception{
        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date dateobj = new Date();
        BorderPane pane = new BorderPane();
        MenuBar menubar = new MenuBar();
        menubar.prefWidthProperty().bind(primaryStage.widthProperty());
        Menu fileMenu = new Menu("File");
        MenuItem save = new MenuItem("Save");
        
        fileMenu.getItems().add(save);
        menubar.getMenus().add(fileMenu);
        
        pane.setTop(menubar);
        
        VBox root = new VBox(30);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        
        HBox rootH = new HBox(30);
        rootH.setAlignment(Pos.CENTER);
        
        VBox firstvbox = new VBox(10);//general info
        firstvbox.setMaxWidth(200);
        firstvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox secondvbox = new VBox(10);//TLAM Counts
        secondvbox.setMaxWidth(200);
        secondvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox thirdvbox = new VBox(10); //PR Summary
        thirdvbox.setMaxWidth(200);
        thirdvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox fourthvbox = new VBox(10); //PR Summary
        fourthvbox.setMaxWidth(200);
        fourthvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        
        VBox commentbox = new VBox(20);
        commentbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        commentbox.setAlignment(Pos.CENTER);
        commentbox.setPrefHeight(50);
        VBox.setVgrow(commentbox, Priority.ALWAYS);
        
        //manually create blank txt file in directory for action,function
        //error if file not detected
        createBlankText();
        String[] actionArr = handleFillCombo("action.txt");
        String[] functionArr = handleFillCombo("function.txt");
        String[] subfunctionArr = handleFillCombo("subfunction.txt");
        String[] deviceArr = handleFillCombo("device.txt");
        String[] impactArr = handleFillCombo("impact.txt");
        String[] tlamtypeArr = handleFillCombo("tlamtype.txt");
        String[] prtypeArr = handleFillCombo("prtype.txt");
        String[] neworexistingArr = handleFillCombo("neworexisting.txt");
        String[] existingArr = handleFillCombo("existing.txt");
        
        //general Info
        Label date = new Label("Date and Time");
        Label recovery = new Label("Recovery (in min)");
        Label action = new Label("Action");
        Label system = new Label("System/Subsystem");
        TextField DateAndTime = new TextField();
        DateAndTime.setText(df.format(dateobj));
        TextField Recovery = new TextField();;
        ChoiceBox Action = new ChoiceBox();
        for(int i = 0; i < actionArr.length; i++){
            Action.getItems().add(actionArr[i]);
        }
        TextField System = new TextField();
        //general Info 2
        Label function = new Label("Function");
        Label subfunction = new Label("SubFunction");
        Label device = new Label("Device");
        Label omf = new Label("Impact");
        ChoiceBox Function = new ChoiceBox();
        for(int i = 0; i < functionArr.length; i++){
            Function.getItems().add(functionArr[i]);
        }
        ChoiceBox SubFunction = new ChoiceBox();
        for(int i = 0; i < subfunctionArr.length; i++){
            SubFunction.getItems().add(subfunctionArr[i]);
        }
        ChoiceBox Device = new ChoiceBox();
        for(int i = 0; i < deviceArr.length; i++){
            Device.getItems().add(deviceArr[i]);
        }
        ChoiceBox OMF = new ChoiceBox();
        for(int i = 0; i < impactArr.length; i++){
            OMF.getItems().add(impactArr[i]);
        }
        
        
        //TLAM Counts
        Label type = new Label("Type");
        Label attempted = new Label("Number of Attempted");
        Label successful = new Label("Number of Successful");
        ChoiceBox Type = new ChoiceBox();
        for(int i = 0; i < tlamtypeArr.length; i++){
            Type.getItems().add(tlamtypeArr[i]);
        }
        TextField Attempted = new TextField();
        TextField Successful = new TextField();
        //PR
        Label prtype = new Label("Type");
        Label prnumber = new Label("Number");
        Label condition = new Label("New or Existing");
        Label prtitle = new Label("Title");
        Label existing = new Label("If Existing, Result?");
        ChoiceBox PRType = new ChoiceBox();
        for(int i = 0; i < prtypeArr.length; i++){
            PRType.getItems().add(prtypeArr[i]);
        }
        TextField PRNumber = new TextField();
        ChoiceBox Condition = new ChoiceBox();//new or existing
        for(int i = 0; i < neworexistingArr.length; i++){
            Condition.getItems().add(neworexistingArr[i]);
        }
        TextField PRTitle = new TextField();
        ChoiceBox Existing = new ChoiceBox();//new or existing
        for(int i = 0; i < existingArr.length; i++){
            Existing.getItems().add(existingArr[i]);
        }
        //comments
        Label comment = new Label("Comments");
        TextArea Comment = new TextArea();
        
        Label generalinfo = new Label("General Info");
        Label generalinfo2 = new Label("General Info 2");
        Label TLAM = new Label("TLAM Counts");
        Label PR = new Label("PR");
        
        firstvbox.getChildren().addAll(generalinfo, date, DateAndTime, recovery, Recovery, action, Action, system, System);
        secondvbox.getChildren().addAll(generalinfo2, function, Function, subfunction, SubFunction, device, Device, omf, OMF);
        thirdvbox.getChildren().addAll(TLAM,type, Type, attempted, Attempted, successful, Successful);
        fourthvbox.getChildren().addAll(PR,prtype, PRType, prnumber, PRNumber, prtitle, PRTitle, condition,Condition,existing,Existing);
        
        commentbox.getChildren().addAll(comment, Comment);
        
        rootH.getChildren().addAll(firstvbox, secondvbox, thirdvbox, fourthvbox);
        root.getChildren().addAll(rootH, commentbox);
        pane.setCenter(root);
        
        Scene scene = new Scene(pane, 900, 650);
        primaryStage.setTitle("Event");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        save.setOnAction(actionEvent ->  {
            try {
                handlesaveEvent(DateAndTime.getText(), Recovery.getText(), (String)Action.getValue(), System.getText(), (String)Function.getValue(), (String)SubFunction.getValue(), (String)Device.getValue(), (String)OMF.getValue()
                        , (String)Type.getValue(), Attempted.getText(), Successful.getText(),(String)PRType.getValue(), PRNumber.getText(),(String)Condition.getValue(), (String)Existing.getValue(), PRTitle.getText(),Comment.getText());
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            primaryStage.close();
        });
        
    }
    
    
    
    
    private void handlesaveEvent(String message, String message2, String message3, String message4, String message5, String message6, String message7, String message8, String message9, String message10
    , String message11, String message12, String message13, String message14, String message15, String message16, String message17) throws FileNotFoundException, IOException{
        String date;
        if(message == null || message.trim().isEmpty()){
            date = "No Data Provided";
        } else {
            date = message;
        }
        //----------------------------------------------------------------------
        String recovery;
        if(message2 == null || message2.trim().isEmpty()){
            recovery = "No Data Provided";
        } else {
            recovery = message2;
        }
        //----------------------------------------------------------------------
        String action;
        if(message3 == null || message3.trim().isEmpty()){
            action = "No Data Provided";
        } else {
            action = message3;
        }
        //----------------------------------------------------------------------
        String system;
        if(message4 == null || message4.trim().isEmpty()){
            system = "No Data Provided";
        } else {
            system = message4;
        }
        //----------------------------------------------------------------------
        String function;
        if(message5 == null || message5.trim().isEmpty()){
            function = "No Data Provided";
        } else {
            function = message5;
        }
        //----------------------------------------------------------------------
        String subfunction;
        if(message6 == null || message6.trim().isEmpty()){
            subfunction = "No Data Provided";
        } else {
            subfunction = message6;
        }
        //----------------------------------------------------------------------
        String device;
        if(message7 == null || message7.trim().isEmpty()){
            device = "No Data Provided";
        } else {
            device = message7;
        }
        //----------------------------------------------------------------------
        String omf;
        if(message8 == null || message8.trim().isEmpty()){
            omf = "No Data Provided";
        } else {
            omf = message8;
        }
        //----------------------------------------------------------------------
        String tlamType;
        if(message9 == null || message9.trim().isEmpty()){
            tlamType = "No Data Provided";
        } else {
            tlamType = message9;
        }
        //----------------------------------------------------------------------
        String tlamAttempted;
        if(message10 == null || message10.trim().isEmpty()){
            tlamAttempted = "No Data Provided";
        } else {
            tlamAttempted = message10;
        }
        //----------------------------------------------------------------------
        String tlamSuccessful;
        if(message11 == null || message11.trim().isEmpty()){
            tlamSuccessful = "No Data Provided";
        } else {
            tlamSuccessful = message11;
        }
        //----------------------------------------------------------------------
        String prType;
        if(message12 == null || message12.trim().isEmpty()){
            prType = "No Data Provided";
        } else {
            prType = message12;
        }
        //----------------------------------------------------------------------
        String prNumber;
        if(message13 == null || message13.trim().isEmpty()){
            prNumber = "No Data Provided";
        } else {
            prNumber = message13;
        }
        //----------------------------------------------------------------------
        String condition;
        if(message14 == null || message14.trim().isEmpty()){
            condition = "No Data Provided";
        } else {
            condition = message14;
        }
        //----------------------------------------------------------------------
        String existing;
        if(message15 == null || message15.trim().isEmpty()){
            existing = "No Data Provided";
        } else {
            existing = message15;
        }
        //----------------------------------------------------------------------
        String title;
        if(message16 == null || message16.trim().isEmpty()){
            title = "No Data Provided";
        } else {
            title = message16;
        }
        //----------------------------------------------------------------------
        String comments;
        if(message17 == null || message17.trim().isEmpty()){
            comments = "No Data Provided";
        } else {
            comments = message17;
        }
        
        FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(file);
        XSSFSheet CorrectSheet = null;
        for(int i = 0;i < wb.getNumberOfSheets(); i++){
            Sheet sheet = wb.getSheetAt(i);
            if(sheet.getSheetName().equals("CCLog")){//maybe ignore lowercase and uppercase??
                int sheetPlacement = i;
                CorrectSheet = wb.getSheetAt(i);
            }
        }
        
        //need to implement if sheet isnt there and create a sheet CCLOG
        int count = 0;
        XSSFRow sheetrow = null;
        while(lock){
            sheetrow = CorrectSheet.getRow(count);
             if(sheetrow == null){
                sheetrow = CorrectSheet.createRow(count);
                
                sheetrow.createCell(0).setCellValue(date);
                sheetrow.createCell(1).setCellValue(recovery);
                sheetrow.createCell(2).setCellValue(action);
                sheetrow.createCell(3).setCellValue(system);
                sheetrow.createCell(4).setCellValue(function);
                sheetrow.createCell(5).setCellValue(subfunction);
                sheetrow.createCell(6).setCellValue(device);
                sheetrow.createCell(7).setCellValue(omf);
                sheetrow.createCell(8).setCellValue(tlamType);
                sheetrow.createCell(9).setCellValue(tlamAttempted);
                sheetrow.createCell(10).setCellValue(tlamSuccessful);
                sheetrow.createCell(11).setCellValue(prType);
                sheetrow.createCell(12).setCellValue(prNumber);
                sheetrow.createCell(13).setCellValue(condition);
                sheetrow.createCell(14).setCellValue(existing);
                sheetrow.createCell(15).setCellValue(title);
                sheetrow.createCell(16).setCellValue(comments);
                lock = false;
            }
            count++;
        }
        lock = true;
        
        file.close();
        FileOutputStream OutFile = new FileOutputStream(new File("ADVANCEDSORT.xlsx"));
        wb.write(OutFile);
        OutFile.close();
    }
 
    public String[] handleFillCombo(String FileName) throws FileNotFoundException, IOException{
        BufferedReader input = new BufferedReader(new FileReader(FileName));
        List<String> strings = new ArrayList<String>();
        try{
            String line = null;
            while((line = input.readLine()) != null){
                strings.add(line);
            }
        } catch (FileNotFoundException e){
            System.out.println("File doesn't exist");
        } finally {
            input.close();
        }
        String[] fileContent = strings.toArray(new String[]{});
        return fileContent;
    }
    
    @FXML
    private void editExistingEvent(ActionEvent event) throws FileNotFoundException, IOException{
        
        Stage primaryStage = new Stage();
        FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(file);
        XSSFSheet CorrectSheet = null;
        int first = 1;
        int second = 2;
        String [] List = {"A", "B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q"};
        EncapEvent encap = new EncapEvent();
        Cell cell = null;
        String [] firstevent = new String[List.length];
        String [] secondevent = new String[List.length];
        
        createBlankText();
        String[] actionArr = handleFillCombo("action.txt");
        String[] functionArr = handleFillCombo("function.txt");
        String[] subfunctionArr = handleFillCombo("subfunction.txt");
        String[] deviceArr = handleFillCombo("device.txt");
        String[] impactArr = handleFillCombo("impact.txt");
        String[] tlamtypeArr = handleFillCombo("tlamtype.txt");
        String[] prtypeArr = handleFillCombo("prtype.txt");
        String[] neworexistingArr = handleFillCombo("neworexisting.txt");
        String[] existingArr = handleFillCombo("existing.txt");
        
        for(int i = 0; i < wb.getNumberOfSheets(); i++){
            Sheet sheet = wb.getSheetAt(i);
            if(sheet.getSheetName().equals("CCLog")){
                CorrectSheet = wb.getSheetAt(i);
            }
            /* if the sheet is not there
            if((i + 1) == wb.getNumberOfSheets()){//not tested
                if(sheet.getSheetName().equals("CCLog")){
                    CorrectSheet = wb.getSheetAt(i);
                } else {
                    JOptionPane.showMessageDialog(null, "Sheet doesn't exist");
                }
            
            for(int i = 0; i < List.length;i++){
                CellReference cellRef = new CellReference(List[i]);
                Cell cell = correctrow.getCell(cellRef.getCol());
                String temp = cell.getStringCellValue();
                storage[i] = temp;
                System.out.println(temp);
            }
            
            }*/
        }
        int numofRow = 0;
        while(lock){//gets number of events
            if(CorrectSheet.getRow(numofRow + 1) == null){
                lock = false;
            }
            numofRow++;
        }
        lock = true;
        //System.out.println(numofRow);
        
        encap.setRowNum(numofRow);
        
        if(numofRow == 1){
            int counter = 0;
            XSSFRow sheetrow = CorrectSheet.getRow(numofRow - first);
            for(int i = 0; i < List.length; i++){
                cell = sheetrow.getCell(i);
                firstevent[i] = cell.getStringCellValue();
            }
        } else {
        
            int counter = 0;
            XSSFRow sheetrow = CorrectSheet.getRow(numofRow - first);
            for(int i = 0; i < List.length; i++){
                cell = sheetrow.getCell(i);
                firstevent[i] = cell.getStringCellValue();
            }

            XSSFRow sheetrow2 = CorrectSheet.getRow(numofRow - second);
            Cell cell2 = null;
            for(int i = 0; i < List.length; i++){
                cell2 = sheetrow2.getCell(i);
                secondevent[i] = cell2.getStringCellValue();
            }
        }
        /*
        for(int i = 0; i < List.length; i++){
            System.out.println("1: " + firstevent[i]);
        }
        
        for(int i = 0; i < List.length; i++){
            System.out.println("2: " + secondevent[i]);
        }
        
        while(lock){
            if(CorrectSheet.getRow(counter + 1) == null){
                lock = false;
            }
            //sheetrow = CorrectSheet.getRow(counter);
            CellReference cellRef = new CellReference(""+ List[counter]);
            Cell cell = sheetrow.getCell(cellRef.getCol());
            cellValue = cell.getStringCellValue();
            /*
            if (eventname.equals(response)){
                correctname = response;
                correctrow = CorrectSheet.getRow(counter);
            }
            
            counter += 1;
            if (lock == false){
                
            }
        }
        lock = true;
        */
        
        
        BorderPane pane = new BorderPane();
        MenuBar menubar = new MenuBar();
        menubar.prefWidthProperty().bind(primaryStage.widthProperty());
        
        Menu fileMenu = new Menu("File");
        MenuItem save = new MenuItem("Save");
        
        fileMenu.getItems().add(save);
        menubar.getMenus().add(fileMenu);
        
        
        pane.setTop(menubar);
        
        VBox root = new VBox(30);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        //------------------------------------------------------------------------------------------------------
        HBox rootH = new HBox(5);
        rootH.setAlignment(Pos.CENTER);
        
        VBox firstvbox = new VBox(10);//general info
        firstvbox.setMaxWidth(200);
        firstvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox secondvbox = new VBox(10);//TLAM Counts
        secondvbox.setMaxWidth(200);
        secondvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox thirdvbox = new VBox(10); //PR Summary
        thirdvbox.setMaxWidth(200);
        thirdvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox fourthvbox = new VBox(10); //PR Summary
        fourthvbox.setMaxWidth(200);
        fourthvbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        
        VBox commentbox = new VBox(20);
        commentbox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        commentbox.setAlignment(Pos.CENTER);
        commentbox.setPrefHeight(50);
        VBox.setVgrow(commentbox, Priority.ALWAYS);
        
         //------------------------------------------------------------------------------------------------------------------
        HBox rootH2 = new HBox(5);
        rootH2.setAlignment(Pos.CENTER);
        
        VBox firstvbox2 = new VBox(10);//general info
        firstvbox2.setMaxWidth(200);
        firstvbox2.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox secondvbox2 = new VBox(10);//TLAM Counts
        secondvbox2.setMaxWidth(200);
        secondvbox2.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox thirdvbox2 = new VBox(10); //PR Summary
        thirdvbox2.setMaxWidth(200);
        thirdvbox2.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        VBox fourthvbox2 = new VBox(10); //PR Summary
        fourthvbox2.setMaxWidth(200);
        fourthvbox2.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        
        
        VBox commentbox2 = new VBox(20);
        commentbox2.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;" + "-fx-border-color: pink");
        commentbox2.setAlignment(Pos.CENTER);
        commentbox2.setPrefHeight(50);
        VBox.setVgrow(commentbox2, Priority.ALWAYS);
        //------------------------------------------------------------------------------------------------------------------
        
        //general Info
        Label date = new Label("Date and Time");
        Label recovery = new Label("Recovery (in min)");
        Label action = new Label("Action");
        Label system = new Label("System/Subsystem");
        TextField DateAndTime = new TextField(firstevent[0]);
        TextField Recovery = new TextField(firstevent[1]);
        ChoiceBox Action = new ChoiceBox();
        Action.setValue(firstevent[2]);
        for(int i = 0; i < actionArr.length; i++){
            Action.getItems().add(actionArr[i]);
        }
        TextField System = new TextField(firstevent[3]);
        
        //general Info 2
        Label function = new Label("Function");
        Label subfunction = new Label("SubFunction");
        Label device = new Label("Device");
        Label omf = new Label("Impact");
        ChoiceBox Function = new ChoiceBox();
        Function.setValue(firstevent[4]);
        for(int i = 0; i < functionArr.length; i++){
            Function.getItems().add(functionArr[i]);
        }
        ChoiceBox SubFunction = new ChoiceBox();
        SubFunction.setValue(firstevent[5]);
        for(int i = 0; i < subfunctionArr.length; i++){
            SubFunction.getItems().add(subfunctionArr[i]);
        }
        ChoiceBox Device = new ChoiceBox();
        Device.setValue(firstevent[6]);
        for(int i = 0; i < deviceArr.length; i++){
            Device.getItems().add(deviceArr[i]);
        }
        ChoiceBox OMF = new ChoiceBox();
        OMF.setValue(firstevent[7]);
        for(int i = 0; i < impactArr.length; i++){
            OMF.getItems().add(impactArr[i]);
        }
        
        //TLAM Counts
        Label type = new Label("Type");
        Label attempted = new Label("Number Attempted");
        Label successful = new Label("Number Successful");
        ChoiceBox Type = new ChoiceBox();
        Type.setValue(firstevent[8]);
        for(int i = 0; i < tlamtypeArr.length; i++){
            Type.getItems().add(tlamtypeArr[i]);
        }
        TextField Attempted = new TextField(firstevent[9]);
        TextField Successful = new TextField(firstevent[10]);
        
        //PR
        Label prtype = new Label("PR Type");
        Label prnumber = new Label("PR Number");
        Label condition = new Label("New or Existing");
        Label prtitle = new Label("PR Title");
        Label existing = new Label("If existing, Result?");
        
        ChoiceBox PRType = new ChoiceBox();
        PRType.setValue(firstevent[11]);
        for(int i = 0; i < prtypeArr.length; i++){
            PRType.getItems().add(prtypeArr[i]);
        }
        TextField PRNumber = new TextField(firstevent[12]);
        ChoiceBox Condition = new ChoiceBox();//new or existing
        Condition.setValue(firstevent[13]);
        for(int i = 0; i < neworexistingArr.length; i++){
            Condition.getItems().add(neworexistingArr[i]);
        }
        ChoiceBox Existing = new ChoiceBox();//new or existing
        Existing.setValue(firstevent[14]);
        for(int i = 0; i < existingArr.length; i++){
            Existing.getItems().add(existingArr[i]);
        }
        TextField PRTitle = new TextField(firstevent[15]);
        
        //comments
        Label comment = new Label("Comments");
        TextArea Comment = new TextArea(firstevent[16]);
        
        // second event --------------------------------------------
        
       //general Info2
        Label date2 = new Label("Date and Time");
        Label recovery2 = new Label("Recovery (in min)");
        Label action2 = new Label("Action");
        Label system2 = new Label("System/Subsystem");
        TextField DateAndTime2 = new TextField(secondevent[0]);
        TextField Recovery2 = new TextField(secondevent[1]);
        ChoiceBox Action2 = new ChoiceBox();
        Action2.setValue(secondevent[2]);
        for(int i = 0; i < actionArr.length; i++){
            Action2.getItems().add(actionArr[i]);
        }
        TextField System2 = new TextField(secondevent[3]);
        //general Info 2
        Label function2 = new Label("Function");
        Label subfunction2 = new Label("SubFunction");
        Label device2 = new Label("Device");
        Label omf2 = new Label("Impact");
        ChoiceBox Function2 = new ChoiceBox();
        Function2.setValue(secondevent[4]);
        for(int i = 0; i < functionArr.length; i++){
            Function2.getItems().add(functionArr[i]);
        }
        ChoiceBox SubFunction2 = new ChoiceBox();
        SubFunction2.setValue(secondevent[5]);
        for(int i = 0; i < subfunctionArr.length; i++){
            SubFunction2.getItems().add(subfunctionArr[i]);
        }
        ChoiceBox Device2 = new ChoiceBox();
        Device2.setValue(secondevent[6]);
        for(int i = 0; i < deviceArr.length; i++){
            Device2.getItems().add(deviceArr[i]);
        }
        ChoiceBox OMF2 = new ChoiceBox();
        OMF2.setValue(secondevent[7]);
        for(int i = 0; i < impactArr.length; i++){
            OMF2.getItems().add(impactArr[i]);
        }
        
        
        
        //TLAM Counts2
        Label type2 = new Label("Type");
        Label attempted2 = new Label("Number Attempted");
        Label successful2 = new Label("Number Successful");
        ChoiceBox Type2 = new ChoiceBox();
        Type2.setValue(secondevent[8]);
        for(int i = 0; i < tlamtypeArr.length; i++){
            Type2.getItems().add(tlamtypeArr[i]);
        }
        TextField Attempted2 = new TextField(secondevent[9]);
        TextField Successful2 = new TextField(secondevent[10]);
        
        
        //PR2
        Label prtype2 = new Label("PR Type");
        Label prnumber2 = new Label("PR Number");
        Label condition2 = new Label("New or Existing");
        Label prtitle2 = new Label("PR Title");
        Label existing2 = new Label("If existing, Result?");
        
        
        
        ChoiceBox PRType2 = new ChoiceBox();
        PRType2.setValue(secondevent[11]);
        for(int i = 0; i < prtypeArr.length; i++){
            PRType.getItems().add(prtypeArr[i]);
        }
        TextField PRNumber2 = new TextField(secondevent[12]);
        ChoiceBox Condition2 = new ChoiceBox();//new or existing
        Condition2.setValue(secondevent[13]);
        for(int i = 0; i < neworexistingArr.length; i++){
            Condition2.getItems().add(neworexistingArr[i]);
        }
        ChoiceBox Existing2 = new ChoiceBox();//new or existing
        Existing2.setValue(secondevent[14]);
        for(int i = 0; i < existingArr.length; i++){
            Existing2.getItems().add(existingArr[i]);
        }
        TextField PRTitle2 = new TextField(secondevent[15]);
        
        
        //comments
        Label comment2 = new Label("Comments");
        TextArea Comment2 = new TextArea(secondevent[16]);
        
        Label generalinfo = new Label("General Info");
        Label generalinfo2 = new Label("General Info 2");
        Label TLAM = new Label("TLAM Counts");
        Label PR = new Label("PR");
        
        Label generalinfo3 = new Label("General Info");
        Label generalinfo4 = new Label("General Info 2");
        Label TLAM2 = new Label("TLAM Counts");
        Label PR2 = new Label("PR");
        
        VBox buttonRoot = new VBox(10);
        buttonRoot.setAlignment(Pos.CENTER);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        HBox buttonBox2 = new HBox(10);
        buttonBox2.setAlignment(Pos.CENTER);
        
        HBox buttonBox3 = new HBox(10);
        buttonBox3.setAlignment(Pos.CENTER);
        
        Button Prev = new Button("Prev");
        Label index1 = new Label("Scroll by 1");
        Button Next = new Button("Next");
        
        Button Prev5 = new Button("Prev");
        Label index2 = new Label("Scroll by 5");
        Button Next5 = new Button("Next");
        
        Button Prev10 = new Button("Prev");
        Label index3 = new Label("Scroll by 10");
        Button Next10 = new Button("Next");
        
        firstvbox.getChildren().addAll(generalinfo, date, DateAndTime, recovery, Recovery, action, Action, system, System);
        secondvbox.getChildren().addAll(generalinfo2, function, Function, subfunction, SubFunction, device, Device, omf, OMF);
        thirdvbox.getChildren().addAll(TLAM,type, Type, attempted, Attempted, successful, Successful);
        fourthvbox.getChildren().addAll(PR,prtype, PRType, prnumber, PRNumber, prtitle, PRTitle, condition,Condition, existing, Existing);
        commentbox.getChildren().addAll(comment, Comment);
        
        firstvbox2.getChildren().addAll(generalinfo3,date2, DateAndTime2, recovery2, Recovery2, action2, Action2, system2, System2);
        secondvbox2.getChildren().addAll(generalinfo4, function2, Function2, subfunction2, SubFunction2, device2, Device2, omf2, OMF2);
        thirdvbox2.getChildren().addAll(TLAM2,type2, Type2, attempted2, Attempted2, successful2, Successful2);
        fourthvbox2.getChildren().addAll(PR2,prtype2, PRType2, prnumber2, PRNumber2, prtitle2, PRTitle2, condition2, Condition2, existing2, Existing2);
        commentbox2.getChildren().addAll(comment2, Comment2);
        
        buttonBox.getChildren().addAll(Prev, index1, Next);
        buttonBox2.getChildren().addAll(Prev5, index2, Next5);
        buttonBox3.getChildren().addAll(Prev10, index3, Next10);
        
        
        rootH.getChildren().addAll(firstvbox, secondvbox, thirdvbox, fourthvbox, commentbox);
        rootH2.getChildren().addAll(firstvbox2, secondvbox2, thirdvbox2, fourthvbox2, commentbox2);
        buttonRoot.getChildren().addAll(buttonBox, buttonBox2, buttonBox3);
        
        root.getChildren().addAll(rootH,rootH2,buttonRoot);
        pane.setCenter(root);
        
        Scene scene = new Scene(pane, 1300, 850);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        encap.setFirstEvent(firstevent);
        encap.setSecondEvent(secondevent);
        if(numofRow > 2){
        Next.setOnAction(actionEvent ->  {
            String[] temp = new String[List.length];
            String[] temp2 = new String[List.length];
            encap.setFirstEvent(firstevent);
            encap.setSecondEvent(secondevent);
            try {
                temp = encap.goBackfirst();
                temp2 = encap.goBacksecond();
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            DateAndTime.setText(temp[0]);
            Recovery.setText(temp[1]);
            Action.setValue(temp[12]);
            System.setText(temp[3]);
            Function.setValue(temp[4]);
            SubFunction.setValue(temp[5]);
            Device.setValue(temp[6]);
            OMF.setValue(temp[7]);
            Type.setValue(temp[8]);
            Attempted.setText(temp[9]);
            Successful.setText(temp[10]);
            PRType.setValue(temp[11]);
            PRNumber.setText(temp[12]);
            Condition.setValue(temp[13]);
            Existing.setValue(temp[14]);
            PRTitle.setText(temp[15]);
            Comment.setText(temp[16]);
            
            DateAndTime2.setText(temp2[0]);
            Recovery2.setText(temp2[1]);
            Action2.setValue(temp2[12]);
            System2.setText(temp2[3]);
            Function2.setValue(temp2[4]);
            SubFunction2.setValue(temp2[5]);
            Device2.setValue(temp2[6]);
            OMF2.setValue(temp2[7]);
            Type2.setValue(temp2[8]);
            Attempted2.setText(temp2[9]);
            Successful2.setText(temp2[10]);
            PRType2.setValue(temp2[11]);
            PRNumber2.setText(temp2[12]);
            Condition2.setValue(temp2[13]);
            Existing2.setValue(temp2[14]);
            PRTitle2.setText(temp2[15]);
            Comment2.setText(temp2[16]);
            
        });
        
        Next5.setOnAction(actionEvent ->  {
            String[] temp = new String[List.length];
            String[] temp2 = new String[List.length];
            encap.setFirstEvent(firstevent);
            encap.setSecondEvent(secondevent);
            try {
                for(int i = 0; i < 5; i++){
                    temp = encap.goBackfirst();
                    temp2 = encap.goBacksecond();
                }
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            DateAndTime.setText(temp[0]);
            Recovery.setText(temp[1]);
            Action.setValue(temp[12]);
            System.setText(temp[3]);
            Function.setValue(temp[4]);
            SubFunction.setValue(temp[5]);
            Device.setValue(temp[6]);
            OMF.setValue(temp[7]);
            Type.setValue(temp[8]);
            Attempted.setText(temp[9]);
            Successful.setText(temp[10]);
            PRType.setValue(temp[11]);
            PRNumber.setText(temp[12]);
            Condition.setValue(temp[13]);
            Existing.setValue(temp[14]);
            PRTitle.setText(temp[15]);
            Comment.setText(temp[16]);
            
            DateAndTime2.setText(temp2[0]);
            Recovery2.setText(temp2[1]);
            Action2.setValue(temp2[12]);
            System2.setText(temp2[3]);
            Function2.setValue(temp2[4]);
            SubFunction2.setValue(temp2[5]);
            Device2.setValue(temp2[6]);
            OMF2.setValue(temp2[7]);
            Type2.setValue(temp2[8]);
            Attempted2.setText(temp2[9]);
            Successful2.setText(temp2[10]);
            PRType2.setValue(temp2[11]);
            PRNumber2.setText(temp2[12]);
            Condition2.setValue(temp2[13]);
            Existing2.setValue(temp2[14]);
            PRTitle2.setText(temp2[15]);
            Comment2.setText(temp2[16]);
            
        });
        
        Next10.setOnAction(actionEvent ->  {
            String[] temp = new String[List.length];
            String[] temp2 = new String[List.length];
            encap.setFirstEvent(firstevent);
            encap.setSecondEvent(secondevent);
            try {
                for(int i = 0; i < 10; i++){
                    temp = encap.goBackfirst();
                    temp2 = encap.goBacksecond();
                }
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            DateAndTime.setText(temp[0]);
            Recovery.setText(temp[1]);
            Action.setValue(temp[12]);
            System.setText(temp[3]);
            Function.setValue(temp[4]);
            SubFunction.setValue(temp[5]);
            Device.setValue(temp[6]);
            OMF.setValue(temp[7]);
            Type.setValue(temp[8]);
            Attempted.setText(temp[9]);
            Successful.setText(temp[10]);
            PRType.setValue(temp[11]);
            PRNumber.setText(temp[12]);
            Condition.setValue(temp[13]);
            Existing.setValue(temp[14]);
            PRTitle.setText(temp[15]);
            Comment.setText(temp[16]);
            
            DateAndTime2.setText(temp2[0]);
            Recovery2.setText(temp2[1]);
            Action2.setValue(temp2[12]);
            System2.setText(temp2[3]);
            Function2.setValue(temp2[4]);
            SubFunction2.setValue(temp2[5]);
            Device2.setValue(temp2[6]);
            OMF2.setValue(temp2[7]);
            Type2.setValue(temp2[8]);
            Attempted2.setText(temp2[9]);
            Successful2.setText(temp2[10]);
            PRType2.setValue(temp2[11]);
            PRNumber2.setText(temp2[12]);
            Condition2.setValue(temp2[13]);
            Existing2.setValue(temp2[14]);
            PRTitle2.setText(temp2[15]);
            Comment2.setText(temp2[16]);
            
        });
        
        Prev.setOnAction(actionEvent ->  {
            String[] temp = new String[List.length];
            String[] temp2 = new String[List.length];
            encap.setFirstEvent(firstevent);
            encap.setSecondEvent(secondevent);
            try {
                temp = encap.goForwardfirst();
                temp2 = encap.goForwardsecond();
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            DateAndTime.setText(temp[0]);
            Recovery.setText(temp[1]);
            Action.setValue(temp[2]);
            System.setText(temp[3]);
            Function.setValue(temp[4]);
            SubFunction.setValue(temp[5]);
            Device.setValue(temp[6]);
            OMF.setValue(temp[7]);
            Type.setValue(temp[8]);
            Attempted.setText(temp[9]);
            Successful.setText(temp[10]);
            PRType.setValue(temp[11]);
            PRNumber.setText(temp[12]);
            Condition.setValue(temp[13]);
            Existing.setValue(temp[14]);
            PRTitle.setText(temp[15]);
            Comment.setText(temp[16]);
            
            DateAndTime2.setText(temp2[0]);
            Recovery2.setText(temp2[1]);
            //Action2.valueProperty().set(null);
            Action2.setValue(temp2[2]);
            System2.setText(temp2[3]);
            Function2.setValue(temp2[4]);
            SubFunction2.setValue(temp2[5]);
            Device2.setValue(temp2[6]);
            OMF2.setValue(temp2[7]);
            Type2.setValue(temp2[8]);
            Attempted2.setText(temp2[9]);
            Successful2.setText(temp2[10]);
            PRType2.setValue(temp2[11]);
            PRNumber2.setText(temp2[12]);
            Condition2.setValue(temp2[13]);
            Existing2.setValue(temp2[14]);
            PRTitle2.setText(temp2[15]);
            Comment2.setText(temp2[16]);
        });
        
        Prev5.setOnAction(actionEvent ->  {
            String[] temp = new String[List.length];
            String[] temp2 = new String[List.length];
            encap.setFirstEvent(firstevent);
            encap.setSecondEvent(secondevent);
            try {
                for(int i = 0; i < 5; i++){
                    temp = encap.goForwardfirst();
                    temp2 = encap.goForwardsecond();
                }
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            DateAndTime.setText(temp[0]);
            Recovery.setText(temp[1]);
            Action.setValue(temp[2]);
            System.setText(temp[3]);
            Function.setValue(temp[4]);
            SubFunction.setValue(temp[5]);
            Device.setValue(temp[6]);
            OMF.setValue(temp[7]);
            Type.setValue(temp[8]);
            Attempted.setText(temp[9]);
            Successful.setText(temp[10]);
            PRType.setValue(temp[11]);
            PRNumber.setText(temp[12]);
            Condition.setValue(temp[13]);
            Existing.setValue(temp[14]);
            PRTitle.setText(temp[15]);
            Comment.setText(temp[16]);
            
            DateAndTime2.setText(temp2[0]);
            Recovery2.setText(temp2[1]);
            //Action2.valueProperty().set(null);
            Action2.setValue(temp2[2]);
            System2.setText(temp2[3]);
            Function2.setValue(temp2[4]);
            SubFunction2.setValue(temp2[5]);
            Device2.setValue(temp2[6]);
            OMF2.setValue(temp2[7]);
            Type2.setValue(temp2[8]);
            Attempted2.setText(temp2[9]);
            Successful2.setText(temp2[10]);
            PRType2.setValue(temp2[11]);
            PRNumber2.setText(temp2[12]);
            Condition2.setValue(temp2[13]);
            Existing2.setValue(temp2[14]);
            PRTitle2.setText(temp2[15]);
            Comment2.setText(temp2[16]);
        });
        
        Prev10.setOnAction(actionEvent ->  {
            String[] temp = new String[List.length];
            String[] temp2 = new String[List.length];
            encap.setFirstEvent(firstevent);
            encap.setSecondEvent(secondevent);
            try {
                for(int i = 0; i < 10; i++){
                    temp = encap.goForwardfirst();
                    temp2 = encap.goForwardsecond();
                }
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
            DateAndTime.setText(temp[0]);
            Recovery.setText(temp[1]);
            Action.setValue(temp[2]);
            System.setText(temp[3]);
            Function.setValue(temp[4]);
            SubFunction.setValue(temp[5]);
            Device.setValue(temp[6]);
            OMF.setValue(temp[7]);
            Type.setValue(temp[8]);
            Attempted.setText(temp[9]);
            Successful.setText(temp[10]);
            PRType.setValue(temp[11]);
            PRNumber.setText(temp[12]);
            Condition.setValue(temp[13]);
            Existing.setValue(temp[14]);
            PRTitle.setText(temp[15]);
            Comment.setText(temp[16]);
            
            DateAndTime2.setText(temp2[0]);
            Recovery2.setText(temp2[1]);
            //Action2.valueProperty().set(null);
            Action2.setValue(temp2[2]);
            System2.setText(temp2[3]);
            Function2.setValue(temp2[4]);
            SubFunction2.setValue(temp2[5]);
            Device2.setValue(temp2[6]);
            OMF2.setValue(temp2[7]);
            Type2.setValue(temp2[8]);
            Attempted2.setText(temp2[9]);
            Successful2.setText(temp2[10]);
            PRType2.setValue(temp2[11]);
            PRNumber2.setText(temp2[12]);
            Condition2.setValue(temp2[13]);
            Existing2.setValue(temp2[14]);
            PRTitle2.setText(temp2[15]);
            Comment2.setText(temp2[16]);
        });
        }
        save.setOnAction(actionEvent ->  {
            try {
                encap.savefirst(DateAndTime.getText(), Recovery.getText(), (String)Action.getValue(), System.getText(), (String)Function.getValue(), (String)SubFunction.getValue(), (String)Device.getValue(), (String)OMF.getValue()
                        , (String)Type.getValue(), Attempted.getText(), Successful.getText(),(String)PRType.getValue(), PRNumber.getText(),(String)Condition.getValue(), (String)Existing.getValue(), PRTitle.getText(),Comment.getText());
                encap.savesecond(DateAndTime2.getText(), Recovery2.getText(), (String)Action2.getValue(), System2.getText(), (String)Function2.getValue(), (String)SubFunction2.getValue(), (String)Device2.getValue(), (String)OMF2.getValue()
                        , (String)Type2.getValue(), Attempted2.getText(), Successful2.getText(),(String)PRType2.getValue(), PRNumber2.getText(),(String)Condition2.getValue(), (String)Existing2.getValue(), PRTitle2.getText(),Comment2.getText());
            } catch (IOException ex) {
                Logger.getLogger(CCLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }
    
    public class EncapEvent{
        private String[] event1;
        private String[] event2;
        private String[] temp1;
        private String[] temp2;
        Boolean even = null;
        Boolean odd = null;
        int iterator = 0;
        int iterator2 = 0;
        Boolean lock = true;     
        int RowNum = 0;
        
        public void setRowNum (int row){
            RowNum = row;
            if(RowNum % 2 == 0){
                even = true;
                odd = false;
            } else{
                even = false;
                odd = true;
            }
        }
        
        public void setFirstEvent(String[] array){
            event1 = Arrays.copyOf(array, array.length);
        }
        
        public void setSecondEvent(String[] array){
            event2 = Arrays.copyOf(array, array.length);
        }
        
        public void savefirst(String message, String message2, String message3, String message4, String message5, String message6, String message7, String message8, String message9, String message10
    , String message11, String message12, String message13, String message14, String message15, String message16, String message17) throws FileNotFoundException, IOException {
            FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet CorrectSheet = null;
            Cell cell = null;
            int first = 1;
            
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                if(sheet.getSheetName().equals("CCLog")){
                    CorrectSheet = wb.getSheetAt(i);
                }
            }
            
            event1[0] = message;
            event1[1] = message2;
            event1[2] = message3;
            event1[3] = message4;
            event1[4] = message5;
            event1[5] = message6;
            event1[6] = message7;
            event1[7] = message8;
            event1[8] = message9;
            event1[9] = message10;
            event1[10] = message11;
            event1[11] = message12;
            event1[12] = message12;
            event1[13] = message13;
            event1[14] = message15;
            event1[15] = message16;
            event1[16] = message17;

            int tempRow = RowNum;
            
            XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - iterator);
            for(int i = 0; i < event1.length; i++){
                cell = sheetrow.getCell(i);
                cell.setCellValue(event1[i]);
            }
            
            file.close();
            FileOutputStream OutFile = new FileOutputStream(new File("ADVANCEDSORT.xlsx"));
            wb.write(OutFile);
            OutFile.close();
        }
        
        public void savesecond(String message, String message2, String message3, String message4, String message5, String message6, String message7, String message8, String message9, String message10
    , String message11, String message12, String message13, String message14, String message15, String message16, String message17) throws FileNotFoundException, IOException {
            FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet CorrectSheet = null;
            Cell cell = null;
            int first = 2;
            
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                if(sheet.getSheetName().equals("CCLog")){
                    CorrectSheet = wb.getSheetAt(i);
                }
            }
            
            event2[0] = message;
            event2[1] = message2;
            event2[2] = message3;
            event2[3] = message4;
            event2[4] = message5;
            event2[5] = message6;
            event2[6] = message7;
            event2[7] = message8;
            event2[8] = message9;
            event2[9] = message10;
            event2[10] = message11;
            event2[11] = message12;
            event2[12] = message12;
            event2[13] = message13;
            event2[14] = message15;
            event2[15] = message16;
            event2[16] = message17;
            
            int tempRow = RowNum;
            
            
            XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - iterator2);
            for(int i = 0; i < event2.length; i++){
                cell = sheetrow.getCell(i);
                cell.setCellValue(event2[i]);
            }
            
            file.close();
            FileOutputStream OutFile = new FileOutputStream(new File("ADVANCEDSORT.xlsx"));
            wb.write(OutFile);
            OutFile.close();
        }
        
        public String[] goBackfirst() throws IOException{
            FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet CorrectSheet = null;
            int first = 1;
            
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                if(sheet.getSheetName().equals("CCLog")){
                    CorrectSheet = wb.getSheetAt(i);
                }
            }
            int tempRow = RowNum;
            //System.out.println("1: "+ (tempRow - first - (iterator + 2)));
            
            if((tempRow - first - (iterator + 2)) == -1 && even){
                return temp1;
            }
            
            if((tempRow - first - (iterator2 + 2)) < 0 && odd){
                return temp1;
            }
            
            if((tempRow - first - (iterator + 2)) == 0 && odd){
                String[] array = new String[17];
                XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - (iterator + 2));
                    Cell cell = null;
                    for(int i = 0; i < event1.length; i++){
                        cell = sheetrow.getCell(i);
                        array[i] = cell.getStringCellValue();
                    }
                temp1 = Arrays.copyOf(array, array.length);
                iterator += 2;
                return array;
            }
            
            if(((tempRow - first - (iterator + 2)) == 1)){
               // iterator = 0;
               if((tempRow - first - (iterator + 2)) >= 1){
                    XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - (iterator + 2));
                    Cell cell = null;
                    for(int i = 0; i < event1.length; i++){
                        cell = sheetrow.getCell(i);
                        event1[i] = cell.getStringCellValue();
                    }
                }
               iterator += 2;
               temp1 = Arrays.copyOf(event1, event1.length);
               return event1;
            }
            else{
                if((tempRow - first - (iterator + 2)) >= 1){
                    iterator += 2;
                    XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - iterator);

                    Cell cell = null;
                    for(int i = 0; i < event1.length; i++){
                        cell = sheetrow.getCell(i);
                        event1[i] = cell.getStringCellValue();
                    }
                }
            }
            return event1;
        }
        
        
        
        
        public String[] goBacksecond() throws IOException{
            FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet CorrectSheet = null;
            int first = 2;
            
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                if(sheet.getSheetName().equals("CCLog")){
                    CorrectSheet = wb.getSheetAt(i);
                }
            }
            
            int tempRow = RowNum;
            //System.out.println("2: " + (tempRow - first - (iterator2 + 2)));
            
            
            if((tempRow - first - (iterator2 + 2)) == -2 && even){
                return temp2;
            }
            if((tempRow - first - (iterator2 + 2)) < -1 && odd){
                return temp2;
            }
            
            if((tempRow - first - (iterator2 + 2)) == -1 && odd){
                String[] array = new String[17];
                for(int i = 0; i < array.length; i++){
                    array[i] = "";
                }
                temp2 = Arrays.copyOf(array, array.length);
                iterator2 += 2;
                return array;
            }
            
            if(((tempRow - first - (iterator2 + 2)) == 0)){
                
                if((tempRow - first - (iterator2 + 2)) >= 0){
                    XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - (iterator2 + 2));
                    Cell cell = null;
                    for(int i = 0; i < event1.length; i++){
                        cell = sheetrow.getCell(i);
                        event2[i] = cell.getStringCellValue();
                    }
                }
                iterator2 += 2;
                //iterator2 = 0;
                temp2 = Arrays.copyOf(event2, event2.length);
                return event2;
            } else{
                 if((tempRow - first - (iterator2 + 2)) >= 0){
                    iterator2 += 2;
                    XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - iterator2);


                    Cell cell = null;
                    for(int i = 0; i < event1.length; i++){
                        cell = sheetrow.getCell(i);
                        event2[i] = cell.getStringCellValue();
                    }
                }
            }
            return event2;
        }
        
        public String[] goForwardfirst() throws IOException{
            FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet CorrectSheet = null;
            int first = 1;
            
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                if(sheet.getSheetName().equals("CCLog")){
                    CorrectSheet = wb.getSheetAt(i);
                }
            }
            
            int tempRow = RowNum;
            
            //System.out.println("1F: "+(tempRow - first - (iterator - 2)));
            if( !((tempRow - first - (iterator - 2)) < tempRow)){
                //iterator = tempRow;
                return event1;
            }
            
            if((tempRow - first - (iterator - 2)) < tempRow){
                iterator -= 2;
                XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - iterator);
                Cell cell = null;
                for(int i = 0; i < event1.length; i++){
                    cell = sheetrow.getCell(i);
                    event1[i] = cell.getStringCellValue();
                }
            }
            
            return event1;
        }
        
        public String[] goForwardsecond() throws IOException{
            FileInputStream file = new FileInputStream("ADVANCEDSORT.xlsx");
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet CorrectSheet = null;
            int first = 2;
            
            for(int i = 0; i < wb.getNumberOfSheets(); i++){
                Sheet sheet = wb.getSheetAt(i);
                if(sheet.getSheetName().equals("CCLog")){
                    CorrectSheet = wb.getSheetAt(i);
                }
            }
            
            int tempRow = RowNum;
            
            
            if( !((tempRow - first - (iterator2 - 2)) < tempRow)){
                return event2;
                //iterator2 = tempRow;
            }
            //System.out.println("12: "+(tempRow - first - (iterator2 - 2)));
            if((tempRow - first - (iterator2 - 2)) < tempRow){
                iterator2 -= 2;
                XSSFRow sheetrow = CorrectSheet.getRow(tempRow - first - iterator2);
                
                Cell cell = null;
                for(int i = 0; i < event2.length; i++){
                    cell = sheetrow.getCell(i);
                    event2[i] = cell.getStringCellValue();
                }
            }
            return event2;
        }
        
        
        public String[] getFirstEvent(){
            return event1;
        }
        
        public String[] getSecondEvent(){
            return event2;
        }
        
        public void printArray(String[] array){
            System.out.println(Arrays.toString(array));
        }
    }
    
    
    @FXML
    private void handleresultExisting(ActionEvent event) throws IOException{
        
        ifExisting.setVisible(true);
        resultMenu.setVisible(true);
        
    }
    
    @FXML
    private void handleresultNew(ActionEvent event) throws IOException{
        
        ifExisting.setVisible(false);
        resultMenu.setVisible(false);
        
    }
    
    // Currently I'm not using the temp solution, will need to discuss some layout changes for this
    // Warning, this is by no mean finished yet, however I have demonstrated that there is no problem writing from and reading to a specific file
    @FXML
    private void saveFile(ActionEvent event) throws IOException, InvalidFormatException{
        FileInputStream file1 = new FileInputStream(new File(FILE_NAME)); 
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
        cell.setCellValue(numAttempted.getText());
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(1);
        if(row == null){ 
            row = sheet1.createRow(1); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(numSuccessful.getText());
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(2);
        if(row == null){ 
            row = sheet1.createRow(2); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(prNumber.getText());
        
        // Saving strings found in TextField numAttempted
        row = sheet1.getRow(3);
        if(row == null){ 
            row = sheet1.createRow(3); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(prTitle.getText());
        
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(4);
        if(row == null){ 
            row = sheet1.createRow(4); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(commentBox.getText());
        
        
         // Saving strings found in TextField numAttempted
        row = sheet1.getRow(5);
        if(row == null){ 
            row = sheet1.createRow(5); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(eventDate.getText());
        
              // Saving strings found in TextField numAttempted
        row = sheet1.getRow(6);
        if(row == null){ 
            row = sheet1.createRow(6); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(recoveryTime.getText());
        
                // Saving strings found in TextField numAttempted
        row = sheet1.getRow(7);
        if(row == null){ 
            row = sheet1.createRow(7); // The missing link!
        }
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(systemTag.getText());
        
        file1.close();
        //Open FileOutputStream to write updates
        FileOutputStream output_file = new FileOutputStream(new File(FILE_NAME));
        //write changes
        workbook1.write(output_file);
        //close the stream
        output_file.close();
    }
    
    
    @FXML
    private void loadFile(ActionEvent event) throws IOException, InvalidFormatException{
        DataFormatter df = new DataFormatter();
        FileInputStream file1 = new FileInputStream(new File(FILE_NAME)); 
        Workbook workbook1 = WorkbookFactory.create(file1);
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
            numAttempted.setText(tempString);
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
            numSuccessful.setText(tempString);
        }
        
        
         
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(2);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            prNumber.setText(tempString);
        }
        
        
         
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(3);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            prTitle.setText(tempString);
        }
        
        
         
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(4);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            commentBox.setText(tempString);
        }
        
        
         
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(5);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            eventDate.setText(tempString);
        }
        
        
        
         
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(6);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            recoveryTime.setText(tempString);
        }
        
        
        // Reading strings found in TextField numAttempted
        row = sheet1.getRow(7);
        if(row == null){
            return;
        }
        // Check if cell is empty or not
        if(!isCellEmpty(row.getCell(0))){
            String tempString = df.formatCellValue(row.getCell(0));
            systemTag.setText(tempString);
        }
        
        
        
        file1.close();
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
    private void handleSystemTime(ActionEvent event) throws IOException{
       DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
       Date dateobj = new Date();
       eventDate.setText(df.format(dateobj));       
    }
    
    /************************************ PAGE JUMPING OPTIONS BEGIN ***************************************************/
    /******************************************************************************************************/
    
    
    @FXML
    private void handleCCLog(ActionEvent event) throws IOException{
        
        Parent CCLog = FXMLLoader.load(getClass().getResource("CCLog.fxml"));
        Scene CCLogScene = new Scene(CCLog);
        Stage CCLog_Stage = (Stage) CCLogMenuBar.getScene().getWindow();
        CCLog_Stage.setScene(CCLogScene);       
        CCLog_Stage.centerOnScreen();
        CCLog_Stage.setTitle("CCLog");
        CCLog_Stage.show();         
    }
    
    
    @FXML
    private void handleCounts(ActionEvent event) throws IOException{
        
        Parent Counts = FXMLLoader.load(getClass().getResource("Counts.fxml"));
        Scene Counts_Scene = new Scene(Counts);
        Stage Counts_Stage = (Stage) CCLogMenuBar.getScene().getWindow();
        Counts_Stage.setScene(Counts_Scene);       
        Counts_Stage.centerOnScreen();
        Counts_Stage.setTitle("Counts");
        Counts_Stage.show();         
    }
    
     @FXML
    private void handleExec(ActionEvent event) throws IOException{
        
        Parent Exec_Sum = FXMLLoader.load(getClass().getResource("Executive.fxml"));
        Scene Exec_Scene = new Scene(Exec_Sum);
        Stage Exec_Stage = (Stage) CCLogMenuBar.getScene().getWindow();
        Exec_Stage.setScene(Exec_Scene);       
        Exec_Stage.centerOnScreen();
        Exec_Stage.setTitle("Executive Summary");
        Exec_Stage.show();         
    }
    
      @FXML
    private void handleShift(ActionEvent event) throws IOException{
        
        Parent Shift = FXMLLoader.load(getClass().getResource("ShiftEntry.fxml"));
        Scene Shift_Scene = new Scene(Shift);
        Stage Shift_Stage = (Stage) CCLogMenuBar.getScene().getWindow();
        Shift_Stage.setScene(Shift_Scene);       
        Shift_Stage.centerOnScreen();
        Shift_Stage.setTitle("Shift Entry");
        Shift_Stage.show();         
    }
    
      @FXML
    private void handleTest(ActionEvent event) throws IOException{
        
        Parent Test = FXMLLoader.load(getClass().getResource("TestMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) CCLogMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();         
    }
    
    
    @FXML
    private void handleEditButton(ActionEvent event) throws IOException{        
        Parent Test = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) CCLogMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();   
    }
    
    @FXML
    private void handleViewButton(ActionEvent event) throws IOException {
        Parent Test = FXMLLoader.load(getClass().getResource("ViewMode.fxml"));
        Scene Test_Scene = new Scene(Test);
        Stage Test_Stage = (Stage) CCLogMenuBar.getScene().getWindow();
        Test_Stage.setScene(Test_Scene);       
        Test_Stage.centerOnScreen();
        Test_Stage.setTitle("Project Scribe");
        Test_Stage.show();   
    }
    
    /************************************ PAGE JUMPING OPTIONS END ***************************************************/
    /******************************************************************************************************/
    
}



