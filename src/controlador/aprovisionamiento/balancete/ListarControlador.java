package controlador.aprovisionamiento.balancete;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.DetalleGastoDAO;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import modelo.DetalleGasto;

public class ListarControlador implements Initializable {
    // Variables de la vista
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;

    @FXML private TextField txtBuscar;
    @FXML private TableView<DetalleGasto> tabListar;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoCuentaContable;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombreCuentaContable;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoPartida;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombrePartida;
    @FXML private TableColumn<DetalleGasto, String> tabcolCodigoCentro;
    @FXML private TableColumn<DetalleGasto, String> tabcolNombreCentro;
    @FXML private TableColumn<DetalleGasto, Double> tabcolMonto01;
    
    @FXML private Label lblNumeroRegistros;
    @FXML private Label lblMontoTotal;
    @FXML private JFXButton btnDescargar;
    
    // Variables de la aplicacion
    DetalleGastoDAO detalleGastoDAO;
    MenuControlador menuControlador;
    FilteredList<DetalleGasto> filteredData;
    SortedList<DetalleGasto> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_BALANCETE_LISTAR.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        detalleGastoDAO = new DetalleGastoDAO();
        this.titulo = "Detalle de Gasto";
        // Periodo seleccionado
        if (menuControlador.periodoSeleccionado % 100 == 0)
            ++menuControlador.periodoSeleccionado;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        // Mes seleccionado
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                buscarPeriodo(menuControlador.periodoSeleccionado);
            }
        });
        
        // Anho seleccionado
        spAnho.getValueFactory().setValue(menuControlador.periodoSeleccionado / 100);
        spAnho.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                menuControlador.periodoSeleccionado = newValue*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                buscarPeriodo(menuControlador.periodoSeleccionado);
            }
        });
        
        // Tabla: Formato
        tabcolCodigoCuentaContable.setCellValueFactory(cellData -> cellData.getValue().codigoCuentaContableProperty());
        tabcolNombreCuentaContable.setCellValueFactory(cellData -> cellData.getValue().nombreCuentaContableProperty());
        tabcolCodigoPartida.setCellValueFactory(cellData -> cellData.getValue().codigoPartidaProperty());
        tabcolNombrePartida.setCellValueFactory(cellData -> cellData.getValue().nombrePartidaProperty());
        tabcolCodigoCentro.setCellValueFactory(cellData -> cellData.getValue().codigoCentroProperty());
        tabcolNombreCentro.setCellValueFactory(cellData -> cellData.getValue().nombreCentroProperty());
        tabcolMonto01.setCellValueFactory(cellData -> cellData.getValue().monto01Property().asObject());
        tabcolMonto01.setCellFactory(column -> {
                return new TableCell<DetalleGasto, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%,.2f", item));
                    }
                }
            };
        });
        
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(detalleGastoDAO.listar(menuControlador.periodoSeleccionado, menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigoCuentaContable().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreCuentaContable().toLowerCase().contains(lowerCaseFilter)) return true;
                if (item.getCodigoPartida().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombrePartida().toLowerCase().contains(lowerCaseFilter)) return true;
                if (item.getCodigoCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombreCentro().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
            lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
            lblMontoTotal.setText(String.format("Total: %,.4f", filteredData.stream().mapToDouble(o -> o.getMonto01()).sum()));
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabListar.comparatorProperty());
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + sortedData.size());
        lblMontoTotal.setText(String.format("Monto total: %,.4f", filteredData.stream().mapToDouble(o -> o.getMonto01()).sum()));
    }

    // Acción de la pestaña 'Inicio'
    @FXML void lnkInicioAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_INICIO);
    }

    // Acción de la pestaña 'Aprovisionamiento'
    @FXML void lnkAprovisionamientoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }

    // Acción de la pestaña 'Balancete'
    @FXML void lnkBalanceteAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
    
    // Acción del botón 'Cargar'
    @FXML void btnCargarAction(ActionEvent event) {
        // Validar si el usuario tiene permiso para cargar el balancete
        if (!menuControlador.usuario.puede("APR_BAL_CARGAR")) {
            menuControlador.navegador.mensajeInformativo("Seguridad", "El usuario no tiene permiso para cargar el Balancete");
            return;
        }
        // Cambiar a la pantalla de cargar
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_CARGAR);
    }

    private void buscarPeriodo(int periodo) {
        List<DetalleGasto> lista = detalleGastoDAO.listar(periodo, menuControlador.repartoTipo);
        LOGGER.log(Level.INFO,"M3");
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        /*DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                if(menuControlador.repartoTipo ==1){
                    descargaFile = new DescargaServicio("CuentasContables", tabListar);
                    descargaFile.descargarTabla(Integer.toString(menuControlador.periodoSeleccionado),directorioSeleccionado.getAbsolutePath());
                    menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, menuControlador.periodoSeleccionado,Navegador.RUTAS_PLANES_ASIGNAR_PERIODO.getDireccion());
                }else{
                    //generar descarga con todos las columnas por mes
                }   
            }else{
                menuControlador.mensaje.download_canceled();
            }
        }else{
            menuControlador.mensaje.download_empty();
        }*/
    }

    // Acción del botón 'Atrás'
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_APROVISIONAMIENTO);
    }
}