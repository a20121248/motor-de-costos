package controlador.parametrizacion.driver_entidad.centros_bolsas;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import dao.CentroDriverDAO;
import dao.DetalleGastoDAO;
import dao.DriverDAO;
import dao.PartidaDAO;
import dao.PlanDeCuentaDAO;
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

    @FXML private TableView<CentroDriver> tabListar;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCuentaContable;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCuentaContable;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoPartida;
    @FXML private TableColumn<CentroDriver, String> tabcolNombrePartida;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreDriver;
    
    @FXML private Label lblNumeroRegistros;
    @FXML private JFXButton btnDescargarLog;    
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    PlanDeCuentaDAO cuentaDAO;
    PartidaDAO partidaDAO;
    CentroDAO centroDAO;
    DriverDAO driverDAO;
    CentroDriverDAO centroDriverDAO;
    DetalleGastoDAO detalleGastoDAO;
    
    List<CentroDriver> listaCargar;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_CARGAR.getControlador());
    String logName;
    String titulo;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        cuentaDAO = new PlanDeCuentaDAO();
        partidaDAO = new PartidaDAO();
        centroDAO = new CentroDAO();
        driverDAO = new DriverDAO();
        detalleGastoDAO = new DetalleGastoDAO();
        centroDriverDAO = new CentroDriverDAO();
        listaCargar = new ArrayList();
        titulo = "Asignar Driver a Centro Bolsa";
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
        
        // Formato de la tabla
        tabcolCodigoCuentaContable.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaProperty());
        tabcolNombreCuentaContable.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_CARGAR);
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
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<CentroDriver> leerArchivo(String rutaArchivo, int periodo, int repartoTipo) {
        List<CentroDriver> lista = new ArrayList();
        List<String> lstCodigosCuentaPeriodo = cuentaDAO.listarCodigosPeriodo(periodo, repartoTipo);
        List<String> lstCodigosPartidaPeriodo = partidaDAO.listarCodigosPeriodo(periodo, repartoTipo);
        List<String> lstCodigosCentros = centroDAO.listarCodigosCentrosPeriodo(periodo, repartoTipo, Arrays.asList("BOLSA", "OFICINA"));
        List<String> lstCodigosDrivers = driverDAO.listarCodigosDriverPeriodo(periodo, repartoTipo, "CECO");
        
        try {
            FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f);
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            // Estructura de la cabecera
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO CUENTA CONTABLE","NOMBRE CUENTA CONTABLE","CODIGO PARTIDA","NOMBRE PARTIDA","CODIGO CENTRO","NOMBRE CENTRO","CODIGO DRIVER","NOMBRE DRIVER")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodoLeido = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCuenta = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCuenta = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoPartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombrePartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCentro = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCentro = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoDriver = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreDriver = celda.getStringCellValue();

                if (periodo != periodoLeido) {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_ERROR_PERIODO);
                    lista.clear();
                    txtRuta.setText("");
                    return null;
                }
                
                // Verifica que exista la cuenta para poder agregarla
                String cuenta = lstCodigosCuentaPeriodo.stream().filter(item -> codigoCuenta.equals(item)).findAny().orElse(null);
                String partida = lstCodigosPartidaPeriodo.stream().filter(item -> codigoPartida.equals(item)).findAny().orElse(null);
                String centro = lstCodigosCentros.stream().filter(item -> codigoCentro.equals(item)).findAny().orElse(null);
                // Validar la existencia del Driver en periodoLeido a Cargar
                String driver = lstCodigosDrivers.stream().filter(item -> codigoDriver.equals(item)).findAny().orElse(null);
                CentroDriver linea = new CentroDriver(periodoLeido, codigoCuenta, nombreCuenta, codigoPartida, nombrePartida, codigoCentro, nombreCentro, codigoDriver, nombreDriver, true);
                if (cuenta!=null && cuenta.charAt(3)=='1' && partida != null && centro!=null && driver!=null) {
                    listaCargar.add(linea);
                } else {
                    String detalleError = "";
                    String repartoTipoStr = repartoTipo == 1 ? "real" : "presupuesto";
                    if (cuenta == null)
                        detalleError += String.format("\n  - La cuenta contable con código '%s' no existe en el periodo %d del %s.", codigoCuenta, periodo, repartoTipoStr);
                    else if (cuenta.charAt(3)!='1')
                        detalleError += String.format("\n  - La cuenta contable con código '%s' del periodo %d del %s no está en soles.", codigoCuenta, periodo, repartoTipoStr);
                    if (partida == null) detalleError += String.format("\n  - La partida con código '%s' no existe en el periodo %d del %s.", codigoPartida, periodo, repartoTipoStr);
                    if (centro == null) detalleError += String.format("\n  - El centro de costos con código '%s' no existe en el periodo %d del %s.", codigoCentro, periodo, repartoTipoStr);
                    if (driver == null) detalleError += String.format("\n  - El driver con código '%s' no existe en el periodo %d del %s.", codigoDriver, periodo, repartoTipoStr);
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
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR);
    }
      
    @FXML void btnSubirAction(ActionEvent event) {
        if (tabListar.getItems().isEmpty()) {
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_EMPTY);
        } else {
            boolean findError = crearReporteLOG();
            if (listaCargar.isEmpty()) {
                menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_UPLOAD_ITEM_DONTEXIST);
            } else {
                centroDriverDAO.insertarListaAsignacionesDriverBolsa(listaCargar,periodoSeleccionado,menuControlador.repartoTipo);
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
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_CENTROBOLSAS_DRIVER.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        for (CentroDriver item: tabListar.getItems()) {
            String mensajeStr;
            if (item.getFlagCargar()) {
                mensajeStr = String.format("Se agregó item ('%s', '%s' , '%s', '%s') en %s correctamente.", item.getCodigoCuenta(), item.getCodigoPartida(), item.getCodigoCentro(), item.getCodigoDriver(), titulo);
                menuControlador.Log.agregarLineaArchivo(mensajeStr);
                
                mensajeStr = String.format("('%s', '%s', '%s', '%s')", item.getCodigoCuenta(), item.getCodigoPartida(), item.getCodigoCentro(), item.getCodigoDriver());
                menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), mensajeStr, periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_CARGAR.getDireccion());
            } else {
                findError = true;
                mensajeStr = String.format("No se agregó el item ('%s', '%s', '%s', '%s') en %s, debido a los siguientes errores: %s", item.getCodigoCuenta(), item.getCodigoPartida(), item.getCodigoCentro(), item.getCodigoDriver(), titulo, item.getDetalleError());
                menuControlador.Log.agregarLineaArchivo(mensajeStr);
            }
        }
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        return findError;
    }
}
