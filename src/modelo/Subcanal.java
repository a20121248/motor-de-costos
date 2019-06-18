package modelo;

import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Subcanal extends EntidadDistribucion {
    public Subcanal(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
    }
}