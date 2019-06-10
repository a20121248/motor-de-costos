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
import modelo.Banca;
import modelo.Centro;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import modelo.Oficina;
import modelo.Producto;

public class DriverDAO {
    DriverLineaDAO driverLineaDAO; 
    
    public DriverDAO() {
        driverLineaDAO = new DriverLineaDAO();
    }
    
    public int eliminarDriverObjeto(String codigo) {
        String queryStr;
        
        queryStr = String.format("DELETE FROM driver_obco_lineas WHERE driver_codigo='%s'",codigo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("DELETE FROM drivers WHERE codigo='%s'",codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarDriverCentro(String codigo) {
        String queryStr;
        
        queryStr = String.format("DELETE FROM driver_lineas WHERE driver_codigo='%s'",codigo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("DELETE FROM drivers WHERE codigo='%s'",codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public List<DriverObjetoLinea> obtenerDriverObjetoLinea(int periodo, String codigo) {
        String queryStr = String.format("" +
                "SELECT A.oficina_codigo,B.nombre oficina_nombre,A.banca_codigo,C.nombre banca_nombre,A.producto_codigo,D.nombre producto_nombre,A.porcentaje\n" +
                "  FROM driver_obco_lineas A\n" +
                "  JOIN oficinas B ON A.oficina_codigo=B.codigo\n" +
                "  JOIN bancas C ON A.banca_codigo=C.codigo\n" +
                "  JOIN productos D ON A.producto_codigo=D.codigo" +
                " WHERE A.periodo=%d AND A.driver_codigo='%s'",
                periodo,codigo);
        List<DriverObjetoLinea> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String oficinaCodigo = rs.getString("oficina_codigo");
                String oficinaNombre = rs.getString("oficina_nombre");
                String bancaCodigo = rs.getString("banca_codigo");
                String bancaNombre = rs.getString("banca_nombre");
                String productoCodigo = rs.getString("producto_codigo");
                String productoNombre = rs.getString("producto_nombre");
                double porcentaje = rs.getDouble("porcentaje");
                Oficina oficina = new Oficina(oficinaCodigo,oficinaNombre,null,0,null,null);
                Banca banca = new Banca(bancaCodigo,bancaNombre,null,0,null,null);
                Producto producto = new Producto(productoCodigo,productoNombre,null,0,null,null);
                DriverObjetoLinea item = new DriverObjetoLinea(banca,oficina,producto,porcentaje,null,null);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public String obtenerNombreDriver(String driverCodigo, int repartoTipo){
        String queryStr = String.format("" +
                "  SELECT A.nombre\n" +
                "    FROM drivers A\n" +
                "   WHERE A.codigo='%s' AND B.reparto_tipo=%d\n",
                driverCodigo,repartoTipo);
        String lista = "";
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                lista = rs.getString("nombre");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<DriverLinea> obtenerLstDriverLinea(int periodo, String driverCodigo, int repartoTipo) {
        String queryStr = String.format("" +
                "  SELECT B.codigo,B.nombre,B.nivel,A.porcentaje\n" +
                "    FROM driver_lineas A\n" +
                "    JOIN centros B ON A.entidad_destino_codigo=B.codigo\n" +
                "   WHERE A.periodo=%d AND A.driver_codigo='%s' AND B.reparto_tipo=%d\n"+
                "ORDER BY A.driver_codigo, B.codigo",
                periodo,driverCodigo,repartoTipo);
        List<DriverLinea> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                int nivel = rs.getInt("nivel");
                double porcentaje = rs.getDouble("porcentaje");               
                Centro centro = new Centro(codigo, nombre, nivel, null, porcentaje, null, null, null);
                DriverLinea item = new DriverLinea(centro, porcentaje, null, null);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    /*public List<DriverObjeto> listarDriversObjeto(int periodo, int repartoTipo) {
        List<DriverObjeto> lista = listarDriversObjetoSinDetalle(periodo, repartoTipo);
        lista.forEach((driver) -> {
            List<DriverObjetoLinea> listaDriverLinea = driverLineaDAO.obtenerListaDriverObjetoLinea(driver.getCodigo(), periodo);
            driver.setListaDriverObjetoLinea(listaDriverLinea);
        });
        return lista;
    }*/
    
    public int actualizarListaDriverObjeto(List<DriverObjeto> lista, int periodo) {
        int resultado = 0;
        for (DriverObjeto driverObjeto: lista) {
            actualizarDriverObjeto(driverObjeto, periodo);
            ++resultado;
        }
        return resultado;
    }
    
    public int insertarListaDriverObjeto(List<DriverObjeto> lista, int periodo, int repartoTipo) {
        int resultado = 0;
        for (DriverObjeto driverObjeto: lista) {
            insertarDriverObjeto(driverObjeto, periodo, repartoTipo);
            ++resultado;
        }
        return resultado;
    }
    
    public int actualizarDriverObjeto(DriverObjeto driver, int periodo) {        
        int resultado = actualizarDriverCabecera(driver.getCodigo(),driver.getNombre(),driver.getDescripcion(),"OBCO");
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverObjetoLinea(driver.getCodigo(), periodo, driver.getListaDriverObjetoLinea());
        return resultado;
    }
    
    public int insertarDriverObjeto(DriverObjeto driver, int periodo, int repartoTipo) {
        int resultado = insertarDriverCabecera(driver.getCodigo(),driver.getNombre(),driver.getDescripcion(),"OBCO",repartoTipo);
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverObjetoLineaBatch(driver.getCodigo(), periodo, driver.getListaDriverObjetoLinea());
        return resultado;
    }
    
    public int actualizarListaDriverCentro(List<DriverCentro> lista, int periodo) {
        int resultado = 0;
        for (DriverCentro driverCentro: lista) {
            actualizarDriverCentro(driverCentro, periodo);
            ++resultado;
        }
        return resultado;
    }
    
    public int insertarListaDriverCentro(List<DriverCentro> lista, int periodo, int repartoTipo) {
        int resultado = 0;
        for (DriverCentro driverCentro: lista) {
            insertarDriverCentro(driverCentro, periodo, repartoTipo);
            ++resultado;
        }
        return resultado;
    }

    public int actualizarDriverCentro(DriverCentro driver, int periodo) {        
        int resultado = actualizarDriverCabecera(driver.getCodigo(),driver.getNombre(),driver.getDescripcion(),"CECO");
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverLinea(driver.getCodigo(), periodo, driver.getListaDriverLinea());
        return 1;
    }

    public int insertarDriverCentro(DriverCentro driver, int periodo, int repartoTipo) {        
        int resultado = insertarDriverCabecera(driver.getCodigo(),driver.getNombre(),driver.getDescripcion(),"CECO",repartoTipo);
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverLinea(driver.getCodigo(), periodo, driver.getListaDriverLinea());
        return resultado;
    }

    public int actualizarDriverCabecera(String codigo, String nombre, String descripcion, String driverTipoCodigo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "UPDATE drivers\n" +
                "   SET nombre='%s',descripcion='%s',driver_tipo_codigo='%s',fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                " WHERE codigo='%s'",
                nombre,
                descripcion,
                driverTipoCodigo,
                fechaStr,
                codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarDriverCabecera(String codigo, String nombre, String descripcion, String driverTipoCodigo, int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO drivers(codigo,nombre,descripcion,driver_tipo_codigo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,
                nombre,
                descripcion,
                driverTipoCodigo,
                repartoTipo,
                fechaStr,
                fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }

    /*public List<DriverCentro> listarDriversCentro(int periodo, int repartoTipo) {
        List<DriverCentro> lista = listarDriversCentroSinDetalle(periodo, repartoTipo);
        lista.forEach((driver) -> {
            List<DriverLinea> listaDriverLinea = driverLineaDAO.obtenerListaDriverLinea(driver.getCodigo(), periodo, repartoTipo);
            driver.setListaDriverLinea(listaDriverLinea);
        });
        return lista;
    }*/

    public List<DriverCentro> listarDriversCentroMaestro() {
        String queryStr = "" +
                "SELECT codigo\n" +
                "  FROM drivers\n" +
                " WHERE driver_tipo_codigo='CECO'\n" +
                " ORDER BY codigo";
        List<DriverCentro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                DriverCentro driver = new DriverCentro(codigo, null, null, null, null, null, null);
                lista.add(driver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<DriverObjeto> listarDriversObjetoMaestro() {
        String queryStr = "" +
                "SELECT codigo\n" +
                "  FROM drivers\n" +
                " WHERE driver_tipo_codigo='OBCO'\n" +
                " ORDER BY codigo";
        List<DriverObjeto> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                DriverObjeto driver = new DriverObjeto(codigo, null, null, null, null, null, null);
                lista.add(driver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<DriverObjeto> listarDriversObjetoSinDetalle(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT DISTINCT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.descripcion,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM drivers A\n" +
                "  JOIN driver_obco_lineas C ON C.driver_codigo=A.codigo\n" +
                " WHERE C.periodo=%d AND A.driver_tipo_codigo='OBCO' AND reparto_tipo=%d\n" +
                " ORDER BY A.codigo",
                periodo,repartoTipo);
        List<DriverObjeto> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                
                //List<DriverLinea> listaDriverLinea = driverLineaDAO.obtenerListaDriverLinea(codigo, periodo);
                DriverObjeto driver = new DriverObjeto(codigo, nombre, descripcion, null, null, fechaCreacion, fechaActualizacion);
                lista.add(driver);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<DriverCentro> listarDriversCentroSinDetalle(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT DISTINCT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.descripcion,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM drivers A\n" +
                "  JOIN driver_lineas C ON C.driver_codigo=A.codigo\n" +
                " WHERE C.periodo=%d AND A.driver_tipo_codigo='CECO' AND reparto_tipo=%d\n" +
                " ORDER BY A.codigo",
                periodo,repartoTipo);
        List<DriverCentro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                
                //List<DriverLinea> listaDriverLinea = driverLineaDAO.obtenerListaDriverLinea(codigo, periodo);
                DriverCentro driver = new DriverCentro(codigo, nombre, descripcion, null, null, fechaCreacion, fechaActualizacion);
                lista.add(driver);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public String obtenerCodigoDriver(String codigoEntidad, int periodo) {
       String queryStr = String.format("" +
               "SELECT driver_codigo\n" +
               "  FROM entidad_origen_driver\n" +
               " WHERE entidad_origen_codigo='%s' AND periodo=%d",
                codigoEntidad, periodo);       
        String codigoDriver = null;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                codigoDriver = rs.getString("driver_codigo");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigoDriver;
    }
}
