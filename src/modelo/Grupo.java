package modelo;

import java.util.Date;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Grupo extends EntidadDistribucion {
    final IntegerProperty nivel;
    Tipo tipo;
    Grupo grupoPadre;
    List<CuentaContable> listaPlanDeCuentas;
    
    public Grupo(String codigo, String nombre, String descripcion, double saldoAcumulado, List<CuentaContable> listaPlanDeCuentas, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        this.listaPlanDeCuentas = listaPlanDeCuentas;
        nivel = new SimpleIntegerProperty(0);
    }
    
    public List<CuentaContable> getListaPlanDeCuentas() {
        return listaPlanDeCuentas;
    }

    public void setListaPlanDeCuentas(List<CuentaContable> listaPlanDeCuentas) {
        this.listaPlanDeCuentas = listaPlanDeCuentas;
    }
    
    public Grupo getGrupoPadre() {
        return grupoPadre;
    }

    public void setGrupoPadre(Grupo grupoPadre) {
        this.grupoPadre = grupoPadre;
    }
    
    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
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
}
