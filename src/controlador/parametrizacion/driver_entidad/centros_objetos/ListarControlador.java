package controlador.parametrizacion.driver_entidad.centros_objetos;


import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarDriverCentroControlador;
import controlador.modals.BuscarDriverObjetoControlador;
import controlador.modals.VerDriverCentroControlador;
import controlador.modals.VerDriverObjetoControlador;
import dao.AsignacionEntidadDriverDAO;
import dao.BancaDAO;
import dao.CentroDAO;
import dao.CentroDriverDAO;
import dao.DriverDAO;
import dao.PlanDeCuentaDAO;
import dao.ProductoDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.CentroDriver;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import modelo.Tipo;
import servicios.DescargaServicio;
import servicios.DriverServicio;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkAsignaciones;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private Label lblTipoCentro;
    @FXML private ComboBox<Tipo> cmbTipoCentro;
    @FXML private JFXButton btnBuscarPeriodo;
    
    @FXML private JFXButton btnCargar;
    
    @FXML private Label lblEntidades;
    @FXML private TextField txtBuscar;
    @FXML private TableView<CentroDriver> tabListar;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolGrupoGasto;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreDriver;
    @FXML private Label lblNumeroRegistros;
    @FXML private Tooltip ttAsignarDriverObjeto;
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnDriver;
    @FXML private JFXButton btnAsignarDriverCentro;
    @FXML private JFXButton btnAsignarDriverObjeto;
    @FXML private JFXButton btnQuitar;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    @FXML private JFXButton btnDescargar;
    
    // Variables de la aplicacion
    DriverDAO driverDAO;
    DriverServicio driverServicio;
    PlanDeCuentaDAO planDeCuentaDAO;
    CentroDAO centroDAO;
    ProductoDAO productoDAO;
    BancaDAO bancaDAO;
    CentroDriverDAO centroDriverDAO;
    FXMLLoader fxmlLoader;
    MenuControlador menuControlador;
    CentroDriver entidadSeleccionada;
    FilteredList<CentroDriver> filteredData;
    SortedList<CentroDriver> sortedData;
    int periodoSeleccionado;
    boolean tablaEstaActualizada;
    String titulo1, titulo2;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        driverServicio = new DriverServicio();
        planDeCuentaDAO = new PlanDeCuentaDAO();
        centroDAO = new CentroDAO();
        productoDAO = new ProductoDAO();
        bancaDAO = new BancaDAO();
        centroDriverDAO = new CentroDriverDAO();
        this.menuControlador = menuControlador;
        this.titulo = "Driver";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        // Ocultar Tipo de Centros
