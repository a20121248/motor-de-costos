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
    @FXML private ComboBox cmbAtribuible;
    @FXML private ComboBox cmbTipoGasto;
    @FXML private ComboBox cmbClaseGasto;
    
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
        if (menuControlador.repartoTipo == 2) {
            lblTitulo.setText("Centros de Beneficio");
            lnkCentros.setText("Centros de Beneficio");
        }
        
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
        if (menuControlador.repartoTipo == 1) {
            cmbTipo.getSelectionModel().select(centro.getTipo());
        } else if (menuControlador.repartoTipo == 2) {
            cmbTipo.getSelectionModel().select(obsListaTipos.size()-1);
            cmbTipo.setDisable(true);
        }
        
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
        if (menuControlador.repartoTipo == 1) {
            int nivel = centro.getNivel();
            if (nivel == 0) {
                cmbNivel.getSelectionModel().select(obsListaTipos.size()-1);
            } else {
                cmbNivel.getSelectionModel().select(centro.getNivel()-1);
            }
        } else if (menuControlador.repartoTipo == 2) {
            cmbNivel.getSelectionModel().select(obsListaTipos.size()-1);
            cmbNivel.setDisable(true);
        }
        cmbEsBolsa.setItems(FXCollections.observableArrayList(menuControlador.lstEsBolsa));
        cmbEsBolsa.getSelectionModel().select(centro.getEsBolsa());
        cmbAtribuible.setItems(FXCollections.observableArrayList(menuControlador.lstAtribuible));
        cmbAtribuible.getSelectionModel().select(centro.getAtribuible());
        cmbTipoGasto.setItems(FXCollections.observableArrayList(menuControlador.lstTipoGasto));
        cmbTipoGasto.getSelectionModel().select(centro.getTipoGasto());
        cmbClaseGasto.setItems(FXCollections.observableArrayList(menuControlador.lstClaseGasto));
        cmbClaseGasto.getSelectionModel().select(centro.getClaseGasto());
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
        String atribuible = cmbAtribuible.getValue().toString();
        String tipoGasto = cmbTipoGasto.getValue().toString();
        String claseGasto = cmbClaseGasto.getValue().toString();
        if (centroDAO.actualizarObjeto(codigo, nombre,codigoGrupo,nivel,cecoPadreCodigo,esBolsa, atribuible,tipoGasto, claseGasto)==1) {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_EDIT_SUCCESS);
            menuControlador.Log.editarItem(LOGGER,menuControlador.usuario.getUsername(), codigo, Navegador.RUTAS_CENTROS_MAESTRO_EDITAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
        } else {
            menuControlador.navegador.mensajeError(titulo,menuControlador.MENSAJE_EDIT_ERROR);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
}
