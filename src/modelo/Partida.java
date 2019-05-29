/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.Date;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Partida extends EntidadDistribucion{
    final IntegerProperty nivel;
    Tipo tipo;
    Partida partidaPadre;
    final BooleanProperty flagCargar;
    // Datos de la CuentaContable asociado a la Partida
    private Tipo cuentaContable;
    
    public Partida(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        nivel = new SimpleIntegerProperty(0);
        this.flagCargar = new SimpleBooleanProperty();
    }
    public Partida(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion, boolean flagCargar) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        nivel = new SimpleIntegerProperty(0);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    public Partida(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion, Tipo cuentaContable) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        nivel = new SimpleIntegerProperty(0);
        this.cuentaContable = cuentaContable;
        this.flagCargar = new SimpleBooleanProperty();
    }
    
//    public List<CuentaContable> getListaPlanDeCuentas() {
//        return listaPlanDeCuentas;
//    }
//
//    public void setListaPlanDeCuentas(List<CuentaContable> listaPlanDeCuentas) {
//        this.listaPlanDeCuentas = listaPlanDeCuentas;
//    }
    
    public Tipo getCuentaContable() {
        return cuentaContable;
    }

    public void setGrupo(Tipo cuentaContable) {
        this.cuentaContable = cuentaContable;
    }
    
    public Partida getPartidaPadre() {
        return partidaPadre;
    }

    public void setPartidaPadre(Partida partidaPadre) {
        this.partidaPadre = partidaPadre;
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
    
    
    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(boolean nivel) {
        this.flagCargar.set(nivel);
    }
}
