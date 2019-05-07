package modelo;

public class AsignacionDriverEntidad {
    private EntidadDistribucion entidad;
    private Driver driver;
    
    public AsignacionDriverEntidad(EntidadDistribucion entidad, Driver driver) {
        this.entidad = entidad;
        this.driver = driver;
    }
    
    public EntidadDistribucion getEntidad() {
        return entidad;
    }

    public void setEntidad(EntidadDistribucion entidad) {
        this.entidad = entidad;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
