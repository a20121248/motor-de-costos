package modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarObjetoLinea {
    private final StringProperty codigo;
    private final StringProperty nombre;
    private BooleanProperty flagCargar;
    
    public CargarObjetoLinea(String codigo, String nombre) {
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
    }
    
    public CargarObjetoLinea(String codigo, String nombre, Boolean flagCargar) {
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
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
    
    public Boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(Boolean flagCargar) {
        this.flagCargar.set(flagCargar);
    }
}
