package pt.dinis.temporary;

import pt.dinis.main.Dealer;
import pt.dinis.main.Display;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by tiago on 22-01-2017.
 */
public class WorkerThread extends Thread {

    private String message;
    private Integer id;

    public WorkerThread(String message, int id) {
        this.message = message;
        this.id = id;
    }

    @Override
    public void run() {
       if (message.equals("all")) {
           Dealer.sendMessage(Dealer.getActiveClients(), "all");
       } else if (message.equals("echo")) {
           Dealer.sendMessage(Collections.singleton(id), "echo");
       } else if (message.equals("others")) {
           Collection<Integer> clients = Dealer.getActiveClients();
           clients.remove(id);
           Dealer.sendMessage(clients, "others");
       } else if (message.equals("server")) {
           Display.display("client " + id + " said: " + message);
       } else if (message.equals("disconnect")) {
           Dealer.disconnectClient(id);
       } else {
           Display.alert("client " + id + " said: " + message);
       }
    }
}
