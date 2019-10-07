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
    @FXML private ComboBox cmbTipoGasto;
    @FXML private ComboBox cmbNIIF17Atribuible;
    @FXML private ComboBox cmbNIIF17Tipo;
    @FXML private ComboBox cmbNIIF17Clase;
    
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
        cmbTipoGasto.setItems(FXCollections.observableArrayList(menuControlador.lstTipoGasto));
        if(planDeCuenta.getTipoGasto().equals("DIRECTO")) cmbTipoGasto.getSelectionModel().select(1);
        else cmbTipoGasto.getSelectionModel().select(0);
        cmbNIIF17Atribuible.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Atribuible));
        cmbNIIF17Atribuible.getSelectionModel().select(planDeCuenta.getNIIF17Atribuible());
        cmbNIIF17Tipo.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Tipo));
        cmbNIIF17Tipo.getSelectionModel().select(planDeCuenta.getNIIF17Tipo());
        cmbNIIF17Clase.setItems(FXCollections.observableArrayList(menuControlador.lstNIIF17Clase));
        cmbNIIF17Clase.getSelectionModel().select(planDeCuenta.getNIIF17Clase());
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
        int tipoGasto = cmbTipoGasto.getSelectionModel().getSelectedIndex();
        String niif17Atribuible = cmbNIIF17Atribuible.getValue().toString();
        String niif17Tipo = cmbNIIF17Tipo.getValue().toString();
        String niif17Clase = cmbNIIF17Clase.getValue().toString();
        if (planDeCuentaDAO.actualizarObjeto(codigo,nombre, tipoGasto, niif17Atribuible, niif17Tipo,niif17Clase)==1) {
            menuControlador.mensaje.edit_success(titulo);
            menuControlador.Log.editarItem(LOGGER,menuControlador.usuario.getUsername(), planDeCuenta.getCodigo(), Navegador.RUTAS_PLANES_MAESTRO_EDITAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
        } else {
            menuControlador.mensaje.edit_error(titulo);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
    }
}
