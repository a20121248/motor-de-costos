package controlador.parametrizacion.planes.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PlanDeCuentaDAO;
import java.io.File;
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
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import modelo.CuentaContable;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkPlanDeCuentas;
    @FXML private Hyperlink lnkCatalogo;

    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnCargar;
    
    @FXML private Label lblTipoGasto;
    @FXML private ComboBox<String> cmbTipoGasto;
    @FXML private JFXButton btnBuscar;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<CuentaContable> tabListar;
    @FXML private TableColumn<CuentaContable, String> tabcolCodigo;
    @FXML private TableColumn<CuentaContable, String> tabcolNombre;
    @FXML private TableColumn<CuentaContable, Boolean> tabcolEstado;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargar;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    PlanDeCuentaDAO planDeCuentaDAO;
    public MenuControlador menuControlador;
    FilteredList<CuentaContable> filteredData;
    SortedList<CuentaContable> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PLANES_MAESTRO_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ocultar para Ingresos Operativos
        if (menuControlador.repartoTipo == 2) {
            lblTipoGasto.setVisible(false);
            cmbTipoGasto.setVisible(false);
            btnBuscar.setVisible(false);
        }
        // Combo para Tipo de Gasto
        List<String> lstTipoGasto = new ArrayList(Arrays.asList("Todos","Administrativo","Operativo"));
        cmbTipoGasto.getItems().addAll(lstTipoGasto);
        cmbTipoGasto.getSelectionModel().select(0);
        // Tabla: Formato
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 85);
        //tabcolEstado.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        /*tabcolEstado.setCellValueFactory(cellData -> cellData.getValue().estaActivaProperty());
        tabcolEstado.setCellFactory(column -> {
            return new TableCell<CuentaContable, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        if (item) {
                            setText("ACTIVA");
                            setTextFill(Color.GREEN);
                        } else {
                            setText("INACTIVA");
                            setTextFill(Color.RED);
                        }
                    }
                }
            };
        });*/
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(planDeCuentaDAO.listarObjetoCuentas(menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                boolean cond;
                switch (cmbTipoGasto.getValue()) {
                    case "Administrativo":
                        cond = item.getCodigo().substring(0,2).equals("45");
                        break;
                    case "Operativo":
                        cond = item.getCodigo().substring(0,2).equals("44");
                        break;
                    default:
                        cond = true;
                }
                if (!cond) return false;
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
        
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkPlanDeCuentasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_CREAR);
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        CuentaContable item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Editar Cuenta Contable", "Por favor seleccione una Cuenta Contable.");
            return;
        }
        menuControlador.objeto = item;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        CuentaContable item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Eliminar Cuenta Contable", "Por favor seleccione una Cuenta Contable.");
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Cuenta Contable", "¿Está seguro de eliminar la Cuenta Contable " + item.getCodigo() + "?")) {
            return;
        }
        
        if(planDeCuentaDAO.verificarObjetoPlanCuenta(item.getCodigo()) == 0){
            planDeCuentaDAO.eliminarObjetoCuenta(item.getCodigo());
            txtBuscar.setText("");
            filteredData = new FilteredList(FXCollections.observableArrayList(planDeCuentaDAO.listarObjetoCuentas(menuControlador.repartoTipo)), p -> true);
            sortedData = new SortedList(filteredData);
            tabListar.setItems(sortedData);
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
            menuControlador.navegador.mensajeInformativo("Eliminar Cuenta Contable", "Grupo eliminado correctamente.");
            LOGGER.log(Level.INFO,String.format("El usuario %s eliminó la Cuenta Contable %s.",menuControlador.usuario.getUsername(),item.getCodigo()));
        }else{
            menuControlador.navegador.mensajeError("Eliminar Cuenta Contable", "No se pudo eliminar la Cuenta Contable pues está siendo utilizada en otros módulos.\nPara eliminarla, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizada.");
        }
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_CARGAR);
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio("CuentasContables-Catálogo", tabListar);
                descargaFile.DescargarTabla(null,directorioSeleccionado.getAbsolutePath());
            }else{
                menuControlador.navegador.mensajeInformativo("Descargar Información", "Canceló la descarga");
            }
        }else{
            menuControlador.navegador.mensajeInformativo("Descargar Información", "No hay información.");
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO);
    }
    
    @FXML void btnBuscarAction(ActionEvent event) {
        filteredData.setPredicate(item -> {
            switch (cmbTipoGasto.getValue()) {
                case "Administrativo":
                    return item.getCodigo().substring(0,2).equals("45");
                case "Operativo":
                    return item.getCodigo().substring(0,2).equals("44");
                default:
                    return true;
            }
        });
        txtBuscar.setText("");
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
    }
}
