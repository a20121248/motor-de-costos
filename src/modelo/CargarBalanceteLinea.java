package modelo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarBalanceteLinea {
    private IntegerProperty periodo;
    private StringProperty codigo;
    private StringProperty nombre;
    private DoubleProperty saldo;
    
    public CargarBalanceteLinea(int periodo, String codigo, String nombre, double saldo) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.saldo = new SimpleDoubleProperty(saldo);
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

    public DoubleProperty saldoProperty() {
        return saldo;
    }
    
    public double getSaldo() {
        return saldo.get();
    }

    public void setSaldo(double saldo) {
        this.saldo.set(saldo);
    }
}
