package pt.dinis.client.login;

import org.apache.log4j.Logger;

/**
 * Created by tiago on 22-01-2017.
 */
public class LoginClientApp {

    private final static Logger logger = Logger.getLogger(LoginClientApp.class);

    public static void main(String[] args) {
        LoginClient client = new LoginClient();
        logger.info("Starting login client.");
        client.start();
    }
}
