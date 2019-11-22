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
import modelo.CentroDriver;
import modelo.CargarObjetoPeriodoLinea;
import modelo.ConnectionDB;
import modelo.Centro;
import modelo.Tipo;

public class CentroDAO {
    ConnectionDB connection;
    TipoDAO tipoDAO;

    public CentroDAO() {
        this.connection = new ConnectionDB();
        this.tipoDAO = new TipoDAO();
    }
    
    public int cantCentrosSinDriver(int repartoTipo, int periodo) {
        periodo = repartoTipo == 1 ? periodo : (int)periodo/100 * 100;
        String queryStr = String.format("" +
            "SELECT COUNT(*) CNT\n" +
            "  FROM MS_CENTRO_LINEAS A\n" +
            "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n" +
            "  LEFT JOIN MS_ENTIDAD_ORIGEN_DRIVER C\n" +
            "    ON A.REPARTO_TIPO=C.REPARTO_TIPO\n" +
            "   AND C.ENTIDAD_ORIGEN_CODIGO=A.CENTRO_CODIGO\n" +
            "   AND C.PERIODO=A.PERIODO\n" +
            "  LEFT JOIN MS_DRIVERS D ON C.DRIVER_CODIGO=D.CODIGO\n" +
            " WHERE A.PERIODO=%d\n" +
            "   AND A.ITERACION=-2\n" +
            "   AND A.REPARTO_TIPO=%d\n" +
            "   AND B.TIPO IN ('STAFF','SOPORTE') AND C.DRIVER_CODIGO IS NULL",
                periodo,repartoTipo);
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
    
    public int cantCentrosObjetosSinDriver(int repartoTipo, int periodo) {
        periodo = repartoTipo == 1 ? periodo : (int)periodo/100 * 100;
        String queryStr = String.format("" +
            "SELECT COUNT(1) CNT\n" +
            "  FROM MS_CENTRO_LINEAS A\n" +
            "  JOIN MS_CENTROS B\n" +
            "    ON B.CODIGO=A.CENTRO_CODIGO\n" +
            "  JOIN MS_GRUPO_GASTOS C\n" +
            "    ON 1=1\n" +
            "  LEFT JOIN MS_OBJETO_DRIVER D\n" +
            "    ON A.REPARTO_TIPO=D.REPARTO_TIPO\n" +
            "   AND D.PERIODO=A.PERIODO\n" +
            "   AND D.CENTRO_CODIGO=A.CENTRO_CODIGO\n" +
            "   AND D.GRUPO_GASTO=C.CODIGO\n" +
            "  LEFT JOIN MS_DRIVERS E\n" +
            "    ON E.CODIGO=D.DRIVER_CODIGO\n" +
            " WHERE A.PERIODO=%d\n" +
            "   AND A.ITERACION=-2\n" +
            "   AND A.REPARTO_TIPO=%d\n" +
            "   AND B.TIPO NOT IN ('BOLSA','OFICINA','STAFF','SOPORTE')\n" +
            "   AND D.DRIVER_CODIGO IS NULL",
        periodo, repartoTipo);
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
    
    public List<String> listarCodigosCentrosObjetosPeriodo(int periodo, int repartoTipo) {
        periodo = repartoTipo == 1? periodo:(int) periodo/100 *100;
        String queryStr = String.format(""+
                "SELECT A.CENTRO_CODIGO CODIGO\n" +
                "  FROM MS_CENTRO_LINEAS A\n" +
                "  JOIN MS_CENTROS B ON B.CODIGO = A.CENTRO_CODIGO\n" +
                " WHERE A.PERIODO=%d\n" +
                "   AND A.REPARTO_TIPO=%d\n" +
                "   AND A.ITERACION=-2\n" +
                "   AND B.TIPO NOT IN ('STAFF','SOPORTE')\n" +
                " ORDER BY A.CENTRO_CODIGO",
                periodo,repartoTipo);
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
    
    public List<String> listarCodigosWithoutBolsas(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
            "SELECT CODIGO\n" +
            "  FROM ms_centro_lineas A\n" +
            "  JOIN MS_centros B\n" +
            "    ON A.centro_codigo=b.codigo\n" +
            " WHERE B.TIPO NOT IN ('BOLSA','OFICINA')\n" +
            "   AND A.PERIODO=%d\n" +
            "   AND A.REPARTO_TIPO=%d",
                periodo,repartoTipo);
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                lista.add(rs.getString("codigo"));
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosPeriodo(int periodo, int repartoTipo) {
        String queryStr = String.format("SELECT CENTRO_CODIGO FROM MS_CENTRO_LINEAS WHERE PERIODO=%d AND REPARTO_TIPO=%d", periodo, repartoTipo);
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                lista.add(rs.getString("CENTRO_CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigosCentrosPeriodo(int periodo, int repartoTipo, List<String> lstTipos) {
        String queryStr = String.format("" +
                "SELECT A.CENTRO_CODIGO\n" +
                "  FROM MS_CENTRO_LINEAS A\n" +
                "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n" +
                " WHERE A.PERIODO=%d\n" +
                "   AND A.ITERACION=-2\n" +
                "   AND A.REPARTO_TIPO=%d",
                periodo, repartoTipo);
        if (lstTipos != null && !lstTipos.isEmpty()) {
            queryStr += "\n   AND B.TIPO IN";
            int i = 0;
            for (String tipo : lstTipos) {
                if (i++ == 0) {
                    queryStr += String.format(" ('%s'", tipo);
                } else {
                    queryStr += String.format(", '%s'", tipo);
                }
            }
            queryStr += ")";
        }
        
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                lista.add(rs.getString("CENTRO_CODIGO"));
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

    public int actualizarObjeto(String codigo, String nombre, String cecoTipoCodigo, int nivel, String cecoPadreCodigo, int tipoGasto, String niif17Atribuible, String niif17Tipo, String niif17Clase) {
        niif17Atribuible = convertirPalabraAbreviatura(niif17Atribuible);
        niif17Tipo = convertirPalabraAbreviatura(niif17Tipo);
        niif17Clase = convertirPalabraAbreviatura(niif17Clase);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
                "UPDATE MS_centros\n" +
                "   SET nombre='%s',tipo='%s',nivel=%d,centro_padre_codigo='%s',tipo_gasto=%d,niif17_atribuible='%s',niif17_tipo='%s',niif17_clase='%s',fecha_actualizacion=TO_DATE('%s','yyyy/mm/dd hh24:mi:ss')\n" +
                " WHERE codigo='%s'",
                nombre,cecoTipoCodigo,nivel,cecoPadreCodigo,tipoGasto,niif17Atribuible, niif17Tipo, niif17Clase,fechaStr,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjeto(String codigo, String nombre, String cecoTipoCodigo, int nivel, String cecoPadreCodigo, int repartoTipo, int tipoGasto, String niif17Atribuible, String niif17Tipo, String niif17Clase ) {
        niif17Atribuible = convertirPalabraAbreviatura(niif17Atribuible);
        niif17Tipo = convertirPalabraAbreviatura(niif17Tipo);
        niif17Clase = convertirPalabraAbreviatura(niif17Clase);
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());        
        String queryStr = String.format("" +
                "INSERT INTO MS_centros(codigo,nombre,esta_activo,tipo,nivel,centro_padre_codigo,tipo_gasto,niif17_atribuible, niif17_tipo, niif17_clase, fecha_creacion,fecha_actualizacion)\n" +
                "VALUES ('%s','%s',%d,'%s',%d,'%s',%d,'%s','%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                codigo,nombre,1,cecoTipoCodigo,nivel,cecoPadreCodigo,tipoGasto,niif17Atribuible,niif17Tipo,niif17Clase,fechaStr,fechaStr);
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
                "       A.tipo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion,\n" +
                "       SUM(COALESCE(C.saldo,0)) saldo\n" +
                "  FROM MS_centros A\n" +
                "  JOIN MS_centro_lineas C ON A.codigo=C.centro_codigo\n" +
                " WHERE C.periodo=%d AND C.reparto_tipo='%d' and A.nivel !=0\n" +
                " GROUP BY A.codigo,A.nombre,A.esta_activo,A.nivel,A.tipo,A.centro_padre_codigo,\n" +
                "          A.fecha_creacion,A.fecha_actualizacion",
                periodo,repartoTipo);
        } else {
            queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.esta_activo,\n" +
                "       A.nivel,\n" +
                "       A.centro_padre_codigo,\n" +
                "       B.tipo,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion,\n" +
                "       SUM(COALESCE(C.saldo,0)) saldo\n" +
                "  FROM MS_centros A\n" +
                "  JOIN MS_centro_lineas C ON A.codigo=C.centro_codigo\n" +
                " WHERE C.periodo=%d AND C.reparto_tipo=%d AND A.nivel !=0 AND A.codigo NOT IN (%s)\n" +
                " GROUP BY A.codigo,A.nombre,A.esta_activo,A.nivel,A.centro_padre_codigo,\n" +
                "          A.fecha_creacion,A.fecha_actualizacion",
                periodo,repartoTipo,codigos);
        }
        
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                int nivel = rs.getInt("nivel");
                String centroPadreCodigo = rs.getString("centro_padre_codigo");
                String tipo = rs.getString("tipo");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = rs.getDouble("saldo");
                
                Centro item = new Centro(codigo, nombre, nivel, null, saldo, tipo, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    public List<Centro> listarPeriodo(int periodo, int repartoTipo) {
        String periodoStr = repartoTipo == 1 ? "C.PERIODO" : "TRUNC(C.PERIODO/100)*100";
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.tipo,\n" +
                "       SUM(COALESCE(C.saldo,0)) saldo\n" +
                "  FROM MS_centros A\n" +
                "  JOIN MS_centro_lineas C ON A.codigo=C.centro_codigo\n" +
                " WHERE %s=%d\n" +
                "   AND C.reparto_tipo=%d\n" +
                "   AND (c.iteracion = -1 OR C.iteracion = -2)\n" +
                " GROUP BY A.codigo,A.nombre,a.TIPO",
                periodoStr, periodo, repartoTipo);
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                double saldo = rs.getDouble("saldo");
                
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
            String tipo = item.getTipo();
            String centroPadreCodigo = "-";
            int nivel = item.getNivel();
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
                    "INSERT INTO MS_CENTROS(CODIGO,NOMBRE,ESTA_ACTIVO,NIVEL,CENTRO_PADRE_CODIGO,TIPO,REPARTO_TIPO,tipo_gasto,niif17_atribuible,niif17_tipo,niif17_clase,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s',%d,%d,'%s','%s',%d,'%d','%s','%s','%s',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,1,nivel,centroPadreCodigo,tipo,0,tipoGasto, niif17Atribuible, niif17Tipo, niif17Clase,fechaStr,fechaStr);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
//    Verifica si esta siendo usado en las lineas
    public int verificarObjetoEnDetalleGasto(String codigo, int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT COUNT(1) COUNT\n"+
                "  FROM MS_CUENTA_PARTIDA_CENTRO\n" +
                " WHERE CENTRO_CODIGO='%s' AND PERIODO=%d AND REPARTO_TIPO=%d",
                codigo,periodo,repartoTipo);
        int cont=-1;
        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
            while(rs.next())
                cont = rs.getInt("COUNT");
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
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cont;
    }
    
    // POR REVISAR
    public int verificarObjetoCentroPeriodoDriver(String codigo, int periodo, int repartoTipo) {
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
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cont;
    }
    
    public boolean verificarDistribucionBolsasPeriodo(int periodo, int repartoTipo){
        String queryStr = String.format("" +
                "SELECT COUNT(1) CNT\n" +
                "  FROM ms_cascada\n" +
                " WHERE periodo = %d AND reparto_tipo = %d AND iteracion < 1",
                periodo,repartoTipo);
        int cont=-1;
        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
            while(rs.next()) {
                cont = rs.getInt("cnt");
            }    
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cont>0;
    }
    
    public boolean verificarDistribucionStaffPeriodo(int periodo, int repartoTipo){
        String queryStr = String.format("" +
                "SELECT COUNT(1) CNT\n" +
                "  FROM ms_cascada\n" +
                " WHERE periodo = %d AND reparto_tipo = %d AND iteracion >= 1",
                periodo,repartoTipo);
        int cont=-1;
        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
            while(rs.next()) {
                cont = rs.getInt("cnt");
            }    
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cont>0;
    }
    
    public boolean verificarDistribucionObjetoPeriodo(int periodo, int repartoTipo){
        String queryStr = String.format("" +
                "SELECT COUNT(1) CNT\n" +
                "  FROM ms_objeto_lineas\n" +
                " WHERE periodo = %d AND reparto_tipo = %d",
                periodo,repartoTipo);
        int cont=-1;
        try(ResultSet rs = ConexionBD.ejecutarQuery(queryStr);) {
            while(rs.next()) {
                cont = rs.getInt("cnt");
            }    
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cont>0;
    }
        
    public int eliminarObjetoCentro(String codigo) {
        String queryStr = String.format("" +
                "DELETE FROM MS_centros\n" +
                " WHERE codigo='%s'",
                codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public List<Centro> listarObjetos() {
        String queryStr = String.format("" +
                "SELECT A.codigo,\n" +
                "       A.nombre,\n" +
                "       A.nivel,\n" +
                "       A.tipo,\n" +
                "       A.centro_padre_codigo,\n" +
                "       A.niif17_atribuible,\n" +
                "       A.niif17_tipo,\n" +
                "       A.niif17_clase,\n" +
                "       CASE WHEN A.tipo_gasto=0 then 'INDIRECTO'\n" +
                "            WHEN A.tipo_gasto=1 then 'DIRECTO'\n" +
                "       END tipo_gasto,\n" +
                "       A.fecha_creacion,\n" +
                "       A.fecha_actualizacion\n" +
                "  FROM MS_centros A\n" +
                " ORDER BY A.codigo");
        List<Centro> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                int nivel = rs.getInt("nivel");
                String centroPadreCodigo = rs.getString("centro_padre_codigo");
                String tipoGasto = rs.getString("tipo_gasto");
                String niif17_atribuible = rs.getString("niif17_atribuible");
                String niif17_tipo = rs.getString("niif17_tipo");
                String niif17_clase = rs.getString("niif17_clase");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                double saldo = 0;
                niif17_atribuible = convertirAbreviaturaPalabra(niif17_atribuible);
                niif17_tipo = convertirAbreviaturaPalabra(niif17_tipo);
                niif17_clase = convertirAbreviaturaPalabra(niif17_clase);
                
                Centro item = new Centro(codigo, nombre, nivel, null, saldo, tipo, niif17_atribuible, niif17_tipo, niif17_clase, tipoGasto, fechaCreacion, fechaActualizacion);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    //Por evaluar
    public List<CentroDriver> listarCuentaPartidaCentroBolsaConDriverDirecta(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
            "SELECT A.CUENTA_CONTABLE_CODIGO CUENTA_CONTABLE_CODIGO,\n" +
            "       B.NOMBRE CUENTA_CONTABLE_NOMBRE,\n" +
            "       A.PARTIDA_CODIGO PARTIDA_CODIGO,\n" +
            "       C.NOMBRE PARTIDA_NOMBRE,\n" +
            "       A.CENTRO_CODIGO CENTRO_CODIGO,\n" +
            "       D.NOMBRE CENTRO_NOMBRE,\n" +
            "       A.DRIVER_CODIGO DRIVER_CODIGO,\n" +
            "       E.NOMBRE DRIVER_NOMBRE\n" +
            "  FROM MS_BOLSA_DRIVER A\n" +
            "  JOIN MS_PLAN_DE_CUENTAS B ON B.CODIGO=A.CUENTA_CONTABLE_CODIGO\n" +
            "  JOIN MS_PARTIDAS C ON C.CODIGO=A.PARTIDA_CODIGO\n" +
            "  JOIN MS_CENTROS D ON D.CODIGO=A.CENTRO_CODIGO\n" +
            "  JOIN MS_DRIVERS E ON E.CODIGO=A.DRIVER_CODIGO\n" +
            " WHERE A.PERIODO=%d AND A.reparto_tipo=%d",
            periodo, repartoTipo);
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
        String periodoStr = repartoTipo == 1 ? "a.PERIODO" : "TRUNC(a.PERIODO/100)*100";
        
        String queryStr = String.format("" +
            "SELECT A.cuenta_contable_codigo cuenta_contable_codigo,\n" +
            "       A.partida_codigo partida_codigo,\n" +
            "       A.centro_codigo centro_codigo,\n" +
            "       NVL(A.saldo,0) saldo,\n" +
            "       B.driver_codigo driver_codigo,\n" +
            "       C.grupo_gasto grupo_gasto\n" +
            "  FROM ms_cuenta_partida_centro A\n" +
            "  JOIN ms_bolsa_driver B ON SUBSTR(b.cuenta_contable_codigo,1,3) = SUBSTR(a.cuenta_contable_codigo,1,3) AND SUBSTR(b.cuenta_contable_codigo,5,11) = SUBSTR(a.cuenta_contable_codigo,5,11) AND b.partida_codigo = a.partida_codigo AND b.centro_codigo = a.centro_codigo AND b.periodo = %s AND b.reparto_tipo = a.reparto_tipo\n" +
            "  JOIN ms_partidas C ON c.codigo = a.partida_codigo\n" +
            "WHERE  a.periodo = '%d' AND a.reparto_tipo = '%d'",
            periodoStr,periodo,repartoTipo);
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
        String periodoStr = repartoTipo == 1 ? "a.PERIODO" : "TRUNC(a.PERIODO/100)*100";
        String queryStr = String.format("" +
            "SELECT COUNT(*) COUNT\n" +
            "  FROM ms_cuenta_partida_centro A\n" +
            "  JOIN ms_centros B ON A.CENTRO_CODIGO=B.CODIGO\n" +
            "    AND A.PERIODO=%d AND A.REPARTO_TIPO=%d\n" +
            "    AND B.TIPO IN ('BOLSA','OFICINA')\n" +
            "  LEFT JOIN ms_bolsa_driver C\n" +
            "    ON c.cuenta_contable_codigo=a.cuenta_contable_codigo\n" +
            "   AND c.partida_codigo=A.partida_codigo\n" +
            "   AND c.centro_codigo=A.centro_codigo\n" +
            "   AND c.periodo=%s\n" +
            "   AND c.reparto_tipo=a.reparto_tipo\n" +
            " WHERE c.driver_codigo IS NULL",
            periodo,repartoTipo,periodoStr);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                count = rs.getInt("COUNT");
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    public List<CentroDriver> listarCentrosNombresConDriver(int periodo, String tipo, int repartoTipo, int nivel) {
        String periodoStr = repartoTipo == 1 ? "a.PERIODO" : "TRUNC(a.PERIODO/100)*100";
        String queryStr = String.format(""+
                " SELECT A.CENTRO_CODIGO CENTRO_CODIGO,\n" +
                "        a.cuenta_contable_origen_codigo cuenta_contable_origen_codigo,\n" +
                "        a.partida_origen_codigo partida_origen_codigo,\n" +
                "        a.centro_origen_codigo centro_origen_codigo,\n" +
                "        A.SALDO MONTO,\n" +
                "        c.driver_codigo driver_codigo,\n" +
                "        a.grupo_gasto grupo_gasto\n" +
                "  FROM MS_CENTRO_LINEAS A\n" +
                "  JOIN MS_CENTROS B ON b.codigo = a.centro_codigo\n" +
                "  JOIN ms_entidad_origen_driver C ON c.entidad_origen_codigo = a.centro_codigo AND c.periodo = %s AND c.reparto_tipo = a.reparto_tipo\n" +
                "  WHERE a.periodo = '%d' AND a.reparto_tipo ='%d' AND a.iteracion >-2 AND B.NIVEL = %d",
                periodoStr, periodo, repartoTipo, nivel);
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String centroCodigo = rs.getString("CENTRO_CODIGO");
                String cuentaOrigenCodigo = rs.getString("cuenta_contable_origen_codigo");
                String partidaOrigenCodigo = rs.getString("partida_origen_codigo");
                String centroOrigenCodigo = rs.getString("centro_origen_codigo");
                double monto = rs.getDouble("MONTO");
                String driverCodigo = rs.getString("driver_codigo");
                String grupoGasto = rs.getString("grupo_gasto");
                
                CentroDriver centroDriver = new CentroDriver(periodo, centroCodigo,cuentaOrigenCodigo,partidaOrigenCodigo,centroOrigenCodigo,driverCodigo,monto,new Tipo(grupoGasto,""));
                lista.add(centroDriver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<CentroDriver> listarCentrosObjetosNombresConDriver(int periodo, int repartoTipo) {
        String periodoStr = repartoTipo == 1 ? "a.PERIODO" : "TRUNC(a.PERIODO/100)*100";
        String queryStr = String.format(""+
                "SELECT a.centro_codigo centro_codigo,\n" +
                "       a.cuenta_contable_origen_codigo cuenta_contable_origen_codigo,\n" +
                "       a.partida_origen_codigo partida_origen_codigo,\n" +
                "       a.centro_origen_codigo centro_origen_codigo,\n" +
                "       a.saldo monto,\n" +
                "       c.driver_codigo driver_codigo,\n" +
                "       a.grupo_gasto grupo_gasto\n" +
                "  FROM MS_CENTRO_LINEAS A\n" +
                "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n" +
                "  JOIN MS_OBJETO_DRIVER C\n" +
                "    ON C.CENTRO_CODIGO=A.CENTRO_CODIGO\n" +
                "   AND c.periodo=%s AND c.reparto_tipo=a.reparto_tipo AND c.grupo_gasto=a.grupo_gasto\n" +
                " WHERE A.PERIODO=%d\n" +
                "   AND A.ITERACION>-2\n" +
                "   AND A.REPARTO_TIPO=%d\n" +
                "   AND B.TIPO NOT IN ('STAFF','SOPORTE')\n" +
                "order by a.centro_codigo",
                periodoStr, periodo,repartoTipo);
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String centroCodigo = rs.getString("CENTRO_CODIGO");
                String cuentaOrigenCodigo = rs.getString("cuenta_contable_origen_codigo");
                String partidaOrigenCodigo = rs.getString("partida_origen_codigo");
                String centroOrigenCodigo = rs.getString("centro_origen_codigo");
                double monto = rs.getDouble("MONTO");
                String driverCodigo = rs.getString("driver_codigo");
                String grupoGasto = rs.getString("grupo_gasto");
                
                CentroDriver centroDriver = new CentroDriver(periodo, centroCodigo,cuentaOrigenCodigo,partidaOrigenCodigo,centroOrigenCodigo,driverCodigo,monto,new Tipo(grupoGasto,""));
                lista.add(centroDriver);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<CentroDriver> listarCentrosConDriver(int periodo, String tipo, int repartoTipo, int nivel) {
        String queryStr = String.format("" +
            "SELECT B.CODIGO,\n" +
            "       B.NOMBRE,\n" +
            "       COALESCE(C.DRIVER_CODIGO,'Sin driver asignado') DRIVER_CODIGO,\n" +
            "       COALESCE(D.NOMBRE,'Sin driver asignado') DRIVER_NOMBRE\n" +
            "  FROM MS_CENTRO_LINEAS A\n" +
            "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n" +
            "  LEFT JOIN MS_ENTIDAD_ORIGEN_DRIVER C ON A.REPARTO_TIPO=C.REPARTO_TIPO AND C.ENTIDAD_ORIGEN_CODIGO=A.CENTRO_CODIGO AND C.PERIODO=A.PERIODO\n" +
            "  LEFT JOIN MS_DRIVERS D ON C.DRIVER_CODIGO=D.CODIGO\n" +
            " WHERE A.PERIODO =%d AND A.ITERACION=-2 AND A.REPARTO_TIPO=%d\n",
            periodo, repartoTipo);
        if (tipo.equals("-")) queryStr += "AND B.TIPO IN ('STAFF','SOPORTE')\n";
        else if (!tipo.equals("-")) queryStr += String.format("   AND B.TIPO='%s'\n", tipo);
        if (nivel!=-1) queryStr += String.format("   AND B.NIVEL=%d\n", nivel);
        queryStr += " ORDER BY A.CENTRO_CODIGO";
        
        List<CentroDriver> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                String driverCodigo = rs.getString("DRIVER_CODIGO");
                String driverNombre = rs.getString("DRIVER_NOMBRE");
                
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
            "SELECT A.CENTRO_CODIGO CENTRO_CODIGO,\n" +
            "       B.NOMBRE CENTRO_NOMBRE,\n" +
            "       C.CODIGO GRUPO_GASTOS,\n" +
            "       COALESCE(D.DRIVER_CODIGO,'Sin driver asignado') DRIVER_CODIGO,\n" +
            "       COALESCE(E.NOMBRE,'Sin driver asignado') DRIVER_NOMBRE\n" +
            "  FROM MS_CENTRO_LINEAS A\n" +
            "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n"+
            "  JOIN MS_GRUPO_GASTOS C ON 1=1\n" +
            "  LEFT JOIN MS_OBJETO_DRIVER D ON A.REPARTO_TIPO=D.REPARTO_TIPO AND D.CENTRO_CODIGO=A.CENTRO_CODIGO AND D.PERIODO=A.PERIODO AND D.GRUPO_GASTO=C.CODIGO\n" +
            "  LEFT JOIN MS_DRIVERS E ON E.CODIGO=D.DRIVER_CODIGO\n" +
            " WHERE A.PERIODO=%d AND A.ITERACION=-2 AND A.REPARTO_TIPO=%d\n",
            periodo, repartoTipo);
        if (tipo.equals("-")) queryStr += "AND B.TIPO IN ('LINEA','CANAL','FICTICIO','PROYECTO','SALUD')\n";
        else if (!tipo.equals("-")) queryStr += String.format("   AND B.TIPO='%s'\n",tipo);
        if (nivel!=-1) queryStr += String.format("   AND A.NIVEL=%d\n", nivel);
        queryStr += " ORDER BY A.CENTRO_CODIGO";
        
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
                "SELECT COUNT(DISTINCT A.CENTRO_CODIGO) CNT\n" +
                "  FROM MS_CUENTA_PARTIDA_CENTRO A\n" +
                "  JOIN MS_CENTROS B\n" +
                "    ON A.CENTRO_CODIGO=B.CODIGO\n" +
                "   AND A.PERIODO=%d\n" +
                "   AND A.REPARTO_TIPO=%d\n" +
                "   AND B.TIPO IN ('STAFF','SOPORTE')",
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
        periodo = repartoTipo == 1 ? periodo : (int)periodo/100 * 100;
        String queryStr = String.format("" +
                "SELECT MAX(B.NIVEL) CNT\n" +
                "  FROM MS_CENTRO_LINEAS A\n" +
                "  JOIN MS_CENTROS B\n" +
                "    ON A.CENTRO_CODIGO=B.CODIGO\n" +
                "   AND A.PERIODO=%d\n" +
                "   AND A.REPARTO_TIPO=%d\n" +
                "   AND B.TIPO IN ('STAFF','SOPORTE')",
                periodo, repartoTipo);
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next())
                cnt = rs.getInt("CNT");
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }
    
    public int borrarDistribuciones(int periodo, int iteracion, int repartoTipo) {
        ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_1");
        ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_2");
        return ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_2_F");
    }
    
    public int borrarDistribucionesCascada(int periodo, int iteracion, int repartoTipo) {
        ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_2");
        return ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_2_F");
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
    
    public void insertarDistribucionBatchConGrupoGasto(String centroCodigo, int periodo, int iteracion, double saldo, String entidadOrigenCodigo, String cuentaContableOrigenCodigo, String partidaOrigenCodigo, String centroOrigenCodigo, String grupoGasto, int repartoTipo) {
        String fechaStr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO MS_centro_lineas(centro_codigo,periodo,iteracion,saldo,entidad_origen_codigo,CUENTA_CONTABLE_ORIGEN_CODIGO,PARTIDA_ORIGEN_CODIGO,CENTRO_ORIGEN_CODIGO,grupo_gasto,reparto_tipo,fecha_creacion,fecha_actualizacion)\n" +
                "VALUES('%s',%d,%d,%.15f,'%s','%s','%s','%s','%s','%d',TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                centroCodigo, periodo, iteracion, saldo, entidadOrigenCodigo, cuentaContableOrigenCodigo, partidaOrigenCodigo, centroOrigenCodigo, grupoGasto, repartoTipo, fechaStr, fechaStr);
        ConexionBD.agregarBatch(queryStr);
    }
    
    public int insertarDistribucionBolsas(int periodo, int repartoTipo) {
        ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_1");
        
        String periodoStr = repartoTipo == 1 ? "A.PERIODO" : "TRUNC(A.PERIODO/100)*100";
        String queryStr = String.format(""+
                "INSERT INTO MS_FASE_1(CUENTA_CONTABLE_ORIGEN_CODIGO,PARTIDA_ORIGEN_CODIGO,CENTRO_ORIGEN_CODIGO,DRIVER_CODIGO,CENTRO_DESTINO_CODIGO,GRUPO_GASTO,MONTO)\n" +
                "SELECT A.CUENTA_CONTABLE_CODIGO CUENTA_CONTABLE_ORIGEN_CODIGO,\n" +
                "       A.PARTIDA_CODIGO PARTIDA_ORIGEN_CODIGO,\n" +
                "       A.CENTRO_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       NVL(B.DRIVER_CODIGO,'N/A') DRIVER_CODIGO,\n" +
                "       NVL(C.ENTIDAD_DESTINO_CODIGO,A.CENTRO_CODIGO) CENTRO_DESTINO_CODIGO,\n" +
                "       D.GRUPO_GASTO,\n" +
                "       A.SALDO*NVL(C.PORCENTAJE/100.0,1) MONTO\n" +
                "  FROM MS_CUENTA_PARTIDA_CENTRO A\n" +
                "  LEFT JOIN MS_BOLSA_DRIVER B\n" +
                "    ON SUBSTR(A.CUENTA_CONTABLE_CODIGO,1,3)=SUBSTR(B.CUENTA_CONTABLE_CODIGO,1,3) AND SUBSTR(A.CUENTA_CONTABLE_CODIGO,5,11)=SUBSTR(B.CUENTA_CONTABLE_CODIGO,5,11)\n" +
                "   AND A.PARTIDA_CODIGO=B.PARTIDA_CODIGO\n" +
                "   AND A.CENTRO_CODIGO=B.CENTRO_CODIGO\n" +
                "   AND B.PERIODO=%s\n" +
                "   AND B.REPARTO_TIPO=A.REPARTO_TIPO\n" +
                "  LEFT JOIN MS_DRIVER_LINEAS C\n" +
                "    ON C.DRIVER_CODIGO=B.DRIVER_CODIGO\n" +
                "   AND C.PERIODO=%s\n" +
                "   AND C.REPARTO_TIPO=A.REPARTO_TIPO\n" +
                "  LEFT JOIN MS_PARTIDAS D\n" +
                "    ON A.PARTIDA_CODIGO=D.CODIGO\n" +
                " WHERE A.PERIODO=%d\n" +
                "   AND A.REPARTO_TIPO=%d",
                periodoStr,periodoStr,periodo,repartoTipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int iniciarFase2(int periodo, int repartoTipo) {
        ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_2");
        ConexionBD.ejecutar("TRUNCATE TABLE MS_FASE_2_F");
        
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO MS_FASE_2(ITERACION,CUENTA_CONTABLE_INICIAL_CODIGO,PARTIDA_INICIAL_CODIGO,CENTRO_INICIAL_CODIGO,CENTRO_ORIGEN_CODIGO,DRIVER_CODIGO,CENTRO_DESTINO_CODIGO,GRUPO_GASTO,MONTO)\n" +
                "SELECT 0 ITERACION,\n" +
                "       CUENTA_CONTABLE_ORIGEN_CODIGO CUENTA_CONTABLE_INICIAL_CODIGO,\n" +
                "       PARTIDA_ORIGEN_CODIGO PARTIDA_INICIAL_CODIGO,\n" +
                "       CENTRO_DESTINO_CODIGO CENTRO_INICIAL_CODIGO,\n" +
                "       CENTRO_DESTINO_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       DRIVER_CODIGO,\n" +
                "       CENTRO_DESTINO_CODIGO,\n" +
                "       GRUPO_GASTO,\n" +
                "       SUM(MONTO) MONTO\n" +
                "  FROM MS_FASE_1\n" +
                " GROUP BY CUENTA_CONTABLE_ORIGEN_CODIGO,\n" +
                "          PARTIDA_ORIGEN_CODIGO,\n" +
                "          DRIVER_CODIGO,\n" +
                "          CENTRO_DESTINO_CODIGO,\n" +
                "          GRUPO_GASTO");
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int terminarFase2(int periodo, int repartoTipo) {
        String queryStr = String.format(Locale.US, "" +
                "INSERT INTO MS_FASE_2_F(CUENTA_CONTABLE_INICIAL_CODIGO,PARTIDA_INICIAL_CODIGO,CENTRO_INICIAL_CODIGO,CENTRO_DESTINO_CODIGO,GRUPO_GASTO,MONTO)\n" +
                "SELECT A.CUENTA_CONTABLE_INICIAL_CODIGO,\n" +
                "       A.PARTIDA_INICIAL_CODIGO,\n" +
                "       A.CENTRO_INICIAL_CODIGO,\n" +
                "       A.CENTRO_DESTINO_CODIGO,\n" +
                "       A.GRUPO_GASTO,\n" +
                "       SUM(A.MONTO) MONTO\n" +
                "  FROM MS_FASE_2 A\n" +
                "  JOIN MS_CENTROS B\n" +
                "    ON A.CENTRO_DESTINO_CODIGO=B.CODIGO\n" +
                "   AND B.TIPO NOT IN ('STAFF','SOPORTE')\n" +
                " GROUP BY CUENTA_CONTABLE_INICIAL_CODIGO,PARTIDA_INICIAL_CODIGO,CENTRO_INICIAL_CODIGO,CENTRO_DESTINO_CODIGO,GRUPO_GASTO");
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarDistribucionCascadaPorNivel(int iteracion, int periodo, int repartoTipo, double precision) {
        String periodoStr = repartoTipo == 1 ? String.format("%d", periodo) : String.format("TRUNC(%d/100)*100", periodo);
        String queryStr = String.format(Locale.US, ""+
                "INSERT INTO MS_FASE_2(ITERACION,CUENTA_CONTABLE_INICIAL_CODIGO,PARTIDA_INICIAL_CODIGO,CENTRO_INICIAL_CODIGO,CENTRO_ORIGEN_CODIGO,DRIVER_CODIGO,CENTRO_DESTINO_CODIGO,GRUPO_GASTO,MONTO)\n" +
                "SELECT %d ITERACION,\n" +
                "       A.CUENTA_CONTABLE_INICIAL_CODIGO,\n" +
                "       A.PARTIDA_INICIAL_CODIGO,\n" +
                "       A.CENTRO_INICIAL_CODIGO,\n" +
                "       A.CENTRO_DESTINO_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       C.DRIVER_CODIGO,\n" +
                "       D.ENTIDAD_DESTINO_CODIGO CENTRO_DESTINO_CODIGO,\n" +
                "       A.GRUPO_GASTO,\n" +
                "       SUM(A.MONTO*D.PORCENTAJE/100.00) MONTO\n" +
                "  FROM MS_FASE_2 A\n" +
                "  JOIN MS_CENTROS B\n" +
                "    ON B.CODIGO=A.CENTRO_DESTINO_CODIGO\n" +
                "   AND B.NIVEL=%d\n" +
                "  JOIN MS_ENTIDAD_ORIGEN_DRIVER C\n" +
                "    ON C.PERIODO=%s\n" +
                "   AND C.REPARTO_TIPO=%d\n" +
                "   AND C.ENTIDAD_ORIGEN_CODIGO=A.CENTRO_DESTINO_CODIGO\n" +
                "  JOIN MS_DRIVER_LINEAS D\n" +
                "    ON D.PERIODO=%s\n" +
                "   AND D.REPARTO_TIPO=%d\n" +
                "   AND D.DRIVER_CODIGO=C.DRIVER_CODIGO\n" +
                " GROUP BY A.CUENTA_CONTABLE_INICIAL_CODIGO,\n" +
                "          A.PARTIDA_INICIAL_CODIGO,\n" +
                "          A.CENTRO_INICIAL_CODIGO,\n" +
                "          A.CENTRO_DESTINO_CODIGO,\n" +
                "          C.DRIVER_CODIGO,\n" +
                "          D.ENTIDAD_DESTINO_CODIGO,\n" +
                "          A.GRUPO_GASTO\n" +
                " HAVING ABS(SUM(A.MONTO*D.PORCENTAJE/100.00))>=%f",
                iteracion,iteracion,periodoStr,repartoTipo,periodoStr,repartoTipo,precision);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarDistribucionCentrosObjetosCosto(String codigo, int periodo, int repartoTipo, double precision) {
        String periodoStr = repartoTipo == 1 ? String.format("%d", periodo) : String.format("TRUNC(%d/100)*100", periodo);
        String queryStr = String.format(Locale.US, ""+
                "INSERT INTO MS_FASE_3(CUENTA_CONTABLE_INICIAL_CODIGO,PARTIDA_INICIAL_CODIGO,CENTRO_INICIAL_CODIGO,CENTRO_DESTINO_CODIGO,DRIVER_CODIGO,PRODUCTO_CODIGO,SUBCANAL_CODIGO,GRUPO_GASTO,MONTO)\n" +
                "SELECT A.CUENTA_CONTABLE_INICIAL_CODIGO,\n" +
                "       A.PARTIDA_INICIAL_CODIGO,\n" +
                "       A.CENTRO_INICIAL_CODIGO,\n" +
                "       A.CENTRO_DESTINO_CODIGO,\n" +
                "       B.DRIVER_CODIGO,\n" +
                "       C.PRODUCTO_CODIGO,\n" +
                "       C.SUBCANAL_CODIGO,\n" +
                "       A.GRUPO_GASTO,\n" +
                "       A.MONTO*C.PORCENTAJE/100.00\n" +
                "  FROM MS_FASE_2_F A\n" +
                "  JOIN MS_OBJETO_DRIVER B\n" +
                "    ON B.CENTRO_CODIGO=A.CENTRO_DESTINO_CODIGO\n" +
                "   AND B.PERIODO=%s\n" +
                "   AND B.REPARTO_TIPO=%d\n" +
                "   AND B.GRUPO_GASTO=A.GRUPO_GASTO\n" +
                "  JOIN MS_DRIVER_OBJETO_LINEAS C\n" +
                "    ON C.DRIVER_CODIGO=B.DRIVER_CODIGO\n" +
                "   AND C.PERIODO=%s\n" +
                "   AND C.REPARTO_TIPO=%d\n" +
                "  JOIN MS_CENTROS B\n" +
                "    ON B.CODIGO=A.CENTRO_DESTINO_CODIGO\n" +
                " WHERE A.CENTRO_DESTINO_CODIGO='%s'\n" +
                "   AND ABS(A.MONTO*C.PORCENTAJE/100.00)>=%f",
                periodoStr, repartoTipo, periodoStr, repartoTipo, codigo, precision);
        return ConexionBD.ejecutar(queryStr);
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