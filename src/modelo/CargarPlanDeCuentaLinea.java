package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarPlanDeCuentaLinea {
    private IntegerProperty periodo;
    private StringProperty rubroContable;
    private StringProperty descripcionCuenta;
    private StringProperty grupoBalance;
    private StringProperty rubroOperativo;
    private StringProperty codPonderacion;
    private StringProperty situacionIVA;
    private StringProperty situacionIT;
    private StringProperty distribuyeCentroCosto;
    private StringProperty codProducto;
    private StringProperty descripcionProducto;
    
    public CargarPlanDeCuentaLinea(int periodo, String rubroContable, String descripcionCuenta, String grupoBalance, String rubroOperativo, String codPonderacion, String situacionIVA, String situacionIT, String distribuyeCentroCosto, String codProducto, String descripcionProducto) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.rubroContable = new SimpleStringProperty(rubroContable);
        this.descripcionCuenta = new SimpleStringProperty(descripcionCuenta);
        this.grupoBalance = new SimpleStringProperty(grupoBalance);
        this.rubroOperativo = new SimpleStringProperty(rubroOperativo);
        this.codPonderacion = new SimpleStringProperty(codPonderacion);
        this.situacionIVA = new SimpleStringProperty(situacionIVA);
        this.situacionIT = new SimpleStringProperty(situacionIT);
        this.distribuyeCentroCosto = new SimpleStringProperty(distribuyeCentroCosto);
        this.codProducto = new SimpleStringProperty(codProducto);
        this.descripcionProducto = new SimpleStringProperty(descripcionProducto);
    }

    public IntegerProperty periodoProperty() {
        return periodo;
    }
    
    public int getPeriodo() {
        return periodo.get();
    }

    public void setPeriodo(int periodo) {
        this.periodo.set(periodo);
    }

    public StringProperty rubroContableProperty() {
        return rubroContable;
    }

    public String getRubroContable() {
        return rubroContable.get();
    }
    
    public void setRubroContable(String rubroContable) {
        this.rubroContable.set(rubroContable);
    }

    public StringProperty descripcionCuentaProperty() {
        return descripcionCuenta;
    }

    public String getDescripcionCuenta() {
        return descripcionCuenta.get();
    }

    public void setDescripcion(String descripcionCuenta) {
        this.descripcionCuenta.set(descripcionCuenta);
    }

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
}
