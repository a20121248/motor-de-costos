package modelo;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;


public class Mensaje {
    
    public void delete_item_maestro_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eliminar " + titulo);
        alert.setContentText("No se pudo eliminar "+ titulo +" item del Catálogo. Está siendo utilizada en periodo");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void delete_item_periodo_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eliminar " + titulo);
        alert.setContentText("No se pudo eliminar "+ titulo +" item del Periodo. Está siendo utilizada en otros módulos.\nPara eliminarla, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizada.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void delete_success(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Eliminar " + titulo);
        alert.setContentText( titulo +" eliminado correctamente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void delete_selected_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eliminar " + titulo);
        alert.setContentText("Por favor seleccione "+ titulo +"." );
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void delete_refresh_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eliminar " + titulo);
        alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void edit_empty_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Editar "+ titulo);
        alert.setContentText("Por favor seleccione item.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void download_empty(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Descargar información");
        alert.setContentText("No hay información");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void download_canceled(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Descargar información");
        alert.setContentText("Descarga Cancelada");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void edit_success(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Editar "+ titulo);
        alert.setContentText(titulo +" editada correctamente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void edit_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Editar "+ titulo);
        alert.setContentText("No se pudo editar " + titulo +".");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void create_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Crear "+ titulo);
        alert.setContentText("No se pudo crear " + titulo +".");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void create_porcent_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Crear "+ titulo);
        alert.setContentText("Los porcentajes de distribución deben sumar 100%.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void create_text_empty(String titulo,String titulo2){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Crear "+ titulo);
        alert.setContentText("Por favor ingrese "+ titulo2 + "  para el "+ titulo +".");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void create_driver_centro_empty(String titulo, String titulo2){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Crear "+ titulo);
        alert.setContentText("No se pudo crear " + titulo +". Por favor ingrese como mínimo un " + titulo2 + " a Distribuir.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void create_success(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Crear "+ titulo);
        alert.setContentText(titulo +" creado correctamente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void create_pattern_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Crear "+ titulo);
        alert.setContentText("El código de " + titulo + " no cumple con el patrón establecido.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void create_exist_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Crear "+ titulo);
        alert.setContentText("El código de " + titulo + " ya existe. No se pudo crear el item.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_header_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Cargar " + titulo);
        alert.setContentText("La cabecera de la hoja no es la correcta.\nNo se puede cargar el archivo.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_empty(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Subir información");
        alert.setContentText("No hay información.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_allCharged_now(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Subida de información " + titulo);
        alert.setContentText("Toda La información ya está cargada.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_success_with_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Subida de información " + titulo);
        alert.setContentText(titulo + " subidas. Se presentaron algunos errores. \nPara mayor información Descargar LOG.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_success(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Subir información");
        alert.setContentText("La información se subió correctamente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_periodo_fail_error(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Subir información");
        alert.setContentText("Presenta inconsistencia con el Periodo a cargar. Por favor, revise el documento a cargar.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_sheet_dont_exist_error(String titulo, String sheetName){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Cargar "+ titulo);
        alert.setContentText("La hoja de trabajo " + sheetName + " no existe.\nNo se puede cargar el archivo.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void upload_title_sheet_error(String titulo, String sheetName, String sheetTitle){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Cargar "+ titulo);
        alert.setContentText("El título de la hoja " + sheetName + " no es la correcta, deber ser " + sheetTitle + ".\n No se puede cargar el archivo.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void add_refresh_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Agregar " + titulo);
        alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void add_empty(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Agregar "+ titulo);
        alert.setContentText("Por favor seleccione item.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void show_table_empty(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Consulta "+ titulo);
        alert.setContentText("No existen "+ titulo+ " para el periodo seleccionado.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void update_refresh_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Actualizar "+ titulo);
        alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void update_empty(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualizar "+ titulo);
        alert.setContentText("Por favor seleccione item.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void update_item_without_allocate(String titulo){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Actualizar "+ titulo);
        alert.setContentText("No se puede actualizar item " + titulo +", pues no ha sido asignado previamente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void edit_porcent_error(String titulo){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Editar "+ titulo);
        alert.setContentText("Los porcentajes de distribución deben sumar 100%.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void execute_phase_currently_error(int nroPhase){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ejecutar FASE "+ nroPhase);
        alert.setContentText("La fase se está ejecutando actualmente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void execute_asign_without_driver_singular_error(int nroPhase, int nroSinDriver){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ejecutar FASE "+ nroPhase);
        alert.setContentText("Existe "+ nroSinDriver +" asociación sin Driver asignado.\n\nPor favor, revise el módulo de Asignaciones de Driver y asegúrese que esté asignado correctamente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void execute_asign_without_driver_plural_error(int nroPhase, int nroSinDriver){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ejecutar FASE "+ nroPhase);
        alert.setContentText("Existen "+ nroSinDriver +" asociaciones sin Driver asignado.\n\nPor favor, revise el módulo de Asignaciones de Driver y asegúrese que estén asignados correctamente.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void execute_asign_bad_driver_error(int nroPhase, String detail){
        String mensaje = "Las siguientes asociaciones con Driver presentan inconsistencia.\r\nPor favor, revise el módulo de Asignaciones de Driver y asegúrese que estén asignados correctamente.\r\n\n";
        mensaje += detail;
        TextArea textArea = new TextArea(mensaje);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ejecutar FASE "+ nroPhase);
//        alert.setContentText(mensaje);
        alert.getDialogPane().setContent(gridPane);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void execute_report_error(String title, String detail){
        String mensaje = String.format("Se ejecutó el reporte %s\n\n", title);
        mensaje += detail;
        TextArea textArea = new TextArea(mensaje);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.getDialogPane().setContent(gridPane);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void execute_report_success(String title, String mensaje){
        TextArea textArea = new TextArea(mensaje);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.getDialogPane().setContent(gridPane);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    public void execute_close_process_empty_error(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ejecutar Cierre de Proceso ");
        alert.setContentText("No hay información de la distribución del presente periodo. Por favor, ejecute las fase 1, 2 y 3.");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
