package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarCentroLinea {
    final StringProperty codigo;
    final StringProperty nombre;
    final StringProperty codigoGrupo;
    final StringProperty nombreGrupo;
    final IntegerProperty nivel;
    final StringProperty codigoCentroPadre;
    
    public CargarCentroLinea(String codigo, String nombre, String codigoGrupo, String nombreGrupo, int nivel, String codigoCentroPadre) {
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.codigoGrupo = new SimpleStringProperty(codigoGrupo);
        this.nombreGrupo = new SimpleStringProperty(nombreGrupo);
        this.nivel = new SimpleIntegerProperty(nivel);
        this.codigoCentroPadre= new SimpleStringProperty(codigoCentroPadre);
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

    public StringProperty codigoGrupoProperty() {
        return codigoGrupo;
    }
    
    public String getCodigoGrupo() {
        return codigoGrupo.get();
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo.set(codigoGrupo);
    }
    
    public StringProperty nombreGrupoProperty() {
        return nombreGrupo;
    }
    
    public String getNombreGrupo() {
        return nombreGrupo.get();
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo.set(nombreGrupo);
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

    public StringProperty codigoCentroPadreProperty() {
        return codigoCentroPadre;
    }
    
    public String getCodigoCentroPadre() {
        return codigoCentroPadre.get();
    }

    public void setCodigoCentroPadre(String codigoCentroPadre) {
        this.codigoCentroPadre.set(codigoCentroPadre);
    }
}
