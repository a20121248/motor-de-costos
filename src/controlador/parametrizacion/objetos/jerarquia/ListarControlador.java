package controlador.parametrizacion.objetos.jerarquia;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import controlador.ObjetoControladorInterfaz;
import controlador.modals.BuscarEntidadControlador;
import dao.ObjetoGrupoDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import modelo.DriverCentro;
import modelo.DriverObjeto;
import modelo.EntidadDistribucion;
import modelo.Grupo;
import modelo.Tipo;
import servicios.DescargaServicio;

public class ListarControlador implements Initializable,ObjetoControladorInterfaz {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkJerarquia;

    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private JFXButton btnBuscarPeriodo;
    
    @FXML private JFXButton btnAsignar;
    @FXML private JFXButton btnQuitar;
    @FXML private JFXButton btnCargar;
    @FXML private JFXButton btnAgrupacion;

    @FXML private TextField txtBuscar;
    @FXML private TableView<Grupo> tabListar;
    @FXML private TableColumn<Grupo, String> tabcolCodigo;
    @FXML private TableColumn<Grupo, String> tabcolNombre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivel;
    @FXML private TableColumn<Grupo, String> tabcolCodigoPadre;
    @FXML private TableColumn<Grupo, String> tabcolNombrePadre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivelPadre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private JFXButton btnAtras;
    @FXML private JFXButton btnDescargar;
    
