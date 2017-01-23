package pt.dinis.simple.client;

import org.apache.log4j.Logger;

/**
 * Created by tiago on 22-01-2017.
 */
public class SimpleClientApp {

    final static Logger logger = Logger.getLogger(SimpleClientApp.class);

    public static void main(String[] args) {
        SimpleClient client = new SimpleClient();
        client.start();
    }
}
