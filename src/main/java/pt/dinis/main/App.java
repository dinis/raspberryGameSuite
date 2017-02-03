package pt.dinis.main;

import pt.dinis.temporary.Configurations;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class App {
    private final static Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args ) throws IOException {
        Dealer dealer;

        try {
            Configurations.setPropertiesFromFile();
        } catch (FileNotFoundException e) {
            Display.alert("Important files not found, this program will close now.");
            return;
        } catch (IOException e) {
            Display.alert("I/O problems, this program will close now.");
            return;
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.fatal("Can't find postgreSQL driver.");
        }

        String user = Configurations.getProperty("db.user");
        String url = "jdbc:postgresql://" + Configurations.getProperty("hostname") + "/" + Configurations.getProperty("db.path");
        String pass = Configurations.getProperty("db.pass");

        Display.display(url);

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, pass);


            String insertUser = "INSERT INTO users (name, password) VALUES (?, ?)";
            try (PreparedStatement prepSt = conn.prepareStatement(insertUser)) {
                prepSt.setString(1, "João Sardinha");
                prepSt.setString(2, "carapau_assado");
                int a = prepSt.executeUpdate();
            } catch (SQLException e) {
                Display.alert("Qualquer problema que desconheço porque a db não me diz.");
            }
        }  catch (SQLException e) {
            Display.alert("não consegue obter conexão");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        dealer = new Dealer(Integer.parseInt(Configurations.getProperty("port")));
        dealer.start();
    }
}