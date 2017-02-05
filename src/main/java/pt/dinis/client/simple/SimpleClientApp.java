package pt.dinis.client.simple;

import org.apache.log4j.Logger;

/**
 * Created by tiago on 22-01-2017.
 */
public class SimpleClientApp {

    private final static Logger logger = Logger.getLogger(SimpleClientApp.class);

    public static void main(String[] args) {
        SimpleClient client = new SimpleClient("localhost", 1500);
        logger.info("Starting simple client.");
        client.start();
    }
}
