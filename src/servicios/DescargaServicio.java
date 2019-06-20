package servicios;

import dao.DriverDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import modelo.CentroDriver;
import modelo.Driver;
import modelo.DriverLinea;
import modelo.DriverObjetoLinea;
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
    private TableView<? extends EntidadDistribucion> tablaEntidadDistribucion;
    private TableView<? extends Driver> tablaDriver;
    private TableView<CentroDriver> tablaCentroDriver;

    
    public DescargaServicio(String serv, TableView<? extends EntidadDistribucion> tabla){
        this.servicio = serv;
        this.tablaEntidadDistribucion = tabla;
        this.tablaDriver = null;
        this.tablaCentroDriver = null;
    }
    
    public DescargaServicio(TableView<? extends Driver> tabla, String serv){
        this.servicio = serv;
        this.tablaDriver = tabla;
        driverDAO = new DriverDAO();
    }
    
    public DescargaServicio(TableView<CentroDriver> tabla, String serv, String tipo){
        this.servicio = serv;
        this.tablaEntidadDistribucion = null;
        this.tablaDriver = null;
        this.tablaCentroDriver = tabla;
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
    
    public void descargarTablaDriverCentros(String periodo, int reparto, String ruta) throws IOException{
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
    
    public void descargarTablaDriverObjetos(String periodo, int reparto, String ruta) throws IOException{
        if(ruta == null){
            FILE_PATH = "";
        }else{
            FILE_PATH =ruta ;
        }
        Workbook workbook = new XSSFWorkbook();    
        Sheet sheet1 = workbook.createSheet("Drivers");
        Sheet sheet2 = workbook.createSheet("Objetos Distribuir");
        Row rowDrivers = sheet1.createRow(0);
        Row rowObjetos = sheet2.createRow(0);
        int k = 1;
        rowDrivers.createCell(0).setCellValue("Periodo");
//        Cabeceras para la Hoja sobre los Centros de Costos a distribuir
        rowObjetos.createCell(0).setCellValue("Periodo");
        rowObjetos.createCell(1).setCellValue("Codigo_Driver");
        rowObjetos.createCell(2).setCellValue("Codigo_Producto");
        rowObjetos.createCell(3).setCellValue("Nombre_Producto");
        rowObjetos.createCell(4).setCellValue("Codigo_Subcanal");
        rowObjetos.createCell(5).setCellValue("Nombre_Subcanal");
        rowObjetos.createCell(6).setCellValue("Porcentaje");
        
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
                        List<DriverObjetoLinea> lstDriverLinea = driverDAO.obtenerDriverObjetoLinea(Integer.parseInt(periodo),value);
                        for(DriverObjetoLinea item: lstDriverLinea){
                            rowObjetos = sheet2.createRow(index++);
                            rowObjetos.createCell(0).setCellValue(periodo);
                            rowObjetos.createCell(1).setCellValue(value);
                            rowObjetos.createCell(2).setCellValue(item.getProducto().getCodigo());
                            rowObjetos.createCell(3).setCellValue(item.getProducto().getNombre());
                            rowObjetos.createCell(4).setCellValue(item.getSubcanal().getCodigo());
                            rowObjetos.createCell(5).setCellValue(item.getSubcanal().getNombre());
                            rowObjetos.createCell(6).setCellValue(item.getPorcentaje());
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
    
    public void descargarTablaAsignarCentroDriver(String periodo, String ruta) throws IOException{
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
        
        for (int j = 0; j < tablaCentroDriver.getColumns().size(); j++) {
            row.createCell(j+k).setCellValue(tablaCentroDriver.getColumns().get(j).getText());
        }

        for (int i = 0; i < tablaCentroDriver.getItems().size(); i++) {
            row = sheet.createRow(i + 1);
            if(periodo != null){
                    row.createCell(0).setCellValue(periodo);
                }
            for (int j = 0; j < tablaCentroDriver.getColumns().size(); j++) {
                if(tablaCentroDriver.getColumns().get(j).getCellData(i) != null) { 
                    row.createCell(j+k).setCellValue(tablaCentroDriver.getColumns().get(j).getCellData(i).toString()); 
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
