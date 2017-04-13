package pt.dinis.server.main;

import pt.dinis.common.core.Display;

import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class ServerScanner extends Thread {

    private Scanner scanner;
    private boolean running;

    public ServerScanner() {
    }

    @Override
    public void run() {
        scanner = new Scanner(System.in);
        running = true;

        while (running) {
            String message = scanner.nextLine();
            try {
                ServerScannerProtocol.protocol(message);
            } catch (Exception e) {
                Display.alert("Unknown message '" +  message + "'");
            }
        }
    }

    public boolean close() {
        running = false;
        scanner.close();
        Display.info("Server scanner is closed.");

        return true;
    }
}
