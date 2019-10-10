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
import javafx.stage.DirectoryChooser;
import modelo.DriverCentro;
import modelo.DriverLinea;
import servicios.DescargaServicio;

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
    @FXML private JFXButton btnDescargar;

    @FXML private TableView<DriverCentro> tabListaDrivers;
    @FXML private TableColumn<DriverCentro, String> tabcolCodigo;
    @FXML private TableColumn<DriverCentro, String> tabcolNombre;
//    @FXML private TableColumn<DriverCentro, String> tabcolDescripcion;
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
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        this.menuControlador = menuControlador;
        titulo = "Driver";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            periodoSeleccionado = menuControlador.periodo - menuControlador.periodo%100;
        } else {
            periodoSeleccionado = menuControlador.periodo;
        }
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
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
        // tabla 1
        tabListaDrivers.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 40);
//        tabcolDescripcion.setMaxWidth(1f * Integer.MAX_VALUE * 45);
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
//        tabcolDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
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
        
               List<DriverCentro> lista = driverDAO.listarDriversCentroSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
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
            menuControlador.mensaje.edit_empty_error(titulo);
            return;
        }
        menuControlador.objeto = driver;
        menuControlador.periodoSeleccionado = periodoSeleccionado;
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
        if (driverDAO.eliminarDriverCentro(item.getCodigo(),periodoSeleccionado,menuControlador.repartoTipo) == -1) {
            menuControlador.mensaje.delete_item_periodo_error(titulo);
            return;
        }
        menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),item.getCodigo(), Navegador.RUTAS_DRIVERS_CENTRO_LISTAR.getDireccion());
        List<DriverCentro> lista = driverDAO.listarDriversCentroSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        tabListaDrivers.getItems().setAll(lista);
        tabDetalleDriver.getItems().clear();
    }
    
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        List<DriverCentro> lista = driverDAO.listarDriversCentroSinDetalle(periodoSeleccionado,menuControlador.repartoTipo);
        if (lista.isEmpty()) {
            menuControlador.navegador.mensajeInformativo("drivers", menuControlador.MENSAJE_TABLE_EMPTY);
            tabListaDrivers.getItems().clear();
            tabDetalleDriver.getItems().clear();
        } else {
            tabListaDrivers.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros : " + lista.size());
        }
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        if (menuControlador.navegador.mensajeConfirmar("Descargar Driver", "¿Está seguro que desea descargar el driver del periodo " + periodoSeleccionado + "?")) {
            DescargaServicio descargaFile;
            if(!tabListaDrivers.getItems().isEmpty()){
                DirectoryChooser directory_chooser = new DirectoryChooser();
                directory_chooser.setTitle("Directorio a Descargar:");
                File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
                if(directorioSeleccionado != null){
                    descargaFile = new DescargaServicio(tabListaDrivers, "DriverCentrosDeCostos");
                    descargaFile.descargarTablaDriverCentros(Integer.toString(periodoSeleccionado),menuControlador.repartoTipo,directorioSeleccionado.getAbsolutePath());
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
