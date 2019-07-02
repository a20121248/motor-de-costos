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
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDrivers;
    @FXML private Hyperlink lnkCargar;
    
    @FXML public ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<DriverCentro> tabListar;
    @FXML private TableColumn<DriverCentro, String> tabcolCodigo;
    @FXML private TableColumn<DriverCentro, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    DriverDAO driverDAO;
    DriverLineaDAO driverLineaDAO;
    CentroDAO centroDAO;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    
    CargarExcelDAO cargarExcelDAO;
    LogServicio logServicio;
    String logName;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_CENTRO_CARGAR.getControlador());
    String titulo, titulo1;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        driverLineaDAO = new DriverLineaDAO();
        centroDAO = new CentroDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
        cargarExcelDAO = new CargarExcelDAO();
        this.titulo = "Drivers";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Centros de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo1 = "Centros de Beneficio";
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
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            cmbMes.setDisable(true);
            spAnho.setDisable(true);
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
        List<DriverCentro> lstDriversCargar = new ArrayList();
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
            
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("DRIVERS")))) {
                String msj = "El título de la hoja " + shIndexName + " no es la correcta, deber ser DRIVERS.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar drivers", msj);
                return null;
            }
            
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO","DRIVER","¿CARGAR?")))) {
                String msj = "La cabecera de la hoja "+ shIndexName +" no es la correcta.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar drivers", msj);
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
                    DriverCentro driver = lstDrivers.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                    if (driver == null) { // crear un driver nuevo
                        driver = new DriverCentro(codigo, nombre, null, null, null, null, null);
                        driver.setEsNuevo(true);
                    }
                    leerHojaDriver(driver, wb);
                    lstDriversCargar.add(driver);
                }
            }
            ConexionBD.ejecutarBatch();
            ConexionBD.cerrarStatement();
        } catch (Throwable ex) {
            menuControlador.navegador.mensajeError("Cargar Drivers", ex.getMessage());
            LOGGER.log(Level.SEVERE, "Cargar Drivers: {0}", ex.getMessage());
            return null;
        }
        return lstDriversCargar;
    }
    
    private void leerHojaDriver(DriverCentro driver, Workbook wb) {
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
        
        if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO_CECO","CECO","PORCENTAJE_DRIVER")))) {
            //String sbMsj = String.format("La cabecera en la %s archivo no es la correcta.\nNo se puede cargar el archivo.",driverCodigo);
            //LogServicio.agregarLineaArchivo(archLogNombre, sbMsj);
            return;
        }
        
        //List<DriverObjetoLinea> lstDriverLinea = new ArrayList();
        while (filas.hasNext()) {
            Row fila = filas.next();
            celdas = fila.cellIterator();
            String centroCodigo = celdas.next().getStringCellValue();
            //por si acaso...
            if (centroCodigo.equals("")) break;
            celdas.next(); //String centroNombre
            double porcentaje = celdas.next().getNumericCellValue();
            
            cargarExcelDAO.insertarLineaDriverCentroBatch(fila.getRowNum(),driver.getCodigo(), centroCodigo, porcentaje);
            //DriverObjetoLinea item = new DriverObjetoLinea(banca, oficina, producto, porcentaje, fecha, fecha);
            //lstDriverLinea.add(item);
        }
        //Date fecha = new Date();
        //DriverObjeto driver = new DriverObjeto(driverCodigo,nombreDriver,descripcionDriver,null,lstDriverLinea,fecha,fecha);
        //LOGGER.log(Level.INFO,String.format("Se creó el driver %s'.",driver.getCodigo()));
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        boolean esPrimerItem = true;
        for (DriverCentro driver: tabListar.getItems()) {
            if (driver.getEsNuevo()) {
                driverDAO.insertarDriverCabecera(driver.getCodigo(), driver.getNombre(), driver.getDescripcion(), "CECO", menuControlador.repartoTipo);
            }
        }
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 10000;
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_DRIVERS_CENTRO.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        for (DriverCentro driver: tabListar.getItems()) {
            StringBuilder sbMsj = new StringBuilder("");
            List<DriverLinea> lista = cargarExcelDAO.obtenerListaCentroLinea(driver.getCodigo(), periodoSeleccionado,sbMsj);
            String msj = "";
//            if (!esPrimerItem) {
//                for (int i=0; i < 100; ++i) msj+="-";msj += "\n";
//            }
            if (lista != null) {
                driverLineaDAO.insertarListaDriverCentroLineaBatch(driver.getCodigo(), periodoSeleccionado, lista);
                msj += String.format("Driver %s: Se cargó correctamente con %s items.\n",driver.getCodigo(),lista.size());
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), driver.getCodigo(), Navegador.RUTAS_DRIVERS_CENTRO_CARGAR.getDireccion());
            } else {
                msj += String.format("Driver %s: No se pudo cargar. Existen los siguientes errores:\n",driver.getCodigo());
            }
            sbMsj.insert(0, msj);
            menuControlador.Log.agregarLineaArchivo(sbMsj.toString());
            esPrimerItem = false;
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        
        //driverDAO.insertarListaDriverObjeto(lista, periodoSeleccionado);
        cmbMes.setDisable(false);
        spAnho.setDisable(false);
        btnDescargarLog.setVisible(true);
        menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Drivers cargados correctamente.");
        //menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        String rutaOrigen = menuControlador.Log.getCarpetaLogDay() + logName;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar LOG");
        fileChooser.setInitialFileName(logName);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivo LOG", "*.log"));
        File archivoSeleccionado = fileChooser.showSaveDialog(btnDescargarLog.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_LOG);
        }
    }
}
