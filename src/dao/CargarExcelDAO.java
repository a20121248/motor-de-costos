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
import servicios.LogServicio;

public class CargarExcelDAO {
    
    public void limpiarTablas() {
        String queryStr = "TRUNCATE TABLE CARGAR_HOJA_DRIVER";
        ConexionBD.ejecutar(queryStr);
    }

    public List<DriverLinea> obtenerListaCentroLinea(String driverCodigo, StringBuilder msj) {
        String queryStr = String.format("" +
                "SELECT A.EXCEL_FILA,\n" +
                "       A.CODIGO_1 CECO_CODIGO,\n" +
                "       NVL((B.nombre),'NO') CECO_EXISTE,\n" +
                "       A.PORCENTAJE\n" +
                "  FROM CARGAR_HOJA_DRIVER A\n" +
                "  LEFT JOIN centros B ON A.CODIGO_1=B.CODIGO\n" +
                " WHERE DRIVER_CODIGO='%s'", driverCodigo);
        List<DriverLinea> lista = new ArrayList();
        boolean tieneErrores = false;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            double porcentajeAcc = 0;
            while(rs.next()) {
                int fila = rs.getInt("EXCEL_FILA");

                String centroCodigo = rs.getString("CECO_CODIGO");
                String centroExiste = rs.getString("CECO_EXISTE");
                if (centroExiste.equals("NO")) {
                    msj.append(String.format("- Fila %d: El Centro de Costos con código %s no existe.\n",fila,centroCodigo));
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
                
                Centro centro = new Centro(centroCodigo, centroExiste, null, 0, null, null);
                DriverLinea item = new DriverLinea(centro, porcentaje, null, null);
                lista.add(item);
            }
            if (Math.abs(porcentajeAcc - 100) > 0.0001) {
                msj.append(String.format("- Los porcentajes no suman %d%%, suman %.4f%%.\n",100,porcentajeAcc));
                tieneErrores = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CargarExcelDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (tieneErrores) return null;
        return lista;
    }
    
    public List<DriverObjetoLinea> obtenerListaObjetoLinea(String driverCodigo, StringBuilder msj) {
        String queryStr = String.format("" +
                "SELECT A.EXCEL_FILA,\n" +
                "       A.CODIGO_1 OFICINA_CODIGO,\n" +
                "       NVL((B.nombre),'NO') OFICINA_EXISTE,\n" +
                "       A.CODIGO_2 BANCA_CODIGO,\n" +
                "       NVL((C.nombre),'NO') BANCA_EXISTE,\n" +
                "       A.CODIGO_3 PRODUCTO_CODIGO,\n" +
                "       NVL((D.nombre),'NO') PRODUCTO_EXISTE,\n" +
                "       A.PORCENTAJE\n" +
                "  FROM CARGAR_HOJA_DRIVER A\n" +
                "  LEFT JOIN oficinas B ON A.CODIGO_1=B.CODIGO\n" +
                "  LEFT JOIN bancas C ON A.CODIGO_2=C.CODIGO\n" +
                "  LEFT JOIN productos D ON A.CODIGO_3=D.CODIGO\n" +
                " WHERE DRIVER_CODIGO='%s'", driverCodigo);
        List<DriverObjetoLinea> lista = new ArrayList();
        boolean tieneErrores = false;
        try (ResultSet rs = ConexionBD.ejecutarQuery(queryStr)) {
            double porcentajeAcc = 0;
            while(rs.next()) {
                int fila = rs.getInt("EXCEL_FILA");

                String oficinaCodigo = rs.getString("OFICINA_CODIGO");
                String oficinaExiste = rs.getString("OFICINA_EXISTE");
                if (oficinaExiste.equals("NO")) {
                    msj.append(String.format("- Fila %d: La Oficina con código %s no existe.\n",fila,oficinaCodigo));
                    tieneErrores = true;
                }
                
                String bancaCodigo = rs.getString("BANCA_CODIGO");
                String bancaExiste = rs.getString("BANCA_EXISTE");
                if (bancaExiste.equals("NO")) {
                    msj.append(String.format("- Fila %d: La Banca con código %s no existe.\n",fila,bancaCodigo));
                    tieneErrores = true;
                }
                
                String productoCodigo = rs.getString("PRODUCTO_CODIGO");
                String productoExiste = rs.getString("PRODUCTO_EXISTE");
                if (productoExiste.equals("NO")) {
                    msj.append(String.format("- Fila %d: La Producto con código %s no existe.\n",fila,productoCodigo));
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
                
                Banca banca = new Banca(bancaCodigo, bancaExiste, null, 0, null, null);
                Oficina oficina = new Oficina(oficinaCodigo, oficinaExiste, null, 0, null, null);
                Producto producto = new Producto(productoCodigo, productoExiste, null, 0, null, null);
                DriverObjetoLinea item = new DriverObjetoLinea(banca, oficina, producto, porcentaje, null, null);
                lista.add(item);
            }
            if (Math.abs(porcentajeAcc - 100) > 0.0001) {
                msj.append(String.format("- Los porcentajes no suman %d%%, suman %.4f%%.\n",100,porcentajeAcc));
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
                "INSERT INTO CARGAR_HOJA_DRIVER(EXCEL_FILA,DRIVER_CODIGO,CODIGO_1,PORCENTAJE)\n" +
                "VALUES (%d,'%s','%s',%.4f)",
                numFila,driverCodigo,centroCodigo,porcentaje);
        ConexionBD.agregarBatch(queryStr);
    }
    
    public void insertarLineaDriverObjetoBatch(int numFila, String driverCodigo, String oficinaCodigo, String bancaCodigo, String productoCodigo, double porcentaje) {
        String queryStr = String.format(Locale.US,"" +
                "INSERT INTO CARGAR_HOJA_DRIVER(EXCEL_FILA,DRIVER_CODIGO,CODIGO_1,CODIGO_2,CODIGO_3,PORCENTAJE)\n" +
                "VALUES (%d,'%s','%s','%s','%s',%.4f)",
                numFila,driverCodigo,oficinaCodigo,bancaCodigo,productoCodigo,porcentaje);
        ConexionBD.agregarBatch(queryStr);
    }
}
