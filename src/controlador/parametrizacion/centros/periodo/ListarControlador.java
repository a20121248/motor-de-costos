package controlador.parametrizacion.centros.periodo;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.ObjetoControladorInterfaz;
import controlador.Navegador;
import controlador.modals.BuscarEntidadControlador;
import dao.CentroDAO;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    @FXML private Label lblTitulo;
    
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkCentros;
    @FXML private Hyperlink lnkAsignacion;
    
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
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnDescargar;
    @FXML private JFXButton btnAtras;
    
    // Variables de la aplicacion
    CentroDAO centroDAO;
    public MenuControlador menuControlador;
    FilteredList<EntidadDistribucion> filteredData;
    SortedList<EntidadDistribucion> sortedData;
    int periodoSeleccionado;
    boolean tablaEstaActualizada;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO.getControlador());
    String titulo1, titulo2;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        centroDAO = new CentroDAO();
        titulo1 = "Centros de Costos";
        titulo2 = "Centro de Costos";
        if (menuControlador.repartoTipo == 2) {
            titulo1 = "Centro de Beneficio";
            titulo2 = "Centro de Beneficio";
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        if (menuControlador.repartoTipo == 2) {
            lblTitulo.setText("Centros de Beneficio");
            lnkCentros.setText("Centros de Beneficio");
        }
        
        // Botones para periodo
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
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 80);
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(centroDAO.listar(periodoSeleccionado,menuControlador.repartoTipo)), p -> true);
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
    
    @FXML void lnkCentrosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO);
    }
    
    @FXML void btnAgregarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Agregar " + titulo2, "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
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
            stage.showAndWait();
        } catch(IOException e) {
            LOGGER.log(Level.INFO,e.getMessage());
        }
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Quitar " + titulo2, "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        EntidadDistribucion item = tabListar.getSelectionModel().getSelectedItem();
        if (item == null) {
            menuControlador.navegador.mensajeInformativo("Quitar " + titulo2, "Por favor seleccione un " + titulo2 + ".");
            return;
        }
       
        if (!menuControlador.navegador.mensajeConfirmar("Quitar " + titulo2, "¿Está seguro de quitar el " + titulo2 + " " + item.getNombre() + "?"))
            return;        
                
        centroDAO.eliminarObjetoPeriodo(item.getCodigo(), periodoSeleccionado);
        buscarPeriodo(periodoSeleccionado, false);
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_ASIGNAR_PERIODO_CARGAR);
    }
    
    @FXML void btnCatalogoAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_CENTROS_MAESTRO_LISTAR);
    }
    
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<Centro> lista = centroDAO.listar(periodo,menuControlador.repartoTipo);
        if (lista.isEmpty() && mostrarMensaje)
            menuControlador.navegador.mensajeInformativo("Consulta de " + titulo1, "No existen " + titulo1 + " para el periodo seleccionado.");
        txtBuscar.setText("");
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        sortedData = new SortedList(filteredData);
        tabListar.setItems(sortedData);
        lblNumeroRegistros.setText("Número de registros: " + filteredData.size());
        tablaEstaActualizada = true;
    }
    
    @FXML void btnDescargarAction(ActionEvent event) throws IOException{
        DescargaServicio descargaFile;
        if(!tabListar.getItems().isEmpty()){
            DirectoryChooser directory_chooser = new DirectoryChooser();
            directory_chooser.setTitle("Directorio a Descargar:");
            File directorioSeleccionado = directory_chooser.showDialog(btnDescargar.getScene().getWindow());
            if(directorioSeleccionado != null){
                descargaFile = new DescargaServicio("CentrosDeCostos", tabListar);
                descargaFile.DescargarTabla(Integer.toString(periodoSeleccionado),directorioSeleccionado.getAbsolutePath());
            }else{
                menuControlador.navegador.mensajeInformativo("Descargar Información", "Canceló la descarga");
            }
        }else{
            menuControlador.navegador.mensajeInformativo("Descargar Información", "No hay información.");
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_MODULO_PARAMETRIZACION);
    }
        
    @Override
    public void seleccionarEntidad(EntidadDistribucion entidad) {
        centroDAO.insertarObjetoPeriodo(entidad.getCodigo(), periodoSeleccionado);
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
