package controlador.aprovisionamiento.balancete;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.PlanDeCuentaDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import modelo.CargarBalanceteLinea;
import modelo.CuentaContable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkAprovisionamiento;
    @FXML private Hyperlink lnkBalancete;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<CargarBalanceteLinea> tabListar;
    @FXML private TableColumn<CargarBalanceteLinea, Integer> tabcolPeriodo;
    @FXML private TableColumn<CargarBalanceteLinea, String> tabcolCodigo;
    @FXML private TableColumn<CargarBalanceteLinea, String> tabcolNombre;
    @FXML private TableColumn<CargarBalanceteLinea, Double> tabcolSaldo;
    
    @FXML private Button btnCancelar;
    @FXML private Button btnSubir;
    @FXML private Label lblNumeroCheck;
    @FXML private Label lblNumeroWarning;
    @FXML private Label lblNumeroError;    

    public MenuControlador menuControlador;
    public PlanDeCuentaDAO planDeCuentaDAO;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_BALANCETE_CARGAR.getControlador());
    
    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        planDeCuentaDAO = new PlanDeCuentaDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // tabla formato
        tabcolPeriodo.setCellValueFactory(cellData -> cellData.getValue().periodoProperty().asObject());
        tabcolCodigo.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        tabcolNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        tabcolSaldo.setCellValueFactory(cellData -> cellData.getValue().saldoProperty().asObject());
        tabcolSaldo.setCellFactory(column -> {
                return new TableCell<CargarBalanceteLinea, Double>() {
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
        // tabla dimensiones
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolPeriodo.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        tabcolSaldo.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
            }
        });
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
    
    // Acción de la pestaña 'Cargar'
    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_CARGAR);
    }
    
    // Acción del botón con ícono de folder
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Balancete");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            cmbMes.setDisable(true);
            spAnho.setDisable(true);
            List<CargarBalanceteLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath(), periodoSeleccionado);
            if (lista != null) {
                tabListar.getItems().setAll(lista);
                lblNumeroCheck.setText("Cuentas contables a cargar: " + lista.size());
            } else {
                txtRuta.setText("");
                cmbMes.setDisable(false);
                spAnho.setDisable(false);
            }
        }
    }
    
    private List<CargarBalanceteLinea> leerArchivo(String rutaArchivo, int periodo) {
        List<CargarBalanceteLinea> lista = new ArrayList();
        List<CargarBalanceteLinea> listaError = new ArrayList();
        List<CuentaContable> listaCuentas = planDeCuentaDAO.listar(periodo,"Todos",menuControlador.repartoTipo);
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(rutaArchivo));
            
            XSSFSheet sh = wb.getSheetAt(0);
            if (sh == null) {
                menuControlador.navegador.mensajeError("Cargar Balancete", "No existen hojas. No se puede cargar el archivo.");
                return null;
            }
            Iterator<Row> filas = sh.iterator();

            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO","NOMBRE","SALDO")))) {
                menuControlador.navegador.mensajeError("Cargar Balancete", "La cabecera de la hoja no es la correcta.\nNo se puede cargar el archivo.");
                return null;
            }
            
            Cell celda;
            while (filas.hasNext()) {
                Iterator<Cell> celdas = filas.next().cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodoLinea = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);double saldo = celda.getNumericCellValue();
                
                CargarBalanceteLinea cuentaLeida = new CargarBalanceteLinea(periodoLinea, codigo, nombre, saldo);                

                CuentaContable cuenta = listaCuentas.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                if (cuenta != null) {
                    lista.add(cuentaLeida);
                    listaCuentas.removeIf(x -> x.getCodigo().equals(cuenta.getCodigo()));
                } else {
                    listaError.add(cuentaLeida);                    
                }
            }
            wb.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        if (listaCuentas.size() > 0) {
            lblNumeroWarning.setText("Cuentas contables pendientes: " + listaCuentas.size());
        }
        if (listaError.size() > 0) {
            lblNumeroError.setText("Cuentas contables no encontradas: " + listaError.size());
        }
        return lista;
    }
    
    // Acción del botón 'Subir'
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        List<CargarBalanceteLinea> lista = tabListar.getItems();
        planDeCuentaDAO.insertarBalancete(periodoSeleccionado,lista);
        menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Balancete subido correctamente.");
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }
    
    // Acción del botón 'Cancelar'
    @FXML void btnCancelarAction(ActionEvent event) throws SQLException {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }   
}
