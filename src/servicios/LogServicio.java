package servicios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogServicio {
    String fileName;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.LogServicio");
    
    public LogServicio(String fileName) {
        this.fileName = fileName;
    }
    
    public String obtenerFecha() {
        return (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
    }
    
    public String obtenerLinea(char caracter, int num) {
        String linea = "";
        while (num-->0)linea+=caracter;
        return linea+System.lineSeparator();
    }
    
    public void crearArchivo() {
        File file = new File("./logs/" + fileName);
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("");
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(LogServicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarLineaArchivo(String linea) {
        File file = new File("./logs/" + fileName);        
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
             BufferedWriter bw = new BufferedWriter(fw);) {
            bw.write(linea + System.lineSeparator());
        } catch (IOException ex) {
            Logger.getLogger(LogServicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void agregarLineaArchivoTiempo(String linea) {
        File file = new File("./logs/" + fileName);        
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
             BufferedWriter bw = new BufferedWriter(fw);) {
            bw.write((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss - ").format(new Date())) + linea + System.lineSeparator());
        } catch (IOException ex) {
            Logger.getLogger(LogServicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void agregarSeparadorArchivo(char caracter, int num) {
        String linea = "";
        while(num-->0)linea+=caracter;
        agregarLineaArchivo(linea);
    }
}
