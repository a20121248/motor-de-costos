package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarEntidadDriver {
    final IntegerProperty periodo;
    final StringProperty codigoEntidad;
    final StringProperty nombreEntidad;
    final StringProperty codigoDriver;
    final StringProperty nombreDriver;
    
    public CargarEntidadDriver(int periodo, String codigoEntidad, String nombreEntidad, String codigoDriver, String nombreDriver) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoEntidad = new SimpleStringProperty(codigoEntidad);
        this.nombreEntidad = new SimpleStringProperty(nombreEntidad);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
    }
    
    public IntegerProperty periodoProperty() {
        return periodo;
    }
    
    public int getPeriodo() {
        return periodo.get();
    }

    public void setPeriodo(int periodo) {
        this.periodo.set(periodo);
    }
    
    public StringProperty codigoEntidadProperty() {
        return codigoEntidad;
    }

    public String getCodigoEntidad() {
        return codigoEntidad.get();
    }

    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad.set(codigoEntidad);
    }
    
    public StringProperty nombreEntidadProperty() {
        return nombreEntidad;
    }

    public String getNombreEntidad() {
        return codigoEntidad.get();
    }

    public void setNombreEntidad(String nombreEntidad) {
        this.nombreEntidad.set(nombreEntidad);
    }
    
    public StringProperty codigoDriverProperty() {
        return codigoDriver;
    }

    public String getCodigoDriver() {
        return codigoDriver.get();
    }

    public void setCodigoDriver(String codigoDriver) {
        this.codigoDriver.set(codigoDriver);
    }
    
    public StringProperty nombreDriverProperty() {
        return nombreDriver;
    }

    public String getNombreDriver() {
        return nombreDriver.get();
    }

    public void setNombreDriver(String nombreDriver) {
        this.nombreDriver.set(nombreDriver);
    }
}
