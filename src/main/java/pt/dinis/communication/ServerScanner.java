package pt.dinis.communication;

import pt.dinis.simple.client.SimpleClient;

import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class ServerScanner extends Thread {

    private boolean running;

    public ServerScanner() {
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        running = true;
        while(running) {
            String message = scanner.nextLine();
            if(message.equals("logout")) {
                running = false;
                scanner.close();
                SimpleClient.close();
            } else {
                SimpleClient.sendMessage(message);
            }
        }
    }

}
