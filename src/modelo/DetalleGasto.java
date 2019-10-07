package modelo;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetalleGasto {

    private StringProperty codigoCuentaContable;
    private StringProperty nombreCuentaContable;
    private StringProperty codigoPartida;
    private StringProperty nombrePartida;
    private StringProperty codigoCentro;
    private StringProperty nombreCentro;
    private DoubleProperty monto01;
    private DoubleProperty monto02;
    private DoubleProperty monto03;
    private DoubleProperty monto04;
    private DoubleProperty monto05;
    private DoubleProperty monto06;
    private DoubleProperty monto07;
    private DoubleProperty monto08;
    private DoubleProperty monto09;
    private DoubleProperty monto10;
    private DoubleProperty monto11;
    private DoubleProperty monto12;
    private BooleanProperty estado;
    private String detalleError;

    public DetalleGasto(String codigoCuentaContable, String nombreCuentaContable, String codigoPartida, String nombrePartida, String codigoCECO, String nombreCECO, double monto01, boolean estado) {
        this.codigoCuentaContable = new SimpleStringProperty(codigoCuentaContable);
        this.nombreCuentaContable = new SimpleStringProperty(nombreCuentaContable);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCentro = new SimpleStringProperty(codigoCECO);
        this.nombreCentro = new SimpleStringProperty(nombreCECO);
        this.monto01 = new SimpleDoubleProperty(monto01);
        this.estado = new SimpleBooleanProperty(estado);
    }

    public DetalleGasto(String codigoCuentaContable, String nombreCuentaContable, String codigoPartida, String nombrePartida, String codigoCentro, String nombreCentro, double monto01, double monto02, double monto03, double monto04, double monto05, double monto06, double monto07, double monto08, double monto09, double monto10, double monto11, double monto12, boolean estado) {
        this.codigoCuentaContable = new SimpleStringProperty(codigoCuentaContable);
        this.nombreCuentaContable = new SimpleStringProperty(nombreCuentaContable);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.monto01 = new SimpleDoubleProperty(monto01);
        this.monto02 = new SimpleDoubleProperty(monto02);
        this.monto03 = new SimpleDoubleProperty(monto03);
        this.monto04 = new SimpleDoubleProperty(monto04);
        this.monto05 = new SimpleDoubleProperty(monto05);
        this.monto06 = new SimpleDoubleProperty(monto06);
        this.monto07 = new SimpleDoubleProperty(monto07);
        this.monto08 = new SimpleDoubleProperty(monto08);
        this.monto09 = new SimpleDoubleProperty(monto09);
        this.monto10 = new SimpleDoubleProperty(monto10);
        this.monto11 = new SimpleDoubleProperty(monto11);
        this.monto12 = new SimpleDoubleProperty(monto12);
        this.estado = new SimpleBooleanProperty(estado);
    }

    public StringProperty codigoCuentaContableProperty() {
        return codigoCuentaContable;
    }

    public String getCodigoCuentaContable() {
        return codigoCuentaContable.get();
    }

    public void setCodigoCuentaContable(String codigo) {
        this.codigoCuentaContable.set(codigo);
    }

    public StringProperty nombreCuentaContableProperty() {
        return nombreCuentaContable;
    }

    public String getNombreCuentaContable() {
        return nombreCuentaContable.get();
    }

    public void setNombreCuentaContable(String nombre) {
        this.nombreCuentaContable.set(nombre);
    }

    public StringProperty codigoPartidaProperty() {
        return codigoPartida;
    }

    public String getCodigoPartida() {
        return codigoPartida.get();
    }

    public void setCodigoPartida(String codigo) {
        this.codigoPartida.set(codigo);
    }

    public StringProperty nombrePartidaProperty() {
        return nombrePartida;
    }

    public String getNombrePartida() {
        return nombrePartida.get();
    }

    public void setNombrePartida(String nombre) {
        this.nombrePartida.set(nombre);
    }

    public StringProperty codigoCentroProperty() {
        return codigoCentro;
    }

    public String getCodigoCentro() {
        return codigoCentro.get();
    }

    public void setCodigoCentro(String codigo) {
        this.codigoCentro.set(codigo);
    }

    public StringProperty nombreCentroProperty() {
        return nombreCentro;
    }

    public String getNombreCentro() {
        return nombreCentro.get();
    }

    public void setNombreCentro(String nombre) {
        this.nombreCentro.set(nombre);
    }

    public DoubleProperty monto01Property() {
        return monto01;
    }

    public double getMonto01() {
        return monto01.get();
    }

    public void setMonto01(double monto01) {
        this.monto01.set(monto01);
    }

    public DoubleProperty monto02Property() {
        return monto02;
    }

    public double getMonto02() {
        return monto02.get();
    }

    public void setMonto02(double monto02) {
        this.monto02.set(monto02);
    }

    public DoubleProperty monto03Property() {
        return monto03;
    }

    public double getMonto03() {
        return monto03.get();
    }

    public void setMonto03(double monto03) {
        this.monto03.set(monto03);
    }

    public DoubleProperty monto04Property() {
        return monto04;
    }

    public double getMonto04() {
        return monto04.get();
    }

    public void setMonto04(double monto04) {
        this.monto04.set(monto04);
    }

    public DoubleProperty monto05Property() {
        return monto05;
    }

    public double getMonto05() {
        return monto05.get();
    }

    public void setMonto05(double monto05) {
        this.monto05.set(monto05);
    }

    public DoubleProperty monto06Property() {
        return monto06;
    }

    public double getMonto06() {
        return monto06.get();
    }

    public void setMonto06(double monto06) {
        this.monto06.set(monto06);
    }

    public DoubleProperty monto07Property() {
        return monto07;
    }

    public double getMonto07() {
        return monto07.get();
    }

    public void setMonto07(double monto07) {
        this.monto07.set(monto07);
    }

    public DoubleProperty monto08Property() {
        return monto08;
    }

    public double getMonto08() {
        return monto08.get();
    }

    public void setMonto08(double monto08) {
        this.monto08.set(monto08);
    }

    public DoubleProperty monto09Property() {
        return monto09;
    }

    public double getMonto09() {
        return monto09.get();
    }

    public void setMonto09(double monto09) {
        this.monto09.set(monto09);
    }

    public DoubleProperty monto10Property() {
        return monto10;
    }

    public double getMonto10() {
        return monto10.get();
    }

    public void setMonto10(double monto10) {
        this.monto10.set(monto10);
    }

    public DoubleProperty monto11Property() {
        return monto11;
    }

    public double getMonto11() {
        return monto11.get();
    }

    public void setMonto11(double monto11) {
        this.monto11.set(monto11);
    }

    public DoubleProperty monto12Property() {
        return monto12;
    }

    public double getMonto12() {
        return monto12.get();
    }

    public void setMonto12(double monto12) {
        this.monto12.set(monto12);
    }

    public List<Double> getMontos() {
        List<Double> montos = new ArrayList();
        montos.add(getMonto01());
        if (monto02Property() != null) {
            montos.add(getMonto02());
            montos.add(getMonto03());
            montos.add(getMonto04());
            montos.add(getMonto05());
            montos.add(getMonto06());
            montos.add(getMonto07());
            montos.add(getMonto08());
            montos.add(getMonto09());
            montos.add(getMonto10());
            montos.add(getMonto11());
            montos.add(getMonto12());
        }
        return montos;
    }

    public BooleanProperty estadoProperty() {
        return estado;
    }
    
    public String getDetalleError() {
        return detalleError;
    }

    public void setDetalleError(String detalleError) {
        this.detalleError = detalleError;
    }

    public boolean getEstado() {
        return estado.get();
    }

    public void setEstado(Boolean estado) {
        this.estado.set(estado);
    }
}
