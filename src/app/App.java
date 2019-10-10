package app;

import controlador.LoginControlador;
import controlador.Navegador;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    // El log para ESTA clase en particular
    final static Logger LOGGER = Logger.getLogger("App");
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vista/Login.fxml"));
        LoginControlador loginControlador = new LoginControlador(this);
        fxmlLoader.setController(loginControlador);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Motor de distribución | Iniciar sesión");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public static void main(String[] args) {
        try {
            Navegador.crearCarpeta("./reportes/");
            Navegador.crearCarpeta("./reportes/real/");
            Navegador.crearCarpeta("./reportes/presupuesto/");
        } catch (SecurityException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        launch(args);        
    }
}