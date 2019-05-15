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
import javafx.scene.paint.Color;
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
    @FXML private TableColumn<CargarBalanceteLinea, Boolean> tabcolEstado;
    @FXML private TableColumn<CargarBalanceteLinea, String> tabcolCodigo;
    @FXML private TableColumn<CargarBalanceteLinea, String> tabcolNombre;
    @FXML private TableColumn<CargarBalanceteLinea, Double> tabcolSaldo;
    
    @FXML private Button btnCancelar;
    @FXML private Button btnSubir;
    @FXML private Label lblNumeroCheck;
    @FXML private Label lblNumeroWarning;
    @FXML private Label lblNumeroError;  
    
    List<CargarBalanceteLinea> listaCargar = new ArrayList() ;

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
//        tabcolPeriodo.setCellValueFactory(cellData -> cellData.getValue().periodoProperty().asObject());
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
//        Estado
        tabcolEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
        tabcolEstado.setCellFactory(column -> {
            return new TableCell<CargarBalanceteLinea, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        if (item) {
                            setText("CORRECTO");
                            setTextFill(Color.GREEN);
                        } else {
                            setText("INCORRECTO");
                            setTextFill(Color.RED);
                        }
                    }
                }
            };
        });
        // tabla dimensiones
        tabListar.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabcolCodigo.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        tabcolNombre.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        tabcolSaldo.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        tabcolEstado.setMaxWidth(1f * Integer.MAX_VALUE * 15);
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
    @FXML void btnCargarRutaAction(ActionEvent event) throws IOException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Balancete");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"));
        File archivoSeleccionado = fileChooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getName());
            List<CargarBalanceteLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            tabListar.getItems().setAll(lista);
//                lblNumeroCheck.setText("Cuentas contables a cargar: " + lista.size());
//            cmbMes.setDisable(true);
//            spAnho.setDisable(true);
//            List<CargarBalanceteLinea> lista = leerArchivo(archivoSeleccionado.getAbsolutePath(), periodoSeleccionado);
//            if (lista != null) {
//                tabListar.getItems().setAll(lista);
//                lblNumeroCheck.setText("Cuentas contables a cargar: " + lista.size());
//            } else {
//                txtRuta.setText("");
//                cmbMes.setDisable(false);
//                spAnho.setDisable(false);
//            }
        }
    }
    
    private List<CargarBalanceteLinea> leerArchivo(String rutaArchivo) {
        List<CargarBalanceteLinea> lista = new ArrayList();
        List<CargarBalanceteLinea> listaError = new ArrayList();
        List<CuentaContable> listaCuentas = planDeCuentaDAO.listar(periodoSeleccionado,"Todos",menuControlador.repartoTipo);
        try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(rutaArchivo));){
            XSSFSheet hoja = wb.getSheetAt(0);
//            if (hoja == null) {
//                menuControlador.navegador.mensajeError("Cargar Balancete", "No existen hojas. No se puede cargar el archivo.");
//                return null;
//            }
            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            if (!menuControlador.navegador.validarFila(filas.next(), new ArrayList(Arrays.asList("PERIODO","CODIGO","NOMBRE","SALDO")))) {
                menuControlador.navegador.mensajeError("Cargar Balancete", "La cabecera de la hoja no es la correcta.\nNo se puede cargar el archivo.");
                return null;
            }
            
            while (filas.hasNext()) {
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // leemos una fila completa
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);double saldo = celda.getNumericCellValue();
                
                // Valida que los items del archivo tengan el periodo correcto
                // De no cumplirlo, cancela la previsualización.
                if(periodo != periodoSeleccionado){
                    menuControlador.navegador.mensajeError("Carga de Información", "Presenta inconsistencia con el Periodo a cargar. Por favor, revise el documento a cargar.");
                    lista.clear();
                    listaError.clear();
                    listaCuentas.clear();
                    txtRuta.setText("");
                    break;
                }
                CargarBalanceteLinea cuentaLeida = new CargarBalanceteLinea(periodo, codigo, nombre, saldo,true);                
                
                // Verifica que exista la cuenta para poder agregarla
                CuentaContable cuenta = listaCuentas.stream().filter(item -> codigo.equals(item.getCodigo())).findAny().orElse(null);
                if (cuenta != null) {
                    listaCargar.add(cuentaLeida);
                    listaCuentas.removeIf(x -> x.getCodigo().equals(cuenta.getCodigo()));
                } else {
                    // >>>agregar linea para log sobre el error
                    cuentaLeida.setEstado(false);
                    listaError.add(cuentaLeida);                    
                }
                
                lista.add(cuentaLeida);
            }
            wb.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        lblNumeroCheck.setText("Cuentas posibles a cargar: " + (lista.size()-listaError.size()));
//
//        if (listaCuentas.size() > 0) {
//            lblNumeroWarning.setText("Cuentas contables pendientes: " + listaCuentas.size());
//        }
//        if (listaError.size() > 0) {
//            lblNumeroError.setText("Cuentas contables no encontradas: " + listaError.size());
//        }
        return lista;
    }
    
    // Acción del botón 'Subir'
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        if(listaCargar.isEmpty()){
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "No hay información.");
        }else {
            planDeCuentaDAO.insertarBalancete(periodoSeleccionado,listaCargar);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Balancete subido correctamente.");
            menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
        }
        
    }
    
    // Acción del botón 'Cancelar'
    @FXML void btnCancelarAction(ActionEvent event) throws SQLException {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_BALANCETE_LISTAR);
    }   
}
