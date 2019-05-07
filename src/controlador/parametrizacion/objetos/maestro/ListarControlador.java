package controlador.parametrizacion.objetos.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoDAO;
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
import modelo.EntidadDistribucion;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkCatalogo;

    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnCargar;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<EntidadDistribucion> tabListar;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolCodigo;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    String objetoNombre1,objetoNombre2;
    ObjetoDAO objetoDAO;
    public MenuControlador menuControlador;
    FilteredList<EntidadDistribucion> filteredData;
    SortedList<EntidadDistribucion> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Oficinas");
                lnkObjetos.setText("Oficinas");
                objetoNombre1 = "Oficina";
                objetoNombre2 = "la Oficina";
                break;
            case "BAN":
                lblTitulo.setText("Bancas");
                lnkObjetos.setText("Bancas");
                objetoNombre1 = "Banca";
                objetoNombre2 = "la Banca";
                break;
            case "PRO":
                lblTitulo.setText("Productos");
                lnkObjetos.setText("Productos");
                objetoNombre1 = "Producto";
                objetoNombre2 = "el Producto";
                break;
            default:
                break;
        }
        // tabla: dimensiones
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 85);
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(objetoDAO.listarObjetos()), p -> true);
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
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_CREAR);
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        EntidadDistribucion item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Editar " + objetoNombre1, "Por favor seleccione un " + objetoNombre1);
            return;
        }
        menuControlador.objeto = item;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        EntidadDistribucion item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Eliminar " + objetoNombre1, "Por favor seleccione " + objetoNombre2 + " a eliminar.");
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar " + objetoNombre1, "¿Está seguro de eliminar " + objetoNombre2 + " " + item.getCodigo() + "?")) {
            return;
        }
        if (objetoDAO.eliminarObjeto(item.getCodigo()) != 1) {
            menuControlador.navegador.mensajeError("Eliminar " + objetoNombre1, "No se pudo eliminar " + objetoNombre2 + " pues está siendo utilizado en otros módulos.\nPara eliminarla, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizado.");
            return;
        }
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(objetoDAO.listarObjetos()), p -> true);
        sortedData = new SortedList(filteredData);
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        menuControlador.navegador.mensajeInformativo("Eliminar " + objetoNombre1, objetoNombre1 + " eliminado correctamente.");
        LOGGER.log(Level.INFO,String.format("El usuario %s eliminó el %s %s.",objetoNombre1,menuControlador.usuario.getUsername(),item.getCodigo()));
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_CARGAR);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
}
