package controlador.aprovisionamiento.balancete;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import dao.DetalleGastoDAO;
import dao.PlanDeCuentaDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import modelo.AsignacionPartidaCuenta;
import modelo.DetalleGasto;
import modelo.Centro;
import modelo.CuentaContable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkBalancete;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    @FXML private JFXButton btnDescargarLog;
    
    @FXML private TableView<DetalleGasto> tabListar;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoCuentaContable;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombreCuentaContable;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoPartida;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombrePartida;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoCentro;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombreCentro;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto01;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto02;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto03;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto04;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto05;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto06;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto07;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto08;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto09;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto10;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto11;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto12;
    @FXML private TableColumn<DetalleGasto, Boolean> tabcolEstado;
    
    @FXML private Button btnCancelar;
    @FXML private Button btnSubir;
    @FXML private Label lblNumeroCheck;
    @FXML private Label lblNumeroWarning;
    @FXML private Label lblNumeroError;  
    
    List<DetalleGasto> listaCargar = new ArrayList() ;

    public MenuControlador menuControlador;
    public DetalleGastoDAO detalleGastoDAO;
    CentroDAO centroDAO;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_BALANCETE_CARGAR.getControlador());
    String titulo;
    String logName;
    Boolean findError;
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        detalleGastoDAO = new DetalleGastoDAO();
        centroDAO = new CentroDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
        titulo = "Detalle de Gasto";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (menuControlador.repartoTipo == 1) {
            tabcolMonto01.setText("MONTO");
            tabListar.getColumns().remove(tabcolMonto02);
            tabListar.getColumns().remove(tabcolMonto03);
            tabListar.getColumns().remove(tabcolMonto04);
            tabListar.getColumns().remove(tabcolMonto05);
            tabListar.getColumns().remove(tabcolMonto06);
            tabListar.getColumns().remove(tabcolMonto07);
            tabListar.getColumns().remove(tabcolMonto08);
            tabListar.getColumns().remove(tabcolMonto09);
            tabListar.getColumns().remove(tabcolMonto10);
            tabListar.getColumns().remove(tabcolMonto11);
            tabListar.getColumns().remove(tabcolMonto12);
        }
        /*
        // tabla dimensiones
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoCuentaContable.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreCuentaContable.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigoPartida.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombrePartida.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigoCECO.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreCECO.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolSaldo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolEstado.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        // tabla formato
        tabcolCodigoCuentaContable.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaContableProperty());
        tabcolNombreCuentaContable.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaContableProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        tabcolCodigoCECO.setCellValueFactory(cellData -> cellData.getValue().codigoCECOProperty());
        tabcolNombreCECO.setCellValueFactory(cellData -> cellData.getValue().nombreCECOProperty());
        tabcolSaldo.setCellValueFactory(cellData -> cellData.getValue().saldoProperty().asObject());
        tabcolSaldo.setCellFactory(column -> {
                return new TableCell<DetalleGasto, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%,.2f", item));
                    }
                }
            };
        });
//        Estado
        tabcolEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        tabcolEstado.setCellFactory(column -> {
            return new TableCell<DetalleGasto, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        if (item) {
                            setText("CORRECTO");
                            setTextFill(Color.GREEN);
                        } else {
                            setText("INCORRECTO");
                            setTextFill(Color.RED);
                        }
                    }
                }
            };
        });
        
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        */
        btnDescargarLog.setVisible(false);
    }
    
    // Acción de la pestaña 'Inicio'
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    // Acción de la pestaña 'Aprovisionamiento'
    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }

    // Acción de la pestaña 'Balancete'
    @FXML void lnkBalanceteAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
    
    // Acción de la pestaña 'Cargar'
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_CARGAR);
    }
    
    // Acción del botón con ícono de folder
    @FXML void btnCargarRutaAction(ActionEvent event) throws IOException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir archivo");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnDescargarLog.setVisible(false);
            txtRuta.setText(archivoSeleccionado.getName());
            
            List<DetalleGasto> lista;
            if (menuControlador.repartoTipo == 1) {
                lista = leerArchivoReal(archivoSeleccionado.getAbsolutePath(), menuControlador.repartoTipo);
            } else {
                lista = leerArchivoPresupuesto(archivoSeleccionado.getAbsolutePath(), menuControlador.repartoTipo);
            }
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroCheck.setText("Cantidad de registros a cargar: " + lista.size());
                cmbMes.setDisable(true);
                spAnho.setDisable(true);
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
                lblNumeroCheck.setText("Cantidad de registros a cargar: " + 0);
            }
        }
    }
    
    private List<DetalleGasto> leerArchivoPresupuesto(String rutaArchivo, int repartoTipo) {
        List<DetalleGasto> lista = new ArrayList();
        List<DetalleGasto> listaError = new ArrayList();
        List<String> listacodigosCuentaPeriodo = detalleGastoDAO.listarCodigosCuenta_CuentaPartida(periodoSeleccionado, repartoTipo);
        List<String> listaCentroPeriodo = centroDAO.listarCodigosPeriodo(periodoSeleccionado, repartoTipo);
        
        try (FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook wb = new XSSFWorkbook(f);){
            String hojaNombre = "Data_EPS_PPS";
            XSSFSheet hoja = wb.getSheet(hojaNombre);
            if (hoja == null) {
                menuControlador.navegador.mensajeError("Cargar archivo", String.format("No existe la hoja '%s'. No se puede cargar el archivo.", hojaNombre));
                return null;
            }
            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("cod cta contable","codpartida","Cuenta","Partida","Codigo CCs","Nombre CCs","M_Enero 2020","M_Febrero 2020","M_Marzo 2020","M_Abril 2020","M_Mayo 2020","M_Junio 2020","M_Julio 2020","M_Agosto 2020","M_Septiembre 2020","M_Octubre 2020","M_Noviembre 2020","M_Diciembre 2020")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                if(periodo == 0){
                    break;
                }
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCuentaContable = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCuentaContable = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoPartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombrePartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCECO = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCECO = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);
                double saldo = Double.valueOf(celda.getStringCellValue());
                
                // Valida que los items del archivo tengan el periodo correcto
                // De no cumplirlo, cancela la previsualización.
                if(periodo != periodoSeleccionado){
                    menuControlador.navegador.mensajeError(menuControlador.MENSAJE_UPLOAD_ERROR_PERIODO);
                    lista.clear();
                    listaError.clear();
                    listacodigosCuentaPeriodo.clear();
                    
                    listaCentroPeriodo.clear();
                    txtRuta.setText("");
                    break;
                }
                DetalleGasto cuentaLeida = new DetalleGasto(periodo, codigoCuentaContable, nombreCuentaContable, codigoPartida, nombrePartida, codigoCECO, nombreCECO, saldo, true);                
                List<String> listacodigosPartidaPeriodo = detalleGastoDAO.listarCodigosPartidas_CuentaPartida(codigoCuentaContable,periodoSeleccionado);
                // Verifica que exista la cuenta para poder agregarla
                String cuenta = listacodigosCuentaPeriodo.stream().filter(item -> codigoCuentaContable.equals(item)).findAny().orElse(null);
                String partida = listacodigosPartidaPeriodo.stream().filter(item -> codigoPartida.equals(item)).findAny().orElse(null);
                String centro = listaCentroPeriodo.stream().filter(item -> codigoCECO.equals(item)).findAny().orElse(null);
                if (cuenta != null && partida!=null && centro != null) {
                    listaCargar.add(cuentaLeida);
                } else {
                    // >>>agregar linea para log sobre el error
                    cuentaLeida.setEstado(false);
                    listaError.add(cuentaLeida);                    
                }
                
                lista.add(cuentaLeida);
                listacodigosPartidaPeriodo.clear();
            }
            wb.close();
            f.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        lblNumeroCheck.setText("Cuentas posibles a cargar: " + (lista.size()-listaError.size()));
