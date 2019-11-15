package controlador.inicio;

import controlador.MenuControlador;
import controlador.Navegador;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class PrincipalControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private ToggleGroup tgTipo;
    @FXML private RadioButton rbGasto;
    @FXML private RadioButton rbIngreso;
    
    @FXML private AnchorPane apTipoReparto;
    @FXML private SplitPane spTipoReparto;
    @FXML private AnchorPane apInput;
    @FXML private SplitPane spBalancete;
    @FXML private AnchorPane apFase1;
    @FXML private SplitPane spGrupos;
    @FXML private AnchorPane apFase2;
    @FXML private SplitPane spGastos;
    @FXML private SplitPane spJerarquia;
    
    MenuControlador menuControlador;
    boolean primeraPregunta;
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        primeraPregunta = true;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Si solo desea ver Costos
        if (menuControlador.verCostos) {
            apTipoReparto.setVisible(false);
            spTipoReparto.setVisible(false);
            
            AnchorPane.setTopAnchor(apInput, AnchorPane.getTopAnchor(apInput)-140);
            AnchorPane.setTopAnchor(spBalancete, AnchorPane.getTopAnchor(spBalancete)-140);
            AnchorPane.setTopAnchor(apFase1, AnchorPane.getTopAnchor(apFase1)-140);
            AnchorPane.setTopAnchor(spGrupos, AnchorPane.getTopAnchor(spGrupos)-140);
            AnchorPane.setTopAnchor(apFase2, AnchorPane.getTopAnchor(apFase2)-140);
            AnchorPane.setTopAnchor(spGastos, AnchorPane.getTopAnchor(spGastos)-140);
            AnchorPane.setTopAnchor(spJerarquia, AnchorPane.getTopAnchor(spJerarquia)-140);
       }
        
        if (menuControlador.repartoTipo == 1) {
            rbGasto.setSelected(true);
        } else if (menuControlador.repartoTipo == 2) {
            rbIngreso.setSelected(true);
        }
        tgTipo.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (primeraPregunta) {
                    String opcion = ((RadioButton)newValue).getText();
                    String mensaje = String.format("¿Está seguro que desea cambiar al tipo %s?",opcion);                
                    if (menuControlador.navegador.mensajeConfirmar("Cambiar de tipo", mensaje)) {
                        primeraPregunta = false;
                        newValue.setSelected(true);
                        menuControlador.lblTitulo.setText(opcion);
                        if (opcion.equals("REAL")) {
                            menuControlador.repartoTipo = 1;
                            menuControlador.objeto = null;
                            menuControlador.apHeader.getStyleClass().remove("ingresos");
                            menuControlador.apSidebar.getStyleClass().remove("ingresos");
                        } else if (opcion.equals("PRESUPUESTO")) {
                            menuControlador.repartoTipo = 2;
                            menuControlador.objeto = null;
                            menuControlador.apHeader.getStyleClass().add("ingresos");
                            menuControlador.apSidebar.getStyleClass().add("ingresos");
                        }
                        primeraPregunta = true;
                    } else {
                        primeraPregunta = false;
                        oldValue.setSelected(true);
                        primeraPregunta = true;
                    }
                }
            }
        });
    }

    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }
    
    @FXML void btnInputBalanceteAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
    
    @FXML
    void btnBolsasDriverAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR);
    }

    @FXML
    void btnCentroDriverAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR);
    }

    @FXML
    void btnCentrosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }

    @FXML
    void btnCuentasContablesAction(ActionEvent event) {
        menuControlador.objeto = "Todos";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CUENTA_PARTIDA_LISTAR);
    }

    @FXML
    void btnObjetosDriverAction(ActionEvent event) {
    menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR);
    }

    @FXML
    void btnPartidasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_ASOCIAR_PERIODO);
    }

    @FXML
    void btnProductosAction(ActionEvent event) {
        menuControlador.objetoTipo = "PRO";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }

    @FXML
    void btnSubcanlesAction(ActionEvent event) {
        menuControlador.objetoTipo = "SCA";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }

}
