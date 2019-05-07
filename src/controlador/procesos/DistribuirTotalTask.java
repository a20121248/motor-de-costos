package controlador.procesos;

import controlador.ConexionBD;
import dao.CentroDAO;
import dao.PlanDeCuentaDAO;
import dao.ProcesosDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import modelo.Centro;
import modelo.Driver;
import modelo.EntidadDistribucion;
import modelo.Grupo;
import servicios.DistribucionServicio;
import servicios.DriverServicio;
import servicios.ReportingServicio;

public class DistribuirTotalTask extends Task {
    final int periodo;
    PrincipalControlador principalControlador;
    PlanDeCuentaDAO planDeCuentaDAO;
    CentroDAO centroDAO;
    ProcesosDAO procesosDAO;
    DriverServicio driverServicio;
    DistribucionServicio distribucionServicio;
    ReportingServicio reportingServicio;
    final static Logger LOGGER = Logger.getLogger("controlador.procesos.DistribuirTotalTask");
    
    public DistribuirTotalTask(int periodo, PrincipalControlador principalControlador) {
        this.periodo = periodo;
        this.principalControlador = principalControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        centroDAO = new CentroDAO();
        procesosDAO = new ProcesosDAO();
        driverServicio = new DriverServicio();
        distribucionServicio = new DistribucionServicio();
        reportingServicio = new ReportingServicio();
    }

    @Override
    public Void call() {
        //principalControlador.ejecutarFase1(periodo);
        int cantEntidadesSinAsignar;
        
        //proceso 1
        procesosDAO.insertarEjecucionIni(periodo, 0, principalControlador.menuControlador.repartoTipo);
        //List<Grupo> listaGrupos = planDeCuentaDAO.listarGruposNombres(periodo,1);
        List<Grupo> listaGrupos = null;
        //List<Centro> listaCentrosCascada = planDeCuentaDAO.listarGruposNombres(periodo);
        //List<Centro> listaCentrosObjetos = planDeCuentaDAO.listarGruposNombres(periodo);
        
        int max = listaGrupos.size();
        cantEntidadesSinAsignar = driverServicio.agregarDriversAGrupos(listaGrupos, periodo,1);        
        updateProgress(0, max+1);
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 1000;
        for (int i = 1; i <= max; ++i) {
            // inicio logica
            EntidadDistribucion entidadOrigen = listaGrupos.get(i-1);
            Driver driver = entidadOrigen.getDriver();
            if (!driver.getNombre().equals("Sin driver asignado")) {
                String claseNombre = driver.getClass().getName();
                switch (claseNombre) {
                    case "modelo.DriverCentro":
                        //distribucionServicio.distribuirEntidad(entidadOrigen, (DriverCentro) driver, periodo, 0);
                        break;
                    case "modelo.DriverObjeto":
                        String oficinaCodigo = centroDAO.obtenerCodigoOficina(entidadOrigen.getCodigo());
                        if (oficinaCodigo == null) {
                            //distribucionServicio.distribuirEntidad(entidadOrigen, (DriverObjeto) driver, periodo);
                        } else {
                            //distribucionServicio.distribuirEntidadOficina(entidadOrigen, (DriverObjeto) driver, periodo, oficinaCodigo);
                        }
                        break;
                }
            }
            // fin logica
            updateProgress(i, max+1);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        procesosDAO.insertarEjecucionFin(periodo, 0, principalControlador.menuControlador.repartoTipo);
        
        //proceso 2
        procesosDAO.insertarEjecucionIni(periodo, 6, principalControlador.menuControlador.repartoTipo);
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 1000;
        int nivelIni = 1;
        int nivelFin = 6;
        List<Centro> lista = new ArrayList();
        for (int i = nivelIni; i <= nivelFin; ++i) {
            //List<Centro> listaNivelI = centroDAO.listarCentros(periodo, i);
            List<Centro> listaNivelI = null;
            lista.addAll(listaNivelI);
        }
        cantEntidadesSinAsignar = driverServicio.agregarDriversACentros(lista, periodo,1);
        int centroI = 0;
        int numCentros = lista.size();
        updateProgress(centroI, numCentros+1);
        if (cantEntidadesSinAsignar != 0) {
            for (int iter = nivelIni; iter <= nivelFin; ++iter) {
                //List<Centro> listaNivelI = centroDAO.listarCentros(periodo, iter);
                List<Centro> listaNivelI = null;
                driverServicio.agregarDriversACentros(listaNivelI, periodo,1);
                for (Centro centro: listaNivelI) {
                    Driver driver = centro.getDriver();
                    if (!driver.getNombre().equals("Sin driver asignado")) {
                        //distribucionServicio.distribuirEntidadCascada(centro, (DriverCentro) driver, periodo, iter);
                    }
                    // fin logica
                    updateProgress(++centroI, numCentros+1);
                }
                ConexionBD.ejecutarBatch();
            }
            ConexionBD.ejecutarBatch();
            ConexionBD.cerrarStatement();
            procesosDAO.insertarEjecucionFin(periodo, 6, principalControlador.menuControlador.repartoTipo);
        }
        //proceso 3
        procesosDAO.insertarEjecucionIni(periodo, 7, principalControlador.menuControlador.repartoTipo);
        
        procesosDAO.insertarEjecucionFin(periodo, 7, principalControlador.menuControlador.repartoTipo);
        return null;
    }
}
