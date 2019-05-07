package servicios;

import dao.DriverDAO;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import modelo.Centro;
import modelo.Driver;
import modelo.DriverCentro;
import modelo.DriverLinea;
import modelo.DriverObjeto;
import modelo.EntidadDistribucion;
import modelo.Grupo;
import modelo.CuentaContable;

public class DriverServicio {
    DriverDAO driverDAO;
    final static Logger LOGGER = Logger.getLogger("controlador.servicios.DriverServicio");
    
    public DriverServicio() {
        driverDAO = new DriverDAO();
    }

    public int agregarDriversAGrupos(List<Grupo> lista, int periodo, int repartoTipo) {
        Hashtable<String,String> contenedor = new Hashtable();
        //List<DriverCentro> listaDrivers = driverDAO.listarDriversCentro(periodo,repartoTipo);
        List<DriverCentro> listaDrivers = null;
        for (Grupo entidad: lista) {
            String codigoDriver = driverDAO.obtenerCodigoDriver(entidad.getCodigo(), periodo);
            Driver driver;
            if (codigoDriver != null) {
                driver = listaDrivers.stream().filter(item -> codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
            } else {
                driver = new Driver("Sin driver asignado", "Sin driver asignado", null, null, null, null);
                contenedor.put(entidad.getCodigo(), "");
            }
            entidad.setDriver(driver);
        }
        return contenedor.size();
    }    

    public int agregarDriversACuentasContables(List<CuentaContable> lista, int periodo, int repartoTipo) {
        Hashtable<String,String> contenedor = new Hashtable();
        //List<DriverCentro> listaDrivers = driverDAO.listarDriversCentro(periodo,repartoTipo);
        List<DriverCentro> listaDrivers = null;
        for (CuentaContable entidad : lista) {
            String codigoDriver = driverDAO.obtenerCodigoDriver(entidad.getGrupo().getCodigo(), periodo);
            Driver driver;
            if (codigoDriver != null) {
                driver = listaDrivers.stream().filter(item -> codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
            } else {
                driver = new Driver("Sin driver asignado", "Sin driver asignado", null, null, null, null);
                contenedor.put(entidad.getGrupo().getCodigo(), "");
            }
            entidad.setDriver(driver);
        }
        return contenedor.size();
    }

    public int agregarDriversACentros(List<Centro> lista, int periodo, int repartoTipo) {
        //List<DriverCentro> listaDriversCentro = driverDAO.listarDriversCentro(periodo,repartoTipo);
        //List<DriverObjeto> listaDriversObjeto = driverDAO.listarDriversObjeto(periodo,repartoTipo);
        List<DriverCentro> listaDriversCentro = null;
        List<DriverObjeto> listaDriversObjeto = null;
        List<Driver> listaDrivers = new ArrayList();
        listaDrivers.addAll(listaDriversCentro);
        listaDrivers.addAll(listaDriversObjeto);
        int cantEntidadesSinAsignar = 0;
        for (Centro centro: lista) {
            Driver driver;
            String codigoDriver = driverDAO.obtenerCodigoDriver(centro.getCodigo(),periodo);
            if (codigoDriver != null) {
                driver = listaDrivers.stream().filter(item -> codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
                // normalizacion para tratamiento de cascada
                if (driver.getClass().getName().equals("modelo.DriverCentro")) {
                    int nivel = centro.getNivel();
                    List<DriverLinea> listaF = new ArrayList();
                    listaF.addAll(((DriverCentro) driver).getListaDriverLinea());
                    listaF = listaF.stream().filter(driverLinea -> nivel<((Centro)driverLinea.getEntidadDistribucionDestino()).getNivel() || ((Centro)driverLinea.getEntidadDistribucionDestino()).getNivel()==0).collect(Collectors.toList());
                    double total = listaF.stream().mapToDouble(f -> f.getPorcentaje()).sum();
                    if (total != 100) {
                        for (DriverLinea driverLinea: listaF) {
                            double porcNormal = driverLinea.getPorcentaje()/total;
                            driverLinea.setPorcentaje(porcNormal);
                            ((DriverCentro) driver).setListaDriverLinea(listaF);
                        }
                    }
                }
            } else {
                driver = new Driver("Sin driver asignado", "Sin driver asignado", null, null, null, null);
                ++cantEntidadesSinAsignar;
            }
            centro.setDriver(driver);
        }
        return cantEntidadesSinAsignar;
    }
    
    public int agregarDriversCECO(List<Centro> lista, int periodo, int repartoTipo) {
        List<Driver> listaDrivers = new ArrayList();
        //listaDrivers.addAll(driverDAO.listarDriversCentro(periodo,repartoTipo));
        int cantEntidadesSinAsignar = 0;
        for (Centro centro: lista) {
            Driver driver;
            String codigoDriver = driverDAO.obtenerCodigoDriver(centro.getCodigo(),periodo);
            if (codigoDriver != null) {
                driver = listaDrivers.stream().filter(item -> codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
                // normalizacion para tratamiento de cascada
                if (driver.getClass().getName().equals("modelo.DriverCentro")) {
                    int nivel = centro.getNivel();
                    List<DriverLinea> listaF = new ArrayList();
                    listaF.addAll(((DriverCentro) driver).getListaDriverLinea());
                    listaF = listaF.stream().filter(driverLinea -> nivel<((Centro)driverLinea.getEntidadDistribucionDestino()).getNivel() || ((Centro)driverLinea.getEntidadDistribucionDestino()).getNivel()==0).collect(Collectors.toList());
                    double total = listaF.stream().mapToDouble(f -> f.getPorcentaje()).sum();
                    if (total != 100) {
                        for (DriverLinea driverLinea: listaF) {
                            double porcNormal = driverLinea.getPorcentaje()/total;
                            driverLinea.setPorcentaje(porcNormal);
                            ((DriverCentro) driver).setListaDriverLinea(listaF);
                        }
                    }
                }
            } else {
                driver = new Driver("Sin driver asignado", "Sin driver asignado", null, null, null, null);
                ++cantEntidadesSinAsignar;
            }
            centro.setDriver(driver);
        }
        return cantEntidadesSinAsignar;
    }
    
    public int agregarDrivers(List<? extends EntidadDistribucion> lista, int periodo, int repartoTipo) {
        //List<DriverCentro> listaDriversCentro = driverDAO.listarDriversCentro(periodo,repartoTipo);
        //List<DriverObjeto> listaDriversObjeto = driverDAO.listarDriversObjeto(periodo,repartoTipo);
        List<DriverCentro> listaDriversCentro = null;
        List<DriverObjeto> listaDriversObjeto = null;
        List<Driver> listaDrivers = new ArrayList();
        listaDrivers.addAll(listaDriversCentro);
        listaDrivers.addAll(listaDriversObjeto);
        int cantEntidadesSinAsignar = 0;
        for (EntidadDistribucion entidad: lista) {
            String codigoDriver = driverDAO.obtenerCodigoDriver(entidad.getCodigo(),periodo);
            Driver driver;
            if (codigoDriver != null) {
                driver = listaDrivers.stream().filter(item -> codigoDriver.equals(item.getCodigo())).findAny().orElse(null);
            } else {
                driver = new Driver("Sin driver asignado", "Sin driver asignado", null, null, null, null);
                ++cantEntidadesSinAsignar;
            }
            entidad.setDriver(driver);
        }
        return cantEntidadesSinAsignar;
    }
}
