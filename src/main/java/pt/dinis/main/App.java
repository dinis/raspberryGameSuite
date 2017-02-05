package pt.dinis.main;

import pt.dinis.dataaccess.DBConnection;
import java.io.IOException;
import org.apache.log4j.Logger;

public class App {
    private final static Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args ) throws IOException {
        Dealer dealer;

        try {
            Configurations.setPropertiesFromFile();
        } catch (IOException e) {
            return;
        }

        try {
            DBConnection.prepareDBAccess();
        } catch (ClassNotFoundException e) {
            logger.fatal("Can't find postgreSQL driver.");
            Display.alert("Data Base driver not found.");
            return;
        }
        
        dealer = new Dealer(Integer.parseInt(Configurations.getProperty("server.port")));
        dealer.start();
    }
}
