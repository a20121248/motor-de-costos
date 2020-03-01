package controlador.parametrizacion.driver_entidad;

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
import dao.DriverDAO;
import dao.PlanDeCuentaDAO;
import dao.ProductoDAO;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import modelo.Tipo;
import servicios.DriverServicio;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkAsignaciones;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private ComboBox<Tipo> cmbTipoEntidad;
    @FXML private Label lblTipoCentro;
    @FXML private ComboBox<Tipo> cmbTipoCentro;
    @FXML private JFXButton btnBuscarPeriodo;
    
    @FXML private JFXButton btnCargar;
    
    @FXML private Label lblEntidades;
    @FXML private TextField txtBuscar;
    @FXML private TableView<EntidadDistribucion> tabEntidades;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolCodigoEntidad;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolNombreEntidad;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolCodigoDriver;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolNombreDriver;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDriver;
    @FXML private JFXButton btnAsignarDriverCentro;
    @FXML private Tooltip ttAsignarDriverCentro;
    @FXML private JFXButton btnAsignarDriverObjeto;
    @FXML private JFXButton btnQuitar;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    DriverDAO driverDAO;
    DriverServicio driverServicio;
    PlanDeCuentaDAO planDeCuentaDAO;
    CentroDAO centroDAO;
    ProductoDAO productoDAO;
    BancaDAO bancaDAO;
    AsignacionEntidadDriverDAO asignacionEntidadDriverDAO;
    FXMLLoader fxmlLoader;
    MenuControlador menuControlador;
    EntidadDistribucion entidadSeleccionada;
    FilteredList<EntidadDistribucion> filteredData;
    SortedList<EntidadDistribucion> sortedData;
    boolean tablaEstaActualizada;
    String titulo1, titulo2;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        driverServicio = new DriverServicio();
        planDeCuentaDAO = new PlanDeCuentaDAO();
        centroDAO = new CentroDAO();
        productoDAO = new ProductoDAO();
        bancaDAO = new BancaDAO();
        asignacionEntidadDriverDAO = new AsignacionEntidadDriverDAO();
        this.menuControlador = menuControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ocultar Tipo de Centros
        lblTipoCentro.setVisible(false);
        cmbTipoCentro.setVisible(false);
        // Cambiar para Ingresos Operativos
        titulo1 = "Centros de Costos";
        titulo2 = "Centro de Costos";
        if (menuControlador.repartoTipo == 2) {
            btnAsignarDriverCentro.setText("Asignar Driver CEBE");
            titulo1 = "Centros de Beneficios";
            titulo2 = "Centro de Beneficio";
        }
        ttAsignarDriverCentro.setText("Asignar un driver que distribuye a " + titulo1);
        // Botones para periodo
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) 
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        spAnho.getValueFactory().setValue(menuControlador.periodoSeleccionado / 100);
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        // inicializar el combo de repartoTipo entidad
        List<String> lstFiltro = Arrays.asList("-","GCTA","CECO");
        cmbTipoEntidad.setItems(FXCollections.observableList(menuControlador.lstEntidadTipos.stream().filter(item -> lstFiltro.contains(item.getCodigo())).collect(Collectors.toList())));
        cmbTipoEntidad.setConverter(new StringConverter<Tipo>() {
            @Override
            public String toString(Tipo object) {
                return object.getNombre();
            }
            @Override
            public Tipo fromString(String string) {
                return cmbTipoEntidad.getItems().stream().filter(ap -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
        cmbTipoEntidad.getSelectionModel().select(0);
        // fin codigo
        // inicializar el combo de repartoTipo Centro
        cmbTipoCentro.setItems(FXCollections.observableList(menuControlador.lstCentroTipos));
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
        // fin codigo
        // Tabla: Formato
        tabEntidades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoEntidad.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreEntidad.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        tabcolCodigoDriver.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreDriver.setMaxWidth(1f * Integer.MAX_VALUE * 35);
        tabcolCodigoEntidad.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombreEntidad.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().getDriver().codigoProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().getDriver().nombreProperty());
        // Tabla: Buscar
        List<EntidadDistribucion> listaEntidades = new ArrayList();
        listaEntidades.addAll(planDeCuentaDAO.listarGruposNombresConDriver(menuControlador.periodoSeleccionado,menuControlador.repartoTipo));
        listaEntidades.addAll(centroDAO.listarCentrosNombresConDriver(menuControlador.periodoSeleccionado,"-",menuControlador.repartoTipo,-1));
        filteredData = new FilteredList(FXCollections.observableArrayList(listaEntidades), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getDriver().getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getDriver().getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabEntidades.comparatorProperty());
        tabEntidades.setItems(sortedData);
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @FXML void cmbTipoEntidadAction(ActionEvent event) {
        Tipo tipo = cmbTipoEntidad.getValue();
        if (tipo.getCodigo().equals("CECO")) {
            lblTipoCentro.setVisible(true);
            cmbTipoCentro.setVisible(true);
            cmbTipoCentro.getSelectionModel().select(0);
        } else {
            lblTipoCentro.setVisible(false);
            cmbTipoCentro.setVisible(false);            
        }
    }

    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(menuControlador.periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<EntidadDistribucion> listaEntidades = new ArrayList();
        switch (cmbTipoEntidad.getValue().getCodigo()) {
            case "GCTA": // Grupo de Cuentas Contables
                lblEntidades.setText("Lista de Grupo de Cuentas Contables");
                tabEntidades.getColumns().get(0).setText("CÓDIGO GRUPO DE CUENTAS");
                tabEntidades.getColumns().get(1).setText("NOMBRE GRUPO DE CUENTAS");
                listaEntidades.addAll(planDeCuentaDAO.listarGruposNombresConDriver(periodo,menuControlador.repartoTipo));
                break;
            case "CECO": // Centro
                lblEntidades.setText("Lista de " + titulo1);
                tabEntidades.getColumns().get(0).setText("CÓDIGO " + titulo2.toUpperCase());
                tabEntidades.getColumns().get(1).setText("NOMBRE " + titulo2.toUpperCase());
                listaEntidades.addAll(centroDAO.listarCentrosNombresConDriver(periodo,cmbTipoCentro.getValue().getCodigo(),menuControlador.repartoTipo,-1));
                break;
            default:
                lblEntidades.setText("Lista de entidades");
                tabEntidades.getColumns().get(0).setText("CÓDIGO ENTIDAD");
                tabEntidades.getColumns().get(1).setText("NOMBRE ENTIDAD");
                listaEntidades.addAll(planDeCuentaDAO.listarGruposNombresConDriver(periodo,menuControlador.repartoTipo));
                listaEntidades.addAll(centroDAO.listarCentrosNombresConDriver(periodo,"-",menuControlador.repartoTipo,-1));
                break;
        }
        
        if (listaEntidades.isEmpty() && mostrarMensaje)
            menuControlador.navegador.mensajeInformativo("Consulta de Entidades", "No existen Entidades para el periodo y tipo seleccionado.");
        filteredData = new FilteredList(FXCollections.observableArrayList(listaEntidades), p -> true);
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabEntidades.comparatorProperty());
        tabEntidades.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        txtBuscar.setText("");
        tablaEstaActualizada = true;
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CARGAR);
    }

    @FXML void btnDriverAction(ActionEvent event) {
        verDriver();
    }
    
    @FXML void btnAsignarDriverCentroAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Asignar driver que distribuye a " + titulo1, "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        entidadSeleccionada = tabEntidades.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar driver que distribuye a " + titulo1, "Por favor seleccione una entidad.");
            return;
        }
        if (!entidadSeleccionada.getDriver().getCodigo().equals("Sin driver asignado")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar driver que distribuye a " + titulo1, "La entidad ya cuenta con un driver asignado.\n¿Está seguro que desea reemplazar dicho driver?")) {
                buscarDriverCentro();
            }
            return;
        }
        buscarDriverCentro();
    }
    
    @FXML void btnAsignarDriverObjetoAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Asignar driver que distribuye a Objetos de Costos", "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        entidadSeleccionada = tabEntidades.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar driver que distribuye a Objetos de Costos", "Por favor seleccione una entidad.");
            return;
        }
        if (!entidadSeleccionada.getDriver().getCodigo().equals("Sin driver asignado")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar driver que distribuye a Objetos de Costos", "La entidad ya cuenta con un driver asignado.\n¿Está seguro que desea reemplazar dicho driver?")) {
                buscarDriverObjeto();
            }
            return;
        }
        buscarDriverObjeto();
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Quitar Driver", "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        entidadSeleccionada = tabEntidades.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Quitar Driver", "Por favor seleccione una entidad.");
            return;
        }
        
        if (entidadSeleccionada.getDriver().getCodigo().equals("Sin driver asignado"))
            return;
        
        if (!menuControlador.navegador.mensajeConfirmar("Quitar Driver", "¿Está seguro de quitar el Driver " + entidadSeleccionada.getDriver().getNombre() + "?"))
            return;
        
        asignacionEntidadDriverDAO.borrarAsignacion(entidadSeleccionada.getCodigo(), menuControlador.periodoSeleccionado);
        buscarPeriodo(menuControlador.periodoSeleccionado, false);
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
            fxmlLoader.setController(new BuscarDriverObjetoControlador(menuControlador, this, menuControlador.periodoSeleccionado));
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
            fxmlLoader.setController(new BuscarDriverCentroControlador(menuControlador, this, menuControlador.periodoSeleccionado));
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
        EntidadDistribucion entidad = tabEntidades.getSelectionModel().getSelectedItem();
        if (entidad == null) {
            menuControlador.navegador.mensajeInformativo("Ver driver", "Por favor seleccione una entidad.");
            return;
        }
        if (entidad.getDriver().getCodigo().equals("Sin driver asignado")) {
            menuControlador.navegador.mensajeInformativo("Ver driver", "La entidad seleccionada no tiene un driver asignado.");
            return;
        }
        try {
            String driverCodigo = entidad.getDriver().getCodigo();
            String driverNombre = entidad.getDriver().getNombre();
            DriverCentro driverCentro = null;
            List<DriverLinea> lstDriverLinea;
            DriverObjeto driverObjeto = null;
            List<DriverObjetoLinea> lstDriverObjetoLinea;
            // asumimos que es del primer repartoTipo
            lstDriverLinea = driverDAO.obtenerLstDriverLinea(menuControlador.periodoSeleccionado, driverCodigo, menuControlador.repartoTipo);
            if (lstDriverLinea.isEmpty()) {
                lstDriverObjetoLinea = driverDAO.obtenerDriverObjetoLinea(menuControlador.periodoSeleccionado, driverCodigo);
                driverObjeto = new DriverObjeto(driverCodigo,driverNombre,null,null,lstDriverObjetoLinea,null,null);
                
                fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_VER_DRIVER_OBJETO.getVista()));
                fxmlLoader.setController(new VerDriverObjetoControlador(menuControlador, driverObjeto));
            } else {
                driverCentro = new DriverCentro(driverCodigo,driverNombre,null,null,lstDriverLinea,null,null);
                
                fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_VER_DRIVER_CENTRO.getVista()));
                fxmlLoader.setController(new VerDriverCentroControlador(menuControlador, driverCentro));
            }
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
    public void seleccionarDriverCentro(DriverCentro driver) {
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar Driver que distribuye a Centros de Costos", "Por favor seleccione una entidad.");
            return;
        }
        asignacionEntidadDriverDAO.asignar(entidadSeleccionada.getCodigo(), driver.getCodigo(), menuControlador.periodoSeleccionado);
        buscarPeriodo(menuControlador.periodoSeleccionado, false);
    }

    @Override
    public void seleccionarDriverObjeto(DriverObjeto driver) {
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Asignar Driver que distribuye a Objetos de Costos", "Por favor seleccione una entidad.");
            return;
        }
        asignacionEntidadDriverDAO.asignar(entidadSeleccionada.getCodigo(), driver.getCodigo(), menuControlador.periodoSeleccionado);
        buscarPeriodo(menuControlador.periodoSeleccionado, false);
    }
}
