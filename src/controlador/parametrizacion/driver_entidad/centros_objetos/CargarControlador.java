package controlador.parametrizacion.driver_entidad.centros_objetos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.CentroDAO;
import dao.CentroDriverDAO;
import dao.DetalleGastoDAO;
import dao.DriverDAO;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.CentroDriver;
import modelo.DetalleGasto;
import modelo.Tipo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkAsignaciones;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    @FXML private JFXButton btnDescargarLog;

    @FXML private TableView<CentroDriver> tabListar;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolGrupoGasto;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreDriver;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnSubir;    
    
    // Variables de la aplicacion
    public MenuControlador menuControlador;
    CentroDAO centroDAO;
    DriverDAO driverDAO;
    CentroDriverDAO centroDriverDAO;
    DetalleGastoDAO detalleGastoDAO;
    
    List<CentroDriver> listaCargar = new ArrayList();
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_CARGAR.getControlador());
    String logName;
    String titulo;
    Boolean findError;
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        driverDAO = new DriverDAO();
        detalleGastoDAO = new DetalleGastoDAO();
        centroDriverDAO = new CentroDriverDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
        this.titulo = "Asignar Driver a CECO Bolsa";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla dimensiones
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoCentro.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreCentro.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        tabcolGrupoGasto.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolCodigoDriver.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreDriver.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolGrupoGasto.setCellValueFactory(cellData -> cellData.getValue().getGrupoGasto().nombreProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().codigoDriverProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().nombreDriverProperty());
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        btnDescargarLog.setVisible(false);

    }
    
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    @FXML void lnkParametrizacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void lnkAsignacionesAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR);
    }
    
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar " + titulo);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            cmbMes.setDisable(true);
            spAnho.setDisable(true);
            List<CentroDriver> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<CentroDriver> leerArchivo(String rutaArchivo) {
        TipoDAO tipoDAO = new TipoDAO();
        List<CentroDriver> lista = new ArrayList();
        List<CentroDriver> lstEntidades = centroDAO.listarCentrosObjetosConDriver(periodoSeleccionado,"-",menuControlador.repartoTipo,-1);
        List<String> lstDrivers = driverDAO.listarCodigosDriverPeriodo(periodoSeleccionado,menuControlador.repartoTipo,"OBCO");
        List<Tipo> listaGrupoGasto = tipoDAO.listarGrupoGastos();
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
            if (!menuControlador.navegador.validarFilaNormal(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO CUENTA","NOMBRE CUENTA","CODIGO PARTIDA","NOMBRE PARTIDA","CODIGO CENTRO","NOMBRE CENTRO","CODIGO DRIVER","NOMBRE DRIVER")))) {
                menuControlador.navegador.mensajeError(titulo, menuControlador.MENSAJE_UPLOAD_HEADER);
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoCentro = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreCentro = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String grupoGasto = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoDriver = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombreDriver = celda.getStringCellValue();

                if (periodoSeleccionado != periodo) {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_ERROR_PERIODO);
                    lista.clear();
                    txtRuta.setText("");
//                    break;
                    return null;
                }
//                Validar la existencia de la llave Cuenta-Partida-Centro
                CentroDriver entidad = lstEntidades.stream().filter(item -> codigoCentro.equals(item.getCodigoCentro()) && grupoGasto.equals(item.getGrupoGasto())).findAny().orElse(null);
//                Validar la existencia del Driver en periodo a Cargar
                String driver = lstDrivers.stream().filter(item -> codigoDriver.equals(item)).findAny().orElse(null);
                Tipo tipoGrupoGasto = listaGrupoGasto.stream().filter(item -> grupoGasto.equals(item.getCodigo())).findAny().orElse(null);
                CentroDriver linea = new CentroDriver(periodo,codigoCentro,nombreCentro,tipoGrupoGasto,codigoDriver,nombreDriver, true);
                if (entidad != null  && driver!=null) {
                    listaCargar.add(linea);
                } else {
                    linea.setFlagCargar(false);
                }
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
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR);
    }
      
    @FXML void btnSubirAction(ActionEvent event) {
        findError = false;
        if(tabListar.getItems().isEmpty()){
            menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_EMPTY);
        }else {
            if(listaCargar.isEmpty()){
                menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_UPLOAD_ITEM_DONTEXIST);
            }else{
                centroDriverDAO.insertarListaAsignacionesDriverBolsa(listaCargar,periodoSeleccionado,menuControlador.repartoTipo);
                crearReporteLOG();
                if(findError == true){
                    menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_UPLOAD_SUCCESS_ERROR);
                }else {
                    menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_UPLOAD_SUCCESS);
                }
                btnDescargarLog.setVisible(true);
            }
        }
    }
    
    void crearReporteLOG(){
        logName = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + "CARGAR_CENTROOBJETOS_DRIVER.log";
        menuControlador.Log.crearArchivo(logName);
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("INICIO DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        tabListar.getItems().forEach((CentroDriver item)->{
            if(item.getFlagCargar()){
                menuControlador.Log.agregarLineaArchivo("Se agregó item "+ item.getCodigoDriver()+ " a ("+ item.getCodigoCuenta()+ "," +item.getCodigoPartida()+ "," +item.getCodigoCentro()+")"+ " en "+ titulo +" correctamente.");
                menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), item.getCodigoDriver()+ " a ("+ item.getCodigoCuenta()+ "," +item.getCodigoPartida()+ "," +item.getCodigoCentro()+")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_CARGAR.getDireccion());
            }
            else{
                menuControlador.Log.agregarLineaArchivo("No se agregó item "+ item.getCodigoDriver()+ " a ("+ item.getCodigoCuenta()+ "," +item.getCodigoPartida()+ "," +item.getCodigoCentro()+")"+ " en "+titulo+", debido a que no existe algún valor en su respectivo catálogo" );
                findError = true;
            }
        });
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
        menuControlador.Log.agregarLineaArchivoTiempo("FIN DEL PROCESO DE CARGA");
        menuControlador.Log.agregarSeparadorArchivo('=', 100);
    }  
}
