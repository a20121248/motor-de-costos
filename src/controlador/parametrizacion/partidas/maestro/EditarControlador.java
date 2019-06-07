package controlador.parametrizacion.partidas.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PartidaDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import modelo.Partida;

public class EditarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkPartidas;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkEditar;
        
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox cmbGrupoGasto;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    Partida partida;
    PartidaDAO partidaDAO;
    public MenuControlador menuControlador;    
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PARTIDAS_MAESTRO_EDITAR.getControlador());
    String titulo;

    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        partida = (Partida) menuControlador.objeto;
        partidaDAO = new PartidaDAO();
        this.titulo = "Partidas";

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        txtCodigo.setText(partida.getCodigo());
        txtNombre.setText(partida.getNombre());
        cmbGrupoGasto.setItems(FXCollections.observableArrayList(menuControlador.lstGrupoGasto));
        cmbGrupoGasto.getSelectionModel().select(partida.getGrupoGasto());
    }    
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkPartidasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_ASOCIAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_EDITAR);
    }
    
    @FXML void btnGuardarAction(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        String grupoGasto = cmbGrupoGasto.getValue().toString();
        partidaDAO.actualizarObjeto(codigo, nombre,grupoGasto);
        menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_EDIT_SUCCESS);
        menuControlador.Log.editarItem(LOGGER,menuControlador.usuario.getUsername(), codigo, Navegador.RUTAS_PARTIDAS_MAESTRO_EDITAR.getDireccion());
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_LISTAR);
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_LISTAR);
    }
}