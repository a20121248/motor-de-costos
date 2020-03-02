package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcesosDAO {
    public boolean verificarProcesosEjecutadosPreviamente(int repartoTipo, int periodo, int fase) {
        String queryStr = String.format("" +
            "SELECT COUNT(1) CNT\n" +
            "  FROM EJECUCIONES\n" +
            " WHERE REPARTO_TIPO=%d AND PERIODO=%d AND FASE<%d",
            repartoTipo, periodo, fase);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                return rs.getInt("CNT") == (fase - 1);
        } catch (SQLException ex) {
            Logger.getLogger(ProcesosDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void insertarEjecucionIni(int periodo, int fase, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO ejecuciones(periodo,fase,reparto_tipo,fecha_ini)\n" +
                "VALUES(%d,%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                periodo,fase,repartoTipo,fechaStr);
        ConexionBD.ejecutar(queryStr);
    }

   public void insertarEjecucionFin(int periodo, int fase, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "UPDATE ejecuciones\n" +
                "   SET fecha_fin=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')" +
                " WHERE periodo=%d AND fase=%d AND reparto_tipo=%d",
                fechaStr,periodo,fase,repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
   
   public void borrarEjecuciones(int periodo, int fase, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM ejecuciones\n" +
                " WHERE periodo=%d AND fase>=%d AND reparto_tipo=%d",
                periodo,fase,repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
   
    public Date obtenerFechaEjecucion(int periodo, int fase, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT fecha_ini\n" +
                "  FROM ejecuciones\n" +
                " WHERE periodo=%d AND fase=%d AND reparto_tipo=%d",
                periodo,fase,repartoTipo);
        Date fecha = null;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                fecha = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_ini"));
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ProcesosDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fecha;
    }
}
