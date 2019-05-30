package controlador.parametrizacion.cuenta_partida;


import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PartidaDAO;
import dao.PlanDeCuentaDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.CargarCuentaPartidaLinea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkAsignaciones;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    @FXML private JFXButton btnDescargarLog;
    
    @FXML private TableView<CargarCuentaPartidaLinea> tabListar;
    @FXML private TableColumn<CargarCuentaPartidaLinea, String> tabcolCodigoCuenta;
    @FXML private TableColumn<CargarCuentaPartidaLinea, String> tabcolNombreCuenta;
    @FXML private TableColumn<CargarCuentaPartidaLinea, String> tabcolCodigoPartida;
    @FXML private TableColumn<CargarCuentaPartidaLinea, String> tabcolNombrePartida;
    
    @FXML private Button btnCancelar;
    @FXML private Button btnSubir;
    @FXML private Label lblNumeroRegistros;
    
    // Variables de la aplicacion
    PartidaDAO partidaDAO;
    PlanDeCuentaDAO planDeCuentaDAO ;
    public MenuControlador menuControlador;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CUENTA_PARTIDA_CARGAR.getControlador());
    String titulo;
    List<CargarCuentaPartidaLinea> listaCargar = new ArrayList() ;
    String logName;
    Boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        partidaDAO = new PartidaDAO();
        planDeCuentaDAO = new PlanDeCuentaDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
        this.titulo = "Asignación";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla formato
        tabcolCodigoCuenta.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaContableProperty());
        tabcolNombreCuenta.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaContableProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        // tabla dimensiones
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigoCuenta.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        tabcolNombreCuenta.setMaxWidth( 1f * Integer.MAX_VALUE * 35);
        tabcolCodigoPartida.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        tabcolNombrePartida.setMaxWidth( 1f * Integer.MAX_VALUE * 35);        
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

    @FXML void lnkAsignarAction(ActionEvent event) {
        menuControlador.objeto = "Todos";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CUENTA_PARTIDA_LISTAR);
    }

    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CUENTA_PARTIDA_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("Abrir asignaciones");
        File archivoSeleccionado = file_chooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<CargarCuentaPartidaLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            tabListar.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
        }
    }
    
    private List<CargarCuentaPartidaLinea> leerArchivo(String rutaArchivo) {
        List<CargarCuentaPartidaLinea> lista = new ArrayList();
        List<String> listaCodigosPartidaPeriodo = partidaDAO.listarCodigosPeriodo(periodoSeleccionado);
        List<String> listaCodigosCuentaPeriodo = planDeCuentaDAO.listarCodigosPeriodo(periodoSeleccionado);
        try {
            FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f);
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila = null;
            Cell celda = null;
            //int numFilasOmitir = 2
            //Estructura de la cabecera
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO CUENTA","NOMBRE CUENTA","CODIGO PARTIDA","NOMBRE PARTIDA")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa                
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCuenta = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCuenta = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoPartida = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombrePartida = celda.getStringCellValue();
                
                // Valida que los items del archivo tengan el periodo correcto
                // De no cumplirlo, cancela la previsualización.
                if(periodo != periodoSeleccionado){
                    menuControlador.navegador.mensajeError(menuControlador.MENSAJE_UPLOAD_ERROR_PERIODO);
                    lista.clear();
                    txtRuta.setText("");
                    break;
                }
                
                CargarCuentaPartidaLinea linea = new CargarCuentaPartidaLinea(periodo,codigoCuenta,nombreCuenta,codigoPartida,nombrePartida,true);
                String partida = listaCodigosPartidaPeriodo.stream().filter(item ->codigoPartida.equals(item)).findAny().orElse(null);
                String cuenta = listaCodigosCuentaPeriodo.stream().filter(item ->codigoCuenta.equals(item)).findAny().orElse(null);
                if(partida!= null && cuenta!=null){
                    listaCargar.add(linea);
                    listaCodigosPartidaPeriodo.removeIf(x -> x.equals(linea.getCodigoPartida()));
                } else {
                    // >>>agregar linea para log sobre el error
                    linea.setFlagCargar(false);
//                    listaError.add(linea);                    
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
    
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_EMPTY);
        }else {
            if(listaCargar.isEmpty()){
                menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_UPLOAD_ITEM_DONTEXIST);
            }else{
                partidaDAO.insertarPartidasCuenta(periodoSeleccionado,listaCargar,menuControlador.repartoTipo);
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
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.objeto = "Todos";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CUENTA_PARTIDA_LISTAR);
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_ASIGNACIONES_CUENTA_PARTIDA.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarLineaArchivo("Se agregó Partida "+ item.getCodigoPartida()+ " con Cuenta Contable "+ item.getCodigoCuentaContable()+" en "+ titulo +" correctamente.");
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), item.getCodigoCuentaContable() +" con "+ item.getCodigoPartida(), Navegador.RUTAS_PLANES_ASIGNAR_PERIODO_CARGAR.getDireccion());
            }
            else{
                menuControlador.Log.agregarLineaArchivo("No se agregó item "+ item.getCodigoPartida()+ " con "+item.getCodigoCuentaContable()+ " en "+titulo+"\n. Debido a que ya está asignado o no exista alguno de los items a asignar en su respectivo periodo.");
                findError = true;
            }
        });
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
    }
}

