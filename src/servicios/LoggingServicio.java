package servicios;

import controlador.Navegador;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;

public class LoggingServicio {
    final public Logger LOG_RAIZ = Logger.getLogger("");
    final static Logger LOG_CONTROLADOR = Logger.getLogger("controlador");
    final static Logger LOG_INICIO = Logger.getLogger("controlador.inicio");
    final static Logger LOG_APROVISIONAMIENTO = Logger.getLogger("controlador.aprovisionamiento");
    final static Logger LOG_PARAMETRIZACION = Logger.getLogger("controlador.parametrizacion");
    final static Logger LOG_PROCESOS = Logger.getLogger("controlador.procesos");
    final static Logger LOG_REPORTING = Logger.getLogger("controlador.reporting");
    
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
    
    public void crearLog() throws IOException{
        Format formatterLogFile = new SimpleDateFormat("yyyyMMdd");
        String fechaStr = formatterLogFile.format(new Date());
        try{
            String carpetaLogDay = FILEPATH+String.format("./%s_Log/",fechaStr);
            Navegador.crearCarpeta(carpetaLogDay);
            String pathFileLogs = carpetaLogDay+ FILENAME;
            Handler fileHandler = new FileHandler(pathFileLogs, true);
            Handler consoleHandler = new ConsoleHandler();
            fileHandler.setFormatter(new SimpleFormatter(){
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n \n";
                @Override
                public String format(LogRecord lr) {
                    return String.format(format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getMessage()
                    );
                }
            });
            LOG_RAIZ.addHandler(consoleHandler);
            LOG_RAIZ.addHandler(fileHandler);
            
            consoleHandler.setLevel(Level.OFF);
            fileHandler.setLevel(Level.ALL);
            
        } catch(SecurityException ex) {
            Logger.getLogger(LoggingServicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cambioVistaLog(Logger logger, String user, String destino){
        logger.log(Level.INFO,String.format("%s cambió a vista %s.",user,destino));
    }
    
}
