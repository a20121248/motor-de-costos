package controlador.parametrizacion.centros.periodo;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.ObjetoControladorInterfaz;
import controlador.Navegador;
import controlador.modals.BuscarEntidadControlador;
import dao.CentroDAO;
import dao.DriverLineaDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Centro;
import modelo.DriverCentro;
import modelo.DriverObjeto;
import modelo.EntidadDistribucion;
import modelo.Tipo;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista    
    @FXML private HBox hbPeriodo;
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    
    @FXML private TextField txtBuscar;
    
    @FXML private TableView<Centro> tabListar;
    @FXML private TableColumn<Centro, String> tabcolCodigo;
    @FXML private TableColumn<Centro, String> tabcolNombre;
    @FXML private TableColumn<Centro, String> tabcolTipoCentro;
    @FXML private TableColumn<Centro, Double> tabcolSaldo;
    
    @FXML private Label lblNumeroRegistros;    
    @FXML private JFXButton btnDescargar;
    
    // Variables de la aplicacion
    CentroDAO centroDAO;
    DriverLineaDAO driverLineaDAO;
    public MenuControlador menuControlador;
    FilteredList<Centro> filteredData;
    SortedList<Centro> sortedData;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO.getControlador());
    String titulo, titulo2;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        driverLineaDAO = new DriverLineaDAO();
        titulo = "Centros de Costos";
        titulo2 = "Centro de Costos";
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Periodo seleccionado
        if (menuControlador.repartoTipo != 1)
            menuControlador.periodoSeleccionado = menuControlador.periodoSeleccionado / 100 * 100;
        
        // Mes seleccionado
        if (menuControlador.repartoTipo == 1) {
            cmbMes.getItems().addAll(menuControlador.lstMeses);
            cmbMes.getSelectionModel().select(menuControlador.periodoSeleccionado % 100 - 1);
            cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    menuControlador.periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
                    buscarPeriodo(menuControlador.periodoSeleccionado);
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
                buscarPeriodo(menuControlador.periodoSeleccionado);
            }
        });

        // Tabla: Formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolTipoCentro.setCellValueFactory(cellData -> cellData.getValue().getTipo().nombreProperty());
        tabcolSaldo.setCellValueFactory(cellData -> cellData.getValue().saldoAcumuladoProperty().asObject());
        tabcolSaldo.setCellFactory(column -> {
                return new TableCell<Centro, Double>() {
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
        filteredData = new FilteredList(FXCollections.observableArrayList(centroDAO.listarPeriodo(menuControlador.periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getTipo().getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
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
    
    @FXML void lnkCentrosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }
    
    @FXML void btnAgregarAction(ActionEvent event) {
        Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.stream().filter(item -> "CECO".equals(item.getCodigo())).findFirst().orElse(null);
        menuControlador.codigos = tabListar.getItems().stream().map(i -> "'"+i.getCodigo()+"'").collect(Collectors.joining (","));
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_ENTIDAD.getVista()));
            BuscarEntidadControlador buscarEntidadControlador = new BuscarEntidadControlador(menuControlador, this, tipoSeleccionado, -1, menuControlador.repartoTipo);
            fxmlLoader.setController(buscarEntidadControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(String.format("Agregar %s para el periodo de %s de %d",titulo2,cmbMes.getValue(),spAnho.getValue()));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        EntidadDistribucion item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.mensaje.delete_selected_error(titulo);
            return;
        }
       
        if (!menuControlador.navegador.mensajeConfirmar("Quitar " + titulo2, "¿Está seguro de quitar el " + titulo2 + " " + item.getNombre() + "?"))
            return;        
        
        if(centroDAO.verificarObjetoEnDetalleGasto(item.getCodigo(),menuControlador.periodoSeleccionado,menuControlador.repartoTipo)==0 && driverLineaDAO.verificarObjetoEnDriver(item.getCodigo(), menuControlador.periodoSeleccionado, menuControlador.repartoTipo)==0 ){
            centroDAO.eliminarObjetoPeriodo(item.getCodigo(), menuControlador.periodoSeleccionado, menuControlador.repartoTipo);
            menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), item.getCodigo(),menuControlador.periodoSeleccionado,Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO.getDireccion());
            buscarPeriodo(menuControlador.periodoSeleccionado);
        } else {
            menuControlador.mensaje.delete_item_periodo_error(titulo);
        }        
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO_CARGAR);
    }
    
    @FXML void btnCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
    
    private void buscarPeriodo(int periodo) {
        List<Centro> lista = centroDAO.listarPeriodo(periodo, menuControlador.repartoTipo);
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                if(menuControlador.repartoTipo ==1){
                    descargaFile = new DescargaServicio("CentrosDeCostos", tabListar);
                descargaFile.descargarTabla(Integer.toString(menuControlador.periodoSeleccionado),directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, menuControlador.periodoSeleccionado,Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO.getDireccion());
                } else {
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
        centroDAO.insertarObjetoPeriodo(entidad.getCodigo(), menuControlador.periodoSeleccionado, menuControlador.repartoTipo);
        menuControlador.Log.agregarItemPeriodo(LOGGER,menuControlador.usuario.getUsername(), entidad.getCodigo(),menuControlador.periodoSeleccionado, Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO.getDireccion());
        buscarPeriodo(menuControlador.periodoSeleccionado);
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
