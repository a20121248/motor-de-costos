package controlador.parametrizacion.driver_entidad.centros_objetos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import dao.CentroDriverDAO;
import dao.DetalleGastoDAO;
import dao.DriverDAO;
import dao.TipoDAO;
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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import modelo.CentroDriver;
import modelo.Tipo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private HBox hbPeriodo;    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;

    @FXML private TableView<CentroDriver> tabCargar;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolGrupoGasto;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreDriver;

    @FXML private Label lblNumeroRegistros;
    @FXML private JFXButton btnDescargarLog;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    CentroDAO centroDAO;
    DriverDAO driverDAO;
    CentroDriverDAO centroDriverDAO;
    DetalleGastoDAO detalleGastoDAO;
    
    List<CentroDriver> listaCargar;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_CARGAR.getControlador());
    String logName;
    String titulo;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        driverDAO = new DriverDAO();
        detalleGastoDAO = new DetalleGastoDAO();
        centroDriverDAO = new CentroDriverDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        listaCargar = new ArrayList();
        titulo = "Asignar Driver Objeto a CECO ";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Periodo seleccionado
        if (menuControlador.repartoTipo == 1)
            periodoSeleccionado = (int) menuControlador.objeto;
        else
            periodoSeleccionado = (int) menuControlador.objeto / 100 * 100;
        
        // Mes seleccionado
        if (menuControlador.repartoTipo == 1) {
            cmbMes.getItems().addAll(menuControlador.lstMeses);
            cmbMes.getSelectionModel().select(periodoSeleccionado % 100 - 1);
            cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    if (menuControlador.repartoTipo == 1)
                        periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                    else
                        periodoSeleccionado = spAnho.getValue()*100;
                }
            });
        } else {
            hbPeriodo.getChildren().remove(cmbMes);
        }
        
        // Seleccionar anho
        spAnho.getValueFactory().setValue(periodoSeleccionado / 100);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (menuControlador.repartoTipo == 1)
                    periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                else
                    periodoSeleccionado = spAnho.getValue()*100;
            }
        });
        
        // Dimensiones de la tabla
        tabCargar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoCentro.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreCentro.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        tabcolGrupoGasto.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolCodigoDriver.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreDriver.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        // Formato de la tabla
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolGrupoGasto.setCellValueFactory(cellData -> cellData.getValue().getGrupoGasto().nombreProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().codigoDriverProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().nombreDriverProperty());

        // Ocultar descarga de Log
        btnDescargarLog.setVisible(false);
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkAsignacionesAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar " + titulo);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            cmbMes.setDisable(true);
            spAnho.setDisable(true);
            List<CentroDriver> lista = leerArchivo(archivoSeleccionado.getAbsolutePath(), periodoSeleccionado, menuControlador.repartoTipo);
            if (lista != null) {
                tabCargar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<CentroDriver> leerArchivo(String rutaArchivo, int periodo, int repartoTipo) {
        TipoDAO tipoDAO = new TipoDAO();
        List<CentroDriver> lista = new ArrayList();
        List<CentroDriver> lstEntidades = centroDAO.listarCentrosObjetosConDriver(periodo, "-", repartoTipo, -1);
        List<String> lstDrivers = driverDAO.listarCodigosDriverObjetosPeriodo(periodo, repartoTipo, "OBCO");
        List<Tipo> listaGrupoGasto = tipoDAO.listarGrupoGastos();
        
        try {
            FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f);
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            //Estructura de la cabecera
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO CENTRO","NOMBRE CENTRO","GRUPO GASTO","CODIGO DRIVER","NOMBRE DRIVER")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodoLeido = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCentro = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCentro = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String grupoGasto = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoDriver = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreDriver = celda.getStringCellValue();

                if (periodo != periodoLeido) {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_ERROR_PERIODO);
                    lista.clear();
                    txtRuta.setText("");
                    return null;
                }
                
                // Validar la existencia del centro
                CentroDriver centro = lstEntidades.stream().filter(item -> codigoCentro.equals(item.getCodigoCentro()) && grupoGasto.equals(item.getGrupoGasto().getCodigo())).findAny().orElse(null);
                // Validar la existencia del Driver en periodo leido a cargar
                String driver = lstDrivers.stream().filter(item -> codigoDriver.equals(item)).findAny().orElse(null);
                // Validar la existencia del Grupo de gasto
                Tipo tipoGrupoGasto = listaGrupoGasto.stream().filter(item -> grupoGasto.equals(item.getCodigo())).findAny().orElse(null);
                
                CentroDriver linea = new CentroDriver(periodoLeido,codigoCentro,nombreCentro,tipoGrupoGasto,codigoDriver,nombreDriver, true);
                if (centro != null  && driver!=null && tipoGrupoGasto!=null) {
                    listaCargar.add(linea);
                } else {
                    String detalleError = "";
                    String repartoTipoStr = repartoTipo == 1 ? "real" : "presupuesto";
                    if (centro == null) detalleError += String.format("\n  - El centro de costos con código '%s' no existe en el periodo %d del %s.", codigoCentro, periodo, repartoTipoStr);
                    if (driver == null) detalleError += String.format("\n  - El driver con código '%s' no existe en el periodo %d del %s.", codigoDriver, periodo, repartoTipoStr);
                    if (tipoGrupoGasto == null) detalleError += String.format("\n  - El grupo de gasto con código '%s' no existe en la base de datos.", grupoGasto);
                    linea.setDetalleError(detalleError);
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
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR);
    }
      
    @FXML void btnSubirAction(ActionEvent event) {
        if (tabCargar.getItems().isEmpty()){
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_EMPTY);
        } else {
            boolean findError = crearReporteLOG();
            if (listaCargar.isEmpty()) {
                menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_UPLOAD_ITEM_DONTEXIST);
            } else {
                centroDriverDAO.insertarListaAsignacionesDriverObjeto(listaCargar,periodoSeleccionado,menuControlador.repartoTipo);
                if (findError == true) {
                    menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_UPLOAD_SUCCESS_ERROR);
                } else {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_SUCCESS);
                }
            }
            btnDescargarLog.setVisible(true);
        }
    }
    
    private boolean crearReporteLOG() {
        boolean findError = false;
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_CENTROOBJETOS_DRIVER.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        
        for (CentroDriver item: tabCargar.getItems()) {
            String mensajeStr;
            if (item.getFlagCargar()) {                
                mensajeStr = String.format("Se agregó item ('%s', '%s') en %s correctamente.", item.getCodigoDriver(), item.getCodigoCentro(), titulo);
                menuControlador.Log.agregarLineaArchivo(mensajeStr);
                
                mensajeStr = String.format("('%s', '%s')", item.getCodigoDriver(), item.getCodigoCentro());
                menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), mensajeStr, periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR.getDireccion());
            }
            else {
                findError = true;                
                mensajeStr = String.format("No se agregó el item ('%s', '%s') en %s, debido a los siguientes errores: %s", item.getCodigoDriver(), item.getCodigoCentro(), titulo, item.getDetalleError());
                menuControlador.Log.agregarLineaArchivo(mensajeStr);
            }
        }
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        return findError;
    }  
}
