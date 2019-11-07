package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.ConnectionDB;

public class ReportingDAO {
    ConnectionDB connection;
    
    public ReportingDAO() {
        this.connection = new ConnectionDB();
    }       
    
    public ResultSet dataReporteCuentaPartidaCentroBolsa(int periodo, int repartoTipo) {
        String queryStr = String.format(""+
                "SELECT  a.periodo PERIODO,\n" +
                "        a.cuenta_contable_origen_codigo CODIGO_CUENTA_ORIGEN,\n" +
                "        c.nombre NOMBRE_CUENTA_ORIGEN,\n" +
                "        a.partida_origen_codigo CODIGO_PARTIDA_ORIGEN,\n" +
                "        d.nombre NOMBRE_PARTIDA_ORIGEN,\n" +
                "        a.centro_origen_codigo CODIGO_CENTRO_ORIGEN,\n" +
                "        e.nombre NOMBRE_CENTRO_ORIGEN,\n" +
                "        a.centro_codigo CODIGO_CENTRO_DESTINO,\n" +
                "        b.nombre NOMBRE_CENTRO_DESTINO,\n" +
                "        a.saldo MONTO,\n" +
                "        coalesce(f.driver_codigo,'N/A') CODIGO_DRIVER,\n" +
                "        coalesce(g.nombre,'N/A') NOMBRE_DRIVER,\n" +
                "        CASE\n" +
                "          WHEN a.centro_origen_codigo = a.centro_codigo THEN 'SEMBRADO'\n" +
                "          WHEN a.centro_origen_codigo != a.centro_codigo THEN 'BOLSA'\n" +
                "        END ASIGNACION    \n" +
                "  FROM ms_centro_lineas A\n" +
                "  JOIN ms_centros B ON a.reparto_tipo = '%d' AND a.periodo='%d' AND b.codigo = a.centro_codigo\n" +
                "  JOIN ms_plan_de_cuentas C ON c.codigo = a.cuenta_contable_origen_codigo\n" +
                "  JOIN ms_partidas D ON d.codigo = a.partida_origen_codigo\n" +
                "  JOIN ms_centros E ON e.codigo = a.centro_origen_codigo\n" +
                "  LEFT JOIN ms_bolsa_driver F ON SUBSTR(f.cuenta_contable_codigo,1,3) = SUBSTR(a.cuenta_contable_origen_codigo,1,3) AND SUBSTR(f.cuenta_contable_codigo,5,11) = SUBSTR(a.cuenta_contable_origen_codigo,5,11) AND f.partida_codigo = a.partida_origen_codigo AND f.centro_codigo = a.centro_origen_codigo AND f.periodo = a.PERIODO AND f.reparto_tipo = a.reparto_tipo\n" +
                "  LEFT JOIN ms_drivers G ON g.codigo = f.driver_codigo AND g.driver_tipo_codigo = 'CECO' \n" +
                "WHERE a.iteracion = 0 OR (a.iteracion = -1 AND b.centro_tipo_codigo!='BOLSA' AND b.centro_tipo_codigo!='OFICINA')",
                repartoTipo,periodo);
        return ConexionBD.ejecutarQuery(queryStr);
    }
    
    public ResultSet dataReporteCascada(int periodo, int repartoTipo) {
        String queryStr = String.format(""+
                "SELECT A.PERIODO,\n" +
                "       A.ITERACION,\n" +
                "       A.CUENTA_CONTABLE_ORIGEN_CODIGO CODIGO_CUENTA_INICIAL,\n" +
                "       C.NOMBRE NOMBRE_CUENTA_INICIAL,\n" +
                "       A.PARTIDA_ORIGEN_CODIGO CODIGO_PARTIDA_INICIAL,\n" +
                "       D.NOMBRE NOMBRE_PARTIDA_INICIAL,\n" +
                "       A.CENTRO_ORIGEN_CODIGO CODIGO_CENTRO_INICIAL,\n" +
                "       E.NOMBRE NOMBRE_CENTRO_INICIAL,\n" +
                "       A.ENTIDAD_ORIGEN_CODIGO CODIGO_CENTRO_ORIGEN,\n" +
                "       COALESCE(F.NOMBRE,'N/A') NOMBRE_CENTRO_ORIGEN,\n" +
                "       A.CENTRO_CODIGO CODIGO_CENTRO_DESTINO,\n" +
                "       B.NOMBRE NOMBRE_CENTRO_DESTINO,\n" +
                "       A.SALDO MONTO,\n" +
                "       COALESCE(G.DRIVER_CODIGO,I.DRIVER_CODIGO,'N/A') CODIGO_DRIVER,\n" +
                "       COALESCE(H.NOMBRE,J.NOMBRE,'N/A') NOMBRE_DRIVER\n" +
                "  FROM MS_CENTRO_LINEAS A\n" +
                "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n" +
                "  JOIN MS_PLAN_DE_CUENTAS C ON C.CODIGO=A.CUENTA_CONTABLE_ORIGEN_CODIGO\n" +
                "  JOIN MS_PARTIDAS D ON D.CODIGO=A.PARTIDA_ORIGEN_CODIGO\n" +
                "  JOIN MS_CENTROS E ON E.CODIGO=A.CENTRO_ORIGEN_CODIGO\n" +
                "  LEFT JOIN MS_CENTROS F ON F.CODIGO=A.ENTIDAD_ORIGEN_CODIGO\n" +
                "  LEFT JOIN MS_BOLSA_DRIVER G\n" +
                "    ON SUBSTR(G.CUENTA_CONTABLE_CODIGO,1,3)=SUBSTR(A.CUENTA_CONTABLE_ORIGEN_CODIGO,1,3) AND SUBSTR(G.CUENTA_CONTABLE_CODIGO,5,11)=SUBSTR(A.CUENTA_CONTABLE_ORIGEN_CODIGO,5,11)\n" +
                "   AND G.PARTIDA_CODIGO=A.PARTIDA_ORIGEN_CODIGO AND G.CENTRO_CODIGO=A.ENTIDAD_ORIGEN_CODIGO AND G.PERIODO=A.PERIODO AND G.REPARTO_TIPO=A.REPARTO_TIPO\n" +
                "  LEFT JOIN MS_DRIVERS H\n" +
                "    ON H.CODIGO=G.DRIVER_CODIGO AND H.DRIVER_TIPO_CODIGO='CECO'\n" +
                "  LEFT JOIN MS_ENTIDAD_ORIGEN_DRIVER I\n" +
                "    ON I.ENTIDAD_ORIGEN_CODIGO=A.ENTIDAD_ORIGEN_CODIGO AND I.PERIODO=A.PERIODO AND I.REPARTO_TIPO=A.REPARTO_TIPO\n" +
                "  LEFT JOIN MS_DRIVERS J\n" +
                "    ON J.CODIGO=I.DRIVER_CODIGO AND J.DRIVER_TIPO_CODIGO='CECO'\n" +
                " WHERE A.PERIODO=%d\n" +
                "   AND A.ITERACION!=-2\n" +
                "   AND A.REPARTO_TIPO=%d",
                periodo,repartoTipo);
        return ConexionBD.ejecutarQuery(queryStr);
    }
    
