package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.ConnectionDB;
import modelo.Tipo;

public class EntidadTipoDAO {
    ConnectionDB connection;

    public EntidadTipoDAO() {
        this.connection = new ConnectionDB();
    }
    
    public List<Tipo> leerTipos() {
        List<Tipo> lista = new ArrayList();
        Connection access = connection.getConnection();
        try {
            String queryStr = String.format("SELECT codigo,nombre FROM entidad_tipos");
            ResultSet rs = access.createStatement().executeQuery(queryStr);            
            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                Tipo tipo = new Tipo(codigo,nombre,null);
                lista.add(tipo);
            }
        } catch (SQLException sqlEx) {
            System.out.println("SQLException occured. getErrorCode=> " + sqlEx.getErrorCode());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getSQLState());
            System.out.println("SQLException occured. getCause=> " + sqlEx.getCause() );
            System.out.println("SQLException occured. getMessage=> " + sqlEx.getMessage());
        }
        return lista;
    }
}
