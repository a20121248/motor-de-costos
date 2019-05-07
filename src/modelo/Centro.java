package modelo;

import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Centro extends EntidadDistribucion {
    private IntegerProperty nivel;
    private ObjectProperty<Centro> centroPadre;
    private ObjectProperty<Tipo> tipo;
    
    public Centro(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, Tipo tipo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleObjectProperty(tipo);
    }
    
    public IntegerProperty nivelProperty() {
        return nivel;
    }
    
    public int getNivel() {
        return nivel.get();
    }

    public void setNivel(int nivel) {
        this.nivel.set(nivel);
    }
    
    public ObjectProperty<Centro> centroPadreProperty() {
        return centroPadre;
    }

    public Centro getCentroPadre() {
        return centroPadre.get();
    }

    public void setCentroPadre(Centro centroPadre) {
        this.centroPadre.set(centroPadre);
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
}
