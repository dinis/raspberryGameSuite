package pt.dinis.main;

import java.io.IOException;

public class App {
    public static void main( String[] args ) throws IOException {
        Dealer dealer;

        try {
            Configurations.setPropertiesFromFile();
        } catch (IOException e) {
            return;
        }

        dealer = new Dealer(Integer.parseInt(Configurations.getProperty("server.port")));
        dealer.start();
    }
}
