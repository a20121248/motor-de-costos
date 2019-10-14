package servicios;

import dao.CentroDAO;
import dao.ObjetoDAO;
import dao.TrazaDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import modelo.Centro;
import modelo.CentroDriver;
import modelo.DriverLinea;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import modelo.Producto;
import modelo.Subcanal;
import modelo.Traza;

public class DistribucionServicio {
    CentroDAO centroDAO;
    ObjetoDAO objetoDAO;
    TrazaDAO trazaDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.DistribucionServicio");
    
    public DistribucionServicio() {
        centroDAO = new CentroDAO();
        objetoDAO = new ObjetoDAO("");
        trazaDAO = new TrazaDAO();
    }

    public void distribuirEntidadCascada(CentroDriver entidad, List<DriverLinea> lstDriverLinea, int periodo, int iteracion, int  maxNivel, int repartoTipo, int reg) {
        double saldo = entidad.getSaldo();
        // obtengo la lista de centros de niveles superiores
        List<DriverLinea> listaDriverLineaSigNiveles = new ArrayList();
        if (iteracion>=1 && iteracion<maxNivel) {
            listaDriverLineaSigNiveles = lstDriverLinea.stream().filter(item -> iteracion < ((Centro)item.getEntidadDistribucionDestino()).getNivel() || 0 == ((Centro)item.getEntidadDistribucionDestino()).getNivel()).collect(Collectors.toList());
        } else if (iteracion==maxNivel) {
            listaDriverLineaSigNiveles = lstDriverLinea.stream().filter(item -> 0 == ((Centro)item.getEntidadDistribucionDestino()).getNivel()).collect(Collectors.toList());
        }
        int  i =0;
        double totalSigNiveles = listaDriverLineaSigNiveles.stream().mapToDouble(f -> f.getPorcentaje()).sum();
        for(DriverLinea item: listaDriverLineaSigNiveles){
            double saldoDestino = saldo*item.getPorcentaje()/totalSigNiveles;
            EntidadDistribucion entidadDestino = item.getEntidadDistribucionDestino();
            if (entidadDestino != null) {
                centroDAO.insertarDistribucionBatchConGrupoGasto(entidadDestino.getCodigo(), periodo, iteracion, saldoDestino, entidad.getCodigoCentro(),entidad.getCodigoCuenta(),entidad.getCodigoPartida(),entidad.getCodigoCentroOrigen(),entidad.getGrupoGasto().getCodigo(), repartoTipo);
            }
            i++;
        }
//        System.out.println(i+" : "+reg);
    }
    
    public void distribuirEntidad(EntidadDistribucion entidad, List<DriverLinea> lstDriverLinea, int periodo, int iteracion) {
        double saldo = entidad.getSaldoAcumulado();
        lstDriverLinea.forEach((item) -> {
            double saldoDestino = saldo*item.getPorcentaje()/100.0;
            EntidadDistribucion entidadDestino = item.getEntidadDistribucionDestino();
            if (entidadDestino != null) {
                centroDAO.insertarDistribucionBatch(entidadDestino.getCodigo(), periodo, iteracion, saldoDestino, entidad.getCodigo());
            }
        });
    }
    
    public void distribuirCentrosBolsas(CentroDriver entidad, List<DriverLinea> lstDriverLinea, int periodo, int iteracion, int repartoTipo) {
        double saldo = entidad.getSaldo();
        lstDriverLinea.forEach((item) -> {
            double saldoDestino = saldo*item.getPorcentaje()/100.0;
            EntidadDistribucion entidadDestino = item.getEntidadDistribucionDestino();
            if (entidadDestino != null) {
                centroDAO.insertarDistribucionBatchConGrupoGasto(entidadDestino.getCodigo(), periodo, iteracion, saldoDestino, entidad.getCodigoCentro(), entidad.getCodigoCuenta(), entidad.getCodigoPartida(), entidad.getCodigoCentro(),entidad.getGrupoGasto().getCodigo(),repartoTipo);
            }
        });
    }
    
    public void distribuirEntidadObjetos(CentroDriver entidad, List<DriverObjetoLinea> lstDriverObjetoLinea, int periodo, int repartoTipo) {
        double saldo = entidad.getSaldo();
        lstDriverObjetoLinea.forEach((item) -> {
            double saldoDestino = saldo*item.getPorcentaje()/100.0;
            Producto producto = item.getProducto();
            Subcanal subcanal = item.getSubcanal();
            if (producto!=null) {
                objetoDAO.insertarDistribucionBatchObjetos(producto.getCodigo(),subcanal.getCodigo(), periodo, entidad.getCodigoCentro(), entidad.getCodigoDriver(),entidad.getGrupoGasto().getCodigo(), saldoDestino, repartoTipo,entidad.getCodigoCuenta(),entidad.getCodigoPartida(),entidad.getCodigoCentroOrigen());
            }
        });
    }
}
