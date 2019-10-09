package controlador.aprovisionamiento.balancete;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.DetalleGastoDAO;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import modelo.DetalleGasto;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;

    @FXML private TextField txtBuscar;
    @FXML private TableView<DetalleGasto> tabListar;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoCuentaContable;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombreCuentaContable;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoPartida;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombrePartida;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoCentro;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombreCentro;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto01;
    
    @FXML private Label lblNumeroRegistros;
    
    // Variables de la aplicacion
    DetalleGastoDAO detalleGastoDAO;
    MenuControlador menuControlador;
    FilteredList<DetalleGasto> filteredData;
    SortedList<DetalleGasto> sortedData;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_BALANCETE_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        detalleGastoDAO = new DetalleGastoDAO();
        this.titulo = "Detalle de Gasto";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Periodo seleccionado
        periodoSeleccionado = menuControlador.periodo;
        
        // Mes seleccionado
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                buscarPeriodo(periodoSeleccionado);
            }
        });
        
        // Anho seleccionado
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                buscarPeriodo(periodoSeleccionado);
            }
        });
        
        // Tabla: Formato
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoCuentaContable.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreCuentaContable.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolCodigoPartida.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombrePartida.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolCodigoCentro.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreCentro.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolMonto01.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolCodigoCuentaContable.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaContableProperty());
        tabcolNombreCuentaContable.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaContableProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolMonto01.setCellValueFactory(cellData -> cellData.getValue().monto01Property().asObject());
        tabcolMonto01.setCellFactory(column -> {
                return new TableCell<DetalleGasto, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%,.2f", item));
                    }
                }
            };
        });
        
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(detalleGastoDAO.listar(periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigoCuentaContable().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreCuentaContable().toLowerCase().contains(lowerCaseFilter)) return true;
                if (item.getCodigoPartida().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombrePartida().toLowerCase().contains(lowerCaseFilter)) return true;
                if (item.getCodigoCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + sortedData.size());
    }

    // Acción de la pestaña 'Inicio'
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    // Acción de la pestaña 'Aprovisionamiento'
    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }

    // Acción de la pestaña 'Balancete'
    @FXML void lnkBalanceteAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
    
    // Acción del botón 'Cargar'
    @FXML void btnCargarAction(ActionEvent event) {
        // Validar si el usuario tiene permiso para cargar el balancete
        if (!menuControlador.usuario.puede("APR_BAL_CARGAR")) {
            menuControlador.navegador.mensajeInformativo("Seguridad", "El usuario no tiene permiso para cargar el Balancete");
            return;
        }
        // Cambiar a la pantalla de cargar mostrando el periodo seleccionado
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_CARGAR);
    }

    private void buscarPeriodo(int periodo) {
        List<DetalleGasto> lista = detalleGastoDAO.listar(periodo, menuControlador.repartoTipo);
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
    }
    
    // Acción del botón 'Atrás'
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }
}