    public ResultSet dataReporteCuentaPartidaCentroPropio(int periodo, int repartoTipo) {
        String queryStr = String.format(""+ 
                "SELECT A.periodo PERIODO,\n" +
                "       a.centro_codigo CODIGO_CENTRO,\n" +
                "       d.nombre NOMBRE_CENTRO,\n" +
                "       a.cuenta_contable_codigo CODIGO_CUENTA_CONTABLE,\n" +
                "       b.nombre NOMBRE_CUENTA_CONTABLE,\n" +
                "       a.partida_codigo CODIGO_PARTIDA,\n" +
                "       c.nombre NOMBRE_PARTIDA,\n" +
                "       a.saldo SALDO,\n" +
                "       '-' CODIGO_CENTRO_ORIGEN,\n" +
                "       '-' NOMBRE_CENTRO_ORIGEN,\n" +
                "       '-' CODIGO_DRIVER_ORIGEN,\n" +
                "       '-' NOMBRE_DRIVER_ORIGEN,\n" +
                "       CASE  \n" +
                "        when d.es_bolsa = 'NO' then 'DIRECTO'\n" +
                "        ELSE 'BOLSA'\n" +
                "      END TIPO_CECO  \n" +
                "FROM cuenta_partida_centro A\n" +
                "join plan_de_cuentas B ON B.codigo = A.cuenta_contable_codigo\n" +
                "join partidas C ON c.codigo = A.partida_codigo\n" +
                "join centros D ON d.codigo = A.centro_codigo\n" +
                "WHERE a.periodo = %d and d.es_bolsa = 'NO'\n" +
                "order by a.cuenta_contable_codigo, a.partida_codigo, a.centro_codigo",
                periodo);
        return ConexionBD.ejecutarQuery(queryStr);
    }
    public ResultSet dataReporteGastoPropioAsignado(int periodo, int repartoTipo) {
        String queryStr;
        queryStr = String.format(""+
                "SELECT a.periodo PERIODO,\n" +
                "      A.centro_codigo CODIGO_CENTRO,\n" +
                "      E.nombre NOMBRE_CENTRO,\n" +
                "      E.nivel NIVEL_CENTRO,\n" +
                "      I.nombre TIPO_CENTRO,\n" +
                "      A.iteracion ITERACION,\n" +
                "      'PROPIO-BOLSA' TIPO_ASIGNACION_GASTO,\n" +
                "      A.entidad_origen_codigo CODIGO_CENTRO_ORIGEN,\n" +
                "      F.nombre NOMBRE_CENTRO_ORIGEN,\n" +
                "      F.nivel NIVEL_CENTRO_ORIGEN,\n" +
                "      J.NOMBRE TIPO_CENTRO_ORIGEN,\n" +
                "      B.cuenta_contable_codigo CODIGO_CUENTA_CONTABLE_ORIGEN,\n" +
                "      H.nombre NOMBRE_CUENTA_CONTABLE_ORIGEN,\n" +
                "      B.partida_codigo CODIGO_PARTIDA_ORIGEN,\n" +
                "      D.nombre NOMBRE_PARTIDA_ORIGEN,\n" +
                "      B.driver_codigo CODIGO_DRIVER,\n" +
                "      G.nombre NOMBRE_DRIVER,\n" +
                "      k.nombre TIPO_GASTO,\n" +
                "      A.saldo SALDO     \n" +
                "FROM centro_lineas A \n" +
                "join bolsa_driver B on a.entidad_origen_codigo = b.centro_codigo AND b.periodo = a.periodo\n" +
                "join driver_lineas C on c.driver_codigo= b.driver_codigo and C.entidad_destino_codigo = a.centro_codigo AND C.PERIODO = B.PERIODO AND A.PERIODO = C.PERIODO\n" +
                "join partidas D on D.codigo = B.partida_codigo AND d.grupo_gasto = a.grupo_gasto\n" +
                "join centros E ON E.codigo = A.centro_codigo\n" +
                "join centros F ON F.codigo = a.entidad_origen_codigo\n" +
                "join drivers G ON G.codigo = B.driver_codigo\n" +
                "JOIN plan_de_cuentas H ON H.codigo = B.cuenta_contable_codigo\n" +
                "JOIN centro_tipos I on e.centro_tipo_codigo = i.codigo\n" +
                "JOIN centro_tipos J on e.centro_tipo_codigo = j.codigo\n" +
                "JOIN grupo_gastos K on k.codigo = a.grupo_gasto\n" +
                "where A.periodo = %d and a.iteracion = 0" +
                "UNION\n" +
                "SELECT  A.periodo PERIODO,\n" +
                "        a.centro_codigo CODIGO_CENTRO,\n" +
                "        d.nombre NOMBRE_CENTRO,\n" +
                "        d.nivel NIVEL_CENTRO,\n" +
                "        e.nombre TIPO_CENTRO,\n" +
                "        -1 ITERACION,\n" +
                "        'PROPIO' TIPO_ASIGNACION_GASTO,\n" +
                "        '-' CODIGO_CENTRO_ORIGEN,\n" +
                "        '-' NOMBRE_CENTRO_ORIGEN,\n" +
                "        d.nivel NIVEL_CENTRO_ORIGEN,\n" +
                "        e.nombre TIPO_CENTRO_ORIGEN,\n" +
                "        a.cuenta_contable_codigo CODIGO_CUENTA_CONTABLE,\n" +
                "         b.nombre NOMBRE_CUENTA_CONTABLE,\n" +
                "         a.partida_codigo CODIGO_PARTIDA,\n" +
                "         c.nombre NOMBRE_PARTIDA,\n" +
                "         '-' CODIGO_DRIVER_ORIGEN,\n" +
                "         '-' NOMBRE_DRIVER_ORIGEN,\n" +
                "         F.nombre TIPO_GASTO,\n" +
                "         a.saldo SALDO\n" +
                "FROM cuenta_partida_centro A\n" +
                "join plan_de_cuentas B ON B.codigo = A.cuenta_contable_codigo\n" +
                "join partidas C ON c.codigo = A.partida_codigo\n" +
                "join centros D ON d.codigo = A.centro_codigo\n" +
                "JOIN centro_tipos E ON E.codigo = d.centro_tipo_codigo\n" +
                "JOIN grupo_gastos F ON F.codigo = c.grupo_gasto\n" +
                "WHERE a.periodo = %d and d.es_bolsa = 'NO'\n" +
                "UNION\n" +
                "select  a.periodo PERIODO,\n" +
                "        a.centro_codigo CODIGO_CENTRO,\n" +
                "        c.nombre NOMBRE_CENTRO,\n" +
                "        c.niveL NIVEL_CENTRO,\n" +
                "        e.nombre TIPO_CENTRO,\n" +
                "        a.iteracion ITERACION,\n" +
                "        'ASIGNADO' TIPO_ASIGNACION_GASTO,\n" +
                "        a.entidad_origen_codigo CODIGO_CENTRO_ORIGEN,\n" +
                "        d.nombre NOMBRE_CENTRO_ORIGEN,\n" +
                "        d.nivel NIVEL_CENTRO_ORIGEN,\n" +
                "        f.nombre TIPO_CENTRO_ORIGEN,\n" +
                "        '-' CODIGO_CUENTA_CONTABLE_ORIGEN,\n" +
                "        '-' NOMBRE_CUENTA_CONTABLE_ORIGEN,\n" +
                "        '-' CODIGO_PARTIDA_ORIGEN,\n" +
                "        '-' N0MBRE_PARTIDA_ORIGEN,\n" +
                "        b.driver_codigo CODIGO_DRIVER,\n" +
                "        h.nombre NOMBRE_DRIVER,\n" +
                "        g.nombre TIPO_GASTO,\n" +
                "        a.saldo SALDO\n" +
                "FROM CENTRO_LINEAS A\n" +
                "JOIN ENTIDAD_ORIGEN_DRIVER B ON A.entidad_origen_codigo = B.entidad_origen_codigo AND A.PERIODO = B.PERIODO\n" +
                "JOIN CENTROS C ON c.codigo = a.centro_codigo\n" +
                "JOIN CENTROS D ON d.codigo = b.entidad_origen_codigo\n" +
                "JOIN centro_tipos E ON e.codigo = c.centro_tipo_codigo\n" +
                "JOIN centro_tipos F ON f.codigo = d.centro_tipo_codigo\n" +
                "JOIN grupo_gastos G ON g.codigo = a.grupo_gasto\n" +
                "join drivers H ON H.codigo = b.driver_codigo\n" +
                "WHERE A.PERIODO = %d\n" +
                "ORDER BY CODIGO_CENTRO" 
                ,periodo,periodo,periodo);
        return ConexionBD.ejecutarQuery(queryStr);
    }
    
