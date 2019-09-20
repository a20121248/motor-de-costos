package modelo;

import javafx.scene.control.Alert;


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
}
