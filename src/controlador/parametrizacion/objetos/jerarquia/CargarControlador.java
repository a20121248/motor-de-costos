package controlador.parametrizacion.objetos.jerarquia;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoDAO;
import dao.ObjetoGrupoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import modelo.Grupo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkObjetos;
    
    @FXML private HBox hbPeriodo;
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<Grupo> tabListar;
    @FXML private TableColumn<Grupo, String> tabcolCodigo;
    @FXML private TableColumn<Grupo, String> tabcolNombre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivel;
    @FXML private TableColumn<Grupo, String> tabcolCodigoPadre;
    @FXML private TableColumn<Grupo, String> tabcolNombrePadre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivelPadre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargarLog;
    @FXML private Button btnSubir;
    
    // Variables de la aplicacion
    ObjetoGrupoDAO objetoGrupoDAO;
    ObjetoDAO objetoDAO;
    public MenuControlador menuControlador;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_JERARQUIA_CARGAR.getControlador());
    List<Grupo> listaCargar  = new ArrayList();
    String titulo;
    String titulo2;
    boolean findError;
    String logName;
    String logDetails;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoGrupoDAO = new ObjetoGrupoDAO(menuControlador.objetoTipo);
        objetoDAO = new ObjetoDAO(menuControlador.objetoTipo);
        // Periodo seleccionado
        if (menuControlador.repartoTipo == 1) {
            if (menuControlador.periodoSeleccionado % 100 == 0)
                ++menuControlador.periodoSeleccionado;
        } else {
            menuControlador.periodoSeleccionado = menuControlador.periodoSeleccionado / 100 * 100;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mes seleccionado
        if (menuControlador.repartoTipo == 1) {
            cmbMes.getItems().addAll(menuControlador.lstMeses);
            cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
            cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                }
            });
        } else {
            hbPeriodo.getChildren().remove(cmbMes);
        }
        
        // Anho seleccionado
        spAnho.getValueFactory().setValue(menuControlador.periodoSeleccionado / 100);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (menuControlador.repartoTipo == 1)
                    menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                else
                    menuControlador.periodoSeleccionado = spAnho.getValue()*100;
            }
        });
        
        switch (menuControlador.objetoTipo) {
            case "PRO":
                lblTitulo.setText("Cargar Jerarquía de Productos");
                lnkObjetos.setText("Productos");
                this.titulo = "Productos";
                this.titulo2 = "Linea";
                break;
            case "SCA":
                lblTitulo.setText("Cargar Jerarquía de Subcanales");
                lnkObjetos.setText("Subcanales");
                this.titulo = "Subcanales";
                this.titulo2 = "Canal";
                break;
            default:
                break;
        }
        
        // Tabla: Formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        tabcolCodigoPadre.setCellValueFactory(cellData -> cellData.getValue().getGrupoPadre().codigoProperty());
        tabcolNombrePadre.setCellValueFactory(cellData -> cellData.getValue().getGrupoPadre().nombreProperty());
        tabcolNivelPadre.setCellValueFactory(cellData -> cellData.getValue().getGrupoPadre().nivelProperty().asObject());
        
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

    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA_CARGAR);
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
            btnDescargarLog.setVisible(false);
            if(menuControlador.repartoTipo == 2){
                spAnho.setDisable(true);
            }
            else{
                cmbMes.setDisable(true);
                spAnho.setDisable(true);
            }
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
    
    @FXML void btnDescargarLogAction(ActionEvent event) throws IOException {
        menuControlador.Log.descargarLog(btnDescargarLog, logName, menuControlador);
    }
    
    private List<Grupo> leerArchivo(String rutaArchivo) {
        List<String> lstCodigo = objetoDAO.listarCodigosPeriodo(menuControlador.periodoSeleccionado,menuControlador.repartoTipo);
        List<Grupo> lista = new ArrayList();
        listaCargar = new ArrayList();
        logDetails = "";
        try {
            FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f);
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;

            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("CODIGO","NOMBRE","NIVEL","CODIGO PADRE","NOMBRE PADRE","NIVEL PADRE")))) {
                menuControlador.mensaje.upload_header_error("Jerarquía");
                return null;
            }
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa                
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                if (codigo.equals("")) break;
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int nivel = (int)celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoPadre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombrePadre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int nivelPadre = (int)celda.getNumericCellValue();

                Grupo itemPadre = new Grupo(codigoPadre, nombrePadre, null, 0, null, null, null);
                itemPadre.setNivel(nivelPadre);

                Grupo itemHijo = new Grupo(codigo, nombre, null, 0, null, null, null);
                itemHijo.setNivel(nivel);
                
                itemHijo.setGrupoPadre(itemPadre);
                List<String> lstCodigoPadre = objetoGrupoDAO.listarCodigoObjetos(nivel);
                String itemObjeto = lstCodigo.stream().filter(item ->codigo.equals(item)).findAny().orElse(null);
                String itemObjetoPadre = lstCodigoPadre.stream().filter(item ->codigoPadre.equals(item)).findAny().orElse(null);
                if(itemObjeto != null && itemObjetoPadre != null){
                    listaCargar.add(itemHijo);
                    itemHijo.setFlagCargar(true);
                    logDetails +=String.format("Se agregó %s %s con %s %s en %s  correctamente.\r\n",titulo, codigo, titulo2, codigoPadre, "Jerarquía");
                }else {
                    logDetails +=String.format("No se agregó %s %s con %s %s al periodo %d de %s. Debido a que existen los siguientes errores:\r\n",titulo, codigo, titulo2, codigoPadre, menuControlador.periodoSeleccionado, "Jerarquía" );
                    if(itemObjeto == null){
                        logDetails +=String.format("- El código de %s no esta asignado en el periodo %d o no existe en su Catálogo.\r\n", titulo, menuControlador.periodoSeleccionado);
                    }
                    if(itemObjetoPadre == null){
                        logDetails +=String.format("- El código de %s no esta asignado en el periodo %d o no existe en su Catálogo.\r\n", titulo2, menuControlador.periodoSeleccionado);
                    }
                    itemHijo.setFlagCargar(false);
                }
                lista.add(itemHijo);
            }
            //cerramos el libro
            f.close();
            libro.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        return lista;
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA);
    }
    
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        findError = false;
        if(tabListar.getItems().isEmpty()) {
            menuControlador.mensaje.upload_empty();
        }else {
            if(listaCargar.isEmpty()) {
                menuControlador.mensaje.upload_allCharged_now("Jerarquía");
            } else {
                objetoGrupoDAO.insertarListaAsignacion(menuControlador.periodoSeleccionado, menuControlador.objetoTipo, listaCargar,menuControlador.repartoTipo);
                crearReporteLOG();
                if(findError == true){
                    menuControlador.mensaje.upload_success_with_error("Jerarquía");
                }else {
                    menuControlador.mensaje.upload_success();
                }
                btnSubir.setDisable(true);
                btnDescargarLog.setVisible(true);
            }
        }
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_JERARQUIA_"+titulo+".log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarItem(LOGGER, menuControlador.usuario.getUsername(), item.getCodigo(), Navegador.RUTAS_OBJETOS_JERARQUIA_CARGAR.getDireccion().replace("/Objetos/", "/"+titulo+"/"));
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
