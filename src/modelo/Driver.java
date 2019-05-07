package modelo;

import java.util.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Driver {
    final StringProperty codigo;
    final StringProperty nombre;
    final StringProperty descripcion;
    final ObjectProperty<Tipo> tipo;
    final ObjectProperty<Date> fechaCreacion;
    final ObjectProperty<Date> fechaActualizacion;
    public BooleanProperty esNuevo;
    
    public Driver(String codigo, String nombre, String descripcion, Tipo tipo, Date fechaCreacion, Date fechaActualizacion) {
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.tipo = new SimpleObjectProperty(tipo);
        this.fechaCreacion = new SimpleObjectProperty(fechaCreacion);
        this.fechaActualizacion = new SimpleObjectProperty(fechaActualizacion);
        this.esNuevo = new SimpleBooleanProperty(false);
    }
    
    public StringProperty codigoProperty() {
        return codigo;
    }
    
    public String getCodigo() {
        return codigo.get();
    }

    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }
    
    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }
    
    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public BooleanProperty esNuevoProperty() {
        return esNuevo;
    }
    
    public boolean getEsNuevo() {
        return esNuevo.get();
    }

    public void setEsNuevo(boolean esNuevo) {
        this.esNuevo.set(esNuevo);
    }
    
    public ObjectProperty<Tipo> tipoProperty() {
        return tipo;
    }

    public Tipo getTipo() {
        return tipo.get();
    }

    public void setTipo(Tipo tipo) {
        this.tipo.set(tipo);
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
