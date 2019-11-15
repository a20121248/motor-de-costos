package modelo;

public class Cabecera {
    private String nombre;
    private int ancho;

    public Cabecera(String nombre) {
        this.nombre = nombre;
        this.ancho = ancho;
    }
    
    public Cabecera(String nombre, int ancho) {
        this.nombre = nombre;
        this.ancho = ancho;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }    
}
