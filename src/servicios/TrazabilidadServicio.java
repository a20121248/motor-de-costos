package servicios;

import dao.CentroDAO;
import dao.TrazaDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import modelo.CentroDriver;
import modelo.DriverLinea;
import modelo.DriverObjetoLinea;
import modelo.Traza;


public class TrazabilidadServicio {
    TrazaDAO trazaDAO;
    CentroDAO centroDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.TrazabilidadServicios");
    
    public TrazabilidadServicio(){
        trazaDAO = new TrazaDAO();
        centroDAO = new CentroDAO();
    }
    
    public void limpiarDatosPeriodo(int periodo){
        trazaDAO.borrarTrazaCascadaPeriodo(periodo);
    }

    public void ingresarPorcentajesCentros(CentroDriver centroDriver, List<DriverLinea> lstDriverLinea, int periodo, int iter) {
        List<Traza> lista = new ArrayList();
        
        for(DriverLinea item:lstDriverLinea){
            int nivelCentroDestino = centroDAO.obtenerNivelCentro(item.getEntidadDistribucionDestino().getCodigo());
            double porcentaje = item.getPorcentaje()/100.0;
            Traza traza = new Traza(centroDriver.getCodigoCentro(),iter,item.getEntidadDistribucionDestino().getCodigo(),nivelCentroDestino,porcentaje);
            lista.add(traza);
        }
        lista.add(new Traza(centroDriver.getCodigoCentro(), iter, centroDriver.getCodigoCentro(), iter, 1.0));
        trazaDAO.insertarPorcentajeDistribucionCentros(lista, periodo);
    }
    
    public void ingresarPorcentajesObjetos(CentroDriver centroDriver, List<DriverObjetoLinea> lstDriverLinea, int periodo) {
        List<Traza> lista = new ArrayList();
        
        for(DriverObjetoLinea item:lstDriverLinea){
            double porcentaje = item.getPorcentaje()/100.0;
            Traza traza = new Traza(centroDriver.getCodigoCentro(),0,"-",item.getProducto().getCodigo(),item.getSubcanal().getCodigo(),999,porcentaje);
            lista.add(traza);
        }
        lista.add(new Traza(centroDriver.getCodigoCentro(), 0, centroDriver.getCodigoCentro(), "-","-", 0, 1.0));
        trazaDAO.insertarPorcentajeDistribucionObjetos(lista, periodo);
    }
    
    
}
