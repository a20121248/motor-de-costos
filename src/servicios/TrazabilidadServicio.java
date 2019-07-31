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
    
    public void ingresarPorcentajesObjetos(int periodo) {
        trazaDAO.insertarPorcentajeDistribucionObjetos(periodo);
    }
    
    public void generarMatriz(int periodo, int repartoTipo){
        double [][] porcentajes;
        List<Traza> listaCentrosDestinos = trazaDAO.listarDestinos(periodo,"!=");
        Date fase1 = procesosDAO.obtenerFechaEjecucion(periodo, 1, repartoTipo);
        Date fase2 = procesosDAO.obtenerFechaEjecucion(periodo, 2, repartoTipo);
        Date fase3 = procesosDAO.obtenerFechaEjecucion(periodo, 3, repartoTipo);
        if(fase1 == null & fase2 == null & fase3 == null ){
//            enviar mensaje alerta
            return;
        }
        int maxNumerosDestinos = trazaDAO.contarItems(periodo);
        int maxNivelCascada = centroDAO.maxNivelCascada(periodo,repartoTipo);
        porcentajes = new double[listaCentrosDestinos.size()][listaCentrosDestinos.size()];
        
        //Carga de los porcentajes a la matriz base.
        for(int iter = 1;iter<=maxNivelCascada;iter++ ){
            List <String> centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(iter, periodo);
            for(String codigoCentroOrigen:centros){
                List<Traza>  centrosDestino = trazaDAO.listarCentrosDestino(codigoCentroOrigen, periodo);
                Traza trazaOrigen = listaCentrosDestinos.stream().filter(item ->codigoCentroOrigen.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                int indexOrigen = listaCentrosDestinos.indexOf(trazaOrigen);
                for(Traza finales:centrosDestino){
                    Traza trazaDestino = listaCentrosDestinos.stream().filter(item ->finales.getCodigoCentroDestino().equals(item.getCodigoCentroDestino())).findAny().orElse(null);   
                    int indexDestino = listaCentrosDestinos.indexOf(trazaDestino);
                    porcentajes[indexDestino][indexOrigen] = finales.getPorcentaje(); 
                    if(iter==maxNivelCascada){
                        porcentajes[indexDestino][indexDestino] = 1.0;
                    }
                }
            }
        }
        System.out.println("terminado");
        for (double[] porcentaje : porcentajes) {
            for (int y = 0; y < porcentaje.length; y++) {
                System.out.print(porcentaje[y]+"\t,");
            }
            System.out.println();           
        }
        System.out.println();
//        Proceso de calculo de la traza
        double[][] porcentajesCascada = new double[listaCentrosDestinos.size()][listaCentrosDestinos.size()];
        for (int x = 0;x < listaCentrosDestinos.size();x++) {
            System.arraycopy(porcentajes[x], 0, porcentajesCascada[x], 0, listaCentrosDestinos.size());           
        }
        
        System.out.println("terminado");
        for (double[] porcentaje : porcentajesCascada) {
            for (int y = 0; y < porcentaje.length; y++) {
                System.out.print(porcentaje[y]+"\t,");
            }
            System.out.println();           
        }
        
        for(int iter = 2; iter <=maxNivelCascada;iter++){
            List<String> centros ;
            List<String> centrosOrigenNivelSuperiorCascada; //Centros de nivel superior matriz Cascada.
            List<String> centrosOrigenNivel; // Centros de nivel inferior matriz porcentajes.
            
            if(iter != maxNivelCascada){
                centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(iter+1, periodo);
            }
            else{
                centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(0, periodo);
            }
            for(int i =iter-1; i>0;i--)
            {   
                centrosOrigenNivelSuperiorCascada = trazaDAO.listarCodigoCentrosOrigenMayorNivel(i,iter+1, periodo);
                centrosOrigenNivel = trazaDAO.listarCodigoCentrosDestinoPorNivel(i, periodo);
                
                for(String centro: centros){
                    Traza trazaCentro = listaCentrosDestinos.stream().filter(item ->centro.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                    int indexDestino = listaCentrosDestinos.indexOf(trazaCentro);
                    for(String centroOrigenSuperior: centrosOrigenNivelSuperiorCascada){
                        Traza trazaCentroOrigenSuperior = listaCentrosDestinos.stream().filter(item ->centroOrigenSuperior.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                        int indexOrigenSuperior = listaCentrosDestinos.indexOf(trazaCentroOrigenSuperior);
                        for(String centroOrigen: centrosOrigenNivel){
                            Traza trazaCentroOrigen = listaCentrosDestinos.stream().filter(item ->centroOrigen.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                            int indexOrigen = listaCentrosDestinos.indexOf(trazaCentroOrigen);
                            porcentajesCascada[indexDestino][indexOrigen]+=porcentajes[indexOrigenSuperior][indexOrigen]*porcentajesCascada[indexDestino][indexOrigenSuperior];
                        }
                    }
                }
            }  
        }
//        
//        for(int iter = 2 ;iter<=maxNivelCascada;iter++ ){
//            List <String> centros;
//            List<String> centrosNivelMenores;
//            List<String> centrosNivelAnterior;
//            if(iter!=maxNivelCascada){
//                centrosNivelAnterior = trazaDAO.listarCodigoCentrosDestinoPorNivel(iter, periodo);
//                centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(iter+1, periodo);
//                centrosNivelMenores = trazaDAO.listarCodigoCentrosDestinoMenoresNivel(iter, periodo);
//            }else{
//                centrosNivelAnterior = trazaDAO.listarCodigoCentrosDestinoPorNivel(maxNivelCascada, periodo);
//                centros = trazaDAO.listarCodigoCentrosDestinoPorNivel(0, periodo);
//                centrosNivelMenores = trazaDAO.listarCodigoCentrosDestinoMenoresNivel(maxNivelCascada, periodo);
//            }            
//            for(String centro: centros){
//                Traza traza = listaCentrosDestinos.stream().filter(item ->centro.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
//                int index = listaCentrosDestinos.indexOf(traza);
//                for(String origen: centrosNivelMenores){
//                    Traza trazaOrigen = listaCentrosDestinos.stream().filter(item ->origen.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
//                    int indexOrigen = listaCentrosDestinos.indexOf(trazaOrigen);
//                    for(String nivelAnterior: centrosNivelAnterior){
//                        Traza trazaNivelAnterior = listaCentrosDestinos.stream().filter(item ->nivelAnterior.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
//                        int indexNivelAnterior = listaCentrosDestinos.indexOf(trazaNivelAnterior);
//                        porcentajesCascada[index][indexOrigen]+=porcentajes[index][indexNivelAnterior]*porcentajesCascada[indexNivelAnterior][indexOrigen];
//                    }
//                }
//            }     
//        }
        
        System.out.println("terminado");
        for (double[] porcentaje : porcentajesCascada) {
            for (int y = 0; y < porcentaje.length; y++) {
                System.out.print(porcentaje[y]+"\t,");
            }
            System.out.println();           
        }
        
        List<Traza> listaObjetosDestinos = trazaDAO.listarDestinos(periodo,"=");
        double [][] porcentajesObjetos = new double[listaObjetosDestinos.size()][listaCentrosDestinos.size()];
        List <String> centrosOrigenDirecto = trazaDAO.listarCodigoCentrosDestinoPorNivel(0, periodo);
        for(String codigoCentroOrigen:centrosOrigenDirecto){
            List<Traza>  objetosDestino = trazaDAO.listarObjetosDestino(codigoCentroOrigen, periodo);
            Traza trazaOrigen = listaCentrosDestinos.stream().filter(item ->codigoCentroOrigen.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
            int indexOrigen = listaCentrosDestinos.indexOf(trazaOrigen);
            for(Traza objeto:objetosDestino){
                Traza trazaDestino = listaObjetosDestinos.stream().filter(item ->objeto.getCodigoProducto().equals(item.getCodigoProducto()) && objeto.getCodigoSubcanal().equals(item.getCodigoSubcanal()) && objeto.getGrupoGasto().equals(item.getGrupoGasto())).findAny().orElse(null);   
                int indexDestino = listaObjetosDestinos.indexOf(trazaDestino);
                porcentajesObjetos[indexDestino][indexOrigen] = objeto.getPorcentaje(); 
            }
        }

        List<String> centrosCascada = trazaDAO.listarCodigoCentrosDestinoMenoresNivel(maxNivelCascada+1, periodo);
        for (Traza destino: listaObjetosDestinos){
            int indexDestino = listaObjetosDestinos.indexOf(destino);
            for(String codigoCentroOrigen: centrosCascada){
                Traza centroOrigen = listaCentrosDestinos.stream().filter(item ->codigoCentroOrigen.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                int indexOrigen = listaCentrosDestinos.indexOf(centroOrigen);
                for(String codigoCentroDirecto:centrosOrigenDirecto){
                    Traza centroDirecto = listaCentrosDestinos.stream().filter(item ->codigoCentroDirecto.equals(item.getCodigoCentroDestino())).findAny().orElse(null);
                    int indexDirecto = listaCentrosDestinos.indexOf(centroDirecto);
                    porcentajesObjetos[indexDestino][indexOrigen] += porcentajesObjetos[indexDestino][indexDirecto]*porcentajesCascada[indexDirecto][indexOrigen] ;
                }
            }
        }
        System.out.println("terminado");
        for (double[] porcentaje : porcentajesObjetos) {
            for (int y = 0; y < porcentaje.length; y++) {
                System.out.print(porcentaje[y]+"\t,");
            }
            System.out.println();           
        }
        guardarDatosMatriz(porcentajesObjetos, listaCentrosDestinos, listaObjetosDestinos, periodo);
    }
    
    public void guardarDatosMatriz(double [][] porcentajes, List<Traza> indexCentrosOrigen, List<Traza> indexObjetosDestino, int periodo){
        trazaDAO.borrarTrazaPeriodo(periodo);
        for (int x = 0; x <indexObjetosDestino.size(); x++ ) {
            List <Traza> listaTraza = new ArrayList();
            for (int y = 0; y < indexCentrosOrigen.size(); y++) {
                Traza unidad = new Traza(indexCentrosOrigen.get(y).getCodigoCentroDestino(),indexObjetosDestino.get(x).getCodigoProducto(),indexObjetosDestino.get(x).getCodigoSubcanal(),indexObjetosDestino.get(x).getGrupoGasto(),porcentajes[x][y]);
                listaTraza.add(unidad);
            }
            trazaDAO.insertarListaObjetosPorcentajesGlobales(listaTraza,periodo);          
        }
    }
}
