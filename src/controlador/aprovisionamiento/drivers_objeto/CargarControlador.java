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
    StringBuilder Msj = new StringBuilder("");
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_CARGAR.getControlador());
    String titulo1;
    
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
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Objetos de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo1 = "Objetos de Beneficio";
            lblTitulo.setText("Drivers - " + titulo1);
            lnkDrivers.setText("Drivers - " + titulo1);
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
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
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
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            cmbMes.setDisable(true);
            spAnho.setDisable(true);
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
        List<DriverObjeto> lstDriversCargar = new ArrayList();
        try (InputStream is = new FileInputStream(new File(rutaArchivo))){
            Workbook wb = ExcelServicio.abrirLibro(is);
            
            String shIndexName = "INDEX";
            Sheet sh = ExcelServicio.abrirHoja(wb, shIndexName);
            if (sh == null) {
                String msj = "La hoja de control " + shIndexName + " no existe.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar drivers", msj);
                //logServicio.agregarLineaArchivo(msj);
                return null;
            }
            
            Iterator<Row> filas = sh.iterator();
            
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("MATRICES")))) {
                String msj = "El título de la hoja " + shIndexName + " no es la correcta, deber ser MATRICES.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar drivers", msj);
                //logServicio.agregarLineaArchivo(msj);
                return null;
            }
            
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO","DRIVER","¿CARGAR?")))) {
                String msj = "La cabecera de la hoja INDEX no es la correcta.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar drivers", msj);
                //logServicio.agregarLineaArchivo(msj);
                //LOGGER.log(Level.SEVERE, String.format("Cargar drivers: La cabecera de la hoja INDEX no es la correcta.\nNo se puede cargar el archivo."));
                return null;
            }
            cargarExcelDAO.limpiarTablas();
            ConexionBD.crearStatement();
            ConexionBD.tamanhoBatchMax=10000;
            while (filas.hasNext()) {
                Iterator<Cell> celdas = filas.next().cellIterator();
                String codigo = celdas.next().getStringCellValue();
                String nombre = celdas.next().getStringCellValue();
                String cargar = celdas.next().getStringCellValue();
                if (cargar.toUpperCase().equals("SÍ")) {
                    DriverObjeto driver = lstDrivers.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                    if (driver == null) { // crear un driver nuevo
                        driver = new DriverObjeto(codigo, nombre, null, null, null, null, null);
                        driver.setEsNuevo(true);
                    }
                    leerHojaDriver(driver, wb);
                    lstDriversCargar.add(driver);
                }
            }
            ConexionBD.ejecutarBatch();
            ConexionBD.cerrarStatement();
        } catch (Throwable ex) {
            menuControlador.navegador.mensajeError("Cargar drivers", ex.getMessage());
            LOGGER.log(Level.SEVERE, "Cargar drivers: {0}", ex.getMessage());
            return null;
        }
        return lstDriversCargar;
    }
    
    private void leerHojaDriver(DriverObjeto driver, Workbook wb) {
        Sheet sh = ExcelServicio.abrirHoja(wb, driver.getCodigo());
        if (sh == null) {
            //String sbMsj = String.format("%s: Su hoja no existe.\nNo se puede cargar dicho driver.",driverCodigo);
            //LogServicio.agregarLineaArchivo(archLogNombre, sbMsj);
            return;
        }
        Iterator<Row> filas = sh.iterator();
        Iterator<Cell> celdas;
        
        menuControlador.navegador.omitirFilas(filas, 2);
        
        celdas = filas.next().cellIterator();
        celdas.next();
        celdas.next();
        celdas.next();
        String nombreDriver = celdas.next().getStringCellValue();
        driver.setNombre(nombreDriver);

        menuControlador.navegador.omitirFilas(filas, 2);
        
        celdas = filas.next().cellIterator();
        celdas.next();
        celdas.next();
        celdas.next();
        String descripcionDriver = celdas.next().getStringCellValue();
        driver.setDescripcion(descripcionDriver);
        
        menuControlador.navegador.omitirFilas(filas, 2);
        
        if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO_PRODUCTO","PRODUCTO","CODIGO_SUBCANAL","SUBCANAL","PORCENTAJE")))) {
            //String sbMsj = String.format("La cabecera en la %s archivo no es la correcta.\nNo se puede cargar el archivo.",driverCodigo);
            //LogServicio.agregarLineaArchivo(archLogNombre, sbMsj);
            return;
        }
        
        //List<DriverObjetoLinea> lstDriverLinea = new ArrayList();
        while (filas.hasNext()) {
            Row fila = filas.next();
            celdas = fila.cellIterator();
//            String oficinaCodigo = celdas.next().getStringCellValue();
//            //por si acaso...
//            if (oficinaCodigo.equals("")) break;
//            celdas.next(); //String nombreOficina
//            String bancaCodigo = celdas.next().getStringCellValue();
//            celdas.next(); //String nombreBanca
            String productoCodigo = celdas.next().getStringCellValue();
            celdas.next(); //String nombreProducto
            String subcanalCodigo = celdas.next().getStringCellValue();
            celdas.next(); //String nombreSubcanal
            double porcentaje = celdas.next().getNumericCellValue();
            
            cargarExcelDAO.insertarLineaDriverObjetoBatch(fila.getRowNum(),driver.getCodigo(), productoCodigo, subcanalCodigo, porcentaje);
            //DriverObjetoLinea item = new DriverObjetoLinea(banca, oficina, producto, porcentaje, fecha, fecha);
            //lstDriverLinea.add(item);
        }
        //Date fecha = new Date();
        //DriverObjeto driver = new DriverObjeto(driverCodigo,nombreDriver,descripcionDriver,null,lstDriverLinea,fecha,fecha);
        //LOGGER.log(Level.INFO,String.format("Se creó el driver %s'.",driver.getCodigo()));
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        for (DriverObjeto driver: tabListar.getItems()) {
            if (driver.getEsNuevo()) {
                driverDAO.insertarDriverCabecera(driver.getCodigo(), driver.getNombre(), driver.getDescripcion(), "OBCO", menuControlador.repartoTipo);
            }
        }
        boolean esPrimerItem = true;
        StringBuilder mensaje = new StringBuilder("");
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 10000;
        for (DriverObjeto driver: tabListar.getItems()) {
            
            List<DriverObjetoLinea> lista = cargarExcelDAO.obtenerListaObjetoLinea(driver.getCodigo(),mensaje);
            String msj = "";
            if (!esPrimerItem) {
                for (int i=0; i < 100; ++i) msj+="-";msj += "\n";
            }
            if (lista != null) {
                driverLineaDAO.insertarListaDriverObjetoLineaBatch(driver.getCodigo(), periodoSeleccionado, lista);
                msj += String.format("Driver %s: Se cargó correctamente."+System.lineSeparator(),driver.getCodigo());
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), driver.getCodigo(), Navegador.RUTAS_DRIVERS_OBJETO_CARGAR.getDireccion());
            } else {
                msj += String.format("Driver %s: No se pudo cargar. Existen los siguientes errores:"+System.lineSeparator(),driver.getCodigo());
            }            
            mensaje.insert(0,msj);
            Msj.append(mensaje.toString());
            mensaje.setLength(0);
            esPrimerItem = false;
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        //driverDAO.insertarListaDriverObjeto(lista, periodoSeleccionado);
        crearReporteLOG();
        cmbMes.setDisable(false);
        spAnho.setDisable(false);
        btnDescargarLog.setVisible(true);
        menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Drivers cargados correctamente.");
        //menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    void crearReporteLOG(){
           logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_DRIVERS_OBJETOS.log";
           menuControlador.Log.generarReporteLOG(logName, Msj);
           Msj.setLength(0);
    } 
}
