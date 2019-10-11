package controlador.parametrizacion.partidas.maestro;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PartidaDAO;
import dao.TipoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
import modelo.Partida;
import modelo.Tipo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import servicios.LogServicio;

public class CargarControlador implements Initializable {
    // Variables de la vista        
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<Partida> tabListar;
    @FXML private TableColumn<Partida, String> tabcolCodigo;
    @FXML private TableColumn<Partida, String> tabcolNombre;
    @FXML private TableColumn<Partida, String> tabcolGrupoGasto;
    @FXML private TableColumn<Partida, String> tabcolTipoGasto;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    
    // Variables de la aplicacion
    PartidaDAO partidaDAO;
    TipoDAO tipoDAO;
    LogServicio logServicio;
    String logName;
    String logDetails;
    public MenuControlador menuControlador;
    List<String> lstCodigos;
    List<Partida> listaCargar = new ArrayList() ;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PARTIDAS_MAESTRO_CARGAR.getControlador());
    String titulo;
    Boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        partidaDAO = new PartidaDAO();
        tipoDAO = new TipoDAO();
        lstCodigos = partidaDAO.listarCodigos();
        this.titulo = "Partida";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla dimensiones
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        tabcolGrupoGasto.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolTipoGasto.setMaxWidth(1f * Integer.MAX_VALUE * 10);

        // tabla formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolGrupoGasto.setCellValueFactory(cellData -> cellData.getValue().getGrupoGasto().nombreProperty());
        tabcolTipoGasto.setCellValueFactory(cellData -> cellData.getValue().tipoGastoProperty());
        
        btnDescargarLog.setVisible(false);
    }    
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkPartidasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_ASOCIAR_PERIODO);
    }
    
    @FXML void lnkCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de Partidas");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            btnDescargarLog.setVisible(false);
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<Partida> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros: " + lista.size());
            } else {
                txtRuta.setText("");
            }
        }
    }
    
    private List<Partida> leerArchivo(String rutaArchivo) {
        List<Partida> lista = new ArrayList();
        List<String> listaCodigos = partidaDAO.listarCodigos();
        List<Tipo> listaGrupoGastos = tipoDAO.listarGrupoGastos();
        listaCargar = new ArrayList();
        logDetails = "";
        try (FileInputStream f = new FileInputStream(rutaArchivo);
             XSSFWorkbook libro = new XSSFWorkbook(f)) {
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE","GRUPO GASTO", "TIPO GASTO")))) {
                menuControlador.mensaje.upload_header_error(titulo);
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String grupoGasto = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int tipoGasto = (int)celda.getNumericCellValue();
                
                Tipo tipoGrupoGasto = listaGrupoGastos.stream().filter(item ->grupoGasto.equals(item.getCodigo())).findAny().orElse(null);       
                String cuenta = listaCodigos.stream().filter(item ->codigo.equals(item)).findAny().orElse(null);
                String strTipoGasto;
                if(tipoGasto == 1) strTipoGasto ="DIRECTO";
                else strTipoGasto ="INDIRECTO";
                Partida linea;
                if(tipoGrupoGasto == null){
                    Tipo gg = new Tipo("NN","NN");
                    linea = new Partida(codigo,nombre,null,gg,strTipoGasto,0,null,null,true);
                }else {
                    linea = new Partida(codigo,nombre,null,tipoGrupoGasto,strTipoGasto,0,null,null,true);
                }
                
                boolean ptrCodigo = menuControlador.patronCodigoPartida(codigo);
                if(cuenta == null && tipoGrupoGasto != null && ptrCodigo){
                    listaCargar.add(linea);                    
                    listaCodigos.removeIf(x->x.equals(linea.getCodigo()));
                    logDetails +=String.format("Se agregó item %s a %s.\r\n",linea.getCodigo(),titulo);
                }else {
                    logDetails +=String.format("No se agregó item %s a %s. Debido a que existen los siguientes errores:\r\n", linea.getCodigo(),titulo);
                    if(cuenta!= null){
                        logDetails +=String.format("- Ya existe en Catálogo.\r\n");
                    }else {
                        if(!ptrCodigo) logDetails +=String.format("- El código no cumple con el patrón establecido.\r\n");
                        if(tipoGrupoGasto == null) logDetails +=String.format("- El grupo de gasto asignado es no existe o está vacio.\r\n");
                    }
                    linea.setFlagCargar(false);
                }
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
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PARTIDAS_MAESTRO_LISTAR);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.mensaje.upload_empty();
        }else{
            if(listaCargar.isEmpty()){
                menuControlador.mensaje.upload_allCharged_now(titulo);
            }else{
                partidaDAO.insertarListaObjeto(listaCargar, menuControlador.repartoTipo);
                crearReporteLOG();
                if(findError == true){
                    menuControlador.mensaje.upload_success_with_error(titulo);
                }else {
                    menuControlador.mensaje.upload_success();
                }
                btnDescargarLog.setVisible(true);
            }
        }  
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_PARTIDAS_CATALOGO.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), item.getCodigo(), Navegador.RUTAS_PLANES_MAESTRO_CARGAR.getDireccion());
            }
            else{
                findError = true;
            }
        });
        menuControlador.Log.agregarLineaArchivo(logDetails);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
    }
}
