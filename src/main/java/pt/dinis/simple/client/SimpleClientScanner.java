package pt.dinis.simple.client;

import pt.dinis.main.Display;

import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class SimpleClientScanner extends Thread {

    private boolean running;

    public SimpleClientScanner() {
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
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
