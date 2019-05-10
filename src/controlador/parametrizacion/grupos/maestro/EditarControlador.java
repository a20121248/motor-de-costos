package controlador.parametrizacion.grupos.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.GrupoDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import modelo.Grupo;

public class EditarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkGrupos;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkEditar;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    Grupo grupo;
    GrupoDAO grupoDAO;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPOS_MAESTRO_EDITAR.getControlador());
    
    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        grupo = (Grupo) menuControlador.objeto;
        grupoDAO = new GrupoDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        txtCodigo.setText(grupo.getCodigo());
        txtNombre.setText(grupo.getNombre());
    }    
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_ASOCIAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_EDITAR);
    }
    
    @FXML void btnGuardarAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        grupoDAO.actualizarObjeto(codigo, nombre, 1);
        menuControlador.navegador.mensajeInformativo("Editar Grupo de Cuentas Contables", "Grupo editado correctamente.");
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
    }
}
