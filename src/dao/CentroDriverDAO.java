/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controlador.ConexionBD;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import modelo.CentroDriver;
import modelo.CargarEntidadDriver;

public class CentroDriverDAO {
    public CentroDriverDAO() {
    }
    
    public int borrarAsignacionBolsa(String cuentaCodigo, String partidaCodigo, String centroCodigo, int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM bolsa_driver \n" +
                " WHERE cuenta_contable_codigo='%s' AND partida_codigo='%s' AND centro_codigo='%s' AND periodo=%d AND reparto_tipo=%d",
                cuentaCodigo, partidaCodigo, centroCodigo, periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarAsignacionDriverObjeto(String centroCodigo, String grupoGasto, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM objeto_driver \n" +
                " WHERE centro_codigo='%s' AND grupo_gasto='%s' AND periodo=%d",
                centroCodigo, grupoGasto,
                periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int asignarDriverBolsa(String cuentaCodigo, String partidaCodigo, String centroCodigo, String driverCodigo, int periodo, int repartoTipo) {
        borrarAsignacionBolsa(cuentaCodigo, partidaCodigo, centroCodigo, periodo, repartoTipo);
        
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO MS_BOLSA_DRIVER(DRIVER_CODIGO,CUENTA_CONTABLE_CODIGO,PARTIDA_CODIGO,CENTRO_CODIGO,PERIODO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                "VALUES ('%s','%s','%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                driverCodigo,
                cuentaCodigo,
                partidaCodigo,
                centroCodigo,
                periodo,
                repartoTipo,
                fechaStr,
                fechaStr);        
        return ConexionBD.ejecutar(queryStr);
    }
    public int asignarDriverObjeto(String centroCodigo, String grupoGasto, String driverCodigo, int periodo, int repartoTipo) {        
        borrarAsignacionDriverObjeto(centroCodigo, grupoGasto, periodo);
        
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO MS_OBJETO_DRIVER(DRIVER_CODIGO,CENTRO_CODIGO,GRUPO_GASTO,PERIODO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                "VALUES ('%s','%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                driverCodigo,
                centroCodigo,
                grupoGasto,
                periodo,
                repartoTipo,
                fechaStr,
                fechaStr);        
        return ConexionBD.ejecutar(queryStr);
    }
    public int borrarAsignacionesBolsaPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_BOLSA_DRIVER A\n" +
                " WHERE PERIODO=%d AND REPARTO_TIPO=%d",
                periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarAsignacionesObjetoPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_OBJETO_DRIVER A\n" +
                " WHERE PERIODO=%d AND REPARTO_TIPO=%d",
                periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    /*public int borrarAsignacionesBolsaPeriodo(List<CentroDriver> lstEntidades, int periodo) {
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        for (CentroDriver item: lstEntidades) {
            String queryStr = String.format("" +
                "DELETE FROM bolsa_driver\n" +
                " WHERE cuenta_contable_codigo='%s' AND partida_codigo='%s' AND centro_codigo='%s' AND periodo=%d",
                item.getCodigoCuenta(),item.getCodigoPartida(),item.getCodigoCentro(),periodo);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        return 1;
    }*/
    
    /*public void insertarListaDriverBolsas(List<CentroDriver> lstCentros, int periodo) {
        //borrarAsignacionesPeriodo(periodo);
        borrarAsignacionesBolsaPeriodo(lstCentros, periodo);
        
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (CentroDriver item: lstCentros) {
            if (!item.getCodigoDriver().equals("Sin driver asignado")) {
                String queryStr = String.format(Locale.US, "" +
                        "INSERT INTO MS_BOLSA_DRIVER(DRIVER_CODIGO,CUENTA_CONTABLE_CODIGO,PARTIDA_CODIGO,CENTRO_CODIGO,PERIODO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                        "VALUES ('%s','%s','%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                        item.getCodigoCuenta(),
                        item.getCodigoPartida(),
                        item.getCodigoCentro(),
                        item.getCodigoDriver(),
                        periodo,
                        fechaStr,
                        fechaStr);
                ConexionBD.agregarBatch(queryStr);
            }
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }*/
    
    public void insertarListaAsignacionesDriverBolsa(List<CentroDriver> lstEntidadDriver, int periodo, int repartoTipo) {
        borrarAsignacionesBolsaPeriodo(periodo, repartoTipo);
        
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (CentroDriver item: lstEntidadDriver) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO MS_BOLSA_DRIVER(DRIVER_CODIGO,CUENTA_CONTABLE_CODIGO,PARTIDA_CODIGO,CENTRO_CODIGO,PERIODO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s','%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    item.getCodigoDriver(),
                    item.getCodigoCuenta(),
                    item.getCodigoPartida(),
                    item.getCodigoCentro(),
                    item.getPeriodo(),
                    repartoTipo,
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public void insertarListaAsignacionesDriverObjeto(List<CentroDriver> lstEntidadDriver, int periodo, int repartoTipo) {
        borrarAsignacionesObjetoPeriodo(periodo, repartoTipo);
        
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (CentroDriver item: lstEntidadDriver) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO MS_OBJETO_DRIVER(CENTRO_CODIGO,GRUPO_GASTO,DRIVER_CODIGO,PERIODO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    item.getCodigoCentro(),
                    item.getGrupoGasto().getCodigo(),
                    item.getCodigoDriver(),
                    periodo,
                    repartoTipo,
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
}
