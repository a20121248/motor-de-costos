/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.Date;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Partida extends EntidadDistribucion{
    
    private DoubleProperty saldo;
    final IntegerProperty nivel;
    private Tipo tipo;
    private Partida partidaPadre;
    // Datos de la CuentaContable asociado a la Partida
    private Tipo cuentaContable;
    final BooleanProperty flagCargar;
    
    public Partida(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        nivel = new SimpleIntegerProperty(0);
        this.saldo = new SimpleDoubleProperty(saldoAcumulado);
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
        this.saldo = new SimpleDoubleProperty(saldoAcumulado);
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

    @Override
    public DoubleProperty saldoAcumuladoProperty() {
        return saldoAcumulado;
    }
    
    @Override
    public double getSaldoAcumulado() {
        return saldoAcumulado.get();
    }

    @Override
    public void setSaldoAcumulado(double saldoAcumulado) {
        this.saldoAcumulado.set(saldoAcumulado);
    }
    
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
