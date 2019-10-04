package controlador.parametrizacion.objetos.periodo;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoDAO;
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
import modelo.CargarObjetoPeriodoLinea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkAsignacion;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    @FXML private JFXButton btnDescargarLog;

    
    @FXML private TableView<CargarObjetoPeriodoLinea> tabListar;
    @FXML private TableColumn<CargarObjetoPeriodoLinea, Integer> tabcolPeriodo;
    @FXML private TableColumn<CargarObjetoPeriodoLinea, String> tabcolCodigo;
    @FXML private TableColumn<CargarObjetoPeriodoLinea, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;
    
    // Variables de la aplicacion
    ObjetoDAO objetoDAO;
    public MenuControlador menuControlador;
    int periodoSeleccionado;
    int anhoSeleccionado;
    int mesSeleccionado;
    String objetoNombre;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO.getControlador());
    String  titulo;
    List<CargarObjetoPeriodoLinea> listaCargar = new ArrayList() ;
    String logName;
    String logDetails;
    Boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnDescargarLog.setVisible(false);
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Cargar Oficinas");
                lnkObjetos.setText("Oficinas");
                objetoNombre = "Oficina";
                this.titulo = "Oficinas";
                break;
            case "BAN":
                lblTitulo.setText("Cargar Bancas");
                lnkObjetos.setText("Bancas");
                objetoNombre = "Banca";
                this.titulo = "Bancas";
                break;
            case "PRO":
                lblTitulo.setText("Cargar Productos");
                lnkObjetos.setText("Productos");
                objetoNombre = "Producto";
                this.titulo = "Productos";
                break;
            case "SCA":
                lblTitulo.setText("Cargar Subcanal");
                lnkObjetos.setText("Subcanales");
                objetoNombre = "Subcanal";
                this.titulo = "Subcanales";
                break;
            default:
                break;
        }
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            periodoSeleccionado = menuControlador.periodo-menuControlador.periodo%100;
        } else {
            periodoSeleccionado = menuControlador.periodo;
        }
        // Tabla
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolPeriodo.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        tabcolCodigo.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f*Integer.MAX_VALUE * 60);
        tabcolPeriodo.setCellValueFactory(cellData -> cellData.getValue().periodoProperty().asObject());
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // Meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        btnDescargarLog.setVisible(false);
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkObjetosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @FXML void lnkAsignacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de " + objetoNombre + "s");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnDescargarLog.setVisible(false);
            if(menuControlador.repartoTipo == 2){
                spAnho.setDisable(true);
            }
            else{
                cmbMes.setDisable(true);
                spAnho.setDisable(true);
            }
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<CargarObjetoPeriodoLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if(lista!=null)tabListar.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros leídos: " + tabListar.getItems().size());
        }
    }
    
    private List<CargarObjetoPeriodoLinea> leerArchivo(String rutaArchivo) {
        List<CargarObjetoPeriodoLinea> lista = new ArrayList();
        List<String> listaCodigos = objetoDAO.listarCodigos();
        listaCargar = new ArrayList();
        logDetails = "";
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
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO","NOMBRE")))) {
                menuControlador.mensaje.upload_header_error(titulo);
                f.close();
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                
                if(periodo != periodoSeleccionado){
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_ERROR_PERIODO);
                    lista.clear();
                    txtRuta.setText("");
                    return null;
                }
                CargarObjetoPeriodoLinea linea = new CargarObjetoPeriodoLinea(periodo,codigo,nombre,true);
                String cuenta = listaCodigos.stream().filter(item ->codigo.equals(item)).findAny().orElse(null);
                if (cuenta != null) {
                    listaCargar.add(linea);
                    listaCodigos.removeIf(x -> x.equals(linea.getCodigo()));
                    logDetails +=String.format("Se agregó item %s al periodo %d de %s. \r\n",linea.getCodigo(),periodoSeleccionado,titulo);
                } else {
                    logDetails +=String.format("No se agregó item %s al periodo %d de %s. Debido a que existen los siguientes errores:\r\n", linea.getCodigo(),periodoSeleccionado,titulo);
                    if(cuenta == null){
                        logDetails +=String.format("- No existe en Catálogo.\r\n");
                    }
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
    
    @FXML void btnSubirAction(ActionEvent event) {
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.mensaje.upload_empty();
        }else {
            if(listaCargar.isEmpty()){
                menuControlador.mensaje.upload_allCharged_now(titulo);
            }else{
                objetoDAO.insertarListaObjetoPeriodo(periodoSeleccionado,listaCargar,menuControlador.repartoTipo);
                crearReporteLOG();
                if(findError == true){
                    menuControlador.mensaje.upload_success_with_error(titulo);
                }else {
                    menuControlador.mensaje.upload_success();
                }
                btnDescargarLog.setVisible(true);
            }
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_"+titulo+".log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), item.getCodigo(), Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO_CARGAR.getDireccion().replace("/Objetos/", "/"+titulo+"/"));
            }
            else{
                findError = true;
            }
        });
        menuControlador.Log.agregarLineaArchivo(logDetails);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
    }
}
