package controlador.parametrizacion.driver_entidad.centros_bolsas;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarDriverCentroControlador;
import controlador.modals.VerDriverCentroControlador;
import controlador.modals.VerDriverObjetoControlador;
import dao.CentroDAO;
import dao.CentroDriverDAO;
import dao.DriverDAO;
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
import javafx.stage.Stage;
import modelo.CentroDriver;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.DriverObjetoLinea;
import modelo.EntidadDistribucion;
import servicios.DescargaServicio;
import servicios.DriverServicio;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private HBox hbPeriodo;
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
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
    @FXML private JFXButton btnDescargar;
    
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
    String titulo1, titulo2;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        driverDAO = new DriverDAO();
        centroDAO = new CentroDAO();
        centroDriverDAO = new CentroDriverDAO();
        titulo = "Asignar Centros Bolsas";
        titulo1 = "Centros de Costos";
        titulo2 = "Centro de Costos";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Periodo seleccionado
        if (menuControlador.repartoTipo == 1)
            periodoSeleccionado = menuControlador.periodo;
        else
            periodoSeleccionado = menuControlador.periodo / 100 * 100;
        
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
                buscarPeriodo(periodoSeleccionado);
            }
        });

        // Formato de la tabla
        tabcolCodigoCuentaContable.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaProperty());
        tabcolNombreCuentaContable.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolCodigoDriver.setCellValueFactory(cellData -> cellData.getValue().codigoDriverProperty());
        tabcolNombreDriver.setCellValueFactory(cellData -> cellData.getValue().nombreDriverProperty());
        
        // Funcionalidad para buscar en la tabla
        List<CentroDriver> listaEntidades = centroDAO.listarCuentaPartidaCentroBolsaConDriverDirecta(periodoSeleccionado, menuControlador.repartoTipo);
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
    
    private void buscarPeriodo(int periodo) {
        List<CentroDriver> listaEntidades = new ArrayList();
        listaEntidades.addAll(centroDAO.listarCuentaPartidaCentroBolsaConDriverDirecta(periodo, menuControlador.repartoTipo));
        filteredData = new FilteredList(FXCollections.observableArrayList(listaEntidades), p -> true);
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        txtBuscar.setText("");
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_CARGAR);
    }

    @FXML void btnDriverAction(ActionEvent event) {
        verDriver();
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio(tabListar,"AsignarCentrosBolsasDriver",null);
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
                lstDriverObjetoLinea = driverDAO.obtenerDriverObjetoLinea(periodoSeleccionado, driverCodigo,menuControlador.repartoTipo);
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
        centroDriverDAO.asignarDriverBolsa(entidadSeleccionada.getCodigoCuenta(),entidadSeleccionada.getCodigoPartida(),entidadSeleccionada.getCodigoCentro(), driver.getCodigo(), periodoSeleccionado, menuControlador.repartoTipo);
        menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), driver.getCodigo() + " a ("+ entidadSeleccionada.getCodigoCuenta()+ "," +entidadSeleccionada.getCodigoPartida()+ "," +entidadSeleccionada.getCodigoCentro()+")", periodoSeleccionado, menuControlador.navegador.RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado);
    }

    @Override
    public void seleccionarDriverObjeto(DriverObjeto driver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

