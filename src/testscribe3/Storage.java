/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testscribe3;

/**
 *
 * @author Alvin
 * 
 * Ultimately, I want this class to hold the file name that every other java class will be using
 * This is done so that further changes to the naming convention can be done in an easier setting
 */
public class Storage {
    static private final String FILE_NAME_EXT = "ADV_";                 // The temp name extension for "Advanced SORT" file
    static private final String FILE_TEMP_NAME = "temp.xlsx";           // The temp name for SORT file
    static private final String FILE_TEMP_NAME_ADV = "tempADV.xlsx";    // The temp name for "Advanced SORT" file
    
    static private String fileNameOnly;   // FILE_NAME_ONLY contains only the file name (without its path). This variable is associated with "Advanced SORT" file
    static private String fileNamePath;   // FILE_NAME_PATH contains the file name with the path. This variable is associated with SORT file
    
    
    static public void setNameTwice(String namePath, String nameOnly){
        fileNamePath = namePath;
        fileNameOnly = nameOnly;
    }
    
    static public String getFileNameOnly(){
        return fileNameOnly;
    }
    
    static public String getFileNamePath(){
        return fileNamePath;
    }
    
    static public String getTemp(){
        return FILE_TEMP_NAME;
    }
    static public String getTempAdv(){
        return FILE_TEMP_NAME_ADV;
    }
    static public String getExt(){
        return FILE_NAME_EXT;
    }
}
