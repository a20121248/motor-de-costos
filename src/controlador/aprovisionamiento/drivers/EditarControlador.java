package controlador.aprovisionamiento.drivers;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarEntidadControlador;
import dao.DriverDAO;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.EntidadDistribucion;
import modelo.Tipo;

public class EditarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDrivers;
    @FXML private Hyperlink lnkEditar;
    
    @FXML public ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    
    @FXML private Label lblEntidades;
    @FXML private JFXButton btnAgregar;
    @FXML private JFXButton btnQuitar;
    @FXML private TableView<DriverLinea> tabDetalleDriver;
    @FXML private TableColumn<DriverLinea, String> tabcolCodigoDestino;
    @FXML private TableColumn<DriverLinea, String> tabcolNombreDestino;
    @FXML private TableColumn<DriverLinea, Double> tabcolPorcentajeDestino;
    @FXML private Label lblNumeroRegistros;
    @FXML private Label lblSuma;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    DriverCentro driver;
    DriverDAO driverDAO;
    int numeroRegistros;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    double porcentajeTotal;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_CENTRO_EDITAR.getControlador());
    String titulo, titulo1,titulo2;
    
    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        driver = (DriverCentro) menuControlador.objeto;
        periodoSeleccionado = menuControlador.periodoSeleccionado;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
        this.titulo = "Drivers";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Centros de Costos";
        titulo2 = "Centro de Costos";
        txtCodigo.setDisable(true);
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            periodoSeleccionado = menuControlador.periodo-menuControlador.periodo%100;
        } else {
            periodoSeleccionado = menuControlador.periodo;
        }
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
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
        tabDetalleDriver.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        // tabla 2
        tabDetalleDriver.setEditable(true);
        DoubleStringConverter converter = new DoubleStringConverter();
        tabcolPorcentajeDestino.setCellFactory(TextFieldTableCell.<DriverLinea, Double>forTableColumn(converter));
        tabcolPorcentajeDestino.setOnEditCommit(data -> {
            double oldValue = data.getOldValue();
            double newValue = data.getNewValue();
            data.getRowValue().setPorcentaje(newValue);
            porcentajeTotal -= oldValue;
            porcentajeTotal += newValue;
            lblSuma.setText(String.format("Suma: %.4f%%",porcentajeTotal));
        });
        // fin codigo

        List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(periodoSeleccionado, driver.getCodigo(), menuControlador.repartoTipo);
        porcentajeTotal = lstDriverLinea.stream().mapToDouble(o -> o.getPorcentaje()).sum();
        driver.setListaDriverLinea(lstDriverLinea);
        numeroRegistros = driver.getListaDriverLinea().size();
        lblSuma.setText(String.format("Suma: %.4f%%",porcentajeTotal));
        // completar datos del driver seleccionado
        txtCodigo.setText(driver.getCodigo());
        txtNombre.setText(driver.getNombre());
        tabDetalleDriver.getItems().setAll(driver.getListaDriverLinea());
        lblNumeroRegistros.setText("Número de registros: " + numeroRegistros);
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }

    @FXML void lnkDriversAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }

    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_EDITAR);
    }
    
    // TODO: Corregir el BuscarEntidad.... Hacerlo genérico
    @FXML void btnAgregarAction(ActionEvent event) {
        // repartoTipo seleccionado -> siempre será centro
        Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.stream().filter(item -> "CECO".equals(item.getCodigo())).findFirst().orElse(null);
        menuControlador.codigos = tabDetalleDriver.getItems().stream().map(i -> "'"+i.getEntidadDistribucionDestino().getCodigo()+"'").collect(Collectors.joining (","));
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_ENTIDAD.getVista()));
            BuscarEntidadControlador buscarEntidadControlador = new BuscarEntidadControlador(menuControlador, this, tipoSeleccionado, periodoSeleccionado, menuControlador.repartoTipo);
            fxmlLoader.setController(buscarEntidadControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Buscar " + tipoSeleccionado.getNombre());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        DriverLinea item = tabDetalleDriver.getSelectionModel().getSelectedItem();            
        if (item == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
        } else {
            porcentajeTotal -= item.getPorcentaje();
            lblSuma.setText(String.format("Suma: %.4f%%",porcentajeTotal));
            tabDetalleDriver.getItems().remove(item);
            menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), item.getEntidadDistribucionDestino().getCodigo() + " Driver " + driver.getCodigo(),periodoSeleccionado,Navegador.RUTAS_DRIVERS_CENTRO_EDITAR.getDireccion());
            --numeroRegistros;
            lblNumeroRegistros.setText("Número de registros: " + numeroRegistros);
        }
    }
    
    @FXML void btnGuardarAction(ActionEvent event) {
        if (Math.abs(porcentajeTotal - 100) > 0.00001) {
            menuControlador.mensaje.edit_porcent_error(titulo);
            return;
        }
        // datos
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        List<DriverLinea> lista = tabDetalleDriver.getItems();
        Date fecha = new Date();
        DriverCentro driver = new DriverCentro(codigo, nombre, null, null, lista, fecha, fecha);

        // inicio mensaje informativo
        if (driverDAO.actualizarDriverCentro(driver, periodoSeleccionado, menuControlador.repartoTipo) != 1) {
            menuControlador.mensaje.edit_error(titulo);
        } else {
            menuControlador.mensaje.edit_success(titulo);
            menuControlador.Log.editarItem(LOGGER,menuControlador.usuario.getUsername(), codigo, Navegador.RUTAS_DRIVERS_CENTRO_EDITAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
        }

    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @Override
    public void seleccionarEntidad(EntidadDistribucion entidad) {
        Date fecha = new Date();
        DriverLinea item = new DriverLinea(entidad, 0, fecha, fecha);        
        // lo agrego a la vista
        tabDetalleDriver.getItems().add(item);
        menuControlador.Log.agregarItemPeriodo(LOGGER,menuControlador.usuario.getUsername(),item.getEntidadDistribucionDestino().getCodigo() + " Driver " + driver.getCodigo(),periodoSeleccionado, Navegador.RUTAS_DRIVERS_CENTRO_EDITAR.getDireccion());
        ++numeroRegistros;
        lblNumeroRegistros.setText("Número de registros: " + numeroRegistros);
    }

    @Override
    public void seleccionarDriverCentro(DriverCentro driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void seleccionarDriverObjeto(DriverObjeto driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
