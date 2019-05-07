package controlador.parametrizacion.driver_entidad;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.AsignacionEntidadDriverDAO;
import dao.CentroDAO;
import dao.DriverDAO;
import dao.GrupoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.CargarEntidadDriver;
import modelo.Driver;
import modelo.EntidadDistribucion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkAsignaciones;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<CargarEntidadDriver> tabAsignaciones;
    @FXML private TableColumn<CargarEntidadDriver, String> tabcolCodigoEntidad;
    @FXML private TableColumn<CargarEntidadDriver, String> tabcolNombreEntidad;
    @FXML private TableColumn<CargarEntidadDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CargarEntidadDriver, String> tabcolNombreDriver;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnCancelar;
    @FXML private JFXButton btnSubir;    
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    GrupoDAO grupoDAO;
    CentroDAO centroDAO;
    DriverDAO driverDAO;
    AsignacionEntidadDriverDAO asignacionEntidadDriverDAO;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CARGAR.getControlador());
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        grupoDAO = new GrupoDAO();
        centroDAO = new CentroDAO();
        driverDAO = new DriverDAO();
        asignacionEntidadDriverDAO = new AsignacionEntidadDriverDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla dimensiones
        tabAsignaciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoEntidad.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreEntidad.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        tabcolCodigoDriver.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreDriver.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        // tabla formato
        tabcolCodigoEntidad.setCellValueFactory(cellData -> cellData.getValue().codigoEntidadProperty());
        tabcolNombreEntidad.setCellValueFactory(cellData -> cellData.getValue().nombreEntidadProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().codigoDriverProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().nombreDriverProperty());
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkAsignacionesAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de planDeCuentas");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            cmbMes.setDisable(true);
            spAnho.setDisable(true);
            List<CargarEntidadDriver> lista = leerArchivo(archivoSeleccionado.getAbsolutePath(), periodoSeleccionado);
            if (lista != null) {
                tabAsignaciones.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<CargarEntidadDriver> leerArchivo(String rutaArchivo, int periodo) {
        List<CargarEntidadDriver> lista = new ArrayList();
        List<EntidadDistribucion> lstEntidades = new ArrayList();
        lstEntidades.addAll(grupoDAO.listar(periodoSeleccionado,"",menuControlador.repartoTipo));
        lstEntidades.addAll(centroDAO.listar(periodoSeleccionado,menuControlador.repartoTipo));
        List<Driver> lstDrivers = new ArrayList();
        lstDrivers.addAll(driverDAO.listarDriversCentroSinDetalle(periodoSeleccionado,menuControlador.repartoTipo));
        lstDrivers.addAll(driverDAO.listarDriversObjetoSinDetalle(periodoSeleccionado,menuControlador.repartoTipo));
        try {
            FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f);
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila = null;
            Cell celda = null;
            //int numFilasOmitir = 2
            //Estructura de la cabecera
            List<String> listaCabecera = new ArrayList(Arrays.asList("PERIODO","CODIGO ENTIDAD","NOMBRE ENTIDAD","CODIGO DRIVER","NOMBRE DRIVER"));
            int numFilaCabecera = 2;
            for (int i = 0; i < numFilaCabecera-1; i++) {
                 filas.next();
             }
            boolean archivoEstaBien = true;
            while (filas.hasNext() && archivoEstaBien) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // valido la cabecera
                if (fila.getRowNum() == numFilaCabecera - 1) {
                    List<String> listaCabeceraLeida = new ArrayList();
                    while (celdas.hasNext()) {
                        celda = celdas.next();
                        listaCabeceraLeida.add(celda.getStringCellValue());
                    }
                    if (!listaCabecera.equals(listaCabeceraLeida)) {
                        menuControlador.navegador.mensajeInformativo("Lectura de archivo Excel", "El archivo seleccionado no es el correcto.");
                        tabAsignaciones.getItems().clear();
                        txtRuta.setText("");                        
                        archivoEstaBien = false;
                    }
                    continue;
                }
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodoArchivo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoEntidad = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreEntidad = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoDriver = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreDriver = celda.getStringCellValue();

                if (periodo != periodoArchivo) {
                    menuControlador.navegador.mensajeInformativo("Cargar asignaciones", "El periodo " + periodoArchivo + " no coincide.\nNo se puede cargar el archivo.");
                    return null;
                }
                
                EntidadDistribucion entidad = lstEntidades.stream().filter(item -> codigoEntidad.equals(item.getCodigo())).findAny().orElse(null);
                if (entidad == null) {
                    menuControlador.navegador.mensajeInformativo("Cargar asignaciones", "No se encontró la entidad con código " + codigoEntidad + ".\nNo se puede cargar el archivo.");
                    return null;
                }
                
                Driver driver = lstDrivers.stream().filter(item -> codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
                if (driver == null) {
                    menuControlador.navegador.mensajeInformativo("Cargar asignaciones", "No se encontró el driver con código " + codigoDriver + ".\nNo se puede cargar el archivo.");
                    return null;
                }

                CargarEntidadDriver linea = new CargarEntidadDriver(periodo,codigoEntidad,nombreEntidad,codigoDriver,nombreDriver);
                lista.add(linea);
            }
            //cerramos el libro
            f.close();
            libro.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        return lista;
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_LISTAR);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        List<CargarEntidadDriver> lista = tabAsignaciones.getItems();
        asignacionEntidadDriverDAO.insertarListaAsignaciones(lista, periodoSeleccionado, menuControlador.repartoTipo);
        menuControlador.navegador.mensajeInformativo("Cargar asignaciones de driver a entidades", "Las asignaciones se guardaron correctamente.");
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_LISTAR);
    }
}
