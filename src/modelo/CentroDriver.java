package modelo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CentroDriver {
    private IntegerProperty periodo;
    private StringProperty codigoCuenta;
    private StringProperty nombreCuenta;
    private StringProperty codigoPartida;
    private StringProperty nombrePartida;
    private StringProperty codigoCentroOrigen;
    private StringProperty nombreCentroOrigen;
    private StringProperty codigoEntidadOrigen;
    private StringProperty nombreEntidadOrigen;
    private StringProperty codigoCentro;
    private StringProperty nombreCentro;
    private StringProperty codigoDriver;
    private StringProperty nombreDriver;
    private DoubleProperty saldo;
    private ObjectProperty<Tipo> grupoGasto;
    private String detalleError;
    
    private BooleanProperty flagCargar;
    
//    Listar Centros
    public CentroDriver(int periodo, String codigoCentro, String nombreCentro, String codigoDriver, String nombreDriver) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
    }
    
//    Cargar Centros
    public CentroDriver(int periodo, String codigoCentro, String nombreCentro, String codigoDriver, String nombreDriver, boolean flagCargar) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
//    Listar Centros Objetos 
    public CentroDriver(int periodo, String codigoCentro, String nombreCentro, Tipo grupoGasto, String codigoDriver, String nombreDriver) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.grupoGasto = new SimpleObjectProperty(grupoGasto);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
    }
//    Cargar Centros Objetos
    public CentroDriver(int periodo, String codigoCentro, String nombreCentro, Tipo grupoGasto, String codigoDriver, String nombreDriver, boolean flagCargar) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.grupoGasto = new SimpleObjectProperty(grupoGasto);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);

    }
    
//    Listar de Bolsas
    public CentroDriver(int periodo, String codigoCuenta, String nombreCuenta, String codigoPartida, String nombrePartida,String codigoCentro, String nombreCentro, String codigoDriver, String nombreDriver) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuenta = new SimpleStringProperty(codigoCuenta);
        this.nombreCuenta = new SimpleStringProperty(nombreCuenta);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
    }
    
//    Cargar de bolsas
    public CentroDriver(int periodo, String codigoCuenta, String nombreCuenta, String codigoPartida, String nombrePartida,String codigoCentro, String nombreCentro, String codigoDriver, String nombreDriver, boolean flagCargar) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuenta = new SimpleStringProperty(codigoCuenta);
        this.nombreCuenta = new SimpleStringProperty(nombreCuenta);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.nombrePartida = new SimpleStringProperty(nombrePartida);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.nombreCentro = new SimpleStringProperty(nombreCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.nombreDriver = new SimpleStringProperty(nombreDriver);
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    //Distribucion Bolsas
    public CentroDriver(int periodo, String codigoCuenta, String codigoPartida, String codigoCentro, String codigoDriver, double saldo, Tipo grupoGasto) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCuenta = new SimpleStringProperty(codigoCuenta);
        this.codigoPartida = new SimpleStringProperty(codigoPartida);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.saldo = new SimpleDoubleProperty(saldo);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.grupoGasto = new SimpleObjectProperty(grupoGasto);
    }
    
    //Distribucion Centros y Objetos
    public CentroDriver(int periodo, String codigoCentro, String codigoDriver, double saldo, Tipo grupoGasto) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.saldo = new SimpleDoubleProperty(saldo);
        this.grupoGasto = new SimpleObjectProperty(grupoGasto);
    }
    // Obtener Centros, con Rastro Cuenta, Partida, Centro origen
    public CentroDriver(int periodo, String codigoCentro, String codigoCuentaOrigen, String codigoPartidaOrigen, String codigoCentroOrigen, String codigoDriver, double saldo, Tipo grupoGasto) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigoCentro = new SimpleStringProperty(codigoCentro);
        this.codigoCuenta = new SimpleStringProperty(codigoCuentaOrigen);
        this.codigoPartida = new SimpleStringProperty(codigoPartidaOrigen);
        this.codigoCentroOrigen = new SimpleStringProperty(codigoCentroOrigen);
        this.codigoDriver = new SimpleStringProperty(codigoDriver);
        this.saldo = new SimpleDoubleProperty(saldo);
        this.grupoGasto = new SimpleObjectProperty(grupoGasto);
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
    
    public StringProperty codigoCentroProperty() {
        return codigoCentro;
    }

    public String getCodigoCentro() {
        return codigoCentro.get();
    }

    public void setCodigoCentro(String codigoEntidad) {
        this.codigoCentro.set(codigoEntidad);
    }
    
    public StringProperty nombreCentroProperty() {
        return nombreCentro;
    }

    public String getNombreCentro() {
        return nombreCentro.get();
    }

    public void setNombreCentro(String nombreEntidad) {
        this.nombreCentro.set(nombreEntidad);
    }
    
    public StringProperty codigoCuentaProperty() {
        return codigoCuenta;
    }

    public String getCodigoCuenta() {
        return codigoCuenta.get();
    }

    public void setCodigoCuenta(String codigoEntidad) {
        this.codigoCuenta.set(codigoEntidad);
    }
    
    public StringProperty nombreCuentaProperty() {
        return nombreCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta.get();
    }

    public void setNombreCuenta(String nombreEntidad) {
        this.nombreCuenta.set(nombreEntidad);
    }
    
    public StringProperty codigoPartidaProperty() {
        return codigoPartida;
    }

    public String getCodigoPartida() {
        return codigoPartida.get();
    }

    public void setCodigoPartida(String codigoEntidad) {
        this.codigoPartida.set(codigoEntidad);
    }
    
    public StringProperty nombrePartidaProperty() {
        return nombrePartida;
    }

    public String getNombrePartida() {
        return nombrePartida.get();
    }

    public void setNombrePartida(String nombreEntidad) {
        this.nombrePartida.set(nombreEntidad);
    }
    
    public StringProperty codigoDriverProperty() {
        return codigoDriver;
    }

    public String getCodigoDriver() {
        return codigoDriver.get();
    }

    public void setCodigoDriver(String codigoDriver) {
        this.codigoDriver.set(codigoDriver);
    }
    
    public StringProperty nombreDriverProperty() {
        return nombreDriver;
    }

    public String getNombreDriver() {
        return nombreDriver.get();
    }

    public void setNombreDriver(String nombreDriver) {
        this.nombreDriver.set(nombreDriver);
    }
    
    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(boolean nivel) {
        this.flagCargar.set(nivel);
    }
    
    public ObjectProperty<Tipo> grupoGastoProperty() {
        return this.grupoGasto;
    }

    public Tipo getGrupoGasto() {
        return grupoGasto.get();
    }

    public void setGrupoGasto(Tipo grupoGasto) {
        this.grupoGasto.set(grupoGasto);
    }
    
    public DoubleProperty saldoProperty(){
        return this.saldo;
    }
    
    public void setSaldo(double saldo){
        this.saldo.set(saldo);
    }
    
    public double getSaldo(){
        return this.saldo.get();
    }
    
    public String getDetalleError() {
        return detalleError;
    }

    public void setDetalleError(String detalleError) {
        this.detalleError = detalleError;
    }
    
    public StringProperty codigoCentroOrigenProperty() {
        return codigoCentroOrigen;
    }

    public String getCodigoCentroOrigen() {
        return codigoCentroOrigen.get();
    }

    public void setCodigoCentroOrigen(String codigoEntidad) {
        this.codigoCentroOrigen.set(codigoEntidad);
    }
}
