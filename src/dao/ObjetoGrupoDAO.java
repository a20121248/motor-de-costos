package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Grupo;

public class ObjetoGrupoDAO {
    String prefixTableName;
    String objetoTipo;
    
    public ObjetoGrupoDAO() {
        
    }
    
    public ObjetoGrupoDAO(String objetoTipo) {
        this.objetoTipo = objetoTipo;
        prefixTableName = "";
        switch (objetoTipo) {
            case "OFI":
                prefixTableName = "OFICINA";
                break;
            case "BAN":
                prefixTableName = "BANCA";
                break;
            case "PRO":
                prefixTableName = "PRODUCTO";
                break;
            case "SCA":
                prefixTableName = "SUBCANAL";
                break;
        }
    }
    
    public List<String> listarCodigos() {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery("SELECT CODIGO FROM " + prefixTableName + "_GRUPOS")) {
            while(rs.next()) lista.add(rs.getString("CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(ObjetoGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigos(String codigo) {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(String.format("SELECT CODIGO FROM " + prefixTableName +"_GRUPOS WHERE CODIGO!='%s'",codigo))) {
            while(rs.next()) lista.add(rs.getString("CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(ObjetoGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Grupo> listarJerarquia(int periodo) {
        String queryStr = String.format("" +
                "SELECT A.CODIGO ENTIDAD_CODIGO,\n" +
                "       A.NOMBRE ENTIDAD_NOMBRE,\n" +
                "       0 NIVEL,\n" +
                "       COALESCE(C.ENTIDAD_PADRE_CODIGO,'SIN ASIGNAR') ENTIDAD_PADRE_CODIGO,\n" +
                "       COALESCE(D.NOMBRE,'SIN ASIGNAR') ENTIDAD_PADRE_NOMBRE,\n" +
                "       CASE WHEN D.NOMBRE IS NULL THEN -1 ELSE 1 END ENTIDAD_PADRE_NIVEL\n" +
                "  FROM %sS A\n" +
                "  JOIN %s_LINEAS B ON A.CODIGO=B.%s_CODIGO\n" +
                "  LEFT JOIN JERARQUIA C ON A.CODIGO=C.ENTIDAD_CODIGO AND C.NIVEL=0 AND B.PERIODO=C.PERIODO AND C.ENTIDAD_TIPO='%s'\n" +
                "  LEFT JOIN %s_GRUPOS D ON C.ENTIDAD_PADRE_CODIGO=D.CODIGO\n" +
                " WHERE B.PERIODO=%d\n" +
                " UNION ALL\n" +
                "SELECT A.CODIGO ENTIDAD_CODIGO,\n" +
                "       A.NOMBRE ENTIDAD_NOMBRE,\n" +
                "       A.NIVEL,\n" +
                "       COALESCE(B.ENTIDAD_PADRE_CODIGO,'SIN ASIGNAR') ENTIDAD_PADRE_CODIGO,\n" +
                "       COALESCE(C.NOMBRE,'SIN ASIGNAR') ENTIDAD_PADRE_NOMBRE,\n" +
                "       COALESCE(C.NIVEL,-1) ENTIDAD_PADRE_NIVEL\n" +
                "  FROM %s_GRUPOS A\n" +
                "  LEFT JOIN JERARQUIA B ON A.CODIGO=B.ENTIDAD_CODIGO AND A.NIVEL=B.NIVEL AND B.PERIODO=%d AND B.ENTIDAD_TIPO='%s'\n" +
                "  LEFT JOIN %s_GRUPOS C ON B.ENTIDAD_PADRE_CODIGO=C.CODIGO\n" +
                " ORDER BY NIVEL,ENTIDAD_CODIGO",prefixTableName,prefixTableName,prefixTableName,objetoTipo,prefixTableName,periodo,prefixTableName,periodo,objetoTipo,prefixTableName);
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String entidadCodigo = rs.getString("ENTIDAD_CODIGO");
                String entidadNombre = rs.getString("ENTIDAD_NOMBRE");
                int nivel = rs.getInt("NIVEL");
                String entidadPadreCodigo = rs.getString("ENTIDAD_PADRE_CODIGO");
                String entidadPadreNombre = rs.getString("ENTIDAD_PADRE_NOMBRE");
                int entidadPadreNivel = rs.getInt("ENTIDAD_PADRE_NIVEL");
                
                Grupo itemPadre = new Grupo(entidadPadreCodigo, entidadPadreNombre, null, 0, null, null, null);
                itemPadre.setNivel(entidadPadreNivel);

                Grupo item = new Grupo(entidadCodigo, entidadNombre, null, 0, null, null, null);
                item.setNivel(nivel);
                
                item.setGrupoPadre(itemPadre);
                lista.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObjetoGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigoObjetos(int nivelOrigen){
        String queryStr = String.format("" +
                "SELECT CODIGO\n" +
                "  FROM %s_GRUPOS\n" +
                " WHERE ESTA_ACTIVO=1\n",prefixTableName);
        if (nivelOrigen!=-1) queryStr += String.format("   AND NIVEL=%d+1\n",nivelOrigen);
        queryStr += " ORDER BY CODIGO";
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                lista.add(codigo);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObjetoGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Grupo> listarObjetos(int nivelOrigen) {
        String queryStr = String.format("" +
                "SELECT CODIGO,\n" +
                "       NOMBRE,\n" +
                "       NIVEL,\n" +
                "       FECHA_CREACION,\n" +
                "       FECHA_ACTUALIZACION\n" +
                "  FROM %s_GRUPOS\n" +
                " WHERE ESTA_ACTIVO=1\n",prefixTableName);
        if (nivelOrigen!=-1) queryStr += String.format("   AND NIVEL=%d+1\n",nivelOrigen);
        queryStr += " ORDER BY NIVEL,CODIGO";
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("CODIGO");
                String nombre = rs.getString("NOMBRE");
                int nivel = rs.getInt("NIVEL");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("FECHA_CREACION"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("FECHA_ACTUALIZACION"));
                Grupo item = new Grupo(codigo, nombre, null, 0, null, fechaCreacion, fechaActualizacion);
                item.setNivel(nivel);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ObjetoGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public int insertarObjeto(String codigo, String nombre, int nivel) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
            "INSERT INTO %s_GRUPOS(CODIGO,NOMBRE,NIVEL,ESTA_ACTIVO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
            "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
            prefixTableName,codigo,nombre,nivel,1,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarObjeto(String codigo, String nombre, int nivel, String codigoAnt) {
        String queryStr = String.format("" +
                "UPDATE %s_GRUPOS\n" +
                "   SET CODIGO='%s',NOMBRE='%s',NIVEL=%d\n" +
                " WHERE CODIGO='%s'",prefixTableName,codigo,nombre,nivel,codigoAnt);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarObjeto(String codigo) {
        String queryStr = String.format("DELETE FROM %s_GRUPOS WHERE CODIGO='%s'",prefixTableName,codigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("DELETE FROM grupo_lineas WHERE grupo_codigo='%s' AND periodo=%d",codigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarObjetoPeriodo(String codigo, int periodo) {
        String queryStr = String.format("INSERT INTO grupo_lineas(grupo_codigo,periodo) VALUES('%s',%d)",codigo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public List<Grupo> listarGrupoConGrupo(int periodo) {
        return null;
    }
    
    public int borrarGrupoPadre(String grupoCodigo, String grupoTipo, int periodo) {
        String queryStr = String.format("" +
                "DELETE FROM jerarquia\n" +
                " WHERE entidad_codigo='%s' AND entidad_tipo='%s' AND periodo=%d",
                grupoCodigo,grupoTipo,periodo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int insertarGrupoPadre(int periodo, String entidadCodigo, String entidadTipo, int nivel, String entidadPadreCodigo) {
        String queryStr = String.format("" +
                "INSERT INTO JERARQUIA(PERIODO,ENTIDAD_CODIGO,ENTIDAD_TIPO,NIVEL,ENTIDAD_PADRE_CODIGO)\n" +
                "VALUES (%d,'%s','%s',%d,'%s')",
                    periodo,
                    entidadCodigo,
                    entidadTipo,
                    nivel,
                    entidadPadreCodigo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarListaObjeto(List<Grupo> lista) {
        ConexionBD.crearStatement();
        lista.stream().filter(item -> item.getFlagCargar()).map((item) -> {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            int nivel = item.getNivel();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO %s_GRUPOS(CODIGO,NOMBRE,NIVEL,ESTA_ACTIVO,FECHA_CREACION,FECHA_ACTUALIZACION)\n" +
                    "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    prefixTableName,codigo,nombre,nivel,1,fechaStr,fechaStr);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
    
    public int borrarListaAsignacion(int periodo, String tipo) {
        String queryStr = String.format("" +
                "DELETE FROM JERARQUIA\n" +
                " WHERE PERIODO=%d AND ENTIDAD_TIPO='%s'",
                periodo,tipo);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public void insertarListaAsignacion(int periodo, String tipo, List<Grupo> lista) {
        borrarListaAsignacion(periodo, tipo);
        ConexionBD.crearStatement();
        lista.stream().map((item) -> {
            String codigo = item.getCodigo();
            int nivel = item.getNivel();
            String codigoPadre = item.getGrupoPadre().getCodigo();
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO JERARQUIA(PERIODO,ENTIDAD_CODIGO,ENTIDAD_TIPO,NIVEL,ENTIDAD_PADRE_CODIGO)\n" +
                    "VALUES (%d,'%s','%s',%d,'%s')",
                    periodo,codigo,tipo,nivel,codigoPadre);
            return queryStr;
        }).forEachOrdered((queryStr) -> {
            ConexionBD.agregarBatch(queryStr);
        });
        ConexionBD.ejecutarBatch();
        ConexionBD.cerrarStatement();
    }
}