//        lblTipoCentro.setVisible(false);
//        cmbTipoCentro.setVisible(false);
        // Cambiar para Ingresos Operativos
        titulo1 = "Centros de Costos";
        titulo2 = "Centro de Costos";
        if (menuControlador.repartoTipo == 2) {
            btnAsignarDriverCentro.setText("Asignar Driver CEBE");
            titulo1 = "Centros de Beneficios";
            titulo2 = "Centro de Beneficio";
        }
        ttAsignarDriverObjeto.setText("Asignar un driver que distribuye a " + titulo1);
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                tablaEstaActualizada = false;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                tablaEstaActualizada = false;
            }
        });
        // Periodo seleccionado
        periodoSeleccionado = menuControlador.periodo;
        // inicializar el combo de repartoTipo Centro
        List<Tipo> lstCentroObjeto = new ArrayList();
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"-".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"D".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"E".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"F".equals(item.getCodigo())).findAny().orElse(null));
        cmbTipoCentro.setItems(FXCollections.observableList(lstCentroObjeto));
        cmbTipoCentro.setConverter(new StringConverter<Tipo>() {
            @Override
            public String toString(Tipo object) {
                return object.getNombre();
            }            
            @Override
            public Tipo fromString(String string) {
                return cmbTipoCentro.getItems().stream().filter(ap -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
        cmbTipoCentro.getSelectionModel().select(0);
        // fin codigo
        // Periodo seleccionado
        periodoSeleccionado = menuControlador.periodo;
        // Tabla: Formato
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoCentro.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreCentro.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolGrupoGasto.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolCodigoDriver.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreDriver.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolGrupoGasto.setCellValueFactory(cellData -> cellData.getValue().getGrupoGasto().nombreProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().codigoDriverProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().nombreDriverProperty());
        // Tabla: Buscar
        List<CentroDriver> listaEntidades = centroDAO.listarCentrosObjetosConDriver(periodoSeleccionado,"-",menuControlador.repartoTipo,-1);
        filteredData = new FilteredList(FXCollections.observableArrayList(listaEntidades), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigoCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getCodigoDriver().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreDriver().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getGrupoGasto().getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + sortedData.size());
        tablaEstaActualizada = true;
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
    

    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<CentroDriver> listaEntidades = centroDAO.listarCentrosObjetosConDriver(periodoSeleccionado,cmbTipoCentro.getValue().getCodigo(),menuControlador.repartoTipo,-1);
        if (listaEntidades.isEmpty() && mostrarMensaje)
            menuControlador.navegador.mensajeInformativo("Consulta de Entidades", "No existen Entidades para el periodo y tipo seleccionado.");
        filteredData = new FilteredList(FXCollections.observableArrayList(listaEntidades), p -> true);
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        txtBuscar.setText("");
        tablaEstaActualizada = true;
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_CARGAR);
    }

    @FXML void btnDriverAction(ActionEvent event) {
        verDriver();
    }
    
    @FXML void btnAsignarDriverCentroAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Asignar driver que distribuye a " + titulo1, "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        entidadSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar driver que distribuye a " + titulo1, "Por favor seleccione una entidad.");
            return;
        }
        if (!entidadSeleccionada.getCodigoDriver().equals("Sin driver asignado")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar driver que distribuye a " + titulo1, "La entidad ya cuenta con un driver asignado.\n¿Está seguro que desea reemplazar dicho driver?")) {
                buscarDriverCentro();
            }
            return;
        }
        buscarDriverCentro();
    }
    
    @FXML void btnAsignarDriverObjetoAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_ADD_REFRESH);
            return;
        }
        
        entidadSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_ADD_EMPTY);
            return;
        }
        if (!entidadSeleccionada.getCodigoDriver().equals("Sin driver asignado")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar driver que distribuye a Objetos de Costos", "La entidad ya cuenta con un driver asignado.\n¿Está seguro que desea reemplazar dicho driver?")) {
                centroDriverDAO.borrarAsignacionDriverObjeto(entidadSeleccionada.getCodigoCentro(), entidadSeleccionada.getGrupoGasto().getCodigo(),periodoSeleccionado);
                buscarDriverObjeto();
            }
            return;
        }
        buscarDriverObjeto();
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo(titulo,menuControlador.MENSAJE_DELETE_REFRESH);
            return;
        }
        
        entidadSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_DELETE_EMPTY);
            return;
        }
        
        if (entidadSeleccionada.getCodigoDriver().equals("Sin driver asignado"))
            return;
        
        if (!menuControlador.navegador.mensajeConfirmar("Quitar Driver", "¿Está seguro de quitar el Driver " + entidadSeleccionada.getNombreDriver()+ "?"))
            return;
        
        centroDriverDAO.borrarAsignacionDriverObjeto(entidadSeleccionada.getCodigoCentro(), entidadSeleccionada.getGrupoGasto().getCodigo(),periodoSeleccionado);
        menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), entidadSeleccionada.getCodigoDriver() + " de (" + entidadSeleccionada.getCodigoCentro()+ ")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado, false);
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio(tabListar,"AsignarCentrosObjetosDriver",null);
                descargaFile.descargarTablaAsignarCentroDriver(Integer.toString(periodoSeleccionado),directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, periodoSeleccionado,Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR.getDireccion());
            }else{
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_CANCELED);
            }
        }else{
            menuControlador.navegador.mensajeError(menuControlador.MENSAJE_DOWNLOAD_EMPTY);
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    /*@FXML void tabEntidadesFilaClick(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2){
                verDriver();
            }
        }
    }*/
    
    private void buscarDriverObjeto() {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_DRIVER_OBJETO.getVista()));
            fxmlLoader.setController(new BuscarDriverObjetoControlador(menuControlador, this, periodoSeleccionado));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Buscar driver que distribuye a Objetos de Costos");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }
    
    private void buscarDriverCentro() {
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_DRIVER_CENTRO.getVista()));
            fxmlLoader.setController(new BuscarDriverCentroControlador(menuControlador, this, periodoSeleccionado));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Buscar driver que distribuye a " + titulo1);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }
    
    private void verDriver() {
        CentroDriver entidad = tabListar.getSelectionModel().getSelectedItem();
        if (entidad == null) {
            menuControlador.navegador.mensajeInformativo("Ver driver", "Por favor seleccione una entidad.");
            return;
        }
        if (entidad.getCodigoDriver().equals("Sin driver asignado")) {
            menuControlador.navegador.mensajeInformativo("Ver driver", "La entidad seleccionada no tiene un driver asignado.");
            return;
        }
        try {
            String driverCodigo = entidad.getCodigoDriver();
            String driverNombre = entidad.getNombreDriver();
            DriverObjeto driverObjeto = null;
            List<DriverObjetoLinea> lstDriverObjetoLinea;
            // asumimos que es del primer repartoTipo
            lstDriverObjetoLinea = driverDAO.obtenerDriverObjetoLinea(periodoSeleccionado, driverCodigo);
            driverObjeto = new DriverObjeto(driverCodigo,driverNombre,null,null,lstDriverObjetoLinea,null,null);
                
            fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_VER_DRIVER_OBJETO.getVista()));
            fxmlLoader.setController(new VerDriverObjetoControlador(menuControlador, driverObjeto));
            
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Ver driver");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }

    @Override
    public void seleccionarEntidad(EntidadDistribucion entidad) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void seleccionarDriverObjeto(DriverObjeto driver) {
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar Driver que distribuye a Objetos de Costos", "Por favor seleccione una entidad.");
            return;
        }
        centroDriverDAO.asignarDriverObjeto(entidadSeleccionada.getCodigoCentro(),entidadSeleccionada.getGrupoGasto().getCodigo(), driver.getCodigo(), periodoSeleccionado, menuControlador.repartoTipo);
        menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), driver.getCodigo() + " a (" + entidadSeleccionada.getCodigoCentro() + ")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado, false);
    }

    @Override
    public void seleccionarDriverCentro(DriverCentro driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    

    
}
