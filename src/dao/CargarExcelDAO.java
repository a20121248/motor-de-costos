package dao;

import controlador.ConexionBD;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Banca;
import modelo.Centro;
import modelo.DriverLinea;
import modelo.DriverObjetoLinea;
import modelo.Oficina;
import modelo.Producto;
import modelo.Subcanal;

public class CargarExcelDAO {
    
    public void limpiarTablas() {
        String queryStr = "TRUNCATE TABLE MS_CARGAR_HOJA_DRIVER";
        ConexionBD.ejecutar(queryStr);
    }
    
    public double porcentajeTotalDriver(String codigoDriver) {
        String queryStr = String.format("" +
            "SELECT SUM(PORCENTAJE) PORCENTAJE_TOTAL\n" +
            "  FROM MS_CARGAR_HOJA_DRIVER A\n" +
            " WHERE A.DRIVER_CODIGO='%s'\n" +
            "GROUP BY a.DRIVER_CODIGO",codigoDriver);
        ResultSet rs = ConexionBD.ejecutarQuery(queryStr);
        double cnt = 0;
        try {
            rs.next();
            cnt = rs.getDouble("PORCENTAJE_TOTAL");
        } catch (SQLException ex) {
            Logger.getLogger(CentroDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;        
    }

    public List<DriverLinea> obtenerListaCentroLinea(String driverCodigo, int periodo, int repartoTipo, StringBuilder msj) {
//        TO DO: Validar que centro este asignado al periodo
        String queryStr = String.format("" +
                "SELECT A.EXCEL_FILA,\n" +
                "       A.CODIGO_1 CECO_CODIGO,\n" +
                "       CASE\n" +
                "        WHEN C.nombre IS NULL THEN 'NO'\n" +
                "        WHEN c.centro_tipo_codigo='BOLSA' AND c.centro_tipo_codigo != 'OFICINA' THEN 'NO, ES BOLSA'\n" +
                "        WHEN C.nombre IS NOT  NULL THEN C.nombre\n" +
                "       END CECO_EXISTE,\n" +
                "       A.PORCENTAJE\n" +
                "  FROM MS_CARGAR_HOJA_DRIVER A\n" +
                "  LEFT JOIN MS_centro_lineas B ON A.CODIGO_1=B.CENTRO_CODIGO and b.periodo = '%d' AND REPARTO_TIPO = '%d'\n" +
                "  LEFT JOIN MS_centros C ON A.CODIGO_1=C.CODIGO AND B.CENTRO_CODIGO = C.CODIGO\n" +
                " WHERE A.DRIVER_CODIGO='%s'\n" +
                " ORDER BY a.excel_fila", periodo,repartoTipo,driverCodigo);
        List<DriverLinea> lista = new ArrayList();
        boolean tieneErrores = false;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            while(rs.next()) {
                int fila = rs.getInt("EXCEL_FILA");
                String centroCodigo = rs.getString("CECO_CODIGO");
                String centroExiste = rs.getString("CECO_EXISTE");
                if (centroExiste.equals("NO")) {
                    msj.append(String.format("- Fila %d: El Centro de Costos con código %s no esta registrado en el periodo %d.\r\n",fila,centroCodigo,periodo));
                    tieneErrores = true;
                }
                if (centroExiste.equals("NO, ES BOLSA")) {
                    msj.append(String.format("- Fila %d: El Centro de Costos con código %s es de TIPO DE CENTRO bolsa.\r\n",fila,centroCodigo));
                    tieneErrores = true;
                } 
                double porcentaje = rs.getDouble("PORCENTAJE");
                Centro centro = new Centro(centroCodigo, centroExiste, null, 0, null, null);
                DriverLinea item = new DriverLinea(centro, porcentaje, null, null);
                lista.add(item);
            }
//            if (Math.abs(porcentajeAcc - 100) > 0.0001) {
//                msj.append(String.format("- Los porcentajes no suman %d%%, suman %.4f%%.\n",100,porcentajeAcc));
//                tieneErrores = true
//            }
        } catch (SQLException ex) {
            Logger.getLogger(CargarExcelDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (tieneErrores) lista = null;
        return lista;
    }
    
    public List<DriverObjetoLinea> obtenerListaObjetoLinea(String driverCodigo, int periodo, StringBuilder msj) {
        String queryStr = String.format("" +
                "SELECT A.EXCEL_FILA,\n" +
                "       A.CODIGO_1 PRODUCTO_CODIGO,\n" +
                "       NVL((C.nombre),'NO') PRODUCTO_EXISTE,\n" +
                "       A.CODIGO_2 SUBCANAL_CODIGO,\n" +
                "       NVL((E.nombre),'NO') SUBCANAL_EXISTE,\n" +
                "       A.PORCENTAJE\n" +
                "  FROM CARGAR_HOJA_DRIVER A\n" +
                "  LEFT JOIN producto_lineas B ON  A.CODIGO_1 = B.PRODUCTO_CODIGO AND b.periodo = %d\n" +
                "  LEFT JOIN productos C ON A.CODIGO_1=C.CODIGO AND b.producto_codigo = c.codigo\n" +
                "  LEFT JOIN subcanal_lineas D ON A.CODIGO_2 = D.SUBCANAL_CODIGO AND d.periodo = %d\n" +
                "  LEFT JOIN subcanals E ON A.CODIGO_2=E.CODIGO AND d.subcanal_codigo = e.codigo\n" +
                " WHERE DRIVER_CODIGO='%s'\n"+
                " ORDER BY a.excel_fila"
                ,periodo ,periodo, driverCodigo);
        List<DriverObjetoLinea> lista = new ArrayList();
        boolean tieneErrores = false;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            double porcentajeAcc = 0;
            while(rs.next()) {
                int fila = rs.getInt("EXCEL_FILA");

                String productoCodigo = rs.getString("PRODUCTO_CODIGO");
                String productoExiste = rs.getString("PRODUCTO_EXISTE");
                if (productoExiste.equals("NO")) {
                    msj.append(String.format("- Fila %d: El Producto con código %s no existe."+System.lineSeparator(),fila,productoCodigo));
                    tieneErrores = true;
                }
                
                String subcanalCodigo = rs.getString("SUBCANAL_CODIGO");
                String subcanalExiste = rs.getString("SUBCANAL_EXISTE");
                if (subcanalExiste.equals("NO")) {
                    msj.append(String.format("- Fila %d: El subcanal con código %s no existe."+System.lineSeparator(),fila,subcanalCodigo));
                    tieneErrores = true;
                }
                
                double porcentaje = rs.getDouble("PORCENTAJE");
                /*try {
                    porcentaje = Double.parseDouble(rs.getString("PORCENTAJE"));
                } catch (NumberFormatException | SQLException ex) {
                    msj.append(String.format("- Fila %d: El porcentaje es incorrecto. (%s).\n",fila,ex.getMessage()));
                    tieneErrores = true;
                }*/
                porcentajeAcc += porcentaje;
                
                Producto producto = new Producto(productoCodigo, productoExiste, null, 0, null, null);
                Subcanal subcanal = new Subcanal(subcanalCodigo, subcanalExiste, null, 0, null, null);
                DriverObjetoLinea item = new DriverObjetoLinea(producto, subcanal, porcentaje, null, null);
                lista.add(item);
            }
            if (Math.abs(porcentajeAcc - 100) > 0.0001) {
                msj.append(String.format("- Los porcentajes no suman %d%%, suman %.4f%%."+System.lineSeparator(),100,porcentajeAcc));
                tieneErrores = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CargarExcelDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (tieneErrores) return null;
        return lista;
    }
    
    public void insertarLineaDriverCentroBatch(int numFila, String driverCodigo, String centroCodigo, double porcentaje) {
        String queryStr = String.format(Locale.US,"" +
                "INSERT INTO MS_CARGAR_HOJA_DRIVER(EXCEL_FILA,DRIVER_CODIGO,CODIGO_1,PORCENTAJE)\n" +
                "VALUES (%d,'%s','%s',%.4f)",
                numFila,driverCodigo,centroCodigo,porcentaje);
        ConexionBD.agregarBatch(queryStr);
    }
    
//    public void insertarLineaDriverObjetoBatch(int numFila, String driverCodigo, String oficinaCodigo, String bancaCodigo, String productoCodigo, double porcentaje) {
//        String queryStr = String.format(Locale.US,"" +
//                "INSERT INTO CARGAR_HOJA_DRIVER(EXCEL_FILA,DRIVER_CODIGO,CODIGO_1,CODIGO_2,CODIGO_3,PORCENTAJE)\n" +
//                "VALUES (%d,'%s','%s','%s','%s',%.4f)",
//                numFila,driverCodigo,oficinaCodigo,bancaCodigo,productoCodigo,porcentaje);
//        ConexionBD.agregarBatch(queryStr);
//    }
    
    public void insertarLineaDriverObjetoBatch(int numFila, String driverCodigo, String productoCodigo, String subcanalCodigo, double porcentaje) {
        String queryStr = String.format(Locale.US,"" +
                "INSERT INTO CARGAR_HOJA_DRIVER(EXCEL_FILA,DRIVER_CODIGO,CODIGO_1,CODIGO_2,PORCENTAJE)\n" +
                "VALUES (%d,'%s','%s','%s',%.4f)",
                numFila,driverCodigo,productoCodigo,subcanalCodigo,porcentaje);
        ConexionBD.agregarBatch(queryStr);
    }
}
