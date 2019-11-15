package controlador.parametrizacion.planes.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PlanDeCuentaDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    
//    @FXML private Label lblTipoGasto;
//    @FXML private ComboBox<String> cmbTipoGasto;
//    @FXML private JFXButton btnBuscar;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<CuentaContable> tabListar;
    @FXML private TableColumn<CuentaContable, String> tabcolCodigo;
    @FXML private TableColumn<CuentaContable, String> tabcolNombre;
    @FXML private TableColumn<CuentaContable, Boolean> tabcolEstado;
    @FXML private TableColumn<CuentaContable, String> tabcolTipoGasto;
    @FXML private TableColumn<CuentaContable, String> tabcolNIIF17Atribuible;
    @FXML private TableColumn<CuentaContable, String> tabcolNIIF17Tipo;
    @FXML private TableColumn<CuentaContable, String> tabcolNIIF17Clase;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargar;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    PlanDeCuentaDAO planDeCuentaDAO;
    public MenuControlador menuControlador;
    FilteredList<CuentaContable> filteredData;
    SortedList<CuentaContable> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PLANES_MAESTRO_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        this.titulo = "Cuenta Contable";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ocultar para Ingresos Operativos
        if (menuControlador.repartoTipo == 2) {

        }
        
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolTipoGasto.setCellValueFactory(cellData -> cellData.getValue().tipoGastoProperty());
        tabcolNIIF17Atribuible.setCellValueFactory(cellData -> cellData.getValue().NIIF17AtribuibleProperty());
        tabcolNIIF17Tipo.setCellValueFactory(cellData -> cellData.getValue().NIIF17TipoProperty());
        tabcolNIIF17Clase.setCellValueFactory(cellData -> cellData.getValue().NIIF17ClaseProperty());
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(planDeCuentaDAO.listarObjetoCuentas(menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getTipoGasto().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNIIF17Atribuible().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNIIF17Tipo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNIIF17Clase().toLowerCase().contains(lowerCaseFilter)) return true;
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
            menuControlador.mensaje.edit_empty_error(titulo);
            return;
        }
        menuControlador.objeto = item;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        CuentaContable item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Cuenta Contable", "¿Está seguro de eliminar la Cuenta Contable " + item.getCodigo() + "?")) {
            return;
        }
        
        if(planDeCuentaDAO.verificarObjetoPlanCuentaLineas(item.getCodigo()) == 0){
            planDeCuentaDAO.eliminarObjetoCuenta(item.getCodigo());
            txtBuscar.setText("");
            filteredData = new FilteredList(FXCollections.observableArrayList(planDeCuentaDAO.listarObjetoCuentas(menuControlador.repartoTipo)), p -> true);
            sortedData = new SortedList(filteredData);
            tabListar.setItems(sortedData);
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
            menuControlador.mensaje.delete_success(titulo);
            menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),item.getCodigo(), Navegador.RUTAS_PLANES_MAESTRO_LISTAR.getDireccion());
        }else{
            menuControlador.mensaje.delete_item_maestro_error(titulo);
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
                descargaFile.descargarTabla(null,directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTabla(LOGGER, menuControlador.usuario.getUsername(), titulo, Navegador.RUTAS_PLANES_MAESTRO_LISTAR.getDireccion());
            }else{
                menuControlador.mensaje.download_canceled();
            }
        }else{
            menuControlador.mensaje.download_empty();
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO);
    }
    
    @FXML void btnBuscarAction(ActionEvent event) {
        txtBuscar.setText("");
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
    }
}
