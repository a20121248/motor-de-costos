package dao;

import controlador.ConexionBD;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import modelo.DetalleGasto;
import modelo.CargarGrupoCuentaLinea;
import modelo.CargarGrupoLinea;
import modelo.CargarObjetoLinea;
import modelo.CargarObjetoPeriodoLinea;
import modelo.CargarPlanDeCuentaLinea;
import modelo.ConnectionDB;
import modelo.Grupo;
import modelo.CuentaContable;
import modelo.Driver;
import modelo.Tipo;

public class PlanDeCuentaDAO {

    ConnectionDB connection;

    public PlanDeCuentaDAO() {
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

    public List<String> listarCodigos() {
        String queryStr = String.format("SELECT CODIGO FROM PLAN_DE_CUENTAS");
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                lista.add(codigo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosPeriodo(int periodo) {
        String queryStr = String.format(String.format("SELECT plan_de_cuenta_codigo FROM PLAN_DE_CUENTA_LINEAS WHERE periodo=%d ", periodo));
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("plan_de_cuenta_codigo");
                lista.add(codigo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public int actualizarGrupoObjeto(String codigo, String nombre, int estado) {
        String queryStr = String.format("" +
                "UPDATE grupos\n" +
                "   SET nombre='%s',esta_activo=%d\n" +
                " WHERE codigo='%s'",
                nombre,estado,codigo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int actualizarObjeto(String codigo, String nombre, String atribuible, String tipoGasto, String claseGasto) {
        atribuible = convertirPalabraAbreviatura(atribuible);
        tipoGasto = convertirPalabraAbreviatura(tipoGasto);
        claseGasto = convertirPalabraAbreviatura(claseGasto);
        String queryStr = String.format("" +
                "UPDATE plan_de_cuentas\n" +
                "   SET nombre='%s',\n"+
                "       atribuible = '%s',\n" +
                "       tipo = '%s',\n" +
                "       clase = '%s'\n" +
                " WHERE codigo='%s'",
                nombre,atribuible,tipoGasto,claseGasto,codigo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int insertarObjetoCuenta(String codigo, String nombre, int repartoTipo, String atribuible, String tipoGasto, String claseGasto) {
        atribuible = convertirPalabraAbreviatura(atribuible);
        tipoGasto = convertirPalabraAbreviatura(tipoGasto);
        claseGasto = convertirPalabraAbreviatura(claseGasto);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO plan_de_cuentas(codigo,nombre,esta_activo,reparto_tipo, atribuible, tipo, clase, fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,%d,'%s','%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,1,repartoTipo,atribuible, tipoGasto, claseGasto, fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }

    public void insertarObjetoCuentaPeriodo(String codigo, int periodo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO plan_de_cuenta_lineas(plan_de_cuenta_codigo,periodo,saldo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,0,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,fechaStr,fechaStr);
        ConexionBD.ejecutar(queryStr);
    }

    public void insertarListaObjetoCuenta(List<CuentaContable> lista, int repartoTipo) {
        ConexionBD.crearStatement();
        for(CuentaContable item: lista){
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String atribuible = item.getAtribuible();
            String tipoGasto = item.getTipoGasto();
            String claseGasto = item.getClaseGasto();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            atribuible = convertirPalabraAbreviatura(atribuible);
            tipoGasto = convertirPalabraAbreviatura(tipoGasto);
            claseGasto = convertirPalabraAbreviatura(claseGasto);
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO plan_de_cuentas(codigo,nombre,esta_activo,reparto_tipo, atribuible, tipo, clase, fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s',%d,%d,'%s','%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,1,repartoTipo,atribuible, tipoGasto, claseGasto, fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }

    public int borrarListaObjetoCuentaPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM plan_de_cuenta_lineas A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM plan_de_cuentas B\n" +
                "                WHERE A.plan_de_cuenta_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND B.reparto_tipo=%d)",
                periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }

    public void insertarListaObjetoCuentaPeriodo(List<CargarObjetoPeriodoLinea> lista, int repartoTipo) {
        borrarListaObjetoCuentaPeriodo(lista.get(0).getPeriodo(), repartoTipo);
        ConexionBD.crearStatement();
        for (CargarObjetoPeriodoLinea item: lista) {
            int periodo = item.getPeriodo();
            String codigo = item.getCodigo();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());

            // inserto una linea dummy
            String queryStr = String.format(Locale.US, "" +
                "INSERT INTO plan_de_cuenta_lineas(plan_de_cuenta_codigo,periodo,saldo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%d',%d,TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s', 'yyyy/mm/dd hh24:mi:ss'))",
                    codigo,
                    periodo,
                    0,
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }

    public List<CuentaContable> listar(int periodo, String tipoGasto, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion,\n" +
                "       SUM(COALESCE(B.saldo,0)) saldo\n" +
                "  FROM plan_de_cuentas A\n" +
                "  JOIN plan_de_cuenta_lineas B ON B.plan_de_cuenta_codigo=A.codigo\n" +
                " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d\n",
                periodo,repartoTipo);
        switch(tipoGasto) {
            case "Administrativo":
                queryStr += "\n   AND SUBSTR(A.codigo,0,2)='45'";
                break;
            case "Operativo":
                queryStr += "\n   AND SUBSTR(A.codigo,0,2)='44'";
        }
        queryStr += "\n GROUP BY A.codigo,A.nombre,A.fecha_creacion,A.fecha_actualizacion\n" +
                    "\n ORDER BY A.codigo";
        List<CuentaContable> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = rs.getDouble("saldo");
                CuentaContable item = new CuentaContable(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public List<CuentaContable> listarObjetoCuentas(int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.atribuible,\n" +
                "       A.tipo,\n" +
                "       A.clase,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM plan_de_cuentas A\n" +
                " WHERE A.reparto_tipo=%d AND A.esta_activo=1\n" +
                " ORDER BY A.codigo",repartoTipo);
        List<CuentaContable> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String atribuible = rs.getString("atribuible");
                String tipoGasto = rs.getString("tipo");
                String claseGasto = rs.getString("clase");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = 0;
                
                atribuible = convertirAbreviaturaPalabra(atribuible);
                tipoGasto = convertirAbreviaturaPalabra(tipoGasto);
                claseGasto = convertirAbreviaturaPalabra(claseGasto);
                
                CuentaContable item = new CuentaContable(codigo, nombre, null, atribuible, tipoGasto, claseGasto, saldo, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public int eliminarObjetoGrupo(String codigo) {
        String queryStr = String.format("" +
                "DELETE FROM grupos\n" +
                " WHERE codigo='%s'",
                codigo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int eliminarObjetoCuenta(String codigo) {
        String queryStr = String.format("" +
                "DELETE FROM plan_de_cuentas\n" +
                " WHERE codigo='%s'",
                codigo);
        return ConexionBD.ejecutar(queryStr);
    }

    public void eliminarObjetoCuentaPeriodo(String codigo, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM plan_de_cuenta_lineas\n" +
                " WHERE plan_de_cuenta_codigo='%s' AND periodo=%d",
                codigo,periodo);
        ConexionBD.ejecutar(queryStr);
    }

    public int verificarObjetoPlanCuentaPeriodoAsignacion(String codigo, int periodo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM partida_cuenta_contable\n" +
                " WHERE cuenta_contable_codigo='%s' AND periodo=%d",
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
    public int verificarObjetoPlanCuentaLineas(String codigo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM plan_de_cuenta_lineas\n" +
                " WHERE plan_de_cuenta_codigo='%s'",
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

    public List<Grupo> listarObjetoGrupos(int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.esta_activo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM grupos A\n" +
                " WHERE A.esta_activo=1 AND reparto_tipo=%d\n" +
                " ORDER BY A.codigo",repartoTipo);
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
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public int insertarListaObjetoGrupo(List<CargarGrupoLinea> lista) {
        ConexionBD.crearStatement();
        for (CargarGrupoLinea item: lista) {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());

            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO grupos(codigo,nombre,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,1,fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
        return 1;
    }

    public int insertarObjetoGrupo(String codigo, String nombre, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO grupos(codigo,nombre,esta_activo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,1,repartoTipo,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }

    public void insertarGrupos(List<CargarGrupoLinea> lista) throws SQLException {
        /*Connection access1 = connection.getConnection();
        Connection access2 = connection.getConnection();
        Connection access3 = connection.getConnection();
        access1.setAutoCommit(false);
        access2.setAutoCommit(false);
        access3.setAutoCommit(false);
        Statement ps1 = null;
        Statement ps2 = null;
        Statement ps3 = null;
        Date fecha = null;
        Format formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        int posI = -1;
        ResultSet rs;
        String query=null, fechaStr=null;
        int numOper = -1;
        int batchSize = 1;
        int result[] = null;
        try {
            ps1 = access1.createStatement();
            ps2 = access2.createStatement();
            ps3 = access3.createStatement();
            numOper = 0;
            for (posI = 0; posI < lista.size(); ++posI) {
                int periodo = lista.get(posI).getPeriodo();
                String codigo = lista.get(posI).getCodigo();
                String nombre = lista.get(posI).getNombre();

                query = String.format("SELECT COUNT(1) count FROM grupos WHERE codigo='%s'",codigo);
                System.out.println(query);
                rs = ps1.executeQuery(query);
                rs.next();
                fecha = new Date();
                fechaStr = formatter.format(fecha);
                if (rs.getInt("count") == 1) {  // lo encontró, entonces lo actualizo
                    query = String.format("" +
                            "UPDATE grupos\n" +
                            "   SET nombre='%s',descripcion=%s,esta_activo=%d,\n" +
                            "       fecha_creacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                            " WHERE codigo='%s'",
                            nombre,null,1,fechaStr,fechaStr,codigo);
                    //System.out.println(query);
                    ps2.addBatch(query);
                } else { // no lo encontró, entonces lo inserto
                    query = String.format("" +
                            "INSERT INTO grupos(codigo,nombre,descripcion,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
                            "VALUES ('%s','%s',%s,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                            codigo,nombre,null,1,fechaStr,fechaStr);
                    //System.out.println(query);
                    ps2.addBatch(query);
                }
                // inserto una linea dummy correspondiente al periodo
                query = String.format("" +
                        "DELETE FROM grupo_plan_de_cuenta\n" +
                        " WHERE grupo_codigo='%s' AND periodo=%d",
                        codigo, periodo);
                //System.out.println(query);
                ps3.addBatch(query);

                query = String.format("" +
                        "INSERT INTO grupo_plan_de_cuenta(grupo_codigo,plan_de_cuenta_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                        "VALUES('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                        codigo,"-",periodo,fechaStr,fechaStr);
                //System.out.println(query);
                ps3.addBatch(query);

                ++numOper;
                if(numOper % batchSize == 0) {
                    result = ps2.executeBatch();
                    access2.commit();
                    result = ps3.executeBatch();
                    access3.commit();
                }
            }
        } catch (BatchUpdateException b) {
            access2.rollback();
            access3.rollback();
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/
    }

    public int borrarCuentasGrupo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM grupo_plan_de_cuenta A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM grupos B\n" +
                "                WHERE A.grupo_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND B.reparto_tipo=%d)",
                periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int borrarCuentaGrupo(String cuentaCodigo,int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM grupo_plan_de_cuenta\n" +
                " WHERE plan_de_cuenta_codigo='%s' AND periodo=%d",
                cuentaCodigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int insertarCuentaGrupo(String cuentaCodigo, String grupoCodigo, int periodo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO grupo_plan_de_cuenta(grupo_codigo,plan_de_cuenta_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    grupoCodigo,
                    cuentaCodigo,
                    periodo,
                    fechaStr,
                    fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }

    public void insertarCuentasGrupo(int periodo, List<CargarGrupoCuentaLinea> lista, int repartoTipo) {
        borrarCuentasGrupo(periodo,repartoTipo);
        ConexionBD.crearStatement();
        for (CargarGrupoCuentaLinea item: lista) {
            String codigoGrupo = item.getCodigoAgrupacion();
            String codigoCuenta = item.getCodigoPlanDeCuenta();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());

            // inserto una linea dummy
            String queryStr = String.format(Locale.US, "" +
                "INSERT INTO grupo_plan_de_cuenta(grupo_codigo,plan_de_cuenta_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigoGrupo,
                    codigoCuenta,
                    periodo,
                    fechaStr,
                    fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }

    public List<Tipo> listarGrupos(int periodo) {
        String queryStr = String.format("" +
                "SELECT DISTINCT A.codigo,\n" +
                "       A.nombre\n" +
                "  FROM grupos A\n" +
                "  JOIN grupo_plan_de_cuenta B ON A.codigo=B.grupo_codigo\n" +
                " WHERE A.esta_activo=1 AND B.periodo=%d",
                periodo);
        List<Tipo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Tipo tipo = new Tipo(codigo,nombre,null);
                lista.add(tipo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public List<Grupo> listarGruposNombresConDriver(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       COALESCE(E.codigo,'Sin driver asignado') driver_codigo,\n" +
                "       COALESCE(E.nombre,'Sin driver asignado') driver_nombre\n" +
                "  FROM grupos A\n" +
                "  JOIN grupo_lineas B ON A.codigo=B.grupo_codigo\n" +
                "  LEFT JOIN grupo_plan_de_cuenta C ON A.codigo=C.grupo_codigo AND B.periodo=C.periodo\n" +
                "  LEFT JOIN entidad_origen_driver D ON A.codigo=D.entidad_origen_codigo AND B.periodo=D.periodo\n" +
                "  LEFT JOIN drivers E ON D.driver_codigo=E.codigo AND A.reparto_tipo=E.reparto_tipo  \n" +
                " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d\n" +
                " GROUP BY A.codigo,A.nombre,E.codigo,E.nombre\n" +
                " ORDER BY A.codigo",
                periodo,repartoTipo);
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String driverCodigo = rs.getString("driver_codigo");
                String driverNombre = rs.getString("driver_nombre");
                Grupo grupo = new Grupo(codigo,nombre,null,0,null,null,null);
                grupo.setDriver(new Driver(driverCodigo,driverNombre,null,null,null,null));
                lista.add(grupo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

public List<Grupo> listarGruposNombres() {
        String queryStr = "SELECT codigo,nombre FROM GRUPOS WHERE esta_activo=1 ORDER BY codigo";
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Grupo grupo = new Grupo(codigo,nombre,null,0,null,null,null);
                lista.add(grupo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public List<Grupo> listarGruposNombres(int periodo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       SUM(C.saldo) saldo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM grupos A\n" +
                "  JOIN grupo_plan_de_cuenta B ON A.codigo=B.grupo_codigo\n" +
                "  JOIN plan_de_cuenta_lineas C ON B.plan_de_cuenta_codigo=C.plan_de_cuenta_codigo\n" +
                "  JOIN plan_de_cuentas D ON C.plan_de_cuenta_codigo=D.codigo\n" +
                " WHERE A.esta_activo=1 AND B.periodo=%d AND C.periodo=%d AND D.esta_activo=1\n" +
                " GROUP BY A.codigo,A.nombre,A.fecha_creacion,A.fecha_actualizacion",
                periodo,periodo);
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                double saldo = rs.getDouble("saldo");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Grupo grupo = new Grupo(codigo,nombre,null,saldo,null,fechaCreacion,fechaActualizacion);
                lista.add(grupo);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public List<CuentaContable> listarProductosNombresNewF(int periodo) {
        String queryStr = String.format("" +
                "SELECT DISTINCT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.descripcion,\n" +
                "       A.grupo_balance,\n" +
                "       A.rubro_operativo,\n" +
                "       A.cod_ponderacion,\n" +
                "       A.situacion_iva,\n" +
                "       A.situacion_it,\n" +
                "       A.distribuye_centro_costo,\n" +
                "       A.cod_producto,\n" +
                "       A.descripcion_producto,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM plan_de_cuentas A\n" +
                "  JOIN plan_de_cuenta_lineas B ON B.plan_de_cuenta_codigo=A.codigo\n" +
                " WHERE A.esta_activo=1 AND B.periodo=%d\n" +
                " ORDER BY A.codigo",
                periodo);
        List<CuentaContable> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String grupoBalance = rs.getString("grupo_balance");
                String rubroOperativo = rs.getString("rubro_operativo");
                String codPonderacion = rs.getString("cod_ponderacion");
                String situacionIVA = rs.getString("situacion_iva");
                String situacionIT = rs.getString("situacion_it");
                String distribuyeCentroCosto = rs.getString("distribuye_centro_costo");
                String codProducto = rs.getString("cod_producto");
                String descripcionProducto = rs.getString("descripcion_producto");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));

                //PlanDeCuenta item = new CuentaContable(codigo,nombre,descripcion,grupoBalance,rubroOperativo,codPonderacion,situacionIVA,situacionIT,distribuyeCentroCosto,codProducto,descripcionProducto,fechaCreacion,fechaActualizacion);
                //lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
        /*List<PlanDeCuenta> lista = new ArrayList();
        try (Connection access = connection.getConnection()) {
            PreparedStatement ps = access.prepareStatement("" +
                    "SELECT DISTINCT A.codigo,\n" +
                    "       A.nombre,\n" +
                    "       A.descripcion,\n" +
                    "       A.grupo_balance,\n" +
                    "       A.rubro_operativo,\n" +
                    "       A.cod_ponderacion,\n" +
                    "       A.situacion_iva,\n" +
                    "       A.situacion_it,\n" +
                    "       A.distribuye_centro_costo,\n" +
                    "       A.cod_producto,\n" +
                    "       A.descripcion_producto,\n" +
                    "       A.fecha_creacion,\n" +
                    "       A.fecha_actualizacion\n" +
                    "  FROM plan_de_cuentas A\n" +
                    "  JOIN plan_de_cuenta_lineas B ON B.plan_de_cuenta_codigo=A.codigo AND B.periodo=?\n" +
                    " WHERE A.esta_activo=1\n" +
                    " ORDER BY A.codigo");
            ps.setInt(1, periodo);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                String grupoBalance = rs.getString("grupo_balance");
                String rubroOperativo = rs.getString("rubro_operativo");
                String codPonderacion = rs.getString("cod_ponderacion");
                String situacionIVA = rs.getString("situacion_iva");
                String situacionIT = rs.getString("situacion_it");
                String distribuyeCentroCosto = rs.getString("distribuye_centro_costo");
                String codProducto = rs.getString("cod_producto");
                String descripcionProducto = rs.getString("descripcion_producto");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));

                CuentaContable item = new CuentaContable(codigo,nombre,descripcion,grupoBalance,rubroOperativo,codPonderacion,situacionIVA,situacionIT,distribuyeCentroCosto,codProducto,descripcionProducto,fechaCreacion,fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista;*/
    }   

    public void insertarListaPlanDeCuenta(List<CargarPlanDeCuentaLinea> lista) {
        Connection access1 = connection.getConnection();
        Connection access2 = connection.getConnection();
        Connection access3 = connection.getConnection();
        Connection access4 = connection.getConnection();
        Connection access5 = connection.getConnection();

        PreparedStatement ps1, ps2, ps3, ps4, ps5 = null;
        ResultSet rs = null;
        java.sql.Date fecha_sql = null;
        int[] result;
        int valor = -1;
        try {
            access1.setAutoCommit(false);
            access2.setAutoCommit(false);
            access3.setAutoCommit(false);
            access4.setAutoCommit(false);
            access5.setAutoCommit(false);
            int numOper2 = 0;
            int numOper3 = 0;
            int numOper4 = 0;
            int batchSize = 20;
            ps1 = access1.prepareStatement("SELECT COUNT(1) count FROM plan_de_cuentas WHERE codigo=?");
            ps2 = access2.prepareStatement("" +
                    "UPDATE plan_de_cuentas\n" +
                    "   SET nombre=?,descripcion=?,grupo_balance=?,rubro_operativo=?,\n" +
                    "       cod_ponderacion=?,situacion_IVA=?,situacion_IT=?,distribuye_centro_costo=?,\n" +
                    "       cod_producto=?,descripcion_producto=?,esta_activo=?,fecha_actualizacion=?\n" +
                    " WHERE codigo=?");
            ps3 = access3.prepareStatement("" +
                    "INSERT INTO plan_de_cuentas(codigo,nombre,descripcion,grupo_balance,rubro_operativo,cod_ponderacion,situacion_iva,situacion_it,distribuye_centro_costo,cod_producto,descripcion_producto,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps4 = access4.prepareStatement("" +
                    "DELETE FROM plan_de_cuenta_lineas\n" +
                    "WHERE plan_de_cuenta_codigo=? AND periodo=?"
            );
            ps5 = access5.prepareStatement("" +
                    "INSERT INTO plan_de_cuenta_lineas(plan_de_cuenta_codigo,periodo,saldo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES (?,?,?,?,?)"
            );
            for (CargarPlanDeCuentaLinea item : lista) {
                // busco el producto
                ps1.setString(1, item.getRubroContable());
                rs = ps1.executeQuery();
                rs.next();

                if (rs.getInt("count") == 1) { // lo encontró, entonces lo actualizo
                    fecha_sql = new java.sql.Date(System.currentTimeMillis());
                    ps2.setString(1, item.getDescripcionCuenta());
                    ps2.setString(2, null);
                    ps2.setString(3, item.getGrupoBalance());
                    ps2.setString(4, item.getRubroOperativo());
                    ps2.setString(5, item.getCodPonderacion());
                    ps2.setString(6, item.getSituacionIVA());
                    ps2.setString(7, item.getSituacionIT());
                    ps2.setString(8, item.getDistribuyeCentroCosto());
                    ps2.setString(9, item.getCodProducto());
                    ps2.setString(10, item.getDescripcionCuenta());
                    ps2.setInt(11, 1);
                    ps2.setDate(12, fecha_sql);
                    ps2.setString(13, item.getRubroContable());
                    System.out.println("ps2: " + ps2.toString());
                    ps2.addBatch();
                    ++numOper2;
                    //valor = ps.executeUpdate();
                } else { // no lo encontró, entonces lo inserto
                    fecha_sql = new java.sql.Date(System.currentTimeMillis());
                    ps3.setString(1, item.getRubroContable());
                    ps3.setString(2, item.getDescripcionCuenta());
                    ps3.setString(3, null);
                    ps3.setString(4, item.getGrupoBalance());
                    ps3.setString(5, item.getRubroOperativo());
                    ps3.setString(6, item.getCodPonderacion());
                    ps3.setString(7, item.getSituacionIVA());
                    ps3.setString(8, item.getSituacionIT());
                    ps3.setString(9, item.getDistribuyeCentroCosto());
                    ps3.setString(10, item.getCodProducto());
                    ps3.setString(11, item.getDescripcionProducto());
                    ps3.setInt(12, 1);
                    ps3.setDate(13, fecha_sql);
                    ps3.setDate(14, fecha_sql);
                    System.out.println("ps3: " + ps3.toString());
                    ps3.addBatch();
                    ++numOper3;
                    //valor = ps.executeUpdate();
                }

                // inserto una linea dummy correspondiente al periodo
                ps4.setString(1, item.getRubroContable());
                ps4.setInt(2, item.getPeriodo());
                //valor = ps.executeUpdate();
                System.out.println("ps4: " + ps4.toString());
                ps4.addBatch();

                ps5.setString(1, item.getRubroContable());
                ps5.setInt(2, item.getPeriodo());
                ps5.setInt(3, 0);
                ps5.setDate(4, fecha_sql);
                ps5.setDate(5, fecha_sql);
                //valor = ps.executeUpdate();
                System.out.println("ps5: " + ps5.toString());
                ps5.addBatch();

                ++numOper4;

                if((numOper2 + numOper3) % batchSize == 0) {
                    result = ps2.executeBatch();
                    access2.commit();
                    result = ps3.executeBatch();
                    access3.commit();
                }

                if(numOper4 % batchSize == 0) {
                    result = ps4.executeBatch();
                    access4.commit();
                    result = ps5.executeBatch();
                    access5.commit();
                }
            }
            ps2.executeBatch();
            access2.commit();
            ps3.executeBatch();
            access3.commit();
            ps4.executeBatch();
            access4.commit();
            ps5.executeBatch();
            access5.commit();
        } catch (BatchUpdateException batupEx) {

        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void insertarPlanDeCuenta(CargarPlanDeCuentaLinea item) {
        Connection access = connection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.sql.Date fecha_sql = null;
        int valor = -1;
        try {
            // busco el producto
            ps = access.prepareStatement("SELECT COUNT(1) count FROM plan_de_cuentas WHERE codigo=?");
            ps.setString(1, item.getRubroContable());
            rs = ps.executeQuery();
            rs.next();
            if (rs.getInt("count") == 1) { // lo encontró, entonces lo actualizo
                ps = access.prepareStatement("" +
                        "UPDATE plan_de_cuentas\n" +
                        "   SET nombre=?,descripcion=?,grupo_balance=?,rubro_operativo=?,\n" +
                        "       cod_ponderacion=?,situacion_IVA=?,situacion_IT=?,distribuye_centro_costo=?,\n" +
                        "       cod_producto=?,descripcion_producto=?,esta_activo=?,fecha_actualizacion=?\n" +
                        " WHERE codigo=?");
                fecha_sql = new java.sql.Date(System.currentTimeMillis());
                ps.setString(1, item.getDescripcionCuenta());
                ps.setString(2, null);
                ps.setString(3, item.getGrupoBalance());
                ps.setString(4, item.getRubroOperativo());
                ps.setString(5, item.getCodPonderacion());
                ps.setString(6, item.getSituacionIVA());
                ps.setString(7, item.getSituacionIT());
                ps.setString(8, item.getDistribuyeCentroCosto());
                ps.setString(9, item.getCodProducto());
                ps.setString(10, item.getDescripcionCuenta());
                ps.setInt(11, 1);
                ps.setDate(12, fecha_sql);
                ps.setString(13, item.getRubroContable());

                valor = ps.executeUpdate();
            } else { // no lo encontró, entonces lo inserto
                ps = access.prepareStatement("" +
                        "INSERT INTO plan_de_cuentas(codigo,nombre,descripcion,grupo_balance,rubro_operativo,cod_ponderacion,situacion_iva,situacion_it,distribuye_centro_costo,cod_producto,descripcion_producto,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                fecha_sql = new java.sql.Date(System.currentTimeMillis());
                ps.setString(1, item.getRubroContable());
                ps.setString(2, item.getDescripcionCuenta());
                ps.setString(3, null);
                ps.setString(4, item.getGrupoBalance());
                ps.setString(5, item.getRubroOperativo());
                ps.setString(6, item.getCodPonderacion());
                ps.setString(7, item.getSituacionIVA());
                ps.setString(8, item.getSituacionIT());
                ps.setString(9, item.getDistribuyeCentroCosto());
                ps.setString(10, item.getCodProducto());
                ps.setString(11, item.getDescripcionProducto());
                ps.setInt(12, 1);
                ps.setDate(13, fecha_sql);
                ps.setDate(14, fecha_sql);
                valor = ps.executeUpdate();
            }

            // inserto una linea dummy correspondiente al periodo


            ps = access.prepareStatement("" +
                    "DELETE FROM plan_de_cuenta_lineas\n" +
                    "WHERE plan_de_cuenta_codigo=? AND periodo=?"
            );
            ps.setString(1, item.getRubroContable());
            ps.setInt(2, item.getPeriodo());
            valor = ps.executeUpdate();

            ps = access.prepareStatement("" +
                    "INSERT INTO plan_de_cuenta_lineas(plan_de_cuenta_codigo,periodo,saldo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES (?,?,?,?,?)"
            );
            ps.setString(1, item.getRubroContable());
            ps.setInt(2, item.getPeriodo());
            ps.setInt(3, 0);
            ps.setDate(4, fecha_sql);
            ps.setDate(5, fecha_sql);
            valor = ps.executeUpdate();
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public List<CuentaContable> listarPlanDeCuentasNombres(int periodo) {
        List<CuentaContable> lista_plan_de_cuentas = new ArrayList();
        try {
            Connection access = connection.getConnection();
            PreparedStatement ps = access.prepareStatement("" +
                    "SELECT DISTINCT A.codigo pdc_codigo,\n" +
                    "       A.nombre pdc_nombre,\n" +
                    "       A.esta_activo pdc_esta_activo,\n" +
                    "       A.descripcion pdc_descripcion,\n" +
                    "       B.saldo pdc_saldo,\n" +
                    "       A.fecha_creacion pdc_fecha_creacion,\n" +
                    "       A.fecha_actualizacion pdc_fecha_actualizacion\n" +
                    "  FROM plan_de_cuentas A\n" +
                    "  JOIN plan_de_cuenta_lineas B ON A.codigo=B.plan_de_cuenta_codigo AND B.periodo=?\n" +
                    " WHERE A.esta_activo=1");
            ps.setInt(1, periodo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String pdc_codigo = rs.getString("pdc_codigo");
                String pdc_nombre = rs.getString("pdc_nombre");
                boolean estaActiva = rs.getInt("pdc_esta_activo") == 1;
                String pdc_descripcion = rs.getString("pdc_descripcion");
                double pdc_saldo = rs.getDouble("pdc_saldo");
                Date pdc_fecha_creacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("pdc_fecha_creacion"));
                Date pdc_fecha_actualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("pdc_fecha_actualizacion"));

                CuentaContable planDeCuenta = new CuentaContable(pdc_codigo, pdc_nombre, pdc_descripcion, pdc_fecha_creacion, pdc_fecha_actualizacion, pdc_saldo, null, estaActiva);

                lista_plan_de_cuentas.add(planDeCuenta);
            }
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return lista_plan_de_cuentas;
    }

    public List<CuentaContable> listarPlanDeCuentaConGrupo(int periodo, String tipoGasto, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo pdc_codigo,\n" +
                "       A.nombre pdc_nombre,\n" +
                "       A.esta_activo pdc_esta_activo,\n" +
                "       NVL(D.codigo,'Sin grupo asignado') grupo_codigo,\n" +
                "       NVL(D.nombre,'Sin grupo asignado') grupo_nombre,\n" +
                "       B.saldo pdc_saldo,\n" +
                "       A.fecha_creacion pdc_fecha_creacion,\n" +
                "       A.fecha_actualizacion pdc_fecha_actualizacion\n" +
                "  FROM plan_de_cuentas A\n" +
                "  JOIN plan_de_cuenta_lineas B ON A.codigo=B.plan_de_cuenta_codigo AND B.periodo=%d\n" +
                "  LEFT JOIN grupo_plan_de_cuenta C ON A.codigo=C.plan_de_cuenta_codigo AND C.periodo=%d\n" +
                "  LEFT JOIN grupos D ON C.grupo_codigo=D.codigo\n" +
                " WHERE A.reparto_tipo=%d",
                periodo,periodo,repartoTipo);
        switch(tipoGasto) {
            case "Administrativo":
                queryStr += "\n WHERE SUBSTR(A.codigo,0,2)='45'";
                break;
            case "Operativo":
                queryStr += "\n WHERE SUBSTR(A.codigo,0,2)='44'";
        }
        queryStr += "\n ORDER BY grupo_codigo,pdc_codigo";
        List<CuentaContable> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String pdcCodigo = rs.getString("pdc_codigo");
                String pdcNombre = rs.getString("pdc_nombre");
                boolean estaActiva = rs.getInt("pdc_esta_activo") == 1;
                String grupoCodigo = rs.getString("grupo_codigo");
                String grupoNombre = rs.getString("grupo_nombre");
                double pdcSaldo = rs.getDouble("pdc_saldo");
                Date pdcFechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("pdc_fecha_creacion"));
                Date pdcFechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("pdc_fecha_actualizacion"));

                Tipo grupo = new Tipo(grupoCodigo, grupoNombre, null);
                CuentaContable item = new CuentaContable(pdcCodigo, pdcNombre, null, pdcFechaCreacion, pdcFechaActualizacion, pdcSaldo, grupo, estaActiva);

                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(PlanDeCuentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public String convertirPalabraAbreviatura(String palabra){
        switch(palabra.toLowerCase()){
            case "sí":
                palabra = "SI";
                break;
            case "no":
                palabra = "NO";
                break;
            case "atribuible":
                palabra = "SI";
                break;
            case "no atribuible":
                palabra = "NO";
                break;
            case "adquisición":
                palabra = "GA";
                break;
            case "mantenimiento":
                palabra = "GM";
                break;
            case "fijo":
                palabra = "FI";
                break;
            case "variable":
                palabra = "VA";
                break;
            default:
                palabra = null;
                break;
        }
        return palabra;
    }
    
    public String convertirAbreviaturaPalabra(String palabra){
        switch(palabra){
                    case "SI":
                        palabra = "Atribuible";
                        break;
                    case "NO":
                        palabra = "No Atribuible";
                        break;
                    case "GA":
                        palabra = "Adquisición";
                        break;
                    case "GM":
                        palabra = "Mantenimiento";
                        break;
                    case "FI":
                        palabra = "Fijo";
                        break;
                    case "VA":
                        palabra = "Variable";
                        break;
                    default:
                        palabra = null;
                        break;
                }
        return palabra;
    }
}
