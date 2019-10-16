/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import modelo.CargarCuentaPartidaLinea;
import modelo.CargarObjetoPeriodoLinea;
import modelo.Driver;
import modelo.Partida;
import modelo.Tipo;

public class PartidaDAO {
    public List<String> listarCodigos() {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery("SELECT codigo FROM MS_partidas")) {
            while(rs.next()) lista.add(rs.getString("codigo"));
        } catch (SQLException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosPeriodo(int periodo, int repartoTipo) {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(String.format("SELECT partida_codigo FROM MS_partida_lineas WHERE periodo=%d and reparto_tipo=%d", periodo,repartoTipo))) {
            while(rs.next()) lista.add(rs.getString("partida_codigo"));
        } catch (SQLException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public void insertarListaObjeto(List<Partida> lista, int repartoTipo) {
        ConexionBD.crearStatement();
        for (Partida item:lista){
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String grupoGasto = item.getGrupoGasto().getCodigo();
            String strTipoGasto = item.getTipoGasto();
            int tipoGasto;
            if(strTipoGasto.equals("DIRECTO")) tipoGasto = 1;
            else tipoGasto = 0;
            
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            String queryStr = String.format("" +
                    "INSERT INTO MS_PARTIDAS(CODIGO,NOMBRE,GRUPO_GASTO,TIPO_GASTO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s',q'[%s]','%s','%d',0,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,grupoGasto,tipoGasto,fechaStr,fechaStr);
//            System.out.println(queryStr+";");
            ConexionBD.agregarBatch(queryStr);
            
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public List<Partida> listarPeriodo(int periodo, int repartoTipo) {        
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       SUM(COALESCE(B.saldo,0)) saldo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM MS_partidas A\n" +
                "  JOIN MS_partida_lineas B ON B.partida_codigo=A.codigo\n" +
                " WHERE B.periodo=%d AND B.reparto_tipo=%d\n",
                periodo,repartoTipo);
        queryStr += "\n GROUP BY A.codigo,A.nombre,A.fecha_creacion,A.fecha_actualizacion\n" +
                    "\n ORDER BY A.codigo";
        List<Partida> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                double saldo = rs.getDouble("saldo");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Partida item = new Partida(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion);              
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Partida> listar(int periodo, String tipoGasto, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       C.driver_codigo,\n" +
                "       SUM(E.saldo) saldo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM partidas A\n" +
                "  JOIN partida_lineas B ON B.partida_codigo=A.codigo\n" +
//                Revisar las consultas con la nueva tabla PARTIDA y PARTIDA_LINEA
                "  LEFT JOIN entidad_origen_driver C ON C.entidad_origen_codigo=A.codigo AND C.periodo=B.periodo\n" +
                "  LEFT JOIN partida_cuenta_contable D ON D.partida_codigo=A.codigo AND D.periodo=B.periodo\n" +
                "  LEFT JOIN plan_de_cuenta_lineas E ON E.plan_de_cuenta_codigo=D.cuenta_contable_codigo AND E.periodo=B.periodo\n" +
                " WHERE  B.periodo=%d AND A.reparto_tipo=%d\n",
                periodo,repartoTipo);
        switch(tipoGasto) {
            case "Administrativo":
                queryStr += "   AND SUBSTR(A.codigo,0,2)='45'\n";
                break;
            case "Operativo":
                queryStr += "   AND SUBSTR(A.codigo,0,2)='44'\n";
        }
        queryStr += " GROUP BY A.codigo,A.nombre,C.driver_codigo,A.fecha_creacion,A.fecha_actualizacion\n" +
                    " ORDER BY A.codigo";
        List<Partida> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                double saldo = rs.getDouble("saldo");
                String driverCodigo = rs.getString("driver_codigo");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Partida item = new Partida(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion);
                if (driverCodigo!=null) {
                    item.setDriver(new Driver(driverCodigo, null, null, null, null, null));
                }                
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    // Lista objetos para catálogo
    public List<Partida> listarObjetos(String codigos, int repartoTipo) {
        TipoDAO tipoDAO = new TipoDAO();
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       grupo_gasto,\n" +
                "       CASE WHEN tipo_gasto=0 then 'INDIRECTO'\n" +
                "            WHEN tipo_gasto=1 then 'DIRECTO'\n" +
                "       END tipo_gasto," +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM MS_partidas\n" +
                " ORDER BY codigo",repartoTipo);
        } else {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       grupo_gasto,\n" +
                "       CASE WHEN tipo_gasto=0 then 'INDIRECTO'\n" +
                "            WHEN tipo_gasto=1 then 'DIRECTO'\n" +
                "       END tipo_gasto," +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM MS_partidas\n" +
                " WHERE codigo NOT IN (%s)\n" +
                " ORDER BY codigo",codigos,repartoTipo);
        }
        List<Tipo> listaGrupoGastos = tipoDAO.listarGrupoGastos();
        List<Partida> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String grupoGasto = rs.getString("grupo_gasto");
                String tipoGasto = rs.getString("tipo_gasto");
                Tipo tipoGrupoGasto = listaGrupoGastos.stream().filter(item ->grupoGasto.equals(item.getCodigo())).findAny().orElse(null); 
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Partida item = new Partida(codigo, nombre, null, tipoGasto,tipoGrupoGasto, 0, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    //TOCHECK: partida_lineas tienen saldo???
    public List<Partida> listarPartidaConCuentaContable(int periodo, int repartoTipo) {
//        actualizarSaldoCuentaPartida(periodo);   
        String queryStr = String.format("" +
                "SELECT NVL(D.codigo,'Sin CuentaContable asignada') cuenta_contable_codigo,\n" +
                "       NVL(D.nombre,'Sin CuentaContable asignada') cuenta_contable_nombre,\n" +
                "       A.codigo partida_codigo,\n" +
                "       A.nombre partida_nombre,\n" +
                "       NVL(C.saldo,0) partida_cuenta_contable_saldo,\n" +
                "       A.fecha_creacion partida_fecha_creacion,\n" +
                "       A.fecha_actualizacion partida_fecha_actualizacion\n" +
                "  FROM MS_partidas A\n" +
                "  JOIN MS_partida_lineas B ON A.codigo=B.partida_codigo AND B.periodo=%d AND B.reparto_tipo=%d\n" +
                "  LEFT JOIN MS_partida_cuenta_contable C ON A.codigo=C.partida_codigo AND C.periodo=B.periodo AND C.reparto_tipo = B.reparto_tipo\n" +
                "  LEFT JOIN MS_plan_de_cuentas D ON C.cuenta_contable_codigo=D.codigo\n" +
                " ORDER BY partida_codigo,cuenta_contable_codigo",
                periodo,repartoTipo);
        List<Partida> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String cuentaContableCodigo = rs.getString("cuenta_contable_codigo");
                String cuentaContableNombre = rs.getString("cuenta_contable_nombre");
                String partidaCodigo = rs.getString("partida_codigo");
                String partidaNombre = rs.getString("partida_nombre");
                double Saldo = rs.getDouble("partida_cuenta_contable_saldo");
                Date partidaFechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("partida_fecha_creacion"));
                Date partidaFechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("partida_fecha_actualizacion"));

                Tipo cuentaContable = new Tipo(cuentaContableCodigo, cuentaContableNombre, null);
                Partida item = new Partida(partidaCodigo, partidaNombre, null, Saldo, partidaFechaCreacion, partidaFechaActualizacion, cuentaContable);

                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public int verificarObjetoPartidaPeriodoAsignacion(String codigo, int periodo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM MS_partida_cuenta_contable\n" +
                " WHERE partida_codigo='%s' AND periodo=%d",
                codigo,periodo);
        int cont=-1;
        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
            while(rs.next()) {
                cont = rs.getInt("COUNT");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cont;
    }
    
    public int verificarObjetoPartida(String codigo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM MS_partida_lineas\n" +
                " WHERE partida_codigo='%s'",
                codigo);
        int cont=-1;
        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
            while(rs.next()) {
                cont = rs.getInt("COUNT");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cont;
    }
    
    public int insertarObjeto(String codigo, String nombre, String grupoGasto, int tipoGasto, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO MS_partidas(codigo,nombre,grupo_gasto,reparto_tipo,tipo_gasto,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%s',0,'%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,grupoGasto,tipoGasto,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarObjeto(String codigo, String nombre, String grupoGasto, int tipoGasto) {
        String queryStr = String.format("UPDATE MS_partidas SET nombre='%s', grupo_gasto='%s', tipo_gasto='%d' WHERE codigo='%s'",nombre,grupoGasto,tipoGasto,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarObjeto(String codigo) {
        String queryStr = String.format("DELETE FROM MS_partidas WHERE codigo='%s'",codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
//    public int cantObjetosSinDriver(int repartoTipo, int periodo) {
//        String queryStr = String.format("" +
//            "SELECT COUNT(1) cnt\n" +
//            "  FROM partidas A\n" +
//            "  JOIN partida_lineas B ON A.codigo=B.partida_codigo\n" +
//            "  LEFT JOIN entidad_origen_driver C ON A.codigo=C.entidad_origen_codigo AND B.periodo=C.periodo\n" +
//            " WHERE A.reparto_tipo=%d\n" +
//            "   AND B.periodo=%d\n" +
//            "   AND C.entidad_origen_codigo IS NULL",repartoTipo,periodo);
//        ResultSet rs = ConexionBD.ejecutarQuery(queryStr);
//        int cnt = 0;
//        try {
//            rs.next();
//            cnt = rs.getInt("cnt");
//        } catch (SQLException ex) {
//            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return cnt;
//    }
    
//    public double leerSaldoCuentaPartida(String cuentaContableCodigo, String partidaCodigo, int periodo){
//        String queryStr = String.format("" +
//                "SELECT COALESCE(saldo,0) SALDO \n"+
//                "  FROM partida_cuenta_contable\n" +
//                " WHERE partida_codigo='%s'"+
//                "   AND cuenta_contable_codigo='%s'"+
//                "   AND periodo = '%d'",
//                cuentaContableCodigo, partidaCodigo, periodo);
//        double saldo=0;
//        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
//            while(rs.next()) {
//                saldo = rs.getInt("SALDO");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return saldo;
//        
//    }
    
    public double leerSaldoPartida(String partidaCodigo, int periodo){
        String queryStr = String.format("" +
                "SELECT COALESCE(saldo,0) SALDO \n"+
                "  FROM partida_lineas\n" +
                " WHERE partida_codigo='%s'"+
                "   AND periodo = '%d'",
                 partidaCodigo, periodo);
        double saldo=0;
        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
            while(rs.next()) {
                saldo = rs.getInt("SALDO");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldo;
    }

//    public int actualizarSaldoCuentaPartida( int periodo){
//        String queryStr = String.format(
//                "UPDATE PARTIDA_CUENTA_CONTABLE A"+ 
//                "   SET SALDO = (SELECT B.SALDO FROM PARTIDA_LINEAS B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND PERIODO = '%d')"+
//                " WHERE EXISTS (SELECT 1 FROM PARTIDA_LINEAS B WHERE A.PARTIDA_CODIGO = B.PARTIDA_CODIGO AND B.PERIODO = '%d')"
//                , periodo,periodo);
//        return ConexionBD.ejecutar(queryStr);
//    }
    
    public int eliminarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("DELETE FROM MS_partida_lineas WHERE partida_codigo='%s' AND periodo=%d",codigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjetoPeriodo(String codigo, int periodo,int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format(""+
                "INSERT INTO MS_partida_lineas(partida_codigo,periodo,saldo,reparto_tipo,fecha_creacion,fecha_actualizacion) "+
                "VALUES('%s',%d,0,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,repartoTipo,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarListaObjetoPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_partida_lineas A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM MS_partidas B\n" +
                "                WHERE A.partida_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND A.reparto_tipo=%d)",
                periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarListaObjetoPeriodo(List<CargarObjetoPeriodoLinea> lista, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        borrarListaObjetoPeriodo(lista.get(0).getPeriodo(),repartoTipo);
        ConexionBD.crearStatement();
        for (CargarObjetoPeriodoLinea item: lista) {
            int periodo = item.getPeriodo();
            String codigo = item.getCodigo();
            String queryStr = String.format(Locale.US, "" +
                "INSERT INTO MS_partida_lineas(partida_codigo,periodo,saldo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s',%d,'%d',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,0,repartoTipo,fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public int insertarPartidaCuenta(String partidaCodigo, String cuentaContableCodigo, int periodo, int reparto_tipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
//        Double saldo = leerSaldoPartida(partidaCodigo, periodo);
        String queryStr = String.format("" +
                "INSERT INTO MS_partida_cuenta_contable(partida_codigo,cuenta_contable_codigo,periodo,saldo,es_bolsa,reparto_tipo, fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%d','%.2f','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    partidaCodigo,
                    cuentaContableCodigo,
                    periodo,
                    0.0,
                    "NO",
                    reparto_tipo,
                    fechaStr,
                    fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarPartidasCuenta(int periodo, List<CargarCuentaPartidaLinea> lista, int repartoTipo) {
        borrarPartidasCuenta(periodo,repartoTipo);
        ConexionBD.crearStatement();
        for (CargarCuentaPartidaLinea item: lista) {
            String codigoPartida = item.getCodigoPartida();
            String codigoCuenta = item.getCodigoCuentaContable();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());

            // inserto una linea dummy
            String queryStr = String.format(Locale.US, "" +
                "INSERT INTO MS_partida_cuenta_contable(partida_codigo,cuenta_contable_codigo,saldo,periodo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%d','%d','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigoPartida,
                    codigoCuenta,
                    0,
                    periodo,
                    repartoTipo,
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public int borrarPartidasCuenta(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_partida_cuenta_contable A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM MS_partidas B\n" +
                "                WHERE A.partida_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND A.reparto_tipo=%d)",
                periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    public int borrarPartidaCuenta(String partidaCodigo,int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_partida_cuenta_contable\n" +
                " WHERE partida_codigo='%s' AND periodo=%d AND reparto_tipo = %d",
                partidaCodigo,periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public String abrevituraPalabra(String palabra){
        switch(palabra){
            case "GT":
                palabra = "Gastos de Tecnología";
                break;
            case "GP":
                palabra = "Gastos de Personal";
                break;
            case "GO":
                palabra = "Gastos de Operaciones";
                break;
            default:
                palabra = null;
                break;
        }
        return palabra;
    }
    
    public String palabraAbreviatura(String palabra){
        switch(palabra){
            case "Gastos de Tecnología":
                palabra = "GT";
                break;
            case "Gastos de Personal":
                palabra = "GP";
                break;
            case "Gastos de Operaciones":
                palabra = "GO";
                break;
            default:
                palabra = null;
                break;
        }
        return palabra;
    }
}
