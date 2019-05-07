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

public class BancaGrupoDAO {
    public List<String> listarCodigos() {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery("SELECT CODIGO FROM BANCA_GRUPOS")) {
            while(rs.next()) lista.add(rs.getString("CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(BancaGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<String> listarCodigos(String codigo) {
        List<String> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(String.format("SELECT CODIGO FROM BANCA_GRUPOS WHERE CODIGO!='%s'",codigo))) {
            while(rs.next()) lista.add(rs.getString("CODIGO"));
        } catch (SQLException ex) {
            Logger.getLogger(BancaGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                "  FROM BANCAS A\n" +
                "  JOIN BANCA_LINEAS B ON A.CODIGO=B.BANCA_CODIGO\n" +
                "  LEFT JOIN JERARQUIA C ON A.CODIGO=C.ENTIDAD_CODIGO AND B.periodo=C.periodo\n" +
                "  LEFT JOIN BANCA_GRUPOS D ON C.ENTIDAD_PADRE_CODIGO=D.CODIGO\n" +
                " WHERE B.PERIODO=%d\n" +
                " UNION ALL\n" +
                "SELECT A.CODIGO ENTIDAD_CODIGO,\n" +
                "       A.NOMBRE ENTIDAD_NOMBRE,\n" +
                "       A.NIVEL,\n" +
                "       COALESCE(B.ENTIDAD_PADRE_CODIGO,'SIN ASIGNAR') ENTIDAD_PADRE_CODIGO,\n" +
                "       COALESCE(C.NOMBRE,'SIN ASIGNAR') ENTIDAD_PADRE_NOMBRE,\n" +
                "       COALESCE(C.NIVEL,-1) ENTIDAD_PADRE_NIVEL\n" +
                "  FROM BANCA_GRUPOS A\n" +
                "  LEFT JOIN JERARQUIA B ON A.CODIGO=B.ENTIDAD_CODIGO AND B.PERIODO=%d AND B.ENTIDAD_TIPO='BAN'\n" +
                "  LEFT JOIN BANCA_GRUPOS C ON B.ENTIDAD_PADRE_CODIGO=C.CODIGO\n" +
                " ORDER BY NIVEL,ENTIDAD_CODIGO",periodo,periodo);
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
            Logger.getLogger(BancaGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public List<Grupo> listarObjetos(String codigos) {
        String queryStr;
        if (codigos.isEmpty()) {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       nivel,\n" +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM banca_grupos\n" +
                " WHERE esta_activo=1\n" +
                " ORDER BY nivel,codigo");
        } else {
            queryStr = String.format("" +
                "SELECT codigo,\n" +
                "       nombre,\n" +
                "       nivel,\n" +
                "       fecha_creacion,\n" +
                "       fecha_actualizacion\n" +
                "  FROM banca_grupos\n" +
                " WHERE esta_activo=1 AND codigo NOT IN (%s)\n" +
                " ORDER BY nivel,codigo",codigos);
        }
        List<Grupo> lista = new ArrayList();
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                int nivel = rs.getInt("nivel");
                Date fechaCreacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_creacion"));
                Date fechaActualizacion = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(rs.getString("fecha_actualizacion"));
                Grupo item = new Grupo(codigo, nombre, null, 0, null, fechaCreacion, fechaActualizacion);
                item.setNivel(nivel);
                lista.add(item);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(BancaGrupoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public int insertarObjeto(String codigo, String nombre, int nivel) {
        String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
        String queryStr = String.format("" +
            "INSERT INTO banca_grupos(codigo,nombre,nivel,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
            "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
            codigo,nombre,nivel,1,fechaStr,fechaStr);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int actualizarObjeto(String codigo, String nombre, int nivel, String codigoAnt) {
        String queryStr = String.format("UPDATE banca_grupos SET codigo='%s',nombre='%s',nivel=%d WHERE codigo='%s'",codigo,nombre,nivel,codigoAnt);
        return ConexionBD.ejecutar(queryStr);
    }
    
    public int eliminarObjeto(String codigo) {
        String queryStr = String.format("DELETE FROM banca_grupos WHERE codigo='%s'",codigo);
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
                "INSERT INTO jerarquia(periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo)\n" +
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
        lista.stream().map((item) -> {
            String codigo = item.getCodigo();
            String nombre = item.getNombre();
            int nivel = item.getNivel();
            String fechaStr = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
            // inserto el nombre
            String queryStr = String.format("" +
                    "INSERT INTO banca_grupos(codigo,nombre,nivel,esta_activo,fecha_creacion,fecha_actualizacion)\n" +
                    "VALUES ('%s','%s',%d,%d,TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'),TO_DATE('%s','yyyy/mm/dd hh24:mi:ss'))",
                    codigo,nombre,nivel,1,fechaStr,fechaStr);
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
                    "INSERT INTO jerarquia(periodo,entidad_codigo,entidad_tipo,nivel,entidad_padre_codigo)\n" +
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
