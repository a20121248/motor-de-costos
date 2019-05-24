package controlador.parametrizacion.centros.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;
import modelo.Centro;
import modelo.Tipo;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkCentros;
    @FXML private Hyperlink lnkCatalogo;
    
    @FXML private ComboBox<Tipo> cmbTipo;
    @FXML private ComboBox<Tipo> cmbNivel;
    @FXML private JFXButton btnFiltrar;

    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnEditar;
    @FXML private JFXButton btnEliminar;
    @FXML private JFXButton btnCargar;
    
    @FXML private TableView<Centro> tabListar;
    @FXML private TableColumn<Centro, String> tabcolCodigo;
    @FXML private TableColumn<Centro, String> tabcolNombre;
    @FXML private TableColumn<Centro, String> tabcolGrupo;
    @FXML private TableColumn<Centro, Integer> tabcolNivel;
    @FXML private TableColumn<Centro, String> tabcolCentroPadre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargar;
    
    // Variables de la aplicacion
    CentroDAO centroDAO;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        titulo = "Centro de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo = "Centro de Beneficio";
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (menuControlador.repartoTipo == 2) {
            lblTitulo.setText("Centros de Beneficio");
            lnkCentros.setText("Centros de Beneficio");
        }
        
        ObservableList<Tipo> obsListaTipos;
        
        obsListaTipos = FXCollections.observableList(menuControlador.lstCentroTipos);
        cmbTipo.setItems(obsListaTipos);
        cmbTipo.setConverter(new StringConverter<Tipo>() {
            @Override
            public String toString(Tipo object) {
                return object.getNombre();
            }
            @Override
            public Tipo fromString(String string) {
                return cmbTipo.getItems().stream().filter(ap -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
        cmbTipo.getSelectionModel().select(0);
        
        obsListaTipos = FXCollections.observableList(menuControlador.lstCentroNiveles);
        cmbNivel.setItems(obsListaTipos);
        cmbNivel.setConverter(new StringConverter<Tipo>() {
            @Override
            public String toString(Tipo object) {
                return object.getNombre();
            }
            @Override
            public Tipo fromString(String string) {
                return cmbNivel.getItems().stream().filter(ap -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
        cmbNivel.getSelectionModel().select(0);
        
        // tabla dimensiones
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigo.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth( 1f * Integer.MAX_VALUE * 40);
        tabcolGrupo.setMaxWidth( 1f * Integer.MAX_VALUE * 20);
        tabcolNivel.setMaxWidth( 1f * Integer.MAX_VALUE * 10);
        tabcolCentroPadre.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        // tabla formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolGrupo.setCellValueFactory(cellData -> cellData.getValue().getTipo().nombreProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        //tabcolCentroPadre.setCellValueFactory(cellData -> cellData.getValue().);        
        // tabla completar items
        llenarTabla("-","-");
    }
    
    private void llenarTabla(String tipoCodigo, String nivelCodigo) {
        List<Centro> lista = centroDAO.listarObjetos(menuControlador.repartoTipo);
        if (!"-".equals(tipoCodigo))
            lista = lista.stream().filter(item -> tipoCodigo.equals(item.getTipo().getCodigo())).collect(Collectors.toList());
        if (!"-".equals(nivelCodigo))
            lista = lista.stream().filter(item -> nivelCodigo.equals(String.valueOf(item.getNivel()))).collect(Collectors.toList());
        tabListar.getItems().setAll(lista);
        lblNumeroRegistros.setText("Número de registros: " + lista.size());
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkCentrosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
    
    @FXML void btnFiltrarAction(ActionEvent event) {
        llenarTabla(cmbTipo.getValue().getCodigo(),cmbNivel.getValue().getCodigo());
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_CREAR);
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        Centro centro = tabListar.getSelectionModel().getSelectedItem();
        if (centro == null) {
            menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_EDIT_EMPTY );
            return;
        }
        menuControlador.objeto = centro;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        Centro centro = tabListar.getSelectionModel().getSelectedItem();
        if (centro == null) {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_DELETE_SELECTED);
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar " + titulo, "¿Está seguro de eliminar el " + titulo + " " + centro.getCodigo() + "?")) {
            return;
        }
        if(centroDAO.verificarObjetoCentro(centro.getCodigo()) == 0){
            centroDAO.eliminarObjetoCentro(centro.getCodigo());
            llenarTabla(cmbTipo.getValue().getCodigo(),cmbNivel.getValue().getCodigo());
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_DELETE_SUCCESS);
            menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),centro.getCodigo(), Navegador.RUTAS_CENTROS_MAESTRO_LISTAR.getDireccion());

        }else{
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_DELETE_ITEM);
        }     
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_CARGAR);
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio("CentrosDeCostos-Catálogo", tabListar);
                descargaFile.descargarTabla(null,directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTabla(LOGGER, menuControlador.usuario.getUsername(), titulo, Navegador.RUTAS_CENTROS_MAESTRO_LISTAR.getDireccion());
            }else{
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_CANCELED);
            }
        }else{
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_EMPTY);
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }
}
