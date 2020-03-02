package dao;

import controlador.ConexionBD;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.CargarCentroLinea;
import modelo.CargarObjetoPeriodoLinea;
import modelo.ConnectionDB;
import modelo.Centro;
import modelo.Driver;
import modelo.Tipo;

public class CentroDAO {
    ConnectionDB connection;
    TipoDAO tipoDAO;

    public CentroDAO() {
        this.connection = new ConnectionDB();
        this.tipoDAO = new TipoDAO();
    }
    
    public int insertarDistribucionGrupo(String grupoCodigo, int repartoTipo, int periodo, int iteracion) {
        String queryStr = String.format("" +
                "INSERT INTO CENTRO_LINEAS(CENTRO_CODIGO,PERIODO,ITERACION,SALDO,ENTIDAD_ORIGEN_CODIGO)" +
                "SELECT F.ENTIDAD_DESTINO_CODIGO CENTRO_CODIGO,\n" +
                "       %d PERIODO,\n" +
                "       0 ITERACION,\n" +
                "       SUM(E.SALDO*F.PORCENTAJE/100) SALDO,\n" +
                "       B.GRUPO_CODIGO ENTIDAD_ORIGEN_CODIGO\n" +
                "  FROM GRUPOS A\n" +
                "  JOIN GRUPO_LINEAS B ON B.PERIODO=%d AND B.GRUPO_CODIGO=A.CODIGO\n" +
                "  LEFT JOIN ENTIDAD_ORIGEN_DRIVER C ON C.PERIODO=%d AND C.ENTIDAD_ORIGEN_CODIGO=A.CODIGO\n" +
                "  LEFT JOIN GRUPO_PLAN_DE_CUENTA D ON D.PERIODO=%d AND D.GRUPO_CODIGO=A.CODIGO\n" +
                "  LEFT JOIN PLAN_DE_CUENTA_LINEAS E ON E.PERIODO=%d AND E.PLAN_DE_CUENTA_CODIGO=D.PLAN_DE_CUENTA_CODIGO\n" +
                "  LEFT JOIN DRIVER_LINEAS F ON F.PERIODO=%d AND F.DRIVER_CODIGO=C.DRIVER_CODIGO\n" +
                " WHERE A.CODIGO='%s' AND A.ESTA_ACTIVO=1 AND A.REPARTO_TIPO=%d\n" +
                " GROUP BY F.ENTIDAD_DESTINO_CODIGO,\n" +
                "       C.DRIVER_CODIGO,\n" +
                "       B.GRUPO_CODIGO\n" +
                "HAVING SUM(E.SALDO*F.PORCENTAJE)!=0",
                periodo,periodo,periodo,periodo,periodo,periodo,grupoCodigo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarDistribucionCentroObjeto(String centroCodigo, int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "INSERT INTO OBCO_LINEAS(OFICINA_CODIGO,BANCA_CODIGO,PRODUCTO_CODIGO,PERIODO,ENTIDAD_ORIGEN_CODIGO,SALDO,REPARTO_TIPO)\n" +
                "SELECT C.OFICINA_CODIGO,\n" +
                "       C.BANCA_CODIGO,\n" +
                "       C.PRODUCTO_CODIGO,\n" +
                "       %d PERIODO,\n" +
                "       '%s' ENTIDAD_ORIGEN_CODIGO,\n" +
                "       SUM(A.SALDO*C.PORCENTAJE/100) SALDO,\n" +
                "       %d REPARTO_TIPO\n" +
                "  FROM CENTRO_LINEAS A\n" +
                "  JOIN ENTIDAD_ORIGEN_DRIVER B ON B.PERIODO=%d AND B.ENTIDAD_ORIGEN_CODIGO=A.CENTRO_CODIGO\n" +
                "  JOIN DRIVER_OBCO_LINEAS C ON C.PERIODO=%d AND C.DRIVER_CODIGO=B.DRIVER_CODIGO\n" +
                " WHERE A.PERIODO=%d AND A.CENTRO_CODIGO='%s'\n" +
                " GROUP BY C.OFICINA_CODIGO,\n" +
                "       C.BANCA_CODIGO,\n" +
                "       C.PRODUCTO_CODIGO\n" +
                "HAVING SUM(A.SALDO*C.PORCENTAJE/100)!=0",
                periodo,centroCodigo,repartoTipo,periodo,periodo,periodo,centroCodigo);
        return ConexionBD.ejecutar(queryStr);
    }
        
    public int cantObjetosSinDriver(int repartoTipo, String operador, int nivel, int periodo) {
        String queryStr = String.format("" +
            "SELECT COUNT(1) cnt\n" +
            "  FROM centros A\n" +
            "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
            "  LEFT JOIN entidad_origen_driver C ON A.codigo=C.entidad_origen_codigo AND B.periodo=C.periodo\n" +
            " WHERE A.reparto_tipo=%d AND A.nivel%s%d\n" +
            "   AND B.periodo=%d\n" +
            "   AND C.entidad_origen_codigo IS NULL",repartoTipo,operador,nivel,periodo);
        ResultSet rs = ConexionBD.ejecutarQuery(queryStr);
        int cnt = 0;
        try {
            rs.next();
            cnt = rs.getInt("cnt");
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }
    
    public List<String> listarCodigos() {
        String queryStr = String.format("SELECT codigo FROM centros");
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                lista.add(rs.getString("codigo"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Centro> listarMaestro(String codigos, int repartoTipo) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("SELECT codigo,nombre FROM centros WHERE esta_activo=1 AND reparto_tipo=%d ORDER BY codigo",repartoTipo);
        } else {
            queryStr = String.format(""+
                    "SELECT codigo,nombre\n" +
                    "  FROM centros\n" +
                    " WHERE esta_activo=1 AND codigo NOT IN (%s) AND reparto_tipo=%d\n" +
                    " ORDER BY codigo",
                    codigos,repartoTipo);
        }
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Centro item = new Centro(codigo, nombre, 0, null, 0, null, null, null);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public int eliminarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("DELETE FROM centro_lineas WHERE centro_codigo='%s' AND periodo=%d",codigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int actualizarObjeto(String codigo, String nombre, String cecoTipoCodigo, int nivel, String cecoPadreCodigo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "UPDATE centros\n" +
                "   SET nombre='%s',esta_activo=%d,centro_tipo_codigo='%s',nivel=%d,centro_padre_codigo='%s',fecha_creacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                " WHERE codigo='%s'",
                nombre,1,cecoTipoCodigo,nivel,cecoPadreCodigo,fechaStr,fechaStr,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjeto(String codigo, String nombre, String cecoTipoCodigo, int nivel, String cecoPadreCodigo, int repartoTipo) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO centros(codigo,nombre,esta_activo,centro_tipo_codigo,nivel,centro_padre_codigo,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,'%s',%d,'%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,1,cecoTipoCodigo,nivel,cecoPadreCodigo,repartoTipo,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjetoPeriodo(String codigo, int periodo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,%d,%d,'%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,-1,0,"0",fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public String obtenerCodigoOficina(String centroCodigo) {
        String queryStr = String.format("SELECT oficina_codigo FROM centros WHERE codigo='%s'", centroCodigo);
        String oficinaCodigo = null;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                oficinaCodigo = rs.getString("oficina_codigo");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return oficinaCodigo;
    }
    
    public int borrarListaObjetoPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM centro_lineas A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM centros B\n" +
                "                WHERE A.centro_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND B.reparto_tipo=%d)",
                periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarListaObjetoPeriodo(List<CargarObjetoPeriodoLinea> lista, int repartoTipo) {
        borrarListaObjetoPeriodo(lista.get(0).getPeriodo(), repartoTipo);
        ConexionBD.crearStatement();
        for (CargarObjetoPeriodoLinea item: lista) {
            int periodo = item.getPeriodo();
            String codigo = item.getCodigo();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            
            // inserto una linea dummy
            String queryStr = String.format("" +
                    "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES('%s',%d,%d,%d,'%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,periodo,-1,0,"0",fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public List<Centro> listar(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.esta_activo,\n" +
                "       A.nivel,\n" +
                "       A.centro_padre_codigo,\n" +
                "       B.codigo tipo_codigo,\n" +
                "       B.nombre tipo_nombre,\n" +
                "       B.descripcion tipo_descripcion,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion,\n" +
                "       SUM(COALESCE(C.saldo,0)) saldo\n" +
                "  FROM centros A\n" +
                "  JOIN centro_tipos B ON A.centro_tipo_codigo=B.codigo\n" +
                "  JOIN centro_lineas C ON A.codigo=C.centro_codigo\n" +
                " WHERE C.periodo=%d AND A.reparto_tipo=%d\n" +
                " GROUP BY A.codigo,A.nombre,A.esta_activo,A.nivel,A.centro_padre_codigo," +
                "          B.codigo,B.nombre,B.descripcion,A.fecha_creacion,A.fecha_actualizacion",
                periodo,repartoTipo);
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                int nivel = rs.getInt("nivel");
                String centroPadreCodigo = rs.getString("centro_padre_codigo");
                String tipoCodigo = rs.getString("tipo_codigo");
                String tipoNombre = rs.getString("tipo_nombre");
                String tipoDescripcion = rs.getString("tipo_descripcion");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = rs.getDouble("saldo");
                
                Tipo tipo = new Tipo(tipoCodigo, tipoNombre, tipoDescripcion);
                Centro item = new Centro(codigo, nombre, nivel, null, saldo, tipo, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public void insertarListaObjeto(List<Centro> lista, int repartoTipo) {
        ConexionBD.crearStatement();
        lista.stream().filter(item -> item.getFlagCargar()).map((item) -> {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            String codigoGrupo = item.getTipo().getCodigo();
            String centroPadreCodigo = "xxx";
            int nivel = item.getNivel();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO CENTROS(CODIGO,NOMBRE,ESTA_ACTIVO,NIVEL,CENTRO_PADRE_CODIGO,CENTRO_TIPO_CODIGO,REPARTO_TIPO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s',%d,%d,'%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,1,nivel,centroPadreCodigo,codigoGrupo,repartoTipo,fechaStr,fechaStr);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
//    Verifica si esta siendo usado en las lineas
    public int verificarObjetoCentroLineas(String codigo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM centro_lineas\n" +
                " WHERE centro_codigo='%s'",
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
//    Verifica si esta siendo usado en las lineas
    public int verificarObjetoCentro(String codigo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM centros\n" +
                " WHERE codigo='%s'",
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
    
    // POR REVISAR
    public int verificarObjetoCentroPeriodoDriver(String codigo, int periodo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM driver_centro_lineas\n" +
                " WHERE centro_codigo='%s' AND periodo=%d",
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
    
    
    public int eliminarObjetoCentro(String codigo) {
        String queryStr = String.format("" +
                "DELETE FROM centros\n" +
                " WHERE codigo='%s'",
                codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public List<Centro> listarObjetos(int tipoReparto) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.esta_activo,\n" +
                "       A.nivel,\n" +
                "       A.centro_padre_codigo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion,\n" +
                "       B.codigo tipo_codigo,\n" +
                "       B.nombre tipo_nombre,\n" +
                "       B.descripcion tipo_descripcion\n" +
                "  FROM centros A\n" +
                "  JOIN centro_tipos B ON A.centro_tipo_codigo=B.codigo\n" +
                " WHERE A.reparto_tipo=%d\n" +
                " ORDER BY A.fecha_creacion",tipoReparto);
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                int estaActivo = rs.getInt("esta_activo");
                int nivel = rs.getInt("nivel");
                String centroPadreCodigo = rs.getString("centro_padre_codigo");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = 0;
                String tipoCodigo = rs.getString("tipo_codigo");
                String tipoNombre = rs.getString("tipo_nombre");
                String tipoDescripcion = rs.getString("tipo_descripcion");
                Tipo tipo = new Tipo(tipoCodigo,tipoNombre,tipoDescripcion);
                Centro item = new Centro(codigo, nombre, nivel, null, saldo, tipo, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public void insertarCentrosDeCostos(List<CargarCentroLinea> lista) throws SQLException {
        Connection access1 = connection.getConnection();
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
                //int periodo = lista.get(posI).getPeriodo();
                int periodo = 201801;
                String codigo = lista.get(posI).getCodigo();
                String nombre = lista.get(posI).getNombre();
                String codigoGrupo = lista.get(posI).getCodigoGrupo();
                int nivel = lista.get(posI).getNivel();
                
                query = String.format("SELECT COUNT(1) count FROM centros WHERE codigo='%s'",codigo);
                System.out.println(query);
                rs = ps1.executeQuery(query);
                rs.next();
                fecha = new Date();
                fechaStr = formatter.format(fecha);
                if (rs.getInt("count") == 1) {  // lo encontró, entonces lo actualizo
                    query = String.format("" +
                            "UPDATE centros\n" +
                            "   SET nombre='%s',descripcion=%s,esta_activo=%d,\n" +
                            "       nivel=%d,centro_padre_codigo=%s,\n" +
                            "       fecha_creacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                            " WHERE codigo='%s'",
                            nombre,null,1,nivel,null,fechaStr,fechaStr,codigo);
                    //System.out.println(query);
                    ps2.addBatch(query);
                } else { // no lo encontró, entonces lo inserto
                    query = String.format("" +
                            "INSERT INTO centros(codigo,nombre,descripcion,esta_activo,nivel,centro_padre_codigo,fecha_creacion,fecha_actualizacion)\n" +
                            "VALUES ('%s','%s',%s,%d,%d,%s,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                            codigo,nombre,null,1,nivel,null,fechaStr,fechaStr);
                    //System.out.println(query);
                    ps2.addBatch(query);
                }
                // inserto una linea dummy correspondiente al periodo
                query = String.format("" +
                        "DELETE FROM centro_tipo\n" +
                        " WHERE centro_codigo='%s' AND periodo=%d",
                        codigo,periodo);
                //System.out.println(query);
                ps3.addBatch(query);
                
                query = String.format("" +
                        "INSERT INTO centro_tipo(centro_codigo,centro_tipo_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
                        "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                        codigo,codigoGrupo,periodo,fechaStr,fechaStr);
                //System.out.println(query);
                ps3.addBatch(query);
                
                query = String.format("" +
                        "DELETE FROM centro_lineas\n" +
                        " WHERE centro_codigo='%s' AND periodo=%d",
                        codigo, periodo);
                //System.out.println(query);
                ps3.addBatch(query);
                
                query = String.format("" +
                        "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,fecha_creacion,fecha_actualizacion)\n" +
                        "VALUES('%s',%d,%d,%d,'%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                        codigo,periodo,-1,0,'0',fechaStr,fechaStr);
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
        }
    }
    
    public List<Centro> listarCentrosNombresObjeto(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
            "SELECT A.CODIGO,\n" +
            "       A.NOMBRE\n" +
            "  FROM CENTROS A\n" +
            "  JOIN CENTRO_LINEAS B ON B.PERIODO=%d AND B.ITERACION=-1 AND A.CODIGO=B.CENTRO_CODIGO\n" +
            " WHERE A.ESTA_ACTIVO=1 AND A.REPARTO_TIPO=%d AND A.CENTRO_TIPO_CODIGO!='A'\n" +
            " ORDER BY A.CODIGO",
            periodo,repartoTipo);
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                Centro centro = new Centro(codigo,nombre,0,null,0,null,null,null);
                lista.add(centro);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Centro> listarCentrosNombresConDriver(int periodo, String tipo, int repartoTipo, int nivel) {
        String queryStr = String.format("" +
            "SELECT A.codigo,\n" +
            "       A.nombre,\n" +
            "       SUM(B.saldo) saldo,\n" +
            "       COALESCE(C.driver_codigo,'Sin driver asignado') driver_codigo,\n" +
            "       COALESCE(D.nombre,'Sin driver asignado') driver_nombre\n" +
            "  FROM centros A\n" +
            "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
            "  LEFT JOIN entidad_origen_driver C ON A.codigo=C.entidad_origen_codigo AND B.periodo=C.periodo\n" +
            "  LEFT JOIN drivers D ON C.driver_codigo=D.codigo\n" +
            " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d\n",
            periodo,repartoTipo);
        if (!tipo.equals("-")) queryStr += String.format("   AND A.centro_tipo_codigo='%s'\n",tipo);
        if (nivel!=-1) queryStr += String.format("   AND A.nivel=%d\n",nivel);
        queryStr += " GROUP BY A.codigo,A.nombre,C.driver_codigo,D.nombre\n" +
                    " ORDER BY A.codigo";
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                double saldo = rs.getDouble("saldo");
                String driverCodigo = rs.getString("driver_codigo");
                String driverNombre = rs.getString("driver_nombre");
                Centro centro = new Centro(codigo,nombre,0,null,saldo,null,null,null);
                centro.setDriver(new Driver(driverCodigo,driverNombre,null,null,null,null));
                lista.add(centro);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public int numeroCentrosCascada(int periodo, int repartoTipo) {
        int cnt = 0;
        String queryStr = String.format("" +
                "SELECT COUNT(1) cnt\n" +
                "  FROM centros A\n" +
                "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
                " WHERE B.periodo=%d AND B.iteracion=-1\n" +
                "   AND A.nivel!=0 AND A.reparto_tipo=%d",
                periodo, repartoTipo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                cnt = rs.getInt("cnt");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }
    
    public int borrarDistribuciones(int periodo, int iteracion, int repartoTipo) {
        String queryStr = String.format("" +
                "DELETE FROM centro_lineas A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM centros B\n" +
                "                WHERE A.centro_codigo=B.codigo\n" +
                "                  AND A.periodo=%d AND A.iteracion>=%d\n" +
                "                  AND B.reparto_tipo=%d)",
                periodo,iteracion,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarDistribucion(String centroCodigo, int periodo, int iteracion, double saldo, String entidadOrigenCodigo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,%d,%.8f,'%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                centroCodigo, periodo, iteracion, saldo, entidadOrigenCodigo, fechaStr, fechaStr);
        ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarDistribucionBatch(String centroCodigo, int periodo, int iteracion, double saldo, String entidadOrigenCodigo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,%d,%.8f,'%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                centroCodigo, periodo, iteracion, saldo, entidadOrigenCodigo, fechaStr, fechaStr);
        ConexionBD.agregarBatch(queryStr);
    }
}
