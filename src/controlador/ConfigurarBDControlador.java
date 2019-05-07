package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class ConfigurarBDControlador implements Initializable {
    @FXML private Label lblTitulo;
    
    @FXML private TextField txtServidor;
    @FXML private TextField txtPuerto;
    @FXML private TextField txtSID;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasenha;
    
    @FXML private Button btnProbar;
    @FXML private Label lblResultadoEstado;
    
    LoginControlador loginControlador;
    
    public ConfigurarBDControlador(LoginControlador loginControlador) {        
        this.loginControlador = loginControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (ConexionBD.connection != null) {            
            lblResultadoEstado.setText("Conectado");
            lblResultadoEstado.setTextFill(Color.web("#228b22"));
        } else {
            lblResultadoEstado.setText("Desconectado");
            lblResultadoEstado.setTextFill(Color.web("#e23232"));
        }        
        
        //obtener los parametros de conexion de las preferencias
        String servidor = loginControlador.preferencias.obtenerServidor();
        String puerto = loginControlador.preferencias.obtenerPuerto();
        String sid = loginControlador.preferencias.obtenerSID();
        String usuario = loginControlador.preferencias.obtenerUsuario();
        String contrasenha = loginControlador.preferencias.obtenerContrasenha();
        
        //mostrar los parametros de conexion
        txtServidor.setText(servidor);
        txtPuerto.setText(puerto);
        txtSID.setText(sid);
        txtUsuario.setText(usuario);
        txtContrasenha.setText(contrasenha);
    }
    
    @FXML void btnProbarAction(ActionEvent event) {
        // leer parametros de conexion de la interfaz
        String servidor = txtServidor.getText();
        String puerto = txtPuerto.getText();
        String sid = txtSID.getText();
        String usuario = txtUsuario.getText();
        String contrasenha = txtContrasenha.getText();
        
        // guardar parametros de conexion en preferencias
        loginControlador.preferencias.guardarParametros(servidor, puerto, sid, usuario, contrasenha);
        
        //conectar a la BD
        ConexionBD.EstablecerParametros(servidor, puerto, sid, usuario, contrasenha);
        ConexionBD.obtenerConexionBD();
        if (ConexionBD.connection != null) {            
            lblResultadoEstado.setText("Conectado");
            lblResultadoEstado.setTextFill(Color.web("#228b22"));
        } else {     
            lblResultadoEstado.setText("Desconectado");
            lblResultadoEstado.setTextFill(Color.web("#e23232"));
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Motor de reparto | Error en conexión a base de datos");
            alert.setHeaderText(null);
            alert.setContentText(ConexionBD.mensaje);
            alert.showAndWait();
        }
    }
}
