package modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DriverObjeto extends Driver {
    private List<DriverObjetoLinea> listaDriverObjetoLinea;
    private BooleanProperty flagCargar;
    
    public DriverObjeto(String codigo, String nombre, String descripcion, Tipo tipo, List<DriverObjetoLinea> listaDriverLinea, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, tipo, fechaCreacion, fechaActualizacion);
        this.listaDriverObjetoLinea = listaDriverLinea;
    }
    
    public DriverObjeto(String codigo, String nombre, String descripcion, Tipo tipo, List<DriverObjetoLinea> listaDriverLinea, Date fechaCreacion, Date fechaActualizacion, Boolean flagCargar) {
        super(codigo, nombre, descripcion, tipo, fechaCreacion, fechaActualizacion);
        if(listaDriverLinea == null) this.listaDriverObjetoLinea = new ArrayList();
        else this.listaDriverObjetoLinea = listaDriverLinea;
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    public List<DriverObjetoLinea> getListaDriverObjetoLinea() {
        return listaDriverObjetoLinea;
    }

    public void setListaDriverObjetoLinea(List<DriverObjetoLinea> listaDriverObjetoLinea) {
        this.listaDriverObjetoLinea = listaDriverObjetoLinea;
    }
    
    public void addItemToListaDriverLinea(DriverObjetoLinea driverLinea) {
        this.listaDriverObjetoLinea.add(driverLinea);
    }
    
    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(Boolean flagCargar) {
        this.flagCargar.set(flagCargar);
    }
}
