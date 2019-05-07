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
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;

public class BuscarDriverCentroControlador implements Initializable {
    // variables de la vista
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private Button btnFiltrar;
    
    @FXML private TableView<DriverCentro> tabDrivers;
    @FXML private TableColumn<DriverCentro, String> tabcolCodigo;
    @FXML private TableColumn<DriverCentro, String> tabcolNombre;
    @FXML private TableView<DriverLinea> tabEntidadesDistribuir;
    @FXML private TableColumn<DriverLinea, String> tabcolCodigoDestino;
    @FXML private TableColumn<DriverLinea, String> tabcolNombreDestino;
    @FXML private TableColumn<DriverLinea, Double> tabcolPorcentajeDestino;    
    @FXML private Label lblCantResultados;
    
    @FXML private Button btnSeleccionar;
    
    // variables de la aplicacion
    MenuControlador menuControlador;
    ObjetoControladorInterfaz objetoControlador;
    int periodoSeleccionado;
    DriverDAO driverDAO;
    FilteredList<DriverCentro> filteredData;
    SortedList<DriverCentro> sortedData;
    
    public BuscarDriverCentroControlador(MenuControlador menuControlador, ObjetoControladorInterfaz objetoControlador, int periodoSeleccionado) {
        this.menuControlador = menuControlador;
        this.objetoControlador = objetoControlador;
        this.periodoSeleccionado = periodoSeleccionado;
        driverDAO = new DriverDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla drivers: dimensiones
        tabDrivers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 80);
        // tabla drivers: formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // tabla detalle driver: dimensiones
        tabEntidadesDistribuir.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoDestino.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombreDestino.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        tabcolPorcentajeDestino.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        // tabla drivers: formato
        tabcolCodigoDestino.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().codigoProperty());
        tabcolNombreDestino.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        // tabla drivers: evento de click
        tabDrivers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            DriverCentro driver = tabDrivers.getSelectionModel().getSelectedItem();
            if (driver != null) {
                List<DriverLinea> listaDriverLinea = driverDAO.obtenerLstDriverLinea(periodoSeleccionado,driver.getCodigo(),menuControlador.repartoTipo);
                tabEntidadesDistribuir.getItems().setAll(listaDriverLinea);
            }
        });
        // Tabla: filtro
        filteredData = new FilteredList(FXCollections.observableArrayList(driverDAO.listarDriversCentroSinDetalle(periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
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
            DriverCentro driver = tabDrivers.getSelectionModel().getSelectedItem();            
            if (driver == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Seleccionar driver");
                alert.setHeaderText(null);
                alert.setContentText("Por favor seleccione un driver.");
                alert.showAndWait();
            } else {
                objetoControlador.seleccionarDriverCentro(driver);
                ((Stage) btnSeleccionar.getScene().getWindow()).close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
