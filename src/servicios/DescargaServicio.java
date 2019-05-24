package servicios;

import dao.DriverDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import modelo.Driver;
import modelo.DriverLinea;
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
    DriverDAO driverDAO;
    private final String servicio;
    private String FILE_PATH;
    private final TableView<? extends EntidadDistribucion> tablaEntidadDistribucion;
    private final TableView<? extends Driver> tablaDriver;

    
    public DescargaServicio(String serv, TableView<? extends EntidadDistribucion> tabla){
        this.servicio = serv;
        this.tablaEntidadDistribucion = tabla;
        this.tablaDriver = null;
    }
    
    public DescargaServicio(TableView<? extends Driver> tabla, String serv){
        this.servicio = serv;
        this.tablaEntidadDistribucion = null;
        this.tablaDriver = tabla;
        driverDAO = new DriverDAO();
    }
    
    public void descargarTabla(String periodo, String ruta) throws IOException{
        if(ruta == null){
            FILE_PATH = "";
        }else{
            FILE_PATH =ruta ;
        }
        Workbook workbook = new XSSFWorkbook();    
        Sheet sheet = workbook.createSheet(servicio);
        Row row = sheet.createRow(0);
        int k = 0;
        
//        Si recibe periodo != null; se agregará la columna Periodo
//                  Periodo se obtiene el valor de AnhoMes numérico
        if(periodo != null){
                row.createCell(0).setCellValue("Periodo");
                k = 1;
                FILE_PATH=FILE_PATH+"/"+periodo+"_";
        }
        else{
            FILE_PATH=FILE_PATH+"/";
        }
        
        for (int j = 0; j < tablaEntidadDistribucion.getColumns().size(); j++) {
            row.createCell(j+k).setCellValue(tablaEntidadDistribucion.getColumns().get(j).getText());
        }

        for (int i = 0; i < tablaEntidadDistribucion.getItems().size(); i++) {
            row = sheet.createRow(i + 1);
            if(periodo != null){
                    row.createCell(0).setCellValue(periodo);
                }
            for (int j = 0; j < tablaEntidadDistribucion.getColumns().size(); j++) {
                if(tablaEntidadDistribucion.getColumns().get(j).getCellData(i) != null) { 
                    row.createCell(j+k).setCellValue(tablaEntidadDistribucion.getColumns().get(j).getCellData(i).toString()); 
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
    
    public void descargarTablaDriverCECO(String periodo, int reparto, String ruta) throws IOException{
        if(ruta == null){
            FILE_PATH = "";
        }else{
            FILE_PATH =ruta ;
        }
        Workbook workbook = new XSSFWorkbook();    
        Sheet sheet1 = workbook.createSheet("Drivers");
        Sheet sheet2 = workbook.createSheet("CECOs Distribuir");
        Row rowDrivers = sheet1.createRow(0);
        Row rowCECO = sheet2.createRow(0);
        int k = 1;
        rowDrivers.createCell(0).setCellValue("Periodo");
//        Cabeceras para la Hoja sobre los Centros de Costos a distribuir
        rowCECO.createCell(0).setCellValue("Periodo");
        rowCECO.createCell(1).setCellValue("Codigo_Driver");
        rowCECO.createCell(2).setCellValue("Codigo_CECO");
        rowCECO.createCell(3).setCellValue("Nombre_CECO");
        rowCECO.createCell(4).setCellValue("Porcentaje");
        
        FILE_PATH=FILE_PATH+"/"+periodo+"_";
        for (int j = 0; j < tablaDriver.getColumns().size(); j++) {
            rowDrivers.createCell(j+k).setCellValue(tablaDriver.getColumns().get(j).getText());
        }
        int index = 1;
        for (int i = 0; i < tablaDriver.getItems().size(); i++) {
            rowDrivers = sheet1.createRow(i + 1);
            rowDrivers.createCell(0).setCellValue(periodo);
            for (int j = 0; j < tablaDriver.getColumns().size(); j++) {
                if(tablaDriver.getColumns().get(j).getCellData(i) != null) {
                    String value = tablaDriver.getColumns().get(j).getCellData(i).toString();
                    rowDrivers.createCell(j+k).setCellValue(value); 
                    if(j==0){
                        List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(Integer.parseInt(periodo),value,reparto);
                        for(DriverLinea item: lstDriverLinea){
                            rowCECO = sheet2.createRow(index++);
                            rowCECO.createCell(0).setCellValue(periodo);
                            rowCECO.createCell(1).setCellValue(value);
                            rowCECO.createCell(2).setCellValue(item.getEntidadDistribucionDestino().getCodigo());
                            rowCECO.createCell(3).setCellValue(item.getEntidadDistribucionDestino().getNombre());
                            rowCECO.createCell(4).setCellValue(item.getPorcentaje());
                        }
                        
                    }
                }
                else {
                    rowDrivers.createCell(j).setCellValue("");
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
