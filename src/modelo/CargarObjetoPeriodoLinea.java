package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarObjetoPeriodoLinea {
    final IntegerProperty periodo;
    final StringProperty codigo;
    private StringProperty nombre;
    boolean flagCargar;
    
    public CargarObjetoPeriodoLinea(int periodo, String codigo) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigo = new SimpleStringProperty(codigo);
    }
    
    public CargarObjetoPeriodoLinea(int periodo, String codigo, String nombre) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
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
}
