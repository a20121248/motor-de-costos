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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Partida extends EntidadDistribucion{
    
    private StringProperty grupoGasto;
    private BooleanProperty flagCargar;
    private Tipo cuentaContable;
    
    public Partida(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
    }
    
    public Partida(String codigo, String nombre, String descripcion, String grupoGasto, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        this.grupoGasto = new SimpleStringProperty(grupoGasto);
    }
    
    public Partida(String codigo, String nombre, String descripcion, String grupoGasto, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion, boolean flagCargar) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        this.grupoGasto = new SimpleStringProperty(grupoGasto);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    public Partida(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion, Tipo cuentaContable) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        this.cuentaContable = cuentaContable;
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

    public void setCuentaContable(Tipo cuentaContable) {
        this.cuentaContable = cuentaContable;
    }
    
//    public ObjectProperty<GrupoGasto> tipoProperty() {
//        return grupoaGasto;
//    }
//    
//    public GrupoGasto getTipo() {
//        return grupoaGasto.get();
//    }
//
//    public void setTipo(GrupoGasto grupoGasto) {
//        this.grupoaGasto.set(grupoGasto);
//    } 
    public StringProperty grupoGastoProperty() {
        return this.grupoGasto;
    }

    public String getGrupoGasto() {
        return grupoGasto.get();
    }

    public void setGrupoGasto(String grupoGasto) {
        this.grupoGasto.set(grupoGasto);
    }
    
    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(boolean nivel) {
        this.flagCargar.set(nivel);
    }
}