    /*public ResultSet dataReporteCascada(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT a.centro_codigo CODIGO_CENTRO,\n" +
                "        E.nombre NOMBRE_CENTRO,\n" +
                "        case  when E.nivel <= 0 then 'NIVEL 999'\n" +
                "              else 'NIVEL '|| TO_CHAR(E.nivel)\n" +
                "        end NIVEL_CENTRO,\n" +
                "        I.NOMBRE TIPO_CENTRO,\n" +
                "        CASE  WHEN A.ITERACION = 0 THEN 'PROPIO'\n" +
                "        END ITERACION,\n" +
                "        a.entidad_origen_codigo CODIGO_CENTRO_ORIGEN,\n" +
                "        F.NOMBRE NOMBRE_CENTRO_ORIGEN,\n" +
                "        B.DRIVER_CODIGO  CODIGO_DRIVER,\n" +
                "        G.NOMBRE NOMBRE_DRIVER,\n" +
                "        SUM(A.SALDO) SALDO\n" +
                "FROM centro_lineas A \n" +
                "join bolsa_driver B on a.entidad_origen_codigo = b.centro_codigo AND b.periodo = a.periodo\n" +
                "join driver_lineas C on c.driver_codigo= b.driver_codigo and C.entidad_destino_codigo = a.centro_codigo AND C.PERIODO = B.PERIODO AND A.PERIODO = C.PERIODO\n" +
                "join partidas D on D.codigo = B.partida_codigo AND d.grupo_gasto = a.grupo_gasto\n" +
                "join centros E ON E.codigo = A.centro_codigo\n" +
                "join centros F ON F.codigo = a.entidad_origen_codigo\n" +
                "join drivers G ON G.codigo = B.driver_codigo\n" +
                "JOIN plan_de_cuentas H ON H.codigo = B.cuenta_contable_codigo\n" +
                "JOIN centro_tipos I on e.centro_tipo_codigo = i.codigo\n" +
                "JOIN centro_tipos J on e.centro_tipo_codigo = j.codigo\n" +
                "JOIN grupo_gastos K on k.codigo = a.grupo_gasto\n" +
                "where A.periodo = %d and a.iteracion = 0\n" +
                "GROUP BY a.centro_codigo, e.nombre, e.nivel, i.nombre, a.iteracion, a.entidad_origen_codigo, f.nombre, b.driver_codigo, g.nombre\n" +
                "UNION\n" +
                "SELECT  A.CENTRO_CODIGO CODIGO_CENTRO,\n" +
                "        D.NOMBRE NOMBRE_CENTRO,\n" +
                "        case  when D.nivel <= 0 then 'NIVEL 999'\n" +
                "              else 'NIVEL '|| TO_CHAR(D.nivel)\n" +
                "        end NIVEL_CENTRO,\n" +
                "        E.NOMBRE TIPO_CENTRO,\n" +
                "        'PROPIO' ITERACION,\n" +
                "        '-' CODIGO_CENTRO_ORIGEN,\n" +
                "        '-' NOMBRE_CENTRO_ORIGEN,\n" +
                "        '-' CODIGO_DRIVER,\n" +
                "        '-' NOMBRE_DRIVER,\n" +
                "        SUM(A.SALDO) SALDO\n" +
                "        \n" +
                "FROM cuenta_partida_centro A\n" +
                "join plan_de_cuentas B ON B.codigo = A.cuenta_contable_codigo\n" +
                "join partidas C ON c.codigo = A.partida_codigo\n" +
                "join centros D ON d.codigo = A.centro_codigo\n" +
                "JOIN centro_tipos E ON E.codigo = d.centro_tipo_codigo\n" +
                "JOIN grupo_gastos F ON F.codigo = c.grupo_gasto\n" +
                "WHERE a.periodo = %d and d.es_bolsa = 'NO'\n" +
                "GROUP BY A.CENTRO_CODIGO, D.NOMBRE,D.NIVEL,E.NOMBRE\n" +
                "union\n" +
                "select  a.centro_codigo CODIGO_CENTRO,\n" +
                "        c.nombre NOMBRE_CENTRO,\n" +
                "        case  when C.nivel <= 0 then 'NIVEL 999'\n" +
                "              else 'NIVEL '|| TO_CHAR(C.nivel)\n" +
                "        end NIVEL_CENTRO,\n" +
                "        E.NOMBRE TIPO_CENTRO,\n" +
                "        CASE  WHEN A.ITERACION > 0 THEN 'CENTRO NIVEL ' ||TO_CHAR(A.ITERACION)\n" +
                "              WHEN A.ITERACION <= 0 THEN 'PROPIO'\n" +
                "        END ITERACION,\n" +
                "        a.entidad_origen_codigo CODIGO_CENTRO_ORIGEN,\n" +
                "        D.NOMBRE NOMBRE_CENTRO_ORIGEN,\n" +
                "        B.DRIVER_CODIGO  CODIGO_DRIVER,\n" +
                "        H.NOMBRE NOMBRE_DRIVER,\n" +
                "        SUM(A.SALDO) SALDO       \n" +
                "FROM CENTRO_LINEAS A\n" +
                "JOIN ENTIDAD_ORIGEN_DRIVER B ON A.entidad_origen_codigo = B.entidad_origen_codigo AND A.PERIODO = B.PERIODO\n" +
                "JOIN CENTROS C ON c.codigo = a.centro_codigo\n" +
                "JOIN CENTROS D ON d.codigo = b.entidad_origen_codigo\n" +
                "JOIN centro_tipos E ON e.codigo = c.centro_tipo_codigo\n" +
                "join drivers H ON H.codigo = b.driver_codigo\n" +
                "WHERE A.PERIODO = %d\n" +
                "GROUP BY A.CENTRO_CODIGO, C.NOMBRE, C.NIVEL, a.iteracion, e.nombre,a.entidad_origen_codigo, d.nombre, b.driver_codigo,h.nombre \n" +
                "ORDER BY NIVEL_CENTRO,CODIGO_CENTRO,ITERACION,CODIGO_CENTRO_ORIGEN",
                periodo,periodo,periodo);
        return ConexionBD.ejecutarQuery(queryStr);
    }*/
    
    public int numeroNiveles(int periodo, String objetoTipo, int repartoTipo) throws SQLException {
        ResultSet rs = ConexionBD.ejecutarQuery(String.format("SELECT COALESCE(MAX(NIVEL),0)+1 NIVELES FROM MS_JERARQUIA WHERE PERIODO=%d AND ENTIDAD_TIPO='%s' AND REPARTO_TIPO=%d",periodo,objetoTipo,repartoTipo));
        rs.next();
        return rs.getInt("NIVELES");
    }
    
