package controlador.parametrizacion.cuenta_partida;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarEntidadControlador;
import dao.PartidaDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Partida;
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
    @FXML private Hyperlink lnkListar;

    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private JFXButton btnBuscarPeriodo;
    @FXML private JFXButton btnEsBolsa;
    @FXML private JFXButton btnAsignar;
    @FXML private JFXButton btnQuitar;
    @FXML private JFXButton btnCargar;
    
    @FXML private TextField txtBuscar;
    @FXML private TableView<Partida> tabListar;
    @FXML private TableColumn<Partida, String> tabcolCodigoCuenta;
    @FXML private TableColumn<Partida, String> tabcolNombreCuenta;
    @FXML private TableColumn<Partida, String> tabcolCodigoPartida;
    @FXML private TableColumn<Partida, String> tabcolNombrePartida;
    @FXML private TableColumn<Partida, Double> tabcolSaldo;
    @FXML private TableColumn<Partida, String> tabcolEsBolsa;
    
    @FXML private Label lblNumeroRegistros;
    @FXML private JFXButton btnDescargar;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    PartidaDAO partidaDAO;
    public MenuControlador menuControlador;
    Partida partidaSeleccionada;
    FilteredList<Partida> filteredData;
    SortedList<Partida> sortedData;
    int periodoSeleccionado;
    String tipoGasto;
    boolean tablaEstaActualizada;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CUENTA_PARTIDA_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        tipoGasto = (String) menuControlador.objeto;
        partidaDAO = new PartidaDAO();
        this.titulo = "Asignación";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Ocultar para Ingresos Operativos
        if (menuControlador.repartoTipo == 2) {
            cmbMes.setVisible(false);
            // Periodo seleccionado
            periodoSeleccionado = menuControlador.periodo-menuControlador.periodo%100;
        } else {
            // Periodo seleccionado
            periodoSeleccionado = menuControlador.periodo;
        }

        // meses
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
        tabcolCodigoCuenta.setCellValueFactory(cellData -> cellData.getValue().getCuentaContable().codigoProperty());
        tabcolNombreCuenta.setCellValueFactory(cellData -> cellData.getValue().getCuentaContable().nombreProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolSaldo.setCellValueFactory(cellData -> cellData.getValue().saldoAcumuladoProperty().asObject());
        tabcolEsBolsa.setCellValueFactory(cellData -> cellData.getValue().esBolsaProperty());
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigoCuenta.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombreCuenta.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolCodigoPartida.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolNombrePartida.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolEsBolsa.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        tabcolSaldo.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(partidaDAO.listarPartidaConCuentaContable(periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getCuentaContable().getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getCuentaContable().getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
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
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CUENTA_PARTIDA_LISTAR);
    }
    @FXML void btnEsBolsaAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.mensaje.update_refresh_error(titulo);
            return;
        }
        
        partidaSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (partidaSeleccionada == null) {
            menuControlador.mensaje.update_empty(titulo);
            return;
        }
        if (partidaSeleccionada.getCuentaContable().getCodigo().equals("Sin CuentaContable asignada")){
            menuControlador.mensaje.update_item_without_allocate(titulo);
            return;
        }    
        partidaDAO.actualizarCuentaPartidaBolsa(partidaSeleccionada, periodoSeleccionado,menuControlador.repartoTipo);
        String item = " de la asignación Cuenta - Partida ("+partidaSeleccionada.getCuentaContable().getCodigo()+","+ partidaSeleccionada.getCodigo()+")";
        menuControlador.Log.editarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(),item,periodoSeleccionado,Navegador.RUTAS_CUENTA_PARTIDA_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado, false);
        
    }
    
    @FXML void btnAsignarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.mensaje.add_refresh_error(titulo);
            return;
        }
        
        partidaSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (partidaSeleccionada == null) {
            menuControlador.mensaje.add_empty(titulo);
            return;
        }
        if (!partidaSeleccionada.getCuentaContable().getCodigo().equals("Sin CuentaContable asignada")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar Partida a Cuentas Contables", "La partida ya cuenta con Cuenta Contable asignada.\n¿Está seguro que desea reemplazar dicha Cuenta Contable?")) {
                partidaDAO.borrarPartidaCuenta(partidaSeleccionada.getCodigo(), periodoSeleccionado, menuControlador.repartoTipo);
                buscarCuentaContable();
            }
            return;
        }
        buscarCuentaContable();
    }
    
    private void buscarCuentaContable() {
        Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.stream().filter(item -> "CTA".equals(item.getCodigo())).findFirst().orElse(null);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_ENTIDAD.getVista()));
            BuscarEntidadControlador buscarEntidadControlador = new BuscarEntidadControlador(menuControlador, this, tipoSeleccionado, periodoSeleccionado, menuControlador.repartoTipo);
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
        
        partidaSeleccionada = tabListar.getSelectionModel().getSelectedItem();
        if (partidaSeleccionada == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
            return;
        }
        
        if (partidaSeleccionada.getCuentaContable().getCodigo().equals("Sin driver asignado"))
            return;
        
        if (!menuControlador.navegador.mensajeConfirmar("Quitar asignación", "¿Está seguro de quitar la Cuenta Contable " + partidaSeleccionada.getCuentaContable().getNombre() + "?"))
            return;
        
        partidaDAO.borrarPartidaCuenta(partidaSeleccionada.getCodigo(), periodoSeleccionado, menuControlador.repartoTipo);
        menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(),partidaSeleccionada.getCuentaContable().getCodigo()+ " a la Partida "+ partidaSeleccionada.getCodigo(),periodoSeleccionado,Navegador.RUTAS_CUENTA_PARTIDA_LISTAR.getDireccion());
        buscarPeriodo(periodoSeleccionado, false);
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CUENTA_PARTIDA_CARGAR);
    }
    
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<Partida> lista = partidaDAO.listarPartidaConCuentaContable(periodo,menuControlador.repartoTipo);
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
                descargaFile = new DescargaServicio("Asignaciones", tabListar);
                descargaFile.descargarTabla(Integer.toString(periodoSeleccionado),directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, periodoSeleccionado,Navegador.RUTAS_CUENTA_PARTIDA_LISTAR.getDireccion());
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
        if (partidaSeleccionada == null) {
            menuControlador.mensaje.add_empty(titulo);
            return;
        }
        partidaDAO.insertarPartidaCuenta(partidaSeleccionada.getCodigo(), entidad.getCodigo(), periodoSeleccionado, menuControlador.repartoTipo);
        menuControlador.Log.agregarItemPeriodo(LOGGER,menuControlador.usuario.getUsername(), entidad.getCodigo()+ " a la partida "+ partidaSeleccionada.getCodigo(),periodoSeleccionado, Navegador.RUTAS_CUENTA_PARTIDA_LISTAR.getDireccion());
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

