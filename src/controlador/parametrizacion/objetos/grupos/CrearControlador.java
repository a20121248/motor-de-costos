package controlador.parametrizacion.objetos.grupos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoGrupoDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class CrearControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkGrupos;
    @FXML private Hyperlink lnkCrear;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private Spinner<Integer> spNivel;
    
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    ObjetoGrupoDAO objetoGrupoDAO;
    List<String> lstCodigos;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPOS_MAESTRO_CREAR.getControlador());
    
    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoGrupoDAO = new ObjetoGrupoDAO(menuControlador.objetoTipo);
        lstCodigos = objetoGrupoDAO.listarCodigos();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Crear Grupo de Oficinas");
                lnkObjetos.setText("Oficinas");
                break;
            case "BAN":
                lblTitulo.setText("Crear Grupo de Bancas");
                lnkObjetos.setText("Bancas");
                break;
            case "PRO":
                lblTitulo.setText("Crear Grupo de Productos");
                lnkObjetos.setText("Productos");
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
    
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
    
    @FXML void lnkCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_CREAR);
    }
    
    @FXML void btnCrearAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        int nivel = spNivel.getValue();
        if (lstCodigos.contains(codigo)) {
            menuControlador.navegador.mensajeError("Crear Grupo", "El código " + codigo + " ya existe. No se puede crear el Grupo.");
            return;
        }
        if (objetoGrupoDAO.insertarObjeto(codigo,nombre,nivel)==1) {
            menuControlador.navegador.mensajeInformativo("Crear Grupo", "Grupo creado correctamente.");
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
        } else {
            menuControlador.navegador.mensajeError("Crear Grupo", "No se puede crear el Grupo.");
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
}
