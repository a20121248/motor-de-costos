/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ConnectionDB;
import modelo.Traza;

public class TrazaDAO {
    ConnectionDB connection;
    
    public TrazaDAO(){
        this.connection = new ConnectionDB();
    }
    
    public void insertarPorcentajeDistribucion(String codigoCentroOrigen, int nivelCentroOrigen, String codigoCentroDestino, int nivelCentroDestino, double porcentaje, int periodo ){
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO TRAZA_CASCADA(centro_origen_codigo,centro_origen_nivel,centro_destino,centro_destino_nivel,porcentaje,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES( '%s', %d, '%s', %d, %.10f,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigoCentroOrigen,nivelCentroOrigen,codigoCentroDestino, nivelCentroDestino, porcentaje, periodo, fechaStr, fechaStr);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarPorcentajeDistribucionCentros(List<Traza> lista, int periodo ){
        
        ConexionBD.crearStatement();
        for (Traza item: lista){
            String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            String queryStr = String.format(Locale.US, "" +
                "INSERT INTO TRAZA_CASCADA(centro_origen_codigo,centro_origen_nivel,centro_destino_codigo,centro_destino_nivel,producto_codigo,subcanal_codigo,porcentaje,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES( '%s', %d, '%s', %d,'-','-', %.8f,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                item.getCodigoCentroOrigen(),
                item.getNivelCentroOrigen(),
                item.getCodigoCentroDestino(),
                item.getNivelCentroDestino(),
                item.getPorcentaje(),
                periodo,
                fechaStr,
                fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public List<String> listarCodigoCentrosDestinoPorNivel(int nivelCentroDestino, int periodo){
        List<String> lstCodigos = new ArrayList();
        String queryStr = String.format(""
                + "SELECT centro_destino_codigo "
                + "FROM traza_cascada "
                + "WHERE periodo = %d AND  nivel = %d "
                + "order by centro_destino_codigo",
                periodo, nivelCentroDestino);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                lstCodigos.add(rs.getString("centro_destino_codigo"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lstCodigos;
    }
    
    public void borrarTrazaCascadaPeriodo(int periodo){
        String queryStr = String.format(Locale.US, "" +
                "DELETE FROM traza_cascada WHERE periodo = %d ",
                periodo);
        ConexionBD.ejecutar(queryStr);
    }

    public void insertarPorcentajeDistribucionObjetos(List<Traza> lista, int periodo) {
        ConexionBD.crearStatement();
        for (Traza item: lista){
            String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            String queryStr = String.format(Locale.US, "" +
                "INSERT INTO TRAZA_CASCADA(centro_origen_codigo,centro_origen_nivel,centro_destino_codigo,centro_destino_nivel,producto_codigo,subcanal_codigo,porcentaje,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES( '%s', %d, '%s', %d,'%s','%s', %.8f,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                item.getCodigoCentroOrigen(),
                item.getNivelCentroOrigen(),
                item.getCodigoCentroDestino(),
                item.getNivelCentroDestino(),
                item.getCodigoProducto(),
                item.getCodigoSubcanal(),
                item.getPorcentaje(),
                periodo,
                fechaStr,
                fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
}
