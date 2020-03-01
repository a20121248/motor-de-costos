package controlador.aprovisionamiento.drivers_objeto;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDrivers;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private JFXButton btnBuscarPeriodo;
    
    @FXML private JFXButton btnCargar;
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    
    @FXML private TableView<DriverObjeto> tabListaDrivers;
    @FXML private TableColumn<DriverObjeto, String> tabcolCodigo;
    @FXML private TableColumn<DriverObjeto, String> tabcolNombre;
    @FXML private TableColumn<DriverObjeto, String> tabcolDescripcion;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private Label lblEntidades;
    @FXML private TableView<DriverObjetoLinea> tabDetalleDriver;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoBanca;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreBanca;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoOficina;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreOficina;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreProducto;
    @FXML private TableColumn<DriverObjetoLinea, Double> tabcolPorcentajeDestino;
    
    // Variables de la aplicacion
    DriverDAO driverDAO;
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR.getControlador());
    String titulo1;
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        this.menuControlador = menuControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Objetos de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo1 = "Objetos de Beneficio";
            lblTitulo.setText("Drivers - " + titulo1);
            lnkDrivers.setText("Drivers - " + titulo1);
            lblEntidades.setText(titulo1 + " a distribuir");
        }
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        spAnho.getValueFactory().setValue(menuControlador.periodoSeleccionado / 100);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        // tabla 1: dimensiones
        tabListaDrivers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        tabcolDescripcion.setMaxWidth(1f * Integer.MAX_VALUE * 50);
        // tabla 1: formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
        
        // tabla 2: dimensiones
        tabDetalleDriver.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoBanca.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreBanca.setMaxWidth(1f * Integer.MAX_VALUE * 19);
        tabcolCodigoOficina.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreOficina.setMaxWidth(1f * Integer.MAX_VALUE * 19);
        tabcolCodigoProducto.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreProducto.setMaxWidth(1f * Integer.MAX_VALUE * 19);
        tabcolPorcentajeDestino.setMaxWidth(1f * Integer.MAX_VALUE * 13);        
        // tabla 2: formato
        tabcolCodigoBanca.setCellValueFactory(cellData -> cellData.getValue().getBanca().codigoProperty());
        tabcolNombreBanca.setCellValueFactory(cellData -> cellData.getValue().getBanca().nombreProperty());
        tabcolCodigoOficina.setCellValueFactory(cellData -> cellData.getValue().getOficina().codigoProperty());
        tabcolNombreOficina.setCellValueFactory(cellData -> cellData.getValue().getOficina().nombreProperty());
        tabcolCodigoProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolNombreProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        
        // tabla 2: evento de click
        tabListaDrivers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            DriverObjeto driver = tabListaDrivers.getSelectionModel().getSelectedItem();
            if (driver != null) {
                List<DriverObjetoLinea> listaDriverLinea = driverDAO.obtenerDriverObjetoLinea(menuControlador.periodoSeleccionado, driver.getCodigo());
                tabDetalleDriver.getItems().setAll(listaDriverLinea);
            }
        });
        
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(menuControlador.periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
        lblNumeroRegistros.setText("Número de registros : " + lista.size());
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }
    
    @FXML void lnkDriversAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }
    
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(menuControlador.periodoSeleccionado,menuControlador.repartoTipo);
        if (lista.isEmpty()) {
            menuControlador.navegador.mensajeInformativo("Consulta de drivers", "No existen drivers para el periodo seleccionado.");
            tabListaDrivers.getItems().clear();
            tabDetalleDriver.getItems().clear();
        } else {
            tabListaDrivers.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros: " + lista.size());
        }
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_CARGAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_CREAR);
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        DriverObjeto driverObjeto = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (driverObjeto == null) {
            menuControlador.navegador.mensajeInformativo("Editar Driver - " + titulo1, "Por favor seleccione un Driver.");
            return;
        }
        menuControlador.objeto = driverObjeto;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        DriverObjeto item = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Eliminar Driver - " + titulo1, "Por favor seleccione un Driver.");
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Driver - " + titulo1, "¿Está seguro de eliminar el Driver " + item.getCodigo() + "?")) {
            return;
        }
        if (driverDAO.eliminarDriverObjeto(item.getCodigo()) == -1) {
            menuControlador.navegador.mensajeError("Eliminar Driver - " + titulo1, "No se pudo eliminar el Driver pues está siendo utilizado en otros módulos.\nPara eliminarlo, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizado.");
            return;
        }
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(menuControlador.periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
        tabDetalleDriver.getItems().clear();
    }
}
