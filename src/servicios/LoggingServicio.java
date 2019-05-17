package servicios;

/**
 *
 * @author briggette.olenka.ro1
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class LoggingServicio {
    private final Preferences prefs;
    private String FILENAME = "";
    private String FILEPATH = "";
    
    public LoggingServicio(String fileName, String filePath){
        this.prefs = Preferences.userRoot().node(this.getClass().getName());
        this.FILENAME = fileName;
        this.FILEPATH = filePath;
    }
    
    public void setFileName(String fileName){
        this.FILENAME = fileName;
    }
    
    public String getFileName(){
        return this.FILENAME;
    }
    
    public void setFilePath(String filePath){
        this.FILEPATH = filePath;
    }
    
    public String getFilePath(){
        return this.FILEPATH;
    }
}
