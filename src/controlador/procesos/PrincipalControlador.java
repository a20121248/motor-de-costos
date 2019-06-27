package controlador.procesos;

import controlador.MenuControlador;
import controlador.Navegador;
import dao.BancaDAO;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.GrupoDAO;
import dao.ObjetoDAO;
import dao.PlanDeCuentaDAO;
import dao.ProcesosDAO;
import dao.ProductoDAO;
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

public class PrincipalControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkProcesos;
    // Periodo
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    // Paneles
    @FXML private TitledPane tpEjecucionCostos;
    @FXML private TitledPane tpEjecucionIngresos;
    @FXML private TitledPane tpEjecucionTotal;
    // Costos: Fase 1
    @FXML private ProgressBar pbFase1;
    @FXML private ProgressIndicator piFase1;
    @FXML public Label lblMensajeFase1;
    // Costos: Fase 2
    @FXML private ProgressBar pbFase2;
    @FXML private ProgressIndicator piFase2;
    @FXML public Label lblMensajeFase2;
    // Costos: Fase 3
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
    @FXML public ProgressBar pbTotal;
    @FXML public ProgressIndicator piTotal;
    
    public MenuControlador menuControlador;
    PlanDeCuentaDAO planDeCuentaDAO;
    DriverDAO driverDAO;
    DriverServicio driverServicio;
    CentroDAO centroDAO;
    ProductoDAO productoDAO;
    BancaDAO bancaDAO;
    ObjetoDAO objetoDAO;
    ProcesosDAO procesosDAO;
    public boolean ejecutoFase1, ejecutoFase2, ejecutoFase3, ejecutoFaseTotal;
    public boolean ejecutandoFase1, ejecutandoFase2, ejecutandoFase3, ejecutandoFaseTotal;
    DistribucionServicio distribucionServicio;
    int periodoSeleccionado;
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
        bancaDAO = new BancaDAO();
        objetoDAO = new ObjetoDAO("");
        procesosDAO = new ProcesosDAO();
        distribucionServicio = new DistribucionServicio();
        executor = Executors.newSingleThreadExecutor();
        if (menuControlador.repartoTipo == 1) {
            this.progreso = 0.3333;
            this.numFasesTotales = 3;
        } else {
            this.progreso = 0.5;
            this.numFasesTotales = 2;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cambiar para Ingresos Operativos
        if (menuControlador.repartoTipo == 2) {
            tpEjecucionCostos.setVisible(false);
            tpEjecucionIngresos.setVisible(true);
            AnchorPane.setTopAnchor(tpEjecucionTotal, AnchorPane.getTopAnchor(tpEjecucionTotal)-75);
            lblEjecucionTotal.setText("Ejecutar las fases 1 y 2");
        }
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
        actualizarEstados(periodoSeleccionado);
    }
    
    void actualizarEstados(int periodo) {
        ejecutandoFase1 = false;
        ejecutandoFase2 = false;
        ejecutandoFase3 = false;
        ejecutandoFaseTotal = false;
        if (menuControlador.repartoTipo == 1) {
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
        } else {
            // FASE 1
            if (procesosDAO.obtenerFechaEjecucion(periodo, 1, menuControlador.repartoTipo) != null) {
                pbFase1Ingresos.setProgress(1);
                piFase1Ingresos.setProgress(1);
                pbTotal.setProgress(progreso);
                piTotal.setProgress(progreso);
                ejecutoFase1 = true;
            } else {
                pbFase1Ingresos.setProgress(0);
                piFase1Ingresos.setProgress(0);
                pbTotal.setProgress(0);
                piTotal.setProgress(0);
                ejecutoFase1 = false;
            }
            // FASE 2
            if (procesosDAO.obtenerFechaEjecucion(periodo, 2, menuControlador.repartoTipo) != null) {
                pbFase2Ingresos.setProgress(1);
                piFase2Ingresos.setProgress(1);
                pbTotal.setProgress(1);
                piTotal.setProgress(1);
                ejecutoFase2 = true;
                ejecutoFaseTotal = true;
            } else {
                pbFase2Ingresos.setProgress(0);
                piFase2Ingresos.setProgress(0);
                ejecutoFase2 = false;
                ejecutoFaseTotal = false;
            }
        }
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }
    
    @FXML void lnkProcesosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PROCESOS);
    }
    
    @FXML void btnFase1Action(ActionEvent event) {
        if (ejecutandoFase1) {
            menuControlador.navegador.mensajeInformativo("Ejecutar FASE 1", "La fase se está ejecutando actualmente.");
            return;
        }
        int cantSinDriver = centroDAO.enumerarListaCentroBolsaSinDriver(periodoSeleccionado,menuControlador.repartoTipo);
        if (cantSinDriver!=0) {
            if (cantSinDriver==1) 
                menuControlador.navegador.mensajeError("Fase 1", "Existe 1 Grupo de Cuentas Contables sin Driver asignado.\n\nPor favor, revise el módulo de Asignaciones y asegúrese que todos los Grupos de Cuentas Contables tengan un Driver.");
            else
                menuControlador.navegador.mensajeError("Fase 1", "Existen " + cantSinDriver + " Grupos de Cuentas Contables sin Driver asignado.\n\nPor favor, revise el módulo de Asignaciones y asegúrese que todos los Grupos de Cuentas Contables tengan un Driver.");
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
            menuControlador.navegador.mensajeInformativo("Ejecutar FASE 2", "La fase se está ejecutando actualmente.");
            return;
        }
        if (menuControlador.repartoTipo == 1) {
            if (!ejecutoFase1) {
                menuControlador.navegador.mensajeError("Fase 2", "Por favor, primero ejecute la Fase 1.");
                return;
            }
            int cantSinDriver = centroDAO.cantCentrosSinDriver(menuControlador.repartoTipo, ">", 0, periodoSeleccionado);
            if (cantSinDriver!=0) {
                menuControlador.navegador.mensajeError("Fase 2", "Existen " + cantSinDriver + " Centros sin driver asignado.\nPor favor, revise el módulo de Asignaciones y asegúrese que todos los Centros tengan un Driver.");
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
    }
    
    @FXML void btnFase3Action(ActionEvent event) {
        if (ejecutandoFase3) {
            menuControlador.navegador.mensajeInformativo("Ejecutar FASE 3", "La fase se está ejecutando actualmente.");
            return;
        }
        if (!ejecutoFase2) {
            menuControlador.navegador.mensajeInformativo("Ejecutar FASE 3", "Necesita ejecutar las fases previas a la FASE 3.");
            return;
        }
        
        int cantSinDriver = centroDAO.cantCentrosObjetosSinDriver(menuControlador.repartoTipo, ">", 0, periodoSeleccionado);
        if (cantSinDriver!=0) {
            menuControlador.navegador.mensajeError("Fase 3", "Existen " + cantSinDriver + " Centros sin driver asignado.\nPor favor, revise el módulo de Asignaciones y asegúrese que todos los Centros tengan un Driver.");
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
        if (menuControlador.repartoTipo == 1) {
            ejecutarFase1(periodoSeleccionado);
            ejecutarFase2(periodoSeleccionado);
            ejecutarFase3(periodoSeleccionado);
        } else {
        }
    }
    
    public void ejecutarFase1(int periodo) {       
        try {
            if (menuControlador.repartoTipo == 1) {
                String carpetaReportesYYYY = String.format("./reportes/gastos/%d/",periodoSeleccionado);
                Navegador.crearCarpeta(carpetaReportesYYYY);
            } else {
                String carpetaReportesYYYY = String.format("./reportes/ingresos/%d/",periodoSeleccionado);
                Navegador.crearCarpeta(carpetaReportesYYYY);
            }
        } catch (SecurityException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        
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
        if (menuControlador.repartoTipo == 1) {
            pbFase1.progressProperty().bind(df1t.progressProperty());
            piFase1.progressProperty().bind(df1t.progressProperty());
        } else {
            pbFase1Ingresos.progressProperty().bind(df1t.progressProperty());
            piFase1Ingresos.progressProperty().bind(df1t.progressProperty());
        }
        executor.execute(df1t);
    } 
   
    public void ejecutarFase2(int periodo) {
        procesosDAO.borrarEjecuciones(periodo, 2, menuControlador.repartoTipo);
        centroDAO.borrarDistribuciones(periodo, 1, menuControlador.repartoTipo);
        objetoDAO.borrarDistribuciones(periodo, menuControlador.repartoTipo);
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
}
