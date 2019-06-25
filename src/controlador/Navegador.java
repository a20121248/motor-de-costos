package controlador;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import modelo.RutaArchivos;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class Navegador {
    private final MenuControlador menuControlador;
    final static Logger LOGGER = Logger.getLogger("app.controlador.Navegador");
    
    public Navegador(MenuControlador menuControlador) {
        this.menuControlador = menuControlador;
    }    

    public static void crearCarpeta(String carpetaNombre) {
        File directorio = new File(carpetaNombre);
        if (!directorio.exists()){
            directorio.mkdir();
        }
    }
    
    // =========================================================
    // ******************** MODULO INICIO **********************
    // =========================================================
    public static final RutaArchivos RUTAS_MODULO_INICIO = new RutaArchivos(
            "/vista/inicio/Principal.fxml",
            "controlador.inicio.PrincipalControlador",
            "INICIO",
            "Inicio"
    );
    // =========================================================
    // ******************** MODULO APROVISIONAMIENTO ***********
    // =========================================================
    public static final RutaArchivos RUTAS_MODULO_APROVISIONAMIENTO = new RutaArchivos(
            "/vista/aprovisionamiento/Principal.fxml",
            "controlador.aprovisionamiento.PrincipalControlador",
            "APROVISIONAMIENTO",
            "Inicio/Aprovisionamiento"
    );
    // --------------------- BALANCETE ----------------------
    public static final RutaArchivos RUTAS_BALANCETE_LISTAR = new RutaArchivos(
            "/vista/aprovisionamiento/balancete/Listar.fxml",
            "controlador.aprovisionamiento.balancete.ListarControlador",
            "Balancete",
            "Inicio/Aprovisionamiento/Detalle Gasto"
    );
    public static final RutaArchivos RUTAS_BALANCETE_CARGAR = new RutaArchivos(
            "/vista/aprovisionamiento/balancete/Cargar.fxml",
            "controlador.aprovisionamiento.balancete.CargarControlador",
            "Cargar Balancete",
            "Inicio/Aprovisionamiento/Detalle Gasto/Cargar"
    );
    // --------------------- DRIVERS ----------------------
    public static final RutaArchivos RUTAS_DRIVERS_CENTRO_LISTAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers/Listar.fxml",
            "controlador.aprovisionamiento.drivers.ListarControlador",
            "Drivers - Centros",
            "Inicio/Aprovisionamiento/Drivers - Centros de Costos"
    );
    public static final RutaArchivos RUTAS_DRIVERS_CENTRO_CREAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers/Crear.fxml",
            "controlador.aprovisionamiento.drivers.CrearControlador",
            "Crear Driver - Centro",
            "Inicio/Aprovisionamiento/Drivers - Centros de Costos/Crear"
    );
    public static final RutaArchivos RUTAS_DRIVERS_CENTRO_EDITAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers/Editar.fxml",
            "controlador.aprovisionamiento.drivers.EditarControlador",
            "Editar Driver - Centro",
            "Inicio/Aprovisionamiento/Drivers - Centros de Costos/Editar"
    );
    public static final RutaArchivos RUTAS_DRIVERS_CENTRO_CARGAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers/Cargar.fxml",
            "controlador.aprovisionamiento.drivers.CargarControlador",
            "Cargar Drivers - Centros",
            "Inicio/Aprovisionamiento/Drivers - Centros de Costos/Cargar"
    );
    // --------------------- DRIVERS OBJETO ----------------------
    public static final RutaArchivos RUTAS_DRIVERS_OBJETO_LISTAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers_objeto/Listar.fxml",
            "controlador.aprovisionamiento.drivers_objeto.ListarControlador",
            "Drivers - Objetos",
            "Inicio/Aprovisionamiento/Drivers - Objetos de Costos"
    );
    public static final RutaArchivos RUTAS_DRIVERS_OBJETO_CREAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers_objeto/Crear.fxml",
            "controlador.aprovisionamiento.drivers_objeto.CrearControlador",
            "Crear Driver - Objeto",
            "Inicio/Aprovisionamiento/Drivers - Objetos de Costos/Crear"
    );
    public static final RutaArchivos RUTAS_DRIVERS_OBJETO_EDITAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers_objeto/Editar.fxml",
            "controlador.aprovisionamiento.drivers_objeto.EditarControlador",
            "Editar Driver - Objeto",
            "Inicio/Aprovisionamiento/Drivers - Objetos de Costos/Editar"
    );
    public static final RutaArchivos RUTAS_DRIVERS_OBJETO_CARGAR = new RutaArchivos(
            "/vista/aprovisionamiento/drivers_objeto/Cargar.fxml",
            "controlador.aprovisionamiento.drivers_objeto.CargarControlador",
            "Cargar Drivers - Objetos",
            "Inicio/Aprovisionamiento/Drivers - Objetos de Costos/Cargar"
    );
    // =========================================================
    // ******************** MODALS *************
    // =========================================================
    public static final RutaArchivos RUTAS_MODALS_VER_DRIVER_CENTRO = new RutaArchivos(
            "/vista/modals/VerDriverCentro.fxml",
            "controlador.modals.VerDriverCentroControlador",
            "Ver Driver - Centro"
    );
    public static final RutaArchivos RUTAS_MODALS_VER_DRIVER_OBJETO = new RutaArchivos(
            "/vista/modals/VerDriverObjeto.fxml",
            "controlador.modals.VerDriverObjetoControlador",
            "Ver Driver - Objeto"
    );
    // ---------------------------------------------------------
    public static final RutaArchivos RUTAS_MODALS_BUSCAR_DRIVER_CENTRO = new RutaArchivos(
            "/vista/modals/BuscarDriverCentro.fxml",
            "controlador.modals.BuscarDriverCentroControlador",
            "Buscar Driver - Centro"
    );
    public static final RutaArchivos RUTAS_MODALS_BUSCAR_DRIVER_OBJETO = new RutaArchivos(
            "/vista/modals/BuscarDriverObjeto.fxml",
            "controlador.modals.BuscarDriverObjetoControlador",
            "Buscar Driver - Objeto"
    );
    // ---------------------------------------------------------
    public static final RutaArchivos RUTAS_MODALS_BUSCAR_ENTIDAD = new RutaArchivos(
            "/vista/modals/BuscarEntidad.fxml",
            "controlador.modals.BuscarEntidadControlador",
            "Buscar Entidad",
            "/Buscar Entidad"
    );
    // =========================================================
    // ******************** MODULO PARAMETRIZACION *************
    // =========================================================
    public static final RutaArchivos RUTAS_MODULO_PARAMETRIZACION = new RutaArchivos(
            "/vista/parametrizacion/Principal.fxml",
            "controlador.parametrizacion.PrincipalControlador",
            "PARAMETRIZACIÓN",
            "Inicio/Parametrización"
    );
    // --------------------- PLAN DE CUENTAS ----------------------
    public static final RutaArchivos RUTAS_PLANES_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/planes/Principal.fxml",
            "controlador.parametrizacion.planes.PrincipalControlador",
            "Cuentas Contables"
    );
    public static final RutaArchivos RUTAS_PLANES_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/planes/maestro/Listar.fxml",
            "controlador.parametrizacion.planes.maestro.ListarControlador",
            "Catálogo de Cuentas Contables",
            "Inicio/Parametrización/Cuentas Contables/Catálogo"
    );
    public static final RutaArchivos RUTAS_PLANES_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/planes/maestro/Crear.fxml",
            "controlador.parametrizacion.planes.maestro.CrearControlador",
            "Crear Cuenta Contable",
            "Inicio/Parametrización/Cuentas Contables/Catálogo/Crear"
    );
    public static final RutaArchivos RUTAS_PLANES_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/planes/maestro/Editar.fxml",
            "controlador.parametrizacion.planes.maestro.EditarControlador",
            "Editar Cuenta Contable",
            "Inicio/Parametrización/Cuentas Contables/Catálogo/Editar"
    );
    public static final RutaArchivos RUTAS_PLANES_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/planes/maestro/Cargar.fxml",
            "controlador.parametrizacion.planes.maestro.CargarControlador",
            "Cargar Cuentas Contables",
            "Inicio/Parametrización/Cuentas Contables/Catálogo/Cargar"
    );
    public static final RutaArchivos RUTAS_PLANES_ASIGNAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/planes/periodo/Listar.fxml",
            "controlador.parametrizacion.planes.periodo.ListarControlador",
            "Asociar Cuentas Contables a un periodo",
            "Inicio/Parametrización/Cuentas Contables"
    );
    public static final RutaArchivos RUTAS_PLANES_ASIGNAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/planes/periodo/Cargar.fxml",
            "controlador.parametrizacion.planes.periodo.CargarControlador",
            "Cargar asociaciones de Cuentas Contables a un periodo",
            "Inicio/Parametrización/Cuentas Contables/Cargar"
    );
    // --------------------- GRUPOS ----------------------
    public static final RutaArchivos RUTAS_GRUPOS_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/grupos/Principal.fxml",
            "controlador.parametrizacion.grupos.PrincipalControlador",
            "Grupos"
    );
    public static final RutaArchivos RUTAS_GRUPOS_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/grupos/maestro/Listar.fxml",
            "controlador.parametrizacion.grupos.maestro.ListarControlador",
            "Catálogo de Grupos",
            "Inicio/Parametrización/Grupos/Catálogo"
    );
    public static final RutaArchivos RUTAS_GRUPOS_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/grupos/maestro/Crear.fxml",
            "controlador.parametrizacion.grupos.maestro.CrearControlador",
            "Crear Grupo",
            "Inicio/Parametrización/Grupos/Catálogo/Crear"
    );
    public static final RutaArchivos RUTAS_GRUPOS_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/grupos/maestro/Editar.fxml",
            "controlador.parametrizacion.grupos.maestro.EditarControlador",
            "Editar Grupo",
            "Inicio/Parametrización/Grupos/Catálogo/Editar"
    );
    public static final RutaArchivos RUTAS_GRUPOS_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/grupos/maestro/Cargar.fxml",
            "controlador.parametrizacion.grupos.maestro.CargarControlador",
            "Cargar Grupos",
            "Inicio/Parametrización/Grupos/Catálogo/Cargar"
    );
    public static final RutaArchivos RUTAS_GRUPOS_ASOCIAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/grupos/periodo/Listar.fxml",
            "controlador.parametrizacion.grupos.periodo.ListarControlador",
            "Asociar Grupos a un periodo",
            "Inicio/Parametrización/Grupos"
    );
    public static final RutaArchivos RUTAS_GRUPOS_ASOCIAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/grupos/periodo/Cargar.fxml",
            "controlador.parametrizacion.grupos.periodo.CargarControlador",
            "Cargar asociaciones de Grupos a un periodo",
            "Inicio/Parametrización/Grupos/Cargar"
    );
     // --------------------- PARTIDAS ----------------------
    public static final RutaArchivos RUTAS_PARTIDAS_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/partidas/Principal.fxml",
            "controlador.parametrizacion.partidas.PrincipalControlador",
            "Partidas"
    );
    public static final RutaArchivos RUTAS_PARTIDAS_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/partidas/maestro/Listar.fxml",
            "controlador.parametrizacion.partidas.maestro.ListarControlador",
            "Catálogo de Partidas",
            "Inicio/Parametrización/Partidas/Catálogo"
    );
    public static final RutaArchivos RUTAS_PARTIDAS_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/partidas/maestro/Crear.fxml",
            "controlador.parametrizacion.partidas.maestro.CrearControlador",
            "Crear Partida",
            "Inicio/Parametrización/Partidas/Catálogo/Crear"
    );
    public static final RutaArchivos RUTAS_PARTIDAS_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/partidas/maestro/Editar.fxml",
            "controlador.parametrizacion.partidas.maestro.EditarControlador",
            "Editar Partida",
            "Inicio/Parametrización/Partidas/Catálogo/Editar"
    );
    public static final RutaArchivos RUTAS_PARTIDAS_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/partidas/maestro/Cargar.fxml",
            "controlador.parametrizacion.partidas.maestro.CargarControlador",
            "Cargar Partidas",
            "Inicio/Parametrización/Partidas/Catálogo/Cargar"
    );
    public static final RutaArchivos RUTAS_PARTIDAS_ASOCIAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/partidas/periodo/Listar.fxml",
            "controlador.parametrizacion.partidas.periodo.ListarControlador",
            "Asociar Partidas a un periodo",
            "Inicio/Parametrización/Partidas"
    );
    public static final RutaArchivos RUTAS_PARTIDAS_ASOCIAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/partidas/periodo/Cargar.fxml",
            "controlador.parametrizacion.partidas.periodo.CargarControlador",
            "Cargar asociaciones de Partidas a un periodo",
            "Inicio/Parametrización/Partidas/Cargar"
    );
    // --------------------- GRUPOS - CUENTA ----------------------
    public static final RutaArchivos RUTAS_GRUPO_CUENTA_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/grupo_cuenta/Listar.fxml",
            "controlador.parametrizacion.grupo_cuenta.ListarControlador",
            "Asignaciones de Grupos a Cuentas Contables",
            "Inicio/Parametrización/Asignaciones"
    );
    public static final RutaArchivos RUTAS_GRUPO_CUENTA_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/grupo_cuenta/Cargar.fxml",
            "controlador.parametrizacion.grupo_cuenta.CargarControlador",
            "Cargar Asignaciones de Grupos a Cuentas Contables",
            "Inicio/Parametrización/Asignaciones/Cargar"
    );
    // --------------------- CUENTA - PARTIDA ----------------------
    public static final RutaArchivos RUTAS_CUENTA_PARTIDA_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/cuenta_partida/Listar.fxml",
            "controlador.parametrizacion.cuenta_partida.ListarControlador",
            "Asignaciones de Cuenats Contables a Partidas",
            "Inicio/Parametrización/Asignaciones"
    );
    public static final RutaArchivos RUTAS_CUENTA_PARTIDA_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/cuenta_partida/Cargar.fxml",
            "controlador.parametrizacion.cuenta_partida.CargarControlador",
            "Cargar Asignaciones de Cuentas Contables a Partidas",
            "Inicio/Parametrización/Asignaciones/Cargar"
    );
    // --------------------- CENTROS ----------------------
    public static final RutaArchivos RUTAS_CENTROS_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/centros/Principal.fxml",
            "controlador.parametrizacion.centros.PrincipalControlador",
            "Centros Principal"
    );
    public static final RutaArchivos RUTAS_CENTROS_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/centros/maestro/Listar.fxml",
            "controlador.parametrizacion.centros.maestro.ListarControlador",
            "Centros Maestro Listar",
            "Inicio/Parametrización/Centros/Catálogo"
    );
    public static final RutaArchivos RUTAS_CENTROS_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/centros/maestro/Crear.fxml",
            "controlador.parametrizacion.centros.maestro.CrearControlador",
            "Centros Maestro Crear",
            "Inicio/Parametrización/Centros/Catálogo/Crear"
    );
    public static final RutaArchivos RUTAS_CENTROS_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/centros/maestro/Editar.fxml",
            "controlador.parametrizacion.centros.maestro.EditarControlador",
            "Centros Maestro Editar",
            "Inicio/Parametrización/Centros/Catálogo/Editar"
    );
    public static final RutaArchivos RUTAS_CENTROS_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/centros/maestro/Cargar.fxml",
            "controlador.parametrizacion.centros.maestro.CargarControlador",
            "Centros Maestro Cargar",
            "Inicio/Parametrización/Centros/Catálogo/Cargar"
    );
    public static final RutaArchivos RUTAS_CENTROS_ASIGNAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/centros/periodo/Listar.fxml",
            "controlador.parametrizacion.centros.periodo.ListarControlador",
            "Centros Asignar Periodo",
            "Inicio/Parametrización/Centros"
    );
    public static final RutaArchivos RUTAS_CENTROS_ASIGNAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/centros/periodo/Cargar.fxml",
            "controlador.parametrizacion.centros.periodo.CargarControlador",
            "Centros Asignar Periodo Cargar",
            "Inicio/Parametrización/Centros/Cargar"
    );
    // --------------------- OFICINAS ----------------------
    public static final RutaArchivos RUTAS_OFICINAS_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/oficinas/Principal.fxml",
            "controlador.parametrizacion.oficinas.PrincipalControlador",
            "Oficinas",
            "Inicio/Parametrización/Oficinas"
    );
    public static final RutaArchivos RUTAS_OFICINAS_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/maestro/Listar.fxml",
            "controlador.parametrizacion.oficinas.maestro.ListarControlador",
            "Catálogo de Oficinas",
            "Inicio/Parametrización/Oficinas/Catálogo"
    );
    public static final RutaArchivos RUTAS_OFICINAS_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/maestro/Crear.fxml",
            "controlador.parametrizacion.oficinas.maestro.CrearControlador",
            "Crear Oficina",
            "Inicio/Parametrización/Oficinas/Catálogo/Crear"
    );
    public static final RutaArchivos RUTAS_OFICINAS_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/maestro/Editar.fxml",
            "controlador.parametrizacion.oficinas.maestro.EditarControlador",
            "Editar Oficina",
            "Inicio/Parametrización/Oficinas/Catálogo/Editar"
    );
    public static final RutaArchivos RUTAS_OFICINAS_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/maestro/Cargar.fxml",
            "controlador.parametrizacion.oficinas.maestro.CargarControlador",
            "Cargar Oficinas",
            "Inicio/Parametrización/Oficinas/Catálogo/Cargar"
    );
    public static final RutaArchivos RUTAS_OFICINAS_ASIGNAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/oficinas/periodo/Listar.fxml",
            "controlador.parametrizacion.oficinas.periodo.ListarControlador",
            "Asociar Oficinas a un periodo",
            "Inicio/Parametrización/Oficinas/Asociación"
    );
    public static final RutaArchivos RUTAS_OFICINAS_ASIGNAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/periodo/Cargar.fxml",
            "controlador.parametrizacion.oficinas.periodo.CargarControlador",
            "Cargar asociaciones de Oficinas a un periodo",
            "Inicio/Parametrización/Oficinas/Asociación/Cargar"
    );
    public static final RutaArchivos RUTAS_OFICINAS_GRUPOS_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/grupos/Listar.fxml",
            "controlador.parametrizacion.oficinas.grupos.ListarControlador",
            "Catálogo de Grupos de Oficinas",
            "Inicio/Parametrización/Oficinas/Grupos"
    );
    public static final RutaArchivos RUTAS_OFICINAS_GRUPOS_CREAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/grupos/Crear.fxml",
            "controlador.parametrizacion.oficinas.grupos.CrearControlador",
            "Crear Grupo de Oficinas",
            "Inicio/Parametrización/Oficinas/Grupos/Crear"
    );
    public static final RutaArchivos RUTAS_OFICINAS_GRUPOS_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/grupos/Editar.fxml",
            "controlador.parametrizacion.oficinas.grupos.EditarControlador",
            "Editar Grupo de Oficinas",
            "Inicio/Parametrización/Oficinas/Grupos/Editar"
    );
    public static final RutaArchivos RUTAS_OFICINAS_GRUPOS_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/grupos/Cargar.fxml",
            "controlador.parametrizacion.oficinas.grupos.CargarControlador",
            "Cargar Grupos de Oficinas",
            "Inicio/Parametrización/Oficinas/Grupos/Cargar"
    );
    public static final RutaArchivos RUTAS_OFICINAS_JERARQUIA = new RutaArchivos(
            "/vista/parametrizacion/oficinas/jerarquia/Listar.fxml",
            "controlador.parametrizacion.oficinas.jerarquia.ListarControlador",
            "Jerarquia de Oficinas",
            "Inicio/Parametrización/Oficinas/Jerarquía"
    );
    public static final RutaArchivos RUTAS_OFICINAS_JERARQUIA_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/oficinas/jerarquia/Cargar.fxml",
            "controlador.parametrizacion.oficinas.jerarquia.CargarControlador",
            "Cargar Jerarquía de Oficinas",
            "Inicio/Parametrización/Oficinas/Jerarquía/Cargar"
    );
    // --------------------- PRODUCTOS ----------------------
    public static final RutaArchivos RUTAS_PRODUCTOS_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/productos/Principal.fxml",
            "controlador.parametrizacion.productos.PrincipalControlador",
            "Productos",
            "Inicio/Parametrización/Productos"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/productos/maestro/Listar.fxml",
            "controlador.parametrizacion.productos.maestro.ListarControlador",
            "Catálogo de Productos",
            "Inicio/Parametrización/Productos/Catálogo"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/productos/maestro/Crear.fxml",
            "controlador.parametrizacion.productos.maestro.CrearControlador",
            "Crear Producto",
            "Inicio/Parametrización/Productos/Catálogo/Crear"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/productos/maestro/Editar.fxml",
            "controlador.parametrizacion.productos.maestro.EditarControlador",
            "Editar Producto",
            "Inicio/Parametrización/Productos/Catálogo/Editar"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/productos/maestro/Cargar.fxml",
            "controlador.parametrizacion.productos.maestro.CargarControlador",
            "Cargar Productos",
            "Inicio/Parametrización/Productos/Catálogo/Cargar"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_ASIGNAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/productos/periodo/Listar.fxml",
            "controlador.parametrizacion.productos.periodo.ListarControlador",
            "Asociar Productos a un periodo",
            "Inicio/Parametrización/Productos/Asociación"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_ASIGNAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/productos/periodo/Cargar.fxml",
            "controlador.parametrizacion.productos.periodo.CargarControlador",
            "Cargar asociaciones de Productos a un periodo",
            "Inicio/Parametrización/Productos/Asociación/Cargar"
    );    
    public static final RutaArchivos RUTAS_PRODUCTOS_GRUPOS_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/productos/grupos/Listar.fxml",
            "controlador.parametrizacion.productos.grupos.ListarControlador",
            "Catálogo de Grupos de Productos",
            "Inicio/Parametrización/Productos/Grupos"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_GRUPOS_CREAR = new RutaArchivos(
            "/vista/parametrizacion/productos/grupos/Crear.fxml",
            "controlador.parametrizacion.productos.grupos.CrearControlador",
            "Crear Grupo de Productos",
            "Inicio/Parametrización/Productos/Grupos/Crear"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_GRUPOS_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/productos/grupos/Editar.fxml",
            "controlador.parametrizacion.productos.grupos.EditarControlador",
            "Editar Grupo de Productos",
            "Inicio/Parametrización/Productos/Grupos/Editar"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_GRUPOS_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/productos/grupos/Cargar.fxml",
            "controlador.parametrizacion.productos.grupos.CargarControlador",
            "Cargar Grupos de Productos",
            "Inicio/Parametrización/Productos/Grupos/Cargar"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_JERARQUIA = new RutaArchivos(
            "/vista/parametrizacion/productos/jerarquia/Listar.fxml",
            "controlador.parametrizacion.productos.jerarquia.ListarControlador",
            "Jerarquia de Productos",
            "Inicio/Parametrización/Productos/Jerarquía"
    );
    public static final RutaArchivos RUTAS_PRODUCTOS_JERARQUIA_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/productos/jerarquia/Cargar.fxml",
            "controlador.parametrizacion.productos.jerarquia.CargarControlador",
            "Cargar Jerarquía de Productos",
            "Inicio/Parametrización/Productos/Jerarquía/Cargar"
    );
    // --------------------- PRODUCTOS ----------------------
    public static final RutaArchivos RUTAS_OBJETOS_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/objetos/Principal.fxml",
            "controlador.parametrizacion.objetos.PrincipalControlador",
            "Productos",
            "Inicio/Parametrización/Objetos"
    );
    public static final RutaArchivos RUTAS_OBJETOS_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/maestro/Listar.fxml",
            "controlador.parametrizacion.objetos.maestro.ListarControlador",
            "Catálogo de Productos",
            "Inicio/Parametrización/Objetos/Asociación/Catálogo"
    );
    public static final RutaArchivos RUTAS_OBJETOS_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/maestro/Crear.fxml",
            "controlador.parametrizacion.objetos.maestro.CrearControlador",
            "Crear Producto",
            "Inicio/Parametrización/Objetos/Asociación/Catálogo/Crear"
    );
    public static final RutaArchivos RUTAS_OBJETOS_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/maestro/Editar.fxml",
            "controlador.parametrizacion.objetos.maestro.EditarControlador",
            "Editar Producto",
            "Inicio/Parametrización/Objetos/Asociación/Catálogo/Editar"
    );
    public static final RutaArchivos RUTAS_OBJETOS_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/maestro/Cargar.fxml",
            "controlador.parametrizacion.objetos.maestro.CargarControlador",
            "Cargar Productos",
            "Inicio/Parametrización/Objetos/Asociación/Catálogo/Cargar"
    );
    public static final RutaArchivos RUTAS_OBJETOS_ASIGNAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/objetos/periodo/Listar.fxml",
            "controlador.parametrizacion.objetos.periodo.ListarControlador",
            "Asociar Productos a un periodo",
            "Inicio/Parametrización/Objetos/Asociación"
    );
    public static final RutaArchivos RUTAS_OBJETOS_ASIGNAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/periodo/Cargar.fxml",
            "controlador.parametrizacion.objetos.periodo.CargarControlador",
            "Cargar asociaciones de Productos a un periodo",
            "Inicio/Parametrización/Objetos/Asociación/Cargar"
    );    
    public static final RutaArchivos RUTAS_OBJETOS_GRUPOS_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/grupos/Listar.fxml",
            "controlador.parametrizacion.objetos.grupos.ListarControlador",
            "Catálogo de Grupos de Productos",
            "Inicio/Parametrización/Objetos/Jerarquía/Grupos"
    );
    public static final RutaArchivos RUTAS_OBJETOS_GRUPOS_CREAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/grupos/Crear.fxml",
            "controlador.parametrizacion.objetos.grupos.CrearControlador",
            "Crear Grupo de Productos",
            "Inicio/Parametrización/Objetos/Jerarquía/Grupos/Crear"
    );
    public static final RutaArchivos RUTAS_OBJETOS_GRUPOS_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/grupos/Editar.fxml",
            "controlador.parametrizacion.objetos.grupos.EditarControlador",
            "Editar Grupo de Productos",
            "Inicio/Parametrización/Objetos/Jerarquía/Grupos/Editar"
    );
    public static final RutaArchivos RUTAS_OBJETOS_GRUPOS_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/grupos/Cargar.fxml",
            "controlador.parametrizacion.objetos.grupos.CargarControlador",
            "Cargar Grupos de Productos",
            "Inicio/Parametrización/Objetos/Jerarquía/Grupos/Cargar"
    );
    public static final RutaArchivos RUTAS_OBJETOS_JERARQUIA = new RutaArchivos(
            "/vista/parametrizacion/objetos/jerarquia/Listar.fxml",
            "controlador.parametrizacion.objetos.jerarquia.ListarControlador",
            "Jerarquia de Productos",
            "Inicio/Parametrización/Objetos/Jerarquía"
    );
    public static final RutaArchivos RUTAS_OBJETOS_JERARQUIA_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/objetos/jerarquia/Cargar.fxml",
            "controlador.parametrizacion.objetos.jerarquia.CargarControlador",
            "Cargar Jerarquía de Productos",
            "Inicio/Parametrización/Objetos/Jerarquía/Cargar"
    );
    // --------------------- BANCAS ----------------------
    public static final RutaArchivos RUTAS_BANCAS_PRINCIPAL = new RutaArchivos(
            "/vista/parametrizacion/bancas/Principal.fxml",
            "controlador.parametrizacion.bancas.PrincipalControlador",
            "Bancas",
            "Inicio/Parametrización/Bancas"
    );
    public static final RutaArchivos RUTAS_BANCAS_MAESTRO_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/maestro/Listar.fxml",
            "controlador.parametrizacion.bancas.maestro.ListarControlador",
            "Catálogo de Bancas",
            "Inicio/Parametrización/Bancas/Catálogo"
    );
    public static final RutaArchivos RUTAS_BANCAS_MAESTRO_CREAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/maestro/Crear.fxml",
            "controlador.parametrizacion.bancas.maestro.CrearControlador",
            "Crear Banca"
    );
    public static final RutaArchivos RUTAS_BANCAS_MAESTRO_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/maestro/Editar.fxml",
            "controlador.parametrizacion.bancas.maestro.EditarControlador",
            "Editar Banca"
    );
    public static final RutaArchivos RUTAS_BANCAS_MAESTRO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/maestro/Cargar.fxml",
            "controlador.parametrizacion.bancas.maestro.CargarControlador",
            "Cargar Bancas"
    );
    public static final RutaArchivos RUTAS_BANCAS_ASIGNAR_PERIODO = new RutaArchivos(
            "/vista/parametrizacion/bancas/periodo/Listar.fxml",
            "controlador.parametrizacion.bancas.periodo.ListarControlador",
            "Asociar Bancas a un periodo"
    );
    public static final RutaArchivos RUTAS_BANCAS_ASIGNAR_PERIODO_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/periodo/Cargar.fxml",
            "controlador.parametrizacion.bancas.periodo.CargarControlador",
            "Cargar asociaciones de Bancas a un periodo"
    );
    public static final RutaArchivos RUTAS_BANCAS_GRUPOS_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/grupos/Listar.fxml",
            "controlador.parametrizacion.bancas.grupos.ListarControlador",
            "Catálogo de Grupos de Bancas"
     
    );
    public static final RutaArchivos RUTAS_BANCAS_GRUPOS_CREAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/grupos/Crear.fxml",
            "controlador.parametrizacion.bancas.grupos.CrearControlador",
            "Crear Grupo de Bancas"
    );
    public static final RutaArchivos RUTAS_BANCAS_GRUPOS_EDITAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/grupos/Editar.fxml",
            "controlador.parametrizacion.bancas.grupos.EditarControlador",
            "Editar Grupo de Bancas"
    );
    public static final RutaArchivos RUTAS_BANCAS_GRUPOS_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/grupos/Cargar.fxml",
            "controlador.parametrizacion.bancas.grupos.CargarControlador",
            "Cargar Grupos de Bancas"
    );
    public static final RutaArchivos RUTAS_BANCAS_JERARQUIA = new RutaArchivos(
            "/vista/parametrizacion/bancas/jerarquia/Listar.fxml",
            "controlador.parametrizacion.bancas.jerarquia.ListarControlador",
            "Jerarquia de Bancas"
    );
    public static final RutaArchivos RUTAS_BANCAS_JERARQUIA_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/bancas/jerarquia/Cargar.fxml",
            "controlador.parametrizacion.bancas.jerarquia.CargarControlador",
            "Cargar Jerarquía de Bancas"
    );
    // --------------------- ENTIDAD - DRIVER ----------------------
    public static final RutaArchivos RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/driver_entidad/centros_centros/Listar.fxml",
            "controlador.parametrizacion.driver_entidad.centros_centros.ListarControlador",
            "Asignaciones de Drivers de Centros a Centros",
            "Inicio/Parametrización/Asignaciones Drivers Centros - Centros"
    );
    public static final RutaArchivos RUTAS_DRIVER_ENTIDAD_CENTROS_CENTROS_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/driver_entidad/centros_centros/Cargar.fxml",
            "controlador.parametrizacion.driver_entidad.centros_centros.CargarControlador",
            "Cargar Asignaciones de Drivers de Centros a Centros",
            "Inicio/Parametrización/Asignaciones Drivers Centros - Centros/Cargar"
    );
    public static final RutaArchivos RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/driver_entidad/centros_bolsas/Listar.fxml",
            "controlador.parametrizacion.driver_entidad.centros_bolsas.ListarControlador",
            "Asignaciones de Drivers de Centros a Bolsas",
            "Inicio/Parametrización/Asignaciones Drivers Centros - Bolsas"
    );
    public static final RutaArchivos RUTAS_DRIVER_ENTIDAD_CENTROS_BOLSAS_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/driver_entidad/centros_bolsas/Cargar.fxml",
            "controlador.parametrizacion.driver_entidad.centros_bolsas.CargarControlador",
            "Cargar Asignaciones de Drivers de Centros a Bolsas",
            "Inicio/Parametrización/Asignaciones Drivers Centros - Bolsas/Cargar"
    );
    public static final RutaArchivos RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_LISTAR = new RutaArchivos(
            "/vista/parametrizacion/driver_entidad/centros_objetos/Listar.fxml",
            "controlador.parametrizacion.driver_entidad.centros_objetos.ListarControlador",
            "Asignaciones de Drivers de Centros a Objetos",
            "Inicio/Parametrización/Asignaciones Drivers Centros - Objetos"
    );
    public static final RutaArchivos RUTAS_DRIVER_ENTIDAD_CENTROS_OBJETOS_CARGAR = new RutaArchivos(
            "/vista/parametrizacion/driver_entidad/centros_objetos/Cargar.fxml",
            "controlador.parametrizacion.driver_entidad.centros_objetos.CargarControlador",
            "Cargar Asignaciones de Drivers de Centros a Objetos",
            "Inicio/Parametrización/Asignaciones Drivers Centros - Objetos/Cargar"
    );
    // =========================================================
    // ******************** MODULO PROCESOS ********************
    // =========================================================
    public static final RutaArchivos RUTAS_MODULO_PROCESOS = new RutaArchivos(
            "/vista/procesos/Principal.fxml",
            "controlador.procesos.PrincipalControlador",
            "PROCESOS",
            "Inicio/Procesos"
    );
    // =========================================================
    // ******************** MODULO REPORTING ********************
    // =========================================================
    public static final RutaArchivos RUTAS_MODULO_REPORTING = new RutaArchivos(
            "/vista/reporting/Principal.fxml",
            "controlador.reporting.PrincipalControlador",
            "REPORTING",
            "Inicio/Reporting"
    );
    
    public void omitirFilas(Iterator<Row> filas, int numFilas) {
        for (int i=0;i<numFilas;++i)filas.next();
    }
    
    public boolean validarFila(Row fila, List<String> lista) {
        /*Iterator<Cell> celdas = fila.cellIterator();
        List<String> listaLeida = new ArrayList();
        while (celdas.hasNext()) {            
            Cell celda = celdas.next();
            celda.setCellType(CellType.STRING);
            String campo = celda.getStringCellValue();
            listaLeida.add(campo);
        }*/
        List<String> listaLeida = new ArrayList();
        for(Cell c: fila)
            listaLeida.add(c.getStringCellValue());
        return listaLeida.subList(0, lista.size()).equals(lista);
    }
    
    public boolean validarFilaNormal(Row fila, List<String> lista) {
        Iterator<Cell> celdas = fila.cellIterator();
        List<String> listaLeida = new ArrayList();
        if (fila.getPhysicalNumberOfCells() == lista.size()){
            while (celdas.hasNext()) {            
                Cell celda = celdas.next();
                celda.setCellType(CellType.STRING);
                listaLeida.add(celda.getStringCellValue());
            }
        }else{
            return false;
        }
        return listaLeida.subList(0, lista.size()).equals(lista);
    }
    
