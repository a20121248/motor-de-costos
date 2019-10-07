package controlador.parametrizacion.grupos.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.GrupoDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
    @FXML private Hyperlink lnkGrupos;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkCrear;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    GrupoDAO grupoDAO;
    List<String> lstCodigos;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPOS_MAESTRO_CREAR.getControlador());
    String titulo;

    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        grupoDAO = new GrupoDAO();
        lstCodigos = grupoDAO.listarCodigos();
        this.titulo = "Grupos de Cuentas Contables";
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
    
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_ASOCIAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_EDITAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        if (lstCodigos.contains(codigo)) {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_CREATE_ITEM_EXIST);
            return;
        }
        if (grupoDAO.insertarObjeto(codigo,nombre,menuControlador.repartoTipo)==1) {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_CREATE_SUCCESS);
            menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), codigo, Navegador.RUTAS_PLANES_MAESTRO_CREAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
        } else {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_CREATE_ERROR);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
    }
}
