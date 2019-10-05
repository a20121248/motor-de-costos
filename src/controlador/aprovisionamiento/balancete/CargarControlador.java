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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import modelo.DetalleGasto;
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
    
    @FXML private HBox hbPeriodo;
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
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
    
    @FXML private Label lblNumeroRegistros;
    @FXML private JFXButton btnDescargarLog;
    
    @FXML private Button btnAtras;
    @FXML private Button btnSubir;
    
    List<DetalleGasto> listaCargar = new ArrayList();

    public MenuControlador menuControlador;
    public DetalleGastoDAO detalleGastoDAO;
    CentroDAO centroDAO;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_BALANCETE_CARGAR.getControlador());
    String titulo;
    String logName;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        detalleGastoDAO = new DetalleGastoDAO();
        centroDAO = new CentroDAO();
        if (menuControlador.repartoTipo == 1)
            periodoSeleccionado = (int) menuControlador.objeto;
        else
            periodoSeleccionado = (int) menuControlador.objeto / 100 * 100;
        titulo = "Detalle de Gasto";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        } else {
            hbPeriodo.getChildren().remove(cmbMes);
        }
        // meses
        spAnho.getValueFactory().setValue(periodoSeleccionado / 100);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (menuControlador.repartoTipo == 1)
                    periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                else
                    periodoSeleccionado = spAnho.getValue()*100;
            }
        });
        btnDescargarLog.setVisible(false);
        
        // tabla formato
        tabcolCodigoCuentaContable.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaContableProperty());
        tabcolNombreCuentaContable.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaContableProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolMonto01.setCellValueFactory(cellData -> cellData.getValue().monto01Property().asObject());
        tabcolMonto01.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
        if (menuControlador.repartoTipo == 2) {
            tabcolMonto02.setCellValueFactory(cellData -> cellData.getValue().monto02Property().asObject());
            tabcolMonto03.setCellValueFactory(cellData -> cellData.getValue().monto03Property().asObject());
            tabcolMonto04.setCellValueFactory(cellData -> cellData.getValue().monto04Property().asObject());
            tabcolMonto05.setCellValueFactory(cellData -> cellData.getValue().monto05Property().asObject());
            tabcolMonto06.setCellValueFactory(cellData -> cellData.getValue().monto06Property().asObject());
            tabcolMonto07.setCellValueFactory(cellData -> cellData.getValue().monto07Property().asObject());
            tabcolMonto08.setCellValueFactory(cellData -> cellData.getValue().monto08Property().asObject());
            tabcolMonto09.setCellValueFactory(cellData -> cellData.getValue().monto09Property().asObject());
            tabcolMonto10.setCellValueFactory(cellData -> cellData.getValue().monto10Property().asObject());
            tabcolMonto11.setCellValueFactory(cellData -> cellData.getValue().monto11Property().asObject());
            tabcolMonto12.setCellValueFactory(cellData -> cellData.getValue().monto12Property().asObject());
            
            tabcolMonto02.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto03.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto04.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto05.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto06.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto07.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto08.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto09.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto10.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto11.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
            tabcolMonto12.setCellFactory(column -> {return new TableCell<DetalleGasto, Double>() {@Override protected void updateItem(Double item, boolean empty) {super.updateItem(item, empty);if (item == null || empty) {setText(null);setStyle("");} else {setText(String.format("%,.2f", item));}}};});
        }
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
            
            List<DetalleGasto> lista = leerArchivo(archivoSeleccionado.getAbsolutePath(), menuControlador.repartoTipo);
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros: " + lista.size());
                if (menuControlador.repartoTipo == 1) cmbMes.setDisable(true);
                spAnho.setDisable(true);
            } else {
                txtRuta.setText("");
                lblNumeroRegistros.setText("Número de registros: " + 0);
                if (menuControlador.repartoTipo == 1) cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<DetalleGasto> leerArchivo(String rutaArchivo, int repartoTipo) {
        List<DetalleGasto> lista = new ArrayList();
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
            filas.next();
            /*if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("cod cta contable","codpartida","Cuenta","Partida","Codigo CCs","Nombre CCs","M_Enero 2020","M_Febrero 2020","M_Marzo 2020","M_Abril 2020","M_Mayo 2020","M_Junio 2020","M_Julio 2020","M_Agosto 2020","M_Septiembre 2020","M_Octubre 2020","M_Noviembre 2020","M_Diciembre 2020")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }*/
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCuentaContable = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoPartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCuentaContable = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombrePartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCentro = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCentro = celda.getStringCellValue();
                
                celda = celdas.next();celda.setCellType(CellType.STRING);double monto01 = Double.valueOf(celda.getStringCellValue());
                DetalleGasto cuentaLeida;
                if (repartoTipo == 1) {
                    cuentaLeida = new DetalleGasto(codigoCuentaContable, nombreCuentaContable, codigoPartida, nombrePartida, codigoCentro, nombreCentro, monto01, true);
                } else {
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto02 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto03 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto04 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto05 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto06 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto07 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto08 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto09 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto10 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto11 = Double.valueOf(celda.getStringCellValue());
                    celda = celdas.next();celda.setCellType(CellType.STRING);double monto12 = Double.valueOf(celda.getStringCellValue());
                    cuentaLeida = new DetalleGasto(codigoCuentaContable, nombreCuentaContable, codigoPartida, nombrePartida, codigoCentro, nombreCentro, monto01, monto02, monto03, monto04, monto05, monto06, monto07, monto08, monto09, monto10, monto11, monto12, true);
                }                
                List<String> listacodigosPartidaPeriodo = detalleGastoDAO.listarCodigosPartidas_CuentaPartida(codigoCuentaContable, periodoSeleccionado);
                // Verifica que exista la cuenta para poder agregarla
                String cuenta = listacodigosCuentaPeriodo.stream().filter(item -> codigoCuentaContable.equals(item)).findAny().orElse(null);
                String partida = listacodigosPartidaPeriodo.stream().filter(item -> codigoPartida.equals(item)).findAny().orElse(null);
                String centro = listaCentroPeriodo.stream().filter(item -> codigoCentro.equals(item)).findAny().orElse(null);
                if (cuenta!=null && partida!=null && centro!=null) {
                    listaCargar.add(cuentaLeida);
                } else {
                    String detalleError = "";
                    String repartoTipoStr = menuControlador.repartoTipo == 1 ? "real" : "presupuesto";
                    if (cuenta == null) detalleError += String.format("\n  - La cuenta contable con código '%s' no existe en el periodo %d del %s.", codigoCuentaContable, periodoSeleccionado, repartoTipoStr);
                    if (partida == null) detalleError += String.format("\n  - La partida con código '%s' no existe en el periodo %d del %s.", codigoPartida, periodoSeleccionado, repartoTipoStr);
                    if (centro == null) detalleError += String.format("\n  - El centro de costos con código '%s' no existe en el periodo %d del %s.", codigoCentro, periodoSeleccionado, repartoTipoStr);
                    cuentaLeida.setDetalleError(detalleError);
                    cuentaLeida.setEstado(false);
                }
                lista.add(cuentaLeida);
            }
            wb.close();
            f.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        lblNumeroRegistros.setText("Número de registros: " + lista.size());
        return lista;
    }
    
    // Acción del botón 'Subir'
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        if (tabListar.getItems().isEmpty()){
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_EMPTY);
        } else {
            boolean findError = crearReporteLOG();
            if (listaCargar.isEmpty()){
                menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_UPLOAD_ITEM_DONTEXIST);
            } else {
                detalleGastoDAO.insertarDetalleGasto(periodoSeleccionado, listaCargar, menuControlador.repartoTipo);
                if (findError == true) {
                    menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_UPLOAD_SUCCESS_ERROR);
                } else {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_SUCCESS);
                }
            }
            btnDescargarLog.setVisible(true);
        }
    }
    
    private boolean crearReporteLOG(){
        boolean findError = false;
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_DETALLE_GASTO.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        for (DetalleGasto item: tabListar.getItems()) {
            String mensajeStr;
            if(item.getEstado()) {
                mensajeStr = String.format("Se creó el item (%s, %s, %s) en %s correctamente.", item.getCodigoCuentaContable(), item.getCodigoPartida(), item.getCodigoCentro(), titulo);
                menuControlador.Log.agregarLineaArchivo(mensajeStr);
                
                mensajeStr = String.format("(%s, %s, %s)", item.getCodigoCuentaContable(), item.getCodigoPartida(), item.getCodigoCentro());
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), mensajeStr, Navegador.RUTAS_BALANCETE_CARGAR.getDireccion());
            } else {
                findError = true;
                mensajeStr = String.format("No se creó el item (%s, %s, %s) en %s, debido a los siguientes errores: %s", item.getCodigoCuentaContable(), item.getCodigoPartida(), item.getCodigoCentro(), titulo, item.getDetalleError());
                menuControlador.Log.agregarLineaArchivo(mensajeStr);
            }
        }
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        return findError;
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

    // Acción del botón 'Cancelar'    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
}
