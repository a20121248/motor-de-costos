package modelo;

import java.util.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DriverObjetoLinea {
    private Banca banca;
    private Oficina oficina;
    private Producto producto;
    private final DoubleProperty porcentaje;
    private final ObjectProperty<Date> fechaCreacion;
    private final ObjectProperty<Date> fechaActualizacion;
    
    public DriverObjetoLinea(Banca banca, Oficina oficina, Producto producto, double porcentaje, Date fechaCreacion, Date fechaActualizacion) {
        this.banca = banca;
        this.oficina = oficina;
        this.producto = producto;
        this.porcentaje = new SimpleDoubleProperty(porcentaje);
        this.fechaCreacion = new SimpleObjectProperty<>(fechaCreacion);
        this.fechaActualizacion = new SimpleObjectProperty<>(fechaActualizacion);
    }

    public Banca getBanca() {
        return banca;
    }

    public void setBanca(Banca banca) {
        this.banca = banca;
    }

    public Oficina getOficina() {
        return oficina;
    }

    public void setOficina(Oficina oficina) {
        this.oficina = oficina;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
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
        return fechaCreacion;
    }
    
    public Date getFechaActualizacion() {
        return fechaActualizacion.get();
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion.set(fechaActualizacion);
    }
}
