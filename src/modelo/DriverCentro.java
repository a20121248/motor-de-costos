package modelo;

import java.util.Date;
import java.util.List;

public class DriverCentro extends Driver {
    private List<DriverLinea> listaDriverLinea;
    
    public DriverCentro(String codigo, String nombre, String descripcion, Tipo tipo, List<DriverLinea> listaDriverLinea, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, tipo, fechaCreacion, fechaActualizacion);
        this.listaDriverLinea = listaDriverLinea;
    }
    
    public List<DriverLinea> getListaDriverLinea() {
        return listaDriverLinea;
    }

    public void setListaDriverLinea(List<DriverLinea> listaDriverLinea) {
        this.listaDriverLinea = listaDriverLinea;
    }
}
