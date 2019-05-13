package servicios;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import modelo.EntidadDistribucion;
/**
 *
 * @author briggette.olenka.ro1
 */
public class DescargaServicio {
    private final String servicio;
    private String FILE_PATH;
    private final TableView<? extends EntidadDistribucion> tabla;

    
    public DescargaServicio(String serv, TableView<? extends EntidadDistribucion> tabla){
        this.servicio = serv;
        this.tabla = tabla;
    }
    
    public void DescargarTabla(String periodo, String ruta) throws IOException{
        if(ruta == null){
            FILE_PATH = "";
        }else{
            FILE_PATH =ruta ;
        }
//        Si recibe periodo != null; se agregará la columna Periodo
//                  Periodo se obtiene el valor de AnhoMes numérico
        Workbook workbook = new XSSFWorkbook();    
        Sheet sheet = workbook.createSheet(servicio);
        Row row = sheet.createRow(0);
        int k = 0;
        if(periodo != null){
                row.createCell(0).setCellValue("Periodo");
                k = 1;
                FILE_PATH=FILE_PATH+"/"+periodo+"_";
        }
        else{
            FILE_PATH=FILE_PATH+"/";
        }
        
        for (int j = 0; j < tabla.getColumns().size(); j++) {
            row.createCell(j+k).setCellValue(tabla.getColumns().get(j).getText());
        }

        for (int i = 0; i < tabla.getItems().size(); i++) {
            row = sheet.createRow(i + 1);
            if(periodo != null){
                    if(periodo!=null)
                        row.createCell(0).setCellValue(periodo);
                }
            for (int j = 0; j < tabla.getColumns().size(); j++) {
                if(tabla.getColumns().get(j).getCellData(i) != null) { 
                    row.createCell(j+k).setCellValue(tabla.getColumns().get(j).getCellData(i).toString()); 
                }
                else {
                    row.createCell(j).setCellValue("");
                }   
            }
        }
        try{
            FileOutputStream fileOut = new FileOutputStream(FILE_PATH+servicio+".xlsx");
            workbook.write(fileOut);
            fileOut.close();
        }
        catch(IOException e){
        }
    }
 }
