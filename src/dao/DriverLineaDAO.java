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
import modelo.Centro;
import modelo.DriverLinea;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import modelo.Oficina;
import modelo.Producto;

public class DriverLineaDAO {
    PlanDeCuentaDAO planDeCuentaDAO;
    CentroDAO centroDAO;
    ProductoDAO productoDAO;
    OficinaDAO oficinaDAO;
    BancaDAO bancaDAO;

    public DriverLineaDAO() {
        planDeCuentaDAO = new PlanDeCuentaDAO();
        centroDAO = new CentroDAO();
        bancaDAO = new BancaDAO();
        oficinaDAO = new OficinaDAO();
        productoDAO = new ProductoDAO();
    }
    
    public void borrarListaDriverObjetoLinea(String driverCodigo, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM driver_obco_lineas\n" +
                " WHERE driver_codigo='%s' AND periodo=%d",
                driverCodigo,
                periodo);
        ConexionBD.agregarBatch(queryStr);
    }
    
    public void borrarListaDriverLinea(String driverCodigo, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM driver_lineas\n" +
                " WHERE driver_codigo='%s' AND periodo=%d",
                driverCodigo,
                periodo);
        ConexionBD.agregarBatch(queryStr);
    }

    public void insertarListaDriverCentroLineaBatch(String driverCodigo, int periodo, List<DriverLinea> listaDriverLinea) {
        borrarListaDriverLinea(driverCodigo, periodo);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (DriverLinea item: listaDriverLinea) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO driver_lineas(driver_codigo,entidad_destino_codigo,periodo,porcentaje,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s',%d,%.4f,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    driverCodigo,
                    item.getEntidadDistribucionDestino().getCodigo(),
                    periodo,
                    item.getPorcentaje(),
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
    }

    public void insertarListaDriverObjetoLinea(String driverCodigo, int periodo, List<DriverObjetoLinea> listaDriverObjetoLinea) {
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 10000;
        borrarListaDriverObjetoLinea(driverCodigo, periodo);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (DriverObjetoLinea item: listaDriverObjetoLinea) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO driver_obco_lineas(driver_codigo,banca_codigo,oficina_codigo,producto_codigo,periodo,porcentaje,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s','%s','%s',%d,%.4f,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    driverCodigo,
                    item.getBanca().getCodigo(),
                    item.getOficina().getCodigo(),
                    item.getProducto().getCodigo(),
                    periodo,
                    item.getPorcentaje(),
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public void insertarListaDriverObjetoLineaBatch(String driverCodigo, int periodo, List<DriverObjetoLinea> listaDriverObjetoLinea) {
        borrarListaDriverObjetoLinea(driverCodigo, periodo);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (DriverObjetoLinea item: listaDriverObjetoLinea) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO driver_obco_lineas(driver_codigo,banca_codigo,oficina_codigo,producto_codigo,periodo,porcentaje,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s','%s','%s',%d,%.4f,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    driverCodigo,
                    item.getBanca().getCodigo(),
                    item.getOficina().getCodigo(),
                    item.getProducto().getCodigo(),
                    periodo,
                    item.getPorcentaje(),
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
    }
    
    public void insertarListaDriverLinea(String driverCodigo, int periodo, List<DriverLinea> listaDriverLinea) {
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 10000;
        borrarListaDriverLinea(driverCodigo, periodo);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (DriverLinea driverLinea : listaDriverLinea) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO driver_lineas(driver_codigo,entidad_destino_codigo,periodo,porcentaje,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s',%d,%.4f,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    driverCodigo,
                    driverLinea.getEntidadDistribucionDestino().getCodigo(),
                    periodo,
                    driverLinea.getPorcentaje(),
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }

    public List<DriverObjetoLinea> obtenerListaDriverObjetoLinea(String driverCodigo, int periodo) {
        // Cargo los objetos de costos: bancas, oficinas, productos
        List<Banca> listaBancas = bancaDAO.listar(periodo);
        List<Oficina> listaOficinas = oficinaDAO.listar(periodo);
        List<Producto> listaProductos = productoDAO.listar(periodo);

        String queryStr = String.format("" +
                "SELECT A.banca_codigo,\n" +
                "       A.oficina_codigo,\n" +
                "       A.producto_codigo,\n" +
                "       A.porcentaje,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM driver_obco_lineas A\n" +
                "  JOIN drivers B ON B.codigo=A.driver_codigo\n" +
                " WHERE A.driver_codigo='%s'\n" +
                "   AND A.periodo=%d",
                driverCodigo, periodo);
        List<DriverObjetoLinea> listaDriverObjetoLinea = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while (rs.next()) {
                String bancaCodigo = rs.getString("banca_codigo");
                Banca banca = listaBancas.stream().filter(item -> bancaCodigo.equals(item.getCodigo())).findAny().orElse(null);
                String oficinaCodigo = rs.getString("oficina_codigo");
                Oficina oficina = listaOficinas.stream().filter(item -> oficinaCodigo.equals(item.getCodigo())).findAny().orElse(null);
                String productoCodigo = rs.getString("producto_codigo");
                Producto producto = listaProductos.stream().filter(item -> productoCodigo.equals(item.getCodigo())).findAny().orElse(null);
                double porcentaje = rs.getDouble("porcentaje");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));

                DriverObjetoLinea driverObjetoLinea = new DriverObjetoLinea(banca, oficina, producto, porcentaje, fechaCreacion, fechaActualizacion);
                listaDriverObjetoLinea.add(driverObjetoLinea);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(DriverLineaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDriverObjetoLinea;
    }
    
    public List<DriverLinea> obtenerListaDriverLinea(String driverCodigo, int periodo, int repartoTipo) {
        // Cargo las entidades: centros de costos
        List<Centro> listaCentros = centroDAO.listar(periodo, repartoTipo);
        List<EntidadDistribucion> listaEntidades = new ArrayList();
        listaEntidades.addAll(listaCentros);
        
        String queryStr = String.format("" +
                "SELECT A.entidad_destino_codigo,\n" +
                "       A.porcentaje,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM driver_lineas A\n" +
                "  JOIN drivers B ON B.codigo=A.driver_codigo\n" +
                " WHERE A.driver_codigo='%s'\n" +
                "   AND A.periodo=%d",
                driverCodigo, periodo);
        List<DriverLinea> listaDriverLinea = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while (rs.next()) {
                String entidad_destino_codigo = rs.getString("entidad_destino_codigo");
                double porcentaje = rs.getDouble("porcentaje");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));

                EntidadDistribucion entidad = listaEntidades.stream().filter(item -> entidad_destino_codigo.equals(item.getCodigo())).findAny().orElse(null);
                
                DriverLinea driverLinea = new DriverLinea(entidad, porcentaje, fechaCreacion, fechaActualizacion);
                listaDriverLinea.add(driverLinea);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(DriverLineaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaDriverLinea;
    }
}
