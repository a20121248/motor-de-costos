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
        try (ResultSet rs = ConexionBD.ejecutarQuery("SELECT codigo FROM partidas")) {
            while(rs.next()) lista.add(rs.getString("codigo"));
        } catch (SQLException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosPeriodo(int periodo) {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(String.format("SELECT partida_codigo FROM partida_lineas WHERE periodo=%d", periodo))) {
            while(rs.next()) lista.add(rs.getString("partida_codigo"));
        } catch (SQLException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public void insertarListaObjeto(List<Partida> lista, int repartoTipo) {
        ConexionBD.crearStatement();
        lista.stream().filter(item -> item.getFlagCargar()).map((item) -> {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            String queryStr = String.format("" +
                    "INSERT INTO PARTIDAS(CODIGO,NOMBRE,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,repartoTipo,fechaStr,fechaStr);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public List<Partida> listarPeriodo(int periodo,String tipoGasto, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       B.saldo saldo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM partidas A\n" +
                "  JOIN partida_lineas B ON B.partida_codigo=A.codigo\n" +
                " WHERE  B.periodo=%d AND A.reparto_tipo=%d\n",
                periodo,repartoTipo);
        switch(tipoGasto) {
            case "Administrativo":
                queryStr += "   AND SUBSTR(A.codigo,0,2)='45'";
                break;
            case "Operativo":
                queryStr += "   AND SUBSTR(A.codigo,0,2)='44'";
        }
        
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
                "  LEFT JOIN partida_plan_de_cuenta D ON D.partida_codigo=A.codigo AND D.periodo=B.periodo\n" +
                "  LEFT JOIN plan_de_cuenta_lineas E ON E.plan_de_cuenta_codigo=D.plan_de_cuenta_codigo AND E.periodo=B.periodo\n" +
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
    
    public List<Partida> listarObjetos(String codigos, int repartoTipo) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM partidas\n" +
                " WHERE reparto_tipo=%d\n" +
                " ORDER BY codigo",repartoTipo);
        } else {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM partidas\n" +
                " WHERE codigo NOT IN (%s) AND reparto_tipo=%d\n" +
                " ORDER BY codigo",codigos,repartoTipo);
        }
        
        List<Partida> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Double saldo = rs.getDouble("saldo");
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
    
    //TOCHECK: partida_lineas tienen saldo???
    public List<Partida> listarPartidaConCuentaContable(int periodo, String tipoGasto, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT "+
                "       NVL(D.codigo,'Sin CuentaContable asignada') plan_de_cuenta_codigo,\n" +
                "       NVL(D.nombre,'Sin CuentaContable asignada') plan_de_cuenta_nombre,\n" +
                "       A.codigo partida_codigo,\n" +
                "       A.nombre partida_nombre,\n" +
                "       B.saldo partida_saldo,\n" +
                "       A.fecha_creacion partida_fecha_creacion,\n" +
                "       A.fecha_actualizacion partida_fecha_actualizacion\n" +
                "  FROM partidas A\n" +
                "  JOIN partida_lineas B ON A.codigo=B.partida_codigo AND B.periodo=%d\n" +
                "  LEFT JOIN partida_plan_de_cuenta C ON A.codigo=C.partida_codigo AND C.periodo=%d\n" +
                "  LEFT JOIN plan_de_cuentas D ON C.plan_de_cuenta_codigo=D.codigo\n" +
                " WHERE A.reparto_tipo=%d",
                periodo,periodo,repartoTipo);
        switch(tipoGasto) {
            case "Administrativo":
                queryStr += "\n WHERE SUBSTR(A.codigo,0,2)='45'";
                break;
            case "Operativo":
                queryStr += "\n WHERE SUBSTR(A.codigo,0,2)='44'";
        }
        queryStr += "\n ORDER BY partida_codigo,plan_de_cuenta_codigo";
        List<Partida> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String cuentaContableCodigo = rs.getString("plan_de_cuenta_codigo");
                String cuentaContableNombre = rs.getString("plan_de_cuenta_nombre");
                String partidaCodigo = rs.getString("partida_codigo");
                String partidaNombre = rs.getString("partida_nombre");
                double partidaSaldo = rs.getDouble("partida_saldo");
                Date partidaFechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("partida_fecha_creacion"));
                Date partidaFechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("partida_fecha_actualizacion"));

                Tipo cuentaContable = new Tipo(cuentaContableCodigo, cuentaContableNombre, null);
                Partida item = new Partida(partidaCodigo, partidaNombre, null, partidaSaldo, partidaFechaCreacion, partidaFechaActualizacion, cuentaContable);

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
                "  FROM partida_plan_de_cuenta\n" +
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
                "  FROM partida_lineas\n" +
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
    
    public int insertarObjeto(String codigo, String nombre, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO partidas(codigo,nombre,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,repartoTipo,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarObjeto(String codigo, String nombre) {
        String queryStr = String.format("UPDATE partidas SET nombre='%s' WHERE codigo='%s'",nombre,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarObjeto(String codigo) {
        String queryStr = String.format("DELETE FROM partidas WHERE codigo='%s'",codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int cantObjetosSinDriver(int repartoTipo, int periodo) {
        String queryStr = String.format("" +
            "SELECT COUNT(1) cnt\n" +
            "  FROM partidas A\n" +
            "  JOIN partida_lineas B ON A.codigo=B.partida_codigo\n" +
            "  LEFT JOIN entidad_origen_driver C ON A.codigo=C.entidad_origen_codigo AND B.periodo=C.periodo\n" +
            " WHERE A.reparto_tipo=%d\n" +
            "   AND B.periodo=%d\n" +
            "   AND C.entidad_origen_codigo IS NULL",repartoTipo,periodo);
        ResultSet rs = ConexionBD.ejecutarQuery(queryStr);
        int cnt = 0;
        try {
            rs.next();
            cnt = rs.getInt("cnt");
        } catch (SQLException ex) {
            Logger.getLogger(PartidaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }
    
    public int eliminarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("DELETE FROM partida_lineas WHERE partida_codigo='%s' AND periodo=%d",codigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjetoPeriodo(String codigo, int periodo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format(""+
                "INSERT INTO partida_lineas(partida_codigo,periodo,saldo,fecha_creacion,fecha_actualizacion) "+
                "VALUES('%s',%d,0,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarListaObjetoPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM partida_lineas A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM partidas B\n" +
                "                WHERE A.partida_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND B.reparto_tipo=%d)",
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
                "INSERT INTO partida_lineas(partida_codigo,periodo,saldo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s',%d,'%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,0,fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public int insertarPartidaCuenta(String partidaCodigo, String cuentaContableCodigo, int periodo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO partida_plan_de_cuenta(partida_codigo,plan_de_cuenta_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    partidaCodigo,
                    cuentaContableCodigo,
                    periodo,
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
                "INSERT INTO partida_plan_de_cuenta(partida_codigo,plan_de_cuenta_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigoPartida,
                    codigoCuenta,
                    periodo,
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public int borrarPartidasCuenta(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM partida_plan_de_cuenta A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM plan_de_cuentas B\n" +
                "                WHERE A.plan_de_cuenta_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND B.reparto_tipo=%d)",
                periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    public int borrarPartidaCuenta(String partidaCodigo,int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM partida_plan_de_cuenta\n" +
                " WHERE partida_codigo='%s' AND periodo=%d",
                partidaCodigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
}
