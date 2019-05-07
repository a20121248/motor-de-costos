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
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoOficina;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreOficina;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoBanca;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreBanca;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreProducto;
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
        tabcolCodigoOficina.setCellValueFactory(cellData -> cellData.getValue().getOficina().codigoProperty());
        tabcolNombreOficina.setCellValueFactory(cellData -> cellData.getValue().getOficina().nombreProperty());
        tabcolCodigoBanca.setCellValueFactory(cellData -> cellData.getValue().getBanca().codigoProperty());
        tabcolNombreBanca.setCellValueFactory(cellData -> cellData.getValue().getBanca().nombreProperty());
        tabcolCodigoProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolNombreProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolPorcentaje.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        // tabla: dimensiones
        tabLista.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoOficina.setMaxWidth(1f * Integer.MAX_VALUE * 14);
        tabcolNombreOficina.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigoBanca.setMaxWidth(1f * Integer.MAX_VALUE * 14);
        tabcolNombreBanca.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigoProducto.setMaxWidth(1f * Integer.MAX_VALUE * 14);
        tabcolNombreProducto.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolPorcentaje.setMaxWidth(1f * Integer.MAX_VALUE * 13);
    }    
}
