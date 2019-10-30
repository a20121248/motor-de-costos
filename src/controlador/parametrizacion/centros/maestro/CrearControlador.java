package controlador.parametrizacion.centros.maestro;

import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import modelo.Tipo;

public class CrearControlador implements Initializable {
    // Variables de la vista        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Tipo> cmbTipo;
    @FXML private ComboBox<Tipo> cmbNivel;
    @FXML private TextField txtCodigoCecoPadre;
    @FXML private ComboBox cmbTipoGasto;
    @FXML private ComboBox cmbNIIF17Tipo;
    @FXML private ComboBox cmbNIIF17Atribuible;
    @FXML private ComboBox cmbNIIF17Clase;
    
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
        titulo = "Centro de Costos";
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
        
        cmbTipoGasto.setItems(FXCollections.observableArrayList(menuControlador.lstTipoGasto));
        cmbTipoGasto.getSelectionModel().select(0);
        
        cmbTipo.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.getCodigo().equals("BOLSA") || newValue.getCodigo().equals("OFICINA")) {
                cmbNivel.getSelectionModel().select(0);
            }
            if (newValue.getCodigo().equals("PROYECTO") || newValue.getCodigo().equals("FICTICIO")) {
                cmbNivel.getSelectionModel().select(99);
            }
        });
        
        cmbNIIF17Atribuible.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Atribuible));
        cmbNIIF17Atribuible.getSelectionModel().select(0);
        
        cmbNIIF17Tipo.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Tipo));
        cmbNIIF17Tipo.getSelectionModel().select(0);
        
        cmbNIIF17Clase.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Clase));
        cmbNIIF17Clase.getSelectionModel().select(0);
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
        int tipoGasto = cmbTipoGasto.getSelectionModel().getSelectedIndex();
        String niif17Atribuible = cmbNIIF17Atribuible.getValue().toString();
        String niif17Tipo = cmbNIIF17Tipo.getValue().toString();
        String niif17Clase = cmbNIIF17Clase.getValue().toString();
        boolean ptrCodigo = menuControlador.patronCodigoCentro(codigo);
        if (!ptrCodigo) {
            menuControlador.mensaje.create_pattern_error(titulo);
            return;
        }
        if (lstCentrosCodigo.contains(codigo)) {
            menuControlador.mensaje.create_exist_error(titulo);
            return;
        }
        if (cecoPadreCodigo.equals("")) {
            cecoPadreCodigo = "-";
        }
        if (centroDAO.insertarObjeto(codigo, nombre,codigoGrupo,nivel,cecoPadreCodigo,menuControlador.repartoTipo,tipoGasto, niif17Atribuible, niif17Tipo, niif17Clase)==1) {
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
