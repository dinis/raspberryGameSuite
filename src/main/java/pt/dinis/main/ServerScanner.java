package pt.dinis.main;

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
            if(message.equals("end")) {
                running = false;
                scanner.close();
                // fechar socket server
            } else {
                // tratar da mensagem
            }
        }
    }

}
