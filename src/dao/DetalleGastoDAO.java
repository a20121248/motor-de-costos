/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.DetalleGasto;
import modelo.ConnectionDB;

/**
 *
 * @author briggette.olenka.ro1
 */
public class DetalleGastoDAO {
    
    ConnectionDB connection;

    public DetalleGastoDAO() {
        connection = new ConnectionDB();
    }
    public List<DetalleGasto> listar(int periodo, String tipoGasto, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.cuenta_contable_codigo cuenta_contable_codigo,\n" +
                "       B.nombre cuenta_contable_nombre,\n" +
                "       A.partida_codigo partida_codigo,\n" +
                "       C.nombre partida_nombre,\n" +
                "       A.centro_codigo centro_codigo,\n" +
                "       D.nombre centro_nombre,\n" +
                "       SUM(COALESCE(A.saldo,0)) saldo\n" +
                "  FROM cuenta_partida_centro A\n" +
                "  JOIN plan_de_cuentas B ON B.codigo=A.cuenta_contable_codigo\n" +
                "  JOIN partidas C ON C.codigo=A.partida_codigo\n" +
                "  JOIN centros D ON D.codigo=A.centro_codigo\n" +
                " WHERE A.periodo=%d\n",
                periodo,repartoTipo);
        switch(tipoGasto) {
            case "Administrativo":
                queryStr += "\n   AND SUBSTR(B.codigo,0,2)='45'";
                break;
            case "Operativo":
                queryStr += "\n   AND SUBSTR(B.codigo,0,2)='44'";
        }
        queryStr += "\n GROUP BY A.cuenta_contable_codigo,B.nombre, A.partida_codigo, C.nombre, A.centro_codigo, D.nombre\n" +
                    "\n ORDER BY A.cuenta_contable_codigo,A.partida_codigo,A.centro_codigo";
        List<DetalleGasto> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCuentaContable = rs.getString("cuenta_contable_codigo");
                String nombreCuentaContable = rs.getString("cuenta_contable_nombre");
                String codigoPartida = rs.getString("partida_codigo");
                String nombrePartida = rs.getString("partida_nombre");
                String codigoCECO = rs.getString("centro_codigo");
                String nombreCECO = rs.getString("centro_nombre");
                double saldo = rs.getDouble("saldo");
                DetalleGasto item = new DetalleGasto(periodo, codigoCuentaContable, nombreCuentaContable,codigoPartida, nombrePartida,codigoCECO, nombreCECO,saldo, true);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosCuenta_CuentaPartida(int periodo) {
        String queryStr = String.format(""
                + "SELECT   distinct(cuenta_contable_codigo)"
                + "  FROM   PARTIDA_CUENTA_CONTABLE"
                + " WHERE   periodo = '%d'  ",periodo);
        List<String> lista = new ArrayList();
        
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCuentaContable = rs.getString("cuenta_contable_codigo");
                lista.add(codigoCuentaContable);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosPartidas_CuentaPartida(String codigoCuentaContable,int periodo) {
        String queryStr = String.format(""
                + "SELECT   partida_codigo"
                + "  FROM   PARTIDA_CUENTA_CONTABLE"
                + " WHERE   periodo = '%d' AND cuenta_contable_codigo = '%s'",periodo, codigoCuentaContable);
        List<String> lista = new ArrayList();
        
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoPartida = rs.getString("partida_codigo");
                lista.add(codigoPartida);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    public int borrarListaDetalleGastoPeriodo(int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM cuenta_partida_centro A\n" +
                " WHERE periodo = '%d'",
                periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarDetalleGasto(int periodo, List<DetalleGasto> lista) throws SQLException {
        limpiarSaldosAsociadosPeriodo(periodo);
        borrarListaDetalleGastoPeriodo(periodo);
        ConexionBD.crearStatement();
        for (DetalleGasto item: lista) {
            String codigoCuentaContable = item.getCodigoCuentaContable();
            String codigoPartida = item.getCodigoPartida();
            String codigoCentro = item.getCodigoCECO();
            double saldo = item.getSaldo();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            String queryStr = String.format(Locale.US, "" +
                    "INSERT INTO cuenta_partida_centro(cuenta_contable_codigo, partida_codigo, centro_codigo, periodo, saldo, fecha_creacion, fecha_actualizacion)\n" +
                    "VALUES ('%s','%s','%s','%d',%.2f,TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'))",
                    codigoCuentaContable,
                    codigoPartida,
                    codigoCentro,
                    periodo,
                    saldo,
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        actualizarSaldoCentroPeriodo(periodo);
        actualizarSaldoPartidaPeriodo(periodo);
        actualizarSaldoCuentaPeriodo(periodo);
        actualizarSaldoCuentaPartidaPeriodo(periodo);
    }

    public int actualizarSaldoCuentaPartidaPeriodo( int periodo){
        String queryStr = String.format(
                "UPDATE PARTIDA_CUENTA_CONTABLE A"+
                "   SET SALDO = (SELECT sum(coalesce(B.saldo,0)) FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND A.cuenta_contable_codigo = B.cuenta_contable_codigo AND PERIODO = '%d' group by B.cuenta_contable_codigo, B.partida_codigo)"+
                " WHERE EXISTS (SELECT 1 FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND A.cuenta_contable_codigo = B.cuenta_contable_codigo AND B.PERIODO = '%d') AND PERIODO = '%d'"
                , periodo,periodo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int actualizarSaldoCentroPeriodo( int periodo){
        String queryStr = String.format(
                "UPDATE CENTRO_LINEAS A"+
                "   SET SALDO = (SELECT sum(coalesce(B.saldo,0)) FROM cuenta_partida_centro B WHERE A.CENTRO_CODIGO = B.CENTRO_CODIGO  AND PERIODO = '%d' group by  B.CENTRO_CODIGO)"+
                " WHERE EXISTS (SELECT 1 FROM CENTRO_LINEAS B WHERE A.CENTRO_CODIGO = B.CENTRO_CODIGO AND B.PERIODO = '%d') AND PERIODO = '%d'"
                , periodo,periodo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int actualizarSaldoPartidaPeriodo( int periodo){
        String queryStr = String.format(
                "UPDATE PARTIDA_LINEAS A"+
                "   SET SALDO = (SELECT sum(coalesce(B.saldo,0)) FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO  AND PERIODO = '%d' group by  B.partida_codigo)"+
                " WHERE EXISTS (SELECT 1 FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND B.PERIODO = '%d') AND PERIODO = '%d'"
                , periodo,periodo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int actualizarSaldoCuentaPeriodo( int periodo){
        String queryStr = String.format(
                "UPDATE PLAN_DE_CUENTA_LINEAS A"+
                "   SET SALDO = (SELECT sum(coalesce(B.saldo,0)) FROM cuenta_partida_centro B WHERE A.PLAN_DE_CUENTA_CODIGO = B.CUENTA_CONTABLE_CODIGO  AND PERIODO = '%d' group by  B.CUENTA_CONTABLE_codigo)"+
                " WHERE EXISTS (SELECT 1 FROM PLAN_DE_CUENTA_LINEAS B WHERE A.PLAN_DE_CUENTA_CODIGO = B.PLAN_DE_CUENTA_CODIGO AND B.PERIODO = '%d')  AND PERIODO = '%d'"
                , periodo,periodo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }

    public void limpiarSaldosAsociadosPeriodo(int periodo){
        ArrayList<String> lineas = new ArrayList();
        lineas.add("centro_lineas");
        lineas.add("partida_lineas");
        lineas.add("plan_de_cuenta_lineas");

        ConexionBD.crearStatement();
        for(String linea:lineas){
          String queryStr = String.format("" +
                "UPDATE "+linea+" \n" +
                "   SET saldo = '0'\n"+
                " WHERE periodo = '%d'",
                periodo);
        ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
}
