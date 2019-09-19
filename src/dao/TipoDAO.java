package dao;

import controlador.ConexionBD;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ConnectionDB;
import modelo.Tipo;

public class TipoDAO {
    ConnectionDB connection;
    
    public TipoDAO() {
        this.connection = new ConnectionDB();
    }

    public List<Tipo> listarCentroNiveles() {
        String queryStr = "SELECT codigo,nombre,descripcion FROM MS_centro_niveles order by nombre";
        List<Tipo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Tipo tipo = new Tipo(codigo,nombre,descripcion);
                lista.add(tipo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TipoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Tipo> listarCentroTipos() {
        String queryStr = "SELECT codigo,nombre,descripcion FROM MS_centro_tipos";
        List<Tipo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Tipo tipo = new Tipo(codigo,nombre,descripcion);
                lista.add(tipo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TipoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Tipo> listarDriverTipos() {
        List<Tipo> lista = new ArrayList();
        try {
            Connection access = connection.getConnection();
            ResultSet rs = access.createStatement().executeQuery("SELECT codigo,nombre,descripcion FROM driver_tipos");
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Tipo tipo = new Tipo(codigo,nombre,descripcion);
                lista.add(tipo);
            }
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause());
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;
    }
    
    public List<Tipo> listarEntidadTipos() {
        String queryStr = "SELECT CODIGO,NOMBRE FROM MS_ENTIDAD_TIPOS";
        List<Tipo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                Tipo tipo = new Tipo(codigo,nombre);
                lista.add(tipo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TipoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Tipo> listarGrupoGastos() {
        String queryStr = "SELECT CODIGO,NOMBRE FROM MS_GRUPO_GASTOS";
        List<Tipo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                Tipo tipo = new Tipo(codigo,nombre);
                lista.add(tipo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TipoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
}
