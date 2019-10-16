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
import modelo.CentroDriver;
import modelo.DriverLinea;
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
        
        this.progresoTotal=0.3333;
        
//        if (principalControlador.menuControlador.repartoTipo == 1) {
//            this.progresoTotal=0.3333;
//        } else {
//            this.progresoTotal=0.5;
//        }
//        
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
//        List<CentroDriver> lista = centroDAO.listarCuentaPartidaCentroBolsaConDriverDistribuir(periodo,principalControlador.menuControlador.repartoTipo);
//        final int max = lista.size();
        updateProgress(0, 1);
//        ConexionBD.crearStatement();
//        ConexionBD.tamanhoBatchMax = 1000;
//        for (int i = 1; i <= max; ++i) {
//            // inicio logica
//            CentroDriver entidadOrigen = lista.get(i-1);
//            int periodoRT;
//            if(principalControlador.menuControlador.repartoTipo == 2) periodoRT = (int) periodo/100 * 100;
//            else  periodoRT = periodo;
//            List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(periodoRT, entidadOrigen.getCodigoDriver(), principalControlador.menuControlador.repartoTipo);
//            distribucionServicio.distribuirCentrosBolsas(entidadOrigen, lstDriverLinea, periodo, iteracion, principalControlador.menuControlador.repartoTipo);
//            principalControlador.piTotal.setProgress(i*progresoTotal/(max+1));
//            principalControlador.pbTotal.setProgress(i*progresoTotal/(max+1));
//            // fin logica
//            updateProgress(i, max+1);
//        }
//        // los posibles registros que no se hayan ejecutado
//        ConexionBD.ejecutarBatch();
//        ConexionBD.cerrarStatement();
        centroDAO.insertarDistribucionBolsas(periodo, principalControlador.menuControlador.repartoTipo);
        
//        String reporteNombre,rutaOrigen;        
//        // Generar reportes
//        if (principalControlador.menuControlador.repartoTipo == 1) {
//            principalControlador.lblMensajeFase1.setVisible(true);
//            
//            reporteNombre = "Reporte 01 - Distribución de Centros Bolsas Real";
//            rutaOrigen = "." + File.separator + "reportes" + File.separator + "real" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//            reportingServicio.crearReporteBolsasOficinas(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
//            principalControlador.lblMensajeFase1.setVisible(false);
//        } else {
//            principalControlador.lblMensajeFase1.setVisible(true);
//            
//            reporteNombre = "Reporte 01 - Distribución de Centros Bolsas Presupuesto";
//            rutaOrigen = "." + File.separator + "reportes" + File.separator + "presupuesto" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//            reportingServicio.crearReporteBolsasOficinas(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
//            principalControlador.lblMensajeFase1.setVisible(false);
//        }
        // Fin generar reportes
        principalControlador.ejecutoFase1 = true;
        principalControlador.ejecutandoFase1 = false;
        principalControlador.piTotal.setProgress(progresoTotal);
        principalControlador.pbTotal.setProgress(progresoTotal);

        updateProgress(1, 1);
        procesosDAO.insertarEjecucionFin(periodo, fase, principalControlador.menuControlador.repartoTipo);
        return null;
    }
}
