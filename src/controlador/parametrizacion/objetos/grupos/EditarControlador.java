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
import modelo.Grupo;

public class EditarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkGrupos;
    @FXML private Hyperlink lnkEditar;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private Spinner<Integer> spNivel;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    Grupo grupo;
    ObjetoGrupoDAO objetoGrupoDAO;
    List<String> lstCodigos;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPOS_MAESTRO_EDITAR.getControlador());
    String titulo;
    
    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        grupo = (Grupo) menuControlador.objeto;
        objetoGrupoDAO = new ObjetoGrupoDAO(menuControlador.objetoTipo);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Editar Grupo de Oficinas");
                lnkObjetos.setText("Oficinas");
                this.titulo = "Oficinas";
                break;
            case "BAN":
                lblTitulo.setText("Editar Grupo de Bancas");
                lnkObjetos.setText("Bancas");
                this.titulo = "Bancas";
                break;
            case "PRO":
                lblTitulo.setText("Editar Grupo de Productos");
                lnkObjetos.setText("Productos");
                this.titulo = "Bancas";
                break;
            case "SCA":
                lblTitulo.setText("Editar Grupo de Subcanales");
                lnkObjetos.setText("Subcanales");
                this.titulo = "Bancas";
                break;
            default:
                break;
        }
        txtCodigo.setText(grupo.getCodigo());
        txtNombre.setText(grupo.getNombre());
        spNivel.getValueFactory().setValue(grupo.getNivel());
        lstCodigos = objetoGrupoDAO.listarCodigos(grupo.getCodigo());
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
    @FXML void lnkJerarquiaAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA);
    }
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
    
    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_EDITAR);
    }
    
    @FXML void btnGuardarAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        int nivel = spNivel.getValue();
        if (lstCodigos.contains(codigo)) {
            menuControlador.navegador.mensajeError("Editar Grupo", "El código " + codigo + " ya existe. No se puede editar el Grupo.");
            return;
        }
        if (objetoGrupoDAO.actualizarObjeto(codigo,nombre,nivel,grupo.getCodigo())==1) {
            menuControlador.navegador.mensajeInformativo("Editar Grupo", "Grupo editado correctamente.");
            menuControlador.Log.editarItem(LOGGER, menuControlador.usuario.getUsername(), codigo, Navegador.RUTAS_OBJETOS_GRUPOS_EDITAR.getDireccion().replace("/Objetos/", "/"+titulo+"/"));
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
        } else {
            menuControlador.navegador.mensajeError("Editar Grupo", "No se puede editar el Grupo.");
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
}
