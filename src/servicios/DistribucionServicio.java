package servicios;

import dao.CentroDAO;
import dao.ObjetoDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import modelo.Banca;
import modelo.Centro;
import modelo.CentroDriver;
import modelo.DriverLinea;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import modelo.Oficina;
import modelo.Producto;

public class DistribucionServicio {
    CentroDAO centroDAO;
    ObjetoDAO objetoDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.DistribucionServicio");
    
    public DistribucionServicio() {
        centroDAO = new CentroDAO();
        objetoDAO = new ObjetoDAO("");
    }

    public void distribuirEntidadCascada(CentroDriver entidad, List<DriverLinea> lstDriverLinea, int periodo, int iteracion, int  maxNivel) {
        double saldo = entidad.getSaldo();
        // obtengo la lista de centros de niveles superiores
        List<DriverLinea> listaDriverLineaSigNiveles = new ArrayList();
        if (iteracion>=1 && iteracion<maxNivel) {
            listaDriverLineaSigNiveles = lstDriverLinea.stream().filter(item -> iteracion < ((Centro)item.getEntidadDistribucionDestino()).getNivel() || 0 == ((Centro)item.getEntidadDistribucionDestino()).getNivel()).collect(Collectors.toList());
        } else if (iteracion==maxNivel) {
            listaDriverLineaSigNiveles = lstDriverLinea.stream().filter(item -> 0 == ((Centro)item.getEntidadDistribucionDestino()).getNivel()).collect(Collectors.toList());
        }
        double totalSigNiveles = listaDriverLineaSigNiveles.stream().mapToDouble(f -> f.getPorcentaje()).sum();
        listaDriverLineaSigNiveles.forEach((item) -> {
            double saldoDestino = saldo*item.getPorcentaje()/totalSigNiveles;
            EntidadDistribucion entidadDestino = item.getEntidadDistribucionDestino();
            if (entidadDestino != null) {
                centroDAO.insertarDistribucionBatchConGrupoGasto(entidadDestino.getCodigo(), periodo, iteracion, saldoDestino, entidad.getCodigoCentro(),entidad.getGrupoGasto().getCodigo());
                //Funcion para trazabilidad
            }
        });
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
    
    public void distribuirCentrosBolsas(CentroDriver entidad, List<DriverLinea> lstDriverLinea, int periodo, int iteracion) {
        double saldo = entidad.getSaldo();
        lstDriverLinea.forEach((item) -> {
            double saldoDestino = saldo*item.getPorcentaje()/100.0;
            EntidadDistribucion entidadDestino = item.getEntidadDistribucionDestino();
            if (entidadDestino != null) {
                centroDAO.insertarDistribucionBatchConGrupoGasto(entidadDestino.getCodigo(), periodo, iteracion, saldoDestino, entidad.getCodigoCentro(),entidad.getGrupoGasto().getCodigo());
            }
        });
    }
    
    public void distribuirEntidadObjetos(EntidadDistribucion entidad, List<DriverObjetoLinea> lstDriverObjetoLinea, int periodo, int repartoTipo) {
        double saldo = entidad.getSaldoAcumulado();
        lstDriverObjetoLinea.forEach((item) -> {
            double saldoDestino = saldo*item.getPorcentaje()/100.0;
            Oficina oficina = item.getOficina();
            Banca banca = item.getBanca();
            Producto producto = item.getProducto();
            if (oficina!=null && banca!=null && producto!=null) {
                objetoDAO.insertarDistribucionBatch(oficina.getCodigo(), banca.getCodigo(), producto.getCodigo(), periodo, entidad.getCodigo(), saldoDestino, repartoTipo);
                //objetoDAO.insertarDistribucion(oficina.getCodigo(), banca.getCodigo(), producto.getCodigo(), periodo, entidad.getCodigo(), saldoDestino);
            }
        });
    }
    
    public void distribuirEntidadObjetosOficina(EntidadDistribucion entidad, List<DriverObjetoLinea> lstDriverObjetoLinea, int periodo, String oficinaCodigo, int repartoTipo) {
        double saldo = entidad.getSaldoAcumulado();
        // obtengo la lista de objetos pero filtrada
        List<DriverObjetoLinea> listaFiltradaOficina = lstDriverObjetoLinea.stream().filter(item -> oficinaCodigo.equals(item.getOficina().getCodigo())).collect(Collectors.toList());
        double totalSigNiveles = listaFiltradaOficina.stream().mapToDouble(f -> f.getPorcentaje()).sum();
        listaFiltradaOficina.forEach((item) -> {
            double saldoDestino = saldo*item.getPorcentaje()/totalSigNiveles;
            Oficina oficina = item.getOficina();
            Banca banca = item.getBanca();
            Producto producto = item.getProducto();
            if (oficina!=null && banca!=null && producto!=null) {
                objetoDAO.insertarDistribucionBatch(oficina.getCodigo(), banca.getCodigo(), producto.getCodigo(), periodo, entidad.getCodigo(), saldoDestino, repartoTipo);
                //objetoDAO.insertarDistribucion(oficina.getCodigo(), banca.getCodigo(), producto.getCodigo(), periodo, entidad.getCodigo(), saldoDestino);
            }
        });
    }
}
