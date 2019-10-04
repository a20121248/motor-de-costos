package controlador.parametrizacion.objetos.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.EntidadDistribucion;
import modelo.Producto;

public class EditarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkEditar;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    
    @FXML private JFXButton btnCancelar;
    @FXML private JFXButton btnGuardar;
    
    // Variables de la aplicacion
    EntidadDistribucion objeto;
    ObjetoDAO objetoDAO;
    String objetoNombre1;
    String objetoNombre2;
    List<String> lstCodigos;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_MAESTRO_EDITAR.getControlador());
    String titulo;
    
    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objeto = (EntidadDistribucion) menuControlador.objeto;
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Editar Oficina");
                lnkObjetos.setText("Oficinas");
                objetoNombre1 = "Oficina";
                objetoNombre2 = "la Oficina";
                this.titulo = "Oficinas";
                break;
            case "BAN":
                lblTitulo.setText("Editar Banca");
                lnkObjetos.setText("Bancas");
                objetoNombre1 = "Banca";
                objetoNombre2 = "la Banca";
                this.titulo = "Bancas";
                break;
            case "PRO":
                lblTitulo.setText("Editar Producto");
                lnkObjetos.setText("Productos");
                objetoNombre1 = "Producto";
                objetoNombre2 = "el Producto";
                this.titulo = "Productos";
                break;
            case "SCA":
                lblTitulo.setText("Editar Subcanal");
                lnkObjetos.setText("Subcanales");
                objetoNombre1 = "Subcanal";
                objetoNombre2 = "el Subcanal";
                this.titulo = "Subcanales";
                break;
            default:
                break;
        }
        txtCodigo.setText(objeto.getCodigo());
        txtNombre.setText(objeto.getNombre());
        lstCodigos = objetoDAO.listarCodigos(objeto.getCodigo());
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
    @FXML void lnkAsignacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
    }
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_EDITAR);
    }
    
    @FXML void btnGuardarAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        if (objetoDAO.actualizarObjeto(codigo, nombre, objeto.getCodigo())==1) {
            menuControlador.mensaje.edit_success(objetoNombre1);
            menuControlador.Log.editarItem(LOGGER, menuControlador.usuario.getUsername(), objeto.getCodigo(),Navegador.RUTAS_OBJETOS_MAESTRO_EDITAR.getDireccion().replace("/Objetos/", "/"+titulo+"/"));
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
        } else {
            menuControlador.mensaje.edit_error(titulo);
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
}
