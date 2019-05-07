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

public class CrearControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkCrear;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    
    @FXML private JFXButton btnCancelar;
    @FXML private JFXButton btnGuardar;
    
    // Variables de la aplicacion
    ObjetoDAO objetoDAO;
    String objetoNombre1,objetoNombre2;
    List<String> lstCodigos;
    public MenuControlador menuControlador;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_MAESTRO_CREAR.getControlador());
    
    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Crear Oficina");
                lnkObjetos.setText("Oficinas");
                objetoNombre1 = "Oficina";
                objetoNombre2 = "la Oficina";
                break;
            case "BAN":
                lblTitulo.setText("Crear Banca");
                lnkObjetos.setText("Bancas");
                objetoNombre1 = "Banca";
                objetoNombre2 = "la Banca";
                break;
            case "PRO":
                lblTitulo.setText("Crear Producto");
                lnkObjetos.setText("Productos");
                objetoNombre1 = "Producto";
                objetoNombre2 = "el Producto";
                break;
            default:
                break;
        }
        lstCodigos = objetoDAO.listarCodigos();
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
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_EDITAR);
    }
    
    @FXML void btnGuardarAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        if (lstCodigos.contains(codigo)) {
            menuControlador.navegador.mensajeError("Crear " + objetoNombre1, String.format("El código %s ya existe. No se puede crear %s.",codigo,objetoNombre2));
            return;
        }
        if (objetoDAO.insertarObjeto(codigo,nombre)==1) {
            menuControlador.navegador.mensajeInformativo("Crear " + objetoNombre1, objetoNombre2 + " se creó correctamente.");
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
        } else {
            menuControlador.navegador.mensajeError("Crear " + objetoNombre1, "No se puede crear " + objetoNombre2 + ".");
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
}
