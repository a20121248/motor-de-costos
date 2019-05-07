package dao;

import java.util.List;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.EntidadDistribucion;

public class EntidadDistribucionDAO {
    
    public EntidadDistribucionDAO() {
    }
    
    public void DistribuirEntidad(EntidadDistribucion entidad) {
        List<DriverLinea> listaDriverLinea = ((DriverCentro) entidad.getDriver()).getListaDriverLinea();
        for (DriverLinea driverLinea : listaDriverLinea) {
            
        }        
    }
}
