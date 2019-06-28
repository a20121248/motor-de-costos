package controlador.procesos;

import controlador.ConexionBD;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.PlanDeCuentaDAO;
import dao.ProcesosDAO;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import modelo.Centro;
import modelo.CentroDriver;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import servicios.DistribucionServicio;
import servicios.DriverServicio;
import servicios.ReportingServicio;

public class DistribuirFase3Task extends Task {
    int periodo;
    DistribucionServicio distribucionServicio;
    PrincipalControlador principalControlador;
    CentroDAO centroDAO;
    ProcesosDAO procesosDAO;
    ReportingServicio reportingServicio;
    PlanDeCuentaDAO planDeCuentaDAO;
    DriverDAO driverDAO;
    DriverServicio driverServicio;
    final int fase;
    double progresoTotal;
    final static Logger LOGGER = Logger.getLogger("controlador.procesos.DistribuirFase3Task");
    
    public DistribuirFase3Task(int periodo, PrincipalControlador principalControlador) {
        this.periodo = periodo;
        this.principalControlador = principalControlador;
        if (principalControlador.menuControlador.repartoTipo == 1) {
            this.fase = 3;
            this.progresoTotal=0.3333;
        } else {
            this.fase = 2;
            this.progresoTotal=0.5;
        }
        
        planDeCuentaDAO = new PlanDeCuentaDAO();
        driverDAO = new DriverDAO();
        driverServicio = new DriverServicio();
        
        distribucionServicio = new DistribucionServicio();
        centroDAO = new CentroDAO();
        procesosDAO = new ProcesosDAO();
        reportingServicio = new ReportingServicio();
    }

    @Override
    public Void call() {        
        if (principalControlador.menuControlador.repartoTipo == 1)
            principalControlador.ejecutandoFase3 = true;
        else
            principalControlador.ejecutandoFase2 = true;
        
        principalControlador.piTotal.setProgress(progresoTotal*(fase-1));
        principalControlador.pbTotal.setProgress(progresoTotal*(fase-1));
        
        //List<Centro> lista = centroDAO.listarCentros(periodo, 0);
        List<CentroDriver> lista = centroDAO.listarCentrosObjetosNombresConDriver(periodo, principalControlador.menuControlador.repartoTipo);
        //driverServicio.agregarDrivers(lista, periodo);
        
        procesosDAO.insertarEjecucionIni(periodo, fase, principalControlador.menuControlador.repartoTipo);
        final int max = lista.size();
        updateProgress(0, max+1);
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 1000;
        for (int i = 1; i <= max; ++i) {
            // inicio logica
            CentroDriver entidadOrigen = lista.get(i-1);
            List<DriverObjetoLinea> listaDriverObjetoLinea = driverDAO.obtenerDriverObjetoLinea(periodo, entidadOrigen.getCodigoDriver());
            distribucionServicio.distribuirEntidadObjetos(entidadOrigen, listaDriverObjetoLinea, periodo, principalControlador.menuControlador.repartoTipo);
            principalControlador.piTotal.setProgress(progresoTotal*(fase-1) + i*progresoTotal/(max+1));
            principalControlador.pbTotal.setProgress(progresoTotal*(fase-1) + i*progresoTotal/(max+1));
            // fin logica
            updateProgress(i, max+1);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        String reporteNombre,rutaOrigen;
        
        if (principalControlador.menuControlador.repartoTipo == 1) {
            // Generar reportes
            principalControlador.lblMensajeFase3.setVisible(true);
            
//            reporteNombre = "Reporte 04 - Objetos de Costos";
//            rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//            reportingServicio.crearReporteObjetos(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
//            
            reporteNombre = "Reporte 04 - Objetos de Costos";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
            reportingServicio.crearReporteObjetosCostos(periodo, rutaOrigen,principalControlador.menuControlador.repartoTipo);

            principalControlador.lblMensajeFase3.setVisible(false);
            // Fin generar reportes
            principalControlador.ejecutoFase3 = true;
            principalControlador.ejecutandoFase3 = false;
            principalControlador.piTotal.setProgress(1);
            principalControlador.pbTotal.setProgress(1);
        } else {
            // Generar reportes
//            principalControlador.lblMensajeFase2Ingresos.setVisible(true);
//            
//            reporteNombre = "Reporte 02 - Objetos de Beneficio";
//            rutaOrigen = "." + File.separator + "reportes" + File.separator + "ingresos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//            reportingServicio.crearReporteObjetos(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
//            
//            reporteNombre = "Reporte 03 - Gastos de Operaciones de Cambio";
//            rutaOrigen = "." + File.separator + "reportes" + File.separator + "ingresos" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//            reportingServicio.crearReporteGastosOperacionesDeCambio(periodo, rutaOrigen);
//
//            principalControlador.lblMensajeFase2Ingresos.setVisible(false);
//            // Fin generar reportes
//            principalControlador.ejecutoFase2 = true;
//            principalControlador.ejecutandoFase2 = false;
//            principalControlador.piTotal.setProgress(1);
//            principalControlador.pbTotal.setProgress(1);
        }

        updateProgress(max+1, max+1);
        procesosDAO.insertarEjecucionFin(periodo, fase, principalControlador.menuControlador.repartoTipo);
        return null;
    }
}
