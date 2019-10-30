package controlador.procesos;

import controlador.ConexionBD;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.ProcesosDAO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import modelo.Centro;
import modelo.CentroDriver;
import modelo.DriverLinea;
import servicios.DistribucionServicio;
import servicios.DriverServicio;
import servicios.ReportingServicio;
import servicios.TrazabilidadServicio;

public class DistribuirFase2Task extends Task {
    int periodo;
    CentroDAO centroDAO;
    DriverDAO driverDAO;
    ProcesosDAO procesosDAO;
    DistribucionServicio distribucionServicio;
    PrincipalControlador principalControlador;
    DriverServicio driverServicio;
    ReportingServicio reportingServicio;
    TrazabilidadServicio trazabilidadServicio;
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
        trazabilidadServicio = new TrazabilidadServicio();
    }

    @Override
    public Void call() {       
        principalControlador.ejecutandoFase2 = true;
        principalControlador.piTotal.setProgress(progresoTotal);
        principalControlador.pbTotal.setProgress(progresoTotal);
        
        int numCentros = centroDAO.numeroCentrosCascada(periodo, principalControlador.menuControlador.repartoTipo);
        int maxNivel  =  centroDAO.maxNivelCascada(periodo, principalControlador.menuControlador.repartoTipo);
        procesosDAO.insertarEjecucionIni(periodo, fase, principalControlador.menuControlador.repartoTipo);
//        ConexionBD.crearStatement();
//        ConexionBD.tamanhoBatchMax = 1000;
        int centroI = 0;
        updateProgress(centroI, maxNivel+1);
        for (int iter=1; iter<=maxNivel; ++iter) {
//            List<CentroDriver> lstNivelI = centroDAO.listarCentrosNombresConDriver(periodo, "-", principalControlador.menuControlador.repartoTipo, iter);
//            int i = 0;
//            for (CentroDriver item: lstNivelI) {
//                List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(periodo, item.getCodigoDriver(), principalControlador.menuControlador.repartoTipo);
//                distribucionServicio.distribuirEntidadCascada(item, lstDriverLinea, periodo, iter,maxNivel, principalControlador.menuControlador.repartoTipo, i);
//                i++;
//            }
//            ConexionBD.ejecutarBatch();
//            System.out.println("iter "+iter+" : "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            centroDAO.insertarDistribucionCascadaPorNivel(iter, periodo, principalControlador.menuControlador.repartoTipo,0.0001);
            
            principalControlador.piTotal.setProgress(progresoTotal + centroI*progresoTotal/maxNivel);
            principalControlador.pbTotal.setProgress(progresoTotal + centroI*progresoTotal/maxNivel);
            // fin logica
            updateProgress(++centroI, maxNivel+1);
        }        

//        // los posibles registros que no se hayan ejecutado
//        ConexionBD.ejecutarBatch();
//        ConexionBD.cerrarStatement();
        
//        for (int itera=1; itera<=maxNivel; ++itera) {
//            List<CentroDriver> lista = centroDAO.listarCentrosNombresConDriver(periodo, "-", principalControlador.menuControlador.repartoTipo, itera);
//            String codigoCentro = "";
//            for (CentroDriver item: lista) {
//                List<DriverLinea> lstDriverLinea = driverDAO.obtenerLstDriverLinea(periodo, item.getCodigoDriver(), principalControlador.menuControlador.repartoTipo);
//                if(!item.getCodigoCentro().equals(codigoCentro)){
//                    trazabilidadServicio.ingresarPorcentajesCentros(item,lstDriverLinea,periodo,itera);
//                }
//                codigoCentro = item.getCodigoCentro();
//                principalControlador.piTotal.setProgress(progresoTotal + centroI*progresoTotal/(2*numCentros+1));
//                principalControlador.pbTotal.setProgress(progresoTotal + centroI*progresoTotal/(2*numCentros+1));
//                // fin logica
//                updateProgress(++centroI, 2*numCentros+1);
//            }       
//        }
        // Generar reportes
//        String reporteNombre,rutaOrigen;
//        principalControlador.lblMensajeFase2.setVisible(true);
//        if(principalControlador.menuControlador.repartoTipo ==1){
//            reporteNombre = "Reporte 02 - Gasto Propio y Asignado de Centros de Costos - Real";
//            rutaOrigen = "." + File.separator + "reportes" + File.separator + "real" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//            reportingServicio.crearReporteGastoPropioAsignado(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
//        } else {
//            reporteNombre = "Reporte 02 - Gasto Propio y Asignado de Centros de Costos - Presupuesto";
//            rutaOrigen = "." + File.separator + "presupuesto" + File.separator + "real" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//            reportingServicio.crearReporteGastoPropioAsignado(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
//        }

//        reporteNombre = "Reporte 03 - Cascada de Staff";
//        rutaOrigen = "." + File.separator + "reportes" + File.separator + "real" + File.separator + periodo + File.separator + reporteNombre +".xlsx";
//        reportingServicio.crearReporteCascada(periodo, rutaOrigen, principalControlador.menuControlador.repartoTipo);
        
        principalControlador.lblMensajeFase2.setVisible(false);
        // Fin generar reportes
        principalControlador.ejecutoFase2 = true;
        principalControlador.ejecutandoFase2 = false;
        principalControlador.piTotal.setProgress(fase*progresoTotal);
        principalControlador.pbTotal.setProgress(fase*progresoTotal);
        updateProgress(maxNivel+1, maxNivel+1);
        procesosDAO.insertarEjecucionFin(periodo, fase, principalControlador.menuControlador.repartoTipo);
        procesosDAO.insertarEjecucionTemporal(periodo, fase, principalControlador.menuControlador.repartoTipo);
        return null;
    }
}
