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
       
    public void crearReporteCuentaCentro(int periodo, String ruta, int repartoTipo) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera;
            if (repartoTipo == 1)
                listaCabecera = new ArrayList(Arrays.asList("PERIODO","CECO_CODIGO","CECO_NOMBRE","GRUPO_CUENTAS_CODIGO","GRUPO_CUENTAS_NOMBRE","CUENTA_CONTABLE_CODIGO","CUENTA_CONTABLE_NOMBRE","GASTO"));
            else
                listaCabecera = new ArrayList(Arrays.asList("PERIODO","CEBE_CODIGO","CEBE_NOMBRE","GRUPO_CUENTAS_CODIGO","GRUPO_CUENTAS_NOMBRE","CUENTA_CONTABLE_CODIGO","CUENTA_CONTABLE_NOMBRE","INGRESO"));
            
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle numberCellStyle = decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteCuentaCentro(periodo, repartoTipo);
            while(rs.next()) {
                String centroCodigo = rs.getString("CENTRO_CODIGO");
                String centroNombre = rs.getString("CENTRO_NOMBRE");
                String grupoCodigo = rs.getString("GRUPO_CODIGO");
                String grupoNombre = rs.getString("GRUPO_NOMBRE");
                String cuentaCodigo = rs.getString("CUENTA_CONTABLE_CODIGO");
                String cuentaNombre = rs.getString("CUENTA_CONTABLE_NOMBRE");
                double saldo = rs.getDouble("SALDO");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodo);
                row.createCell(idxColumn++).setCellValue(centroCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(centroNombre);
                row.createCell(idxColumn++).setCellValue(grupoCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(grupoNombre);
                row.createCell(idxColumn++).setCellValue(cuentaCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(cuentaNombre);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(saldo);
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

    public void crearReporteGastoPropioAsignado(int periodo, String ruta, int repartoTipo) {
        try {
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera = new ArrayList(Arrays.asList("PERIODO","CECO_CODIGO","CECO_NOMBRE","CECO_NIVEL","CECO_TIPO","ITERACION","TIPO_GASTO_CODIGO","TIPO_ENTIDAD","CODIGO_ENTIDAD","NOMBRE_ENTIDAD","CECO_ASIGNADO_NIVEL","CUENTA_CONTABLE_CODIGO","CUENTA_CONTABLE_NOMBRE","GASTO"));
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle decimalCellStyle = decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteGastoPropioAsignado(periodo,repartoTipo);
            while(rs.next()) {
                String centroCodigo = rs.getString("CECO_CODIGO");
                String centroNombre = rs.getString("CECO_NOMBRE");
                int centroNivel = rs.getInt("CECO_NIVEL");
                String centroTipo = rs.getString("CECO_TIPO");
                int iteracion = rs.getInt("ITERACION");
                String tipoGasto = rs.getString("TIPO_GASTO");
                String tipoEntidad = rs.getString("TIPO_ENTIDAD");
                String entidadCodigo = rs.getString("CODIGO_ENTIDAD");
                String entidadNombre = rs.getString("NOMBRE_ENTIDAD");
                String nivelCecoAsignado = rs.getString("CECO_ASIGNADO_NIVEL");
                String cuentaCodigo = rs.getString("CUENTA_CONTABLE_CODIGO");
                String cuentaNombre = rs.getString("CUENTA_CONTABLE_NOMBRE");
                double gasto = rs.getDouble("GASTO");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodo);
                row.createCell(idxColumn++).setCellValue(centroCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(centroNombre);
                row.createCell(idxColumn++).setCellValue(centroNivel);
                row.createCell(idxColumn++).setCellValue(centroTipo);
                row.createCell(idxColumn++).setCellValue(iteracion);
                row.createCell(idxColumn++).setCellValue(tipoGasto);
                row.createCell(idxColumn++).setCellValue(tipoEntidad);
                row.createCell(idxColumn++).setCellValue(entidadCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(entidadNombre);
                row.createCell(idxColumn++).setCellValue(nivelCecoAsignado);
                row.createCell(idxColumn++).setCellValue(cuentaCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(cuentaNombre);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(gasto);
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

    public void crearReporteCascada(int periodo, String ruta, int repartoTipo) {
        try {
            String shDataName = "REPORTE_DATA";
            String shPivotName = "REPORTE";
            XSSFWorkbook wb = new XSSFWorkbook();
            wb.createSheet(shPivotName);
            XSSFSheet sh = wb.createSheet(shDataName);
            
            // Cabecera de la tabla
            int rowNum = 0;
            List<String> listaCabecera = new ArrayList(Arrays.asList("CECO","CECO_NIVEL","CECO_TIPO","ITERACION","SALDO"));
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            crearCabecera(listaCabecera, sh, rowNum++, headerCellStyle);
            
            // Contenido de la tabla en la fila rowNum+1
            CellStyle numberCellStyle = ReportingServicio.this.decimalEstilo(wb);
            ResultSet rs = reportingDAO.dataReporteCascada(periodo, repartoTipo);
            while(rs.next()) {
                String cecoCodigo = rs.getString("CECO_CODIGO");
                String cecoNombre = rs.getString("CECO_NOMBRE");
                String cecoNivel = rs.getString("CECO_NIVEL");
                String cecoTipo = rs.getString("CECO_TIPO");
                int iteracion = rs.getInt("ITERACION");
                double saldo = rs.getDouble("SALDO");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                sh.setColumnWidth(idxColumn, 8000);
                row.createCell(idxColumn++).setCellValue(cecoCodigo + " - " + cecoNombre);
                row.createCell(idxColumn++).setCellValue(cecoNivel);
                row.createCell(idxColumn++).setCellValue(cecoTipo);
                row.createCell(idxColumn++).setCellValue(iteracion == 0 ? "CUENTAS CONTABLES" : "CECOS NIVEL " + iteracion);
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
    
    public void crearReporteObjetos(int periodo, String ruta, int repartoTipo) {
        try {            
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh = wb.createSheet("REPORTE");
            
            int oficinaNiveles = reportingDAO.numeroNiveles(periodo,"OFI");
            int bancaNiveles = reportingDAO.numeroNiveles(periodo,"BAN");
            int productoNiveles = reportingDAO.numeroNiveles(periodo,"PRO");
            
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
    
    public void crearReporteObjetosGastoAdmOpe(int periodo, String ruta, int repartoTipo) {
        try {            
            SXSSFWorkbook wb = new SXSSFWorkbook(-1);
            SXSSFSheet sh;
            int rowNum;
            List<String> listaCabecera;
            CellStyle headerCellStyle = cabeceraEstilo(wb);
            CellStyle numberCellStyle = decimalEstilo(wb);
            ResultSet rs;
            // crea las tablas intermedias
            reportingDAO.dataReporteObjetosGastoAdmOpeBorrarTablas();
            reportingDAO.dataReporteObjetosGastoAdmOpeLlenarTablas(periodo, repartoTipo);
            //HOJA
            sh = wb.createSheet("REPORTE");            
            // Cabecera de la tabla
            rowNum = 0;
            listaCabecera = new ArrayList(Arrays.asList("PERIODO","OFICINA_CODIGO","OFICINA_NOMBRE","BANCA_CODIGO","BANCA_NOMBRE","PRODUCTO_CODIGO","PRODUCTO_NOMBRE","GASTOS_OPERATIVOS","GASTOS_ADMINISTRATIVOS","GASTOS_TOTALES","CECO_ORIGEN_CODIGO","CECO_ORIGEN_NOMBRE","DRIVER_CODIGO","DRIVER_NOMBRE"));
            crearCabecera(listaCabecera, sh, rowNum, headerCellStyle);            
            // Contenido de la tabla en la fila rowNum+1
            ++rowNum;
            rs = reportingDAO.dataReporteObjetosGastoAdmOpe("JMD_REP_ADM_OPE_OBCO_F");
            while(rs.next()) {
                String oficinaCodigo = rs.getString("OFICINA_CODIGO");
                String oficinaNombre = rs.getString("OFICINA_NOMBRE");
                String bancaCodigo = rs.getString("BANCA_CODIGO");
                String bancaNombre = rs.getString("BANCA_NOMBRE");
                String productoCodigo = rs.getString("PRODUCTO_CODIGO");
                String productoNombre = rs.getString("PRODUCTO_NOMBRE");
                double gastosOperativos = rs.getDouble("GASTOS_OPERATIVOS");
                double gastosAdministrativos = rs.getDouble("GASTOS_ADMINISTRATIVOS");
                double gastosTotales = rs.getDouble("GASTOS_TOTALES");
                String cecoOrigenCodigo = rs.getString("CECO_ORIGEN_CODIGO");
                String cecoOrigenNombre = rs.getString("CECO_ORIGEN_NOMBRE");
                String driverCodigo = rs.getString("DRIVER_CODIGO");
                String driverNombre = rs.getString("DRIVER_NOMBRE");

                Row row = sh.createRow(rowNum++);
                int idxColumn = 0;
                row.createCell(idxColumn++).setCellValue(periodo);
                row.createCell(idxColumn++).setCellValue(oficinaCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(oficinaNombre);
                row.createCell(idxColumn++).setCellValue(bancaCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(bancaNombre);
                row.createCell(idxColumn++).setCellValue(productoCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(productoNombre);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(gastosOperativos);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(gastosAdministrativos);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                sh.setColumnWidth(idxColumn, 5000);
                row.createCell(idxColumn).setCellValue(gastosTotales);
                row.getCell(idxColumn++).setCellStyle(numberCellStyle);
                row.createCell(idxColumn++).setCellValue(cecoOrigenCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(cecoOrigenNombre);
                row.createCell(idxColumn++).setCellValue(driverCodigo);
                sh.setColumnWidth(idxColumn, 6000);
                row.createCell(idxColumn++).setCellValue(driverNombre);
                
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
}
