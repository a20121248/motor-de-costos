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
import servicios.TrazabilidadServicio;

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
    TrazabilidadServicio trazabilidadServicio;
    final int fase;
    double progresoTotal;
    final static Logger LOGGER = Logger.getLogger("controlador.procesos.DistribuirFase3Task");
    
    public DistribuirFase3Task(int periodo, PrincipalControlador principalControlador) {
        this.periodo = periodo;
        this.principalControlador = principalControlador;
            this.fase = 3;
            this.progresoTotal=0.3333;
        
        planDeCuentaDAO = new PlanDeCuentaDAO();
        driverDAO = new DriverDAO();
        driverServicio = new DriverServicio();
        trazabilidadServicio = new TrazabilidadServicio();
        distribucionServicio = new DistribucionServicio();
        centroDAO = new CentroDAO();
        procesosDAO = new ProcesosDAO();
        reportingServicio = new ReportingServicio();
        
    }

    @Override
    public Void call() {        
        principalControlador.ejecutandoFase3 = true;
        principalControlador.piTotal.setProgress(progresoTotal*(fase-1));
        principalControlador.pbTotal.setProgress(progresoTotal*(fase-1));
        
        //List<Centro> lista = centroDAO.listarCentros(periodo, 0);
//        List<CentroDriver> lista = centroDAO.listarCentrosObjetosNombresConDriver(periodo, principalControlador.menuControlador.repartoTipo);
        //driverServicio.agregarDrivers(lista, periodo);
        
        procesosDAO.insertarEjecucionIni(periodo, fase, principalControlador.menuControlador.repartoTipo);
//        final int max = lista.size();
        updateProgress(0, 1);
//        ConexionBD.crearStatement();
//        ConexionBD.tamanhoBatchMax = 1000;
//        for (int i = 1; i <= max; ++i) {
//            // inicio logica
//            CentroDriver entidadOrigen = lista.get(i-1);
//            List<DriverObjetoLinea> listaDriverObjetoLinea = driverDAO.obtenerDriverObjetoLinea(periodo, entidadOrigen.getCodigoDriver(),principalControlador.menuControlador.repartoTipo);
//            distribucionServicio.distribuirEntidadObjetos(entidadOrigen, listaDriverObjetoLinea, periodo, principalControlador.menuControlador.repartoTipo);
//            principalControlador.piTotal.setProgress(progresoTotal*(fase-1) + i*progresoTotal/(max+1));
//            principalControlador.pbTotal.setProgress(progresoTotal*(fase-1) + i*progresoTotal/(max+1));
//            // fin logica
//            updateProgress(i, max+1);
//        }
//        // los posibles registros que no se hayan ejecutado
//        ConexionBD.ejecutarBatch();
//        ConexionBD.cerrarStatement();

        centroDAO.insertarDistribucionCentrosObjetosCosto(periodo, principalControlador.menuControlador.repartoTipo);
        principalControlador.lblMensajeFase3.setVisible(true);
        
        principalControlador.lblMensajeFase3.setVisible(false);

        principalControlador.ejecutoFase3 = true;
        principalControlador.ejecutandoFase3 = false;
        principalControlador.piTotal.setProgress(1);
        principalControlador.pbTotal.setProgress(1);
        updateProgress(1, 1);
        procesosDAO.insertarEjecucionFin(periodo, fase, principalControlador.menuControlador.repartoTipo);
        return null;
    }
}
