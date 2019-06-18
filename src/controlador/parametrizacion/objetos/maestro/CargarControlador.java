package controlador.parametrizacion.objetos.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.Banca;
import modelo.EntidadDistribucion;
import modelo.Oficina;
import modelo.Producto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import servicios.LogServicio;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkCargar;
        
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<EntidadDistribucion> tabListar;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolCodigo;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;
    
    // Variables de la aplicacion
    ObjetoDAO objetoDAO;
    LogServicio logServicio;
    String logName;
    String objetoNombre1,objetoNombre2;
    public MenuControlador menuControlador;
    List<String> lstCodigos;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_MAESTRO_CARGAR.getControlador());
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
        lstCodigos = objetoDAO.listarCodigos();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Cargar Oficinas");
                lnkObjetos.setText("Oficinas");
                objetoNombre1 = "Oficina";
                objetoNombre2 = "la Oficina";
                break;
            case "BAN":
                lblTitulo.setText("Cargar Bancas");
                lnkObjetos.setText("Bancas");
                objetoNombre1 = "Banca";
                objetoNombre2 = "la Banca";
                break;
            case "PRO":
                lblTitulo.setText("Cargar Productos");
                lnkObjetos.setText("Productos");
                objetoNombre1 = "Producto";
                objetoNombre2 = "el Producto";
                break;
            case "SCA":
                lblTitulo.setText("Cargar Subcanales");
                lnkObjetos.setText("Subcanales");
                objetoNombre1 = "Subcanal";
                objetoNombre2 = "el Subcanal";
                break;
            default:
                break;
        }
        // tabla dimensiones
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 80);
        // tabla formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
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
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de " + objetoNombre1 + "s");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnDescargarLog.setVisible(false);
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<EntidadDistribucion> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros: " + lista.size());
            } else {
                txtRuta.setText("");
            }
        }
    }
    
    private List<EntidadDistribucion> leerArchivo(String rutaArchivo) {
        List<EntidadDistribucion> lstPrevisualizar = new ArrayList();
        try (FileInputStream f = new FileInputStream(rutaArchivo);
             XSSFWorkbook libro = new XSSFWorkbook(f)) {
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE")))) {
                String msj = "La cabecera no es la correcta.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar " + objetoNombre1 + "s", msj);
                return null;
            }
            
            logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_" + objetoNombre1.toUpperCase() + "S.log";
            logServicio = new LogServicio(logName);
            logServicio.crearArchivo();
            logServicio.agregarSeparadorArchivo('=', 100);
            logServicio.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
            logServicio.agregarSeparadorArchivo('=', 100);
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                // leemos una fila completa                
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                
                switch (menuControlador.objetoTipo) {
                    case "OFI":
                        lstPrevisualizar.add(new Oficina(codigo, nombre, null, 0, null, null));
                        break;
                    case "BAN":
                        lstPrevisualizar.add(new Banca(codigo, nombre, null, 0, null, null));
                        break;
                    case "PRO":
                        lstPrevisualizar.add(new Producto(codigo, nombre, null, 0, null, null));
                        break;
                }
            }
            //cerramos el libro
            f.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        return lstPrevisualizar;
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        String rutaOrigen = "." + File.separator + "logs" + File.separator + logName;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar LOG");
        fileChooser.setInitialFileName(logName);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivo LOG", "*.log"));
        File archivoSeleccionado = fileChooser.showSaveDialog(btnDescargarLog.getScene().getWindow());
        if (archivoSeleccionado != null) {
            Path origen = Paths.get(rutaOrigen);
            Path destino = Paths.get(archivoSeleccionado.getAbsolutePath());
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            menuControlador.navegador.mensajeInformativo("Guardar LOG","Descarga completa.");
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_MAESTRO_LISTAR);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        List<EntidadDistribucion> lista = tabListar.getItems();
        if(lista.isEmpty())
        {
            menuControlador.navegador.mensajeInformativo("Subir Información", "No hay información.");
        }
        else{
            for (EntidadDistribucion item: lista) {
                if (lstCodigos.contains(item.getCodigo())) {
                    logServicio.agregarLineaArchivo(String.format("No se pudo crear %s porque el código %s ya existe.",objetoNombre2,item.getCodigo()));
                    item.setFlagCargar(false);
                } else {
                    logServicio.agregarLineaArchivo(String.format("Se creó %s con código %s.",objetoNombre2,item.getCodigo()));
                    item.setFlagCargar(true);
                }
            }
            objetoDAO.insertarListaObjeto(lista);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", objetoNombre1 + "s subidos correctamente.");
            btnDescargarLog.setVisible(true);
            LOGGER.log(Level.INFO,String.format("El usuario %s subió el catálogo de %ss.",menuControlador.usuario.getUsername(),objetoNombre1));
        }
        
    }
}