    // Variables de la aplicacion
    ObjetoGrupoDAO objetoGrupoDAO;
    public MenuControlador menuControlador;
    Grupo grupoSeleccionado;
    FilteredList<Grupo> filteredData;
    SortedList<Grupo> sortedData;
    int periodoSeleccionado;
    boolean tablaEstaActualizada;
    String objetoNombre;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_JERARQUIA.getControlador());
    String titulo;
    
    public ListarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoGrupoDAO = new ObjetoGrupoDAO(menuControlador.objetoTipo);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Jerarquía de Oficinas");
                lnkObjetos.setText("Oficinas");
                objetoNombre = "Oficina";
                this.titulo = "Oficinas";
                break;
            case "BAN":
                lblTitulo.setText("Jerarquía de Bancas");
                lnkObjetos.setText("Bancas");
                objetoNombre = "Banca";
                this.titulo = "Bancas";
                break;
            case "PRO":
                lblTitulo.setText("Jerarquía de Productos");
                lnkObjetos.setText("Productos");
                objetoNombre = "Producto";
                this.titulo = "Productos";
                break;
            case "SCA":
                lblTitulo.setText("Jerarquía de Subcanales");
                lnkObjetos.setText("Subcanales");
                objetoNombre = "Subcanal";
                this.titulo = "Subcanales";
                break;
            default:
                break;
        }
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
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolNivel.setCellValueFactory(cellData -> cellData.getValue().nivelProperty().asObject());
        tabcolCodigoPadre.setCellValueFactory(cellData -> cellData.getValue().getGrupoPadre().codigoProperty());
        tabcolNombrePadre.setCellValueFactory(cellData -> cellData.getValue().getGrupoPadre().nombreProperty());
        tabcolNivelPadre.setCellValueFactory(cellData -> cellData.getValue().getGrupoPadre().nivelProperty().asObject());
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 12);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolNivel.setMaxWidth(1f * Integer.MAX_VALUE * 13);
        tabcolCodigoPadre.setMaxWidth(1f * Integer.MAX_VALUE * 12);
        tabcolNombrePadre.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolNivelPadre.setMaxWidth(1f * Integer.MAX_VALUE * 13);
        // Tabla: Buscar
        filteredData = new FilteredList(FXCollections.observableArrayList(objetoGrupoDAO.listarJerarquia(periodoSeleccionado)), p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (item.getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getGrupoPadre().getCodigo().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (item.getGrupoPadre().getNombre().toLowerCase().contains(lowerCaseFilter)) return true;
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

    @FXML void lnkObjetosAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }

    @FXML void lnkJerarquiaAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA);
    }
    
    @FXML void btnAsignarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Asignar Grupo", "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        grupoSeleccionado = tabListar.getSelectionModel().getSelectedItem();
        if (grupoSeleccionado == null) {
            menuControlador.navegador.mensajeInformativo("Asignar Grupo", "Por favor seleccione un Grupo.");
            return;
        }
        if (!grupoSeleccionado.getGrupoPadre().getCodigo().equals("SIN ASIGNAR")) {
            if (menuControlador.navegador.mensajeConfirmar("Asignar Grupo", "El Grupo ya cuenta con un Grupo Padre.\n¿Está seguro que desea reemplazar dicho Grupo?")) {
                buscarGrupo();
            }
            return;
        }
        buscarGrupo();
    }
    
    private void buscarGrupo() {
        //Tipo tipoSeleccionado = menuControlador.lstEntidadTipos.stream().filter(item -> "GPRO".equals(item.getCodigo())).findFirst().orElse(null);
        Tipo tipoSeleccionado = new Tipo("G"+menuControlador.objetoTipo,"Grupo de " + objetoNombre + "s",null);
        //menuControlador.codigos = tabListar.getItems().stream().map(i -> "'"+i.getCodigo()+"'").collect(Collectors.joining (","));
        menuControlador.codigos = "";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Navegador.RUTAS_MODALS_BUSCAR_ENTIDAD.getVista()));
            BuscarEntidadControlador buscarEntidadControlador = new BuscarEntidadControlador(menuControlador, this, tipoSeleccionado, grupoSeleccionado.getNivel(), -1);
            fxmlLoader.setController(buscarEntidadControlador);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(String.format("Asignar %s para el periodo de %s de %d",tipoSeleccionado.getNombre(),cmbMes.getValue(),spAnho.getValue()));
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch(IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
    }
    
    @FXML void btnQuitarAction(ActionEvent event) {
        if (!tablaEstaActualizada) {
            menuControlador.navegador.mensajeInformativo("Quitar asignación", "Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
            return;
        }
        
        grupoSeleccionado = tabListar.getSelectionModel().getSelectedItem();
        if (grupoSeleccionado == null) {
            menuControlador.navegador.mensajeInformativo("Quitar asignación", "Por favor seleccione una entidad.");
            return;
        }
        
        if (grupoSeleccionado.getGrupoPadre().getCodigo().equals("SIN ASIGNAR"))
            return;
        
        if (!menuControlador.navegador.mensajeConfirmar("Quitar asignación", "¿Está seguro de quitar el Grupo " + grupoSeleccionado.getGrupoPadre().getNombre() + "?"))
            return;
        
        objetoGrupoDAO.borrarGrupoPadre(grupoSeleccionado.getCodigo(), menuControlador.objetoTipo, periodoSeleccionado);
        menuControlador.Log.deleteItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), grupoSeleccionado.getGrupoPadre().getCodigo() +" del "+grupoSeleccionado.getCodigo(),periodoSeleccionado,Navegador.RUTAS_OBJETOS_JERARQUIA.getDireccion().replace("/Objetos/", "/"+titulo+"/"));
        buscarPeriodo(periodoSeleccionado, false);
    }
    
    @FXML void btnCargarAction(ActionEvent event) {
        menuControlador.objeto = periodoSeleccionado;
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA_CARGAR);
    }
    
    @FXML void btnAgrupacionAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_GRUPOS_LISTAR);
    }
    
    @FXML void btnBuscarPeriodoAction(ActionEvent event) {
        buscarPeriodo(periodoSeleccionado, true);
    }
    
    private void buscarPeriodo(int periodo, boolean mostrarMensaje) {
        List<Grupo> lista = objetoGrupoDAO.listarJerarquia(periodo);
        if (lista.isEmpty() && mostrarMensaje)
            menuControlador.navegador.mensajeInformativo("Consulta de Jerarquia", "No existe una Jerarquia de " + objetoNombre + "s para el periodo seleccionado.");
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
                descargaFile = new DescargaServicio(titulo + "-Jerarquía", tabListar);
                descargaFile.descargarTabla(Integer.toString(periodoSeleccionado),directorioSeleccionado.getAbsolutePath());
                menuControlador.Log.descargarTablaPeriodo(LOGGER, menuControlador.usuario.getUsername(), titulo, periodoSeleccionado,Navegador.RUTAS_OBJETOS_JERARQUIA.getDireccion().replace("/Objetos/", "/"+titulo+"/"));
            }else{
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_DOWNLOAD_CANCELED);
            }
        }else{
            menuControlador.navegador.mensajeError(menuControlador.MENSAJE_DOWNLOAD_EMPTY);
        }
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_PRINCIPAL);
    }
    
    @Override
    public void seleccionarEntidad(EntidadDistribucion entidad) {
        if (grupoSeleccionado == null) {
            menuControlador.navegador.mensajeInformativo("Asignar Grupo de " + objetoNombre + "s", "Por favor seleccione un Grupo de " + objetoNombre + "s.");
            return;
        }
        objetoGrupoDAO.insertarGrupoPadre(periodoSeleccionado,grupoSeleccionado.getCodigo(),menuControlador.objetoTipo,grupoSeleccionado.getNivel(),entidad.getCodigo());
        menuControlador.Log.agregarItemPeriodo(LOGGER, menuControlador.usuario.getUsername(), grupoSeleccionado.getGrupoPadre().getCodigo() +" al "+grupoSeleccionado.getCodigo(),periodoSeleccionado,Navegador.RUTAS_OBJETOS_ASIGNAR_PERIODO.getDireccion().replace("/Objetos/", "/"+objetoNombre+"/"));
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
