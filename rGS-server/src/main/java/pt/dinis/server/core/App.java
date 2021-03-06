package pt.dinis.server.core;

import pt.dinis.common.core.Configurations;
import pt.dinis.common.core.Display;
import pt.dinis.server.data.access.DBConnection;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.log4j.Logger;

public class App {
    private final static Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args ) throws IOException {
        Dealer dealer;

        try {
            Configurations.setPropertiesFromFile("user.properties.file.name", true);
        } catch (IOException e) {
            return;
        }

        try {
            DBConnection.prepareDBAccess();
            DBConnection.testConnection();
        } catch (ClassNotFoundException e) {
            logger.fatal("Can't find postgreSQL driver.", e);
            Display.alert("Data Base driver not found.");
            return;
        } catch (ConnectException ex) {
            Display.alert("Data base is not started or it's credentials are wrong.");
            return;
        }
        
        dealer = new Dealer(Integer.parseInt(Configurations.getProperty("server.port")));
        dealer.start();
    }
}
