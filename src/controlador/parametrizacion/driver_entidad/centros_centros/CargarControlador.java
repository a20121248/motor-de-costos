package controlador.parametrizacion.driver_entidad.centros_centros;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.AsignacionEntidadDriverDAO;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.GrupoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
import modelo.CargarEntidadDriver;
import modelo.Centro;
import modelo.CentroDriver;
import modelo.Driver;
import modelo.DriverCentro;
import modelo.EntidadDistribucion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkAsignaciones;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<CentroDriver> tabAsignaciones;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoEntidad;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreEntidad;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreDriver;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;    
    
    // Variables de la aplicacion
    List<CentroDriver> listaCargar = new ArrayList();
    public MenuControlador menuControlador;
    GrupoDAO grupoDAO;
    CentroDAO centroDAO;
    DriverDAO driverDAO;
    AsignacionEntidadDriverDAO asignacionEntidadDriverDAO;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR.getControlador());
    String titulo = "Driver";
    boolean findError;
    String logName;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        grupoDAO = new GrupoDAO();
        centroDAO = new CentroDAO();
        driverDAO = new DriverDAO();
        asignacionEntidadDriverDAO = new AsignacionEntidadDriverDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla dimensiones
        tabAsignaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoEntidad.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreEntidad.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        tabcolCodigoDriver.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreDriver.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        // tabla formato
        tabcolCodigoEntidad.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreEntidad.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().codigoDriverProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().nombreDriverProperty());
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
        btnDescargarLog.setVisible(false);
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkAsignacionesAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de planDeCuentas");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            cmbMes.setDisable(true);
            spAnho.setDisable(true);
            List<CentroDriver> lista = leerArchivo(archivoSeleccionado.getAbsolutePath(), periodoSeleccionado);
            if (lista != null) {
                tabAsignaciones.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<CentroDriver> leerArchivo(String rutaArchivo, int periodo) {
        List<CentroDriver> lista = new ArrayList();
        List<CentroDriver> lstEntidades = centroDAO.listarCentrosConDriver(periodoSeleccionado,"-",menuControlador.repartoTipo,-1);
        List<String> lstDrivers = driverDAO.listarCodigosDriverPeriodo(periodoSeleccionado,menuControlador.repartoTipo,"CECO");
        
        try {
            FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f);
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila = null;
            Cell celda = null;
            
            
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO CENTRO","NOMBRE CENTRO","CODIGO DRIVER","NOMBRE DRIVER")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodoArchivo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoEntidad = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreEntidad = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoDriver = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreDriver = celda.getStringCellValue();

                if (periodo != periodoArchivo) {
                    menuControlador.navegador.mensajeInformativo("Cargar asignaciones", "El periodo " + periodoArchivo + " no coincide.\nNo se puede cargar el archivo.");
                    return null;
                }
                
                CentroDriver entidad = lstEntidades.stream().filter(item -> codigoEntidad.equals(item.getCodigoCentro())).findAny().orElse(null);
                String driver = lstDrivers.stream().filter(item -> codigoDriver.equals(item)).findAny().orElse(null);

                CentroDriver linea = new CentroDriver(periodo,codigoEntidad,nombreEntidad,codigoDriver,nombreDriver,true);
                if (entidad != null  && driver!=null) {
                    listaCargar.add(linea);
                } else {
                    linea.setFlagCargar(false);
                }
                lista.add(linea);
            }
            //cerramos el libro
            f.close();
            libro.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        return lista;
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR);
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        findError = false;
        if(tabAsignaciones.getItems().isEmpty()){
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_EMPTY);
        }else {
            if(listaCargar.isEmpty()){
                menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_UPLOAD_ITEM_DONTEXIST);
            }else{
                asignacionEntidadDriverDAO.insertarListaAsignaciones(listaCargar, periodoSeleccionado, menuControlador.repartoTipo);
                crearReporteLOG();
                if(findError == true){
                    menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_UPLOAD_SUCCESS_ERROR);
                }else {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_SUCCESS);
                }
                btnDescargarLog.setVisible(true);
            }
        }
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_CENTROOBJETOS_DRIVER.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabAsignaciones.getItems().forEach((CentroDriver item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarLineaArchivo("Se agregó item "+ item.getCodigoDriver()+ " a (" +item.getCodigoCentro()+")"+ " en "+ titulo +" correctamente.");
                menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), item.getCodigoDriver()+ " a (" +item.getCodigoCentro()+")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR.getDireccion());
            }
            else{
                menuControlador.Log.agregarLineaArchivo("No se agregó item "+ item.getCodigoDriver()+ " a (" +item.getCodigoCentro()+")"+ " en "+titulo+", debido a que no existe algún valor en su respectivo catálogo" );
                findError = true;
            }
        });
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
    }  
}
