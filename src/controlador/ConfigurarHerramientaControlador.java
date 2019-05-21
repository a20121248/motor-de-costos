package controlador;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ConfigurarHerramientaControlador implements Initializable {
    @FXML private Label lblTitulo;
    
    @FXML private TextField txtRutaLogs;
    @FXML private Button btnCargarRuta;    
    @FXML private ComboBox<String> cmbBD;
    @FXML private ComboBox<String> cmbTema;
    
    @FXML private Button btnGuardar;
    
    LoginControlador loginControlador;
    
    public ConfigurarHerramientaControlador(LoginControlador loginControlador) {
        this.loginControlador = loginControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String rutaLogs = loginControlador.preferencias.obtenerRutaLogs();
        txtRutaLogs.setText(rutaLogs);
        List<String> lstIdiomas = new ArrayList(Arrays.asList("Oracle","MySQL"));
        cmbBD.getItems().addAll(lstIdiomas);
        cmbBD.getSelectionModel().select(0);
        List<String> lstEstilos = new ArrayList(Arrays.asList("Default","Banco Ganadero","BCP","PACIFICO"));
        cmbTema.getItems().addAll(lstEstilos);
        cmbTema.getSelectionModel().select(0);
    }

    @FXML void btnCargarRutaAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar directorio de logs");
        File archivoSeleccionado = directoryChooser.showDialog((btnCargarRuta.getScene().getWindow()));
        if (archivoSeleccionado != null) {
            txtRutaLogs.setText(archivoSeleccionado.getAbsolutePath());
        }
    }
    
    @FXML void btnGuardarAction(ActionEvent event) {
        loginControlador.preferencias.guardarParametrosLog(txtRutaLogs.getText());
        loginControlador.estiloSeleccionado = cmbTema.getValue();
        loginControlador.idiomaSeleccionado = cmbTema.getValue();
        loginControlador.nombreBD = cmbBD.getValue();
        
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }
}
