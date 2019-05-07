package controlador.aprovisionamiento.drivers;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.DriverDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.DriverCentro;
import modelo.DriverLinea;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;    
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDriversCentro;   
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private JFXButton btnBuscarPeriodo;
    
    @FXML private JFXButton btnCargar;
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;

    @FXML private TableView<DriverCentro> tabListaDrivers;
    @FXML private TableColumn<DriverCentro, String> tabcolCodigo;
    @FXML private TableColumn<DriverCentro, String> tabcolNombre;
    @FXML private TableColumn<DriverCentro, String> tabcolDescripcion;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private Label lblEntidades;
    @FXML private TableView<DriverLinea> tabDetalleDriver;
    @FXML private TableColumn<DriverLinea, String> tabcolCodigoDestino;
    @FXML private TableColumn<DriverLinea, String> tabcolNombreDestino;
    @FXML private TableColumn<DriverLinea, Double> tabcolPorcentajeDestino;
    @FXML private Label lblNumeroCentros;
    @FXML private Label lblSuma;
    
    // Variables de la aplicacion
    DriverDAO driverDAO;
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    int periodoSeleccionado;
    double porcentajeTotal;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR.getControlador());
    String titulo1;
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        this.menuControlador = menuControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Centros de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo1 = "Centros de Beneficio";
            lblTitulo.setText("Drivers - " + titulo1);
            lnkDriversCentro.setText("Drivers - " + titulo1);
            lblEntidades.setText(titulo1 + " a distribuir");
        }
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        // Periodo seleccionado
        periodoSeleccionado = menuControlador.periodo;
        // tabla 1
        tabListaDrivers.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        tabcolDescripcion.setMaxWidth(1f * Integer.MAX_VALUE * 45);
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
        // tabla 2
        tabDetalleDriver.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigoDestino.setMaxWidth(1f * Integer.MAX_VALUE * 12);
        tabcolNombreDestino.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        tabcolPorcentajeDestino.setMaxWidth(1f * Integer.MAX_VALUE * 28);
        tabcolCodigoDestino.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().codigoProperty());
        tabcolNombreDestino.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        tabcolPorcentajeDestino.setCellFactory(column -> {
                return new TableCell<DriverLinea, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%,.4f", item));
                    }
                }
            };
        });
        // tabla 2: evento de click
        tabListaDrivers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            DriverCentro driver = tabListaDrivers.getSelectionModel().getSelectedItem();
            if (driver != null) {
                List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(periodoSeleccionado, driver.getCodigo(), menuControlador.repartoTipo);
                tabDetalleDriver.getItems().setAll(lstDriverLinea);
                porcentajeTotal = lstDriverLinea.stream().mapToDouble(o -> o.getPorcentaje()).sum();
                lblNumeroCentros.setText("Número de registos: " + lstDriverLinea.size());
                lblSuma.setText(String.format("Suma: %,.4f%%",porcentajeTotal));
            }
        });
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }

    @FXML void lnkDriversCentroAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_CARGAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_CREAR);
    }

    @FXML void btnEditarAction(ActionEvent event) {
        DriverCentro driver = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (driver == null) {
            menuControlador.navegador.mensajeInformativo("Editar Driver - " + titulo1, "Por favor seleccione un Driver.");
            return;
        }
        menuControlador.objeto = driver;
        menuControlador.periodoSeleccionado = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        DriverCentro item = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Eliminar Driver - " + titulo1, "Por favor seleccione un Driver.");
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Driver - " + titulo1, "¿Está seguro de eliminar el Driver " + item.getCodigo() + "?")) {
            return;
        }
        if (driverDAO.eliminarDriverCentro(item.getCodigo()) == -1) {
            menuControlador.navegador.mensajeError("Eliminar Driver - " + titulo1, "No se pudo eliminar el Driver pues está siendo utilizado en otros módulos.\nPara eliminarlo, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizado.");
            return;
        }
        List<DriverCentro> lista = driverDAO.listarDriversCentroSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
        tabDetalleDriver.getItems().clear();
    }
    
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        List<DriverCentro> lista = driverDAO.listarDriversCentroSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        if (lista.isEmpty()) {
            menuControlador.navegador.mensajeInformativo("Consulta de drivers", "No existen drivers para el periodo seleccionado.");
            tabListaDrivers.getItems().clear();
            tabDetalleDriver.getItems().clear();
        } else {
            tabListaDrivers.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros : " + lista.size());
        }
    }
}
