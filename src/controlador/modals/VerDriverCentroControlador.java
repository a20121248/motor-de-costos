package controlador.modals;

import controlador.MenuControlador;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modelo.DriverCentro;
import modelo.DriverLinea;

public class VerDriverCentroControlador implements Initializable {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    
    @FXML private TableView<DriverLinea> tabLista;
    @FXML private TableColumn<DriverLinea, String> tabcolCodigo;
    @FXML private TableColumn<DriverLinea, String> tabcolNombre;
    @FXML private TableColumn<DriverLinea, Double> tabcolPorcentaje;
    
    @FXML private Label lblNumeroRegistros;

    MenuControlador menuControlador;
    DriverCentro driver;
    
    public VerDriverCentroControlador(MenuControlador menuControlador, DriverCentro driver) {
        this.menuControlador = menuControlador;
        this.driver = driver;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // datos del driver
        txtCodigo.setText(driver.getCodigo());
        txtNombre.setText(driver.getNombre());
        txtDescripcion.setText(driver.getDescripcion());
        tabLista.getItems().setAll(driver.getListaDriverLinea());
        lblNumeroRegistros.setText("Número de registros: " + driver.getListaDriverLinea().size());
        // tabla: formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().nombreProperty());
        tabcolPorcentaje.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        // tabla: dimensiones
        tabLista.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        tabcolPorcentaje.setMaxWidth(1f * Integer.MAX_VALUE * 30);
    }    
}
