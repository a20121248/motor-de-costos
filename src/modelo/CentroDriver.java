package modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CentroDriver {
    private IntegerProperty periodo;
    private StringProperty codigoCuenta;
    private StringProperty nombreCuenta;
    private StringProperty codigoPartida;
    private StringProperty nombrePartida;
    private StringProperty codigoCentro;
    private StringProperty nombreCentro;
    private StringProperty codigoDriver;
    private StringProperty nombreDriver;
    private BooleanProperty flagCargar;
    
    public CentroDriver(int periodo, String codigoCentro, String nombreCentro, String codigoDriver, String nombreDriver) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
    }
    
    public CentroDriver(int periodo, String codigoCuenta, String nombreCuenta, String codigoPartida, String nombrePartida,String codigoCentro, String nombreCentro, String codigoDriver, String nombreDriver) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuenta = new SimpleStringProperty(codigoCuenta);
        this.nombreCuenta = new SimpleStringProperty(nombreCuenta);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
    }
    
    public CentroDriver(int periodo, String codigoCuenta, String nombreCuenta, String codigoPartida, String nombrePartida,String codigoCentro, String nombreCentro, String codigoDriver, String nombreDriver, boolean flagCargar) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuenta = new SimpleStringProperty(codigoCuenta);
        this.nombreCuenta = new SimpleStringProperty(nombreCuenta);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
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
    
    public StringProperty codigoCentroProperty() {
        return codigoCentro;
    }

    public String getCodigoCentro() {
        return codigoCentro.get();
    }

    public void setCodigoCentro(String codigoEntidad) {
        this.codigoCentro.set(codigoEntidad);
    }
    
    public StringProperty nombreCentroProperty() {
        return nombreCentro;
    }

    public String getNombreCentro() {
        return nombreCentro.get();
    }

    public void setNombreCentro(String nombreEntidad) {
        this.nombreCentro.set(nombreEntidad);
    }
    
    public StringProperty codigoCuentaProperty() {
        return codigoCuenta;
    }

    public String getCodigoCuenta() {
        return codigoCuenta.get();
    }

    public void setCodigoCuenta(String codigoEntidad) {
        this.codigoCuenta.set(codigoEntidad);
    }
    
    public StringProperty nombreCuentaProperty() {
        return nombreCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta.get();
    }

    public void setNombreCuenta(String nombreEntidad) {
        this.nombreCuenta.set(nombreEntidad);
    }
    
    public StringProperty codigoPartidaProperty() {
        return codigoPartida;
    }

    public String getCodigoPartida() {
        return codigoPartida.get();
    }

    public void setCodigoPartida(String codigoEntidad) {
        this.codigoPartida.set(codigoEntidad);
    }
    
    public StringProperty nombrePartidaProperty() {
        return nombrePartida;
    }

    public String getNombrePartida() {
        return nombrePartida.get();
    }

    public void setNombrePartida(String nombreEntidad) {
        this.nombrePartida.set(nombreEntidad);
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
    
    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(boolean nivel) {
        this.flagCargar.set(nivel);
    }
}
