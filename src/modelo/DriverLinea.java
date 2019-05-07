package modelo;

import java.util.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DriverLinea {
    private EntidadDistribucion entidadDistribucionDestino;
    private DoubleProperty porcentaje;
    private ObjectProperty<Date> fechaCreacion;
    private ObjectProperty<Date> fechaActualizacion;
    
    public DriverLinea(EntidadDistribucion entidadDistribucionDestino, double porcentaje, Date fechaCreacion, Date fechaActualizacion) {
        this.entidadDistribucionDestino = entidadDistribucionDestino;
        this.porcentaje = new SimpleDoubleProperty(porcentaje);
        this.fechaCreacion = new SimpleObjectProperty<>(fechaCreacion);
        this.fechaActualizacion = new SimpleObjectProperty<>(fechaActualizacion);
    }

    public EntidadDistribucion getEntidadDistribucionDestino() {
        return entidadDistribucionDestino;
    }

    public void setEntidadDistribucionDestino(EntidadDistribucion entidadDistribucionDestino) {
        this.entidadDistribucionDestino = entidadDistribucionDestino;
    }

    public DoubleProperty porcentajeProperty() {
        return porcentaje;
    }
    
    public double getPorcentaje() {
        return porcentaje.get();
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje.set(porcentaje);
    }
    
    public ObjectProperty<Date> fechaCreacionProperty() {
        return fechaCreacion;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion.get();
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion.set(fechaCreacion);
    }
    
    public ObjectProperty<Date> fechaActualizacionProperty() {
        return fechaActualizacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion.get();
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion.set(fechaActualizacion);
    }
}
