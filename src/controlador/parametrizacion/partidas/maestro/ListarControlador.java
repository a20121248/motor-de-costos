package controlador.parametrizacion.partidas.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PartidaDAO;
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
import modelo.Partida;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkPartidas;
    @FXML private Hyperlink lnkCatalogo;

    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnCargar;
    
    @FXML private Label lblTipoGasto;
    @FXML private ComboBox<String> cmbTipoGasto;
    @FXML private JFXButton btnBuscar;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<Partida> tabListar;
    @FXML private TableColumn<Partida,String> tabcolCodigo;
    @FXML private TableColumn<Partida,String> tabcolNombre;
    @FXML private TableColumn<Partida,String> tabcolGrupoGasto;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargar;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    PartidaDAO partidaDAO;
    public MenuControlador menuControlador;
    FilteredList<Partida> filteredData;
    SortedList<Partida> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PARTIDAS_MAESTRO_LISTAR.getControlador());
    String titulo;

    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        partidaDAO = new PartidaDAO();
        this.titulo = "Partidas";
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
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 70);
        tabcolGrupoGasto.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolGrupoGasto.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(partidaDAO.listarObjetos("",menuControlador.repartoTipo)), p -> true);
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
    
    @FXML void lnkPartidasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_ASOCIAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_LISTAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_CREAR);
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        Partida item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_EDIT_EMPTY);
            return;
        }
        menuControlador.objeto = item;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        Partida item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_DELETE_SELECTED);
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar Partida de Cuentas Contables", "¿Está seguro de eliminar el Partida " + item.getCodigo() + "?")) {
            return;
        }
        
        if(partidaDAO.verificarObjetoPartida(item.getCodigo()) == 0){
            partidaDAO.eliminarObjeto(item.getCodigo());
            txtBuscar.setText("");
            filteredData = new FilteredList(FXCollections.observableArrayList(partidaDAO.listarObjetos("",menuControlador.repartoTipo)), p -> true);
            sortedData = new SortedList(filteredData);
            tabListar.setItems(sortedData);
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_DELETE_SUCCESS);
            menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),item.getCodigo(), Navegador.RUTAS_PLANES_MAESTRO_LISTAR.getDireccion());
        }else{
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_DELETE_ITEM);
        }
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_CARGAR);
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio("Partidas-Catálogo", tabListar);
                descargaFile.descargarTabla(null,directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTabla(LOGGER, menuControlador.usuario.getUsername(), titulo, Navegador.RUTAS_PLANES_MAESTRO_LISTAR.getDireccion());
            }else{
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_CANCELED);
            }
        }else{
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_EMPTY);
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_ASOCIAR_PERIODO);
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

