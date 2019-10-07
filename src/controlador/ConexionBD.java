package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    private final static Logger LOGGER = Logger.getLogger("app.controlador.ConexionBD");
    public static Connection connection;
    public static String url;
    public static String usuario;
    public static String contrasenha;
    public static String mensaje;
    private static Statement statement;
    private static int tamanhoBatchActual;
    public static int tamanhoBatchMax = 50;

    public static void EstablecerParametros(String servidor, String puerto, String sid, String usuario, String contrasenha) {
        ConexionBD.url = String.format("jdbc:oracle:thin:@%s:%s/%s",servidor,puerto,sid);
        ConexionBD.usuario = usuario;
        ConexionBD.contrasenha = contrasenha;
    }
    
    public static void obtenerConexionBD() {
        try {
            connection = DriverManager.getConnection(url,usuario,contrasenha);
            LOGGER.log(Level.INFO, "Se realizó correctamente la conexión a base de datos.");
        } catch (SQLException sqlEx) {
            connection = null;
            String msj1 = "SQLException occured. getErrorCode=> " + sqlEx.getErrorCode();
            String msj2 = "SQLException occured. getCause=> " + sqlEx.getSQLState();
            String msj3 = "SQLException occured. getMessage=> " + sqlEx.getMessage();
            
            mensaje = String.format("%s\n%s\n%s\n",msj1,msj2,msj3);
            System.out.println(msj1);
            System.out.println(msj2);
            System.out.println(msj3);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void CerrarConexionBD() {
        try {
            connection.close();
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause());
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static ResultSet ejecutarQuery(String queryStr) {
        if (connection==null) {
            ConexionBD.obtenerConexionBD();
        }
        ResultSet rs = null;
        try {
            Statement stmt = connection.createStatement();
            rs = stmt.executeQuery(queryStr);
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause());
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //LOGGER.log(Level.INFO, String.format("Se ejecutó el query:\n%s",queryStr));
        return rs;
    }
    
    public static int borrarTabla(String tableName) {
        if (connection==null) {
            ConexionBD.obtenerConexionBD();
        }
        int result = -1;
        try (Statement stmt = connection.createStatement()) {
            result = stmt.executeUpdate("DROP TABLE " + tableName);
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause());
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getErrorCode=> %s\n",sqlEx.getErrorCode()));
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getCause=> %s\n",sqlEx.getSQLState()));
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getCause=> %s\n",sqlEx.getCause()));
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getMessage=> %s\n",sqlEx.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            LOGGER.log(Level.INFO, String.format("Exception: %s",e.getMessage()));
            return -1;
        }
        //LOGGER.log(Level.INFO, String.format("Se ejecutó el query:\n%s",queryStr));
        return result;
    }
    
    public static int ejecutar(String queryStr) {
        if (connection==null) {
            ConexionBD.obtenerConexionBD();
        }
        int result = -1;
        try (Statement stmt = connection.createStatement()) {
            result = stmt.executeUpdate(queryStr);
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause());
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getErrorCode=> %s\n",sqlEx.getErrorCode()));
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getCause=> %s\n",sqlEx.getSQLState()));
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getCause=> %s\n",sqlEx.getCause()));
            LOGGER.log(Level.WARNING, String.format("SQLException occured. getMessage=> %s\n",sqlEx.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            LOGGER.log(Level.INFO, String.format("Exception: %s",e.getMessage()));
        }
        //LOGGER.log(Level.INFO, String.format("Se ejecutó el query:\n%s",queryStr));
        return result;
    }
    
    public static void crearStatement() {
        try {
            statement = connection.createStatement();
            tamanhoBatchActual = 0;
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void cerrarStatement() {
        try {
            connection.setAutoCommit(true);
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void agregarBatch(String queryStr) {
        try {
            statement.addBatch(queryStr);
            ++tamanhoBatchActual;
            if (tamanhoBatchActual == tamanhoBatchMax) {
                ejecutarBatch();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        //LOGGER.log(Level.INFO, String.format("Se ejecutó el query:\n%s",queryStr));
    }
    
    public static void ejecutarBatch() {
        try {
            tamanhoBatchActual = 0;
            statement.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
