package modelo;

public class RutaArchivos {
    private String vista;
    private String controlador;
    private String titulo;

    public RutaArchivos(String vista) {
        this.vista = vista;
    }
    
    public RutaArchivos(String vista, String controlador) {
        this.vista = vista;
        this.controlador = controlador;
    }
    
    public RutaArchivos(String vista, String controlador, String titulo) {
        this.vista = vista;
        this.controlador = controlador;
        this.titulo = titulo;
    }

    public String getVista() {
        return vista;
    }

    public void setVista(String vista) {
        this.vista = vista;
    }

    public String getControlador() {
        return controlador;
    }

    public void setControlador(String controlador) {
        this.controlador = controlador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }    
}
