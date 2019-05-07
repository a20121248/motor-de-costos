package modelo;

import java.util.Date;
import java.util.List;

public class DriverObjeto extends Driver {
    private List<DriverObjetoLinea> listaDriverObjetoLinea;
    
    public DriverObjeto(String codigo, String nombre, String descripcion, Tipo tipo, List<DriverObjetoLinea> listaDriverLinea, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, tipo, fechaCreacion, fechaActualizacion);
        this.listaDriverObjetoLinea = listaDriverLinea;
    }
    
    public List<DriverObjetoLinea> getListaDriverObjetoLinea() {
        return listaDriverObjetoLinea;
    }

    public void setListaDriverObjetoLinea(List<DriverObjetoLinea> listaDriverObjetoLinea) {
        this.listaDriverObjetoLinea = listaDriverObjetoLinea;
    }
}
