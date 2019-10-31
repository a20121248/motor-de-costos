package controlador.parametrizacion.objetos.grupos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoGrupoDAO;
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
import modelo.Grupo;
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
    @FXML private Hyperlink lnkGrupos;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<Grupo> tabListar;
    @FXML private TableColumn<Grupo, String> tabcolCodigo;
    @FXML private TableColumn<Grupo, String> tabcolNombre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivel;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;
    
    // Variables de la aplicacion
    ObjetoGrupoDAO objetoGrupoDAO;
    LogServicio logServicio;
    String logName;
    String logDetails;
    public MenuControlador menuControlador;
    List<String> lstCodigos;
    List<Grupo> listaCargar = new ArrayList();
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_GRUPOS_CARGAR.getControlador());
    String titulo;
    boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoGrupoDAO = new ObjetoGrupoDAO(menuControlador.objetoTipo);
        lstCodigos = objetoGrupoDAO.listarCodigos();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "PRO":
                lblTitulo.setText("Cargar Grupos de Productos");
                lnkObjetos.setText("Productos");
                this.titulo = "Productos";
                break;
            case "SCA":
                lblTitulo.setText("Cargar Grupos de Subcanales");
                lnkObjetos.setText("Subcanales");
                this.titulo = "Subcanales";
                break;
            default:
                break;
        }
        // tabla: dimensiones
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 70);
        tabcolNivel.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        // tabla: formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        
        // Ocultar el botón de descarga de LOG
        btnDescargarLog.setVisible(false);
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
    
    @FXML void lnkJerarquiaAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA);
    }
    @FXML void lnkGruposAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        btnDescargarLog.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir catálogo de Grupos");
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
        List<Grupo> lstPrevisualizar = new ArrayList();
        listaCargar = new ArrayList();
        logDetails = "";
        try (FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f)) {
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE","NIVEL")))) {
                menuControlador.mensaje.upload_header_error("Grupos");
                return null;
            }
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                // leemos una fila completa                
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int nivel = (int)celda.getNumericCellValue();
                
                Grupo linea = new Grupo(codigo,nombre,null,0,null,null,null);
                linea.setNivel(nivel);
                linea.setFlagCargar(true);
                String grupo = lstCodigos.stream().filter(item ->codigo.equals(item)).findAny().orElse(null);
                
                if(grupo == null){
                    listaCargar.add(linea);
                    linea.setFlagCargar(true);
                    lstCodigos.removeIf(x->x.equals(linea.getCodigo()));
                    logDetails +=String.format("Se agregó item %s a %s.\r\n",linea.getCodigo(),titulo);
                }else {
                    logDetails +=String.format("No se agregó item %s a %s. Debido a que existen los siguientes errores:\r\n", linea.getCodigo(),titulo);
                    if(grupo!= null){
                        logDetails +=String.format("- Ya existe en Catálogo.\r\n");
                    }
                    linea.setFlagCargar(false);
                }
                lstPrevisualizar.add(linea);
            }
            //cerramos el libro
            f.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        return lstPrevisualizar;
    }
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
    
    @FXML void btnSubirAction(ActionEvent event) {
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.mensaje.upload_empty();
        }else{
            if(listaCargar.isEmpty()){
                menuControlador.mensaje.upload_allCharged_now("Grupos");
            }else{
                objetoGrupoDAO.insertarListaObjeto(listaCargar);
                crearReporteLOG();
                if(findError == true){
                    menuControlador.mensaje.upload_success_with_error("Grupos");
                }else {
                    menuControlador.mensaje.upload_success();
                }
                btnSubir.setDisable(true);
                btnDescargarLog.setVisible(true);
            }        
        }
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_GRUPOS.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), item.getCodigo(), Navegador.RUTAS_OBJETOS_GRUPOS_CARGAR.getDireccion().replace("/Objetos/", "/"+titulo+"/"));
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
