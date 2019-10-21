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
   public void updateCierreProceso(int periodo, int repartoTipo, int value) {   
        String queryStr = String.format("" +
                "UPDATE MS_cierre_estado\n" +
                "   SET estado = %d" +
                " WHERE periodo=%d AND reparto_tipo=%d",
                ((value + 1)%2), periodo, repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
   
   public void insertarCierreProceso(int periodo, int repartoTipo, int value) {        
        String queryStr = String.format("" +
                "INSERT INTO MS_cierre_estado(periodo,reparto_tipo,estado)\n" +
                "VALUES(%d,%d,%d)",
                periodo,repartoTipo,value);
        ConexionBD.ejecutar(queryStr);
    }
   
   public int obtenerEstadoProceso(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT estado\n" +
                "  FROM MS_cierre_estado\n" +
                " WHERE periodo=%d AND reparto_tipo=%d",
                periodo,repartoTipo);
        int estado = 0;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                estado = rs.getInt("estado");
        } catch (SQLException ex) {
            Logger.getLogger(ProcesosDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return estado;
    }
   
   public void insertarEjecucionIni(int periodo, int fase, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO ms_ejecuciones(periodo,fase,reparto_tipo,fecha_ini)\n" +
                "VALUES(%d,%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                periodo,fase,repartoTipo,fechaStr);
        ConexionBD.ejecutar(queryStr);
    }

   public void insertarEjecucionFin(int periodo, int fase, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "UPDATE MS_ejecuciones\n" +
                "   SET fecha_fin=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')" +
                " WHERE periodo=%d AND fase=%d AND reparto_tipo=%d",
                fechaStr,periodo,fase,repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
   
   public void borrarEjecuciones(int periodo, int fase, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_ejecuciones\n" +
                " WHERE periodo=%d AND fase>=%d AND reparto_tipo=%d",
                periodo,fase,repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
   
    public Date obtenerFechaEjecucion(int periodo, int fase, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT fecha_ini\n" +
                "  FROM MS_ejecuciones\n" +
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
