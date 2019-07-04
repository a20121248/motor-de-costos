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
                + "SELECT distinct centro_destino_codigo "
                + "FROM traza_cascada "
                + "WHERE periodo = %d AND  centro_destino_nivel = %d "
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
    
    public List<String> listarCodigoCentrosDestinoMenoresNivel(int nivelCentroDestino, int periodo){
        List<String> lstCodigos = new ArrayList();
        String queryStr = String.format(""
                + "SELECT distinct centro_destino_codigo "
                + "FROM traza_cascada "
                + "WHERE periodo = %d AND  centro_origen_nivel < %d and centro_destino_nivel!=0"
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
    
    public int contarItems(int periodo){
        int cnt = 0;
        String queryStr = String.format(""+
                "select count(*) CNT\n" +
                "from (select  distinct centro_destino_codigo,producto_codigo,subcanal_codigo \n" +
                "        from  Traza_cascada \n" +
                "       where  PERIODO = %d)",
                periodo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                cnt = rs.getInt("CNT");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }
    
    public List<Traza> listarCentrosDestino(String centroOrigen, int periodo){
        List<Traza> lstCentros = new ArrayList();
        String queryStr = String.format(""+ 
                "SELECT  centro_origen_codigo,\n" +
                "        centro_origen_nivel,\n" +
                "        centro_destino_codigo,\n" +
                "        centro_destino_nivel,\n" +
                "        porcentaje\n" +
                "        \n" +
                "FROM traza_cascada\n" +
                "WHERE periodo = %d and centro_origen_codigo = '%s'",
                periodo,centroOrigen);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCentroOrigen = rs.getString("centro_origen_codigo");
                int nivelCentroOrigen = rs.getInt("centro_origen_nivel");
                String codigoCentroDestino = rs.getString("centro_destino_codigo");
                int nivelCentroDestino = rs.getInt("centro_destino_nivel");
                double porcentaje = rs.getDouble("porcentaje");
                Traza item = new Traza(codigoCentroOrigen, nivelCentroOrigen, codigoCentroDestino, nivelCentroDestino, porcentaje);
                lstCentros.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lstCentros;
    }
    
    public List<Traza> listarCentros(int periodo){
        List<Traza> lstCentros = new ArrayList();
        String queryStr = String.format(""+ 
                "select distinct centro_destino_codigo,\n" +
                "                producto_codigo,\n" +
                "                subcanal_codigo,\n" +
                "                case when centro_destino_nivel=0 then 900\n" +
                "                    else centro_destino_nivel\n" +
                "                end centro_destino_nivel\n" +
                "from traza_cascada\n" +
                "where periodo = %d\n" +
                "order by centro_destino_nivel, centro_destino_codigo,producto_codigo,subcanal_codigo",
                periodo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCentroDestino = rs.getString("centro_destino_codigo");
                String codigoProducto = rs.getString("producto_codigo");
                String codigoSubcanal = rs.getString("subcanal_codigo");
                int nivelCentroDestino = rs.getInt("centro_destino_nivel");
                Traza item = new Traza(codigoCentroDestino, codigoProducto, codigoSubcanal, nivelCentroDestino);
                lstCentros.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lstCentros;
    }
}
