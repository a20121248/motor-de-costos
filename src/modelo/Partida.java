/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Partida extends EntidadDistribucion{
    
    private ObjectProperty<Tipo> grupoGasto;
    private BooleanProperty flagCargar;
    private Tipo cuentaContable;
    private StringProperty tipoGasto;
    
    public Partida(String codigo, String nombre, String descripcion, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
    }
    
    public Partida(String codigo, String nombre, String descripcion, String tipoGasto, Tipo grupoGasto, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        this.grupoGasto = new SimpleObjectProperty(grupoGasto);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
    }
    
    public Partida(String codigo, String nombre, String descripcion, Tipo grupoGasto, String tipoGasto, double saldoAcumulado, Date fechaCreacion, Date fechaActualizacion, boolean flagCargar) {
        super(codigo, nombre, descripcion, saldoAcumulado, fechaCreacion, fechaActualizacion, true);
        this.grupoGasto = new SimpleObjectProperty(grupoGasto);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    // Construcción para Listar ASignacion Cuenta-Partida
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
    public ObjectProperty<Tipo> grupoGastoProperty() {
        return this.grupoGasto;
    }

    public Tipo getGrupoGasto() {
        return grupoGasto.get();
    }

    public void setGrupoGasto(Tipo grupoGasto) {
        this.grupoGasto.set(grupoGasto);
    }
    
    @Override
    public boolean getFlagCargar() {
        return flagCargar.get();
    }
    @Override
    public void setFlagCargar(boolean nivel) {
        this.flagCargar.set(nivel);
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
