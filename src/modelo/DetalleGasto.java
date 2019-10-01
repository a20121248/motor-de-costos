package modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetalleGasto {
    private IntegerProperty periodo;
    private StringProperty codigoCuentaContable;
    private StringProperty nombreCuentaContable;
    private StringProperty codigoPartida;
    private StringProperty nombrePartida;
    private StringProperty codigoCECO;
    private StringProperty nombreCECO;
    private DoubleProperty monto1;
    private DoubleProperty monto2;
    private DoubleProperty monto3;
    private DoubleProperty monto4;
    private DoubleProperty monto5;
    private DoubleProperty monto6;
    private DoubleProperty monto7;
    private DoubleProperty monto8;
    private DoubleProperty monto9;
    private DoubleProperty monto10;
    private BooleanProperty estado;
    
    public DetalleGasto(int periodo, String codigoCuentaContable, String nombreCuentaContable, String codigoPartida, String nombrePartida, String codigoCECO, String nombreCECO, double monto1, boolean estado) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuentaContable = new SimpleStringProperty(codigoCuentaContable);
        this.nombreCuentaContable = new SimpleStringProperty(nombreCuentaContable);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCECO = new SimpleStringProperty(codigoCECO);
        this.nombreCECO = new SimpleStringProperty(nombreCECO);
        this.monto1 = new SimpleDoubleProperty(monto1);
        this.estado = new SimpleBooleanProperty(estado);
    }
    
    public DetalleGasto(int periodo, String codigoCuentaContable, String nombreCuentaContable, String codigoPartida, String nombrePartida, String codigoCECO, String nombreCECO, double monto1, double monto2, double monto3, double monto4, double monto5, double monto6, double monto7, double monto8, double monto9, double monto10, boolean estado) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuentaContable = new SimpleStringProperty(codigoCuentaContable);
        this.nombreCuentaContable = new SimpleStringProperty(nombreCuentaContable);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCECO = new SimpleStringProperty(codigoCECO);
        this.nombreCECO = new SimpleStringProperty(nombreCECO);
        this.monto1 = new SimpleDoubleProperty(monto1);
        this.monto2 = new SimpleDoubleProperty(monto2);
        this.monto3 = new SimpleDoubleProperty(monto3);
        this.monto4 = new SimpleDoubleProperty(monto4);
        this.monto5 = new SimpleDoubleProperty(monto5);
        this.monto6 = new SimpleDoubleProperty(monto6);
        this.monto7 = new SimpleDoubleProperty(monto7);
        this.monto8 = new SimpleDoubleProperty(monto8);
        this.monto9 = new SimpleDoubleProperty(monto9);
        this.monto10 = new SimpleDoubleProperty(monto10);
        this.estado = new SimpleBooleanProperty(estado);
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
    
    public StringProperty codigoCECOProperty() {
        return codigoCECO;
    } 

    public String getCodigoCECO() {
        return codigoCECO.get();
    }

    public void setCodigoCECO(String codigo) {
        this.codigoCECO.set(codigo);
    }
    
    public StringProperty nombreCECOProperty() {
        return nombreCECO;
    } 

    public String getNombreCECO() {
        return nombreCECO.get();
    }

    public void setNombreCECO(String nombre) {
        this.nombreCECO.set(nombre);
    }

    public DoubleProperty monto1Property() {return monto1;}public double getMonto1() {return monto1.get();}public void setMonto1(double monto1) {this.monto1.set(monto1);}
    public DoubleProperty monto2Property() {return monto2;}public double getmonto2() {return monto2.get();}public void setmonto2(double monto2) {this.monto2.set(monto2);}
    public DoubleProperty monto3Property() {return monto3;}public double getmonto3() {return monto3.get();}public void setmonto3(double monto3) {this.monto3.set(monto3);}
    public DoubleProperty monto4Property() {return monto4;}public double getmonto4() {return monto4.get();}public void setmonto4(double monto4) {this.monto4.set(monto4);}
    public DoubleProperty monto5Property() {return monto5;}public double getmonto5() {return monto5.get();}public void setmonto5(double monto5) {this.monto5.set(monto5);}
    public DoubleProperty monto6Property() {return monto6;}public double getmonto6() {return monto6.get();}public void setmonto6(double monto6) {this.monto6.set(monto6);}
    public DoubleProperty monto7Property() {return monto7;}public double getmonto7() {return monto7.get();}public void setmonto7(double monto7) {this.monto7.set(monto7);}
    public DoubleProperty monto8Property() {return monto8;}public double getmonto8() {return monto8.get();}public void setmonto8(double monto8) {this.monto8.set(monto8);}
    public DoubleProperty monto9Property() {return monto9;}public double getmonto9() {return monto9.get();}public void setmonto9(double monto9) {this.monto9.set(monto9);}
    public DoubleProperty monto10Property() {return monto10;}public double getmonto10() {return monto10.get();}public void setmonto10(double monto10) {this.monto10.set(monto10);}
    
    public BooleanProperty estadoProperty() {
        return estado;
    }
    
    public boolean getEstado() {
        return estado.get();
    }
    
    public void setEstado(Boolean estado){
        this.estado.set(estado);
    }
}
