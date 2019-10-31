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
import modelo.Centro;
import modelo.CentroDriver;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.Oficina;
import modelo.Producto;
import modelo.Subcanal;

public class DriverDAO {
    DriverLineaDAO driverLineaDAO; 
    
    public DriverDAO() {
        driverLineaDAO = new DriverLineaDAO();
    }
    
    public int eliminarDriverObjeto(String codigo, int periodo, int repartoTipo) {
        String queryStr;
        
        queryStr = String.format(""+
                "DELETE FROM MS_driver_objeto_lineas \n" +
                "        WHERE driver_codigo='%s' and periodo ='%d' and reparto_tipo='%d'"
                ,codigo,periodo,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarDriverCentro(String codigo, int periodo, int repartoTipo) {
        String queryStr;
        
        queryStr = String.format(""+ 
                "DELETE FROM MS_driver_lineas \n" +
                "      WHERE driver_codigo='%s' and periodo ='%d' and reparto_tipo='%d'"
                ,codigo,periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
//    public List<DriverObjetoLinea> obtenerDriverObjetoLinea(int periodo, String codigo) {
//        String queryStr = String.format("" +
//                "SELECT A.oficina_codigo,B.nombre oficina_nombre,A.banca_codigo,C.nombre banca_nombre,A.producto_codigo,D.nombre producto_nombre,A.porcentaje\n" +
//                "  FROM driver_obco_lineas A\n" +
//                "  JOIN oficinas B ON A.oficina_codigo=B.codigo\n" +
//                "  JOIN bancas C ON A.banca_codigo=C.codigo\n" +
//                "  JOIN productos D ON A.producto_codigo=D.codigo" +
//                " WHERE A.periodo=%d AND A.driver_codigo='%s'",
//                periodo,codigo);
//        List<DriverObjetoLinea> lista = new ArrayList();
//        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
//            while(rs.next()) {
//                String oficinaCodigo = rs.getString("oficina_codigo");
//                String oficinaNombre = rs.getString("oficina_nombre");
//                String bancaCodigo = rs.getString("banca_codigo");
//                String bancaNombre = rs.getString("banca_nombre");
//                String productoCodigo = rs.getString("producto_codigo");
//                String productoNombre = rs.getString("producto_nombre");
//                double porcentaje = rs.getDouble("porcentaje");
//                Oficina oficina = new Oficina(oficinaCodigo,oficinaNombre,null,0,null,null);
//                Banca banca = new Banca(bancaCodigo,bancaNombre,null,0,null,null);
//                Producto producto = new Producto(productoCodigo,productoNombre,null,0,null,null);
//                DriverObjetoLinea item = new DriverObjetoLinea(banca,oficina,producto,porcentaje,null,null);
//                lista.add(item);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return lista;
//    }
    
    public List<DriverObjetoLinea> obtenerDriverObjetoLinea(int periodo, String codigo,int repartoTipo) {
        periodo = repartoTipo == 1? periodo: (int)periodo/100 *100;
        String queryStr = String.format("" +
                "SELECT A.producto_codigo,B.nombre producto_nombre,A.subcanal_codigo, C.nombre subcanal_nombre,A.porcentaje\n" +
                "  FROM MS_driver_objeto_lineas A\n" +
                "  JOIN MS_productos B ON A.producto_codigo=B.codigo  \n" +
                "  JOIN MS_subcanals C ON A.subcanal_codigo=C.codigo\n" +
                " WHERE A.periodo='%d' AND A.driver_codigo='%s' AND a.reparto_tipo = '%d'",
                periodo,codigo,repartoTipo);
        List<DriverObjetoLinea> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String productoCodigo = rs.getString("producto_codigo");
                String productoNombre = rs.getString("producto_nombre");
                String subcanalCodigo = rs.getString("subcanal_codigo");
                String subcanalNombre = rs.getString("subcanal_nombre");
                double porcentaje = rs.getDouble("porcentaje");
                Producto producto = new Producto(productoCodigo,productoNombre,null,0,null,null);
                Subcanal subcanal = new Subcanal(subcanalCodigo, subcanalNombre, null, 0, null, null);
                DriverObjetoLinea item = new DriverObjetoLinea(producto,subcanal,porcentaje,null,null);
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
        if(repartoTipo == 2) periodo = (int)periodo/100 * 100;
        String queryStr = String.format("" +
                "  SELECT B.codigo,B.nombre,B.nivel,A.porcentaje\n" +
                "    FROM MS_driver_lineas A\n" +
                "    JOIN MS_centros B ON A.entidad_destino_codigo=B.codigo\n" +
                "   WHERE A.periodo=%d AND A.driver_codigo='%s' AND A.reparto_tipo=%d\n"+
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
    
    public int actualizarListaDriverObjeto(List<DriverObjeto> lista, int periodo, int repartoTipo) {
        int resultado = 0;
        for (DriverObjeto driverObjeto: lista) {
            actualizarDriverObjeto(driverObjeto, periodo, repartoTipo);
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
    
    public int actualizarDriverObjeto(DriverObjeto driver, int periodo, int repartoTipo) {        
        int resultado = actualizarDriverCabecera(driver.getCodigo(),driver.getNombre(),driver.getDescripcion(),"OBCO");
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverObjetoLinea(driver.getCodigo(), periodo, driver.getListaDriverObjetoLinea(),repartoTipo);
        return resultado;
    }
    
    public int insertarDriverObjeto(DriverObjeto driver, int periodo, int repartoTipo) {
        int resultado = insertarDriverCabecera(driver.getCodigo(),driver.getNombre(),"OBCO",repartoTipo);
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverObjetoLineaBatch(driver.getCodigo(), periodo, driver.getListaDriverObjetoLinea(),repartoTipo);
        return resultado;
    }
    
    public int actualizarListaDriverCentro(List<DriverCentro> lista, int periodo, int repartoTipo) {
        int resultado = 0;
        for (DriverCentro driverCentro: lista) {
            actualizarDriverCentro(driverCentro, periodo, repartoTipo);
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

    public int actualizarDriverCentro(DriverCentro driver, int periodo, int repartoTipo) {        
        int resultado = actualizarDriverCabecera(driver.getCodigo(),driver.getNombre(),driver.getDescripcion(),"CECO");
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverLinea(driver.getCodigo(), periodo, driver.getListaDriverLinea(), repartoTipo);
        return 1;
    }

    public int insertarDriverCentro(DriverCentro driver, int periodo, int repartoTipo) {        
        int resultado = insertarDriverCabecera(driver.getCodigo(),driver.getNombre(),"CECO",repartoTipo);
        if (resultado == -1) return resultado;
        driverLineaDAO.insertarListaDriverLinea(driver.getCodigo(), periodo, driver.getListaDriverLinea(), repartoTipo);
        return resultado;
    }

    public int actualizarDriverCabecera(String codigo, String nombre, String descripcion, String driverTipoCodigo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "UPDATE MS_drivers\n" +
                "   SET nombre='%s',driver_tipo_codigo='%s',fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                " WHERE codigo='%s'",
                nombre,
                driverTipoCodigo,
                fechaStr,
                codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarDriverCabecera(String codigo, String nombre, String driverTipoCodigo, int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO MS_drivers(codigo,nombre,driver_tipo_codigo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s',q'[%s]','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,
                nombre,
                driverTipoCodigo,
                0,
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
                "SELECT codigo,\n" +
                "       nombre\n" +
                "  FROM MS_drivers\n" +
                " WHERE driver_tipo_codigo='CECO'\n" +
                " ORDER BY codigo";
        List<DriverCentro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                DriverCentro driver = new DriverCentro(codigo, nombre, null, null, null, null, null,true);
                lista.add(driver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<DriverObjeto> listarDriversObjetoMaestro() {
        String queryStr = "" +
                "SELECT codigo,\n" +
                "       nombre\n" +
                "  FROM MS_drivers\n" +
                " WHERE driver_tipo_codigo='OBCO'\n" +
                " ORDER BY codigo";
        List<DriverObjeto> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                DriverObjeto driver = new DriverObjeto(codigo, nombre, null, null, null, null, null,true);
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
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM MS_drivers A\n" +
                "  JOIN MS_driver_objeto_lineas C ON C.driver_codigo=A.codigo\n" +
                " WHERE C.periodo='%d' AND A.driver_tipo_codigo='OBCO' AND C.reparto_tipo='%d'\n" +
                " ORDER BY A.codigo",
                periodo,repartoTipo);
        List<DriverObjeto> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                
                //List<DriverLinea> listaDriverLinea = driverLineaDAO.obtenerListaDriverLinea(codigo, periodo);
                DriverObjeto driver = new DriverObjeto(codigo, nombre, null, null, null, fechaCreacion, fechaActualizacion);
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
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM MS_drivers A\n" +
                "  JOIN MS_driver_lineas C ON C.driver_codigo=A.codigo\n" +
                " WHERE C.periodo=%d AND A.driver_tipo_codigo='CECO' AND C.reparto_tipo=%d\n" +
                " ORDER BY A.codigo",
                periodo,repartoTipo);
        List<DriverCentro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                
                //List<DriverLinea> listaDriverLinea = driverLineaDAO.obtenerListaDriverLinea(codigo, periodo);
                DriverCentro driver = new DriverCentro(codigo, nombre, null, null, null, fechaCreacion, fechaActualizacion);
                lista.add(driver);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosDriverPeriodo(int periodo, int repartoTipo, String tipoDriver) {
        String queryStr = String.format("" +
                "SELECT DISTINCT A.DRIVER_CODIGO DRIVER_CODIGO\n" +
                "  FROM MS_DRIVER_LINEAS A\n" +
                "  JOIN MS_DRIVERS B ON B.CODIGO=A.DRIVER_CODIGO\n" +
                " WHERE A.PERIODO=%d AND B.DRIVER_TIPO_CODIGO='%s' AND A.REPARTO_TIPO=%d\n" +
                " ORDER BY A.DRIVER_CODIGO",
                periodo, tipoDriver, repartoTipo);
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                lista.add(rs.getString("DRIVER_CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosDriverObjetosPeriodo(int periodo, int repartoTipo, String tipoDriver) {
        String queryStr = String.format("" +
                "SELECT DISTINCT A.DRIVER_CODIGO DRIVER_CODIGO\n" +
                "  FROM MS_DRIVER_OBJETO_LINEAS A\n" +
                "  JOIN MS_DRIVERS B ON B.CODIGO=A.DRIVER_CODIGO\n" +
                " WHERE A.PERIODO=%d AND B.DRIVER_TIPO_CODIGO='%s' AND A.REPARTO_TIPO=%d\n" +
                " ORDER BY A.DRIVER_CODIGO",
                periodo, tipoDriver, repartoTipo);
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                lista.add(rs.getString("DRIVER_CODIGO"));
        } catch (SQLException ex) {
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

    public List<String> listarCodigos() {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery("SELECT codigo FROM MS_drivers")) {
            while(rs.next()) lista.add(rs.getString("codigo"));
        } catch (SQLException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public String obtenerCentroDriverConError(int periodo, int repartoTipo){
        String detail = "";
        String periodoStr = repartoTipo == 1 ? "b.PERIODO" : "TRUNC(b.PERIODO/100)*100";
        String queryStr = String.format("" +
                "SELECT  entidad_origen_codigo CENTRO_ORIGEN_CODIGO,\n" +
                "        driver_codigo driver_codigo,\n" +
                "        periodo PERIODO\n" +
                "  FROM (SELECT  a.entidad_origen_codigo,\n" +
                "                a.driver_codigo,\n" +
                "                a.periodo,\n" +
                "                MAX( \n" +
                "                  CASE\n" +
                "                    WHEN E.CODIGO IS NULL THEN 1\n" +
                "                    WHEN E.CODIGO IS NOT NULL THEN 0\n" +
                "                  END) estado \n" +
                "          FROM ms_entidad_origen_driver A\n" +
                "          JOIN ms_centro_lineas B on %s = a.periodo and b.reparto_tipo = a.reparto_tipo and a.entidad_origen_codigo = b.centro_codigo and b.iteracion = -2\n" +
                "          JOIN ms_centros C ON a.entidad_origen_codigo = C.CODIGO\n" +
                "        LEFT JOIN ms_driver_lineas D ON d.driver_codigo = a.driver_codigo and d.periodo = a.periodo and b.reparto_tipo = a.reparto_tipo\n" +
                "        LEFT JOIN ms_centros E ON d.entidad_destino_codigo = E.CODIGO AND E.NIVEL > C.NIVEL\n" +
                "        where a.periodo = %d and a.reparto_tipo = %d\n" +
                "        GROUP BY a.entidad_origen_codigo,a.driver_codigo,a.periodo)\n" +
                "WHERE ESTADO=1",
                periodoStr,periodo,repartoTipo);
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCentro = rs.getString("CENTRO_ORIGEN_CODIGO");
                String codigoDriver = rs.getString("driver_codigo");
                int periodoQ = rs.getInt("PERIODO");
                
                //List<DriverLinea> listaDriverLinea = driverLineaDAO.obtenerListaDriverLinea(codigo, periodo);
                CentroDriver cDriver = new CentroDriver(periodoQ, codigoCentro, null, codigoDriver, null);
                lista.add(cDriver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DriverDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(CentroDriver cDriver: lista){
            detail += String.format("* Codigo Centro: %s; Codigo Driver: %s; Periodo: %d.\r\n",cDriver.getCodigoCentro(),cDriver.getCodigoDriver(), periodo);
        }
        return detail;
    }
}
