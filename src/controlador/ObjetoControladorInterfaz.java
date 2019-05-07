package controlador;

import modelo.DriverCentro;
import modelo.DriverObjeto;
import modelo.EntidadDistribucion;

public interface ObjetoControladorInterfaz {
    void seleccionarEntidad(EntidadDistribucion entidad);
    void seleccionarDriverCentro(DriverCentro driver);
    void seleccionarDriverObjeto(DriverObjeto driver);
}
