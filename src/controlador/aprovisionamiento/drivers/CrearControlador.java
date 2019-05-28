package controlador.aprovisionamiento.drivers;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarEntidadControlador;
import dao.DriverDAO;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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

public class CrearControlador implements Initializable,ObjetoControladorInterfaz {
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDrivers;
    @FXML private Hyperlink lnkCrear;
    
    @FXML public ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtareaDescripcion;
    
    @FXML private Label lblEntidades;
    @FXML private JFXButton btnAgregar;
    @FXML private JFXButton btnQuitar;
    @FXML private TableView<DriverLinea> tabDetalleDriver;
    @FXML private TableColumn<DriverLinea, String> tabcolCodigoDestino;
    @FXML private TableColumn<DriverLinea, String> tabcolNombreDestino;
    @FXML private TableColumn<DriverLinea, Double> tabcolPorcentajeDestino;
    @FXML private Label lblNumeroRegistros;
    @FXML private Label lblSuma;
    
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnCancelar;
    
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    DriverDAO driverDAO;
    int numeroRegistros;
    int periodoSeleccionado;
    double porcentajeTotal;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_CENTRO_CREAR.getControlador());
    String titulo, titulo1;
    
    
    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        numeroRegistros = 0;
        this.titulo = "Drivers";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Centros de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo1 = "Centros de Beneficio";
            lblTitulo.setText("Drivers - " + titulo1);
            lnkDrivers.setText("Drivers - " + titulo1);
            lblEntidades.setText(titulo1 + " a distribuir");
        }
        
        // codigo: setear las propiedades de la tabla agregar drivers, entre ellas el formato del porcentaje
        tabcolCodigoDestino.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().codigoProperty());
        tabcolNombreDestino.setCellValueFactory(cellData -> cellData.getValue().getEntidadDistribucionDestino().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        
        tabDetalleDriver.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoDestino.setMaxWidth(1f * Integer.MAX_VALUE * 12);
        tabcolNombreDestino.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        tabcolPorcentajeDestino.setMaxWidth(1f * Integer.MAX_VALUE * 28);
        
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
        
        // iniciarlizar el combo de periodoSeleccionado
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

    @FXML void lnkCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_CREAR);
    }
    
    @FXML void btnAgregarAction(ActionEvent event) {
        // repartoTipo seleccionado -> siempre será centro de costo
        //Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.get(2);
        Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.stream().filter(item -> "CECO".equals(item.getCodigo())).findFirst().orElse(null);
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
        } catch(IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        DriverLinea item = tabDetalleDriver.getSelectionModel().getSelectedItem();            
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Seleccionar línea del driver", "No seleccionó ninguna línea del driver.");
        } else {
            porcentajeTotal -= item.getPorcentaje();
            lblSuma.setText(String.format("Suma: %.4f%%",porcentajeTotal));
            tabDetalleDriver.getItems().remove(item);
            --numeroRegistros;
            lblNumeroRegistros.setText("Número de registros: " + numeroRegistros);
        }
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        // validar que se haya insertado el detalle
        List<DriverLinea> lstDriverLinea = tabDetalleDriver.getItems();
        if (lstDriverLinea == null || lstDriverLinea.isEmpty()) {
            menuControlador.navegador.mensajeInformativo("Crear Driver", "Por favor ingrese como mínimo un Centro de Costo a Distribuir.");
            return;
        }        
        // validar suma 100%
        double porcTotal = lstDriverLinea.stream().mapToDouble(o -> o.getPorcentaje()).sum();
        if (Math.abs(porcTotal - 100) > 0.00001) {
            menuControlador.navegador.mensajeError("Crear Driver", "Los porcentajes de distribución deben sumar 100%.");
            return;
        }
        // validar el codigo
        String codigo = txtCodigo.getText();
        if (codigo.isEmpty()) {
            // TODO: validar que el codigo no exista
            menuControlador.navegador.mensajeInformativo("Crear Driver", "Por favor ingrese un código para el Driver.");
            return;
        }
        // validar el nombre
        String nombre = txtNombre.getText();
        if (nombre.isEmpty()) {
            menuControlador.navegador.mensajeInformativo("Crear Driver", "Por favor ingrese un nombre para el Driver.");
            return;
        }
        // validar que se haya seleccionado el repartoTipo origen
        String descripcion = txtareaDescripcion.getText();
        if (descripcion.isEmpty()) {
            menuControlador.navegador.mensajeInformativo("Crear Driver", "Por favor ingrese una descripción para el Driver.");
            return;
        }
        try {
            // creamos el driver
            Date fecha = new Date();
            DriverCentro driver = new DriverCentro(codigo, nombre, descripcion, null, lstDriverLinea, fecha, fecha);
            
            // lo insertamos en la base de datos
            int mesSeleccionado = cmbMes.getSelectionModel().getSelectedIndex() + 1;
            int anhoSeleccionado = spAnho.getValue();
            int periodo = anhoSeleccionado*100 + mesSeleccionado;
            // inicio mensaje informativo
            if (driverDAO.insertarDriverCentro(driver, periodo,menuControlador.repartoTipo) != 1) {
                menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_CREATE_ERROR);
            } else {
                menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_CREATE_SUCCESS);
                menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), codigo + " con " + driver.getListaDriverLinea().size() + " elementos ", periodo, Navegador.RUTAS_DRIVERS_CENTRO_CREAR.getDireccion());
                menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }

    @Override
    public void seleccionarEntidad(EntidadDistribucion entidad) {
        Date fecha = new Date();
        DriverLinea driverLinea = new DriverLinea(entidad, 0, fecha, fecha);        
        // lo agrego a la vista
        tabDetalleDriver.getItems().add(driverLinea);
        ++numeroRegistros;
        lblNumeroRegistros.setText("Número de registros: " + numeroRegistros);
    }

    @Override
    public void seleccionarDriverCentro(DriverCentro driver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void seleccionarDriverObjeto(DriverObjeto driver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
