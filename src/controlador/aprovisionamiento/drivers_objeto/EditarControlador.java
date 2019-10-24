package controlador.aprovisionamiento.drivers_objeto;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.BancaDAO;
import dao.DriverDAO;
import dao.OficinaDAO;
import dao.ProductoDAO;
import dao.SubcanalDAO;
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
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.Producto;
import modelo.Subcanal;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EditarControlador implements Initializable {
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
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreProducto;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolCodigoSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, String> tabcolNombreSubcanal;
    @FXML private TableColumn<DriverObjetoLinea, Double> tabcolPorcentajeDestino;   
    @FXML private Label lblNumeroRegistros;
    @FXML private Label lblSuma;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    DriverObjeto driver;
    DriverDAO driverDAO;
    BancaDAO bancaDAO;
    OficinaDAO oficinaDAO;
    ProductoDAO productoDAO;
    SubcanalDAO subcanalDAO;
    int numeroRegistros;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    double porcentajeTotal;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVERS_OBJETO_EDITAR.getControlador());
    String titulo, titulo2;
    
    public EditarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driver = (DriverObjeto) menuControlador.objeto;
        driverDAO = new DriverDAO();
        bancaDAO = new BancaDAO();
        oficinaDAO = new OficinaDAO();
        productoDAO = new ProductoDAO();
        subcanalDAO = new SubcanalDAO();
        periodoSeleccionado = menuControlador.periodoSeleccionado;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo = " Driver - Objetos de Costos";
        titulo2 = "Objeto de Costos";
        if (menuControlador.repartoTipo == 2) { 
            titulo = "Driver - Objetos de Beneficio";
            titulo2 = "Objeto de Beneficio";
            lblTitulo.setText(titulo);
            lnkDrivers.setText(titulo);
            lblEntidades.setText(titulo + " a distribuir");
        }
        
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
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
        // formato tabla 2
        tabDetalleDriver.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoProducto.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreProducto.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolCodigoSubcanal.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreSubcanal.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolPorcentajeDestino.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolCodigoProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().codigoProperty());
        tabcolNombreProducto.setCellValueFactory(cellData -> cellData.getValue().getProducto().nombreProperty());
        tabcolCodigoSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().codigoProperty());
        tabcolNombreSubcanal.setCellValueFactory(cellData -> cellData.getValue().getSubcanal().nombreProperty());
        tabcolPorcentajeDestino.setCellValueFactory(cellData -> cellData.getValue().porcentajeProperty().asObject());
        tabcolPorcentajeDestino.setCellFactory(column -> {
                return new TableCell<DriverObjetoLinea, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%,.4f", item));
                    }
                }
            };
        });
        // tabla 2
        tabDetalleDriver.setEditable(true);
        DoubleStringConverter converter = new DoubleStringConverter();
        tabcolPorcentajeDestino.setCellFactory(TextFieldTableCell.<DriverObjetoLinea, Double>forTableColumn(converter));
        tabcolPorcentajeDestino.setOnEditCommit(data -> {
            double oldValue = data.getOldValue();
            double newValue = data.getNewValue();
            data.getRowValue().setPorcentaje(newValue);
            porcentajeTotal -= oldValue;
            porcentajeTotal += newValue;
            lblSuma.setText(String.format("Suma: %.4f%%",porcentajeTotal));
        });
        
        // completar datos del driver seleccionado
        List<DriverObjetoLinea> lstDriverLinea = driverDAO.obtenerDriverObjetoLinea(periodoSeleccionado, driver.getCodigo(),menuControlador.repartoTipo);
        porcentajeTotal = lstDriverLinea.stream().mapToDouble(o -> o.getPorcentaje()).sum();
        driver.setListaDriverObjetoLinea(lstDriverLinea);
        numeroRegistros = driver.getListaDriverObjetoLinea().size();
        lblSuma.setText(String.format("Suma: %.4f%%",porcentajeTotal));
        
        txtCodigo.setText(driver.getCodigo());
        txtNombre.setText(driver.getNombre());
        txtareaDescripcion.setText(driver.getDescripcion());
        tabDetalleDriver.getItems().setAll(driver.getListaDriverObjetoLinea());
        lblNumeroRegistros.setText("Número de registros: " + numeroRegistros);
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
    
    @FXML void lnkEditarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_EDITAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir detalle del driver");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<DriverObjetoLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            tabDetalleDriver.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
        }
    }
    
    private List<DriverObjetoLinea> leerArchivo(String rutaArchivo) {
        List<Producto> listaProductos = productoDAO.listar(periodoSeleccionado,menuControlador.repartoTipo);
        List<Subcanal> listaSubcanal = subcanalDAO.listar(periodoSeleccionado,menuControlador.repartoTipo);
        List<DriverObjetoLinea> lista = new ArrayList();
        double porAcumulado = 0;
        boolean archivoEstaBien = true;
        String mensaje = null;
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
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO PRODUCTO","NOMBRE PRODUCTO","CODIGO SUBCANAL","NOMBRE SUBCANAL","PORCENTAJE")))) {
                menuControlador.navegador.mensajeError(titulo,menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();

                // Codigo Producto
                celda = celdas.next();
                celda.setCellType(CellType.STRING);
                String codigoProducto = celda.getStringCellValue();
                // Nombre Producto
                celda = celdas.next();
                // Codigo Subcanal
                celda = celdas.next();
                celda.setCellType(CellType.STRING);
                String codigoBanca = celda.getStringCellValue();
                // Nombre Subcanal
                celda = celdas.next();
                // Porcentaje
                celda = celdas.next();
                celda.setCellType(CellType.NUMERIC);
                double porcentaje = celda.getNumericCellValue();
                
                porAcumulado += porcentaje;
                
                // busco el producto
                Producto producto = listaProductos.stream().filter(item -> codigoProducto.equals(item.getCodigo())).findAny().orElse(null);
                if (producto==null) {
                    mensaje = "No se encontró al Producto con código " + codigoProducto + ".";
                    lista.clear();
                    break;
                }
                Subcanal subcanal = listaSubcanal.stream().filter(item -> codigoBanca.equals(item.getCodigo())).findAny().orElse(null);
                if (subcanal==null) {
                    mensaje = "No se encontró al Subcanal con código " + codigoProducto + ".";
                    lista.clear();
                    break;
                }
                DriverObjetoLinea linea = new DriverObjetoLinea( producto, subcanal, porcentaje, null, null);
                lista.add(linea);
            }
            //cerramos el libro
            f.close();
            libro.close();
            //verificamos el porcentaje
            if (porAcumulado > 100) {
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
            System.out.println(ex.getMessage());
        }
        return lista;
    }

    @FXML void btnGuardarAction(ActionEvent event) {
        if (Math.abs(porcentajeTotal - 100) > 0.00001) {
            menuControlador.navegador.mensajeError("Guardar Driver - " + titulo2, "Los porcentajes no suman 100%.\nNo se puede guardar el driver.");
            return;
        }
              
        // datos
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        String descripcion = txtareaDescripcion.getText();
        List<DriverObjetoLinea> lista = tabDetalleDriver.getItems();
        Date fecha = new Date();
        DriverObjeto driver = new DriverObjeto(codigo, nombre, descripcion, null, lista, fecha, fecha);

        // inicio mensaje informativo
        if (driverDAO.actualizarDriverObjeto(driver, periodoSeleccionado, menuControlador.repartoTipo) != 1) {
            menuControlador.navegador.mensajeInformativo("Guardar Driver - " + titulo2, "No se pudo guardar el driver.");
        } else {
            menuControlador.navegador.mensajeInformativo("Guardar Driver - " + titulo2, "Driver guardado correctamente.");
            menuControlador.Log.editarItem(LOGGER,menuControlador.usuario.getUsername(), driver.getCodigo(), Navegador.RUTAS_DRIVERS_OBJETO_EDITAR.getDireccion());
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
        }            
        // fin
    }
    
    @FXML void btnCancelarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVERS_OBJETO_LISTAR);
    }    
}
