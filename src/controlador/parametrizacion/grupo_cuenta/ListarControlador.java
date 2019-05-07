package controlador.parametrizacion.grupo_cuenta;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarEntidadControlador;
import dao.PlanDeCuentaDAO;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.CuentaContable;
import modelo.DriverCentro;
import modelo.DriverObjeto;
import modelo.EntidadDistribucion;
import modelo.Tipo;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkPlanDeCuentas;
    @FXML private Hyperlink lnkListar;

    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private JFXButton btnBuscarPeriodo;
    @FXML private JFXButton btnAsignar;
    @FXML private JFXButton btnQuitar;
    @FXML private JFXButton btnCargar;
    
    @FXML private Label lblTipoGasto;
    @FXML private ComboBox<String> cmbTipoGasto;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<CuentaContable> tabListar;
    @FXML private TableColumn<CuentaContable, String> tabcolCodigoCuenta;
    @FXML private TableColumn<CuentaContable, String> tabcolNombreCuenta;
    @FXML private TableColumn<CuentaContable, String> tabcolCodigoGrupo;
    @FXML private TableColumn<CuentaContable, String> tabcolNombreGrupo;
    
    @FXML private Label lblNumeroRegistros;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    PlanDeCuentaDAO planDeCuentaDAO;
    public MenuControlador menuControlador;
    CuentaContable cuentaSeleccionada;
    FilteredList<CuentaContable> filteredData;
    SortedList<CuentaContable> sortedData;
    int periodoSeleccionado;
    String tipoGasto;
    boolean tablaEstaActualizada;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPO_CUENTA_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        tipoGasto = (String) menuControlador.objeto;
        planDeCuentaDAO = new PlanDeCuentaDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ocultar para Ingresos Operativos
        if (menuControlador.repartoTipo == 2) {
            lblTipoGasto.setVisible(false);
            cmbTipoGasto.setVisible(false);
        }
        // Combo para Tipo de Gasto
        List<String> lstTipoGasto = new ArrayList(Arrays.asList("Todos","Administrativo","Operativo"));
        cmbTipoGasto.getItems().addAll(lstTipoGasto);
        cmbTipoGasto.getSelectionModel().select(tipoGasto);
        cmbTipoGasto.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                tablaEstaActualizada = false;
            }
        });
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                tablaEstaActualizada = false;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                tablaEstaActualizada = false;
            }
        });
        // Periodo seleccionado
        periodoSeleccionado = menuControlador.periodo;
        // Tabla: Formato
        tabcolCodigoCuenta.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombreCuenta.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolCodigoGrupo.setCellValueFactory(cellData -> cellData.getValue().getGrupo().codigoProperty());
        tabcolNombreGrupo.setCellValueFactory(cellData -> cellData.getValue().getGrupo().nombreProperty());        
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoCuenta.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreCuenta.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        tabcolCodigoGrupo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreGrupo.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(planDeCuentaDAO.listarPlanDeCuentaConGrupo(periodoSeleccionado,cmbTipoGasto.getValue(),menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getGrupo().getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getGrupo().getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + sortedData.size());
        tablaEstaActualizada = true;
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }

    @FXML void lnkAsignacionesAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPO_CUENTA_LISTAR);
    }
    
    @FXML void btnAsignarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Asignar Grupo de Cuentas Contables", "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        cuentaSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (cuentaSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar Grupo de Cuentas Contables", "Por favor seleccione una Cuenta Contable.");
            return;
        }
        if (!cuentaSeleccionada.getGrupo().getCodigo().equals("Sin grupo asignado")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar Grupo de Cuentas Contables", "La Cuenta Contable ya cuenta con un Grupo asignado.\n¿Está seguro que desea reemplazar dicho Grupo?")) {
                buscarGrupo();
            }
            return;
        }
        buscarGrupo();
    }
    
    private void buscarGrupo() {
        Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.stream().filter(item -> "GCTA".equals(item.getCodigo())).findFirst().orElse(null);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_ENTIDAD.getVista()));
            BuscarEntidadControlador buscarEntidadControlador = new BuscarEntidadControlador(menuControlador, this, tipoSeleccionado, periodoSeleccionado, menuControlador.repartoTipo);
            fxmlLoader.setController(buscarEntidadControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(String.format("Agregar %s para el periodo de %s de %d",tipoSeleccionado.getNombre(),cmbMes.getValue(),spAnho.getValue()));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Quitar asignación", "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        cuentaSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (cuentaSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Quitar asignación", "Por favor seleccione una Cuenta Contable.");
            return;
        }
        
        if (cuentaSeleccionada.getGrupo().getCodigo().equals("Sin driver asignado"))
            return;
        
        if (!menuControlador.navegador.mensajeConfirmar("Quitar asignación", "¿Está seguro de quitar el Grupo " + cuentaSeleccionada.getGrupo().getNombre() + "?"))
            return;
        
        planDeCuentaDAO.borrarCuentaGrupo(cuentaSeleccionada.getCodigo(), periodoSeleccionado);
        buscarPeriodo(periodoSeleccionado, false);
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPO_CUENTA_CARGAR);
    }
    
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<CuentaContable> lista = planDeCuentaDAO.listarPlanDeCuentaConGrupo(periodo,cmbTipoGasto.getValue(),menuControlador.repartoTipo);
        if (lista.isEmpty() && mostrarMensaje)
            menuControlador.navegador.mensajeInformativo("Consulta de asignaciones de Cuentas Contables", "No existen Cuentas Contables para el periodo seleccionado.");
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        txtBuscar.setText("");
        tablaEstaActualizada = true;
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @Override
    public void seleccionarEntidad(EntidadDistribucion entidad) {
        if (cuentaSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar Grupo de Cuentas Contables", "Por favor seleccione una Cuenta Contable.");
            return;
        }
        planDeCuentaDAO.insertarCuentaGrupo(cuentaSeleccionada.getCodigo(), entidad.getCodigo(), periodoSeleccionado);
        buscarPeriodo(periodoSeleccionado, false);
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
