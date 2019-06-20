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
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;

public class VerDriverObjetoControlador implements Initializable {
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    
    @FXML private TableView<DriverObjetoLinea> tabLista;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, Double> tabcolPorcentaje;
    
    @FXML private Label lblNumeroRegistros;

    MenuControlador menuControlador;
    DriverObjeto driver;
    
    public VerDriverObjetoControlador(MenuControlador menuControlador, DriverObjeto driver) {
        this.menuControlador = menuControlador;
        this.driver = driver;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // datos del driver
        txtCodigo.setText(driver.getCodigo());
        txtNombre.setText(driver.getNombre());
        txtDescripcion.setText(driver.getDescripcion());
        tabLista.getItems().setAll(driver.getListaDriverObjetoLinea());
        lblNumeroRegistros.setText("Número de registros: " + driver.getListaDriverObjetoLinea().size());
        // tabla: formato
        tabcolCodigoProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolNombreProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolCodigoSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().codigoProperty());
        tabcolNombreSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().nombreProperty());
        tabcolPorcentaje.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        // tabla: dimensiones
        tabLista.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoProducto.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreProducto.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolCodigoSubcanal.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreSubcanal.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolPorcentaje.setMaxWidth(1f * Integer.MAX_VALUE * 20);
    }    
}
