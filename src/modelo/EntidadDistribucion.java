package modelo;

import java.time.LocalDate;
import java.util.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EntidadDistribucion {
    final StringProperty codigo;
    final StringProperty nombre;
    final StringProperty descripcion;
    final BooleanProperty estaActiva;
    final DoubleProperty saldoAcumulado;
    final ObjectProperty<Date> fechaCreacion;
    final ObjectProperty<Date> fechaActualizacion;
    final ObjectProperty<Driver> driver;
    boolean flagCargar;
    
    public EntidadDistribucion(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion, boolean estaActiva) {
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.saldoAcumulado = new SimpleDoubleProperty(saldoAcumulado);
        this.fechaCreacion = new SimpleObjectProperty(fechaCreacion);
        this.fechaActualizacion = new SimpleObjectProperty(fechaActualizacion);
        this.estaActiva = new SimpleBooleanProperty(estaActiva);
        this.driver = new SimpleObjectProperty(null);
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

    public DoubleProperty saldoAcumuladoProperty() {
        return saldoAcumulado;
    }
    
    public double getSaldoAcumulado() {
        return saldoAcumulado.get();
    }

    public void setSaldoAcumulado(double saldoAcumulado) {
        this.saldoAcumulado.set(saldoAcumulado);
    }
    
    public BooleanProperty estaActivaProperty() {
        return estaActiva;
    }
        
    public boolean getEstaActiva() {
        return estaActiva.get();
    }

    public void setEstaActiva(boolean estaActiva) {
        this.estaActiva.set(estaActiva);
    }
    
    public ObjectProperty<Driver> driverProperty() {
        return driver;
    }
    
    public Driver getDriver() {
        return driver.get();
    }

    public void setDriver(Driver driver) {
        this.driver.set(driver);
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
    
    public boolean getFlagCargar() {
        return flagCargar;
    }

    public void setFlagCargar(boolean flagCargar) {
        this.flagCargar = flagCargar;
    }
}
