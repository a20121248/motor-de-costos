package controlador.parametrizacion.driver_entidad.centros_bolsas;

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
import modelo.CentroDriver;
import modelo.Centro;
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
    @FXML private JFXButton btnBuscarPeriodo;
    
    @FXML private JFXButton btnCargar;
    
    @FXML private Label lblCentrosBolsas;
    @FXML private TextField txtBuscar;
    @FXML private TableView<CentroDriver> tabListar;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCuentaContable;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCuentaContable;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoPartida;
    @FXML private TableColumn<CentroDriver, String> tabcolNombrePartida;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreCentro;
    @FXML private TableColumn<CentroDriver, String> tabcolCodigoDriver;
    @FXML private TableColumn<CentroDriver, String> tabcolNombreDriver;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDriver;
    @FXML private JFXButton btnAsignarDriverCentro;
    @FXML private Tooltip ttAsignarDriverCentro;
    @FXML private JFXButton btnQuitar;
    
    @FXML private JFXButton btnGuardar;
    @FXML private JFXButton btnCancelar;
    
    // Variables de la aplicacion
    DriverDAO driverDAO;
    DriverServicio driverServicio;
    CentroDAO centroDAO;
    CentroDriverDAO centroDriverDAO;
    FXMLLoader fxmlLoader;
    MenuControlador menuControlador;
    CentroDriver entidadSeleccionada;
    FilteredList<CentroDriver> filteredData;
    SortedList<CentroDriver> sortedData;
    int periodoSeleccionado;
    boolean tablaEstaActualizada;
    String titulo1, titulo2;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR.getControlador());
    
    public ListarControlador(MenuControlador menuControlador) {
        driverDAO = new DriverDAO();
        centroDAO = new CentroDAO();
        centroDriverDAO = new CentroDriverDAO();
        this.menuControlador = menuControlador;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cambiar para Ingresos Operativos
        titulo1 = "Centros de Costos";
        titulo2 = "Centro de Costos";
        if (menuControlador.repartoTipo == 2) {
            btnAsignarDriverCentro.setText("Asignar Driver CEBE");
            titulo1 = "Centros de Beneficios";
            titulo2 = "Centro de Beneficio";
        }
        ttAsignarDriverCentro.setText("Asignar un driver que distribuye a " + titulo1);
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
        // Tabla: Formato
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        tabcolCodigoCuentaContable.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreCuentaContable.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigoPartida.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombrePartida.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigoCentro.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreCentro.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigoDriver.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolNombreDriver.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        
        tabcolCodigoCuentaContable.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaProperty());
        tabcolNombreCuentaContable.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().codigoDriverProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().nombreDriverProperty());
        // Tabla: Buscar
        List<CentroDriver> listaEntidades = centroDAO.listarCuentaPartidaCentroConDriver(periodoSeleccionado,"-",menuControlador.repartoTipo,-1,"SI");
        filteredData = new FilteredList(FXCollections.observableArrayList(listaEntidades), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigoCuenta().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreCuenta().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getCodigoPartida().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombrePartida().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getCodigoCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getCodigoDriver().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreDriver().toLowerCase().contains(lowerCaseFilter)) return true;
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR);
    }

    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<CentroDriver> listaEntidades = new ArrayList();
        listaEntidades.addAll(centroDAO.listarCuentaPartidaCentroConDriver(periodoSeleccionado,"-",menuControlador.repartoTipo,-1,"SI"));
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_CARGAR);
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
    
    @FXML void btnQuitarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Quitar Driver", "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        entidadSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (entidadSeleccionada == null) {
            menuControlador.navegador.mensajeInformativo("Quitar Driver", "Por favor seleccione una entidad.");
            return;
        }
        
        if (entidadSeleccionada.getCodigoDriver().equals("Sin driver asignado"))
            return;
        
        if (!menuControlador.navegador.mensajeConfirmar("Quitar Driver", "¿Está seguro de quitar el Driver " + entidadSeleccionada.getNombreDriver() + "?"))
            return;
        
        centroDriverDAO.borrarAsignacionBolsa(entidadSeleccionada.getCodigoCuenta(),entidadSeleccionada.getCodigoPartida(),entidadSeleccionada.getCodigoCentro(), periodoSeleccionado);
        menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), entidadSeleccionada.getCodigoDriver() + " de ("+ entidadSeleccionada.getCodigoCuenta()+ "," +entidadSeleccionada.getCodigoPartida()+ "," +entidadSeleccionada.getCodigoCentro()+")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado, false);
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
            DriverCentro driverCentro = null;
            List<DriverLinea> lstDriverLinea;
            DriverObjeto driverObjeto = null;
            List<DriverObjetoLinea> lstDriverObjetoLinea;
            // asumimos que es del primer repartoTipo
            lstDriverLinea = driverDAO.obtenerLstDriverLinea(periodoSeleccionado, driverCodigo, menuControlador.repartoTipo);
            if (lstDriverLinea.isEmpty()) {
                lstDriverObjetoLinea = driverDAO.obtenerDriverObjetoLinea(periodoSeleccionado, driverCodigo);
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
        centroDriverDAO.asignarDriverBolsa(entidadSeleccionada.getCodigoCuenta(),entidadSeleccionada.getCodigoPartida(),entidadSeleccionada.getCodigoCentro(), driver.getCodigo(), periodoSeleccionado);
        menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), driver.getCodigo() + " a ("+ entidadSeleccionada.getCodigoCuenta()+ "," +entidadSeleccionada.getCodigoPartida()+ "," +entidadSeleccionada.getCodigoCentro()+")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado, false);
    }

    @Override
    public void seleccionarDriverObjeto(DriverObjeto driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

