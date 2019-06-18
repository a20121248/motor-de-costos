package controlador.parametrizacion.objetos.periodo;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoDAO;
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
import modelo.CargarObjetoPeriodoLinea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkAsignacion;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<CargarObjetoPeriodoLinea> tabListar;
    @FXML private TableColumn<CargarObjetoPeriodoLinea, Integer> tabcolPeriodo;
    @FXML private TableColumn<CargarObjetoPeriodoLinea, String> tabcolCodigo;
    @FXML private TableColumn<CargarObjetoPeriodoLinea, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnCancelar;
    @FXML private JFXButton btnSubir;
    
    // Variables de la aplicacion
    ObjetoDAO objetoDAO;
    public MenuControlador menuControlador;
    int periodoSeleccionado;
    int anhoSeleccionado;
    int mesSeleccionado;
    String objetoNombre;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO.getControlador());
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Cargar Oficinas");
                lnkObjetos.setText("Oficinas");
                objetoNombre = "Oficina";
                break;
            case "BAN":
                lblTitulo.setText("Cargar Bancas");
                lnkObjetos.setText("Bancas");
                objetoNombre = "Banca";
                break;
            case "PRO":
                lblTitulo.setText("Cargar Productos");
                lnkObjetos.setText("Productos");
                objetoNombre = "Producto";
                break;
            case "SCA":
                lblTitulo.setText("Cargar Subcanal");
                lnkObjetos.setText("Subcanales");
                objetoNombre = "Subcanal";
                break;
            default:
                break;
        }
        // Tabla
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolPeriodo.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        tabcolCodigo.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f*Integer.MAX_VALUE * 60);
        tabcolPeriodo.setCellValueFactory(cellData -> cellData.getValue().periodoProperty().asObject());
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // Meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
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
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkObjetosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @FXML void lnkAsignacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de " + objetoNombre + "s");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<CargarObjetoPeriodoLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            tabListar.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
        }
    }
    
    private List<CargarObjetoPeriodoLinea> leerArchivo(String rutaArchivo) {
        List<CargarObjetoPeriodoLinea> lista = new ArrayList();
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
            List<String> listaCabecera = new ArrayList(Arrays.asList("PERIODO","CODIGO","NOMBRE"));
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
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();

                CargarObjetoPeriodoLinea linea = new CargarObjetoPeriodoLinea(periodo,codigo,nombre);
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
    
    @FXML void btnSubirAction(ActionEvent event) {
        List<CargarObjetoPeriodoLinea> lista = tabListar.getItems();
        if(lista.isEmpty())
        {
            menuControlador.navegador.mensajeInformativo("Subir Información", "No hay información.");
        }
        else{
            objetoDAO.insertarListaObjetoPeriodo(periodoSeleccionado,lista);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", objetoNombre + "s asignados correctamente.");
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
        }
        objetoDAO.insertarListaObjetoPeriodo(periodoSeleccionado,lista);
        menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", objetoNombre + "s asignados correctamente.");
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO);
    }
}
