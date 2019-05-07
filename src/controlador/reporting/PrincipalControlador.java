package controlador.reporting;

import controlador.MenuControlador;
import controlador.Navegador;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import servicios.ReportingServicio;

public class PrincipalControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkReporting;
    // periodo
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private HBox hbxReporte1;
    @FXML private TextArea txtareaReporte1;
    @FXML private HBox hbxReporte2;
    @FXML private TextArea txtareaReporte2;
    @FXML private HBox hbxReporte3;
    @FXML private TextArea txtareaReporte3;
    @FXML private HBox hbxReporte4;
    @FXML private TextArea txtareaReporte4;
    @FXML private HBox hbxReporte5;
    @FXML private TextArea txtareaReporte5;
    
    public MenuControlador menuControlador;
    ReportingServicio reportingServicio;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_MODULO_REPORTING.getControlador());
    
    public PrincipalControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        reportingServicio = new ReportingServicio();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        // Periodo seleccionado
        periodoSeleccionado = menuControlador.periodo;
        if (menuControlador.repartoTipo == 2) {
            hbxReporte4.setVisible(false);
            hbxReporte5.setVisible(false);
            txtareaReporte3.setText("Gasto de Operaciones de Cambio ajustado por Oficina.");
        }
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }
    
    @FXML void lnkReportingAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PROCESOS);
    }
    
    private boolean archivoExiste(String ruta) {        
        return Files.exists(Paths.get(ruta));
    }
    
    @FXML void btnReporte1Action(ActionEvent event) throws IOException {
        String reporteNombre,rutaOrigen;
        if (menuControlador.repartoTipo == 1) {
            reporteNombre = "Reporte 01 - Cuentas Contables a Centros de Costos";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodoSeleccionado + File.separator + reporteNombre + ".xlsx";
        } else {
            reporteNombre = "Reporte 01 - Cuentas Contables a Centros de Beneficio";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "ingresos" + File.separator + periodoSeleccionado + File.separator + reporteNombre + ".xlsx";
        }
        if (!archivoExiste(rutaOrigen)) {
            if (menuControlador.navegador.mensajeConfirmar(reporteNombre, "No se puede descargar el reporte pues aún no se ha generado.\n¿Desea generarlo ahora?")) {
                reportingServicio.crearReporteCuentaCentro(periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
            } else {
                return;
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(periodoSeleccionado + " - " + reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre,"Descarga completa.");
        }
    }
    
    @FXML void btnReporte2Action(ActionEvent event) throws IOException {
        String reporteNombre;
        String rutaOrigen;
        if (menuControlador.repartoTipo==1) {
            reporteNombre = "Reporte 02 - Gasto Propio y Asignado de Centros de Costos";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodoSeleccionado + File.separator + reporteNombre +".xlsx";
            if (!archivoExiste(rutaOrigen)) {
                if (menuControlador.navegador.mensajeConfirmar(reporteNombre, "No se puede descargar el reporte pues aún no se ha generado.\n¿Desea generarlo ahora?")) {
                    reportingServicio.crearReporteGastoPropioAsignado(periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
                } else {
                    return;
                }
            }
        } else {
            reporteNombre = "Reporte 02 - Objetos de Beneficio";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "ingresos" + File.separator + periodoSeleccionado + File.separator + reporteNombre +".xlsx";
            if (!archivoExiste(rutaOrigen)) {
                if (menuControlador.navegador.mensajeConfirmar(reporteNombre, "No se puede descargar el reporte pues aún no se ha generado.\n¿Desea generarlo ahora?")) {
                    reportingServicio.crearReporteObjetos(periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
                } else {
                    return;
                }
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(periodoSeleccionado + " - " + reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre,"Descarga completa.");
        }
    }
    
    @FXML void btnReporte3Action(ActionEvent event) throws IOException {
        String reporteNombre;
        String rutaOrigen;
        if (menuControlador.repartoTipo==1) {
            reporteNombre = "Reporte 03 - Cascada de Staff";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodoSeleccionado + File.separator + reporteNombre +".xlsx";
            if (!archivoExiste(rutaOrigen)) {
                if (menuControlador.navegador.mensajeConfirmar(reporteNombre, "No se puede descargar el reporte pues aún no se ha generado.\n¿Desea generarlo ahora?")) {
                    reportingServicio.crearReporteCascada(periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
                } else {
                    return;
                }
            }
        } else {
            reporteNombre = "Reporte 03 - Gastos de Operaciones de Cambio";
            rutaOrigen = "." + File.separator + "reportes" + File.separator + "ingresos" + File.separator + periodoSeleccionado + File.separator + reporteNombre +".xlsx";
            if (!archivoExiste(rutaOrigen)) {
                if (menuControlador.navegador.mensajeConfirmar(reporteNombre, "No se puede descargar el reporte pues aún no se ha generado.\n¿Desea generarlo ahora?")) {
                    reportingServicio.crearReporteGastosOperacionesDeCambio(periodoSeleccionado, rutaOrigen);
                } else {
                    return;
                }
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(periodoSeleccionado + " - " + reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre,"Descarga completa.");
        }
    }
    
    @FXML void btnReporte4Action(ActionEvent event) throws IOException {
        String reporteNombre = "Reporte 04 - Objetos de Costos";
        String rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodoSeleccionado + File.separator + reporteNombre +".xlsx";
        if (!archivoExiste(rutaOrigen)) {
            if (menuControlador.navegador.mensajeConfirmar(reporteNombre, "No se puede descargar el reporte pues aún no se ha generado.\n¿Desea generarlo ahora?")) {
                reportingServicio.crearReporteObjetos(periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
            } else {
                return;
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(periodoSeleccionado + " - " + reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre,"Descarga completa.");
        }
    }
    
    @FXML void btnReporte5Action(ActionEvent event) throws IOException {
        String reporteNombre = "Reporte 05 - Gastos Administrativos y Operativos";
        String rutaOrigen = "." + File.separator + "reportes" + File.separator + "gastos" + File.separator + periodoSeleccionado + File.separator + reporteNombre +".xlsx";
        if (!archivoExiste(rutaOrigen)) {
            if (menuControlador.navegador.mensajeConfirmar(reporteNombre, "No se puede descargar el reporte pues aún no se ha generado.\n¿Desea generarlo ahora?")) {
                reportingServicio.crearReporteObjetosGastoAdmOpe(periodoSeleccionado, rutaOrigen, menuControlador.repartoTipo);
            } else {
                return;
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar " + reporteNombre);
        fileChooser.setInitialFileName(periodoSeleccionado + " - " + reporteNombre);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showSaveDialog(cmbMes.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo(reporteNombre,"Descarga completa.");
        }
    }
}
