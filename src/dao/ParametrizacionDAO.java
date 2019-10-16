package dao;

import controlador.ConexionBD;
import java.text.SimpleDateFormat;
import java.util.Date;
import modelo.ConnectionDB;

public class ParametrizacionDAO {
    ConnectionDB connection;
    
    public ParametrizacionDAO() {
        this.connection = new ConnectionDB();
    }
        
    public void copiarCuentasContables(int periodoOrigen, int periodoDestino, String fechaStr, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_plan_de_cuenta_lineas\n" +
                " WHERE periodo=%d AND reparto_tipo = %d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO MS_plan_de_cuenta_lineas(plan_de_cuenta_codigo,periodo,saldo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                    "SELECT plan_de_cuenta_codigo,%d periodo,0 saldo,%d reparto_tipo, sysdate, sysdate \n" +
                    "  FROM MS_plan_de_cuenta_lineas\n" +
                    " WHERE periodo=%d and reparto_tipo=%d",
                    periodoDestino,
                    repartoTipo,
                    periodoOrigen,
                    repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    public void copiarPartidas(int periodoOrigen, int periodoDestino, String fechaStr, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_partida_lineas\n" +
                " WHERE periodo=%d AND reparto_tipo=%d",
                periodoDestino, repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO MS_partida_lineas(partida_codigo,periodo,saldo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                    "SELECT partida_codigo,%d periodo,0 saldo,%d reparto_tipo,sysdate,sysdate\n" +
                    "  FROM MS_partida_lineas\n" +
                    " WHERE periodo=%d AND reparto_tipo=%d",
                    periodoDestino,
                    repartoTipo,
                    periodoOrigen,
                    repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarCuentaPartida(int periodoOrigen, int periodoDestino, String fechaStr, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_partida_cuenta_contable\n" +
                " WHERE periodo=%d AND reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO MS_partida_cuenta_contable(partida_codigo,cuenta_contable_codigo,periodo,saldo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                    "SELECT partida_codigo,cuenta_contable_codigo,%d periodo,0 saldo,%d reparto_tipo,sysdate,sysdate\n" +
                    "  FROM MS_partida_cuenta_contable\n" +
                    " WHERE periodo=%d AND reparto_tipo=%d",
                    periodoDestino,
                    repartoTipo,
                    periodoOrigen,
                    repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarCentroLineas(int periodoOrigen, int periodoDestino, String fechaStr) {
        String queryStr = String.format("" +
                "DELETE FROM centro_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino);
        ConexionBD.ejecutar(queryStr);

        queryStr = String.format("" +
                "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,grupo_gasto,fecha_creacion,fecha_actualizacion,entidad_origen_codigo)\n" +
                "SELECT centro_codigo,%d periodo,-2 iteracion,0 saldo,'-' grupo_gasto,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion,0 entidad_origen_codigo\n" +
                "  FROM centro_lineas\n" +
                " WHERE periodo=%d AND iteracion=-1",
                periodoDestino,
                fechaStr,
                fechaStr,
                periodoOrigen);
        ConexionBD.ejecutar(queryStr);
    }

    public void copiarOficinas(int periodoOrigen, int periodoDestino, String fechaStr) {
        String queryStr = String.format("" +
                "DELETE FROM oficina_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO oficina_lineas(oficina_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT oficina_codigo,%d periodo,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion\n" +
                "  FROM oficina_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino,fechaStr,fechaStr,
                periodoOrigen);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarBancas(int periodoOrigen, int periodoDestino, String fechaStr) {
        String queryStr = String.format("" +
                "DELETE FROM BANCA_LINEAS\n" +
                " WHERE PERIODO=%d",
                periodoDestino);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO banca_lineas(banca_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                    "SELECT banca_codigo,%d periodo,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion\n" +
                    "  FROM banca_lineas\n" +
                    " WHERE periodo=%d",
                    periodoDestino,
                    fechaStr,
                    fechaStr,
                    periodoOrigen);
        ConexionBD.ejecutar(queryStr);
    }
        
    public void copiarProductos(int periodoOrigen, int periodoDestino, String fechaStr) {
        String queryStr = String.format("" +
                "DELETE FROM producto_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO producto_lineas(producto_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                    "SELECT producto_codigo,%d periodo,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion\n" +
                    "  FROM producto_lineas\n" +
                    " WHERE periodo=%d",
                    periodoDestino,
                    fechaStr,
                    fechaStr,
                    periodoOrigen);
        ConexionBD.ejecutar(queryStr);
    }

    public void copiarDriverCentroLineas(int periodoOrigen, int periodoDestino, String fechaStr) {
        String queryStr = String.format("" +
                "DELETE FROM driver_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO driver_lineas(driver_codigo,entidad_destino_codigo,periodo,porcentaje,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT driver_codigo,entidad_destino_codigo,%d periodo,porcentaje,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion\n" +
                "  FROM driver_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino,
                fechaStr,
                fechaStr,
                periodoOrigen);
        ConexionBD.ejecutar(queryStr);
    }

    public void copiarDriverObjetoLineas(int periodoOrigen, int periodoDestino, String fechaStr) {
        String queryStr = String.format("" +
                "DELETE FROM driver_obco_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO driver_obco_lineas(driver_codigo,oficina_codigo,banca_codigo,producto_codigo,periodo,porcentaje,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT driver_codigo,oficina_codigo,banca_codigo,producto_codigo,%d periodo,porcentaje,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion\n" +
                "  FROM driver_obco_lineas\n" +
                " WHERE periodo=%d",
                periodoDestino,
                fechaStr,
                fechaStr,
                periodoOrigen);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarDriverAsignacionEntidades(int periodoOrigen, int periodoDestino, String fechaStr) {
        String queryStr = String.format("" +
                "DELETE FROM entidad_origen_driver\n" +
                " WHERE periodo=%d",
                periodoDestino);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO entidad_origen_driver(entidad_origen_codigo,driver_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT entidad_origen_codigo,driver_codigo,%d periodo,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion\n" +
                "  FROM entidad_origen_driver\n" +
                " WHERE periodo=%d",
                periodoDestino,
                fechaStr,
                fechaStr,
                periodoOrigen);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarParametrizacion(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        // copiar las Cuentas Contables
        copiarCuentasContables(periodoOrigen, periodoDestino, fechaStr, repartoTipo);
        // copiar las Partidas
        copiarPartidas(periodoOrigen, periodoDestino, fechaStr, repartoTipo);
        // copiar las Asignaciones de Cuenta - Partida
        copiarCuentaPartida(periodoOrigen, periodoDestino, fechaStr, repartoTipo);
//        // copiar los centros de costos
//        copiarCentroLineas(periodoOrigen, periodoDestino, fechaStr);
//        // copiar las oficinas
//        copiarOficinas(periodoOrigen, periodoDestino, fechaStr);
//        // copiar las bancas
//        copiarBancas(periodoOrigen, periodoDestino, fechaStr);
//        // copiar lor productos
//        copiarProductos(periodoOrigen, periodoDestino, fechaStr);
//        // copiar los drivers centro lineas
//        copiarDriverCentroLineas(periodoOrigen, periodoDestino, fechaStr);
//        // copiar los drivers centro lineas
//        copiarDriverObjetoLineas(periodoOrigen, periodoDestino, fechaStr);
//        // copiar los asginacion drivers x entidades
//        copiarDriverAsignacionEntidades(periodoOrigen, periodoDestino, fechaStr);
    }
}
