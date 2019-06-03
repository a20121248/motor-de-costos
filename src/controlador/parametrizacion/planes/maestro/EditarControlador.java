package controlador.parametrizacion.planes.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PlanDeCuentaDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import modelo.CuentaContable;

public class EditarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkPlanDeCuentas;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkEditar;
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox cmbAtribuible;
    @FXML private ComboBox cmbTipoGasto;
    @FXML private ComboBox cmbClaseGasto;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    CuentaContable planDeCuenta;
    PlanDeCuentaDAO planDeCuentaDAO;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PLANES_MAESTRO_EDITAR.getControlador());
    String titulo;
    
    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuenta = (CuentaContable) menuControlador.objeto;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        this.titulo = "Cuentas Contables";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        txtCodigo.setText(planDeCuenta.getCodigo());
        txtNombre.setText(planDeCuenta.getNombre());
        cmbAtribuible.setItems(FXCollections.observableArrayList(menuControlador.lstAtribuible));
        cmbAtribuible.getSelectionModel().select(planDeCuenta.getAtribuible());
        cmbTipoGasto.setItems(FXCollections.observableArrayList(menuControlador.lstTipoGasto));
        cmbTipoGasto.getSelectionModel().select(planDeCuenta.getTipoGasto());
        cmbClaseGasto.setItems(FXCollections.observableArrayList(menuControlador.lstClaseGasto));
        cmbClaseGasto.getSelectionModel().select(planDeCuenta.getClaseGasto());
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkPlanDeCuentasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
    }
    
    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_EDITAR);
    }

    @FXML void btnGuardarAction(ActionEvent event) {
        String codigo = planDeCuenta.getCodigo();
        String nombre = txtNombre.getText();
        String atribuible = cmbAtribuible.getValue().toString();
        String tipoGasto = cmbTipoGasto.getValue().toString();
        String claseGasto = cmbClaseGasto.getValue().toString();
        if (planDeCuentaDAO.actualizarObjeto(codigo,nombre, atribuible, tipoGasto,claseGasto)==1) {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_EDIT_SUCCESS);
            menuControlador.Log.editarItem(LOGGER,menuControlador.usuario.getUsername(), planDeCuenta.getCodigo(), Navegador.RUTAS_PLANES_MAESTRO_EDITAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
        } else {
            menuControlador.navegador.mensajeError(titulo,menuControlador.MENSAJE_EDIT_ERROR);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
    }
}
