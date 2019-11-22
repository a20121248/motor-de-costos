package controlador.reporting;

import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import dao.ObjetoGrupoDAO;
import dao.ProcesosDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import modelo.Grupo;
import org.controlsfx.control.CheckComboBox;
import servicios.ReportingServicio;

public class PrincipalControlador implements Initializable {
    // periodo
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private CheckComboBox<Grupo> cmbR5Dimensiones, cmbR5Lineas;
    
    // Variables de la aplicacion
    ObjetoGrupoDAO productoGrupoDAO, subcanalGrupoDAO;
    CentroDAO centroDAO;
    ProcesosDAO procesosDAO;
    List<CheckBox> listaDescargar = new ArrayList();
    public MenuControlador menuControlador;
    ReportingServicio reportingServicio;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_MODULO_REPORTING.getControlador());
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        productoGrupoDAO = new ObjetoGrupoDAO("PRO");
        subcanalGrupoDAO = new ObjetoGrupoDAO("SCA");
        centroDAO = new CentroDAO();
        reportingServicio = new ReportingServicio();
        procesosDAO = new ProcesosDAO();
        // Periodo seleccionado
        if (menuControlador.periodoSeleccionado % 100 == 0)
            ++menuControlador.periodoSeleccionado;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mes seleccionado
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        
        // Anho seleccionado
        spAnho.getValueFactory().setValue(menuControlador.periodoSeleccionado / 100);
        spAnho.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                menuControlador.periodoSeleccionado = newValue*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        
        ObservableList<Grupo> obsLstLineas = FXCollections.observableList(productoGrupoDAO.listarObjetos(0));
        
        cmbR5Dimensiones.getItems().addAll(obsLstLineas);
        cmbR5Dimensiones.setConverter(new StringConverter<Grupo>() {
            @Override
            public String toString(Grupo cmb) {
                return cmb.getNombre();
            }
            @Override
            public Grupo fromString(String string) {
                return cmbR5Dimensiones.getItems().stream().filter(ap -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
        
        cmbR5Lineas.getItems().addAll(obsLstLineas);
        cmbR5Lineas.setConverter(new StringConverter<Grupo>() {
            @Override
            public String toString(Grupo cmb) {
                return cmb.getNombre();
            }
            @Override
            public Grupo fromString(String string) {
                return cmbR5Lineas.getItems().stream().filter(ap -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }
    
    @FXML void lnkReportingAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_REPORTING);
    }
    
    @FXML void lnkControlAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_REPORTING);
    }
    
    @FXML void btnGenerarR1Action(ActionEvent event) throws IOException {
        String nombreTabla = menuControlador.tablaReporteBolsasOficinas + (menuControlador.repartoTipo == 1 ? "_R" : "_P");

        String nombre = "Reporte de distribución de bolsas y oficinas";
        if (reportingServicio.existeInformacionReporteBolsasOficinas(menuControlador.periodoSeleccionado, menuControlador.repartoTipo))
            if (!menuControlador.navegador.mensajeConfirmar(nombre, String.format("Existe un reporte ya guardado.\n¿Está seguro que desea borrarlo y generar uno nuevo?")))
                return;
        
        String mensaje = String.format(
                "El resultado del reporte se ha guardado en la tabla '%s'.\n\nPara acceder a la información, considerar lo siguiente:\n" +
                " - Utilizar los filtros de PERIODO=%d en dicha tabla.\n" +
                " - Para mayor velocidad, se puede acceder directamente a la partición 'P_%d'.",
                nombreTabla, menuControlador.periodoSeleccionado, menuControlador.periodoSeleccionado
        );
        
        //if (procesosDAO.obtenerEstadoProceso(periodoSeleccionado, menuControlador.repartoTipo) == 1) {// proceso está cerrado
            //menuControlador.mensaje.execute_close_process_empty_error();
        //} else {
            //if (procesosDAO.obtenerEstadoProceso(periodoSeleccionado, menuControlador.repartoTipo) == 1) { // valida si hay informacion en el procesamiento (existeDistribucionBolsa/Staff/Objeto-Periodo, dentro de CentroDAO)
                reportingServicio.generarReporteBolsasOficinas(menuControlador.periodoSeleccionado, menuControlador.repartoTipo, nombreTabla);
                menuControlador.mensaje.execute_report_success(nombre, mensaje);
            //} else {
                //menuControlador.mensaje.execute_report_error(nombre, "No se pudo generar el reporte porque no hay información en el procesamiento.");
            //}
        //}
    }
    
    @FXML void btnExportarR1Action(ActionEvent event) throws IOException {
        String fechaFase1Str = reportingServicio.obtenerFechaFase(menuControlador.periodoSeleccionado, menuControlador.repartoTipo, 1);
        
        if (fechaFase1Str.equals("")) {
            menuControlador.navegador.mensajeError("Reporte 1", "No se ejecutó la fase 1.");
            return;
        }
        
        String tipoRepartoStr = menuControlador.repartoTipo == 1 ? "real" : "presupuesto";
        String rutaOrigen = "." + File.separator + "reportes" + File.separator + tipoRepartoStr + File.separator + menuControlador.periodoSeleccionado;
        Navegador.crearCarpeta(rutaOrigen);//TODO, quitar el static para rendimiento
        String reporteNombre = fechaFase1Str + " - " + menuControlador.periodoSeleccionado + " - " + "Reporte de bolsas y oficinas";
        rutaOrigen += File.separator + reporteNombre +".xlsx";
        reportingServicio.crearReporteBolsasOficinas(menuControlador.periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre, "Descarga completa.");
        }
    }
    
    @FXML void btnGenerarR2Action(ActionEvent event) throws IOException {
        String nombreTabla = menuControlador.tablaReporteCascada + (menuControlador.repartoTipo == 1 ? "_R" : "_P");
        
        String nombre = "Distribución de cascada de centros de costos";
        if (reportingServicio.existeInformacionReporteBolsasOficinas(menuControlador.periodoSeleccionado, menuControlador.repartoTipo))
            if (!menuControlador.navegador.mensajeConfirmar(nombre, String.format("Existe un reporte ya guardado.\n¿Está seguro que desea borrarlo y generar uno nuevo?")))
                return;
        
        String mensaje = String.format("" +
                "El resultado del reporte se ha guardado en la tabla '%s'.\n\nPara acceder a la información, considerar lo siguiente:\n" +
                " - Utilizar los filtros de PERIODO=%d en dicha tabla.\n" +
                " - Para mayor velocidad, se puede acceder directamente a la partición 'P_%d'.",
                nombreTabla, menuControlador.periodoSeleccionado, menuControlador.periodoSeleccionado
        );
        
        //if (procesosDAO.obtenerEstadoProceso(periodoSeleccionado, menuControlador.repartoTipo) == 1) {// proceso está cerrado
            //menuControlador.mensaje.execute_close_process_empty_error();
        //} else {
            //if (procesosDAO.obtenerEstadoProceso(periodoSeleccionado, menuControlador.repartoTipo) == 1) { // valida si hay informacion en el procesamiento (existeDistribucionBolsa/Staff/Objeto-Periodo, dentro de CentroDAO)
                reportingServicio.generarReporteCascada(menuControlador.periodoSeleccionado, menuControlador.repartoTipo, nombreTabla);
                menuControlador.mensaje.execute_report_success(nombre, mensaje);
            //} else {
                //menuControlador.mensaje.execute_report_error(nombre, "No se pudo generar el reporte porque no hay información en el procesamiento.");
            //}
        //}
    }
    
    @FXML void btnGenerarR3Action(ActionEvent event) throws IOException {
        String nombreTabla = menuControlador.tablaReporteObjetos + (menuControlador.repartoTipo == 1 ? "_R" : "_P");
        
        String nombre = "Distribución de objetos de costos";
        if (reportingServicio.existeInformacionReporteBolsasOficinas(menuControlador.periodoSeleccionado, menuControlador.repartoTipo))
            if (!menuControlador.navegador.mensajeConfirmar(nombre, String.format("Existe un reporte ya guardado.\n¿Está seguro que desea borrarlo y generar uno nuevo?")))
                return;
        
        String mensaje = String.format("" +
                "El resultado del reporte se ha guardado en la tabla '%s'.\n\nPara acceder a la información, considerar lo siguiente:\n" +
                " - Utilizar los filtros de PERIODO=%d en dicha tabla.\n" +
                " - Para mayor velocidad, se puede acceder directamente a la partición 'P_%d'.",
                nombreTabla, menuControlador.periodoSeleccionado, menuControlador.periodoSeleccionado
        );
        
        //if (procesosDAO.obtenerEstadoProceso(periodoSeleccionado, menuControlador.repartoTipo) == 1) {// proceso está cerrado
            //menuControlador.mensaje.execute_close_process_empty_error();
        //} else {
            //if (procesosDAO.obtenerEstadoProceso(periodoSeleccionado, menuControlador.repartoTipo) == 1) { // valida si hay informacion en el procesamiento (existeDistribucionBolsa/Staff/Objeto-Periodo, dentro de CentroDAO)
                reportingServicio.generarReporteObjetos(menuControlador.periodoSeleccionado, menuControlador.repartoTipo, nombreTabla);
                menuControlador.mensaje.execute_report_success(nombre, mensaje);
            //} else {
                //menuControlador.mensaje.execute_report_error(nombre, "No se pudo generar el reporte porque no hay información en el procesamiento.");
            //}
        //}
    }
        
    @FXML void btnExportarR4Action(ActionEvent event) throws IOException {
        String fechaFase3Str = reportingServicio.obtenerFechaFase(menuControlador.periodoSeleccionado, menuControlador.repartoTipo, 1);
        
        if (fechaFase3Str.equals("")) {
            menuControlador.navegador.mensajeError("Reporte 5", "No se ejecutó la fase 3.");
            return;
        }
        
        String tipoRepartoStr = menuControlador.repartoTipo == 1 ? "real" : "presupuesto";
        String rutaOrigen = "." + File.separator + "reportes" + File.separator + tipoRepartoStr + File.separator + menuControlador.periodoSeleccionado;
        Navegador.crearCarpeta(rutaOrigen);//TODO, quitar el static para rendimiento
        String reporteNombre = fechaFase3Str + " - " + menuControlador.periodoSeleccionado + " - " + "Reporte de cascada de centros";
        rutaOrigen += File.separator + reporteNombre +".xlsx";
        reportingServicio.crearReporteCascadaCentros(menuControlador.periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre, "Descarga completa.");
        }
    }
    
    @FXML void btnExportarR5Action(ActionEvent event) throws IOException {
        String fechaFase3Str = reportingServicio.obtenerFechaFase(menuControlador.periodoSeleccionado, menuControlador.repartoTipo, 1);
        
        if (fechaFase3Str.equals("")) {
            menuControlador.navegador.mensajeError("Reporte 4", "No se ejecutó la fase 3.");
            return;
        }
        
        String tipoRepartoStr = menuControlador.repartoTipo == 1 ? "real" : "presupuesto";
        String rutaOrigen = "." + File.separator + "reportes" + File.separator + tipoRepartoStr + File.separator + menuControlador.periodoSeleccionado;
        Navegador.crearCarpeta(rutaOrigen);//TODO, quitar el static para rendimiento
        String reporteNombre = fechaFase3Str + " - " + menuControlador.periodoSeleccionado + " - " + "Reporte Línea y Canal";
        rutaOrigen += File.separator + reporteNombre +".xlsx";
        reportingServicio.crearReporteLineaCanal(menuControlador.periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre, "Descarga completa.");
        }
    }
}
