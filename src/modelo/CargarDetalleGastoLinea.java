package modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarDetalleGastoLinea {
    private IntegerProperty periodo;
    private StringProperty codigoCuentaContable;
    private StringProperty nombreCuentaContable;
    private StringProperty codigoPartida;
    private StringProperty nombrePartida;
    private StringProperty codigoCECO;
    private StringProperty nombreCECO;
    private DoubleProperty saldo;
    private BooleanProperty estado;
    
    public CargarDetalleGastoLinea(int periodo, String codigoCuentaContable, String nombreCuentaContable, String codigoPartida, String nombrePartida, String codigoCECO, String nombreCECO, double saldo, boolean estado) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuentaContable = new SimpleStringProperty(codigoCuentaContable);
        this.nombreCuentaContable = new SimpleStringProperty(nombreCuentaContable);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCECO = new SimpleStringProperty(codigoCECO);
        this.nombreCECO = new SimpleStringProperty(nombreCECO);
        this.saldo = new SimpleDoubleProperty(saldo);
        this.estado = new SimpleBooleanProperty(estado);
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
    
    public StringProperty codigoCuentaContableProperty() {
        return codigoCuentaContable;
    } 

    public String getCodigoCuentaContable() {
        return codigoCuentaContable.get();
    }

    public void setCodigoCuentaContable(String codigo) {
        this.codigoCuentaContable.set(codigo);
    }
    
    public StringProperty nombreCuentaContableProperty() {
        return nombreCuentaContable;
    } 

    public String getNombreCuentaContable() {
        return nombreCuentaContable.get();
    }

    public void setNombreCuentaContable(String nombre) {
        this.nombreCuentaContable.set(nombre);
    }
    
    public StringProperty codigoPartidaProperty() {
        return codigoPartida;
    } 

    public String getCodigoPartida() {
        return codigoPartida.get();
    }

    public void setCodigoPartida(String codigo) {
        this.codigoPartida.set(codigo);
    }
    
    public StringProperty nombrePartidaProperty() {
        return nombrePartida;
    } 

    public String getNombrePartida() {
        return nombrePartida.get();
    }

    public void setNombrePartida(String nombre) {
        this.nombrePartida.set(nombre);
    }
    
    public StringProperty codigoCECOProperty() {
        return codigoCECO;
    } 

    public String getCodigoCECO() {
        return codigoCECO.get();
    }

    public void setCodigoCECO(String codigo) {
        this.codigoCECO.set(codigo);
    }
    
    public StringProperty nombreCECOProperty() {
        return nombreCECO;
    } 

    public String getNombreCECO() {
        return nombreCECO.get();
    }

    public void setNombreCECO(String nombre) {
        this.nombreCECO.set(nombre);
    }

    public DoubleProperty saldoProperty() {
        return saldo;
    }
    
    public double getSaldo() {
        return saldo.get();
    }

    public void setSaldo(double saldo) {
        this.saldo.set(saldo);
    }
    
    public BooleanProperty estadoProperty() {
        return estado;
    }
    
    public boolean getEstado() {
        return estado.get();
    }
    
    public void setEstado(Boolean estado){
        this.estado.set(estado);
    }
}
