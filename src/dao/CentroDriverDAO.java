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
    
    public int borrarAsignacionBolsa(String cuentaCodigo, String partidaCodigo, String centroCodigo, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM bolsa_driver \n" +
                " WHERE cuenta_contable_codigo='%s' AND partida_codigo='%s' AND centro_codigo='%s' AND periodo=%d",
                cuentaCodigo, partidaCodigo, centroCodigo,
                periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int asignarDriverBolsa(String cuentaCodigo, String partidaCodigo, String centroCodigo, String driverCodigo, int periodo) {        
        borrarAsignacionBolsa(cuentaCodigo, partidaCodigo, centroCodigo, periodo);
        
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO bolsa_driver(driver_codigo, cuenta_contable_codigo, partida_codigo, centro_codigo, periodo, fecha_creacion, fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                driverCodigo,
                cuentaCodigo, partidaCodigo, centroCodigo,
                periodo,
                fechaStr,
                fechaStr);        
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarAsignacionesBolsaPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM bolsa_driver A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM centros B\n" +
                "                WHERE A.centro_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND B.reparto_tipo=%d)\n" +
                "    AND EXISTS (SELECT 1\n" +
                "                 FROM partidas C\n" +
                "                WHERE A.partida_codigo=C.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND C.reparto_tipo=%d)\n"+
                "    AND EXISTS (SELECT 1\n" +
                "                 FROM plan_de_cuentas C\n" +
                "                WHERE A.cuenta_contable_codigo=C.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND C.reparto_tipo=%d)\n",
                periodo,repartoTipo,periodo,repartoTipo,periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarAsignacionesBolsaPeriodo(List<CentroDriver> lstEntidades, int periodo) {
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
    }
    
    public void insertarListaDriverBolsas(List<CentroDriver> lstCentros, int periodo) {
        //borrarAsignacionesPeriodo(periodo);
        borrarAsignacionesBolsaPeriodo(lstCentros, periodo);
        
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (CentroDriver item: lstCentros) {
            if (!item.getCodigoDriver().equals("Sin driver asignado")) {
                String queryStr = String.format(Locale.US, "" +
                        "INSERT INTO bolsa_driver(driver_codigo, cuenta_contable_codigo, partida_codigo, centro_codigo, periodo,fecha_creacion,fecha_actualizacion)\n" +
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
    }
    
    public void insertarListaAsignacionesDriverBolsa(List<CentroDriver> lstEntidadDriver, int periodo, int repartoTipo) {
        CentroDriverDAO.this.borrarAsignacionesBolsaPeriodo(periodo, repartoTipo);
        
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (CentroDriver item: lstEntidadDriver) {
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO bolsa_driver(driver_codigo, cuenta_contable_codigo, partida_codigo, centro_codigo, periodo, fecha_creacion, fecha_actualizacion)\n" +
                    "VALUES ('%s','%s','%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    item.getCodigoDriver(),
                    item.getCodigoCuenta(),
                    item.getCodigoPartida(),
                    item.getCodigoCentro(),
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
