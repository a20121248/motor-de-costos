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
    @FXML private TableColumn<Centro, String> tabcolEsBolsa;
    @FXML private TableColumn<Centro, String> tabcolTipoGasto;
    @FXML private TableColumn<Centro, String> tabcolNIIF17Atribuible;
    @FXML private TableColumn<Centro, String> tabcolNIIF17Tipo;
    @FXML private TableColumn<Centro, String> tabcolNIIF17Clase;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;
    String titulo1,titulo2;    
    
    // Variables de la aplicacion
    CentroDAO centroDAO;
    LogServicio logServicio;
    String logName;
    String logDetails;
    public MenuControlador menuControlador;
    List<String> lstCodigos;
    List<Centro> listaCargar = new ArrayList() ;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_MAESTRO_CARGAR.getControlador());
    String titulo;
    Boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        lstCodigos = centroDAO.listarCodigos();
        this.titulo = "Centros de Costos";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {    
        // tabla centros
        tabListar.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        tabcolCodigo.setMaxWidth( 1f * Integer.MAX_VALUE * 10);
        tabcolNombre.setMaxWidth( 1f * Integer.MAX_VALUE * 20);
        tabcolCodigoGrupo.setMaxWidth( 1f * Integer.MAX_VALUE * 15);
        tabcolNivel.setMaxWidth( 1f * Integer.MAX_VALUE * 5);
        tabcolCodigoCentroPadre.setMaxWidth( 1f * Integer.MAX_VALUE * 5);
        tabcolEsBolsa.setMaxWidth( 1f * Integer.MAX_VALUE * 5);
        tabcolTipoGasto.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNIIF17Atribuible.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNIIF17Tipo.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNIIF17Clase.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        // tabla formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolCodigoGrupo.setCellValueFactory(cellData -> cellData.getValue().getTipo().codigoProperty());
        tabcolNombreGrupo.setCellValueFactory(cellData -> cellData.getValue().getTipo().nombreProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        //tabcolCentroPadre.setCellValueFactory(cellData -> cellData.getValue().);
        tabcolEsBolsa.setCellValueFactory(cellData -> cellData.getValue().esBolsaProperty());
        tabcolTipoGasto.setCellValueFactory(cellData -> cellData.getValue().tipoGastoProperty());
        tabcolNIIF17Atribuible.setCellValueFactory(cellData -> cellData.getValue().NIIF17atribuibleProperty());
        tabcolNIIF17Tipo.setCellValueFactory(cellData -> cellData.getValue().NIIF17TipoProperty());
        tabcolNIIF17Clase.setCellValueFactory(cellData -> cellData.getValue().NIIF17ClaseProperty());
        btnDescargarLog.setVisible(false);
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
        List<String> listaCodigos = centroDAO.listarCodigos();
        List<Tipo> listaTipoCentro = menuControlador.lstCentroTipos;
        listaCargar = new ArrayList();
        logDetails = "";
        List<String> grupoTipoBolsa = new ArrayList();
        grupoTipoBolsa.add("BOLSA");
        grupoTipoBolsa.add("OFICINA");
        List<String> grupoTipoFicticio = new ArrayList();
        grupoTipoFicticio.add("FICTICIO");
        grupoTipoFicticio.add("PROYECTO");
        try (FileInputStream f = new FileInputStream(rutaArchivo);
             XSSFWorkbook libro = new XSSFWorkbook(f)) {
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE","CODIGO GRUPO", "NOMBRE GRUPO", "NIVEL" ,"ES BOLSA","TIPO GASTO","NIIF17 ATRIBUIBLE","NIIF17 TIPO","NIIF17 CLASE")))) {
                menuControlador.mensaje.upload_header_error(titulo);
                return null;
            }

            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoGrupo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreGrupo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int nivel = (int)celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String esBolsa = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int tipoGasto = (int)celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String niif17Atribuible = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String niif17Tipo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String niif17Clase = celda.getStringCellValue();
                
                niif17Atribuible = centroDAO.convertirAbreviaturaPalabra(niif17Atribuible);
                niif17Tipo = centroDAO.convertirAbreviaturaPalabra(niif17Tipo);
                niif17Clase = centroDAO.convertirAbreviaturaPalabra(niif17Clase);
                String strTipoGasto;
                if(tipoGasto == 1) strTipoGasto ="DIRECTO";
                else strTipoGasto ="INDIRECTO";
                Centro linea = new Centro(codigo,nombre,nivel,null,0,new Tipo(codigoGrupo,nombreGrupo),esBolsa, strTipoGasto,niif17Atribuible,niif17Tipo, niif17Clase, null,null,true);
                String cuenta = listaCodigos.stream().filter(item ->codigo.equals(item)).findAny().orElse(null);
                Tipo tipoCentro = listaTipoCentro.stream().filter(item ->codigoGrupo.equals(item.getCodigo())).findAny().orElse(null);
                boolean ptrCodigo = menuControlador.patronCodigoCentro(codigo);
                String relación = "";
                boolean relacionGrupoNivel = false;
                if(tipoCentro != null){
                    if(grupoTipoBolsa.contains(codigoGrupo)){
                        if(nivel == 0){
                            if(esBolsa.equals("SI"))relacionGrupoNivel = true;
                            else {
                                relacionGrupoNivel = false;
                                relación += String.format("- El grupo %s es BOLSA = SI.\r\n",codigoGrupo);
                            }
                        } else {
                            relacionGrupoNivel = false;
                            relación += String.format("- El grupo %s es NIVEL = 0.\r\n",codigoGrupo);
                        }
                    } else {
                        if(grupoTipoFicticio.contains(codigoGrupo)){
                            if(nivel == 99){
                                if(esBolsa.equals("NO"))relacionGrupoNivel = true;
                                else {
                                    relacionGrupoNivel = false;
                                    relación += String.format("- El grupo %s es BOLSA = NO.\r\n",codigoGrupo);
                                }
                            } else {
                                relacionGrupoNivel = false;
                                relación += String.format("- El grupo %s es NIVEL = 99.\r\n",codigoGrupo);
                            }
                        } else {
                            if(nivel>0 && nivel < 99){
                                if(esBolsa.equals("NO"))relacionGrupoNivel = true;
                                else {
                                    relacionGrupoNivel = false;
                                    relación += String.format("- El grupo %s es BOLSA = NO.\r\n",codigoGrupo);
                                }
                            } else {
                                relacionGrupoNivel = false;
                                relación += String.format("- El grupo %s es NIVEL > 0 Y NIVEL < 99.\r\n",codigoGrupo);
                            }
                        }
                    }
                } else {
                    relación += String.format("- El grupo %s asignado no está listado.\r\n",codigoGrupo);
                }
                
                
                if( cuenta == null && ptrCodigo && tipoCentro!= null &&relacionGrupoNivel){
                    listaCargar.add(linea);                    
                    listaCodigos.removeIf(x->x.equals(linea.getCodigo()));
                    logDetails +=String.format("Se agregó item %s a %s.\r\n",linea.getCodigo(),titulo);
                }else {
                    logDetails +=String.format("No se agregó item %s a %s. Debido a que existen los siguientes errores:\r\n", linea.getCodigo(),titulo);
                    if(cuenta!= null){
                        logDetails +=String.format("- Ya existe en Catálogo.\r\n");
                    }else {
                        if(!ptrCodigo) logDetails +=String.format("- El código no cumple con el patrón establecido.\r\n");
                        logDetails += relación;
                    }
                    linea.setFlagCargar(false);
//                    listaError.add(linea);
                }
                if(niif17Atribuible == null) logDetails +=String.format("* Considerar que no se ha asignado correctamente el atributo (NIIF).\r\n");
                if(niif17Tipo == null) logDetails +=String.format("* Considerar que no se ha asignado correctamente el Tipo Gasto (NIIF).\r\n");
                if(niif17Clase == null) logDetails +=String.format("* Considerar que no se ha asignado correctamente el Clase Gasto (NIIF) .\r\n");
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
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.mensaje.upload_empty();
        }else{
            if(listaCargar.isEmpty()){
                menuControlador.mensaje.upload_allCharged_now(titulo);
            }else{
                centroDAO.insertarListaObjeto(listaCargar, menuControlador.repartoTipo);
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
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_CENTROS_CATALOGO.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), item.getCodigo(), Navegador.RUTAS_CENTROS_MAESTRO_CARGAR.getDireccion());
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
