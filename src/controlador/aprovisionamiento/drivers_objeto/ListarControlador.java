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
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private HBox hbPeriodo;
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private TextField txtBuscar;
    
    @FXML private JFXButton btnCargar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnDescargar;
    
    @FXML private TableView<DriverObjeto> tabListaDrivers;
    @FXML private TableColumn<DriverObjeto, String> tabcolCodigo;
    @FXML private TableColumn<DriverObjeto, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private Label lblEntidades;
    @FXML private TableView<DriverObjetoLinea> tabDetalleDriver;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, Double> tabcolPorcentajeDestino;
    @FXML private Label lblNumeroCentros;
    @FXML private Label lblSuma;
    
    // Variables de la aplicacion
    DriverDAO driverDAO;
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    double porcentajeTotal;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR.getControlador());
    String titulo;
    FilteredList<DriverObjeto> filteredData;
    SortedList<DriverObjeto> sortedData;
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        titulo = " Driver - Objetos de costos";
        this.menuControlador = menuControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        // Periodo seleccionado
        if (menuControlador.repartoTipo != 1)
            menuControlador.periodoSeleccionado = menuControlador.periodoSeleccionado / 100 * 100;
        
        // Mes seleccionado
        if (menuControlador.repartoTipo == 1) {
            cmbMes.getItems().addAll(menuControlador.lstMeses);
            cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
            cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                    buscarPeriodo(menuControlador.periodoSeleccionado);
                }
            });
        } else {
            hbPeriodo.getChildren().remove(cmbMes);
        }
        
        // Anho seleccionado
        spAnho.getValueFactory().setValue(menuControlador.periodoSeleccionado / 100);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (menuControlador.repartoTipo == 1)
                    menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                else
                    menuControlador.periodoSeleccionado = spAnho.getValue()*100;
                buscarPeriodo(menuControlador.periodoSeleccionado);
            }
        });

        // Tabla Maestro: Formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        
        // tabla Detalle: Formato
        tabcolCodigoProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolNombreProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolCodigoSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().codigoProperty());
        tabcolNombreSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        tabcolPorcentajeDestino.setCellFactory(column -> {
                return new TableCell<DriverObjetoLinea, Double>() {
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
        
        // Tabla 2: evento de click
        tabListaDrivers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            DriverObjeto driver = tabListaDrivers.getSelectionModel().getSelectedItem();
            if (driver != null) {
                List<DriverObjetoLinea> listaDriverLinea = driverDAO.obtenerDriverObjetoLinea(menuControlador.periodoSeleccionado, driver.getCodigo(),menuControlador.repartoTipo);
                tabDetalleDriver.getItems().setAll(listaDriverLinea);
                porcentajeTotal = listaDriverLinea.stream().mapToDouble(o -> o.getPorcentaje()).sum();
                lblEntidades.setText("DETALLE DEL DRIVER " + driver.getCodigo());
                lblNumeroCentros.setText("Número de registos: " + listaDriverLinea.size());
                lblSuma.setText(String.format("Suma: %,.4f%%",porcentajeTotal));
            }
        });
        
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(driverDAO.listarDriversObjetoSinDetalle(menuControlador.periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListaDrivers.comparatorProperty());
        tabListaDrivers.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + sortedData.size());
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
    
    private void buscarPeriodo(int periodo) {
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(periodo, menuControlador.repartoTipo);
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        tabListaDrivers.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_CARGAR);
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
        if (driverDAO.eliminarDriverObjeto(item.getCodigo(),menuControlador.periodoSeleccionado,menuControlador.repartoTipo) == -1) {
            menuControlador.mensaje.delete_item_periodo_error(titulo);
            return;
        }
        menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),item.getCodigo(), Navegador.RUTAS_DRIVERS_OBJETO_LISTAR.getDireccion());
        List<DriverObjeto> lista = driverDAO.listarDriversObjetoSinDetalle(menuControlador.periodoSeleccionado,menuControlador.repartoTipo);
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
                descargaFile.descargarTablaDriverObjetos(Integer.toString(menuControlador.periodoSeleccionado),menuControlador.repartoTipo,directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, menuControlador.periodoSeleccionado,Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR.getDireccion());
            }else{
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_CANCELED);
            }
        }else{
            menuControlador.navegador.mensajeError(menuControlador.MENSAJE_DOWNLOAD_EMPTY);
        }
    }
}
