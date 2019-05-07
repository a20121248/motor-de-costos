package controlador.parametrizacion.planes.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PlanDeCuentaDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

public class CrearControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkPlanDeCuentas;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkCrear;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    PlanDeCuentaDAO planDeCuentaDAO;
    List<String> lstCodigos;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PLANES_MAESTRO_CREAR.getControlador());
    
    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        lstCodigos = planDeCuentaDAO.listarCodigos();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkPlanDeCuentasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_PRINCIPAL);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
    }
    
    @FXML void lnkCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_CREAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        if (lstCodigos.contains(codigo)) {
            menuControlador.navegador.mensajeError("Crear Cuenta Contable", "El código " + codigo + " ya existe. No se puede crear la Cuenta Contable.");
            return;
        }
        if (planDeCuentaDAO.insertarObjetoCuenta(codigo,nombre,menuControlador.repartoTipo)==1) {
            menuControlador.navegador.mensajeInformativo("Crear Cuenta Contable", "Cuenta Contable creada correctamente.");
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
        } else {
            menuControlador.navegador.mensajeError("Crear Cuenta Contable", "No se puede crear la cuenta contable.");
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
    }
}
