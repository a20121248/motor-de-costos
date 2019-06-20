package controlador;

import com.jfoenix.controls.JFXButton;
import dao.TipoDAO;
import java.io.IOException;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import modelo.Tipo;
import modelo.Usuario;
import servicios.LoggingServicio;

public class MenuControlador implements Initializable {
    @FXML private ImageView imgLogo;
    @FXML public JFXButton btnInicio;
    @FXML public JFXButton btnAprovisionamiento;
    @FXML public JFXButton btnParametrizacion;
    @FXML public JFXButton btnProcesos;
    @FXML public JFXButton btnReporting;
    
    @FXML public AnchorPane apHeader;
    @FXML public AnchorPane apSidebar;
    
    @FXML public Label lblTitulo;
    @FXML private StackPane spnContenido;
        
    // variables transversales a toda la aplicacion
    FXMLLoader fxmlLoader;
    public List<String> lstMeses;
    public int periodoSeleccionado;
    public int periodo, mesActual, anhoActual;
    public int periodoAnterior, mesAnterior, anhoAnterior;
    public int repartoTipo;
    public String objetoTipo;
    final TipoDAO tipoDAO;
    public List<Tipo> lstCentroTipos, lstCentroNiveles, lstEntidadTipos, lstGrupoGasto;
    public List<String> lstEsBolsa, lstAtribuible, lstTipoGasto, lstClaseGasto;
    public String codigos;
    final Image img;
    public List<Integer> lstFases;
    public Object objeto;
    public Navegador navegador;
    public Usuario usuario;
    String nombreBD;
    public boolean verCostos;
    final static Logger LOGGER = Logger.getLogger("controlador.MenuControlador");
    public LoggingServicio Log;
    
    // =========================================================
    // *************************** MENSAJES*********************
    // =========================================================
   
    
    public final String MENSAJE_UPLOAD_HEADER = "UPLOAD_HEADER";
    public final String MENSAJE_DOWNLOAD = "DOWNLOAD";
    public final String MENSAJE_DOWNLOAD_LOG = "DOWNLOAD_LOG";
    public final String MENSAJE_DOWNLOAD_EMPTY = "DOWNLOAD_EMPTY";
    public final String MENSAJE_DOWNLOAD_CANCELED = "DOWNLOAD_CACNCELED";
    public final String MENSAJE_DELETE_EMPTY = "DELETE_EMPTY";
    public final String MENSAJE_DELETE_SELECTED = "DELETE_SELECTED";
    public final String MENSAJE_DELETE_REFRESH = "DELETE_REFRESH";
    public final String MENSAJE_DELETE_ITEM = "DELETE_ITEM";
    public final String MENSAJE_DELETE_SUCCESS = "DELETE_SUCCESS";
    public final String MENSAJE_UPDATE_EMPTY = "UPDATE_EMPTY";
    public final String MENSAJE_UPDATE_REFRESH = "UPDATE_REFRESH";
    public final String MENSAJE_UPLOAD = "UPLOAD";
    public final String MENSAJE_UPLOAD_SUCCESS = "UPLOAD_SUCCESS";
    public final String MENSAJE_UPLOAD_SUCCESS_ERROR = "UPLOAD_SUCCESS_ERROR";
    public final String MENSAJE_UPLOAD_ERROR_PERIODO = "UPLOAD_ERROR_PERIODO";
    public final String MENSAJE_UPLOAD_EMPTY = "UPLOAD_EMPTY";
    public final String MENSAJE_UPLOAD_ALLCHARGED_YET = "UPLOAD_ALLCHARGED_YET";
    public final String MENSAJE_UPLOAD_ITEM_DONTEXIST = "UPLOAD_ITEM_DONTEXIST";
    public final String MENSAJE_TABLE_EMPTY = "TABLE_EMPTY";
    public final String MENSAJE_ADD_REFRESH = "ADD_REFRESH";
    public final String MENSAJE_ADD_EMPTY = "ADD_EMPTY";
    public final String MENSAJE_SELECT_ENTITY = "SELECT_ENTITY";
    public final String MENSAJE_EDIT_EMPTY = "EDIT_EMPTY";
    public final String MENSAJE_EDIT_SUCCESS = "EDIT_SUCCESS";
    public final String MENSAJE_EDIT_ERROR = "EDIT_ERROR";
    public final String MENSAJE_CREATE_ITEM_EXIST = "CREATE_ITEM_EXIST";
    public final String MENSAJE_CREATE_SUCCESS = "CREATE_SUCCESS";
    public final String MENSAJE_CREATE_ERROR = "CREATE_ERROR";
    public final String MENSAJE_CREATE_ITEM_PATTERN = "CREATE_ITEM_PATTERN";
    public final String MENSAJE_EDIT_ITEM_WITHOUT_ALLOCATE = "EDIT_ITEM_WITHOUT_ALLOCATE";
    
