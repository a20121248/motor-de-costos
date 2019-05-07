package controlador.aprovisionamiento.drivers_objeto;

import com.jfoenix.controls.JFXButton;
import controlador.ConexionBD;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.BancaDAO;
import dao.DriverDAO;
import dao.OficinaDAO;
import dao.ProductoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.Banca;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.Oficina;
import modelo.Producto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CrearControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkDrivers;
    @FXML private Hyperlink lnkCrear;
    
    @FXML public ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtareaDescripcion;
    
    @FXML private Label lblEntidades;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    @FXML private TableView<DriverObjetoLinea> tabDetalleDriver;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoBanca;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreBanca;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoOficina;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreOficina;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreProducto;
    @FXML private TableColumn<DriverObjetoLinea, Double> tabcolPorcentajeDestino;   
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnCrear;
    @FXML private JFXButton btnCancelar;
        
    FXMLLoader fxmlLoader;
    public MenuControlador menuControlador;
    DriverDAO driverDAO;
    BancaDAO bancaDAO;
    OficinaDAO oficinaDAO;
    ProductoDAO productoDAO;
    int periodoSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_CREAR.getControlador());
    String titulo1;
    
    public CrearControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        bancaDAO = new BancaDAO();
        oficinaDAO = new OficinaDAO();
        productoDAO = new ProductoDAO();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Objetos de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo1 = "Objetos de Beneficio";
            lblTitulo.setText("Drivers - " + titulo1);
            lnkDrivers.setText("Drivers - " + titulo1);
            lblEntidades.setText(titulo1 + " a distribuir");
        }
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
        // formato tabla 2
        tabDetalleDriver.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoBanca.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreBanca.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolCodigoOficina.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreOficina.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolCodigoProducto.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreProducto.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolPorcentajeDestino.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        
        tabcolCodigoBanca.setCellValueFactory(cellData -> cellData.getValue().getBanca().codigoProperty());
        tabcolNombreBanca.setCellValueFactory(cellData -> cellData.getValue().getBanca().nombreProperty());
        tabcolCodigoOficina.setCellValueFactory(cellData -> cellData.getValue().getOficina().codigoProperty());
        tabcolNombreOficina.setCellValueFactory(cellData -> cellData.getValue().getOficina().nombreProperty());
        tabcolCodigoProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolNombreProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }

    @FXML void lnkDriversAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }

    @FXML void lnkCrearAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_CREAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir detalle del driver");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<DriverObjetoLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if (!lista.isEmpty()) tabDetalleDriver.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
        }
    }
    
    private List<DriverObjetoLinea> leerArchivo(String rutaArchivo) {
        List<Banca> listaBancas = bancaDAO.listar(periodoSeleccionado);
        List<Oficina> listaOficinas = oficinaDAO.listar(periodoSeleccionado);
        List<Producto> listaProductos = productoDAO.listar(periodoSeleccionado);
        List<DriverObjetoLinea> lista = new ArrayList();
        double porAcumulado = 0;
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
            List<String> listaCabecera = new ArrayList(Arrays.asList("CODIGO OFICINA","NOMBRE OFICINA","CODIGO BANCA","NOMBRE BANCA","CODIGO PRODUCTO","NOMBRE PRODUCTO","PORCENTAJE"));
            int numFilaCabecera = 1;
            boolean archivoEstaBien = true;
            String mensaje = "Detalle cargado correctamente.";
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
                        tabDetalleDriver.getItems().clear();
                        txtRuta.setText("");
                        archivoEstaBien = false;
                    }
                    continue;
                }
                
                // Codigo Oficina
                celda = celdas.next();
                celda.setCellType(CellType.STRING);
                String codigoOficina = celda.getStringCellValue();
                // Nombre Oficina
                celda = celdas.next();
                // Codigo Banca
                celda = celdas.next();
                celda.setCellType(CellType.STRING);
                String codigoBanca = celda.getStringCellValue();
                // Nombre Banca
                celda = celdas.next();
                // Codigo Producto
                celda = celdas.next();
                celda.setCellType(CellType.STRING);
                String codigoProducto = celda.getStringCellValue();
                // Nombre Producto
                celda = celdas.next();
                // Porcentaje
                celda = celdas.next();
                celda.setCellType(CellType.NUMERIC);
                double porcentaje = celda.getNumericCellValue();
                
                porAcumulado += porcentaje;
                
                // busco la banca
                Banca banca = listaBancas.stream().filter(item -> codigoBanca.equals(item.getCodigo())).findAny().orElse(null);
                if (banca==null) {
                    archivoEstaBien = false;
                    mensaje = "No se encontró a la Banca con código " + codigoBanca + ".";
                    break;
                }
                // busco la oficina
                Oficina oficina = listaOficinas.stream().filter(item -> codigoOficina.equals(item.getCodigo())).findAny().orElse(null);
                if (oficina==null) {
                    archivoEstaBien = false;
                    mensaje = "No se encontró a la Oficina con código " + codigoOficina + ".";
                    break;
                }
                // busco el producto
                Producto producto = listaProductos.stream().filter(item -> codigoProducto.equals(item.getCodigo())).findAny().orElse(null);
                if (producto==null) {
                    archivoEstaBien = false;
                    mensaje = "No se encontró al Producto con código " + codigoProducto + ".";
                    break;
                }                
                DriverObjetoLinea linea = new DriverObjetoLinea(banca, oficina, producto, porcentaje, null, null);
                lista.add(linea);
            }
            //cerramos el libro
            f.close();
            libro.close();
            //verificamos el porcentaje
            if (Math.abs(porAcumulado-100) > 0.00001) {
                archivoEstaBien = false;
                mensaje = "Los porcentajes no suman 100%.";
            }
            //mostramos el mensaje de error
            if (!archivoEstaBien) {
                menuControlador.navegador.mensajeInformativo("Cargar archivo Excel", mensaje);
                tabDetalleDriver.getItems().clear();
                txtRuta.setText("");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.INFO,ex.getMessage());
        }
        return lista;
    }

    @FXML void btnCrearAction(ActionEvent event) {
        try {            
            // datos para crear un driver
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            String descripcion = txtareaDescripcion.getText();
            List<DriverObjetoLinea> lista = tabDetalleDriver.getItems();
            Date fecha = new Date();
            // creamos el driver
            DriverObjeto driver = new DriverObjeto(codigo, nombre, descripcion, null, lista, fecha, fecha);
            
            // inicio mensaje informativo
            ConexionBD.crearStatement();
            ConexionBD.tamanhoBatchMax = 1000;
            if (driverDAO.insertarDriverObjeto(driver, periodoSeleccionado, menuControlador.repartoTipo) != 1) {
                menuControlador.navegador.mensajeInformativo("Crear driver", "No se pudo crear el driver.");
            } else {
                menuControlador.navegador.mensajeInformativo("Crear driver", "Driver creado correctamente.");
                menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
            }
            ConexionBD.ejecutarBatch();
            ConexionBD.cerrarStatement();
            // fin            
        } catch(Exception e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }    
}
