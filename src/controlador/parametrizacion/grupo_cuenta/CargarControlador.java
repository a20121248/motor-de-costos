package controlador.parametrizacion.grupo_cuenta;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PlanDeCuentaDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.CargarGrupoCuentaLinea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkAsignaciones;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<CargarGrupoCuentaLinea> tabListar;
    @FXML private TableColumn<CargarGrupoCuentaLinea, String> tabcolCodigoCuenta;
    @FXML private TableColumn<CargarGrupoCuentaLinea, String> tabcolNombreCuenta;
    @FXML private TableColumn<CargarGrupoCuentaLinea, String> tabcolCodigoGrupo;
    @FXML private TableColumn<CargarGrupoCuentaLinea, String> tabcolNombreGrupo;
    
    @FXML private Button btnCancelar;
    @FXML private Button btnSubir;
    @FXML private Label lblNumeroRegistros;
    
    // Variables de la aplicacion
    PlanDeCuentaDAO planDeCuentaDAO;
    public MenuControlador menuControlador;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPO_CUENTA_CARGAR.getControlador());
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla formato
        tabcolCodigoCuenta.setCellValueFactory(cellData -> cellData.getValue().codigoPlanDeCuentaProperty());
        tabcolNombreCuenta.setCellValueFactory(cellData -> cellData.getValue().nombrePlanDeCuentaProperty());
        tabcolCodigoGrupo.setCellValueFactory(cellData -> cellData.getValue().codigoAgrupacionProperty());
        tabcolNombreGrupo.setCellValueFactory(cellData -> cellData.getValue().nombreAgrupacionProperty());
        // tabla dimensiones
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigoCuenta.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        tabcolNombreCuenta.setMaxWidth( 1f * Integer.MAX_VALUE * 35);
        tabcolCodigoGrupo.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        tabcolNombreGrupo.setMaxWidth( 1f * Integer.MAX_VALUE * 35);        
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

    @FXML void lnkAsignarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPO_CUENTA_LISTAR);
    }

    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPO_CUENTA_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("Abrir asignaciones");
        /*file_chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx")
        );*/

        File archivoSeleccionado = file_chooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<CargarGrupoCuentaLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            tabListar.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
        }
    }
    
    private List<CargarGrupoCuentaLinea> leerArchivo(String rutaArchivo) {
        List<CargarGrupoCuentaLinea> lista = new ArrayList();
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
            List<String> listaCabecera = new ArrayList(Arrays.asList("PERIODO","CODIGO CUENTA","NOMBRE CUENTA","CODIGO AGRUPACION","NOMBRE AGRUPACION"));
            int numFilaCabecera = 1;
            boolean archivoEstaBien = true;
            while (filas.hasNext() && archivoEstaBien) {
                /*for (int i = 0; i < numFilasOmitir; i++) {
                    filas.next();
                }*/
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
                        tabListar.getItems().clear();
                        txtRuta.setText("");                        
                        archivoEstaBien = false;
                    }
                    continue;
                }
                
                // leemos una fila completa                
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCuenta = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCuenta = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoAgrupacion = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreAgrupacion = celda.getStringCellValue();

                CargarGrupoCuentaLinea linea = new CargarGrupoCuentaLinea(periodo,codigoCuenta,nombreCuenta,codigoAgrupacion,nombreAgrupacion);
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
    
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        List<CargarGrupoCuentaLinea> lista = tabListar.getItems();
        if(lista.isEmpty())
        {
            menuControlador.navegador.mensajeInformativo("Subir Información", "No hay información.");
        }
        else{
            planDeCuentaDAO.insertarCuentasGrupo(periodoSeleccionado,lista,menuControlador.repartoTipo);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Asignaciones subidas correctamente.");
            menuControlador.objeto = "Todos";
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPO_CUENTA_LISTAR);
        }
        
        /*for (CargarCentroLinea cargarCentroLinea : lista) {
            //planDeCuentaDAO.insertarSaldo(cargarBalanceteLinea);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Centros de Costos subidos correctamente.");
            Navegador.cambiarVista(Navegador.RUTAS_CENTROS_LISTAR);
        }*/
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.objeto = "Todos";
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPO_CUENTA_LISTAR);
    }
}
