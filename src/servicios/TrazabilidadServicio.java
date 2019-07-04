package servicios;

import dao.CentroDAO;
import dao.ProcesosDAO;
import dao.TrazaDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import modelo.CentroDriver;
import modelo.DriverLinea;
import modelo.DriverObjetoLinea;
import modelo.Traza;


public class TrazabilidadServicio {
    TrazaDAO trazaDAO;
    ProcesosDAO procesosDAO;
    CentroDAO centroDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.TrazabilidadServicios");
    
    public TrazabilidadServicio(){
        trazaDAO = new TrazaDAO();
        centroDAO = new CentroDAO();
        procesosDAO = new ProcesosDAO();
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
    
    public void generarMatriz(int periodo, int repartoTipo){
        double [][] porcentajes;
        List<Traza> listaDestinos = trazaDAO.listarCentros(periodo);
        Date fase1 = procesosDAO.obtenerFechaEjecucion(periodo, 1, repartoTipo);
        Date fase2 = procesosDAO.obtenerFechaEjecucion(periodo, 2, repartoTipo);
        Date fase3 = procesosDAO.obtenerFechaEjecucion(periodo, 3, repartoTipo);
        if(fase1 == null & fase2 == null & fase3 == null ){
//            enviar mensaje alerta
            return;
        }
        int maxNumerosDestinos = trazaDAO.contarItems(periodo);
        int maxNivelCascada = centroDAO.maxNivelCascada(periodo,repartoTipo);
        porcentajes = new double[maxNumerosDestinos][maxNumerosDestinos];
        
        for(int iter = 1;iter<=maxNivelCascada;iter++ ){
            List <String> centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(iter, periodo);
            for(String codigoCentroOrigen:centros){
                List<Traza>  centrosDestino = trazaDAO.listarCentrosDestino(codigoCentroOrigen, periodo);
                Traza trazaOrigen = listaDestinos.stream().filter(item ->codigoCentroOrigen.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                int indexOrigen = listaDestinos.indexOf(trazaOrigen);
                for(Traza finales:centrosDestino){
                    Traza trazaDestino = listaDestinos.stream().filter(item ->finales.getCodigoCentroDestino().equals(item.getCodigoCentroDestino())).findAny().orElse(null);   
                    int indexDestino = listaDestinos.indexOf(trazaDestino);
                    porcentajes[indexDestino][indexOrigen] = finales.getPorcentaje(); 
                    if(iter==maxNivelCascada){
                        porcentajes[indexDestino][indexDestino] = 1.0;
                    }
                }
            }
        }
        
        for (double[] porcentaje : porcentajes) {
            for (int y = 0; y < porcentaje.length; y++) {
                System.out.print(porcentaje[y]+"\t,");
            }
            System.out.println();           
        }
        System.out.println();
//        Proceso de calculo de la traza
        
        for(int iter = 2 ;iter<=maxNivelCascada;iter++ ){
            List <String> centros;
            List<String> centrosNivelMenores;
            List<String> centrosNivelAnterior;
            if(iter!=maxNivelCascada){
                centrosNivelAnterior = trazaDAO.listarCodigoCentrosDestinoPorNivel(iter, periodo);
                centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(iter+1, periodo);
                centrosNivelMenores = trazaDAO.listarCodigoCentrosDestinoMenoresNivel(iter, periodo);
            }else{
                centrosNivelAnterior = trazaDAO.listarCodigoCentrosDestinoPorNivel(maxNivelCascada, periodo);
                centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(0, periodo);
                centrosNivelMenores = trazaDAO.listarCodigoCentrosDestinoMenoresNivel(maxNivelCascada, periodo);
            }
            
            for(String centro: centros){
                Traza traza = listaDestinos.stream().filter(item ->centro.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                int index = listaDestinos.indexOf(traza);
                for(String origen: centrosNivelMenores){
                    Traza trazaOrigen = listaDestinos.stream().filter(item ->origen.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                    int indexOrigen = listaDestinos.indexOf(trazaOrigen);
                    for(String nivelAnterior: centrosNivelAnterior){
                        Traza trazaNivelAnterior = listaDestinos.stream().filter(item ->nivelAnterior.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                        int indexNivelAnterior = listaDestinos.indexOf(trazaNivelAnterior);
                        porcentajes[index][indexOrigen]+=porcentajes[index][indexNivelAnterior]*porcentajes[indexNivelAnterior][indexOrigen];
                    }
                }
            }     
        }
        

//        for(int i = 2; i< maxNumerosDestinos;i++){
//            for(int j=i-2;j>=0;j--){
//                for(int k = i-1;k>j;k--){
//                    porcentajes[i][j]+= porcentajes[i][k]*porcentajes[k][j];
//                }
//            }
//        }
        
        for (double[] porcentaje : porcentajes) {
            for (int y = 0; y < porcentaje.length; y++) {
                System.out.print(porcentaje[y]+"\t,");
            }
            System.out.println();           
        }
    }
}
