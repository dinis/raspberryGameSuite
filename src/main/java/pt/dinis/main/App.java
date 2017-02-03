package pt.dinis.main;


import pt.dinis.temporary.Configurations;
import java.io.FileNotFoundException;
import java.io.IOException;


public class App {
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

        dealer = new Dealer(Integer.parseInt(Configurations.getProperty("port")));
        dealer.start();
    }
}
