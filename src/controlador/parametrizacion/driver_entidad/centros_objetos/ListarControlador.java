package controlador.parametrizacion.driver_entidad.centros_objetos;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarDriverObjetoControlador;
import controlador.modals.VerDriverObjetoControlador;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.CentroDriver;
import modelo.DriverCentro;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import modelo.Tipo;
import servicios.DescargaServicio;
import servicios.DriverServicio;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private HBox hbPeriodo;
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private ComboBox<Tipo> cmbTipoCentro;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<CentroDriver> tabListar;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolGrupoGasto;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreDriver;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private Tooltip ttAsignarDriverObjeto;
    
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
        // Periodo seleccionado
        if (menuControlador.repartoTipo == 1)
            periodoSeleccionado = menuControlador.periodo;
        else
            periodoSeleccionado = menuControlador.periodo / 100 * 100;
        
        // Mensaje de ayuda sobre botón de asignar
        ttAsignarDriverObjeto.setText("Asignar un driver que distribuye a " + titulo1);
        
        // Mes seleccionado
        if (menuControlador.repartoTipo == 1) {
            cmbMes.getItems().addAll(menuControlador.lstMeses);
            cmbMes.getSelectionModel().select(menuControlador.mesActual - 1);
            cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    if (menuControlador.repartoTipo == 1)
                        periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                    else
                        periodoSeleccionado = spAnho.getValue()*100;
                    buscarPeriodo(periodoSeleccionado, cmbTipoCentro.getValue().getCodigo(), menuControlador.repartoTipo);
                }
            });
        } else {
            hbPeriodo.getChildren().remove(cmbMes);
        }
        
        // Seleccionar anho
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if (menuControlador.repartoTipo == 1)
                    periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                else
                    periodoSeleccionado = spAnho.getValue()*100;
                buscarPeriodo(periodoSeleccionado, cmbTipoCentro.getValue().getCodigo(), menuControlador.repartoTipo);
            }
        });
        
        // inicializar el combo de repartoTipo Objeto
        List<Tipo> lstCentroObjeto = new ArrayList();
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"-".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"LINEA".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"CANAL".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"FICTICIO".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"PROYECTO".equals(item.getCodigo())).findAny().orElse(null));
        lstCentroObjeto.add(menuControlador.lstCentroTipos.stream().filter(item ->"SALUD".equals(item.getCodigo())).findAny().orElse(null));
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
        cmbTipoCentro.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                buscarPeriodo(periodoSeleccionado, newValue.getCodigo(), menuControlador.repartoTipo);
            }
        });

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
    
    private void buscarPeriodo(int periodo, String tipoCentro, int repartoTipo) {
        List<CentroDriver> listaEntidades = centroDAO.listarCentrosObjetosConDriver(periodo, tipoCentro, repartoTipo, -1);
        filteredData = new FilteredList(FXCollections.observableArrayList(listaEntidades), p -> true);
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        txtBuscar.setText("");
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_CARGAR);
    }

    @FXML void btnDriverAction(ActionEvent event) {
        verDriver();
    }
    
    @FXML void btnAsignarDriverCentroAction(ActionEvent event) {        
        entidadSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar driver que distribuye a " + titulo1, "Por favor seleccione una entidad.");
            return;
        }
        if (!entidadSeleccionada.getCodigoDriver().equals("Sin driver asignado")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar driver que distribuye a " + titulo1, "La entidad ya cuenta con un driver asignado.\n¿Está seguro que desea reemplazar dicho driver?")) {
                buscarDriverObjeto();
            }
            return;
        }
        buscarDriverObjeto();
    }
    
    @FXML void btnAsignarDriverObjetoAction(ActionEvent event) {        
        entidadSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_ADD_EMPTY);
            return;
        }
        if (!entidadSeleccionada.getCodigoDriver().equals("Sin driver asignado")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar driver que distribuye a Objetos de Costos", "La entidad ya cuenta con un driver asignado.\n¿Está seguro que desea reemplazar dicho driver?")) {
                centroDriverDAO.borrarAsignacionDriverObjeto(entidadSeleccionada.getCodigoCentro(), entidadSeleccionada.getGrupoGasto().getCodigo(), periodoSeleccionado, menuControlador.repartoTipo);
                buscarDriverObjeto();
            }
            return;
        }
        buscarDriverObjeto();
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        entidadSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo(titulo, menuControlador.MENSAJE_DELETE_EMPTY);
            return;
        }
        
        if (entidadSeleccionada.getCodigoDriver().equals("Sin driver asignado"))
            return;
        
        if (!menuControlador.navegador.mensajeConfirmar("Quitar Driver", "¿Está seguro de quitar el Driver " + entidadSeleccionada.getNombreDriver()+ "?"))
            return;
        
        centroDriverDAO.borrarAsignacionDriverObjeto(entidadSeleccionada.getCodigoCentro(), entidadSeleccionada.getGrupoGasto().getCodigo(), periodoSeleccionado, menuControlador.repartoTipo);
        menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), entidadSeleccionada.getCodigoDriver() + " de (" + entidadSeleccionada.getCodigoCentro()+ ")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado, cmbTipoCentro.getValue().getCodigo(), menuControlador.repartoTipo);
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
            lstDriverObjetoLinea = driverDAO.obtenerDriverObjetoLinea(periodoSeleccionado, driverCodigo,menuControlador.repartoTipo);
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
        buscarPeriodo(periodoSeleccionado, cmbTipoCentro.getValue().getCodigo(), menuControlador.repartoTipo);
    }

    @Override
    public void seleccionarDriverCentro(DriverCentro driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
