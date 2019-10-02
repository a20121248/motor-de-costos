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
import modelo.CentroDriver;
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
    
    public int cantCentrosSinDriver(int repartoTipo, String operador, int nivel, int periodo) {
        String queryStr = String.format("" +
            "SELECT COUNT(1) cnt\n" +
            "  FROM centros A\n" +
            "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
            "  LEFT JOIN entidad_origen_driver C ON A.codigo=C.entidad_origen_codigo AND B.periodo=C.periodo\n" +
            " WHERE A.reparto_tipo=%d AND A.nivel%s%d\n" +
            "   AND B.periodo=%d\n" +
            "   AND C.entidad_origen_codigo IS NULL\n" +
            "   AND B.iteracion =-2",repartoTipo,operador,nivel,periodo);
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
    
    public int cantCentrosObjetosSinDriver(int repartoTipo, String operador, int nivel, int periodo) {
        String queryStr = String.format("" +
            "SELECT COUNT(1) cnt\n" +
            "  FROM centros A\n" +
            "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
            "  LEFT JOIN entidad_origen_driver C ON A.codigo=C.entidad_origen_codigo AND B.periodo=C.periodo\n" +
            " WHERE A.reparto_tipo=%d AND A.nivel%s%d\n" +
            "   AND B.periodo=%d\n" +
            "   AND C.entidad_origen_codigo IS NULL\n" +
            "   AND B.iteracion =-2",repartoTipo,operador,nivel,periodo);
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
        String queryStr = String.format("SELECT codigo FROM MS_centros");
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
    
    public List<String> listarCodigosPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT centro_codigo\n" +
                "  FROM ms_centro_lineas\n" +
                " WHERE periodo=%d\n" +
                "   AND reparto_tipo=%d", periodo, repartoTipo);
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                lista.add(rs.getString("centro_codigo"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosCentrosBolsasPeriodo(int periodo) {
        String queryStr = String.format(""
                + "SELECT A.centro_codigo "
                + "  FROM centro_lineas A"
                + "  JOIN centros B ON B.codigo = A.centro_codigo"
                + " WHERE A.periodo = '%d' and B.es_bolsa = 'SI'",periodo);
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                lista.add(rs.getString("centro_codigo"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Centro> listarMaestro(String codigos, int repartoTipo) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("SELECT codigo,nombre FROM MS_centros WHERE esta_activo=1  ORDER BY codigo");
        } else {
            queryStr = String.format(""+
                    "SELECT codigo,nombre\n" +
                    "  FROM MS_centros\n" +
                    " WHERE esta_activo=1 AND codigo NOT IN (%s) \n" +
                    " ORDER BY codigo",
                    codigos);
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
    
    public int obtenerNivelCentro(String codigoCentro){
        String queryStr = String.format("Select nivel from centros where codigo='%s'", codigoCentro);
        int nivel = -9999;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                nivel = rs.getInt("nivel");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nivel;
    }
    
    public int eliminarObjetoPeriodo(String codigo, int periodo, int repartoTipo) {
        String queryStr = String.format("DELETE FROM MS_centro_lineas WHERE centro_codigo='%s' AND periodo=%d AND reparto_tipo='%d'",codigo,periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }

    public int actualizarObjeto(String codigo, String nombre, String cecoTipoCodigo, int nivel, String cecoPadreCodigo, String esBolsa, int tipoGasto, String niif17Atribuible, String niif17Tipo, String niif17Clase) {
        niif17Atribuible = convertirPalabraAbreviatura(niif17Atribuible);
        niif17Tipo = convertirPalabraAbreviatura(niif17Tipo);
        niif17Clase = convertirPalabraAbreviatura(niif17Clase);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "UPDATE MS_centros\n" +
                "   SET nombre='%s',centro_tipo_codigo='%s',nivel=%d,centro_padre_codigo='%s',es_bolsa='%s',tipo_gasto='%d',niif17_atribuible='%s',niif17_tipo='%s',niif17_clase='%s',fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                " WHERE codigo='%s'",
                nombre,cecoTipoCodigo,nivel,cecoPadreCodigo,esBolsa,tipoGasto,niif17Atribuible, niif17Tipo, niif17Clase,fechaStr,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjeto(String codigo, String nombre, String cecoTipoCodigo, int nivel, String cecoPadreCodigo, int repartoTipo, String es_bolsa, int tipoGasto, String niif17Atribuible, String niif17Tipo, String niif17Clase ) {
        niif17Atribuible = convertirPalabraAbreviatura(niif17Atribuible);
        niif17Tipo = convertirPalabraAbreviatura(niif17Tipo);
        niif17Clase = convertirPalabraAbreviatura(niif17Clase);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO MS_centros(codigo,nombre,esta_activo,centro_tipo_codigo,nivel,centro_padre_codigo,reparto_tipo, es_bolsa, tipo_gasto,niif17_atribuible, niif17_tipo, niif17_clase, fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,'%s',%d,'%s',%d,'%s','%d','%s','%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,1,cecoTipoCodigo,nivel,cecoPadreCodigo,0,es_bolsa,tipoGasto, niif17Atribuible, niif17Tipo, niif17Clase, fechaStr, fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjetoPeriodo(String codigo, int periodo, int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format("" +
                "INSERT INTO MS_centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,grupo_gasto,CUENTA_CONTABLE_ORIGEN_CODIGO, PARTIDA_ORIGEN_CODIGO, CENTRO_ORIGEN_CODIGO, reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,%d,%d,'%s','%s','%s','%s','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,periodo,-2,0,"0","-","-","-","-",repartoTipo,fechaStr,fechaStr);
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
                "DELETE FROM MS_centro_lineas A\n" +
                " WHERE EXISTS (SELECT 1\n" +
                "                 FROM MS_centros B\n" +
                "                WHERE A.centro_codigo=B.codigo\n" +
                "                  AND A.periodo=%d\n" +
                "                  AND A.reparto_tipo=%d)",
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
                    "INSERT INTO MS_centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,grupo_gasto,CUENTA_CONTABLE_ORIGEN_CODIGO, PARTIDA_ORIGEN_CODIGO, CENTRO_ORIGEN_CODIGO,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES('%s',%d,%d,%d,'%s','%s','%s','%s','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,periodo,-2,0,"0","-","-","-","-",repartoTipo,fechaStr,fechaStr);
            ConexionBD.agregarBatch(queryStr);
        }
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public List<Centro> listar(String codigos,int periodo, int repartoTipo) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("" +
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
                "  FROM MS_centros A\n" +
                "  JOIN MS_centro_tipos B ON A.centro_tipo_codigo=B.codigo\n" +
                "  JOIN MS_centro_lineas C ON A.codigo=C.centro_codigo\n" +
                " WHERE C.periodo=%d AND C.reparto_tipo='%d' and A.nivel !=0\n" +
                " GROUP BY A.codigo,A.nombre,A.esta_activo,A.nivel,A.centro_padre_codigo," +
                "          B.codigo,B.nombre,B.descripcion,A.fecha_creacion,A.fecha_actualizacion",
                periodo,repartoTipo);
        } else {
            queryStr = String.format("" +
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
                "  FROM MS_centros A\n" +
                "  JOIN MS_centro_tipos B ON A.centro_tipo_codigo=B.codigo\n" +
                "  JOIN MS_centro_lineas C ON A.codigo=C.centro_codigo\n" +
                " WHERE C.periodo=%d AND C.reparto_tipo='%d' AND A.nivel !=0 AND A.codigo NOT IN (%s)\n" +
                " GROUP BY A.codigo,A.nombre,A.esta_activo,A.nivel,A.centro_padre_codigo," +
                "          B.codigo,B.nombre,B.descripcion,A.fecha_creacion,A.fecha_actualizacion",
                periodo,repartoTipo,codigos);
        }
        
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
    public List<Centro> listarPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       B.codigo tipo_codigo,\n" +
                "       B.nombre tipo_nombre,\n" +
                "       SUM(COALESCE(C.saldo,0)) saldo\n" +
                "  FROM MS_centros A\n" +
                "  JOIN MS_centro_tipos B ON A.centro_tipo_codigo=B.codigo\n" +
                "  JOIN MS_centro_lineas C ON A.codigo=C.centro_codigo\n" +
                " WHERE C.periodo=%d AND C.reparto_tipo=%d\n AND (c.iteracion = -1 OR C.iteracion = -2)" +
                " GROUP BY A.codigo,A.nombre," +
                "          B.codigo,B.nombre",
                periodo,repartoTipo);
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String tipoCodigo = rs.getString("tipo_codigo");
                String tipoNombre = rs.getString("tipo_nombre");
                double saldo = rs.getDouble("saldo");
                
                Tipo tipo = new Tipo(tipoCodigo, tipoNombre, "");
                Centro item = new Centro(codigo, nombre, saldo, tipo);
                lista.add(item);
            }
        } catch (SQLException ex) {
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
            String centroPadreCodigo = "-";
            int nivel = item.getNivel();
            String esBolsa = item.getEsBolsa();
            String strTipoGasto = item.getTipoGasto();
            String niif17Atribuible = item.getNIIF17Atribuible();
            String niif17Tipo = item.getNIIF17Tipo();
            String niif17Clase = item.getNIIF17Clase();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            int tipoGasto;
            
            niif17Atribuible = convertirPalabraAbreviatura(niif17Atribuible);
            niif17Tipo = convertirPalabraAbreviatura(niif17Tipo);
            niif17Clase = convertirPalabraAbreviatura(niif17Clase);
            if(strTipoGasto.equals("DIRECTO")) tipoGasto = 1;
            else tipoGasto = 0;
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO MS_CENTROS(CODIGO,NOMBRE,ESTA_ACTIVO,NIVEL,CENTRO_PADRE_CODIGO,CENTRO_TIPO_CODIGO,REPARTO_TIPO, es_bolsa, tipo_gasto, niif17_atribuible, niif17_tipo, niif17_clase,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s',%d,%d,'%s','%s',%d,'%s','%d','%s','%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,1,nivel,centroPadreCodigo,codigoGrupo,0,esBolsa, tipoGasto, niif17Atribuible, niif17Tipo, niif17Clase,fechaStr,fechaStr);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
//    Verifica si esta siendo usado en las lineas
    public int verificarObjetoEnDetalleGasto(String codigo, int periodo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM MS_cuenta_partida_centro\n" +
                " WHERE centro_codigo='%s' AND periodo = '%s'",
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
    public int verificarObjetoCentroLineas(String codigo) {
        String queryStr = String.format("" +
                "SELECT count(*) as COUNT\n"+
                "  FROM MS_centro_lineas\n" +
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
//    Verifica si esta siendo usado en el maestro de Centro
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
                "DELETE FROM MS_centros\n" +
                " WHERE codigo='%s'",
                codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public List<Centro> listarObjetos(int tipoReparto) {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.nivel,\n" +
                "       A.centro_padre_codigo,\n" +
                "       A.es_bolsa,\n" +
                "       A.niif17_atribuible,\n" +
                "       A.niif17_tipo,\n" +
                "       A.niif17_clase,\n" +
                "       CASE WHEN A.tipo_gasto=0 then 'INDIRECTO'\n" +
                "            WHEN A.tipo_gasto=1 then 'DIRECTO'\n" +
                "       END tipo_gasto,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion,\n" +
                "       B.codigo tipo_codigo,\n" +
                "       B.nombre tipo_nombre,\n" +
                "       B.descripcion tipo_descripcion\n" +
                "  FROM MS_centros A\n" +
                "  JOIN MS_centro_tipos B ON A.centro_tipo_codigo=B.codigo\n" +
                " ORDER BY A.codigo",tipoReparto);
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                int nivel = rs.getInt("nivel");
                String centroPadreCodigo = rs.getString("centro_padre_codigo");
                String esBolsa = rs.getString("es_bolsa");
                String tipoGasto = rs.getString("tipo_gasto");
                String niif17_atribuible = rs.getString("niif17_atribuible");
                String niif17_tipo = rs.getString("niif17_tipo");
                String niif17_clase = rs.getString("niif17_clase");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = 0;
                String tipoCodigo = rs.getString("tipo_codigo");
                String tipoNombre = rs.getString("tipo_nombre");
                String tipoDescripcion = rs.getString("tipo_descripcion");
                Tipo tipo = new Tipo(tipoCodigo,tipoNombre,tipoDescripcion);
                
                niif17_atribuible = convertirAbreviaturaPalabra(niif17_atribuible);
                niif17_tipo = convertirAbreviaturaPalabra(niif17_tipo);
                niif17_clase = convertirAbreviaturaPalabra(niif17_clase);
                
                Centro item = new Centro(codigo, nombre, nivel, null, saldo, tipo, esBolsa, niif17_atribuible, niif17_tipo, niif17_clase, tipoGasto, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
//    public void insertarCentrosDeCostos(List<CargarCentroLinea> lista) throws SQLException {
//        Connection access1 = connection.getConnection();
//        Connection access2 = connection.getConnection();
//        Connection access3 = connection.getConnection();
//        access1.setAutoCommit(false);
//        access2.setAutoCommit(false);
//        access3.setAutoCommit(false);
//        Statement ps1 = null;
//        Statement ps2 = null;
//        Statement ps3 = null;
//        Date fecha = null;
//        Format formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        int posI = -1;
//        ResultSet rs;
//        String query=null, fechaStr=null;
//        int numOper = -1;
//        int batchSize = 1;
//        int result[] = null;
//        try {
//            ps1 = access1.createStatement();
//            ps2 = access2.createStatement();
//            ps3 = access3.createStatement();
//            numOper = 0;
//            for (posI = 0; posI < lista.size(); ++posI) {
//                //int periodo = lista.get(posI).getPeriodo();
//                int periodo = 201801;
//                String codigo = lista.get(posI).getCodigo();
//                String nombre = lista.get(posI).getNombre();
//                String codigoGrupo = lista.get(posI).getCodigoGrupo();
//                int nivel = lista.get(posI).getNivel();
//                
//                query = String.format("SELECT COUNT(1) count FROM centros WHERE codigo='%s'",codigo);
//                System.out.println(query);
//                rs = ps1.executeQuery(query);
//                rs.next();
//                fecha = new Date();
//                fechaStr = formatter.format(fecha);
//                if (rs.getInt("count") == 1) {  // lo encontró, entonces lo actualizo
//                    query = String.format("" +
//                            "UPDATE centros\n" +
//                            "   SET nombre='%s',descripcion=%s,esta_activo=%d,\n" +
//                            "       nivel=%d,centro_padre_codigo=%s,\n" +
//                            "       fecha_creacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
//                            " WHERE codigo='%s'",
//                            nombre,null,1,nivel,null,fechaStr,fechaStr,codigo);
//                    //System.out.println(query);
//                    ps2.addBatch(query);
//                } else { // no lo encontró, entonces lo inserto
//                    query = String.format("" +
//                            "INSERT INTO centros(codigo,nombre,descripcion,esta_activo,nivel,centro_padre_codigo,fecha_creacion,fecha_actualizacion)\n" +
//                            "VALUES ('%s','%s',%s,%d,%d,%s,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
//                            codigo,nombre,null,1,nivel,null,fechaStr,fechaStr);
//                    //System.out.println(query);
//                    ps2.addBatch(query);
//                }
//                // inserto una linea dummy correspondiente al periodo
//                query = String.format("" +
//                        "DELETE FROM centro_tipo\n" +
//                        " WHERE centro_codigo='%s' AND periodo=%d",
//                        codigo,periodo);
//                //System.out.println(query);
//                ps3.addBatch(query);
//                
//                query = String.format("" +
//                        "INSERT INTO centro_tipo(centro_codigo,centro_tipo_codigo,periodo,fecha_creacion,fecha_actualizacion)\n" +
//                        "VALUES ('%s','%s',%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
//                        codigo,codigoGrupo,periodo,fechaStr,fechaStr);
//                //System.out.println(query);
//                ps3.addBatch(query);
//                
//                query = String.format("" +
//                        "DELETE FROM centro_lineas\n" +
//                        " WHERE centro_codigo='%s' AND periodo=%d",
//                        codigo, periodo);
//                //System.out.println(query);
//                ps3.addBatch(query);
//                
//                query = String.format("" +
//                        "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,fecha_creacion,fecha_actualizacion)\n" +
//                        "VALUES('%s',%d,%d,%d,'%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
//                        codigo,periodo,-1,0,'0',fechaStr,fechaStr);
//                //System.out.println(query);
//                ps3.addBatch(query);
//                
//                ++numOper;
//                if(numOper % batchSize == 0) {
//                    result = ps2.executeBatch();
//                    access2.commit();
//                    result = ps3.executeBatch();
//                    access3.commit();
//                }
//            }            
//        } catch (BatchUpdateException b) {
//            access2.rollback();
//            access3.rollback();
//        } catch (SQLException sqlEx) {
//            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
//            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
//            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
//            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
    public List<CentroDriver> listarCuentaPartidaCentroBolsaConDriver(int periodo, String tipo, int repartoTipo, int nivel, String esBolsa) {
        String queryStr = String.format("" +
            "SELECT E.cuenta_contable_codigo cuenta_contable_codigo,\n" +
            "       F.nombre cuenta_contable_nombre,\n" +
            "       E.partida_codigo partida_codigo,\n" +
            "       G.nombre partida_nombre,\n" +
            "       A.codigo centro_codigo,\n" +
            "       A.nombre centro_nombre,\n" +
//            "       SUM(E.saldo) saldo,\n" +
            "       COALESCE(C.driver_codigo,'Sin driver asignado') driver_codigo,\n" +
            "       COALESCE(D.nombre,'Sin driver asignado') driver_nombre\n" +
            "  FROM centros A\n" +
            "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
            "  JOIN partida_cuenta_contable E ON E.es_bolsa = 'SI'\n" +
            "  JOIN plan_de_cuentas F on F.codigo = E.cuenta_contable_codigo\n" +
            "  JOIN partidas G on G.codigo = E.partida_codigo and B.periodo=E.periodo\n" +
            "  LEFT JOIN bolsa_driver C ON A.codigo=C.centro_codigo AND E.periodo=C.periodo AND e.partida_codigo = C.partida_codigo\n" +
            "  LEFT JOIN drivers D ON C.driver_codigo=D.codigo\n" +
            " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d AND a.es_bolsa = '%s'\n",
            periodo,repartoTipo,esBolsa);
        if (!tipo.equals("-")) queryStr += String.format("   AND A.centro_tipo_codigo='%s'\n",tipo);
        if (nivel!=-1) queryStr += String.format("   AND A.nivel=%d\n",nivel);
        queryStr += " GROUP BY E.cuenta_contable_codigo,F.nombre, E.partida_codigo,G.nombre, A.codigo,A.nombre,C.driver_codigo,D.nombre\n" +
                    " ORDER BY E.cuenta_contable_codigo,E.partida_codigo,A.codigo";
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCuenta = rs.getString("cuenta_contable_codigo");
                String nombreCuenta = rs.getString("cuenta_contable_nombre");
                String codigoPartida = rs.getString("partida_codigo");
                String nombrePartida = rs.getString("partida_nombre");
                String codigoCentro = rs.getString("centro_codigo");
                String nombreCentro = rs.getString("centro_nombre");
//                double saldo = rs.getDouble("saldo");
                String driverCodigo = rs.getString("driver_codigo");
                String driverNombre = rs.getString("driver_nombre");
                CentroDriver item = new CentroDriver(periodo, codigoCuenta, nombreCuenta, codigoPartida, nombrePartida, codigoCentro, nombreCentro, driverCodigo, driverNombre);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    //Por evaluar
    public List<CentroDriver> listarCuentaPartidaCentroBolsaConDriverDirecta(int periodo) {
        String queryStr = String.format("" +
            "SELECT  a.cuenta_contable_codigo cuenta_contable_codigo,\n" +
            "        B.NOMBRE cuenta_contable_nombre,\n" +
            "        A.PARTIDA_CODIGO partida_codigo,\n" +
            "        C.NOMBRE partida_nombre,\n" +
            "        A.CENTRO_CODIGO centro_codigo,\n" +
            "        D.NOMBRE centro_nombre,\n" +
            "        A.DRIVER_CODIGO driver_codigo,\n" +
            "        E.NOMBRE driver_nombre\n" +
            "FROM bolsa_driver A\n" +
            "JOIN plan_de_cuentas B ON B.CODIGO = A.CUENTA_CONTABLE_CODIGO\n" +
            "JOIN PARTIDAS C ON C.CODIGO = A.PARTIDA_CODIGO\n" +
            "JOIN CENTROS D ON  D.CODIGO = A.CENTRO_CODIGO\n" +
            "JOIN DRIVERS E ON E.CODIGO = A.DRIVER_CODIGO\n" +
            "WHERE A.PERIODO = %d",
            periodo);
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCuenta = rs.getString("cuenta_contable_codigo");
                String nombreCuenta = rs.getString("cuenta_contable_nombre");
                String codigoPartida = rs.getString("partida_codigo");
                String nombrePartida = rs.getString("partida_nombre");
                String codigoCentro = rs.getString("centro_codigo");
                String nombreCentro = rs.getString("centro_nombre");
//                double saldo = rs.getDouble("saldo");
                String driverCodigo = rs.getString("driver_codigo");
                String driverNombre = rs.getString("driver_nombre");
                CentroDriver item = new CentroDriver(periodo, codigoCuenta, nombreCuenta, codigoPartida, nombrePartida, codigoCentro, nombreCentro, driverCodigo, driverNombre);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<CentroDriver> listarCuentaPartidaCentroBolsaConDriverDistribuir(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
            "SELECT C.cuenta_contable_codigo cuenta_contable_codigo,\n" +
            "       C.partida_codigo partida_codigo,\n" +
            "       C.centro_codigo centro_codigo,\n" +
            "       NVL(B.saldo,0) saldo,\n" +
            "       C.driver_codigo driver_codigo,\n" +
            "       B.grupo_gasto grupo_gasto\n" +
            "  FROM centros A\n" +
            "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
            "  LEFT JOIN bolsa_driver C ON A.codigo=C.centro_codigo AND B.periodo=C.periodo\n" +
            "  LEFT JOIN drivers D ON C.driver_codigo=D.codigo\n" +
            "  JOIN plan_de_cuentas F on F.codigo = C.cuenta_contable_codigo\n" +
            "  JOIN partidas G on  G.codigo = C.partida_codigo AND g.grupo_gasto = b.grupo_gasto\n" +
            " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d AND a.es_bolsa = 'SI'\n",
            periodo,repartoTipo);
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCuenta = rs.getString("cuenta_contable_codigo");
                String codigoPartida = rs.getString("partida_codigo");
                String codigoCentro = rs.getString("centro_codigo");
                double saldo = rs.getDouble("saldo");
                String driverCodigo = rs.getString("driver_codigo");
                String grupoGasto = rs.getString("grupo_gasto");
                CentroDriver item = new CentroDriver(periodo, codigoCuenta, codigoPartida, codigoCentro, driverCodigo,saldo,new Tipo(grupoGasto,""));
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    
    public int enumerarListaCentroBolsaSinDriver(int periodo, int repartoTipo){
        int count = 0;
        String queryStr = String.format("" +
            "SELECT count(1) COUNT\n" +
            "  FROM centros A\n" +
            "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
            "  JOIN partida_cuenta_contable E ON E.es_bolsa = 'SI'\n" +
            "  JOIN plan_de_cuentas F on F.codigo = E.cuenta_contable_codigo\n" +
            "  JOIN partidas G on G.codigo = E.partida_codigo and B.periodo=E.periodo\n" +
            "  LEFT JOIN bolsa_driver C ON A.codigo=C.centro_codigo AND E.periodo=C.periodo AND e.partida_codigo = C.partida_codigo\n" +
            "  LEFT JOIN drivers D ON C.driver_codigo=D.codigo\n" +
            " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d AND a.es_bolsa = 'SI' AND c.driver_codigo IS NULL AND b.iteracion = -2\n",
            periodo,repartoTipo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                count = rs.getInt("COUNT");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    public List<CentroDriver> listarCentrosNombresConDriver(int periodo, String tipo, int repartoTipo, int nivel) {
        String queryStr = String.format(""+
                "select a.centro_codigo centro_codigo,\n" +
                "        SUM(A.saldo) saldo,\n" +
                "        a.grupo_gasto grupo_gasto,\n" +
                "        c.driver_codigo driver_codigo \n" +
                "from centro_lineas A \n" +
                "join centros B on b.codigo = a.centro_codigo\n" +
                "join entidad_origen_driver C on c.entidad_origen_codigo = a.centro_codigo and a.periodo = c.periodo\n" +
                "join drivers D on d.codigo = c.driver_codigo\n" +
                "where A. periodo = %d and (a.iteracion >=0 or (a.iteracion =-1 and b.es_bolsa = 'NO')) \n",
                periodo);
        if (!tipo.equals("-")) queryStr += String.format("   AND b.centro_tipo_codigo='%s'\n",tipo);
        if (nivel>0) queryStr += String.format("   AND b.nivel=%d\n",nivel);
        queryStr += " group by a.centro_codigo,a.grupo_gasto, c.driver_codigo\n" +
                    " order by a.centro_codigo";
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String centroCodigo = rs.getString("centro_codigo");
                double saldo = rs.getDouble("saldo");
                String grupoGasto = rs.getString("grupo_gasto");
                String driverCodigo = rs.getString("driver_codigo");
                
                CentroDriver centroDriver = new CentroDriver(periodo, centroCodigo,driverCodigo,saldo,new Tipo(grupoGasto,""));
                lista.add(centroDriver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<CentroDriver> listarCentrosObjetosNombresConDriver(int periodo, int repartoTipo) {
        String queryStr = String.format(""+
                "select a.centro_codigo centro_codigo,\n" +
                "        SUM(A.saldo) saldo,\n" +
                "        a.grupo_gasto grupo_gasto,\n" +
                "        c.driver_codigo driver_codigo\n" +
                "from centro_lineas A\n" +
                "join centros B on b.codigo = a.centro_codigo\n" +
                "join objeto_driver C on c.centro_codigo = a.centro_codigo and A.grupo_gasto = c.grupo_gasto and A.periodo = c.periodo\n" +
                "join drivers D on d.codigo = c.driver_codigo\n" +
                "where A. periodo = %d and a.iteracion >=-1\n" +
                "group by a.centro_codigo,a.grupo_gasto, c.driver_codigo\n" +
                "order by a.centro_codigo",
                periodo);
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String centroCodigo = rs.getString("centro_codigo");
                double saldo = rs.getDouble("saldo");
                String grupoGasto = rs.getString("grupo_gasto");
                String driverCodigo = rs.getString("driver_codigo");
                
                CentroDriver centroDriver = new CentroDriver(periodo, centroCodigo,driverCodigo,saldo,new Tipo(grupoGasto,""));
                lista.add(centroDriver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<CentroDriver> listarCentrosConDriver(int periodo, String tipo, int repartoTipo, int nivel) {
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
            " WHERE A.esta_activo=1 AND B.periodo=%d AND A.reparto_tipo=%d AND A.es_bolsa='NO'\n",
            periodo,repartoTipo);
        if (tipo.equals("-")) queryStr += "AND (A.centro_tipo_codigo = 'A' OR A.centro_tipo_codigo = 'B')\n";
        else if (!tipo.equals("-")) queryStr += String.format("   AND A.centro_tipo_codigo='%s'\n",tipo);
        if (nivel!=-1) queryStr += String.format("   AND A.nivel=%d\n",nivel);
        queryStr += " GROUP BY A.codigo,A.nombre,C.driver_codigo,D.nombre\n" +
                    " ORDER BY A.codigo";
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                double saldo = rs.getDouble("saldo");
                String driverCodigo = rs.getString("driver_codigo");
                String driverNombre = rs.getString("driver_nombre");
                
                CentroDriver centroDriver = new CentroDriver(periodo, codigo, nombre, driverCodigo, driverNombre);
                lista.add(centroDriver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<CentroDriver> listarCentrosObjetosConDriver(int periodo, String tipo, int repartoTipo, int nivel) {
        List<Tipo> listaGrupoGasto = tipoDAO.listarGrupoGastos();
        String queryStr = String.format("" +
            "SELECT A.centro_codigo centro_codigo,\n" +
            "       B.nombre centro_nombre,\n" +
            "       C.codigo grupo_gastos,\n" +
            "       COALESCE(D.driver_codigo,'Sin driver asignado') driver_codigo,\n" +
            "       COALESCE(E.nombre,'Sin driver asignado') driver_nombre\n" +
            "  FROM centro_lineas A\n" +
            "  JOIN centros B ON  B.codigo = A.centro_codigo\n"+
            "  JOIN grupo_gastos C ON 1=1\n" +
            "  LEFT JOIN objeto_driver D ON D.centro_codigo = A.centro_codigo AND d.periodo=a.periodo AND D.grupo_gasto = c.codigo\n" +
            "  LEFT JOIN drivers E ON e.codigo = d.driver_codigo\n" +
            " WHERE A.periodo =%d AND A.iteracion = -2\n",
            periodo);
        if (tipo.equals("-")) queryStr += "AND (B.centro_tipo_codigo = 'D' OR B.centro_tipo_codigo = 'E' OR B.centro_tipo_codigo = 'F')\n";
        else if (!tipo.equals("-")) queryStr += String.format("   AND B.centro_tipo_codigo='%s'\n",tipo);
        if (nivel!=-1) queryStr += String.format("   AND A.nivel=%d\n",nivel);
        queryStr += " ORDER BY A.centro_codigo";
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigoCentro = rs.getString("centro_codigo");
                String nombreCentro = rs.getString("centro_nombre");
                String grupoGasto = rs.getString("grupo_gastos");
                String codigoDriver = rs.getString("driver_codigo");
                String nombreDriver = rs.getString("driver_nombre");
                Tipo tipoGrupoGasto = listaGrupoGasto.stream().filter(item ->grupoGasto.equals(item.getCodigo())).findAny().orElse(null);       
                CentroDriver centroDriver = new CentroDriver(periodo, codigoCentro, nombreCentro, tipoGrupoGasto, codigoDriver, nombreDriver);
                lista.add(centroDriver);
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
                " WHERE B.periodo=%d AND B.iteracion=-2\n" +
                "   AND A.nivel>0 AND A.reparto_tipo=%d",
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
    
    public int maxNivelCascada(int periodo, int repartoTipo) {
        int cnt = 0;
        String queryStr = String.format("" +
                "SELECT max(A.Nivel) cnt\n" +
                "  FROM centros A\n" +
                "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
                " WHERE B.periodo=%d AND B.iteracion=-2\n" +
                "   AND A.nivel>0 AND A.reparto_tipo=%d",
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
    
    public void insertarDistribucionBatchConGrupoGasto(String centroCodigo, int periodo, int iteracion, double saldo, String entidadOrigenCodigo, String grupoGasto) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,grupo_gasto,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,%d,%.8f,'%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                centroCodigo, periodo, iteracion, saldo, entidadOrigenCodigo, grupoGasto, fechaStr, fechaStr);
        ConexionBD.agregarBatch(queryStr);
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