    public ResultSet dataReporteObjetos(int periodo, int repartoTipo, int oficinaNiveles, int bancaNiveles, int productoNiveles) {
        String queryStr = "SELECT ";
        
        for (int i = 1; i <= oficinaNiveles; ++i) {
            queryStr += String.format("COALESCE(C%d.CODIGO,'SIN ASIGNAR') OFICINA_CODIGO_N%d,",i,i);
            queryStr += String.format("COALESCE(C%d.NOMBRE,'SIN ASIGNAR') OFICINA_NOMBRE_N%d,\n",i,i);
        }
        queryStr += "       A0.OFICINA_CODIGO,C.NOMBRE OFICINA_NOMBRE,\n";
        
        for (int i = 1; i <= bancaNiveles; ++i) {
            queryStr += String.format("       COALESCE(D%d.CODIGO,'SIN ASIGNAR') BANCA_CODIGO_N%d,",i,i);
            queryStr += String.format("COALESCE(D%d.NOMBRE,'SIN ASIGNAR') BANCA_NOMBRE_N%d,\n",i,i);
        }
        queryStr += "       A0.BANCA_CODIGO,D.NOMBRE BANCA_NOMBRE,\n";
        
        for (int i = 1; i <= productoNiveles; ++i) {
            queryStr += String.format("       COALESCE(E%d.CODIGO,'SIN ASIGNAR') PRODUCTO_CODIGO_N%d,",i,i);
            queryStr += String.format("COALESCE(E%d.NOMBRE,'SIN ASIGNAR') PRODUCTO_NOMBRE_N%d,\n",i,i);
        }
        queryStr += "       A0.PRODUCTO_CODIGO,E.NOMBRE PRODUCTO_NOMBRE,\n";
        
        queryStr += "" +
                    "       A0.ENTIDAD_ORIGEN_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                    "       F.NOMBRE CENTRO_ORIGEN_NOMBRE,\n" +
                    "       A0.SALDO,\n" +
                    "       B.DRIVER_CODIGO,\n" +
                    "       G.NOMBRE DRIVER_NOMBRE\n" +
                    "  FROM OBCO_LINEAS A0\n" +
                    "  LEFT JOIN ENTIDAD_ORIGEN_DRIVER B ON A0.ENTIDAD_ORIGEN_CODIGO=B.ENTIDAD_ORIGEN_CODIGO AND A0.PERIODO=B.PERIODO\n" +
                    "  JOIN OFICINAS C ON A0.OFICINA_CODIGO=C.CODIGO\n" +
                    "  JOIN BANCAS D ON A0.BANCA_CODIGO=D.CODIGO\n" +
                    "  JOIN PRODUCTOS E ON A0.PRODUCTO_CODIGO=E.CODIGO\n" +
                    "  JOIN CENTROS F ON A0.ENTIDAD_ORIGEN_CODIGO=F.CODIGO\n" +
                    "  JOIN DRIVERS G ON B.DRIVER_CODIGO=G.CODIGO\n";
        
        for (int i = 1; i <= oficinaNiveles; ++i) {
            if (i == 1)
                queryStr += String.format("  LEFT JOIN JERARQUIA HC%d ON A0.OFICINA_CODIGO=HC%d.ENTIDAD_CODIGO AND A0.PERIODO=HC%d.PERIODO AND HC%d.ENTIDAD_TIPO='OFI' AND HC%d.NIVEL=%d\n",i,i,i,i,i,i-1);
            else
                queryStr += String.format("  LEFT JOIN JERARQUIA HC%d ON HC%d.ENTIDAD_PADRE_CODIGO=HC%d.ENTIDAD_CODIGO AND A0.PERIODO=HC%d.PERIODO AND HC%d.ENTIDAD_TIPO='OFI' AND HC%d.NIVEL=%d\n",i,i-1,i,i,i,i,i-1);
            queryStr += String.format("  LEFT JOIN OFICINA_GRUPOS C%d ON HC%d.ENTIDAD_PADRE_CODIGO=C%d.CODIGO\n",i,i,i);
        }
        for (int i = 1; i <= bancaNiveles; ++i) {
            if (i == 1)
                queryStr += String.format("  LEFT JOIN JERARQUIA HD%d ON A0.BANCA_CODIGO=HD%d.ENTIDAD_CODIGO AND A0.PERIODO=HD%d.PERIODO AND HD%d.ENTIDAD_TIPO='BAN' AND HD%d.NIVEL=%d\n",i,i,i,i,i,i-1);
            else
                queryStr += String.format("  LEFT JOIN JERARQUIA HD%d ON HD%d.ENTIDAD_PADRE_CODIGO=HD%d.ENTIDAD_CODIGO AND A0.PERIODO=HD%d.PERIODO AND HD%d.ENTIDAD_TIPO='BAN' AND HD%d.NIVEL=%d\n",i,i-1,i,i,i,i,i-1);
            queryStr += String.format("  LEFT JOIN BANCA_GRUPOS D%d ON HD%d.ENTIDAD_PADRE_CODIGO=D%d.CODIGO\n",i,i,i);
        }
        for (int i = 1; i <= productoNiveles; ++i) {
            if (i == 1)
                queryStr += String.format("  LEFT JOIN JERARQUIA HE%d ON A0.PRODUCTO_CODIGO=HE%d.ENTIDAD_CODIGO AND A0.PERIODO=HE%d.PERIODO AND HE%d.ENTIDAD_TIPO='PRO' AND HE%d.NIVEL=%d\n",i,i,i,i,i,i-1);
            else
                queryStr += String.format("  LEFT JOIN JERARQUIA HE%d ON HE%d.ENTIDAD_PADRE_CODIGO=HE%d.ENTIDAD_CODIGO AND A0.PERIODO=HE%d.PERIODO AND HE%d.ENTIDAD_TIPO='PRO' AND HE%d.NIVEL=%d\n",i,i-1,i,i,i,i,i-1);
            queryStr += String.format("  LEFT JOIN PRODUCTO_GRUPOS E%d ON HE%d.ENTIDAD_PADRE_CODIGO=E%d.CODIGO\n",i,i,i);
        }        
        queryStr += String.format(" WHERE A0.PERIODO=%d AND A0.REPARTO_TIPO=%d",periodo,repartoTipo);
        queryStr +="";
        /*queryStr += String.format("" +                
                "SELECT " +
                "       A.OFICINA_CODIGO,C.NOMBRE OFICINA_NOMBRE,\n" +
                "       A.BANCA_CODIGO,D.NOMBRE BANCA_NOMBRE,\n" +
                "       A.PRODUCTO_CODIGO,E.NOMBRE PRODUCTO_NOMBRE,\n" +
                "       A.ENTIDAD_ORIGEN_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       F.NOMBRE CENTRO_ORIGEN_NOMBRE,\n" +
                "       A.SALDO,\n" +
                "       B.DRIVER_CODIGO,\n" +
                "       G.NOMBRE DRIVER_NOMBRE\n" +
                "  FROM OBCO_LINEAS A\n" +
                "  JOIN ENTIDAD_ORIGEN_DRIVER B ON A.ENTIDAD_ORIGEN_CODIGO=B.ENTIDAD_ORIGEN_CODIGO\n" +
                "  JOIN OFICINAS C ON A.OFICINA_CODIGO=C.CODIGO\n" +
                "  JOIN BANCAS D ON A.BANCA_CODIGO=D.CODIGO\n" +
                "  JOIN PRODUCTOS E ON A.PRODUCTO_CODIGO=E.CODIGO\n" +
                "  JOIN CENTROS F ON A.ENTIDAD_ORIGEN_CODIGO=F.CODIGO\n" +
                "  JOIN DRIVERS G ON B.DRIVER_CODIGO=G.CODIGO\n" +
                " WHERE A0.PERIODO=%d AND A.REPARTO_TIPO=%d",
                periodo,repartoTipo);*/
        
        /*queryStr = String.format("" +
                "SELECT " +
                "       A.OFICINA_CODIGO,E.NOMBRE OFICINA_NOMBRE,\n" +
                "       A.BANCA_CODIGO,D.NOMBRE BANCA_NOMBRE,\n" +
                "       A.PRODUCTO_CODIGO,C.NOMBRE PRODUCTO_NOMBRE,\n" +
                "       A.ENTIDAD_ORIGEN_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       F.NOMBRE CENTRO_ORIGEN_NOMBRE,\n" +
                "       A.SALDO,\n" +
                "       B.DRIVER_CODIGO,\n" +
                "       G.NOMBRE DRIVER_NOMBRE\n" +
                "  FROM OBCO_LINEAS A\n" +
                "  JOIN ENTIDAD_ORIGEN_DRIVER B ON A.ENTIDAD_ORIGEN_CODIGO=B.ENTIDAD_ORIGEN_CODIGO\n" +
                "  JOIN PRODUCTOS C ON A.PRODUCTO_CODIGO=C.CODIGO\n" +
                "  JOIN BANCAS D ON A.BANCA_CODIGO=D.CODIGO\n" +
                "  JOIN OFICINAS E ON A.OFICINA_CODIGO=E.CODIGO\n" +
                "  JOIN CENTROS F ON A.ENTIDAD_ORIGEN_CODIGO=F.CODIGO\n" +
                "  JOIN DRIVERS G ON B.DRIVER_CODIGO=G.CODIGO\n" +
                " WHERE A.PERIODO=%d AND B.PERIODO=%d AND A.REPARTO_TIPO=%d",
                periodo,periodo,repartoTipo);*/
        return ConexionBD.ejecutarQuery(queryStr);
    }

    public void dataReporteObjetosGastoAdmOpeBorrarTablas() {
        ConexionBD.ejecutar("TRUNCATE TABLE JMD_REP_ADM_OPE_M");
        ConexionBD.ejecutar("TRUNCATE TABLE JMD_REP_ADM_OPE_ITER_0_ACC");
        for (int i = 1; i <= 6; ++i) {
            ConexionBD.ejecutar("TRUNCATE TABLE JMD_REP_ADM_OPE_ITER_"+i);
            ConexionBD.ejecutar("TRUNCATE TABLE JMD_REP_ADM_OPE_ITER_"+i+"_ACC");
        }
        ConexionBD.ejecutar("TRUNCATE TABLE JMD_REP_ADM_OPE_OBCO_F");
    }
    
