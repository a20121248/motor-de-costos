package modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DriverCentro extends Driver {
    private List<DriverLinea> listaDriverLinea;
    private BooleanProperty flagCargar;
    
    public DriverCentro(String codigo, String nombre, String descripcion, Tipo tipo, List<DriverLinea> listaDriverLinea, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, tipo, fechaCreacion, fechaActualizacion);
        this.listaDriverLinea = listaDriverLinea;
    }
    
    public DriverCentro(String codigo, String nombre, String descripcion, Tipo tipo, List<DriverLinea> listaDriverLinea, Date fechaCreacion, Date fechaActualizacion, Boolean flagCargar) {
        super(codigo, nombre, descripcion, tipo, fechaCreacion, fechaActualizacion);
        if(listaDriverLinea == null) this.listaDriverLinea = new ArrayList();
        else this.listaDriverLinea = listaDriverLinea;
        this.flagCargar = new SimpleBooleanProperty(flagCargar);
    }
    
    public List<DriverLinea> getListaDriverLinea() {
        return listaDriverLinea;
    }

    public void setListaDriverLinea(List<DriverLinea> listaDriverLinea) {
        this.listaDriverLinea = listaDriverLinea;
    }
    
    public void addItemToListaDriverLinea(DriverLinea driverLinea) {
        this.listaDriverLinea.add(driverLinea);
    }
    
    public boolean getFlagCargar() {
        return flagCargar.get();
    }

    public void setFlagCargar(Boolean flagCargar) {
        this.flagCargar.set(flagCargar);
    }
}
