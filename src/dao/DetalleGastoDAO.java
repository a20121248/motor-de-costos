/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controlador.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.AsignacionPartidaCuenta;
import modelo.CargarDetalleGastoLinea;
import modelo.ConnectionDB;
import modelo.CuentaContable;
import modelo.Partida;
import modelo.Centro;

/**
 *
 * @author briggette.olenka.ro1
 */
public class DetalleGastoDAO {
    
    ConnectionDB connection;

    public DetalleGastoDAO() {
        connection = new ConnectionDB();
    }
    public List<CuentaContable> listarMaestro(String codigos, int repartoTipo) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("SELECT codigo,nombre FROM plan_de_cuentas WHERE esta_activo=1 AND reparto_tipo=%d ORDER BY codigo",repartoTipo);
        } else {
            queryStr = String.format(""+
                    "SELECT codigo,nombre\n" +
                    "  FROM plan_de_cuentas\n" +
                    " WHERE esta_activo=1 AND codigo NOT IN (%s) AND reparto_tipo=%d\n" +
                    " ORDER BY codigo",
                    codigos,repartoTipo);
        }
        List<CuentaContable> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                CuentaContable item = new CuentaContable(codigo, nombre, null, 0, null, null);
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
                + " WHERE   periodo = '%d'",periodo);
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
    
    public List<String> listarCodigosPartidas_CuentaPartida(int periodo) {
        String queryStr = String.format(""
                + "SELECT   partida_codigo"
                + "  FROM   PARTIDA_CUENTA_CONTABLE"
                + " WHERE   periodo = '%d'",periodo);
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
    
    public void insertarDetalleGasto(int periodo, List<CargarDetalleGastoLinea> lista) throws SQLException {
        ConexionBD.crearStatement();
        ConexionBD.tamanhoBatchMax = 100;
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        for (CargarDetalleGastoLinea item: lista) {
            String queryStr = String.format(Locale.US, "" +
                    "UPDATE plan_de_cuenta_lineas\n" +
                    "   SET SALDO=%.2f,fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                    " WHERE plan_de_cuenta_codigo='%s' AND periodo=%d",
                    item.getSaldo(),
                    fechaStr,
                    item.getCodigoCuentaContable(),
                    periodo);
            ConexionBD.agregarBatch(queryStr);
        }
        // los posibles registros que no se hayan ejecutado
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
//    public void insertarSaldo(CargarDetalleGastoLinea cargarDetalleGastoLinea) {
//        Connection access = connection.getConnection();
//        PreparedStatement ps = null;
//        int valor = -1;
//        try {
//            ps = access.prepareStatement("" +
//                    "DELETE FROM plan_de_cuenta_lineas\n" +
//                    " WHERE plan_de_cuenta_codigo=? AND periodo=?"
//            );
//            ps.setString(1, cargarDetalleGastoLinea.getCodigoCuentaContable());
//            ps.setInt(2, cargarDetalleGastoLinea.getPeriodo());
//            valor = ps.executeUpdate();
//
//            ps = access.prepareStatement("" +
//                    "INSERT INTO plan_de_cuenta_lineas(plan_de_cuenta_codigo,periodo,saldo,fecha_creacion,fecha_actualizacion)\n" +
//                    "VALUES (?,?,?,?,?)");
//            java.sql.Date fecha_sql = new java.sql.Date(System.currentTimeMillis());
//            ps.setString(1, cargarDetalleGastoLinea.getCodigoCuentaContable());
//            ps.setInt(2, cargarDetalleGastoLinea.getPeriodo());
//            ps.setDouble(3, cargarDetalleGastoLinea.getSaldo());
//            ps.setDate(4, fecha_sql);
//            ps.setDate(5, fecha_sql);
//            valor = ps.executeUpdate();
//        } catch (SQLException sqlEx) {
//            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
//            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
//            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
//            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
}
