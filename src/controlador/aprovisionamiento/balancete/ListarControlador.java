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
import dao.PlanDeCuentaDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
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
import modelo.CuentaContable;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;    
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkBalancete;

    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private JFXButton btnBuscarPeriodo;
    @FXML private Label lblNumeroRegistros;

    @FXML private Label lblTipoGasto;
    @FXML private ComboBox<String> cmbTipoGasto;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<CuentaContable> tabListar;
    @FXML private TableColumn<CuentaContable, String> tabcolCodigo;
    @FXML private TableColumn<CuentaContable, String> tabcolNombre;
    @FXML private TableColumn<CuentaContable, Double> tabcolSaldo;
    
    @FXML private JFXButton btnCargar;
    
    // Variables de la aplicacion
    PlanDeCuentaDAO planDeCuentaDAO;
    MenuControlador menuControlador;
    FilteredList<CuentaContable> filteredData;
    SortedList<CuentaContable> sortedData;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_BALANCETE_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        planDeCuentaDAO = new PlanDeCuentaDAO();
        this.menuControlador = menuControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (menuControlador.repartoTipo == 2) {
            lblTipoGasto.setVisible(false);
            cmbTipoGasto.setVisible(false);
        }
        
        // Combo para Tipo de Gasto
        List<String> lstTipoGasto = new ArrayList(Arrays.asList("Todos","Administrativo","Operativo"));
        cmbTipoGasto.getItems().addAll(lstTipoGasto);
        cmbTipoGasto.getSelectionModel().select(0);
        // Botones para periodo
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
        // Tabla: Formato
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        tabcolSaldo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolSaldo.setCellValueFactory(cellData -> cellData.getValue().saldoAcumuladoProperty().asObject());
        tabcolSaldo.setCellFactory(column -> {
                return new TableCell<CuentaContable, Double>() {
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
        filteredData = new FilteredList(FXCollections.observableArrayList(planDeCuentaDAO.listar(periodoSeleccionado,cmbTipoGasto.getValue(),menuControlador.repartoTipo)), p -> true);
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
    
    // Acción del botón con ícono de lupa
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }

    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<CuentaContable> lista = planDeCuentaDAO.listar(periodo,cmbTipoGasto.getValue(),menuControlador.repartoTipo);
        if (lista.isEmpty() && mostrarMensaje)
            menuControlador.navegador.mensajeInformativo("Consulta de Balancete", "No existen Cuentas Contables para el periodo seleccionado.");
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
