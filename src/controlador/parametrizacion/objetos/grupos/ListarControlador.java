package controlador.parametrizacion.objetos.grupos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoGrupoDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import modelo.Grupo;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkGrupos;

    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnCargar;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<Grupo> tabListar;
    @FXML private TableColumn<Grupo, String> tabcolCodigo;
    @FXML private TableColumn<Grupo, String> tabcolNombre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivel;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    ObjetoGrupoDAO objetoGrupoDAO;
    public MenuControlador menuControlador;
    FilteredList<Grupo> filteredData;
    SortedList<Grupo> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoGrupoDAO = new ObjetoGrupoDAO(menuControlador.objetoTipo);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Grupos de Oficinas");
                lnkObjetos.setText("Oficinas");
                break;
            case "BAN":
                lblTitulo.setText("Grupos de Bancas");
                lnkObjetos.setText("Bancas");
                break;
            case "PRO":
                lblTitulo.setText("Grupos de Productos");
                lnkObjetos.setText("Productos");
                break;
            case "SCA":
                lblTitulo.setText("Grupos de Subcanales");
                lnkObjetos.setText("Subcanales");
                break;
            default:
                break;
        }
        // tabla: dimensiones
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 70);
        tabcolNivel.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        // tabla: formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(objetoGrupoDAO.listarObjetos(-1)), p -> true);
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
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkObjetosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_CREAR);
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        Grupo item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Editar Grupo", "Por favor seleccione un Grupo.");
            return;
        }
        menuControlador.objeto = item;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        Grupo item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Eliminar Grupo", "Por favor seleccione un Grupo.");
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Grupo", "¿Está seguro de eliminar el Grupo " + item.getCodigo() + "?")) {
            return;
        }
        if (objetoGrupoDAO.eliminarObjeto(item.getCodigo()) != 1) {
            menuControlador.navegador.mensajeError("Eliminar Grupo", "No se pudo eliminar el Grupo pues está siendo utilizado en otros módulos.\nPara eliminarla, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizado.");
            return;
        }
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(objetoGrupoDAO.listarObjetos(-1)), p -> true);
        sortedData = new SortedList(filteredData);
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        menuControlador.navegador.mensajeInformativo("Eliminar Grupo", "Grupo eliminado correctamente.");
        LOGGER.log(Level.INFO,String.format("El usuario %s eliminó el Grupo %s.",menuControlador.usuario.getUsername(),item.getCodigo()));
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_CARGAR);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
}
