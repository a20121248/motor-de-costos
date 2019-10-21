package controlador.procesos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.ObjetoDAO;
import dao.PlanDeCuentaDAO;
import dao.ProcesosDAO;
import dao.ProductoDAO;
import dao.TrazaDAO;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import servicios.DistribucionServicio;
import servicios.DriverServicio;
import servicios.TrazabilidadServicio;

public class PrincipalControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkProcesos;
    // Periodo
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private CheckBox cbCierreProceso;
    // Paneles
    @FXML private TitledPane tpEjecucionCostos;
    @FXML private TitledPane tpEjecucionIngresos;
    @FXML private TitledPane tpEjecucionTotal;
    // Costos: Fase 1
    @FXML private JFXButton btnFase1;
    @FXML private ProgressBar pbFase1;
    @FXML private ProgressIndicator piFase1;
    @FXML public Label lblMensajeFase1;
    // Costos: Fase 2
    @FXML private JFXButton btnFase2;
    @FXML private ProgressBar pbFase2;
    @FXML private ProgressIndicator piFase2;
    @FXML public Label lblMensajeFase2;
    // Costos: Fase 3
    @FXML private JFXButton btnFase3;
    @FXML public Label lblEjecucionTotal;
    @FXML private ProgressBar pbFase3;
    @FXML private ProgressIndicator piFase3;
    @FXML public Label lblMensajeFase3;
    // Ingresos: Fase 1
    @FXML private ProgressBar pbFase1Ingresos;
    @FXML private ProgressIndicator piFase1Ingresos;
    @FXML public Label lblMensajeFase1Ingresos;
    // Ingresos: Fase 2
    @FXML private ProgressBar pbFase2Ingresos;
    @FXML private ProgressIndicator piFase2Ingresos;
    @FXML public Label lblMensajeFase2Ingresos;
    // Total
    @FXML private JFXButton btnTotal;
    @FXML public ProgressBar pbTotal;
    @FXML public ProgressIndicator piTotal;
    
    public MenuControlador menuControlador;
    PlanDeCuentaDAO planDeCuentaDAO;
    DriverDAO driverDAO;
    DriverServicio driverServicio;
    CentroDAO centroDAO;
    ProductoDAO productoDAO;
    ObjetoDAO objetoDAO;
    ProcesosDAO procesosDAO;
    TrazaDAO trazaDAO;
    public boolean ejecutoFase1, ejecutoFase2, ejecutoFase3, ejecutoFaseTotal;
    public boolean ejecutandoFase1, ejecutandoFase2, ejecutandoFase3, ejecutandoFaseTotal;
    DistribucionServicio distribucionServicio;
    TrazabilidadServicio trazabilidadServicio;
    int periodoSeleccionado;
    int estadoProceso;
    final ExecutorService executor;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_MODULO_PROCESOS.getControlador());
    double progreso;
    int numFasesTotales;
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        driverDAO = new DriverDAO();
        driverServicio = new DriverServicio();
        centroDAO = new CentroDAO();
        productoDAO = new ProductoDAO();
        objetoDAO = new ObjetoDAO("");
        procesosDAO = new ProcesosDAO();
        trazaDAO = new TrazaDAO();
        distribucionServicio = new DistribucionServicio();
        trazabilidadServicio = new TrazabilidadServicio();
        executor = Executors.newSingleThreadExecutor();
        this.progreso = 0.3333;
        this.numFasesTotales = 3;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cambiar para Ingresos Operativos
//        if (menuControlador.repartoTipo == 2) {
//            tpEjecucionCostos.setVisible(false);
//            tpEjecucionIngresos.setVisible(true);
//            AnchorPane.setTopAnchor(tpEjecucionTotal, AnchorPane.getTopAnchor(tpEjecucionTotal)-75);
//            lblEjecucionTotal.setText("Ejecutar las fases 1 y 2");
//        }
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                actualizarEstados(spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1);
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                actualizarEstados(spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1);
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            } 
        });
        periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