    public void dataReporteObjetosGastoAdmOpeLlenarTablas(int periodo, int repartoTipo) {
        String query;
        query = String.format("" +
                "INSERT INTO JMD_REP_ADM_OPE_M\n" +
                "SELECT B.periodo,\n" +
                "       A.codigo centro_codigo,\n" +
                "       A.nivel centro_nivel\n" +
                "  FROM centros A\n" +
                "  JOIN centro_lineas B ON A.codigo=B.centro_codigo AND B.iteracion=-1\n" +
                " WHERE B.periodo=%d AND A.reparto_tipo=%d\n" +
                " GROUP BY B.periodo,A.codigo,A.nivel",
                periodo,repartoTipo);
        ConexionBD.ejecutar(query);
        query = "INSERT INTO JMD_REP_ADM_OPE_ITER_0_ACC\n" +
                "SELECT A.periodo,\n" +
                "       A.centro_codigo,\n" +
                "       A.centro_nivel,\n" +
                "       SUM(CASE WHEN SUBSTR(B.entidad_origen_codigo,0,2)='44' THEN B.saldo ELSE 0 END) GASTOS_OPERATIVOS,\n" +
                "       SUM(CASE WHEN SUBSTR(B.entidad_origen_codigo,0,2)='45' THEN B.saldo ELSE 0 END) GASTOS_ADMINISTRATIVOS,\n" +
                "       SUM(COALESCE(B.saldo,0)) GASTOS_TOTALES\n" +
                "  FROM JMD_REP_ADM_OPE_M A\n" +
                "  LEFT JOIN centro_lineas B ON A.centro_codigo=B.centro_codigo AND A.periodo=B.periodo AND B.iteracion=0\n" +
                " GROUP BY A.periodo,A.centro_codigo,A.centro_nivel";
        ConexionBD.ejecutar(query);
        //CASCADA
        for (int i = 1; i <= 6; ++i) {
            query = String.format("" +
                    "INSERT INTO JMD_REP_ADM_OPE_ITER_%d\n" +
                    "SELECT A.periodo,\n" +
                    "       A.centro_codigo,\n" +
                    "       SUM(CASE WHEN B.GASTOS_TOTALES=0 THEN 0 ELSE A.saldo*B.GASTOS_OPERATIVOS/B.GASTOS_TOTALES END) GASTOS_OPERATIVOS,\n" +
                    "       SUM(CASE WHEN B.GASTOS_TOTALES=0 THEN 0 ELSE A.saldo*B.GASTOS_ADMINISTRATIVOS/B.GASTOS_TOTALES END) GASTOS_ADMINISTRATIVOS,\n" +
                    "       SUM(A.saldo) GASTOS_TOTALES\n" +
                    "  FROM centro_lineas A\n" +
                    "  JOIN JMD_REP_ADM_OPE_ITER_%d_ACC B ON A.entidad_origen_codigo=B.centro_codigo AND A.periodo=B.periodo\n" +
                    " WHERE A.iteracion=%d\n" +
                    " GROUP BY A.periodo,A.centro_codigo",
                    i, i-1, i);
            ConexionBD.ejecutar(query);
            query = String.format("" +
                    "INSERT INTO JMD_REP_ADM_OPE_ITER_%d_ACC\n" +
                    "SELECT A.periodo,\n" +
                    "       A.centro_codigo,\n" +
                    "       A.centro_nivel,\n" +
                    "       CASE WHEN A.centro_nivel=%d THEN 0 ELSE A.GASTOS_ADMINISTRATIVOS+COALESCE(B.GASTOS_ADMINISTRATIVOS,0) END GASTOS_ADMINISTRATIVOS,\n" +
                    "       CASE WHEN A.centro_nivel=%d THEN 0 ELSE A.GASTOS_OPERATIVOS+COALESCE(B.GASTOS_OPERATIVOS,0) END GASTOS_OPERATIVOS,\n" +
                    "       CASE WHEN A.centro_nivel=%d THEN 0 ELSE A.GASTOS_TOTALES+COALESCE(B.GASTOS_TOTALES,0) END GASTOS_TOTALES\n" +
                    "  FROM JMD_REP_ADM_OPE_ITER_%d_ACC A\n" +
                    "  LEFT JOIN JMD_REP_ADM_OPE_ITER_%d B ON A.centro_codigo=B.centro_codigo",
                    i,i,i,i,i-1,i);
            ConexionBD.ejecutar(query);
        }        
        //SALDO DE LOS CECOS HASTA LOS OBJETOS DE COSTOS
        query = String.format("" +
                "INSERT INTO JMD_REP_ADM_OPE_OBCO_F\n" +
                "SELECT A.PERIODO,\n" +
                "       A.OFICINA_CODIGO,C.NOMBRE OFICINA_NOMBRE,\n" +
                "       A.BANCA_CODIGO,D.NOMBRE BANCA_NOMBRE,\n" +
                "       A.PRODUCTO_CODIGO,E.NOMBRE PRODUCTO_NOMBRE,\n" +
                "       CASE WHEN B.GASTOS_TOTALES=0 THEN 0 ELSE A.SALDO*B.GASTOS_OPERATIVOS/B.GASTOS_TOTALES END GASTOS_OPERATIVOS,\n" +
                "       CASE WHEN B.GASTOS_TOTALES=0 THEN 0 ELSE A.SALDO*B.GASTOS_ADMINISTRATIVOS/B.GASTOS_TOTALES END GASTOS_ADMINISTRATIVOS,\n" +
                "       A.SALDO GASTOS_TOTALES,\n" +
                "       A.ENTIDAD_ORIGEN_CODIGO CECO_ORIGEN_CODIGO,\n" +
                "       G.NOMBRE CECO_ORIGEN_NOMBRE,\n" +
                "       F.DRIVER_CODIGO,\n" +
                "       H.NOMBRE DRIVER_NOMBRE\n" +
                "  FROM OBCO_LINEAS A\n" +
                "  JOIN JMD_REP_ADM_OPE_ITER_6_ACC B ON A.ENTIDAD_ORIGEN_CODIGO=B.CENTRO_CODIGO AND A.PERIODO=B.PERIODO\n" +
                "  JOIN OFICINAS C ON A.OFICINA_CODIGO=C.CODIGO\n" +
                "  JOIN BANCAS D ON A.BANCA_CODIGO=D.CODIGO\n" +
                "  JOIN PRODUCTOS E ON A.PRODUCTO_CODIGO=E.CODIGO\n" +
                "  JOIN ENTIDAD_ORIGEN_DRIVER F ON A.ENTIDAD_ORIGEN_CODIGO=F.ENTIDAD_ORIGEN_CODIGO AND B.PERIODO=F.PERIODO\n" +
                "  JOIN CENTROS G ON A.ENTIDAD_ORIGEN_CODIGO=G.CODIGO AND A.REPARTO_TIPO=G.REPARTO_TIPO\n" +
                "  JOIN DRIVERS H ON F.DRIVER_CODIGO=H.CODIGO AND A.REPARTO_TIPO=H.REPARTO_TIPO\n" +
                " WHERE A.REPARTO_TIPO=%d", repartoTipo);
        ConexionBD.ejecutar(query);
    }
    
