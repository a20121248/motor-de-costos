package servicios;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.TableView;
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
    private String servicio;
    private String FILE_PATH = "vistas/";
    private TableView<EntidadDistribucion> tabla;

    
    public DescargaServicio(String serv, TableView<EntidadDistribucion> tabla){
        this.servicio = serv;
        this.tabla = tabla;
        
    }
    
    public void DescargarTabla(String nameFileXLS, int flag, String periodo) throws IOException{
//        Si recibe flag = 1; se agregara la columna Periodo
//                  Periodo se obtiene el valor de AnhoMes
        Workbook workbook = new XSSFWorkbook();    
        Sheet sheet = workbook.createSheet(servicio);
        Row row = sheet.createRow(0);
        int k = 0;
        if(flag == 1){
                row.createCell(0).setCellValue("Periodo");
                k = 1;
        }
        
        for (int j = 0; j < tabla.getColumns().size(); j++) {
            row.createCell(j+k).setCellValue(tabla.getColumns().get(j).getText());
        }

        for (int i = 0; i < tabla.getItems().size(); i++) {
            row = sheet.createRow(i + 1);
            if(flag == 1){
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
            FileOutputStream fileOut = new FileOutputStream(FILE_PATH+nameFileXLS);
            workbook.write(fileOut);
            fileOut.close();
        }
        catch(IOException e){
        }

    }
 }
