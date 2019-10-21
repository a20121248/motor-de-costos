package servicios;

import dao.ReportingDAO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportingServicio {
    ReportingDAO reportingDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.ReportingServicio");
    
    public ReportingServicio() {
        reportingDAO = new ReportingDAO();
    }
    
    private CellStyle cabeceraEstilo(SXSSFWorkbook wb) {
        // Create a Font for styling header cells
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillBackgroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerCellStyle;
    }
    
    private CellStyle cabeceraEstilo(XSSFWorkbook wb) {
        // Create a Font for styling header cells
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillBackgroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerCellStyle;
    }
    
    private CellStyle decimalEstilo(XSSFWorkbook wb) {
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle numberStyle = wb.createCellStyle();
        numberStyle.setDataFormat(createHelper.createDataFormat().getFormat("* #,##0.0000"));
        return numberStyle;
    }
    
    private CellStyle decimalEstilo(SXSSFWorkbook wb) {
        CreationHelper createHelper = wb.getCreationHelper();
        CellStyle numberStyle = wb.createCellStyle();
        numberStyle.setDataFormat(createHelper.createDataFormat().getFormat("* #,##0.0000"));
        return numberStyle;
    }
    
    private void crearCabecera(List<String> listaCabecera, Sheet sh, int numFila, CellStyle headerCellStyle) {
        Row row = sh.createRow(numFila);
        for (int i = 0; i < listaCabecera.size(); ++i) {
            Cell cell = row.createCell(i);
            cell.setCellValue(listaCabecera.get(i));
            cell.setCellStyle(headerCellStyle);
        }
    }
       
    public void crearReporteBolsasOficinas(int periodo, String ruta, int repartoTipo) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera;
            listaCabecera = new ArrayList(Arrays.asList("PERIODO","CODIGO_CUENTA_ORIGEN","NOMBRE_CUENTA_ORIGEN","CODIGO_PARTIDA_ORIGEN","NOMBRE_PARTIDA_ORIGEN","CODIGO_CENTRO_ORIGEN","NOMBRE_CENTRO_ORIGEN","CODIGO_CENTRO_DESTINO","NOMBRE_CENTRO_DESTINO","MONTO","CODIGO_DRIVER","NOMBRE_DRIVER","ASIGNACION"));
            
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle numberCellStyle = decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteCuentaPartidaCentroBolsa(periodo, repartoTipo);
            while(rs.next()) {
                String periodoReporte = rs.getString("PERIODO");
                String codigoCuentaOrigen = rs.getString("CODIGO_CUENTA_ORIGEN");
                String nombreCuentaOrigen = rs.getString("NOMBRE_CUENTA_ORIGEN");
                String codigoPartidaOrigen = rs.getString("CODIGO_PARTIDA_ORIGEN");
                String nombrePartidaOrigen = rs.getString("NOMBRE_PARTIDA_ORIGEN");
                String codigoCentroOrigen = rs.getString("CODIGO_CENTRO_ORIGEN");
                String nombreCentroOrigen = rs.getString("NOMBRE_CENTRO_ORIGEN");
                String codigoCentro = rs.getString("CODIGO_CENTRO_DESTINO");
                String nombreCentro = rs.getString("NOMBRE_CENTRO_DESTINO");
                double monto = rs.getDouble("MONTO");
                String codigoDriver = rs.getString("CODIGO_DRIVER");
                String nombreDriver = rs.getString("NOMBRE_DRIVER");
                String tipoAsignacion = rs.getString("ASIGNACION");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodoReporte);
                row.createCell(idxColumn++).setCellValue(codigoCuentaOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCuentaOrigen);
                row.createCell(idxColumn++).setCellValue(codigoPartidaOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombrePartidaOrigen);
                row.createCell(idxColumn++).setCellValue(codigoCentroOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCentroOrigen);
                row.createCell(idxColumn++).setCellValue(codigoCentro);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCentro);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(monto);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                row.createCell(idxColumn++).setCellValue(codigoDriver);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreDriver);
                row.createCell(idxColumn++).setCellValue(tipoAsignacion);                
                
                if(rowNum % 100 == 0) ((SXSSFSheet)sh).flushRows(100);
            }
            FileOutputStream out = new FileOutputStream(ruta);
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.INFO,ex.getMessage());
        }
    }

    public void crearReporteCascada(int periodo, String ruta, int repartoTipo) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera;
            listaCabecera = new ArrayList(Arrays.asList("PERIODO","ITERACION","CODIGO_CUENTA_ORIGEN","NOMBRE_CUENTA_ORIGEN","CODIGO_PARTIDA_ORIGEN","NOMBRE_PARTIDA_ORIGEN","CODIGO_CENTRO_ORIGEN","NOMBRE_CENTRO_ORIGEN","CODIGO_CENTRO_DESTINO","NOMBRE_CENTRO_DESTINO","CODIGO_ENTIDAD_ORIGEN","NOMBRE_ENTIDAD_ORIGEN","MONTO","CODIGO_DRIVER","NOMBRE_DRIVER"));
            
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle numberCellStyle = decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteCascada(periodo, repartoTipo);
            while(rs.next()) {
                int periodoReporte = rs.getInt("PERIODO");
                int iteracion = rs.getInt("ITERACION");
                String codigoCuentaOrigen = rs.getString("CODIGO_CUENTA_ORIGEN");
                String nombreCuentaOrigen = rs.getString("NOMBRE_CUENTA_ORIGEN");
                String codigoPartidaOrigen = rs.getString("CODIGO_PARTIDA_ORIGEN");
                String nombrePartidaOrigen = rs.getString("NOMBRE_PARTIDA_ORIGEN");
                String codigoCentroOrigen = rs.getString("CODIGO_CENTRO_ORIGEN");
                String nombreCentroOrigen = rs.getString("NOMBRE_CENTRO_ORIGEN");
                String codigoCentro = rs.getString("CODIGO_CENTRO_DESTINO");
                String nombreCentro = rs.getString("NOMBRE_CENTRO_DESTINO");
                String codigoEntidadOrigen = rs.getString("CODIGO_ENTIDAD_ORIGEN");
                String nombreEntidadOrigen = rs.getString("NOMBRE_ENTIDAD_ORIGEN");
                double monto = rs.getDouble("MONTO");
                String codigoDriver = rs.getString("CODIGO_DRIVER");
                String nombreDriver = rs.getString("NOMBRE_DRIVER");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;

                row.createCell(idxColumn++).setCellValue(periodoReporte);

                row.createCell(idxColumn++).setCellValue(iteracion);
                
                row.createCell(idxColumn++).setCellValue(codigoCuentaOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCuentaOrigen);
                
                row.createCell(idxColumn++).setCellValue(codigoPartidaOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombrePartidaOrigen);
                
                row.createCell(idxColumn++).setCellValue(codigoCentroOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCentroOrigen);
                
                row.createCell(idxColumn++).setCellValue(codigoCentro);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCentro);
                
                row.createCell(idxColumn++).setCellValue(codigoEntidadOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreEntidadOrigen);                
                // monto
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(monto);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);                
                // driver
                row.createCell(idxColumn++).setCellValue(codigoDriver);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreDriver);
                
                if(rowNum % 100 == 0) ((SXSSFSheet)sh).flushRows(100);
            }
            FileOutputStream out = new FileOutputStream(ruta);
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.INFO,ex.getMessage());
        }
    }
    
 
    public void crearReporteObjetos(int periodo, String ruta, int repartoTipo) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            int oficinaNiveles = reportingDAO.numeroNiveles(periodo, "OFI", repartoTipo);
            int bancaNiveles = reportingDAO.numeroNiveles(periodo, "BAN", repartoTipo);
            int productoNiveles = reportingDAO.numeroNiveles(periodo,"PRO", repartoTipo);
            
            // Cabecera de la tabla
            List<String> listaCabecera = new ArrayList();
            listaCabecera.add("PERIODO");
            for (int i=oficinaNiveles; i>=1; --i) {
                listaCabecera.add(String.format("OFICINA_CODIGO_N%d",i));
                listaCabecera.add(String.format("OFICINA_NOMBRE_N%d",i));
            }
            listaCabecera.add("OFICINA_CODIGO");
            listaCabecera.add("OFICINA_NOMBRE");

            for (int i=bancaNiveles; i>=1; --i) {
                listaCabecera.add(String.format("BANCA_CODIGO_N%d",i));
                listaCabecera.add(String.format("BANCA_NOMBRE_N%d",i));
            }
            listaCabecera.add("BANCA_CODIGO");
            listaCabecera.add("BANCA_NOMBRE");

            for (int i=productoNiveles; i>=1; --i) {
                listaCabecera.add(String.format("PRODUCTO_CODIGO_N%d",i));
                listaCabecera.add(String.format("PRODUCTO_NOMBRE_N%d",i));
            }
            listaCabecera.add("PRODUCTO_CODIGO");
            listaCabecera.add("PRODUCTO_NOMBRE");
            if (repartoTipo==1) {
                listaCabecera.addAll(new ArrayList(Arrays.asList("GASTO","CECO_ORIGEN_CODIGO","CECO_ORIGEN_NOMBRE","DRIVER_CODIGO","DRIVER_NOMBRE")));
            } else {
                listaCabecera.addAll(new ArrayList(Arrays.asList("INGRESO","CEBE_ORIGEN_CODIGO","CEBE_ORIGEN_NOMBRE","DRIVER_CODIGO","DRIVER_NOMBRE")));
            }
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            int rowNum = 0;
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle numberCellStyle = decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteObjetos(periodo, repartoTipo, oficinaNiveles, bancaNiveles, productoNiveles);
            while(rs.next()) {
                Row row = sh.createRow(rowNum++);
                
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodo);
                for (int i=oficinaNiveles; i>=1; --i) {
                    row.createCell(idxColumn++).setCellValue(rs.getString(String.format("OFICINA_CODIGO_N%d",i)));
                    sh.setColumnWidth(idxColumn, 6000);
                    row.createCell(idxColumn++).setCellValue(rs.getString(String.format("OFICINA_NOMBRE_N%d",i)));
                }
                row.createCell(idxColumn++).setCellValue(rs.getString("OFICINA_CODIGO"));
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(rs.getString("OFICINA_NOMBRE"));
                
                for (int i=bancaNiveles; i>=1; --i) {
                    row.createCell(idxColumn++).setCellValue(rs.getString(String.format("BANCA_CODIGO_N%d",i)));
                    sh.setColumnWidth(idxColumn, 6000);
                    row.createCell(idxColumn++).setCellValue(rs.getString(String.format("BANCA_NOMBRE_N%d",i)));
                }
                row.createCell(idxColumn++).setCellValue(rs.getString("BANCA_CODIGO"));
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(rs.getString("BANCA_NOMBRE"));
                
                for (int i=productoNiveles; i>=1; --i) {
                    row.createCell(idxColumn++).setCellValue(rs.getString(String.format("PRODUCTO_CODIGO_N%d",i)));
                    sh.setColumnWidth(idxColumn, 6000);
                    row.createCell(idxColumn++).setCellValue(rs.getString(String.format("PRODUCTO_NOMBRE_N%d",i)));
                }
                row.createCell(idxColumn++).setCellValue(rs.getString("PRODUCTO_CODIGO"));
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(rs.getString("PRODUCTO_NOMBRE"));
                
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(rs.getDouble("SALDO"));
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                row.createCell(idxColumn++).setCellValue(rs.getString("CENTRO_ORIGEN_CODIGO"));
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(rs.getString("CENTRO_ORIGEN_NOMBRE"));
                row.createCell(idxColumn++).setCellValue(rs.getString("DRIVER_CODIGO"));
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(rs.getString("DRIVER_NOMBRE"));                
                
                if(rowNum % 100 == 0) ((SXSSFSheet)sh).flushRows(100);
            }
            FileOutputStream out = new FileOutputStream(ruta);
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.INFO,ex.getMessage());
        }
    }    
    
    public void crearReporteGastoPropioAsignado(int periodo, String ruta, int repartoTipo) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera = new ArrayList(Arrays.asList("PERIODO","CODIGO_CENTRO","NOMBRE_CENTRO","NIVEL_CENTRO","TIPO_CENTRO","ITERACION","TIPO_ASIGNACION_GASTO","CODIGO_CENTRO_ORIGEN","NOMBRE_CENTRO_ORIGEN","NIVEL_CENTRO_ORIGEN","TIPO_CENTRO_ORIGEN","CODIGO_CUENTA_CONTABLE_ORIGEN","NOMBRE_CUENTA_CONTABLE_ORIGEN","CODIGO_PARTIDA_ORIGEN","NOMBRE_PARTIDA_ORIGEN","CODIGO_DRIVER","NOMBRE_DRIVER","TIPO_GASTO","SALDO"));
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle decimalCellStyle = decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteGastoPropioAsignado(periodo,repartoTipo);
            while(rs.next()) {
                int periodoReporte = rs.getInt("PERIODO");
                String codigoCentro = rs.getString("CODIGO_CENTRO");
                String nombreCentro = rs.getString("NOMBRE_CENTRO");
                int nivelCentro = rs.getInt("NIVEL_CENTRO");
                String tipoCentro = rs.getString("TIPO_CENTRO");
                int iteracion = rs.getInt("ITERACION");
                String tipoAsignacion = rs.getString("TIPO_ASIGNACION_GASTO");
                String codigoCentroOrigen = rs.getString("CODIGO_CENTRO_ORIGEN");
                String nombreCentroOrigen = rs.getString("NOMBRE_CENTRO_ORIGEN");
                int nivelCentroOrigen = rs.getInt("NIVEL_CENTRO_ORIGEN");
                String tipoCentroOrigen = rs.getString("TIPO_CENTRO_ORIGEN");
                String codigoCuenta = rs.getString("CODIGO_CUENTA_CONTABLE_ORIGEN");
                String nombreCuenta = rs.getString("NOMBRE_CUENTA_CONTABLE_ORIGEN");
                String codigoPartida = rs.getString("CODIGO_PARTIDA_ORIGEN");
                String nombrePartida = rs.getString("NOMBRE_PARTIDA_ORIGEN");
                String codigoDriver = rs.getString("CODIGO_DRIVER");
                String nombreDriver = rs.getString("NOMBRE_DRIVER");
                String tipoGasto = rs.getString("TIPO_GASTO");
                double saldo = rs.getDouble("SALDO");
                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodoReporte);
                row.createCell(idxColumn++).setCellValue(codigoCentro);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCentro);
                row.createCell(idxColumn++).setCellValue(nivelCentro);
                row.createCell(idxColumn++).setCellValue(tipoCentro);
                row.createCell(idxColumn++).setCellValue(iteracion);
                row.createCell(idxColumn++).setCellValue(tipoAsignacion);
                row.createCell(idxColumn++).setCellValue(codigoCentroOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCentroOrigen);
                row.createCell(idxColumn++).setCellValue(nivelCentroOrigen);
                row.createCell(idxColumn++).setCellValue(tipoCentroOrigen);
                row.createCell(idxColumn++).setCellValue(codigoCuenta);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCuenta);
                row.createCell(idxColumn++).setCellValue(codigoPartida);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombrePartida);
                row.createCell(idxColumn++).setCellValue(codigoDriver);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreDriver);
                row.createCell(idxColumn++).setCellValue(tipoGasto);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(saldo);
                row.getCell(idxColumn++).setCellStyle(decimalCellStyle);
                
                if(rowNum % 100 == 0) ((SXSSFSheet)sh).flushRows(100);
            }
            FileOutputStream out = new FileOutputStream(ruta);
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.INFO,ex.getMessage());
        }
    }
    
    private void crearTablaDinamica(String ruta, String shDataName, String shPivotName) {
        try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(ruta))) {
            XSSFSheet shData = wb.getSheet(shDataName);
            XSSFSheet shPivot = wb.getSheet(shPivotName);
            
            int firstRow = shData.getFirstRowNum();
            int lastRow = shData.getLastRowNum();
            int firstCol = shData.getRow(0).getFirstCellNum();
            int lastCol = shData.getRow(0).getLastCellNum();
            CellReference topLeft = new CellReference(firstRow, firstCol);
            CellReference botRight = new CellReference(lastRow, lastCol - 1);
            AreaReference aref = new AreaReference(topLeft, botRight, SpreadsheetVersion.EXCEL2007);
            CellReference pos = new CellReference(0, 0);
            XSSFPivotTable pivotTable = shPivot.createPivotTable(aref, pos, shData);
            pivotTable.addRowLabel(1);
            pivotTable.addRowLabel(0);
            pivotTable.addColLabel(3);
            pivotTable.addReportFilter(2);
            pivotTable.addColumnLabel(DataConsolidateFunction.SUM, 4);
            try {
                FileOutputStream fileOut = new FileOutputStream(ruta);
                wb.write(fileOut);
            } catch (IOException e) {
                LOGGER.log(Level.INFO,String.format("Error: %s.",e.getMessage()));
            }
            wb.setSheetOrder(shPivotName, 0);
            wb.setSheetOrder(shDataName, 1);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.INFO,String.format("Error: %s.",ex.getMessage()));
        } catch (IOException exc) {
            LOGGER.log(Level.INFO,String.format("Error: %s.",exc.getMessage()));
        }
    }
