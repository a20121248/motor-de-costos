package controlador.parametrizacion.planes;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PlanDeCuentaDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

public class PrincipalControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkProductos;

    @FXML private JFXButton btnCatalogo;
    @FXML private JFXButton btnAsignacion;
    
    // Variables de la aplicacion
    PlanDeCuentaDAO planDeCuentaDAO;
    public MenuControlador menuControlador;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PLANES_PRINCIPAL.getControlador());
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Inicio.",menuControlador.usuario.getUsername()));
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Parametrización.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void lnkPlanDeCuentasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_PRINCIPAL);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Cuentas Contables.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void btnCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Catálogo de Cuentas Contables.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void btnAsignacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO);
        LOGGER.log(Level.INFO,String.format("El usuario %s cambió a la pestaña Asociación al periodo de Cuentas Contables.",menuControlador.usuario.getUsername()));
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
}
