package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarGrupoCuentaLinea {
    private IntegerProperty periodo;
    private StringProperty codigoPlanDeCuenta;
    private StringProperty nombrePlanDeCuenta;
    private StringProperty codigoAgrupacion;
    private StringProperty nombreAgrupacion;
    
    public CargarGrupoCuentaLinea(int periodo, String codigoPlanDeCuenta, String nombrePlanDeCuenta, String codigoAgrupacion, String nombreAgrupacion) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoPlanDeCuenta = new SimpleStringProperty(codigoPlanDeCuenta);
        this.nombrePlanDeCuenta = new SimpleStringProperty(nombrePlanDeCuenta);
        this.codigoAgrupacion = new SimpleStringProperty(codigoAgrupacion);
        this.nombreAgrupacion = new SimpleStringProperty(nombreAgrupacion);
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

    public StringProperty codigoPlanDeCuentaProperty() {
        return codigoPlanDeCuenta;
    }
    
    public String getCodigoPlanDeCuenta() {
        return codigoPlanDeCuenta.get();
    }

    public void setCodigoPlanDeCuenta(String codigoPlanDeCuenta) {
        this.codigoPlanDeCuenta.set(codigoPlanDeCuenta);
    }
    
    public StringProperty nombrePlanDeCuentaProperty() {
        return nombrePlanDeCuenta;
    }
    
    public String getNombrePlanDeCuenta() {
        return nombrePlanDeCuenta.get();
    }

    public void setNombrePlanDeCuenta(String nombrePlanDeCuenta) {
        this.nombrePlanDeCuenta.set(nombrePlanDeCuenta);
    }

    public StringProperty codigoAgrupacionProperty() {
        return codigoAgrupacion;
    }

    public String getCodigoAgrupacion() {
        return codigoAgrupacion.get();
    }

    public void setCodigoAgrupacion(String codigoAgrupacion) {
        this.codigoAgrupacion.set(codigoAgrupacion);
    }
    
    public StringProperty nombreAgrupacionProperty() {
        return nombreAgrupacion;
    }

    public String getNombreAgrupacion() {
        return nombreAgrupacion.get();
    }

    public void setNombreAgrupacion(String nombreAgrupacion) {
        this.nombreAgrupacion.set(nombreAgrupacion);
    }
}
