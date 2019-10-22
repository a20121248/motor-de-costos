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
    public int borrarListaDetalleGastoPeriodo(int periodo, int repartoTipo) {
        String periodoStr = repartoTipo == 1 ? "PERIODO" : "TRUNC(PERIODO/100)*100";
        String queryStr = String.format("" +
                "DELETE FROM MS_CUENTA_PARTIDA_CENTRO\n" +
                " WHERE %s=%d AND REPARTO_TIPO=%d", periodoStr, periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarDetalleGasto(int periodo, List<DetalleGasto> lista, int repartoTipo) throws SQLException {
        limpiarSaldosAsociadosPeriodo(periodo, repartoTipo);
        borrarListaDetalleGastoPeriodo(periodo, repartoTipo);
        ConexionBD.crearStatement();
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (DetalleGasto item: lista) {
            int periodoIter = periodo;
            if (repartoTipo != 1) ++periodoIter;
            String codigoCuentaContable = item.getCodigoCuentaContable();
            String codigoPartida = item.getCodigoPartida();
            String codigoCentro = item.getCodigoCentro();
            List<Double> montos = item.getMontos();
            for (Double monto: montos) {
                String queryStr = String.format(Locale.US, "" +
                        "INSERT INTO MS_CUENTA_PARTIDA_CENTRO(CUENTA_CONTABLE_CODIGO,PARTIDA_CODIGO,CENTRO_CODIGO,REPARTO_TIPO,PERIODO,SALDO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                        "VALUES ('%s','%s','%s',%d,'%d',%.4f,TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'))",
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
        actualizarSaldoCuentaPartidaPeriodo(periodo, repartoTipo);
        
        // actualizo montos por mes
        int periodoIter = periodo;
        if (repartoTipo != 1) ++periodoIter;
        for (Double monto: lista.get(0).getMontos())
            insertarMontoTablaCentroPeriodo(periodoIter++, repartoTipo);
    }
    
    public int actualizarSaldoCuentaPeriodo(int periodo, int repartoTipo) {        
        String periodoStr = repartoTipo == 1 ? "PERIODO" : "TRUNC(PERIODO/100)*100";
        String queryStr = String.format("" +
                "UPDATE MS_PLAN_DE_CUENTA_LINEAS A\n" +
                "   SET A.SALDO=(SELECT SUM(COALESCE(B.SALDO,0)) FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PLAN_DE_CUENTA_CODIGO=B.CUENTA_CONTABLE_CODIGO AND %s=%d AND B.REPARTO_TIPO=%d GROUP BY B.CUENTA_CONTABLE_CODIGO)\n" +
                " WHERE %s=%d AND A.REPARTO_TIPO=%d AND EXISTS (SELECT 1 FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PLAN_DE_CUENTA_CODIGO=B.CUENTA_CONTABLE_CODIGO AND %s=%d AND B.REPARTO_TIPO=%d)",
                periodoStr, periodo, repartoTipo,
                periodoStr, periodo, repartoTipo,
                periodoStr, periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarSaldoPartidaPeriodo(int periodo, int repartoTipo) {
        String periodoStr = repartoTipo == 1 ? "PERIODO" : "TRUNC(PERIODO/100)*100";
        String queryStr = String.format("" +
                "UPDATE MS_PARTIDA_LINEAS A\n" +
                "   SET A.SALDO=(SELECT SUM(COALESCE(B.SALDO,0)) FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PARTIDA_CODIGO=B.PARTIDA_CODIGO AND %s=%d AND B.REPARTO_TIPO=%d GROUP BY B.PARTIDA_CODIGO)\n" +
                " WHERE %s=%d AND A.REPARTO_TIPO=%d AND EXISTS (SELECT 1 FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PARTIDA_CODIGO=B.PARTIDA_CODIGO AND %s=%d AND B.REPARTO_TIPO=%d)",
                periodoStr, periodo, repartoTipo,
                periodoStr, periodo, repartoTipo,
                periodoStr, periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarSaldoCuentaPartidaPeriodo(int periodo, int repartoTipo) {        
        String periodoStr = repartoTipo == 1 ? "PERIODO" : "TRUNC(PERIODO/100)*100";
        String queryStr = String.format("" +
                "UPDATE MS_PARTIDA_CUENTA_CONTABLE A\n" +
                "   SET SALDO=(SELECT SUM(COALESCE(B.SALDO,0)) FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PARTIDA_CODIGO=B.PARTIDA_CODIGO AND A.CUENTA_CONTABLE_CODIGO = B.CUENTA_CONTABLE_CODIGO AND %s=%d AND B.REPARTO_TIPO=%d GROUP BY B.CUENTA_CONTABLE_CODIGO,B.PARTIDA_CODIGO)\n"+
                " WHERE %s=%d AND A.REPARTO_TIPO=%d AND EXISTS (SELECT 1 FROM MS_CUENTA_PARTIDA_CENTRO B WHERE A.PARTIDA_CODIGO=B.PARTIDA_CODIGO AND A.CUENTA_CONTABLE_CODIGO=B.CUENTA_CONTABLE_CODIGO AND %s=%d AND B.REPARTO_TIPO=%d)",
                periodoStr, periodo, repartoTipo,
                periodoStr, periodo, repartoTipo,
                periodoStr, periodo, repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarMontoTablaCentroPeriodo(int periodo, int repartoTipo) {
        actualizarSaldoCentroPeriodo("MS_CENTRO_LINEAS", periodo, repartoTipo);
    }

    public void insertarMontoTablaCascada(int periodo, int repartoTipo) {
        actualizarSaldoCentroPeriodo("MS_CASCADA", periodo, repartoTipo);
    }
    
    public void actualizarSaldoCentroPeriodo(String tabla, int periodo, int repartoTipo) {        
        String queryStr = String.format("" +
                "DELETE FROM %s\n" +
                " WHERE PERIODO=%d AND ITERACION=-1 AND REPARTO_TIPO=%d",
                tabla, periodo, repartoTipo);
        ConexionBD.ejecutar(queryStr);

        queryStr = String.format("" +
                "INSERT INTO %s(PERIODO,CENTRO_CODIGO,ITERACION,SALDO,ENTIDAD_ORIGEN_CODIGO,GRUPO_GASTO,REPARTO_TIPO,CUENTA_CONTABLE_ORIGEN_CODIGO,PARTIDA_ORIGEN_CODIGO,CENTRO_ORIGEN_CODIGO)\n" +
                "SELECT A.PERIODO,\n" +
                "       A.CENTRO_CODIGO,\n" +
                "       -1 ITERACION,\n" +
                "       SUM(COALESCE(A.SALDO,0)) SALDO,\n" +
                "       A.CENTRO_CODIGO ENTIDAD_ORIGEN_CODIGO,\n" + // el mismo centro de costos
                "       B.GRUPO_GASTO,\n" +
                "       A.REPARTO_TIPO REPARTO_TIPO,\n" +
                "       A.CUENTA_CONTABLE_CODIGO CUENTA_CONTABLE_ORIGEN_CODIGO,\n" +
                "       A.PARTIDA_CODIGO PARTIDA_ORIGEN_CODIGO,\n" +
                "       A.CENTRO_CODIGO CENTRO_ORIGEN_CODIGO\n" +
                "  FROM MS_CUENTA_PARTIDA_CENTRO A\n" +
                "  JOIN MS_PARTIDAS B ON B.CODIGO=A.partida_codigo\n" +
                " WHERE A.PERIODO=%d AND A.REPARTO_TIPO=%d\n" +
                " GROUP BY A.PERIODO,A.CENTRO_CODIGO,B.GRUPO_GASTO,A.REPARTO_TIPO,A.CUENTA_CONTABLE_CODIGO,A.PARTIDA_CODIGO,A.CENTRO_CODIGO",
                tabla, periodo, repartoTipo);
        ConexionBD.ejecutar(queryStr);        
    }

    public void limpiarSaldosAsociadosPeriodo(int periodo, int repartoTipo) {
        ArrayList<String> lineas = new ArrayList();
        lineas.add("MS_PARTIDA_LINEAS");
        lineas.add("MS_PLAN_DE_CUENTA_LINEAS");
        String periodoStr = repartoTipo == 1 ? "PERIODO" : "TRUNC(PERIODO/100)*100";
        
        ConexionBD.crearStatement();
        for (String linea: lineas) {
            String queryStr;
            queryStr = String.format("" +
                "UPDATE %s\n" +
                "   SET SALDO=0\n"+
                " WHERE %s=%d AND REPARTO_TIPO=%d", linea, periodoStr, periodo, repartoTipo);
            ConexionBD.agregarBatch(queryStr);
        }
        String queryStr = String.format("" +
                "DELETE FROM MS_CENTRO_LINEAS\n" +
                " WHERE ITERACION=-1 AND %s=%d AND REPARTO_TIPO=%d", periodoStr, periodo, repartoTipo);
        ConexionBD.agregarBatch(queryStr);
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
}
