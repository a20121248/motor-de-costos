package servicios;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import modelo.Grupo;
/**
 *
 * @author briggette.olenka.ro1
 */
public class DescargaServicio {
    private String servicio;
    private String FILE_PATH = "vistas/";
    private List<Grupo> tabla;

    
    public DescargaServicio(String serv, List<Grupo> tabla){
        this.servicio = serv;
        this.tabla = tabla;
        
    }
    
    public  void DescargarTabla(String nameFileXLS) throws IOException{
        Workbook workbook = new XSSFWorkbook();    
        Sheet hoja = workbook.createSheet(servicio);
        Row row = hoja.createRow(0);
        
        int rowIndex = 0;
        int columnIndex = 0;
        row.createCell(columnIndex++).setCellValue("Codigo");
        row.createCell(columnIndex++).setCellValue("Nombre");
        for(Grupo item:tabla){
            row = hoja.createRow(++rowIndex);
            columnIndex = 0;
            row.createCell(columnIndex++).setCellValue(item.getCodigo());
            row.createCell(columnIndex++).setCellValue(item.getNombre());
        }
        
        try{
            FileOutputStream fileOut = new FileOutputStream(FILE_PATH+nameFileXLS);
            workbook.write(fileOut);
            fileOut.close();
        }
        catch(IOException e){
        }
        
    }
}
