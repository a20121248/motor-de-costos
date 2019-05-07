package controlador;

import com.jfoenix.controls.JFXButton;
import dao.TipoDAO;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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
    public List<Tipo> lstCentroTipos, lstCentroNiveles, lstEntidadTipos;
    public String codigos;
    final Image img;
    public List<Integer> lstFases;
    public Object objeto;
    public Navegador navegador;
    public Usuario usuario;
    String nombreBD;
    public boolean verCostos;
    final static Logger LOGGER = Logger.getLogger("controlador.MenuControlador");
    
    public MenuControlador(String rutaImagen, Usuario usuario, String nombreBD) {
        // Administrador de pantallas
        navegador = new Navegador(this);
        
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
}
