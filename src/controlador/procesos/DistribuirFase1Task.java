package controlador.procesos;

import controlador.ConexionBD;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.GrupoDAO;
import dao.PlanDeCuentaDAO;
import dao.ProcesosDAO;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import modelo.EntidadDistribucion;
import modelo.Grupo;
import servicios.DistribucionServicio;
import servicios.DriverServicio;
import servicios.ReportingServicio;

public class DistribuirFase1Task extends Task {
    int periodo;
    DistribucionServicio distribucionServicio;
    PrincipalControlador principalControlador;
    CentroDAO centroDAO;
    ProcesosDAO procesosDAO;
    ReportingServicio reportingServicio;
    PlanDeCuentaDAO planDeCuentaDAO;
    GrupoDAO grupoDAO;
    DriverDAO driverDAO;
    DriverServicio driverServicio;
    final int fase;
    final int iteracion;
    double progresoTotal;
    final static Logger LOGGER = Logger.getLogger("controlador.procesos.DistribuirFase1Task");
    
    public DistribuirFase1Task(int periodo, PrincipalControlador principalControlador) {
        this.periodo = periodo;
        this.principalControlador = principalControlador;
        this.fase = 1;
        this.iteracion = 0;
        if (principalControlador.menuControlador.repartoTipo == 1) {
            this.progresoTotal=0.3333;
        } else {
            this.progresoTotal=0.5;
        }
        
        planDeCuentaDAO = new PlanDeCuentaDAO();
        grupoDAO = new GrupoDAO();
        driverDAO = new DriverDAO();
        driverServicio = new DriverServicio();
        
        distribucionServicio = new DistribucionServicio();
        centroDAO = new CentroDAO();
        procesosDAO = new ProcesosDAO();
        reportingServicio = new ReportingServicio();
    }

    @Override
    public Void call() {        
        principalControlador.ejecutandoFase1 = true;
        procesosDAO.insertarEjecucionIni(periodo, fase, principalControlador.menuControlador.repartoTipo);
        List<Grupo> lista = grupoDAO.listar(periodo,"",principalControlador.menuControlador.repartoTipo);
        final int max = lista.size();
        updateProgress(0, max+1);
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 1000;
        for (int i = 1; i <= max; ++i) {
            // inicio logica
            EntidadDistribucion entidadOrigen = lista.get(i-1);
            centroDAO.insertarDistribucionGrupo(entidadOrigen.getCodigo(), principalControlador.menuControlador.repartoTipo, periodo, iteracion);
            principalControlador.piTotal.setProgress(i*progresoTotal/(max+1));
            principalControlador.pbTotal.setProgress(i*progresoTotal/(max+1));
            // fin logica
            updateProgress(i, max+1);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        
        String reporteNombre,rutaOrigen;        
        // Generar reportes
        if (principalControlador.menuControlador.repartoTipo == 1) {
            principalControlador.lblMensajeFase1.setVisible(true);
            
            reporteNombre = "Reporte 01 - Cuentas Contables a Centros de Costos";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
            reportingServicio.crearReporteCuentaCentro(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
            
            principalControlador.lblMensajeFase1.setVisible(false);
        } else {
            principalControlador.lblMensajeFase1Ingresos.setVisible(true);
            
            reporteNombre = "Reporte 01 - Cuentas Contables a Centros de Beneficio";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "ingresos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
            reportingServicio.crearReporteCuentaCentro(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
            
            principalControlador.lblMensajeFase1Ingresos.setVisible(false);
        }
        // Fin generar reportes
        principalControlador.ejecutoFase1 = true;
        principalControlador.ejecutandoFase1 = false;
        principalControlador.piTotal.setProgress(progresoTotal);
        principalControlador.pbTotal.setProgress(progresoTotal);

        updateProgress(max+1, max+1);
        procesosDAO.insertarEjecucionFin(periodo, fase, principalControlador.menuControlador.repartoTipo);
        return null;
    }
}
