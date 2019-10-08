package controlador.aprovisionamiento.drivers_objeto;

import com.jfoenix.controls.JFXButton;
import controlador.ConexionBD;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.BancaDAO;
import dao.CargarExcelDAO;
import dao.DriverDAO;
import dao.DriverLineaDAO;
import dao.OficinaDAO;
import dao.ProductoDAO;
import dao.SubcanalDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import servicios.ExcelServicio;
import servicios.LogServicio;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDrivers;
    @FXML private Hyperlink lnkCargar;
    
    @FXML public ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<DriverObjeto> tabListar;
    @FXML private TableColumn<DriverObjeto, String> tabcolCodigo;
    @FXML private TableColumn<DriverObjeto, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;    
    @FXML private JFXButton btnSubir;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    List<DriverObjeto> listaCargar = new ArrayList();
    List<DriverObjeto> lstDriversCargar;
    DriverDAO driverDAO;
    DriverLineaDAO driverLineaDAO;
    ProductoDAO productoDAO;
    SubcanalDAO subcanalDAO;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    CargarExcelDAO cargarExcelDAO;
    LogServicio logServicio;
    String logName;
    String logDetail;
    StringBuilder Msj = new StringBuilder("");
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_CARGAR.getControlador());
    String titulo, titulo1;
    boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        driverLineaDAO = new DriverLineaDAO();
        productoDAO = new ProductoDAO();
        subcanalDAO = new SubcanalDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
        cargarExcelDAO = new CargarExcelDAO();
        this.titulo = "Drivers - Objetos de Costos";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Objetos de Costos";
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            periodoSeleccionado = menuControlador.periodo-menuControlador.periodo%100;
        } else {
            periodoSeleccionado = menuControlador.periodo;
        }
        // tabla dimensiones
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 85);
        // tabla formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }
    
    @FXML void lnkDriversAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        btnDescargarLog.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de Drivers");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnDescargarLog.setVisible(false);
            if(menuControlador.repartoTipo == 2){
                spAnho.setDisable(true);
            }
            else{
                cmbMes.setDisable(true);
                spAnho.setDisable(true);
            }
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<DriverObjeto> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros: " + lista.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<DriverObjeto> leerArchivo(String rutaArchivo) {
        List<DriverObjeto> lstDrivers = driverDAO.listarDriversObjetoMaestro();
        lstDriversCargar = new ArrayList();
        
        try (InputStream is = new FileInputStream(new File(rutaArchivo))){
            Workbook wb = ExcelServicio.abrirLibro(is);
            
            String shIndexName = "INDEX";
            Sheet sh = ExcelServicio.abrirHoja(wb, shIndexName);
            if (sh == null) {
                menuControlador.mensaje.upload_sheet_dont_exist_error(titulo, shIndexName);
                return null;
            }
            
            Iterator<Row> filas = sh.iterator();
            
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("MODELO DE COSTOS")))) {
                menuControlador.mensaje.upload_title_sheet_error(titulo, shIndexName, "MODELO DE COSTOS");
                return null;
            }
            
            menuControlador.navegador.omitirFilas(filas, 4);
            
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE","TIPO","¿CARGAR?")))) {
                menuControlador.mensaje.upload_header_error(titulo);
                return null;
            }
            
            cargarExcelDAO.limpiarTablas();
            ConexionBD.crearStatement();
            ConexionBD.tamanhoBatchMax=10000;
            while (filas.hasNext()) {
                Iterator<Cell> celdas = filas.next().cellIterator();
                String codigo = celdas.next().getStringCellValue();
                String nombre = celdas.next().getStringCellValue();
                String tipo = celdas.next().getStringCellValue();
                String cargar = celdas.next().getStringCellValue();
                
                DriverObjeto driver = lstDrivers.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                if (driver == null) { // crear un driver nuevo
                    driver = new DriverObjeto(codigo, nombre, null, null, null, null, null,true);
                    driver.setEsNuevo(true);
                } else {
                    driver.setListaDriverObjetoLinea(new ArrayList());
                }
                if (cargar.toUpperCase().equals("SI")) {
                    driver.setFlagCargar(true);
                } else {
                    driver.setFlagCargar(false);                    
                }
                lstDriversCargar.add(driver);
            }
            leerHojaDetalleDriver(wb);
            ConexionBD.ejecutarBatch();
            ConexionBD.cerrarStatement();
        } catch (Throwable ex) {
            menuControlador.navegador.mensajeError("Cargar drivers", ex.getMessage());
            LOGGER.log(Level.SEVERE, "Cargar drivers: {0}", ex.getMessage());
            return null;
        }
        return lstDriversCargar;
    }
    
    private void leerHojaDetalleDriver(Workbook wb) {
        List<String> lstProductosPeriodo = productoDAO.listarCodigosPeriodo(periodoSeleccionado,menuControlador.repartoTipo);
        List<String> lstSubcanalesPeriodo = subcanalDAO.listarCodigosPeriodo(periodoSeleccionado,menuControlador.repartoTipo);
        
        String sheetName = "DETALLE";
        Sheet sh = ExcelServicio.abrirHoja(wb, sheetName);
        if (sh == null) {
            menuControlador.mensaje.upload_sheet_dont_exist_error(titulo, sheetName);
            return;
        }
        Iterator<Row> filas = sh.iterator();
        Iterator<Cell> celdas;
        
        Row fila;
        Cell celda;
        menuControlador.navegador.omitirFilas(filas, 6);
        
        if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE","CODIGO PRODUCTO","NOMBRE PRODUCTO","CODIGO SUBCANAL","NOMBRE SUBCANAL","PORCENTAJE")))) {
            menuControlador.mensaje.upload_header_error(titulo);
            return;
        }
        
        //List<DriverObjetoLinea> lstDriverLinea = new ArrayList();
        while (filas.hasNext()) {
            fila = filas.next();
            celdas = fila.cellIterator();
            int index;
            
            celda = celdas.next();String codigoDriver = celda.getStringCellValue();
            celda = celdas.next();String nombreDriver = celda.getStringCellValue();
            celda = celdas.next();String codigoProducto = celda.getStringCellValue();
            celda = celdas.next();String nombreProducto = celda.getStringCellValue();
            celda = celdas.next();String codigoSubcanal = celda.getStringCellValue();
            celda = celdas.next();String nombreSubcanal = celda.getStringCellValue();
            celda = celdas.next();double porcentaje = (double)celda.getNumericCellValue();
            
//            cargarExcelDAO.insertarLineaDriverObjetoBatch(fila.getRowNum(),driver.getCodigo(), productoCodigo, subcanalCodigo, porcentaje);
            DriverObjeto dObjeto = lstDriversCargar.stream().filter(item ->codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
            index = lstDriversCargar.indexOf(dObjeto);
            if(dObjeto != null){
                if(lstDriversCargar.get(index).getFlagCargar()){
                    cargarExcelDAO.insertarLineaDriverObjetoBatch(fila.getRowNum(),codigoDriver, codigoProducto,codigoSubcanal, porcentaje);
                }
            }
        }
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        logDetail = "";
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.mensaje.upload_empty();
        }else{
            for (DriverObjeto driver: tabListar.getItems()) {
                if(driver.getFlagCargar()){
                    if (driver.getEsNuevo()) {
                        driverDAO.insertarDriverCabecera(driver.getCodigo(), driver.getNombre(), "OBCO", menuControlador.repartoTipo);
                    }
                    StringBuilder sbMsj = new StringBuilder("");
                    List<DriverObjetoLinea> lista = cargarExcelDAO.obtenerListaObjetoLinea(driver.getCodigo(),periodoSeleccionado,menuControlador.repartoTipo, sbMsj);
//                    List<DriverObjetoLinea> lista = cargarExcelDAO.obtenerListaCentroLinea(driver.getCodigo(), periodoSeleccionado, menuControlador.repartoTipo, sbMsj);
                    String msj = "";
                    msj = sbMsj.toString();
                    double porcentaje = cargarExcelDAO.porcentajeTotalDriverObjeto(driver.getCodigo());
                    if( porcentaje != 100.00){
                        findError = true;
                        msj += String.format("- Para el driver %s, los porcentajes de los centros suman %.4f. Debe sumar 100.00%%.\r\n",driver.getCodigo(),porcentaje);
                        logDetail += String.format("Driver %s: No se pudo cargar. Existen los siguientes errores:\r\n",driver.getCodigo());
                        logDetail += msj + "\r\n";
                    } else {
                        if (lista != null) {
                            ConexionBD.crearStatement();
                            ConexionBD.tamanhoBatchMax = 10000;
                            driverLineaDAO.insertarListaDriverObjetoLineaBatch(driver.getCodigo(), periodoSeleccionado, lista, menuControlador.repartoTipo);
                            ConexionBD.ejecutarBatch();
                            ConexionBD.cerrarStatement();
                            logDetail += String.format("Driver %s: Se cargó correctamente con %s items.\r\n\r\n",driver.getCodigo(),lista.size());
                            menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), driver.getCodigo(), Navegador.RUTAS_DRIVERS_CENTRO_CARGAR.getDireccion());
                        } else {
                            findError = true;
                            logDetail += String.format("Driver %s: No se pudo cargar. Existen los siguientes errores:\r\n",driver.getCodigo());
                            logDetail += msj  + "\r\n";
                        }
                    }
                }
            }
            crearReporteLOG();
            if(findError == true){
                menuControlador.mensaje.upload_success_with_error(titulo);
            }else {
                menuControlador.mensaje.upload_success();
            }
            btnDescargarLog.setVisible(true);  
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    void crearReporteLOG(){
            logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_DRIVERS_OBJETOS.log";
            menuControlador.Log.crearArchivo(logName);
            menuControlador.Log.agregarSeparadorArchivo('=', 100);
            menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
            menuControlador.Log.agregarSeparadorArchivo('=', 100);
            menuControlador.Log.agregarLineaArchivo(logDetail);
            menuControlador.Log.agregarSeparadorArchivo('=', 100);
            menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
            menuControlador.Log.agregarSeparadorArchivo('=', 100);
    } 
}
