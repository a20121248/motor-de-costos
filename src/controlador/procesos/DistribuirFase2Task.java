package controlador.procesos;

import controlador.ConexionBD;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.ProcesosDAO;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import modelo.Centro;
import modelo.DriverLinea;
import servicios.DistribucionServicio;
import servicios.DriverServicio;
import servicios.ReportingServicio;

public class DistribuirFase2Task extends Task {
    int periodo;
    CentroDAO centroDAO;
    DriverDAO driverDAO;
    ProcesosDAO procesosDAO;
    DistribucionServicio distribucionServicio;
    PrincipalControlador principalControlador;
    DriverServicio driverServicio;
    ReportingServicio reportingServicio;
    final int fase;
    double progresoTotal;
    final static Logger LOGGER = Logger.getLogger("controlador.procesos.DistribuirFase2Task");
    
    public DistribuirFase2Task(int periodo, PrincipalControlador principalControlador) {
        this.periodo = periodo;
        this.principalControlador = principalControlador;
        this.fase = 2;
        this.progresoTotal=0.3333;
        
        centroDAO = new CentroDAO();
        driverDAO = new DriverDAO();
        procesosDAO = new ProcesosDAO();
        distribucionServicio = new DistribucionServicio();
        driverServicio = new DriverServicio();
        reportingServicio = new ReportingServicio();
    }

    @Override
    public Void call() {       
        principalControlador.ejecutandoFase2 = true;
        principalControlador.piTotal.setProgress(progresoTotal);
        principalControlador.pbTotal.setProgress(progresoTotal);
        
        int numCentros = centroDAO.numeroCentrosCascada(periodo, principalControlador.menuControlador.repartoTipo);
        procesosDAO.insertarEjecucionIni(periodo, fase, principalControlador.menuControlador.repartoTipo);
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 1000;
        int centroI = 0;
        updateProgress(centroI, numCentros+1);
        for (int iter=1; iter<=6; ++iter) {
            List<Centro> lstNivelI = centroDAO.listarCentrosNombresConDriver(periodo, "-", principalControlador.menuControlador.repartoTipo, iter);
            for (Centro centro: lstNivelI) {
                List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(periodo, centro.getDriver().getCodigo(), principalControlador.menuControlador.repartoTipo);
                distribucionServicio.distribuirEntidadCascada(centro, lstDriverLinea, periodo, iter);
                principalControlador.piTotal.setProgress(progresoTotal + centroI*progresoTotal/(numCentros+1));
                principalControlador.pbTotal.setProgress(progresoTotal + centroI*progresoTotal/(numCentros+1));
                // fin logica
                updateProgress(++centroI, numCentros+1);
            }
            ConexionBD.ejecutarBatch();
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();

        // Generar reportes
        String reporteNombre,rutaOrigen;
        principalControlador.lblMensajeFase2.setVisible(true);
        
        reporteNombre = "Reporte 02 - Gasto Propio y Asignado de Centros de Costos";
        rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
        reportingServicio.crearReporteGastoPropioAsignado(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);

        reporteNombre = "Reporte 03 - Cascada de Staff";
        rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
        reportingServicio.crearReporteCascada(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
        
        principalControlador.lblMensajeFase2.setVisible(false);
        // Fin generar reportes
        principalControlador.ejecutoFase2 = true;
        principalControlador.ejecutandoFase2 = false;
        principalControlador.piTotal.setProgress(fase*progresoTotal);
        principalControlador.pbTotal.setProgress(fase*progresoTotal);
        updateProgress(numCentros+1, numCentros+1);
        procesosDAO.insertarEjecucionFin(periodo, fase, principalControlador.menuControlador.repartoTipo);
        return null;
    }
}
