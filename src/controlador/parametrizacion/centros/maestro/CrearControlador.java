package controlador.parametrizacion.centros.maestro;

import com.jfoenix.controls.JFXButton;
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
    
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    CentroDAO centroDAO;
    List<String> lstCentrosCodigo;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_MAESTRO_CREAR.getControlador());
    
    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        lstCentrosCodigo = centroDAO.listarCodigos();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (menuControlador.repartoTipo == 2) {
            lblTitulo.setText("Centros de Beneficio");
            lnkCentros.setText("Centros de Beneficio");
        }
        
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
        if (menuControlador.repartoTipo == 1) {
            cmbTipo.getSelectionModel().select(0);
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
            cmbNivel.getSelectionModel().select(0);
        } else if (menuControlador.repartoTipo == 2) {
            cmbNivel.getSelectionModel().select(obsListaTipos.size()-1);
            cmbNivel.setDisable(true);
        }
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
        String titulo1 = "Centros de Costos";
        String titulo2 = "Centro de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo1 = "Centros de Beneficios";
            titulo2 = "Centro de Beneficio";
        }
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        String codigoGrupo = cmbTipo.getValue().getCodigo();
        int nivel = Integer.parseInt(cmbNivel.getValue().getCodigo());
        String cecoPadreCodigo = txtCodigoCecoPadre.getText();
        if (lstCentrosCodigo.contains(codigo)) {
            menuControlador.navegador.mensajeInformativo("Crear " + titulo1, "El código ya existe. No se puede crear el " + titulo2 + ".");
            return;
        }
        if (centroDAO.insertarObjeto(codigo, nombre,codigoGrupo,nivel,cecoPadreCodigo,menuControlador.repartoTipo)==1) {
            menuControlador.navegador.mensajeInformativo("Crear " + titulo1, titulo2 + " creado correctamente.");
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
        } else {
            menuControlador.navegador.mensajeInformativo("Crear " + titulo1, "Error. No se puede crear el " + titulo2);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
}
