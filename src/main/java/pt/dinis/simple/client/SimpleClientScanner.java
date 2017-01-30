package pt.dinis.simple.client;

import pt.dinis.main.Display;

import java.util.Scanner;

/**
 * Created by tiago on 22-01-2017.
 */
public class SimpleClientScanner extends Thread {

    private Scanner scanner;

    private boolean running;

    public SimpleClientScanner() {
    }

    @Override
    public void run() {
        scanner = new Scanner(System.in);
        running = true;
        while(running) {
            String message = scanner.nextLine();
            SimpleClient.sendMessage(message);
        }
    }

    public boolean close() {
        running = false;
        scanner.close();
        return true;
    }

}
