package controlador.parametrizacion.objetos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class PrincipalControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;

    @FXML private JFXButton btnCatalogo;
    @FXML private JFXButton btnAsignacion;
    @FXML private JFXButton btnGrupos;
    @FXML private JFXButton btnJerarquia;
    
    @FXML private TextArea txtCatalogo;
    @FXML private TextArea txtAsociacion;
    @FXML private TextArea txtGrupos;
    @FXML private TextArea txtJerarquia;
    
    // Variables de la aplicacion
    ObjetoDAO objetoDAO;
    public MenuControlador menuControlador;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_PRINCIPAL.getControlador());
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Oficinas");
                lnkObjetos.setText("Oficinas");
                txtCatalogo.setText("- Esta sección permite la creación, edición y eliminación de Oficinas.");
                txtAsociacion.setText("- Esta sección permite asociar una Oficina a un periodo.");
                txtGrupos.setText("- Esta sección permite la creación, edición y eliminación de un Grupo de Oficinas.");
                txtJerarquia.setText("- Esta sección permite crear una estructura jerárquica de Oficinas para un periodo.");
                break;
            case "BAN":
                lblTitulo.setText("Bancas");
                lnkObjetos.setText("Bancas");
                txtCatalogo.setText("- Esta sección permite la creación, edición y eliminación de Bancas.");
                txtAsociacion.setText("- Esta sección permite asociar una Banca a un periodo.");
                txtGrupos.setText("- Esta sección permite la creación, edición y eliminación de un Grupo de Bancas.");
                txtJerarquia.setText("- Esta sección permite crear una estructura jerárquica de Bancas para un periodo.");
                break;
            case "PRO":
                lblTitulo.setText("Productos");
                lnkObjetos.setText("Productos");
                txtCatalogo.setText("- Esta sección permite la creación, edición y eliminación de Productos.");
                txtAsociacion.setText("- Esta sección permite asociar un Producto a un periodo.");
                txtGrupos.setText("- Esta sección permite la creación, edición y eliminación de un Grupo de Productos.");
                txtJerarquia.setText("- Esta sección permite crear una estructura jerárquica de Productos para un periodo.");
                break;
            default:
                break;
        }
    }

    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkObjetosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @FXML void btnCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
    
    @FXML void btnAsignacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
    }
    
    @FXML void btnGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
    
    @FXML void btnJerarquiaAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
}