    public ResultSet dataReporteObjetosCostos(int periodo, int repartoTipo) {
        String periodoStr = repartoTipo == 1 ? "A.PERIODO" : "TRUNC(A.PERIODO/100)*100";
        String queryStr = String.format("" +
                "SELECT A.PERIODO,\n" +
                "       A.CUENTA_CONTABLE_ORIGEN_CODIGO CODIGO_CUENTA_INICIAL,\n" +
                "       G.NOMBRE NOMBRE_CUENTA_INICIAL,\n" +
                "       A.PARTIDA_ORIGEN_CODIGO CODIGO_PARTIDA_INICIAL,\n" +
                "       H.NOMBRE NOMBRE_PARTIDA_INICIAL,\n" +
                "       A.CENTRO_ORIGEN_CODIGO CODIGO_CENTRO_INICIAL,\n" +
                "       I.NOMBRE NOMBRE_CENTRO_INICIAL,\n" +
                "       A.PRODUCTO_CODIGO CODIGO_PRODUCTO,\n" +
                "       B.NOMBRE NOMBRE_PRODUCTO,\n" +
                "       COALESCE(J2.CODIGO,'N/A') CODIGO_LINEA,\n" +
                "       COALESCE(J2.NOMBRE,'N/A') NOMBRE_LINEA,\n" +
                "       A.SUBCANAL_CODIGO CODIGO_SUBCANAL,\n" +
                "       C.NOMBRE NOMBRE_SUBCANAL,\n" +
                "       COALESCE(K2.CODIGO,'N/A') CODIGO_CANAL,\n" +
                "       COALESCE(K2.NOMBRE,'N/A') NOMBRE_CANAL,\n" +
                "       A.ENTIDAD_ORIGEN_CODIGO CODIGO_CENTRO_ORIGEN,\n" +
                "       COALESCE(D.NOMBRE,'N/A') NOMBRE_CENTRO_ORIGEN,\n" +
                "       E.NOMBRE GRUPO_GASTO,\n" +
                "       A.SALDO MONTO,\n" +
                "       A.DRIVER_CODIGO CODIGO_DRIVER,\n" +
                "       F.NOMBRE NOMBRE_DRIVER\n" +
                "  FROM MS_OBJETO_LINEAS A\n" +
                "  JOIN MS_PRODUCTOS B ON B.CODIGO=A.PRODUCTO_CODIGO\n" +
                "  JOIN MS_SUBCANALS C ON C.CODIGO=A.SUBCANAL_CODIGO\n" +
                "  JOIN MS_CENTROS D ON D.CODIGO=A.ENTIDAD_ORIGEN_CODIGO\n" +
                "  JOIN MS_GRUPO_GASTOS E ON E.CODIGO=A.GRUPO_GASTO\n" +
                "  JOIN MS_DRIVERS F ON F.CODIGO=A.DRIVER_CODIGO\n" +
                "  LEFT JOIN MS_PLAN_DE_CUENTAS G ON A.CUENTA_CONTABLE_ORIGEN_CODIGO=G.CODIGO\n" +
                "  LEFT JOIN MS_PARTIDAS H ON A.PARTIDA_ORIGEN_CODIGO=H.CODIGO\n" +
                "  LEFT JOIN MS_CENTROS I ON A.CENTRO_ORIGEN_CODIGO=I.CODIGO\n" +
                "  LEFT JOIN MS_JERARQUIA J1 ON J1.PERIODO=%s AND J1.REPARTO_TIPO=A.REPARTO_TIPO AND J1.ENTIDAD_TIPO='PRO' AND J1.ENTIDAD_CODIGO = A.PRODUCTO_CODIGO\n" +
                "  LEFT JOIN MS_PRODUCTO_GRUPOS J2 ON J2.CODIGO=J1.ENTIDAD_PADRE_CODIGO\n" +
                "  LEFT JOIN MS_JERARQUIA K1 ON K1.PERIODO=%s AND K1.REPARTO_TIPO=A.REPARTO_TIPO AND K1.ENTIDAD_TIPO='SCA' AND K1.ENTIDAD_CODIGO = A.SUBCANAL_CODIGO\n" +
                "  LEFT JOIN MS_SUBCANAL_GRUPOS K2 ON K2.CODIGO=K1.ENTIDAD_PADRE_CODIGO\n" +
                "WHERE A.PERIODO=%d\n" +
                "  AND A.REPARTO_TIPO=%d",
                periodoStr, periodoStr, periodo, repartoTipo);
        return ConexionBD.ejecutarQuery(queryStr);
    }
    
    public ResultSet dataReporteGastosOperacionesDeCambio(int periodo) {
        String queryStr;
        
        ConexionBD.ejecutar("TRUNCATE TABLE TP_OFICINAS_OC_1");
        queryStr = String.format("" +
                "INSERT INTO TP_OFICINAS_OC_1\n" +
                "SELECT B.CODIGO OFICINA_CODIGO,\n" +
                "       B.NOMBRE OFICINA_NOMBRE,\n" +
                "       SUM(CASE WHEN A.ENTIDAD_ORIGEN_CODIGO='%s' THEN A.SALDO\n" +
                "                ELSE 0\n" +
                "       END)INGRESOS_GIROS,\n" +
                "       SUM(CASE WHEN A.ENTIDAD_ORIGEN_CODIGO IN('%s','%s') THEN A.SALDO\n" +
                "                ELSE 0\n" +
                "       END)INGRESOS_OPE_DE_CAMBIO,\n" +
                "       SUM(CASE WHEN A.ENTIDAD_ORIGEN_CODIGO='%s' THEN A.SALDO\n" +
                "                ELSE 0\n" +
                "       END)GASTOS_OPE_DE_CAMBIO\n" +
                "  FROM OBCO_LINEAS A\n" +
                "  JOIN OFICINAS B ON A.OFICINA_CODIGO=B.CODIGO\n" +
                " WHERE A.PERIODO=%d AND B.CODIGO!='%s'\n" +
                " GROUP BY B.CODIGO,B.NOMBRE",
                "80054001","80054002","80054013","70000027",periodo,"790");
        ConexionBD.ejecutar(queryStr);
        
        ConexionBD.ejecutar("TRUNCATE TABLE TP_OFICINAS_OC_2");
        queryStr = String.format("" +
                "INSERT INTO TP_OFICINAS_OC_2\n" +
                "SELECT A.OFICINA_CODIGO,\n" +
                "       A.OFICINA_NOMBRE,\n" +
                "       A.INGRESOS_OPE_DE_CAMBIO,\n" +
                "       A.INGRESOS_GIROS,\n" +
                "       A.GASTOS_OPE_DE_CAMBIO,\n" +
                "       B.GASTOS_TOTAL*((A.INGRESOS_OPE_DE_CAMBIO+A.INGRESOS_GIROS)/B.INGRESOS_TOTAL)GASTOS_OPE_DE_CAMBIO_AJUSTADO\n" +
                "  FROM TP_OFICINAS_OC_1 A\n" +
                "  JOIN (SELECT SUM(INGRESOS_OPE_DE_CAMBIO+INGRESOS_GIROS)INGRESOS_TOTAL,\n" +
                "               SUM(GASTOS_OPE_DE_CAMBIO)GASTOS_TOTAL\n" +
                "          FROM TP_OFICINAS_OC_1) B ON 1=1");
        ConexionBD.ejecutar(queryStr);
        
        return ConexionBD.ejecutarQuery("SELECT * FROM TP_OFICINAS_OC_2");
    }
    
    public boolean existeInformacionReporteBolsasOficinas(int periodo, int repartoTipo) {
        return existeInformacionReporteTabla(periodo, repartoTipo, 1);
    }
    
    public boolean existeInformacionReporteCascada(int periodo, int repartoTipo)  {
        return existeInformacionReporteTabla(periodo, repartoTipo, 2);
    }
    
    public boolean existeInformacionReporteObjetos(int periodo, int repartoTipo) {
        return existeInformacionReporteTabla(periodo, repartoTipo, 3);
    }
    
    public boolean existeInformacionReporteTabla(int periodo, int repartoTipo, int nroReporte) {
        try {
            String queryStr = String.format("SELECT COUNT(1) CNT FROM MS_EJECUCIONES_REPORTES WHERE PERIODO=%d AND REPARTO_TIPO=%d AND NRO_REPORTE=%d", periodo, repartoTipo, nroReporte);
            ResultSet rs = ConexionBD.ejecutarQuery(queryStr);
            while(rs.next())
                return rs.getInt("CNT") != 0;
        } catch (SQLException ex) {
            
        }
        return false;
        /*try {
            String queryStr = String.format("SELECT COUNT(1) CNT FROM %s PARTITION (P_%d) WHERE REPARTO_TIPO=%d", tabla, periodo, repartoTipo);
            ResultSet rs = ConexionBD.ejecutarQuery(queryStr);
            while(rs.next())
                return rs.getInt("CNT") >= 1;
        } catch (SQLException ex) {
            
        }
        return false;*/
    }
    
    public void insertarGeneracionReporte(int periodo, int repartoTipo, int nroReporte) {
        String queryStr = String.format("" +
                "DELETE FROM MS_EJECUCIONES_REPORTES\n" +
                " WHERE PERIODO=%d AND REPARTO_TIPO=%d AND NRO_REPORTE=%d",
                periodo, repartoTipo, nroReporte);
        ConexionBD.ejecutarQuery(queryStr);
        
        queryStr = String.format("" +
                "INSERT INTO MS_EJECUCIONES_REPORTES(PERIODO,REPARTO_TIPO,NRO_REPORTE,FECHA)\n" +
                "SELECT %d PERIODO, %d REPARTO_TIPO, %d NRO_REPORTE, SYSDATE FECHA\n" +
                "  FROM DUAL",
                periodo, repartoTipo, nroReporte);
        ConexionBD.ejecutarQuery(queryStr);
    }    
    
