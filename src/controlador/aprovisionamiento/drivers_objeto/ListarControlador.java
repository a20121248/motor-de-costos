package controlador.aprovisionamiento.drivers_objeto;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.DriverDAO;
import java.io.File;
import java.io.IOException;
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
import javafx.stage.DirectoryChooser;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import servicios.DescargaServicio;

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
//    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnDescargar;
    
    @FXML private TableView<DriverObjeto> tabListaDrivers;
    @FXML private TableColumn<DriverObjeto, String> tabcolCodigo;
    @FXML private TableColumn<DriverObjeto, String> tabcolNombre;
    @FXML private TableColumn<DriverObjeto, String> tabcolDescripcion;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private Label lblEntidades;
    @FXML private TableView<DriverObjetoLinea> tabDetalleDriver;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, Double> tabcolPorcentajeDestino;
    
    // Variables de la aplicacion
    DriverDAO driverDAO;
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        this.menuControlador = menuControlador;
        if (menuControlador.objeto != null)
            periodoSeleccionado = (int) menuControlador.objeto;
        else
            periodoSeleccionado = menuControlador.periodo;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo = " Driver - Objetos de Costos";
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            periodoSeleccionado = periodoSeleccionado / 100 * 100;
        }
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(periodoSeleccionado % 100 - 1);
        spAnho.getValueFactory().setValue(periodoSeleccionado / 100);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
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
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
        lblNumeroRegistros.setText("Número de registros: " + lista.size());
        
        // tabla 2: dimensiones
        tabDetalleDriver.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoProducto.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreProducto.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        tabcolCodigoSubcanal.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreSubcanal.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        tabcolPorcentajeDestino.setMaxWidth(1f * Integer.MAX_VALUE * 10);        
        // tabla 2: formato
        tabcolCodigoProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolNombreProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolCodigoSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().codigoProperty());
        tabcolNombreSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        
        // tabla 2: evento de click
        tabListaDrivers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            DriverObjeto driver = tabListaDrivers.getSelectionModel().getSelectedItem();
            if (driver != null) {
                List<DriverObjetoLinea> listaDriverLinea = driverDAO.obtenerDriverObjetoLinea(periodoSeleccionado, driver.getCodigo(),menuControlador.repartoTipo);
                tabDetalleDriver.getItems().setAll(listaDriverLinea);
            }
        });
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
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        if (lista.isEmpty()) {
            menuControlador.mensaje.show_table_empty(titulo);
            tabListaDrivers.getItems().clear();
            tabDetalleDriver.getItems().clear();
        } else {
            tabListaDrivers.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros: " + lista.size());
        }
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_CARGAR);
    }
    
//    @FXML void btnCrearAction(ActionEvent event) {
//        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_CREAR);
//    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        DriverObjeto driverObjeto = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (driverObjeto == null) {
            menuControlador.mensaje.edit_empty_error(titulo);
            return;
        }
        menuControlador.objeto = driverObjeto;
        menuControlador.periodoSeleccionado = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        DriverObjeto item = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Driver - " + titulo, "¿Está seguro de eliminar el Driver " + item.getCodigo() + "?")) {
            return;
        }
        if (driverDAO.eliminarDriverObjeto(item.getCodigo(),periodoSeleccionado,menuControlador.repartoTipo) == -1) {
            menuControlador.mensaje.delete_item_periodo_error(titulo);
            return;
        }
        menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),item.getCodigo(), Navegador.RUTAS_DRIVERS_OBJETO_LISTAR.getDireccion());
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
        tabDetalleDriver.getItems().clear();
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListaDrivers.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio(tabListaDrivers,"DriversObjetos");
                descargaFile.descargarTablaDriverObjetos(Integer.toString(periodoSeleccionado),menuControlador.repartoTipo,directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, periodoSeleccionado,Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR.getDireccion());
            }else{
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_CANCELED);
            }
        }else{
            menuControlador.navegador.mensajeError(menuControlador.MENSAJE_DOWNLOAD_EMPTY);
        }
    }
}
