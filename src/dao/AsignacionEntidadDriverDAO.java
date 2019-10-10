package dao;

import controlador.ConexionBD;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import modelo.CargarEntidadDriver;
import modelo.CentroDriver;
import modelo.Driver;
import modelo.EntidadDistribucion;

public class AsignacionEntidadDriverDAO {
    public AsignacionEntidadDriverDAO() {
    }
    
    public int borrarAsignacion(String entidadCodigo, int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_ENTIDAD_ORIGEN_DRIVER\n" +
                " WHERE ENTIDAD_ORIGEN_CODIGO='%s'\n" +
                "   AND PERIODO=%d\n" +
                "   AND REPARTO_TIPO=%d",
                entidadCodigo, periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int asignar(String entidadCodigo, String driverCodigo, int periodo, int repartoTipo) {        
        borrarAsignacion(entidadCodigo, periodo, repartoTipo);
        
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO MS_ENTIDAD_ORIGEN_DRIVER(ENTIDAD_ORIGEN_CODIGO,DRIVER_CODIGO,PERIODO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                entidadCodigo,
                driverCodigo,
                periodo,
                repartoTipo,
                fechaStr,
                fechaStr);        
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarAsignacionesPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_ENTIDAD_ORIGEN_DRIVER A\n" +
                " WHERE A.PERIODO=%d\n" +
                "   AND A.REPARTO_TIPO=%d",
                periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarAsignacionesPeriodo(List<EntidadDistribucion> lstEntidades, int periodo, int repartoTipo) {
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        for (EntidadDistribucion item: lstEntidades) {
            String queryStr = String.format("" +
                "DELETE FROM MS_ENTIDAD_ORIGEN_DRIVER\n" +
                " WHERE ENTIDAD_ORIGEN_CODIGO='%s'\n" +
                "   AND PERIODO=%d\n" +
                "   AND REPARTO_TIPO=%d",
                item.getCodigo(), periodo, repartoTipo);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        return 1;
    }
    
    public void insertarListaEntidades(List<EntidadDistribucion> lstEntidades, int periodo, int repartoTipo) {
        //borrarAsignacionesPeriodo(periodo);
        borrarAsignacionesPeriodo(lstEntidades, periodo, repartoTipo);
        
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (EntidadDistribucion item: lstEntidades) {
            if (!item.getDriver().getCodigo().equals("Sin driver asignado")) {
                String queryStr = String.format(Locale.US, "" +
                        "INSERT INTO entidad_origen_driver(entidad_origen_codigo,driver_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                        "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                        item.getCodigo(),
                        item.getDriver().getCodigo(),
                        periodo,
                        fechaStr,
                        fechaStr);
                ConexionBD.agregarBatch(queryStr);
            }
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public void insertarListaAsignaciones(List<CentroDriver> lstEntidadDriver, int periodo, int repartoTipo) {
        borrarAsignacionesPeriodo(periodo, repartoTipo);
        
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (CentroDriver item: lstEntidadDriver) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO entidad_origen_driver(entidad_origen_codigo,driver_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    item.getCodigoCentro(),
                    item.getCodigoDriver(),
                    item.getPeriodo(),
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
}
