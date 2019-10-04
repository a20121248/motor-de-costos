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

public class DetalleGastoDAO {
    ConnectionDB connection;

    public DetalleGastoDAO() {
        connection = new ConnectionDB();
    }
    
    public List<DetalleGasto> listar(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.cuenta_contable_codigo cuenta_contable_codigo,\n" +
                "       B.nombre cuenta_contable_nombre,\n" +
                "       A.partida_codigo partida_codigo,\n" +
                "       C.nombre partida_nombre,\n" +
                "       A.centro_codigo centro_codigo,\n" +
                "       D.nombre centro_nombre,\n" +
                "       SUM(COALESCE(A.saldo,0)) saldo\n" +
                "  FROM ms_cuenta_partida_centro A\n" +
                "  JOIN ms_plan_de_cuentas B ON B.codigo=A.cuenta_contable_codigo\n" +
                "  JOIN ms_partidas C ON C.codigo=A.partida_codigo\n" +
                "  JOIN ms_centros D ON D.codigo=A.centro_codigo\n" +
                " WHERE A.periodo=%d AND A.reparto_tipo=%d\n",
                periodo, repartoTipo);
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
                DetalleGasto item = new DetalleGasto(codigoCuentaContable, nombreCuentaContable,codigoPartida, nombrePartida,codigoCECO, nombreCECO,saldo, true);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosCuenta_CuentaPartida(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT DISTINCT(CUENTA_CONTABLE_CODIGO) CUENTA_CONTABLE_CODIGO\n" +
                "  FROM MS_PARTIDA_CUENTA_CONTABLE\n" +
                " WHERE PERIODO=%d\n" +
                "   AND REPARTO_TIPO=%d", periodo, repartoTipo);
        List<String> lista = new ArrayList();
        
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCuentaContable = rs.getString("CUENTA_CONTABLE_CODIGO");
                lista.add(codigoCuentaContable);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosPartidas_CuentaPartida(String codigoCuentaContable, int periodo) {
        String queryStr = String.format("" +
                "SELECT PARTIDA_CODIGO\n" +
                "  FROM MS_PARTIDA_CUENTA_CONTABLE\n" +
                " WHERE PERIODO='%d' AND CUENTA_CONTABLE_CODIGO='%s'", periodo, codigoCuentaContable);
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
    public int borrarListaDetalleGastoPeriodo(int periodo, boolean considerarMes) {
        String periodoStr = considerarMes ? "PERIODO" : "TRUNC(PERIODO/100)*100";
        String queryStr = String.format("" +
                "DELETE FROM MS_CUENTA_PARTIDA_CENTRO\n" +
                " WHERE %s=%d", periodoStr, periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarDetalleGasto(int periodo, List<DetalleGasto> lista, int repartoTipo) throws SQLException {
        limpiarSaldosAsociadosPeriodo(periodo, false);
        borrarListaDetalleGastoPeriodo(periodo, false);
        ConexionBD.crearStatement();
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        int periodoIter = periodo;
        if (repartoTipo != 1) ++periodoIter;
        for (DetalleGasto item: lista) {
            String codigoCuentaContable = item.getCodigoCuentaContable();
            String codigoPartida = item.getCodigoPartida();
            String codigoCentro = item.getCodigoCentro();
            List<Double> montos = item.getMontos();
            for (Double monto: montos) {
                String queryStr = String.format(Locale.US, "" +
                        "INSERT INTO MS_CUENTA_PARTIDA_CENTRO(CUENTA_CONTABLE_CODIGO,PARTIDA_CODIGO,CENTRO_CODIGO,REPARTO_TIPO,PERIODO,SALDO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                        "VALUES ('%s','%s','%s',%d,'%d',%.2f,TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'))",
                        codigoCuentaContable,
                        codigoPartida,
                        codigoCentro,
                        repartoTipo,
                        periodoIter++,
                        monto,
                        fechaStr,
                        fechaStr);
                ConexionBD.agregarBatch(queryStr);
            }
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        
        // actualizo montos por mes o de anho dependiendo del tipo de reparto
        actualizarSaldoCuentaPeriodo(periodo, repartoTipo);
        actualizarSaldoPartidaPeriodo(periodo, repartoTipo);
        actualizarSaldoCentroPeriodo(periodo, repartoTipo);
        
        // actualizo montos por mes
        periodoIter = periodo;
        if (repartoTipo != 1) ++periodoIter;
        for (Double monto: lista.get(0).getMontos()) {
            actualizarSaldoCuentaPartidaPeriodo(periodoIter, repartoTipo);
        }
    }
    
    public int actualizarSaldoCuentaPeriodo(int periodo, int repartoTipo) {        
        String queryStr = null;
        if (repartoTipo == 1) {
            queryStr = String.format(
                    "UPDATE MS_PLAN_DE_CUENTA_LINEAS A\n" +
                    "   SET SALDO=(SELECT SUM(COALESCE(B.SALDO,0)) FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PLAN_DE_CUENTA_CODIGO=B.CUENTA_CONTABLE_CODIGO AND TRUNC(B.PERIODO/100)*100=%d AND B.REPARTO_TIPO=%d GROUP BY B.CUENTA_CONTABLE_CODIGO)\n" +
                    " WHERE TRUNC(A.PERIODO/100)*100=%d AND A.REPARTO_TIPO=%d AND EXISTS (SELECT 1 FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PLAN_DE_CUENTA_CODIGO=B.CUENTA_CONTABLE_CODIGO AND TRUNC(B.PERIODO/100)*100=%d AND B.REPARTO_TIPO=%d)",
                    periodo, repartoTipo,
                    periodo, repartoTipo,
                    periodo, repartoTipo);
        } else {
            queryStr = String.format(
                    "UPDATE MS_PLAN_DE_CUENTA_LINEAS A\n" +
                    "   SET SALDO=(SELECT SUM(COALESCE(B.SALDO,0)) FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PLAN_DE_CUENTA_CODIGO=B.CUENTA_CONTABLE_CODIGO AND B.PERIODO=%d AND B.REPARTO_TIPO=%d GROUP BY B.CUENTA_CONTABLE_CODIGO)\n" +
                    " WHERE A.PERIODO=%d AND A.REPARTO_TIPO=%d AND EXISTS (SELECT 1 FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PLAN_DE_CUENTA_CODIGO=B.CUENTA_CONTABLE_CODIGO AND B.PERIODO=%d AND B.REPARTO_TIPO=%d)",
                    periodo, repartoTipo,
                    periodo, repartoTipo,
                    periodo, repartoTipo);
        }
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarSaldoPartidaPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format(
                "UPDATE PARTIDA_LINEAS A\n" +
                "   SET SALDO = (SELECT sum(coalesce(B.saldo,0)) FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO  AND PERIODO = '%d' group by  B.partida_codigo)"+
                " WHERE EXISTS (SELECT 1 FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND B.PERIODO = '%d') AND PERIODO = '%d'"
                ,periodo, periodo, periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarSaldoCuentaPartidaPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format(
                "UPDATE PARTIDA_CUENTA_CONTABLE A"+
                "   SET SALDO = (SELECT sum(coalesce(B.saldo,0)) FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND A.cuenta_contable_codigo = B.cuenta_contable_codigo AND PERIODO = '%d' group by B.cuenta_contable_codigo, B.partida_codigo)"+
                " WHERE EXISTS (SELECT 1 FROM cuenta_partida_centro B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND A.cuenta_contable_codigo = B.cuenta_contable_codigo AND B.PERIODO = '%d') AND PERIODO = '%d'"
                , periodo,periodo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }

    public void actualizarSaldoCentroPeriodo(int periodo, int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "DELETE FROM MS_CENTRO_LINEAS\n" +
                " WHERE PERIODO=%d AND ITERACION=-1 AND REPARTO_TIPO=%d",
                periodo, repartoTipo);
        ConexionBD.ejecutar(queryStr);

        queryStr = String.format("" +
                "INSERT INTO MS_CENTRO_LINEAS(CENTRO_CODIGO,PERIODO,ITERACION,SALDO,GRUPO_GASTO,FECHA_CREACION,FECHA_ACTUALIZACION,ENTIDAD_ORIGEN_CODIGO,REPARTO_TIPO)\n" +
                "SELECT CENTRO_CODIGO,\n" +
                "       A.PERIODO,\n" +
                "       -1 iteracion,\n" +
                "       sum(coalesce(A.saldo,0)) saldo," +
                "       B.grupo_gasto," +
                "       TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_creacion," +
                "       TO_DATE('%s','yyyy/mm/dd hh24:mi:ss') fecha_actualizacion," +
                "       0 entidad_origen_codigo,\n" +
                "       %d REPARTO_TIPO\n" +
                "  FROM CUENTA_PARTIDA_CENTRO A\n" +
                "  JOIN PARTIDAS B ON B.codigo = A.partida_codigo\n" +
                " WHERE a.periodo=%d\n" +
                " GROUP BY a.centro_codigo,b.grupo_gasto", fechaStr, fechaStr, repartoTipo, periodo);
        ConexionBD.ejecutar(queryStr);
    }

    public void limpiarSaldosAsociadosPeriodo(int periodo, boolean considerarMes) {
        ArrayList<String> lineas = new ArrayList();
        lineas.add("MS_PARTIDA_LINEAS");
        lineas.add("MS_PLAN_DE_CUENTA_LINEAS");
        String periodoStr = considerarMes ? "PERIODO" : "TRUNC(PERIODO/100)*100";
        
        ConexionBD.crearStatement();
        for (String linea: lineas) {
            String queryStr;
            queryStr = String.format("" +
                "UPDATE %s\n" +
                "   SET SALDO=0\n"+
                " WHERE %s=%d", linea, periodoStr, periodo);
            System.out.println(queryStr);//===========================================
            ConexionBD.agregarBatch(queryStr);
        }
        String queryStr = String.format("" +
                "DELETE FROM MS_CENTRO_LINEAS\n" +
                " WHERE ITERACION=-1\n" +
                "   AND %s=%d", periodoStr, periodo);
        System.out.println(queryStr);//===========================================
        ConexionBD.agregarBatch(queryStr);
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
}
