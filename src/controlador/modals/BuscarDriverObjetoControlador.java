package controlador.modals;

import controlador.MenuControlador;
import controlador.ObjetoControladorInterfaz;
import dao.DriverDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;

public class BuscarDriverObjetoControlador implements Initializable {
    // variables de la vista
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private Button btnFiltrar;
    
    @FXML private TableView<DriverObjeto> tabDrivers;
    @FXML private TableColumn<DriverObjeto, String> tabcolCodigo;
    @FXML private TableColumn<DriverObjeto, String> tabcolNombre;
    @FXML private TableColumn<DriverObjeto, String> tabcolDescripcion;
    @FXML private TableView<DriverObjetoLinea> tabEntidades;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolSubcanalCodigo;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolSubcanalNombre;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolProductoCodigo;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolProductoNombre;
    @FXML private TableColumn<DriverObjetoLinea, Double> tabcolPorcentaje;    
    @FXML private Label lblCantResultados;
    
    @FXML private Button btnSeleccionar;
    
    // variables de la aplicacion
    MenuControlador menuControlador;
    ObjetoControladorInterfaz objetoControlador;
    int periodoSeleccionado;
    DriverDAO driverDAO;
    FilteredList<DriverObjeto> filteredData;
    SortedList<DriverObjeto> sortedData;
    
    public BuscarDriverObjetoControlador(MenuControlador menuControlador, ObjetoControladorInterfaz objetoControlador, int periodoSeleccionado) {
        this.menuControlador = menuControlador;
        this.objetoControlador = objetoControlador;
        this.periodoSeleccionado = periodoSeleccionado;
        driverDAO = new DriverDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla drivers: dimensiones
        tabDrivers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        tabcolDescripcion.setMaxWidth(1f * Integer.MAX_VALUE * 45);
        // tabla drivers: formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
        // tabla detalle driver: dimensiones
        tabEntidades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolProductoCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolProductoNombre.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolSubcanalCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolSubcanalNombre.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolPorcentaje.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        // tabla drivers: formato
        tabcolProductoCodigo.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolProductoNombre.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolSubcanalCodigo.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().codigoProperty());
        tabcolSubcanalNombre.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().nombreProperty());
        tabcolPorcentaje.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        // tabla drivers: evento de click
        tabDrivers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            DriverObjeto driver = tabDrivers.getSelectionModel().getSelectedItem();
            if (driver != null) {
                List<DriverObjetoLinea> listaDriverLinea = driverDAO.obtenerDriverObjetoLinea(periodoSeleccionado, driver.getCodigo(),menuControlador.repartoTipo);
                tabEntidades.getItems().setAll(listaDriverLinea);
            }
        });
        // Tabla: filtro
        filteredData = new FilteredList(FXCollections.observableArrayList(driverDAO.listarDriversObjetoSinDetalle(periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
        txtCodigo.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                String codigoFiltrar = newValue.toLowerCase();
                String nombreFiltrar = txtNombre.getText().toLowerCase();
                return item.getCodigo().toLowerCase().contains(codigoFiltrar) && item.getNombre().toLowerCase().contains(nombreFiltrar);
            });
            lblCantResultados.setText("Número de registros: " + filteredData.size());
        });
        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                String codigoFiltrar = txtCodigo.getText().toLowerCase();
                String nombreFiltrar = newValue.toLowerCase();
                return item.getCodigo().toLowerCase().contains(codigoFiltrar) && item.getNombre().toLowerCase().contains(nombreFiltrar);
            });
            lblCantResultados.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabDrivers.comparatorProperty());
        tabDrivers.setItems(sortedData);
        lblCantResultados.setText("Número de registros: " + sortedData.size());
    }
    
    @FXML void btnSeleccionarAction(ActionEvent event) {
        try {
            DriverObjeto driver = tabDrivers.getSelectionModel().getSelectedItem();            
            if (driver == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Seleccionar driver");
                alert.setHeaderText(null);
                alert.setContentText("Por favor seleccione un driver.");
                alert.showAndWait();
            } else {
                objetoControlador.seleccionarDriverObjeto(driver);
                ((Stage) btnSeleccionar.getScene().getWindow()).close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
