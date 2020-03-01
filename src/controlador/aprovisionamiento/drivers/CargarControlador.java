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
import java.io.FileNotFoundException;
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
import javafx.concurrent.Task;
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
import org.controlsfx.dialog.ProgressDialog;
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
    
    CargarExcelDAO cargarExcelDAO;
    LogServicio logServicio;
    String logName;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_CENTRO_CARGAR.getControlador());
    String titulo, titulo1;
    List<DriverCentro> lstDriversCargar;
    int cantDriversSubidos;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        driverLineaDAO = new DriverLineaDAO();
        centroDAO = new CentroDAO();
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
        // Botones para periodo
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) 
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        spAnho.getValueFactory().setValue(menuControlador.periodoSeleccionado / 100);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
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
            
            Task leerArchivoWorker = createLeerArchivoWorker(archivoSeleccionado.getAbsolutePath());
            ProgressDialog dialog = new ProgressDialog(leerArchivoWorker);
            dialog.setTitle("Cargar drivers de " + titulo1);
            dialog.setHeaderText("Cargar drivers");
            dialog.setContentText("Plantilla de drivers");
            new Thread(leerArchivoWorker).start();
            dialog.showAndWait();            
            
            if (lstDriversCargar != null) {
                tabListar.getItems().setAll(lstDriversCargar);
                lblNumeroRegistros.setText("Número de registros: " + lstDriversCargar.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    
    public Task createLeerArchivoWorker(String rutaArchivo) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                LOGGER.log(Level.INFO, String.format("Abrir el archivo '%s'.", rutaArchivo));
                try {
                    InputStream is = new FileInputStream(new File(rutaArchivo));
                    Workbook wb = ExcelServicio.abrirLibro(is);

                    if (wb == null) {
                        String msj = String.format("No se pudo abrir el archivo '%s'.", rutaArchivo);
                        LOGGER.log(Level.SEVERE, msj);
                        menuControlador.navegador.mensajeError("Cargar drivers", msj);
                        return null;
                    }

                    String shIndexName = "INDEX";
                    Sheet sh = ExcelServicio.abrirHoja(wb, shIndexName);
                    if (sh == null) {
                        String msj = "La hoja de control " + shIndexName + " no existe. No se puede cargar el archivo.";
                        LOGGER.log(Level.SEVERE, msj);
                        menuControlador.navegador.mensajeError("Cargar drivers", msj);
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

                    //==================================================================
                    Date fecha = new Date();            
                    String fechaStr = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(fecha);

                    logName = fechaStr + "CARGAR_DRIVERS_CENTRO.log";
                    logServicio = new LogServicio(logName);
                    logServicio.crearArchivo();
                    String msj = "";
                    for (int i=0; i < 100; ++i) msj+="=";msj += "\n";
                    fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(fecha);
                    msj += String.format("%s - PERIODO %d: FL PROCESO DE CARGA\n", fechaStr, menuControlador.periodoSeleccionado);
                    for (int i=0; i < 100; ++i) msj+="=";msj += "\n";
                    logServicio.agregarLineaArchivo(msj);
                    cargarExcelDAO.limpiarTablas();

                    List<DriverCentro> lstDrivers = driverDAO.listarDriversCentroMaestro();
                    lstDriversCargar = new ArrayList();
                    while (filas.hasNext()) {
                        Row fila = filas.next();
                        String codigo = fila.getCell(0).getStringCellValue();
                        String nombre = fila.getCell(1).getStringCellValue();
                        String cargar = fila.getCell(2).getStringCellValue();
                        LOGGER.log(Level.INFO, String.format("Hoja %s, Fila %d - Driver %s: Leer indíce", shIndexName, fila.getRowNum() + 1, codigo));
                        if (cargar.toUpperCase().equals("SÍ")) {
                            if (lstDriversCargar.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null) != null) {
                                LOGGER.log(Level.SEVERE, String.format("Hoja %s, Fila %d - Driver %s: Ya se leyó previamente el driver. Se omitirá esta fila.", shIndexName, fila.getRowNum() + 1, codigo));
                            } else {
                                DriverCentro driver = lstDrivers.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                                if (driver == null) { // Crear un driver nuevo
                                    driver = new DriverCentro(codigo, nombre, null, null, null, null, null);
                                    driver.setEsNuevo(true);
                                }
                                lstDriversCargar.add(driver);
                            }
                        }
                    }

                    ConexionBD.crearStatement();
                    ConexionBD.tamanhoBatchMax=5000;
                    int max= lstDriversCargar.size();
                    updateProgress(0, max);
                    int i = 1;
                    for (DriverCentro driver: lstDriversCargar) {
                        updateMessage(String.format("Leyendo driver %s (%d/%d)", driver.getCodigo(), i, lstDriversCargar.size()));
                        leerHojaDriver(driver, wb);
                        updateProgress(i++, max);
                    }
                    // los posibles registros que no se hayan ejecutado
                    ConexionBD.ejecutarBatch();
                    ConexionBD.cerrarStatement();
                } catch (FileNotFoundException exc) { 
                    menuControlador.navegador.mensajeError("Cargar drivers", exc.getMessage());
                    LOGGER.log(Level.SEVERE, "Cargar drivers: {0}", exc.getMessage());
                    return null;
                }
                return true;
            }            
        };
    }
    
    private void leerHojaDriver(DriverCentro driver, Workbook wb) {
        Sheet sh = ExcelServicio.abrirHoja(wb, driver.getCodigo());
        if (sh == null) {
            String sbMsj = String.format("Driver %s: La hoja %s no existe en el archivo. No se puede cargar el driver.", driver.getCodigo(), driver.getCodigo());
            LOGGER.log(Level.SEVERE, sbMsj);
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
        
        Row fila = filas.next();
        celdas = fila.cellIterator();
        celdas.next();
        celdas.next();
        celdas.next();
        String descripcionDriver = celdas.next().getStringCellValue();
        driver.setDescripcion(descripcionDriver);
        
        menuControlador.navegador.omitirFilas(filas, 2);
        
        if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO_CECO","CECO","PORCENTAJE_DRIVER")))) {
            String sbMsj = String.format("Hoja %s, Fila %d - Driver %s: La cabecera no es la correcta.", driver.getCodigo(), fila.getRowNum() + 1, driver.getCodigo());
            LOGGER.log(Level.SEVERE, sbMsj);
            return;
        }
        
        int cantidadFilas = 0;
        while (filas.hasNext()) {
            fila = filas.next();
            if (fila.getCell(0) == null) break;
            String centroCodigo = fila.getCell(0).getStringCellValue();
            if (centroCodigo.isEmpty()) break;
            double porcentaje = fila.getCell(2).getNumericCellValue();
            ++cantidadFilas;
            cargarExcelDAO.insertarLineaDriverCentroBatch(fila.getRowNum()+1, driver.getCodigo(), centroCodigo, porcentaje);
        }
        LOGGER.log(Level.INFO, String.format("Hoja %s, Fila %d - Driver %s: Se leyeron %d filas.", driver.getCodigo(), fila.getRowNum() + 1, driver.getCodigo(), cantidadFilas));
    }
    
    public Task createSubirArchivoWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                boolean esPrimerItem = true;
                tabListar.getItems().stream().filter((driver) -> (driver.getEsNuevo())).forEachOrdered((driver) -> {
                    driverDAO.insertarDriverCabecera(driver.getCodigo(), driver.getNombre(), driver.getDescripcion(), "CECO", menuControlador.repartoTipo);
                });                
                ConexionBD.crearStatement();
                ConexionBD.tamanhoBatchMax = 5000;
                cantDriversSubidos = 0;
                int max= lstDriversCargar.size();
                updateProgress(0, max);
                int i = 1;
                for (DriverCentro driver: tabListar.getItems()) {
                    updateMessage(String.format("Subiendo driver %s (%d/%d)", driver.getCodigo(), i, tabListar.getItems().size()));
                    
                    StringBuilder sbMsj = new StringBuilder("");
                    List<DriverLinea> lista = cargarExcelDAO.obtenerListaCentroLinea(driver.getCodigo(),sbMsj);
                    String msjBuilder = "";
                    if (!esPrimerItem) {
                        for (int j=0; j < 100; ++j) msjBuilder+="-";msjBuilder += "\n";
                    }
                    if (lista != null) {
                        driverLineaDAO.insertarListaDriverCentroLineaBatch(driver.getCodigo(), menuControlador.periodoSeleccionado, lista);
                        msjBuilder += String.format("Driver %s: Se cargó correctamente el detalle con %d filas en la base de datos.\n",driver.getCodigo(), lista.size());
                        ++cantDriversSubidos;
                    } else {
                        msjBuilder += String.format("Driver %s: No se pudo cargar. Existen los siguientes errores:\n",driver.getCodigo());
                    }
                    sbMsj.insert(0, msjBuilder);
                    logServicio.agregarLineaArchivo(sbMsj.toString());

                    // Mostrar en consola
                    if (lista != null) {
                        LOGGER.log(Level.INFO, sbMsj.toString());
                    } else {
                        LOGGER.log(Level.SEVERE, sbMsj.toString());
                    }
                    esPrimerItem = false;
                    
                    updateProgress(i++, max);
                }
                // los posibles registros que no se hayan ejecutado
                ConexionBD.ejecutarBatch();
                ConexionBD.cerrarStatement();
                
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
                btnDescargarLog.setVisible(true);
                
                return true;
            }            
        };
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        Task subirArchivoWorker = createSubirArchivoWorker();
        ProgressDialog dialog = new ProgressDialog(subirArchivoWorker);
        dialog.setTitle("Subir drivers de " + titulo1);
        dialog.setHeaderText("Cargar drivers");
        dialog.setContentText("Plantilla de drivers");
        new Thread(subirArchivoWorker).start();
        dialog.showAndWait();
        
        if (tabListar.getItems().size() == cantDriversSubidos) {
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Drivers cargados correctamente.");
        } else if (cantDriversSubidos == 0) {
            menuControlador.navegador.mensajeError("Subida de archivo Excel", "No se cargó ningún driver. Por favor revise el log");
        } else {
          menuControlador.navegador.mensajeError("Subida de archivo Excel", String.format("Existen %d drivers que no se cargaron. Por favor revise el log.", tabListar.getItems().size() - cantDriversSubidos));
        }
        String msj = String.format("Se cargaron %d/%d drivers.", cantDriversSubidos, tabListar.getItems().size());
        logServicio.agregarLineaArchivo(msj);
        LOGGER.log(Level.INFO, msj);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        String rutaOrigen = "." + File.separator + "logs" + File.separator + logName;
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