//        cbCierreProceso.selectedProperty().addListener(listener);
        actualizarEstados(periodoSeleccionado);
    }
    
    void actualizarEstados(int periodo) {
        ejecutandoFase1 = false;
        ejecutandoFase2 = false;
        ejecutandoFase3 = false;
        ejecutandoFaseTotal = false;
        
        cierreProceso(periodo, menuControlador.repartoTipo);
        // FASE 1
        if (procesosDAO.obtenerFechaEjecucion(periodo, 1, menuControlador.repartoTipo) != null) {
            pbFase1.setProgress(1);
            piFase1.setProgress(1);
            pbTotal.setProgress(progreso);
            piTotal.setProgress(progreso);
            ejecutoFase1 = true;
        } else {
            pbFase1.setProgress(0);
            piFase1.setProgress(0);
            pbTotal.setProgress(0);
            piTotal.setProgress(0);
            ejecutoFase1 = false;
        }
        // FASE 2
        if (procesosDAO.obtenerFechaEjecucion(periodo, 2, menuControlador.repartoTipo) != null) {
            pbFase2.setProgress(1);
            piFase2.setProgress(1);
            pbTotal.setProgress(progreso*2);
            piTotal.setProgress(progreso*2);
            ejecutoFase2 = true;
            ejecutoFaseTotal = true;
        } else {
            pbFase2.setProgress(0);
            piFase2.setProgress(0);
            ejecutoFase2 = false;
            ejecutoFaseTotal = false;
        }
        // FASE 3
        if (procesosDAO.obtenerFechaEjecucion(periodo, 3, menuControlador.repartoTipo) != null) {
            pbFase3.setProgress(1);
            piFase3.setProgress(1);
            pbTotal.setProgress(1);
            piTotal.setProgress(1);
            ejecutoFase3 = true;
            ejecutoFaseTotal = true;
        } else {
            pbFase3.setProgress(0);
            piFase3.setProgress(0);
            ejecutoFase3 = false;
            ejecutoFaseTotal = false;
        }
    }
    
    public void cierreProceso(int periodo, int repartoTipo){
//        Cierre de Proceso
        estadoProceso = procesosDAO.obtenerEstadoProceso(periodo,repartoTipo);
        if( estadoProceso == 0 || estadoProceso == -1) {
            btnFase1.setDisable(false);
            btnFase2.setDisable(false);
            btnFase3.setDisable(false);
            btnTotal.setDisable(false);
        } else {
            btnFase1.setDisable(true);
            btnFase2.setDisable(true);
            btnFase3.setDisable(true);
            btnTotal.setDisable(true);
        }
    } 
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }
    
    @FXML void lnkProcesosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PROCESOS);
    }
    
    @FXML void btnGenerarTrazaAction(ActionEvent event) {
        trazabilidadServicio.generarMatriz(periodoSeleccionado, menuControlador.repartoTipo);
    }
    
    @FXML void btnFase1Action(ActionEvent event) {
        if (ejecutandoFase1) {
            menuControlador.mensaje.execute_phase_currently_error(1);
            return;
        }
        int cantSinDriver = centroDAO.enumerarListaCentroBolsaSinDriver(periodoSeleccionado,menuControlador.repartoTipo);
        if (cantSinDriver!=0) {
            if (cantSinDriver==1) menuControlador.mensaje.execute_asign_without_driver_singular_error(1, cantSinDriver);
            else menuControlador.mensaje.execute_asign_without_driver_plural_error(1, cantSinDriver);
            return;
        }
        
        Date fechaEjecucion = procesosDAO.obtenerFechaEjecucion(periodoSeleccionado, 1, menuControlador.repartoTipo);
        if (fechaEjecucion != null) {
            String mensaje = String.format("Existe una ejecución el %s a las %s.\n" +
                    "¿Está seguro que desea reprocesar la fase %d?\n\nNota: Esta acción borrará las fases posteriores.",
                    (new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"))).format(fechaEjecucion),
                    (new SimpleDateFormat("HH:mm:ss")).format(fechaEjecucion),
                    1);
            if (!menuControlador.navegador.mensajeConfirmar("Ejecutar FASE 1", mensaje)) return;
        }
        ejecutarFase1(periodoSeleccionado);
    }

    @FXML void btnFase2Action(ActionEvent event) {
        if (ejecutandoFase2) {
            menuControlador.mensaje.execute_phase_currently_error(2);
            return;
        }
        if (!ejecutoFase1) {
            menuControlador.navegador.mensajeError("Fase 2", "Por favor, primero ejecute la Fase 1.");
            return;
        }
        int cantSinDriver = centroDAO.cantCentrosSinDriver(menuControlador.repartoTipo, periodoSeleccionado);
        if (cantSinDriver!=0) {
            if (cantSinDriver==1) menuControlador.mensaje.execute_asign_without_driver_singular_error(2, cantSinDriver);
            else menuControlador.mensaje.execute_asign_without_driver_plural_error(2, cantSinDriver);
            return;
        }
        
        String detail = driverDAO.obtenerCentroDriverConError(periodoSeleccionado, menuControlador.repartoTipo);
        if (!detail.equals("")) {
            menuControlador.mensaje.execute_asign_bad_driver_error(2, detail);
            return;
        }
        Date fechaEjecucion = procesosDAO.obtenerFechaEjecucion(periodoSeleccionado, 2, menuControlador.repartoTipo);
        if (fechaEjecucion != null) {
            String mensaje = String.format("Existe una ejecución el %s a las %s.\n" +
                    "¿Está seguro que desea reprocesar la fase %d?\n\nNota: Esta acción borrará las fases posteriores.",
                    (new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"))).format(fechaEjecucion),
                    (new SimpleDateFormat("HH:mm:ss")).format(fechaEjecucion),
                    2);
            if (!menuControlador.navegador.mensajeConfirmar("Ejecutar FASE 2", mensaje)) return;
        }
        ejecutarFase2(periodoSeleccionado);
    }
    
    @FXML void btnFase3Action(ActionEvent event) {
        if (ejecutandoFase3) {
            menuControlador.mensaje.execute_phase_currently_error(3);
            return;
        }
        if (!ejecutoFase2) {
            menuControlador.navegador.mensajeInformativo("Ejecutar FASE 3", "Necesita ejecutar las fases previas a la FASE 3.");
            return;
        }
        
        int cantSinDriver = centroDAO.cantCentrosObjetosSinDriver(menuControlador.repartoTipo, periodoSeleccionado);
        if (cantSinDriver!=0) {
            if (cantSinDriver==1) menuControlador.mensaje.execute_asign_without_driver_singular_error(3, cantSinDriver);
            else menuControlador.mensaje.execute_asign_without_driver_plural_error(3, cantSinDriver);
            return;
        }
        Date fechaEjecucion = procesosDAO.obtenerFechaEjecucion(periodoSeleccionado, 3, menuControlador.repartoTipo);
        if (fechaEjecucion != null) {
            String mensaje = String.format("Existe una ejecución el %s a las %s.\n" +
                    "¿Está seguro que desea reprocesar la fase %d?",
                    (new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"))).format(fechaEjecucion),
                    (new SimpleDateFormat("HH:mm:ss")).format(fechaEjecucion),
                    3);
            if (!menuControlador.navegador.mensajeConfirmar("Ejecutar FASE 3", mensaje)) return;            
        }
        ejecutarFase3(periodoSeleccionado);
    }
    
    @FXML void btnTotalAction(ActionEvent event) throws InterruptedException {
        if (ejecutandoFase1 || ejecutandoFase2 || ejecutandoFase3) {
            menuControlador.navegador.mensajeInformativo("Ejecutar FASE TOTAL", "La FASE TOTAL se está ejecutando actualmente.");
            return;
        }
        int cantBolsasSinDriver = centroDAO.enumerarListaCentroBolsaSinDriver(periodoSeleccionado,menuControlador.repartoTipo);
        int cantCentrosSinDriver = centroDAO.cantCentrosSinDriver(menuControlador.repartoTipo, periodoSeleccionado);
        int cantCentroObjetosSinDriver = centroDAO.cantCentrosObjetosSinDriver(menuControlador.repartoTipo, periodoSeleccionado);
        if (cantBolsasSinDriver != 0 && cantCentrosSinDriver !=0 && cantCentroObjetosSinDriver !=0){
            String msj = null;
            if (cantBolsasSinDriver != 0) msj+= "\n- Existen  "+ cantBolsasSinDriver + " Centros Bolsas sin driver asignado.";
            if (cantCentrosSinDriver != 0) msj+= "\n- Existen  "+ cantCentrosSinDriver + " Centros sin driver asignado.";
            if (cantCentroObjetosSinDriver != 0) msj+= "\n- Existen  "+ cantCentroObjetosSinDriver + " Centros para Objetos de Costos sin driver asignado.";
            menuControlador.navegador.mensajeError("FASE TOTAL", msj + "\nPor favor, revise el módulo de Asignaciones y asegúrese que todos los Centros tengan un Driver. ");
            return;
        }
        
        Date fechaEjecucion1 = procesosDAO.obtenerFechaEjecucion(periodoSeleccionado, 1, menuControlador.repartoTipo);
        Date fechaEjecucion2 = procesosDAO.obtenerFechaEjecucion(periodoSeleccionado, 2, menuControlador.repartoTipo);
        Date fechaEjecucion3 = procesosDAO.obtenerFechaEjecucion(periodoSeleccionado, 3, menuControlador.repartoTipo);
        if(fechaEjecucion1!=null &&fechaEjecucion2!=null && fechaEjecucion3!=null){
            String mensaje = "Existe ejecución previa. \n¿Está seguro que desea reprocesar?";
            if (!menuControlador.navegador.mensajeConfirmar("Ejecutar FASE TOTAL", mensaje)) return;
        }
        if (menuControlador.repartoTipo == 1) {
            ejecutarFase1(periodoSeleccionado);
            ejecutarFase2(periodoSeleccionado);
            ejecutarFase3(periodoSeleccionado);
        } else {
        }
    }
    
    public void ejecutarFase1(int periodo) {
        
        procesosDAO.borrarEjecuciones(periodo, 1, menuControlador.repartoTipo);
        centroDAO.borrarDistribuciones(periodo, 0, menuControlador.repartoTipo);
        objetoDAO.borrarDistribuciones(periodo, menuControlador.repartoTipo);
        //pbFase1.setProgress(0);
        //piFase1.setProgress(0);
        // COSTOS
        if (menuControlador.repartoTipo == 1) {
            ejecutoFase1 = false;
            pbFase2.progressProperty().unbind();
            piFase2.progressProperty().unbind();
            pbFase2.setProgress(0);
            piFase2.setProgress(0);
            ejecutoFase2 = false;
            pbFase3.progressProperty().unbind();
            piFase3.progressProperty().unbind();
            pbFase3.setProgress(0);
            piFase3.setProgress(0);
            ejecutoFase3 = false;
            pbTotal.progressProperty().unbind();
            piTotal.progressProperty().unbind();
            pbTotal.setProgress(0);
            piTotal.setProgress(0);
        } else {
            // INGRESOS
            ejecutoFase1 = false;
            pbFase2.progressProperty().unbind();
            piFase2.progressProperty().unbind();
            pbFase2.setProgress(0);
            piFase2.setProgress(0);
            ejecutoFase2 = false;
        }
        // FLAG DE EJECUCION TOTAL
        ejecutoFaseTotal = false;
        
        DistribuirFase1Task df1t = new DistribuirFase1Task(periodo, this);
        pbFase1.progressProperty().bind(df1t.progressProperty());
        piFase1.progressProperty().bind(df1t.progressProperty());
//        if (menuControlador.repartoTipo == 1) {
//            pbFase1.progressProperty().bind(df1t.progressProperty());
//            piFase1.progressProperty().bind(df1t.progressProperty());
//        } else {
//            pbFase1Ingresos.progressProperty().bind(df1t.progressProperty());
//            piFase1Ingresos.progressProperty().bind(df1t.progressProperty());
//        }
        executor.execute(df1t);
    } 
   
    public void ejecutarFase2(int periodo) {
        procesosDAO.borrarEjecuciones(periodo, 2, menuControlador.repartoTipo);
        centroDAO.borrarDistribuciones(periodo, 1, menuControlador.repartoTipo);
        objetoDAO.borrarDistribuciones(periodo, menuControlador.repartoTipo);
//        trazaDAO.borrarTrazaCascadaPeriodo(periodo);
        //pbFase2.setProgress(0);
        //piFase2.setProgress(0);
        ejecutoFase2 = false;
        pbFase3.progressProperty().unbind();
        piFase3.progressProperty().unbind();
        pbFase3.setProgress(0);
        piFase3.setProgress(0);
        ejecutoFase3 = false;
        
        DistribuirFase2Task df2t = new DistribuirFase2Task(periodo, this);
        pbFase2.progressProperty().bind(df2t.progressProperty());
        piFase2.progressProperty().bind(df2t.progressProperty());
        executor.execute(df2t);
    }
    
    public void ejecutarFase3(int periodo) {       
        procesosDAO.borrarEjecuciones(periodo, 3, menuControlador.repartoTipo);
        objetoDAO.borrarDistribuciones(periodo, menuControlador.repartoTipo);
               
        DistribuirFase3Task df3t = new DistribuirFase3Task(periodo, this);
        pbFase3.progressProperty().bind(df3t.progressProperty());
        piFase3.progressProperty().bind(df3t.progressProperty());
        executor.execute(df3t);
    }
    
    @FXML void cbCierreProcesoAction (ActionEvent event) {
        boolean isSelected = cbCierreProceso.isSelected();
        int value = isSelected? 1 : 0;
        if(estadoProceso == -1){
            procesosDAO.insertarCierreProceso(periodoSeleccionado, menuControlador.repartoTipo,value);
        } else {
            procesosDAO.updateCierreProceso(periodoSeleccionado, menuControlador.repartoTipo,value);
        }
        cierreProceso(periodoSeleccionado, menuControlador.repartoTipo);
    }
}
