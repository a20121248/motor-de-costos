package controlador.parametrizacion;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ParametrizacionDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;

public class PrincipalControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;    
    @FXML private Hyperlink lnkParametrizacion;    
    @FXML private JFXButton btnPlanDeCuentas;
    @FXML private JFXButton btnGrupos;
    @FXML private JFXButton btnGrupoCuenta;
    @FXML private TitledPane tpCentros;
    @FXML private JFXButton btnCentros;
    @FXML private TextArea txtareaCentros;
    @FXML private TitledPane tpObjetos;
    // copiar parametrizacion
    @FXML private ComboBox<String> cmbMesOrigen;
    @FXML private Spinner<Integer> spAnhoOrigen;
    @FXML private ComboBox<String> cmbMesDestino;
    @FXML private Spinner<Integer> spAnhoDestino;
    @FXML private JFXButton btnCopiar;

    public MenuControlador menuControlador;
    ParametrizacionDAO parametrizacionDAO;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_MODULO_PARAMETRIZACION.getControlador());
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        parametrizacionDAO = new ParametrizacionDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // meses origen
        cmbMesOrigen.getItems().addAll(menuControlador.lstMeses);
        cmbMesOrigen.getSelectionModel().select(menuControlador.mesAnterior-1);
        spAnhoOrigen.getValueFactory().setValue(menuControlador.anhoAnterior);
        // meses destino
        cmbMesDestino.getItems().addAll(menuControlador.lstMeses);
        cmbMesDestino.getSelectionModel().select(menuControlador.mesActual-1);
        spAnhoDestino.getValueFactory().setValue(menuControlador.anhoActual);
        
        if (menuControlador.repartoTipo == 2) {
            tpCentros.setText("CENTROS DE BENEFICIO");
            txtareaCentros.setText("- Esta sección permite el mantenimiento de los Centros de Beneficio.");
            tpObjetos.setText("OBJETOS DE BENEFICIO");
        }
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }
    
    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void btnPlanDeCuentasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO);
    }
    
    @FXML void btnGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_ASOCIAR_PERIODO);
    }
    
    @FXML void btnPartidasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_ASOCIAR_PERIODO);
    }
    @FXML void btnGrupoCuentaAction(ActionEvent event) {
        menuControlador.objeto = "Todos";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPO_CUENTA_LISTAR);
    }
    
    @FXML void btnCuentaPartidaAction(ActionEvent event) {
        menuControlador.objeto = "Todos";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CUENTA_PARTIDA_LISTAR);
    }
    
    @FXML void btnCentrosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }

    @FXML void btnOficinasAction(ActionEvent event) {
        menuControlador.objetoTipo = "OFI";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @FXML void btnBancasAction(ActionEvent event) {
        menuControlador.objetoTipo = "BAN";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @FXML void btnProductosAction(ActionEvent event) {
        menuControlador.objetoTipo = "PRO";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @FXML void btnDriverEntidadAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_LISTAR);
    }
    
    @FXML void btnCopiarAction(ActionEvent event) {
        int periodoOrigen = spAnhoOrigen.getValue()*100 + cmbMesOrigen.getSelectionModel().getSelectedIndex() + 1;
        int periodoDestino = spAnhoDestino.getValue()*100 + cmbMesDestino.getSelectionModel().getSelectedIndex() + 1;
        parametrizacionDAO.copiarParametrizacion(periodoOrigen, periodoDestino);
        menuControlador.navegador.mensajeInformativo("Parametrización", "Parametrización cargada correctamente.");
    }    
}
