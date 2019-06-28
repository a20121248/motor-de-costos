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
    
    
//    public ResultSet dataReporteCuentaCentro(int periodo, int repartoTipo) {
//        String queryStr = String.format("" +
//                "SELECT B.codigo CENTRO_CODIGO,\n" +
//                "       B.nombre CENTRO_NOMBRE,\n" +
//                "       C.codigo GRUPO_CODIGO,\n" +
//                "       C.nombre GRUPO_NOMBRE,\n" +
//                "       E.codigo CUENTA_CONTABLE_CODIGO,\n" +
//                "       E.nombre CUENTA_CONTABLE_NOMBRE,\n" +
//                "       CASE WHEN G.saldo_grupo=0 THEN 0\n" +
//                "            ELSE A.saldo*F.saldo/G.saldo_grupo\n" +
//                "       END SALDO" +
//                "  FROM centro_lineas A\n" +
//                "  JOIN centros B ON A.centro_codigo=B.codigo\n" +
//                "  JOIN grupos C ON A.entidad_origen_codigo=C.codigo\n" +
//                "  JOIN grupo_plan_de_cuenta D ON C.codigo=D.grupo_codigo\n" +
//                "  JOIN plan_de_cuentas E ON D.plan_de_cuenta_codigo=E.codigo\n" +
//                "  JOIN plan_de_cuenta_lineas F ON E.codigo=F.plan_de_cuenta_codigo\n" +
//                "  JOIN (SELECT A.codigo,SUM(C.saldo) saldo_grupo FROM grupos A JOIN grupo_plan_de_cuenta B ON A.codigo=B.grupo_codigo JOIN plan_de_cuenta_lineas C ON B.plan_de_cuenta_codigo=C.plan_de_cuenta_codigo WHERE B.periodo=%d AND C.periodo=%d GROUP BY A.codigo) G ON C.codigo=G.codigo\n" +
//                " WHERE A.iteracion=0\n" +
//                "   AND A.periodo=%d\n" +
//                "   AND B.reparto_tipo=%d\n" +
//                "   AND D.periodo=%d\n" +
//                "   AND F.periodo=%d",
//            periodo,periodo,periodo,repartoTipo,periodo,periodo);
//        return ConexionBD.ejecutarQuery(queryStr);
//    }
    
    public ResultSet dataReporteCuentaPartidaCentroBolsa(int periodo, int repartoTipo) {
        String queryStr = String.format(""+
                "select a.periodo PERIODO,\n" +
                "      A.centro_codigo CODIGO_CENTRO,\n" +
                "      E.nombre NOMBRE_CENTRO,\n" +
                "      B.cuenta_contable_codigo CODIGO_CUENTA_CONTABLE,\n" +
                "      H.nombre NOMBRE_CUENTA_CONTABLE,\n" +
                "      B.partida_codigo CODIGO_PARTIDA,\n" +
                "      D.nombre NOMBRE_PARTIDA,\n" +
                "      A.saldo SALDO,\n" +
                "      A.entidad_origen_codigo CODIGO_CENTRO_ORIGEN,\n" +
                "      F.nombre NOMBRE_CENTRO_ORIGEN,\n" +
                "      B.driver_codigo CODIGO_DRIVER,\n" +
                "      G.nombre NOMBRE_DRIVER,\n" +
                "      CASE  \n" +
                "        when A.iteracion=0 then 'BOLSA'\n" +
                "        ELSE 'DIRECTO'\n" +
                "      END ASIGNACION      \n" +
                "FROM centro_lineas A \n" +
                "join bolsa_driver B on a.entidad_origen_codigo = b.centro_codigo\n" +
                "join driver_lineas C on C.entidad_destino_codigo = a.centro_codigo\n" +
                "join partidas D on D.codigo = B.partida_codigo AND d.grupo_gasto = a.grupo_gasto\n" +
                "join centros E ON E.codigo = A.centro_codigo\n" +
                "join centros F ON F.codigo = a.entidad_origen_codigo\n" +
                "join drivers G ON G.codigo = B.driver_codigo\n" +
                "JOIN plan_de_cuentas H ON H.codigo = B.cuenta_contable_codigo\n" +
                "where A.periodo = %d and a.iteracion = 0\n" +
                "UNION\n" +
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
                "      END ASIGNACION  \n" +
                "FROM cuenta_partida_centro A\n" +
                "join plan_de_cuentas B ON B.codigo = A.cuenta_contable_codigo\n" +
                "join partidas C ON c.codigo = A.partida_codigo\n" +
                "join centros D ON d.codigo = A.centro_codigo\n" +
                "WHERE a.periodo = %d and d.es_bolsa = 'NO'\n" +
                "ORDER BY CODIGO_CENTRO",
                periodo,periodo);
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
                "join bolsa_driver B on a.entidad_origen_codigo = b.centro_codigo\n" +
                "join driver_lineas C on C.entidad_destino_codigo = a.centro_codigo\n" +
                "join partidas D on D.codigo = B.partida_codigo AND d.grupo_gasto = a.grupo_gasto\n" +
                "join centros E ON E.codigo = A.centro_codigo\n" +
                "join centros F ON F.codigo = a.entidad_origen_codigo\n" +
                "join drivers G ON G.codigo = B.driver_codigo\n" +
                "JOIN plan_de_cuentas H ON H.codigo = B.cuenta_contable_codigo\n" +
                "JOIN centro_tipos I on e.centro_tipo_codigo = i.codigo\n" +
                "JOIN centro_tipos J on e.centro_tipo_codigo = j.codigo\n" +
                "JOIN grupo_gastos K on k.codigo = a.grupo_gasto\n" +
                "where A.periodo = 201906 and a.iteracion = 0\n" +
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
                "WHERE a.periodo = 201906 and d.es_bolsa = 'NO'\n" +
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
                "JOIN ENTIDAD_ORIGEN_DRIVER B ON A.entidad_origen_codigo = B.entidad_origen_codigo\n" +
                "JOIN CENTROS C ON c.codigo = a.centro_codigo\n" +
                "JOIN CENTROS D ON d.codigo = b.entidad_origen_codigo\n" +
                "JOIN centro_tipos E ON e.codigo = c.centro_tipo_codigo\n" +
                "JOIN centro_tipos F ON f.codigo = d.centro_tipo_codigo\n" +
                "JOIN grupo_gastos G ON g.codigo = a.grupo_gasto\n" +
                "join drivers H ON H.codigo = b.driver_codigo\n" +
                "WHERE A.PERIODO = %d\n" +
                "ORDER BY CODIGO_CENTRO" 
                ,periodo,repartoTipo);
        return ConexionBD.ejecutarQuery(queryStr);
    }
    
    public ResultSet dataReporteCascada(int periodo, int repartoTipo) {
        String queryStr = String.format("" +
                "SELECT A.CODIGO CECO_CODIGO,\n" +
                "       A.NOMBRE CECO_NOMBRE,\n" +
                "       CASE WHEN A.nivel=0 THEN 999\n" +
                "            ELSE A.nivel\n" +
                "       END NIVEL_DUMMY,\n" +
                "       'NIVEL '||TO_CHAR(A.nivel) CECO_NIVEL,\n" +
                "       C.NOMBRE CECO_TIPO,\n" +
                "       B.ITERACION,\n" +
                "       B.SALDO,\n" +
                "       B.ENTIDAD_ORIGEN_CODIGO,\n" +
                "       COALESCE(E.nombre,F.nombre) ENTIDAD_ORIGEN_NOMBRE,\n" +
                "       D.DRIVER_CODIGO\n" +
                "  FROM centros A\n" +
                "  JOIN centro_lineas B ON A.codigo=B.centro_codigo\n" +
                "  JOIN centro_tipos C ON A.centro_tipo_codigo=C.codigo\n" +
                "  LEFT JOIN entidad_origen_driver D ON A.codigo=D.entidad_origen_codigo AND B.periodo=D.periodo\n" +
                "  LEFT JOIN grupos E ON B.entidad_origen_codigo=E.codigo\n" +
                "  LEFT JOIN centros F ON B.entidad_origen_codigo=F.codigo\n" +
                " WHERE B.periodo=%d AND A.reparto_tipo=%d AND B.iteracion!=-1\n" +
                " ORDER BY NIVEL_DUMMY,A.codigo,B.iteracion,B.entidad_origen_codigo",
                periodo,repartoTipo);
        return ConexionBD.ejecutarQuery(queryStr);
    }
    
    public int numeroNiveles(int periodo, String objetoTipo) throws SQLException {
        ResultSet rs = ConexionBD.ejecutarQuery(String.format("SELECT COALESCE(MAX(NIVEL),0)+1 NIVELES FROM JERARQUIA WHERE PERIODO=%d AND ENTIDAD_TIPO='%s'",periodo,objetoTipo));
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
        String queryStr = String.format(""+
                "SELECT  A.periodo PERIODO,\n" +
                "        A.producto_codigo CODIGO_PRODUCTO,\n" +
                "        b.nombre NOMBRE_PRODUCTO,\n" +
                "        a.subcanal_codigo CODIGO_SUBCANAL,\n" +
                "        c.nombre NOMBRE_SUBCANAL,\n" +
                "        e.nombre GRUPO_GASTO,\n" +
                "        a.saldo SALDO,\n" +
                "        a.entidad_origen_codigo CODIGO_CENTRO_ORIGEN,\n" +
                "        d.nombre NOMBRE_CENTRO_ORIGEN,\n" +
                "        a.driver_codigo CODIGO_DRIVER,\n" +
                "        f.nombre NOMBRE_DRIVER\n" +
                "FROM objeto_lineas A\n" +
                "JOIN productos B ON b.codigo = a.producto_codigo\n" +
                "JOIN subcanals C ON c.codigo = a.subcanal_codigo\n" +
                "JOIN centros D ON d.codigo = a.entidad_origen_codigo\n" +
                "join grupo_gastos E ON e.codigo = a.grupo_gasto\n" +
                "JOIN drivers F ON f.codigo = a.driver_codigo\n" +
                "WHERE A.PERIODO = 201906",
                periodo);
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
    
    
}
