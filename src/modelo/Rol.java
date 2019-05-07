package modelo;

import java.util.List;

public class Rol extends Tipo {
    List<Permiso> lstPermisos;
    
    public Rol(String codigo, String nombre, String descripcion) {
        super(codigo, nombre, descripcion);
    }
    
    public List<Permiso> getLstPermisos() {
        return lstPermisos;
    }

    public void setLstPermisos(List<Permiso> lstPermisos) {
        this.lstPermisos = lstPermisos;
    }
}
