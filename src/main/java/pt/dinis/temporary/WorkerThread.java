package pt.dinis.temporary;

import org.apache.log4j.Logger;
import pt.dinis.main.Dealer;
import pt.dinis.main.Display;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by tiago on 22-01-2017.
 */
public class WorkerThread extends Thread {

    private final static Logger logger = Logger.getLogger(WorkerThread.class);

    public enum MessageType {
        LOGIN("login"),
        RELOGIN("relogin"),
        LOGOUT("logout"),
        CLOSE("disconnect"),
        MESSAGE("message");

        private String word;

        MessageType(String word) {
            this.word = word;
        }

        String getWord() {
            return word;
        }
    }

    private String message;
    private Integer id;

    public WorkerThread(String message, int id) {
        this.message = message;
        this.id = id;
    }

    @Override
    public void run() {

        try {
            List<String> words = splitMessage(message);
            if (words.isEmpty()) {
                return;
            }
            String word = words.get(0).toLowerCase();

            if (MessageType.LOGIN.getWord().equals(word)) {
                login();
            } else if (MessageType.RELOGIN.getWord().equals(word)) {
                if (words.size() != 2) {
                    Display.alert("Unknown message '" + message + "'");
                    return;
                }
                relogin(words.get(1));
            } else if (MessageType.CLOSE.getWord().equals(word)) {
                close();
            } else if (MessageType.LOGOUT.getWord().equals(word)) {
                logout();
            } else if (MessageType.MESSAGE.getWord().equals(word)) {
                message(message.substring(message.indexOf(word) + word.length()).trim());
            } else {
                message(message.trim());
            }
        } catch (Exception e) {
            Display.alert("error in message '" + message + "' from client " + id);
            logger.error("Error interpreting message '" + message + "'", e);
        }
    }

    private static List<String> splitMessage(String message) {
        List<String> words = (new ArrayList<>());
        words.addAll(Arrays.asList(message.split("\\s+")));
        while(words.remove("")) { }
        return words;
    }

    private void login() {
        Display.info("Log in client " + id);
        Dealer.loginClient(id);
    }

    private void relogin(String hash) {
        Display.info("Relog in client " + id + " with hash '" + hash + "'");
        Dealer.reloginClient(hash, id);
    }

    private void logout() {
        Display.info("Log out client " + id);
        Dealer.logoutClient(id);
    }

    private void close() {
        Dealer.disconnectClient(id);
    }

    private void message(String message) {
        List<String> words = splitMessage(message);
        if(words.isEmpty()) {
            Display.alert("client " + id + "sent an empty message");
            return;
        }
        String word = words.get(0).toLowerCase();
        String remaining = message.substring(message.indexOf(word) + word.length()).trim();

        if (word.equals("all")) {
            Dealer.sendMessage(Dealer.getActiveClients(), remaining);
        } else if (word.equals("echo")) {
            Dealer.sendMessage(Collections.singleton(id), remaining);
        } else if (word.equals("others")) {
            Collection<Integer> clients = Dealer.getActiveClients();
            clients.remove(id);
            Dealer.sendMessage(clients, remaining);
        } else if (word.equals("server")) {
            Display.display("client " + id + " said: " + remaining);
        } else {
            Display.alert("client " + id + " said: " + message);
        }
    }
}
