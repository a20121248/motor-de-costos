package controlador.parametrizacion.grupos.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.GrupoDAO;
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
import modelo.Grupo;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkGrupos;
    @FXML private Hyperlink lnkCatalogo;

    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnCargar;
    
    @FXML private Label lblTipoGasto;
    @FXML private ComboBox<String> cmbTipoGasto;
    @FXML private JFXButton btnBuscar;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<Grupo> tabListar;
    @FXML private TableColumn<Grupo,String> tabcolCodigo;
    @FXML private TableColumn<Grupo,String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    GrupoDAO grupoDAO;
    public MenuControlador menuControlador;
    FilteredList<Grupo> filteredData;
    SortedList<Grupo> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        grupoDAO = new GrupoDAO();
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
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(grupoDAO.listarObjetos("",menuControlador.repartoTipo)), p -> true);
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
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Inicio.",menuControlador.usuario.getUsername()));
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Parametrización.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_ASOCIAR_PERIODO);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Grupos.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Catálogo de Grupos",menuControlador.usuario.getUsername()));
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_CREAR);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Crear Grupo.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        Grupo item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Editar Grupo de Cuentas Contables", "Por favor seleccione un Grupo.");
            return;
        }
        menuControlador.objeto = item;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_EDITAR);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Editar Grupo.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        Grupo item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Eliminar Grupo de Cuentas Contables", "Por favor seleccione un Grupo.");
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Grupo de Cuentas Contables", "¿Está seguro de eliminar el Grupo " + item.getCodigo() + "?")) {
            return;
        }
        if (grupoDAO.eliminarObjeto(item.getCodigo()) != 1) {
            menuControlador.navegador.mensajeError("Eliminar Grupo de Cuentas Contables", "No se pudo eliminar el Grupo pues está siendo utilizado en otros módulos.\nPara eliminarla, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizado.");
            return;
        }
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(grupoDAO.listarObjetos("",menuControlador.repartoTipo)), p -> true);
        sortedData = new SortedList(filteredData);
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        menuControlador.navegador.mensajeInformativo("Eliminar Grupo de Cuentas Contables", "Grupo eliminado correctamente.");
        LOGGER.log(Level.INFO,String.format("El usuario %s eliminó el Grupo %s.",menuControlador.usuario.getUsername(),item.getCodigo()));
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_CARGAR);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Cargar Grupos.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_ASOCIAR_PERIODO);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Asociación.",menuControlador.usuario.getUsername()));
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