// =======================================================================================
// ******************** MENSAJES DE ERROR, CONFIRMACION E INFORMATIVO ********************
// =======================================================================================
    public void mensajeCarga(String titulo, int nroError) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        // Si no se presentan errores en la validación presentará el primer mensaje
        if(nroError == 0 ){
            alert.setContentText("El archivo ha sido cargado correctamente.");
        }else {
            alert.setContentText("El archivo presenta "+ Integer.toString(nroError) + " conflictos al cargar. Se le sugiere DESCARGAR el LOG para más detalle." );
        }
        alert.showAndWait();
    }

    public void mensajeError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        switch(contenido){
            case "UPLOAD_ERROR_PERIODO":
                alert.setTitle("Subida de archivo Excel");
                alert.setContentText("Presenta inconsistencia con el Periodo a cargar. \\Por favor, revise el documento a cargar.");
                break; 
            case "UPLOAD_HEADER":
                alert.setTitle("Cargar " + titulo);
                alert.setContentText("La cabecera de la hoja no es la correcta.\nNo se puede cargar el archivo.");
                break;
            case "DOWNLOAD":
                alert.setTitle("Descargar "+ titulo);
                alert.setContentText(contenido);
                break;
            case "DELETE_SELECTED":
                alert.setTitle("Quitar " + titulo);
                alert.setContentText("Por favor seleccione una Cuenta Contable.");
                break;
            case "DELETE_REFRESH":
                alert.setTitle("Quitar " + titulo);
                alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
                break;
            case "DELETE_ITEM":
                alert.setTitle("Quitar " + titulo);
                alert.setContentText("No se pudo eliminar "+ titulo +" item del Periodo. Está siendo utilizada en otros módulos.\nPara eliminarla, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizada.");
                break;
            case "TABLE_EMPTY":
                alert.setTitle("Consulta "+ titulo);
                alert.setContentText("No existen "+ titulo+ " para el periodo seleccionado.");
                break;
            case "ADD_REFRESH":
                alert.setTitle("Agregar "+ titulo);
                alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
                break;
            default:    
                alert.setTitle(titulo);
                alert.setContentText(contenido);
        }
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    public void mensajeError(String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        switch(contenido){
            case "UPLOAD_ERROR_PERIODO":
                alert.setTitle("Subida de archivo Excel");
                alert.setContentText("Presenta inconsistencia con el Periodo a cargar. \\Por favor, revise el documento a cargar.");
                break;
            case "UPLOAD_EMPTY":
                alert.setTitle("Subir Información");
                alert.setContentText("No hay información.");
                break;
            case "DOWNLOAD_EMPTY":
                alert.setTitle("Descargar información");
                alert.setContentText("No hay información");
                break;
            case "DOWNLOAD_CANCELED":
                alert.setTitle("Descargar información");
                alert.setContentText("Descarga Cancelada");
                break;
            case "SELECT_ENTITY":
                alert.setTitle("Seleccionar entidad");
                alert.setContentText("No seleccionó ninguna entidad.");
                break;
            default:
                alert.setTitle("");
                alert.setContentText(contenido);
        }
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    public void mensajeInformativo(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        switch(contenido){
            case "UPDATE_REFRESH":
                alert.setTitle("Actualizar "+ titulo);
                alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
                break;
            case "UPDATE_EMPTY":
                alert.setTitle("Actualizar "+ titulo);
                alert.setContentText("Por favor seleccione item.");
                break;
            case "UPLOAD":
                alert.setTitle("Subida de archivo Excel");
                alert.setContentText(titulo +  " asignados correctamente.");
                break;
            case "UPLOAD_HEADER":
                alert.setTitle("Cargar " + titulo);
                alert.setContentText("La cabecera de la hoja no es la correcta.\nNo se puede cargar el archivo.");
                break;
            case "UPLOAD_SUCCESS_ERROR":
                alert.setTitle("Subida de información " + titulo);
                alert.setContentText(titulo + " subidas. Se presentaron algunos errores. \nPara mayor información Descargar LOG.");
                break;
            case "UPLOAD_ALLCHARGED_YET":
                alert.setTitle("Subida de información " + titulo);
                alert.setContentText("Toda La información ya está cargada.");
                break;
            case "UPLOAD_ITEM_DONTEXIST":
                alert.setTitle("Subida de información " + titulo);
                alert.setContentText("Los items no existen en su respectivo Catálogo");
                break;
            case "DOWNLOAD":
                alert.setTitle("Descargar "+ titulo);
                alert.setContentText(contenido);
                break;
            case "DELETE_EMPTY":
                alert.setTitle("Eliminar " + titulo);
                alert.setContentText("Por favor seleccione item");
                break;
            case "DELETE_SELECTED":
                alert.setTitle("Eliminar " + titulo);
                alert.setContentText("Por favor seleccione "+ titulo);
                break;
            case "DELETE_REFRESH":
                alert.setTitle("Eliminar " + titulo);
                alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
                break;
            case "DELETE_ITEM":
                alert.setTitle("Eliminar " + titulo);
                alert.setContentText("No se pudo eliminar "+ titulo +" item del Periodo. Está siendo utilizada en otros módulos.\nPara eliminarla, primero debe quitar las asociaciones/asignaciones donde esté siendo utilizada.");
                break;
            case "DELETE_SUCCESS":
                alert.setTitle("Eliminar " + titulo);
                alert.setContentText( titulo +" eliminado correctamente.");
                break;
            case "TABLE_EMPTY":
                alert.setTitle("Consulta "+ titulo);
                alert.setContentText("No existen "+ titulo+ " para el periodo seleccionado.");
                break;
            case "ADD_REFRESH":
                alert.setTitle("Agregar "+ titulo);
                alert.setContentText("Se realizó un cambio en el periodo y no en la tabla. Por favor haga click en el botón Buscar para continuar.");
                break;
            case "ADD_EMPTY":
                alert.setTitle("Agregar "+ titulo);
                alert.setContentText("Por favor seleccione item.");
                break;
            case "EDIT_EMPTY":
                alert.setTitle("Editar "+ titulo);
                alert.setContentText("Por favor seleccione item.");
                break;
            case "EDIT_SUCCESS":
                alert.setTitle("Editar "+ titulo);
                alert.setContentText(titulo +" editada correctamente.");
                break;
            case "EDIT_ERROR":
                alert.setTitle("Editar "+ titulo);
                alert.setContentText("No se pudo editar " + titulo +".");
                break;
            case "CREATE_ITEM_EXIST":
                alert.setTitle("Crear "+ titulo);
                alert.setContentText("El código de " + titulo + " ya existe. No se pudo crear el item.");
                break;
            case "CREATE_ITEM_PATTERN":
                alert.setTitle("Crear "+ titulo);
                alert.setContentText("El código de " + titulo + " no cumple con el patrón establecido.");
                break;
            case "CREATE_SUCCESS":
                alert.setTitle("Crear "+ titulo);
                alert.setContentText(titulo +" creado correctamente.");
                break;
            case "CREATE_ERROR":
                alert.setTitle("Crear "+ titulo);
                alert.setContentText("Error. No se pudo crear " + titulo);
                break;
            case "EDIT_ITEM_WITHOUT_ALLOCATE":
                alert.setTitle("Editar "+ titulo);
                alert.setContentText("No se puede editar item " + titulo +", pues no ha sido asignado previamente.");
                break;
            case "PHASE1_BOLSAS_WITHOUT_DRIVERS":
                alert.setTitle("FASE 1");
                alert.setContentText("Existe(n) " + titulo +"Centros Bolsas sin Driver asignado.\n\nPor favor, revise el módulo de Asignaciones y asegúrese que todos los Centros Bolsas tengan un Driver.");
                break; 
            default:
                alert.setTitle(titulo);
                alert.setContentText(contenido);
        }
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    public void mensajeInformativo(String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        switch(contenido){
            case "DOWNLOAD":
                alert.setTitle("Descargar información");
                alert.setContentText("Descargar completa.");
                break;
            case "DOWNLOAD_LOG":
                alert.setTitle("Guardar LOG");
                alert.setContentText("Descarga completa.");
                break;
            case "DOWNLOAD_CANCELED":
                alert.setTitle("Descargar información");
                alert.setContentText("Descarga Cancelada");
                break;
            case "SELECT_ENTITY":
                alert.setTitle("Seleccionar entidad");
                alert.setContentText("No seleccionó ninguna entidad.");
                break;
            case "DOWNLOAD_EMPTY":
                alert.setTitle("Descargar información");
                alert.setContentText("No hay información");
                break;
            case "UPLOAD_ERROR_PERIODO":
                alert.setTitle("Subida de archivo Excel");
                alert.setContentText("Presenta inconsistencia con el Periodo a cargar. \\Por favor, revise el documento a cargar.");
                break;
            case "UPLOAD_EMPTY":
                alert.setTitle("Subir Información");
                alert.setContentText("No hay información.");
                break;
            case "UPLOAD_SUCCESS":
                alert.setTitle("Subir Información");
                alert.setContentText("La información se subió correctamente.");
                break;
            default:
                alert.setTitle("");
                alert.setContentText(contenido);
        }
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    public boolean mensajeConfirmar(String titulo, String contenido) {
        ButtonType btnSi = new ButtonType("Sí", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.WARNING,contenido,btnSi,btnNo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        
        return result.orElse(btnNo) == btnSi;
    }
        
    public void cambiarVista(RutaArchivos rutas) {
        URL url  = Navegador.class.getResource(rutas.getVista());
        FXMLLoader fxmlLoader = new FXMLLoader(url);        
        try {
            Class<?> c = Class.forName(rutas.getControlador());
            Constructor<?> cons = c.getConstructor(MenuControlador.class);
            Object object = cons.newInstance(menuControlador);
            fxmlLoader.setController(object);
            menuControlador.setVista(fxmlLoader.load());
        } catch(IOException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
        if(rutas.getDireccion().contains("Inicio/Parametrización/Objetos/")){
            String obj = null;
            switch(menuControlador.objetoTipo){
                case "SCA":
                    obj = "Subcanales";
                    break;
                case "PRO":
                    obj = "Productos";
                    break;
                default:
                    obj = "objetos";
                    break;
            }
            String ruta = rutas.getDireccion().replace("/Objetos/", "/"+obj+"/");
            menuControlador.Log.cambioVistaLog(LOGGER,menuControlador.usuario.getUsername(), ruta);
        }else{
            menuControlador.Log.cambioVistaLog(LOGGER,menuControlador.usuario.getUsername(), rutas.getDireccion());
        }
    }
}
