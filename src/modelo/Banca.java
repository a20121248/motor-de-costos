package modelo;

import java.util.Date;

public class Banca extends EntidadDistribucion {

    public Banca(String codigo, String nombre, String descripcion, double saldo, Date fechaCreacion, Date fechaActualizacion) {
        super(codigo, nombre, descripcion, saldo, fechaCreacion, fechaActualizacion, true);
    }
    
}
