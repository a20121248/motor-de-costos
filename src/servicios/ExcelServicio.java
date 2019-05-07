package servicios;

import com.github.pjfanning.xlsx.StreamingReader;
import controlador.Navegador;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.DriverObjetoLinea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelServicio {
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.ExcelServicio");
    
    public static String leerString(Iterator<Cell> celdas) {
        Cell celda = celdas.next();
        celda.setCellType(CellType.STRING);
        return celda.getStringCellValue();
    }
    
    public static Workbook abrirLibro(InputStream is) {
        try {
            return StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(is);            // InputStream or File for XLSX file (required
        } catch (Throwable ex) {
            return null;
        }
    }
    
    public static Sheet abrirHoja(Workbook wb, String sheetName) {
        try {
            return wb.getSheet(sheetName);
        } catch (Throwable ex) {
            return null;
        }
    }
    
    public static double leerDouble(Iterator<Cell> celdas) {
        Cell celda = celdas.next();
        celda.setCellType(CellType.NUMERIC);
        return (double) celda.getNumericCellValue();
    }
}
