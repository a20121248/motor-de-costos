package controlador.parametrizacion.centros.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import modelo.Tipo;

public class CrearControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkCentros;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkCrear;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Tipo> cmbTipo;
    @FXML private ComboBox<Tipo> cmbNivel;
    @FXML private TextField txtCodigoCecoPadre;
    @FXML private ComboBox cmbEsBolsa;
    @FXML private ComboBox cmbAtribuible;
    @FXML private ComboBox cmbTipoGasto;
    @FXML private ComboBox cmbClaseGasto;

    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    CentroDAO centroDAO;
    List<String> lstCentrosCodigo;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_MAESTRO_CREAR.getControlador());
    String titulo;
    
    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        lstCentrosCodigo = centroDAO.listarCodigos();
        this.titulo = "Centro de Costos";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        
        cmbEsBolsa.setItems(FXCollections.observableArrayList(menuControlador.lstEsBolsa));
        
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
        
        cmbAtribuible.setItems(FXCollections.observableArrayList(menuControlador.lstAtribuible));
        cmbAtribuible.getSelectionModel().select(0);
        
        cmbTipoGasto.setItems(FXCollections.observableArrayList(menuControlador.lstTipoGasto));
        cmbTipoGasto.getSelectionModel().select(0);
        
        cmbClaseGasto.setItems(FXCollections.observableArrayList(menuControlador.lstClaseGasto));
        cmbClaseGasto.getSelectionModel().select(0);
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
    
    @FXML void lnkCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_EDITAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        String codigoGrupo = cmbTipo.getValue().getCodigo();
        int nivel = Integer.parseInt(cmbNivel.getValue().getCodigo());
        String cecoPadreCodigo = txtCodigoCecoPadre.getText();
        String esBolsa = cmbEsBolsa.getValue().toString();
        String atribuible = cmbAtribuible.getValue().toString();
        String tipoGasto = cmbTipoGasto.getValue().toString();
        String claseGasto = cmbClaseGasto.getValue().toString();
        boolean ptrCodigo = menuControlador.patronCodigoCentro(codigo);
        if (!ptrCodigo) {
            menuControlador.mensaje.create_pattern_error(titulo);
            return;
        }
        if (lstCentrosCodigo.contains(codigo)) {
            menuControlador.mensaje.create_exist_error(titulo);
            return;
        }
        if(cecoPadreCodigo.equals("")){
            cecoPadreCodigo = "-";
        }
        if (centroDAO.insertarObjeto(codigo, nombre,codigoGrupo,nivel,cecoPadreCodigo,menuControlador.repartoTipo,esBolsa, atribuible, tipoGasto, claseGasto)==1) {
            menuControlador.mensaje.create_success(titulo);
            menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), codigo, Navegador.RUTAS_CENTROS_MAESTRO_CREAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
        } else {
            menuControlador.mensaje.create_error(titulo);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
}
