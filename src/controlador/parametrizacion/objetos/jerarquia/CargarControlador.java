package controlador.parametrizacion.objetos.jerarquia;

import com.jfoenix.controls.JFXButton;
import controlador.MenuControlador;
import controlador.Navegador;
import dao.ObjetoGrupoDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modelo.Grupo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CargarControlador implements Initializable {
    // Variables de la vista
    @FXML private Label lblTitulo;
    @FXML private Hyperlink lnkInicio;
    @FXML private Hyperlink lnkParametrizacion;
    @FXML private Hyperlink lnkObjetos;
    @FXML private Hyperlink lnkJerarquia;
    @FXML private Hyperlink lnkCargar;
    
    @FXML private ComboBox<String> cmbMes;
    @FXML private Spinner<Integer> spAnho;
    @FXML private TextField txtRuta;
    @FXML private JFXButton btnCargarRuta;
    
    @FXML private TableView<Grupo> tabListar;
    @FXML private TableColumn<Grupo, String> tabcolCodigo;
    @FXML private TableColumn<Grupo, String> tabcolNombre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivel;
    @FXML private TableColumn<Grupo, String> tabcolCodigoPadre;
    @FXML private TableColumn<Grupo, String> tabcolNombrePadre;
    @FXML private TableColumn<Grupo, Integer> tabcolNivelPadre;
    @FXML private Label lblNumeroRegistros;
    
    @FXML private Button btnAtras;
    @FXML private Button btnSubir;
    
    // Variables de la aplicacion
    ObjetoGrupoDAO objetoGrupoDAO;
    public MenuControlador menuControlador;
    int periodoSeleccionado;
    final int anhoSeleccionado;
    final int mesSeleccionado;
    final static Logger LOGGER = Logger.getLogger(Navegador.RUTAS_OBJETOS_JERARQUIA_CARGAR.getControlador());

    public CargarControlador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
        objetoGrupoDAO = new ObjetoGrupoDAO();
        periodoSeleccionado = (int) menuControlador.objeto;
        anhoSeleccionado = periodoSeleccionado / 100;
        mesSeleccionado = periodoSeleccionado % 100;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        switch (menuControlador.objetoTipo) {
            case "OFI":
                lblTitulo.setText("Cargar Jerarquía de Oficinas");
                lnkObjetos.setText("Oficinas");
                break;
            case "BAN":
                lblTitulo.setText("Cargar Jerarquía de Bancas");
                lnkObjetos.setText("Bancas");
                break;
            case "PRO":
                lblTitulo.setText("Cargar Jerarquía de Productos");
                lnkObjetos.setText("Productos");
                break;
            default:
                break;
        }
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
        // meses
        cmbMes.getItems().addAll(menuControlador.lstMeses);
        cmbMes.getSelectionModel().select(mesSeleccionado-1);
        spAnho.getValueFactory().setValue(anhoSeleccionado);
        cmbMes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
        spAnho.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!oldValue.equals(newValue))
                periodoSeleccionado = spAnho.getValue()*100 + cmbMes.getSelectionModel().getSelectedIndex() + 1;
        });
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

    @FXML void lnkCargarAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA_CARGAR);
    }
    
    @FXML void btnCargarRutaAction(ActionEvent event) {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("Abrir asignaciones");
        /*file_chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx")
        );*/

        File archivoSeleccionado = file_chooser.showOpenDialog(btnCargarRuta.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRuta.setText(archivoSeleccionado.getAbsolutePath());
            List<Grupo> lista = leerArchivo(archivoSeleccionado.getAbsolutePath());
            tabListar.getItems().setAll(lista);
            lblNumeroRegistros.setText("Número de registros leídos: " + lista.size());
        }
    }
    
    private List<Grupo> leerArchivo(String rutaArchivo) {
        List<Grupo> lista = new ArrayList();
        try {
            FileInputStream f = new FileInputStream(rutaArchivo);
            XSSFWorkbook libro = new XSSFWorkbook(f);
            XSSFSheet hoja = libro.getSheetAt(0);

            Iterator<Row> filas = hoja.iterator();
            Iterator<Cell> celdas;
            Row fila;
            Cell celda;
            //int numFilasOmitir = 2
            //Estructura de la cabecera
            List<String> listaCabecera = new ArrayList(Arrays.asList("PERIODO","CODIGO","NOMBRE","NIVEL","CODIGO PADRE","NOMBRE PADRE","NIVEL PADRE"));
            int numFilaCabecera = 1;
            boolean archivoEstaBien = true;
            while (filas.hasNext() && archivoEstaBien) {
                /*for (int i = 0; i < numFilasOmitir; i++) {
                    filas.next();
                }*/
                fila = filas.next();
                celdas = fila.cellIterator();
                
                // valido la cabecera
                if (fila.getRowNum() == numFilaCabecera - 1) {
                    List<String> listaCabeceraLeida = new ArrayList();
                    while (celdas.hasNext()) {
                        celda = celdas.next();
                        listaCabeceraLeida.add(celda.getStringCellValue());
                    }
                    if (!listaCabecera.equals(listaCabeceraLeida)) {
                        menuControlador.navegador.mensajeInformativo("Lectura de archivo Excel", "El archivo seleccionado no es el correcto.");
                        tabListar.getItems().clear();
                        txtRuta.setText("");                        
                        archivoEstaBien = false;
                    }
                    continue;
                }
                
                // leemos una fila completa                
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int periodo = (int) celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigo = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int nivel = (int)celda.getNumericCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String codigoPadre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.STRING);String nombrePadre = celda.getStringCellValue();
                celda = celdas.next();celda.setCellType(CellType.NUMERIC);int nivelPadre = (int)celda.getNumericCellValue();

                Grupo itemPadre = new Grupo(codigoPadre, nombrePadre, null, 0, null, null, null);
                itemPadre.setNivel(nivelPadre);

                Grupo item = new Grupo(codigo, nombre, null, 0, null, null, null);
                item.setNivel(nivel);
                
                item.setGrupoPadre(itemPadre);
                lista.add(item);
            }
            //cerramos el libro
            f.close();
            libro.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,ex.getMessage());
        }
        return lista;
    }
    
    @FXML void btnAtrasAction(ActionEvent event) {
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA);
    }
    
    @FXML void btnSubirAction(ActionEvent event) throws SQLException {
        List<Grupo> lista = tabListar.getItems();
        objetoGrupoDAO.insertarListaAsignacion(periodoSeleccionado, menuControlador.objetoTipo, lista);
        menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Asignaciones subidas correctamente.");
        menuControlador.navegador.cambiarVista(Navegador.RUTAS_OBJETOS_JERARQUIA);
        /*for (CargarCentroLinea cargarCentroLinea : lista) {
            //planDeCuentaDAO.insertarSaldo(cargarBalanceteLinea);
            menuControlador.navegador.mensajeInformativo("Subida de archivo Excel", "Centros de Costos subidos correctamente.");
            Navegador.cambiarVista(Navegador.RUTAS_CENTROS_LISTAR);
        }*/
    }
}
