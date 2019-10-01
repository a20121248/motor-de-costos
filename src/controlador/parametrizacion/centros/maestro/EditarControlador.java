package controlador.parametrizacion.centros.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import modelo.Centro;
import modelo.Tipo;

public class EditarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkCentros;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkEditar;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Tipo> cmbTipo;
    @FXML private ComboBox<Tipo> cmbNivel;
    @FXML private TextField txtCodigoCecoPadre;
    @FXML private ComboBox cmbEsBolsa;
    @FXML private ComboBox cmbTipoGasto;
    @FXML private ComboBox cmbNIIF17Atribuible;
    @FXML private ComboBox cmbNIIF17Tipo;
    @FXML private ComboBox cmbNIIF17Clase;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    Centro centro;
    CentroDAO centroDAO;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_MAESTRO_EDITAR.getControlador());
    String titulo;
    
    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centro = (Centro) menuControlador.objeto;
        centroDAO = new CentroDAO();
        this.titulo = "Centro de Costos";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        txtCodigo.setText(centro.getCodigo());
        txtNombre.setText(centro.getNombre());
        
        ObservableList<Tipo> obsListaTipos;

        obsListaTipos = FXCollections.observableList(menuControlador.lstCentroTipos.subList(1, menuControlador.lstCentroTipos.size()));
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
        cmbTipo.setValue(centro.getTipo());
        
        obsListaTipos = FXCollections.observableList(menuControlador.lstCentroNiveles.subList(1, menuControlador.lstCentroNiveles.size()));
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
        cmbNivel.getSelectionModel().select(centro.getNivel());
        
        cmbEsBolsa.setItems(FXCollections.observableArrayList(menuControlador.lstEsBolsa));
        cmbEsBolsa.getSelectionModel().select(centro.getEsBolsa());
        
        cmbTipoGasto.setItems(FXCollections.observableArrayList(menuControlador.lstTipoGasto));
        if(centro.getTipoGasto().equals("DIRECTO")) cmbTipoGasto.getSelectionModel().select(1);
        else cmbTipoGasto.getSelectionModel().select(0);
        
        
        cmbNIIF17Atribuible.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Atribuible));
        cmbNIIF17Atribuible.getSelectionModel().select(centro.getNIIF17Atribuible());
        
        cmbNIIF17Tipo.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Tipo));
        cmbNIIF17Tipo.getSelectionModel().select(centro.getNIIF17Tipo());
        
        cmbNIIF17Clase.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Clase));
        cmbNIIF17Clase.getSelectionModel().select(centro.getNIIF17Clase());
        
        cmbTipo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.getCodigo().equals("BOLSA") || newValue.getCodigo().equals("OFICINA")) {
                cmbEsBolsa.getSelectionModel().select(1);
                cmbNivel.getSelectionModel().select(0);
            }
            if (newValue.getCodigo().equals("PROYECTO") || newValue.getCodigo().equals("FICTICIO")) {
                cmbEsBolsa.getSelectionModel().select(0);
                cmbNivel.getSelectionModel().select(99);
            }
        });
        
        cmbNivel.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.getCodigo().equals("0")) {
                cmbEsBolsa.getSelectionModel().select(1);
            } else {
                cmbEsBolsa.getSelectionModel().select(0);
            }
        });
        
        cmbEsBolsa.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.equals("SI")) {
                cmbNivel.getSelectionModel().select(0);
            }
        });
        
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
    
    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_EDITAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        String codigoGrupo = cmbTipo.getValue().getCodigo();
        int nivel = Integer.parseInt(cmbNivel.getValue().getCodigo());
        String cecoPadreCodigo = txtCodigoCecoPadre.getText();
        String esBolsa = cmbEsBolsa.getValue().toString();
        int tipoGasto = cmbTipoGasto.getSelectionModel().getSelectedIndex();
        String niif17Atribuible = cmbNIIF17Atribuible.getValue().toString();
        String niif17Tipo = cmbNIIF17Tipo.getValue().toString();
        String niif17Clase = cmbNIIF17Clase.getValue().toString();
        if (centroDAO.actualizarObjeto(codigo, nombre,codigoGrupo,nivel,cecoPadreCodigo,esBolsa,tipoGasto,niif17Atribuible,niif17Tipo,niif17Clase)==1) {
            menuControlador.mensaje.edit_success(titulo);
            menuControlador.Log.editarItem(LOGGER,menuControlador.usuario.getUsername(), codigo, Navegador.RUTAS_CENTROS_MAESTRO_EDITAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
        } else {
            menuControlador.mensaje.edit_error(titulo);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
}
