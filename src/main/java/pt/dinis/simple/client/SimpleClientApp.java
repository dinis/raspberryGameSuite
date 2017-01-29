package pt.dinis.simple.client;

import org.apache.log4j.Logger;

/**
 * Created by tiago on 22-01-2017.
 */
public class SimpleClientApp {

    private final static Logger logger = Logger.getLogger(SimpleClientApp.class);

    public static void main(String[] args) {
        SimpleClient client = new SimpleClient("192.168.1.73", 1500);
        logger.info("Starting simple client.");
        client.start();
    }
}
