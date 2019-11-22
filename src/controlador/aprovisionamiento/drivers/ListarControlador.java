package controlador.aprovisionamiento.drivers;

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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import modelo.DriverCentro;
import modelo.DriverLinea;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private HBox hbPeriodo;
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private TextField txtBuscar;
    
    @FXML private JFXButton btnCargar;
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnDescargar;

    @FXML private TableView<DriverCentro> tabListaDrivers;
    @FXML private TableColumn<DriverCentro, String> tabcolCodigo;
    @FXML private TableColumn<DriverCentro, String> tabcolNombre;
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
    double porcentajeTotal;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR.getControlador());
    String titulo;
    FilteredList<DriverCentro> filteredData;
    SortedList<DriverCentro> sortedData;
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        this.menuControlador = menuControlador;
        titulo = "Driver - Centros de costos";
        // Periodo seleccionado
        if (menuControlador.repartoTipo == 1) {
            if (menuControlador.periodoSeleccionado % 100 == 0)
                ++menuControlador.periodoSeleccionado;
        } else {
            menuControlador.periodoSeleccionado = menuControlador.periodoSeleccionado / 100 * 100;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
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
        
        // Tabla 2: evento de click
        tabListaDrivers.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            DriverCentro driver = tabListaDrivers.getSelectionModel().getSelectedItem();
            if (driver != null) {
                List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(menuControlador.periodoSeleccionado, driver.getCodigo(), menuControlador.repartoTipo);
                tabDetalleDriver.getItems().setAll(lstDriverLinea);
                porcentajeTotal = lstDriverLinea.stream().mapToDouble(o -> o.getPorcentaje()).sum();
                lblEntidades.setText("DETALLE DEL DRIVER " + driver.getCodigo());
                lblNumeroCentros.setText("Número de registos: " + lstDriverLinea.size());
                lblSuma.setText(String.format("Suma: %,.4f%%",porcentajeTotal));
            }
        });
        
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(driverDAO.listarDriversCentroSinDetalle(menuControlador.periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
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

    @FXML void lnkDriversCentroAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_CARGAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_CREAR);
    }

    @FXML void btnEditarAction(ActionEvent event) {
        DriverCentro driver = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (driver == null) {
            menuControlador.mensaje.edit_empty_error(titulo);
            return;
        }
        menuControlador.objeto = driver;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        DriverCentro item = tabListaDrivers.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Driver - " + titulo, "¿Está seguro de eliminar el Driver " + item.getCodigo() + "?")) {
            return;
        }
        if (driverDAO.eliminarDriverCentro(item.getCodigo(), menuControlador.periodoSeleccionado,menuControlador.repartoTipo) == -1) {
            menuControlador.mensaje.delete_item_periodo_error(titulo);
            return;
        }
        menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),item.getCodigo(), Navegador.RUTAS_DRIVERS_CENTRO_LISTAR.getDireccion());
        List<DriverCentro> lista = driverDAO.listarDriversCentroSinDetalle(menuControlador.periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
        lblNumeroRegistros.setText("Número de registos: " + lista.size());
        tabDetalleDriver.getItems().clear();
    }
    
    private void buscarPeriodo(int periodo) {
        List<DriverCentro> lista = driverDAO.listarDriversCentroSinDetalle(periodo, menuControlador.repartoTipo);
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        tabListaDrivers.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
    }    
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        if (menuControlador.navegador.mensajeConfirmar("Descargar Driver", "¿Está seguro que desea descargar el driver del periodo " + menuControlador.periodoSeleccionado + "?")) {
            DescargaServicio descargaFile;
            if(!tabListaDrivers.getItems().isEmpty()){
                DirectoryChooser directory_chooser = new DirectoryChooser();
                directory_chooser.setTitle("Directorio a Descargar:");
                File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
                if(directorioSeleccionado != null){
                    descargaFile = new DescargaServicio(tabListaDrivers, "DriverCentrosDeCostos");
                    descargaFile.descargarTablaDriverCentros(Integer.toString(menuControlador.periodoSeleccionado),menuControlador.repartoTipo,directorioSeleccionado.getAbsolutePath());
                    menuControlador.Log.descargarTabla(LOGGER, menuControlador.usuario.getUsername(), titulo, Navegador.RUTAS_CENTROS_MAESTRO_LISTAR.getDireccion());
                }else{
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_CANCELED);
                }
            }else{
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_EMPTY);
            }
        }
    }
}