    public void generarReporteBolsasOficinas(int periodo, int repartoTipo, String nombreTabla) {
        ConexionBD.ejecutarQuery(String.format("ALTER TABLE %s TRUNCATE PARTITION P_%d DROP STORAGE", nombreTabla, periodo));
        String periodoStr = repartoTipo == 1 ? "A.PERIODO" : "TRUNC(A.PERIODO/100)*100";
        String queryStr = String.format("" +
                "INSERT INTO %s(REPARTO_TIPO,PERIODO,CUENTA_CONTABLE_ORIGEN_CODIGO,CUENTA_CONTABLE_ORIGEN_NOMBRE,PARTIDA_ORIGEN_CODIGO,PARTIDA_ORIGEN_NOMBRE,CENTRO_ORIGEN_CODIGO,CENTRO_ORIGEN_NOMBRE,CENTRO_ORIGEN_NIVEL,CENTRO_ORIGEN_TIPO,CENTRO_DESTINO_CODIGO,CENTRO_DESTINO_NOMBRE,CENTRO_DESTINO_NIVEL,CENTRO_DESTINO_TIPO,MONTO,DRIVER_CODIGO,DRIVER_NOMBRE,ASIGNACION)\n" +
                "SELECT A.REPARTO_TIPO,\n" +
                "       A.PERIODO PERIODO,\n" +
                "       A.CUENTA_CONTABLE_ORIGEN_CODIGO CUENTA_CONTABLE_ORIGEN_CODIGO,\n" +
                "       C.NOMBRE CUENTA_CONTABLE_ORIGEN_NOMBRE,\n" +
                "       A.PARTIDA_ORIGEN_CODIGO PARTIDA_ORIGEN_CODIGO,\n" +
                "       D.NOMBRE PARTIDA_ORIGEN_NOMBRE,\n" +
                "       A.CENTRO_ORIGEN_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       E.NOMBRE CENTRO_ORIGEN_NOMBRE,\n" +
                "       E.NIVEL CENTRO_ORIGEN_NIVEL,\n" +
                "       E.CENTRO_TIPO_CODIGO CENTRO_ORIGEN_TIPO,\n" +
                "       A.CENTRO_CODIGO CENTRO_DESTINO_CODIGO,\n" +
                "       B.NOMBRE CENTRO_DESTINO_NOMBRE,\n" +
                "       B.NIVEL CENTRO_DESTINO_NIVEL,\n" +
                "       B.CENTRO_TIPO_CODIGO CENTRO_DESTINO_TIPO,\n" +
                "       A.SALDO MONTO,\n" +
                "       COALESCE(F.DRIVER_CODIGO,'N/A') DRIVER_CODIGO,\n" +
                "       COALESCE(G.NOMBRE,'N/A') DRIVER_NOMBRE,\n" +
                "       CASE WHEN A.CENTRO_ORIGEN_CODIGO=A.CENTRO_CODIGO THEN 'SEMBRADO'\n" +
                "            WHEN A.CENTRO_ORIGEN_CODIGO!=A.CENTRO_CODIGO THEN 'BOLSA'\n" +
                "       END ASIGNACION\n" +
                "  FROM MS_CASCADA A\n" +
                "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n" +
                "  JOIN MS_PLAN_DE_CUENTAS C ON C.CODIGO=A.CUENTA_CONTABLE_ORIGEN_CODIGO\n" +
                "  JOIN MS_PARTIDAS D ON D.CODIGO=A.PARTIDA_ORIGEN_CODIGO\n" +
                "  JOIN MS_CENTROS E ON E.CODIGO=A.CENTRO_ORIGEN_CODIGO\n" +
                "  LEFT JOIN MS_BOLSA_DRIVER F ON SUBSTR(F.CUENTA_CONTABLE_CODIGO,1,3)=SUBSTR(A.CUENTA_CONTABLE_ORIGEN_CODIGO,1,3) AND SUBSTR(F.CUENTA_CONTABLE_CODIGO,5,11)=SUBSTR(a.cuenta_contable_origen_codigo,5,11) AND F.PARTIDA_CODIGO=A.PARTIDA_ORIGEN_CODIGO AND F.CENTRO_CODIGO=A.CENTRO_ORIGEN_CODIGO AND F.PERIODO=%s AND F.REPARTO_TIPO=A.REPARTO_TIPO\n" +
                "  LEFT JOIN MS_DRIVERS G ON G.CODIGO=F.DRIVER_CODIGO AND G.DRIVER_TIPO_CODIGO='CECO'\n" +
                " WHERE A.ITERACION=0 OR (A.ITERACION=-1 AND B.CENTRO_TIPO_CODIGO!='BOLSA' AND B.CENTRO_TIPO_CODIGO!='OFICINA')",
                nombreTabla, periodoStr);
        ConexionBD.ejecutarQuery(queryStr);
        
        insertarGeneracionReporte(periodo, repartoTipo, 1);
    }
    
    public void generarReporteCascada(int periodo, int repartoTipo, String nombreTabla) {
        ConexionBD.ejecutarQuery(String.format("ALTER TABLE %s TRUNCATE PARTITION P_%d DROP STORAGE", nombreTabla, periodo));
        String periodoStr = repartoTipo == 1 ? "A.PERIODO" : "TRUNC(A.PERIODO/100)*100";
        String queryStr = String.format("" +
                "INSERT INTO %s(REPARTO_TIPO,PERIODO,ITERACION,CUENTA_CONTABLE_INICIAL_CODIGO,CUENTA_CONTABLE_INICIAL_NOMBRE,PARTIDA_INICIAL_CODIGO,PARTIDA_INICIAL_NOMBRE,CENTRO_INICIAL_CODIGO,CENTRO_INICIAL_NOMBRE,CENTRO_INICIAL_NIVEL,CENTRO_INICIAL_TIPO,CENTRO_ORIGEN_CODIGO,CENTRO_ORIGEN_NOMBRE,CENTRO_ORIGEN_NIVEL,CENTRO_ORIGEN_TIPO,CENTRO_DESTINO_CODIGO,CENTRO_DESTINO_NOMBRE,CENTRO_DESTINO_NIVEL,CENTRO_DESTINO_TIPO,MONTO,DRIVER_CODIGO,DRIVER_NOMBRE)\n" +
                "SELECT A.REPARTO_TIPO,\n" +
                "       A.PERIODO,\n" +
                "       A.ITERACION,\n" +
                "       A.CUENTA_CONTABLE_ORIGEN_CODIGO CUENTA_CONTABLE_INICIAL_CODIGO,\n" +
                "       C.NOMBRE CUENTA_CONTABLE_INICIAL_NOMBRE,\n" +
                "       A.PARTIDA_ORIGEN_CODIGO PARTIDA_INICIAL_CODIGO,\n" +
                "       D.NOMBRE PARTIDA_INICIAL_NOMBRE,\n" +
                "       A.CENTRO_ORIGEN_CODIGO CENTRO_INICIAL_CODIGO,\n" +
                "       E.NOMBRE CENTRO_INICIAL_NOMBRE,\n" +
                "       E.NIVEL CENTRO_INICIAL_NIVEL,\n" +
                "       E.CENTRO_TIPO_CODIGO CENTRO_INICIAL_TIPO,\n" +
                "       A.ENTIDAD_ORIGEN_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       COALESCE(F.NOMBRE,'N/A') CENTRO_ORIGEN_NOMBRE,\n" +
                "       COALESCE(F.NIVEL,-1) CENTRO_ORIGEN_NIVEL,\n" +
                "       COALESCE(F.CENTRO_TIPO_CODIGO,'N/A') CENTRO_ORIGEN_TIPO,\n" +
                "       A.CENTRO_CODIGO CENTRO_DESTINO_CODIGO,\n" +
                "       B.NOMBRE CENTRO_DESTINO_NOMBRE,\n" +
                "       B.NIVEL CENTRO_DESTINO_NIVEL,\n" +
                "       B.CENTRO_TIPO_CODIGO CENTRO_DESTINO_TIPO,\n" +
                "       A.SALDO MONTO,\n" +
                "       COALESCE(G.DRIVER_CODIGO,I.DRIVER_CODIGO,'N/A') DRIVER_CODIGO,\n" +
                "       COALESCE(H.NOMBRE,J.NOMBRE,'N/A') DRIVER_NOMBRE\n" +
                "  FROM MS_CASCADA A\n" +
                "  JOIN MS_CENTROS B ON B.CODIGO=A.CENTRO_CODIGO\n" +
                "  JOIN MS_PLAN_DE_CUENTAS C ON C.CODIGO=A.CUENTA_CONTABLE_ORIGEN_CODIGO\n" +
                "  JOIN MS_PARTIDAS D ON D.CODIGO=A.PARTIDA_ORIGEN_CODIGO\n" +
                "  JOIN MS_CENTROS E ON E.CODIGO=A.CENTRO_ORIGEN_CODIGO\n" +
                "  LEFT JOIN MS_CENTROS F ON F.CODIGO=A.ENTIDAD_ORIGEN_CODIGO\n" +
                "  LEFT JOIN MS_BOLSA_DRIVER G\n" +
                "    ON SUBSTR(G.CUENTA_CONTABLE_CODIGO,1,3)=SUBSTR(A.CUENTA_CONTABLE_ORIGEN_CODIGO,1,3) AND SUBSTR(G.CUENTA_CONTABLE_CODIGO,5,11)=SUBSTR(A.CUENTA_CONTABLE_ORIGEN_CODIGO,5,11)\n" +
                "   AND G.PARTIDA_CODIGO=A.PARTIDA_ORIGEN_CODIGO AND G.CENTRO_CODIGO=A.ENTIDAD_ORIGEN_CODIGO AND G.PERIODO=%s AND G.REPARTO_TIPO=A.REPARTO_TIPO\n" +
                "  LEFT JOIN MS_DRIVERS H\n" +
                "    ON H.CODIGO=G.DRIVER_CODIGO AND H.DRIVER_TIPO_CODIGO='CECO'\n" +
                "  LEFT JOIN MS_ENTIDAD_ORIGEN_DRIVER I\n" +
                "    ON I.ENTIDAD_ORIGEN_CODIGO=A.ENTIDAD_ORIGEN_CODIGO AND I.PERIODO=A.PERIODO AND I.REPARTO_TIPO=A.REPARTO_TIPO\n" +
                "  LEFT JOIN MS_DRIVERS J\n" +
                "    ON J.CODIGO=I.DRIVER_CODIGO AND J.DRIVER_TIPO_CODIGO='CECO'",
                nombreTabla, periodoStr);
        ConexionBD.ejecutarQuery(queryStr);
        
        insertarGeneracionReporte(periodo, repartoTipo, 2);
    }
    
