package controlador;

import app.App;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import modelo.RutaArchivos;
import modelo.Usuario;
import servicios.LoggingServicio;
import servicios.SeguridadServicio;

public class LoginControlador implements Initializable {
    @FXML private Label lblTitulo;
    @FXML private Label lblUsuario;
    @FXML private Label lblContrasenha;
    @FXML private JFXTextField txtUsuario;
    @FXML private JFXPasswordField txtContrasenha;
    @FXML private JFXButton btnLogin;
    
    @FXML private MenuItem itmConfigurarBD;
    @FXML private MenuItem itmConfigurarHerramienta;
    @FXML private MenuItem itmSalir;
    @FXML private MenuItem itmAcerca;
    
    public String estiloSeleccionado;
    public String idiomaSeleccionado;    
    public String nombreBD;
    
    private final App app;
    public Preferencias preferencias;
    final SeguridadServicio seguridadServicio;
    final static Logger LOGGER = Logger.getLogger("app.controlador.LoginControlador");
    
    // =========================================================
    // ******************** BARRA DE MENU **********************
    // =========================================================
    final RutaArchivos RUTAS_MENU_CONFIGURAR_BD = new RutaArchivos(
            "/vista/ConfigurarBD.fxml",
            "controlador.ConfigurarBDControlador"
    );
    final RutaArchivos RUTAS_MENU_CONFIGURAR_HERRAMIENTA = new RutaArchivos(
            "/vista/ConfigurarHerramienta.fxml",
            "controlador.ConfigurarHerramientaControlador"
    );
    // =========================================================
    // ******************** MENU **********************
    // =========================================================
    final RutaArchivos RUTAS_MENU = new RutaArchivos(
            "/vista/Menu.fxml",
            "controlador.MenuControlador",
            "Menú Principal"
    );
    
    public LoginControlador(App app) {
        this.app = app;
        estiloSeleccionado = "PACIFICO";
        preferencias = new Preferencias();
        seguridadServicio = new SeguridadServicio();
        nombreBD = "Oracle";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtUsuario.setText("admin");
        txtContrasenha.setText("secret");
    }
    
    @FXML void btnLoginAction(ActionEvent event)  {
        login();
    }
    
    private void login(){
        String rutaLog = preferencias.obtenerRutaLogs();
        if (ConexionBD.connection == null) {
            //si la BD es nula, intento conectarme con las preferencias
            //obtener los parametros de conexion de las preferencias
            String servidorBD = preferencias.obtenerServidor();
            String puertoBD = preferencias.obtenerPuerto();
            String sidBD = preferencias.obtenerSID();
            String usuarioBD = preferencias.obtenerUsuario();
            String contrasenhaBD = preferencias.obtenerContrasenha();
            
            //conectar a la BD
            ConexionBD.EstablecerParametros(servidorBD, puertoBD, sidBD, usuarioBD, contrasenhaBD);
            ConexionBD.obtenerConexionBD();
            if (ConexionBD.connection == null) {
                //si la conexion sigue siendo nula, mandar mensaje de error
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Motor de distribución | Login");
                alert.setHeaderText(null);
                alert.setContentText("Por favor establezca una conexión correcta a base de datos en el menú Archivo > Configuración de base de datos.");
                alert.showAndWait();
                return;
            }
        }
        
        if (txtUsuario.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Motor de distribución | Login");
            alert.setHeaderText(null);
            alert.setContentText("El usuario está vacío.");
            alert.showAndWait();
            return;            
        }
        
        if (txtContrasenha.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Motor de distribución | Login");
            alert.setHeaderText(null);
            alert.setContentText("La contraseña está vacía.");
            alert.showAndWait();
            return;
        }
        
        // Buscar el Usuario
        Usuario usuario = seguridadServicio.buscarUsuario(txtUsuario.getText());
        if (usuario == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Motor de distribución | Login");
            alert.setHeaderText(null);
            alert.setContentText("El usuario " + txtUsuario.getText() + "no existe.");
            alert.showAndWait();
            LOGGER.log(Level.WARNING,String.format("Se introdujo el usuario %s que no existe.\n",txtUsuario.getText()));
            return;
        }
        
        // Validar la contraseña
        if (!usuario.getPassword().equals(txtContrasenha.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Motor de distribución | Login");
            alert.setHeaderText(null);
            alert.setContentText("La contraseña es incorrecta.");
            alert.showAndWait();
            LOGGER.log(Level.WARNING,String.format("Se introdujo una contraseña incorrecta para el usuario %s.\n",txtUsuario.getText()));
            return;
        }        
        LOGGER.log(Level.INFO,String.format("El usuario %s inició sesión correctamente.\n",txtUsuario.getText()));
        
        try {
            String rutaEstilo = "/recursos/estilos/menuGANADERO.css";
            String rutaImagen = "/recursos/imagenes/logoGANADERO.png";
            String rutaIcono = "/recursos/imagenes/iconoGANADERO.png";
            
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RUTAS_MENU.getVista()));
            MenuControlador menuControlador = new MenuControlador(rutaImagen,usuario,nombreBD,rutaLog);
            fxmlLoader.setController(menuControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(rutaEstilo).toExternalForm());
            
            Stage stage = new Stage();
            stage.getIcons().add(new Image(rutaIcono));
            stage.setTitle("Motor de distribución");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            Stage stage2 = (Stage) btnLogin.getScene().getWindow();
            stage2.hide();
            //stage2.close();
            
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    stage2.show();
                    txtContrasenha.setText("");
                    //Platform.exit();
                    //System.exit(0);
                }
            });
        } catch(IOException e) {
            LOGGER.log(Level.SEVERE,String.format("%s",e.getMessage()));
        }
    }
    
    @FXML void btnSalirAction(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML void itmConfigurarBDAction(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RUTAS_MENU_CONFIGURAR_BD.getVista()));
            ConfigurarBDControlador configurarBDControlador = new ConfigurarBDControlador(this);
            fxmlLoader.setController(configurarBDControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Motor de distribución | Configurar base de datos");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.SEVERE,String.format("%s",e.getMessage()));
        }
    }
    
    @FXML void itmConfigurarHerramientaAction(ActionEvent event) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(RUTAS_MENU_CONFIGURAR_HERRAMIENTA.getVista()));
            ConfigurarHerramientaControlador configurarHerramientaControlador = new ConfigurarHerramientaControlador(this);
            fxmlLoader.setController(configurarHerramientaControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Motor de distribución | Configurar herramienta");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.SEVERE,String.format("%s",e.getMessage()));
        }
    }
    
    @FXML void itmSalirAction(ActionEvent event) {
        System.exit(0);
    }
    
    @FXML void itmAcercaAction(ActionEvent event) {
        
    }
}
