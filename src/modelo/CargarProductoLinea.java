package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CargarProductoLinea {
    private IntegerProperty periodo;
    private StringProperty codigo;
    private StringProperty tipo;
    private StringProperty banca;
    private StringProperty producto;
    private StringProperty monto;
    private StringProperty descripcion;
    
    public CargarProductoLinea(int periodo, String codigo, String tipo, String banca, String producto, String monto, String descripcion) {
        this.periodo = new SimpleIntegerProperty(periodo);
        this.codigo = new SimpleStringProperty(codigo);
        this.tipo = new SimpleStringProperty(tipo);
        this.banca = new SimpleStringProperty(banca);
        this.producto = new SimpleStringProperty(producto);
        this.monto = new SimpleStringProperty(monto);
        this.descripcion = new SimpleStringProperty(descripcion);
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

    public StringProperty codigoProperty() {
        return codigo;
    }
    
    public String getCodigo() {
        return codigo.get();
    }

    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }
    
    public StringProperty tipoProperty() {
        return tipo;
    }

    public String getTipo() {
        return tipo.get();
    }

    public void setTipo(String tipo) {
        this.tipo.set(tipo);
    }
    
    public StringProperty bancaProperty() {
        return banca;
    }

    public String getBanca() {
        return banca.get();
    }

    public void setBanca(String banca) {
        this.banca.set(banca);
    }
    
    public StringProperty productoProperty() {
        return producto;
    }

    public String getProducto() {
        return producto.get();
    }

    public void setProducto(String producto) {
        this.producto.set(producto);
    }
    
    public StringProperty montoProperty() {
        return monto;
    }

    public String getMonto() {
        return monto.get();
    }

    public void setMonto(String monto) {
        this.monto.set(monto);
    }
    
    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }
}
