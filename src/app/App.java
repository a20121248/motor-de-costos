package app;

import controlador.LoginControlador;
import controlador.Navegador;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    final static Logger LOG_RAIZ = Logger.getLogger("");
    final static Logger LOG_CONTROLADOR = Logger.getLogger("controlador");
    final static Logger LOG_INICIO = Logger.getLogger("controlador.inicio");
    final static Logger LOG_APROVISIONAMIENTO = Logger.getLogger("controlador.aprovisionamiento");
    final static Logger LOG_PARAMETRIZACION = Logger.getLogger("controlador.parametrizacion");
    final static Logger LOG_PROCESOS = Logger.getLogger("controlador.procesos");
    final static Logger LOG_REPORTING = Logger.getLogger("controlador.reporting");

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
        Format formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        SimpleFormatter simpleFormatter = new SimpleFormatter();
        String fechaStr = formatter.format(new Date());
        try {
            Navegador.crearCarpeta("./reportes/");
            Navegador.crearCarpeta("./reportes/gastos/");
            Navegador.crearCarpeta("./reportes/ingresos/");
            
            String carpetaLogsNombre = "./logs/";
            Navegador.crearCarpeta(carpetaLogsNombre);

            String fileName = String.format(carpetaLogsNombre + "%s_app.log",fechaStr);
            Handler fileHandler = new FileHandler(fileName, false);
            fileHandler.setFormatter(simpleFormatter);
            Handler consoleHandler = new ConsoleHandler();            

            LOG_RAIZ.addHandler(consoleHandler);
            LOG_RAIZ.addHandler(fileHandler);
            
            consoleHandler.setLevel(Level.OFF);
            fileHandler.setLevel(Level.ALL);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        launch(args);        
    }
}