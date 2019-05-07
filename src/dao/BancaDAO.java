package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.CargarObjetoLinea;
import modelo.CargarObjetoPeriodoLinea;
import modelo.Banca;

public class BancaDAO {
    public List<Banca> listarMaestro(String codigos) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = "SELECT codigo,nombre FROM bancas WHERE esta_activo=1 ORDER BY codigo";
        } else {
            queryStr = String.format(""+
                    "SELECT codigo,nombre\n" +
                    "  FROM bancas\n" +
                    " WHERE esta_activo=1 AND codigo NOT IN (%s)\n" +
                    " ORDER BY codigo",
                    codigos);
        }
        List<Banca> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Banca item = new Banca(codigo, nombre, null, 0, null, null);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BancaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Banca> listar(int periodo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM bancas A\n" +
                "  JOIN banca_lineas B ON B.banca_codigo=A.codigo\n" +
                " WHERE A.esta_activo=1 AND B.periodo=%d\n" +
                " GROUP BY A.codigo,A.nombre,A.fecha_creacion,A.fecha_actualizacion\n" +
                " ORDER BY A.codigo",
                periodo);
        List<Banca> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Banca item = new Banca(codigo, nombre, null, 0, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(BancaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Banca> listarObjetos() {
        String queryStr = "" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.esta_activo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM bancas A\n" +
                " ORDER BY A.codigo";
        List<Banca> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = 0;
                Banca item = new Banca(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(BancaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public void insertarListaObjeto(List<CargarObjetoLinea> lista) {
        ConexionBD.crearStatement();
        for (CargarObjetoLinea item: lista) {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO bancas(codigo,nombre,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,1,fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();        
    }
    
    public void insertarListaObjetoPeriodo(List<CargarObjetoPeriodoLinea> lista) {
        ConexionBD.crearStatement();
        for (CargarObjetoPeriodoLinea item: lista) {
            int periodo = item.getPeriodo();
            String codigo = item.getCodigo();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            
            // inserto una linea dummy
            String queryStr = String.format("" +
                    "INSERT INTO banca_lineas(banca_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES('%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,periodo,fechaStr,fechaStr);            
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();        
    }

    public void actualizarObjeto(String codigo, String nombre, int estaActivo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "UPDATE bancas\n" +
                "   SET nombre='%s',esta_activo=%d,fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                " WHERE codigo='%s'",
                nombre,estaActivo,fechaStr,codigo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarObjeto(String codigo, String nombre) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO bancas(codigo,nombre,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,1,fechaStr,fechaStr);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarObjetoPeriodo(String codigo, int periodo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO banca_lineas(banca_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,fechaStr,fechaStr);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void eliminarObjeto(String codigo) {
        String queryStr = String.format("" +
                "DELETE FROM bancas\n" +
                " WHERE codigo='%s'",
                codigo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void eliminarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM banca_lineas\n" +
                " WHERE banca_codigo='%s' AND periodo=%d",
                codigo,periodo);
        ConexionBD.ejecutar(queryStr);
    }
}
