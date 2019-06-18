package controlador;

import java.util.prefs.Preferences;

public class Preferencias {
    private final Preferences prefs;
    private final String ParametroServidor = "Servidor";
    private final String ParametroPuerto = "Puerto";
    private final String ParametroSID = "SID";
    private final String ParametroUsuario = "Usuario";
    private final String ParametroContrasenha = "Contrasenha";
    private final String ParametroRutaLogs = "Logs";
    
    public Preferencias() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }
    
    public String obtenerServidor() {
        return prefs.get(ParametroServidor, "localhost");
    }
    
    public String obtenerPuerto() {
        return prefs.get(ParametroPuerto, "1521");
    }
    
    public String obtenerSID() {
        return prefs.get(ParametroSID, "orcl.SCMS.ms.corp");
    }
    
    public String obtenerUsuario() {
        return prefs.get(ParametroUsuario, "a20121248");
    }
    
    public String obtenerContrasenha() {
        return prefs.get(ParametroContrasenha, "fibonacci");
    }    
    
    public String obtenerRutaLogs() {
        return prefs.get(ParametroRutaLogs, "./Logs/");
    }
    public void guardarParametros(String servidor, String puerto, String sid, String usuario, String contrasenha) {
        prefs.put(ParametroServidor, servidor);
        prefs.put(ParametroPuerto, puerto);
        prefs.put(ParametroSID, sid);
        prefs.put(ParametroUsuario, usuario);
        prefs.put(ParametroContrasenha, contrasenha);
    }
    public void guardarParametrosLog(String rutaLogs){
        prefs.put(ParametroRutaLogs, rutaLogs);
    }
}