/*
    public void crearReporteCascada(int periodo, String ruta, int repartoTipo) {
        try {
            String shDataName = "REPORTE_DATA";
            String shPivotName = "REPORTE";
            XSSFWorkbook wb = new XSSFWorkbook();
            wb.createSheet(shPivotName);
            XSSFSheet sh = wb.createSheet(shDataName);
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera = new ArrayList(Arrays.asList("CENTRO","CENTRO_NIVEL","TIPO_CENTRO","ITERACION","SALDO"));
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle numberCellStyle = ReportingServicio.this.decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteCascada(periodo, repartoTipo);
            while(rs.next()) {
                String cecoCodigo = rs.getString("CODIGO_CENTRO");
                String cecoNombre = rs.getString("NOMBRE_CENTRO");
                String cecoNivel = rs.getString("NIVEL_CENTRO");
                String cecoTipo = rs.getString("TIPO_CENTRO");
                String iteracion = rs.getString("ITERACION");
                double saldo = rs.getDouble("SALDO");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                sh.setColumnWidth(idxColumn, 8000);
                row.createCell(idxColumn++).setCellValue(cecoCodigo + " - " + cecoNombre);
                row.createCell(idxColumn++).setCellValue(cecoNivel.equals("NIVEL 999") ? "-":cecoNivel);
                row.createCell(idxColumn++).setCellValue(cecoTipo);
                row.createCell(idxColumn++).setCellValue(iteracion);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(saldo);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                
               //if(rowNum % 100 == 0) ((SXSSFSheet)shData).flushRows(100);
            }
            FileOutputStream out = new FileOutputStream(ruta);
            wb.write(out);
            out.close();
            //wb.dispose();
            crearTablaDinamica(ruta,shDataName,shPivotName);
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.INFO,String.format("Error: %s.",ex.getMessage()));
        }
    }
*/  
    public void crearReporteObjetosCostos(int periodo, String ruta, int repartoTipo) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera = new ArrayList(Arrays.asList("PERIODO","CODIGO_PRODUCTO","NOMBRE_PRODUCTO","CODIGO_LINEA","NOMBRE_LINEA","CODIGO_SUBCANAL","NOMBRE_SUBCANAL","CODIGO_CANAL","NOMBRE_CANAL","CODIGO_CENTRO_ORIGEN","NOMBRE_CENTRO_ORIGEN","GRUPO_GASTO","MONTO","CODIGO_CUENTA_CONTABLE_ORIGEN","NOMBRE_CUENTA_CONTABLE_ORIGEN","CODIGO_PARTIDA_ORIGEN","NOMBRE_PARTIDA_ORIGEN","CODIGO_CENTRO_ORIGEN","NOMBRE_CENTRO_ORIGEN","CODIGO_DRIVER","NOMBRE_DRIVER"));
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            // Contenido de la tabla en la fila rowNum+1
            CellStyle decimalCellStyle = decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteObjetosCostos(periodo, repartoTipo);
            while(rs.next()) {
                int periodoReporte = rs.getInt("PERIODO");
                String codigoProducto = rs.getString("CODIGO_PRODUCTO");
                String nombreProducto = rs.getString("NOMBRE_PRODUCTO");
                String codigoLinea = rs.getString("CODIGO_LINEA");
                String nombreLinea = rs.getString("NOMBRE_LINEA");
                String codigoSubcanal = rs.getString("CODIGO_SUBCANAL");
                String nombreSubcanal = rs.getString("NOMBRE_SUBCANAL");
                String codigoCanal = rs.getString("CODIGO_CANAL");
                String nombreCanal = rs.getString("NOMBRE_CANAL");
                String codigoEntidadOrigen = rs.getString("CODIGO_ENTIDAD_ORIGEN");
                String nombreEntidadOrigen = rs.getString("NOMBRE_ENTIDAD_ORIGEN");
                String grupoGasto = rs.getString("GRUPO_GASTO");
                double saldo = rs.getDouble("MONTO");
                String codigoCuentaContableOrigen = rs.getString("CODIGO_CUENTA_ORIGEN");
                String nombreCuentaContableOrigen = rs.getString("NOMBRE_CUENTA_ORIGEN");
                String codigoPartidaOrigen = rs.getString("CODIGO_PARTIDA_ORIGEN");
                String nombrePartidaOrigen = rs.getString("NOMBRE_PARTIDA_ORIGEN");
                String codigoCentroOrigen = rs.getString("CODIGO_CENTRO_ORIGEN");
                String nombreCentroOrigen = rs.getString("NOMBRE_CENTRO_ORIGEN");
                String codigoDriver = rs.getString("CODIGO_DRIVER");
                String nombreDriver = rs.getString("NOMBRE_DRIVER");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodoReporte);
                // PRODUCTO
                row.createCell(idxColumn++).setCellValue(codigoProducto);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreProducto);
                // LINEA
                row.createCell(idxColumn++).setCellValue(codigoLinea);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreLinea);
                // SUBCANAL
                row.createCell(idxColumn++).setCellValue(codigoSubcanal);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreSubcanal);
                // CANAL
                row.createCell(idxColumn++).setCellValue(codigoCanal);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCanal);
                // ENTIDAD ORIGEN
                row.createCell(idxColumn++).setCellValue(codigoEntidadOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreEntidadOrigen);
                // GRUPO DE GASTO
                row.createCell(idxColumn++).setCellValue(grupoGasto);
                // MONTO
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(saldo);
                // CUENTA CONTABLE ORIGEN
                row.getCell(idxColumn++).setCellStyle(decimalCellStyle);
                row.createCell(idxColumn++).setCellValue(codigoCuentaContableOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCuentaContableOrigen);
                // PARTIDA ORIGEN
                
                row.createCell(idxColumn++).setCellValue(codigoPartidaOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombrePartidaOrigen);
                // CENTRO ORIGEN
                
                row.createCell(idxColumn++).setCellValue(codigoCentroOrigen);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreCentroOrigen);
                // DRIVER
                row.createCell(idxColumn++).setCellValue(codigoDriver);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(nombreDriver);
                
               if(rowNum % 100 == 0) ((SXSSFSheet)sh).flushRows(100);
            }
            FileOutputStream out = new FileOutputStream(ruta);
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.INFO,ex.getMessage());
        }
    }
    
    public void crearReporteGastosOperacionesDeCambio(int periodo, String ruta) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh;
            int rowNum;
            List<String> listaCabecera;
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            CellStyle numberCellStyle = decimalEstilo(wb);
            ResultSet rs;
            //HOJA
            sh = wb.createSheet("REPORTE");            
            // Cabecera de la tabla
            rowNum = 0;
            listaCabecera = new ArrayList(Arrays.asList("PERIODO","OFICINA_CODIGO","OFICINA_NOMBRE","INGRESOS_OPERACIONES_DE_CAMBIO","INGRESOS_GIROS","GASTOS_OPERACIONES_DE_CAMBIO","GASTOS_OPERACIONES_DE_CAMBIO_AJUSTADO","RESULTADO_ORIGINAL","RESULTADO_AJUSTADO","DIFERENCIA"));
            crearCabecera(listaCabecera, sh, rowNum, headerCellStyle);            
            // Contenido de la tabla en la fila rowNum+1
            ++rowNum;
            rs = reportingDAO.dataReporteGastosOperacionesDeCambio(periodo);
            while(rs.next()) {
                String oficinaCodigo = rs.getString("OFICINA_CODIGO");
                String oficinaNombre = rs.getString("OFICINA_NOMBRE");
                double ingresosOperacionesDeCambio = rs.getDouble("INGRESOS_OPE_DE_CAMBIO");
                double ingresosGiros = rs.getDouble("INGRESOS_GIROS");
                double gastosOperacionesDeCambio = rs.getDouble("GASTOS_OPE_DE_CAMBIO");
                double gastosOperacionesDeCambioAjustados = rs.getDouble("GASTOS_OPE_DE_CAMBIO_AJUSTADO");
                
                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodo);
                row.createCell(idxColumn++).setCellValue(oficinaCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(oficinaNombre);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(ingresosOperacionesDeCambio);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(ingresosGiros);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(gastosOperacionesDeCambio);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(gastosOperacionesDeCambioAjustados);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(ingresosOperacionesDeCambio+ingresosGiros+gastosOperacionesDeCambio);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(ingresosOperacionesDeCambio+ingresosGiros+gastosOperacionesDeCambioAjustados);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(gastosOperacionesDeCambio-gastosOperacionesDeCambioAjustados);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                
               if(rowNum % 100 == 0) ((SXSSFSheet)sh).flushRows(100);
            }
            FileOutputStream out = new FileOutputStream(ruta);
            wb.write(out);
            out.close();
            wb.dispose();
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.INFO,ex.getMessage());
        }
    }
    
    public void crearReporteTrazabilidad(){
        
    }
    
    public boolean existeInformacionReporteBolsasOficinas(int periodo, int repartoTipo) {
        return reportingDAO.existeInformacionReporteBolsasOficinas(periodo, repartoTipo);
    }
    
    public boolean existeInformacionReporteCascada(int periodo, int repartoTipo) {
        return reportingDAO.existeInformacionReporteCascada(periodo, repartoTipo);
    }
    
    public boolean existeInformacionReporteObjetos(int periodo, int repartoTipo) {
        return reportingDAO.existeInformacionReporteObjetos(periodo, repartoTipo);
    }
    
    public void generarReporteBolsasOficinas(int periodo, int repartoTipo) {
        reportingDAO.generarReporteBolsasOficinas(periodo, repartoTipo);
    }
    
    public void generarReporteCascada(int periodo, int repartoTipo) {
        reportingDAO.generarReporteCascada(periodo, repartoTipo);
    }
    
    public void generarReporteObjetos(int periodo, int repartoTipo) {
        reportingDAO.generarReporteObjetos(periodo, repartoTipo);
    }
}