//
//        if (listaCuentas.size() > 0) {
//            lblNumeroWarning.setText("Cuentas contables pendientes: " + listaCuentas.size());
//        }
//        if (listaError.size() > 0) {
//            lblNumeroError.setText("Cuentas contables no encontradas: " + listaError.size());
//        }
        return lista;
    }
    
    private List<DetalleGasto> leerArchivoReal(String rutaArchivo, int repartoTipo) {
        List<DetalleGasto> lista = new ArrayList();
        List<DetalleGasto> listaError = new ArrayList();
        List<String> listacodigosCuentaPeriodo = detalleGastoDAO.listarCodigosCuenta_CuentaPartida(periodoSeleccionado, repartoTipo);
        
        List<String> listaCentroPeriodo = centroDAO.listarCodigosPeriodo(periodoSeleccionado, repartoTipo);
            
        try (FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook wb = new XSSFWorkbook(f);){
            String hojaNombre = "Data_EPS_PPS";
            XSSFSheet hoja = wb.getSheet(hojaNombre);
            if (hoja == null) {
                menuControlador.navegador.mensajeError("Cargar archivo", String.format("No existe la hoja '%s'. No se puede cargar el archivo.", hojaNombre));
                return null;
            }
            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO CUENTA CONTABLE","NOMBRE CUENTA CONTABLE","CODIGO PARTIDA","NOMBRE PARTIDA","CODIGO CENTRO COSTO","NOMBRE CENTRO COSTO","SALDO")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                if(periodo == 0){
                    break;
                }
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCuentaContable = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCuentaContable = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoPartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombrePartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCECO = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCECO = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);
                double saldo = Double.valueOf(celda.getStringCellValue());
                
                // Valida que los items del archivo tengan el periodo correcto
                // De no cumplirlo, cancela la previsualización.
                if(periodo != periodoSeleccionado){
                    menuControlador.navegador.mensajeError(menuControlador.MENSAJE_UPLOAD_ERROR_PERIODO);
                    lista.clear();
                    listaError.clear();
                    listacodigosCuentaPeriodo.clear();
                    
                    listaCentroPeriodo.clear();
                    txtRuta.setText("");
                    break;
                }
                DetalleGasto cuentaLeida = new DetalleGasto(periodo, codigoCuentaContable, nombreCuentaContable, codigoPartida, nombrePartida, codigoCECO, nombreCECO, saldo, true);                
                List<String> listacodigosPartidaPeriodo = detalleGastoDAO.listarCodigosPartidas_CuentaPartida(codigoCuentaContable,periodoSeleccionado);
                // Verifica que exista la cuenta para poder agregarla
                String cuenta = listacodigosCuentaPeriodo.stream().filter(item -> codigoCuentaContable.equals(item)).findAny().orElse(null);
                String partida = listacodigosPartidaPeriodo.stream().filter(item -> codigoPartida.equals(item)).findAny().orElse(null);
                String centro = listaCentroPeriodo.stream().filter(item -> codigoCECO.equals(item)).findAny().orElse(null);
                if (cuenta != null && partida!=null && centro != null) {
                    listaCargar.add(cuentaLeida);
                } else {
                    // >>>agregar linea para log sobre el error
                    cuentaLeida.setEstado(false);
                    listaError.add(cuentaLeida);                    
                }
                
                lista.add(cuentaLeida);
                listacodigosPartidaPeriodo.clear();
            }
            wb.close();
            f.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        lblNumeroCheck.setText("Cuentas posibles a cargar: " + (lista.size()-listaError.size()));
