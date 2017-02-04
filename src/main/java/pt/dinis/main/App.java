package pt.dinis.main;

import pt.dinis.dataaccess.DBConnection;
import pt.dinis.temporary.Configurations;
import java.io.FileNotFoundException;
import java.io.IOException;
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
            DBConnection.prepareDBAccess();
        } catch (ClassNotFoundException e) {
            logger.fatal("Can't find postgreSQL driver.");
            Display.alert("Data Base driver not found.");
            return;
        }
        
        dealer = new Dealer(Integer.parseInt(Configurations.getProperty("port")));
        dealer.start();
    }
}