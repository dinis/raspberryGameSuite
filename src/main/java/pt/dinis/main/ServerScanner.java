package pt.dinis.main;

import java.util.Collections;
import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class ServerScanner extends Thread {

    private Scanner scanner;
    private boolean running;
    static private final String MESSAGE = "message";
    static private final String ALL = "all";
    static private final String GET = "get";
    static private final String LOGOUT = "logout";
    static private final String END = "end";

    public ServerScanner() {
    }

    @Override
    public void run() {
        scanner = new Scanner(System.in);
        running = true;

        while (running) {
            String message = scanner.nextLine();
            protocol(message);
        }
    }

    public boolean close() {
        running = false;
        scanner.close();
        Display.display("Server scanner is closed.");

        return true;
    }

    private void protocol(String message) {
        String[] splittedMessage = message.split(" ");

        if (splittedMessage[0].equals(MESSAGE)) {
            if (splittedMessage[1].equals(ALL)) {
                Dealer.sendMessage(Dealer.getActiveClients(), message);
                return;
            }

            int id = Integer.parseInt(splittedMessage[1]);
            Dealer.sendMessage(Collections.singleton(id), message);

        } else if (splittedMessage[0].equals(LOGOUT)) {
            if (splittedMessage[1].equals(ALL)) {
                for (Integer id: Dealer.getActiveClients()) {
                    Dealer.closeClient(id);
                }

                return;
            }

            int id = Integer.parseInt(splittedMessage[1]);
            Dealer.closeClient(id);

        } else if (splittedMessage[0].equals(GET)) {
            Display.display("Clients list: " + Dealer.getActiveClients());
        } else if (splittedMessage[0].equals(END)) {
            Dealer.stop();
        } else {
            Display.alert("Unknown message " +  message);
        }
    }
}
