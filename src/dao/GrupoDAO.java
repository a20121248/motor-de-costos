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
import modelo.CargarObjetoPeriodoLinea;
import modelo.Driver;
import modelo.Grupo;

public class GrupoDAO {
    public List<String> listarCodigos() {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery("SELECT codigo FROM grupos")) {
            while(rs.next()) lista.add(rs.getString("codigo"));
        } catch (SQLException ex) {
            Logger.getLogger(GrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public void insertarListaObjeto(List<Grupo> lista, int repartoTipo) {
        ConexionBD.crearStatement();
        lista.stream().filter(item -> item.getFlagCargar()).map((item) -> {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            String queryStr = String.format("" +
                    "INSERT INTO GRUPOS(CODIGO,NOMBRE,ESTA_ACTIVO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,1,repartoTipo,fechaStr,fechaStr);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public List<Grupo> listar(int periodo, String tipoGasto, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.esta_activo,\n" +
                "       C.driver_codigo,\n" +
                "       SUM(E.saldo) saldo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM grupos A\n" +
                "  JOIN grupo_lineas B ON B.grupo_codigo=A.codigo\n" +
                "  LEFT JOIN entidad_origen_driver C ON C.entidad_origen_codigo=A.codigo AND C.periodo=B.periodo\n" +
                "  LEFT JOIN grupo_plan_de_cuenta D ON D.grupo_codigo=A.codigo AND D.periodo=B.periodo\n" +
                "  LEFT JOIN plan_de_cuenta_lineas E ON E.plan_de_cuenta_codigo=D.plan_de_cuenta_codigo AND E.periodo=B.periodo\n" +
                " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d\n",
                periodo,repartoTipo);
        switch(tipoGasto) {
            case "Administrativo":
                queryStr += "   AND SUBSTR(A.codigo,0,2)='45'\n";
                break;
            case "Operativo":
                queryStr += "   AND SUBSTR(A.codigo,0,2)='44'\n";
        }
        queryStr += " GROUP BY A.codigo,A.nombre,A.esta_activo,C.driver_codigo,A.fecha_creacion,A.fecha_actualizacion\n" +
                    " ORDER BY A.codigo";
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                double saldo = rs.getDouble("saldo");
                String driverCodigo = rs.getString("driver_codigo");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Grupo item = new Grupo(codigo, nombre, null, saldo, null, fechaCreacion, fechaActualizacion);
                if (driverCodigo!=null) {
                    item.setDriver(new Driver(driverCodigo, null, null, null, null, null));
                }                
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(GrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Grupo> listarObjetos(String codigos, int repartoTipo) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       esta_activo,\n" +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM grupos\n" +
                " WHERE esta_activo=1 AND reparto_tipo=%d\n" +
                " ORDER BY codigo",repartoTipo);
        } else {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       esta_activo,\n" +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM grupos\n" +
                " WHERE esta_activo=1 AND codigo NOT IN (%s) AND reparto_tipo=%d\n" +
                " ORDER BY codigo",codigos,repartoTipo);
        }
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Grupo item = new Grupo(codigo, nombre, null, 0, null, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(GrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public int verificarObjetoGrupoPeriodoAsignacion(String codigo, int periodo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM grupo_plan_de_cuenta\n" +
                " WHERE grupo_codigo='%s' AND periodo=%d",
                codigo,periodo);
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
    
//    Verifica si esta siendo usado en las lineas
    public int verificarObjetoGrupo(String codigo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM grupo_lineas\n" +
                " WHERE grupo_codigo='%s'",
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
                "INSERT INTO grupos(codigo,nombre,esta_activo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,1,repartoTipo,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarObjeto(String codigo, String nombre, int estado) {
        String queryStr = String.format("UPDATE grupos SET nombre='%s',esta_activo=%d WHERE codigo='%s'",nombre,estado,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarObjeto(String codigo) {
        String queryStr = String.format("DELETE FROM grupos WHERE codigo='%s'",codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int cantObjetosSinDriver(int repartoTipo, int periodo) {
        String queryStr = String.format("" +
            "SELECT COUNT(1) cnt\n" +
            "  FROM grupos A\n" +
            "  JOIN grupo_lineas B ON A.codigo=B.grupo_codigo\n" +
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
            Logger.getLogger(GrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }
    
    public int eliminarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("DELETE FROM grupo_lineas WHERE grupo_codigo='%s' AND periodo=%d",codigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("INSERT INTO grupo_lineas(grupo_codigo,periodo) VALUES('%s',%d)",codigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int borrarListaObjetoPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM grupo_lineas A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM grupos B\n" +
                "                WHERE A.grupo_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND B.reparto_tipo=%d)",
                periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarListaObjetoPeriodo(List<CargarObjetoPeriodoLinea> lista, int repartoTipo) {
        borrarListaObjetoPeriodo(lista.get(0).getPeriodo(),repartoTipo);
        ConexionBD.crearStatement();
        for (CargarObjetoPeriodoLinea item: lista) {
            int periodo = item.getPeriodo();
            String codigo = item.getCodigo();
            String queryStr = String.format(Locale.US, "" +
                "INSERT INTO grupo_lineas(grupo_codigo,periodo)\n" +
                "VALUES ('%s',%d)",
                codigo,periodo);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    /*
    public ArrayList<Grupo> listar_Grupo(int periodo) {
        ArrayList<Grupo> lista_Grupo = new ArrayList();
        ArrayList<PlanDeCuenta> lista_PlanDeCuenta = null;
        try {
            Connection access = connection.getConnection();
            PreparedStatement ps = access.prepareStatement("" +
                "SELECT A.codigo grupo_codigo,\n" +
                "       A.periodo periodo,\n" +
                "       A.nombre grupo_nombre,\n" +
                "       A.descripcion grupo_descripcion,\n" +
                "       A.fecha_creacion grupo_fecha_creacion,\n" +
                "       A.fecha_actualizacion grupo_fecha_actualizacion,\n" +
                "       C.codigo pdc_codigo,\n" +
                "       C.nombre pdc_nombre,\n" +
                "       C.descripcion pdc_descripcion,\n" +
                "       C.fecha_creacion pdc_fecha_creacion,\n" +
                "       C.fecha_actualizacion pdc_fecha_actualizacion,\n" +
                "       D.saldo\n" +
                "  FROM grupos A\n" +
                "  JOIN grupo_plan_de_cuenta B ON A.codigo = B.grupo_codigo AND A.periodo = B.periodo\n" +
                "  JOIN plan_de_cuentas C ON B.plan_de_cuenta_codigo = C.codigo\n" +
                "  JOIN plan_de_cuenta_lineas D ON C.codigo = D.plan_de_cuenta_codigo\n" +
                " WHERE A.esta_activo = 1 AND C.esta_activo = 1\n" +
                "   AND A.periodo = " + periodo);
            ResultSet rs = ps.executeQuery();
            
            String grupo_codigo_ant = "";
            String grupo_codigo_act;
            while(rs.next()) {
                grupo_codigo_act = rs.getString("grupo_codigo");
                if (!grupo_codigo_ant.equals(grupo_codigo_act)) {
                    // es un nuevo codigo
                    String grupo_nombre = rs.getString("grupo_nombre");
                    String grupo_descripcion = rs.getString("grupo_descripcion");
                    Date grupo_fecha_creacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("grupo_fecha_creacion"));
                    Date grupo_fecha_actualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("grupo_fecha_actualizacion"));
                
                    lista_PlanDeCuenta =  new ArrayList();
                    Grupo grupo = new Grupo(grupo_codigo_act, periodo, grupo_nombre, grupo_descripcion, lista_PlanDeCuenta, grupo_fecha_creacion, grupo_fecha_actualizacion);
                    lista_Grupo.add(grupo);
                }
                String pdc_codigo = rs.getString("pdc_codigo");
                String pdc_nombre = rs.getString("pdc_nombre");
                String pdc_descripcion = rs.getString("pdc_descripcion");
                Date pdc_fecha_creacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("pdc_fecha_creacion"));
                Date pdc_fecha_actualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("pdc_fecha_actualizacion"));
                double saldo = rs.getDouble("saldo");
                CuentaContable planDeCuenta = new CuentaContable(pdc_codigo, pdc_nombre, pdc_descripcion, pdc_fecha_creacion, pdc_fecha_actualizacion, periodo, saldo);
                
                lista_PlanDeCuenta.add(planDeCuenta);
                grupo_codigo_ant = grupo_codigo_act;
            }
        } catch (Exception e) {            
        }
        return lista_Grupo;
    }
    */
}
