package modelo;

import java.util.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CuentaContable extends EntidadDistribucion {
    private StringProperty grupoBalance;
    private StringProperty rubroOperativo;
    private StringProperty codPonderacion;
    private StringProperty situacionIVA;
    private StringProperty situacionIT;
    private StringProperty distribuyeCentroCosto;
    private StringProperty codProducto;
    private StringProperty descripcionProducto;
    private BooleanProperty flagCargar;
    private StringProperty tipoGasto;
    private StringProperty niif17Atribuible;
    private StringProperty niif17Tipo;
    private StringProperty niif17Clase;
    // datos del saldo
    private Tipo grupo;
    
    
    public CuentaContable(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
    }
    
    public CuentaContable(String codigo, String nombre, String descripcion, Date fechaCreacion, Date fechaActualizacion, double saldo, Tipo grupo, boolean estaActiva) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, estaActiva);
        this.grupo = grupo;
    }
    
    public CuentaContable(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion, boolean flagCargar) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    public CuentaContable(String codigo, String nombre, String descripcion, String tipoGasto, String niif17Atribuible, String niif17Tipo, String niif17Clase,  double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
        this.niif17Atribuible = new SimpleStringProperty(niif17Atribuible);
        this.niif17Tipo = new SimpleStringProperty(niif17Tipo);
        this.niif17Clase = new SimpleStringProperty(niif17Clase);
    }
    public CuentaContable(String codigo, String nombre, String descripcion, String tipoGasto, String niif17Atribuible, String niif17Tipo, String niif17Clase,  double saldo, Date fechaCreacion, Date fechaActualizacion, boolean  flagCargar) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
        this.tipoGasto = new SimpleStringProperty(tipoGasto);
        this.niif17Atribuible = new SimpleStringProperty(niif17Atribuible);
        this.niif17Tipo = new SimpleStringProperty(niif17Tipo);
        this.niif17Clase = new SimpleStringProperty(niif17Clase);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    public Tipo getGrupo() {
        return grupo;
    }

    public void setGrupo(Tipo grupo) {
        this.grupo = grupo;
    }
    
    //-------------------------------------------------
    public StringProperty grupoBalanceProperty() {
        return grupoBalance;
    }
    
    public String getGrupoBalance() {
        return grupoBalance.get();
    }

    public void setGrupoBalance(String grupoBalance) {
        this.grupoBalance.set(grupoBalance);
    }

    public StringProperty rubroOperativoProperty() {
        return rubroOperativo;
    }
    
    public String getRubroOperativo() {
        return rubroOperativo.get();
    }

    public void setRubroOperativo(String rubroOperativo) {
        this.rubroOperativo.set(rubroOperativo);
    }

    public StringProperty codPonderacionProperty() {
        return codPonderacion;
    }
    
    public String getCodPonderacion() {
        return codPonderacion.get();
    }

    public void setCodPonderacion(String codPonderacion) {
        this.codPonderacion.set(codPonderacion);
    }

    public StringProperty situacionIVAProperty() {
        return situacionIVA;
    }
    
    public String getSituacionIVA() {
        return situacionIVA.get();
    }

    public void setSituacionIVA(String situacionIVA) {
        this.situacionIVA.set(situacionIVA);
    }

    public StringProperty situacionITProperty() {
        return situacionIT;
    }
    
    public String getSituacionIT() {
        return situacionIT.get();
    }

    public void setSituacionIT(String situacionIT) {
        this.situacionIT.set(situacionIT);
    }

    public StringProperty distribuyeCentroCostoProperty() {
        return distribuyeCentroCosto;
    }
    
    public String getDistribuyeCentroCosto() {
        return distribuyeCentroCosto.get();
    }

    public void setDistribuyeCentroCosto(String distribuyeCentroCosto) {
        this.distribuyeCentroCosto.set(distribuyeCentroCosto);
    }

    public StringProperty codProductoProperty() {
        return codProducto;
    }
    
    public String getCodProducto() {
        return codProducto.get();
    }

    public void setCodProducto(String codProducto) {
        this.codProducto.set(codProducto);
    }

    public StringProperty descripcionProductoProperty() {
        return descripcionProducto;
    }
    
    public String getDescripcionProducto() {
        return descripcionProducto.get();
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto.set(descripcionProducto);
    }

    public StringProperty NIIF17AtribuibleProperty() {
        return this.niif17Atribuible;
    }

    public String getNIIF17Atribuible() {
        return niif17Atribuible.get();
    }

    public void setNIIF17Atribuible(String atribuible) {
        this.niif17Atribuible.set(atribuible);
    }

    public StringProperty NIIF17TipoProperty() {
        return this.niif17Tipo;
    }

    public String getNIIF17Tipo() {
        return niif17Tipo.get();
    }

    public void setNIIF17Tipo(String tipoGasto) {
        this.niif17Tipo.set(tipoGasto);
    }

    public StringProperty NIIF17ClaseProperty() {
        return this.niif17Clase;
    }

    public String getNIIF17Clase() {
        return niif17Clase.get();
    }

    public void setNIIF17Clase(String claseGasto) {
        this.niif17Clase.set(claseGasto);
    }
@Override
    public boolean getFlagCargar(){
        return flagCargar.get();
    }
    
@Override    
    public void setFlagCargar(boolean flagCargar){
        this.flagCargar.set(flagCargar);
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
}