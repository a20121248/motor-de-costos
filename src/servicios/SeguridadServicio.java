package servicios;

import dao.SeguridadDAO;
import java.util.logging.Logger;
import modelo.Usuario;

public class SeguridadServicio {
    SeguridadDAO seguridadDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.SeguridadServicio");
    
    public SeguridadServicio() {
        seguridadDAO = new SeguridadDAO();
    }
    
    public Usuario buscarUsuario(String usuarioCodigo) {
        return seguridadDAO.obtenerUsuario(usuarioCodigo);
    }
}
