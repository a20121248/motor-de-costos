package servicios;

import dao.CargarExcelDAO;
import java.util.logging.Logger;

public class CargarExcelServicio {
    CargarExcelDAO cargarExcelDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.CargarExcelServicio");
    
    public CargarExcelServicio() {
        cargarExcelDAO = new CargarExcelDAO();
    }
    
    public void insertarLineaDriverCentro(String driverCodigo, String centroCodigo, String porcentaje) {
        //cargarExcelDAO.insertarLineaDriverCentroBatch(driverCodigo, centroCodigo, porcentaje);
    }
    
}
