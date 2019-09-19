package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Permiso;
import modelo.Rol;
import modelo.Usuario;

public class SeguridadDAO {
    public Permiso obtenerPermisoRol(String permisoCodigo, String rolCodigo) {
        String queryStr = String.format("" +
                "SELECT B.codigo,B.nombre,B.descripcion\n" +
                "  FROM seguridad_permiso_rol A\n" +
                "  JOIN seguridad_permisos B ON A.permiso_codigo=B.codigo\n" +
                " WHERE rol_codigo='%s' AND permiso_codigo='%s'",
                rolCodigo,permisoCodigo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                return new Permiso(codigo, nombre, descripcion);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Permiso obtenerPermiso(String permisoCodigo) {
        String queryStr = String.format("" +
                "SELECT codigo,nombre,descripcion\n" +
                "  FROM seguridad_permisos\n" +
                " WHERE codigo='%s'",
                permisoCodigo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                return new Permiso(codigo, nombre, descripcion);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Rol obtenerRol(String rolCodigo) {
        String queryStr = String.format("" +
                "SELECT codigo,nombre,descripcion\n" +
                "  FROM seguridad_roles\n" +
                " WHERE codigo='%s'",
                rolCodigo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                return new Rol(codigo, nombre, descripcion);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Usuario obtenerUsuario(String usuarioCodigo) {
        String queryStr = String.format("" +
                "SELECT A.usuario,A.contrasenha,A.nombres,A.apellidos,\n" +
                "       B.codigo,B.nombre,B.descripcion\n" +
                "  FROM MS_seguridad_usuarios A\n" +
                "  JOIN MS_seguridad_roles B ON A.rol_codigo=B.codigo\n" +
                " WHERE usuario='%s'",usuarioCodigo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String usuario = rs.getString("usuario");
                String contrasenha = rs.getString("contrasenha");
                String nombres = rs.getString("nombres");
                String apellidos = rs.getString("apellidos");
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Rol rol = new Rol(codigo, nombre, descripcion);
                return new Usuario(usuario, contrasenha, nombres, apellidos, rol);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SeguridadDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
