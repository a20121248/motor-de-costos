package controlador.parametrizacion.centros.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
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
import modelo.Centro;
import modelo.Tipo;
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
    @FXML private Hyperlink lnkCentros;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkCargar;
        
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<Centro> tabListar;
    @FXML private TableColumn<Centro, String> tabcolCodigo;
    @FXML private TableColumn<Centro, String> tabcolNombre;
    @FXML private TableColumn<Centro, String> tabcolCodigoGrupo;
    @FXML private TableColumn<Centro, String> tabcolNombreGrupo;
    @FXML private TableColumn<Centro, Integer> tabcolNivel;
    @FXML private TableColumn<Centro, String> tabcolCodigoCentroPadre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;
    String titulo1,titulo2;    
    
    // Variables de la aplicacion
    CentroDAO centroDAO;
    LogServicio logServicio;
    String logName;
    public MenuControlador menuControlador;
    List<String> lstCodigos;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_MAESTRO_CARGAR.getControlador());
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        lstCodigos = centroDAO.listarCodigos();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titulo1 = "Centros de Costos";
        titulo2 = "Centro de Costos";
        if (menuControlador.repartoTipo == 2) {
            titulo1 = "Centros de Beneficio";
            titulo2 = "Centro de Beneficio";
            lblTitulo.setText("Cargar Centros de Beneficio");
            lnkCentros.setText("Cargar Centros de Beneficio");
        }
        // tabla centros
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigo.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth( 1f * Integer.MAX_VALUE * 36);
        tabcolCodigoGrupo.setMaxWidth( 1f * Integer.MAX_VALUE * 12);
        tabcolNombreGrupo.setMaxWidth( 1f * Integer.MAX_VALUE * 12);
        tabcolNivel.setMaxWidth( 1f * Integer.MAX_VALUE * 10);
        tabcolCodigoCentroPadre.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        // tabla formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolCodigoGrupo.setCellValueFactory(cellData -> cellData.getValue().getTipo().codigoProperty());
        tabcolNombreGrupo.setCellValueFactory(cellData -> cellData.getValue().getTipo().nombreProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        //tabcolCentroPadre.setCellValueFactory(cellData -> cellData.getValue().);
    }    
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkCentrosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de " + titulo1);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnDescargarLog.setVisible(false);
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<Centro> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros: " + lista.size());
            } else {
                txtRuta.setText("");
            }
        }
    }
    
    private List<Centro> leerArchivo(String rutaArchivo) {
        List<Centro> lista = new ArrayList();
        try (FileInputStream f = new FileInputStream(rutaArchivo);
             XSSFWorkbook libro = new XSSFWorkbook(f)) {
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE")))) {
                String msj = "La cabecera no es la correcta.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar " + titulo1, msj);
                return null;
            }
            
            logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_CENTROS.log";
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
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoGrupo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreGrupo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int nivel = (int)celda.getNumericCellValue();

                Centro linea = new Centro(codigo,nombre,nivel,null,0,new Tipo(codigoGrupo,nombreGrupo),null,null);
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        List<Centro> lista = tabListar.getItems();
        if(lista.isEmpty())
        {
            menuControlador.navegador.mensajeInformativo("Subir Información", "No hay información.");
        }
        else{
            for (Centro item: lista) {
                if (lstCodigos.contains(item.getCodigo())) {
                    logServicio.agregarLineaArchivo(String.format("No se pudo crear el %s porque el código %s ya existe.",titulo2,item.getCodigo()));
                    item.setFlagCargar(false);
                } else {
                    logServicio.agregarLineaArchivo(String.format("Se creó el %s con código %s.",titulo2,item.getCodigo()));
                    item.setFlagCargar(true);
                }
            }
            logServicio.agregarSeparadorArchivo('=', 100);
            logServicio.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
            logServicio.agregarSeparadorArchivo('=', 100);
            centroDAO.insertarListaObjeto(lista, menuControlador.repartoTipo);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", titulo1 + " subidos correctamente.");
            btnDescargarLog.setVisible(true);
            LOGGER.log(Level.INFO,String.format("El usuario %s subió el catálogo de %s.",menuControlador.usuario.getUsername(),titulo1));
        }
    }
}