//
//        if (listaCuentas.size() > 0) {
//            lblNumeroWarning.setText("Cuentas contables pendientes: " + listaCuentas.size());
//        }
//        if (listaError.size() > 0) {
//            lblNumeroError.setText("Cuentas contables no encontradas: " + listaError.size());
//        }
        return lista;
    }
    
    // Acción del botón 'Subir'
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_EMPTY);
        }else {
            if(listaCargar.isEmpty()){
                menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_UPLOAD_ITEM_DONTEXIST);
                detalleGastoDAO.insertarDetalleGasto(periodoSeleccionado,listaCargar);
                crearReporteLOG();
                if(findError == true){
                    menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_UPLOAD_SUCCESS_ERROR);
                }else {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_SUCCESS);
                }
                btnDescargarLog.setVisible(true);
            }else{
                detalleGastoDAO.insertarDetalleGasto(periodoSeleccionado,listaCargar);
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
    
    // Acción del botón 'Cancelar'
    @FXML void btnCancelarAction(ActionEvent event) throws SQLException {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_DETALLE_GASTO.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((item)->{
            if(item.getEstado()){
                menuControlador.Log.agregarLineaArchivo("Se creó item ( " + item.getCodigoCuentaContable() + ", " + item.getCodigoPartida() + ", " + item.getCodigoCECO() + " ) en "+ titulo+" correctamente.");
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), " ( " + item.getCodigoCuentaContable() + ", " + item.getCodigoPartida() + ", " + item.getCodigoCECO() + " ) " , Navegador.RUTAS_BALANCETE_CARGAR.getDireccion());
            }
            else{
                menuControlador.Log.agregarLineaArchivo("No se creó item "+ " ( " + item.getCodigoCuentaContable() + ", " + item.getCodigoPartida() + ", " + item.getCodigoCECO() + " ) " + " en Balancete, debido a que no existe en Cuentas Contables.");
                findError = true;
            }
        });
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
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
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
}