    public MenuControlador(String rutaImagen, Usuario usuario, String nombreBD, String rutaLog) throws IOException {
        // Administrador de pantallas
        navegador = new Navegador(this);
        
        // Creación de Log por dia
        Format formatterLogFile = new SimpleDateFormat("yyyyMMdd");
        String fechaStr = formatterLogFile.format(new Date());
        Log = new LoggingServicio(String.format("%s_app.log",fechaStr), rutaLog);
        Log.crearLog();
        
        // Usuario logueado
        this.usuario = usuario;
        
        // Nombre de la BD
        this.nombreBD = nombreBD;
        
        // Logo de la aplicacion
        img = new Image(rutaImagen);
        
        // meses
        lstMeses = new ArrayList();
        lstMeses.add("Enero");
        lstMeses.add("Febrero");
        lstMeses.add("Marzo");
        lstMeses.add("Abril");
        lstMeses.add("Mayo");
        lstMeses.add("Junio");
        lstMeses.add("Julio");
        lstMeses.add("Agosto");
        lstMeses.add("Septiembre");
        lstMeses.add("Octubre" );
        lstMeses.add("Noviembre");
        lstMeses.add("Diciembre");
        
        // periodo actual
        Calendar cal = Calendar.getInstance();
        Date fecha = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        Date fechaAnt = cal.getTime();

        periodo = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(fecha));
        anhoActual = periodo / 100;
        mesActual = periodo % 100;
        
        periodoAnterior = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(fechaAnt));
        anhoAnterior = periodoAnterior / 100;
        mesAnterior = periodoAnterior % 100;
        
        tipoDAO = new TipoDAO();
        // repartoTipo de entidades
        lstCentroTipos = tipoDAO.listarCentroTipos();
        // repartoTipo de entidades
        lstCentroNiveles = tipoDAO.listarCentroNiveles();
        // repartoTipo de entidades
        lstEntidadTipos = tipoDAO.listarEntidadTipos();
        // grupoGastos de Partidas, Objetos-Drivers
        lstGrupoGasto = tipoDAO.listarGrupoGastos();

        //Caracteristicas de los Gastos de Centros y Cuentas Contables
        lstEsBolsa = new ArrayList();
        lstEsBolsa.add("NO");
        lstEsBolsa.add("SI");
        
        lstAtribuible = new ArrayList();
        lstAtribuible.add("Atribuible");
        lstAtribuible.add("No Atribuible");

        lstTipoGasto = new ArrayList();
        lstTipoGasto.add("Adquisición");
        lstTipoGasto.add("Mantenimiento");

        lstClaseGasto = new ArrayList();
        lstClaseGasto.add("Fijo");
        lstClaseGasto.add("Variable");
        
        //Caracteristicas Partidas
//        lstGrupoGasto = new ArrayList();
//        lstGrupoGasto.add("Gastos de Tecnología");
//        lstGrupoGasto.add("Gastos de Personal");
//        lstGrupoGasto.add("Gastos de Operaciones");
        
        // repartoTipo de fases
        lstFases = new ArrayList();
        lstFases.add(1);
        lstFases.add(2);
        lstFases.add(3);
        lstFases.add(4);
        
        verCostos = nombreBD.equals("MySQL");
        repartoTipo = 1;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgLogo.setImage(img);
        navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void btnInicioAction(ActionEvent event) {
        btnInicio.getStyleClass().add("selected");
        btnAprovisionamiento.getStyleClass().removeAll("selected");
        btnParametrizacion.getStyleClass().removeAll("selected");
        btnProcesos.getStyleClass().removeAll("selected");
        btnReporting.getStyleClass().removeAll("selected");
        navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void btnAprovisionamientoAction(ActionEvent event) {
        btnInicio.getStyleClass().removeAll("selected");
        btnAprovisionamiento.getStyleClass().add("selected");
        btnParametrizacion.getStyleClass().removeAll("selected");
        btnProcesos.getStyleClass().removeAll("selected");
        btnReporting.getStyleClass().removeAll("selected");
        navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }

    @FXML void btnParametrizacionAction(ActionEvent event) {
        btnInicio.getStyleClass().removeAll("selected");
        btnAprovisionamiento.getStyleClass().removeAll("selected");
        btnParametrizacion.getStyleClass().add("selected");
        btnProcesos.getStyleClass().removeAll("selected");
        btnReporting.getStyleClass().removeAll("selected");
        navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }

    @FXML void btnProcesosAction(ActionEvent event) {
        btnInicio.getStyleClass().removeAll("selected");
        btnAprovisionamiento.getStyleClass().removeAll("selected");
        btnParametrizacion.getStyleClass().removeAll("selected");
        btnProcesos.getStyleClass().add("selected");
        btnReporting.getStyleClass().removeAll("selected");
        navegador.cambiarVista(Navegador.RUTAS_MODULO_PROCESOS);
    }

    @FXML void btnReportingAction(ActionEvent event) {
        btnInicio.getStyleClass().removeAll("selected");
        btnAprovisionamiento.getStyleClass().removeAll("selected");
        btnParametrizacion.getStyleClass().removeAll("selected");
        btnProcesos.getStyleClass().removeAll("selected");
        btnReporting.getStyleClass().add("selected");
        navegador.cambiarVista(Navegador.RUTAS_MODULO_REPORTING);
    }
    
    public void setVista(Node node) {
        spnContenido.getChildren().setAll(node);
    }
    
    public boolean patronCodigoPartida(String codigo){
        Pattern str = Pattern.compile("[A-Z]{2}[0-9]{2}");
        return str.matcher(codigo).matches();
    }
    
    public boolean patronCodigoCuenta(String codigo){
        Pattern str = Pattern.compile("[0-9]{2}.[0-9]{1}.[0-9]{1}.[A-Z0-9]{2}.[A-Z0-9]{2}.[0-9]{2}");
        return str.matcher(codigo).matches();
    }
    
    public boolean patronCodigoCentro(String codigo){
        Pattern str = Pattern.compile("[0-9]{2}.[A-Z0-9]{2}.[A-Z0-9]{2}");
        return str.matcher(codigo).matches();
    }
}
