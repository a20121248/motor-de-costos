package modelo;

import java.util.Date;

public class Oficina extends EntidadDistribucion {
    public Oficina(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
    }
}
