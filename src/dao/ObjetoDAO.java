package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Banca;
import modelo.CargarObjetoPeriodoLinea;
import modelo.EntidadDistribucion;
import modelo.Oficina;
import modelo.Producto;

public class ObjetoDAO {
    String prefixTableName;
    
    public ObjetoDAO(String objetoTipo) {
        prefixTableName = "";
        switch (objetoTipo) {
            case "OFI":
                prefixTableName = "OFICINA";
                break;
            case "BAN":
                prefixTableName = "BANCA";
                break;
            case "PRO":
                prefixTableName = "PRODUCTO";
                break;
        }
    }
    
    public List<String> listarCodigos() {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery("SELECT CODIGO FROM " + prefixTableName+"s")) {
            while(rs.next()) lista.add(rs.getString("CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(ObjetoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigos(String codigo) {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(String.format("SELECT CODIGO FROM " + prefixTableName + "S WHERE CODIGO!='%s'",codigo))) {
            while(rs.next()) lista.add(rs.getString("CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(ObjetoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<EntidadDistribucion> listarMaestro(String codigos) {
        String queryStr = String.format("SELECT CODIGO,NOMBRE\n  FROM %s  WHERE ESTA_ACTIVO=1\n",prefixTableName);
        if (!codigos.isEmpty())
            queryStr += String.format("   AND CODIGO NOT IN (%s)\n",codigos);
        queryStr += " ORDER BY CODIGO";
        List<EntidadDistribucion> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                switch (prefixTableName) {
                    case "OFICINA":
                        lista.add(new Oficina(codigo, nombre, null, 0, null, null));
                        break;
                    case "BANCA":
                        lista.add(new Banca(codigo, nombre, null, 0, null, null));
                        break;
                    case "PRODUCTO":
                        lista.add(new Producto(codigo, nombre, null, 0, null, null));
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObjetoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<EntidadDistribucion> listar(int periodo) {
        String queryStr = String.format("" +
                "SELECT A.CODIGO,\n" +
                "       A.NOMBRE,\n" +
                "       A.FECHA_CREACION,\n" +
                "       A.FECHA_ACTUALIZACION\n" +
                "  FROM %sS A\n" +
                "  JOIN %s_LINEAS B ON B.%s_CODIGO=A.CODIGO\n" +
                " WHERE A.ESTA_ACTIVO=1 AND B.PERIODO=%d\n" +
                " GROUP BY A.CODIGO,A.NOMBRE,A.FECHA_CREACION,A.FECHA_ACTUALIZACION\n" +
                " ORDER BY A.CODIGO",
                prefixTableName,prefixTableName,prefixTableName,periodo);
        List<EntidadDistribucion> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("FECHA_CREACION"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("FECHA_ACTUALIZACION"));
                switch (prefixTableName) {
                    case "OFICINA":
                        lista.add(new Oficina(codigo, nombre, null, 0, fechaCreacion, fechaActualizacion));
                        break;
                    case "BANCA":
                        lista.add(new Banca(codigo, nombre, null, 0, fechaCreacion, fechaActualizacion));
                        break;
                    case "PRODUCTO":
                        lista.add(new Producto(codigo, nombre, null, 0, fechaCreacion, fechaActualizacion));
                        break;
                }
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ObjetoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<EntidadDistribucion> listarObjetos() {
        String queryStr = String.format("" +
                "SELECT A.CODIGO,\n" +
                "       A.NOMBRE,\n" +
                "       A.ESTA_ACTIVO,\n" +
                "       A.FECHA_CREACION,\n" +
                "       A.FECHA_ACTUALIZACION\n" +
                "  FROM %sS A\n" +
                " ORDER BY A.CODIGO",prefixTableName);
        List<EntidadDistribucion> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("FECHA_CREACION"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("FECHA_ACTUALIZACION"));
                switch (prefixTableName) {
                    case "OFICINA":
                        lista.add(new Oficina(codigo, nombre, null, 0, fechaCreacion, fechaActualizacion));
                        break;
                    case "BANCA":
                        lista.add(new Banca(codigo, nombre, null, 0, fechaCreacion, fechaActualizacion));
                        break;
                    case "PRODUCTO":
                        lista.add(new Producto(codigo, nombre, null, 0, fechaCreacion, fechaActualizacion));
                        break;
                }
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ObjetoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public void insertarListaObjeto(List<EntidadDistribucion> lista) {
        ConexionBD.crearStatement();
        lista.stream().filter(item -> item.getFlagCargar()).map((item) -> {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO %sS(CODIGO,NOMBRE,ESTA_ACTIVO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    prefixTableName,codigo,nombre,1,fechaStr,fechaStr);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();        
    }
    
    public int borrarListaAsignacion(int periodo, String tipo) {
        String queryStr = String.format("" +
                "DELETE FROM %s_LINEAS\n" +
                " WHERE PERIODO=%d",
                tipo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarListaObjetoPeriodo(int periodo, List<CargarObjetoPeriodoLinea> lista) {
        borrarListaAsignacion(periodo, prefixTableName);
        ConexionBD.crearStatement();
        for (CargarObjetoPeriodoLinea item: lista) {
            String codigo = item.getCodigo();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            
            // inserto una linea dummy
            String queryStr = String.format("" +
                    "INSERT INTO %s_LINEAS(%s_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES('%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    prefixTableName,prefixTableName,codigo,periodo,fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();        
    }

    public int actualizarObjeto(String codigo, String nombre, String codigoAnt) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "UPDATE %sS\n" +
                "   SET CODIGO='%s',NOMBRE='%s',FECHA_ACTUALIZACION=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                " WHERE CODIGO='%s'",
                prefixTableName,codigo,nombre,fechaStr,codigoAnt);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjeto(String codigo, String nombre) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO %sS(CODIGO,NOMBRE,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                "VALUES ('%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                prefixTableName,codigo,nombre,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarObjetoPeriodo(String codigo, int periodo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO %s_LINEAS(%s_CODIGO,PERIODO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                "VALUES('%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                prefixTableName,prefixTableName,codigo,periodo,fechaStr,fechaStr);
        ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarObjeto(String codigo) {
        String queryStr = String.format("" +
                "DELETE FROM %sS\n" +
                " WHERE codigo='%s'",
                prefixTableName,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void eliminarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM %s_LINEAS\n" +
                " WHERE %s_CODIGO='%s' AND PERIODO=%d",
                prefixTableName,prefixTableName,codigo,periodo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void borrarDistribuciones(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM obco_lineas\n" +
                " WHERE periodo=%d AND reparto_tipo=%d",
                periodo, repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarDistribucion(String oficinaCodigo, String bancaCodigo, String productoCodigo, int periodo, String entidadOrigenCodigo, double saldo, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO obco_lineas(oficina_codigo,banca_codigo,producto_codigo,periodo,entidad_origen_codigo,saldo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s','%s','%s',%d,'%s',%.8f,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                oficinaCodigo,bancaCodigo,productoCodigo,periodo,entidadOrigenCodigo,saldo,repartoTipo,fechaStr,fechaStr);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarDistribucionBatch(String oficinaCodigo, String bancaCodigo, String productoCodigo, int periodo, String entidadOrigenCodigo, double saldo, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO obco_lineas(oficina_codigo,banca_codigo,producto_codigo,periodo,entidad_origen_codigo,saldo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s','%s','%s',%d,'%s',%.8f,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                oficinaCodigo,bancaCodigo,productoCodigo,periodo,entidadOrigenCodigo,saldo,repartoTipo,fechaStr,fechaStr);
        ConexionBD.agregarBatch(queryStr);
    }
}
