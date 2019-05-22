package controlador.modals;

import controlador.MenuControlador;
import controlador.ObjetoControladorInterfaz;
import dao.BancaDAO;
import dao.CentroDAO;
import dao.GrupoDAO;
import dao.OficinaDAO;
import dao.PlanDeCuentaDAO;
import dao.ProductoDAO;
import dao.ObjetoGrupoDAO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.EntidadDistribucion;
import modelo.Tipo;

public class BuscarEntidadControlador implements Initializable {    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Tipo> cmbTipo;
    
    @FXML private TableView<EntidadDistribucion> tabEntidades;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolCodigo;
    @FXML private TableColumn<EntidadDistribucion, String> tabcolNombre;
    
    @FXML private Label lblCantResultados;
    @FXML private Button btnSeleccionar;
    
    //EntidadDistribucionDAO entidadDistribucionDAO;
    PlanDeCuentaDAO planDeCuentaDAO;
    GrupoDAO grupoDAO;
    CentroDAO centroDAO;
    OficinaDAO oficinaDAO;
    BancaDAO bancaDAO;
    ProductoDAO productoDAO;
    ObjetoGrupoDAO objetoGrupoDAO;
    MenuControlador menuControlador;
    ObjetoControladorInterfaz objetoControlador;
    List<Tipo> lstTipos;
    Tipo tipoSeleccionado;
    FilteredList<EntidadDistribucion> filteredData;
    SortedList<EntidadDistribucion> sortedData;
    int periodoSeleccionado, repartoTipo;
    
    public BuscarEntidadControlador(MenuControlador menuControlador, ObjetoControladorInterfaz objetoControlador, Tipo tipoSeleccionado, int periodoSeleccionado, int repartoTipo) {
        this.menuControlador = menuControlador;
        this.objetoControlador = objetoControlador;
        this.tipoSeleccionado = tipoSeleccionado;
        this.periodoSeleccionado = periodoSeleccionado;
        this.repartoTipo = repartoTipo;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        grupoDAO = new GrupoDAO();
        centroDAO = new CentroDAO();
        bancaDAO = new BancaDAO();
        oficinaDAO = new OficinaDAO();
        productoDAO = new ProductoDAO();
        if (menuControlador.objetoTipo != null)
            objetoGrupoDAO = new ObjetoGrupoDAO(menuControlador.objetoTipo);
        lstTipos = menuControlador.lstEntidadTipos;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<? extends EntidadDistribucion> lista = null;
        try {
            switch (tipoSeleccionado.getCodigo()) {
                case "CTA": // Cuenta Contable
                    if (periodoSeleccionado==-1) lista = planDeCuentaDAO.listarMaestro(menuControlador.codigos, repartoTipo);
                    else lista = planDeCuentaDAO.listar(periodoSeleccionado,"Todos", repartoTipo);
                    break;
                case "GCTA": // Grupo de Cuentas Contables
                    //lista = planDeCuentaDAO.listarGruposNombres(periodoSeleccionado,repartoTipo);
                    if (periodoSeleccionado==-1) lista = grupoDAO.listarObjetos(menuControlador.codigos, repartoTipo);
                    else lista = grupoDAO.listar(periodoSeleccionado,"Todos", repartoTipo);
                    break;
                case "CECO": // Centro de Costo
                    if (periodoSeleccionado==-1) lista = centroDAO.listarMaestro(menuControlador.codigos, repartoTipo);
                    else lista = centroDAO.listar(periodoSeleccionado, repartoTipo);
                    break;
                case "BAN": // Banca
                    if (periodoSeleccionado==-1) lista = bancaDAO.listarMaestro(menuControlador.codigos);
                    else lista = bancaDAO.listar(periodoSeleccionado);
                    break;
                case "OFI": // Oficina
                    if (periodoSeleccionado==-1) lista = oficinaDAO.listarMaestro(menuControlador.codigos);
                    else lista = oficinaDAO.listar(periodoSeleccionado);
                    break;
                case "PRO": // Producto
                    if (periodoSeleccionado==-1) lista = productoDAO.listarMaestro(menuControlador.codigos);
                    else lista = productoDAO.listar(periodoSeleccionado);
                    break;
                case "GOFI": // Grupo de Productos
                    lista = objetoGrupoDAO.listarObjetos(periodoSeleccionado);
                    break;
                case "GBAN": // Grupo de Productos
                    lista = objetoGrupoDAO.listarObjetos(periodoSeleccionado);
                    break;
                case "GPRO": // Grupo de Productos
                    lista = objetoGrupoDAO.listarObjetos(periodoSeleccionado);
                    break;
                default:
                    lista = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        // tabla: formato
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // tabla: dimensiones
        tabEntidades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 80);
        // Tabla: filtro
        filteredData = new FilteredList(FXCollections.observableArrayList(lista), p -> true);
        txtCodigo.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                String codigoFiltrar = newValue.toLowerCase();
                String nombreFiltrar = txtNombre.getText().toLowerCase();
                return item.getCodigo().toLowerCase().contains(codigoFiltrar) && item.getNombre().toLowerCase().contains(nombreFiltrar);
            });
            lblCantResultados.setText("Número de registros: " + filteredData.size());
        });
        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                String codigoFiltrar = txtCodigo.getText().toLowerCase();
                String nombreFiltrar = newValue.toLowerCase();
                return item.getCodigo().toLowerCase().contains(codigoFiltrar) && item.getNombre().toLowerCase().contains(nombreFiltrar);
            });
            lblCantResultados.setText("Número de registros: " + filteredData.size());
        });
        sortedData = new SortedList(filteredData);
        sortedData.comparatorProperty().bind(tabEntidades.comparatorProperty());
        tabEntidades.setItems(sortedData);
        lblCantResultados.setText("Número de registros: " + sortedData.size());
        
        cmbTipo.setItems(FXCollections.observableList(lstTipos));
        cmbTipo.setConverter(new StringConverter<Tipo>() {
            @Override
            public String toString(Tipo object) {
                return object.getNombre();
            }            
            @Override
            public Tipo fromString(String string) {
                return cmbTipo.getItems().stream().filter(ap -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
        cmbTipo.getSelectionModel().select(tipoSeleccionado);
    }

    @FXML void btnSeleccionarAction(ActionEvent event) {
        try {
            EntidadDistribucion entidadSeleccionada = tabEntidades.getSelectionModel().getSelectedItem();            
            if (entidadSeleccionada == null) {
                menuControlador.navegador.mensajeInformativo(menuControlador.MENSAJE_SELECT_ENTITY);
            } else {
                objetoControlador.seleccionarEntidad(entidadSeleccionada);
                ((Stage) btnSeleccionar.getScene().getWindow()).close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
