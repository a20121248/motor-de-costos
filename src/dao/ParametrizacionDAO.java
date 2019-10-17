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
        
    public void copiarCuentasContables(int periodoOrigen, int periodoDestino, int repartoTipo) {
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
    public void copiarPartidas(int periodoOrigen, int periodoDestino, int repartoTipo) {
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
    
    public void copiarCuentaPartida(int periodoOrigen, int periodoDestino, int repartoTipo) {
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
    
    public void copiarCentro(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_centro_lineas\n" +
                " WHERE periodo=%d and periodo =%d",
                periodoDestino, repartoTipo);
        ConexionBD.ejecutar(queryStr);

        queryStr = String.format("" +
                "INSERT INTO MS_centro_lineas\n" +
                "(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,grupo_gasto,reparto_tipo,cuenta_contable_origen_codigo,partida_origen_codigo,centro_origen_codigo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT centro_codigo,%d periodo,-2 iteracion,0 saldo,entidad_origen_codigo, grupo_gasto, reparto_tipo, cuenta_contable_origen_codigo, partida_origen_codigo, centro_origen_codigo,sysdate,sysdate\n" +
                "  FROM MS_centro_lineas\n" +
                " WHERE periodo=%d AND reparto_tipo=%d AND iteracion=-2",
                periodoDestino,
                periodoOrigen,
                repartoTipo);
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
        
    public void copiarProductos(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_producto_lineas\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO MS_producto_lineas(producto_codigo,periodo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                    "SELECT producto_codigo,%d periodo,reparto_tipo,sysdate,sysdate\n" +
                    "  FROM MS_producto_lineas\n" +
                    " WHERE periodo=%d and reparto_tipo=%d",
                    periodoDestino,
                    periodoOrigen,
                    repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO MS_JERARQUIA(periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo,reparto_tipo)\n" +
                    "SELECT %d periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo,reparto_tipo\n" +
                    "  FROM MS_JERARQUIA\n" +
                    " WHERE periodo=%d and reparto_tipo=%d and entidad_tipo='PRO'",
                    periodoDestino,
                    periodoOrigen,
                    repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarSubcanales(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_subcanal_lineas\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO MS_subcanal_lineas(subcanal_codigo,periodo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                    "SELECT subcanal_codigo,%d periodo,reparto_tipo,sysdate,sysdate\n" +
                    "  FROM MS_subcanal_lineas\n" +
                    " WHERE periodo=%d and reparto_tipo=%d",
                    periodoDestino,
                    periodoOrigen,
                    repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                    "INSERT INTO MS_JERARQUIA(periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo,reparto_tipo)\n" +
                    "SELECT %d periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo,reparto_tipo\n" +
                    "  FROM MS_JERARQUIA\n" +
                    " WHERE periodo=%d and reparto_tipo=%d and entidad_tipo='SCA'",
                    periodoDestino,
                    periodoOrigen,
                    repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }

    public void copiarDriverCentroLineas(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_driver_lineas\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO MS_driver_lineas(driver_codigo,entidad_destino_codigo,periodo,porcentaje,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT driver_codigo,entidad_destino_codigo,%d periodo,porcentaje,reparto_tipo,sysdate,sysdate\n" +
                "  FROM MS_driver_lineas\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,
                periodoOrigen,
                repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }

    public void copiarDriverObjetoLineas(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM ms_driver_objeto_lineas\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO ms_driver_objeto_lineas"+
                "(driver_codigo,producto_codigo,subcanal_codigo,periodo,porcentaje,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT driver_codigo,producto_codigo,subcanal_codigo,%d periodo,porcentaje,reparto_tipo,sysdate,sysdate\n" +
                "  FROM ms_driver_objeto_lineas\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,
                periodoOrigen,
                repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarDriverAsignacionCentroStaff(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_entidad_origen_driver\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO MS_entidad_origen_driver(entidad_origen_codigo,driver_codigo,periodo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT entidad_origen_codigo,driver_codigo,%d periodo,reparto_tipo,sysdate,sysdate\n" +
                "  FROM MS_entidad_origen_driver\n" +
                " WHERE periodo=%d AND reparto_tipo =%d",
                periodoDestino,
                periodoOrigen,
                repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarDriverAsignacionCentroBolsa(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_bolsa_driver\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO MS_bolsa_driver(driver_codigo,cuenta_contable_codigo,partida_codigo,centro_codigo,periodo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT driver_codigo,cuenta_contable_codigo,partida_codigo,centro_codigo,%d periodo,reparto_tipo,sysdate,sysdate\n" +
                "  FROM MS_bolsa_driver\n" +
                " WHERE periodo=%d AND reparto_tipo =%d",
                periodoDestino,
                periodoOrigen,
                repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarDriverAsignacionCentroObjeto(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_objeto_driver\n" +
                " WHERE periodo=%d and reparto_tipo=%d",
                periodoDestino,repartoTipo);
        ConexionBD.ejecutar(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO MS_objeto_driver(centro_codigo,grupo_gasto,driver_codigo,periodo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "SELECT centro_codigo,grupo_gasto,driver_codigo,%d periodo,reparto_tipo,sysdate,sysdate\n" +
                "  FROM MS_objeto_driver\n" +
                " WHERE periodo=%d AND reparto_tipo =%d",
                periodoDestino,
                periodoOrigen,
                repartoTipo);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void copiarParametrizacion(int periodoOrigen, int periodoDestino, int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        // copiar las Cuentas Contables
        copiarCuentasContables(periodoOrigen, periodoDestino, repartoTipo);
        // copiar las Partidas
        copiarPartidas(periodoOrigen, periodoDestino, repartoTipo);
        // copiar las Asignaciones de Cuenta - Partida
        copiarCuentaPartida(periodoOrigen, periodoDestino, repartoTipo);
        // copiar los centros de costos
        copiarCentro(periodoOrigen, periodoDestino, repartoTipo);
        // copiar lor productos
        copiarProductos(periodoOrigen, periodoDestino, repartoTipo);
        // copiar lor subcanales
        copiarSubcanales(periodoOrigen, periodoDestino, repartoTipo);
        // copiar los drivers centro lineas
        copiarDriverCentroLineas(periodoOrigen, periodoDestino, repartoTipo);
        // copiar los drivers centro lineas
        copiarDriverObjetoLineas(periodoOrigen, periodoDestino, repartoTipo);
        // copiar los asginacion drivers x centros staff
        copiarDriverAsignacionCentroStaff(periodoOrigen, periodoDestino, repartoTipo);
        // copiar los asginacion drivers x centros Bolsa
        copiarDriverAsignacionCentroBolsa(periodoOrigen, periodoDestino, repartoTipo);
        // copiar los asginacion drivers x centros objeto
        copiarDriverAsignacionCentroObjeto(periodoOrigen, periodoDestino, repartoTipo);
    }
}
