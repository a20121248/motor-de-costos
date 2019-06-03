package modelo;

import java.util.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Centro extends EntidadDistribucion {
    private IntegerProperty nivel;
    private ObjectProperty<Centro> centroPadre;
    private ObjectProperty<Tipo> tipo;
    private BooleanProperty flagCargar;
    private StringProperty esBolsa;
    private StringProperty atribuible;
    private StringProperty tipoGasto;
    private StringProperty claseGasto;
    
    public Centro(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, Tipo tipo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleObjectProperty(tipo);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, Tipo tipo, Date fechaCreacion, Date fechaActualizacion, Boolean flagCargar) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleObjectProperty(tipo);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, Tipo tipo, String esBolsa,  String atribuible, String tipoGasto, String claseGasto, Date fechaCreacion, Date fechaActualizacion, Boolean flagCargar) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleObjectProperty(tipo);
        this.esBolsa =  new SimpleStringProperty(esBolsa);
        this.atribuible = new SimpleStringProperty(atribuible);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
        this.claseGasto = new SimpleStringProperty(claseGasto);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, Tipo tipo, String esBolsa,  String atribuible, String tipoGasto, String claseGasto, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleObjectProperty(tipo);
        this.esBolsa =  new SimpleStringProperty(esBolsa);
        this.atribuible = new SimpleStringProperty(atribuible);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
        this.claseGasto = new SimpleStringProperty(claseGasto);
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
    
    public StringProperty esBolsaProperty() {
        return this.esBolsa;
    }

    public String getEsBolsa() {
        return esBolsa.get();
    }

    public void setEsBolsa(String esBolsa) {
        this.esBolsa.set(esBolsa);
    }
    
    public StringProperty atribuibleProperty() {
        return this.atribuible;
    }

    public String getAtribuible() {
        return atribuible.get();
    }

    public void setAtribuible(String atribuible) {
        this.atribuible.set(atribuible);
    }

    public StringProperty tipoGastoProperty() {
        return this.tipoGasto;
    }

    public String getTipoGasto() {
        return tipoGasto.get();
    }

    public void setTipoGasto(String tipoGasto) {
        this.tipoGasto.set(tipoGasto);
    }

    public StringProperty claseGastoProperty() {
        return this.claseGasto;
    }

    public String getClaseGasto() {
        return claseGasto.get();
    }

    public void setClaseGasto(String claseGasto) {
        this.claseGasto.set(claseGasto);
    }

    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(Boolean flagCargar) {
        this.flagCargar.set(flagCargar);
    }
}