    public void generarReporteObjetos(int periodo, int repartoTipo, String nombreTabla) {
        ConexionBD.ejecutarQuery(String.format("ALTER TABLE %s TRUNCATE PARTITION P_%d DROP STORAGE", nombreTabla, periodo));
        String periodoStr = repartoTipo == 1 ? "A.PERIODO" : "TRUNC(A.PERIODO/100)*100";
        String queryStr = String.format("" +
                "INSERT INTO %s(REPARTO_TIPO,PERIODO,CUENTA_CONTABLE_INICIAL_CODIGO,CUENTA_CONTABLE_INICIAL_NOMBRE,PARTIDA_INICIAL_CODIGO,PARTIDA_INICIAL_NOMBRE,CENTRO_INICIAL_CODIGO,CENTRO_INICIAL_NOMBRE,CENTRO_INICIAL_NIVEL,CENTRO_INICIAL_TIPO,PRODUCTO_CODIGO,PRODUCTO_NOMBRE,LINEA_CODIGO,LINEA_NOMBRE,SUBCANAL_CODIGO,SUBCANAL_NOMBRE,CANAL_CODIGO,CANAL_NOMBRE,CENTRO_ORIGEN_CODIGO,CENTRO_ORIGEN_NOMBRE,CENTRO_ORIGEN_NIVEL,CENTRO_ORIGEN_TIPO,CENTRO_CODIGO,CENTRO_NOMBRE,CENTRO_NIVEL,CENTRO_TIPO,GRUPO_GASTO,MONTO,DRIVER_CODIGO,DRIVER_NOMBRE)\n" +
                "SELECT A.REPARTO_TIPO,\n" +
                "       A.PERIODO,\n" +
                "       A.CUENTA_CONTABLE_ORIGEN_CODIGO CUENTA_INICIAL_CODIGO,\n" +
                "       G.NOMBRE CUENTA_INICIAL_NOMBRE,\n" +
                "       A.PARTIDA_ORIGEN_CODIGO PARTIDA_INICIAL_CODIGO,\n" +
                "       H.NOMBRE PARTIDA_INICIAL_NOMBRE,\n" +
                "       A.CENTRO_ORIGEN_CODIGO CENTRO_INICIAL_CODIGO,\n" +
                "       I.NOMBRE CENTRO_INICIAL_NOMBRE,\n" +
                "       I.NIVEL CENTRO_INICIAL_NIVEL,\n" +
                "       I.CENTRO_TIPO_CODIGO CENTRO_INICIAL_TIPO,\n" +
                "       A.PRODUCTO_CODIGO PRODUCTO_CODIGO,\n" +
                "       B.NOMBRE PRODUCTO_NOMBRE,\n" +
                "       COALESCE(J2.CODIGO,'N/A') LINEA_CODIGO,\n" +
                "       COALESCE(J2.NOMBRE,'N/A') LINEA_NOMBRE,\n" +
                "       A.SUBCANAL_CODIGO SUBCANAL_CODIGO,\n" +
                "       C.NOMBRE SUBCANAL_NOMBRE,\n" +
                "       COALESCE(K2.CODIGO,'N/A') CANAL_CODIGO,\n" +
                "       COALESCE(K2.NOMBRE,'N/A') CANAL_NOMBRE,\n" +
                "       A.ENTIDAD_ORIGEN_CODIGO CENTRO_ORIGEN_CODIGO,\n" +
                "       COALESCE(D.NOMBRE,'N/A') CENTRO_ORIGEN_NOMBRE,\n" +
                "       COALESCE(D.NIVEL,-1) CENTRO_ORIGEN_NIVEL,\n" +
                "       COALESCE(D.CENTRO_TIPO_CODIGO,'N/A') CENTRO_ORIGEN_TIPO,\n" +
                "       A.CENTRO_CODIGO CENTRO_CODIGO,\n" +
                "       COALESCE(L.NOMBRE,'N/A') CENTRO_NOMBRE,\n" +
                "       COALESCE(L.NIVEL,-1) CENTRO_NIVEL,\n" +
                "       COALESCE(L.CENTRO_TIPO_CODIGO,'N/A') CENTRO_TIPO,\n" +
                "       E.NOMBRE GRUPO_GASTO,\n" +
                "       A.SALDO MONTO,\n" +
                "       A.DRIVER_CODIGO DRIVER_CODIGO,\n" +
                "       F.NOMBRE DRIVER_NOMBRE\n" +
                "  FROM MS_OBJETO_LINEAS A\n" +
                "  JOIN MS_PRODUCTOS B ON B.CODIGO=A.PRODUCTO_CODIGO\n" +
                "  JOIN MS_SUBCANALS C ON C.CODIGO=A.SUBCANAL_CODIGO\n" +
                "  JOIN MS_CENTROS D ON D.CODIGO=A.ENTIDAD_ORIGEN_CODIGO\n" +
                "  JOIN MS_GRUPO_GASTOS E ON E.CODIGO=A.GRUPO_GASTO\n" +
                "  JOIN MS_DRIVERS F ON F.CODIGO=A.DRIVER_CODIGO\n" +
                "  LEFT JOIN MS_PLAN_DE_CUENTAS G ON A.CUENTA_CONTABLE_ORIGEN_CODIGO=G.CODIGO\n" +
                "  LEFT JOIN MS_PARTIDAS H ON A.PARTIDA_ORIGEN_CODIGO=H.CODIGO\n" +
                "  LEFT JOIN MS_CENTROS I ON A.CENTRO_ORIGEN_CODIGO=I.CODIGO\n" +
                "  LEFT JOIN MS_CENTROS L ON A.CENTRO_CODIGO=L.CODIGO\n" +
                "  LEFT JOIN MS_JERARQUIA J1 ON J1.PERIODO=%s AND J1.REPARTO_TIPO=A.REPARTO_TIPO AND J1.ENTIDAD_TIPO='PRO' AND J1.ENTIDAD_CODIGO = A.PRODUCTO_CODIGO\n" +
                "  LEFT JOIN MS_PRODUCTO_GRUPOS J2 ON J2.CODIGO=J1.ENTIDAD_PADRE_CODIGO\n" +
                "  LEFT JOIN MS_JERARQUIA K1 ON K1.PERIODO=%s AND K1.REPARTO_TIPO=A.REPARTO_TIPO AND K1.ENTIDAD_TIPO='SCA' AND K1.ENTIDAD_CODIGO = A.SUBCANAL_CODIGO\n" +
                "  LEFT JOIN MS_SUBCANAL_GRUPOS K2 ON K2.CODIGO=K1.ENTIDAD_PADRE_CODIGO",
                nombreTabla, periodoStr, periodoStr, periodo, repartoTipo);
        ConexionBD.ejecutarQuery(queryStr);
        
        insertarGeneracionReporte(periodo, repartoTipo, 3);
    }
}
