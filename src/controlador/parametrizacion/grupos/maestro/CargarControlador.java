package controlador.parametrizacion.grupos.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.GrupoDAO;
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
import modelo.CargarObjetoLinea;
import modelo.Grupo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import servicios.LogServicio;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkGrupos;
    @FXML private Hyperlink lnkCatalogo;
    @FXML private Hyperlink lnkCargar;
        
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<Grupo> tabListar;
    @FXML private TableColumn<Grupo, String> tabcolCodigo;
    @FXML private TableColumn<Grupo, String> tabcolNombre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;
    
    // Variables de la aplicacion
    GrupoDAO grupoDAO;
    LogServicio logServicio;
    String logName;
    public MenuControlador menuControlador;
    List<String> lstCodigos;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_GRUPOS_MAESTRO_CARGAR.getControlador());
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        grupoDAO = new GrupoDAO();
        lstCodigos = grupoDAO.listarCodigos();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla dimensiones
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 85);
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
    
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_ASOCIAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de Grupos de Cuentas Contables");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnDescargarLog.setVisible(false);
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<Grupo> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros: " + lista.size());
            } else {
                txtRuta.setText("");
            }
        }
    }
    
    private List<Grupo> leerArchivo(String rutaArchivo) {
        List<Grupo> lista = new ArrayList();
        try (FileInputStream f = new FileInputStream(rutaArchivo);
             XSSFWorkbook libro = new XSSFWorkbook(f)) {
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE")))) {
                String msj = "La cabecera no es la correcta.\nNo se puede cargar el archivo.";
                menuControlador.navegador.mensajeError("Cargar Grupos de Cuentas Contables", msj);
                return null;
            }
            
            logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_GRUPOS_DE_CUENTAS_CONTABLES.log";
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

                Grupo linea = new Grupo(codigo,nombre,null,0,null,null,null);
                lista.add(linea);
            }
            // Cerrar el libro
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_GRUPOS_MAESTRO_LISTAR);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        List<Grupo> lista = tabListar.getItems();
        if(lista.isEmpty())
        {
            menuControlador.navegador.mensajeInformativo("Subir Información", "No hay información.");
        }
        else{
            for (Grupo item: lista) {
                if (lstCodigos.contains(item.getCodigo())) {
                    logServicio.agregarLineaArchivo(String.format("No se pudo crear el Grupo porque el código %s ya existe.",item.getCodigo()));
                    item.setFlagCargar(false);
                } else {
                    logServicio.agregarLineaArchivo(String.format("Se creó el Grupo con código %s.",item.getCodigo()));
                    item.setFlagCargar(true);
                }
            }
            logServicio.agregarSeparadorArchivo('=', 100);
            logServicio.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
            logServicio.agregarSeparadorArchivo('=', 100);
            grupoDAO.insertarListaObjeto(lista, menuControlador.repartoTipo);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Grupos de Cuentas Contables subidos correctamente.");
            btnDescargarLog.setVisible(true);
            LOGGER.log(Level.INFO,String.format("El usuario %s subió el catálogo de Grupos de Cuentas Contables.",menuControlador.usuario.getUsername()));
        }
        
    }
}
