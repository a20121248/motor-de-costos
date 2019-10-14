package modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarCuentaPartidaLinea {
    private IntegerProperty periodo;
    private StringProperty codigoPartida;
    private StringProperty nombrePartida;
    private StringProperty codigoCuentaContable;
    private StringProperty nombreCuentaContable;
    private BooleanProperty flagCargar;

    
    public CargarCuentaPartidaLinea(int periodo, String codigoCuentaContable, String nombreCuentaContable, String codigoPartida, String nombrePartida, boolean flagCargar) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCuentaContable = new SimpleStringProperty(codigoCuentaContable);
        this.nombreCuentaContable = new SimpleStringProperty(nombreCuentaContable);
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

    public StringProperty codigoPartidaProperty() {
        return codigoPartida;
    }
    
    public String getCodigoPartida() {
        return codigoPartida.get();
    }

    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida.set(codigoPartida);
    }
    
    public StringProperty nombrePartidaProperty() {
        return nombrePartida;
    }
    
    public String getNombrePartida() {
        return nombrePartida.get();
    }

    public void setNombrePartida(String nombrePlanDeCuenta) {
        this.nombrePartida.set(nombrePlanDeCuenta);
    }

    public StringProperty codigoCuentaContableProperty() {
        return codigoCuentaContable;
    }

    public String getCodigoCuentaContable() {
        return codigoCuentaContable.get();
    }

    public void setCodigoCuentaContable(String codigoCuentaContable) {
        this.codigoCuentaContable.set(codigoCuentaContable);
    }
    
    public StringProperty nombreCuentaContableProperty() {
        return nombreCuentaContable;
    }

    public String getNombreCuentaContable() {
        return nombreCuentaContable.get();
    }

    public void setNombreCuentaContable(String nombreCuentaContable) {
        this.nombreCuentaContable.set(nombreCuentaContable);
    }
    
    public Boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(Boolean flagCargar) {
        this.flagCargar.set(flagCargar);
    }
}
