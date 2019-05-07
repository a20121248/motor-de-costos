package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConnectionDB {
    public ConnectionDB(){    
    }
    
    public Connection getConnection() {
        Connection con = null;
        try {
            File file = new File("credenciales.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            List<String> credenciales = new ArrayList();
            while ((line = bufferedReader.readLine()) != null) {
                credenciales.add(line);
            }
            fileReader.close();
            if (credenciales.size()!=2) {
                return null;
            }
            String user = credenciales.get(0);
            String password = credenciales.get(1);
            
            //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
            String driverName = "oracle";
            String serverName = "localhost";
            String portNumber = "1521";
            String instanceName = "orcl.SCMS.ms.corp";
            
            //String url = "jdbc:oracle:thin:@localhost:1521/orcl.SCMS.ms.corp";
            String url = String.format("jdbc:%s:thin:@%s:%s/%s",
                    driverName,
                    serverName,
                    portNumber,
                    instanceName);
            con = DriverManager.getConnection(url,user,password);
        } catch (Exception e) {
            System.out.println("Hubo un error en la conexión con la base de datos.");
        }
        return con;
    }
}
