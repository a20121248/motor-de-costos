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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;
import modelo.Centro;
import modelo.Tipo;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable {
    // Variables de la vista    
    @FXML private ComboBox<Tipo> cmbTipo;
    @FXML private ComboBox<Tipo> cmbNivel;
    
    @FXML private TextField txtBuscar;
    
    @FXML private TableView<Centro> tabListar;
    @FXML private TableColumn<Centro, String> tabcolCodigo;
    @FXML private TableColumn<Centro, String> tabcolNombre;
    @FXML private TableColumn<Centro, String> tabcolTipo;
    @FXML private TableColumn<Centro, Integer> tabcolNivel;
    @FXML private TableColumn<Centro, String> tabcolCentroPadre;
    @FXML private TableColumn<Centro, String> tabcolTipoGasto;
    @FXML private TableColumn<Centro, String> tabcolNIIF17Atribuible;
    @FXML private TableColumn<Centro, String> tabcolNIIF17Tipo;
    @FXML private TableColumn<Centro, String> tabcolNIIF17Clase;
    
    @FXML private Label lblNumeroRegistros;    
    @FXML private JFXButton btnDescargar;
    
    // Variables de la aplicacion
    CentroDAO centroDAO;
    public MenuControlador menuControlador;    
    FilteredList<Centro> filteredData;
    SortedList<Centro> sortedData;
    String tipoCodigo, nivelCodigo, buscar;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        titulo = "Centro de Costos";
        tipoCodigo = "-";
        nivelCodigo = "-";
        buscar = "";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        cmbTipo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                tipoCodigo = cmbTipo.getValue().getCodigo();
                filteredData.setPredicate(item -> {
                    return validarTipo(item, tipoCodigo) && validarNivel(item, nivelCodigo) && validarBuscar(item, buscar);
                });
                lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
            }
        });
        
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
        cmbNivel.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                nivelCodigo = cmbNivel.getValue().getCodigo();
                filteredData.setPredicate(item -> {
                    return validarTipo(item, tipoCodigo) && validarNivel(item, nivelCodigo) && validarBuscar(item, buscar);
                });
                lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
            }
        });
        
        // tabla formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolTipo.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        tabcolTipoGasto.setCellValueFactory(cellData -> cellData.getValue().tipoGastoProperty());
        tabcolNIIF17Atribuible.setCellValueFactory(cellData -> cellData.getValue().NIIF17atribuibleProperty());
        tabcolNIIF17Tipo.setCellValueFactory(cellData -> cellData.getValue().NIIF17TipoProperty());
        tabcolNIIF17Clase.setCellValueFactory(cellData -> cellData.getValue().NIIF17ClaseProperty());
        
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(centroDAO.listarObjetos()), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                buscar = newValue;
                return validarTipo(item, tipoCodigo) && validarNivel(item, nivelCodigo) && validarBuscar(item, buscar);
            });
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + sortedData.size());
    }

    private boolean validarTipo(Centro item, String tipo) {
        if (tipo.equals("-")) return true;
        else if (item.getTipo().equals(tipoCodigo)) return true;
        return false;
    }

    private boolean validarNivel(Centro item, String nivel) {
        if (nivel.equals("-")) return true;
        else if (item.getNivel() == Integer.parseInt(nivel)) return true;
        return false;
    }
    
    private boolean validarBuscar(Centro item, String buscar) {
        if (buscar == null || buscar.isEmpty()) return true;
        String buscarMinuscula = buscar.toLowerCase();
        if (item.getCodigo().toLowerCase().contains(buscarMinuscula)) return true;
        else if (item.getNombre().toLowerCase().contains(buscarMinuscula)) return true;
        else if (item.getTipo().toLowerCase().contains(buscarMinuscula)) return true;
        return false;
    }
    
    /*private void llenarTabla(String tipoCodigo, String nivelCodigo) {
        List<Centro> lista = centroDAO.listarObjetos();
        if (!"-".equals(tipoCodigo))
            lista = lista.stream().filter(item -> tipoCodigo.equals(item.getTipo().getCodigo())).collect(Collectors.toList());
        if (!"-".equals(nivelCodigo))
            lista = lista.stream().filter(item -> nivelCodigo.equals(String.valueOf(item.getNivel()))).collect(Collectors.toList());
        tabListar.getItems().setAll(lista);
        lblNumeroRegistros.setText("Número de registros: " + lista.size());
    }*/
    
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
    
    @FXML void btnCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_CREAR);
    }
    
    @FXML void btnEditarAction(ActionEvent event) {
        Centro centro = tabListar.getSelectionModel().getSelectedItem();
        if (centro == null) {
            menuControlador.mensaje.edit_empty_error(titulo);
            return;
        }
        menuControlador.objeto = centro;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_EDITAR);
    }
    
    @FXML void btnEliminarAction(ActionEvent event) {
        Centro centro = tabListar.getSelectionModel().getSelectedItem();
        if (centro == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
            return;
        }
        if (!menuControlador.navegador.mensajeConfirmar("Eliminar " + titulo, "¿Está seguro de eliminar el " + titulo + " " + centro.getCodigo() + "?")) {
            return;
        }
        if (centroDAO.verificarObjetoCentroLineas(centro.getCodigo()) == 0) {
            centroDAO.eliminarObjetoCentro(centro.getCodigo());
            filteredData.getSource().remove(centro);
            menuControlador.mensaje.delete_success(titulo);
            menuControlador.Log.deleteItem(LOGGER,menuControlador.usuario.getUsername(),centro.getCodigo(), Navegador.RUTAS_CENTROS_MAESTRO_LISTAR.getDireccion());
        } else {
            menuControlador.mensaje.delete_item_maestro_error(titulo);
        }
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_CARGAR);
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if (!tabListar.getItems().isEmpty()) {
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio("CentrosDeCostos-Catálogo", tabListar);
                descargaFile.descargarTabla(null,directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTabla(LOGGER, menuControlador.usuario.getUsername(), titulo, Navegador.RUTAS_CENTROS_MAESTRO_LISTAR.getDireccion());
            } else {
                menuControlador.mensaje.download_canceled();
            }
        }else{
            menuControlador.mensaje.download_empty();
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }
}
