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
    private StringProperty tipo;
    private BooleanProperty flagCargar;
    private StringProperty niif17_atribuible;
    private StringProperty niif17_tipo;
    private StringProperty niif17_clase;
    private StringProperty tipoGasto;
    
    public Centro(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
    }
    
    //Objetos Centros para Listar en Periodo 
    public Centro(String codigo, String nombre, double saldo, String tipo) {
        super(codigo, nombre, null, saldo, null, null, true);
        this.tipo = new SimpleStringProperty(tipo);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, String tipo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleStringProperty(tipo);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, String tipo, Date fechaCreacion, Date fechaActualizacion, Boolean flagCargar) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleStringProperty(tipo);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, String tipo, String tipoGasto, String niif17_atribuible, String niif17_tipo, String niif17_clase, Date fechaCreacion, Date fechaActualizacion, Boolean flagCargar) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleStringProperty(tipo);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
        this.niif17_atribuible = new SimpleStringProperty(niif17_atribuible);
        this.niif17_tipo = new SimpleStringProperty(niif17_tipo);
        this.niif17_clase = new SimpleStringProperty(niif17_clase);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    public Centro(String codigo, String nombre, int nivel, Centro centroPadre, double saldo, String tipo, String niif17_atribuible, String niif17_tipo, String niif17_clase, String tipoGasto, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, null, saldo, fechaCreacion, fechaActualizacion, true);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.centroPadre = new SimpleObjectProperty(centroPadre);
        this.tipo = new SimpleStringProperty(tipo);
        this.niif17_atribuible = new SimpleStringProperty(niif17_atribuible);
        this.niif17_tipo = new SimpleStringProperty(niif17_tipo);
        this.niif17_clase = new SimpleStringProperty(niif17_clase);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
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
    
    public StringProperty tipoProperty() {
        return this.tipo;
    }

    public String getTipo() {
        return tipo.get();
    }

    public void setTipo(String tipo) {
        this.tipo.set(tipo);
    }    
    
    public StringProperty NIIF17atribuibleProperty() {
        return this.niif17_atribuible;
    }

    public String getNIIF17Atribuible() {
        return niif17_atribuible.get();
    }

    public void setNIIF17Atribuible(String atribuible) {
        this.niif17_atribuible.set(atribuible);
    }

    public StringProperty NIIF17TipoProperty() {
        return this.niif17_tipo;
    }

    public String getNIIF17Tipo() {
        return niif17_tipo.get();
    }

    public void setNIIF17Tipo(String tipoGasto) {
        this.niif17_tipo.set(tipoGasto);
    }

    public StringProperty NIIF17ClaseProperty() {
        return this.niif17_clase;
    }

    public String getNIIF17Clase() {
        return niif17_clase.get();
    }

    public void setNIIF17Clase(String claseGasto) {
        this.niif17_clase.set(claseGasto);
    }

    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(Boolean flagCargar) {
        this.flagCargar.set(flagCargar);
    }
    
    public StringProperty tipoGastoProperty() {
        return this.tipoGasto;
    }

    public String getTipoGasto() {
        return tipoGasto.get();
    }

    public void setTipoGastoDirecto(String tipoGasto) {
        this.tipoGasto.set(tipoGasto);
    }
}
