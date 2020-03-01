package controlador.aprovisionamiento.drivers_objeto;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import controlador.ConexionBD;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.modals.ProgresoControlador;
import dao.BancaDAO;
import dao.CargarExcelDAO;
import dao.DriverDAO;
import dao.DriverLineaDAO;
import dao.OficinaDAO;
import dao.ProductoDAO;
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
import javafx.scene.control.ProgressBar;
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
import org.controlsfx.dialog.ProgressDialog;
import servicios.ExcelServicio;
import servicios.LogServicio;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;

    @FXML public ProgressBar pbCargando;
    //@FXML public Label lblCargando;
    @FXML public JFXSpinner spCargando;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDrivers;
    @FXML private Hyperlink lnkCargar;
    
    @FXML public ComboBox<String> cmbMes;
    @FXML public Spinner<Integer> spAnho;
    @FXML public TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML public TableView<DriverObjeto> tabListar;
    @FXML private TableColumn<DriverObjeto, String> tabcolCodigo;
    @FXML private TableColumn<DriverObjeto, String> tabcolNombre;
    @FXML public Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;    
    @FXML private JFXButton btnSubir;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    DriverDAO driverDAO;
    DriverLineaDAO driverLineaDAO;
    OficinaDAO oficinaDAO;
    BancaDAO bancaDAO;
    ProductoDAO productoDAO;
    CargarExcelDAO cargarExcelDAO;
    public LogServicio logServicio;
    public String logName;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_CARGAR.getControlador());
    String titulo1;
    List<DriverObjeto> lstDriversCargar;
    int cantDriversSubidos;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        driverLineaDAO = new DriverLineaDAO();
        oficinaDAO = new OficinaDAO();
        bancaDAO = new BancaDAO();
        productoDAO = new ProductoDAO();
        cargarExcelDAO = new CargarExcelDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pbCargando.setProgress(0);
        spCargando.setProgress(0);
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
                    if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("MATRICES")))) {
                        String msj = "El título de la hoja " + shIndexName + " no es la correcta, deber ser MATRICES. No se puede cargar el archivo.";
                        menuControlador.navegador.mensajeError("Cargar drivers", msj);
                        return null;
                    }

                    if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO","DRIVER","¿CARGAR?")))) {
                        String msj = "La cabecera de la hoja INDEX no es la correcta. No se puede cargar el archivo.";
                        menuControlador.navegador.mensajeError("Cargar drivers", msj);
                        return null;
                    }

                    //==================================================================
                    Date fecha = new Date();            
                    String fechaStr = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(fecha);

                    logName = fechaStr + "CARGAR_DRIVERS_OBJETO.log";
                    logServicio = new LogServicio(logName);
                    logServicio.crearArchivo();
                    String msj = "";
                    for (int i=0; i < 100; ++i) msj+="=";msj += "\n";
                    fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(fecha);
                    msj += String.format("%s - PERIODO %d: INICIO DEL PROCESO DE CARGA\n", fechaStr, menuControlador.periodoSeleccionado);
                    for (int i=0; i < 100; ++i) msj+="=";msj += "\n";
                    logServicio.agregarLineaArchivo(msj);
                    cargarExcelDAO.limpiarTablas();

                    List<DriverObjeto> lstDrivers = driverDAO.listarDriversObjetoMaestro();
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
                                DriverObjeto driver = lstDrivers.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                                if (driver == null) { // Crear un driver nuevo
                                    driver = new DriverObjeto(codigo, nombre, null, null, null, null, null);
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
                    for (DriverObjeto driver: lstDriversCargar) {
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
    
    public void leerHojaDriver(DriverObjeto driver, Workbook wb) {
        Sheet sh = ExcelServicio.abrirHoja(wb, driver.getCodigo());
        if (sh == null) {
            String sbMsj = String.format("Driver %s: La hoja %s no existe en el archivo. No se puede cargar el driver.", driver.getCodigo(), driver.getCodigo());
            LOGGER.log(Level.SEVERE, sbMsj);
            return;
        }
        Iterator<Row> filas = sh.iterator();
        Iterator<Cell> celdas;
        
        menuControlador.navegador.omitirFilas(filas, 2);
        
        Row fila = filas.next();
        celdas = fila.cellIterator();
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
        
        if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("CODIGO_OFICINA","OFICINA","CODIGO_BANCA","BANCA","CODIGO_PRODUCTO","PRODUCTO","PORCENTAJE")))) {
            String sbMsj = String.format("Hoja %s, Fila %d - Driver %s: La cabecera no es la correcta.", driver.getCodigo(), fila.getRowNum() + 1, driver.getCodigo());
            LOGGER.log(Level.SEVERE, sbMsj);
            return;
        }
        
        int cantidadFilas = 0;
        while (filas.hasNext()) {
            fila = filas.next();
            if (fila.getCell(0) == null) break;
            String oficinaCodigo = fila.getCell(0).getStringCellValue();
            if (oficinaCodigo.isEmpty()) break;
            String bancaCodigo = fila.getCell(2).getStringCellValue();
            String productoCodigo = fila.getCell(4).getStringCellValue();
            double porcentaje = fila.getCell(6).getNumericCellValue();
            ++cantidadFilas;
            cargarExcelDAO.insertarLineaDriverObjetoBatch(fila.getRowNum() + 1,driver.getCodigo(), oficinaCodigo, bancaCodigo, productoCodigo, porcentaje);
        }
        LOGGER.log(Level.INFO, String.format("Hoja %s, Fila %d - Driver %s: Se leyeron %d filas.", driver.getCodigo(), fila.getRowNum() + 1, driver.getCodigo(), cantidadFilas));
    }    
    
    public Task createSubirArchivoWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                boolean esPrimerItem = true;
                tabListar.getItems().stream().filter((driver) -> (driver.getEsNuevo())).forEachOrdered((driver) -> {
                    driverDAO.insertarDriverCabecera(driver.getCodigo(), driver.getNombre(), driver.getDescripcion(), "OBCO", menuControlador.repartoTipo);
                });
                ConexionBD.crearStatement();
                ConexionBD.tamanhoBatchMax = 5000;
                cantDriversSubidos = 0;
                int max= lstDriversCargar.size();
                updateProgress(0, max);
                int i = 1;
                for (DriverObjeto driver: tabListar.getItems()) {
                    updateMessage(String.format("Subiendo driver %s (%d/%d)", driver.getCodigo(), i, tabListar.getItems().size()));
                    
                    StringBuilder sbMsj = new StringBuilder("");
                    List<DriverObjetoLinea> lista = cargarExcelDAO.obtenerListaObjetoLinea(driver.getCodigo(),sbMsj);
                    String msjBuilder = "";
                    if (!esPrimerItem) {
                        for (int j=0; j < 100; ++j) msjBuilder+="-";msjBuilder += "\n";
                    }
                    if (lista != null) {
                        driverLineaDAO.insertarListaDriverObjetoLineaBatch(driver.getCodigo(), menuControlador.periodoSeleccionado, lista);
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
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
