package controlador.parametrizacion.planes.periodo;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.ObjetoControladorInterfaz;
import controlador.Navegador;
import controlador.modals.BuscarEntidadControlador;
import dao.PlanDeCuentaDAO;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.CuentaContable;
import modelo.DriverCentro;
import modelo.DriverObjeto;
import modelo.EntidadDistribucion;
import modelo.Tipo;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkPlanDeCuentas;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private JFXButton btnBuscarPeriodo;
    
    @FXML private JFXButton btnAgregar;
    @FXML private JFXButton btnQuitar;
    @FXML private JFXButton btnCargar;
    @FXML private JFXButton btnCatalogo;
    
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<EntidadDistribucion> tabListar;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolCodigo;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolNombre;
    @FXML private TableColumn<EntidadDistribucion, Double> tabcolSaldo;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargar;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    FXMLLoader fxmlLoader;
    PlanDeCuentaDAO planDeCuentaDAO;
    public MenuControlador menuControlador;
    FilteredList<EntidadDistribucion> filteredData;
    SortedList<EntidadDistribucion> sortedData;
    int periodoSeleccionado;
    boolean tablaEstaActualizada;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        this.titulo = "Cuentas Contables";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            // Periodo seleccionado
            periodoSeleccionado = menuControlador.periodo-menuControlador.periodo%100;
        } else {
            // Periodo seleccionado
            periodoSeleccionado = menuControlador.periodo;
        }

        // Botones para periodo
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.mesActual-1);
        spAnho.getValueFactory().setValue(menuControlador.anhoActual);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
               if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                tablaEstaActualizada = false;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                if(menuControlador.repartoTipo == 2) periodoSeleccionado = spAnho.getValue()*100;
                else periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                tablaEstaActualizada = false;
            }
        });
        
        // Tabla: Formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolSaldo.setCellValueFactory(cellData -> cellData.getValue().saldoAcumuladoProperty().asObject());
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 60);
        tabcolSaldo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(planDeCuentaDAO.listar(periodoSeleccionado,null,menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
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
    
    @FXML void lnkPlanDeCuentasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO);
    }
    
    @FXML void btnAgregarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.mensaje.add_refresh_error(titulo);
            return;
        }
        Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.stream().filter(item -> "CTA".equals(item.getCodigo())).findFirst().orElse(null);
        menuControlador.codigos = tabListar.getItems().stream().map(i -> "'"+i.getCodigo()+"'").collect(Collectors.joining (","));
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_ENTIDAD.getVista()));
            BuscarEntidadControlador buscarEntidadControlador = new BuscarEntidadControlador(menuControlador, this, tipoSeleccionado, -1, menuControlador.repartoTipo);
            fxmlLoader.setController(buscarEntidadControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(String.format("Agregar %s para el periodo de %s de %d",tipoSeleccionado.getNombre(),cmbMes.getValue(),spAnho.getValue()));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.mensaje.delete_refresh_error(titulo);
            return;
        }
        
        EntidadDistribucion item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
            return;
        }
       
        if (!menuControlador.navegador.mensajeConfirmar("Quitar Cuenta Contable", "¿Está seguro de quitar la Cuenta Contable " + item.getNombre() + "?"))
            return;        
        
        //Si PlanDeCuenta no encuentra items asociados al periodo podrá eliminar el objeto

        if(planDeCuentaDAO.verificarObjetoPlanCuentaPeriodoAsignacion(item.getCodigo(),periodoSeleccionado) == 0){
            planDeCuentaDAO.eliminarObjetoCuentaPeriodo(item.getCodigo(), periodoSeleccionado);
            menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), item.getCodigo(),periodoSeleccionado,Navegador.RUTAS_PLANES_ASIGNAR_PERIODO.getDireccion());
            buscarPeriodo(periodoSeleccionado, false);
        }else{
            menuControlador.mensaje.delete_item_periodo_error(titulo);
        }   
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_ASIGNAR_PERIODO_CARGAR);
    }
    
    @FXML void btnCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_PLANES_MAESTRO_LISTAR);
    }
       
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<CuentaContable> lista = planDeCuentaDAO.listar(periodo,null,menuControlador.repartoTipo);
        if (lista.isEmpty() && mostrarMensaje)
            menuControlador.mensaje.show_table_empty(titulo);
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        txtBuscar.setText("");
        tablaEstaActualizada = true;
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                if(menuControlador.repartoTipo ==1){
                    descargaFile = new DescargaServicio("CuentasContables", tabListar);
                    descargaFile.descargarTabla(Integer.toString(periodoSeleccionado),directorioSeleccionado.getAbsolutePath());
                    menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, periodoSeleccionado,Navegador.RUTAS_PLANES_ASIGNAR_PERIODO.getDireccion());
                }else{
                    /*generar descarga con todos las columnas por mes*/
                }   
            }else{
                menuControlador.mensaje.download_canceled();
            }
        }else{
            menuControlador.mensaje.download_empty();
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
    
    @Override
    public void seleccionarEntidad(EntidadDistribucion entidad) {
        planDeCuentaDAO.insertarObjetoCuentaPeriodo(entidad.getCodigo(),periodoSeleccionado,menuControlador.repartoTipo);
        menuControlador.Log.agregarItemPeriodo(LOGGER,menuControlador.usuario.getUsername(), entidad.getCodigo(),periodoSeleccionado, Navegador.RUTAS_PLANES_ASIGNAR_PERIODO.getDireccion());
        buscarPeriodo(periodoSeleccionado, false);
    }

    @Override
    public void seleccionarDriverCentro(DriverCentro driver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void seleccionarDriverObjeto(DriverObjeto driver) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
