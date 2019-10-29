package controlador.aprovisionamiento.drivers;

import com.jfoenix.controls.JFXButton;
import controlador.ConexionBD;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CargarExcelDAO;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.DriverLineaDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.DriverCentro;
import modelo.DriverLinea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import servicios.ExcelServicio;
import servicios.LogServicio;

public class CargarControlador implements Initializable {
    // Variables de la vista    
    @FXML public ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<DriverCentro> tabListar;
    @FXML private TableColumn<DriverCentro, String> tabcolCodigo;
    @FXML private TableColumn<DriverCentro, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    DriverDAO driverDAO;
    DriverLineaDAO driverLineaDAO;
    CentroDAO centroDAO;
    List<DriverCentro> lstDriversCargar;
    int periodoSeleccionado;
    
    CargarExcelDAO cargarExcelDAO;
    LogServicio logServicio;
    String logName;
    String logDetail;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_CENTRO_CARGAR.getControlador());
    String titulo, titulo1;
    boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        driverLineaDAO = new DriverLineaDAO();
        centroDAO = new CentroDAO();
        cargarExcelDAO = new CargarExcelDAO();
        titulo = "Drivers";
        if (menuControlador.objeto != null)
            periodoSeleccionado = (int) menuControlador.objeto;
        else
            periodoSeleccionado = menuControlador.periodo;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Centros de Costos";
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            periodoSeleccionado = periodoSeleccionado / 100 * 100;
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
        cmbMes.getSelectionModel().select(periodoSeleccionado % 100 - 1);
        spAnho.getValueFactory().setValue(periodoSeleccionado / 100);
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
        
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_CARGAR);
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
            List<DriverCentro> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
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
    
    private List<DriverCentro> leerArchivo(String rutaArchivo) {
        List<DriverCentro> lstDrivers = driverDAO.listarDriversCentroMaestro();
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
                
                DriverCentro driver = lstDrivers.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                if (driver == null) { // crear un driver nuevo
                    driver = new DriverCentro(codigo, nombre, null, null, null, null, null,true);
                    driver.setEsNuevo(true);
                } else {
                    driver.setListaDriverLinea(new ArrayList());
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
            return null;
        }
        return lstDriversCargar;
    }
    
    private void leerHojaDetalleDriver(Workbook wb) {
        List<String> lstCentrosSinBolsas = centroDAO.listarCodigosWithoutBolsas(periodoSeleccionado,menuControlador.repartoTipo);
        
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
        
        if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE","CODIGO CENTRO","NOMBRE CENTRO","PORCENTAJE")))) {
            menuControlador.mensaje.upload_header_error(titulo);
            return;
        }
        
        while(filas.hasNext()){
            fila = filas.next();
            celdas = fila.cellIterator();
            int index;
            
            celda = celdas.next();String codigoDriver = celda.getStringCellValue();
            celda = celdas.next();String nombreDriver = celda.getStringCellValue();
            celda = celdas.next();String codigoCentro = celda.getStringCellValue();
            celda = celdas.next();String nombreCentro = celda.getStringCellValue();
            celda = celdas.next();double porcentaje = (double)celda.getNumericCellValue();

            DriverCentro dCentro = lstDriversCargar.stream().filter(item ->codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
            index = lstDriversCargar.indexOf(dCentro);
            if(dCentro != null){
                if(lstDriversCargar.get(index).getFlagCargar()){
                    cargarExcelDAO.insertarLineaDriverCentroBatch(fila.getRowNum(),codigoDriver, codigoCentro, porcentaje);
                }
            }
        }
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        logDetail = "";
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.mensaje.upload_empty();
        } else {
            for (DriverCentro driver: tabListar.getItems()) {
                if(driver.getFlagCargar()){
                    if (driver.getEsNuevo()) {
                        driverDAO.insertarDriverCabecera(driver.getCodigo(), driver.getNombre(), "CECO", menuControlador.repartoTipo);
                    }
                    StringBuilder sbMsj = new StringBuilder("");
                    List<DriverLinea> lista = cargarExcelDAO.obtenerListaCentroLinea(driver.getCodigo(), periodoSeleccionado, menuControlador.repartoTipo, sbMsj);
                    String msj = sbMsj.toString();
                    double porcentaje = cargarExcelDAO.porcentajeTotalDriverCentro(driver.getCodigo());
                    if (porcentaje != 100.00){
                        findError = true;
                        msj += String.format("- Para el driver %s, los porcentajes de los centros suman %.4f. Debe sumar 100.00%%.\r\n",driver.getCodigo(),porcentaje);
                        logDetail += String.format("Driver %s: No se pudo cargar. Existen los siguientes errores:\r\n",driver.getCodigo());
                        logDetail += msj + "\r\n";
                    } else {
                        if (lista != null) {
                            driverLineaDAO.borrarListaDriverLinea(driver.getCodigo(), periodoSeleccionado,menuControlador.repartoTipo);
                            ConexionBD.crearStatement();
                            ConexionBD.tamanhoBatchMax = 1000;
                            driverLineaDAO.insertarListaDriverCentroLineaBatch(driver.getCodigo(), periodoSeleccionado, lista, menuControlador.repartoTipo);
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
            crearReporteLOG(periodoSeleccionado);
            if(findError == true){
                menuControlador.mensaje.upload_success_with_error(titulo);
            }else {
                menuControlador.mensaje.upload_success();
            }
            btnDescargarLog.setVisible(true);        
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    void crearReporteLOG(int periodo){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_DRIVERS_CENTRO.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA: PERIODO " + periodo);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivo(logDetail);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
    }
}
