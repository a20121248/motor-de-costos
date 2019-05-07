package controlador.aprovisionamiento;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import controlador.MenuControlador;
import controlador.Navegador;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TitledPane;

public class PrincipalControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;    
    @FXML private Hyperlink lnkAprovisionamiento;    
    @FXML private JFXButton btnBalancete;
    @FXML private JFXButton btnDrivers;
    @FXML private JFXButton btnDriversObjeto;
    
    @FXML private TitledPane tpDriversCentros;
    @FXML private TitledPane tpDriversObjetos;
    
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_MODULO_APROVISIONAMIENTO.getControlador());
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (menuControlador.repartoTipo == 2) {
            tpDriversCentros.setText("DRIVERS - CENTROS DE BENEFICIO");
            tpDriversObjetos.setText("DRIVERS - OBJETOS DE BENEFICIO");
        }
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }
    
    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }
    
    @FXML void btnBalanceteAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
    
    @FXML void btnDriversAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_CENTRO_LISTAR);
    }
    
    @FXML void btnDriversObjetoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }
}
