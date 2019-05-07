package modelo;

import dao.SeguridadDAO;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {
    final StringProperty username;
    final StringProperty password;
    final StringProperty nombres;
    final StringProperty apellidos;
    final ObjectProperty<Rol> rol;
    final SeguridadDAO seguridadDAO;
    
    public Usuario(String username, String password, String nombres, String apellidos, Rol rol) {
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.nombres = new SimpleStringProperty(nombres);
        this.apellidos = new SimpleStringProperty(apellidos);
        this.rol = new SimpleObjectProperty(rol);
        this.seguridadDAO = new SeguridadDAO();
    }

    public StringProperty usernameProperty() {
        return username;
    }
    
    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty passwordProperty() {
        return password;
    }
    
    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
    
    public StringProperty nombresProperty() {
        return nombres;
    }
    
    public String getNombres() {
        return nombres.get();
    }

    public void setNombres(String nombres) {
        this.nombres.set(nombres);
    }

    public StringProperty apellidosProperty() {
        return apellidos;
    }
    
    public String getApellidos() {
        return apellidos.get();
    }

    public void setApellidos(String apellidos) {
        this.apellidos.set(apellidos);
    }
    
    public ObjectProperty<Rol> rolProperty() {
        return rol;
    }
    
    public Rol getRol() {
        return rol.get();
    }

    public void setRol(Rol rol) {
        this.rol.set(rol);
    }
    
    public boolean puede(String permisoCodigo) {
        return seguridadDAO.obtenerPermisoRol(permisoCodigo,rol.get().getCodigo()) != null;
    }
}
